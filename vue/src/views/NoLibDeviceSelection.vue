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

        <el-row :gutter="20" style="margin: 20px">
          <!-- 左侧：任务意图 + 设备操作 -->
          <el-col :span="12" style="display: flex; flex-direction: column; height: 100%">

            <!-- ⬇️ 1. 左上：任务意图卡片 -->
            <!-- ✅ 新增外层包裹，使其独立为“左上”模块 -->
            <div style="flex: 1; max-height: 300px; overflow: auto; margin-bottom: 10px;">
              <el-card class="box-card" style="padding: 10px;">
                <div slot="header" class="clearfix">
                  <span style="font-size: 14px;">根据任务意图，我们向你推荐以下设备，请选择：</span>
                  <el-button style="float: right; padding: 3px 0" type="text" @click="toggleContent">
                    {{ isContentVisible ? '收起' : '展开' }}
                  </el-button>
                </div>
                <div v-if="isContentVisible">
                  <div
                      v-for="item in tableData5"
                      class="text item"
                      style="font-size: 13px; line-height: 1.5; margin-bottom: 5px;"
                  >
                    {{ item.sentence }}
                  </div>
                </div>
              </el-card>
            </div>


            <!-- ⬇️ 2. 左下：设备选择与交互 -->
            <!-- ✅ 包裹在 div 中作为“左下”模块 -->
            <div style="flex: 2; overflow: auto;">
              <!-- 操作按钮 -->
              <div style="margin: 10px 0; display: flex; flex-wrap: wrap;">
                <el-button type="primary" @click="RecommendByGpt">大模型推荐</el-button>
                <el-button type="primary" @click="submit">确定选择</el-button>
                <el-input v-model="inputDeviceName" placeholder="请输入内容" style="width: 150px; margin: 0 10px;"></el-input>
                <el-button type="primary" @click="addDevice">添加设备</el-button>
                <el-button type="primary" @click="initProblemDiagram">生成上下文图</el-button>
              </div>

              <!-- 复选框 -->
              <el-row>
                <el-col v-for="option in options" :key="option" :span="24">
                  <el-checkbox v-model="checkedValues" border :label="option">
                    {{ option.name }}
                  </el-checkbox>
                </el-col>
              </el-row>

              <!-- 设备表格 -->
              <div style="margin-top: 20px;">
                <b>已选设备</b>
                <el-table :data="tableData1" style="width: 100%;" border stripe :header-cell-class-name="headerBg" @selection-change="handleSelectionChange">
                  <el-table-column type="selection" width="40"></el-table-column>
                  <el-table-column prop="name" label="设备" width="120"></el-table-column>
                  <el-table-column width="140" label="操作">
                    <template slot-scope="scope">
                      <el-button @click="handleEdit(scope.row)">查看详细信息<i class="el-icon-search"></i></el-button>
                    </template>
                  </el-table-column>
                  <el-table-column width="200" label="操作">
                    <template slot-scope="scope">
                      <el-button type="success" @click="handleSelect(scope.row.id)">选择<i class="el-icon-check"></i></el-button>
                      <el-button type="info" @click="handleCancelSelect(scope.row.id)">取消选择<i class="el-icon-delete"></i></el-button>
                    </template>
                  </el-table-column>
                  <el-table-column label="设备个数">
                    <template slot-scope="scope">
                      <el-input-number v-model="scope.row.number" @change="handleChange(scope.row.name, scope.row.number)" :min="1" :max="10" label="描述文字"></el-input-number>
                    </template>
                  </el-table-column>
                </el-table>
              </div>

              <!-- 设备详细信息对话框 -->
              <el-dialog title="设备详细信息" :visible.sync="dialogFormVisible" width="50%">
                <el-form label-width="100px" size="small">
                  <el-form-item label="设备名称">
                    <el-input v-model="form.name" autocomplete="off"></el-input>
                  </el-form-item>
                  <el-form-item label="设备类型">
                    <el-input v-model="form.type" autocomplete="off"></el-input>
                  </el-form-item>
                  <el-form-item label="设备功能列表">
                    <el-input v-model="form.deviceCapacity" autocomplete="off"></el-input>
                  </el-form-item>
                  <el-form-item label="设备接口">
                    <el-input v-model="form.deviceInterface" autocomplete="off"></el-input>
                  </el-form-item>
                </el-form>
                <div slot="footer" class="dialog-footer">
                  <el-button>取 消</el-button>
                  <el-button type="primary">确 定</el-button>
                </div>
              </el-dialog>
            </div>
          </el-col>

          <!-- 右侧：上下文图 -->
          <el-col :span="12">
            <div id="context-diagram" ref="canvas" style="height: 100%; min-height: 500px; border: 1px solid #ccc;">
              <!-- 使用 JointJS 或 SVG 显示上下文图 -->
            </div>
          </el-col>
        </el-row>






