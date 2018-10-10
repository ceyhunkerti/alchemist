<template lang="pug">
el-row.container(justify="center" v-loading="loading" style="margin-bottom:30px;")
  el-col(:span='12', :push='6')
    el-form(ref='form', :model='form', label-width='120px', :rules="rules")
      el-collapse(v-model='activeNames')
        el-collapse-item(:title="$t('connection.odi.title')" name='1')
          el-form-item(prop="name" :label="$t('connection.odi.loginName')")
            el-input(v-model='form.name', style='width: 100%;', placeholder='Name of the connection')
          el-form-item(prop="odiUsername" :label="$t('connection.odi.user')")
            el-input(v-model='form.odiUsername', style='width: 100%;', placeholder='ODI username eg. SUPERVISOR')
          el-form-item(prop="odiPassword" :label="$t('connection.odi.password')")
            el-input(type='password' v-model='form.odiPassword', style='width: 100%;', placeholder='ODI password')
        el-collapse-item(:title="$t('connection.db.title')" name='2')
          el-form-item(prop="masterUsername" :label="$t('connection.db.user')")
            el-input(v-model='form.masterUsername', style='width: 100%;', placeholder='DB username eg. PROD_ODI_REPO')
          el-form-item(prop="masterPassword" :label="$t('connection.db.password')")
            el-input(type='password' v-model='form.masterPassword', style='width: 100%;', placeholder='DB password')
          el-form-item(prop="url" label="URL")
            el-input(v-model='form.url', style='width: 100%;', placeholder='JDBC URL eg. jdbc:oracle:thin:@localhost:1521/orcl')
        el-collapse-item(:title="$t('connection.repo.title')" name='3')
          el-form-item
            el-radio-group(v-model='repo' size='medium')
              el-radio-button(label='m') Master Repository Only
              el-radio-button(label='w') Work Repository
          el-form-item(v-if="repo==='w'")
            el-row(type="flex")
              el-col(:span="20")
                el-select(v-model='form.workRepo' placeholder='Select' style="width:100%")
                  el-option(v-for='item in options' :key='item.name' :label='item.name' :value='item.name')
              el-col(:span="2" :push='1')
                el-button(@click="onFindWorkRepos" icon="el-icon-refresh" circle size="mini")
      el-row(type="flex" style="margin-top:40px;")
        el-col
          el-row(type="flex" class="row-bg" justify="start")
            el-checkbox(v-model="form.isDefault" label="Default Connection" border)
        el-col
          el-row(type="flex" class="row-bg" justify="end")
            el-button(type='primary', @click='onSave') Save
            el-button(type='success' plain @click='onTest') Test
            el-button(type='danger' plain @click='onClose') Close

</template>

<script>
  import _ from 'lodash'
  import { mapGetters, mapActions } from 'vuex'
  import request from '@/utils/request'

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
        repo: 'm',
        activeNames: ['1'],
        form: {
          isDefault: true,
          name: undefined,
          odiUsername: undefined,
          odiPassword: undefined,
          workRepo: undefined,
          masterUsername: undefined,
          masterPassword: undefined,
          url: undefined
        },
        rules: {
          name: [{ required: true, trigger: 'blur', message: 'Login name is required' }],
          odiUsername: [{ required: true, trigger: 'blur', message: 'ODI user is required' }],
          odiPassword: [{ required: true, trigger: 'blur', message: 'ODI password is required' }],
          masterUsername: [{ required: true, trigger: 'blur', message: 'DB user is required' }],
          masterPassword: [{ required: true, trigger: 'blur', message: 'DB password is required' }],
          url: [{ required: true, trigger: 'blur', message: 'JDBC URL is required' }]
        },
        options: []
      }
    },
    computed: {
      ...mapGetters('connection', [
        'all'
      ])
    },
    methods: {
      ...mapActions('connection', [
        'create',
        'update',
        'test'
      ]),
      onFindWorkRepos() {
        this.loading = true
        request.post('/connections/work-repos', this.form).then(response => {
          this.options = response.data
          this.loading = false
        }).catch(_ => { this.loading = false })
      },
      onSave() {
        let response
        this.loading = true
        if (this.id) {
          response = this.update(this.form)
        } else {
          response = this.create(this.form)
        }
        response
          .then(_ => { this.loading = false })
          .catch(_ => { this.loading = false })
      },
      onTest() {
        this.loading = true
        this.test(this.form)
          .then(_ => { this.loading = false })
          .catch(_ => { this.loading = false })
      },
      onClose() {
        this.$emit('closeConnection')
      },
      init() {
        if (this.id) {
          this.form = _.chain(this.all).find({ id: this.id }).cloneDeep().value()
          this.repo = _.isEmpty(this.form.workRepo) ? 'm' : 'w'
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
