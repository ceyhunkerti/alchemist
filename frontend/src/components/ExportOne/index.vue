<template lang="pug">
el-dialog(v-loading="loading" @close="onClose" @open="onOpen" :visible="visible") {{header}}
  el-form(ref='options', :model='options', label-width='140px', :rules="rules")
    el-form-item(prop="path" label="Export name")
      el-input(value="obj.name" v-model='options.name', style='width: 100%;', placeholder='' webkitdirectory directory )
    el-form-item
      el-checkbox(v-model='options.exportChildComponents' label='Child components export')
    el-form-item(label="Export key")
      el-input(type="password" v-model='options.exportKey')
    el-form-item(label="Confirm export key")
      el-input(type="password" v-model='options.reExportKey')
    el-form-item(label="XML version")
      el-input(v-model='options.xmlVersion')
    el-form-item(label="XML charset")
      el-input(v-model='options.xmlCharSet')
    el-form-item(label="Java charset")
      el-input(v-model='options.javaCharSet')
  el-row(type="flex" class="row-bg")
    el-button(type='primary', @click='onExport' style="width:100%;") Export

</template>

<script>
import { saveAs } from 'file-saver/FileSaver'
import { Message } from 'element-ui'
import { mapActions, mapGetters } from 'vuex'

export default {
  props: {
    visible: { type: Boolean, default: false },
    obj: { type: Object, default: () => {} }
  },
  data() {
    return {
      loading: false,
      header: '',
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
    ...mapGetters(['connection'])
  },
  methods: {
    ...mapActions('common', [
      'exp'
    ]),
    onClose() {
      this.$emit('close')
    },
    onOpen() {
      switch (this.obj.type) {
        case 'SCENARIO': this.options.name = `${this.obj.name}_Version_${this.obj.version}`; break
        default: this.options.name = this.obj.name
      }
    },
    onExport() {
      const connectionId = this.connection.id
      const objectType = this.obj.type
      const internalId = this.obj.internalId
      const payload = {
        connectionId,
        objectType,
        internalId,
        ...this.options
      }
      this.loading = true
      console.log(payload)
      this.exp(payload).then(response => {
        this.loading = false
        Message.success('Export success')
        saveAs(new Blob([response.data.content], { type: 'text/plain;charset=utf-8' }), response.data.name)
        this.$emit('close')
      }).catch(err => {
        this.loading = false
        console.log(err)
        Message.error('Export failed')
      })
    }
  }

}
</script>
