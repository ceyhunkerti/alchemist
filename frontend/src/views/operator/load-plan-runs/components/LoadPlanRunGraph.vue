<template lang="pug">
el-dialog(@close="onClose" @open="onOpen" :visible="visible" fullscreen) {{header}}
  div#run-graph-container(ref="container" :style="{height:height,width:width}")
</template>

<script>
import moment from 'moment'
import _ from 'lodash'
import echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme
import { debounce } from '@/utils'

export default {
  props: {
    run: { type: Object, default: () => {} },
    root: { type: Object, default: () => {} },
    visible: { type: Boolean, default: false },
    width: { type: String, default: '100%' },
    height: { type: String, default: '750px' }
  },
  data() {
    return {
      chart: undefined
    }
  },
  methods: {
    onClose() {
      this.$emit('close')
    },
    onOpen() {
      setTimeout(() => {
        this.loadGraph()
        this.__resizeHanlder = debounce(() => {
          if (this.chart) {
            this.chart.resize()
          }
        }, 100)
        window.addEventListener('resize', this.__resizeHanlder)
      }, 10)
    },
    time(t) {
      if (!t) {
        return ''
      }
      return moment(t).format('MMMM D hh:mm:ss a')
    },
    loadGraph() {
      const graph = { categories: [], links: [], data: [] }
      const root = this.root

      const mkGraph = (parent, node, graph) => {
        const name = node.name + ' - ' + node.stepId
        const _name = node.name

        let log = {}
        if (node.log) {
          log = {
            startTime: node.log.startTime,
            endTime: node.log.endTime,
            status: node.log.status,
            duration: node.log.duration
          }
        }
        let category = parent
        const stepType = node.stepType
        const source = parent
        const target = name
        let size = 1

        if (source && target) { // add link
          graph.links.push({ source, target })
        }

        if (node.children) { // add category
          category = parent ? name : undefined
          graph.categories.push(parent ? category : name)
          size = node.children.length
        }
        graph.data.push({ _name, name, log, category, size, stepType })

        _.forEach(node.children, n => { mkGraph(name, n, graph) })
      }

      mkGraph(undefined, root, graph)

      const graphData = _.map(graph.data, d => {
        const name = d.name
        const _name = d._name
        const value = d.log.status
        const symbolSize = d.size
        const category = d.category
        const draggable = 'true'
        return { _name, name, value, symbolSize, category, draggable }
      })

      const categories = _.map(graph.categories, c => {
        const name = c
        return { name }
      })

      const el = document.getElementById('run-graph-container')
      this.chart = echarts.init(el, 'macarons')
      const options = {
        backgroundColor: new echarts.graphic.RadialGradient(0.3, 0.3, 0.8, [{
          offset: 0,
          color: '#FFF'
        }, {
          offset: 1,
          color: '#FFF'
        }]),
        tooltip: {},
        legend: [{
          formatter: function(name) {
            return echarts.format.truncateText(name, 100, '14px Microsoft Yahei', '…')
          },
          tooltip: {
            show: true
          },
          selectedMode: 'false',
          bottom: 5,
          data: graph.categories
        }],
        toolbox: {
          show: true,
          feature: {
            dataView: { show: true, readOnly: true },
            restore: { show: true },
            saveAsImage: { show: true }
          }
        },
        // animationDuration: 1000,
        // animationEasingUpdate: 'quinticInOut',
        series: [{
          name: root.name,
          type: 'graph',
          layout: 'force',
          force: {
            repulsion: 50
          },
          data: graphData,
          links: graph.links,
          categories: categories,
          focusNodeAdjacency: true,
          roam: true,
          label: {
            normal: {
              show: true,
              position: 'top',
              formatter: function(v) {
                return v.data._name
              }
            }
          },
          lineStyle: {
            normal: {
              color: 'source',
              curveness: 0,
              type: 'solid'
            }
          }
        }]
      }
      this.chart.setOption(options)
    }
  },
  computed: {
    header() {
      return `${this.run.loadPlanInstance.name} - ${this.time(this.run.startTime)}`
    }
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    window.removeEventListener('resize', this.__resizeHanlder)
    this.chart.dispose()
    this.chart = null
  }
}
</script>
