<template lang="pug">
el-dialog(
  @close="onClose"
  :visible="visible"
  :title="'Upload - ' + status"
  @open="onShow"
  :show-close="showClose"
  :close-on-click-modal="false"
  :close-on-press-escape="false"
)
  el-alert(v-if="status !== 'RUNNING'" :title="result(status).message" :type="result(status).status" :closable="false")
  el-table(
    v-if="data.connections && data.statuses.length > 0"
    :data='data.statuses'
    fit
  )
    el-table-column(label='' width='38' align="center")
      template(slot-scope='scope')
        i.el-icon-loading(v-if="scope.row.status.toUpperCase() === 'RUNNING'")
    el-table-column(label='Status' width='200' align='center')
      template(slot-scope='scope')
        el-tag(:type='uploadStatus(scope.row.status)') {{ scope.row.status.toUpperCase() }}
    el-table-column(label='Name' min-width='200' )
      template(slot-scope='scope')
        | {{ scope.row.connection.name }}
</template>

<script>

import { Message } from 'element-ui'

export default {
  name: 'UploadDialog',
  props: {
    visible: { type: Boolean, default: false },
    data: { type: Object, default: () => {} }
  },
  data() {
    return {
      showClose: true
    }
  },
  computed: {
    status() {
      return this.data.status
    }
  },
  methods: {
    init() {},
    onClose() {
      if (this.status.toUpperCase() !== 'RUNNING') {
        this.$emit('close')
      } else {
        Message.warning('Wait for the import done!')
      }
    },
    onShow() { this.init() },
    uploadStatus(s) {
      switch (s.toUpperCase()) {
        case 'RUNNING': return 'primary'
        case 'ERROR': return 'danger'
        case 'SUCCESS': return 'success'
        case 'WARNING': return 'warning'
        case 'WAITING': return 'info'
      }
      return 'warning'
    },
    result(s) {
      let status = 'info'
      let message = 'Running ...'
      switch (s.toUpperCase()) {
        case 'ERROR':
          status = 'error'
          message = 'Error! Failed to import all.'
          break
        case 'SUCCESS':
          status = 'success'
          message = 'Success. Imported all objects'
          break
        case 'WARNING':
          status = 'warning'
          message = 'Warning! Some tasks failed.'
          break
      }
      return { status, message }
    }
  }

}
</script>
