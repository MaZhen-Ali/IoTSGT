package com.zjq.springboot;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class WordTest {

    //文本
    @Test
    public void test2() throws IOException{
        //word模板
        String path = "D:\\AppFile\\Idea\\工具\\springboot\\src\\main\\resources\\doc\\test2.docx";
        XWPFTemplate template = XWPFTemplate.compile(path);  //读取模板
        Map<String, Object> map = new HashMap<>();  //在HashMap中准备数据,key是String
        map.put("date","2024-11-26");  //map里面的变量名称一定要跟模板里的一致
        template.render(map);
        template.writeAndClose(Files.newOutputStream(Paths.get("D:\\AppFile\\Idea\\工具\\springboot\\src\\main\\resources\\doc\\text.docx")));
    }

    //表格
    @Test
    public void test3() throws IOException{
        //word模板
        String path = "D:\\AppFile\\Idea\\工具\\springboot\\src\\main\\resources\\doc\\tableTemp.docx";
        XWPFTemplate template = XWPFTemplate.compile(path);  //读取模板
        Map<String, Object> map = new HashMap<>();

        RowRenderData row0 = Rows.of("姓名","年龄","头像").center().textColor("FFFFFF").bgColor("6495ED").create();  //center居中，textColor字体颜色,bgColor设置背景颜色（要用16进制）
        RowRenderData row1 = Rows.of("张三","20").center().create();

        PictureRenderData urlImg = Pictures.ofUrl("https://img-blog.csdnimg.cn/522902589b3d4c21b30e7bc229897b41.png")
                .size(50,50).create();  //size设置图片大小
        CellRenderData cellRenderData = Cells.of(urlImg).create();  //创建单元格
        row1.addCell(cellRenderData);  //把单元格加到row1

        RowRenderData row2 = Rows.of("李四","22").center().create();
        row2.addCell(cellRenderData);
        TableRenderData tableRenderData = Tables.create(row0,row1,row2);

        map.put("table",tableRenderData);  //map里面的变量名称一定要跟模板里的一致
        template.render(map);
        template.writeAndClose(Files.newOutputStream(Paths.get("D:\\AppFile\\Idea\\工具\\springboot\\src\\main\\resources\\doc\\table.docx")));
    }


    //图片
    @Test
    public void test4() throws IOException{
        //word模板
        String path = "D:\\AppFile\\Idea\\工具\\springboot\\src\\main\\resources\\doc\\imgTemp.docx";
        XWPFTemplate template = XWPFTemplate.compile(path);  //读取模板
        Map<String, Object> map = new HashMap<>();

        PictureRenderData img = Pictures.of("D:\\AppFile\\Idea\\工具\\springboot\\src\\main\\resources\\doc\\hz.jpg").create();
        PictureRenderData streamImg = Pictures.ofStream(Files.newInputStream(Paths.get("D:\\AppFile\\Idea\\工具\\springboot\\src\\main\\resources\\doc\\hz.jpg"))).create();
        PictureRenderData urlImg = Pictures.ofUrl("https://img-blog.csdnimg.cn/522902589b3d4c21b30e7bc229897b41.png").create();

        map.put("img",img);  //map里面的变量名称一定要跟模板里的一致
        map.put("streamImg",streamImg);
        map.put("urlImg",urlImg);
        template.render(map);
        template.writeAndClose(Files.newOutputStream(Paths.get("D:\\AppFile\\Idea\\工具\\springboot\\src\\main\\resources\\doc\\img.docx")));
    }
}
