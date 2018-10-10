import request from '@/utils/request'

export default {
  findAll(connectionId) {
    return request.get(`/load-plan-runs?connectionId=${connectionId}`)
  },
  findLast(connectionId) {
    return request.get(`/load-plan-runs/last?connectionId=${connectionId}`)
  },
  findByLoadPlan(connectionId, id) {
    return request.get(`/load-plan-runs/load-plan/${id}?connectionId=${connectionId}`)
  },
  findOneWithLogs(connectionId, payload) {
    return request.post(`/load-plan-runs/with-logs/?connectionId=${connectionId}`, payload)
  }
}
