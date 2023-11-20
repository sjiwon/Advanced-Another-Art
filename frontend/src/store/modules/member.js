const memberStore = {
  namespaced: true,

  state: {
    loginSuccess: false,
    memberId: '',
    nickname: '',
  },

  getters: {
    isAuthenticated: state => state.loginSuccess,
    getMemberId: state => state.memberId,
    getMemberNickname: state => state.nickname,
  },

  mutations: {
    loginSuccess: (state, payload) => {
      state.loginSuccess = true
      state.memberId = payload.memberId
      state.nickname = payload.nickname
    },
    reset: (state) => {
      state.loginSuccess = false
      state.memberId = ''
      state.nickname = ''
    }
  }
}

export default memberStore
