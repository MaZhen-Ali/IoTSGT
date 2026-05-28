package com.zjq.springboot.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.zjq.springboot.Utils.GPTBaiduQianfanAI;
import com.zjq.springboot.entity.ReqDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RestController
@RequestMapping("/evaluate")
public class DocumentEvaluationController {

    @PostMapping("/api")
    public JSONArray evaluate(@RequestBody ReqDocument request) throws IOException {
        String html = request.getContent();

        // 1. 使用 Jsoup 解析 HTML
        Document doc = Jsoup.parse(html);

        // 2. 获取文本内容
        String documentText = doc.text();

        // 2. 提取 <img> 标签
        Elements images = doc.select("img");

        StringBuilder imageJsonBlocks = new StringBuilder();

        for (Element img : images) {
            String src = img.attr("src");

            // 3. 检测 base64 图片
            if (src.startsWith("data:image")) {
                try {
                    String base64Image = src.split(",")[1];
                    byte[] imageBytes = Base64.getDecoder().decode(base64Image);

                    // 4. 解码为 BufferedImage（在内存中）
                    ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
                    BufferedImage image = ImageIO.read(bis);

                    if (image != null) {
                        System.out.println("✅ 提取成功：" + image.getWidth() + "x" + image.getHeight());



                        // ✅ 添加尺寸压缩逻辑
                        int maxWidth = 256;
                        int maxHeight = 256;
                        int width = image.getWidth();
                        int height = image.getHeight();

                        if (width > maxWidth || height > maxHeight) {
                            float scale = Math.min((float) maxWidth / width, (float) maxHeight / height);
                            int newWidth = Math.round(width * scale);
                            int newHeight = Math.round(height * scale);
                            image = resizeImage(image, newWidth, newHeight);
                            System.out.println("✅ 已压缩至：" + newWidth + "x" + newHeight);
                        }

                        // 5. 再次编码为 base64 PNG（带前缀）
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(image, "png", baos);
                        String base64Encoded = Base64.getEncoder().encodeToString(baos.toByteArray());
                        String dataUrl = "data:image/png;base64," + base64Encoded;

                        // 6. 构造 JSON 的一部分
                        imageJsonBlocks.append("{\n" +
                                "  \"type\": \"image\",\n" +
                                "  \"image\": \"" + dataUrl + "\"\n" +
                                "}");

                    } else {
                        System.out.println("⚠️ 图片解码失败！");
                    }
                } catch (Exception e) {
                    System.err.println("❌ 图片处理出错：" + e.getMessage());
                }
            }
        }

        // 7. 构造完整 JSON（拼接文本 + 所有图片块）
        String payload = "{\n" +
                "  \"messages\": [\n" +
                "    {\n" +
                "      \"role\": \"user\",\n" +
                "      \"content\": [\n" +
                "        {\n" +
                "          \"type\": \"text\",\n" +
                "          \"text\": \"评估：\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"type\": \"image\",\n" +
                "          \"image\": \"" + imageJsonBlocks + "\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        System.out.println("📦 最终 JSON Payload:");
        System.out.println(payload);





//        // 3. 提取 <img> 标签
//        Elements images = doc.select("img");
//        for (Element img : images) {
//            String src = img.attr("src");
//
//            // 3. 检测是否是 base64 图片
//            if (src.startsWith("data:image")) {
//                try {
//                    String base64Image = src.split(",")[1];
//                    byte[] imageBytes = Base64.getDecoder().decode(base64Image);
//
//                    // 4. 解码为 BufferedImage（在内存中）
//                    ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
//                    BufferedImage image = ImageIO.read(bis);
//
//                    if (image != null) {
//                        System.out.println("✅ 成功提取图片至内存");
//                        System.out.println("图片宽度：" + image.getWidth());
//                        System.out.println("图片高度：" + image.getHeight());
//                    } else {
//                        System.out.println("⚠️ 图片解码失败！");
//                    }
//                } catch (Exception e) {
//                    System.err.println("❌ 图片处理出错：" + e.getMessage());
//                }
//            }
//        }


//        // 3. 获取图片地址（包含 base64 或 URL）
//        Elements images = doc.select("img");
//        String uploadDir = "uploads";
//        Files.createDirectories(Paths.get(uploadDir)); // 确保目录存在
//        for (Element img : images) {
//            String src = img.attr("src");
//
//            // TODO: 提取 base64 部分，保存为图片文件
//            if (src.startsWith("data:image")) {
//                String base64Image = src.split(",")[1];
//                byte[] imageBytes = Base64.getDecoder().decode(base64Image);
//
//                String filename = UUID.randomUUID().toString() + ".png";
//                Path outputPath = Paths.get(uploadDir, filename);
//
//                Files.write(outputPath, imageBytes);
//                System.out.println("图片保存成功：" + outputPath.toAbsolutePath());
//
//                // 你还可以考虑替换原 img 标签的 src 地址为服务器可访问路径
//                img.attr("src", "/uploads/" + filename);
//            }
//        }



        // 4. 你可以调用 AI 服务对 text 进行分析...

        GPTBaiduQianfanAI gptBaiduQianfanAI = new GPTBaiduQianfanAI();
//        String message = documentText + payload +
//                "完整性\n" +
//                "编号  指标名称  描述\n" +
//                "R12使用完整动词（Use full verb）：确保需求中的动词是完整表达，如 “验证” 而非 “被验证”。\n" +
//                "R13避免比较词（Avoid comparison）：需求应避免 “多于”、“优于” 等模糊比较。\n" +
//                "R14使用清晰的比较（Use clear comparison）：需求如果涉及比较，必须提供明确的数值或基准。\n" +
//                "R16使用明确定义单位（Use defined units）：需求涉及时间、长度、重量等指标时必须有单位（如 “毫秒”、“千克\"）。\n" +
//                "R17避免模糊术语（Avoid vague terms）：避免使用 “快速”、“安全” 、“尽快”、“好”、“易用”、“友好”、“稍后”等主观词，提供具体数值。\n" +
//                "R18避免逃避责任的条款（Avoid escape clauses）：避免 “如果可能”、“视情况而定” 等含糊表述。\n" +
//                "R19避免开放式条款（Avoid open-ended clauses）：需求不应包含 “等”、“例如” 等不明确的扩展内容。\n" +
//                "R28避免使用代词（Avoid pronouns）：避免使用 “它”、“它们” 等代词，改为具体的术语。\n" +
//                "R29需求应独立于上下文（Context-free）：需求应自包含，避免需要额外背景知识才能理解。\n" +
//                "R31使用明确条件（Use explicit conditions）：需求中涉及条件时，条件必须清晰，如 “如果 A, 则 B”。\n" +
//                "R34使用清晰的量词（Use clear quantifiers）：需求中的数量表述应明确，如 “至少3个” 而不是 “一些”。\n" +
//                "R35使用数值容差（Use value tolerances）：需求中涉及数值时，应提供误差范围，如 “100ms ± 5ms”。\n" +
//                "R36表达一个单独的需求（Express one atomic need）：需求应单独表达一个功能，不应包含多个目标。\n" +
//                "R37使用清晰的前置条件（Use clear preconditions）：需求涉及依赖关系时，应明确前置条件。\n" +
//                "R38使用清晰的业务逻辑（Use clear business logic）：需求应清楚描述业务规则，不产生逻辑冲突。\n" +
//                "R39使用明确的主体（Use clear subject）：需求的主体（执行者）应清晰，如 “系统shall” 而不是 “它shall”。"+
//                "以上是我的需求文档和评价规则，请你按照规则依次评价需求文档,返回以下JSON格式的数据：违反规则的需求文档中出现的语句、原因和改进建议,你怎样可以查看我提供的图片链接中的图片呢"+
//                "请返回以下格式的数据：[{\"originalRequirement\": \"\",\" reason\": \"\",\" advice\": \"\"},{\"originalRequirement\": \"\",\" reason\": \"\",\" advice\": \"\"}]" ;
//


        String message = documentText + payload +
                "语言模糊性：标记含主观词的句子数量（如“快速响应”“友好界面”）\n" +
                "数量与程度不确定性：检查是否存在不明确的量词或程度词，如“若干”“尽可能”“部分”“大约”“不少于”" +
                "代词指代歧义：检查代词（如“它”“该系统”“他们”）的指代是否唯一。" +
                "软件需求完整性：需求描述是否缺乏关键细节（如输入、输出、约束条件等）" +
                "非功能需求覆盖性：是否包含主要非功能属性（性能、安全性、可靠性）。" +
                "冲突需求率：需求条目间的逻辑矛盾数量" +
                "句式一致性：句式风格是否统一" +
                "可测试性：是否可以用客观数据验证需求是否被满足。不可测示例：\n" +
                "“外部温度超过 40°C”。 （条件不可复现，缺乏可控触发条件）\n" +
                "“10 年内稳定运行”。（验证周期不可实现，超出项目周期）\n" +
                "验证条件明确性：是否给出可执行的验证步骤或预期结果。不可验证示例：\n" +
                "系统应具有良好的响应性能。（“良好”无具体标准，无法测试。）\n" +
                "软件需求信息冗余度：同一软件需求或信息是否在多个位置重复出现。过多重复会导致修改困难。" +
                "需求独立性：各需求是否独立表达、未与其他需求混合在同一句或同段描述。" +





                "以上是我的需求文档和评价规则，请你按照规则依次评价需求文档,返回以下JSON格式的数据：违反规则的需求文档中出现的语句、原因和改进建议,"+
                "请返回以下格式的数据：[{\"originalRequirement\": \"\",\" reason\": \"\",\" advice\": \"\"},{\"originalRequirement\": \"\",\" reason\": \"\",\" advice\": \"\"}]";




        String result = gptBaiduQianfanAI.BaiduGPT(message);
        System.out.println(result);


        // 正则表达式：匹配每个对象的 "originalRequirement", "reason", 和 "advice"
        String regex = "\\{[^}]*\"originalRequirement\":\\s*\"([^\"]+)\"[^}]*\"reason\":\\s*\"([^\"]+)\"[^}]*\"advice\":\\s*\"([^\"]+)\"[^}]*\\}";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(result);

        // 创建一个 JSON 数组来存储匹配的结果
        JSONArray jsonArray = new JSONArray();

        // 查找所有匹配并将其转换为 JSON 对象
        while (matcher.find()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("originalRequirement", matcher.group(1));
            jsonObject.put("reason", matcher.group(2));
            jsonObject.put("advice", matcher.group(3));
            jsonArray.put(jsonObject);
        }

        // 输出 JSON 数组
        System.out.println(jsonArray.toString());

        return jsonArray;
    }

    // ✅ 缩放图像工具方法
    private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(resultingImage, 0, 0, null);
        g2d.dispose();

        return outputImage;
    }


    // 去掉最后一个多余的逗号
    private static String trimTrailingComma(String json) {
        return json.replaceAll(",\\s*$", "");}
}
