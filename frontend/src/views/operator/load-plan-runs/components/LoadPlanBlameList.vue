<template lang="pug">
el-table(:data='list'
  @current-change="handleTableSelect"
  style='width: 100%;padding-top: 15px;'
  max-height="450"
  height="417"
  fit,
  :row-class-name="rowClassName"
)
  el-table-column(type="index" width="40")
  el-table-column(label='Name' min-width='200', prop="name")
  el-table-column(label='Errors' max-width='30' align="center" prop="count")
</template>

<script>
import _ from 'lodash'

export default {
  props: {
    blame: { type: Array, default: () => [] }
  },
  data() {
    return {
      list: []
    }
  },
  created() {
  },
  watch: {
    'blame'() {
      this.loadList()
    }
  },
  methods: {
    rowClassName({ row }) {
      const isTop = !_.chain(this.list).take(3).filter(r => r.name === row.name).isEmpty().value()
      return isTop ? 'danger' : ''
    },
    loadList() {
      const sessions = _.chain(this.blame).groupBy('name').value()
      this.list = _.chain(sessions).keys().map(k => {
        const name = k
        const count = sessions[k].length
        return { name, count }
      }).sortBy(s => s.count).reverse().take(10).value()
    },
    handleTableSelect() {}
  }
}
</script>

<style>
.el-table .danger {
  background: #FEF0F0 !important;
}


</style>
