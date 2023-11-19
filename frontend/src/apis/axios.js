import Axios from 'axios'
import {accessTokenProvider, refreshTokenProvider} from '@/utils/token'

const axios = Axios.create({
  baseURL: process.env.VUE_APP_API_URL,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8',
    accept: 'application/json'
  },
  withCredentials: true
})

const axiosWithAccessToken = Axios.create({
  baseURL: process.env.VUE_APP_API_URL,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8',
    accept: 'application/json'
  },
  withCredentials: true
})

const axiosWithRefreshToken = Axios.create({
  baseURL: process.env.VUE_APP_API_URL,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8',
    accept: 'application/json'
  },
  withCredentials: true
})

// Request Interceptor
axiosWithAccessToken.interceptors.request.use(
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
axiosWithAccessToken.interceptors.response.use(
  response => {
    return response
  },
  error => {
    const originalConfig = error.config

    if (error.response.data.errorCode === 'AUTH_005') { // 유효하지 않은 토큰
      axiosWithRefreshToken // 토큰 재발급
        .post('/api/token/reissue')
        .then(response => {
          accessTokenProvider.set(response.data.accessToken)
          refreshTokenProvider.set(response.data.refreshToken)
          return axiosWithAccessToken.request(originalConfig)
        })
        .catch(() => {
          alert('권한이 없습니다.\n로그인 페이지로 이동합니다.')
          window.location.href = '/login'
        })
    } else if (error.response.data.errorCode === 'AUTH_003') {
      alert('권한이 없습니다.\n로그인 페이지로 이동합니다.')
      window.location.href = '/login'
    } else {
      return Promise.reject(error)
    }
  }
)

export {
  axios,
  axiosWithAccessToken,
  axiosWithRefreshToken
}
