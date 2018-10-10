
import _ from 'lodash'
import { Message } from 'element-ui'
import api from '@/api/session'
import Cookies from 'js-cookie'
import Push from 'push.js'
import * as audio from '@/utils/audio'

const SET_ALL = 'SET_ALL'
const SET_ERRORS = 'SET_ERRORS'
const SET_RUNNING = 'SET_RUNNING'
const RESET = 'RESET'

const state = {
  all: [],
  running: [],
  errors: []
}

const getters = {
  all: state => state.all,
  errors: state => state.errors,
  running: state => state.running
}

// actions
const actions = {
  reset({ commit }) { commit(RESET) },
  findErrors({ commit }, payload) {
    const { connectionId, criteria } = payload
    const statuses = ['ERROR']
    return api.findByCriteria(connectionId, { statuses, ...criteria }).then(response => {
      commit(SET_ERRORS, response.data)
      const se = Cookies.get('session-errors')
      let err
      if (se) {
        err = _.chain(response.data)
          .map(d => `${connectionId}-${d.id}`)
          .difference(se.split(','))
          .map(id => { return _.find(response.data, d => `${connectionId}-${d.id}` === id) })
          .flatten()
          .value()
      } else {
        err = response.data
      }
      const cookie = _.chain(se).split(',').union(_.map(err, e => `${connectionId}-${e.id}`)).join(',').value()
      Cookies.set('session-errors', cookie)
      _.forEach(err, e => {
        audio.error()
        Push.create('Session Error', {
          body: `${e.name} has error!`,
          timeout: 4000,
          onClick: () => {
            window.focus()
            this.close()
          }
        })
      })
    },
    error => {
      Message.error(`Failed to find ERROR sessions ${error.response.data.message}`)
    })
  },
  findRunning({ commit }, payload) {
    const { connectionId, criteria } = payload
    const statuses = ['RUNNING']
    return api.findByCriteria(connectionId, { statuses, ...criteria }).then(response => {
      commit(SET_RUNNING, response.data)
    },
    error => {
      Message.error(`Failed to find RUNNING sessions ${error.response.data.message}`)
    })
  },
  findByCriteria({ commit }, payload) {
    const { connectionId, criteria } = payload
    return api.findByCriteria(connectionId, criteria)
  },
  findAll({ commit }, payload) {
    const { connectionId } = payload
    return api.findAll(connectionId)
  },
  findById({ commit }, payload) {
    const { connectionId, id } = payload
    return api.findById(connectionId, id)
  },
  purge({ commit }, { connectionId }) {
    return api.purge(connectionId)
  }
}

// mutations
const mutations = {
  [SET_ALL](state, data) {
    state.all = data
  },
  [SET_RUNNING](state, data) {
    state.running = data
  },
  [SET_ERRORS](state, data) {
    state.errors = data
  },
  [RESET](state) {
    state.all = []
    state.running = []
    state.errors = []
  }
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
}
