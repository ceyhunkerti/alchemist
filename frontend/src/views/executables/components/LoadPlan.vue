<template lang="pug">
.row
  load-plan-graph(:plan="loadPlan" :visible="graphVisible" @close="graphVisible=false")
  edit-load-plan(:plan="loadPlan", :visible="editVisible" @close="editVisible=false" )
  run-prompt(
    :running="running"
    :visible="runPromptVisible"
    @close="runPromptVisible=false"
    @run="(o) => onRun(o)"
  )
  el-card.box-card.tree-card
    .clearfix(slot="header")
      span(style="margin:auto;") {{header}}
      el-button(@click="onBack" type="danger" plain title="Back" icon='el-icon-arrow-left' circle style="float:right;margin-left:10px;")
      el-button(:disabled="loading" @click="onReload" icon="el-icon-refresh" title="Refresh" circle style="float:right;margin-left:10px;")
      el-button(:disabled="loading" @click="onGraph" icon="el-icon-news" title="Graph" circle style="float:right;margin-left:10px;")
      el-button(:disabled="loading" @click="onSchedules" title="Schedules" icon="el-icon-time" circle style="float:right;margin-left:10px;")
      el-button(:disabled="loading" @click="onEdit" title="Edit" icon="el-icon-edit" circle style="float:right;margin-left:10px;")
      el-input(:disabled="loading" placeholder='Search' v-model='filterText' style="width:400px;float:right;padding:3px 0")
    el-tree(
      style="margin-bottom:100px;"
      v-loading="loading"
      ref="loadPlan"
      :data='steps'
      node-key='key'
      :highlight-current="true"
      :expand-on-click-node='false'
      :props="treeProps"
      :render-content="renderContent"
      :filter-node-method="filterNode"
    )
</template>

<script>
import { Message } from 'element-ui'
import { mapActions, mapGetters } from 'vuex'
import clipboard from '@/utils/clipboard'
import LoadPlanGraph from './LoadPlanGraph'
import EditLoadPlan from './EditLoadPlan'
import RunPrompt from '@/components/RunPrompt'

export default {
  components: {
    LoadPlanGraph,
    EditLoadPlan,
    RunPrompt
  },
  props: {
    loading: { type: Boolean, default: false },
    loadPlan: { type: Object, default: () => {} },
    name: { type: String, required: true }
  },
  watch: {
    filterText(val) {
      this.$refs.loadPlan.filter(val)
    }
  },
  data() {
    return {
      header: this.name + ' Load Plan',
      filterText: undefined,
      treeProps: {
        label: 'name'
      },
      graphVisible: false,
      editVisible: false,
      runPromptVisible: false,
      exec: {},
      running: false
    }
  },
  computed: {
    ...mapGetters(['connection']),
    steps() {
      if (!this.loadPlan) { return [] }
      if (this.loadPlan.rootStep) {
        return [this.loadPlan.rootStep]
      }
      return []
    }
  },
  methods: {
    ...mapActions('executable', [
      'runScenario'
    ]),
    _runScenario(scenario, options) {
      console.log(scenario, options)
      const name = scenario.tag.name
      const version = scenario.tag.version
      const contextCode = options.contextCode
      const agentId = options.agentId
      const connectionId = this.connection.id
      this.running = true
      this.runScenario({
        connectionId, name, version, contextCode, agentId
      }).then(response => {
        this.running = false
        this.runPromptVisible = false
        Message.success('Started scenario')
      }).catch(error => {
        this.running = false
        this.runPromptVisible = false
        console.log(error)
        Message.error('Unable to scenario!')
      })
    },
    onRunPrompt(data) {
      this.runPromptVisible = true
      this.exec = data
    },
    onRun(options) {
      this._runScenario(this.exec, options)
    },
    onGraph() {
      this.graphVisible = true
    },
    onEdit() {
      this.$notify({
        title: 'Edit Load Plan',
        message: 'Coming soon'
      })
      // this.editVisible = true
    },
    onReload() { this.$emit('reload') },
    onSchedules() {
      this.$notify({
        title: 'Schedules',
        message: 'Coming soon'
      })
    },
    onBack() { this.$emit('close') },
    renderContent(h, { node, data, store }) {
      const getStepType = (s) => {
        if (!s || s === 'SCENARIO') return
        return s[0].toUpperCase()
      }

      const getContainerStyle = (s) => {
        return `margin-right:10px;font-size:11px;font-weight:bold;color:#42A5F5`
      }

      return (
        <span class='custom-tree-node'>
          <span>
            <span
              title={data.stepType}
              style={getContainerStyle(data.stepType)}>{getStepType(data.stepType)}
            </span>
            <span title='Scenario' style={data.stepType !== 'SCENARIO' ? 'display:none;' : 'margin-right:7px;'}>
              <svg-icon icon-class='box'></svg-icon>
            </span>
            <span on-click={ (e) => clipboard(data.name, e)}>{data.name}</span>
          </span>
          <span>
            <span>
              <el-tag size='mini' type='danger' style={ data.isEnabled ? 'display:none;' : '' }>
                {data.isEnabled ? '' : 'DISABLED'}
              </el-tag>
            </span>
            <span class='action' on-click={ (_) => this.onRunPrompt(data) } title='Run scenario'>
              <svg-icon icon-class='play-button' style={data.children ? 'opacity:0' : '' }>
              </svg-icon>
            </span>
          </span>
        </span>
      )
    },
    filterNode(value, data) {
      if (!value) return true
      return data.name.toUpperCase().indexOf(value.toUpperCase()) !== -1
    },
    init() {
    }
  }
}
</script>

<style scoped>

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

.details {
  margin-left: 10px;
  opacity: 0.1;
}

.el-tree-node__content:hover .details {
  opacity: 1;
}

.details:hover {
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
