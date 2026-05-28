package com.zjq.springboot.controller;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.PictureRenderData;
import com.deepoove.poi.data.Pictures;
import com.zjq.springboot.entity.SelectedDevice;
import com.zjq.springboot.entity.SoftwareRequirement;
import com.zjq.springboot.entity.SystemReq;
import com.zjq.springboot.mapper.*;
import com.zjq.springboot.service.SystemReqService;
import com.zjq.springboot.service.TaskIntentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/documentGeneration")
public class DocumentGenerationController {
    @Autowired
    private TaskIntentService taskIntentService;
    @Autowired
    private TaskIntentMapper taskIntentMapper;
    @Autowired
    private DeviceLibraryMapper deviceLibraryMapper;
    @Autowired
    private SelectedDeviceMapper selectedDeviceMapper;
    @Autowired
    private SystemReqService systemReqService;
    @Autowired
    private SoftwareRequirementMapper softwareRequirementMapper;

    //查询所有数据，生成需求文档
    @GetMapping
    public void generateDocu() throws IOException {


        List<Object> taskIntentList = taskIntentMapper.findSentence();  //任务意图句子
        List<SelectedDevice> selectedDeviceList = selectedDeviceMapper.findAllSelected();  //已选设备的所有信息
        List<Object> deviceTypeList = selectedDeviceMapper.findDeviceType();  //已选设备类型,去重
        List<SystemReq> systemReqList = systemReqService.list();  //查询系统需求
        List<SoftwareRequirement> softwareRequirements = softwareRequirementMapper.findAllSelected();  //查询已选软件需求

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
            device.put("system", "老人监护软件系统");
            device.put("name", selectedDeviceList.get(i).getName());
            device.put("number", selectedDeviceList.get(i).getNumber());
            selectedDevicesWithIndex.add(device);
        }

        List<Map<String, Object>> systemReqListWithIndex = new ArrayList<>();
        for (int i = 0; i < systemReqList.size(); i++) {
            Map<String, Object> application = new HashMap<>();
            application.put("indexSys", i + 1); // 从1开始的序号
            application.put("systemSys", "智能家居系统");
            application.put("systemRequirement", systemReqList.get(i).getRequirement());
            application.put("description", systemReqList.get(i).getDescription());
            systemReqListWithIndex.add(application);
        }

        List<Map<String, Object>> softwareRequirementsWithIndex = new ArrayList<>();
        for (int i = 0; i < softwareRequirements.size(); i++) {
            Map<String, Object> softwareReq = new HashMap<>();
            softwareReq.put("indexSoftware", i + 1); // 从1开始的序号
            softwareReq.put("softwareReq", softwareRequirements.get(i).getDescription());
            softwareRequirementsWithIndex.add(softwareReq);
        }




        String path = "D:\\AppFile\\Idea\\工具\\springboot\\src\\main\\resources\\doc\\requirementSpecificationTemplate.docx";
        XWPFTemplate template = XWPFTemplate.compile(path);  //读取模板

        PictureRenderData img = Pictures.of("C:\\Users\\lenovo\\Downloads\\Context-Diagram.png")
                .size(525,300)
                .create();

        PictureRenderData imgProblem = Pictures.of("C:\\Users\\lenovo\\Downloads\\Problem-Diagram.png")
                .size(525,300)
                .create();




        Map<String, Object> map = new HashMap<>();  //在HashMap中准备数据,key是String
        map.put("system","老人监护软件系统");  //map里面的变量名称一定要跟模板里的一致
        map.put("taskIntent",taskIntentList);
        map.put("deviceType",deviceTypeList.get(0).toString()+','+deviceTypeList.get(1).toString());
        map.put("Type1",deviceTypeList.get(0));
        map.put("Type2",deviceTypeList.get(1));
//        map.put("Type3",deviceTypeList.get(2));

        for (int i = 0; i < deviceNameLists.size(); i++) {
            map.put("deviceName" + (i + 1), deviceNameLists.get(i));
        }


