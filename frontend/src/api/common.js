import request from '@/utils/request'

export default {
  exp(connectionId, options) {
    return request.post(`/app/export?connectionId=${connectionId}`, options)
  }
}
