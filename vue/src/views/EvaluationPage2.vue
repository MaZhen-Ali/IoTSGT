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

          <el-col :span="12">
            <el-card shadow="hover" style="height: 650px; overflow: auto;">
              <div slot="header">
                <span>需求文档编辑区</span>
              </div>
              <div>
              <editor
                  api-key="iefjau2jzlu2aozjwzbd2n8ln6txi63gx6g3k509q5krzq59"
                  v-model="editorContent"
                  :init="editorInit"
                  ref="tinymceEditor"
              ></editor>

<!--              <input type="file" accept=".docx" @change="handleFileUpload" />-->

              <!-- 隐藏的 input -->
              <input
                  ref="fileInput"
                  type="file"
                  accept=".docx"
                  @change="handleFileUpload"
                  style="display: none"
              />

              <!-- 用 el-button 触发 input 点击 -->
              <el-button type="primary" @click="triggerFileInput">上传 Word 需求文档</el-button>
              <el-button type="primary" @click="evaluateDocument" style="margin-top: 10px;">提交评估</el-button>
              <el-button type="primary" @click="downloadAsDocx">下载为 Word 文档</el-button>

            </div>
            </el-card>
          </el-col>

          <el-col :span="12">
            <el-card shadow="hover" style="height: 650px; overflow: auto;">
              <div slot="header">
                <span>AI 检测建议</span>
              </div>

              <div class="suggestion-scroll">
                <div v-if="suggestions.length">
                  <div
                      v-for="(suggestion, index) in suggestions"
                      :key="'right-' + index"
                      class="suggestion-item"
                      @mouseenter="handleMouseEnter(suggestion)"
                      @mouseleave="handleMouseLeave"
                  >
                    <el-card>
                      <div slot="header" class="suggestion-title">
                        <i class="el-icon-message"></i>
                        建议 {{ index + 1 }}
                      </div>
                      <div class="suggestion-content"><i class="el-icon-warning" style="color: red;" /><strong>检测到的问题：</strong>{{ suggestion.reason }}</div>
                      <div class="suggestion-content"><i class="el-icon-edit-outline" style="color: green;" /><strong>修改建议：</strong>{{ suggestion.advice }}</div>
                    </el-card>
                  </div>
                </div>
                <el-empty v-else description="请先提交评估文档" />
              </div>


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

import Editor from '@tinymce/tinymce-vue';
import * as mammoth from 'mammoth';
import htmlDocx from 'html-docx-js/dist/html-docx';




export default {
  name: "EvaluationPage2",
  components: {
    Header,
    Aside,

    Editor,


  },
  data() {
    return {
      collapseBtnClass: 'el-icon-s-fold',
      isCollapse: false,
      sideWidth: 200,
      logoTextShow: true,

      editorContent: '',
      editorInit: {
        height: 500,
        menubar: true,
        plugins: 'lists link image code',
        toolbar: 'undo redo | formatselect | bold italic | alignleft aligncenter alignright | code',
      },

      activeContent: '', // 当前激活的段落索引
      paragraphs: [
        { content: "项目背景：本系统用于实现学生信息管理。" },
        { content: "功能需求：用户可以登录、查询和修改信息。" },
        { content: "非功能需求：系统响应时间不超过1秒。" },
      ],
      suggestions: [],








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

    handleFileUpload(event) {
      const file = event.target.files[0];
      if (file && file.name.endsWith('.docx')) {
        const reader = new FileReader();
        reader.onload = async (e) => {
          const arrayBuffer = e.target.result;
          const result = await mammoth.convertToHtml({ arrayBuffer });
          this.editorContent = result.value; // 插入到 TinyMCE 编辑器
        };
        reader.readAsArrayBuffer(file);
      } else {
        this.$message.error('请上传 .docx 格式的文件');
      }
    },

    handleMouseEnter(suggestion) {

      this.activeContent = suggestion.originalRequirement; // 如果你还想让它高亮显示

      console.log(suggestion.originalRequirement);



      //高亮显示！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！

      // 创建正则表达式匹配原始内容（防止多次高亮先清除旧的高亮）
      const raw = this.editorContent.replace(/<mark class="highlight">([\s\S]*?)<\/mark>/g, '$1');

      // 用于精确匹配 suggestion.originalRequirement 的内容（转义特殊字符）
      const escapedContent = suggestion.originalRequirement.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
      const regex = new RegExp(escapedContent, 'g');

      // 替换为高亮内容
      const highlighted = raw.replace(regex, `<mark class="highlight">${suggestion.originalRequirement}</mark>`);

      this.editorContent = highlighted;



      // 滚动到高亮区域！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
      this.$nextTick(() => {
        const iframe = this.$refs.tinymceEditor.editor.iframeElement;
        const doc = iframe?.contentDocument;
        const highlightEl = doc?.querySelector('.highlight');

        if (highlightEl) {
          highlightEl.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
      });
    },

    handleMouseLeave() {
      this.editorContent = this.editorContent.replace(/<mark class="highlight">([\s\S]*?)<\/mark>/g, '$1');
      this.activeContent = '';
    },


    evaluateDocument() {
      // suggestions的格式
      // this.suggestions = [
      //   {
      //     originalRequirement: "原始需求语句",
      //     reason: "原因",
      //     advice: "修复建议",
      //   },
      //   {
      //     originalRequirement: "原始需求语句",
      //     reason: "原因",
      //     advice: "修复建议",
      //   },
      // ]

      this.request.post('/evaluate/api', {
        content: this.editorContent
      }).then(res => {
        this.$message.success('提交成功！');
        this.suggestions = res
        console.log(res)
      }).catch(err => {
        console.error(err);
        this.$message.error('提交失败');
      });




    },

    downloadAsDocx() {
      const content = `<html><head><meta charset="utf-8"></head><body>${this.editorContent}</body></html>`;
      const blob = htmlDocx.asBlob(content);
      const link = document.createElement("a");
      link.href = URL.createObjectURL(blob);
      link.download = "需求规约.docx";
      link.click();
    },


    triggerFileInput() {
      this.$refs.fileInput.click();
    }











  },

}

</script>



<style scoped>


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

.highlight {
  background-color: yellow;
}

/*添加滚动条样式*/
.suggestion-scroll {
  max-height: 500px;
  overflow-y: auto;
  padding-right: 5px; /* 避免滚动条遮住内容 */
}



</style>