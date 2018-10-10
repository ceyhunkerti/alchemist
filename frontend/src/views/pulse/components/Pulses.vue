<template lang="pug">
el-row(style="padding-bottom:20px;")
  el-col(:span='12', :push='6' v-loading="loading" style="margin-bottom:40px;")
    el-card(v-for="item in pulses", :key="item.id" shadow="hover" style="margin-bottom:20px;")
      .clearfix(slot='header')
        span {{item.name}}
        el-button(@click="onEdit(item)", circle=true, icon="el-icon-edit", style='float: right; margin-left:5px;')
        el-button(@click="onDelete(item)" circle=true, icon="el-icon-delete", style='float: right;', type='danger', plain)
      .bottom.clearfix
        el-row
          el-col(:span='12')
            .left
              span.url(type='text' style="margin-top:5px;") {{item._pulseType}}
          el-col(:span='12')
            el-switch.active(v-model="item.active" @change="(v) => { onPusleActiveChange(item.id, v) }")
    el-card(shadow="never" style="margin-top:20px;")
      el-button(type="success" plain style="width:100%;" @click="onCreate") Create pulse


</template>

<script>

import { mapActions, mapGetters } from 'vuex'
// import { Message } from 'element-ui'

export default {
  name: 'connections',
  components: { },
  data() {
    return {
      loading: false
    }
  },
  mounted() {
    this.init()
  },
  computed: {
    ...mapGetters(['connection']),
    ...mapGetters('pulse', [
      'pulses'
    ])
  },
  methods: {
    ...mapActions('pulse', [
      'setActive',
      'remove'
    ]),
    onPusleActiveChange(id, active) {
      this.loading = true
      this.setActive({ id, active })
        .then(_ => { this.loading = false })
        .catch(_ => { this.loading = false })
    },
    init() {},
    onCreate() {
      this.$emit('create')
    },
    onEdit(item) {
      this.$emit('edit', item)
    },
    onDelete(item) {
      this.$confirm(`This will permanently delete ${item.name}. Continue?`, 'Warning', {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(() => {
        const { id } = item
        this.mask = true
        this.remove({ id })
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

.active {
  float: right;
  margin-left: 10px;
}

.left span {
  display: block;
}

</style>

