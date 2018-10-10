import request from '@/utils/request'

export default {
  findOdiUsers(connectionId) {
    return request.get(`/security/users?connectionId=${connectionId}`)
  },
  deleteOdiUser(connectionId, id) {
    return request.delete(`/security/users/${id}?connectionId=${connectionId}`)
  },
  cloneOdiUser(connectionId, id) {
    return request.get(`/security/users/clone/${id}?connectionId=${connectionId}`)
  },
  renameOdiUser(connectionId, params) {
    return request.put(`/security/users/rename?connectionId=${connectionId}`, params)
  }

}
