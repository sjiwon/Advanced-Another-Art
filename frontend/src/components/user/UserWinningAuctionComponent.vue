<template>
  <div>
    <h2 class="text-center mb-4 fw-bold text-black">낙찰된 경매 작품</h2>
    <div class="album py-5">
      <div class="container">
        <div class="row row-cols-1 row-cols-sm-2 row-cols-md-3 g-3">
          <div v-for="(art, index) in fetchDataList" :key="index">
            <SimpleWinningAuctionArtComponent :auction-art="art"/>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import SimpleWinningAuctionArtComponent from '@/components/art/SimpleWinningAuctionArtComponent.vue'

export default {
  name: 'UserWinningAuctionComponent',
  components: {
    SimpleWinningAuctionArtComponent
  },
  data() {
    return {
      fetchDataList: []
    }
  },

  created() {
    this.fetchData()
  },

  methods: {
    async fetchData() {
      try {
        const memberId = this.$store.getters['memberStore/getMemberId']
        const response = await this.axiosWithAccessToken.get(`/api/members/${memberId}/winning-auctions`)
        this.fetchDataList = response.data.result
      } catch (err) {
        alert(err.response.data.message)
      }
    }
  }
}
</script>

<style scoped>

</style>
