<template lang="pug">
el-dialog(
  @close="onClose"
  @open="onOpen"
  :visible="visible"
  :title="title"
  fullscreen
)
  el-row.panel-group(:gutter='40')
    el-col.card-panel-col(:xs='12' :sm='12' :lg='6')
      .card-panel
        .card-panel-icon-wrapper.icon-database
          svg-icon(icon-class='stopwatch' class-name='card-panel-icon icon-avg-duration')
        .card-panel-description
          .card-panel-text Average Duration
          count-to.card-panel-num(:startVal='0' :endVal='duration.avg' :duration='3600')
    el-col.card-panel-col(:xs='12' :sm='12' :lg='6')
      .card-panel
        .card-panel-icon-wrapper.icon-database
          svg-icon(icon-class='stopwatch' class-name='card-panel-icon icon-max-duration')
        .card-panel-description
          .card-panel-text Maximum Duration
          count-to.card-panel-num(:startVal='0' :endVal='duration.max' :duration='3600')
    el-col.card-panel-col(:xs='12' :sm='12' :lg='6')
      .card-panel
        .card-panel-icon-wrapper.icon-database
          svg-icon(icon-class='stopwatch' class-name='card-panel-icon icon-min-duration')
        .card-panel-description
          .card-panel-text Minimum Duration
          count-to.card-panel-num(:startVal='0' :endVal='duration.min' :duration='3600')
    el-col.card-panel-col(:xs='12' :sm='12' :lg='6')
      .card-panel
        .card-panel-icon-wrapper.icon-database
          svg-icon(icon-class='numeral-symbol' class-name='card-panel-icon icon-hash')
        .card-panel-description
            .card-panel-text Number of Runs
            count-to.card-panel-num(:startVal='0' :endVal='duration.num' :duration='3600')
  el-row
    run-hist-chart(:data="_data" ref="chart")

</template>

<script>
import _ from 'lodash'
import CountTo from 'vue-count-to'
import RunHistChart from '@/components/RunHistChart'
import moment from 'moment'
import echarts from 'echarts'

export default {
  components: {
    CountTo,
    RunHistChart
  },
  props: {
    type: { type: String, required: true },
    title: { type: String, default: 'Title' },
    visible: { type: Boolean, default: false },
    data: { type: Array, required: true }
  },
  data() {
    return {
      duration: {
        min: 0,
        max: 0,
        avg: 0,
        num: 0
      },
      _data: {
        time: [],
        value: []
      }
    }
  },
  watch: {
    data() {
      this.init()
    }
  },
  methods: {
    onClose() { this.$emit('close') },
    onOpen() {},
    init() {
      const data = this.data
      const withLoadPlan = () => {
        const instances = _.chain(data)
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
        // const runs = _.chain(data).sortBy(r => r.startTime).value()
        this._data.time = _.map(runs, r => {
          return moment(new Date(r.startTime)).format('YYYY.MM.DD')
        })
        this._data.value = _.map(runs, r => {
          return echarts.number.round(r.duration / (1000 * 60), 2)
        })
      }
      const withSession = () => {
        const dones = _.filter(data, { status: 'DONE' })
        this._data.time = _.map(dones, r => {
          return moment(new Date(r.startTime)).format('YYYY.MM.DD')
        })
        this._data.value = []
        this._data.value = _.map(dones, r => {
          const duration = r.endTime - r.startTime
          return echarts.number.round(duration / (1000 * 60), 2)
        })
      }
      switch (this.type) {
        case 'LOAD_PLAN': withLoadPlan(); break
        case 'SESSION': withSession(); break
      }
      this.duration.min = _.min(this._data.value) || 0
      this.duration.max = _.max(this._data.value) || 0
      this.duration.avg = _.mean(this._data.value) || 0
      this.duration.num = _.size(this._data.value)
      this.$refs.chart.initChart(true)
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.panel-group {
  margin-top: 20px;
  background-color: #fafafa;
  .card-panel-icon-wrapper {
    float: left;
    margin: 14px 0 0 14px;
    padding: 16px;
    transition: all 0.38s ease-out;
    border-radius: 6px;
  }
  .card-panel-icon {
    float: left;
    font-size: 48px;
  }
  .icon-avg-duration {
    color:#ffb74d;
  }
  .icon-max-duration {
    color:#dd2c00;
  }
  .icon-min-duration {
    color: #43a047;
  }
  .icon-hash {
    color: #03a9f4;
  }
}
.card-panel-col {
  margin-bottom: 32px;
  .card-panel {
    height: 108px;
    cursor: pointer;
    font-size: 12px;
    position: relative;
    overflow: hidden;
    color: #666;
    background: #fff;
    box-shadow: 4px 4px 40px rgba(0, 0, 0, 0.05);
    border-color: rgba(0, 0, 0, 0.05);
  }
}

.card-panel-description {
  float: right;
  font-weight: bold;
  margin: 26px;
  margin-left: 0px;
  .card-panel-text {
    line-height: 18px;
    color: rgba(0, 0, 0, 0.45);
    font-size: 16px;
    margin-bottom: 12px;
  }
  .card-panel-num {
    font-size: 20px;
  }
}
</style>
