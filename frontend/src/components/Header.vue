<template>
  <header class="border-bottom">
    <div>
      <div class="container">
        <div class="d-flex flex-wrap align-items-center justify-content-center py-md-3" :style="css.barStyle">
          <div class="nav col-12 col-lg-auto me-lg-auto justify-content-center">
            <b-link @click="moveToInitPage()" class="d-flex align-items-center mb-2 mb-lg-0 text-dark text-decoration-none">
              <b-img rounded="circle" :src="css.headerImage" :style="css.headerImageStyle" alt=""></b-img>
            </b-link>
            <b-link @click="moveToInitPage()" :style="css.titleLinkStyle"><span :style="css.titleStyle">Another Art</span></b-link>
          </div>

          <div class="nav col-12 col-lg-auto me-lg-auto justify-content-center">
            <b-input-group>
              <template #prepend>
                <b-dropdown size="sm" split v-model:text="search.defaultSearch" variant="outline-primary">
                  <b-dropdown-item @click="changeToAuction()">{{ search.auction }}</b-dropdown-item>
                  <b-dropdown-item @click="changeToGeneral()">{{ search.general }}</b-dropdown-item>
                </b-dropdown>
              </template>
              <b-form-input v-model="search.inputKeyword" @keyup.enter="keywordSearch()"></b-form-input>
              <b-button variant="outline-primary" @click="keywordSearch()">
                <font-awesome-icon icon="fa-solid fa-magnifying-glass"/>
              </b-button>
              <b-button @click="activeDetectionAIModal()" variant="outline-primary" v-b-modal.aiSearchModal>
                <font-awesome-icon icon="fa-solid fa-robot"/>
              </b-button>
            </b-input-group>
          </div>
          <!-- 해시태그 기반 AI 작품 검색 모달창 -->
          <div class="modal fade" id="aiSearchModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1"
               aria-labelledby="staticBackdropLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
              <div class="modal-content h-598">
                <div class="modal-header">
                  <h5 class="modal-title" id="staticBackdropLabel">이미지를 통한 해시태그 기반 검색</h5>
                  <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                  <div class="input-group rounded text-center">
                    <h1 v-if="isHashtagEmpty" class="w-100">Loading∙∙∙</h1>
                    <div id="webcam-container" class="w-100"></div>
                    <div id="label-container" class="w-100 mt-3"></div>
                  </div>
                  <div class="text-center h-60 mt-3" style="vertical-align: bottom;">
                    <b-button variant="outline-primary" @click="hashtagSearch()" data-bs-dismiss="modal">검색하기</b-button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="text-end">
            <div v-if="isAuthenticated === false">
              <b-button @click="$router.push('/login')" pill variant="outline-info">로그인</b-button>
              <b-button @click="$router.push('/signup')" pill variant="outline-primary">회원가입</b-button>
            </div>
            <div v-else>
              <p class="text-center"><b>{{ getMemberNickname }}님</b> 환영합니다</p>
              <b-dropdown variant="primary" right split text="Menu">
                <b-dropdown-item @click="$router.push('/art/register')">
                  <font-awesome-icon icon="fa-solid fa-paintbrush" style="margin-right: 5px;"/> 작품 등록
                </b-dropdown-item>
                <b-dropdown-item @click="$router.push('/mypage')">
                  <font-awesome-icon icon="fa-solid fa-circle-user" style="margin-right: 5px;"/> 마이 페이지
                </b-dropdown-item>
                <b-dropdown-divider></b-dropdown-divider>
                <b-dropdown-item @click="logout()">
                  <font-awesome-icon icon="fa-solid fa-right-from-bracket" style="margin-right: 5px;"/> 로그아웃
                </b-dropdown-item>
              </b-dropdown>
            </div>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<script>
import * as tmImage from '@teachablemachine/image'

// the link to your model provided by Teachable Machine export panel
const URL = 'https://teachablemachine.withgoogle.com/models/cWFTIou2L/'
let model, webcam, labelContainer, maxPredictions

