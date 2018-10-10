import Mock from 'mockjs'
import loginAPI from './login'
Mock.mock(/\/login\/logout/, 'post', loginAPI.logout)

export default Mock
