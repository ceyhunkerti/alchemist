<template lang="pug">
el-row(style="padding-bottom:20px;")
  el-col(:span='12', :push='6' v-loading="loading" style="margin-bottom:40px;")
    el-card(v-for="m in all", :key="m.id" shadow="hover" style="margin-bottom:20px;")
      .clearfix(slot='header')
        span {{m.name}}
        el-button(@click="onEdit(m.id)", circle=true, icon="el-icon-edit", style='float: right; margin-left:5px;')
        el-button(:disabled="m.system" @click="onDelete(m)" circle=true, icon="el-icon-delete", style='float: right;', type='danger', plain)
      .bottom.clearfix
        span.mail(@click="(e) => onClipboard(m.email, e)") {{ m.email }}
        span.username(type='text') {{m.username}}
      div(style="margin-top:10px;")
        el-tag(v-if="m.system" type="warning" style="margin-right:10px;" title="System user") SYSTEM
        el-tag(style="margin-right:10px;" title="Role") {{m.role}}
    el-card(shadow="never" style="margin-top:20px;")
      el-button(type="success" plain style="width:100%;" @click="onCreate") Create user


</template>

<script>

import clipboard from '@/utils/clipboard'
import { mapGetters, mapActions } from 'vuex'

export default {
  name: 'connections',
  components: { },
  data() {
    return {
      loading: false
    }
  },
  computed: {
    ...mapGetters('users', [
      'all'
    ])
  },
  methods: {
    ...mapActions('users', [
      'remove'
    ]),
    onClipboard(text, e) {
      clipboard(text, e)
    },
    onCreate() {
      this.$emit('create')
    },
    onEdit(id) {
      this.$emit('edit', id)
    },
    onDelete(item) {
      this.$confirm(`This will permanently delete ${item.name}. Continue?`, 'Warning', {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(() => {
        const id = item.id
        this.remove(id)
      })
    }
  }
}
</script>

<style scoped>
.mail {
  font-size: 15px;
  color: #999;
}

.username {
  padding: 0;
  float: right;
  color: #999;
}

</style>

