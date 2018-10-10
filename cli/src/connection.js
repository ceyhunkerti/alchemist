
const request = require('./auth.js').request


const findAll = () => {
  return new Promise((resolve, reject) => {
    return request.get(`/connections`).then(response => {
      resolve(response.data)
    }, error => { reject(error) })
  })
}

module.exports = {
  findAll
}