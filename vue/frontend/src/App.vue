<template>
  <div>
    <Header></Header><br>
    <router-view :key="$route.fullPath"/><br>
    <Footer></Footer>
  </div>
</template>

<script>
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'

export default {
  name: 'App',
  components: {
    Header,
    Footer
  }
}
</script>

<style scoped>

</style>
