<template>
  <div>
    <div ref="canvas" class="paper-container"></div>
    <div
        v-if="cloudShape"
        class="cloud-editable-text"
        contenteditable
        @blur="onTextBlur"
        @input="onTextInput"
        :style="textStyle"
    >
      {{ cloudText }}
    </div>
  </div>
</template>

<script>
import * as  joint from 'jointjs';

export default {
  data() {
    return {
      paper: null,
      graph: null,
      cloudShape: null,
      cloudText: '云朵文字', // 默认文本
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
        whiteSpace: 'nowrap',
        backgroundColor: 'white',
        border: '1px solid gray'
      }
    };
  },
  mounted() {
    this.initializeGraph();
    this.$nextTick(() => {
      this.updateTextPosition();
    });
  },
  methods: {
    initializeGraph() {
      this.graph = new joint.dia.Graph();

      this.paper = new joint.dia.Paper({
        el: this.$refs.canvas,
        model: this.graph,
        width: 800,
        height: 600,
        gridSize: 10,
        drawGrid: true,
        background: {
          color: 'rgba(255, 255, 255, 0.3)'
        }
      });

      // 创建云朵形状
      this.cloudShape = new joint.shapes.standard.Polygon({
        position: { x: 100, y: 100 },
        size: { width: 200, height: 100 },
        attrs: {
          body: {
            d: 'M 50 100 Q 20 120 20 150 Q 20 180 50 200 Q 80 220 100 200 Q 120 180 150 180 Q 180 180 200 200 Q 220 220 250 200 Q 280 180 300 180 Q 320 180 350 200 Q 380 220 380 150 Q 380 120 350 100 Q 320 80 300 100 Q 280 120 250 120 Q 220 120 200 100 Q 180 80 150 80 Q 120 80 100 100 Z',
            fill: '#ffffff',
            stroke: '#000000',
            strokeWidth: 2
          },
          label: {
            text: this.cloudText,
            fill: 'black',
            fontSize: 14,
            textAnchor: 'middle',
            x: 100,
            y: 50
          }
        }
      });

      this.graph.addCell(this.cloudShape);
    },
    updateTextPosition() {
      if (this.cloudShape) {
        const bbox = this.cloudShape.getBBox();
        this.textStyle.left = `${bbox.x + bbox.width / 2 - 100}px`; // Adjust the positioning
        this.textStyle.top = `${bbox.y + bbox.height / 2 - 50}px`;
      }
    },
    onTextInput(event) {
      this.cloudText = event.target.innerText;
      if (this.cloudShape) {
        this.cloudShape.attr('label/text', this.cloudText);
      }
    },
    onTextBlur() {
      if (this.cloudShape) {
        this.cloudShape.attr('label/text', this.cloudText);
      }
    }
  }
};
</script>

<style scoped>
.paper-container {
  border: 1px solid #000;
  position: relative; /* Ensure that child elements are positioned relative to this container */
}

.cloud-editable-text {
  border: 1px solid #ddd;
  border-radius: 4px;
  background: #fff;
  cursor: text;
}
</style>
