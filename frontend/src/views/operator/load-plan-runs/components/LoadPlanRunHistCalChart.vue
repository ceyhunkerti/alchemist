<template>
  <div :style="{height:height,width:width}"></div>
</template>

<script>
import _ from 'lodash'
import echarts from 'echarts'
require('echarts/theme/macarons')
import { debounce } from '@/utils'
import moment from 'moment'

const animationDuration = 600

export default {
  props: {
    loadPlanRun: { type: Object, default: () => {} },
    hist: { type: Array, default: () => [] },
    width: { type: String, default: '100%' },
    height: { type: String, default: '400px' }
  },
  data() {
    return {
      chart: null
    }
  },
  mounted() {
    this.initChart(this.hist)
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
      let data = []

      const randomize = () => {
        const start = moment('07-01-2017', 'MM-DD-YYYY')
        for (var i = 0; i < 500; i++) {
          var val = Math.random() * 1000
          data.push([
            start.add(1, 'days').format('YYYY-MM-DD'),
            echarts.number.round(val, 2)
          ])
        }
      }

      const loadHist = () => {
        const instances = _.chain(hist)
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

        data = runs.map(r => [
          moment(new Date(r.startTime)).format('YYYY-MM-DD'),
          echarts.number.round(r.duration / (1000 * 60), 2)
        ])
      }

      if (!hist) { randomize() } else { loadHist() }

      const option = {
        tooltip: {
          position: 'top',
          formatter(p) {
            const format = echarts.format.formatTime('yyyy-MM-dd', p.data[0])
            return format + ': ' + p.data[1]
          }
        },
        visualMap: {
          min: 0,
          max: 2000,
          itemHeight: 800,
          calculable: true,
          orient: 'horizontal',
          left: 'center',
          top: 'top'
        },
        calendar: [{
          range: '2018',
          cellSize: ['auto', 20]
        }, {
          top: 240,
          range: '2017',
          cellSize: ['auto', 20]
        }],
        series: [{
          type: 'heatmap',
          coordinateSystem: 'calendar',
          calendarIndex: 0,
          data: data,
          animationDuration
        }, {
          type: 'heatmap',
          coordinateSystem: 'calendar',
          calendarIndex: 1,
          data: data,
          animationDuration
        }]
      }
      this.chart.setOption(option)
    }
  }
}
</script>
