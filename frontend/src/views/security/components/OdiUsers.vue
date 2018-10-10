<template lang="pug">
el-row(style="margin-top:20px;")
  el-col(:span='16', :push='4' v-loading="loading" style="margin-bottom:40px;")
    el-card.box-card.tree-card
      .clearfix(slot="header")
        span(style="margin:auto;") {{header}}
        el-button(:disabled="loading" @click="onReload" icon="el-icon-refresh" title="Refresh" circle style="float:right;margin-left:10px;")
        el-button(:disabled="loading" type="success" plain @click="onAddUser" icon="el-icon-plus" title="Add user" circle style="float:right;margin-left:10px;")
        el-button(:disabled="cloneDisabled" type="primary" plain @click="onClone" title="Duplicate" circle style="float:right;margin-left:10px;")
          svg-icon(icon-class='duplicate')
        el-input(:disabled="loading" placeholder='Search' v-model='filterText' style="width:400px;float:right;padding:3px 0")
      el-table(
        ref="odiUsers"
        :data='filteredUsers'
        style='width: 100%'
        highlight-current-row
        @current-change="onCurrentChange"
      )
        el-table-column(label='Name')
          template(slot-scope='scope')
            span(style='margin-left: 10px') {{ scope.row.name }}
        el-table-column(align="right")
          template(slot-scope='scope')
            el-tag(v-if="scope.row.isSupervisor" type="warning") SUPERVISOR
        el-table-column(align="right")
          template(slot-scope='scope')
            el-button.actions(size='mini' @click='onChangePassword(scope.$index, scope.row)') Change password
            // el-button.actions(size='mini' @click='onRename(scope.$index, scope.row)') Rename
            el-button.actions(size='mini' type='danger' @click='onDelete(scope.$index, scope.row)') Delete
</template>

<script>

import _ from 'lodash'
import { Message } from 'element-ui'
import { mapGetters, mapActions } from 'vuex'

export default {
  data() {
    return {
      filterText: '',
      header: 'Odi Users',
      cloneDisabled: true,
      currentRow: undefined
    }
  },
  mounted() {
    this.init()
  },
  computed: {
    ...mapGetters(['connection']),
    ...mapGetters('security', [
      'loading',
      'users'
    ]),
    filteredUsers() {
      return _.filter(this.users, u => {
        return _.isEmpty(this.filterText) ||
        u.name.toLowerCase().indexOf(this.filterText.toLowerCase()) > -1
      })
    }
  },
  methods: {
    ...mapActions('security', [
      'findOdiUsers',
      'deleteOdiUser',
      'cloneOdiUser',
      'renameOdiUser'
    ]),
    onCurrentChange(current) {
      this.cloneDisabled = false
      this.currentRow = current
    },
    init() {
      const connectionId = this.connection.id
      this.findOdiUsers({ connectionId })
    },
    onReload() {
      this.init()
    },
    onClone() {
      if (!this.currentRow) {
        Message.warning('Select users firs!')
        return
      }
      const connectionId = this.connection.id
      const { internalId } = this.currentRow
      this.cloneOdiUser({ connectionId, internalId })
    },
    handleEdit(index, row) {
      console.log(index, row)
    },
    onDelete(index, row) {
      if (row.isSupervisor) {
        Message.warning('Deleting SUPERVISOR users is not supported!')
        return
      }
      this.$confirm(`This will permanently delete ${row.name}. Continue?`, 'Warning', {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(() => {
        const connectionId = this.connection.id
        const { internalId } = row
        this.mask = true
        this.deleteOdiUser({ connectionId, internalId })
      })
    },
    onRename(index, row) {
      this.$prompt('', 'Raname', {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        inputValue: row.name,
        inputValidator: (v) => {
          return v ? true : 'Can not be blank'
        }
      }).then(e => {
        if (!e.value) {
          Message.warning('Can not be blank!')
          return
        }
        const connectionId = this.connection.id
        const { name } = row
        const params = { name, newName: e.value }
        this.renameOdiUser({ connectionId, params })
      })
    },
    onChangePassword(index, row) {
      const h = this.$createElement
      this.$notify({
        title: 'Coming soon',
        message: h('i', { style: 'color: teal' }, 'This feature will be available')
      })
    },
    onAddUser() {
      const h = this.$createElement
      this.$notify({
        title: 'Coming soon',
        message: h('i', { style: 'color: teal' }, 'This feature will be available')
      })
    }
  }
}
</script>

<style scoped>
.actions {
  display: none;
}

.el-table__row:hover .actions {
  display: unset;
}

</style>
