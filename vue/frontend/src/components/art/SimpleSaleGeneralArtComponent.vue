<template>
  <div class="col">
    <div class="card border-black-50">
      <div>
        <a>
          <img :src="require(`@/../../../src/main/resources/static/images/arts/${art.saleGeneralArt.artStorageName}`)"
               alt="" style="width: 100%; height: 200px; margin-bottom: 10px;">
        </a>
      </div>
      <div class="card-header">
        <h3>{{ art.saleGeneralArt.artName }}</h3>
        <h6>{{ art.saleGeneralArt.artDescription }}</h6>
      </div>
      <div class="card-body">
        <p>
          <b>구매자</b><br>
          <span>
            {{ art.saleGeneralArt.purchaseUserNickname }}
            <small style="font-size: 12px;">({{ art.saleGeneralArt.purchaseUserSchool }})</small>
          </span>
        </p>
        <p>
          <b>구매 가격</b><br>
          <small>{{ art.saleGeneralArt.purchasePrice }}원</small>
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
  name: 'SimpleSaleGeneralArtComponent',
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
