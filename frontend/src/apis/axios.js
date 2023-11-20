import Axios from 'axios'

const axios = Axios.create({
  baseURL: process.env.SERVER_API_URL,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8',
    accept: 'application/json'
  },
  withCredentials: true
})

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
          return axios.request(originalConfig)
        })
        .catch(() => {
          alert('권한이 없습니다.\n로그인 페이지로 이동합니다.')
          window.location.href = '/login'
        })
    } else if (error.response.data.errorCode === 'AUTH_003' || error.response.data.errorCode === 'AUTH_004') {
      alert('권한이 없습니다.\n로그인 페이지로 이동합니다.')
      window.location.href = '/login'
    } else {
      return Promise.reject(error)
    }
  }
)

export {
  axios
}
