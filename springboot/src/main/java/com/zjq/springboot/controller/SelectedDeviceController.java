package com.zjq.springboot.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.baidubce.qianfan.model.chat.ChatResponse;
import com.baidubce.qianfan.util.Json;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhipu.oapi.service.v4.model.Choice;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import com.zjq.springboot.Utils.GPTBaiduQianfanAI;
import com.zjq.springboot.Utils.GPTZhiPuAI;
import com.zjq.springboot.controller.dto.DeviceNameDTO;
import com.zjq.springboot.entity.ApplicationList;
import com.zjq.springboot.entity.EntityList;
import com.zjq.springboot.entity.SelectedDevice;
import com.zjq.springboot.entity.TaskIntent;
import com.zjq.springboot.mapper.SelectedDeviceMapper;
import com.zjq.springboot.mapper.TaskIntentMapper;
import com.zjq.springboot.service.ApplicationListService;
import com.zjq.springboot.service.EntityListService;
import com.zjq.springboot.service.SelectedDeviceService;
import com.zjq.springboot.service.TaskIntentService;
import org.apache.catalina.WebResourceRoot;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/selectedDevice")
public class SelectedDeviceController {

    @Autowired
    private SelectedDeviceService selectedDeviceService;
    @Autowired
    private SelectedDeviceMapper selectedDeviceMapper;
    @Autowired
    private ApplicationListService applicationListService;
    @Autowired
    private TaskIntentMapper taskIntentMapper;
    @Autowired
    private TaskIntentService taskIntentService;
    @Autowired
    private EntityListService entityListService;

    //查询所有已选设备
    @GetMapping("/selected")
    public List<SelectedDevice> findAll(){
        return selectedDeviceService.list();
    }

    //查询所有未选设备
    @GetMapping("/unselected")
    public List<SelectedDevice> findAllUnSelected(){

        return selectedDeviceMapper.findAllUnSelected();
    }

    //修改
    @PostMapping
    public boolean save(@RequestBody SelectedDevice selectedDevice){

        selectedDeviceService.saveSelectedDevice(selectedDevice);
        return selectedDeviceService.saveSelectedDevice(selectedDevice);
    }

    //按id选择设备
    @PostMapping("/select/{id}")
    public boolean selectById(@PathVariable Integer id){
        String selectedDeviceName = selectedDeviceMapper.findNameById(id);
        boolean flag = selectedDeviceMapper.selectById(id);
        //选择设备数量默认为1
        selectedDeviceMapper.initNum(selectedDeviceName);

        return flag;
    }

    //按id编辑设备个数
    //Mybatis报错: Parameter ‘XXX‘ not found. Available parameters are [arg1, arg0, param1, param2]解决方案及问题根源
    //mapper层加@Param("id")
    @PostMapping("/select/num")
    public boolean updateNumById(@RequestBody List<SelectedDevice> selectedDevices){
        for (SelectedDevice device : selectedDevices) {

            selectedDeviceMapper.updateNumById(device.getName(),device.getNumber());
            // 在这里处理设备数据，比如更新数据库
            System.out.println("设备ID: " + device.getName() + ", 数量: " + device.getNumber());
        }
        return true;
    }


    //按id取消选择已选设备
    @DeleteMapping("/cancelSelect/{id}")
    public boolean deleteById(@PathVariable Integer id){  //[1,2,3]
        return selectedDeviceService.removeById(id);
    }


