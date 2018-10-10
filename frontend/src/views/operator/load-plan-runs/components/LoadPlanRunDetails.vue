<template lang="pug">
div
  load-plan-run-graph(:run="run" :root="rootStep" :visible="graphVisible" @close="graphVisible=false")
  el-card.box-card.tree-card
    .clearfix(slot="header")
      span(style="margin:auto;") {{header}}
      el-button(@click="onBack" type="danger" plain title="Back" icon='el-icon-arrow-left' circle style="float:right;margin-left:10px;")
      el-button(:disabled="loading" @click="onReload" icon="el-icon-refresh" title="Refresh" circle style="float:right;margin-left:10px;")
      el-button(:disabled="loading" @click="onGraph" icon="el-icon-news" title="Graph" circle style="float:right;margin-left:10px;")
      el-input(:disabled="loading" placeholder='Search' v-model='filterText' style="width:400px;float:right;padding:3px 0")
    el-tree(
      ref="tree"
      v-loading="loading"
      :data='steps'
      node-key='stepId'
      :render-content="renderContent"
      :expand-on-click-node='false'
      props="treeProps"
      :filter-node-method="filterNode"
    )

</template>

<script>
import { Message } from 'element-ui'
import { mapActions, mapGetters } from 'vuex'
import moment from 'moment'
import clipboard from '@/utils/clipboard'
import _ from 'lodash'
import LoadPlanRunGraph from './LoadPlanRunGraph'

export default {
  props: {
    run: { type: Object, default: () => {} }
  },
  components: {
    LoadPlanRunGraph
  },
  data() {
    return {
      now: moment(),
      loading: false,
      hoverId: undefined,
      steps: [],
      filterText: '',
      treeProps: {
        children: 'children'
      },
      graphVisible: false,
      rootStep: {}
    }
  },
  mounted() {
    this.loadData()
  },
  created() {
    setInterval(() => { this.now = new Date() }, 1000)
  },
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val)
    }
  },
  computed: {
    ...mapGetters(['connection']),
    internalId() {
      return this.run.internalId
    },
    header() {
      return `${this.run.loadPlanInstance.name} - ${this.time(this.run.startTime)}`
    }
  },
  methods: {
    ...mapActions('loadPlanRun', [
      'findOneWithLogs'
    ]),
    onGraph() {
      this.graphVisible = true
    },
    onBack() {
      this.$emit('close')
    },
    filterNode(value, data) {
      if (!value) return true
      return data.name.toUpperCase().indexOf(value.toUpperCase()) !== -1
    },
    onReload() {
      this.loadData()
    },
    loadData() {
      const connectionId = this.connection.id
      const internalId = this.internalId
      this.loading = true
      this.findOneWithLogs({ connectionId, internalId }).then(response => {
        this.rootStep = response.data.loadPlanInstance.rootStep
        this.steps = this.rootStep.children
        this.loading = false
      }).catch(e => {
        console.log(e)
        this.loading = false
        Message.error('Unable to load steps!')
      })
    },
    time(t) {
      if (!t) {
        return ''
      }
      return moment(t).format('MMMM D hh:mm:ss a')
    },
    duration(startTime, endTime) {
      if (!startTime) return

      if (!endTime) {
        endTime = this.now
      }
      const d = moment.duration(moment(endTime) - moment(startTime))
      const h = _.padStart(d.days() * 24 + d.hours(), 2, 0)
      const m = _.padStart(d.minutes(), 2, 0)
      const s = _.padStart(d.seconds(), 2, 0)
      return `${h}:${m}:${s}`
    },

    renderContent(h, { node, data, store }) {
      const getType = (s) => {
        switch (s) {
          case 'DONE': return 'success'
          case 'ERROR': return 'danger'
          case 'RUNNING': return ''
        }
        return 'warning'
      }

      const getStepType = (s) => {
        if (!s) return
        return s[0].toUpperCase()
      }

      const getContainerStyle = (s) => {
        return `margin-right:10px;font-size:11px;font-weight:bold;color:#42A5F5`
      }

      const onClearLog = (data) =>  {
        console.log(data)
      }

      return (
        <span class='custom-tree-node'>
          <span>
            <span
              title={data.stepType}
              style={getContainerStyle(data.stepType)}>{getStepType(data.stepType)}</span>
            <span on-click={ (e) => clipboard(data.name, e)}>{data.name}</span>
          </span>
          <span>
            <span title='Start time' class='node-time'>
              { data.log ? this.time(data.log.startTime) : '' }
            </span>
            <span title='End time' class='node-time'>
              { data.log ? this.time(data.log.endTime) : '' }
            </span>
            <span title='Duration' class='node-time'>
              { data.log ? this.duration(data.log.startTime, data.log.endTime) : '' }
            </span>
            <span>
              <el-tag
                size='mini'
                type={getType(data.log.status)}
              >
                {data.log.status ? data.log.status : 'WAITING'}
              </el-tag>
            </span>
            <span class='session-details'>
              <svg-icon icon-class='list' style={ !data.log.sessionId ? 'opacity:0' : '' }>
              </svg-icon>
            </span>
          </span>
        </span>
      )
    }
  }
}
// <span class='session-details' style="color:red;" on-click= { (e) => onClearLog(data) }>
//    <i class="el-icon-delete"></i>
// </span>
</script>


<style >

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
}

.alert {
  color: #FF6E40;
  font-weight: bolder;
  /* background-color: #fef0f0; */
}

/* .el-tree-node {
  padding: 5px;
} */

.el-tree-node__content {
  height: 34px;
}

.custom-tree-node {
  flex: 1;
  display: flex;
  /* align-items: center; */
  justify-content: space-between;
  font-size: 16px;
  padding-right: 8px;
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

.tree-card .el-card__header {
  /* padding-bottom: 0; */
}

.session-details {
  margin-left: 20px;
  opacity: 0.1;
}

.session-details:hover {
  color: #0091EA
}

.el-tree-node__content:hover .session-details {
  opacity: 1;
}

</style>
