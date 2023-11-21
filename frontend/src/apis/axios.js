import Axios from 'axios'
import {accessTokenProvider} from '@/utils/token'

const axios = Axios.create({
  baseURL: process.env.SERVER_API_URL,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8',
    accept: 'application/json'
  },
  withCredentials: true
})

// Request Interceptor
axios.interceptors.request.use(
  config => {
    const accessToken = accessTokenProvider.get()

    if (config.headers && accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`
    }

    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

axiosWithRefreshToken.interceptors.request.use(
  config => {
    const refreshToken = refreshTokenProvider.get()

    if (config.headers && refreshToken) {
      config.headers.Authorization = `Bearer ${refreshToken}`
    }

    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response Interceptor
axios.interceptors.response.use(
  response => {
    return response
  },
  error => {
    const originalConfig = error.config

    if (error.response.data.errorCode === 'TOKEN_001') { // 유효하지 않은 토큰
      axios // 토큰 재발급
        .post('/api/token/reissue')
        .then(response => {
          accessTokenProvider.set(response.headers.get('Authorization'))
          return axios.request(originalConfig)
        })
        .catch(() => {
          alert('권한이 없습니다.\n로그인 페이지로 이동합니다.')
          this.$store.commit('memberStore/reset')
          window.location.href = '/login'
        })
    } else if (error.response.data.errorCode === 'AUTH_003' || error.response.data.errorCode === 'AUTH_004') {
      alert('권한이 없습니다.\n로그인 페이지로 이동합니다.')
      this.$store.commit('memberStore/reset')
      window.location.href = '/login'
    } else {
      this.$store.commit('memberStore/reset')
      return Promise.reject(error)
    }
  }
)

export {
  axios
}
