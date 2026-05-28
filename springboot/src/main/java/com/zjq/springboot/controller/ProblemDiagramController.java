package com.zjq.springboot.controller;

import com.zjq.springboot.entity.ContextDiagram;
import com.zjq.springboot.entity.SystemReq;
import com.zjq.springboot.mapper.SystemReqMapper;
import com.zjq.springboot.service.SystemReqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/problemDiagram")
public class ProblemDiagramController {
    @Autowired
    private SystemReqMapper systemReqMapper;
    @Autowired
    private SystemReqService systemReqService;


    //（根据前端输入）提取、识别问题图<设备、实体、交互信息、需求引用、需求约束、需求>
    @PostMapping("/identify")
    public Map<String,List<String>> ExtractExample(@RequestBody String text){
        // 去掉首尾的双引号
        if (text.startsWith("\"") && text.endsWith("\"")) {
            text = text.substring(1, text.length() - 1);
        }

        System.out.println("输入的内容为："+text);

        // 示例输入字符串
        String input = "IF需要监测老人的身体状况 THEN通过医用腕表获取监测值";

        //定义正则表达式，匹配冒号前的“需求”
        String regexReq = "^(.+?)：";

        //定义正则表达式，匹配“系统shall enable家庭医生设置老人的身体安全指标范围by手机from老人”
        String RuleRegex1 = "系统shall enable([^\\s]+)(设置|测量|查看|通知|发送)([\\u4e00-\\u9fa5]+)(by[\\u4e00-\\u9fa5]+)?(from[\\u4e00-\\u9fa5]+)?[^\\n]*";

        //定义正则表达式，匹配“系统shall存储<>in<设计领域>”
        String RuleRegex2 = "系统shall(存储)([^\\s]+)in([^\\s]+)[^\\n]*";

        //定义正则表达式，匹配“系统shall<交互动词><交互信息>”
        String RuleRegex3 = "系统shall(设置|测量|查看|通知|发送)([\\u4e00-\\u9fa5]+)(by[\\u4e00-\\u9fa5]+)?(from[\\u4e00-\\u9fa5]+)?[^\\n]*";

        //规则四：
        //需求引用：设置、测量、查看、from
        //需求约束：存储、通知、by

        //规则五：IF THEN


        // 创建模式对象
        Pattern ReqPattern = Pattern.compile(regexReq);
        Pattern RulePattern1 = Pattern.compile(RuleRegex1);
        Pattern RulePattern2 = Pattern.compile(RuleRegex2);
        Pattern RulePattern3 = Pattern.compile(RuleRegex3);

        // 创建匹配器对象
        Matcher ReqMatcher = ReqPattern.matcher(text);
        Matcher RuleMatcher1 = RulePattern1.matcher(text);
        Matcher RuleMatcher2 = RulePattern2.matcher(text);
        Matcher RuleMatcher3 = RulePattern3.matcher(text);

        // 创建列表来存储提取的内容
        String result = "";
        List<String> RuleResults1 = new ArrayList<>();
        List<String> RuleResults2 = new ArrayList<>();
        List<String> RuleResults3 = new ArrayList<>();


        // 提取匹配的部分
        if (ReqMatcher.find()) {
            // 获取冒号前面的内容
            result = ReqMatcher.group(1);
            System.out.println("冒号前的字: " + result);
        }

        // 从IF部分提取内容
        if (RuleMatcher1.find()) {

            // 循环提取每个捕获组的值
            for (int i = 0; i <= RuleMatcher1.groupCount(); i++) {
                RuleResults1.add(RuleMatcher1.group(i));
            }

        }

        // 从THEN部分提取内容
        if (RuleMatcher2.find()) {

            // 循环提取每个捕获组的值
            for (int i = 0; i <= RuleMatcher2.groupCount(); i++) {
                RuleResults2.add(RuleMatcher2.group(i));
            }

        }

        // 从THEN部分提取内容
        if (RuleMatcher3.find()) {

            // 循环提取每个捕获组的值
            for (int i = 0; i <= RuleMatcher3.groupCount(); i++) {
                RuleResults3.add(RuleMatcher3.group(i));
            }

        }

        // 输出结果
        System.out.println("rule1中提取的内容：");
        System.out.println(RuleResults1.isEmpty());
        System.out.println(RuleResults1);
        for (String result1 : RuleResults1) {
            System.out.println(result1);
        }

        System.out.println("r2中提取的内容：");
        for (String result2 : RuleResults2) {
            System.out.println(result2);
        }

        System.out.println("r3中提取的内容：");
        for (String result3 : RuleResults3) {
            System.out.println(result3);
        }

        Map<String,List<String>> arrays = new HashMap<>();
        arrays.put("RuleResults1",RuleResults1);
        arrays.put("RuleResults2",RuleResults2);
        arrays.put("RuleResults3",RuleResults3);


        // 定义关键字集合
        List<String> validActions1 = Arrays.asList("设置","测量","查看");     //需求引用
        //设置
        List<String> validActions2 = Arrays.asList("通知");      //需求约束


        Map<String,List<String>> problemDiagramELement = new HashMap<>();

        List<String> problemDiagramRuel1AndElement1 = new ArrayList<>();
        List<String> problemDiagramRuel1AndElement2 = new ArrayList<>();
        List<String> problemDiagramRuel1AndElement3 = new ArrayList<>();
        List<String> problemDiagramRuel2AndElement1 = new ArrayList<>();
        List<String> problemDiagramRuel2AndElement2 = new ArrayList<>();
        List<String> problemDiagramRuel2AndElement3 = new ArrayList<>();
        List<String> problemDiagramRuel3AndElement1 = new ArrayList<>();
        List<String> problemDiagramRuel3AndElement2 = new ArrayList<>();
        List<String> problemDiagramRuel3AndElement3 = new ArrayList<>();

        if(RuleResults1.isEmpty()==false){
            problemDiagramRuel1AndElement1.add(RuleResults1.get(1));
            problemDiagramRuel1AndElement1.add(RuleResults1.get(3));
            problemDiagramRuel1AndElement1.add(RuleResults1.get(2));
            if(validActions1.contains(problemDiagramRuel1AndElement1.get(2))){
                problemDiagramRuel1AndElement1.add("需求引用");
            }else if(validActions2.contains(problemDiagramRuel1AndElement1.get(2))){
                problemDiagramRuel1AndElement1.add("需求约束");
            }
            problemDiagramRuel1AndElement1.add(result);


            if(RuleResults1.get(4)!=null){
                problemDiagramRuel1AndElement2.add(RuleResults1.get(4).replace("by", "").trim());
                problemDiagramRuel1AndElement2.add(RuleResults1.get(3));
                problemDiagramRuel1AndElement2.add("by");
                problemDiagramRuel1AndElement2.add("需求约束");
                problemDiagramRuel1AndElement2.add(result);
            }

            if(RuleResults1.get(5)!=null){
                problemDiagramRuel1AndElement3.add(RuleResults1.get(5).replace("from", "").trim());
                problemDiagramRuel1AndElement3.add(RuleResults1.get(3));
                problemDiagramRuel1AndElement3.add("from");
                problemDiagramRuel1AndElement3.add("需求引用");
                problemDiagramRuel1AndElement3.add(result);
            }
            System.out.println(problemDiagramRuel1AndElement1);
            System.out.println(problemDiagramRuel1AndElement2);
            System.out.println(problemDiagramRuel1AndElement3);
        }

        if(RuleResults2.isEmpty()==false){
            problemDiagramRuel2AndElement1.add(RuleResults2.get(3));
            problemDiagramRuel2AndElement1.add(RuleResults2.get(2));
            problemDiagramRuel2AndElement1.add(RuleResults2.get(1));
            problemDiagramRuel2AndElement1.add("需求约束");
            problemDiagramRuel2AndElement1.add(result);
            problemDiagramRuel2AndElement1.add("设计领域");
            System.out.println(problemDiagramRuel2AndElement1);

        }


        if(RuleResults3.isEmpty()==false){

            if(RuleResults3.get(3)!=null){
                problemDiagramRuel3AndElement1.add(RuleResults3.get(3).replace("by", "").trim());
                problemDiagramRuel3AndElement1.add(RuleResults3.get(2));
                problemDiagramRuel3AndElement1.add("by");
                problemDiagramRuel3AndElement1.add("需求约束");
                problemDiagramRuel3AndElement1.add(result);
            }

            if(RuleResults3.get(4)!=null){
                problemDiagramRuel3AndElement2.add(RuleResults3.get(4).replace("from", "").trim());
                problemDiagramRuel3AndElement2.add(RuleResults3.get(2));
                problemDiagramRuel3AndElement2.add("from");
                problemDiagramRuel3AndElement2.add("需求引用");
                problemDiagramRuel3AndElement2.add(result);
            }

            System.out.println(problemDiagramRuel3AndElement1);
            System.out.println(problemDiagramRuel3AndElement2);

        }

        problemDiagramELement.put("problemDiagramRuel1AndElement1",problemDiagramRuel1AndElement1);
        problemDiagramELement.put("problemDiagramRuel1AndElement2",problemDiagramRuel1AndElement2);
        problemDiagramELement.put("problemDiagramRuel1AndElement3",problemDiagramRuel1AndElement3);
        problemDiagramELement.put("problemDiagramRuel2AndElement1",problemDiagramRuel2AndElement1);
        problemDiagramELement.put("problemDiagramRuel3AndElement1",problemDiagramRuel3AndElement1);
        problemDiagramELement.put("problemDiagramRuel3AndElement2",problemDiagramRuel3AndElement2);





        return problemDiagramELement;


    }



