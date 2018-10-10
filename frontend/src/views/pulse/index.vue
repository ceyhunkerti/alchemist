<template lang="pug">
  el-row.mailSettings-container(justify="center", style="padding: 40px 0;" v-loading="loading")
    el-col(:span='16', :push='4')
      .warn-content.noConnection-container(v-if="!component")
        svg-icon(class-name='pulse-icon' icon-class='pulse')
        span.noConnection-text You do not have any pulses
        el-button.create-button(type="primary", @click="component = 'pulse'") Create pulse
    pulse(v-if="component==='pulse'",
      :data="pulse"
      @close='init'
    )
    pulses(v-if="component==='pulses'",
      @create="onCreate",
      @edit='onEdit'
    )
</template>

<script>
import _ from 'lodash'
import { mapActions, mapGetters } from 'vuex'
import Pulses from './components/Pulses'
import Pulse from './components/Pulse'

export default {
  name: 'dashboard-admin',
  components: {
    Pulses,
    Pulse
  },
  data() {
    return {
      loading: false,
      pulse: {},
      component: undefined
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
      'findAll'
    ]),
    onCreate() {
      this.component = 'pulse'
    },
    onEdit(pulse) {
      this.component = 'pulse'
      this.pulse = pulse
    },
    init() {
      const connectionId = this.connection.id
      this.loading = true
      this.findAll({ connectionId })
        .then(response => {
          this.loading = false
          if (!_.isEmpty(response)) {
            this.component = 'pulses'
          } else {
            this.component = undefined
          }
        })
        .catch(_ => { this.loading = false; this.component = undefined })
    }
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

.warn-content {
  background-color: #EAE9E1;
  border-radius: 5px;
}

.pulse-icon {
  margin-right: 20px;
  font-size: 24px;
  color: gray;
}

</style>

