import store from '@/store'

const accessTokenProvider = {
  get: () => {
    return store.getters['memberStore/getAccessToken'] ?? ''
  },
  set: (accessToken) => {
    store.commit('memberStore/updateAccessToken', accessToken)
  }
}

export {accessTokenProvider}
