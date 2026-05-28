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
        <div>

          <el-form :inline="true" label-width="80px" size="small">
            <el-form-item >
              <el-input v-model="form.sys" autocomplete="off" placeholder="系统"></el-input>
            </el-form-item>
            <span class="ml-5">需要</span>
            <el-form-item class="ml-5">
              <el-input v-model="form.task" autocomplete="off" placeholder="做什么任务"></el-input>
            </el-form-item>
            <span class="ml-5">以达成</span>
            <el-form-item class="ml-5">
              <el-input v-model="form.target" autocomplete="off" placeholder="目标"></el-input>
            </el-form-item>
            <span class="ml-5">的目的</span>

<!--            <br>-->
<!--            你认为这里面哪几个是外部实体？请填写一下：-->
<!--            <el-form-item>-->
<!--              <el-input autocomplete="off" placeholder="外部实体"></el-input>-->
<!--            </el-form-item>-->

            <el-button type="primary" @click="save" class="ml-5">确 定</el-button>

          </el-form>


        </div>

<!--        <div>-->
<!--          <el-button type="primary" @click="addInput">+</el-button>-->
<!--          <el-form ref="form" :model="form1" label-width="80px">-->
<!--            &lt;!&ndash; 这个的v-for是核心是重点，一定要明白！ &ndash;&gt;-->
<!--            <div v-for="(item,index) in array" :key="index">-->
<!--              <el-form-item>-->
<!--                <el-input style="width:20%;margin-right:10px" v-model="form1.value[index]" placeholder="请输入任务！"></el-input>-->
<!--                <el-button type="danger" size="small" icon="el-icon-delete" circle @click="delInput(index)"></el-button>-->
<!--              </el-form-item>-->
<!--            </div>-->
<!--          </el-form>-->
<!--        </div>-->

        <div style="display: flex; align-items: flex-start; margin-bottom: 20px;">
          <el-input
              type="textarea"
              :rows="2"
              placeholder="请输入需求(每条需求之间以句号分隔)"
              v-model="textarea"
              style="flex: 1; margin-right: 10px;"
          ></el-input>

          <el-button type="primary" size="small" icon="el-icon-check" @click="addRequirementSentence">
            确定
          </el-button>
        </div>







        <el-table :data="tableData" border stripe :header-cell-class-name="headerBg" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column prop="id" label="ID" width="80"></el-table-column>
<!--          <el-table-column prop="sys" label="系统" width="140"></el-table-column>-->
<!--          <el-table-column label="需要" width="120">需要</el-table-column>-->
<!--          <el-table-column prop="task" label="做什么任务"></el-table-column>-->
<!--          <el-table-column label="以达成">以达成</el-table-column>-->
<!--          <el-table-column prop="target" label="目标"></el-table-column>-->
<!--          <el-table-column label="的目的">的目的</el-table-column>-->

          <el-table-column prop="sentence" label="任务"></el-table-column>

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

        <el-form style="margin-top: 20px;">
          <el-form-item label="当前已确认的实体">
            <div style="display: flex; flex-wrap: wrap; gap: 8px; align-items: center;">
              <!-- 已确认的实体标签 -->
              <el-tag
                  v-for="tag in tags"
                  :key="tag.entity"
                  closable
                  :disable-transitions="false"
                  @close="handleClose(tag)"
              >
                {{ tag.entity }}
              </el-tag>

              <!-- 输入新标签 -->
              <el-input
                  v-if="inputVisible"
                  class="input-new-tag"
                  v-model="inputValue"
                  ref="saveTagInput"
                  size="small"
                  style="width: 120px"
                  @keyup.enter.native="handleInputConfirm"
                  @blur="handleInputConfirm"
              ></el-input>

              <!-- 新增按钮 -->
              <el-button
                  v-else
                  class="button-new-tag"
                  size="small"
                  type="primary"
                  plain
                  @click="showInput"
              >
                + 实体
              </el-button>
            </div>
          </el-form-item>
        </el-form>





        <el-button type="primary" @click="identifyEntity" class="ml-5">识别实体</el-button>

<!--        <el-input v-model="entityName" autocomplete="off" placeholder="请输入实体名称"></el-input>-->
<!--        <el-button type="primary" @click="addEntity" class="ml-5">添加实体</el-button>-->



        <div v-show="downShow">
          <div  style="display: flex">
            <el-table :data="tableDataGeneration" :header-cell-class-name="headerBg" style="width: 50px">

              <el-table-column prop="name" label="实体名称" width="140"></el-table-column>

              <el-table-column width="220" label="操作">
                <template slot-scope="scope">
                  <el-button type="success" @click="handleSelect(scope.row.name)">选择<i class="el-icon-check"></i></el-button>
                </template>
              </el-table-column>

            </el-table>
          </div>

        </div>


<!--        <div  style="display: flex">-->
<!--          <el-table :data="entityList" :header-cell-class-name="headerBg" style="width: 50px">-->
<!--            <el-table-column prop="id" width="140"></el-table-column>-->
<!--            <el-table-column prop="name" label="实体名称" width="140"></el-table-column>-->

