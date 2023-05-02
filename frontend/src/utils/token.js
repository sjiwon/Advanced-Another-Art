import store from '@/store'

const accessTokenProvider = {
  get: () => {
    return store.getters['memberStore/getAccessToken'] ?? ''
  },
  set: (accessToken) => {
    store.commit('memberStore/updateAccessToken', accessToken)
  }
}

const refreshTokenProvider = {
  get: () => {
    return store.getters['memberStore/getRefreshToken'] ?? ''
  },
  set: (refreshToken) => {
    store.commit('memberStore/updateRefreshToken', refreshToken)
  }
}

export { accessTokenProvider, refreshTokenProvider }
