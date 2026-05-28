package com.zjq.springboot.controller;



import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.zjq.springboot.Utils.GPTBaiduQianfanAI;
import com.zjq.springboot.entity.ApplicationList;
import com.zjq.springboot.entity.SoftwareRequirement;
import com.zjq.springboot.mapper.SelectedDeviceMapper;
import com.zjq.springboot.mapper.SoftwareReqRecommendationMapper;
import com.zjq.springboot.mapper.TaskIntentMapper;
import com.zjq.springboot.service.ApplicationListService;
import com.zjq.springboot.service.SoftwareReqRecommendationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/softwareReqRecommendation")
public class SoftwareReqRecommendationController {

    @Autowired
    private SoftwareReqRecommendationService softwareReqRecommendationService;
    @Autowired
    private SoftwareReqRecommendationMapper softwareReqRecommendationMapper;
    @Autowired
    private ApplicationListService applicationListService;
    @Autowired
    private TaskIntentMapper taskIntentMapper;
    @Autowired
    private SelectedDeviceMapper selectedDeviceMapper;

    //查询所有数据
    @GetMapping
    public List<SoftwareRequirement> findAll(){

        return softwareReqRecommendationService.list();
    }

    //按id选择系统需求
    @PostMapping("/select/{id}")
    public boolean selectDevice(@PathVariable Integer id){  //[1,2,3]
        return softwareReqRecommendationMapper.selectById(id);
    }

    //按id取消选择系统需求
    @PostMapping("/cancelSelect/{id}")
    public boolean cancelSelectDevice(@PathVariable Integer id){  //[1,2,3]

        return softwareReqRecommendationMapper.cancelSelectById(id);
    }


    //gpt生成--推荐的软件需求
    @GetMapping("/generate/gpt")
    public JSONArray RecommendSysReq(){

        List<ApplicationList> applicationList = applicationListService.list();

        List<String> applicationListStrings = new ArrayList<>();
        for (ApplicationList list : applicationList) {
            applicationListStrings.add(list.getId()+","+list.getName()+","+list.getApplication());
        }
        String content1 = convertListToString(applicationListStrings);
        String content2 = ListToStringExample(selectedDeviceMapper.findInterface());
        String content3 = ListToStringExample(selectedDeviceMapper.findDeviceName());


        GPTBaiduQianfanAI gptBaiduQianfanAI = new GPTBaiduQianfanAI();
        String content = content1 +
                "端口：\n" +
                content2 +
                "以上是各个设备的功能列表和功能端口，\n" +
                "采集器推荐数据采集、设备初始化、故障诊断等功能，控制器推荐控制计算等功能，执行器推荐控制计算、设备初始化、故障诊断等功能。系统包含的设备有" +
                content3 +
                "软件需求如：太阳搜索系统将加电控制信号发送给太阳敏感器，太阳敏感器进行加电操作，实现太阳敏感器初始化功能。软件需求是指控制器与设备之间的交互，请根据控制器与设备之间的交互，以及交互端口，推荐软件需求。" +
                "请按“软件需求+序号：”的格式返回";
        String result = gptBaiduQianfanAI.BaiduGPT(content);
        System.out.println("gpt结果"+result);

        List<String> SoftwareRequirementsList = SoftwareRequirementsToList(result);
        JSONArray SoftwareRequirementsJSON = SoftwareRequirementsToJson(SoftwareRequirementsList);

//        for (int i = 0 ; i < SoftwareRequirementsJSON.size();i++){
//            JSONObject key = (JSONObject)SoftwareRequirementsJSON.get(i);
//            String description= (String) key.get("description");
//            SoftwareReqRecommendation softwareReqRecommendation=new SoftwareReqRecommendation(description);
//            softwareReqRecommendationService.save(softwareReqRecommendation);
//        }



        return SoftwareRequirementsJSON;
    }


    //将gpt返回的结果转化为java列表
    public List<String> SoftwareRequirementsToList(String requirementsText) {
        // 定义一个包含所有软件需求的字符串,软件需求格式如下所示
//        "1. 软件需求1：太阳搜索控制系统应通过串行端口向太阳敏感器发送加电控制信号，以实现太阳敏感器的初始化功能。\n" +
//        "2. 软件需求2：太阳敏感器应通过串行端口向太阳搜索控制系统发送太阳测量角度和太阳可见标志数据。\n" +
//        "3. 软件需求3：太阳搜索控制系统应接收并处理太阳敏感器发送的数据，以确定太阳的位置，并据此调整航天器的姿态。\n" +
//        "4. 软件需求4：太阳搜索控制系统应通过串行端口向陀螺发送指令，请求采集卫星姿态角和卫星三轴角速度数据。";
        // 使用正则表达式按行分割字符串，并移除每行前面的序号和"软件需求"等文字
        List<String> softwareRequirementsList = new ArrayList<>();
        String regex = "软件需求\\d+：(.*?)\\。"; // 正则表达式匹配"软件需求X："后面的内容，直到遇到"。"为止
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(requirementsText);
        while (matcher.find()) {
            String requirement = matcher.group(1).trim(); // 提取匹配的部分，并去除前后空白字符
            softwareRequirementsList.add(requirement);
        }
        return softwareRequirementsList;
    }

