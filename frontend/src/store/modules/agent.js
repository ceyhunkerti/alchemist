
import _ from 'lodash'
import { Message } from 'element-ui'
import api from '@/api/agent'

const SET_ALL = 'SET_ALL'
const SET_STATUS = 'SET_STATUS'
const RESET = 'RESET'

const state = {
  all: []
}

const getters = {
  all: state => state.all
}

// actions
const actions = {
  reset({ commit }) { commit(RESET) },
  findAll({ commit }, payload) {
    const { connectionId } = payload
    return api.findAll(connectionId).then(response => {
      commit(SET_ALL, response.data)
    },
    error => {
      Message.error(`Failed to find agents ${error.response.data.message}`)
    })
  },
  test({ commit }, payload) {
    const { connectionId, internalId } = payload
    return api.test(connectionId, internalId).then(response => {
      const status = 'UP'
      commit(SET_STATUS, { status, internalId })
      return response
    },
    error => {
      const status = 'DOWN'
      commit(SET_STATUS, { status, internalId })
      Message.error(`Agent is down! ${error}`)
      return error
    })
  }
}

// mutations
const mutations = {
  [SET_ALL](state, data) {
    state.all = _.map(data, d => { d.status = 'UNKNOWN'; return d })
  },
  [SET_STATUS](state, { internalId, status }) {
    const agent = _.chain(state.all).find({ internalId }).cloneDeep().value()
    agent.status = status
    const i = _.findIndex(state.all, { internalId })
    state.all.splice(i, 1)
    state.all.push(agent)
  },
  [RESET](state) {
    state.all = []
  }
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
}
