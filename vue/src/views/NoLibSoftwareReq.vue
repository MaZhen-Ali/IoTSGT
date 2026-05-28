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

        <div class="container">
          <el-card class="box-card">
            <div slot="header" class="clearfix">
              <span>推荐软件需求</span>
            </div>

            <el-row :gutter="20">
              <!-- 系统需求列表 -->
              <el-col :span="6">
                <el-card shadow="never" class="sub-card">
                  <div class="section-title">系统需求列表</div>
                  <el-menu>
                    <template v-for="(descList, reqTitle) in groupedRequirements">
                      <el-menu-item :key="reqTitle" class="grouped-item">
                        <div><strong>{{ reqTitle }}</strong></div>
                        <ul class="grouped-desc-list">
                          <li v-for="(desc, i) in descList" :key="i">
                            {{ desc }}
                          </li>
                        </ul>
                      </el-menu-item>
                    </template>
                  </el-menu>
                </el-card>
              </el-col>

              <!-- 推荐软件需求 和 已选软件需求：并列两列 -->
              <el-col :span="18">
                <el-card shadow="never" class="sub-card">
                  <div class="section-title">
                    推荐软件需求列表
                    <el-input
                        placeholder="搜索..."
                        size="mini"
                        v-model="searchKeyword"
                        class="search-input"
                    />
                  </div>

                  <el-table
                      :data="tableData1"
                      border
                      stripe
                      :header-cell-class-name="headerBg"
                  >
                    <el-table-column prop="id" label="ID" width="60" />
                    <el-table-column prop="description" label="软件需求描述" />
                    <el-table-column prop="flag" label="是否选择" width="100" />
                    <el-table-column label="操作" width="260">
                      <template slot-scope="scope" align="center">
                        <el-button
                            type="success"
                            size="mini"
                            style="width: 100px"
                            v-if="scope.row.flag === '否'"
                            @click="handleSelect(scope.row.id)"
                        >
                          选择
                        </el-button>
                        <el-button
                            type="warning"
                            size="mini"
                            style="width: 100px"
                            v-if="scope.row.flag === '是'"
                            @click="handleCancelSelect(scope.row.id)"
                        >
                          取消选择
                        </el-button>

                        <el-button
                            type="primary"
                            size="mini"
                            style="margin-right: 5px"
                            @click="editSoftwareRequirement(scope.row)"
                        >
                          修改
                        </el-button>
                        <el-button
                            type="danger"
                            size="mini"
                            @click="deleteSoftwareRequirement(scope.row.id)"
                        >
                          删除
                        </el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                </el-card>
              </el-col>


            </el-row>

            <!-- 操作按钮 -->
            <div class="action-buttons">
              <el-button type="primary" @click="generateSoftwareRequirement">推荐软件需求</el-button>
              <el-button type="primary" @click="dialogFormVisible = true">添加软件需求</el-button>
            </div>

          </el-card>
        </div>


        <el-dialog title="添加软件需求" :visible.sync="dialogFormVisible" width="30%">
          <el-form :model="inputForm">
            <el-form-item label="软件需求描述" :label-width="formLabelWidth">
              <el-input type="textarea" v-model="inputForm.description" autocomplete="off"></el-input>
            </el-form-item>
          </el-form>
          <span slot="footer" class="dialog-footer">
            <el-button @click="dialogFormVisible = false">取消</el-button>
            <el-button type="primary" @click="submitSoftwareRequirement">添加</el-button>
          </span>
        </el-dialog>


        <el-dialog title="编辑软件需求" :visible.sync="dialogFormVisibleEdit" width="30%">
          <el-form label-width="80px" size="small" :model="editForm">
            <el-form-item label="软件需求描述">
              <el-input v-model="editForm.description" autocomplete="off"></el-input>
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
import VueOfficeDocx from "@vue-office/docx";

