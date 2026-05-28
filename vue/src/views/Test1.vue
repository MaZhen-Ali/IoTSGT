<template>
  <div>

    你好
    <el-button @click="saveAsImage">保存为图像</el-button>


    <div>
      <div id="paper" style="width: 800px; height: 600px; border: 1px solid #ccc;"></div>

      <div ref="canvas" style="width: 600px; height: 400px; border: 1px solid #ccc;"></div>

    </div>



  </div>


</template>


<script>
import * as joint from 'jointjs';
import html2canvas from 'html2canvas';


export default {
  name: "Test1",
  data () {
    return {
      paper: null,
      cloudText: '祝融的代码', // 默认文本
      graph: null,
      cloudShape: null,
      textStyle: {
        position: 'absolute',
        left: '100px',
        top: '100px',
        color: 'black',
        fontSize: '14px',
        textAlign: 'center',
        width: '200px',
        height: '100px',
        lineHeight: '100px', // 垂直居中
        overflow: 'hidden',
        whiteSpace: 'nowrap'
      },
    }
  },

  mounted() {
    this.initializeGraph();
  },

  methods: {
    initializeGraph() {
      // 创建一个自定义云朵形状的图形
      const CloudShape = joint.dia.Element.extend({
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
          type: 'custom.CloudShape',
          size: { width: 100, height: 50 },
          attrs: {
            '.cloud-body': {
              d: 'M 650 300 L 300 300 Q 300 250 350 250 Q 350 200 450 200 A 50 50 0 1 1 600 200 Q 650 200 650 300',
              //机器领域
              // M 100 200 L 650 200 L 650 350 L 100 350 L 100 200 L 150 200 L 150 350 L 200 350 L 200 200
              //云朵形状
              // M 650 300 L 300 300 Q 300 250 350 250 Q 350 200 450 200 A 50 50 0 1 1 600 200 Q 650 200 650 300
              fill: '#dbe7ff',
              stroke: '#8aacc8',
              strokeWidth: 2
            },
            '.cloud-label': {
              text: this.cloudText,
              fill: 'black',
              fontSize: 14,
              textAnchor: 'middle',
              x: 500,
              y: 250
              // x: { field: 'size', offset: 100 },
              // y: { field: 'size', offset: 55 }
            }
          }
        }, joint.dia.Element.prototype.defaults)
      });

      // 创建画布
      this.graph = new joint.dia.Graph();

      // 创建一个云朵形状的元素
      const cloud = new CloudShape();
      cloud.position(50, 50);

      // 创建纸张
      this.paper = new joint.dia.Paper({
        el: document.getElementById('paper'),
        width: 800,
        height: 600,
        model: this.graph,
        gridSize: 1
      });

      cloud.addTo(this.graph);

      // 将云朵形状的元素添加到画布中
      this.graph.addCell(cloud);

      const rect = new joint.shapes.standard.Rectangle();
      rect.position(100, 100);
      rect.resize(100, 40);
      rect.attr({
        body: { fill: 'lightblue' },
        label: { text: '矩形', fill: 'black' },
      });
      rect.addTo(this.graph);


    },

    saveAsImage(){

      const paperElement = document.getElementById('paper'); // 替换为实际 ID

      html2canvas(paperElement).then(canvas => {
        const imgData = canvas.toDataURL('image/png');
        const link = document.createElement('a');
        link.href = imgData;
        link.download = 'canvas-image.png';
        link.click();
      });


    },

  }
}
</script>


<style scoped>
.cloud-shape {
  cursor: pointer;
}

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

.cloud-editable-text {
  border: 1px solid #ddd;
  border-radius: 4px;
  background: #fff;
  cursor: text;
}
</style>