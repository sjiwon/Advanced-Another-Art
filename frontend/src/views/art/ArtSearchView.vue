<template>
  <div class="container">
    <!-- DropDown 메뉴 -->
    <div class="clearfix text-center">
      <b-dropdown v-if="$store.getters['detailSearch/isCurrentRequestEqualsAuctionArt'] === true" size="lg" v-model:text="sortType.defaultSort" split variant="outline-secondary">
        <b-dropdown-item @click="changeToAuctionDateDESC()">{{ sortType.auction.dateDESC }}</b-dropdown-item>
        <b-dropdown-item @click="changeToAuctionDateASC()">{{ sortType.auction.dateASC }}</b-dropdown-item>
        <b-dropdown-divider></b-dropdown-divider>
        <b-dropdown-item @click="changeToAuctionPriceDESC()">{{ sortType.auction.priceDESC }}</b-dropdown-item>
        <b-dropdown-item @click="changeToAuctionPriceASC()">{{ sortType.auction.priceASC }}</b-dropdown-item>
        <b-dropdown-divider></b-dropdown-divider>
        <b-dropdown-item @click="changeToAuctionCountDESC()">{{ sortType.auction.countDESC }}</b-dropdown-item>
        <b-dropdown-item @click="changeToAuctionCountASC()">{{ sortType.auction.countASC }}</b-dropdown-item>
        <b-dropdown-divider></b-dropdown-divider>
        <b-dropdown-item @click="changeToAuctionLikeDESC()">{{ sortType.auction.likeDESC }}</b-dropdown-item>
        <b-dropdown-item @click="changeToAuctionLikeASC()">{{ sortType.auction.likeASC }}</b-dropdown-item>
      </b-dropdown>
      <b-dropdown v-if="$store.getters['detailSearch/isCurrentRequestEqualsGeneralArt'] === true" size="lg" v-model:text="sortType.defaultSort" split variant="outline-secondary">
        <b-dropdown-item @click="changeToGeneralDateDESC()">{{ sortType.general.dateDESC }}</b-dropdown-item>
        <b-dropdown-item @click="changeToGeneralDateASC()">{{ sortType.general.dateASC }}</b-dropdown-item>
        <b-dropdown-divider></b-dropdown-divider>
        <b-dropdown-item @click="changeToGeneralPriceDESC()">{{ sortType.general.priceDESC }}</b-dropdown-item>
        <b-dropdown-item @click="changeToGeneralPriceASC()">{{ sortType.general.priceASC }}</b-dropdown-item>
        <b-dropdown-divider></b-dropdown-divider>
        <b-dropdown-item @click="changeToGeneralLikeDESC()">{{ sortType.general.likeDESC }}</b-dropdown-item>
        <b-dropdown-item @click="changeToGeneralLikeASC()">{{ sortType.general.likeASC }}</b-dropdown-item>
      </b-dropdown>
    </div>

    <!-- 작품 리스트 -->
    <div class="album py-5">
      <div class="container">
        <div class="row row-cols-sm-2 row-cols-md-4 g-4">
          <div v-for="(art, index) in fetchDataList" :key="index">
            <AuctionArtSearchComponent v-if="$store.getters['detailSearch/isCurrentRequestEqualsAuctionArt'] === true" :auction-art="art"/>
            <GeneralArtSearchComponent v-if="$store.getters['detailSearch/isCurrentRequestEqualsGeneralArt'] === true" :general-art="art"/>
          </div>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <ul class="pagination justify-content-center pagination-circle">
      <li v-if="pagination.prev === true" class="page-item">
        <b-button v-if="$store.getters['detailSearch/isCurrentRequestEqualsKeyword']" class="page-link" @click="moveToPreviousPageWithKeywordSearch()">Previous</b-button>
        <b-button v-if="$store.getters['detailSearch/isCurrentRequestEqualsHashtag']" class="page-link" @click="moveToPreviousPageWithHashtagSearch()">Previous</b-button>
      </li>
      <li v-for="(index) in range" :key="index" :class="checkActive(pagination.currentPage, index)" class="page-item">
        <b-button v-if="$store.getters['detailSearch/isCurrentRequestEqualsKeyword']" class="page-link" @click="moveToIndexPageWithKeywordSearch(index)">{{ index }}</b-button>
        <b-button v-if="$store.getters['detailSearch/isCurrentRequestEqualsHashtag']" class="page-link" @click="moveToIndexPageWithHashtagSearch(index)">{{ index }}</b-button>
      </li>
      <li v-if="pagination.next === true" class="page-item">
        <b-button v-if="$store.getters['detailSearch/isCurrentRequestEqualsKeyword']" class="page-link" @click="moveToNextPageWithKeywordSearch()">Next</b-button>
        <b-button v-if="$store.getters['detailSearch/isCurrentRequestEqualsHashtag']" class="page-link" @click="moveToNextPageWithHashtagSearch()">Next</b-button>
      </li>
    </ul>
  </div>
