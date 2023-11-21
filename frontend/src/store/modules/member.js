const memberStore = {
  namespaced: true,

  state: {
    loginSuccess: false,
    memberId: '',
    nickname: '',
    accessToken: '',
  },

  getters: {
    isAuthenticated: state => state.loginSuccess,
    getMemberId: state => state.memberId,
    getMemberNickname: state => state.nickname,
    getAccessToken: state => state.accessToken,
  },

  mutations: {
    loginSuccess: (state, payload) => {
      state.loginSuccess = true
      state.memberId = payload.memberId
      state.nickname = payload.nickname
      state.accessToken = payload.accessToken
    },
    updateAccessToken: (state, accessToken) => {
      state.accessToken = accessToken
    },
    reset: (state) => {
      state.loginSuccess = false
      state.memberId = ''
      state.nickname = ''
      state.accessToken = ''
    }
  }
}

export default memberStore
