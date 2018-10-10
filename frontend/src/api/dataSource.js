import request from '@/utils/request'

export default {
  findAll(connectionId) {
    return request.get(`/data-servers?connectionId=${connectionId}`)
  },
  test(connectionId, dataServerId, agentId) {
    if (agentId) {
      return request.get(`/data-servers/${dataServerId}/test?connectionId=${connectionId}&agentId=${agentId}`)
    }
    return request.get(`/data-servers/${dataServerId}/test?connectionId=${connectionId}`)
  }
}
