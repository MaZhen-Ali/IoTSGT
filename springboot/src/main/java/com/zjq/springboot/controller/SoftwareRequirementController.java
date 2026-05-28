package com.zjq.springboot.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.zjq.springboot.Utils.GPTBaiduQianfanAI;
import com.zjq.springboot.entity.ContextDiagram;
import com.zjq.springboot.entity.SoftwareRequirement;
import com.zjq.springboot.entity.SystemReq;
import com.zjq.springboot.entity.TaskIntent;
import com.zjq.springboot.mapper.SoftwareRequirementMapper;
import com.zjq.springboot.service.SoftwareRequirementService;
import com.zjq.springboot.service.SystemReqService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/softwareRequirement")
public class SoftwareRequirementController {

    @Autowired
    private SoftwareRequirementMapper softwareRequirementMapper;
    @Autowired
    private SoftwareRequirementService softwareRequirementService;
    @Autowired
    private SystemReqService systemReqService;

    //查询所有数据
    @GetMapping
    public List<SoftwareRequirement> findAll(){
        return softwareRequirementService.list();
    }


    //按id选择软件需求
    @PostMapping("/selectSoftwareRequirement/{id}")
    public boolean selectSoftwareRequirement(@PathVariable Integer id){  //[1,2,3]
        return softwareRequirementMapper.selectById(id);
    }

    //按id取消选择软件需求
    @PostMapping("/cancelSelectSoftwareRequirement/{id}")
    public boolean cancelSoftwareRequirement(@PathVariable Integer id){  //[1,2,3]

        return softwareRequirementMapper.cancelSelectById(id);
    }


    //大模型根据用户输入的系统需求推荐软件需求（没有设备知识库）
    @GetMapping("/NoLibRecommendSoftwareRequirement/gpt")
    public JSONArray RecByGPT() {

        List<SystemReq> systemReq = systemReqService.list();
        List<String> systemReqStrings =  new ArrayList<>();
        for (SystemReq list : systemReq) {
            systemReqStrings.add(list.getDescription());
        }

        String content1 = ListToStringExample(systemReqStrings);

        GPTBaiduQianfanAI gptBaiduQianfanAI = new GPTBaiduQianfanAI();

        String content =
                "你是一名需求分析专家，现在我有一个嵌入式系统的系统需求列表。请根据以下系统需求，生成详细的软件需求。请注意以下几点：\n" +
                "系统需求：侧重于需求与设备及外部实体的交互，定义了系统如何与环境中的各种实体（如用户、硬件、外部系统等）进行互动，以完成预定的任务和目标。\n" +
                "软件需求：侧重于软件控制器与设备之间的交互，描述了软件系统如何控制和与硬件设备进行通信与交互，确保系统的功能得以实现。\n" +
                "参考上述系统需求与软件需求的区别，请你根据以上系统需求生成其对应的软件需求,主要针对软件控制器与物理设备之间的交互。交互的实现，通过什么接口，发送什么指令，主语为软件应该。\n" +
                "以下是系统需求：" +  content1 +
                "请返回JSON格式的数据：[{\"id\": \"\",\" instructionContent\": \"\"},{\"id\": \"\",\" instructionContent\": \"\"}]，其中id为数字，不加引号" ;

        String result = gptBaiduQianfanAI.BaiduGPT(content);
        System.out.println(result);

        // 正则表达式：匹配每个对象的 "entity", "interactiveInformation", 和 "device"
        String regex = "\\{[^}]*\"id\":\\s*([^\",]+)[^}]*\"instructionContent\":\\s*\"([^\"]+)\"[^}]*\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(result);

        // 输入的 JSON 格式字符串
        String jsonText = result;

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

        //大模型推荐的软件需求存入数据库
        for (Object obj : jsonArray) {
            JSONObject json = (JSONObject) obj;
            String description = json.getStr("instructionContent");

            softwareRequirementMapper.insertRequirement(description,"否");

        }

        return jsonArray;

    }

    public static String ListToStringExample(List<String> list) {
        String result = StringUtils.join(list, ", "); // 使用自定义分隔符
        return result;

    }

    //用户手动输入添加软件需求
    @PostMapping("/addSoftwareRequirement")
    public boolean addSoftwareRequirement(@RequestBody SoftwareRequirement requirement){
        return softwareRequirementMapper.insertRequirement(requirement.getDescription(), requirement.getFlag());

    }


    //按id删除
    @DeleteMapping("/delSoftwareRequirement/{id}")
    public boolean NoLibDelInfo(@PathVariable Integer id){
        return softwareRequirementService.removeById(id);
    }


    //修改
    @PostMapping("/editSoftwareRequirement")
    public boolean save(@RequestBody SoftwareRequirement requirement){

        return softwareRequirementMapper.editSoftwareRequirement(requirement.getDescription(),requirement.getId());
    }




}
