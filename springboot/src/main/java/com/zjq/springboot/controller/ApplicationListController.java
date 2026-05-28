package com.zjq.springboot.controller;


import com.zjq.springboot.Utils.GPTBaiduQianfanAI;
import com.zjq.springboot.entity.ApplicationList;
import com.zjq.springboot.entity.DeviceLibrary;
import com.zjq.springboot.entity.TaskIntent;
import com.zjq.springboot.mapper.ApplicationListMapper;
import com.zjq.springboot.service.ApplicationListService;
import com.zjq.springboot.service.TaskIntentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/applicationList")
public class ApplicationListController {

    @Autowired
    private ApplicationListMapper applicationListMapper;

    @Autowired
    private ApplicationListService applicationListService;

    @Autowired
    private TaskIntentService taskIntentService;

    //查询所有数据
    @GetMapping
    public List<ApplicationList> findAll() {
        return applicationListService.list();
    }

    //查询已选系统需求
    @GetMapping("/selected")
    public List<ApplicationList> findAllSelected(){
        return applicationListMapper.findAllSelected();
    }

    //查询未选系统需求
    @GetMapping("/unselected")
    public List<ApplicationList> findAllUnSelected(){
        return applicationListMapper.findAllUnSelected();
    }

    @GetMapping("/Name")
    public List<ApplicationList> findNameByIntent() {
        return applicationListMapper.findNameByIntent();
    }

    //按id选择系统需求
    @PostMapping("/select/{id}")
    public boolean selectDevice(@PathVariable Integer id){  //[1,2,3]

        return applicationListMapper.selectById(id);
    }

    //按id取消选择系统需求
    @PostMapping("/cancelSelect/{id}")
    public boolean cancelSelectDevice(@PathVariable Integer id){  //[1,2,3]

        return applicationListMapper.cancelSelectById(id);
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
        System.out.println("gpt输入，转换后的："+content1);

        List<TaskIntent> taskIntentList = taskIntentService.list();
        List<String> taskIntentListStrings = new ArrayList<>();
        for (TaskIntent list : taskIntentList) {
            taskIntentListStrings.add(list.getTask());
        }
        System.out.println("taskIntentListStrings："+taskIntentListStrings);
        String content2 = ListToString(taskIntentListStrings);
        System.out.println("转换后的content2："+content2);


        GPTBaiduQianfanAI gptBaiduQianfanAI = new GPTBaiduQianfanAI();
        String content = content1+
                "以上是设备名称及设备功能列表，请根据我要达成的任务意图为我推荐设备及对应的功能列表。我要达成的任务意图是" +
                content2 +
                "。请为我返回功能及对应的功能序号，功能序号的格式为：（功能序号：2）";
        String result = gptBaiduQianfanAI.BaiduGPT(content);
        System.out.println(content);
        System.out.println(result);

        List<Integer> numbers = extractNumbersFromString(result);
        System.out.println("转换之后的功能序号列表："+numbers);
        return applicationListMapper.findUnSelectedById(numbers);
//        return applicationListMapper.selectBatchIds(numbers);

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



    //List<String>列表转化为一整个字符串，为了调用gpt
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



    //普通列表转换为字符串
    public String ListToString(List<String> list) {
        // 构造list
        String str = String.join(",", list);// StringUtils.join(list, ",");
        return str;
    }



}
