import _ from 'lodash'
import api from '@/api/security'
import { Message } from 'element-ui'

const state = {
  loading: false,
  users: []
}

const SET_LOADING = 'SET_LOADING'
const SET_USERS = 'SET_USERS'
const DELETE_USER = 'DELETE_USER'
const ADD_USER = 'ADD_USER'
const RENAME_USER = 'RENAME_USER'

const getters = {
  loading: state => state.loading,
  users: state => state.users
}

// actions
const actions = {
  findOdiUsers({ commit }, payload) {
    const { connectionId } = payload
    commit(SET_LOADING, true)
    return api.findOdiUsers(connectionId).then(response => {
      commit(SET_LOADING, false)
      commit(SET_USERS, response.data)
      return response.data
    },
    error => {
      commit(SET_LOADING, false)
      console.log(error)
      Message.error(error)
    })
  },
  deleteOdiUser({ commit }, payload) {
    const { connectionId, internalId } = payload
    commit(SET_LOADING, true)
    return api.deleteOdiUser(connectionId, internalId).then(response => {
      commit(SET_LOADING, false)
      Message.success('Deleted ODI user ' + response.data.name)
      commit(DELETE_USER, response.data)
    },
    error => {
      console.log(error)
      commit(SET_LOADING, false)
      Message.error(error)
    })
  },
  cloneOdiUser({ commit }, payload) {
    const { connectionId, internalId } = payload
    commit(SET_LOADING, true)
    return api.cloneOdiUser(connectionId, internalId).then(response => {
      commit(SET_LOADING, false)
      Message.success('Cloned ODI user ' + response.data.name)
      commit(ADD_USER, response.data)
      return response.data
    },
    error => {
      console.log(error)
      commit(SET_LOADING, false)
      Message.error(error)
    })
  },
  renameOdiUser({ commit }, payload) {
    const { connectionId, params } = payload
    commit(SET_LOADING, true)
    return api.renameOdiUser(connectionId, params).then(response => {
      commit(SET_LOADING, false)
      commit(RENAME_USER, response.data)
      Message.success('Renamed ODI user.')
      return response.data
    },
    error => {
      Message.error('Unable to rename ODI user!')
      commit(SET_LOADING, false)
      console.log(error)
    })
  }
}

// mutations
const mutations = {
  [SET_LOADING](state, loading) {
    state.loading = loading
  },
  [SET_USERS](state, data) {
    state.users = data
  },
  [DELETE_USER](state, data) {
    const { internalId } = data
    const i = _.findIndex(state.all, { internalId })
    state.users.splice(i, 1)
  },
  [ADD_USER](state, data) {
    state.users.push(data)
  }
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
}
