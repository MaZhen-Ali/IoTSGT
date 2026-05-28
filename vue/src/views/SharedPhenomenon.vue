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
          <div v-for="item in tableData1" class="text item"  v-if="isContentVisible">
<!--            {{item.sys}}需要{{item.task}}以达成{{item.target}}的目的-->
            {{item.sentence}}
          </div>
        </el-card>


        <div>
          <el-row :gutter="12">
            <el-col :span="8">
              <el-card shadow="always">
                根据上面任务意图，我们向你推荐以下设备功能，请选择：
              </el-card>
            </el-col>
          </el-row>
        </div>




        <el-main>
          <el-table :data="tableData3" border stripe :header-cell-class-name="headerBg" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55"></el-table-column>

            <el-table-column prop="id" label="id" width="50"></el-table-column>
            <el-table-column prop="name" label="设备名称" width="150"></el-table-column>
            <el-table-column prop="application" label="设备功能" width="800"></el-table-column>

            <el-table-column width="220" label="操作">
              <template slot-scope="scope">
                <el-button type="success" @click="handleSelect(scope.row.id)">选择<i class="el-icon-check"></i></el-button>
                <el-button type="info" @click="handleCancelSelect(scope.row.id)">取消选择<i class="el-icon-delete"></i></el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-main>

        <el-main>
          <el-table :data="tableData2" border stripe :header-cell-class-name="headerBg" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55"></el-table-column>

            <el-table-column prop="id" label="id" width="50"></el-table-column>
            <el-table-column prop="name" label="设备名称" width="150"></el-table-column>
            <el-table-column prop="application" label="设备功能" width="800"></el-table-column>

            <el-table-column width="220" label="操作">
              <template slot-scope="scope">
                <el-button type="success" @click="handleSelect(scope.row.id)">选择<i class="el-icon-check"></i></el-button>
                <el-button type="info" @click="handleCancelSelect(scope.row.id)">取消选择<i class="el-icon-delete"></i></el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-main>









      </el-main>



<!--      <el-main>-->

<!--        <el-input-number v-model="similarityNum" @change="handleChange" :min="0.00" :max="1.00" :precision="2" :step="0.10" label="描述文字"></el-input-number>-->
<!--        <el-button type="primary" @click="selectBySimilarityNum(similarityNum)" class="ml-5">查询</el-button>-->
<!--        -->
<!--        <el-table :data="tableData2" border stripe :header-cell-class-name="headerBg" @selection-change="handleSelectionChange">-->
<!--          <el-table-column type="selection" width="55"></el-table-column>-->



<!--          <el-table-column prop="sys" label="系统" width="140">太阳搜索控制系统</el-table-column>-->
<!--          <el-table-column label="需要" width="50">需要</el-table-column>-->
<!--          <el-table-column prop="physical" label="交互信息" width="180"></el-table-column>-->
<!--          <el-table-column label="用" width="40">用</el-table-column>-->
<!--          <el-table-column prop="device" label="设备名称" width="110"></el-table-column>-->
<!--          <el-table-column label="为了" width="50">为了</el-table-column>-->
<!--          <el-table-column prop="task" label="任务"width="100"></el-table-column>-->

<!--          <el-table-column width="10px"></el-table-column>-->

<!--          <el-table-column prop="task" width="100" label="任务"></el-table-column>-->
<!--&lt;!&ndash;          <el-table-column prop="device" width="110" label="设备"></el-table-column>&ndash;&gt;-->
<!--          <el-table-column prop="physical" width="180" label="物理量"></el-table-column>-->
<!--          <el-table-column prop="similarity" width="160" label="相似度"></el-table-column>-->


<!--          <el-table-column width="220" label="操作">-->
<!--            <template slot-scope="scope">-->
<!--              <el-button type="success" @click="handleSelect(scope.row.id)">选择<i class="el-icon-check"></i></el-button>-->
<!--              <el-button type="info" @click="handleCancelSelect(scope.row.id)">取消选择<i class="el-icon-delete"></i></el-button>-->
<!--            </template>-->
<!--          </el-table-column>-->
<!--        </el-table>-->
<!--      </el-main>-->







    </el-container>
  </el-container>

</template>


<script>
import Aside from "@/components/Aside.vue";
import Header from "@/components/Header.vue";
import ContextDiagram from "@/components/ContextDiagram.vue";

export default {
  name: "SystemReq",
  data(){
    return {
      collapseBtnClass:'el-icon-s-fold',
      isCollapse:false,
      sideWidth:200,
      logoTextShow:true,
      headerBg:'headerBg',
      tableData1: [],
      tableData2: [],
      tableData3: [],
      arr1: { id : 1,city: '苏州', remarks: '学校' },
      arr: [
          { id : 1,
            city: '苏州', remarks: '学校' },
        { id : 2, city: '扬州', remarks: '老家' }
      ],

      similarityNum:0.50,

      isContentVisible: false, // 控制内容是否可见


    }

  },
  components: {
    ContextDiagram,
    Header,
    Aside
  },
  created() {
    this.load();
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
    handleSelectionChange(val)
    {
      console.log(val)
      this.multipleSelection = val
    },
    load(){

      this.request.get("/taskIntent", ).then(res => {
        this.tableData1 = res
      })

      this.request.get("/applicationList/selected", ).then(res => {
        this.tableData2 = res
      })

      this.request.get("/applicationList/select/gpt", ).then(res => {
        this.tableData3 = res
      })
    },

    handleChange(value) {
      console.log(value);
    },

    selectBySimilarityNum(similarityNum){

      this.request.get("/systemReq/select/"+similarityNum, ).then(res => {
        this.tableData2 = res
      })
    },
    handleSelect(id)
    {
      this.request.post("/applicationList/select/" + id).then(res => {
        if (res) {
          this.$message.success("选择成功")
          this.load()
        } else {
          this.$message.success("选择失败")
        }
      })
    },
    handleCancelSelect(id){
      this.request.post("/applicationList/cancelSelect/" + id).then(res => {
        if (res) {
          this.$message.success("取消成功")
          this.load()
        } else {
          this.$message.success("取消失败")
        }
      })
    },

    toggleContent() {
      this.isContentVisible = !this.isContentVisible; // 切换内容可见性
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
    width: 1000px;
  }

</style>