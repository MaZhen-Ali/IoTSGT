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

        <el-button @click="generateData">生成<i class="el-icon-check"></i></el-button>

        <el-button @click="genContextDia">上下文图<i class="el-icon-check"></i></el-button>

        <el-button @click="selectByType1">系统-设备<i class="el-icon-check"></i></el-button>

        <el-button @click="selectByType2">设备-实体<i class="el-icon-check"></i></el-button>

        <el-button @click="selectByType3">意图-实体<i class="el-icon-check"></i></el-button>

        <el-button @click="saveAsImage">保存为图像</el-button>
        <el-button @click="saveAsSVG">保存为SVG</el-button>
        <el-button @click="saveAsJSON">保存为JSON</el-button>



        <el-table :data="tableData" border stripe :header-cell-class-name="headerBg">
          <el-table-column prop="type" width="140" label="关系类型"></el-table-column>
          <el-table-column prop="begin" width="200" label="起点"></el-table-column>
          <el-table-column prop="link" width="200" label="连接"></el-table-column>
          <el-table-column prop="end" width="200" label="终点"></el-table-column>
        </el-table>

        <div  id="paper" class="canvas" ref="canvas" style="width: 1600px; height: 600px; border: 1px solid #ccc;"></div>




      </el-main>





    </el-container>




  </el-container>

</template>

<script>

import Aside from "@/components/Aside.vue";
import Header from "@/components/Header.vue";
import * as joint from "jointjs";
import html2canvas from "html2canvas";
// import * as joint from '@joint/core'; //版本不同



