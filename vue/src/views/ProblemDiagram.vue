<template>
  <el-container style="min-height: 100vh">

    <el-aside :width="sideWidth + 'px' " style="background-color: rgb(238, 241, 246);box-shadow: 2px 0 6px rgb(0 21 41 / 35%);">
      <Aside :isCollapse="isCollapse" :logoTextShow="logoTextShow" />

    </el-aside>

    <el-container>

      <el-header style="border-bottom: 1px solid #ccc;">
        <Header :collapseBtnClass="collapseBtnClass" :collapse="collapse" />
      </el-header>

      <el-main>
        <el-button @click="genProblemDia">问题图<i class="el-icon-check"></i></el-button>

        <el-button @click="selectByType1">系统-设备<i class="el-icon-check"></i></el-button>

        <el-button @click="selectByType2">设备-实体<i class="el-icon-check"></i></el-button>

        <el-button @click="selectByType3">意图-实体<i class="el-icon-check"></i></el-button>

        <el-button @click="saveAsImage">保存为图像</el-button>
        <el-button @click="saveAsSVG">保存为SVG</el-button>
        <el-button @click="saveAsJSON">保存为JSON</el-button>

        <div style="display: flex; align-items: center; gap: 10px;">
          <el-input v-model="inputRequirement" placeholder="请输入需求" :style="{ width: '150px' }"></el-input>
<!--          <el-input v-model="inputEnd" placeholder="请输入终点" :style="{ width: '150px' }"></el-input>-->
          <el-input v-model="inputInfo" placeholder="请输入信息" :style="{ width: '150px' }"></el-input>
        </div>

<!--        <div style="display: flex; align-items: center; gap: 10px;">-->
<!--          <div>-->
<!--            <el-radio-group v-model="radio">-->
<!--              <el-radio-button label="温度传感器"></el-radio-button>-->
<!--              <el-radio-button label="湿度传感器"></el-radio-button>-->
<!--              <el-radio-button label="磁性报警接触开关"></el-radio-button>-->
<!--              <el-radio-button label="显示器"></el-radio-button>-->
<!--              <el-radio-button label="摄像头"></el-radio-button>-->
<!--              <el-radio-button label="浴缸"></el-radio-button>-->
<!--              <el-radio-button label="智能家居窗帘"></el-radio-button>-->
<!--              <el-radio-button label="清洁机器人"></el-radio-button>-->
<!--              <br>-->
<!--              <el-radio-button label="室内外空气"></el-radio-button>-->
<!--              <el-radio-button label="门窗"></el-radio-button>-->
<!--              <el-radio-button label="陌生人"></el-radio-button>-->
<!--              <el-radio-button label="水"></el-radio-button>-->
<!--              <el-radio-button label="地板/地毯"></el-radio-button>-->
<!--              <el-radio-button label="用户"></el-radio-button>-->
<!--              <el-radio-button label="障碍物"></el-radio-button>-->
<!--            </el-radio-group>-->
<!--          </div>-->
<!--          <el-button @click="addRequirement">添加</el-button>-->
<!--        </div>-->

        <div>
          <el-checkbox-group v-model="checkbox">
            <el-checkbox-button v-for="city in cities" :label="city" :key="city">{{city}}</el-checkbox-button>
          </el-checkbox-group>

          <el-button @click="addRequirements">添加s</el-button>
        </div>

        <div>

<!--          <el-input v-model="inputReq" placeholder="请输入需求" :style="{ width: '150px' }"></el-input>-->
          <el-input v-model="inputInfomation" placeholder="请输入信息" :style="{ width: '150px' }"></el-input>
          <el-select v-model="ifValue" clearable placeholder="请选择">
            <el-option
                v-for="(item, index) in ifOptions"
                :key="index"
                :label="item.label"
                :value="index">
            </el-option>
          </el-select>

            <el-select v-model="thenValue" clearable placeholder="请选择">
              <el-option
                  v-for="(item, index) in thenOptions"
                  :key="index"
                  :label="item.label"
                  :value="index">
              </el-option>
            </el-select>

          <el-button @click="addReq">添加</el-button>

        </div>

        <div>

          <el-select v-model="value" clearable placeholder="请选择">
            <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value">
            </el-option>
          </el-select>

          <el-input v-model="inputSentence" placeholder="请输入内容"></el-input>


          <el-button @click="addExtract">确定</el-button>

        </div>

        <div>

          <el-input v-model="inputReq" placeholder="请输入内容"></el-input>

          <el-input type="textarea" v-model="inputSystemReq" placeholder="系统需求：系统shall<系统行为>"></el-input>




          <el-button @click="addIdentify">确定</el-button>

        </div>




        <div id="paper" class="canvas" ref="canvas" style="width: 1600px; height: 800px; border: 1px solid #ccc;"></div>




      </el-main>





    </el-container>




  </el-container>

</template>

<script>
import Aside from "@/components/Aside.vue";
import Header from "@/components/Header.vue";
import * as joint from "jointjs";
import html2canvas from "html2canvas";

