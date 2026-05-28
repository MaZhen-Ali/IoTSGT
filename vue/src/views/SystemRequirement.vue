

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

        <div class="system-requirement-page">
          <!-- 顶部输入框 -->
          <el-row :gutter="20" class="input-bar" style="margin-bottom: 20px;">
            <el-col :span="6">
              <el-input
                  v-model="userInput"
                  placeholder="请输入需求"
                  clearable
                  @keyup.enter.native="generateRecommendations"
              />
            </el-col>
            <el-col :span="3">
              <el-button type="primary" @click="generateRecommendations">生成推荐</el-button>
            </el-col>

            <el-col :span="3">
              <el-button type="primary" @click="generateRecommendationsByGPT">LLM推荐</el-button>
            </el-col>

            <el-col :span="3">
              <el-button @click="clearAll">重新推荐</el-button>
            </el-col>

            <el-col :span="3">
              <el-button type="success"  @click="addIdentify">生成问题图</el-button>
            </el-col>



          </el-row>

          <!-- 三栏主体结构 -->
          <el-row :gutter="20" class="content-area">
            <!-- 左侧：推荐需求 -->
            <el-col :span="6">
              <h3>推荐系统需求</h3>
              <el-card v-for="(item, index) in recommendations" :key="index" class="recommend-card" style="margin-bottom: 10px;">
                <div class="recommend-text">{{ item.systemRequirement }}</div>
                <div class="recommend-actions" style="margin-top: 10px;">
                  <el-button type="primary" size="mini" @click="addToConfirmed(item)">✓ 选择</el-button>
                  <el-button size="mini" @click="editRequirement(item, index, 'recommendations')">✎ 修改</el-button>
                  <el-button type="danger" size="mini" @click="removeItem(index, 'recommendations')">✕ 删除</el-button>
                </div>
              </el-card>
            </el-col>

            <!-- 中间：已确认需求 -->
            <el-col :span="6">
              <h3>已确认系统需求</h3>
              <div v-for="(groupItems, requirement) in groupedConfirmed" :key="requirement">
                <el-card class="confirm-card">
                  <div class="confirm-text"><strong>{{ requirement }}</strong></div>
                  <div
                      v-for="item in groupItems"
                      :key="item._originalIndex"
                      class="confirm-text"
                  >
                    {{ item.description }}
                  </div>
                </el-card>
              </div>
            </el-col>

            <!-- 右侧：上下文图区域 -->
            <el-col :span="12">
              <h3>嵌入式系统问题图</h3>
              <div id="context-diagram" ref="canvas" style="height: 1700px; border: 1px solid #ccc;">
                <!-- 可使用 JointJS / ECharts / SVG 显示图形 -->
              </div>

              <div>


                <el-input type="textarea" v-model="inputSystemReq" placeholder="系统需求：系统shall<系统行为>"></el-input>




                <el-button>确定</el-button>

              </div>
            </el-col>



          </el-row>

          <!-- 底部操作 -->
          <div class="footer-bar" style="margin-top: 20px; text-align: right;">
            <el-button @click="clearAll">重新推荐</el-button>
            <el-button type="success" @click="confirmAndExport">确认生成</el-button>
          </div>

          <!-- 编辑弹窗 -->
          <el-dialog title="修改系统需求" :visible.sync="editDialogVisible">
            <el-input type="textarea" v-model="editContent" rows="4" />
            <span slot="footer" class="dialog-footer">
              <el-button @click="editDialogVisible = false">取消</el-button>
              <el-button type="primary" @click="saveEdit">保存</el-button>
            </span>
          </el-dialog>
        </div>








      </el-main>
    </el-container>
  </el-container>

</template>

