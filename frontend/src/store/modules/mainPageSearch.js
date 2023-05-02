const mainPageSearch = {
  namespaced: true,

  state: {
    sortType: '등록 날짜 최신순',
    criteria: {
      sort: 'rdate',
      page: '1'
    }
  },

  getters: {
    getSortType: state => state.sortType,
    getSortInCriteria: state => state.criteria.sort,
    getPageInCriteria: state => state.criteria.page,
    getMainPageCriteria: state => state.criteria
  },

  mutations: {
    changeSortType: (state, type) => {
      state.sortType = type
    },
    changeSortCriteria: (state, sortType) => {
      state.criteria.sort = sortType
    },
    changePageToPrevious: (state) => {
      if (state.criteria.page % 10 === 0) {
        state.criteria.page -= 19
      } else {
        state.criteria.page = Math.floor(pageInfo / 10 - 1) + 1
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
      state.sortType = '등록 날짜 최신순'
      state.criteria.sort = 'rdate'
      state.criteria.page = 1
    }
  }
}

export default mainPageSearch