        map.put("dev",selectedDevicesWithIndex);
        map.put("systemReq",systemReqListWithIndex);
        map.put("softwareRequirement",softwareRequirementsWithIndex);

        map.put("image",img);
        map.put("imgProblem",imgProblem);

//        map.put("test",test);

        template.render(map);
        template.writeAndClose(Files.newOutputStream(Paths.get("D:\\AppFile\\Idea\\工具\\springboot\\src\\main\\resources\\doc\\requireDocument.docx")));
    }


    //查询所有数据，生成需求文档
    @GetMapping("/new")
    public void generateDocuNew() throws IOException {


        List<Object> taskIntentList = taskIntentMapper.findSentence();  //任务意图句子
        List<SelectedDevice> selectedDeviceList = selectedDeviceMapper.findAllSelected();  //已选设备的所有信息
        List<Object> deviceTypeList = selectedDeviceMapper.findDeviceType();  //已选设备类型,去重
        List<SystemReq> systemReqList = systemReqService.list();  //查询系统需求
        List<SoftwareRequirement> softwareRequirements = softwareRequirementMapper.findAllSelected();  //查询已选软件需求

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
            device.put("system", "老人监护系统");
            device.put("name", selectedDeviceList.get(i).getName());
            device.put("number", selectedDeviceList.get(i).getNumber());
            selectedDevicesWithIndex.add(device);
        }

        List<Map<String, Object>> systemReqListWithIndex = new ArrayList<>();
        for (int i = 0; i < systemReqList.size(); i++) {
            Map<String, Object> application = new HashMap<>();
            application.put("indexSys", i + 1); // 从1开始的序号
            application.put("systemSys", "老人监护系统");
            application.put("systemRequirement", systemReqList.get(i).getRequirement());
            application.put("description", systemReqList.get(i).getDescription());
            systemReqListWithIndex.add(application);
        }

        List<Map<String, Object>> softwareRequirementsWithIndex = new ArrayList<>();
        for (int i = 0; i < softwareRequirements.size(); i++) {
            Map<String, Object> softwareReq = new HashMap<>();
            softwareReq.put("indexSoftware", i + 1); // 从1开始的序号
            softwareReq.put("softwareReq", softwareRequirements.get(i).getDescription());
            softwareRequirementsWithIndex.add(softwareReq);
        }




        String path = "D:\\AppFile\\Idea\\工具\\springboot\\src\\main\\resources\\doc\\requirementSpecificationTemplate.docx";
        XWPFTemplate template = XWPFTemplate.compile(path);  //读取模板

        PictureRenderData img = Pictures.of("C:\\Users\\lenovo\\Downloads\\Context-Diagram.png")
                .size(525,300)
                .create();

        PictureRenderData imgProblem = Pictures.of("C:\\Users\\lenovo\\Downloads\\Problem-Diagram.png")
                .size(525,300)
                .create();




        Map<String, Object> map = new HashMap<>();  //在HashMap中准备数据,key是String
        map.put("system","老人监护软件系统");  //map里面的变量名称一定要跟模板里的一致
        map.put("taskIntent",taskIntentList);
        map.put("deviceType",deviceTypeList.get(0).toString()+','+deviceTypeList.get(1).toString());
        map.put("Type1",deviceTypeList.get(0));
        map.put("Type2",deviceTypeList.get(1));
//        map.put("Type3",deviceTypeList.get(2));

        for (int i = 0; i < deviceNameLists.size(); i++) {
            map.put("deviceName" + (i + 1), deviceNameLists.get(i));
        }


        map.put("dev",selectedDevicesWithIndex);
        map.put("systemReq",systemReqListWithIndex);
        map.put("softwareRequirement",softwareRequirementsWithIndex);

        map.put("image",img);
        map.put("imgProblem",imgProblem);

//        map.put("test",test);

        template.render(map);
        template.writeAndClose(Files.newOutputStream(Paths.get("D:\\AppFile\\Idea\\工具\\springboot\\src\\main\\resources\\doc\\requireDocument.docx")));
    }
}
