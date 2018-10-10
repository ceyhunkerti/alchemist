<template lang="pug">
.row(v-loading="loadingAll")
  session-detail(:obj="session" :visible="detailVisible" @close="detailVisible=false")
  el-card.box-card.tree-card
    .clearfix(slot="header")
      span(style="margin:auto;") {{header}}
      el-button(:disabled="loading" @click="onReload" icon="el-icon-refresh" title="Refresh" circle style="float:right;margin-left:10px;")
      el-dropdown(style="float:right;margin-left:10px;" @command="onMenuCommand" trigger="click")
        el-button(:disabled="loading" icon="el-icon-more" title="Import" circle)
        el-dropdown-menu(slot='dropdown')
          el-dropdown-item(command="purge") Purge Logs
      el-input(:disabled="loading" placeholder='Search' v-model='filterText' style="width:400px;float:right;padding:3px 0")
    el-tree(
      style="margin-bottom:100px;"
      v-loading="loading"
      ref="executables"
      :load='load'
      lazy
      node-key='_key'
      :expand-on-click-node='false'
      :props="treeProps"
      :render-content="renderContent"
      :filter-node-method="filterNode"
    )

</template>

<script>
/* eslint-disable no-case-declarations, no-fallthrough  */
import moment from 'moment'
import _ from 'lodash'
import { mapGetters, mapActions } from 'vuex'
import clipboard from '@/utils/clipboard'
import { option2time } from '@/utils/time'
import { Message } from 'element-ui'
import SessionDetail from './components/SessionDetail'

