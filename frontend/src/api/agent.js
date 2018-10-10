import request from '@/utils/request'

export default {
  findAll(connectionId) {
    return request.get(`/agents?connectionId=${connectionId}`)
  },
  test(connectionId, id) {
    return request.get(`/agents/${id}/test?connectionId=${connectionId}`)
  }

}