<!--        <el-table :data="tableData3" v-show="downShow1" border stripe :header-cell-class-name="headerBg" @selection-change="handleSelectionChange">-->
<!--          <el-table-column type="selection" width="55"></el-table-column>-->
<!--          <el-table-column prop="sys" label="系统" width="140">太阳搜索控制系统</el-table-column>-->
<!--          <el-table-column label="需要" width="120">需要</el-table-column>-->
<!--          <el-table-column prop="name" label="设备名称"></el-table-column>-->
<!--          <el-table-column label="为了">为了</el-table-column>-->
<!--          <el-table-column prop="intent" label="意图"></el-table-column>-->

<!--          <el-table-column label="操作">-->


<!--            <template slot-scope="scope">-->
<!--              <el-button type="success" @click="handleEdit(scope.row)">编辑<i class="el-icon-edit"></i></el-button>-->

<!--              <el-popconfirm-->
<!--                  class="ml-5"-->
<!--                  confirm-button-text='确定'-->
<!--                  cancel-button-text='我再想想'-->
<!--                  icon="el-icon-info"-->
<!--                  icon-color="red"-->
<!--                  title="您确定删除吗？"-->
<!--                  @confirm="del(scope.row.id)"-->
<!--              >-->
<!--                <el-button type="danger" slot="reference">删除<i class="el-icon-remove-outline"></i></el-button>-->
<!--              </el-popconfirm>-->
<!--            </template>-->
<!--          </el-table-column>-->
<!--        </el-table>-->

<!--        <el-table :data="tableData4" v-show="downShow2" border stripe :header-cell-class-name="headerBg" @selection-change="handleSelectionChange">-->
<!--          <el-table-column type="selection" width="55"></el-table-column>-->
<!--          <el-table-column prop="sys" label="系统" width="140">太阳搜索控制系统</el-table-column>-->
<!--          <el-table-column label="需要" width="120">需要</el-table-column>-->
<!--          <el-table-column prop="physical" label="交互信息"></el-table-column>-->
<!--          <el-table-column label="用">用</el-table-column>-->
<!--          <el-table-column prop="name" label="设备名称"></el-table-column>-->
<!--          <el-table-column label="为了">为了</el-table-column>-->
<!--          <el-table-column prop="intent" label="系统需求"></el-table-column>-->
<!--          <el-table-column label="操作">-->


<!--            <template slot-scope="scope">-->
<!--              <el-button type="success" @click="handleEdit(scope.row)">编辑<i class="el-icon-edit"></i></el-button>-->

<!--              <el-popconfirm-->
<!--                  class="ml-5"-->
<!--                  confirm-button-text='确定'-->
<!--                  cancel-button-text='我再想想'-->
<!--                  icon="el-icon-info"-->
<!--                  icon-color="red"-->
<!--                  title="您确定删除吗？"-->
<!--                  @confirm="del(scope.row.id)"-->
<!--              >-->
<!--                <el-button type="danger" slot="reference">删除<i class="el-icon-remove-outline"></i></el-button>-->
<!--              </el-popconfirm>-->
<!--            </template>-->
<!--          </el-table-column>-->
<!--        </el-table>-->







      </el-main>
    </el-container>
  </el-container>

</template>

<script>

