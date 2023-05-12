<template>
  <div>
    <h2 class="text-center mb-4 fw-bold text-black">구매한 작품</h2>
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
          <div v-if="isAuctionSelected === true && isGeneralSelected === false" v-for="(art, index) in auctionArtFechDataList" :key="index">
            <SimplePurchaseAuctionArtComponent :auction-art="art"/>
          </div>
          <div v-if="isAuctionSelected === false && isGeneralSelected === true" v-for="(art, index) in generalArtFechDataList" :key="index">
            <SimplePurchaseGeneralArtComponent :general-art="art"/>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
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
      auctionArtFechDataList: [],
      generalArtFechDataList: []
    }
  },

  created() {
    this.fetchData()
  },

  methods: {
    async fetchData() {
      try {
        const memberId = this.$store.getters['memberStore/getMemberId']
        const response = await this.axiosWithAccessToken.get(`/api/members/${memberId}/arts/purchase`)
        this.auctionArtFechDataList = response.data.tradedAuctions
        this.generalArtFechDataList = response.data.tradedGenerals
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
    }
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
