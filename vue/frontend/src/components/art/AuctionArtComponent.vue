<template>
  <div class="col">
    <div class="card border-black-50">
      <div>
        <a>
          <img :src="require(`@/../../../src/main/resources/static/images/arts/${art.auctionArt.artStorageName}`)"
               @click="goToDetailPage(art.auctionArt.artId, art.auctionArt.artName)"
               alt="" style="width: 100%; height: 200px; margin-bottom: 10px; cursor: pointer;">
        </a>
      </div>
      <div class="card-header">
        <h3>{{ art.auctionArt.artName }}</h3>
        <h6>
          {{ art.auctionArt.artOwnerNickname }}
          <small style="font-size: 12px;">({{ art.auctionArt.artOwnerSchool }})</small>
        </h6>
      </div>
      <div class="card-body">
        <p>
          <b>현재 경매가</b><br>
          <small>{{ art.auctionArt.highestBidPrice }}원</small>
        </p>
        <p>
          <b>현재 입찰횟수</b><br>
          <small>{{ art.artBidUserIdList.length }}회</small>
        </p>
        <p>
          <b>작품 찜 횟수</b><br>
          <small>{{ art.artLikedUserIdList.length }}회</small>
        </p>
        <p>
          <b>작품 등록 날짜</b><br>
          <small>{{ translateLocalDateTime(art.auctionArt.artRegisterDate) }}</small>
        </p>
        <p>
          <b>경매 시작 날짜</b><br>
          <small>{{ translateLocalDateTime(art.auctionArt.auctionStartDate) }}</small>
        </p>
        <p>
          <b>경매 종료 날짜</b><br>
          <small>{{ translateLocalDateTime(art.auctionArt.auctionEndDate) }}</small>
        </p>
      </div>
      <div class="card-footer">
        <span class="product_tag" v-for="(tag, index) in art.hashtagList" :key="index">#{{ tag }}</span>
      </div>
    </div>
  </div>
</template>

<script>
import dayjs from 'dayjs'

export default {
  name: 'AuctionArtComponent',
  props: {
    art: Object
  },
  components: {},
  data() {
    return {
      sampleData: ''
    }
  },
  setup() {
  },
  created() {
  },
  mounted() {
  },
  unmounted() {
  },
  methods: {
    translateLocalDateTime(date) {
      return dayjs(date).format('YYYY년 MM월 DD일 HH시 mm분')
    },
    goToDetailPage(artId, artName) {
      this.$store.commit('applyCurrentSelectedArt', {
        artId: artId,
        artSaleType: 'auction'
      })
      this.$router.push({
        path: '/art',
        query: {
          name: artName
        }
      })
    }
  }
}
</script>

<style scoped>
.product_tag {
  background: lightgray;
  border-radius: 4px;
  margin: 0.5%;
  padding-left: 7px;
  padding-right: 7px;
  font-size: 0.8rem;
  font-style: italic;
  font-weight: bold;
}
</style>
