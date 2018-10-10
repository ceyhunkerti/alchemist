<template lang="pug">
el-row(style="padding-bottom:20px;")
  el-col(:span='12', :push='6' v-loading="loading" style="margin-bottom:40px;")
    el-card(v-for="m in all", :key="m.id" shadow="hover" style="margin-bottom:20px;")
      .clearfix(slot='header')
        span {{m.name}}
        i.el-icon-star-on(v-if="m.isDefault" style="color:lightpink;margin-left:10px;")
        el-button(@click="onTest(m)", circle=true, icon="el-icon-refresh", style='float: right; margin-left:5px;')
        el-button(@click="onEdit(m.id)", circle=true, icon="el-icon-edit", style='float: right; margin-left:5px;')
        el-button(@click="onDelete(m)" circle=true, icon="el-icon-delete", style='float: right;', type='danger', plain)
      .bottom.clearfix
        span.url(@click="onUrlClick") {{ m.url }}
        span.username(type='text') {{m.odiUsername}}
    el-card(shadow="never" style="margin-top:20px;")
      el-button(type="success" plain style="width:100%;" @click="onCreateConnection") Create connection


</template>

<script>

import { mapGetters, mapActions } from 'vuex'
import { Message } from 'element-ui'

export default {
  name: 'connections',
  components: { },
  data() {
    return {
      loading: false
    }
  },
  computed: {
    ...mapGetters('connection', [
      'all'
    ])
  },
  methods: {
    ...mapActions('connection', [
      'test',
      'remove'
    ]),
    onTest(connection) {
      this.loading = true
      this.test(connection)
        .then(_ => { this.loading = false })
        .catch(_ => { this.loading = false })
    },
    onUrlClick(e) {
      const text = e.target.innerText
      const handler = (e) => {
        e.clipboardData.setData('text/plain', text)
        e.preventDefault()
        document.removeEventListener('copy', handler, true)
        Message.success('Url copied to clipboard')
      }
      document.addEventListener('copy', handler, true)
      document.execCommand('copy')
    },
    onCreateConnection() {
      this.$emit('createConnection')
    },
    onEdit(id) {
      this.$emit('editConnection', id)
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
.url {
    font-size: 15px;
    color: #999;
}

.username {
  padding: 0;
  float: right;
  color: #999;
}

</style>

