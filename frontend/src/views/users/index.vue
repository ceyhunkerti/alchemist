<template lang="pug">
  el-row.mailSettings-container(justify="center", style="padding: 40px 0;")
    el-col(:span='16', :push='4')
      .warn-content.emptyList-container(v-if="!component")
        span.emptyList-text You do not have any users
        el-button.create-button(type="primary", @click="component = 'user'") Create user
    user(v-if="component === 'user'",
      :id="userId"
      @close='init'
    )
    users(v-if="component === 'users'",
      @create="onCreate",
      @edit='onEdit'
    )
</template>

<script>
import _ from 'lodash'
import { mapGetters } from 'vuex'
import User from './components/User'
import Users from './components/Users'

export default {
  name: 'dashboard-admin',
  components: {
    User,
    Users
  },
  data() {
    return {
      component: undefined,
      userId: undefined
    }
  },
  methods: {
    handlePanelClick(type) {
      this.component = type
    },
    onCreate() {
      this.userId = undefined
      this.component = 'user'
    },
    onEdit(id) {
      this.component = 'user'
      this.userId = id
    },
    init() {
      if (!_.isEmpty(this.all)) {
        this.component = 'users'
      } else {
        this.component = undefined
      }
    }
  },
  computed: {
    ...mapGetters('users', [
      'all'
    ])
  },
  mounted() {
    this.init()
  }
}
</script>




<style scoped>

.emptyList-text {
  color: gray;
}

.emptyList-container {
  width: 100%;
  display: inline-flex;
}

.create-button {
  margin-left:auto;
  margin-right:0;
}

</style>