<script>
import Header from "@/components/Header.vue";
import Aside from "@/components/Aside.vue";
import * as joint from "jointjs";
// 初始化节点存储对象
const requirementNodes = {};
export default {
  name: "SystemRequirement",
  components: {Aside, Header},
  created() {
    this.load();
  },
  mounted() {
    this.initProblemDiagram();
  },

  computed: {
    groupedConfirmed() {
      const grouped = {};
      this.confirmed.forEach((item, index) => {
        const key = item.requirement;
        if (!grouped[key]) {
          grouped[key] = [];
        }
        grouped[key].push({ ...item, _originalIndex: index });
      });
      return grouped;
    }
  },
  data() {
    return {
      collapseBtnClass: 'el-icon-s-fold',
      isCollapse: false,
      sideWidth: 200,
      logoTextShow: true,
      headerBg: 'headerBg',


      userInput: "",
      recommendations: [],
      confirmed: [],
      editDialogVisible: false,
      editContent: "",
      editIndex: -1,
      editTarget: "", // 'recommendations' or 'confirmed'


      paper: null,
      graph: null,


      device_entity_arr: [],




      systemName: '智能家居系统',
      inputSystemReq: '',


      designDomainArray: [],





    }
  },
  methods: {

    collapse(){ //点击收缩按钮触发
      this.isCollapse = !this.isCollapse
      if(this.isCollapse){  //收缩
        this.sideWidth = 64
        this.collapseBtnClass = 'el-icon-s-unfold'
        this.logoTextShow = false
      }
      else{   //展开
        this.sideWidth = 200
        this.collapseBtnClass = 'el-icon-s-fold'
        this.logoTextShow = true
      }
    },
    load(){
      this.request.get("/systemReq", ).then(res => {
        console.log(res)
        this.confirmed = res
      })




    },

    generateRecommendations() {
      if (!this.userInput.trim()) {
        this.$message.warning("请输入需求描述");
        return;
      }

      this.request.get("/systemReq/RecommendSystemRequirement/gpt", {
        params: {
          userInput: this.userInput
        }}).then(res => {
        console.log(res)
        this.recommendations = res
      })
      // 模拟调用模型生成结果
      // this.recommendations = [
      //   { id: 1, systemRequirement: "系统 shall enable 家庭医生 获取 数据 from 老人 by 医用腕表" },
      //   { id: 2, systemRequirement: "系统 shall 发送 指令" },
      //   { id: 3, systemRequirement: "系统 shall 存储 数据 in 生命体征数据库" },
      // ];
    },

    generateRecommendationsByGPT() {


      this.request.get("/systemReq/RecommendSystemRequirement/gptNew").then(res => {
        console.log(res)
        this.recommendations = res
      })
      // 模拟调用模型生成结果
      // this.recommendations = [
      //   { id: 1, systemRequirement: "系统 shall enable 家庭医生 获取 数据 from 老人 by 医用腕表" },
      //   { id: 2, systemRequirement: "系统 shall 发送 指令" },
      //   { id: 3, systemRequirement: "系统 shall 存储 数据 in 生命体征数据库" },
      // ];
    },
    addToConfirmed(item) {

      this.request.post("/systemReq/add", {
        userInput : this.userInput,
        systemRequirement: item.systemRequirement
      })

      this.$message.success('添加成功');

      this.load()
    },
    editRequirement(item, index, target) {
      this.editContent = item.systemRequirement;
      this.editIndex = index;
      this.editTarget = target;
      this.editDialogVisible = true;
    },
    saveEdit() {
      if (this.editTarget === 'recommendations') {
        this.$set(this.recommendations, this.editIndex, {
          ...this.recommendations[this.editIndex],
          systemRequirement: this.editContent,
        });
      } else {
        this.$set(this.confirmed, this.editIndex, {
          ...this.confirmed[this.editIndex],
          systemRequirement: this.editContent,
        });
      }
      this.editDialogVisible = false;
    },
    removeItem(index, target) {
      if (target === 'recommendations') this.recommendations.splice(index, 1);
      else this.confirmed.splice(index, 1);
    },
    clearAll() {
      this.recommendations = [];
      this.userInput = "";
    },
    confirmAndExport() {
      console.log("最终系统需求：", this.confirmed);
      this.$message.success("系统需求已确认，可导出 JSON");
    },



    initProblemDiagram() {

      this.request.get("/contextDiagram/select/type2", ).then(res => {
        this.device_entity_arr = res
        console.log(res)

        // 确保数据准备好后再继续执行下面的逻辑
        this.drawProblemDiagram(); // 把后续的绘图逻辑封装进一个方法


      })



    },

    drawProblemDiagram(){

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

      let machineDomain = new MachineDomainShape();
      machineDomain.attr('.machineDomain-label/text',this.systemName);

      machineDomain.position(10, 300);
      this.graph.addCell(machineDomain);


      // 初始化节点存储对象
      const nodes = {};
      // 用于获取或创建节点的函数
      const getNode = (name) => {
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
          newNode.addTo(this.graph); // 将节点添加到图中
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

      console.log(triples)

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
          link.addTo(this.graph); // 将连线添加到图中
          links[linkId] = link; // 将连线存储在 links 对象中
          console.log(link);
        }
      });

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

      this.request.post("/problemDiagram/build").then(res => {

        const arrayList = res  // List<Map<String, List<String>>>
        console.log(arrayList)

        // 清空旧的设计领域数据
        this.designDomainArray = [];

        arrayList.forEach(array => {
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



        })
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


      // this.request.post("/problemDiagram/identify",this.inputSystemReq).then(res => {
      //
      //   const array = res
      //   console.log(array)
      //
      //
      //   Object.keys(array).forEach(key => {
      //     // 先检查属性值是否为空或不存在
      //     if (array[key] && array[key].length > 0) {
      //
      //       if(array[key][5] && array[key][5] === '设计领域'){
      //
      //
      //         //将设计领域单独存储起来!!!!!!!!!!!!!
      //         this.designDomainArray.push({ designDomainName: array[key][0], dataName: array[key][1] });
      //         //[{designDomainName:"",dataName:""},{designDomainName:"",dataName:""}]
      //         console.log("设计领域集合：：：",this.designDomainArray)
      //
      //
      //
      //         const subjectNodeString = array[key][0]
      //         const machineDomain = this.findMachineNodeByLabelText(this.systemName)
      //         console.log(machineDomain)
      //         console.log(this.systemName)
      //
      //
      //         let designDomain = new DesignDomainShape();
      //         designDomain.attr('.designDomain-label/text',subjectNodeString);
      //         designDomain.position(300, 300);
      //         this.graph.addCell(designDomain);
      //
      //         const link1 = new joint.shapes.standard.Link();
      //         link1.source(designDomain);
      //         link1.target(machineDomain);
      //         link1.addTo(this.graph);
      //         link1.attr({
      //           line: {
      //             stroke: 'black',
      //             strokeWidth: 2 ,
      //             sourceMarker: {
      //               'type': 'none',  // 起点无箭头
      //             },
      //             targetMarker: {
      //               'type': 'none',  // 终点无箭头
      //             },
      //           }
      //         });
      //
      //         const systemRequirement = array[key][4]
      //         const interInfo = array[key][1]
      //         const reqFeature = array[key][3]
      //         const systemRequirementNode = getNode.call(this,systemRequirement);
      //
      //         const subjectNode = this.findDesignNodeByLabelText(subjectNodeString);
      //         const link2 = new joint.shapes.standard.Link();
      //         link2.source(systemRequirementNode);
      //         link2.target(subjectNode);
      //         if(reqFeature==="需求引用"){
      //           link1.attr({
      //             line: {
      //               stroke: 'black',
      //               strokeWidth: 2 ,
      //               strokeDasharray: '5,5' ,
      //               targetMarker: {
      //                 'type': 'none', // 终点无箭头
      //               },
      //             }
      //           })
      //         }else if(reqFeature==="需求约束"){
      //           link2.attr({
      //             line: {
      //               stroke: 'black',
      //               strokeWidth: 2 ,
      //               strokeDasharray: '5,5' ,
      //             }
      //           })
      //         }
      //
      //         // 设置标签，将用户输入的信息作为标签内容显示在连线上
      //         link2.label(0, {
      //           attrs: {
      //             text: {
      //               text: interInfo, // 使用输入框中的信息
      //               fill: 'black',
      //               fontSize: 12,
      //               fontWeight: 'bold',
      //             }
      //           },
      //           position: {
      //             offset: 10, // 标签在连线上的偏移
      //           }
      //         });
      //         link2.addTo(this.graph); // 将连线添加到图中
      //
      //
      //
      //
      //
      //
      //       }else{
      //
      //         const systemRequirement = array[key][4]
      //         const subjectNodeString = array[key][0]
      //         const interInfo = array[key][1]
      //         const reqFeature = array[key][3]
      //         const systemRequirementNode = getNode.call(this,systemRequirement);
      //
      //
      //
      //         // 使用 some() 判断（交互信息在设计领域中）是否存在
      //         let exists = this.designDomainArray.some(item => item.dataName === interInfo);
      //
      //         if (exists) {
      //           console.log("字符串在数组中存在");
      //
      //           // 假设你要查找的 dataName
      //           let targetDataName = interInfo;
      //
      //           // 使用 find() 查找第一个匹配的对象
      //           let result = this.designDomainArray.find(item => item.dataName === targetDataName);
      //
      //           console.log(result.designDomainName);  // 输出: DesignDomainName
      //
      //           //!!!!!!!!!!!!需求和设计领域连接
      //           const objectNode = this.findDesignNodeByLabelText(result.designDomainName)
      //           const link1 = new joint.shapes.standard.Link();
      //           link1.source(systemRequirementNode);
      //           link1.target(objectNode);
      //           link1.attr({
      //             line: {
      //               stroke: 'black',
      //               strokeWidth: 2 ,
      //               strokeDasharray: '5,5' ,
      //               targetMarker: {
      //                 'type': 'none', // 终点无箭头
      //               },
      //             }
      //           })
      //
      //           // 设置标签，将用户输入的信息作为标签内容显示在连线上
      //           link1.label(0, {
      //             attrs: {
      //               text: {
      //                 text: interInfo, // 使用输入框中的信息
      //                 fill: 'black',
      //                 fontSize: 12,
      //                 fontWeight: 'bold',
      //               }
      //             },
      //             position: {
      //               offset: 10, // 标签在连线上的偏移
      //             }
      //           });
      //           link1.addTo(this.graph); // 将连线添加到图中
      //
      //
      //
      //
      //
      //
      //         } else {
      //           console.log("字符串在数组中不存在");
      //         }
      //
      //
      //
      //
      //
      //
      //
      //
      //
      //
      //         const subjectNode = this.findNodeByLabelText(subjectNodeString);
      //         const link1 = new joint.shapes.standard.Link();
      //         link1.source(systemRequirementNode);
      //         link1.target(subjectNode);
      //         if(reqFeature==="需求引用"){
      //           link1.attr({
      //             line: {
      //               stroke: 'black',
      //               strokeWidth: 2 ,
      //               strokeDasharray: '5,5' ,
      //               targetMarker: {
      //                 'type': 'none', // 终点无箭头
      //               },
      //             }
      //           })
      //         }else if(reqFeature==="需求约束"){
      //           link1.attr({
      //             line: {
      //               stroke: 'black',
      //               strokeWidth: 2 ,
      //               strokeDasharray: '5,5' ,
      //             }
      //           })
      //         }
      //
      //         // 设置标签，将用户输入的信息作为标签内容显示在连线上
      //         link1.label(0, {
      //           attrs: {
      //             text: {
      //               text: interInfo, // 使用输入框中的信息
      //               fill: 'black',
      //               fontSize: 12,
      //               fontWeight: 'bold',
      //             }
      //           },
      //           position: {
      //             offset: 10, // 标签在连线上的偏移
      //           }
      //         });
      //         link1.addTo(this.graph); // 将连线添加到图中
      //
      //       }
      //
      //
      //
      //
      //
      //
      //     }
      //   });
      //
      //
      //   // 用于获取或创建节点的函数
      //   function getNode(name) {
      //     // 如果节点不存在，则创建新的节点并存储
      //     if (!requirementNodes[name]) {
      //       const newNode = new joint.shapes.standard.Ellipse();
      //       newNode.position(1200, 200 + Object.keys(requirementNodes).length * 100); // 设置节点的位置
      //       newNode.resize(150, 40); // 设置节点大小
      //       newNode.attr({
      //         body: {
      //           fill: 'white',
      //           strokeDasharray: '5,5' ,  //虚线
      //         },
      //         label: {
      //           text: name,
      //           fill: 'black'
      //         }
      //       });
      //       newNode.addTo(this.graph); // 将节点添加到图中
      //       requirementNodes[name] = newNode; // 将节点存储在 nodes 对象中
      //     }
      //     return requirementNodes[name]; // 返回节点，无论是新创建的还是已存在的
      //   }
      //
      //
      //
      //
      //
      //
      // })

      // this.request.post("/systemReq/addSystemRequirements", this.inputSystemReq).then(res => {
      //   if (res) {
      //     this.$message.success("系统需求解析并保存成功");
      //   } else {
      //     this.$message.error("提交失败");
      //   }
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






  },
}
</script>

<style scoped>
.system-requirement-page {
  padding: 20px;
}
.input-bar {
  margin-bottom: 20px;
}
.content-area {
  min-height: 400px;
}
.recommend-card,
.confirm-card {
  margin-bottom: 10px;
}
.recommend-actions,
.confirm-actions {
  margin-top: 8px;
}
.footer-bar {
  margin-top: 20px;
  text-align: right;
}




</style>

