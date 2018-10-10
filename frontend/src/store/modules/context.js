import api from '@/api/context'
import { Message } from 'element-ui'

const SET_ALL = 'SET_ALL'
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
      console.log(error)
      Message.error('Failed to find contexts')
    })
  }
}

// mutations
const mutations = {
  [SET_ALL](state, data) {
    state.all = data
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
