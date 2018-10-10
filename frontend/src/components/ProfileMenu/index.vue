<template lang="pug">
el-popover(placement="bottom" width="350" trigger="click", v-model="popOver")
  el-card.box-card(shadow="never")
    .clearfix(slot="header")
      span {{me.name}}
      router-link(to="/options/index")
        el-button(@click="popOver=false", style="float: right; padding: 3px 0" type="text") Options
    .bottom.clearfix
      div
        span.username {{ me.username }}
      div
        span.email(type='text') {{me.email}}
    .signOut
      el-button(@click="logout" type="danger", plain, style="width:100%") Sign out
  div.userIcon-container(slot="reference")
    svg-icon(class-name='user-icon', icon-class='user' :style="me.system ? 'color:#f4516c;' : ''")
</template>

<script>

import { mapGetters } from 'vuex'

export default {
  name: 'profileMenu',
  data() {
    return {
      popOver: false
    }
  },
  computed: {
    ...mapGetters(['me'])
  },
  methods: {
    handleSetConnection(con) {
    },
    logout() {
      this.$store.dispatch('LogOut').then(() => {
        location.reload() // In order to re-instantiate the vue-router object to avoid bugs
      })
    }
  },
  mounted() {
  }
}
</script>

<style scoped>
.userIcon-container {
  height: 20px;
}
.user-icon {
  color: #606266;
  font-size: 20px;
  cursor: pointer;
  vertical-align: 10px;
}
.username {
  padding: 0;
  color: #999;
}
.email {
  padding: 0;
  color: #999;
  /* float: right; */
}
.signOut {
  margin-top: 20px;
}
</style>