    //（后端查询数据库）提取、识别问题图<设备、实体、交互信息、需求引用、需求约束、需求>
    @PostMapping("/build")
    public List<Map<String, List<String>>> ExtractProblemElement(){

//        调用测试
//        String Req = "监护老人";
//        String text = "系统shall测量身体状况from老人by医用腕表";
//        return extractFromText(text,Req);

        List<SystemReq> systemRequirementsList = systemReqService.list();
        System.out.println("systemRequirementsList:"+systemRequirementsList);

        List<Map<String, List<String>>> resultList = new ArrayList<>();

        for (SystemReq systemReq : systemRequirementsList) {
            String text = systemReq.getDescription();
            String Req = systemReq.getRequirement();
            Map<String, List<String>> oneResult = extractFromText(text, Req);
            resultList.add(oneResult);
        }

        System.out.println(resultList);

        return resultList;




    }



    //封装提取的方法
    private Map<String, List<String>> extractFromText(String text, String Req){

        //定义正则表达式，匹配“系统shall enable家庭医生设置老人的身体安全指标范围by手机from老人”
        String RuleRegex1 = "系统shall enable([^\\s]+)(设置|测量|查看|通知|发送)([\\u4e00-\\u9fa5]+)(by[\\u4e00-\\u9fa5]+)?(from[\\u4e00-\\u9fa5]+)?[^\\n]*";

        //定义正则表达式，匹配“系统shall存储<>in<设计领域>”
        String RuleRegex2 = "系统shall(存储)([^\\s]+)in([^\\s]+)[^\\n]*";

        //定义正则表达式，匹配“系统shall<交互动词><交互信息>”
        String RuleRegex3 = "系统shall(设置|测量|查看|通知|发送)([\\u4e00-\\u9fa5]+)(by[\\u4e00-\\u9fa5]+)?(from[\\u4e00-\\u9fa5]+)?[^\\n]*";

        //规则四：
        //需求引用：设置、测量、查看、from
        //需求约束：存储、通知、by

        //规则五：IF THEN


        // 创建模式对象
        Pattern RulePattern1 = Pattern.compile(RuleRegex1);
        Pattern RulePattern2 = Pattern.compile(RuleRegex2);
        Pattern RulePattern3 = Pattern.compile(RuleRegex3);

        // 创建匹配器对象
        Matcher RuleMatcher1 = RulePattern1.matcher(text);
        Matcher RuleMatcher2 = RulePattern2.matcher(text);
        Matcher RuleMatcher3 = RulePattern3.matcher(text);

        // 创建列表来存储提取的内容
        List<String> RuleResults1 = new ArrayList<>();
        List<String> RuleResults2 = new ArrayList<>();
        List<String> RuleResults3 = new ArrayList<>();


        // 从IF部分提取内容
        if (RuleMatcher1.find()) {

            // 循环提取每个捕获组的值
            for (int i = 0; i <= RuleMatcher1.groupCount(); i++) {
                RuleResults1.add(RuleMatcher1.group(i));
            }

        }

        // 从THEN部分提取内容
        if (RuleMatcher2.find()) {

            // 循环提取每个捕获组的值
            for (int i = 0; i <= RuleMatcher2.groupCount(); i++) {
                RuleResults2.add(RuleMatcher2.group(i));
            }

        }

        // 从THEN部分提取内容
        if (RuleMatcher3.find()) {

            // 循环提取每个捕获组的值
            for (int i = 0; i <= RuleMatcher3.groupCount(); i++) {
                RuleResults3.add(RuleMatcher3.group(i));
            }

        }

        // 输出结果
        System.out.println("rule1中提取的内容：");
        System.out.println(RuleResults1.isEmpty());
        System.out.println(RuleResults1);
        for (String result1 : RuleResults1) {
            System.out.println(result1);
        }

        System.out.println("r2中提取的内容：");
        for (String result2 : RuleResults2) {
            System.out.println(result2);
        }

        System.out.println("r3中提取的内容：");
        for (String result3 : RuleResults3) {
            System.out.println(result3);
        }

        Map<String,List<String>> arrays = new HashMap<>();
        arrays.put("RuleResults1",RuleResults1);
        arrays.put("RuleResults2",RuleResults2);
        arrays.put("RuleResults3",RuleResults3);


        // 定义关键字集合
        List<String> validActions1 = Arrays.asList("设置","测量","查看");     //需求引用
        //设置
        List<String> validActions2 = Arrays.asList("通知");      //需求约束


        Map<String,List<String>> problemDiagramELement = new HashMap<>();

        List<String> problemDiagramRuel1AndElement1 = new ArrayList<>();
        List<String> problemDiagramRuel1AndElement2 = new ArrayList<>();
        List<String> problemDiagramRuel1AndElement3 = new ArrayList<>();
        List<String> problemDiagramRuel2AndElement1 = new ArrayList<>();
        List<String> problemDiagramRuel2AndElement2 = new ArrayList<>();
        List<String> problemDiagramRuel2AndElement3 = new ArrayList<>();
        List<String> problemDiagramRuel3AndElement1 = new ArrayList<>();
        List<String> problemDiagramRuel3AndElement2 = new ArrayList<>();
        List<String> problemDiagramRuel3AndElement3 = new ArrayList<>();

        if(RuleResults1.isEmpty()==false){
            problemDiagramRuel1AndElement1.add(RuleResults1.get(1));
            problemDiagramRuel1AndElement1.add(RuleResults1.get(3));
            problemDiagramRuel1AndElement1.add(RuleResults1.get(2));
            if(validActions1.contains(problemDiagramRuel1AndElement1.get(2))){
                problemDiagramRuel1AndElement1.add("需求引用");
            }else if(validActions2.contains(problemDiagramRuel1AndElement1.get(2))){
                problemDiagramRuel1AndElement1.add("需求约束");
            }
            problemDiagramRuel1AndElement1.add(Req);


            if(RuleResults1.get(4)!=null){
                problemDiagramRuel1AndElement2.add(RuleResults1.get(4).replace("by", "").trim());
                problemDiagramRuel1AndElement2.add(RuleResults1.get(3));
                problemDiagramRuel1AndElement2.add("by");
                problemDiagramRuel1AndElement2.add("需求约束");
                problemDiagramRuel1AndElement2.add(Req);
            }

            if(RuleResults1.get(5)!=null){
                problemDiagramRuel1AndElement3.add(RuleResults1.get(5).replace("from", "").trim());
                problemDiagramRuel1AndElement3.add(RuleResults1.get(3));
                problemDiagramRuel1AndElement3.add("from");
                problemDiagramRuel1AndElement3.add("需求引用");
                problemDiagramRuel1AndElement3.add(Req);
            }
            System.out.println(problemDiagramRuel1AndElement1);
            System.out.println(problemDiagramRuel1AndElement2);
            System.out.println(problemDiagramRuel1AndElement3);
        }

        if(RuleResults2.isEmpty()==false){
            problemDiagramRuel2AndElement1.add(RuleResults2.get(3));
            problemDiagramRuel2AndElement1.add(RuleResults2.get(2));
            problemDiagramRuel2AndElement1.add(RuleResults2.get(1));
            problemDiagramRuel2AndElement1.add("需求约束");
            problemDiagramRuel2AndElement1.add(Req);
            problemDiagramRuel2AndElement1.add("设计领域");
            System.out.println(problemDiagramRuel2AndElement1);

        }


        if(RuleResults3.isEmpty()==false){

            if(RuleResults3.get(3)!=null){
                problemDiagramRuel3AndElement1.add(RuleResults3.get(3).replace("by", "").trim());
                problemDiagramRuel3AndElement1.add(RuleResults3.get(2));
                problemDiagramRuel3AndElement1.add("by");
                problemDiagramRuel3AndElement1.add("需求约束");
                problemDiagramRuel3AndElement1.add(Req);
            }

            if(RuleResults3.get(4)!=null){
                problemDiagramRuel3AndElement2.add(RuleResults3.get(4).replace("from", "").trim());
                problemDiagramRuel3AndElement2.add(RuleResults3.get(2));
                problemDiagramRuel3AndElement2.add("from");
                problemDiagramRuel3AndElement2.add("需求引用");
                problemDiagramRuel3AndElement2.add(Req);
            }

            System.out.println(problemDiagramRuel3AndElement1);
            System.out.println(problemDiagramRuel3AndElement2);

        }

        problemDiagramELement.put("problemDiagramRuel1AndElement1",problemDiagramRuel1AndElement1);
        problemDiagramELement.put("problemDiagramRuel1AndElement2",problemDiagramRuel1AndElement2);
        problemDiagramELement.put("problemDiagramRuel1AndElement3",problemDiagramRuel1AndElement3);
        problemDiagramELement.put("problemDiagramRuel2AndElement1",problemDiagramRuel2AndElement1);
        problemDiagramELement.put("problemDiagramRuel3AndElement1",problemDiagramRuel3AndElement1);
        problemDiagramELement.put("problemDiagramRuel3AndElement2",problemDiagramRuel3AndElement2);





        return problemDiagramELement;

    }


