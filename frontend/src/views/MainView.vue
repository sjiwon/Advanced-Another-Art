<template>
  <div class="container">
    <h1 class="text-center">현재 경매중인 작품들</h1><br>
    <!-- DropDown 메뉴 -->
    <div class="clearfix text-center">
      <b-dropdown size="lg" v-model:text="sortType.defaultSort" split variant="outline-secondary">
        <b-dropdown-item @click="changeToDateDESC()">{{ sortType.dateDESC }}</b-dropdown-item>
        <b-dropdown-item @click="changeToDateASC()">{{ sortType.dateASC }}</b-dropdown-item>
        <b-dropdown-divider></b-dropdown-divider>
        <b-dropdown-item @click="changeToPriceDESC()">{{ sortType.priceDESC }}</b-dropdown-item>
        <b-dropdown-item @click="changeToPriceASC()">{{ sortType.priceASC }}</b-dropdown-item>
        <b-dropdown-divider></b-dropdown-divider>
        <b-dropdown-item @click="changeToCountDESC()">{{ sortType.bidCountDESC }}</b-dropdown-item>
        <b-dropdown-item @click="changeToCountASC()">{{ sortType.bidCountASC }}</b-dropdown-item>
        <b-dropdown-divider></b-dropdown-divider>
        <b-dropdown-item @click="changeToLikeDESC()">{{ sortType.likeDESC }}</b-dropdown-item>
        <b-dropdown-item @click="changeToLikeASC()">{{ sortType.likeASC }}</b-dropdown-item>
      </b-dropdown>
    </div>

    <!-- 작품 리스트 -->
    <div class="album py-5">
      <div class="container">
        <div class="row row-cols-1 row-cols-sm-2 row-cols-md-4 g-3">
          <div v-for="(art, index) in fetchDataList" :key="index">
            <AuctionArtComponent :auction-art="art" />
          </div>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <ul class="pagination justify-content-center pagination-circle">
      <li v-if="pagination.prev === true" class="page-item">
        <b-button class="page-link" @click="moveToPreviousPage()">Previous</b-button>
      </li>
      <li v-for="(index) in range" :key="index" :class="checkActive(pagination.currentPage, index)" class="page-item">
        <b-button class="page-link" @click="moveToIndexPage(index)">{{ index }}</b-button>
      </li>
      <li v-if="pagination.next === true" class="page-item">
        <b-button class="page-link" @click="moveToNextPage()">Next</b-button>
      </li>
    </ul>
  </div>
</template>

<script>
import AuctionArtComponent from '@/components/art/AuctionArtComponent.vue'

export default {
  name: 'MainView',

  components: {
    AuctionArtComponent
  },

  data() {
    return {
      sortType: {
        page: 1,
        defaultSort: this.getSortType(),
        defaultType: 'rdate',
        dateDESC: '등록 날짜 최신순',
        dateASC: '등록 날짜 오래된순',
        priceDESC: '입찰 가격 높은순',
        priceASC: '입찰 가격 낮은순',
        bidCountDESC: '입찰 횟수 많은순',
        bidCountASC: '입찰 횟수 적은순',
        likeDESC: '좋아요 횟수 많은순',
        likeASC: '좋아요 횟수 적은순'
      },
      fetchDataList: [],
      pagination: {},
      range: []
    }
  },

  created() {
    this.fetchData()
  },

  methods: {
    getSortType() {
      return this.$store.getters['mainPageSearch/getSortType']
    },
    checkActive(currentPage, idx) {
      if (currentPage === idx) {
        return 'active'
      } else {
        return ''
      }
    },
    async fetchData() {
      try {
        const { sortType, page } = this.$store.getters['mainPageSearch/getMainPageCriteria']
        const response = await this.axios.get(`/api/arts?sortType=${sortType}&page=${page}`)

        this.fetchDataList = [...response.data.result]
        this.pagination = response.data.pagination
        this.range = []
        for (let i = this.pagination.rangeStartNumber; i <= this.pagination.rangeEndNumber; i++) {
          this.range.push(i)
        }
      } catch (err) {
        alert(err.response.data.message)
      }
    },

    applyNewFetchData() {
      this.$store.commit('mainPageSearch/changeSortCriteria', this.sortType.defaultType)
      this.fetchData()
      this.$router.push({
        path: '/',
        query: {
          sort: this.$store.getters['mainPageSearch/getSortInCriteria'],
          page: this.$store.getters['mainPageSearch/getPageInCriteria']
        }
      })
    },
    changeToDateDESC() {
      this.$store.commit('mainPageSearch/changeSortType', this.sortType.dateDESC)
      this.sortType.defaultType = 'rdate'
      this.applyNewFetchData()
    },
    changeToDateASC() {
      this.$store.commit('mainPageSearch/changeSortType', this.sortType.dateASC)
      this.sortType.defaultType = 'date'
      this.applyNewFetchData()
    },
    changeToPriceDESC() {
      this.$store.commit('mainPageSearch/changeSortType', this.sortType.priceDESC)
      this.sortType.defaultType = 'rprice'
      this.applyNewFetchData()
    },
    changeToPriceASC() {
      this.$store.commit('mainPageSearch/changeSortType', this.sortType.priceASC)
      this.sortType.defaultType = 'price'
      this.applyNewFetchData()
    },
    changeToCountDESC() {
      this.$store.commit('mainPageSearch/changeSortType', this.sortType.bidCountDESC)
      this.sortType.defaultType = 'rcount'
      this.applyNewFetchData()
    },
    changeToCountASC() {
      this.$store.commit('mainPageSearch/changeSortType', this.sortType.bidCountASC)
      this.sortType.defaultType = 'count'
      this.applyNewFetchData()
    },
    changeToLikeDESC() {
      this.$store.commit('mainPageSearch/changeSortType', this.sortType.likeDESC)
      this.sortType.defaultType = 'rlike'
      this.applyNewFetchData()
    },
    changeToLikeASC() {
      this.$store.commit('mainPageSearch/changeSortType', this.sortType.likeASC)
      this.sortType.defaultType = 'like'
      this.applyNewFetchData()
    },

    applyPageMove() {
      this.fetchData()
      this.$router.push({
        path: '/',
        query: {
          sort: this.$store.getters['mainPageSearch/getSortInCriteria'],
          page: this.$store.getters['mainPageSearch/getPageInCriteria']
        }
      })
      window.scrollTo(0, 0)
    },
    moveToPreviousPage() {
      this.$store.commit('mainPageSearch/changePageToPrevious')
      this.applyPageMove()
    },
    moveToIndexPage(index) {
      this.$store.commit('mainPageSearch/changePageToIndex', index)
      this.applyPageMove()
    },
    moveToNextPage() {
      this.$store.commit('mainPageSearch/changePageToNext')
      this.applyPageMove()
    }
  }
}
</script>

<style scoped>

</style>
