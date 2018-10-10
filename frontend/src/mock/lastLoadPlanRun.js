import Mock from 'mockjs'

const List = []
const count = 20

for (let i = 0; i < count; i++) {
  List.push(Mock.mock({
    order_no: '@guid()',
    startTime: +Mock.Random.date('T'),
    endTime: '@name()',
    status: '',
    price: '@float(1000, 15000, 0, 2)',
    'status|1': ['success', 'pending']
  }))
}

export default {
  getList: () => {
    return {
      total: List.length,
      items: List
    }
  }
}
