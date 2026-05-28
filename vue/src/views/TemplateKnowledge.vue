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
          <!-- 左侧导航 -->
          <el-col :span="6">
            <el-menu default-active="structure" @select="handleSelect">
              <el-menu-item index="structure">需求文档结构模板</el-menu-item>
              <el-menu-item index="intent">任务意图模板</el-menu-item>
              <el-menu-item index="sysreq">系统需求模板</el-menu-item>
            </el-menu>
          </el-col>

          <!-- 右侧内容展示 -->
          <el-col :span="18">
            <!-- 结构模板树 -->
            <el-card v-if="selected === 'structure'" shadow="hover">
              <div slot="header"><b>需求文档结构模板</b></div>
              <el-tree
                  :data="structureTree"
                  :props="treeProps"
                  accordion
                  highlight-current
                  default-expand-all
              ></el-tree>
            </el-card>

            <!-- 任务意图模板 -->
            <el-card v-if="selected === 'intent'" shadow="hover">
              <div slot="header"><b>任务意图模板</b></div>
              <el-table :data="intentTemplates" border>
                <el-table-column prop="template" label="模板示例" width="600px"></el-table-column>
                <el-table-column prop="description" label="说明"></el-table-column>
              </el-table>
            </el-card>

            <!-- 系统需求模板 -->
            <el-card v-if="selected === 'sysreq'" shadow="hover">
              <div slot="header"><b>系统需求模板</b></div>
              <el-table :data="sysReqTemplates" border>
                <el-table-column prop="id" label="句式" width="70px"></el-table-column>
                <el-table-column prop="template" label="模板示例" width="600px"></el-table-column>
                <el-table-column prop="description" label="说明"></el-table-column>
              </el-table>
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
  name: "TemplateKnowledge",
  components: {Aside, Header},
  data(){
    return{
      collapseBtnClass:'el-icon-s-fold',
      isCollapse:false,
      sideWidth:200,
      logoTextShow:true,


      selected: "structure",
      treeProps: {
        children: "children",
        label: "label"
      },
      structureTree: [
        {
          label: "1. 介绍",
          children: [
            { label: "1.1 系统意图和任务" },
            { label: "1.2 系统组成" },
            { label: "1.3 上下文图" }
          ]
        },
        {
          label: "2. 系统需求",
        },
        {
          label: "3. 软件需求",
          children: [
            { label: "3.1 问题图" },
            { label: "3.2 功能需求" },
            { label: "3.3 非功能需求" }
          ]
        }
      ],
      intentTemplates: [
        {
          template: "【系统】需要【做什么任务】以达成【什么目的】",
          description: "用于表达开发嵌入式系统要达成的目标"
        },

      ],
      sysReqTemplates: [
        {
          id : "句式一",
          template: "系统shall【交互动词】【交互信息】",
          description: "描述系统与用户或外部系统之间的信息交互。"
        },
        {
          id : "句式二",
          template: "系统shall enable【外部实体】【交互操作】from【外部实体/物理设备】by【物理设备】",
          description: "表达系统如何允许外部参与者进行某种交互操作。"
        },
        {
          id : "句式三",
          template: "系统shall存储【数据】in【设计领域】（作为数据库的设计领域）",
          description: "指明数据存放的位置或数据库设计领域的结构组织"
        },
        {
          id : "句式四",
          template: "IF【计算|判断】，THEN【句式一|句式二】",
          description: "表达系统在特定条件或判断下的响应行为，支持表达系统逻辑分支、触发条件和应对动作"
        }
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


    handleSelect(index) {
      this.selected = index;
    }


  }
}
</script>



<style scoped>
.el-menu {
  border-right: none;
}
.el-card {
  margin-bottom: 20px;
}
</style>