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


        <el-button type="primary" @click="GenerateReq" class="ml-5">生成软件需求</el-button>

        <el-button type="primary" @click="AddReq" class="ml-5">添加软件需求</el-button>

        <el-dialog title="软件需求填写" :visible.sync="dialogFormVisible" width="90%">
          <el-form :inline="true" label-width="80px" size="small">

            <p class="ml-5">句式1：普适型系统能力</p>
            <span class="ml-5">系统具备</span>
            <el-form-item >
              <el-input autocomplete="off" placeholder="系统能力"></el-input>
            </el-form-item>
            <span class="ml-5">以实现</span>
            <el-form-item class="ml-5">
              <el-input autocomplete="off" placeholder="设备"></el-input>
            </el-form-item>
            <el-form-item class="ml-5">
              <el-input autocomplete="off" placeholder="交互动词"></el-input>
            </el-form-item>
            <el-form-item class="ml-5">
              <el-input autocomplete="off" placeholder="交互信息"></el-input>
            </el-form-item>
            <span class="ml-5">的能力</span>
            <el-button type="primary" class="ml-5">确 定</el-button>
          </el-form>

          <p class="ml-5">句式2：事件驱动型系统能力</p>
          <div>
            <el-form :inline="true" label-width="80px" size="small">
              <span class="ml-5">当</span>
              <el-form-item >
                <el-input autocomplete="off" placeholder="触发事件"></el-input>
              </el-form-item>
              <span class="ml-5">时，</span>
              <el-form-item class="ml-5">
                <el-input autocomplete="off" placeholder="普适性系统能力"></el-input>
              </el-form-item>
              <el-button type="primary" class="ml-5">确 定</el-button>
            </el-form>
          </div>


          <p class="ml-5">句式3：状态驱动型系统能力</p>
          <div>
            <el-form :inline="true" label-width="80px" size="small">
              <span class="ml-5">当</span>
              <el-form-item >
                <el-input autocomplete="off" placeholder="环境实体|系统"></el-input>
              </el-form-item>
              <span class="ml-5">处于</span>
              <el-form-item class="ml-5">
                <el-input autocomplete="off" placeholder="状态"></el-input>
              </el-form-item>
              <span class="ml-5">时，</span>
              <el-form-item class="ml-5">
                <el-input autocomplete="off" placeholder="普适性系统能力"></el-input>
              </el-form-item>
              <el-button type="primary" class="ml-5">确 定</el-button>
            </el-form>
          </div>

          <div slot="footer" class="dialog-footer">
            <el-button>取 消</el-button>
            <el-button type="primary">确 定</el-button>
          </div>
        </el-dialog>

        <div v-show="downShow">

          <div>

            句式1：普适型系统能力
            <div>
              <el-form :inline="true" label-width="80px" size="small">

                <span class="ml-5">系统具备</span>
                <el-form-item >
                  <el-input autocomplete="off" placeholder="系统能力"></el-input>
                </el-form-item>
                <span class="ml-5">以实现</span>
                <el-form-item class="ml-5">
                  <el-input autocomplete="off" placeholder="设备"></el-input>
                </el-form-item>
                <el-form-item class="ml-5">
                  <el-input autocomplete="off" placeholder="交互动词"></el-input>
                </el-form-item>
                <el-form-item class="ml-5">
                  <el-input autocomplete="off" placeholder="交互信息"></el-input>
                </el-form-item>
                <span class="ml-5">的能力</span>
                <el-button type="primary" class="ml-5">确 定</el-button>
              </el-form>
            </div>

            句式2：事件驱动型系统能力
            <div>
              <el-form :inline="true" label-width="80px" size="small">
                <span class="ml-5">当</span>
                <el-form-item >
                  <el-input autocomplete="off" placeholder="触发事件"></el-input>
                </el-form-item>
                <span class="ml-5">时，</span>
                <el-form-item class="ml-5">
                  <el-input autocomplete="off" placeholder="普适性系统能力"></el-input>
                </el-form-item>
                <el-button type="primary" class="ml-5">确 定</el-button>
              </el-form>
            </div>

            句式3：状态驱动型系统能力
            <div>
              <el-form :inline="true" label-width="80px" size="small">
                <span class="ml-5">当</span>
                <el-form-item >
                  <el-input autocomplete="off" placeholder="环境实体|系统"></el-input>
                </el-form-item>
                <span class="ml-5">处于</span>
                <el-form-item class="ml-5">
                  <el-input autocomplete="off" placeholder="状态"></el-input>
                </el-form-item>
                <span class="ml-5">时，</span>
                <el-form-item class="ml-5">
                  <el-input autocomplete="off" placeholder="普适性系统能力"></el-input>
                </el-form-item>
                <el-button type="primary" class="ml-5">确 定</el-button>
              </el-form>
            </div>

            句式4：特有特征型系统能力
            <div>
              <el-form :inline="true" label-width="80px" size="small">
                <span class="ml-5">当</span>
                <el-form-item >
                  <el-input autocomplete="off" placeholder="环境实体|系统"></el-input>
                </el-form-item>
                <span class="ml-5">具有</span>
                <el-form-item class="ml-5">
                  <el-input autocomplete="off" placeholder="特定特征"></el-input>
                </el-form-item>
                <span class="ml-5">时，</span>
                <el-form-item class="ml-5">
                  <el-input autocomplete="off" placeholder="普适性系统能力"></el-input>
                </el-form-item>
                <el-button type="primary" class="ml-5">确 定</el-button>
              </el-form>
            </div>

            句式5：不期待发生型系统能力
            <div>
              <el-form :inline="true" label-width="80px" size="small">
                <span class="ml-5">一旦</span>
                <el-form-item >
                  <el-input autocomplete="off" placeholder="特定状态|触发事件"></el-input>
                </el-form-item>
                <span class="ml-5">，那么</span>
                <el-form-item class="ml-5">
                  <el-input autocomplete="off" placeholder="普适性系统能力"></el-input>
                </el-form-item>
                <el-button type="primary" class="ml-5">确 定</el-button>
              </el-form>
            </div>
          </div>

        </div>

        <el-table :data="tableData1" width="280px" border stripe :header-cell-class-name="headerBg" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column prop="id" label="软件需求列表" width="100"></el-table-column>
          <el-table-column prop="description" label="详细描述" width="800"></el-table-column>
          <el-table-column prop="flag" label="是否选择" width="100"></el-table-column>
          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-button type="success" @click="handleSelect(scope.row.id)">选择<i class="el-icon-check"></i></el-button>
              <el-button type="info" @click="handleCancelSelect(scope.row.id)">取消选择<i class="el-icon-delete"></i></el-button>
            </template>
          </el-table-column>
        </el-table>


      </el-main>
    </el-container>
  </el-container>

</template>

<script>
import Aside from "@/components/Aside.vue";
import Header from "@/components/Header.vue";
import VueOfficeDocx from "@vue-office/docx";

export default {
  name: "FuncRecommendation",
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
      downShow:false,
      dialogFormVisible: false,
      tableData1: [],
      headerBg:'headerBg',
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
    load()
    {
      this.request.get("/softwareReqRecommendation", ).then(res => {
        this.tableData1 = res
      })
    },
    GenerateReq(){
      this.request.get("/softwareReqRecommendation/generate/gpt")
    },

    AddReq(){
      this.dialogFormVisible = true
    },
    handleSelectionChange(val)
    {
      console.log(val)
      this.multipleSelection = val
    },

    handleSelect(id)
    {
      this.request.post("/softwareReqRecommendation/select/" + id).then(res => {
        if (res) {
          this.$message.success("选择成功")
          this.load()
        } else {
          this.$message.success("选择失败")
        }
      })
    },
    handleCancelSelect(id){
      this.request.post("/softwareReqRecommendation/cancelSelect/" + id).then(res => {
        if (res) {
          this.$message.success("取消成功")
          this.load()
        } else {
          this.$message.success("取消失败")
        }
      })
    },



  }

}

</script>



<style scoped>

</style>