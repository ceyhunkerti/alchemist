
// import _ from 'lodash'
import { Message } from 'element-ui'
import api from '@/api/executable'

const SET_FOLDERS = 'SET_FOLDERS'
const SET_ALL = 'SET_ALL'
const SET_LOADING_FOLDERS = 'SET_LOADING_FOLDERS'
const SET_LOADING_ALL = 'SET_LOADING_ALL'

const state = {
  folders: [],
  all: {},
  loading: {
    folders: false,
    all: false
  }
}

const getters = {
  folders: state => state.folders,
  executables: state => state.all
}

// actions
const actions = {
  findAllFolders({ commit }, payload) {
    commit(SET_LOADING_FOLDERS, true)
    const { connectionId } = payload
    api.findAllFolders(connectionId).then(response => {
      commit(SET_FOLDERS, response.data)
      commit(SET_LOADING_FOLDERS, false)
    },
    error => {
      console.log(error)
      Message.error(`Failed to find folders ${error}`)
      commit(SET_LOADING_FOLDERS, false)
    })
  },
  findAll({ commit }, payload) {
    commit(SET_LOADING_ALL, true)
    const { connectionId } = payload
    return api.findAll(connectionId).then(response => {
      commit(SET_ALL, response.data)
      commit(SET_LOADING_ALL, false)
      return response
    },
    error => {
      console.log(error)
      Message.error(`Failed to find executables ${error}`)
      commit(SET_LOADING_ALL, false)
    })
  },
  run({ commit }, payload) {
    const { connectionId } = payload
    return api.run(connectionId, payload)
  },
  findScenarios({ commit }, payload) {
    const { connectionId } = payload
    return api.findScenarios(connectionId)
  }
}

// mutations
const mutations = {
  [SET_FOLDERS](state, data) {
    state.folders = data
  },
  [SET_ALL](state, data) {
    state.all = data
  },
  [SET_LOADING_FOLDERS](state, loading) {
    state.loading.folders = loading
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
