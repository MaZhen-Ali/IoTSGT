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


        <el-row :gutter="20">
          <el-col :span="8" v-for="model in models" :key="model.name">
            <el-card shadow="hover">
              <h3>{{ model.name }}</h3>
              <p>用途：{{ model.purpose }}</p>
              <p>阶段：{{ model.stage }}</p>
              <div style="text-align: right;">
                <el-button type="text" @click="viewModel(model)">查看</el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>



      </el-main>
    </el-container>
  </el-container>

</template>

<script>
import Header from "@/components/Header.vue";
import Aside from "@/components/Aside.vue";

export default {
  name: "ModelKnowledge",
  components: {Aside, Header},
  data(){
    return{
      collapseBtnClass:'el-icon-s-fold',
      isCollapse:false,
      sideWidth:200,
      logoTextShow:true,



      models: [
        { name: '上下文图', purpose: '系统边界建模', stage: '需求定义阶段' },
        { name: '问题图', purpose: '问题建模', stage: '系统需求阶段、软件需求阶段' }
      ]

    }
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


    viewModel(model) {
      // 跳转查看模型图页面
    }
  }
}
</script>



<style scoped>

</style>