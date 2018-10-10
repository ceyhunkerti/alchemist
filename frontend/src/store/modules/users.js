import _ from 'lodash'
import { Message } from 'element-ui'
import api from '@/api/users'

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

const actions = {
  findAll({ commit }) {
    return api.findAll().then(response => {
      commit(SET_ALL, response.data)
    },
    error => {
      console.log(error)
      Message.error(`Unable to find users ${error}`)
    })
  },
  create({ commit }, payload) {
    return api.create(payload).then(response => {
      commit(CREATE, response.data)
      Message.success('Created user')
    },
    error => {
      console.log(error)
      Message.error(`Unable to create user ${error}`)
    })
  },
  update({ commit }, payload) {
    return api.update(payload, payload.id).then(response => {
      commit(UPDATE, response.data)
      Message.success('Updated user')
    },
    error => {
      console.log(error)
      Message.error(`Unable to update user ${error}`)
    })
  },
  remove({ commit }, id) {
    return api.remove(id).then(response => {
      commit(DELETE, id)
      Message.success('Deleted user')
    },
    error => {
      console.log(error)
      Message.error(`Unable to delete user ${error}`)
    })
  }
}

const mutations = {
  [SET_ALL](state, data) {
    state.all = data
  },
  [CREATE](state, data) {
    state.all.push(data)
  },
  [UPDATE](state, data) {
    const id = data.id
    const i = _.findIndex(state.all, { id })
    state.all.splice(i, 1)
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
