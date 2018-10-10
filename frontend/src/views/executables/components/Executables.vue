<template lang="pug">
.row
  stats(
    :type="stats.type"
    :visible="stats.visible"
    v-loading="stats.loading"
    :data="stats.data"
    @close="stats.visible=false"
  )
  export-one(:obj="exportObj" :visible="exportVisible" @close="exportVisible=false")
  run-prompt(
    :running="running"
    :visible="runPromptVisible"
    :objectType="runObjectType"
    @close="runPromptVisible=false"
    @run="(o) => onRun(o)"
  )
  el-card.box-card.tree-card
    .clearfix(slot="header")
      span(style="margin:auto;") {{header}}
      el-button(:disabled="loading" @click="onReload" icon="el-icon-refresh" title="Refresh" circle style="float:right;margin-left:10px;")
      el-dropdown(style="float:right;margin-left:10px;" @command="onMenu" trigger="click")
        el-button(:disabled="loading" icon="el-icon-more" title="Import" circle)
        el-dropdown-menu(slot='dropdown')
          el-dropdown-item(disabled='') New Load Plan
          el-dropdown-item(disabled='') Import Load Plan
          el-dropdown-item(command="SCEN") Import Scenario
      el-input(:disabled="loading" placeholder='Search' v-model='filterText' style="width:400px;float:right;padding:3px 0")
    el-tree(
      style="margin-bottom:100px;"
      v-loading="loading"
      ref="executables"
      :data='executables'
      node-key='key'
      :expand-on-click-node='false'
      :props="treeProps"
      :render-content="renderContent"
      :filter-node-method="filterNode"
    )

</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import clipboard from '@/utils/clipboard'
import ExportOne from '@/components/ExportOne'
import RunPrompt from '@/components/RunPrompt'
import Stats from './Stats'
import { Message } from 'element-ui'

