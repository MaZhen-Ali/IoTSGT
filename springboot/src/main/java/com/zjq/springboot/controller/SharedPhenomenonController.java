package com.zjq.springboot.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.zjq.springboot.Utils.GPTBaiduQianfanAI;
import com.zjq.springboot.controller.dto.IntentTaskDTO;
import com.zjq.springboot.entity.ApplicationList;
import com.zjq.springboot.entity.SharedPhenomenon;
import com.zjq.springboot.mapper.*;
import com.zjq.springboot.service.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.CosineSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sharedPhenomenon")
public class SharedPhenomenonController {

    @Autowired
    private SelectedDeviceService selectedDeviceService;
    @Autowired
    private SelectedDeviceMapper selectedDeviceMapper;
    @Autowired
    private TaskIntentService taskIntentService;
    @Autowired
    private TaskIntentMapper taskIntentMapper;
    @Autowired
    private SharedPhenomenonService sharedPhenomenonService;
    @Autowired
    private SharedPhenomenonMapper sharedPhenomenonMapper;
    @Autowired
    private DeviceLibraryMapper deviceLibraryMapper;
    @Autowired
    private DeviceLibraryService deviceLibraryService;
    @Autowired
    private ApplicationListMapper applicationListMapper;
    @Autowired
    private ApplicationListService applicationListService;


    //查询所有数据
    @GetMapping
    public List<SharedPhenomenon> findAll(){
        return sharedPhenomenonService.list();
    }


    //查询意图和物理量，计算其相似度
    @GetMapping("/calculateSimi")
    public List<IntentTaskDTO> calculateAll(){


        List<String> list1 = taskIntentMapper.findTask();
        System.out.println(list1);

        List<String> list2 = selectedDeviceMapper.findAllPhysical();
        System.out.println(list2);

//        List<Object> list3 = new ArrayList<>();
//        list3.add("iagf");
//        list3.add(123);
//        System.out.println(list3);
//
//        List<Object> list4 = new ArrayList<>();
//        list4.add("iagf");
//        list4.add(121);
//        System.out.println(list4);


        List<IntentTaskDTO> intentTaskDTOList= new ArrayList<IntentTaskDTO>();

        for (int i = 0; i < list1.size(); i++){

            String str1 = list1.get(i);
            Map<CharSequence, Integer> map1 = Arrays.stream(str1.split("")).collect(Collectors.toMap(c -> c, c -> 1, Integer::sum));

//            List<Double> similarityList = new ArrayList<>();
            for (int j = 0; j < list2.size(); j++) {
                IntentTaskDTO intentTaskDTO = new IntentTaskDTO();
                SharedPhenomenon sharedPhenomenon = new SharedPhenomenon();
                intentTaskDTO.setTask(str1);
                sharedPhenomenon.setTask(str1);
                String str2 = list2.get(j);
                intentTaskDTO.setPhysical(str2);
                sharedPhenomenon.setPhysical(str2);
                Map<CharSequence, Integer> map2 = Arrays.stream(str2.split("")).collect(Collectors.toMap(c -> c, c -> 1, Integer::sum));
                CosineSimilarity cosineSimilarity = new CosineSimilarity();
                double similarity = cosineSimilarity.cosineSimilarity(map1, map2);
                intentTaskDTO.setSimilarity(similarity);
                sharedPhenomenon.setSimilarity(similarity);
//                systemRequirementService.saveSystemRequirement(systemRequirement);!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                intentTaskDTOList.add(intentTaskDTO);
//                System.out.println("当前intentTaskDTOList0------------------------"+intentTaskDTOList);
//                System.out.println("当前intentTaskDTO------------------------"+intentTaskDTO);
//                System.out.println(str1+"|"+str2+"|"+"相似度（Cosine相似度）：" + similarity);
//                similarityList.add(similarity);
            }
//            System.out.println(similarityList);

//            System.out.println("intentTaskDTOList1:::"+intentTaskDTOList);
        }
//        System.out.println("intentTaskDTOList2:::"+intentTaskDTOList);
        return intentTaskDTOList;
    }

    //查询，根据相似度阈值
    @GetMapping("/select/{similarity}")
    public List<SharedPhenomenon> findBySimilarity(@PathVariable Double similarity){
        return sharedPhenomenonMapper.selectBySimilarity(similarity);
    }

