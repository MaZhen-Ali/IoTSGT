<template>
  <el-menu :default-openeds="['1', '2']" style="min-height: 100%; overflow-x: hidden"
           :collapse-transition="false"
           :default-active="$route.path"
           :collapse="isCollapse"
           router
  >
    <div style="height: 60px;line-height: 60px;text-align: center;">
      <img src="../assets/logo.png" alt="" style="width: 20px;position: relative;top: 5px;margin-right: 5px;">
      <b style="color: black;" v-show="logoTextShow">需求文档生成</b>
    </div>

    <el-submenu index="1">
      <template slot="title">
        <i class="el-icon-s-operation"></i>
        <span>生成流程</span>
      </template>
      <el-menu-item index="/TaskIntent">填写任务意图</el-menu-item>
      <el-menu-item index="/NoLibDeviceSelection">选择物理设备</el-menu-item>
      <el-menu-item index="/SystemRequirement">推导系统需求</el-menu-item>
      <el-menu-item index="/NoLibSoftwareReq">补全软件需求</el-menu-item>
      <el-menu-item index="/GenerateDocu">生成需求文档</el-menu-item>
    </el-submenu>


    <el-submenu index="2">
      <template slot="title">
        <i class="el-icon-document"></i>
        <span>知识管理</span>
      </template>
      <el-menu-item index="/DeviceKnowledge">设备知识</el-menu-item>
      <el-menu-item index="/ModelKnowledge">模型知识</el-menu-item>
      <el-menu-item index="/TemplateKnowledge">模板管理</el-menu-item>
    </el-submenu>

    <el-submenu index="3">
      <template slot="title">
        <i class="el-icon-document"></i>
        <span>文档评估</span>
      </template>
      <el-menu-item index="/DocumentEvaluation">文档评估1</el-menu-item>
      <el-menu-item index="/EvaluationPage2">文档评估2</el-menu-item>
    </el-submenu>






<!--    <el-menu-item index="/">-->
<!--      <template slot="title">-->
<!--        <i class="el-icon-house"></i>-->
<!--        <span slot="title">主页</span>-->
<!--      </template>-->
<!--    </el-menu-item>-->


<!--    <el-submenu index="0">-->
<!--      <template slot="title">-->
<!--        <i class="el-icon-menu"></i>-->
<!--        <span slot="title">系统管理</span>-->
<!--      </template>-->

<!--      <el-menu-item index="/user">-->
<!--        <i class="el-icon-s-custom"></i>-->
<!--        <span slot="title">用户管理</span>-->
<!--      </el-menu-item>-->
<!--    </el-submenu>-->


<!--    <el-menu-item index="/TaskIntent">-->
<!--      <template slot="title">-->
<!--        <i class="el-icon-s-flag"></i>-->
<!--        <span slot="title">任务意图</span>-->
<!--      </template>-->
<!--    </el-menu-item>-->

<!--    <el-submenu index="2">-->
<!--      <template slot="title">-->
<!--        <i class="el-icon-menu"></i>-->
<!--        <span slot="title">设备选择</span>-->
<!--      </template>-->

<!--      <el-menu-item index="/DeviceSelection">-->
<!--        <i class="el-icon-house"></i>-->
<!--        <span slot="title">有设备库</span>-->
<!--      </el-menu-item>-->

<!--      <el-menu-item index="/NoLibDeviceSelection">-->
<!--        <i class="el-icon-house"></i>-->
<!--        <span slot="title">无设备库</span>-->
<!--      </el-menu-item>-->
<!--    </el-submenu>-->


<!--    <el-submenu index="4">-->
<!--      <template slot="title">-->
<!--        <i class="el-icon-menu"></i>-->
<!--        <span slot="title">共享现象</span>-->
<!--      </template>-->

<!--      <el-menu-item index="/SharedPhenomenon">-->
<!--        <i class="el-icon-house"></i>-->
<!--        <span slot="title">有设备库</span>-->
<!--      </el-menu-item>-->

<!--      <el-menu-item index="/NoLibSharedPhenomenon">-->
<!--        <i class="el-icon-house"></i>-->
<!--        <span slot="title">无设备库</span>-->
<!--      </el-menu-item>-->
<!--    </el-submenu>-->


<!--    <el-menu-item index="/SystemRequirement">-->
<!--      <template slot="title">-->
<!--        <i class="el-icon-s-flag"></i>-->
<!--        <span slot="title">系统需求</span>-->
<!--      </template>-->
<!--    </el-menu-item>-->




<!--    <el-menu-item index="/ContextDiagram">-->
<!--      <template slot="title">-->
<!--        <i class="el-icon-house"></i>-->
<!--        <span slot="title">上下文图</span>-->
<!--      </template>-->
<!--    </el-menu-item>-->

<!--    <el-menu-item index="/ProblemDiagram">-->
<!--      <template slot="title">-->
<!--        <i class="el-icon-house"></i>-->
<!--        <span slot="title">问题图</span>-->
<!--      </template>-->
<!--    </el-menu-item>-->


<!--    <el-submenu index="6">-->
<!--      <template slot="title">-->
<!--        <i class="el-icon-menu"></i>-->
<!--        <span slot="title">功能推荐</span>-->
<!--      </template>-->

<!--      <el-menu-item index="/FuncRecommendation">-->
<!--        <i class="el-icon-house"></i>-->
<!--        <span slot="title">有设备库</span>-->
<!--      </el-menu-item>-->

<!--      <el-menu-item index="/NoLibSoftwareReq">-->
<!--        <i class="el-icon-house"></i>-->
<!--        <span slot="title">无设备库</span>-->
<!--      </el-menu-item>-->
<!--    </el-submenu>-->

<!--    <el-menu-item index="/GenerateDocu">-->
<!--      <template slot="title">-->
<!--        <i class="el-icon-document"></i>-->
<!--        <span slot="title">文档生成</span>-->
<!--      </template>-->
<!--    </el-menu-item>-->


<!--    <el-menu-item index="/DocumentEvaluation">-->
<!--      <template slot="title">-->
<!--        <i class="el-icon-document"></i>-->
<!--        <span slot="title">文档评估</span>-->
<!--      </template>-->
<!--    </el-menu-item>-->

<!--    <el-menu-item index="/EvaluationPage2">-->
<!--      <template slot="title">-->
<!--        <i class="el-icon-document"></i>-->
<!--        <span slot="title">文档评估2</span>-->
<!--      </template>-->
<!--    </el-menu-item>-->





  </el-menu>

</template>

<script>
export default {
  name: "Aside",
  props: {
    isCollapse: Boolean,
    logoTextShow: Boolean,
  }

}
</script>



<style scoped>

.el-menu {
  height: 100%;
  border-right: none;
}

</style>