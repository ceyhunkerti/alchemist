import request from '@/utils/request'

export default {
  findAll(connectionId) {
    return request.get(`/pulses?connectionId=${connectionId}`)
  },
  create(connectionId, data) {
    return request.post(`/pulses?connectionId=${connectionId}`, data)
  },
  update(id, data) {
    return request.put(`/pulses/${id}`, data)
  },
  remove(id) {
    return request.delete(`/pulses/${id}`)
  },
  setActive(id, active) {
    return request.put(`/pulses/${id}?active=${active}`)
  }
}