    //测试nlp库，提取名词
    @PostMapping("/test")
    public void test(@RequestBody String text){



    }

















    //提取问题图需求引用和需求约束(信息显示)
    @PostMapping("/1")
    public Map<String,List<String>> ExtractExample1(@RequestBody String text){
        // 去掉首尾的双引号
        if (text.startsWith("\"") && text.endsWith("\"")) {
            text = text.substring(1, text.length() - 1);
        }

        // 示例输入字符串
        String input = "IF需要监测老人的身体状况 THEN通过医用腕表获取监测值";

        //定义正则表达式，分别提取IF和THEN部分（信息显示问题框架）
        String ifRegex = "IF需要(监测|获取)([^\\s]+)的([^\\s]+)"; // 提取任务部分，如“老人”、“身体状况”
        String thenRegex = "THEN(需要|通过)([^\\s]+)(获取|采集|显示)([^\\s]+)"; // 提取工具和目标部分，如“医用腕表”和“监测值”

        // 创建模式对象
        Pattern ifPattern = Pattern.compile(ifRegex);
        Pattern thenPattern = Pattern.compile(thenRegex);

        // 创建匹配器对象
        Matcher ifMatcher = ifPattern.matcher(text);
        Matcher thenMatcher = thenPattern.matcher(text);

        // 创建列表来存储提取的内容
        List<String> ifResults = new ArrayList<>();
        List<String> thenResults = new ArrayList<>();

        // 从IF部分提取内容
        if (ifMatcher.find()) {

            ifResults.add(ifMatcher.group(2));
            ifResults.add(ifMatcher.group(3));

//            // 循环提取每个捕获组的值
//            for (int i = 0; i <= ifMatcher.groupCount(); i++) {
//                ifResults.add(ifMatcher.group(i));
//            }

        }

        // 从THEN部分提取内容
        if (thenMatcher.find()) {

            thenResults.add(thenMatcher.group(2));
            thenResults.add(thenMatcher.group(4));
//            // 循环提取每个捕获组的值
//            for (int i = 0; i <= thenMatcher.groupCount(); i++) {
//                thenResults.add(thenMatcher.group(i));
//            }

        }

        // 输出结果
        System.out.println("if中提取的内容：");
        for (String result : ifResults) {
            System.out.println(result);
        }

        System.out.println("then中提取的内容：");
        for (String result : thenResults) {
            System.out.println(result);
        }

        Map<String,List<String>> arrays = new HashMap<>();
        arrays.put("ifResults",ifResults);
        arrays.put("thenResults",thenResults);

        return arrays;


    }