export default {
  name: "NoLibSoftwareReq",
  components: {VueOfficeDocx, Header, Aside},
  created() {
    this.load();
  },
  data(){
    return{
      collapseBtnClass:'el-icon-s-fold',
      isCollapse:false,
      sideWidth:200,
      logoTextShow:true,
      headerBg:'headerBg',

      tableData1:'',


      rawSystemRequirements: [], // 后端原始数据
      groupedRequirements: {},  // 分组后的数据



      searchKeyword: "",

      dialogFormVisible: false,
      inputForm: {
        description: ""
      },
      formLabelWidth: "120px",

      editForm: {
        id:null,
        description: "",
        flag:'',
      },
      dialogFormVisibleEdit: false,



    }
  },

  watch: {
    searchKeyword(newVal) {
      if (!newVal) {
        // 如果搜索框清空，重新加载原始数据
        this.load();
      } else {
        this.tableData1 = this.tableData1.filter(item =>
            item.description.toLowerCase().includes(newVal.toLowerCase())
        );
      }
    }
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
    load(){
      this.request.get("/systemReq", ).then(res => {
        console.log(res)
        this.rawSystemRequirements = res

        // 分组逻辑
        this.groupedRequirements = this.rawSystemRequirements.reduce((acc, item) => {
          if (!acc[item.requirement]) {
            acc[item.requirement] = [];
          }
          acc[item.requirement].push(item.description);
          return acc;
        }, {});
      })

      this.request.get("/softwareRequirement", ).then(res => {
        console.log(res)
        this.tableData1 = res
      })


    },



    generateSoftwareRequirement() {
      this.request.get("/softwareRequirement/NoLibRecommendSoftwareRequirement/gpt").then(res => {
        console.log(res)
      })


    },


    handleSelect(id) {

      this.request.post("/softwareRequirement/selectSoftwareRequirement/" + id).then(res => {
        if (res) {
          this.$message.success("选择成功")
          this.load()
        } else {
          this.$message.success("选择失败")
        }
      })
    },

    handleCancelSelect(id) {
      this.request.post("/softwareRequirement/cancelSelectSoftwareRequirement/" + id).then(res => {
        if (res) {
          this.$message.success("取消成功")
          this.load()
        } else {
          this.$message.success("取消失败")
        }
      })
    },


    submitSoftwareRequirement() {
      if (!this.inputForm.description.trim()) {
        this.$message.warning("请输入软件需求描述！");
        return;
      }

      this.request.post("/softwareRequirement/addSoftwareRequirement", {
        id : "",
        description: this.inputForm.description,
        flag: "是"
      }).then(res => {
        if (res) {
          this.$message.success("添加成功");
          this.dialogFormVisible = false;
          this.inputForm.description = "";
          this.load(); // 刷新表格数据
        } else {
          this.$message.error("添加失败");
        }
      });
    },



    deleteSoftwareRequirement(id){
      this.request.delete("/softwareRequirement/delSoftwareRequirement/" + id).then(res => {
        if (res) {
          this.$message.success("删除成功")
          this.load()
        } else {
          this.$message.success("删除失败")
        }
      })
    },


    editSoftwareRequirement(row)
    {
      this.editForm = row
      this.dialogFormVisibleEdit = true
    },
    save() {
      console.log(this.editForm)

      this.request.post("/softwareRequirement/editSoftwareRequirement", this.editForm).then(res => {
            if (res) {
              this.$message.success("保存成功")
              this.dialogFormVisibleEdit = false
              this.editForm = {}
              this.load()
            } else {
              this.$message.success("保存失败")
            }
          }
      )
    },
    cancel()
    {
      this.dialogFormVisibleEdit = false
    },











  }

}

</script>



<style scoped>
.container {
  padding: 20px;
}
.box-card {
  border-radius: 8px;
}
.sub-card {
  height: 500px;
  overflow: auto;
}
.section-title {
  font-weight: bold;
  margin-bottom: 10px;
}
.search-input {
  width: 60%;
  margin-top: 5px;
}
.action-buttons {
  text-align: right;
  margin-top: 20px;
}
.grouped-item {
  white-space: normal;
  height: auto;
  line-height: 1.4;
  margin-bottom: 14px;
}
.grouped-desc-list {
  padding-left: 12px;
  font-size: 13px;
  color: #666;
}
.grouped-desc-list li {
  margin-bottom: 6px;
}
</style>