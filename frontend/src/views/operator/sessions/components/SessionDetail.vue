<template lang="pug">
el-dialog.session-dialog(
  @close="onClose"
  @open="onOpen"
  :visible="visible"
  :title="title"
)
  el-tabs(v-model='activeTab' tab-position="left")
    el-tab-pane(label='General' name='general')
      el-alert(:title="obj.status" :type="alertType" :closable="false")
      el-form(label-width='140px' style="margin-top:20px;")
        el-form-item(label="Start time")
          el-input(readonly suffix-icon='el-icon-date' v-model='startTime')
        el-form-item(label="End time")
          el-input(readonly suffix-icon='el-icon-date' v-model='endTime')
        el-form-item(label="Duration")
          el-input(readonly placeholder='Pick a date' v-model='obj.duration')
            span(slot="suffix" class="seconds") seconds
        .error-message(v-if="obj.errorMessage" style="max-height:200px;")
          editor(:value="obj.errorMessage")
    el-tab-pane(label='Code' name='code')
      el-collapse(v-model='activeCode' accordion)
        el-collapse-item(title='Source' name='code-source')
          editor(:value="obj.sourceCommand ? obj.sourceCommand : ''")
        el-collapse-item(title='Target' name='code-target')
          editor(:value="obj.targetCommand ? obj.targetCommand : ''")
    el-tab-pane(label='Connection' name='connection')
      el-collapse(v-model='activeConnection' accordion)
        el-collapse-item(title='Source' name='connection-source')
          el-form(label-width='140px' style="margin-top:20px;")
            el-form-item(label="Connection name")
              el-input(readonly v-model='obj.sourceDataServerName')
            el-form-item(label="Logical schema")
              el-input(readonly v-model='obj.sourceLogicalSchemaName')
        el-collapse-item(title='Target' name='connection-target')
          el-form(label-width='140px' style="margin-top:20px;")
            el-form-item(label="Connection name")
              el-input(readonly v-model='obj.targetDataServerName')
            el-form-item(label="Logical schema")
              el-input(readonly v-model='obj.targetLogicalSchemaName')
</template>

<script>
// import { saveAs } from 'file-saver/FileSaver'
// import { Message } from 'element-ui'
import { mapGetters } from 'vuex'
import Editor from '@/components/Editor'
import { time2str } from '@/utils/time'

export default {
  props: {
    visible: { type: Boolean, default: false },
    obj: { type: Object, default: () => {} }
  },
  components: {
    Editor
  },
  data() {
    return {
      activeTab: 'general',
      loading: false,
      activeConnection: 'connection-target',
      options: {
        name: '',
        exportChildComponents: true,
        exportKey: undefined,
        reExportKey: undefined,
        xmlVersion: '1.0',
        xmlCharSet: 'ISO-8859-1',
        javaCharSet: 'ISO8859_1'
      },
      rules: {
        name: [{ required: true, trigger: 'blur', message: 'Name is required' }]
      }
    }
  },
  computed: {
    ...mapGetters(['connection']),
    title() { return `${this.obj.name1} ${this.obj.name2} ${this.obj.name3}` },
    startTime() { return time2str(this.obj.startTime) },
    endTime() { return time2str(this.obj.endTime) },
    alertType() {
      switch (this.obj.status) {
        case 'ERROR': return 'error'
        case 'DONE': return 'success'
        case 'WARNING': return 'warning'
        default: return 'primary'
      }
    },
    activeCode() {
      if (this.obj.sourceCommand) {
        return 'code-source'
      } else if (this.obj.targetCommand) {
        return 'code-target'
      }
    }
  },
  methods: {
    onClose() {
      this.$emit('close')
    },
    onOpen() {
      switch (this.obj.type) {
        case 'SCENARIO': this.options.name = `${this.obj.name}_Version_${this.obj.version}`; break
        default: this.options.name = this.obj.name
      }
    }
  }

}
</script>

<style scoped>
.timing {
    display: inline-block;
    width: 130px;
  }
.session-dialog {
  min-height: 700px;
}
.error-message .editor{
  max-height: 200px !important;
  min-height: unset;
}
.CodeMirror {
  min-height: unset !important;
  max-height: 200px !important;
}

.error-message .editor {
  min-height: unset;
}

/* .cm-s-monokai {
  max-height: 200px !important;
} */

</style>