    //提取问题图需求引用和需求约束（命令式）
    @PostMapping("/2")
    public Map<String,List<String>> ExtractExample2(@RequestBody String text){

        // 去掉首尾的双引号
        if (text.startsWith("\"") && text.endsWith("\"")) {
            text = text.substring(1, text.length() - 1);
        }

        // 示例输入字符串
        String input = "IF住户家人想知道监测值 THEN通过手机查看监测值";
        String input1 = "IF家庭医生想要设置安全范围 THEN通过家庭医生工作站设置周期和范围";


        //定义正则表达式，分别提取IF和THEN部分（命令式行为问题框架）
        String ifRegex = "IF([^\\s]+)(发送|想要|想知道)([^\\s]+)";
        String thenRegex = "THEN(需要|通过)([^\\s]+)(设置|查看)([^\\s]+)";

        // 创建模式对象
        Pattern ifPattern = Pattern.compile(ifRegex);
        Pattern thenPattern = Pattern.compile(thenRegex);

        // 创建匹配器对象
        Matcher ifMatcher = ifPattern.matcher(text);
        Matcher thenMatcher = thenPattern.matcher(text);

        // 创建列表来存储提取的内容
        List<String> ifResults = new ArrayList<>();
        List<String> thenResults = new ArrayList<>();

        // 从IF部分提取内容
        if (ifMatcher.find()) {
            ifResults.add(ifMatcher.group(1));
            ifResults.add(ifMatcher.group(3));

//            // 循环提取每个捕获组的值
//            for (int i = 0; i <= ifMatcher.groupCount(); i++) {
//                ifResults.add(ifMatcher.group(i));
//            }

        }

        // 从THEN部分提取内容
        if (thenMatcher.find()) {
            thenResults.add(thenMatcher.group(2));
            thenResults.add(thenMatcher.group(4));
//            // 循环提取每个捕获组的值
//            for (int i = 0; i <= thenMatcher.groupCount(); i++) {
//                thenResults.add(thenMatcher.group(i));
//            }

        }

        // 输出结果
        System.out.println("if中提取的内容：");
        for (String result : ifResults) {
            System.out.println(result);
        }

        System.out.println("then中提取的内容：");
        for (String result : thenResults) {
            System.out.println(result);
        }

        Map<String,List<String>> arrays = new HashMap<>();
        arrays.put("ifResults",ifResults);
        arrays.put("thenResults",thenResults);

        return arrays;






    }

