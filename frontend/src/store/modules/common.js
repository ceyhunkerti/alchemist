
import api from '@/api/common'

const state = {}

const getters = {
}

// actions
const actions = {
  exp({ commit }, payload) {
    const { connectionId } = payload
    return api.exp(connectionId, payload)
  }
}

// mutations
const mutations = {
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
}