<!--            <el-table-column width="220" label="操作">-->
<!--              <template slot-scope="scope">-->
<!--                <el-button type="success" @click="handleSelect(scope.row.id)">选择<i class="el-icon-check"></i></el-button>-->
<!--                <el-button type="info" @click="handleCancelSelect(scope.row.id)">取消选择<i class="el-icon-delete"></i></el-button>-->
<!--              </template>-->
<!--            </el-table-column>-->
<!--          </el-table>-->
<!--        </div>-->


      </el-main>

      <el-dialog title="任务意图" :visible.sync="dialogFormVisible" width="30%">
        <el-form label-width="80px" size="small">
          <el-form-item label="系统">
            <el-input v-model="form.sys" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="需要">
          </el-form-item>
          <el-form-item label="任务">
            <el-input v-model="form.task" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="以达成">
          </el-form-item>
          <el-form-item label="目标">
            <el-input v-model="form.target" autocomplete="off"></el-input>
          </el-form-item>
        </el-form>

        <div slot="footer" class="dialog-footer">
          <el-button @click="cancel">取 消</el-button>
          <el-button type="primary" @click="save">确 定</el-button>
        </div>
      </el-dialog>




    </el-container>




  </el-container>



</template>

<script>

import Aside from "@/components/Aside";
import Header from "@/components/Header";
export default {
  name: "Taskintent",
  data(){
    return {

      tableData: [],
      collapseBtnClass:'el-icon-s-fold',
      isCollapse:false,
      sideWidth:200,
      logoTextShow:true,
      headerBg:'headerBg',
      form: {},
      sys:'',
      task:'',
      target:'',
      dialogFormVisible: false,
      downShow: false,
      tableDataGeneration: [],

      entityList: [],
      tags: [],

      entityName:'',
      array:[1],  //创建一个数组
      form1:{
        value:[]  //接收每个input框的值
      },

      textarea:'',

      inputVisible: false,
      inputValue: '',

    }

  },
  components: {
    Aside,
    Header
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
    load()
    {
      this.request.get("/taskIntent", ).then(res => {
        console.log(res)
        console.log("_____________")
        // console.log(res[1].sys)

        //解析res中的数据
        this.tableData = res
        console.log("_____________")
        console.log(this.tableData)
        //控制台打印数据类型  console.log(typeof())
        console.log(typeof (this.tableData))
        // console.log(typeof (this.tableData[1]))
        // console.log(typeof (this.tableData[1].task))
      })

      console.log("isCollapse的类型"+typeof this.isCollapse,'value:', this.isCollapse);


      //已确定实体列表
      this.request.get("/entityList/findAllEntity").then(res => {
        this.tags = res
      })



    }
    ,

    save() {

      this.request.post("/taskIntent", this.form).then(res => {
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


    handleAdd(){
      this.save();

    },
    handleEdit(row)
    {
      this.form = row
      this.dialogFormVisible = true
    },
    del(id)
    {
      this.request.delete("/taskIntent/" + id).then(res => {
        if (res) {
          this.$message.success("删除成功")
          this.load()
        } else {
          this.$message.success("删除失败")
        }
      })
    }
    ,

    handleSelectionChange(val)
    {
      console.log(val)
      this.multipleSelection = val
    }
    ,
    identifyEntity(){
      this.downShow = true
      this.request.get("/taskIntent/NoLibEntity/gpt", ).then(res => {
        this.tableDataGeneration = res

        res.forEach(item => {
          console.log(item.name); // 打印每个对象的name属性
        });
      })

    },
    // 添加按钮
    addInput(){
      this.array.push(1)  //通过添加array的值，增加input的个数
    },
    delInput(index){
      this.form1.value.splice(index,1)   //先删除form中value对应索引的值
      this.array.splice(index,1)  //然后删除array对应索引的值，实现点击删除按钮，减少input框效果
    },

    addRequirementSentence(){

      this.request.post("/taskIntent/sentences", this.textarea).then(res => {
            if (res) {
              this.$message.success("保存成功")
              this.load()
            } else {
              this.$message.success("保存失败")
            }
          }
      )

    },





    handleSelect(row)
    {
      console.log(row)
      this.request.post("/entityList/addEntity",row).then(res => {
        if (res) {
          this.$message.success("选择成功")

        } else {
          this.$message.success("选择失败")
        }
      })
    },

    addEntity(){
      this.request.post("/entityList/addEntity",this.entityName).then(res => {
        if (res) {
          this.$message.success("选择成功")
        } else {
          this.$message.success("选择失败")
        }
      })

    },

    handleClose(tag) {
      console.log(tag.entity)
      this.request.delete("/entityList/delEntity",{
        data: { entityName: tag.entity }  // { entityName: "空气" }
      }).then(res => {
        if (res) {
          this.$message.success("删除成功")
        } else {
          this.$message.success("删除失败")
        }
      })
      this.tags.splice(this.tags.indexOf(tag), 1)

    },

    showInput() {
      this.inputVisible = true;
      this.$nextTick(_ => {
        this.$refs.saveTagInput.$refs.input.focus();
      });
    },

    handleInputConfirm() {

      this.request.post("/entityList/addEntity",this.inputValue).then(res => {
        if (res) {
          this.$message.success("添加成功")
        } else {
          this.$message.success("添加失败")
        }
      })

      this.inputVisible = false;
      this.inputValue = '';




    }






















  }

}



</script>



<style scoped>

.headerBg{
  background: #eee !important;
}

.el-tag + .el-tag {
  margin-left: 10px;
}
.button-new-tag {
  margin-left: 10px;
  height: 32px;
  line-height: 30px;
  padding-top: 0;
  padding-bottom: 0;
}
.input-new-tag {
  width: 90px;
  margin-left: 10px;
  vertical-align: bottom;
}
</style>