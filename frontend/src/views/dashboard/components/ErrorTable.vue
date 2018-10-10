<template lang="pug">
div
  el-dialog.err-dialog(title='Error message' :visible.sync="dialogVisible" :close-on-click-modal="false")
    editor.exception-message(:value="exceptionMessage")

  el-row.empty-container(v-if="errors.length === 0")
    el-row.icon-container(type="flex" justify="center")
      svg-icon(icon-class='confetti' class-name='empty-icon')
    el-row.empty-text(type="flex" justify="center")
      span Hoooooray! No errors.

  el-table(
    v-if="errors.length > 0"
    :data='errors'
    style='width: 100%;padding-top: 15px;'
    v-on:cell-click="onCopyText"
    max-height="450"
    height="450"
    fit
  )
    el-table-column(label='Status' width='100' align='center')
      template(slot-scope='scope')
        el-tag(type='danger') ERROR
    el-table-column(label='Name' min-width='200' )
      template(slot-scope='scope')
        | {{scope.row.name}}
    el-table-column(label='Start time' min-width='200')
      template(slot-scope='scope')
        |{{time(scope.row.startTime)}}
    el-table-column(label='Error time' min-width='200')
      template(slot-scope='scope')
        |{{time(scope.row.endTime)}}
    el-table-column(label='Message' max-width='20' align="center")
      template(slot-scope='scope')
        i.el-icon-more(@click="onMessage(scope.row.errorMessage)")
</template>

<script>
import moment from 'moment'
import { Message } from 'element-ui'
import Editor from '@/components/Editor'

export default {
  props: {
    errors: { type: Array, default: () => [] }
  },
  components: {
    Editor
  },
  data() {
    return {
      list: null,
      dialogVisible: false,
      exceptionMessage: ''
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
  methods: {
    time(t) {
      if (!t) {
        return ''
      }
      return moment(t).format('MMMM D hh:mm:ss a')
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
    onMessage(message) {
      this.dialogVisible = true
      this.exceptionMessage = message
    }
  }
}
</script>

<style scoped>

.el-icon-more {
  cursor: pointer;
}

.more-bold {
  font-weight: bolder;
  cursor: pointer;
}
.more-fade {
  color: #dbcdcd;
}

.err-dialog {
  max-height: 700px;
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

.el-icon-more {
  opacity: 0.2;
}

.el-table__row:hover .el-icon-more {
  opacity: 1;
}

</style>
