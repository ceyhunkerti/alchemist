import Vue from 'vue'
import Vuex from 'vuex'
import app from './modules/app'
import errorLog from './modules/errorLog'
import permission from './modules/permission'
import tagsView from './modules/tagsView'
import user from './modules/user'
import users from './modules/users'
import connection from './modules/connection'
import loadPlan from './modules/loadPlan'
import executable from './modules/executable'
import session from './modules/session'
import loadPlanRun from './modules/loadPlanRun'
import agent from './modules/agent'
import dataSource from './modules/dataSource'
import common from './modules/common'
import context from './modules/context'
import security from './modules/security'
import settings from './modules/settings'
import pulse from './modules/pulse'
import getters from './getters'
import actions from './actions'

Vue.use(Vuex)

const store = new Vuex.Store({
  modules: {
    app,
    errorLog,
    permission,
    tagsView,
    user,
    users,
    connection,
    loadPlan,
    executable,
    session,
    loadPlanRun,
    agent,
    dataSource,
    security,
    common,
    context,
    settings,
    pulse
  },
  actions,
  getters
})

export default store
