import request from '@/utils/request'

export default {
  findAllFolders(connectionId) {
    return request.get(`/executables/folders?connectionId=${connectionId}`)
  },
  findAll(connectionId) {
    return request.get(`/executables?connectionId=${connectionId}`)
  },
  run(connectionId, payload) {
    return request.post(`/executables/run?connectionId=${connectionId}`, payload)
  },
  findScenarios(connectionId) {
    return request.get(`/scenarios?connectionId=${connectionId}`)
  }
}