    //提取问题图需求引用和需求约束（需求式行为）
    @PostMapping("/3")
    public Map<String,List<String>> ExtractExample3(@RequestBody String text){

        // 去掉首尾的双引号
        if (text.startsWith("\"") && text.endsWith("\"")) {
            text = text.substring(1, text.length() - 1);
        }
        // 示例输入字符串
        String input = "IF需要监测老人的身体状况 THEN通过医用腕表获取监测值";

        //定义正则表达式，分别提取IF和THEN部分（需求式行为问题框架）
//        String ifRegex = "需要控制([^\\s]+)的([^\\s]+)";
        String thenRegex = "需要(从|通过)([^\\s]+)获取([^\\s]+)";


        // 创建模式对象
        Pattern thenPattern = Pattern.compile(thenRegex);

        // 创建匹配器对象
        Matcher thenMatcher = thenPattern.matcher(text);

        // 创建列表来存储提取的内容
        List<String> ifResults = new ArrayList<>();
        List<String> thenResults = new ArrayList<>();


        // 从THEN部分提取内容
        if (thenMatcher.find()) {

            thenResults.add(thenMatcher.group(2));
            thenResults.add(thenMatcher.group(3));
//            // 循环提取每个捕获组的值
//            for (int i = 0; i <= thenMatcher.groupCount(); i++) {
//                thenResults.add(thenMatcher.group(i));
//            }

        }

        // 输出结果

        System.out.println("then中提取的内容：");
        for (String result : thenResults) {
            System.out.println(result);
        }

        Map<String,List<String>> arrays = new HashMap<>();
        arrays.put("thenResults",thenResults);

        return arrays;






    }


}
