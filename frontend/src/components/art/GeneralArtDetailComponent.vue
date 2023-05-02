<template>
  <div class="container" style="margin-bottom: 50px;">
    <div class="row">
      <div class="col-md-5">
        <div class="row">
          <div class="col-md-12 offset-md-1 mt-5">
            <img :src="require(`/public/images/arts/${generalArt.art.artStorageName}`)"
                 style="width: 100%; height: 100%;" alt="">
          </div>
        </div>
      </div>

      <div class="col-lg-5 offset-lg-1 mt-4">
        <div class="s_product_text">
          <div class="row g-2 mt-2">
            <div>
              <h2>
                {{ generalArt.art.artName }} - {{ generalArt.art.ownerNickname }}
                <span v-if="isAlreadySoldOut === true" style="color: red; font-size:20px;">(판매 완료)</span>
              </h2>
              <p>{{ generalArt.art.artDescription }}</p>
              <span class="product_tag" v-for="(tag, index) in generalArt.hashtags" :key="index">#{{ tag }}</span>
            </div>

            <hr>

            <div>
              <p>작품 찜 횟수 | {{ generalArt.likeMarkingMembers.length }}회</p>
              <p>작품 등록 날짜 | {{ translateLocalDateTime(generalArt.art.artRegistrationDate) }}</p>
            </div>

            <hr>

            <div>
              <h4>작품 가격 | {{ generalArt.art.artPrice }}원</h4>
            </div>
          </div>

          <div class="card_area">
            <div class="row g-3">
              <div v-if="isAlreadySoldOut === true">
                <span style="color: blueviolet; font-size: 20px;">
                  구매자 <font-awesome-icon icon="fa-solid fa-user-secret" /> | {{ generalArt.art.buyerNickname }}
                  <span style="font-size: 13px;">{{ generalArt.art.buyerSchool }}</span>
                </span>
              </div>
              <div v-if="isAlreadySoldOut === false">
                <span v-if="Object.values(generalArt.likeMarkingMembers).includes(currentAuthenticatedUserId) === false">
                    <b-button @click="likeMarking()" variant="outline-danger">
                      <font-awesome-icon icon="fa-solid fa-heart" style="margin-right: 5px;"/> 찜 등록
                    </b-button>
                  </span>
                <span v-else>
                    <b-button @click="likeCancel()" variant="danger">
                      <font-awesome-icon icon="fa-solid fa-heart" style="margin-right: 5px;"/> 찜 취소
                    </b-button>
                  </span>
                <span v-if="isOwnWork === true">
                  <b-button @click="deleteGeneralArt()" variant="danger">작품 삭제하기</b-button>
                </span>
                <span v-else>
                  <b-button variant="primary" v-b-modal.purchaseModal>
                    <font-awesome-icon icon="fa-solid fa-money-check-dollar" style="margin-right: 5px;"/> 작품 구매하기
                  </b-button>
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 일반 작품 구매 모달창 -->
      <div class="modal fade" id="purchaseModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1"
           aria-labelledby="staticBackdropLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title" id="staticBackdropLabel">작품 구매</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body text-center">
              <h5><b>{{ generalArt.art.artName }}</b>의 가격은 <b>{{ generalArt.art.artPrice }}포인트</b>입니다</h5>
              <h5>구매를 진행하시겠습니까?</h5>
            </div>
            <div class="modal-footer">
              <div class="mx-auto">
                <b-button @click="generalArtPurchase()" variant="primary" data-bs-dismiss="modal">구매하기</b-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <br>
</template>

<script>
import dayjs from 'dayjs'

export default {
  name: 'GeneralArtDetailComponent',

  props: {
    generalArt: Object
  },

  data() {
    return {
      currentAuthenticatedUserId: this.$store.getters['memberStore/getMemberId']
    }
  },

  methods: {
    translateLocalDateTime(date) {
      return dayjs(date).format('YYYY년 MM월 DD일 HH시 mm분')
    },
    async likeMarking() {
      try {
        await this.axiosWithAccessToken.post(`/api/art/${this.generalArt.art.artId}/like`)
        this.$router.go()
      } catch (err) {
        alert(err.response.data.message)
      }
    },
    async likeCancel() {
      try {
        await this.axiosWithAccessToken.delete(`/api/art/${this.generalArt.art.artId}/like`)
        this.$router.go()
      } catch (err) {
        alert(err.response.data.message)
      }
    },
    async deleteGeneralArt() {
      const check = confirm('작품을 정말 삭제하시겠습니까?')
      if (check) {
        try {
          await this.axiosWithAccessToken.delete(`/api/art/${this.generalArt.art.artId}`)
          alert('작품이 삭제되었습니다')
          this.$router.push('/')
        } catch (err) {
          alert(err.response.data.message)
        }
      }
    },
    async generalArtPurchase() {
      try {
        await this.axiosWithAccessToken.post(`/api/art/${this.generalArt.art.artId}/purchase`)
        alert('구매가 완료되었습니다')
        this.$router.push('/')
      } catch (err) {
        alert(err.response.data.message)
      }
    }
  },
  computed: {
    isOwnWork() {
      return this.currentAuthenticatedUserId === this.generalArt.art.artOwnerId
    },
    isAlreadySoldOut() {
      return this.generalArt.art.artStatus === 'SOLD_OUT'
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
