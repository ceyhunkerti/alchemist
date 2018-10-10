<template lang="pug">
div
  time-select(:visible="timeSelectVisible" @close="onCloseTimeSelect", @select="onTimeFilter", :init="timeSelectInit")

  el-row.panel-group(:gutter='40')
    el-col.card-panel-col(:xs='12' :sm='12' :lg='6')
      .card-panel(@click="(e) => onPanelSelect(e, 'errors')")
        .card-panel-icon-wrapper.icon-notification(:class="activePanel==='errors' ? 'error':''")
          svg-icon(icon-class='notification' class-name='card-panel-icon')
        .card-panel-description
          .card-panel-text
            span Errors
            i#err-cal-icon.el-icon-date.time-select
          count-to.card-panel-num(:startVal='0' :endVal='status.errors.length' :duration='3200')
    el-col.card-panel-col(:xs='12' :sm='12' :lg='6')
      .card-panel(@click="(e) => onPanelSelect(e, 'running')")
        .card-panel-icon-wrapper.icon-gears(:class="activePanel==='running' ? 'running':''")
          svg-icon(icon-class='gears' class-name='card-panel-icon')
        .card-panel-description
          .card-panel-text
            span Running
            i#run-cal-icon.el-icon-date.time-select
          count-to.card-panel-num(:startVal='0' :endVal='status.running.length' :duration='2600')
    el-col.card-panel-col(:xs='12' :sm='12' :lg='6')
      .card-panel(@click="(e) => onPanelSelect(e, 'agents')")
        .card-panel-icon-wrapper.icon-antenna(:class="activePanel==='agents' ? 'agents':''")
          svg-icon(icon-class='antenna' class-name='card-panel-icon')
        .card-panel-description
          .card-panel-text Agents
          count-to.card-panel-num(:startVal='0' :endVal="agents.length" :duration='3000')
    el-col.card-panel-col(:xs='12' :sm='12' :lg='6')
      .card-panel(@click="(e) => onPanelSelect(e, 'datasources')")
        .card-panel-icon-wrapper.icon-database(:class="activePanel==='datasources' ? 'datasources':''")
          svg-icon(icon-class='database' class-name='card-panel-icon')
        .card-panel-description
          .card-panel-text Data sources
          count-to.card-panel-num(:startVal='0' :endVal='dataSources.length' :duration='3600')
</template>

<script>

import { mapActions, mapGetters } from 'vuex'
import Cookies from 'js-cookie'
import TimeSelect from '@/components/TimeSelect'
import CountTo from 'vue-count-to'
import { option2time } from '@/utils/time'

export default {
  props: {
    status: { type: Object },
    agents: { type: Array },
    dataSources: { type: Array }
  },
  components: {
    CountTo,
    TimeSelect
  },
  data() {
    return {
      errFilter: false,
      runFilter: false,
      activePanel: 'errors'
    }
  },
  computed: {
    ...mapGetters(['connection']),
    timeSelectVisible() {
      return this.errFilter || this.runFilter
    },
    timeSelectInit() {
      if (this.errFilter) return Cookies.get('err-date-filter')
      if (this.runFilter) return Cookies.get('run-date-filter')
    }
  },
  methods: {
    ...mapActions('session', [
      'findRunning',
      'findErrors'
    ]),
    onCloseTimeSelect() {
      this.errFilter = false
      this.runFilter = false
    },
    onTimeFilter(value) {
      const criteria = {}
      criteria.startTime = parseInt(option2time(value))
      const connectionId = this.connection.id
      if (this.errFilter) {
        Cookies.set('err-date-filter', value)
        this.findErrors({ connectionId, criteria })
      }
      if (this.runFilter) {
        Cookies.set('run-date-filter', value)
        this.findRunning({ connectionId, criteria })
      }
    },
    onPanelSelect(e, type) {
      this.activePanel = type
      switch (e.target.id) {
        case 'err-cal-icon': this.errFilter = true; return
        case 'run-cal-icon': this.runFilter = true; return
      }
      this.$emit('panelSelect', type)
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.panel-group {
  margin-top: 18px;
  .card-panel-col {
    margin-bottom: 32px;
  }

  .error {
    background: #f4516c;
    .card-panel-icon {
      color: #fff;
    }
  }

  .running {
    background: #40c9c6;
    .card-panel-icon {
      color: #fff;
    }
  }

  .agents {
    background: #36a3f7;
    .card-panel-icon {
      color: #fff;
    }
  }

  .datasources {
    background: #ffb94e;
    .card-panel-icon {
      color: #fff;
    }
  }


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
    &:hover {
      .card-panel-icon-wrapper {
        color: #fff;
      }
      .icon-gears {
        background: #40c9c6;
      }
      .icon-antenna {
        background: #36a3f7;
      }
      .icon-notification {
        background: #f4516c;
      }
      .icon-database {
        background: #ffb94e;
      }
    }
    .icon-gears {
      color: #40c9c6;
    }
    .icon-antenna {
      color: #36a3f7;
    }
    .icon-notification {
      color: #f4516c;
    }
    .icon-database {
      color: #ffb94e;
    }
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
  }
  .time-select {
    margin-left: 5px;
  }
  #err-cal-icon {
    &:hover {
      color: #f4516c;
    }
  }

  #run-cal-icon {
    &:hover {
      color: #40c9c6;
    }
  }
}
</style>