// 初始化节点存储对象
const requirementNodes = {};
export default {
  name: "ProblemDiagram",
  data(){
    return {

      collapseBtnClass:'el-icon-s-fold',
      isCollapse:false,
      sideWidth:200,
      logoTextShow:true,
      headerBg:'headerBg',

      paper: null,
      graph: null,
      machineDomain: '',
      cloud: '',

      tableData: [],
      sys_device_arr: [],
      device_entity_arr: [],
      intent_entity_arr: [],
      all: [],

      inputRequirement: '',
      inputEnd: '',
      inputInfo: '',

      radio: '',

      checkbox: [ ],
      cities: [],

      systemName: '太阳搜索控制软件',



      inputReq: '',
      inputSystemReq: '',
      inputInfomation: '',

      ifOptions: [],
      ifValue: '',

      thenOptions: [],
      thenValue: '',

      options: [{
        value: 1,
        label: '信息显示'
      }, {
        value: 2,
        label: '命令式'
      }, {
        value: 3,
        label: '需求式行为'
      }],

      value: '',
      inputSentence: '',

      ifResults: '',
      thenResults: '',

      designDomainArray: [],



    }

  },
  components: {Header, Aside},
  mounted(){
    this.initProblemDiagram()
    this.initNameAndEntity()
  },

  methods: {


    collapse() { //点击收缩按钮触发
      this.isCollapse = !this.isCollapse
      if (this.isCollapse) {  //收缩
        this.sideWidth = 64
        this.collapseBtnClass = 'el-icon-s-unfold'
        this.logoTextShow = false
      } else {   //展开
        this.sideWidth = 200
        this.collapseBtnClass = 'el-icon-s-fold'
        this.logoTextShow = true
      }
    },

    genProblemDia(){
      this.initProblemDiagram();
    },
    selectByType1(){
      this.request.get("/contextDiagram/select/type1", ).then(res => {
        this.tableData = res
      })

    },

    selectByType2(){
      this.request.get("/contextDiagram/select/type2", ).then(res => {
        this. tableData= res
      })

    },

    selectByType3(){
      this.request.get("/contextDiagram/select/type3", ).then(res => {
        this.tableData = res
      })

    },

    initProblemDiagram() {

      this.request.get("/contextDiagram/select/type2", ).then(res => {this.device_entity_arr = res})

      this.request.get("/taskIntent", ).then(res => {this.intent_entity_arr = res})

      // 创建一个自定义机器领域形状的图形
      let MachineDomainShape = joint.dia.Element.extend({
        markup: [
          {
            tagName: 'g',
            className: 'machineDomain-shape',
            children: [
              {
                tagName: 'path',
                className: 'machineDomain-body'
              },
              {
                tagName: 'text',
                className: 'machineDomain-label'
              }
            ]
          }
        ],
        defaults: joint.util.deepSupplement({
          type: 'custom.MachineDomainShape',
          size: { width: 100, height: 50 },
          attrs: {
            '.machineDomain-body': {
              d: 'M 30 60 L 195 60 L 195 105 L 30 105 L 30 60 L 45 60 L 45 105 L 60 105 L 60 60\n',
              //机器领域
              // M 100 200 L 650 200 L 650 350 L 100 350 L 100 200 L 150 200 L 150 350 L 200 350 L 200 200
              //云朵形状
              // M 650 300 L 300 300 Q 300 250 350 250 Q 350 200 450 200 A 50 50 0 1 1 600 200 Q 650 200 650 300
              fill: '#ffffff',
              stroke: '#000000',
              strokeWidth: 2
            },
            '.machineDomain-label': {
              text: '',
              fill: 'black',
              fontSize: 14,
              textAnchor: 'middle',
              x: 120,
              y: 90
              // x: { field: 'size', offset: 100 },
              // y: { field: 'size', offset: 55 }
            }
          }
        }, joint.dia.Element.prototype.defaults)
      });

      // 创建一个自定义云朵形状的图形
      let CloudShape = joint.dia.Element.extend({
        markup: [
          {
            tagName: 'g',
            className: 'cloud-shape',
            children: [
              {
                tagName: 'path',
                className: 'cloud-body'
              },
              {
                tagName: 'text',
                className: 'cloud-label'
              }
            ]
          }
        ],
        defaults: joint.util.deepSupplement({
          type: 'custom.CloudShape',
          size: { width: 100, height: 50 },
          attrs: {
            '.cloud-body': {
              d: 'M 195 90 L 90 90 Q 90 75 105 75 Q 105 60 135 60 A 15 15 0 1 1 180 60 Q 195 60 195 90',
              //机器领域
              // M 100 200 L 650 200 L 650 350 L 100 350 L 100 200 L 150 200 L 150 350 L 200 350 L 200 200
              //云朵形状
              // M 650 300 L 300 300 Q 300 250 350 250 Q 350 200 450 200 A 50 50 0 1 1 600 200 Q 650 200 650 300
              fill: '#ffffff',
              stroke: '#000000',
              strokeWidth: 2,
            },
            '.cloud-label': {
              text: '',
              fill: 'black',
              fontSize: 14,
              textAnchor: 'middle',
              x: 145,
              y: 80
              // x: { field: 'size', offset: 100 },
              // y: { field: 'size', offset: 55 }
            }
          }
        }, joint.dia.Element.prototype.defaults)
      });

      //创建画布
      this.graph = new joint.dia.Graph

      //创建纸张
      this.paper = new joint.dia.Paper({
        el: this.$refs.canvas,
        model: this.graph,
        width: 2000,
        height: 800,
        gridSize: 10, // 网格大小
        drawGrid: true, // 显示网格
        background: {
          color: 'rgba(255, 255, 255, 0.3)'
          // color: 'rgba(0, 255, 0, 0.3)'
        }
      })

      this.createSmartHomeProblemDiagram(this.graph);


    },

    createSmartHomeProblemDiagram(graph) {


      let MachineDomainShape = joint.dia.Element.extend({
        markup: [
          {
            tagName: 'g',
            className: 'machineDomain-shape',
            children: [
              {
                tagName: 'path',
                className: 'machineDomain-body'
              },
              {
                tagName: 'text',
                className: 'machineDomain-label'
              }
            ]
          }
        ],
        defaults: joint.util.deepSupplement({
          type: 'custom.MachineDomainShape',
          size: { width: 100, height: 50 },
          attrs: {
            '.machineDomain-body': {
              d: 'M 30 60 L 195 60 L 195 105 L 30 105 L 30 60 L 45 60 L 45 105 L 60 105 L 60 60\n',
              //机器领域
              // M 100 200 L 650 200 L 650 350 L 100 350 L 100 200 L 150 200 L 150 350 L 200 350 L 200 200
              //云朵形状
              // M 650 300 L 300 300 Q 300 250 350 250 Q 350 200 450 200 A 50 50 0 1 1 600 200 Q 650 200 650 300
              fill: '#ffffff',
              stroke: '#000000',
              strokeWidth: 2
            },
            '.machineDomain-label': {
              text: '',
              fill: 'black',
              fontSize: 14,
              textAnchor: 'middle',
              x: 120,
              y: 90
              // x: { field: 'size', offset: 100 },
              // y: { field: 'size', offset: 55 }
            }
          }
        }, joint.dia.Element.prototype.defaults)
      });

      let machineDomain = new MachineDomainShape();
      machineDomain.attr('.machineDomain-label/text',this.systemName);

      machineDomain.position(10, 300);
      this.graph.addCell(machineDomain);


      // 初始化节点存储对象
      const nodes = {};
      // 用于获取或创建节点的函数
      function getNode(name) {
        // 如果节点不存在，则创建新的节点并存储
        if (!nodes[name]) {
          const newNode = new joint.shapes.standard.Rectangle();
          newNode.position(100 + Object.keys(nodes).length * 150, 100); // 设置节点的位置
          newNode.resize(150, 40); // 设置节点大小
          newNode.attr({
            body: {
              fill: 'white'
            },
            label: {
              text: name,
              fill: 'black'
            }
          });
          newNode.addTo(graph); // 将节点添加到图中
          nodes[name] = newNode; // 将节点存储在 nodes 对象中
        }
        return nodes[name]; // 返回节点
      }



      const triples = [];
      this.device_entity_arr.forEach(entity => {
        triples.push({
          subject: entity.begin,
          predicate: entity.link,
          object: entity.end
        });
      });

      // 创建一个用于存储连线的对象
      const links = {};
      // 定义节点的初始位置和间隔
      let xPos = 300;  // 初始 x 坐标
      let yPos1 = 150;  // 初始 y 坐标
      let yPos2 = 150;  // 初始 y 坐标
      const xSpacing = 200;  // 节点之间的水平间隔
      const ySpacing = 50;  // 节点之间的垂直间隔

      // 遍历每个三元组
      triples.forEach(triple => {
        // 获取节点
        const subjectNode = getNode(triple.subject);
        subjectNode.position(xPos, yPos1);
        nodes[triple.subject] = subjectNode;
        yPos1 += ySpacing;  // 更新 xPos 以均匀分布


        const link1 = new joint.shapes.standard.Link();
        link1.source(machineDomain);
        link1.target(subjectNode);
        link1.addTo(graph);
        link1.attr({
          line: {
            stroke: 'black',
            strokeWidth: 2 ,
            sourceMarker: {
              'type': 'none',  // 起点无箭头
            },
            targetMarker: {
              'type': 'none',  // 终点无箭头
            },
          },
          label: {
            text: triple.predicate,
            fill: 'black'
          }
        });

        const objectNode = getNode(triple.object);
        objectNode.position(800, yPos2);
        objectNode.attr({
          body: {
            fill: 'lightgray' // 设置背景颜色为浅灰色
          }
        });
        nodes[triple.object] = objectNode;
        yPos2 += ySpacing;  // 更新 xPos 以均匀分布

        // 创建一个唯一的链接标识
        const linkId = `${triple.subject}-${triple.object}-${triple.predicate}`;

        // 如果链接不存在，则创建新链接
        if (!links[linkId]) {
          const link = new joint.shapes.standard.Link();
          link.source(subjectNode);
          link.target(objectNode);
          link.attr({
            line: {
              stroke: 'black',
              strokeWidth: 2 ,
              sourceMarker: {
                'type': 'none',  // 起点无箭头
              },
              targetMarker: {
                'type': 'none',  // 终点无箭头
              },
            },
            label: {
              text: triple.predicate,
              fill: 'black'
            }
          });

          link.appendLabel({
            attrs: {
              text: {
                text: triple.predicate,         // 文字内容
                fill: 'black',              // 文字颜色
                fontSize: 14,               // 文字大小
                textAnchor: 'middle',       // 文字对齐方式
                refX: 0.5,                  // 文字在连接线上的位置（百分比）
                refY: -15,                   // 文字在连接线上的位置（百分比）
              }
            }
          })
          link.addTo(graph); // 将连线添加到图中
          links[linkId] = link; // 将连线存储在 links 对象中
          console.log(link);
        }
      });





    },

    //按labelText查找节点并返回该节点
    findNodeByLabelText(labelText) {
      // 获取图中所有的元素
      const elements = this.graph.getElements();

      // 遍历所有元素，检查每个元素的 label.text 是否与目标文本匹配
      for (let i = 0; i < elements.length; i++) {
        const element = elements[i];
        const currentLabelText = element.attr('label/text');  // 获取 label.text 属性


        // 检查是否匹配目标 labelText
        if (currentLabelText === labelText) {
          return element;  // 返回匹配的节点
        }
      }

      // 如果没有找到，返回 null 或 undefined
      return null;
    },

    //按labelText查找自定义机器领域节点并返回该节点
    findMachineNodeByLabelText(labelText) {
      // 获取图中所有的元素
      const elements = this.graph.getElements();

      // 遍历所有元素，检查每个元素的 label.text 是否与目标文本匹配
      for (let i = 0; i < elements.length; i++) {
        const element = elements[i];
        const currentLabelText = element.attr('.machineDomain-label/text');  // 获取 label.text 属性


        // 检查是否匹配目标 labelText
        if (currentLabelText === labelText) {
          return element;  // 返回匹配的节点
        }
      }

      // 如果没有找到，返回 null 或 undefined
      return null;
    },

    //按labelText查找自定义机器领域节点并返回该节点
    findDesignNodeByLabelText(labelText) {
      // 获取图中所有的元素
      const elements = this.graph.getElements();

      // 遍历所有元素，检查每个元素的 label.text 是否与目标文本匹配
      for (let i = 0; i < elements.length; i++) {
        const element = elements[i];
        const currentLabelText = element.attr('.designDomain-label/text');  // 获取 label.text 属性


        // 检查是否匹配目标 labelText
        if (currentLabelText === labelText) {
          return element;  // 返回匹配的节点
        }
      }

      // 如果没有找到，返回 null 或 undefined
      return null;
    },

    addRequirement(){


      // 用于获取或创建节点的函数
      function getNode(name) {
        // 如果节点不存在，则创建新的节点并存储
        if (!requirementNodes[name]) {
          const newNode = new joint.shapes.standard.Ellipse();
          newNode.position(1200, 200 + Object.keys(requirementNodes).length * 100); // 设置节点的位置
          newNode.resize(150, 40); // 设置节点大小
          newNode.attr({
            body: {
              fill: 'white',
              strokeDasharray: '5,5' ,  //虚线
            },
            label: {
              text: name,
              fill: 'black'
            }
          });
          newNode.addTo(this.graph); // 将节点添加到图中
          requirementNodes[name] = newNode; // 将节点存储在 nodes 对象中
        }
        return requirementNodes[name]; // 返回节点，无论是新创建的还是已存在的
      }



      // 获取输入框的值
      const requirement = this.inputRequirement.trim(); // 假设 inputRequirement 是你绑定的输入框的值
      const subjectNode = getNode.call(this,requirement);  // 使用 call 将 this 绑定到当前 Vue 实例---------------------------------------------------------


      const targetNode = this.findNodeByLabelText(this.radio);

      // 获取目标节点的背景颜色
      const targetColor = targetNode.attr('body/fill'); // 获取背景颜色

      const link = new joint.shapes.standard.Link();
      link.source(subjectNode);
      link.target(targetNode);
      link.attr({
        line: {
          stroke: 'black',
          strokeWidth: 2 ,
          strokeDasharray: '5,5' ,  //虚线
        }
      });

      link.addTo(this.graph); // 将连线添加到图中



      // 根据目标节点的背景颜色判断是否加箭头
      if (targetColor === 'lightgray') { // 替换 'desiredColor' 为你想要的颜色值，例如 'lightgray'
        link.attr({
          line: {
            targetMarker: {
              'type': 'none', // 终点无箭头
            },
          },
        });
      }

      link.addTo(this.graph); // 将连线添加到图中

    },

    initNameAndEntity(){

      this.request.get("/contextDiagram/select/type2").then(res => {
        const result = [
          ...res.map(item => item.begin),  // 提取 begin 字段
          ...res.map(item => item.end)     // 提取 end 字段
        ];

        // 使用 Set 去重
        const uniqueResult = [...new Set(result)];

        console.log(uniqueResult);
        this.cities = uniqueResult;

        this.ifOptions = uniqueResult.map((item, index) => ({
          ifValue: index, // 使用索引值作为唯一标识
          label: item // 数据库中的 label 作为标签
        }));


        this.thenOptions = uniqueResult.map((item, index) => ({
          thenValue: index, // 使用索引值作为唯一标识
          label: item // 数据库中的 label 作为标签
        }));
      })










    },

    addRequirements(){
      console.log(this.checkbox);

      function getNode(name) {
        // 如果节点不存在，则创建新的节点并存储
        if (!requirementNodes[name]) {
          const newNode = new joint.shapes.standard.Ellipse();
          newNode.position(1200, 200 + Object.keys(requirementNodes).length * 100); // 设置节点的位置
          newNode.resize(150, 40); // 设置节点大小
          newNode.attr({
            body: {
              fill: 'white',
              strokeDasharray: '5,5' ,  //虚线
            },
            label: {
              text: name,
              fill: 'black'
            }
          });
          newNode.addTo(this.graph); // 将节点添加到图中
          requirementNodes[name] = newNode; // 将节点存储在 nodes 对象中
        }
        return requirementNodes[name]; // 返回节点，无论是新创建的还是已存在的
      }

      // 获取输入框的值
      const requirement = this.inputRequirement.trim(); // 假设 inputRequirement 是你绑定的输入框的值
      const subjectNode = getNode.call(this, requirement);  // 使用 call 将 this 绑定到当前 Vue 实例

      const info = this.inputInfo.trim();

      // 假设 this.radio 是从多选框中获取的选中节点数组
      const targetNodes = this.checkbox.map(targetLabel => this.findNodeByLabelText(targetLabel));

      // 获取目标节点的背景颜色
      // 遍历每个 targetNode，创建链接
      targetNodes.forEach(targetNode => {
        const targetColor = targetNode.attr('body/fill'); // 获取背景颜色

        const link = new joint.shapes.standard.Link();
        link.source(subjectNode);
        link.target(targetNode);
        link.attr({
          line: {
            stroke: 'black',
            strokeWidth: 2,
            strokeDasharray: '5,5',  //虚线
          }
        });

        // 设置标签，将用户输入的信息作为标签内容显示在连线上
        link.label(0, {
          attrs: {
            text: {
              text: info, // 使用输入框中的信息
              fill: 'black',
              fontSize: 12,
              fontWeight: 'bold',
            }
          },
          position: {
            offset: 10, // 标签在连线上的偏移
          }
        });

        link.addTo(this.graph); // 将连线添加到图中

        // 根据目标节点的背景颜色判断是否加箭头
        if (targetColor === 'lightgray') { // 替换 'desiredColor' 为你想要的颜色值，例如 'lightgray'
          link.attr({
            line: {
              targetMarker: {
                'type': 'none', // 终点无箭头
              },
            },
          });
        }

        link.addTo(this.graph); // 将连线添加到图中
      });




    },

    saveAsImage() {

      const paperElement = document.getElementById('paper'); // 替换为实际 ID

      html2canvas(paperElement).then(canvas => {
        const imgData = canvas.toDataURL('image/png');
        const link = document.createElement('a');
        link.href = imgData;
        link.download = 'Problem-Diagram.png';
        link.click();
      });

    },

    saveAsSVG(){
      const svgData = this.paper.svg.outerHTML;
      const svgBlob = new Blob([svgData], { type: 'image/svg+xml;charset=utf-8' });
      const url = URL.createObjectURL(svgBlob);
      const link = document.createElement('a');
      link.href = url;
      link.download = 'canvas.svg';
      link.click();
    },

    saveAsJSON(){
      const jsonData = JSON.stringify(this.graph.toJSON());
      const link = document.createElement('a');
      link.href = URL.createObjectURL(new Blob([jsonData], { type: 'application/json' }));
      link.download = 'canvas.json';
      link.click();
    },

    addReq(){

      // 用于获取或创建节点的函数
      function getNode(name) {
        // 如果节点不存在，则创建新的节点并存储
        if (!requirementNodes[name]) {
          const newNode = new joint.shapes.standard.Ellipse();
          newNode.position(1200, 200 + Object.keys(requirementNodes).length * 100); // 设置节点的位置
          newNode.resize(150, 40); // 设置节点大小
          newNode.attr({
            body: {
              fill: 'white',
              strokeDasharray: '5,5' ,  //虚线
            },
            label: {
              text: name,
              fill: 'black'
            }
          });
          newNode.addTo(this.graph); // 将节点添加到图中
          requirementNodes[name] = newNode; // 将节点存储在 nodes 对象中
        }
        return requirementNodes[name]; // 返回节点，无论是新创建的还是已存在的
      }



      // 获取输入框的值
      const requirement = this.inputReq.trim(); // 假设 inputRequirement 是你绑定的输入框的值
      const subjectNode = getNode.call(this,requirement);  // 使用 call 将 this 绑定到当前 Vue 实例---------------------------------------------------------




      console.log(this.ifValue);
      // 根据 value 查找对应的 label
      const selectedIfOption = this.ifOptions.find(item => item.ifValue === this.ifValue);
      console.log("label",selectedIfOption ? selectedIfOption.label : '')


      console.log(this.thenValue);
      // 根据 value 查找对应的 label
      const selectedThenOption = this.thenOptions.find(item => item.thenValue === this.thenValue);
      console.log("label",selectedThenOption ? selectedThenOption.label : '')

      const information = this.inputInfomation.trim();



      if (selectedIfOption){
        const targetNode1 = this.findNodeByLabelText(selectedIfOption.label);
        const link1 = new joint.shapes.standard.Link();
        link1.source(subjectNode);
        link1.target(targetNode1);
        link1.attr({
          line: {
            stroke: 'black',
            strokeWidth: 2 ,
            strokeDasharray: '5,5' ,  //虚线
          }
        });
        // 设置标签，将用户输入的信息作为标签内容显示在连线上
        link1.label(0, {
          attrs: {
            text: {
              text: information, // 使用输入框中的信息
              fill: 'black',
              fontSize: 12,
              fontWeight: 'bold',
            }
          },
          position: {
            offset: 10, // 标签在连线上的偏移
          }
        });
        link1.addTo(this.graph); // 将连线添加到图中


      }
      if (selectedThenOption){

        const targetNode2 = this.findNodeByLabelText(selectedThenOption.label);
        const link2 = new joint.shapes.standard.Link();
        link2.source(subjectNode);
        link2.target(targetNode2);
        link2.attr({
          line: {
            stroke: 'black',
            strokeWidth: 2 ,
            strokeDasharray: '5,5' ,  //虚线
            targetMarker: {
              'type': 'none', // 终点无箭头
            },
          }
        });
        // 设置标签，将用户输入的信息作为标签内容显示在连线上
        link2.label(0, {
          attrs: {
            text: {
              text: information, // 使用输入框中的信息
              fill: 'black',
              fontSize: 12,
              fontWeight: 'bold',
            }
          },
          position: {
            offset: 10, // 标签在连线上的偏移
          }
        });
        link2.addTo(this.graph); // 将连线添加到图中

      }




    },

    addIdentify(){

      // 创建一个自定义设计领域形状的图形
      let DesignDomainShape = joint.dia.Element.extend({
        markup: [
          {
            tagName: 'g',
            className: 'designDomain-shape',
            children: [
              {
                tagName: 'path',
                className: 'designDomain-body'
              },
              {
                tagName: 'text',
                className: 'designDomain-label'
              }
            ]
          }
        ],
        defaults: joint.util.deepSupplement({
          type: 'custom.DesignDomainShape',
          size: { width: 100, height: 50 },
          attrs: {
            '.designDomain-body': {
              d: 'M 30 60 L 195 60 L 195 105 L 30 105 L 30 60 L 45 60 L 45 105 L 60 105\n',
              fill: '#ffffff',
              stroke: '#000000',
              strokeWidth: 2
            },
            '.designDomain-label': {
              text: '',
              fill: 'black',
              fontSize: 14,
              textAnchor: 'middle',
              x: 120,
              y: 90
              // x: { field: 'size', offset: 100 },
              // y: { field: 'size', offset: 55 }
            }
          }
        }, joint.dia.Element.prototype.defaults)
      });


      this.request.post("/problemDiagram/identify",this.inputSystemReq).then(res => {

        const array = res
        console.log(array)


        Object.keys(array).forEach(key => {
          // 先检查属性值是否为空或不存在
          if (array[key] && array[key].length > 0) {

            if(array[key][5] && array[key][5] === '设计领域'){


              //将设计领域单独存储起来!!!!!!!!!!!!!
              this.designDomainArray.push({ designDomainName: array[key][0], dataName: array[key][1] });
              //[{designDomainName:"",dataName:""},{designDomainName:"",dataName:""}]
              console.log("设计领域集合：：：",this.designDomainArray)



              const subjectNodeString = array[key][0]
              const machineDomain = this.findMachineNodeByLabelText(this.systemName)
              console.log(machineDomain)
              console.log(this.systemName)


              let designDomain = new DesignDomainShape();
              designDomain.attr('.designDomain-label/text',subjectNodeString);
              designDomain.position(300, 300);
              this.graph.addCell(designDomain);

              const link1 = new joint.shapes.standard.Link();
              link1.source(designDomain);
              link1.target(machineDomain);
              link1.addTo(this.graph);
              link1.attr({
                line: {
                  stroke: 'black',
                  strokeWidth: 2 ,
                  sourceMarker: {
                    'type': 'none',  // 起点无箭头
                  },
                  targetMarker: {
                    'type': 'none',  // 终点无箭头
                  },
                }
              });

              const systemRequirement = array[key][4]
              const interInfo = array[key][1]
              const reqFeature = array[key][3]
              const systemRequirementNode = getNode.call(this,systemRequirement);

              const subjectNode = this.findDesignNodeByLabelText(subjectNodeString);
              const link2 = new joint.shapes.standard.Link();
              link2.source(systemRequirementNode);
              link2.target(subjectNode);
              if(reqFeature==="需求引用"){
                link1.attr({
                  line: {
                    stroke: 'black',
                    strokeWidth: 2 ,
                    strokeDasharray: '5,5' ,
                    targetMarker: {
                      'type': 'none', // 终点无箭头
                    },
                  }
                })
              }else if(reqFeature==="需求约束"){
                link2.attr({
                  line: {
                    stroke: 'black',
                    strokeWidth: 2 ,
                    strokeDasharray: '5,5' ,
                  }
                })
              }

              // 设置标签，将用户输入的信息作为标签内容显示在连线上
              link2.label(0, {
                attrs: {
                  text: {
                    text: interInfo, // 使用输入框中的信息
                    fill: 'black',
                    fontSize: 12,
                    fontWeight: 'bold',
                  }
                },
                position: {
                  offset: 10, // 标签在连线上的偏移
                }
              });
              link2.addTo(this.graph); // 将连线添加到图中






            }else{

              const systemRequirement = array[key][4]
              const subjectNodeString = array[key][0]
              const interInfo = array[key][1]
              const reqFeature = array[key][3]
              const systemRequirementNode = getNode.call(this,systemRequirement);



              // 使用 some() 判断（交互信息在设计领域中）是否存在
              let exists = this.designDomainArray.some(item => item.dataName === interInfo);

              if (exists) {
                console.log("字符串在数组中存在");

                // 假设你要查找的 dataName
                let targetDataName = interInfo;

                // 使用 find() 查找第一个匹配的对象
                let result = this.designDomainArray.find(item => item.dataName === targetDataName);

                console.log(result.designDomainName);  // 输出: DesignDomainName

                //!!!!!!!!!!!!需求和设计领域连接
                const objectNode = this.findDesignNodeByLabelText(result.designDomainName)
                const link1 = new joint.shapes.standard.Link();
                link1.source(systemRequirementNode);
                link1.target(objectNode);
                link1.attr({
                  line: {
                    stroke: 'black',
                    strokeWidth: 2 ,
                    strokeDasharray: '5,5' ,
                    targetMarker: {
                      'type': 'none', // 终点无箭头
                    },
                  }
                })

                // 设置标签，将用户输入的信息作为标签内容显示在连线上
                link1.label(0, {
                  attrs: {
                    text: {
                      text: interInfo, // 使用输入框中的信息
                      fill: 'black',
                      fontSize: 12,
                      fontWeight: 'bold',
                    }
                  },
                  position: {
                    offset: 10, // 标签在连线上的偏移
                  }
                });
                link1.addTo(this.graph); // 将连线添加到图中






              } else {
                console.log("字符串在数组中不存在");
              }










              const subjectNode = this.findNodeByLabelText(subjectNodeString);
              const link1 = new joint.shapes.standard.Link();
              link1.source(systemRequirementNode);
              link1.target(subjectNode);
              if(reqFeature==="需求引用"){
                link1.attr({
                  line: {
                    stroke: 'black',
                    strokeWidth: 2 ,
                    strokeDasharray: '5,5' ,
                    targetMarker: {
                      'type': 'none', // 终点无箭头
                    },
                  }
                })
              }else if(reqFeature==="需求约束"){
                link1.attr({
                  line: {
                    stroke: 'black',
                    strokeWidth: 2 ,
                    strokeDasharray: '5,5' ,
                  }
                })
              }

              // 设置标签，将用户输入的信息作为标签内容显示在连线上
              link1.label(0, {
                attrs: {
                  text: {
                    text: interInfo, // 使用输入框中的信息
                    fill: 'black',
                    fontSize: 12,
                    fontWeight: 'bold',
                  }
                },
                position: {
                  offset: 10, // 标签在连线上的偏移
                }
              });
              link1.addTo(this.graph); // 将连线添加到图中

            }






          }
        });


        // 用于获取或创建节点的函数
        function getNode(name) {
          // 如果节点不存在，则创建新的节点并存储
          if (!requirementNodes[name]) {
            const newNode = new joint.shapes.standard.Ellipse();
            newNode.position(1200, 200 + Object.keys(requirementNodes).length * 100); // 设置节点的位置
            newNode.resize(150, 40); // 设置节点大小
            newNode.attr({
              body: {
                fill: 'white',
                strokeDasharray: '5,5' ,  //虚线
              },
              label: {
                text: name,
                fill: 'black'
              }
            });
            newNode.addTo(this.graph); // 将节点添加到图中
            requirementNodes[name] = newNode; // 将节点存储在 nodes 对象中
          }
          return requirementNodes[name]; // 返回节点，无论是新创建的还是已存在的
        }






      })

      this.request.post("/systemReq/addSystemRequirements", this.inputSystemReq).then(res => {
        if (res) {
          this.$message.success("系统需求解析并保存成功");
        } else {
          this.$message.error("提交失败");
        }
      });

    },

    addExtract(){

      function getNode(name) {
        // 如果节点不存在，则创建新的节点并存储
        if (!requirementNodes[name]) {
          const newNode = new joint.shapes.standard.Ellipse();
          newNode.position(1200, 200 + Object.keys(requirementNodes).length * 100); // 设置节点的位置
          newNode.resize(150, 40); // 设置节点大小
          newNode.attr({
            body: {
              fill: 'white',
              strokeDasharray: '5,5' ,  //虚线
            },
            label: {
              text: name,
              fill: 'black'
            }
          });
          newNode.addTo(this.graph); // 将节点添加到图中
          requirementNodes[name] = newNode; // 将节点存储在 nodes 对象中
        }
        return requirementNodes[name]; // 返回节点，无论是新创建的还是已存在的
      }

      if(this.value === 1){
        this.request.post("/problemDiagram/1",this.inputSentence).then(res => {
          this.ifResults = res.ifResults
          this.thenResults = res.thenResults

          // 获取输入框的值
          const requirement = this.inputRequirement.trim(); // 假设 inputRequirement 是你绑定的输入框的值
          const subjectNode = getNode.call(this, requirement);  // 使用 call 将 this 绑定到当前 Vue 实例
          const objectNode0 = this.findNodeByLabelText(this.ifResults[0]);
          const objectNode1 = this.findNodeByLabelText(this.thenResults[0]);

          const ifLink = new joint.shapes.standard.Link();
          ifLink.source(subjectNode);
          ifLink.target(objectNode0);
          ifLink.attr({
            line: {
              stroke: 'black',
              strokeWidth: 2,
              strokeDasharray: '5,5',  //虚线
              targetMarker: {
                'type': 'none', // 终点无箭头
              },
            }
          });

          // 设置标签，将用户输入的信息作为标签内容显示在连线上
          ifLink.label(0, {
            attrs: {
              text: {
                text: this.ifResults[1], // 使用输入框中的信息
                fill: 'black',
                fontSize: 12,
                fontWeight: 'bold',
              }
            },
            position: {
              offset: 10, // 标签在连线上的偏移
            }
          });

          ifLink.addTo(this.graph); // 将连线添加到图中


          const thenLink = new joint.shapes.standard.Link();
          thenLink.source(subjectNode);
          thenLink.target(objectNode1);
          thenLink.attr({
            line: {
              stroke: 'black',
              strokeWidth: 2,
              strokeDasharray: '5,5',  //虚线

            }
          });

          // 设置标签，将用户输入的信息作为标签内容显示在连线上
          thenLink.label(0, {
            attrs: {
              text: {
                text: this.thenResults[1], // 使用输入框中的信息
                fill: 'black',
                fontSize: 12,
                fontWeight: 'bold',
              }
            },
            position: {
              offset: 10, // 标签在连线上的偏移
            }
          });

          thenLink.addTo(this.graph); // 将连线添加到图中

          console.log(res.ifResults[0])
          console.log(res.ifResults[1])
          console.log(res.thenResults[0])
          console.log(res.thenResults[1])
        })
      }
      else if(this.value === 2){
        this.request.post("/problemDiagram/2",this.inputSentence).then(res => {
          this.ifResults = res.ifResults
          this.thenResults = res.thenResults

          // 获取输入框的值
          const requirement = this.inputRequirement.trim(); // 假设 inputRequirement 是你绑定的输入框的值
          const subjectNode = getNode.call(this, requirement);  // 使用 call 将 this 绑定到当前 Vue 实例
          const objectNode0 = this.findNodeByLabelText(this.ifResults[0]);
          const objectNode1 = this.findNodeByLabelText(this.thenResults[0]);

          const ifLink = new joint.shapes.standard.Link();
          ifLink.source(subjectNode);
          ifLink.target(objectNode0);
          ifLink.attr({
            line: {
              stroke: 'black',
              strokeWidth: 2,
              strokeDasharray: '5,5',  //虚线
              targetMarker: {
                'type': 'none', // 终点无箭头
              },
            }
          });

          // 设置标签，将用户输入的信息作为标签内容显示在连线上
          ifLink.label(0, {
            attrs: {
              text: {
                text: this.ifResults[1], // 使用输入框中的信息
                fill: 'black',
                fontSize: 12,
                fontWeight: 'bold',
              }
            },
            position: {
              offset: 10, // 标签在连线上的偏移
            }
          });

          ifLink.addTo(this.graph); // 将连线添加到图中


          const thenLink = new joint.shapes.standard.Link();
          thenLink.source(subjectNode);
          thenLink.target(objectNode1);
          thenLink.attr({
            line: {
              stroke: 'black',
              strokeWidth: 2,
              strokeDasharray: '5,5',  //虚线

            }
          });

          // 设置标签，将用户输入的信息作为标签内容显示在连线上
          thenLink.label(0, {
            attrs: {
              text: {
                text: this.thenResults[1], // 使用输入框中的信息
                fill: 'black',
                fontSize: 12,
                fontWeight: 'bold',
              }
            },
            position: {
              offset: 10, // 标签在连线上的偏移
            }
          });

          thenLink.addTo(this.graph); // 将连线添加到图中

          console.log(res.ifResults[0])
          console.log(res.ifResults[1])
          console.log(res.thenResults[0])
          console.log(res.thenResults[1])
        })
      }
      else if(this.value === 3){
        this.request.post("/problemDiagram/3",this.inputSentence).then(res => {
          this.thenResults = res.thenResults

          // 获取输入框的值
          const requirement = this.inputRequirement.trim(); // 假设 inputRequirement 是你绑定的输入框的值
          const subjectNode = getNode.call(this, requirement);  // 使用 call 将 this 绑定到当前 Vue 实例
          const objectNode1 = this.findNodeByLabelText(this.thenResults[0]);



          const thenLink = new joint.shapes.standard.Link();
          thenLink.source(subjectNode);
          thenLink.target(objectNode1);
          thenLink.attr({
            line: {
              stroke: 'black',
              strokeWidth: 2,
              strokeDasharray: '5,5',  //虚线

            }
          });

          // 设置标签，将用户输入的信息作为标签内容显示在连线上
          thenLink.label(0, {
            attrs: {
              text: {
                text: this.thenResults[1], // 使用输入框中的信息
                fill: 'black',
                fontSize: 12,
                fontWeight: 'bold',
              }
            },
            position: {
              offset: 10, // 标签在连线上的偏移
            }
          });

          thenLink.addTo(this.graph); // 将连线添加到图中

        })
      }






    }





  },
}
</script>



<style scoped>
.cloud-shape {
  cursor: pointer;
}

.cloud-label {
  fill: black;
  font-size: 14px;
  text-anchor: middle;
  alignment-baseline: middle;
}


</style>