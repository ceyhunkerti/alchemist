<template lang="pug">
el-row.mailSettings-container(justify="center" v-loading="loading")
  el-col(:span='12', :push='6')
    el-form(ref='form', :model='form', label-width='120px')
      el-form-item(label='Active')
        el-switch(v-model='form.active')
      el-row
        el-col(:span='14')
          el-form-item(label='Host')
            el-input(v-model='form.host', style='width: 100%;', :disabled="!form.active")
        el-col(:span='10')
          el-form-item(label="Port")
            el-input-number(v-model='form.port', style='width: 100%;', :disabled="!form.active")
      el-form-item(label='Security')
        el-radio-group(v-model='form.connectionSecurity', :disabled="!form.active")
          el-radio-button(label='SSL')
          el-radio-button(label='TLS')
          el-radio-button(label='None')
      el-form-item(label='Username')
        el-input(v-model='form.username', style='width: 100%;', :disabled="!form.active")
      el-form-item(label='Password')
        el-input(type="password" v-model='form.password', style='width: 100%;', :disabled="!form.active")
      el-form-item(label='Send from')
        el-input(v-model='form.from', style='width: 100%;', placeholder="sakura@primeit.com.tr", :disabled="!form.active")
      el-form-item(label='Send to')
        el-input(v-model='form.to', style='width: 100%;', placeholder="your.mail@primeit.com.tr", :disabled="!form.active")
    el-row(type="flex" class="row-bg" justify="end")
      el-button(type='primary', @click='onSave') Save
      el-button(type='success' plain @click='onTest') Test
      el-button(type='danger' plain @click='onClose') Close

</template>

<script>
  import _ from 'lodash'
  import { Message } from 'element-ui'
  import { mapActions } from 'vuex'

  export default {
    data() {
      return {
        loading: false,
        form: {
          active: true,
          host: '',
          port: 465,
          username: '',
          password: '',
          connectionSecurity: 'SSL',
          from: undefined,
          to: undefined
        }
      }
    },
    computed: {
    },
    methods: {
      ...mapActions('settings', [
        'save',
        'testMail',
        'findByName'
      ]),
      onSave() {
        const s = {
          name: 'MAIL',
          value: JSON.stringify(this.form)
        }
        this.save(s)
      },
      onClose() {
        window.history.back()
      },
      onTest() {
        this.loading = true
        this.testMail(this.form).then(response => {
          this.loading = false
          Message.success('Test mail sent')
        }, error => {
          this.loading = false
          console.log(error)
          Message.error('Unable to test mail server')
        })
      },
      init() {
        this.loading = true
        const name = 'MAIL'
        this.findByName({ name }).then(response => {
          this.loading = false
          const data = JSON.parse(response.data.value)
          if (!_.isEmpty(data)) { this.form = data }
        }, _ => {
          this.loading = false
          Message.error('Failed find mail settings')
        })
      }
    },
    mounted() {
      this.init()
    }
  }
</script>

<style rel="stylesheet/css"  scoped>
  .mailSettings-container {
    padding: 40px 0;
  }

  .el-checkbox-group {
    width: 320px;
  }

</style>
