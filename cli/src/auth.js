const axios = require('axios')

APP_URL = 'http://localhost:9090/api/v1'

const request = axios.create({
  baseURL: APP_URL, // api的base_url
  timeout: 15000000 // request timeout
})
request.interceptors.response.use(
  response => response,
  error => {
    console.log('err' + error) // for debug
    return Promise.reject(error)
})

function loginByUsername(username, password) {
  return axios.request({
    method: 'post',
    url: `http://localhost:9090/oauth/token?grant_type=password&username=${username}&password=${password}`,
    auth: {
      username: 'alchemist-client-id',
      password: 'XY7kmzoNzl100'
    },
    data: {
    },
    config: {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
        'Access-Control-Allow-Origin': '*',
        'X-Application-Context':	'application:9090'
      }
    }
  })
}

const login = async (username, password) => {
  return new Promise((resolve, reject) => {
    return loginByUsername(username, password).then(response => {
      const token = response.data.access_token
      request.interceptors.request.use(config => {
        config.headers.Authorization = `Bearer ${token}`
        return config
      }, error => {
        console.log(error) // for debug
        Promise.reject(error)
      })
      resolve(request)
    },error => { reject(error) })
  })
}

module.exports = {
  login,
  request
}