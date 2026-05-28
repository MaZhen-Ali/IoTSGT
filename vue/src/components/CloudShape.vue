<template>
  <div>
    <div ref="paperContainer" style="width: 500px; height: 400px; border: 1px solid #ccc;"></div>
  </div>
</template>

<script>
import * as joint from 'jointjs';
import 'jointjs/dist/joint.css'; // 确保引入 JointJS 的样式

export default {
  name: 'CloudShape',
  props: {
    width: {
      type: Number,
      default: 200
    },
    height: {
      type: Number,
      default: 100
    },
    text: {
      type: String,
      default: 'Cloud Text'
    },
    x: {
      type: Number,
      default: 300
    },
    y: {
      type: Number,
      default: 60
    },
    cloudPositionX: {
      type: Number,
      default: 50
    },
    cloudPositionY: {
      type: Number,
      default: 50
    }
  },
  mounted() {
    this.initializeGraph();
  },
  methods: {
    initializeGraph() {
      // 创建一个简单的云朵形状
      const SimpleCloudShape = joint.dia.Element.extend({
        markup: [
          {
            tagName: 'g',
            className: 'cloud-shape',
            children: [
              {
                tagName: 'path',
                className: 'cloud-body'
              },
              {
                tagName: 'text',
                className: 'cloud-label'
              }
            ]
          }
        ],
        defaults: joint.util.deepSupplement({
          type: 'custom.SimpleCloudShape',
          size: { width: this.width, height: this.height },
          attrs: {
            '.cloud-body': {
              d: 'M 650 300 L 300 300 Q 300 250 350 250 Q 350 200 450 200 A 50 50 0 1 1 600 200 Q 650 200 650 300',
              fill: '#dbe7ff',
              stroke: '#8aacc8',
              strokeWidth: 2
            },
            '.cloud-label': {
              text: this.text,
              fill: 'black',
              fontSize: 14,
              x: this.x, // 绝对 x 坐标
              y: this.y, // 绝对 y 坐标
              textAnchor: 'middle', // 水平居中
              alignmentBaseline: 'middle' // 垂直居中
            }
          }
        }, joint.dia.Element.prototype.defaults)
      });

      // 创建画布
      const graph = new joint.dia.Graph();

      // 创建一个简单的云朵形状的元素
      const cloud = new SimpleCloudShape();
      cloud.position(this.cloudPositionX, this.cloudPositionY); // 设置云朵的初始位置

      // 创建纸张
      const paper = new joint.dia.Paper({
        el: this.$refs.paperContainer,
        width: 800, // 设置宽度为 800px
        height: 600, // 设置高度为 600px
        model: graph,
        gridSize: 1
      });

      console.log('Paper created:', paper);

      // 将云朵形状的元素添加到画布中
      graph.addCell(cloud);
    }
  }
};
</script>

<style scoped>
.cloud-body {
  fill: #dbe7ff;
  stroke: #8aacc8;
  stroke-width: 2;
}

.cloud-label {
  fill: black;
  font-size: 14px;
  text-anchor: middle;
  alignment-baseline: middle;
}
</style>
