import {createStore} from 'vuex'
import {createVuexPersistedState} from 'vue-persistedstate'

export default createStore({
  state: {
    loginSuccess: false,
    user: {
      id: '',
      name: '',
      role: ''
    },
    currentSearchSortType: '등록 날짜 최신순',
    pagingSearch: {
      sort: 'rdate',
      page: '1'
    },
    keywordSearch: {
      keyword: '',
      type: '',
      sort: 'rdate',
      page: 1
    },
    hashtagSearch: {
      hashtag: '',
      type: '',
      sort: 'rdate',
      page: 1
    },
    currentSearchType: '',
    currentSelectedArt: {
      artId: '',
      artSaleType: ''
    }
  },
  getters: {
    isAuthenticated: state => state.loginSuccess,
    getUserId: state => state.user.id,
    getUserName: state => state.user.name,
    getUserRole: state => state.user.role,

    getCurrentSearchSortType: state => state.currentSearchSortType,

    getMainPagingSort: state => state.pagingSearch.sort,
    getMainPagingPage: state => state.pagingSearch.page,
    getMainPagingSearch: state => state.pagingSearch,

    getKeywordSearchKeyword: state => state.keywordSearch.keyword,
    getKeywordSearchType: state => state.keywordSearch.type,
    getKeywordSearchSort: state => state.keywordSearch.sort,
    getKeywordSearch: state => state.keywordSearch,

    getHashtagSearchHashtag: state => state.hashtagSearch.hashtag,
    getHashtagSearchType: state => state.hashtagSearch.type,
    getHashtagSearchSort: state => state.hashtagSearch.sort,
    getHashtagSearch: state => state.hashtagSearch,

    getTypeTranslation: state => (state.currentSearchType === '') ? '경매 작품' : state.currentSearchType,

    isCurrentRequestEqualsKeyword: state => state.keywordSearch.keyword !== '' && state.hashtagSearch.hashtag === '',
    isCurrentRequestEqualsHashtag: state => state.keywordSearch.keyword === '' && state.hashtagSearch.hashtag !== '',
    isCurrentRequestEqualsAuctionArt: state => state.keywordSearch.type === 'auction' || state.hashtagSearch.type === 'auction',
    isCurrentRequestEqualsGeneralArt: state => state.keywordSearch.type === 'general' || state.hashtagSearch.type === 'general',

    getCurrentSelectedArtId: state => state.currentSelectedArt.artId,
    getCurrentSelectedArtSaleType: state => state.currentSelectedArt.artSaleType
  },
  mutations: {
    loginSuccess: (state, payload) => {
      state.loginSuccess = true
      state.user.id = payload.id
      state.user.name = payload.name
      state.user.role = payload.role
    },
    loginFail: (state) => {
      state.loginSuccess = false
    },

    logout: (state) => {
      state.loginSuccess = false
      state.user.id = ''
      state.user.name = ''
      state.user.role = ''
    },

    changeSortType: (state, type) => {
      state.currentSearchSortType = type
    },

    applyKeywordSearch: (state, searchInfo) => {
      state.keywordSearch.keyword = searchInfo.keyword
      state.keywordSearch.sort = 'rdate'
      state.keywordSearch.type = searchInfo.type
      state.hashtagSearch.hashtag = ''
      state.hashtagSearch.sort = 'rdate'
      state.hashtagSearch.type = ''
      state.currentSearchSortType = '등록 날짜 최신순'

      if (searchInfo.type === 'auction') {
        state.currentSearchType = '경매 작품'
      } else {
        state.currentSearchType = '일반 작품'
      }
    },
    applyHashtagSearch: (state, searchInfo) => {
      state.hashtagSearch.hashtag = searchInfo.hashtag
      state.hashtagSearch.sort = 'rdate'
      state.hashtagSearch.type = searchInfo.type
      state.keywordSearch.keyword = ''
      state.keywordSearch.sort = 'rdate'
      state.keywordSearch.type = ''
      state.currentSearchSortType = '등록 날짜 최신순'

      if (searchInfo.type === 'auction') {
        state.currentSearchType = '경매 작품'
      } else {
        state.currentSearchType = '일반 작품'
      }
    },

    changePagingSortType: (state, sortType) => {
      state.pagingSearch.sort = sortType
    },
    changeKeywordSearchSortType: (state, sortType) => {
      state.keywordSearch.sort = sortType
    },
    changeHashtagSearchSortType: (state, sortType) => {
      state.hashtagSearch.sort = sortType
    },

    changePageToPrevious: (state) => {
      if (state.pagingSearch.page % 10 === 0) {
        state.pagingSearch.page -= 19;
      } else {
        state.pagingSearch.page = Math.floor(pageInfo / 10 - 1) + 1
      }
    },
    changePageToIndex: (state, index) => {
      state.pagingSearch.page = index
    },
    changePageToNext: (state) => {
      if (state.pagingSearch.page % 10 === 0) {
        state.pagingSearch.page = Math.floor(state.pagingSearch.page / 10) + 1;
      } else {
        state.pagingSearch.page = Math.floor(state.pagingSearch.page / 10 + 1) + 1;
      }
    },

    changePageToPreviousWithKeywordSearch: (state) => {
      if (state.keywordSearch.page % 10 === 0) {
        state.keywordSearch.page -= 19;
      } else {
        state.keywordSearch.page = Math.floor(pageInfo / 10 - 1) + 1
      }
    },
    changePageToIndexWithKeywordSearch: (state, index) => {
      state.keywordSearch.page = index
    },
    changePageToNextWithKeywordSearch: (state) => {
      if (state.keywordSearch.page % 10 === 0) {
        state.keywordSearch.page = Math.floor(state.keywordSearch.page / 10) + 1;
      } else {
        state.keywordSearch.page = Math.floor(state.keywordSearch.page / 10 + 1) + 1;
      }
    },

    changePageToPreviousWithHashtagSearch: (state) => {
      if (state.hashtagSearch.page % 10 === 0) {
        state.hashtagSearch.page -= 19;
      } else {
        state.hashtagSearch.page = Math.floor(pageInfo / 10 - 1) + 1
      }
    },
    changePageToIndexWithHashtagSearch: (state, index) => {
      state.hashtagSearch.page = index
    },
    changePageToNextWithHashtagSearch: (state) => {
      if (state.hashtagSearch.page % 10 === 0) {
        state.hashtagSearch.page = Math.floor(state.hashtagSearch.page / 10) + 1;
      } else {
        state.hashtagSearch.page = Math.floor(state.hashtagSearch.page / 10 + 1) + 1;
      }
    },

    resetPagingSortType: (state) => {
      state.pagingSearch.sort = 'rdate'
      state.pagingSearch.page = 1
      state.keywordSearch.keyword = ''
      state.keywordSearch.sort = 'rdate'
      state.keywordSearch.page = 1
      state.hashtagSearch.hashtag = ''
      state.hashtagSearch.sort = 'rdate'
      state.hashtagSearch.page = 1
      state.currentSearchType = ''
    },

    applyCurrentSelectedArt: (state, changeInfo) => {
      state.currentSelectedArt.artId = changeInfo.artId
      state.currentSelectedArt.artSaleType = changeInfo.artSaleType
    }
  },
  actions: {
  },
  modules: {
  },
  plugins: [createVuexPersistedState({
    key: 'vuex',
    storage: window.sessionStorage
  })]
})