export default {
  name: 'Header',
  components: {},
  data() {
    return {
      css: {
        barStyle: {
          padding: '30px'
        },
        headerImage: require('../../src/assets/header.png'),
        headerImageStyle: {
          width: '50px',
          height: '50px'
        },
        titleStyle: {
          fontStyle: 'italic',
          fontSize: '30px',
          marginLeft: '10px'
        },
        titleLinkStyle: {
          textDecoration: 'none',
          color: 'black'
        }
      },
      search: {
        defaultSearch: this.getType(),
        defaultType: 'auction',
        auction: '경매 작품',
        general: '일반 작품',
        inputKeyword: this.getInputKeyword()
      },
      withHashtag: {
        hashtag: ''
      },
      aiModelCount: 0
    }
  },
  methods: {
    getType() {
      return this.$store.getters['detailSearch/getArtSearchType']
    },
    getInputKeyword() {
      return this.$store.getters['detailSearch/getKeywordWithKeywordSearch']
    },
    moveToInitPage() {
      this.changeToAuction()
      this.search.inputKeyword = ''
      this.$store.commit('detailSearch/changeSortType', '등록 날짜 최신순')
      this.$store.commit('mainPageSearch/resetMainPageCriteria')
      this.$store.commit('detailSearch/resetDetailSearchCriteria')
      this.$router.push('/')
    },
    changeToAuction() {
      this.search.defaultSearch = this.search.auction
      this.search.defaultType = 'auction'
    },
    changeToGeneral() {
      this.search.defaultSearch = this.search.general
      this.search.defaultType = 'general'
    },
    keywordSearch() {
      const searchInfo = {
        keyword: this.search.inputKeyword,
        type: this.search.defaultType
      }
      this.$store.commit('detailSearch/applyKeywordSearch', searchInfo)
      this.$router.push({
        path: '/search',
        query: {
          type: this.$store.getters['detailSearch/getTypeWithKeywordSearch'],
          sort: this.$store.getters['detailSearch/getSortWithKeywordSearch'],
          keyword: this.$store.getters['detailSearch/getKeywordWithKeywordSearch']
        }
      })
    },
    hashtagSearch() {
      const searchInfo = {
        hashtag: this.withHashtag.hashtag,
        type: this.search.defaultType
      }
      this.$store.commit('detailSearch/applyHashtagSearch', searchInfo)
      this.$router.push({
        path: '/search',
        query: {
          type: this.$store.getters['detailSearch/getTypeWithHashtagSearch'],
          sort: this.$store.getters['detailSearch/getSortWithHashtagSearch'],
          hashtag: this.$store.getters['detailSearch/getHashtagWithHashtagSearch']
        }
      })
    },
    async logout() {
      try {
        await this.axiosWithAccessToken.post('/api/logout')
        alert('로그아웃이 완료되었습니다')
        this.$store.commit('memberStore/reset')
        this.$router.push('/')
      } catch (err) {
        console.log(err)
      }
    },
    activeDetectionAIModal() {
      if (this.aiModelCount === 0) {
        this.init()
      }
      this.aiModelCount++
    },
    async init() {
      const modelURL = URL + 'model.json'
      const metadataURL = URL + 'metadata.json'

      model = await tmImage.load(modelURL, metadataURL)
      maxPredictions = model.getTotalClasses()

      const flip = true
      webcam = new tmImage.Webcam(400, 400, flip)
      await webcam.setup()
      await webcam.play()
      window.requestAnimationFrame(this.loop)

      document.getElementById('webcam-container').appendChild(webcam.canvas)
      labelContainer = document.getElementById('label-container')
      for (let i = 0; i < maxPredictions; i++) { // and class labels
        labelContainer.appendChild(document.createElement('div'))
      }
    },
    async loop() {
      webcam.update() // update the webcam frame
      await this.predict()
      window.requestAnimationFrame(this.loop)
    },
    async predict() {
      const prediction = await model.predict(webcam.canvas)

      if ((prediction[0].className = 'hand') && (prediction[0].probability.toFixed(2) >= 0.80)) {
        this.withHashtag.hashtag = '손'
        labelContainer.childNodes[0].innerHTML = '손'
      } else if ((prediction[1].className = 'face') && (prediction[1].probability.toFixed(2) >= 0.80)) {
        this.withHashtag.hashtag = '얼굴'
        labelContainer.childNodes[0].innerHTML = '얼굴'
      } else {
        labelContainer.childNodes[0].innerHTML = '추천 작품을 찾지 못했습니다.'
      }
    }
  },
  computed: {
    isHashtagEmpty() {
      return this.withHashtag.hashtag === ''
    },
    isAuthenticated() {
      return this.$store.getters['memberStore/isAuthenticated']
    },
    getMemberNickname() {
      return this.$store.getters['memberStore/getMemberNickname']
    }
  }
}
</script>

<style scoped>

</style>
