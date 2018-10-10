<template lang="pug">
div
  el-dialog.err-dialog(title='Error message' :visible.sync="dialogVisible")
    editor.exception-message(value="")

  el-table(
    v-loading="loading"
    :data='agents'
    style='width: 100%;padding-top: 15px;'
    v-on:cell-click="onCopyText"
    max-height="450"
    height="450"
    fit
  )
    el-table-column(label='Status' width='150' align='center')
      template(slot-scope='scope')
        el-tag(:type="scope.row.status !== 'UNKNOWN' ? (scope.row.status === 'UP' ? 'success' : 'danger') : 'warning'") {{ scope.row.status }}
    el-table-column(label='Name' min-width='200' )
      template(slot-scope='scope')
        | {{scope.row.name}}
    el-table-column(label='Host' min-width='200')
      template(slot-scope='scope')
        | {{scope.row.host}} : {{scope.row.port}}
    el-table-column(label='Running' min-width='200')
      template(slot-scope='scope')
        | {{runningCount(scope.row.name)}}
    el-table-column(label='Test' max-width='20' align="center")
      template(slot-scope='scope')
        div(@click="onTest(scope.row.internalId)")
          svg-icon(icon-class='test' class-name="test-icon")
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
import _ from 'lodash'
import { Message } from 'element-ui'
import Editor from '@/components/Editor'

export default {
  props: {
    agents: { type: Array, default: () => [] },
    running: { type: Array, default: () => [] }
  },
  components: {
    Editor
  },
  data() {
    return {
      loading: false,
      dialogVisible: false
    }
  },
  filters: {
    statusFilter(status) {
      const statusMap = {
        success: 'success',
        pending: 'danger'
      }
      return statusMap[status]
    }
  },
  created() {
  },
  computed: {
    ...mapGetters(['connection']),
    agentSessions() {
      return _.groupBy(this.running, 'agentName')
    }
  },
  methods: {
    ...mapActions('agent', [
      'test'
    ]),
    runningCount(name) {
      const list = this.agentSessions[name]
      if (list) {
        return list.length + ' session' + (list.length > 1 ? 's' : '')
      } else {
        return 'Idle'
      }
    },
    onCopyText(row, column, cell, event) {
      if (column.label !== 'Name') return
      const text = row.name
      const handler = (e) => {
        e.clipboardData.setData('text/plain', text)
        e.preventDefault()
        document.removeEventListener('copy', handler, true)
        Message.success('Copied to clipboard')
      }
      document.addEventListener('copy', handler, true)
      document.execCommand('copy')
    },
    onTest(internalId) {
      if (!this.connection) {
        Message.warning('No connection selected')
        return
      }
      const connectionId = this.connection.id
      this.loading = true
      this.test({ connectionId, internalId }).then(() => {
        this.loading = false
      }).catch(error => {
        this.loading = false
        console.log(error)
      })
    }
  }
}
</script>

<style scoped>
.more-bold {
  font-weight: bolder;
  cursor: pointer;
  color: #36A3F7;
}
.more-fade {
  color: #dbcdcd;

}
.err-dialog {
  max-height: 700px;
}

.test-icon {
  font-size: 20px;
  opacity: 0.2;
  cursor: pointer;
}

.el-table__row:hover .test-icon {
  opacity: 1;
}

</style>
