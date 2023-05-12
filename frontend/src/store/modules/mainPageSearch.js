const mainPageSearch = {
  namespaced: true,

  state: {
    sortTypeStr: '등록 날짜 최신순',
    criteria: {
      sortType: 'rdate',
      page: '1'
    }
  },

  getters: {
    getSortType: state => state.sortTypeStr,
    getSortInCriteria: state => state.criteria.sortType,
    getPageInCriteria: state => state.criteria.page,
    getMainPageCriteria: state => state.criteria
  },

  mutations: {
    changeSortType: (state, type) => {
      state.sortTypeStr = type
    },
    changeSortCriteria: (state, sortType) => {
      state.criteria.sortType = sortType
    },
    changePageToPrevious: (state) => {
      if (state.criteria.page % 10 === 0) {
        state.criteria.page -= 19
      } else {
        state.criteria.page = Math.floor(state.criteria.page / 10 - 1) + 1
      }
    },
    changePageToIndex: (state, index) => {
      state.criteria.page = index
    },
    changePageToNext: (state) => {
      if (state.criteria.page % 10 === 0) {
        state.criteria.page = Math.floor(state.criteria.page / 10) + 1
      } else {
        state.criteria.page = Math.floor(state.criteria.page / 10 + 1) + 1
      }
    },
    resetMainPageCriteria: (state) => {
      state.sortTypeStr = '등록 날짜 최신순'
      state.criteria.sortType = 'rdate'
      state.criteria.page = 1
    }
  }
}

export default mainPageSearch