    //java列表转换为JSON格式
    public JSONArray SoftwareRequirementsToJson(List<String> descriptions){
        // 创建一个JSONArray对象
        JSONArray softwareRequirementsArray = new JSONArray();
        // 将每个软件需求作为一个JSONObject添加到JSONArray中
        for (int i = 0; i < descriptions.size(); i++) {
            JSONObject requirement = new JSONObject();
            requirement.put("id", "软件需求" + (i + 1));
            requirement.put("description", descriptions.get(i));
            softwareRequirementsArray.put(requirement);
        }
        // 将JSONArray转换为字符串并打印输出
        String jsonString = softwareRequirementsArray.toString(); // 参数4代表缩进字符数，用于美化输出
        return softwareRequirementsArray;
    }




    //List<String>列表转化为一整个字符串，为了调用gpt(将相同设备的设备功能合并在一起)
    public static String convertListToString(List<String> list) {
        StringBuilder result = new StringBuilder();
        String currentDevice = "";
        int itemNumber = 0;
        for (String item : list) {
            String[] parts = item.split(",");
            int newItemNumber = Integer.parseInt(parts[0]);
            String newDevice = parts[1];
            String description = parts[2];
            // 检查是否需要添加新的设备名称
            if (!newDevice.equals(currentDevice)) {
                if (!currentDevice.isEmpty()) {
                    result.append("\n\n");
                }
                currentDevice = newDevice;
                result.append(currentDevice).append("：\n");
            }
            // 添加条目描述
            result.append(newItemNumber).append(description).append("\n");
        }
        return result.toString();
    }


    public static String ListToStringExample(List<String> list) {

        String result = StringUtils.join(list, ", "); // 使用自定义分隔符
        return result;

    }


    //大模型推荐软件需求（没有设备知识库）
    @GetMapping("/NoLibRecommendSoftwareReq/gpt")
    public JSONArray RecByGPT(){


        GPTBaiduQianfanAI gptBaiduQianfanAI = new GPTBaiduQianfanAI();
        String content1 = "家属与智能手机之间的交互信息为监测值，老人与医用腕表之间的交互信息为身体状况，家庭医生与家庭医生工作站之间的交互信息为健康指标范围设置，" +
                "软件控制器应该发送什么指令给设备，可以达到以上目的"+
                "请返回以下格式的数据：[{\"id\": \"\",\" instructionContent\"\"},{\"id\": \"\",\" instructionContent\": \"\"}]";

        String content2 = "家属与智能手机之间的交互信息为监测值，老人与医用腕表之间的交互信息为身体状况，家庭医生与家庭医生工作站之间的交互信息为健康指标范围设置，" +
                "软件控制器应该发送什么指令给设备，可以达到以上目的" +
                "软件需求例如：太阳搜索系统将加电控制信号发送给太阳敏感器，太阳敏感器进行加电操作，实现太阳敏感器初始化功能。" +
                "软件需求是指控制器与设备之间的交互，请根据控制器与设备之间的交互，以及交互端口，推荐软件需求。"+
                "请返回以下格式的数据：[{\"id\": \"\",\" instructionContent\"\"},{\"id\": \"\",\" instructionContent\": \"\"}]" +
                "其中id为数字，不加引号";


        String result = gptBaiduQianfanAI.BaiduGPT(content2);
        System.out.println(result);


        // 正则表达式：匹配每个对象的 "entity", "interactiveInformation", 和 "device"
//        String regex = "\\{[^}]*\"id\":\\s*\"([^\"]+)\"[^}]*\"instructionContent\":\\s*\"([^\"]+)\"[^}]*\\}";
        String regex = "\\{[^}]*\"id\":\\s*([^\",]+)[^}]*\"instructionContent\":\\s*\"([^\"]+)\"[^}]*\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(result);

        // 创建一个 JSON 数组来存储匹配的结果
        JSONArray jsonArray = new JSONArray();

        // 查找所有匹配并将其转换为 JSON 对象
        while (matcher.find()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", matcher.group(1));
            jsonObject.put("instructionContent", matcher.group(2));
            jsonArray.put(jsonObject);
        }

        // 输出 JSON 数组
        System.out.println(jsonArray.toString());

        return jsonArray;





    }




}
