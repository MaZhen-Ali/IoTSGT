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

        <el-button type="primary" @click="generateDocu" class="ml-5">生成需求文档</el-button>
        <el-button type="primary" @click="generateDocuNew" class="ml-5">生成需求文档(新)</el-button>
        <el-button @click="downloadDoc">下载文档</el-button>

        <div v-show="downShow">
          <vue-office-docx
              ref="officeDoc"
              :src="url"
              @rendered="renderingCompleted"
          />


        </div>


      </el-main>
    </el-container>
  </el-container>

</template>

<script>
import VueOfficeDocx from "@vue-office/docx";
import Aside from "@/components/Aside.vue";
import Header from "@/components/Header.vue";

export default {
  name: "GenerateDocu",
  components: {
    Header,
    Aside,
    VueOfficeDocx
  },
  data(){
    return{
      collapseBtnClass:'el-icon-s-fold',
      isCollapse:false,
      sideWidth:200,
      logoTextShow:true,
      downShow:false,
      url: "http://localhost:9090/file/src/main/resources/doc/requireDocument.docx",
      docContent: null,
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
    generateDocu(){
      this.request.get("/documentGeneration").then(res => {
        if (res) {
          this.$message.success("生成成功")
        } else {
          this.$message.success("生成失败")
        }
      })
      this.downShow = true;
      this.downShow = false; // 隐藏组件
      this.$nextTick(() => {
        this.downShow = true;  // 重新显示组件，强制刷新
      });
    },
    generateDocuNew(){
      this.request.get("/documentGeneration/new").then(res => {
        if (res) {
          this.$message.success("生成成功")
        } else {
          this.$message.success("生成失败")
        }
      })
      this.downShow = true;
      this.downShow = false; // 隐藏组件
      this.$nextTick(() => {
        this.downShow = true;  // 重新显示组件，强制刷新
      });
    },
    renderingCompleted(doc) {
      console.log('渲染完成');
      this.docContent = doc; // 假设 doc 是文档内容
    },

    downloadDoc() {

      // 创建一个隐藏的下载链接
      const link = document.createElement("a");
      link.href = this.url;
      link.download = "requireDocument.docx"; // 设置下载文件的名称
      link.target = "_blank"; // 打开新窗口进行下载（避免导航跳转）

      // 触发下载
      document.body.appendChild(link);
      link.click();

      // 移除链接
      document.body.removeChild(link);

    },
  },


}
</script>



<style scoped>

</style>