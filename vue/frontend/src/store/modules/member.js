const memberStore = {
  namespaced: true,

  state: {
    loginSuccess: false,
    id: '',
    nickname: '',
    accessToken: '',
    refreshToken: ''
  },

  getters: {
    isAuthenticated: state => state.loginSuccess,
    getMemberId: state => state.id,
    getMemberNickname: state => state.nickname,
    getAccessToken: state => state.accessToken,
    getRefreshToken: state => state.refreshToken
  },

  mutations: {
    loginSuccess: (state, payload) => {
      state.loginSuccess = true
      state.id = payload.memberId
      state.nickname = payload.nickname
      state.accessToken = payload.accessToken
      state.refreshToken = payload.refreshToken
    },
    updateAccessToken: (state, accessToken) => {
      state.accessToken = accessToken
    },
    updateRefreshToken: (state, refreshToken) => {
      state.refreshToken = refreshToken
    },
    reset: (state) => {
      state.loginSuccess = false
      state.id = ''
      state.nickname = ''
      state.accessToken = ''
      state.refreshToken = ''
    }
  }
}

export default memberStore
