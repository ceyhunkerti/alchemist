<template>
  <div :style="{height:height,width:width}"></div>
</template>

<script>
// ! Deprecated

import _ from 'lodash'
import echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme
import { debounce } from '@/utils'
import moment from 'moment'

const animationDuration = 600

export default {
  props: {
    loadPlanRun: { type: Object, default: () => {} },
    hist: { type: Array, default: () => [] },
    width: { type: String, default: '100%' },
    height: { type: String, default: '350px' }
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
    'loadPlanRun'() {
      this.initChart(this.hist)
    }
  },
  methods: {
    initChart(hist) {
      this.chart = echarts.init(this.$el, 'macarons')
      let timeData = []
      let barData = []
      const randomize = () => {
        const start = moment('07-01-2018', 'MM-DD-YYYY')
        for (var i = 0; i < 60; i++) {
          var val = Math.random() * 100
          timeData.push(start.add(i, 'days').format('YYYY.MM.DD'))
          barData.push(echarts.number.round(val, 2))
        }
      }
      const loadHist = () => {
        const instances = _.chain(hist)
          .filter(r => r.status !== 'RUNNING')
          .sortBy(r => r.startTime)
          .groupBy(r => r.loadPlanInstance.internalId)
          .value()

        const runs = _.chain(instances)
          .keys()
          .map(k => {
            const startTime = instances[k][0].startTime
            const duration = _.chain(instances[k]).map(i => i.duration).sum().value()
            return { startTime, duration }
          }).value()
        // const runs = _.chain(hist).sortBy(r => r.startTime).value()
        timeData = _.map(runs, r => {
          return moment(new Date(r.startTime)).format('YYYY.MM.DD')
        })
        barData = _.map(runs, r => {
          return echarts.number.round(r.duration / (1000 * 60), 2)
        })
      }

      if (!hist) { randomize() } else { loadHist() }

      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        dataZoom: [{
          type: 'slider',
          start: 55,
          end: 100
        }],
        xAxis: {
          // type: 'time',
          data: timeData,
          min: 50
        },
        yAxis: {},
        series: [{
          type: 'bar',
          name: 'Duration(minutes) ',
          data: barData,
          itemStyle: {
            normal: {
              color: '#77bef7'
            }
          },
          markLine: {
            symbol: ['none', 'none'],
            data: [{
              lineStyle: { color: '#F56C6C' },
              name: 'average duration',
              type: 'average'
            }, {
              lineStyle: { color: '#67C23A' },
              name: 'minimum duration',
              type: 'min'
            }, {
              lineStyle: { color: '#F7BA2A' },
              name: 'maximum duration',
              type: 'max'
            }]
          },
          animationDuration
        }]
      }
      this.chart.setOption(option)
    }
  }
}
</script>
