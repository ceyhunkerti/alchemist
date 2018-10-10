<template lang="pug">
  div
    #chart-container(v-if="!showEmpty" :style="{height:height,width:width}")
    el-row.empty-container(v-if="showEmpty")
      el-row.icon-container(type="flex" justify="center")
        // svg-icon(icon-class='confetti' class-name='empty-icon')
      el-row.empty-text(type="flex" justify="center")
        span No data available.
</template>

<script>

import _ from 'lodash'
require('echarts/theme/macarons') // echarts theme
import { debounce } from '@/utils'
import moment from 'moment'
import echarts from 'echarts'

const animationDuration = 600

export default {
  props: {
    data: { type: Object, required: true },
    width: { type: String, default: '100%' },
    height: { type: String, default: '350px' }
  },
  data() {
    return {
      chart: null,
      showEmpty: false
    }
  },
  watch: {
    type() {
      this.initChart()
    }
  },
  mounted() {
    this.initChart(this.data)
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
  methods: {
    demo() {
      console.log('demo')
    },
    initChart(showEmpty) {
      this.showEmpty = showEmpty

      let _time = []
      let _value = []
      const randomize = () => {
        const start = moment('07-01-2018', 'MM-DD-YYYY')
        for (var i = 0; i < 60; i++) {
          var val = Math.random() * 100
          _time.push(start.add(i, 'days').format('YYYY.MM.DD'))
          _value.push(echarts.number.round(val, 2))
        }
      }

      if (this.showEmpty && _.isEmpty(this.data.value)) {
        return
      } else if (_.isEmpty(this.data) || _.isEmpty(this.data.value)) {
        randomize()
      } else {
        _time = this.data.time
        _value = this.data.value
      }

      this.chart = echarts.init(this.$el, 'macarons')
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
          data: _time,
          min: 50
        },
        yAxis: {},
        series: [{
          type: 'bar',
          name: 'Duration(minutes) ',
          data: _value,
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

<style scoped>
.empty-container {
  width: 100%;
  min-height: 400px;
  padding-top: 100px;
}

.empty-text {
  margin-top: 30px;
}

.empty-text span {
  font-size: 22px;
  color: #BDBDBD;
}

</style>
