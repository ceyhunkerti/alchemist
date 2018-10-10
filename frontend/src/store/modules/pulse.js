
import _ from 'lodash'
import { Message } from 'element-ui'
import api from '@/api/pulse'

const SET_ALL = 'SET_ALL'
const SET_LOADING = 'SET_LOADING'
const ADD_PULSE = 'ADD_PULSE'
const UPDATE = 'UPDATE'
const DELETE = 'DELETE'

const state = {
  all: [],
  loading: false
}

const getters = {
  pulses: state => _.map(state.all, r => {
    switch (r.pulseType) {
      case 'LOAD_PLAN_ERROR': r._pulseType = 'Load plan error'; break
    }
    return r
  }),
  pulsesLoading: state => state.loading
}

// actions
const actions = {
  findAll({ commit }, { connectionId }) {
    commit(SET_LOADING, true)
    return api.findAll(connectionId).then(response => {
      commit(SET_ALL, response.data)
      commit(SET_LOADING, false)
      return response.data
    },
    error => {
      console.log(error.response.data.message)
      commit(SET_LOADING, false)
      Message.error(`Failed to load settings ${error.response.data.message}`)
      return error
    })
  },
  create({ commit }, { connectionId, data }) {
    return api.create(connectionId, data).then(response => {
      commit(ADD_PULSE, response.data)
      Message.success('Created puslse')
      return response.data
    },
    error => {
      console.log(error)
      Message.error(`Failed to create pulse ${error}`)
      return error
    })
  },
  update({ commit }, { data }) {
    return api.update(data.id, data).then(response => {
      commit(UPDATE, response.data)
      Message.success('Updated puslse')
      return response
    },
    error => {
      console.log(error)
      Message.error(`Failed to update pulse ${error}`)
      return error
    })
  },
  setActive({ commit }, { id, active }) {
    return api.setActive(id, active).then(response => {
      commit(UPDATE, response.data)
      Message.success('Success.')
      return response
    }, error => {
      console.log(error)
      Message.error(error)
      return error
    })
  },
  remove({ commit }, { id }) {
    return api.remove(id).then(response => {
      commit(DELETE, id)
      Message.success('Success')
      return response
    }, error => {
      Message.error(error)
      console.log(error)
      return error
    })
  }
}

// mutations
const mutations = {
  [SET_ALL](state, data) {
    state.all = data
  },
  [SET_LOADING](setate, loading) {
    state.loading = loading
  },
  [ADD_PULSE](state, data) {
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
