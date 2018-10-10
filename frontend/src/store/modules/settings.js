
import _ from 'lodash'
import { Message } from 'element-ui'
import api from '@/api/settings'

const SAVE = 'SAVE'
const LOAD = 'LOAD'

const state = {
  all: []
}

const getters = {
  all: state => state.all,
  mail: state => {
    const m = _.find(state.all, { name: 'MAIL' })
    return m ? m.val : {}
  }
}

// actions
const actions = {
  findByName({ commit }, { name }) {
    return api.findByName(name)
  },
  findAll({ commit }) {
    api.findAll().then(response => {
      commit(LOAD, response.data)
    },
    error => {
      console.log(error.response.data.message)
      Message.error(`Failed to load settings ${error.response.data.message}`)
    })
  },
  save({ commit }, payload) {
    api.save(payload).then(response => {
      commit(SAVE, response.data)
      Message.success('Saved settings')
    },
    error => {
      console.log(error.response.data.message)
      Message.error(`Failed to save settings ${error.response.data.message}`)
    })
  },
  testMail({ commit }, payload) {
    return api.testMail(payload)
  }
}

// mutations
const mutations = {
  [LOAD](state, data) {
    state.all = _.map(data, d => {
      d.val = JSON.parse(d.value)
      return d
    })
  },
  [SAVE](state, data) {
    const name = data.name
    data.val = JSON.parse(data.value)
    const i = _.findIndex(state.all, { name })
    state.all.splice(i, 1)
    state.all.push(data)
  }
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
}
