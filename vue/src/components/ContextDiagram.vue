<template>

  <div>
    <div class="canvas" ref="canvas"></div>
  </div>


</template>

<script>

import * as joint from 'jointjs'
import 'jointjs/dist/joint.css'
export default {
  name: "ContextDiagram",
  data () {
    return {
      paper: null
    }
  },
  mounted() {
    this.init()
  },
  methods: {
    init() {
      let graph = new joint.dia.Graph

      this.paper = new joint.dia.Paper({
        el: this.$refs.canvas,
        model: graph,
        width: 2000,
        height: 800,
        gridSize: 10, // 网格大小
        drawGrid: true, // 显示网格
        background: {
          color: 'rgba(255, 255, 255, 0.3)'
          // color: 'rgba(0, 255, 0, 0.3)'
        }
      })

      let rect1 = new joint.shapes.standard.Rectangle()
      rect1.position(600, 30)
      rect1.resize(120, 40)
      rect1.attr({
        body: {
          fill: 'white'
        },
        label: {
          text: '太阳敏感器',
          fill: 'black'
        }
      })
      rect1.addTo(graph)

      let rect0 = rect1.clone()
      rect0.translate(-300, 150)
      rect0.attr('label/text', '太阳搜索控制系统')
      rect0.addTo(graph)

      let rect5 = rect1.clone()
      rect5.translate(300, 0)
      rect5.attr('label/text', '太阳')
      rect5.addTo(graph)


      let elli1 = new joint.shapes.standard.Ellipse()
      elli1.position(1200, 100)
      elli1.resize(100, 40)
      elli1.attr({
        body: {
          fill: 'white'
        },
        label: {
          text: '追踪太阳',
          fill: 'black'
        }
      })
      elli1.addTo(graph)

      let elli2 = elli1.clone()
      elli2.translate(0, 100)
      elli2.attr('label/text', '响应操作员指令')
      elli2.addTo(graph)




      let rect2 = rect1.clone()
      rect2.translate(0, 100)
      rect2.attr('label/text', '推力器')
      rect2.addTo(graph)

      let rect3 = rect2.clone()
      rect3.translate(0, 100)
      rect3.attr('label/text', '陀螺')
      rect3.addTo(graph)

      let rect4 = rect3.clone()
      rect4.translate(0, 100)
      rect4.attr('label/text', '数据管理计算机')
      rect4.addTo(graph)

      let link1 = new joint.shapes.standard.Link()
      link1.source(rect0)
      link1.target(rect1)
      link1.addTo(graph)

      let link2 = new joint.shapes.standard.Link()
      link2.source(rect0)
      link2.target(rect2)
      link2.addTo(graph)

      let link3 = new joint.shapes.standard.Link()
      link3.source(rect0)
      link3.target(rect3)
      link3.addTo(graph)

      let link4 = new joint.shapes.standard.Link()
      link4.source(rect0)
      link4.target(rect4)
      link4.addTo(graph)



      let rect6 = rect5.clone()
      rect6.translate(0, 100)
      rect6.attr('label/text', '卫星')
      rect6.addTo(graph)

      let rect7 = rect6.clone()
      rect7.translate(0, 100)
      rect7.attr('label/text', '地面操作员')
      rect7.addTo(graph)

      let link5 = new joint.shapes.standard.Link()
      link5.source(rect1)
      link5.target(rect5)
      link5.appendLabel({
        attrs: {
          text: {
            text: '太阳可见标志，太阳方位角度'
          }
        }
      })
      link5.addTo(graph)

      let link6 = new joint.shapes.standard.Link()
      link6.source(rect2)
      link6.target(rect6)
      link6.appendLabel({
        attrs: {
          text: {
            text: '卫星姿态角、卫星角速度'
          }
        }
      })
      link6.addTo(graph)

      let link7 = new joint.shapes.standard.Link()
      link7.source(rect3)
      link7.target(rect6)
      link7.appendLabel({
        attrs: {
          text: {
            text: '卫星姿态角、卫星角速度'
          }
        }
      })
      link7.addTo(graph)

      let link8 = new joint.shapes.standard.Link()
      link8.source(rect4)
      link8.target(rect7)
      link8.appendLabel({
        attrs: {
          text: {
            text: '地面指令、遥测数据'
          }
        }
      })
      link8.addTo(graph)

      let link9 = new joint.shapes.standard.Link()
      link9.source(elli1)
      link9.target(rect5)
      link9.appendLabel({
        attrs: {
          text: {
            text: '感知太阳位置'
          }
        }
      })
      link9.addTo(graph)

      let link10 = new joint.shapes.standard.Link()
      link10.source(elli1)
      link10.target(rect6)
      link10.appendLabel({
        attrs: {
          text: {
            text: '调整卫星姿态'
          }
        }
      })
      link10.addTo(graph)

      let link11 = new joint.shapes.standard.Link()
      link11.source(elli2)
      link11.target(rect6)
      link11.appendLabel({
        attrs: {
          text: {
            text: '设置卫星模式'
          }
        }
      })
      link11.addTo(graph)

      let link12 = new joint.shapes.standard.Link()
      link12.source(elli2)
      link12.target(rect7)
      link12.appendLabel({
        attrs: {
          text: {
            text: '指令'
          }
        }
      })
      link12.addTo(graph)

    }
  }
}
</script>



<style scoped>

</style>