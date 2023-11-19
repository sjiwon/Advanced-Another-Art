import {createStore} from 'vuex'
import {createVuexPersistedState} from 'vue-persistedstate'

import memberStore from '@/store/modules/member.js'
import mainPageSearch from '@/store/modules/mainPageSearch.js'
import detailSearch from '@/store/modules/detailSearch.js'

export default createStore({
  modules: {
    namespaced: true,
    memberStore: memberStore,
    mainPageSearch: mainPageSearch,
    detailSearch: detailSearch
  },
  plugins: [createVuexPersistedState({
    key: 'vuex',
    paths: ['memberStore', 'mainPageSearch', 'detailSearch']
  })]
})