</template>

<script>
import AuctionArtSearchComponent from '@/components/art/AuctionArtSearchComponent.vue'
import GeneralArtSearchComponent from '@/components/art/GeneralArtSearchComponent.vue'

export default {
  name: 'ArtSearchView',
  components: {
    AuctionArtSearchComponent,
    GeneralArtSearchComponent
  },
  data() {
    return {
      sortType: {
        defaultSort: this.getSortType(),
        defaultType: 'rdate',
        auction: {
          dateDESC: '등록 날짜 최신순',
          dateASC: '등록 날짜 오래된순',
          priceDESC: '입찰 가격 높은순',
          priceASC: '입찰 가격 낮은순',
          countDESC: '입찰 횟수 많은순',
          countASC: '입찰 횟수 적은순',
          likeDESC: '좋아요 횟수 많은순',
          likeASC: '좋아요 횟수 적은순'
        },
        general: {
          dateDESC: '등록 날짜 최신순',
          dateASC: '등록 날짜 오래된순',
          priceDESC: '구매 가격 높은순',
          priceASC: '구매 가격 낮은순',
          likeDESC: '좋아요 횟수 많은순',
          likeASC: '좋아요 횟수 적은순'
        }
      },
      fetchDataList: [],
      pagination: {},
      range: []
    }
  },
  setup() {
  },
  created() {
    this.fetchData()
  },
  mounted() {
  },
  unmounted() {
  },
  methods: {
    getSortType() {
      return this.$store.getters['detailSearch/getSortType']
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
        let response
        if (this.$store.getters['detailSearch/isCurrentRequestEqualsKeyword']) {
          response = await this.axios.get(
            '/api/arts/keyword',
            {
              params: this.$store.getters['detailSearch/getKeywordSearchCriteria']
            }
          )
        } else if (this.$store.getters['detailSearch/isCurrentRequestEqualsHashtag']) {
          response = await this.axios.get(
            '/api/arts/hashtag',
            {
              params: this.$store.getters['detailSearch/getHashtagSearchCriteria']
            }
          )
        }

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
      if (this.$store.getters['detailSearch/isCurrentRequestEqualsKeyword'] === true) { // keyword search
        this.$store.commit('detailSearch/changeKeywordSearchSortType', this.sortType.defaultType)
        this.fetchData()
        this.$router.replace({
          path: '/search',
          query: {
            type: this.$store.getters['detailSearch/getTypeWithKeywordSearch'],
            sort: this.$store.getters['detailSearch/getSortWithKeywordSearch'],
            keyword: this.$store.getters['detailSearch/getKeywordWithKeywordSearch']
          }
        })
      } else { // hashtag search
        this.$store.commit('detailSearch/changeHashtagSearchSortType', this.sortType.defaultType)
        this.fetchData()
        this.$router.replace({
          path: '/search',
          query: {
            type: this.$store.getters['detailSearch/getTypeWithHashtagSearch'],
            sort: this.$store.getters['detailSearch/getSortWithHashtagSearch'],
            hashtag: this.$store.getters['detailSearch/getHashtagWithHashtagSearch']
          }
        })
      }
    },

    changeToAuctionDateDESC() {
      this.$store.commit('detailSearch/changeSortType', this.sortType.auction.dateDESC)
      this.sortType.defaultType = 'rdate'
      this.applyNewFetchData()
    },
    changeToAuctionDateASC() {
      this.$store.commit('detailSearch/changeSortType', this.sortType.auction.dateASC)
      this.sortType.defaultType = 'date'
      this.applyNewFetchData()
    },
    changeToAuctionPriceDESC() {
      this.$store.commit('detailSearch/changeSortType', this.sortType.auction.priceDESC)
      this.sortType.defaultType = 'rprice'
      this.applyNewFetchData()
    },
    changeToAuctionPriceASC() {
      this.$store.commit('detailSearch/changeSortType', this.sortType.auction.priceASC)
      this.sortType.defaultType = 'price'
      this.applyNewFetchData()
    },
    changeToAuctionLikeDESC() {
      this.$store.commit('detailSearch/changeSortType', this.sortType.auction.likeDESC)
      this.sortType.defaultType = 'rlike'
      this.applyNewFetchData()
    },
    changeToAuctionLikeASC() {
      this.$store.commit('detailSearch/changeSortType', this.sortType.auction.likeASC)
      this.sortType.defaultType = 'like'
      this.applyNewFetchData()
    },
    changeToAuctionCountDESC() {
      this.$store.commit('detailSearch/changeSortType', this.sortType.auction.countDESC)
      this.sortType.defaultType = 'rcount'
      this.applyNewFetchData()
    },
    changeToAuctionCountASC() {
      this.$store.commit('detailSearch/changeSortType', this.sortType.auction.countASC)
      this.sortType.defaultType = 'count'
      this.applyNewFetchData()
    },

    changeToGeneralDateDESC() {
      this.$store.commit('detailSearch/changeSortType', this.sortType.general.dateDESC)
      this.sortType.defaultType = 'rdate'
      this.applyNewFetchData()
    },
    changeToGeneralDateASC() {
      this.$store.commit('detailSearch/changeSortType', this.sortType.general.dateASC)
      this.sortType.defaultType = 'date'
      this.applyNewFetchData()
    },
    changeToGeneralPriceDESC() {
      this.$store.commit('detailSearch/changeSortType', this.sortType.general.priceDESC)
      this.sortType.defaultType = 'rprice'
      this.applyNewFetchData()
    },
    changeToGeneralPriceASC() {
      this.$store.commit('detailSearch/changeSortType', this.sortType.general.priceASC)
      this.sortType.defaultType = 'price'
      this.applyNewFetchData()
    },
    changeToGeneralLikeDESC() {
      this.$store.commit('detailSearch/changeSortType', this.sortType.general.likeDESC)
      this.sortType.defaultType = 'rlike'
      this.applyNewFetchData()
    },
    changeToGeneralLikeASC() {
      this.$store.commit('detailSearch/changeSortType', this.sortType.general.likeASC)
      this.sortType.defaultType = 'like'
      this.applyNewFetchData()
    },

    applyKeywordSearchPageMove() {
      this.fetchData()
      this.$router.replace({
        path: '/search',
        query: {
          type: this.$store.getters['detailSearch/getTypeWithKeywordSearch'],
          sort: this.$store.getters['detailSearch/getSortWithKeywordSearch'],
          keyword: this.$store.getters['detailSearch/getKeywordWithKeywordSearch']
        }
      })
      window.scrollTo(0, 0)
    },
    moveToPreviousPageWithKeywordSearch() {
      this.$store.commit('detailSearch/changePageToPreviousWithKeywordSearch')
      this.applyKeywordSearchPageMove()
    },
    moveToIndexPageWithKeywordSearch(index) {
      this.$store.commit('detailSearch/changePageToIndexWithKeywordSearch', index)
      this.applyKeywordSearchPageMove()
    },
    moveToNextPageWithKeywordSearch() {
      this.$store.commit('detailSearch/changePageToNextWithKeywordSearch')
      this.applyKeywordSearchPageMove()
    },

    applyHashtagSearchPageMove() {
      this.fetchData()
      this.$router.replace({
        path: '/search',
        query: {
          type: this.$store.getters['detailSearch/getTypeWithHashtagSearch'],
          sort: this.$store.getters['detailSearch/getSortWithHashtagSearch'],
          hashtag: this.$store.getters['detailSearch/getHashtagWithHashtagSearch']
        }
      })
      window.scrollTo(0, 0)
    },
    moveToPreviousPageWithHashtagSearch() {
      this.$store.commit('detailSearch/changePageToPreviousWithHashtagSearch')
      this.applyHashtagSearchPageMove()
    },
    moveToIndexPageWithHashtagSearch(index) {
      this.$store.commit('detailSearch/changePageToIndexWithHashtagSearch', index)
      this.applyHashtagSearchPageMove()
    },
    moveToNextPageWithHashtagSearch() {
      this.$store.commit('detailSearch/changePageToNextWithHashtagSearch')
      this.applyHashtagSearchPageMove()
    }
  }
}
</script>

<style scoped>

</style>
