<template>
  <div class="col">
    <div v-if="generalArt.artStatus === '판매중'">
      <div class="card border-black-50">
        <div>
          <a>
            <img :src="`${generalArt.artStorageUrl}`"
                 @click="goToDetailPage(generalArt.artId, generalArt.artName)"
                 alt="" style="width: 100%; height: 200px; margin-bottom: 10px; cursor: pointer;">
          </a>
        </div>
        <div class="card-header">
          <h3>{{ generalArt.artName }}</h3>
          <h6>
            {{ generalArt.ownerNickname }}
            <small style="font-size: 12px;">({{ generalArt.ownerSchool }})</small>
          </h6>
        </div>
        <div class="card-body">
          <p>
            <b>가격</b><br>
            <small>{{ generalArt.artPrice }}원</small>
          </p>
          <p>
            <b>작품 찜 횟수</b><br>
            <small>{{ generalArt.likeMembers.length }}회</small>
          </p>
          <p>
            <b>작품 등록 날짜</b><br>
            <small>{{ translateLocalDateTime(generalArt.artRegistrationDate) }}</small>
          </p>
        </div>
        <div class="card-footer">
          <span class="product_tag" v-for="(tag, index) in generalArt.hashtags" :key="index">#{{ tag }}</span>
        </div>
      </div>
    </div>

    <div v-else>
      <div class="card border-black-50">
        <div>
          <a>
            <img :src="`${generalArt.artStorageUrl}`"
                 @click="goToDetailPage(generalArt.artId, generalArt.artName)"
                 alt="" style="width: 100%; height: 200px; margin-bottom: 10px; cursor: pointer;" :style="applyOpacity">
          </a>
        </div>
        <div class="card-header">
          <h3 :style="applyOpacity">{{ generalArt.artName }} <b style="color: red; font-size: 18px;"
                                                                :style="applyOpacity">(판매 완료)</b></h3>
          <h6 :style="applyOpacity">
            {{ generalArt.ownerNickname }}
            <small style="font-size: 12px;" :style="applyOpacity">({{ generalArt.ownerSchool }})</small>
          </h6>
        </div>
        <div class="card-body">
          <p>
            <b :style="applyOpacity">가격</b><br>
            <small :style="applyOpacity">{{ generalArt.artPrice }}원</small>
          </p>
          <p>
            <b :style="applyOpacity">작품 찜 횟수</b><br>
            <small :style="applyOpacity">{{ generalArt.likeMembers.length }}회</small>
          </p>
          <p>
            <b :style="applyOpacity">작품 등록 날짜</b><br>
            <small :style="applyOpacity">{{ translateLocalDateTime(generalArt.artRegistrationDate) }}</small>
          </p>
        </div>
        <div class="card-footer">
          <span :style="applyOpacity" class="product_tag" v-for="(tag, index) in generalArt.hashtags"
                :key="index">#{{ tag }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import dayjs from 'dayjs'

export default {
  name: 'GeneralArtSearchComponent',
  props: {
    generalArt: Object
  },
  components: {},
  data() {
    return {
      applyOpacity: {
        opacity: 0.5
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
  methods: {
    translateLocalDateTime(date) {
      return dayjs(date).format('YYYY년 MM월 DD일 HH시 mm분')
    },
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
