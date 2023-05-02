const detailSearch = {
  namespaced: true,

  state: {
    keywordSearch: {
      keyword: '',
      type: '', // general, auction
      sort: 'rdate',
      page: 1
    },

    hashtagSearch: {
      hashtag: '',
      type: '', // general, auction
      sort: 'rdate',
      page: 1
    },

    sortType: '등록 날짜 최신순',
    currentSearchType: '',
    currentSelectedArt: {
      artId: '',
      artType: '' // general, auction
    }
  },

  getters: {
    getKeywordWithKeywordSearch: state => state.keywordSearch.keyword,
    getTypeWithKeywordSearch: state => state.keywordSearch.type,
    getSortWithKeywordSearch: state => state.keywordSearch.sort,
    getKeywordSearchCriteria: state => state.keywordSearch,

    getHashtagWithHashtagSearch: state => state.hashtagSearch.hashtag,
    getTypeWithHashtagSearch: state => state.hashtagSearch.type,
    getSortWithHashtagSearch: state => state.hashtagSearch.sort,
    getHashtagSearchCriteria: state => state.hashtagSearch,

    isCurrentRequestEqualsKeyword: state => state.keywordSearch.keyword !== '' && state.hashtagSearch.hashtag === '',
    isCurrentRequestEqualsHashtag: state => state.keywordSearch.keyword === '' && state.hashtagSearch.hashtag !== '',
    isCurrentRequestEqualsAuctionArt: state => state.keywordSearch.type === 'auction' || state.hashtagSearch.type === 'auction',
    isCurrentRequestEqualsGeneralArt: state => state.keywordSearch.type === 'general' || state.hashtagSearch.type === 'general',

    getSortType: state => state.sortType,
    getArtSearchType: state => (state.currentSearchType === '') ? '경매 작품' : state.currentSearchType,
    getCurrentSelectedArtId: state => state.currentSelectedArt.artId,
    getCurrentSelectedArtType: state => state.currentSelectedArt.artType
  },

  mutations: {
    changeSortType: (state, type) => {
      state.sortType = type
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

    applyCurrentSelectedArt: (state, changeInfo) => {
      state.currentSelectedArt.artId = changeInfo.artId
      state.currentSelectedArt.artType = changeInfo.artType
    },

    changeKeywordSearchSortType: (state, sortType) => {
      state.keywordSearch.sort = sortType
    },
    changeHashtagSearchSortType: (state, sortType) => {
      state.hashtagSearch.sort = sortType
    },

    changePageToPreviousWithKeywordSearch: (state) => {
      if (state.keywordSearch.page % 10 === 0) {
        state.keywordSearch.page -= 19
      } else {
        state.keywordSearch.page = Math.floor(pageInfo / 10 - 1) + 1
      }
    },
    changePageToIndexWithKeywordSearch: (state, index) => {
      state.keywordSearch.page = index
    },
    changePageToNextWithKeywordSearch: (state) => {
      if (state.keywordSearch.page % 10 === 0) {
        state.keywordSearch.page = Math.floor(state.keywordSearch.page / 10) + 1
      } else {
        state.keywordSearch.page = Math.floor(state.keywordSearch.page / 10 + 1) + 1
      }
    },

    changePageToPreviousWithHashtagSearch: (state) => {
      if (state.hashtagSearch.page % 10 === 0) {
        state.hashtagSearch.page -= 19
      } else {
        state.hashtagSearch.page = Math.floor(pageInfo / 10 - 1) + 1
      }
    },
    changePageToIndexWithHashtagSearch: (state, index) => {
      state.hashtagSearch.page = index
    },
    changePageToNextWithHashtagSearch: (state) => {
      if (state.hashtagSearch.page % 10 === 0) {
        state.hashtagSearch.page = Math.floor(state.hashtagSearch.page / 10) + 1
      } else {
        state.hashtagSearch.page = Math.floor(state.hashtagSearch.page / 10 + 1) + 1
      }
    },

    resetDetailSearchCriteria: (state) => {
      state.sortType = '등록 날짜 최신순'
      state.currentSearchType = '경매 작품'
      state.currentSelectedArt.artId = ''
      state.currentSelectedArt.artType = ''

      state.keywordSearch.keyword = ''
      state.keywordSearch.sort = 'rdate'
      state.keywordSearch.page = 1
      state.hashtagSearch.hashtag = ''
      state.hashtagSearch.sort = 'rdate'
      state.hashtagSearch.page = 1
      state.currentSearchType = ''
    }
  }
}

export default detailSearch
