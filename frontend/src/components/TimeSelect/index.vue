<template lang="pug">
  el-dialog.err-dialog(
    title="Select time limit" :visible="visible"
    width="400px",
    @close="onClose"
  )
    el-select(v-model='value' placeholder='Pick an option' style="width:100%")
      el-option(v-for='item in list' :key='item.value' :label='item.name' :value='item.value')
    span.dialog-footer(slot='footer')
      el-button(@click='onClose') Cancel
      el-button(type='primary' @click='onOk') Ok

</template>

<script>

export default {
  name: 'timeSelect',
  props: {
    visible: { type: Boolean, default: false },
    init: { type: String, default: undefined }
  },
  data() {
    return {
      editor: false,
      value: undefined,
      list: [
        { name: 'Last hour', value: 'LAST_HOUR' },
        { name: 'Last three hours', value: 'LAST_THREE_HOURS' },
        { name: 'Last 24 hours', value: 'LAST_24_HOURS' },
        { name: 'Today', value: 'TODAY' }
      ]
    }
  },
  watch: {
    'init'() { this.value = this.init }
  },
  computed: {
  },
  methods: {
    onClose() {
      this.$emit('close')
    },
    onOk() {
      this.$emit('select', this.value)
      this.onClose()
    }
  }
}
</script>

<style scoped>

</style>
