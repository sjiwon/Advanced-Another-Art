<template>
  <footer class="bg-secondary">
    <div class="text-center p-3">
      <span class="text-black" :style="copyrightFontSize">© Copyright 2022. AnotherArt</span><br>
      <span class="text-black" :style="descriptionFontSize">
        <a href="https://github.com/sjiwon"><font-awesome-icon icon="fa-brands fa-github" :style="textColor"/> <b :style="textColor">Seo Ji Won</b></a> Refactored
      </span>
    </div>
  </footer>
</template>

<script>
export default {
  name: 'Footer',
  components: {},
  data() {
    return {
      copyrightFontSize: {
        fontSize: '16px'
      },
      descriptionFontSize: {
        fontSize: '14px'
      },
      textColor: {
        color: 'black'
      }
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

</style>
