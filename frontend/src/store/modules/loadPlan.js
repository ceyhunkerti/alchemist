
// import _ from 'lodash'
import { Message } from 'element-ui'
import api from '@/api/loadPlan'

const SET_ALL = 'SET_ALL'
const SET_LOADING_ALL = 'SET_LOADING_ALL'

const state = {
  all: [],
  loading: {
    all: false
  }
}

const getters = {
  all: state => state.all,
  loadingAll: state => state.loading.all
}

// actions
const actions = {
  findAll({ commit }, payload) {
    commit(SET_LOADING_ALL, true)
    const { connectionId } = payload
    return api.findAll(connectionId).then(response => {
      commit(SET_ALL, response.data)
      commit(SET_LOADING_ALL, false)
      return response.data
    },
    error => {
      Message.error(`Failed to find load plans ${error.response.data.message}`)
      commit(SET_LOADING_ALL, false)
      return error
    })
  },
  findById({ commit }, payload) {
    const { id, internalId } = payload
    return api.findById(id, internalId)
  }
}

// mutations
const mutations = {
  [SET_ALL](state, data) {
    state.all = data
  },
  [SET_LOADING_ALL](state, loading) {
    state.loading.all = loading
  }

}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
}
