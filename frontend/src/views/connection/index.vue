<template lang="pug">
  el-row.mailSettings-container(justify="center", style="padding: 40px 0;")
    el-col(:span='16', :push='4')
      .warn-content.noConnection-container(v-if="!component")
        span.noConnection-text You do not have any connections
        el-button.create-button(type="primary", @click="component = 'connection'") Create connection
    connection(v-if="component === 'connection'",
      :id="connectionId"
      @closeConnection='init'
    )
    connections(v-if="component === 'connections'",
      @createConnection="onCreateConnection",
      @editConnection='onEdit'
    )
</template>

<script>
import _ from 'lodash'
import { mapGetters } from 'vuex'
import Connection from './components/Connection'
import Connections from './components/Connections'

export default {
  name: 'dashboard-admin',
  components: {
    Connection,
    Connections
  },
  data() {
    return {
      component: undefined,
      connectionId: undefined
    }
  },
  methods: {
    handlePanelClick(type) {
      this.component = type
    },
    onCancelConnection() {
      this.init()
    },
    onCreateConnection() {
      this.connectionId = undefined
      this.component = 'connection'
    },
    onEdit(id) {
      this.component = 'connection'
      this.connectionId = id
    },
    init() {
      if (!_.isEmpty(this.all)) {
        this.component = 'connections'
      } else {
        this.component = undefined
      }
    }
  },
  computed: {
    ...mapGetters('connection', [
      'all'
    ])
  },
  mounted() {
    this.init()
  }
}
</script>




<style scoped>

.noConnection-text {
  color: gray;
}

.noConnection-container {
  width: 100%;
  display: inline-flex;
}

.create-button {
  margin-left:auto;
  margin-right:0;
}

</style>

