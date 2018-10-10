<template lang="pug">
  div(:style="{height:height,width:width}")
</template>

<script>
import _ from 'lodash'
import echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme
import { debounce } from '@/utils'

export default {
  props: {
    className: { type: String, default: 'chart' },
    width: { type: String, default: '100%' },
    height: { type: String, default: '400px' },
    loadPlans: { type: Array, default: () => [] }
  },
  data() {
    return {
      chart: null
    }
  },
  mounted() {
    this.initChart()
    this.__resizeHanlder = debounce(() => {
      if (this.chart) {
        this.chart.resize()
      }
    }, 100)
    window.addEventListener('resize', this.__resizeHanlder)
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    window.removeEventListener('resize', this.__resizeHanlder)
    this.chart.dispose()
    this.chart = null
  },
  watch: {
    'loadPlans'() { this.initChart() }
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$el, 'macarons')
      const names = _.map(this.loadPlans, 'name')
      const data = _.map(this.loadPlans, p => {
        const value = p.errorCount
        const name = p.name
        return { name, value }
      })

      this.chart.setOption({
        title: {
          text: 'Load Plan Error Counts'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)'
        },
        legend: {
          left: 'center',
          bottom: '10',
          data: names
        },
        calculable: true,
        series: [
          {
            top: 60,
            name: 'Error count',
            type: 'pie',
            // roseType: 'radius',
            radius: [15, 115],
            // center: ['50%', '38%'],
            data: data,
            animationEasing: 'cubicInOut',
            animationDuration: 2600
          }
        ]
      })
    }
  }
}
</script>