import Aside from "@/components/Aside";
import Header from "@/components/Header";
import ContextDiagram from "@/components/ContextDiagram";
import * as joint from "jointjs";
export default {
  name: "DeviceSelection",
  data(){

    const generateData = _ => {
      const data = [];
      const cities = ['太阳敏感器', '陀螺', '推力器', '数据管理计算机'];
      const pinyin = ['太阳敏感器', '陀螺', '推力器', '数据管理计算机'];
      cities.forEach((city, index) => {
        data.push({
          label: city,
          key: index,
          pinyin: pinyin[index]
        });
      });
      return data;
    };
    return {

      collapseBtnClass:'el-icon-s-fold',
      isCollapse:false,
      sideWidth:200,
      logoTextShow:true,
      tableData1: [],
      tableData2: [],
      tableData3: [],
      tableData4: [],
      tableData5: [],
      headerBg:'headerBg',
      titles:['设备列表','已选设备'],
      data: generateData(),
      form: {},
      name:'',
      type:'',
      applicationlist:'',
      deviceCapacity:'',
      deviceinterface:'',
      deviceInterface:'',
      dialogFormVisible: false,
      value: [],

      downShow1: false,
      downShow2: false,
      filterMethod(query, item) {
        return item.pinyin.indexOf(query) > -1;
      },


      isContentVisible: true, // 控制内容是否可见

      checked1: true,
      checked2: false,
      checkedValues: [],
      options: [],
      inputDeviceName:'',



      paper: null,
      graph: null,
      device_entity_arr: [],
      systemName: '老人家庭监护软件',


    }

  },
  components: {
    Aside,
    Header,
    ContextDiagram
  },
  created() {
    this.load();
  },
  methods:{
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
    handleSelectionChange(val)
    {
      console.log(val)
      this.multipleSelection = val
    },
    load()
    {
      this.request.get("/selectedDevice/selected", ).then(res => {
        console.log(res)
        console.log("_____________")
        // console.log(res[0].name)

        //解析res中的数据
        this.tableData1 = res
        console.log("_____________")
        console.log(this.tableData)
        console.log(typeof (this.tableData))
        // console.log(typeof (this.tableData[0]))
        // console.log(typeof (this.tableData[0].name))
      })

      this.request.get("/taskIntent", ).then(res => {
        this.tableData5 = res
      })

    },
    selBatch()
    {
      let ids = this.multipleSelection.map(v => v.id)  //[{},{},{}] => [1,2,3]
      // console.log(typeof (ids))
      this.request.post("/deviceLibrary/select/batch", ids).then(res => {
        if (res) {
          this.$message.success("批量删除成功")
          this.load()
        } else {
          this.$message.success("批量删除失败")
        }
      })
    },
    handleSelect(id)
    {
      this.request.post("/selectedDevice/select/" + id).then(res => {
        if (res) {
          this.$message.success("选择成功")
          this.load()
        } else {
          this.$message.success("选择失败")
        }
      })
    },
    handleCancelSelect(id){
      this.request.delete("/selectedDevice/cancelSelect/" + id).then(res => {
        if (res) {
          this.$message.success("取消成功")
          this.load()
        } else {
          this.$message.success("取消失败")
        }
      })
    },
    handleEdit(row)
    {

      console.log(row.name)
      this.form = row
      this.dialogFormVisible = true

      this.request.post("/selectedDevice/NoLibRecommendDeviceApplicationList/gpt", row.name).then(res => {
        console.log(res)
        this.form.type = res[0].type
        this.form.deviceCapacity = res[0].deviceCapacity
        this.form.deviceInterface = res[0].deviceInterface
    })


    },

    handleChange(name,number) {
      console.log(name,number);
      this.request.post("/selectedDevice/select/num",this.tableData1)
      console.log(this.tableData1);


    },

    ShowDeviceReq(){
      this.request.get("/selectedDevice/selected", ).then(res => {
        console.log(res)
        console.log("_____________")
        // console.log(res[1].sys)

        //解析res中的数据
        this.tableData3 = res
      })
      this.downShow1 = !(this.downShow1)
    },
    ShowSysReq(){
      this.request.get("/selectedDevice/selected", ).then(res => {
        console.log(res)
        console.log("_____________")
        // console.log(res[1].sys)

        //解析res中的数据
        this.tableData4 = res
      })
      this.downShow2 = !(this.downShow2)
    },

    toggleContent() {
      this.isContentVisible = !this.isContentVisible; // 切换内容可见性
    },


    RecommendByGpt(){
      this.request.get("/selectedDevice/NoLibRecommendDevice/gpt", ).then(res => {
        console.log(res)
        this.options = res

      })

    },

    submit() {
      console.log("已选中的值:", this.checkedValues);


      this.request.post("/selectedDevice/NoLibSelectDevice",  this.checkedValues).then(res => {
        console.log(res)

        if (res) {
          this.request.get("/selectedDevice/selected", ).then(res => {
            this.tableData1 = res
          })
        }

      })
    },

    addDevice(){

      this.request.post("/selectedDevice/NoLibAddDevice",  this.inputDeviceName).then(res => {
        console.log(res)

        if (res) {
          this.request.get("/selectedDevice/selected", ).then(res => {
            this.tableData1 = res
          })
        } else {

        }
      })


      this.request.get("/selectedDevice/selected", ).then(res => {
        this.tableData1 = res
      })


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
        width: 700,
        height: 500,
        gridSize: 10, // 网格大小
        drawGrid: true, // 显示网格
        background: {
          color: 'rgba(255, 255, 255, 0.3)'
          // color: 'rgba(0, 255, 0, 0.3)'
        }
      })

      let machineDomain = new MachineDomainShape();
      machineDomain.attr('.machineDomain-label/text',this.systemName);

      machineDomain.position(10, 150);
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
      let xPos = 250;  // 初始 x 坐标
      let yPos1 = 150;  // 初始 y 坐标
      let yPos2 = 150;  // 初始 y 坐标
      const xSpacing = 100;  // 节点之间的水平间隔
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
        objectNode.position(500, yPos2);
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










  }
}



</script>



<style scoped>
.text {
  font-size: 14px;
}

.item {
  margin-bottom: 18px;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both
}

.box-card {
  width: 650px;
}

</style>