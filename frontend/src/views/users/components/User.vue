<template lang="pug">
el-row.container(justify="center" v-loading="loading")
  el-col(:span='12', :push='6')
    el-alert(v-if="form.system" title="System user" type="warning" show-icon :closable="false" description="System user can not be deleted")
    el-form(ref='form', :model='form', label-width='120px', :rules="rules" style="margin-top:20px;")
      el-form-item(prop="username" :label="$t('user.username')")
        el-input(v-model='form.username', style='width: 100%;')
      el-form-item(prop="name" :label="$t('user.name')")
        el-input(v-model='form.name', style='width: 100%;', placeholder='Name of the user eg. William Bonney')
      el-form-item(prop="email" :label="$t('user.email')")
        el-input(v-model='form.email', style='width: 100%;' placeholder='@yourdomain.com')
      el-form-item(v-if="!form.system" prop="role" :label="$t('user.role')")
        el-select(v-model='form.role' placeholder='Select role')
          el-option(v-for='item in roles' :key='item.value' :label='item.label' :value='item.value')
      el-row(type="flex" class="row-bg" justify="end")
        el-button(type='primary', @click='onSave') Save
        el-button(type='danger' plain @click='onClose') Close

</template>

<script>
  import _ from 'lodash'
  import { mapGetters, mapActions } from 'vuex'

  export default {
    props: {
      id: {
        type: Number,
        required: false
      }
    },
    data() {
      return {
        loading: false,
        roles: [
          { value: 'MASTER', label: 'MASTER' },
          { value: 'OPERATOR', label: 'OPERATOR' },
          { value: 'GUEST', label: 'GUEST' }
        ],
        form: {
          username: undefined,
          name: undefined,
          email: undefined,
          role: 'GUEST',
          system: false
        },
        rules: {
          username: [{ required: true, trigger: 'blur', message: 'Username is required' }],
          name: [{ required: true, trigger: 'blur', message: 'Name is required' }],
          email: [
            { required: true, trigger: 'blur', message: 'Email is required' },
            { type: 'email', message: 'Please input correct email address', trigger: ['blur', 'change'] }]
        },
        options: []
      }
    },
    computed: {
      ...mapGetters('users', [
        'all'
      ])
    },
    methods: {
      ...mapActions('users', [
        'create',
        'update'
      ]),
      onSave() {
        let response
        this.loading = true
        if (this.id) {
          response = this.update(this.form)
        } else {
          response = this.create(this.form)
        }
        response
          .then(_ => { this.loading = false; this.$emit('close') })
          .catch(_ => { this.loading = false })
      },
      onClose() {
        this.$emit('close')
      },
      init() {
        if (this.id) {
          this.form = _.chain(this.all).find({ id: this.id }).cloneDeep().value()
        }
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
