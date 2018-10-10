<template lang="pug">
.dashboard-editor-container
  panel-group(@panelSelect='onPanelSelect' :status="status" :agents="agents" :dataSources="dataSources")
  el-row(style='background:#fff;margin-bottom:32px;')
    error-table(v-if="activePanel === 'errors'" :errors="errorSessions")
    running-table(v-if="activePanel === 'running'" :running="runningSessions" )
    agent-table(v-if="activePanel === 'agents'" :agents="agents", :running="runningSessions")
    data-source-table(v-if="activePanel === 'datasources'" :dataSources="dataSources", :agents="agents")
</template>

<script>
import { mapGetters } from 'vuex'
import PanelGroup from './components/PanelGroup'
import ErrorTable from './components/ErrorTable'
import RunningTable from './components/RunningTable'
import AgentTable from './components/AgentTable'
import DataSourceTable from './components/DataSourceTable'

export default {
  name: 'dashboard-admin',
  components: {
    PanelGroup,
    ErrorTable,
    RunningTable,
    AgentTable,
    DataSourceTable
  },
  data() {
    return {
      activePanel: 'errors'
    }
  },
  watch: {
  },
  computed: {
    ...mapGetters('session', {
      'errorSessions': 'errors',
      'runningSessions': 'running'
    }),
    ...mapGetters('agent', {
      'agents': 'all'
    }),
    ...mapGetters('dataSource', {
      'dataSources': 'all'
    }),
    status() {
      const running = this.runningSessions
      const errors = this.errorSessions
      return { running, errors }
    }
  },
  methods: {
    onPanelSelect(p) {
      this.activePanel = p
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.dashboard-editor-container {
  padding: 32px;
  background-color: rgb(240, 242, 245);
  .chart-wrapper {
    background: #fff;
    padding: 16px 16px 0;
    margin-bottom: 32px;
  }
}
</style>
