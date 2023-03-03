<template>
  <div class="col">
    <div class="card border-black-50">
      <div>
        <a>
          <img :src="require(`@/../../../src/main/resources/static/images/arts/${generalArt.art.artStorageName}`)"
               @click="goToDetailPage(generalArt.art.artId, generalArt.art.artName)"
               alt="" style="width: 100%; height: 200px; margin-bottom: 10px; cursor: pointer;">
        </a>
      </div>
      <div class="card-header">
        <h3>{{ generalArt.art.artName }}</h3>
        <h6>{{ generalArt.art.artDescription }}</h6>
      </div>
      <div class="card-body">
        <p>
          <b>구매자</b><br>
          <span>
            {{ generalArt.art.buyerNickname }}
            <small style="font-size: 12px;">({{ generalArt.art.buyerSchool }})</small>
          </span>
        </p>
        <p>
          <b>판매 가격</b><br>
          <small>{{ generalArt.art.purchasePrice }}원</small>
        </p>
      </div>
      <div class="card-footer">
        <span class="product_tag" v-for="(tag, index) in generalArt.hashtags" :key="index">#{{ tag }}</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SimpleSaleGeneralArtComponent',
  props: {
    generalArt: Object
  },
  methods: {
    goToDetailPage(artId, artName) {
      this.$store.commit('detailSearch/applyCurrentSelectedArt', {
        artId: artId,
        artType: 'general'
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
