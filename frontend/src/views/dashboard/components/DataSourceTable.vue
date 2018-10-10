<template lang="pug">
div
  el-dialog.err-dialog(
    @close="reset"
    :title="'Test ' + test.name" :visible.sync="test.dialog" :close-on-click-modal="false"
  )
    el-select(v-model='test.agent' placeholder='Select agent' :disabled="test.loading" style="width:100%")
      el-option(key="undefined" label='Local agent' value="undefined")
      el-option(v-for='item in agents' :key='item.internalId' :label='item.name' :value='item.internalId')

    el-progress.test-progress(v-if="test.loading" :percentage="test.progress")
    span.dialog-footer(slot='footer')
      el-button(@click='test.dialog = false' :disabled="test.loading") Cancel
      el-button(type='primary' @click='onTest' :disabled="test.loading") Test


  el-table(
    :data='dataSources'
    style='width: 100%;padding-top: 15px;'
    v-on:cell-click="onCopyText"
    max-height="450"
    height="450"
    fit
  )
    el-table-column(label='Status' width='110' align='center')
      template(slot-scope='scope')
        el-tag(:type="status(scope.row.status)") {{scope.row.status}}
    el-table-column(label='Name' min-width='200' )
      template(slot-scope='scope')
        | {{scope.row.name}}
    el-table-column(label='Schema' min-width='200')
      template(slot-scope='scope')
        | {{schema(scope.row.physicalSchemas)}}
    el-table-column(label='Test' max-width='20' align="center")
      template(slot-scope='scope')
        div(@click="onTestDialog(scope.row.internalId)")
          svg-icon(icon-class='test' class-name="test-icon")
</template>

<script>
import _ from 'lodash'
import { mapGetters, mapActions } from 'vuex'
import { Message } from 'element-ui'
import Editor from '@/components/Editor'

export default {
  props: {
    dataSources: { type: Array, default: () => [] },
    agents: { type: Array, default: () => [] }
  },
  components: {
    Editor
  },
  data() {
    return {
      test: {
        interval: undefined,
        progress: 20,
        loading: false,
        dialog: false,
        name: '',
        internalId: undefined,
        agent: undefined
      },
      testSourceName: '',
      dialogVisible: false,
      agent: undefined
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
    ...mapGetters(['connection'])
  },
  methods: {
    ...mapActions('dataSource', {
      'doTest': 'test'
    }),
    reset() {
      this.test.progress = 20
      this.test.loading = false
      clearInterval(this.test.interval)
    },
    schema(schemas) {
      if (!schemas || schemas.length === 0) {
        return 'No physical schemas'
      }
      if (schemas.length === 1) {
        return '1 physical schema'
      }
      return `${schemas.length} physical schemas`
    },
    status(s) {
      switch (s) {
        case 'UP': return 'success'
        case 'DOWN': return 'danger'
      }
      return 'warning'
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
    onTestDialog(internalId) {
      if (!this.connection) {
        Message.warning('No connection selected')
        return
      }
      this.test.internalId = internalId
      this.test.name = _.find(this.dataSources, { internalId }).name
      this.test.dialog = true
      this.test.interval = setInterval(() => {
        if (this.test.progress < 99) {
          this.test.progress += 1
        }
      }, 200)
    },
    onTest() {
      this.test.loading = true
      if (this.test.agent === 'undefined') this.test.agent = undefined
      const dataServerId = this.test.internalId
      const agentId = this.test.agent
      const connectionId = this.connection.id
      this.doTest({ connectionId, dataServerId, agentId }).then(() => {
        this.test.dialog = false
      }).catch(_ => { this.test.dialog = false })
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

.test-progress {
  width: 100%;
  margin-top: 30px;
}
</style>