export default {
  name: "ContextDiagram",
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
      systemName: '太阳搜索控制系统',


    }

  },

  components: {Header, Aside},

  mounted(){
    // this.initContextDiagram()
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

    generateData(){

      // this.request.post("/contextDiagram/del")
      // this.request.post("/contextDiagram/update")

      this.request.post("/contextDiagram/del")
          .then(response1 => {
            console.log('删除请求响应:', response1);

            // 第二个请求，只有在第一个请求完成后发送
            return this.request.post("/contextDiagram/update");
          })
          .then(response2 => {
            console.log('更新请求响应:', response2);
          })
          .catch(error => {
            console.error('请求发生错误:', error);
          });

    },

    genContextDia(){
      this.initContextDiagram();
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

    initContextDiagram() {

      // this.request.get("/contextDiagram/select/type1", ).then(res => {this.sys_device_arr = res})
      this.request.get("/contextDiagram/select/type2", ).then(res => {this.device_entity_arr = res})
      // this.request.get("/contextDiagram/select/type3", ).then(res => {this.task_entity_arr = res})

      this.request.get("/taskIntent", ).then(res => {this.intent_entity_arr = res})
      // this.request.get("/contextDiagram", ).then(res => {this.all = res})

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
        width: 1400,
        height: 800,
        gridSize: 10, // 网格大小
        drawGrid: true, // 显示网格
        background: {
          color: 'rgba(255, 255, 255, 0.3)'
          // color: 'rgba(0, 255, 0, 0.3)'
        }
      })

      this.createSmartHomeDiagram(this.graph);






      // // 创建一个机器领域形状的元素
      // let machineDomain = new MachineDomainShape();
      // machineDomain.attr('.machineDomain-label/text',this.sys_device_arr[0].begin);
      // machineDomain.position(100, 100);
      // this.graph.addCell(machineDomain);

      // //绘制矩形元素
      // let rect = new joint.shapes.standard.Rectangle()
      // rect.position(400, 30)
      // rect.resize(120, 40)
      // rect.attr({
      //   body: {
      //     fill: 'white'
      //   },
      //   label: {
      //     text: this.device_entity_arr[0].begin,
      //     fill: 'black'
      //   }
      // })
      // rect.addTo(this.graph)

      // for(let i = 0;i < this.device_entity_arr.length; i++){
      //
      //   let rect1 = rect.clone()
      //   rect1.translate(0, 100*i)
      //   rect1.attr('label/text', this.device_entity_arr[i].begin)
      //   rect1.addTo(this.graph)
      //
      //   let rect2 = rect.clone()
      //   rect2.translate(400, 100*i)
      //   rect2.attr('label/text', this.device_entity_arr[i].end)
      //   rect2.addTo(this.graph)
      //
      //   let link = new joint.shapes.standard.Link()
      //   link.source(rect1)
      //   link.target(rect2)
      //   link.attr({
      //     line: {
      //       stroke: 'black',  //线条颜色
      //       strokeWidth: 2,  //线条宽度
      //       // 虚线
      //       // strokeDasharray:'5,5'
      //       sourceMarker: {
      //         'type': 'none',  // 起点无箭头
      //       },
      //       targetMarker: {
      //         'type': 'none',  // 终点无箭头
      //       },
      //     },
      //
      //   });
      //   link.appendLabel({
      //     attrs: {
      //       text: {
      //         text: this.device_entity_arr[i].link,         // 文字内容
      //         fill: 'black',              // 文字颜色
      //         fontSize: 14,               // 文字大小
      //         textAnchor: 'middle',       // 文字对齐方式
      //         refX: 0.5,                  // 文字在连接线上的位置（百分比）
      //         refY: -15,                   // 文字在连接线上的位置（百分比）
      //       }
      //     }
      //   })
      //   link.addTo(this.graph)

        // let link111 = new joint.shapes.standard.Link()
        // link111.source(rect1)
        // link111.target(machineDomain)
        // link111.attr({
        //   line: {
        //     stroke: 'black',  //线条颜色
        //     strokeWidth: 2,  //线条宽度
        //     // 虚线
        //     // strokeDasharray:'5,5'
        //     sourceMarker: {
        //       'type': 'none',  // 起点无箭头
        //     },
        //     targetMarker: {
        //       'type': 'none',  // 终点无箭头
        //     },
        //   },
        //
        // });
        // link111.addTo(this.graph)
      // }



      // // 创建一个云朵形状的元素
      // let cloud1 = new CloudShape();
      // cloud1.attr('.cloud-label/text', '追踪太阳');
      // cloud1.position(1000, 50);
      // // 将云朵形状的元素添加到画布中
      // this.graph.addCell(cloud1);
      //
      // let cloud2 = new CloudShape();
      // cloud2.attr('.cloud-label/text', '响应操作指令');
      // cloud2.position(1000, 200);
      // this.graph.addCell(cloud2);


    },

    createSmartHomeDiagram(graph) {

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

      // 初始化云朵节点存储对象
      const cloudNodes = {};
      // 用于获取或创建云朵节点的函数
      function getCloudNode(name) {
        // 如果节云朵点不存在，则创建新的云朵节点并存储
        if (!cloudNodes[name]) {
          const newNode = new CloudShape();
          newNode.position(100 + Object.keys(cloudNodes).length * 150, 100); // 设置节点的位置
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
          newNode.attr('.cloud-label/text',name);
          newNode.addTo(graph); // 将节点添加到图中
          cloudNodes[name] = newNode; // 将节点存储在 nodes 对象中
        }
        return cloudNodes[name]; // 返回节点
      }

      // 定义三元组
      // const triples = [
      //   {subject: this.device_entity_arr[0].begin, predicate: this.device_entity_arr[0].link, object:this.device_entity_arr[0].end },
      //   {subject: this.device_entity_arr[1].begin, predicate: this.device_entity_arr[1].link, object:this.device_entity_arr[1].end },
      //   {subject: this.device_entity_arr[2].begin, predicate: this.device_entity_arr[2].link, object:this.device_entity_arr[2].end },
      //   {subject: this.device_entity_arr[3].begin, predicate: this.device_entity_arr[3].link, object:this.device_entity_arr[3].end }
      // ];
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
      



      // //绘制意图到实体
      // const intentEntityTriples = [];
      // this.intent_entity_arr.forEach(entity => {
      //   intentEntityTriples.push({
      //     subject: entity.target,
      //     predicate: entity.task,
      //     object: entity.entity
      //   });
      // });
      //
      // // 创建一个用于存储连线的对象
      // const intentEntityLinks = {};
      // // 定义节点的初始位置和间隔
      // let intentxPos = 1100;  // 初始 x 坐标
      // let intentyPos1 = 150;  // 初始 y 坐标
      // let intentyPos2 = 150;  // 初始 y 坐标
      // const intentxSpacing = 200;  // 节点之间的水平间隔
      // const intentySpacing = 50;  // 节点之间的垂直间隔
      //
      //
      // // 遍历每个三元组
      // intentEntityTriples.forEach(triple => {
      //   // 获取节点
      //   const subjectNode = getCloudNode(triple.subject);
      //   subjectNode.position(intentxPos, intentyPos1);
      //   cloudNodes[triple.subject] = subjectNode;
      //   intentyPos1 += intentySpacing;  // 更新 xPos 以均匀分布
      //
      //
      //   const objectNode = getNode(triple.object);
      //
      //
      //   const link1 = new joint.shapes.standard.Link();
      //   link1.source(subjectNode);
      //   link1.target(objectNode);
      //   console.log(triple.predicate);
      //   link1.addTo(graph);
      //   link1.attr({
      //     line: {
      //       stroke: 'black',
      //       strokeWidth: 2 ,
      //       strokeDasharray: '5,5' ,  //虚线
      //     }
      //   });
      //
      //   link1.appendLabel({
      //     attrs: {
      //       text: {
      //         text: triple.predicate,         // 文字内容
      //         fill: 'black',              // 文字颜色
      //         fontSize: 14,               // 文字大小
      //         textAnchor: 'middle',       // 文字对齐方式
      //         refX: 0.5,                  // 文字在连接线上的位置（百分比）
      //         refY: -15,                   // 文字在连接线上的位置（百分比）
      //       }
      //     }
      //   })
      // });




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



    saveAsImage() {


      const paperElement = document.getElementById('paper'); // 替换为实际 ID

      html2canvas(paperElement).then(canvas => {
        const imgData = canvas.toDataURL('image/png');
        const link = document.createElement('a');
        link.href = imgData;
        link.download = 'Context-Diagram.png';
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