    //gpt推荐系统需求
    @GetMapping("/select/gpt")
    public List<ApplicationList> RecommendSysReq(){


        List<ApplicationList> applicationList = applicationListService.list();

        List<String> applicationListStrings = new ArrayList<>();
        for (ApplicationList list : applicationList) {
            applicationListStrings.add(list.getId()+","+list.getName()+","+list.getApplication());
        }
        String content1 = convertListToString(applicationListStrings);
//        System.out.println("gpt输入，转换后的："+content1);

        String content2 = ListToStringExample(taskIntentMapper.findTask());
//        System.out.println("content2："+content2);


        GPTBaiduQianfanAI gptBaiduQianfanAI = new GPTBaiduQianfanAI();
        String content = content1+
                "以上是设备名称及设备功能列表，请根据我要达成的任务意图为我推荐设备及对应的功能列表。我要达成的任务意图是" +
                content2 +
                "请为我返回功能及对应的功能序号，" +
                "功能序号的格式为：（功能序号：数字）";
        String result = gptBaiduQianfanAI.BaiduGPT(content);
        System.out.println("gpt-content:"+content);
        System.out.println(result);

        List<Integer> numbers = extractNumbersFromString(result);
        System.out.println("转换之后的功能序号列表："+numbers);
        return applicationListMapper.selectBatchIds(numbers);


    }




    //获取gpt结果的功能序号
    public static List<Integer> extractNumbersFromString(String str){
        List<Integer> nums = new ArrayList<>();
        Pattern pattern = Pattern.compile("功能序号：(\\d+)"); // 正则表达式，匹配数字
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            String numberStr = matcher.group(1); // 提取匹配到的数字字符串
            nums.add(Integer.parseInt(numberStr)); // 将数字字符串转换为整数并添加到列表中
        }
    return nums;
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
    @GetMapping("/NoLibRecommendSharedPhenomenon/gpt")
    public JSONArray RecByGPT(){


        GPTBaiduQianfanAI gptBaiduQianfanAI = new GPTBaiduQianfanAI();
        String content =
                "需求分析师选择的外部实体为家属、老人、家庭医生。" +
                "选择的设备有智能手机、医用腕表、家庭医生工作站。" +
                "老人家庭监护软件的任务意图为上班时，家属能随时观察家中老人的生活起居。" +
                "能够实时监护老人身体状况，包括定时测量血压、体温、脉搏等。" +
                "家庭医生可以指定身体指标范围，如果指标超出了医生给定的安全范围，" +
                "监护人能及时得到通知。如果监控设备失效，监护人也能收到提示"+
                    "为了达成以上任务意图，选择的设备设备与外部实体之间传递的信息有哪些。" +
//                "为了达成以上任务意图，老人家庭监护软件使用的设备与外部实体进行交互的交互信息有哪些。" +
//                "交互信息是指设备和外部实体（如用户或其他系统）之间进行数据交换、控制命令传递或事件通知的具体内容。它描述了信息如何从一个参与方传递到另一个参与方，以及这些信息的形式和功能作用。" +
                "每一项里面只包含一个实体和一个设备，其中传递的信息为名词，" +
                "请返回以下格式的数据：[{\"entity\": \"\",\" interactiveInformation\": \"\",\" device\": \"\"},{\"entity\": \"\",\" interactiveInformation\": \"\",\" device\": \"\"}]" ;

        String result = gptBaiduQianfanAI.BaiduGPT(content);
        System.out.println(result);


        // 正则表达式：匹配每个对象的 "entity", "interactiveInformation", 和 "device"
        String regex = "\\{[^}]*\"entity\":\\s*\"([^\"]+)\"[^}]*\"interactiveInformation\":\\s*\"([^\"]+)\"[^}]*\"device\":\\s*\"([^\"]+)\"[^}]*\\}";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(result);

        // 创建一个 JSON 数组来存储匹配的结果
        JSONArray jsonArray = new JSONArray();

        // 查找所有匹配并将其转换为 JSON 对象
        while (matcher.find()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("entity", matcher.group(1));
            jsonObject.put("interactiveInformation", matcher.group(2));
            jsonObject.put("device", matcher.group(3));
            jsonArray.put(jsonObject);
        }

        // 输出 JSON 数组
        System.out.println(jsonArray.toString());

        return jsonArray;

    }




}
