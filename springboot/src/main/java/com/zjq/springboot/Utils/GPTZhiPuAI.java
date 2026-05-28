package com.zjq.springboot.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;

import java.util.ArrayList;
import java.util.List;


public class GPTZhiPuAI {

    public ModelApiResponse testInvoke(String question) throws JsonProcessingException {
        ClientV4 client = new ClientV4.Builder("9e0184149a0235ec828e7b0990375951.CWSeSosMWknTEJAS").build();
        String requestIdTemplate = "request_%d";
        ObjectMapper mapper = new ObjectMapper();
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), question);
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

        try {
            System.out.println("model output:" + mapper.writeValueAsString(invokeModelApiResp));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return invokeModelApiResp;

    }
}
