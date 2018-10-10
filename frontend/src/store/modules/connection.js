
import _ from 'lodash'
import { Message } from 'element-ui'
import api from '@/api/connection'

const SET_ALL = 'SET_ALL'
const CREATE = 'CREATE'
const UPDATE = 'UPDATE'
const DELETE = 'DELETE'

const state = {
  all: []
}

const getters = {
  all: state => state.all
}

// actions
const actions = {
  findAll({ commit, dispatch }) {
    return new Promise((resolve, reject) => {
      return api.findAll().then(response => {
        commit(SET_ALL, response.data)
        dispatch('setConnection', _.find(response.data, { isDefault: true }), { root: true })
        resolve(response.data)
      },
      error => {
        console.log(error.response.data.message)
        Message.error(`Failed to load connections ${error.response.data.message}`)
        reject(error)
      })
    })
  },
  create({ commit }, payload) {
    api.create(payload).then(response => {
      commit(CREATE, response.data)
      Message.success('Saved connection')
    },
    error => {
      console.log(error.response.data.message)
      Message.error(`Failed to save connection ${error.response.data.message}`)
    })
  },
  update({ commit }, payload) {
    api.update(payload, payload.id).then(response => {
      commit(UPDATE, response.data)
      Message.success('Saved connection')
    },
    error => {
      console.log(error.response.data.message)
      Message.error(`Failed to save connection ${error.response.data.message}`)
    })
  },
  test({ commit }, payload) {
    return api.test(payload).then(response => {
      Message.success('Success. Connection works')
    },
    error => {
      console.log(error.response.data.message)
      Message.error(`Error ${error.response.data.message}`)
    })
  },
  remove({ commit }, id) {
    api.remove(id).then(response => {
      commit(DELETE, id)
      Message.success('Deleted connection')
    },
    error => {
      console.log(error.response.data.message)
      Message.error(`Failed to delete ${error.response.data.message}`)
    })
  }
}

// mutations
const mutations = {
  [SET_ALL](state, data) {
    state.all = data
  },
  [CREATE](state, data) {
    if (data.isDefault) {
      state.all = _.map(state.all, d => {
        d.isDefault = false
        return d
      })
    }
    state.all.push(data)
  },
  [UPDATE](state, data) {
    const id = data.id
    const i = _.findIndex(state.all, { id })
    state.all.splice(i, 1)
    if (data.isDefault) {
      state.all = _.map(state.all, d => {
        d.isDefault = false
        return d
      })
    }
    state.all.push(data)
  },
  [DELETE](state, id) {
    const i = _.findIndex(state.all, { id })
    state.all.splice(i, 1)
  }
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
}
