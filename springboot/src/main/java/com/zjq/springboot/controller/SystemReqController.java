package com.zjq.springboot.controller;


import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.google.gson.JsonObject;
import com.zjq.springboot.Utils.GPTBaiduQianfanAI;
import com.zjq.springboot.Utils.MultimodalGPTBaiduQianfanAI;
import com.zjq.springboot.controller.dto.SystemRequirementDTO;
import com.zjq.springboot.entity.SystemReq;
import com.zjq.springboot.entity.TaskIntent;
import com.zjq.springboot.mapper.SystemReqMapper;
import com.zjq.springboot.service.SystemReqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/systemReq")
public class SystemReqController {

    @Autowired
    private SystemReqService systemReqService;
    @Autowired
    private SystemReqMapper systemReqMapper;

    //查询所有数据
    @GetMapping
    public List<SystemReq> findAll(){
        return systemReqService.list();
    }

    //新增和修改
    @PostMapping("/add")
    public int save(@RequestBody SystemRequirementDTO dto){
        System.out.println(dto.getUserInput());
        System.out.println(dto.getSystemRequirement());

        return systemReqMapper.AddSystemRequirement(dto.getUserInput(), dto.getSystemRequirement());
    }


    @PostMapping("/addSystemRequirements")
    public boolean parseSystemRequirements(@RequestBody String input) {


        input = input.replace("\"", "");   //去掉双引号
        input = input.replace("\\n", "\n");  // 将字面量 '\n' 替换为实际的换行符
        System.out.println(input);
        String[] lines = input.split("\\r?\\n");  // 按行分割输入数据
        String currentCategory = null;
        System.out.println(Arrays.toString(lines));


        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (line.startsWith("系统shall")) {
                if (currentCategory != null) {
                    System.out.println(currentCategory);
                    currentCategory = currentCategory.replaceAll("：$", "");  //去掉冒号！！！注意是中文冒号
                    SystemReq requirement = new SystemReq();
                    requirement.setId(null);
                    requirement.setRequirement(currentCategory);
                    System.out.println("currentCategory:"+currentCategory);

                    requirement.setDescription(line);
                    System.out.println("line:"+line);
                    systemReqMapper.insert(requirement);  // 执行数据库插入操作
                }
            } else {
                // 如果是新的类别标题（例如：设置安全范围），则更新 currentCategory
                currentCategory = line;
            }
        }
        return true;
    }


    //大模型根据用户输入的系统需求推荐系统需求
    @GetMapping("/RecommendSystemRequirement/gpt")
    public JSONArray RecByGPT(@RequestParam String userInput) throws IOException {

        GPTBaiduQianfanAI gptBaiduQianfanAI = new GPTBaiduQianfanAI();

        String content = "你是一名需求分析专家。\n" +
                "现在我定义了一个嵌入式系统的“系统需求”模板，用于描述系统与外部实体、设备的交互方式\n" +
                "你必须严格遵循以下四种句式生成系统需求：" +
                "【系统需求模板】" +
                "句式一：系统shall<交互动词><交互信息>\n" +
                "句式二：系统shall enable<外部实体><交互操作>【from<外部实体/物理设备>】【by<物理设备>】\n" +
                "（其中from<外部实体/物理设备>by<物理设备>为可选项）\n" +
                "句式三：系统shall存储<>in<设计领域>（作为数据库的设计领域）\n" +
                "句式四：IF<计算|判断>，THEN<句式一|句式二>\n" +
                "【交互动词】：发送|修改|存储|设置|接收|采集|获取\n" +
                "【交互信息】：信号|数据|指令|状态\n" +
                "方括号【】表示为可选部分，英文介词如 from、by、in 必须保留为英文，不可翻译。" +
                "以上是系统需求的模板，" +
                "现在我的需求是" + userInput + "。\n" +
                "已有的设备：医用腕表、手机、家庭医生工作站，\n" +
                "外部实体：老人、住户家人、家庭医生，\n" +
                "请按照上述模板结合需求内容、设备、与外部实体，为我生成系统需求，\n" +
                "请返回JSON格式的数据：[{\"id\": \"\",\" systemRequirement\": \"\"},{\"id\": \"\",\" systemRequirement\": \"\"}]，其中id为数字，不加引号\n" +
                "注意事项：\n" +
                "只能使用定义的4种句式\n" +
                "严格使用指定的交互动词和交互信息\n" +
                "只返回 JSON 数据，不要附加解释或自然语言描述\n" +
                "示例：系统shall接收数据\n" +
                "示例：系统shall enable家庭医生设置指令from老人by医用腕表" ;


        System.out.println(content);

        String result = gptBaiduQianfanAI.BaiduGPT(content);
//        String result = MultimodalGPTBaiduQianfanAI.BaiduGPT(content);
        System.out.println(result);

        // 正则表达式：匹配每个对象的 "id", "systemRequirement"
        String regex = "\\{[^}]*\"id\":\\s*([^\",]+)[^}]*\"systemRequirement\":\\s*\"([^\"]+)\"[^}]*\\}";
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
            jsonObject.put("systemRequirement", matcher.group(2));
            jsonArray.put(jsonObject);
        }

        // 输出 JSON 数组
        System.out.println(jsonArray.toString());

        //大模型推荐的软件需求存入数据库
