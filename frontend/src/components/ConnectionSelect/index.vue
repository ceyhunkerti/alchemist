<template lang="pug">
el-dropdown(trigger='click', @command='handleSelect')
  div
    svg-icon(class-name='connection-icon', icon-class='plug')
  el-dropdown-menu(slot='dropdown')
    el-dropdown-item(v-for="m in all" :command='m.id' :key="m.id") {{m.name}}
    router-link(to='/connections/index')
      el-dropdown-item(divided style="color:#F9A825 !important;") Create connection
</template>

<script>

import _ from 'lodash'
import { mapGetters } from 'vuex'

export default {
  name: 'connectionSelect',
  computed: {
    ...mapGetters('connection', [
      'all'
    ])
  },
  methods: {
    handleSelect(id) {
      const connection = _.find(this.all, { id })
      if (connection) {
        this.$store.dispatch('reset')
        setTimeout(_ => {
          this.$store.dispatch('setConnection', connection)
          this.$router.push('/dashboard')
        }, 10)
      }
    }
  }
}
</script>

<style scoped>
.connection-icon {
  font-size: 20px;
  cursor: pointer;
  vertical-align: 10px;
}
</style>


