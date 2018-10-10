
import _ from 'lodash'
import { Message } from 'element-ui'
import api from '@/api/loadPlanRun'

const SET_ALL = 'SET_ALL'
const SET_LAST = 'SET_LAST'
const SET_LOADING_LAST = 'SET_LOADING_LAST'
const SORT_LAST = 'SORT_LAST'
const RESET = 'RESET'

const state = {
  all: [],
  last: [],
  loading: {
    last: false
  }
}

const getters = {
  all: state => state.all,
  last: state => state.last,
  runHist: state => state.runHist,
  loadingLast: state => state.loading.last
}

// actions
const actions = {
  reset({ commit }) { commit(RESET) },
  sortLastRuns({ commit }, payload) {
    commit(SORT_LAST, payload)
  },
  findAll({ commit }, payload) {
    const { connectionId } = payload
    api.findAll(connectionId).then(response => {
      commit(SET_ALL, response.data)
    },
    error => {
      Message.error(`Failed to find load plan runs ${error.response.data.message}`)
    })
  },
  findLast({ commit }, payload) {
    const { connectionId } = payload
    commit(SET_LOADING_LAST, true)
    api.findLast(connectionId).then(response => {
      commit(SET_LAST, response.data)
      commit(SET_LOADING_LAST, false)
    },
    error => {
      commit(SET_LOADING_LAST, false)
      Message.error(`Failed to find load plan runs ${error.response.data.message}`)
    })
  },
  findByLoadPlan({ commit }, payload) {
    const { connectionId, internalId } = payload
    return api.findByLoadPlan(connectionId, internalId)
  },
  findOneWithLogs({ commit }, payload) {
    const { connectionId, internalId } = payload
    return api.findOneWithLogs(connectionId, internalId)
  }
}

// mutations
const mutations = {
  [SET_ALL](state, data) {
    state.all = data
  },
  [SET_LAST](state, data) {
    const sort = (o) => {
      if (o.status === 'RUNNING') return '0'
      return o.loadPlanInstance.name
    }
    state.last = _.sortBy(data, [sort])
  },
  [SORT_LAST](state, { prop, order }) {
    const sortfn = (r) => {
      if (prop === 'duration') {
        return r.startTime - (r.endTime ? r.endTime : (new Date()).getTime())
      }
      return r[prop]
    }
    const ord = (order === 'ascending' ? 'asc' : 'desc')
    state.last = _.chain(state.last).cloneDeep().orderBy([sortfn], [ord]).value()
  },
  [SET_LOADING_LAST](state, loading) {
    state.loading.last = loading
  },
  [RESET](state) {
    state.all = []
    state.last = []
  }
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
}
