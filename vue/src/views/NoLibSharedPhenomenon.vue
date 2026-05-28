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
                根据上面任务意图，我们向你推荐以下系统需求，请选择：
              </el-card>
            </el-col>
          </el-row>
        </div>


        <div style="margin: 10px 0;display: flex">
          <el-input v-model="informationForm.device" placeholder="设备名称" style="width: 150px; margin: 0 10px;"></el-input>
          <el-input v-model="informationForm.interactiveInformation" placeholder="交互信息" style="width: 150px; margin: 0 10px;"></el-input>
          <el-input v-model="informationForm.entity" placeholder="实体名称" style="width: 150px; margin: 0 10px;"></el-input>
          <el-button type="primary" @click="addInfo">添加</el-button>
        </div>




        <el-main>
          未选
          <el-table :data="tableData3" border stripe :header-cell-class-name="headerBg" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55"></el-table-column>

            <el-table-column prop="id" label="id" width="50"></el-table-column>
            <el-table-column prop="entity" label="实体" width="150"></el-table-column>
            <el-table-column prop="interactiveInformation" label="信息" width="300"></el-table-column>
            <el-table-column prop="device" label="设备" width="200"></el-table-column>

            <el-table-column width="220" label="操作">
              <template slot-scope="scope">
                <el-button type="success" @click="handleSelect(scope.row)">选择<i class="el-icon-check"></i></el-button>
                <el-button type="info" @click="handleCancelSelect(scope.row.id)">取消选择<i class="el-icon-delete"></i></el-button>
              </template>
            </el-table-column>





          </el-table>
        </el-main>

        <el-main>
          已选
          <el-table :data="tableData2" border stripe :header-cell-class-name="headerBg" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55"></el-table-column>

            <el-table-column prop="id" label="id" width="50"></el-table-column>
            <el-table-column prop="end" label="实体" width="150"></el-table-column>
            <el-table-column prop="link" label="信息" width="300"></el-table-column>
            <el-table-column prop="begin" label="设备" width="200"></el-table-column>

            <el-table-column width="220" label="操作">
              <template slot-scope="scope">
                <el-button type="success" @click="handleSelect(scope.row.id)">选择<i class="el-icon-check"></i></el-button>
                <el-button type="info" @click="handleCancelSelect(scope.row.id)">取消选择<i class="el-icon-delete"></i></el-button>
              </template>
            </el-table-column>

            <el-table-column width="220" label="编辑">
              <template slot-scope="scope">
                <el-button type="success" @click="handleEdit(scope.row)">编辑<i class="el-icon-edit"></i></el-button>
              </template>
            </el-table-column>
          </el-table>

        </el-main>

        <el-dialog title="系统需求" :visible.sync="dialogFormVisible" width="30%">
          <el-form label-width="80px" size="small">
            <el-form-item label="实体">
              <el-input v-model="form.end" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item label="交互信息">
              <el-input v-model="form.link" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item label="设备">
              <el-input v-model="form.begin" autocomplete="off"></el-input>
            </el-form-item>
          </el-form>

          <div slot="footer" class="dialog-footer">
            <el-button @click="cancel">取 消</el-button>
            <el-button type="primary" @click="save">确 定</el-button>
          </div>
        </el-dialog>






      </el-main>






    </el-container>
  </el-container>

</template>


<script>
import Aside from "@/components/Aside.vue";
import Header from "@/components/Header.vue";
import ContextDiagram from "@/components/ContextDiagram.vue";

export default {
  name: "SharedPhenomenon",
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
      form: {},
      dialogFormVisible: false,


      informationForm: {
        device: '',
        interactiveInformation: '',
        entity: ''
      }


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

      this.request.get("/contextDiagram", ).then(res => {
        this.tableData2 = res
      })

      this.request.get("/sharedPhenomenon/NoLibRecommendSharedPhenomenon/gpt", ).then(res => {
        this.tableData3 = res
      })

    },

    handleChange(value) {
      console.log(value);
    },

    handleSelect(row)
    {
      console.log(row)
      this.request.post("/contextDiagram/NoLibAddInfo",row).then(res => {
        if (res) {
          this.$message.success("选择成功")

        } else {
          this.$message.success("选择失败")
        }
      })
    },
    handleCancelSelect(id){
      this.request.delete("/contextDiagram/NoLibDelInfo/" + id).then(res => {
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


    handleEdit(row)
    {
      this.form = row
      this.dialogFormVisible = true
    },
    save() {

      this.request.post("/contextDiagram", this.form).then(res => {
            if (res) {
              this.$message.success("保存成功")
              this.dialogFormVisible = false
              this.form = {}
              this.load()
            } else {
              this.$message.success("保存失败")
            }
          }
      )
    },
    cancel()
    {
      this.dialogFormVisible = false
    },

    addInfo(){
      this.request.post("/contextDiagram/NoLibAddInfo", this.informationForm).then(res => {
            if (res) {
              this.$message.success("添加成功")
              // 正确清空每个字段，而不是整个对象
              this.informationForm.device = ''
              this.informationForm.interactiveInformation = ''
              this.informationForm.entity = ''
              this.load()
            } else {
              this.$message.success("添加失败")
            }
          }
      )


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