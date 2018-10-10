import request from '@/utils/request'

export default {
  findAll(connectionId) {
    return request.get(`/load-plans?connectionId=${connectionId}`)
  },
  findById(connectionId, id) {
    return request.get(`/load-plans/${id}?connectionId=${connectionId}`)
  }
}
