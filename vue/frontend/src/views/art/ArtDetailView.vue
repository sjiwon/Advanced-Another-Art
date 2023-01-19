<template>
  <div>
    <AuctionArtDetailComponent v-if="artSaleType === 'auction'" :art="art"/>
    <GeneralArtDetailComponent v-if="artSaleType === 'general'" :art="art"/>
  </div>
</template>

<script>
import axios from 'axios'
import AuctionArtDetailComponent from '@/components/art/AuctionArtDetailComponent.vue'
import GeneralArtDetailComponent from '@/components/art/GeneralArtDetailComponent.vue'

export default {
  name: 'ArtDetailView',
  components: {
    AuctionArtDetailComponent,
    GeneralArtDetailComponent
  },
  data() {
    return {
      artId: this.$store.getters.getCurrentSelectedArtId,
      artSaleType: this.$store.getters.getCurrentSelectedArtSaleType,
      art: {}
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
        const response = await axios.get(`/api/arts/${this.artId}`)
        this.art = response.data.art
      } catch (err) {
        alert(err.response.data.message)
      }
    }
  }
}
</script>

<style scoped>

</style>