export default {
  components: {
    SessionDetail
  },
  props: {
    loading: { type: Boolean, default: false }
  },
  watch: {
    filterText(val) {
      this.$refs.executables.filter(val)
    }
  },
  data() {
    return {
      loadingAll: false,
      running: false,
      session: {},
      detailVisible: false,
      header: '',
      filterText: undefined,
      treeProps: {
        label: 'name',
        isLeaf: 'leaf'
      },
      exec: {},
      sessions: [{
        name: 'Date',
        icon: 'calendar',
        _key: 'CALENDAR',
        leaf: false
      }, {
        name: 'Agent',
        icon: 'antenna-2',
        leaf: false,
        _key: 'AGENTS',
        children: []
      }, {
        name: 'Status',
        icon: 'traffic-lights',
        _key: 'STATUS',
        children: []
      }, {
        name: 'Users',
        _key: 'USERS',
        icon: 'woman',
        children: []
      }, {
        _key: 'ALL',
        name: 'All Sessions',
        icon: 'office-material',
        children: []
      }],
      statuses: [{
        name: 'Error',
        _key: 'ERROR',
        icon: 'status-ERROR',
        children: []
      }, {
        name: 'Success',
        icon: 'status-DONE',
        _key: 'DONE',
        children: []
      }, {
        name: 'Running',
        icon: 'status-RUNNING',
        _key: 'RUNNING',
        children: []
      }, {
        name: 'Warning',
        icon: 'status-WARNING',
        _key: 'WARNING',
        children: []
      }, {
        name: 'Waiting',
        icon: 'status-WAITING',
        _key: 'WAITING',
        children: []
      }],
      calendar: [{
        name: 'Today',
        children: [],
        icon: 'calendar',
        isLeaf: 'false',
        _key: 'TODAY'
      }, {
        name: 'Yesterday',
        children: [],
        icon: 'calendar',
        _key: 'YESTERDAY'
      }, {
        name: 'This week',
        children: [],
        icon: 'calendar',
        _key: 'THIS_WEEK'
      }]
    }
  },
  computed: {
    ...mapGetters(['connection']),
    ...mapGetters('users', {
      'users': 'all'
    }),
    ...mapGetters('agent', {
      'agents': 'all'
    })
  },
  methods: {
    ...mapActions('session', [
      'findByCriteria',
      'findAll',
      'findById',
      'purge'
    ]),
    ...mapActions('security', {
      'findOdiUsers': 'findOdiUsers'
    }),
    purgeLogs() {
      const connectionId = this.connection.id
      this.loadingAll = true
      this.purge({ connectionId }).then(response => {
        Message.success('Success. Purged logs.')
        this.loadingAll = false
      }, error => {
        console.log(error)
        this.loadingAll = false
        Message.success('Error! Failed to purge logs.')
      })
    },
    onMenuCommand(command) {
      switch (command) {
        case 'purge': this.purgeLogs(); break
      }
    },
    load(node, resolve) {
      if (node.level === 0) {
        return resolve(this.sessions)
      }
      const connectionId = this.connection.id
      let id, criteria

      switch (node.data._key) {
        case 'ALL':
          this.findAll({ connectionId }).then(response => {
            resolve(_.map(response.data, d => {
              d.icon = `status-${d.status}`
              d._name = d.name
              d._key = 'SESSION'
              if (d.startTime) {
                d.name = `${d._name} - ${moment(d.startTime).format('MMMM D hh:mm:ss a')}`
              }
              return d
            }))
          },
          error => {
            resolve([])
            console.log(error)
            Message.error(error)
          })
          break
        case 'USERS':
          this.findOdiUsers({ connectionId }).then(data => {
            resolve(_.map(data, d => {
              d._key = 'USER'
              d.icon = 'boy'
              return d
            }))
          },
          error => {
            console.log(error)
            resolve([])
          })
          break
        case 'STATUS':
          resolve(this.statuses)
          break
        case 'CALENDAR':
          resolve(this.calendar)
          break
        case 'SESSION':
          id = node.data.id
          this.findById({ connectionId, id }).then(response => {
            const result = response.data
            result.icon = `status-${result.status}`
            result._key = 'SESSION_STEP'
            resolve([result])
          },
          error => {
            console.log(error)
            Message.error(error)
            resolve([])
          })
          break
        case 'SESSION_STEP':
          resolve(_.map(node.data.children, d => {
            d.icon = `status-${d.status}`
            d._key = 'SESSION_TASK_LOG'
            return d
          }))
          break
        case 'SESSION_TASK_LOG':
          if (_.isEmpty(node.data.children)) {
            resolve([])
          }
          resolve(_.chain(node.data.children).filter(s => !s.name1.startsWith('SERIAL')).map(d => {
            d.icon = `status-${d.status}`
            d._key = 'SESSION_TASK_LOG'
            d.name = `${d.name1} ${d.name2} ${d.name3}`
            d._leaf = _.isEmpty(d.children)
            return d
          }).value())
          break
        case 'USER':
          criteria = { submitterName: node.data.name }
          this.findByCriteria({ connectionId, criteria }).then(response => {
            resolve(_.map(response.data, d => {
              d.icon = `status-${d.status}`
              d._name = d.name
              d._key = 'SESSION'
              if (d.startTime) {
                d.name = `${d._name} - ${moment(d.startTime).format('MMMM D hh:mm:ss a')}`
              }
              return d
            }))
          },
          error => {
            resolve([])
            console.log(error)
            Message.error(error)
          })
          break
        case 'DONE': case 'ERROR': case 'WAITING': case 'WARNING': case 'RUNNING':
          criteria = { statuses: [node.data._key] }
          this.findByCriteria({ connectionId, criteria }).then(response => {
            resolve(_.map(response.data, d => {
              d.icon = `status-${d.status}`
              d._name = d.name
              d._key = 'SESSION'
              if (d.startTime) {
                d.name = `${d._name} - ${moment(d.startTime).format('MMMM D hh:mm:ss a')}`
              }
              return d
            }))
          },
          error => {
            resolve([])
            console.log(error)
            Message.error(error)
          })
          break
        case 'AGENTS':
          resolve(_.map(this.agents, d => {
            d.icon = 'antenna'
            d.key = 'AGENT'
            return d
          }))
          break
        case 'AGENT':
          criteria = { agentNames: [node.data.name] }
          this.findByCriteria({ connectionId, criteria }).then(response => {
            resolve(_.map(response.data, d => {
              d.icon = `status-${d.status}`
              d._name = d.name
              d._key = 'SESSION'
              if (d.startTime) {
                d.name = `${d._name} - ${moment(d.startTime).format('MMMM D hh:mm:ss a')}`
              }
              return d
            }))
          },
          error => {
            resolve([])
            console.log(error)
            Message.error(error)
          })
        case 'TODAY': case 'YESTERDAY': case 'THIS_WEEK':
          const startTime = parseInt(option2time(node.data._key))
          let endTime
          if (node.data._key === 'YESTERDAY') {
            endTime = parseInt(option2time('TODAY'))
          } else {
            endTime = parseInt(option2time('NOW'))
          }
          criteria = { startTime, endTime }
          this.findByCriteria({ connectionId, criteria }).then(response => {
            resolve(_.map(response.data, d => {
              d.icon = `status-${d.status}`
              d._name = d.name
              d._key = 'SESSION'
              if (d.startTime) {
                d.name = `${d._name} - ${moment(d.startTime).format('MMMM D hh:mm:ss a')}`
              }
              return d
            }))
          },
          error => {
            resolve([])
            console.log(error)
            Message.error(error)
          })
          break
      }
    },
    onExport(data) {
      this.exportVisible = true
      this.exportObj = data
    },
    onGraph() {},
    onReload() {
      this.loadingAll = true
      const connectionId = this.connection.id
      this.findAll({ connectionId }).then(_ => {
        this.loadingAll = false
      }, _ => {
        this.loadingAll = false
        console.log(_)
        Message.error('Failed to fetch sessions!')
      })
    },
    onBack() {},
    onDetails(data) {
      console.log(data)
      this.session = data
      this.detailVisible = true
    },
    filterNode(value, data) {
      if (!value) return true
      return data.name.toUpperCase().indexOf(value.toUpperCase()) !== -1
    },
    renderContent(h, { node, data, store }) {
      return (
        <span class='custom-tree-node'>
          <span>
            <span style='margin-right:10px;' title={ data.status }>
              <svg-icon icon-class={data.icon ? data.icon : ''}></svg-icon>
            </span>
            <span on-click={ (e) => clipboard(data.name, e)}>{data.name}</span>
          </span>
          <span>
            <span class='action' on-click={ (_) => this.onDetails(data) } title='Details'>
              <svg-icon icon-class='list' style={ data._leaf ? '' : 'opacity:0' }>
              </svg-icon>
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
