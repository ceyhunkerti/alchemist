import request from '@/utils/request'

export default {
  create(payload) {
    return request.post(`/users`, payload)
  },
  update(payload, id) {
    return request.put(`/users/${id}`, payload)
  },
  findAll() {
    return request.get(`/users`)
  },
  remove(id) {
    return request.delete(`/users/${id}`)
  }
}