export default {
  components: {
    ExportOne,
    RunPrompt,
    Stats
  },
  props: {
    executables: { type: Array, default: () => [] },
    loading: { type: Boolean, default: false }
  },
  watch: {
    filterText(val) {
      this.$refs.executables.filter(val)
    }
  },
  data() {
    return {
      running: false,
      runObjectType: 'SCENARIO',
      exportObj: {},
      runPromptVisible: false,
      exportVisible: false,
      header: '',
      filterText: undefined,
      treeProps: {
        label: 'name'
      },
      exec: {},
      statsVisible: false,
      stats: {
        visible: false,
        data: [],
        type: 'SCENARIO',
        loading: false
      }
    }
  },
  computed: {
    ...mapGetters(['connection'])
  },
  methods: {
    ...mapActions('executable', [
      'run'
    ]),
    ...mapActions('loadPlanRun', {
      'findLoadPlanRuns': 'findByLoadPlan'
    }),
    ...mapActions('session', {
      'findSessions': 'findByCriteria'
    }),
    _run(runnable, options) {
      const name = runnable.name
      const version = runnable.version
      const type = runnable.type
      const contextCode = options.contextCode
      const agentId = options.agentId
      const connectionId = this.connection.id
      this.running = true
      this.run({ connectionId, type, name, version, contextCode, agentId }).then(response => {
        this.running = false
        this.runPromptVisible = false
        Message.success('Started session')
      }).catch(error => {
        this.running = false
        this.runPromptVisible = false
        console.log(error)
        Message.error('Unable to run object!')
      })
    },
    onRun(options) {
      options.type = this.exec.type
      this._run(this.exec, options)
    },
    onRunPrompt(data) {
      this.runObjectType = data.type
      this.runPromptVisible = true
      this.exec = data
    },
    onMenu(command) {
      this.$emit('import', command)
    },
    onStats(data) {
      this.stats.type = data.type
      const scenarioName = data.name
      const criteria = { scenarioName }
      this.stats.visible = true
      const { internalId } = data
      const connectionId = this.connection.id
      switch (this.stats.type) {
        case 'SCENARIO':
          this.stats.loading = true
          this.stats.type = 'SESSION'
          this.findSessions({ connectionId, criteria }).then(response => {
            this.stats.data = response.data
            this.stats.loading = false
          }, _ => (this.stats.loading = false))
          break
        case 'LOAD_PLAN':
          this.stats.loading = true
          this.stats.type = 'LOAD_PLAN'
          this.findLoadPlanRuns({ connectionId, internalId }).then(response => {
            this.stats.data = response.data
            this.stats.loading = false
          }, () => { this.stats.loading = false })
          break
      }
    },
    onExport(data) {
      this.exportVisible = true
      this.exportObj = data
    },
    onDelete() {
      const h = this.$createElement
      this.$notify({
        title: 'Coming soon',
        message: h('i', { style: 'color: teal' }, 'This feature will be available')
      })
    },
    onReload() { this.$emit('reload') },
    onBack() {},
    filterNode(value, data) {
      if (!value) return true
      return data.name.toUpperCase().indexOf(value.toUpperCase()) !== -1
    },
    renderContent(h, { node, data, store }) {
      const getIcon = (type) => {
        switch (type) {
          case 'SCEN_FOLDER': return 'folder'
          case 'LOAD_PLAN': return 'flow-chart'
          case 'SCENARIO': return 'box'
        }
        return 'box'
      }
      const getTitle = (type) => {
        switch (type) {
          case 'SCEN_FOLDER': return 'Scenario Folder'
          case 'LOAD_PLAN': return 'Load Plan'
          case 'SCENARIO': return 'Scenario'
        }
      }

      const onMenu = (command, data) => {
        switch (command) {
          case 'EXPORT': this.onExport(data); break
          case 'RUN': this.onRunPrompt(data); break
          case 'DELETE': this.onDelete(data); break
          case 'STATS': this.onStats(data); break
        }
      }

      return (
        <span class='custom-tree-node'>
          <span>
            <span style='margin-right:10px;' title={getTitle(data.type)}>
              <svg-icon class-name={ 'icon-' + getIcon(data.type)} icon-class={getIcon(data.type) }></svg-icon>
            </span>
            <span on-click={ (e) => clipboard(data.name, e)}>{data.name}</span>
          </span>
          <span>
            <span class='action' on-click={ (_) => this.onRunPrompt(data) } title='Run'>
              <svg-icon icon-class='play-button' style={ data.type === 'SCEN_FOLDER' ? 'opacity:0' : '' }>
              </svg-icon>
            </span>
            <span class='action' on-click={ (_) => this.onExport(data) } title='Export'>
              <i class='el-icon-download'></i>
            </span>
            <span class='action' on-click={ (_) => this.$emit('itemDetail', data) } title='Details'>
              <svg-icon icon-class='list' style={ data.type !== 'LOAD_PLAN' ? 'opacity:0' : '' }>
              </svg-icon>
            </span>
            <span class='action'>
              <el-dropdown trigger='click' on-command={ command => onMenu(command, data) }>
                <span class='el-dropdown-link'>
                  <i class='el-icon-more el-icon--right'></i>
                </span>
                <el-dropdown-menu slot='dropdown'>
                  <el-dropdown-item disabled={ data.type === 'SCEN_FOLDER' } command='STATS'>Show stats</el-dropdown-item>
                  <el-dropdown-item divided command='EXPORT'>Export</el-dropdown-item>
                  <el-dropdown-item disabled={ data.type === 'SCEN_FOLDER' } command='RUN'>Run</el-dropdown-item>
                  <el-dropdown-item command='DELETE'>
                    <i class='el-icon-delete' style='color:#F56C6C;'></i>
                    <span style='margin-left:3px;'>Delete</span>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </span>
          </span>
        </span>
      )
    }
  }
}
</script>

<style scoped>
.el-tree-node__content {
  height: 34px;
}


.node-time {
  margin-right: 10px;
  font-size: 14px;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both
}

.el-card {
  border: none;
}

.el-card.is-always-shadow, .el-card.is-hover-shadow:focus, .el-card.is-hover-shadow:hover {
-webkit-box-shadow : none;
 box-shadow: none;
}



</style>

<style>

.custom-tree-node {
  flex: 1;
  display: flex;
  /* align-items: center; */
  justify-content: space-between;
  font-size: 16px;
  padding-right: 8px;
}

.action {
  margin-left: 10px;
  opacity: 0.1;
}

.el-tree-node__content:hover .action {
  opacity: 1;
}

.action:hover {
  color: #0091EA
}

.icon-folder {
  /* color: #FF7043; */
  /* font-weight: bolder; */
}

.icon-process {
 /* color: #40C4FF; */
}

.icon-box {
  /* color: #FFD54F; */
}

</style>
