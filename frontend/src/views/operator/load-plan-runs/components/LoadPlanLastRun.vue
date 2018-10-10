<template lang="pug">
el-table.run-table(
  v-loading="loading"
  :data='lastRuns'
  highlight-current-row
  @current-change="handleTableSelect"
  @sort-change="onSortChange"
  style='width: 100%;padding-top: 15px;'
)
  el-table-column(
    label='Status'
    width='100'
    align='center'
    sortable
    prop="status"
    :filters="[{text: 'DONE', value: 'DONE'}, {text: 'ERROR', value: 'ERROR'}, {text: 'RUNNING', value: 'RUNNING'}]"
    :filter-method="onSelectFilter"
  )
    template(slot-scope='scope')
      el-badge(v-if="scope.row.loadPlanInstance.runCount > 1" :value="scope.row.loadPlanInstance.runCount" class="item")
        el-tag(:type='status(scope.row.status)')  {{scope.row.status}}
      el-tag(v-else :type='status(scope.row.status)')  {{scope.row.status}}
  el-table-column(label='Name' min-width='200' sortable prop="name")
    template(slot-scope='scope')
      | {{scope.row.loadPlanInstance.name}}
  el-table-column(label='Start time' min-width='200' sortable prop="startTime")
    template(slot-scope='scope')
      | {{time(scope.row.startTime)}}
  el-table-column(label='End time' min-width='200' prop="endTime" sortable)
    template(slot-scope='scope')
      | {{time(scope.row.endTime)}}
  el-table-column(label='Duration' min-width='200' sortable prop="duration")
    template(slot-scope='scope')
      div(:class="alertClassName(scope.row)")
        | {{duration(scope.row.startTime, scope.row.endTime)}}
  el-table-column(label='Average' min-width='200' sortable prop="avgDuration")
    template(slot-scope='scope')
      | {{avgDuration(scope.row.avgDuration)}}
  el-table-column(label='Progress' min-width='200' sortable)
    template(slot-scope='scope')
      el-progress(:text-inside="true" :stroke-width="18" :status="progressStatus(scope.row)" :percentage="progressPct(scope.row)")
  el-table-column(max-width='20' align="center")
    template(slot-scope='scope')
      div(@click="onDetails(scope.row)")
        svg-icon(icon-class='list' class-name="list-icon")


</template>

<script>
import { Message } from 'element-ui'
import { mapGetters, mapActions } from 'vuex'
import moment from 'moment'
import _ from 'lodash'

export default {
  data() {
    return {
      now: moment(),
      detailsClicked: false
    }
  },
  created() {
    setInterval(() => { this.now = new Date() }, 1000)
  },
  computed: {
    ...mapGetters(['connection']),
    ...mapGetters('loadPlanRun', [
      'last',
      'loadingLast'
    ]),
    lastRuns() {
      return this.last || []
    },
    loading() {
      return this.loadingLast && _.isEmpty(this.lastRuns)
    }
  },
  methods: {
    ...mapActions('loadPlanRun', [
      'sortLastRuns'
    ]),
    onSortChange({ prop, order }) {
      this.sortLastRuns({ prop, order })
    },
    onSelectFilter(value, row, column) {
      const property = column['property']
      return row[property] === value
    },
    onDetails(run) {
      this.detailsClicked = true
      if (!this.connection) {
        Message.warning('No connection selected')
        return
      }
      this.$emit('steps', run)
    },
    time(t) {
      if (!t) {
        return ''
      }
      return moment(t).format('MMMM D hh:mm:ss a')
    },
    alertClassName(row) {
      const d = this.duration(row.startTime, row.endTime)
      const a = this.avgDuration(row.avgDuration)
      return (d > a ? 'alert' : '')
    },
    duration(startTime, endTime) {
      if (!endTime) {
        endTime = this.now
      }
      const d = moment.duration(moment(endTime) - moment(startTime))
      const h = _.padStart(d.days() * 24 + d.hours(), 2, 0)
      const m = _.padStart(d.minutes(), 2, 0)
      const s = _.padStart(d.seconds(), 2, 0)
      return `${h}:${m}:${s}`
    },
    avgDuration(avgd) {
      if (!avgd) return
      const d = moment.duration(avgd)
      const h = _.padStart(d.days() * 24 + d.hours(), 2, 0)
      const m = _.padStart(d.minutes(), 2, 0)
      const s = _.padStart(d.seconds(), 2, 0)
      return `${h}:${m}:${s}`
    },
    progressPct(r) {
      switch (r.status) {
        case 'ERROR': return 100
        case 'DONE': return 100
      }
      const startTime = r.startTime
      const endTime = r.endTime ? r.endTime : this.now
      const d = moment.duration(moment(endTime) - moment(startTime)).asMilliseconds()
      if (d >= r.avgDuration) { return 99 }
      return parseFloat((d * 100 / r.avgDuration).toFixed(2))
    },
    progressStatus(r) {
      switch (r.status) {
        case 'ERROR': return 'exception'
        case 'DONE': return 'success'
      }
      return 'default'
    },
    status(s, p) {
      const m = {
        'DONE': 'success',
        'RUNNING': 'default',
        'ERROR': 'danger'
      }
      return m[s]
    },
    handleTableSelect(row) {
      if (!row) return
      setTimeout(() => {
        if (!this.detailsClicked) {
          this.$emit('rowSelect', row)
        }
        this.detailsClicked = false
      }, 10)
    }
  }
}
</script>

<style>
.run-table .el-badge__content.is-fixed {
   transform: translateY(0%) translateX(100%) !important;
}
</style>


<style scoped>

.more-bold {
  font-weight: bolder;
  cursor: pointer;
  color: #36A3F7;
}
.more-fade {
  color: #dbcdcd;

}

.list-icon {
  font-size: 16px;
  opacity: 0.3;
  cursor: pointer;
}

.el-table__row:hover  .list-icon {
  opacity: 1;
}

.alert {
  color: #FF6E40;
  font-weight: bolder;
  /* background-color: #fef0f0; */
}

</style>