    //大模型推荐设备
    @GetMapping("/gpt")
    public String BaiduGPT(){
        List<ApplicationList> applicationList = applicationListService.list();

        List<String> applicationListStrings = new ArrayList<>();
        for (ApplicationList list : applicationList) {
            applicationListStrings.add(list.getId()+","+list.getName()+","+list.getApplication());
        }
        String content1 = convertListToString(applicationListStrings);
        String content2 = ListToStringExample(taskIntentMapper.findTask());

        GPTBaiduQianfanAI gptBaiduQianfanAI = new GPTBaiduQianfanAI();
        String content = content1+
                "以上是设备名称及设备功能列表，请根据我要达成的任务意图为我推荐设备。我要达成的任务意图是" +
                content2 +
                "请将推荐设备的设备名称返回给我，不需要具体原因，请注意只要推荐的设备名称，不需要其功能";

        String result = gptBaiduQianfanAI.BaiduGPT(content);
        System.out.println(result);

        //字符串转换成json格式
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("key1", result);
        String resultJson = JSON.toJSONString(map);

        return resultJson;

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



    //大模型推荐设备（没有设备知识库）
    @GetMapping("/NoLibRecommendDevice/gpt")
    public List<Map<String, String>> RecByGPT(){

        List<TaskIntent> taskIntent = taskIntentService.list();
        List<EntityList> entityList = entityListService.list();
        List<String> taskIntentStrings = new ArrayList<>();
        for (TaskIntent list : taskIntent) {
            taskIntentStrings.add(list.getSentence());
        }
        List<String> entityListStrings = new ArrayList<>();
        for (EntityList list : entityList) {
            entityListStrings.add(list.getEntity());
        }

        String content1 = convertListToStringEasy(taskIntentStrings);
        String content2 = convertListToStringEasy(entityListStrings);




        GPTBaiduQianfanAI gptBaiduQianfanAI = new GPTBaiduQianfanAI();
        String content =
                "需求分析师选择的外部实体为" + content2 +
                "老人家庭监护软件的任务意图为" + content1 +
                "为了达成以上任务意图，老人家庭监护软件应该使用哪些设备与已选择外部实体进行交互呢，请你把具体的可能用到的设备列出来。" +
                "包括设备类型、设备能力和设备接口" +
                "设备类型为采集器或者执行器" +
                "请返回JSON格式的数据：[{\"deviceName\": \"\"},{\"deviceName\": \"\"}]" ;

        String result = gptBaiduQianfanAI.BaiduGPT(content);
        System.out.println(result);


        // 输入的 JSON 格式字符串
        String jsonText = result;

        // 定义正则表达式
        String regex = "\"deviceName\":\\s*\"([^\"]+)\"";

        // 创建模式对象
        Pattern pattern = Pattern.compile(regex);

        // 创建匹配器对象
        Matcher matcher = pattern.matcher(jsonText);

        // 使用一个集合存储所有匹配的实体名称
        List<String> entityNames = new ArrayList<>();
        List<Map<String, String>> entities = new ArrayList<>();

        // 查找并提取所有匹配的内容
        while (matcher.find()) {
            // 提取捕获的组（即每个 entityName 的值）
            entityNames.add(matcher.group(1));
            Map<String, String> entity = new HashMap<>();
            entity.put("name", matcher.group(1));
            entities.add(entity);
        }


//        // 使用一个集合存储所有匹配的实体名称和类型
//        List<Map<String, String>> entities = new ArrayList<>();
//
//        // 查找并提取所有匹配的内容
//        while (matcher.find()) {
//            // 提取捕获的组（即每个 entityName 和 entityType 的值）
//            Map<String, String> entity = new HashMap<>();
//            entity.put("entityName", matcher.group(1));
//            entity.put("entityType", matcher.group(2));
//            entities.add(entity);
//        }
//
//        // 输出所有提取的实体名称和类型
//        System.out.println("匹配到的实体信息: " + entities);


//        //字符串转换成json格式
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("key1", "1. 智能血压计：用于定时或手动测量老人的血压，并将数据传输到软件平台进行分析和存储。");
//        map.put("key2", "2. 智能体温计：用于测量老人的体温，可以与软件平台连接，实时显示体温数据。");
//        map.put("key3", "3. 智能脉搏仪：用于测量老人的脉搏，可以与软件平台配合使用，提供实时脉搏数据。");
//        map.put("key4", "4. 智能摄像头/监控系统：让家属能够随时观察老人的生活起居。");
//        map.put("key5", "5. 健康监护设备：集成血压计、体温计、脉搏仪等功能的综合健康监测设备，可以实时上传数据到软件平台。");
//        map.put("key6", "6. 家庭医生监控平台接口：连接软件与家庭医生或医疗机构之间的接口，实现身体指标数据的实时上传与通知。");
//        map.put("key7", "7. 智能传感器网络：用于监测家中的环境因素，如温度、湿度等，以保障老人的居住环境安全。");
//        map.put("key8", "8. 移动设备（如智能手机或平板电脑）：用于安装监护软件，并接收来自设备的实时数据和警报通知。");
//        String resultJson = JSON.toJSONString(map);

        return entities;

    }


    //List<String>列表转化为一整个字符串，为了调用gpt
    public static String convertListToStringEasy(List<String> list) {
        // 使用 String.join() 方法将 List<String> 转换为一个用逗号分隔的字符串
        return String.join(", ", list);
    }

    //大模型推荐设备（没有设备知识库）
    @PostMapping("/NoLibRecommendDeviceApplicationList/gpt")
    public JSONArray RecDeviceApplicationListByGPT(@RequestBody String name){


        GPTBaiduQianfanAI gptBaiduQianfanAI = new GPTBaiduQianfanAI();
        String content =
                "在老人监护软件系统中，请你根据设备名称填写其他设备属性，" +
                        "设备名称为：" + name +
                        "其他属性包括设备类型、设备能力和设备接口" +
                        "设备类型为采集器或者执行器" +
                        "请返回JSON格式的数据：[{\"type\": \"\",\" deviceCapacity\": \"\",\" deviceInterface\": \"\"},{\"type\": \"\",\" deviceCapacity\": \"\",\" deviceInterface\": \"\"}]" ;

        System.out.println(content);

        String result = gptBaiduQianfanAI.BaiduGPT(content);
        System.out.println(result);


        // 正则表达式：匹配每个对象的 "entity", "interactiveInformation", 和 "device"
        String regex = "\\{[^}]*\"type\":\\s*\"([^\"]+)\"[^}]*\"deviceCapacity\":\\s*\"([^\"]+)\"[^}]*\"deviceInterface\":\\s*\"([^\"]+)\"[^}]*\\}";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(result);

        // 创建一个 JSON 数组来存储匹配的结果
        JSONArray jsonArray = new JSONArray();

        // 查找所有匹配并将其转换为 JSON 对象
        while (matcher.find()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", matcher.group(1));
            jsonObject.put("deviceCapacity", matcher.group(2));
            jsonObject.put("deviceInterface", matcher.group(3));
            jsonArray.put(jsonObject);
        }

        // 输出 JSON 数组
        System.out.println(jsonArray.toString());

        return jsonArray;


//        // 使用一个集合存储所有匹配的实体名称和类型
//        List<Map<String, String>> entities = new ArrayList<>();
//
//        // 查找并提取所有匹配的内容
//        while (matcher.find()) {
//            // 提取捕获的组（即每个 entityName 和 entityType 的值）
//            Map<String, String> entity = new HashMap<>();
//            entity.put("entityName", matcher.group(1));
//            entity.put("entityType", matcher.group(2));
//            entities.add(entity);
//        }
//
//        // 输出所有提取的实体名称和类型
//        System.out.println("匹配到的实体信息: " + entities);


//        //字符串转换成json格式
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("key1", "1. 智能血压计：用于定时或手动测量老人的血压，并将数据传输到软件平台进行分析和存储。");
//        map.put("key2", "2. 智能体温计：用于测量老人的体温，可以与软件平台连接，实时显示体温数据。");
//        map.put("key3", "3. 智能脉搏仪：用于测量老人的脉搏，可以与软件平台配合使用，提供实时脉搏数据。");
//        map.put("key4", "4. 智能摄像头/监控系统：让家属能够随时观察老人的生活起居。");
//        map.put("key5", "5. 健康监护设备：集成血压计、体温计、脉搏仪等功能的综合健康监测设备，可以实时上传数据到软件平台。");
//        map.put("key6", "6. 家庭医生监控平台接口：连接软件与家庭医生或医疗机构之间的接口，实现身体指标数据的实时上传与通知。");
//        map.put("key7", "7. 智能传感器网络：用于监测家中的环境因素，如温度、湿度等，以保障老人的居住环境安全。");
//        map.put("key8", "8. 移动设备（如智能手机或平板电脑）：用于安装监护软件，并接收来自设备的实时数据和警报通知。");
//        String resultJson = JSON.toJSONString(map);


    }


    @PostMapping("/NoLibSelectDevice")
    public boolean NoLibSelectDevice(@RequestBody List<DeviceNameDTO> selectedDevices){

        for (DeviceNameDTO device : selectedDevices) {

            selectedDeviceMapper.addDeviceByName(device.getName());
            System.out.println(device.getName());
            System.out.println(device);
        }
        return true;
    }


    @PostMapping("/NoLibAddDevice")
    public boolean NoLibAddDevice(@RequestBody String selectedName){
        // 去掉首尾的双引号
        if (selectedName.startsWith("\"") && selectedName.endsWith("\"")) {
            selectedName = selectedName.substring(1, selectedName.length() - 1);
        }
        selectedDeviceMapper.addDeviceByName(selectedName);
        return true;
    }








}
