
import _ from 'lodash'
import { Message } from 'element-ui'
import api from '@/api/dataSource'

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
      Message.error(`Failed to find data sources ${error.response.data.message}`)
    })
  },
  test({ commit }, payload) {
    const { connectionId, dataServerId, agentId } = payload
    const internalId = dataServerId
    return api.test(connectionId, dataServerId, agentId).then(response => {
      const status = 'UP'
      commit(SET_STATUS, { internalId, status })
      Message.success('Data server is up.')
    },
    error => {
      const status = 'DOWN'
      commit(SET_STATUS, { internalId, status })
      Message.error(`Failed to reach data source! ${error.response.data.message}`)
    })
  }
}

// mutations
const mutations = {
  [SET_ALL](state, data) {
    const statuses = _.map(state.all, d => {
      const internalId = d.internalId
      const status = d.status
      return { internalId, status }
    })
    state.all = _.chain(data).map(d => {
      const internalId = d.internalId
      const item = _.find(statuses, { internalId })
      d.status = item ? item.status : d.status
      return d
    }).sortBy(['name'], ['asc']).value()
  },
  [SET_STATUS](state, payload) {
    const { internalId, status } = payload
    const source = _.find(state.all, { internalId })
    source.status = status
    const i = _.findIndex(state.all, { internalId })
    state.all.splice(i, 1)
    state.all.push(source)
    state.all = _.sortBy(state.all, ['name'], ['asc'])
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
