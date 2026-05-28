package com.zjq.springboot.controller;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.PictureRenderData;
import com.deepoove.poi.data.Pictures;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.zjq.springboot.Utils.GPTBaiduQianfanAI;
import com.zjq.springboot.entity.ApplicationList;
import com.zjq.springboot.entity.SelectedDevice;
import com.zjq.springboot.entity.SoftwareRequirement;
import com.zjq.springboot.entity.TaskIntent;
import com.zjq.springboot.mapper.*;
import com.zjq.springboot.service.TaskIntentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/taskIntent")
public class TaskIntentController {

    @Autowired
    private TaskIntentService taskIntentService;
    @Autowired
    private TaskIntentMapper taskIntentMapper;
    @Autowired
    private DeviceLibraryMapper deviceLibraryMapper;
    @Autowired
    private SelectedDeviceMapper selectedDeviceMapper;
    @Autowired
    private ApplicationListMapper applicationListMapper;
    @Autowired
    private SoftwareReqRecommendationMapper softwareReqRecommendationMapper;



    //查询所有数据
    @GetMapping
    public List<TaskIntent> findAll(){
        return taskIntentService.list();
    }


    //新增和修改
    @PostMapping
    public boolean save(@RequestBody TaskIntent taskIntent){
        //新增或者更新
        //return taskIntentService.saveTaskIntent(taskIntent);
        taskIntentService.saveTaskIntent(taskIntent);
        taskIntentMapper.updateSentence(taskIntent);
        return taskIntentService.saveTaskIntent(taskIntent);
    }


    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id){  //[1,2,3]
        return taskIntentService.removeById(id);
    }

    @PostMapping("/sentences")
    public boolean addSentences(@RequestBody String text) {

        // 去掉首尾的双引号
        if (text.startsWith("\"") && text.endsWith("\"")) {
            text = text.substring(1, text.length() - 1);
        }

        // 将文本按句号分割为数组
        String[] sentences = text.split("。");

        // 遍历数组，将每个句子存入数据库
        Arrays.stream(sentences).forEach(sentenceText -> {
            if (!sentenceText.trim().isEmpty()) {
                TaskIntent sentence = new TaskIntent();
                sentence.setSentence(sentenceText.trim() + "。"); // 加回句号
                taskIntentService.saveTaskIntent(sentence);
            }
        });

        return true;
    }

    //查询所有数据，生成需求文档
    @GetMapping("/document")
    public void generateDocu() throws IOException {


        List<Object> taskIntentList = taskIntentMapper.findSentence();  //任务意图句子
        List<SelectedDevice> selectedDeviceList = selectedDeviceMapper.findAllSelected();  //已选设备的所有信息
        List<Object> deviceTypeList = selectedDeviceMapper.findDeviceType();  //已选设备类型,去重
        List<ApplicationList> applicationList = applicationListMapper.findAllSelected();  //查询已选系统需求
        List<SoftwareRequirement> softwareRequirements = softwareReqRecommendationMapper.findAllSelected();  //查询已选系统需求

        //每个类型的设备名称
        List<List<Object>> deviceNameLists = new ArrayList<>();
        for (int i = 0; i < deviceTypeList.size(); i++) {
            List<Object> deviceNameList = selectedDeviceMapper.findDeviceNameByType(deviceTypeList.get(i).toString());
            deviceNameLists.add(deviceNameList);
        }
        List<Object> deviceInterfaceList = deviceLibraryMapper.findDeviceInterface();

        List<Map<String, Object>> selectedDevicesWithIndex = new ArrayList<>();
        for (int i = 0; i < selectedDeviceList.size(); i++) {
            Map<String, Object> device = new HashMap<>();
            device.put("index", i + 1); // 从1开始的序号
            device.put("system", "智能家居系统");
            device.put("name", selectedDeviceList.get(i).getName());
            device.put("number", selectedDeviceList.get(i).getNumber());
            device.put("deviceinterface", selectedDeviceList.get(i).getDeviceinterface());
            device.put("physical", selectedDeviceList.get(i).getPhysical());
            device.put("intent", selectedDeviceList.get(i).getIntent());
            selectedDevicesWithIndex.add(device);
        }

        List<Map<String, Object>> softwareReqRecommendationsWithIndex = new ArrayList<>();
        for (int i = 0; i < softwareRequirements.size(); i++) {
            Map<String, Object> softwareReq = new HashMap<>();
            softwareReq.put("indexSys", i + 1); // 从1开始的序号
            softwareReq.put("softwareReq", softwareRequirements.get(i).getDescription());
            softwareReqRecommendationsWithIndex.add(softwareReq);
        }

        List<Map<String, Object>> applicationListWithIndex = new ArrayList<>();
        for (int i = 0; i < applicationList.size(); i++) {
            Map<String, Object> application = new HashMap<>();
            application.put("indexSys", i + 1); // 从1开始的序号
            application.put("systemSys", "智能家居系统");
            application.put("nameSys", applicationList.get(i).getName());
            application.put("physicalSys", applicationList.get(i).getPhysical());
            application.put("intentSys", applicationList.get(i).getIntent());
            applicationListWithIndex.add(application);
        }

//        List<Object> test = new ArrayList<>();
//        if(SelectedDeviceList.contains("太阳敏感器")) test.add("太阳敏感器：数据采集、设备初始化、故障诊断");
//        if(SelectedDeviceList.contains("陀螺")) test.add("陀螺：数据采集、设备初始化、故障诊断");
//        if(SelectedDeviceList.contains("推力器")) test.add("推力器：控制计算、设备初始化、故障诊断");
//        if(SelectedDeviceList.contains("数据管理计算机")) test.add("数据管理计算机：控制计算、遥控处理");
//        if(deviceTypeList.contains("采集器")) test.add("数据采集、设备初始化、故障诊断");
//        if(deviceTypeList.contains("执行器")) test.add("控制计算、设备初始化、故障诊断");
//        if(deviceTypeList.contains("控制器")) test.add("控制计算、遥控处理");

        String path = "D:\\AppFile\\Idea\\工具\\springboot\\src\\main\\resources\\doc\\requireDocuTemp.docx";
        XWPFTemplate template = XWPFTemplate.compile(path);  //读取模板

        PictureRenderData img = Pictures.of("C:\\Users\\lenovo\\Downloads\\Context-Diagram.png")
                .size(525,300)
                .create();

        PictureRenderData imgProblem = Pictures.of("C:\\Users\\lenovo\\Downloads\\Problem-Diagram.png")
                .size(525,300)
                .create();




        Map<String, Object> map = new HashMap<>();  //在HashMap中准备数据,key是String
        map.put("system","智能家居系统");  //map里面的变量名称一定要跟模板里的一致
        map.put("taskIntent",taskIntentList);
        map.put("deviceType",deviceTypeList.get(0).toString()+','+deviceTypeList.get(1).toString());
        map.put("Type1",deviceTypeList.get(0));
        map.put("Type2",deviceTypeList.get(1));
//        map.put("Type3",deviceTypeList.get(2));

        for (int i = 0; i < deviceNameLists.size(); i++) {
            map.put("deviceName" + (i + 1), deviceNameLists.get(i));
        }




        map.put("dev",selectedDevicesWithIndex);
        map.put("systemReq",applicationListWithIndex);
        map.put("softwareRequirement",softwareReqRecommendationsWithIndex);

        map.put("image",img);
        map.put("imgProblem",imgProblem);

//        map.put("test",test);

        template.render(map);
        template.writeAndClose(Files.newOutputStream(Paths.get("D:\\AppFile\\Idea\\工具\\springboot\\src\\main\\resources\\doc\\requireDocument.docx")));
    }

    @GetMapping("/entityidentification")
    public void generateNer(){
        String content = "设置卫星模式";
        List<String> keywordList = HanLP.extractKeyword(content, 1);
        System.out.println(keywordList);

        // 创建分词器
        Segment segment = HanLP.newSegment();
        // 对文本进行分词并进行词性标注
        List<Term> termList = segment.seg("太阳敏感器通过获得太阳测量角度和太阳可见标志，感知太阳位置");
        // 遍历分词结果，输出词汇和词性
        for (Term term : termList) {    System.out.println(term.word + " " + term.nature); }

        // 人名识别示例
        String[] test_person_case = new String[]{
                "签约仪式前，秦光荣、李纪恒、仇和等一同会见了参加签约的企业家。",
                "武大靖创世界纪录夺冠，中国代表团平昌首金",
                "区长庄木弟新年致辞",
                "朱立伦：两岸都希望共创双赢 习朱历史会晤在即",
                "陕西首富吴一坚被带走 与×××妻子有交集",
                "据美国之音电台网站4月28日报道，8岁的凯瑟琳·克罗尔（凤甫娟）和很多华裔美国小朋友一样，小小年纪就开始学小提琴了。她的妈妈是位虎妈么？",
                "凯瑟琳和露西（庐瑞媛），跟她们的哥哥们有一些不同。",
                "王国强、高峰、×××、张朝阳光着头、韩寒、小四",
                "张浩和胡健康复员回家了",
                "王总和小丽结婚了",
                "编剧邵钧林和稽道青说",
                "这里有关天培的有关事迹",
                "龚学平等领导说,×××生前杜绝超生",
                "蓝翔给宁夏固原市彭阳县红河镇黑牛沟村捐赠了挖掘机",
                "我在上海林原科技有限公司兼职工作，",
        "我经常在台川喜宴餐厅吃饭，",
                "偶尔去开元地中海影城看电影。",
                "不用词典，福哈生态工程有限公司是动态识别的结果。"
    };
        for (String sentence : test_person_case)
        {
            List<Term> termList1 = recognizeNER(sentence);
            System.out.println(sentence + "\t" + termList1.toString());
        }



    }

    // 识别中文人名、中文地名、中文机构名
    public static List<Term> recognizeNER(String text) {
        List<Term> ner_li = new LinkedList<Term>();
        Segment segment = HanLP.newSegment();
        List<Term> termList = segment.seg(text);
        for(Term term: termList) {
            String nature = term.nature != null ? term.nature.toString() : "空";
            if(nature.equals("nr")) {
                ner_li.add(term);
            }
            else if(nature.equals("ns")) {
                ner_li.add(term);
            }
            else if(nature.equals("nt")) {
                ner_li.add(term);
            }
        }
        return ner_li;
    }



    //大模型识别实体（没有设备知识库）
    @GetMapping("/NoLibEntity/gpt")
    public List<Map<String, String>> RecByGPT() {

        List<TaskIntent> taskIntent = taskIntentService.list();
        List<String> taskIntentStrings = new ArrayList<>();
        for (TaskIntent list : taskIntent) {
            taskIntentStrings.add(list.getSentence());
        }

        String content1 = convertListToString(taskIntentStrings);


        GPTBaiduQianfanAI gptBaiduQianfanAI = new GPTBaiduQianfanAI();
        String content =
                "老人家庭监护软件的任务意图为" + content1 +
                        "请你识别以上任务意图中的外部实体。" +
                        "请返回JSON格式的数据：[{\"entity\": \"\"},{\"entity\": \"\"}]";

        System.out.println("prompt"+content);
        String result = gptBaiduQianfanAI.BaiduGPT(content);
        System.out.println(result);


        // 输入的 JSON 格式字符串
        String jsonText = result;

        // 定义正则表达式
        String regex = "\"entity\":\\s*\"([^\"]+)\"";

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

        return entities;
    }

    //List<String>列表转化为一整个字符串，为了调用gpt
    public static String convertListToString(List<String> list) {
        // 使用 String.join() 方法将 List<String> 转换为一个用逗号分隔的字符串
        return String.join(", ", list);
    }

}
