<template lang="pug">
.dashboard-editor-container(v-loading="loading")

  el-row(v-if="loadPlanRun")
    el-row(:gutter='8')
      el-col(:xs='{span: 24}' :sm='{span: 24}' :md='{span: 24}' :lg='{span: 24}' :xl='{span: 24}')
        load-plan-run-details(
          :run="loadPlanRun"
          :planName="loadPlanRun.loadPlanInstance.name"
          :internalId="loadPlanRun.internalId"
          @close="(id) => (loadPlanRun = undefined)")
  el-row(v-else)
    el-row(:gutter='8')
      el-col(:xs='{span: 24}' :sm='{span: 24}' :md='{span: 24}' :lg='{span: 24}' :xl='{span: 24}')
        load-plan-last-run(@rowSelect="onLoadPlanRowSelect", @steps="(run) => (loadPlanRun = run)")
    el-row(style='background:#fff;padding:16px 16px 0;')

      el-row.empty-container(v-if="!rowSelected")
        el-row.icon-container(type="flex" justify="center")
          svg-icon(icon-class='menu' class-name='empty-icon')
        el-row.empty-text(type="flex" justify="center")
          span Select a load plan above.

      el-container(v-if="rowSelected" v-loading="isCurrentEmpty")
        el-header.lph-chart-header
          span(style="color:#606266") {{currentPlanName}}
          //- el-row(type="flex" class="row-bg" justify="end")
          el-dropdown.graph-options(@command='onSetLoadPlanHistChart')
            //- i.el-icon-menu(style='margin-right:15px;color:#F56C6C;cursor:pointer;')
            span.el-dropdown-link
              | More
              i.el-icon-arrow-down.el-icon--right
            el-dropdown-menu(slot='dropdown')
              el-dropdown-item(:command="1") Bar
              el-dropdown-item(:command="2") Calendar
              el-dropdown-item(:command="3") Table
        el-main.lph-chart-main
          load-plan-run-hist-bar-chart(v-if="loadPlanHistChart===1" :loadPlanRun='current', :hist="hist")
          load-plan-run-hist-cal-chart(v-if="loadPlanHistChart===2" :loadPlanRun='current', :hist="hist")
          load-plan-run-hist-table(v-if="loadPlanHistChart===3" :loadPlanRun='current', :data="hist")
  el-row(:gutter='32' style="margin-top:32px;")
    el-col(:xs='24' :sm='24' :lg='8')
      el-container
      .chart-wrapper
        load-plan-dur-pie-chart(:loadPlans="loadPlans" v-loading="loadingLoadPlans")
    el-col(:xs='24' :sm='24' :lg='8')
      el-container
      .chart-wrapper
        load-plan-err-pie-chart(:loadPlans="loadPlans" v-loading="loadingLoadPlans")
    el-col(:xs='24' :sm='24' :lg='8')
      load-plan-blame-list(v-loading="loadingBlame" :blame="blame")

</template>

<script>
import _ from 'lodash'
import { mapActions, mapGetters } from 'vuex'
import LoadPlanLastRun from './components/LoadPlanLastRun'
import LoadPlanRunHistBarChart from './components/LoadPlanRunHistBarChart'
import LoadPlanRunHistCalChart from './components/LoadPlanRunHistCalChart'
import LoadPlanRunHistTable from './components/LoadPlanRunHistTable'
import LoadPlanDurPieChart from './components/LoadPlanDurPieChart'
import LoadPlanErrPieChart from './components/LoadPlanErrPieChart'
import LoadPlanBlameList from './components/LoadPlanBlameList'
import LoadPlanRunDetails from './components/LoadPlanRunDetails'

export default {
  name: 'dashboard-admin',
  components: {
    LoadPlanLastRun,
    LoadPlanRunHistBarChart,
    LoadPlanRunHistCalChart,
    LoadPlanDurPieChart,
    LoadPlanErrPieChart,
    LoadPlanBlameList,
    LoadPlanRunDetails,
    LoadPlanRunHistTable
  },
  data() {
    return {
      loading: false,
      loadPlanHistChart: 1,
      current: {},
      hist: [],
      rowSelected: false,
      blame: [],
      loadingBlame: false,
      loadPlanRun: undefined
    }
  },
  methods: {
    ...mapActions('session', {
      'findSessions': 'findByCriteria'
    }),
    ...mapActions('loadPlanRun', [
      'findByLoadPlan'
    ]),
    onSetLoadPlanHistChart(i) {
      this.loadPlanHistChart = i
    },
    onLoadPlanRowSelect(row) {
      if (!this.connection) { return }
      this.current = {}
      this.rowSelected = true
      const internalId = row.loadPlanInstance.loadPlan.internalId
      const connectionId = this.connection.id
      this.findByLoadPlan({ connectionId, internalId }).then(response => {
        console.log(response.data)
        this.current = row
        this.hist = response.data
      })
    },
    laodBlameList() {
      if (!this.connection) return
      const connectionId = this.connection.id
      const statuses = ['ERROR']
      const criteria = { statuses }
      this.loadingBlame = true
      this.findSessions({ connectionId, criteria }).then(response => {
        this.loadingBlame = false
        this.blame = response.data
      }).catch(e => (this.loadingBlame = true))
    }
  },
  computed: {
    ...mapGetters(['connection']),
    ...mapGetters('loadPlan', {
      'loadPlans': 'all',
      'loadingLoadPlans': 'loadingAll'
    }),
    currentPlanName() {
      if (!this.current.loadPlanInstance) { return '' }
      return this.current.loadPlanInstance.name
    },
    isCurrentEmpty() {
      return _.isEmpty(this.current)
    }
  },
  watch: {
    'connection'() {
      this.laodBlameList()
    }
  },
  created() {
    this.laodBlameList()
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.dashboard-editor-container {
  padding: 32px;
  padding-bottom: 150px;
  background-color: rgb(240, 242, 245);
}
.lph-chart-main {
  padding: 0;
  overflow: hidden;
}
.lph-chart-header {
  height: unset;
}

.chart-wrapper {
    background: #fff;
    padding: 16px 16px 0;
    margin-bottom: 32px;
  }

  .empty-icon {
  font-size: 96px;
  color: #BDBDBD;
}
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

.graph-options {
  position: absolute;
  right: 0;
  margin-right: 40px;
  cursor: pointer;
}


</style>
