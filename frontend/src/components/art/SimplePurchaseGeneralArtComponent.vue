<template>
  <div class="col">
    <div class="card border-black-50">
      <div>
        <a>
          <img :src="`${generalArt.art.storageName}`"
               @click="goToDetailPage(generalArt.art.id, generalArt.art.name)"
               alt="" style="width: 100%; height: 200px; margin-bottom: 10px; cursor: pointer;">
        </a>
      </div>
      <div class="card-header">
        <h3>{{ generalArt.art.name }}</h3>
        <h6>{{ generalArt.art.description }}</h6>
      </div>
      <div class="card-body">
        <p>
          <b>판매자</b><br>
          <span>
            {{ generalArt.owner.nickname }}
            <small style="font-size: 12px;">({{ generalArt.owner.school }})</small>
          </span>
        </p>
        <p>
          <b>구매 가격</b><br>
          <small>{{ generalArt.art.price }}원</small>
        </p>
      </div>
      <div class="card-footer">
        <span class="product_tag" v-for="(tag, index) in generalArt.art.hashtags" :key="index">#{{ tag }}</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SimpleGeneralArtComponent',
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
