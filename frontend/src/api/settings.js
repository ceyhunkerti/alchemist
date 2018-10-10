import request from '@/utils/request'

export default {
  save(payload) {
    return request.post(`/settings`, payload)
  },
  findAll() {
    return request.get(`/settings`)
  },
  testMail(payload) {
    return request.post(`/mail/test`, payload)
  },
  findByName(name) {
    return request.get(`/settings/name/${name}`)
  }
}
