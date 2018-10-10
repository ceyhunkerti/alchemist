import request from '@/utils/request'

export default {
  test(payload) {
    return request.post(`/connections/test`, payload)
  },
  create(payload) {
    return request.post(`/connections`, payload)
  },
  update(payload, id) {
    return request.put(`/connections/${id}`, payload)
  },
  findAll() {
    return request.get(`/connections`)
  },
  remove(id) {
    return request.delete(`/connections/${id}`)
  }
}
