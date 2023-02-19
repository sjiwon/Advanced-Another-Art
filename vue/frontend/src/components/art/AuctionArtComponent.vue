<template>
  <div class="col">
    <div class="card border-black-50">
      <div>
        <a>
          <img :src="require(`@/../../../src/main/resources/static/images/arts/${auctionArt.art.artStorageName}`)"
               @click="goToDetailPage(auctionArt.art.artId, auctionArt.art.artName)"
               alt="" style="width: 100%; height: 200px; margin-bottom: 10px; cursor: pointer;">
        </a>
      </div>
      <div class="card-header">
        <h3>{{ auctionArt.art.artName }}</h3>
        <h6>
          {{ auctionArt.art.ownerNickname }}
          <small style="font-size: 12px;">({{ auctionArt.art.ownerSchool }})</small>
        </h6>
      </div>
      <div class="card-body">
        <p>
          <b>최고 입찰가</b><br>
          <small>{{ auctionArt.art.highestBidPrice }}원</small>
        </p>
        <p>
          <b>입찰횟수</b><br>
          <small>{{ auctionArt.bidCount }}회</small>
        </p>
        <p>
          <b>작품 좋아요 횟수</b><br>
          <small>{{ auctionArt.likeMarkingMembers.length }}회</small>
        </p>
        <p>
          <b>작품 등록 날짜</b><br>
          <small>{{ translateLocalDateTime(auctionArt.art.artRegistrationDate) }}</small>
        </p>
        <p>
          <b>경매 시작 날짜</b><br>
          <small>{{ translateLocalDateTime(auctionArt.art.auctionStartDate) }}</small>
        </p>
        <p>
          <b>경매 종료 날짜</b><br>
          <small>{{ translateLocalDateTime(auctionArt.art.auctionEndDate) }}</small>
        </p>
      </div>
      <div class="card-footer">
        <span class="product_tag" v-for="(tag, index) in auctionArt.hashtags" :key="index">#{{ tag }}</span>
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
