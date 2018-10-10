import request from '@/utils/request'

export default {
  findAll(connectionId) {
    return request.get(`/contexts?connectionId=${connectionId}`)
  }
}
