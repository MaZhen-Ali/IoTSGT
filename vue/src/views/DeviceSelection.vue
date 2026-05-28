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

        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>任务意图</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="toggleContent">{{ isContentVisible ? '收起' : '展开' }}</el-button>
          </div>
          <div v-for="item in tableData5" class="text item" v-if="isContentVisible">
<!--            {{item.sys}}需要{{item.task}}以达成{{item.target}}的目的-->
            {{item.sentence}}
          </div>

        </el-card>

        <div>
          <el-row :gutter="12">
            <el-col :span="17">
              <el-card shadow="always">
                根据上面任务意图，我们向你推荐以下设备，请选择：
                <div>
                  {{recommendDevice}}
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>


        <div style="margin: 10px 0;">
          <el-button type="primary" @click="handleAdd">批量选择<i class="el-icon-circle-plus-outline"></i></el-button>

          <el-popconfirm
              class="ml-5"
              confirm-button-text='确定'
              cancel-button-text='我再想想'
              icon="el-icon-info"
              icon-color="red"
              title="您确定批量删除这些数据吗？"
              @confirm="selBatch"
          >
            <el-button type="danger"  slot="reference">批量取消选择<i class="el-icon-remove-outline"></i></el-button>
          </el-popconfirm>

          <el-button type="primary" class="ml-5">导入<i class="el-icon-bottom"></i></el-button>
          <el-button type="primary">导出<i class="el-icon-top"></i></el-button>

          <el-button type="primary" @click="RecommendByGpt">大模型推荐</el-button>
        </div>

        <div>
          <el-row>
            <el-col v-for="option in options" :key="option" :span="24">
              <el-checkbox v-model="checkedValues" border :label="option">
                {{ option.entityName }}
              </el-checkbox>
            </el-col>
          </el-row>

<!--              :label="option"   确保复选框的label是唯一的-->
        </div>





        <div class="mt-5" border stripe>
          已选设备
        </div>
        <el-table :data="tableData1"  width="280px" border stripe :header-cell-class-name="headerBg" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55"></el-table-column>
<!--          <el-table-column prop="id" label="ID" width="80"></el-table-column>-->
          <el-table-column prop="name" label="设备" width="140"></el-table-column>
<!--          <el-table-column prop="type" label="类型"></el-table-column>-->
<!--          <el-table-column prop="applicationlist" label="设备用途列表"></el-table-column>-->

          <el-table-column width="140" label="操作">


            <template slot-scope="scope">
              <el-button @click="handleEdit(scope.row)">查看详细信息<i class="el-icon-search"></i></el-button>
            </template>
          </el-table-column>


          <el-table-column width="220" label="操作">
            <template slot-scope="scope">
              <el-button type="success" @click="handleSelect(scope.row.id)">选择<i class="el-icon-check"></i></el-button>

              <el-button type="info" @click="handleCancelSelect(scope.row.id)">取消选择<i class="el-icon-delete"></i></el-button>


            </template>
          </el-table-column>

          <el-table-column width="220" label="设备个数">
            <template  slot-scope="scope">
              <el-input-number v-model="scope.row.number" @change="handleChange(scope.row.name, scope.row.number)" :min="1" :max="10" label="描述文字"></el-input-number>
            </template>
          </el-table-column>


        </el-table>



        <div class="mt-5">
          未选设备
        </div>
        <el-table :data="tableData2"  width="280px" border stripe :header-cell-class-name="headerBg" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55"></el-table-column>
<!--          <el-table-column prop="id" label="ID" width="80"></el-table-column>-->
          <el-table-column prop="name" label="设备" width="140"></el-table-column>

          <el-table-column width="140" label="操作">
            <template slot-scope="scope">
              <el-button @click="handleEdit(scope.row)">查看详细信息<i class="el-icon-search"></i></el-button>
            </template>
          </el-table-column>

          <el-table-column width="220" label="操作">
            <template slot-scope="scope">
              <el-button type="success" @click="handleSelect(scope.row.id)">选择<i class="el-icon-check"></i></el-button>
              <el-button type="info" @click="handleCancelSelect(scope.row.id)">取消选择<i class="el-icon-delete"></i></el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-dialog title="设备详细信息" :visible.sync="dialogFormVisible" width="50%">
          <el-form label-width="100px" size="small">
            <el-form-item label="设备名称">
              <el-input v-model="form.name" autocomplete="off" :disabled="true"></el-input>
            </el-form-item>
            <el-form-item label="设备类型">
              <el-input v-model="form.type" autocomplete="off" :disabled="true"></el-input>
            </el-form-item>
            <el-form-item label="设备功能列表">
              <el-input v-model="form.applicationlist" autocomplete="off" :disabled="true"></el-input>
            </el-form-item>
            <el-form-item label="设备接口">
              <el-input v-model="form.deviceinterface" autocomplete="off" :disabled="true"></el-input>
            </el-form-item>

          </el-form>

          <div slot="footer" class="dialog-footer">
            <el-button>取 消</el-button>
            <el-button type="primary">确 定</el-button>
          </div>
        </el-dialog>