//        for (Object obj : jsonArray) {
//            JSONObject json = (JSONObject) obj;
//            String description = json.getStr("systemRequirement");
//
//            softwareRequirementMapper.insertRequirement(description,"否");
//        }

        return jsonArray;

    }


    //大模型根据用户输入的系统需求推荐系统需求（新的，直接根据任务意图推荐）
    @GetMapping("/RecommendSystemRequirement/gptNew")
    public JSONArray RecByGPTNew() throws IOException {

        GPTBaiduQianfanAI gptBaiduQianfanAI = new GPTBaiduQianfanAI();

        String content = "你是一名需求分析专家。\n" +
                "现在我定义了一个嵌入式系统的“系统需求”模板，用于描述系统与外部实体、设备的交互方式\n" +
                "系统需求描述模板\n" +
                "标准句式：<系统>需 集成/具备/通过<设备资源>（<硬件参数>），实现 <任务目标>，[满足<性能指标|环境要求>]。包括以下三种能力描述的句式。 \n" +
                "①感知能力：系统需通过<传感器>实时监测<物理量|环境参数>，[采样周期设置<设备约束阈值>|误差范围<硬件指标>] \n" +
                "例如：系统需通过温度传感器（单总线接口，测量范围-10~50℃）实时监测室内环境温度，采样周期设置 10 秒/次，误差范围≤±0.5℃。\n" +
                "②控制能力：系统需通过<执行器|驱动模块>对[控制实体]实现[控制动作]，[响应时间<设备模块上限>|<软件算法延迟>] \n" +
                "例如：系统需通过蜂鸣器与 LED 指示灯对异常温度状态实现声光报警（蜂鸣器 1kHz 持续鸣响，红色 LED 1Hz 闪烁），响应时间≤1秒\n" +
                "③计算与通信：系统需支持<通信协议|接口类型>，在<延迟|带宽>约束下完成<数据|指令>传输\n" +
                "我选择的设备是<HUAWEI WATCH D2医用腕表、 Xiaomi 15 Ultra手机、EmdoorPad 医疗平板，EZVIZ C6c 4K摄像头>，" +

                "任务意图是：“老人监护系统”应通过远程视频访问能力，实现家属在上班期间随时观察家中老人的生活起居，达成增强家属陪伴感与家庭安全感的目标。\n" +
                "“老人监护系统”应通过实时采集与定时测量能力，实现对老人血压、体温、脉搏等身体指标的持续监护，达成保障老人身体健康、预防突发风险的目标。\n" +
                "“老人监护系统”应通过医生设定指标范围管理与自动异常检测能力，实现当身体指标超出安全范围时及时向监护人推送通知，达成辅助家庭医生监护与及时干预健康风险的目标。\n" +
                "“老人监护系统”应通过设备运行状态监测与故障自检能力，实现当监控设备失效时（包括通信失效、供电失效、硬件故障）向监护人发送提示信息，达成确保系统稳定运行与监护连续性的目标。" +
                "请参照系统需求模板，对任务意图进行分解：将核心目标转化为系统能力（如“实时监测”对应 “感知能力”），将业务价值转化为性能指标，并结合设备参数生成含有功能逻辑、性能指标的系统需求列表。" +
                "请返回JSON格式的数据：[{\"id\": \"\",\" systemRequirement\": \"\"},{\"id\": \"\",\" systemRequirement\": \"\"}]，其中id为数字，不加引号\n" +
                "注意事项：\n" +
                "只能使用定义的4种句式\n" +
                "";


        System.out.println(content);

        String result = gptBaiduQianfanAI.BaiduGPT(content);
//        String result = MultimodalGPTBaiduQianfanAI.BaiduGPT(content);
        System.out.println(result);

        // 正则表达式：匹配每个对象的 "id", "systemRequirement"
        String regex = "\\{[^}]*\"id\":\\s*([^\",]+)[^}]*\"systemRequirement\":\\s*\"([^\"]+)\"[^}]*\\}";
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
            jsonObject.put("systemRequirement", matcher.group(2));
            jsonArray.put(jsonObject);
        }

        // 输出 JSON 数组
        System.out.println(jsonArray.toString());

        //大模型推荐的软件需求存入数据库
//        for (Object obj : jsonArray) {
//            JSONObject json = (JSONObject) obj;
//            String description = json.getStr("systemRequirement");
//
//            softwareRequirementMapper.insertRequirement(description,"否");
//        }

        return jsonArray;

    }
}
