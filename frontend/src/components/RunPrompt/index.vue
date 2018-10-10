<template lang="pug">
el-dialog(
  @close="onClose"
  @open="onOpen"
  :visible="visible"
  :close-on-click-modal="false"
  width="600px"
  :title="header"
)
  el-form(v-loading="running" ref='options', :model='options', label-width='100px', :rules="rules")
    el-form-item(prop="context" label="Context")
      el-select(v-model='options.contextCode' placeholder='Select' style="width:100%;")
        el-option(v-for='item in contexts' :key='item.code' :label='item.name' :value='item.code')
    el-form-item(prop="agent" label="Agent")
      el-select(v-model='options.agentId' placeholder='Select' style="width:100%;")
        el-option(v-if="showLoacalAgent" key='-1' label='Local Agent' value='-1')
        el-option(v-for='item in agents' :key='item.internalId' :label='item.name' :value='item.internalId')
    el-row(type="flex" class="row-bg")
      el-button(type='primary', @click='onRun' style="width:100%;") Run
  el-row(type="flex" class="row-bg")
    el-progress(v-if="running" :show-text="false" :percentage="progress.percentage" :color="progress.color" style="width:100%;margin-top:10px;")
</template>

<script>
// import { Message } from 'element-ui'
import { mapActions, mapGetters } from 'vuex'

export default {
  props: {
    objectType: { type: String, default: 'SCENARIO' },
    running: { type: Boolean, default: false },
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      loading: false,
      progress: {
        inteval: undefined,
        percentage: 10,
        color: '#F56C6C'
      },
      header: '',
      options: {
        contextCode: undefined,
        agentId: undefined
      },
      rules: {
        name: [{ required: true, trigger: 'blur', message: 'Name is required' }]
      }
    }
  },
  watch: {
    running() {
      if (!this.running) {
        if (this.progress.inteval) {
          clearInterval(this.progress.inteval)
        }
        return
      }
      this.progress.inteval = setInterval(() => {
        if (this.progress.percentage !== 99) {
          this.progress.percentage += 1
        }
      }, 100)
    }
  },
  computed: {
    ...mapGetters(['connection']),
    ...mapGetters('context', {
      'contexts': 'all'
    }),
    ...mapGetters('agent', {
      'agents': 'all'
    }),
    showLoacalAgent() {
      switch (this.objectType) {
        case 'SCENARIO': return true
      }
      return false
    }
  },
  methods: {
    ...mapActions('common', [
      'exp'
    ]),
    onClose() {
      this.$emit('close')
    },
    onOpen() {
      this.progress.percentage = 10
    },
    onRun() {
      this.$emit('run', this.options)
    }
  }

}
</script>
