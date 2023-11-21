<template>
  <div>
    <AuctionArtDetailComponent v-if="artType === 'auction'" :auction-art="art"/>
    <GeneralArtDetailComponent v-if="artType === 'general'" :general-art="art"/>
  </div>
</template>

<script>
import AuctionArtDetailComponent from '@/components/art/AuctionArtDetailComponent.vue'
import GeneralArtDetailComponent from '@/components/art/GeneralArtDetailComponent.vue'
import {API_PATH} from "@/apis/api";

export default {
  name: 'ArtDetailView',
  components: {
    AuctionArtDetailComponent,
    GeneralArtDetailComponent
  },
  data() {
    return {
      artId: this.$store.getters['detailSearch/getCurrentSelectedArtId'],
      artType: this.$store.getters['detailSearch/getCurrentSelectedArtType'],
      art: {}
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    async fetchData() {
      try {
        const response = await this.axios.get(API_PATH.ART.GET_SINGLE_ART(this.artId))
        this.art = response.data.result
      } catch (err) {
        alert(err.response.data.message)
      }
    }
  }
}
</script>

<style scoped>

</style>
