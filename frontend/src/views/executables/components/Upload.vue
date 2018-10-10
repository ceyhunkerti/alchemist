<template lang="pug">
.container
  upload-dialog(:visible="multiDeploy.dialogVisible", :data="multiDeploy" @close="multiDeploy.dialogVisible=false")
  el-row.container(:gutter='20' style="margin:40px;height:100;")
    el-col(:span='12', :push='6' v-loading="loading" style="margin-bottom:40px;")
      el-alert(
        style="margin-bottom:10px;"
        v-if="multiDeploy.status !== 'RUNNING'" :title="result(multiDeploy.status).message" :type="result(multiDeploy.status).status" )
      el-upload.upload(
        ref="upload"
        drag
        multiple,
        :headers="headers"
        :action="url"
        :file-list='fileList'
        :auto-upload="false"
        :on-preview='onPreview'
        :on-remove='onRemove'
        :on-error="onError"
        :on-success="onSuccess"
        :on-change="onChange"
      )
        i.el-icon-upload
        .el-upload__text
          | Drop file here or
          em  click to upload
        .el-upload__tip(slot='tip') {{tip}} (XML)

      el-form(style="margin-top: 20px;")
        el-form-item
          el-row.container(:gutter='20')
            el-col(:span="6")
              el-checkbox(v-model='importSchedules' label='Import schedules' border style="width:100%;")
            el-col(:span="18")
              el-select(v-model='mode' placeholder='Select' style="width:100%")
                el-option(v-for='item in options' :key='item.value' :label='item.label' :value='item.value')
        el-form-item
          el-row.container(:gutter='20')
            el-col(:span="6")
              el-checkbox(v-model='multiConnection' label='Multi connection' border style="width:100%;")
            el-col(v-if="multiConnection" :span="18")
              el-select(v-model='multiDeploy.connections' placeholder='Select' style="width:100%" multiple collapse-tags)
                el-option(v-for='item in connections' :key='item.id' :label='item.name' :value='item.id')
        el-row(type="flex" class="row-bg" justify="end")
          el-button(type='primary', @click='onImport' :disabled="importDisabled") Import
          el-button(type='danger' plain @click='onClose') Close


</template>

<script>
import _ from 'lodash'
import UploadDialog from './UploadDialog'
import { Message } from 'element-ui'
import { mapGetters } from 'vuex'
import { getToken } from '@/utils/auth'

