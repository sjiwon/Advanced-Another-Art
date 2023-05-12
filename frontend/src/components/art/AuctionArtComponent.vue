<template>
  <div class="col">
    <div class="card border-black-50">
      <div>
        <a>
          <img :src="`${auctionArt.art.storageName}`"
               @click="goToDetailPage(auctionArt.art.id, auctionArt.art.name)"
               alt="" style="width: 100%; height: 200px; margin-bottom: 10px; cursor: pointer;">
        </a>
      </div>
      <div class="card-header">
        <h3>{{ auctionArt.art.name }}</h3>
        <h6>
          {{ auctionArt.owner.nickname }}
          <small style="font-size: 12px;">({{ auctionArt.owner.school }})</small>
        </h6>
      </div>
      <div class="card-body">
        <p>
          <b>최고 입찰가</b><br>
          <small>{{ auctionArt.auction.highestBidPrice }}원</small>
        </p>
        <p>
          <b>입찰횟수</b><br>
          <small>{{ auctionArt.auction.bidCount }}회</small>
        </p>
        <p>
          <b>작품 좋아요 횟수</b><br>
          <small>{{ auctionArt.art.likeMembers.length }}회</small>
        </p>
        <p>
          <b>작품 등록 날짜</b><br>
          <small>{{ translateLocalDateTime(auctionArt.art.registrationDate) }}</small>
        </p>
        <p>
          <b>경매 시작 날짜</b><br>
          <small>{{ translateLocalDateTime(auctionArt.auction.startDate) }}</small>
        </p>
        <p>
          <b>경매 종료 날짜</b><br>
          <small>{{ translateLocalDateTime(auctionArt.auction.endDate) }}</small>
        </p>
      </div>
      <div class="card-footer">
        <span class="product_tag" v-for="(tag, index) in auctionArt.art.hashtags" :key="index">#{{ tag }}</span>
      </div>
    </div>
  </div>
</template>

<script>
import dayjs from 'dayjs'

export default {
  name: 'AuctionArtComponent',

  props: {
    auctionArt: Object
  },

  data() {
    return {
      sampleData: ''
    }
  },

  methods: {
    translateLocalDateTime(date) {
      return dayjs(date).format('YYYY년 MM월 DD일 HH시 mm분')
    },
    goToDetailPage(artId, artName) {
      this.$store.commit('detailSearch/applyCurrentSelectedArt', {
        artId: artId,
        artType: 'auction'
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
