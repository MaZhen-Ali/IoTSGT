package com.zjq.springboot.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zjq.springboot.Utils.MultimodalGPTBaiduQianfanAI;
import com.zjq.springboot.entity.ReqDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.commons.text.similarity.JaccardSimilarity;
import org.apache.commons.text.similarity.CosineSimilarity;
import com.hankcs.hanlp.dictionary.CoreSynonymDictionary;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.suggest.Suggester;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;




import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import okhttp3.*;
import org.springframework.web.bind.annotation.RequestBody;


import java.io.*;
import java.util.Base64;


@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping
    public void test() {


        String str1 = "感知太阳位置";
        String str2 = "太阳测量角度和太阳可见标志";

        System.out.println(str1 + "," + str2);


//        Levenshtein距离：也称为编辑距离，用于计算两个字符串之间的最小编辑操作次数（插入、删除、替换）来转换一个字符串为另一个字符串。编辑距离越小，表示两个字符串越相似。

        int distance = LevenshteinDistance.getDefaultInstance().apply(str1, str2);
        double similarity1 = 1 - (double) distance / Math.max(str1.length(), str2.length());
        System.out.println("相似度（根据编辑距离计算）：" + similarity1);


//        Jaccard相似度：用于计算两个集合之间的相似度，可以将字符串视为字符的集合，计算它们的交集和并集的比值。Jaccard相似度的取值范围是0到1，值越接近1表示相似度越高。

        JaccardSimilarity jaccardSimilarity = new JaccardSimilarity();
        double similarity2 = jaccardSimilarity.apply(str1, str2);

        System.out.println("相似度（Jaccard相似度）：" + similarity2);


//        Cosine相似度：常用于计算文本相似度，将字符串视为向量，计算它们的夹角余弦值。Cosine相似度的取值范围也是0到1，值越接近1表示相似度越高。

        Map<CharSequence, Integer> map1 = Arrays.stream(str1.split("")).collect(Collectors.toMap(c -> c, c -> 1, Integer::sum));
        Map<CharSequence, Integer> map2 = Arrays.stream(str2.split("")).collect(Collectors.toMap(c -> c, c -> 1, Integer::sum));

        CosineSimilarity cosineSimilarity = new CosineSimilarity();
        double similarity3 = cosineSimilarity.cosineSimilarity(map1, map2);
        System.out.println("相似度（Cosine相似度）：" + similarity3);

        System.out.println("平均相似度：" + (similarity1 + similarity2 + similarity3) / 3);


//        hanlp语义相似度
//        double similarity4 = CoreSynonymDictionary.similarity("我喜欢你","我爱你");
//        System.out.println("相似度（语义相似度）：" + similarity4);


    }

    @PostMapping("/api")
    public void testJSON(@RequestBody ReqDocument request) throws IOException {

        String html = request.getContent();

        // 1. 使用 Jsoup 解析 HTML
        Document doc = Jsoup.parse(html);

        // 2. 获取文本内容
        String documentText = doc.text();

        // 3. 提取 <img> 标签
        Elements images = doc.select("img");

        List<String> base64Images = new ArrayList<>();
        for (Element img : images) {
            String src = img.attr("src");

            // 4. 检测 base64 图片
            if (src.startsWith("data:image")) {
                try {
                    String base64Image = src.split(",")[1];
                    byte[] imageBytes = Base64.getDecoder().decode(base64Image);

                    // 5. 解码为 BufferedImage（在内存中）
                    ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
                    BufferedImage image = ImageIO.read(bis);

                    if (image != null) {
                        System.out.println("✅ 提取成功：" + image.getWidth() + "x" + image.getHeight());

                        // 6. 再次编码为 base64 PNG（带前缀）
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(image, "png", baos);
                        String base64Encoded = Base64.getEncoder().encodeToString(baos.toByteArray());
                        String dataUrl = "data:image/png;base64," + base64Encoded;

                        base64Images.add(dataUrl);  // ✅ 添加到列表中

                    } else {
                        System.out.println("⚠️ 图片解码失败！");
                    }
                } catch (Exception e) {
                    System.err.println("❌ 图片处理出错：" + e.getMessage());
                }
            }
        }



        // 7. 构造完整请求 JSON（拼接文本 + 所有图片块）
        // 构建 content 数组
        JsonArray imageContentArray = new JsonArray();
        // 图片部分
        for (String base64 : base64Images) {
            JsonObject imageUrl = new JsonObject();
            imageUrl.addProperty("url", base64);

            JsonObject imageObj = new JsonObject();
            imageObj.addProperty("type", "image_url");
            imageObj.add("image_url", imageUrl);

            imageContentArray.add(imageObj);
        }


        // 文本部分
        JsonObject textContent = new JsonObject();
        textContent.addProperty("type", "text");
        textContent.addProperty("text", "这幅图片里面有什么?有几张图片");

        // 合并 content
        JsonArray contentArray = new JsonArray();
        contentArray.add(textContent);
        // ❗ 逐个添加 imageObj，而不是整个 imageContentArray
        for (JsonElement img : imageContentArray) {
            contentArray.add(img);
        }

        // 构造 message
        JsonObject messageObj = new JsonObject();
        messageObj.addProperty("role", "user");
        messageObj.add("content", contentArray);

        JsonArray messagesArray = new JsonArray();
        messagesArray.add(messageObj);

        // 最终 payload
        JsonObject payload = new JsonObject();
        payload.addProperty("model", "ernie-4.5-8k-preview");
        payload.add("messages", messagesArray);
        payload.addProperty("stream", false);

        System.out.println(payload.toString());  // ✅ 这是你的 JSON 字符串



        String result = MultimodalGPTBaiduQianfanAI.BaiduGPT(payload.toString());
        System.out.println(result);


//        调用百度大模型（图片+文本输入模板）
//        response = client.chat.completions.create(
//                model="ernie-4.5-8k-preview",
//                messages=[
//                    {
//                        "role": "user",
//                        "content": [
//                            {
//                                "type": "text",
//                                "text": "What is in this image?",
//                            },
//                            {
//                                "type": "image_url",
//                                    "image_url": {
//                                "url": f"data:image/jpeg;base64,{base64_image}"
//                            },
//                    },
//                        ],
//                    }
//                ],
//                    stream=True
//            )




    }
}
