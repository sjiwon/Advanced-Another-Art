<template>
  <div class="col">
    <div class="card border-black-50">
      <div>
        <a>
          <img :src="require(`@/../../../src/main/resources/static/images/arts/${art.saleAuctionArt.artStorageName}`)"
               alt="" style="width: 100%; height: 200px; margin-bottom: 10px;">
        </a>
      </div>
      <div class="card-header">
        <h3>{{ art.saleAuctionArt.artName }}</h3>
        <h6>{{ art.saleAuctionArt.artDescription }}</h6>
      </div>
      <div class="card-body">
        <p>
          <b>구매자</b><br>
          <span>
            {{ art.saleAuctionArt.purchaseUserNickname }}
            <small style="font-size: 12px;">({{ art.saleAuctionArt.purchaseUserSchool }})</small>
          </span>
        </p>
        <p>
          <b>낙찰 가격</b><br>
          <small>{{ art.saleAuctionArt.purchasePrice }}원</small>
        </p>
      </div>
      <div class="card-footer">
        <span class="product_tag" v-for="(tag, index) in art.hashtagList" :key="index">#{{ tag }}</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SimpleSaleAuctionArtComponent',
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
  methods: {}
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
