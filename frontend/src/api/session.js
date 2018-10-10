import request from '@/utils/request'

export default {
  findByCriteria(connectionId, criteria = {}) {
    return request.post(`/sessions?connectionId=${connectionId}`, criteria)
  },
  findAll(connectionId) {
    return request.get(`/sessions?connectionId=${connectionId}`)
  },
  findById(connectionId, id) {
    return request.get(`/sessions/${id}?connectionId=${connectionId}`)
  },
  purge(connectionId) {
    return request.get(`/sessions/purge?connectionId=${connectionId}`)
  }
}
