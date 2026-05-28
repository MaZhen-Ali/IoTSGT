package com.zjq.springboot.Utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


//图片+文本输入
public class MultimodalGPTBaiduQianfanAI {
    public static String BaiduGPT(String content) throws IOException {


        // 1. 设置 Token（你自己替换）
        String accessToken = "bce-v3/ALTAK-sYdyAcsaKYOe6aUFpo90C/2cd29f23c43aa19c68e3182e6ef51e3ca8bdcaa6"; // ⚠️ 记得换成你自己的 IAM token

        // 4. 发送 POST 请求
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS) // 连接超时
                .readTimeout(30, TimeUnit.SECONDS)    // 读取超时
                .writeTimeout(30, TimeUnit.SECONDS)   // 写入超时（比如上传）
                .build();
        okhttp3.RequestBody body = okhttp3.RequestBody.create(content, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url("https://qianfan.bj.baidubce.com/v2/chat/completions")
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        String result = new String();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("❌ 请求失败: " + response);
            } else {
                String responseStr = response.body().string();  // 把你的 JSON 字符串粘这里
                JsonObject jsonObject = JsonParser.parseString(responseStr).getAsJsonObject();
                JsonArray choices = jsonObject.getAsJsonArray("choices");
                JsonObject firstChoice = choices.get(0).getAsJsonObject();
                JsonObject message = firstChoice.getAsJsonObject("message");
                result = message.get("content").getAsString();

            }
        }

        return result;

    }
}
