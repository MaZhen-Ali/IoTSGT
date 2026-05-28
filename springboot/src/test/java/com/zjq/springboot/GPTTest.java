package com.zjq.springboot;

import com.alibaba.fastjson.JSON;
import com.baidubce.qianfan.Qianfan;
import com.baidubce.qianfan.core.auth.Auth;
import com.baidubce.qianfan.model.chat.ChatResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.*;
import com.zjq.springboot.Utils.GPTZhiPuAI;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GPTTest {

    //http调用
    @Test
    public void zhiPu(){
        String apiUrl = "https://open.bigmodel.cn/api/paas/v4/chat/completions"; // 官网中说明文档的URL
        String apiKey = "9e0184149a0235ec828e7b0990375951.CWSeSosMWknTEJAS"; // 你的API密钥
        String inputText = "你叫什么名字"; // 你想要问答的内容

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoOutput(true);

            // 创建请求体
            String jsonInputString = "{\"model\": \"glm-4\", \"messages\": [{\"role\": \"user\", \"content\": \"" + inputText + "\"}]}";

            // 发送请求体
            try (DataOutputStream os = new DataOutputStream(connection.getOutputStream())) {
                os.write(jsonInputString.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            // 获取响应
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) { // 成功响应
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    String responseLine;
                    StringBuilder response = new StringBuilder();

                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }

                    System.out.println("Response: " + response.toString());
                }
            } else {
                System.out.println("Failed to call GLM-4 API");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    //sdk同步调用
    @Test
    public void testInvoke() {
        ClientV4 client = new ClientV4.Builder("9e0184149a0235ec828e7b0990375951.CWSeSosMWknTEJAS").build();
        String requestIdTemplate = "request_%d";
        ObjectMapper mapper = new ObjectMapper();
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), "作为一名营销专家，请为智谱开放平台创作一个吸引人的slogan");
        messages.add(chatMessage);
        String requestId = String.format(requestIdTemplate, System.currentTimeMillis());

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .requestId(requestId)
                .build();
        ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);
        Object content = invokeModelApiResp.getData().getChoices().get(0); // 获取响应内容
        try {
            System.out.println("model output:" + mapper.writeValueAsString(invokeModelApiResp));
            System.out.println("content output:" + mapper.writeValueAsString(content));
            System.out.println("content output:" + mapper.writeValueAsString(invokeModelApiResp.getData().getChoices().get(0)));
            System.out.println("model output:" + JSON.toJSONString(invokeModelApiResp.getData().getChoices().get(0)));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void Test() throws JsonProcessingException {
        GPTZhiPuAI gptZhiPuAI = new GPTZhiPuAI();
        ObjectMapper mapper = new ObjectMapper();
        String content = "你叫什么名字！";
        ModelApiResponse answer = gptZhiPuAI.testInvoke(content);
        System.out.println(answer.getData().getChoices());
        System.out.println(answer.getData().getChoices().get(0));
        System.out.println(answer.getData().getChoices().size());

//        System.out.println(answer.getData());
//        System.out.println(answer.getData().getChoices());
//        System.out.println(answer.getData().getChoices().get(0));

    }

    //百度大模型
    @Test
    public void BaiduGPT(){
        Qianfan qianfan = new Qianfan(Auth.TYPE_OAUTH, "abYjQAuvrkE78upQqTHlOkCI", "78FMQLe6Ei7nfcMwDS36PNxuU06yD8u2");
        ChatResponse response = qianfan.chatCompletion()
                .model("ERNIE-4.0-8K") // 使用model指定预置模型
                // .endpoint("completions_pro") // 也可以使用endpoint指定任意模型 (二选一)
                .addMessage("user", "你好！你叫什么名字") // 添加用户消息 (此方法可以调用多次，以实现多轮对话的消息传递)
                .temperature(0.7) // 自定义超参数
                .execute(); // 发起请求
        System.out.println(response.getResult());

    }


}
