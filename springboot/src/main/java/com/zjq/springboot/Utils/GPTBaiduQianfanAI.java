package com.zjq.springboot.Utils;

import com.baidubce.qianfan.Qianfan;
import com.baidubce.qianfan.core.auth.Auth;
import com.baidubce.qianfan.model.chat.ChatResponse;

public class GPTBaiduQianfanAI {

    public static String BaiduGPT(String content){
        Qianfan qianfan = new Qianfan(Auth.TYPE_OAUTH, "abYjQAuvrkE78upQqTHlOkCI", "78FMQLe6Ei7nfcMwDS36PNxuU06yD8u2");
        ChatResponse response = qianfan.chatCompletion()
//                .model("ERNIE-4.0-8K") // 使用model指定预置模型
                .model("ERNIE-Lite-8K") // 使用model指定预置模型（一直使用的这个）
                // .endpoint("completions_pro") // 也可以使用endpoint指定任意模型 (二选一)
                .addMessage("user", content) // 添加用户消息 (此方法可以调用多次，以实现多轮对话的消息传递)
                .temperature(0.7) // 自定义超参数
                .execute(); // 发起请求
        return response.getResult();
//        System.out.println(response.getResult());

    }
}
