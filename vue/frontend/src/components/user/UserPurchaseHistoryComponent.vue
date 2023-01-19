<template>
  <div>
    <h2 class="text-center mb-4 fw-bold text-black">작품 구매 내역</h2>
    <div class="btn-group" style="border: none; margin-left: 20px;" role="group">
      <b-button :class="{ selectedBackgroundColor: isAuctionSelected, notSelectedBackground: isGeneralSelected }" @click="changeSaleType('auction')">
        경매 작품
      </b-button>
      <b-button :class="{ selectedBackgroundColor: isGeneralSelected, notSelectedBackground: isAuctionSelected }" @click="changeSaleType('general')">
        일반 작품
      </b-button>
    </div>

    <div class="album py-5">
      <div class="container">
        <div class="row row-cols-1 row-cols-sm-2 row-cols-md-3 g-3">
          <div v-if="isAuctionSelected === true && isGeneralSelected === false" v-for="(art, index) in fetchDataList.purchaseAuctionArts" :key="index">
            <SimplePurchaseAuctionArtComponent :art="art"/>
          </div>
          <div v-if="isAuctionSelected === false && isGeneralSelected === true" v-for="(art, index) in fetchDataList.purchaseGeneralArts" :key="index">
            <SimplePurchaseGeneralArtComponent :art="art"/>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import SimplePurchaseAuctionArtComponent from '@/components/art/SimplePurchaseAuctionArtComponent.vue'
import SimplePurchaseGeneralArtComponent from '@/components/art/SimplePurchaseGeneralArtComponent.vue'

export default {
  name: 'UserPurchaseHistoryComponent',
  components: {
    SimplePurchaseAuctionArtComponent,
    SimplePurchaseGeneralArtComponent
  },
  data() {
    return {
      isAuctionSelected: true,
      isGeneralSelected: false,
      fetchDataList: [],
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
    async fetchData() {
      try {
        const response = await axios.get('/api/user/art/purchase')
        this.fetchDataList = response.data
      } catch (err) {
        alert(err.response.data.message)
      }
    },
    changeSaleType(saleType) {
      if (saleType === 'auction') {
        this.isAuctionSelected = true
        this.isGeneralSelected = false
      } else if (saleType === 'general') {
        this.isAuctionSelected = false
        this.isGeneralSelected = true
      }
    },
  }
}
</script>

<style scoped>
.selectedBackgroundColor {
  opacity: 1;
  background-color: powderblue;
}

.notSelectedBackground {
  opacity: 0.5;
  background-color: white;
}
</style>
