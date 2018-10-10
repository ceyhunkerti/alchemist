<template lang="pug">
.row
  executables(
    v-if="visible === 'EXECUTABLES'"
    :loading="loading"
    :executables="executables"
    @itemDetail="onExecutableDetail"
    @reload="init"
    @import="(objectType) => onUpload(objectType)"
  )
  load-plan(
    v-if="visible === 'LOAD_PLAN'"
    :name="executableName"
    :loading="loading"
    :loadPlan="loadPlan"
    @reload="init"
    @close="visible='EXECUTABLES'"
  )
  upload(
    v-if="visible === 'UPLOAD'"
    :objectType="upload.objectType"
    @close="visible='EXECUTABLES'"
  )

</template>

<script>

import _ from 'lodash'
import { mapGetters, mapActions } from 'vuex'
import Executables from './components/Executables'
import LoadPlan from './components/LoadPlan'
import Upload from './components/Upload'

export default {
  components: {
    Executables,
    LoadPlan,
    Upload
  },
  data() {
    return {
      executableName: undefined,
      visible: 'EXECUTABLES',
      executables: [],
      loading: false,
      loadPlan: {},
      upload: {
        objectType: 'SCEN'
      }
    }
  },
  computed: {
    ...mapGetters(['connection'])
  },
  methods: {
    ...mapActions('executable', [
      'findAll'
    ]),
    ...mapActions('loadPlan', [
      'findById'
    ]),
    init() {
      const connectionId = this.connection.id
      if (!connectionId) { return }
      this.loading = true
      this.findAll({ connectionId }).then(response => {
        if (!response.data) return
        const setNode = (node) => {
          const loadPlans = _.map(node.loadPlans, lp => {
            lp.type = 'LOAD_PLAN'
            lp.key = `${lp.type}-${lp.internalId}`
            return lp
          })
          const scenarios = _.map(node.scenarios, s => {
            s.type = 'SCENARIO'
            s.key = `${s.type}-${s.internalId}`
            return s
          })
          const subFolders = _.map(node.subFolders, f => {
            f.type = 'SCEN_FOLDER'
            f.key = `${f.type}-${f.internalId}`
            return f
          })
          const children = _.chain(loadPlans).concat(scenarios, subFolders).flatten().value()

          // delete node.loadPlans
          // delete node.scenarios
          // delete node.subFolders

          node.children = children
          _.chain(node.children).filter({ type: 'SCEN_FOLDER' }).each(child => {
            setNode(child)
          }).value()
          return node
        }

        const subFolders = response.data.folders
        const scenarios = response.data.scenarios
        const loadPlans = response.data.loadPlans
        const root = _.cloneDeep({ name: 'root', type: 'SCEN_FOLDER', subFolders, scenarios, loadPlans, children: [] })
        this.executables = _.orderBy(setNode(root).children, c => {
          switch (c.type) {
            case 'SCEN_FOLDER': return 1
            case 'LOAD_PLAN': return 2
            default: return 99
          }
        }, ['asc'])
        this.loading = false
      }).catch(_ => { this.loading = false })
    },
    onExecutableDetail(payload) {
      if (!this.connection) { return }
      const { internalId } = payload
      const { id } = this.connection
      this.loading = true
      this.findById({ id, internalId }).then(response => {
        this.executableName = response.data.name
        this.loadPlan = response.data
        this.loading = false
        this.visible = 'LOAD_PLAN'
      }).catch(_ => (this.loading = false))
    },
    onUpload(objectType) {
      this.visible = 'UPLOAD'
      this.upload.objectType = objectType
    }
  },
  mounted() {
    this.init()
  }

}
</script>
