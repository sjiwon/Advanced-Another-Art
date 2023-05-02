<template>
  <div class="col">
    <div class="card border-black-50">
      <div>
        <a>
          <img :src="require(`/public/images/arts/${auctionArt.art.artStorageName}`)"
               @click="goToDetailPage(auctionArt.art.artId, auctionArt.art.artName)"
               alt="" style="width: 100%; height: 200px; margin-bottom: 10px; cursor: pointer;">
        </a>
      </div>
      <div class="card-header">
        <h3>{{ auctionArt.art.artName }}</h3>
        <h6>{{ auctionArt.art.artDescription }}</h6>
      </div>
      <div class="card-body">
        <p>
          <b>구매자</b><br>
          <span>
            {{ auctionArt.art.buyerNickname }}
            <small style="font-size: 12px;">({{ auctionArt.art.buyerSchool }})</small>
          </span>
        </p>
        <p>
          <b>판매 가격</b><br>
          <small>{{ auctionArt.art.purchasePrice }}원</small>
        </p>
      </div>
      <div class="card-footer">
        <span class="product_tag" v-for="(tag, index) in auctionArt.hashtags" :key="index">#{{ tag }}</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SimpleSaleAuctionArtComponent',
  props: {
    auctionArt: Object
  },
  methods: {
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
