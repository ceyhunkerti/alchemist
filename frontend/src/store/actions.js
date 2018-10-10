import Cookies from 'js-cookie'
import { option2time } from '@/utils/time'
import Push from 'push.js'

export default {
  reset({ commit }) {
    this.dispatch('session/reset')
    this.dispatch('agent/reset')
    this.dispatch('dataSource/reset')
    this.dispatch('loadPlanRun/reset')
    this.dispatch('context/reset')
  },
  refresh({ commit }, payload) {
    const { connectionId } = payload
    if (!connectionId) { return }
    const criteria = {}
    criteria.startTime = parseInt(option2time(Cookies.get('run-date-filter')))
    this.dispatch('session/findRunning', { connectionId, criteria })
    criteria.startTime = parseInt(option2time(Cookies.get('err-date-filter')))
    this.dispatch('session/findErrors', { connectionId, criteria })
    this.dispatch('agent/findAll', { connectionId })
    this.dispatch('dataSource/findAll', { connectionId })
    this.dispatch('loadPlanRun/findLast', { connectionId })
    this.dispatch('loadPlan/findAll', { connectionId })
    this.dispatch('context/findAll', { connectionId })
  },
  init() {
    Push.Permission.request()
    this.dispatch('users/findAll')
    this.dispatch('connection/findAll').then(() => {
      const interval = setInterval(() => {
        const connectionId = this.getters.connection.id
        if (connectionId) {
          this.dispatch('refresh', { connectionId })
        }
      }, 60 * 1000)
      this.dispatch('setInterval', interval)
    })
  }
}
