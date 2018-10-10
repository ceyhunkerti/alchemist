<template lang="pug">
el-row.mailSettings-container(justify="center")
  el-col(:span='12', :push='6')
    el-container
      el-header Coming Soon
    //- el-tabs(v-model='activeName', @tab-click='handleClick')
    //-   el-tab-pane(label='Mail', name='mail')
    //-     el-form(ref='notifications', :model='notifications', label-width='120px')
    //-       el-form-item(label='Blocking')
    //-         el-switch(v-model='notifications.mail.bloks', active-text='Send mail for blocking sessions')

    //-   el-tab-pane(label='Desktop', name='desktop')
    //-     el-form(ref='notifications', :model='notifications', label-width='120px')
    //-       el-form-item(label='Blocking')
    //-         el-switch(v-model='notifications.desktop.bloks', active-text='Show desktop notifications for blocking sessions')
    //-       el-form-item(label='Sound')
    //-         el-switch(v-model='notifications.desktop.sound', active-text='Play sound for notifications')
    //-   el-tab-pane(label='SMS/WhatsApp', name='sms') Prime Content
    //-   el-tab-pane(label='Slack', name='slack') Prime Content
    //-   el-tab-pane(label='Discord', name='discord') Coming soon ...


</template>

<script>
  import _ from 'lodash'
  import { mapGetters, mapActions } from 'vuex'

  export default {
    data() {
      return {
        activeName: 'mail',
        notifications: {
          mail: {
            blocks: false
          },
          desktop: {
            blocks: false,
            sound: false
          }
        }
      }
    },
    computed: {
      ...mapGetters('settings', [
        'mail',
        'all'
      ])
    },
    methods: {
      ...mapActions('settings', [
        'save'
      ]),
      onSave() {
        const s = {
          name: 'MAIL',
          value: JSON.stringify(this.form)
        }
        this.save(s)
      },
      init() {
        const m = _.cloneDeep(this.mail)
        if (m) {
          this.form = m
        }
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

  .el-header, .el-footer {
    background-color: #EAE9E1;
    color: #908A82;
    text-align: center;
    line-height: 60px;
    width: 100%;
    border-radius: 5px;
  }

</style>