export default {
  props: {
    objectType: { type: String, default: 'SCEN' }
  },
  components: {
    UploadDialog
  },
  data() {
    return {
      importDisabled: true,
      connectionId: undefined,
      multiDeploy: {
        dialogVisible: false,
        status: 'RUNNING',
        connections: [],
        responses: [],
        statuses: []
      },
      multiConnection: false,
      headers: { 'Authorization': `Bearer ${getToken()}` },
      loading: false,
      importSchedules: false,
      mode: '4',
      options: [
        { label: 'Duplication', value: '1' },
        { label: 'Synonym Mode INSERT', value: '2' },
        { label: 'Synonym Mode UPDATE', value: '3' },
        { label: 'Synonym Mode INSERT_UPDATE', value: '4' }
      ],
      fileList: []
    }
  },
  watch: {
    'multiDeploy.statuses'() {
      const result = _.countBy(this.multiDeploy.statuses, 'status')
      if ((result['waiting'] && result['waiting'] > 0) || (result['running'] && result['running'] > 0)) {
        this.multiDeploy.status = 'RUNNING'
      } else if (result['error'] && result['error'] === this.multiDeploy.statuses.length) {
        this.multiDeploy.status = 'ERROR'
      } else if (result['error'] && result['error'] !== this.multiDeploy.statuses.length) {
        this.multiDeploy.status = 'WARNING'
      } else {
        this.multiDeploy.status = 'SUCCESS'
      }
    },
    'multiDeploy.connections'() {
      if (this.multiConnection && _.isEmpty(this.multiDeploy.connections)) {
        this.importDisabled = true
      } else {
        if (_.isEmpty(this.$refs.upload.uploadFiles)) {
          this.importDisabled = true
        } else {
          this.importDisabled = false
        }
      }
    },
    multiConnection(v) {
      if (v && _.isEmpty(this.multiDeploy.connections)) {
        this.importDisabled = true
      } else {
        this.importDisabled = false
      }
    }
  },
  computed: {
    ...mapGetters(['connection']),
    ...mapGetters('connection', {
      'connections': 'all'
    }),
    uploadData() {
      const connections = this.connections
      return { connections }
    },
    url() {
      let connectionId
      if (this.connectionId) {
        connectionId = this.connectionId
      } else {
        connectionId = this.connection.id
      }
      console.log(`importing for connection: ` + connectionId)
      return `${process.env.BASE_API}/app/import?connectionId=${connectionId}&mode=${this.mode}&objectType=${this.objectType}`
    },
    tip() {
      switch (this.objectType) {
        case 'SCEN': return 'Import scenarios'
      }
      return 'Import ODI object'
    }
  },
  methods: {
    result(s) {
      let status = 'info'
      let message = 'Running ...'
      switch (this.multiDeploy.status.toUpperCase()) {
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
    },
    isImportDisabled(fileList) {
      if (_.isEmpty(fileList)) {
        this.importDisabled = true
      } else if (this.multiConnection && _.isEmpty(this.multiDeploy.connections)) {
        this.importDisabled = true
      } else {
        this.importDisabled = false
      }
    },
    onRemove(f, fileList) {
      this.isImportDisabled(fileList)
    },
    onChange(f, fileList) {
      this.isImportDisabled(fileList)
    },
    onError(response) {
      console.log(response)
      this.loading = false
      if (_.isEmpty(this.multiDeploy.connections)) {
        Message.error('Import failed')
        return
      }
      const record = _.find(this.multiDeploy.statuses, s => s.status.toLowerCase() === 'running')
      record.status = 'error'
      this.multiDeploy.statuses = _.cloneDeep(this.multiDeploy.statuses)
      this.importNext()
    },
    onSuccess(response) {
      console.log(response)
      this.loading = false
      if (_.isEmpty(this.multiDeploy.connections)) {
        switch (response.status) {
          case 'error': Message.error('Import failed'); break
          case 'success': Message.success('Import success'); break
        }
        return
      }
      this.multiDeploy.responses.push(response)
      const { connectionId, status } = response
      const record = _.find(this.multiDeploy.statuses, s => s.connection.id === connectionId)
      record.status = status
      this.multiDeploy.statuses = _.cloneDeep(this.multiDeploy.statuses)
      this.importNext()
    },

    onPreview() {},
    initMultiDeploy() {
      this.multiDeploy.status = 'RUNNING'
      this.multiDeploy.responses = []

      this.multiDeploy.statuses = _.map(this.multiDeploy.connections, id => {
        const status = 'waiting'
        const connection = _.chain(this.connections).find({ id }).cloneDeep().value()
        return { connection, status }
      })
    },
    importNext() {
      console.log('Last state:')
      console.log(this.multiDeploy.statuses)
      for (let i = 0; i < this.multiDeploy.connections.length; i++) {
        const connectionId = this.multiDeploy.connections[i]
        const record = _.find(this.multiDeploy.statuses, s => (s.connection.id === connectionId && s.status.toLowerCase() === 'waiting'))
        if (!record) {
          continue
        } else {
          record.status = 'running'
          this.connectionId = connectionId
          setTimeout(() => {
            console.log('Calling upload for connection: ' + connectionId)
            console.log(this.$refs.upload)
            const upload = this.$refs.upload
            _.each(upload.uploadFiles, f => { f.status = 'ready' })
            console.log(upload.uploadFiles)
            upload.submit()
          }, 200)
          break
        }
      }
    },
    onImport() {
      this.loading = true

      const upload = this.$refs.upload
      _.each(upload.uploadFiles, f => { f.status = 'ready' })

      if (_.isEmpty(this.multiDeploy.connections)) {
        this.$refs.upload.submit()
      } else {
        this.initMultiDeploy()
        this.multiDeploy.dialogVisible = true
        this.importNext()
      }
    },
    onClose() {
      this.$emit('close')
    }
  }

}
</script>

<style>

.upload,
.el-upload,
.el-upload-dragger {
  width: 100%;
}
.upload-left-area {
  border-right: 1px solid #E0E0E0;
  height: 100%;
}

.row,
.container {
  height: 100%;
}

</style>
