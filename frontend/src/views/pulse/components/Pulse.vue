<template lang="pug">
el-row.container(justify="center" v-loading="loading")
  el-col(:span='12', :push='6')
    el-alert(v-if="form.system" title="System user" type="warning" show-icon :closable="false" description="System user can not be deleted")
    el-form(ref='form', :model='form', label-width='120px', :rules="rules" style="margin-top:20px;")
      el-form-item(label='Active')
        el-switch(v-model='form.active')
      el-form-item(prop="name" label="Name")
        el-input(v-model='form.name', style='width: 100%;' :disabled="!form.active")
      el-form-item(prop="pulseType" label="Type")
        el-select(v-model='form.pulseType' placeholder='Select' style="width:100%" :disabled="!form.active")
          el-option(v-for='item in pulseTypes' :key='item.value' :label='item.label' :value='item.value' :disabled="item.disabled")
      el-form-item(prop="loadPlans" label="Load plans")
        el-select(v-model='form.loadPlans' multiple='' placeholder='Select load plan ...' style="width:100%" :disabled="!form.active")
          el-option(v-for='item in plans' :key='item.internalId' :label='item.name' :value='item.internalId')
      el-form-item(prop="mail" label="Mail")
        el-input(v-model='form.mail', style='width: 100%;' :disabled="!form.active")
      el-row(type="flex" class="row-bg" justify="end")
        el-button(type='primary', @click='onSave') Save
        el-button(type='danger' plain @click='onClose') Close

</template>

<script>
  import _ from 'lodash'
  import { mapGetters, mapActions } from 'vuex'

  export default {
    props: {
      data: { type: Object, required: false, default: () => {} }
    },
    data() {
      return {
        loading: false,
        plans: [],
        pulseTypes: [
          { value: 'LOAD_PLAN_ERROR', label: 'Load plan error', disabled: false },
          { value: 'LOAD_PLAN_DURATION', label: 'Load plan duration', disabled: true },
          { value: 'AGENT_DOWN', label: 'Agent down', disabled: true }
        ],
        form: {
          id: undefined,
          active: true,
          username: name,
          pulseType: 'LOAD_PLAN_ERROR',
          loadPlans: []
        },
        rules: {
          name: [{ required: true, trigger: 'blur', message: 'Name is required' }],
          pulseType: [{ required: true, trigger: 'blur', message: 'Pulse type is required' }],
          loadPlans: [{ required: true, trigger: 'blur', message: 'Load plan is required' }]
        },
        options: []
      }
    },
    computed: {
      ...mapGetters(['connection']),
      ...mapGetters('loadPlan', {
        'loadPlans': 'all'
      })
    },
    methods: {
      ...mapActions('pulse', {
        'createPulse': 'create',
        'updatePulse': 'update'
      }),
      ...mapActions('loadPlan', {
        'findAllLoadPlans': 'findAll'
      }),
      create() {
        const connectionId = this.connection.id
        const active = this.form.active
        const name = this.form.name
        const pulseType = this.form.pulseType
        let content
        switch (pulseType) {
          case 'LOAD_PLAN_ERROR':
            content = JSON.stringify(this.form.loadPlans)
            break
        }
        const data = { active, name, pulseType, content }
        this.loading = true
        this.createPulse({ connectionId, data }).then(_ => {
          this.loading = false
          this.$emit('close')
        }, _ => { this.loading = false })
      },
      update() {
        const connectionId = this.connection.id
        const active = this.form.active
        const name = this.form.name
        const pulseType = this.form.pulseType
        let content
        switch (pulseType) {
          case 'LOAD_PLAN_ERROR':
            content = JSON.stringify(this.form.loadPlans)
            break
        }
        const data = { active, name, pulseType, content }
        this.loading = true
        this.updatePulse({ connectionId, data }).then(_ => {
          this.loading = false
          this.$emit('close')
        }, _ => { this.loading = false })
      },
      onSave() {
        this.loading = true
        if (this.form.id) {
          this.update()
        } else {
          this.create()
        }
      },
      onClose() {
        this.$emit('close')
      },
      init() {
        if (!_.isEmpty(this.data)) {
          const loadPlans = JSON.parse(this.data.content)
          this.form = { ...this.data, loadPlans }
        }
        const connectionId = this.connection.id
        this.loading = true
        this.findAllLoadPlans({ connectionId })
          .then(plans => {
            this.loading = false
            this.plans = plans
          })
          .catch(_ => { this.loading = false })
      }
    },
    mounted() {
      this.init()
    }
  }
</script>

<style rel="stylesheet/css"  scoped>
  .container {
    padding: 40px 0;
  }

  .el-checkbox-group {
    width: 320px;
  }

</style>
