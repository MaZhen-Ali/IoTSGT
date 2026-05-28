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
          <!-- 左侧需求文档编辑 -->
          <el-col :span="12">
            <el-card shadow="hover">
              <div slot="header" class="clearfix">
                <span>需求文档编辑</span>
              </div>

              <div
                  v-for="(paragraph, index) in paragraphs"
                  :key="'left-' + index"
                  class="edit-block"
                  :class="{ 'highlight-block': activeIndex === index }"
              >
                <el-input
                    type="textarea"
                    :rows="4"
                    v-model="paragraph.content"
                    placeholder="请输入需求段落"
                />
                <div class="paragraph-info">段落 {{ index + 1 }}</div>
              </div>

              <el-button type="primary" @click="evaluateDocument" style="margin-top: 10px;">提交评估</el-button>
            </el-card>
          </el-col>

          <!-- 右侧AI建议 -->
          <el-col :span="12">
            <el-card shadow="hover">
              <div slot="header">
                <span>AI 评估建议</span>
              </div>

              <div v-if="suggestions.length">
                <div
                    v-for="(suggestion, index) in suggestions"
                    :key="'right-' + index"
                    class="suggestion-item"
                    @mouseenter="activeIndex = suggestion.relIndex"
                    @mouseleave="activeIndex = -1"
                >
                  <el-card>
                    <div slot="header" class="suggestion-title">
                      <i class="el-icon-message"></i>
                      建议 {{ index + 1 }}
                    </div>
                    <div class="suggestion-content">{{ suggestion.content }}</div>
                  </el-card>
                </div>
              </div>
              <el-empty v-else description="请先提交评估文档" />
            </el-card>
          </el-col>
        </el-row>



      </el-main>
    </el-container>
  </el-container>

</template>

<script>
import Aside from "@/components/Aside.vue";
import Header from "@/components/Header.vue";

export default {
  name: "EvaluationPage",
  components: {
    Header,
    Aside,
  },
  data() {
    return {
      collapseBtnClass: 'el-icon-s-fold',
      isCollapse: false,
      sideWidth: 200,
      logoTextShow: true,

      activeIndex: -1, // 当前激活的段落索引
      paragraphs: [
        { content: "项目背景：本系统用于实现学生信息管理。" },
        { content: "功能需求：用户可以登录、查询和修改信息。" },
        { content: "非功能需求：系统响应时间不超过1秒。" },
      ],

      suggestions: [],
      // suggestions: [
      //   {
      //     content: '建议补充市场分析数据',
      //     relIndex: 1
      //   },
      //   {
      //     content: '建议细化用户权限描述',
      //     relIndex: 1
      //   },
      //   {
      //     content: '建议增加技术架构图',
      //     relIndex: 1
      //   }
      // ]


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

    evaluateDocument() {
      this.suggestions = this.paragraphs.map((p, i) => ({
        content: `针对段落 ${i + 1} 的建议：请添加更详细的说明。`,
        relIndex: i,
      }));
    },


  }
}

</script>



<style scoped>



.edit-block {
  margin-bottom: 20px;
  padding: 0;
  position: relative;
  transition: all 0.3s;
}

.paragraph-info {
  position: absolute;
  top: 10px;
  right: 10px;
  font-size: 12px;
  color: #999;
}

.suggestion-item {
  margin-bottom: 15px;
  cursor: pointer;
}

.suggestion-title {
  font-weight: bold;
  color: #409EFF;
}

.suggestion-content {
  line-height: 1.6;
  color: #333;
}

/* 取消 edit-block 的背景 */
.highlight-block {
  background-color: transparent;
}

/* 高亮 el-input 本身 */
.highlight-block /deep/ .el-textarea__inner {
  border-color: #409EFF !important;
  box-shadow: 0 0 6px rgba(64, 158, 255, 0.5);
}



</style>