<!--        <el-transfer-->
<!--            filterable-->
<!--            :filter-method="filterMethod"-->
<!--            :titles='titles'-->
<!--            filter-placeholder="请输入设备名称"-->
<!--            v-model="value"-->
<!--            :data="data">-->
<!--        </el-transfer>-->



        <el-button type="primary" @click="GenerateContextDiagram" class="mt-5">生成上下文图</el-button>
        <el-button type="primary" @click="ShowDeviceReq" class="mt-5">查看设备需求</el-button>
        <el-button type="primary" @click="ShowSysReq" class="mt-5">查看系统需求</el-button>

        <ContextDiagram v-show="downShow" />

        <el-table :data="tableData3" v-show="downShow1" border stripe :header-cell-class-name="headerBg" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column prop="sys" label="系统" width="140">太阳搜索控制系统</el-table-column>
          <el-table-column label="需要" width="120">需要</el-table-column>
          <el-table-column prop="name" label="设备名称"></el-table-column>
          <el-table-column label="为了">为了</el-table-column>
          <el-table-column prop="intent" label="意图"></el-table-column>

          <el-table-column label="操作">


            <template slot-scope="scope">
              <el-button type="success" @click="handleEdit(scope.row)">编辑<i class="el-icon-edit"></i></el-button>

              <el-popconfirm
                  class="ml-5"
                  confirm-button-text='确定'
                  cancel-button-text='我再想想'
                  icon="el-icon-info"
                  icon-color="red"
                  title="您确定删除吗？"
                  @confirm="del(scope.row.id)"
              >
                <el-button type="danger" slot="reference">删除<i class="el-icon-remove-outline"></i></el-button>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>

        <el-table :data="tableData4" v-show="downShow2" border stripe :header-cell-class-name="headerBg" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column prop="sys" label="系统" width="140">太阳搜索控制系统</el-table-column>
          <el-table-column label="需要" width="120">需要</el-table-column>
          <el-table-column prop="physical" label="交互信息"></el-table-column>
          <el-table-column label="用">用</el-table-column>
          <el-table-column prop="name" label="设备名称"></el-table-column>
          <el-table-column label="为了">为了</el-table-column>
          <el-table-column prop="intent" label="系统需求"></el-table-column>
          <el-table-column label="操作">


            <template slot-scope="scope">
              <el-button type="success" @click="handleEdit(scope.row)">编辑<i class="el-icon-edit"></i></el-button>

              <el-popconfirm
                  class="ml-5"
                  confirm-button-text='确定'
                  cancel-button-text='我再想想'
                  icon="el-icon-info"
                  icon-color="red"
                  title="您确定删除吗？"
                  @confirm="del(scope.row.id)"
              >
                <el-button type="danger" slot="reference">删除<i class="el-icon-remove-outline"></i></el-button>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>







      </el-main>
    </el-container>
  </el-container>

</template>

<script>

import Aside from "@/components/Aside";
import Header from "@/components/Header";
import ContextDiagram from "@/components/ContextDiagram";
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
      recommendDevice: '',
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
      deviceinterface:'',
      dialogFormVisible: false,
      value: [],
      downShow: false,
      downShow1: false,
      downShow2: false,
      filterMethod(query, item) {
        return item.pinyin.indexOf(query) > -1;
      },


      isContentVisible: false, // 控制内容是否可见

      checked1: true,
      checked2: false,
      checkedValues: [],
      options: [],


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

      this.request.get("/selectedDevice/unselected", ).then(res => {
        this.tableData2 = res
      })

      this.request.get("/taskIntent", ).then(res => {
        this.tableData5 = res
      })

      this.request.get("/selectedDevice/gpt", ).then(res => {
        this.recommendDevice = res.key1
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
      this.form = row
      this.dialogFormVisible = true
    },
    GenerateContextDiagram(){
      this.downShow = !(this.downShow)
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
      this.request.get("/selectedDevice/recommend/gpt", ).then(res => {
        console.log(res)
        this.options = res

      })

    }







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
    width: 1000px;
  }

</style>