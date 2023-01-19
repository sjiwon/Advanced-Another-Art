<template>
  <div class="container" style="margin-bottom: 50px;">
    <div class="row">
      <div class="col-md-5">
        <div class="row">
          <div class="col-md-12 offset-md-1 mt-5">
            <img :src="require(`@/../../../src/main/resources/static/images/arts/${art.auctionArt.artStorageName}`)"
                 style="width: 100%; height: 100%;" alt="">
          </div>
        </div>
      </div>

      <div class="col-lg-5 offset-lg-1 mt-4">
        <div class="s_product_text">
          <div class="row g-2 mt-2">
            <div>
              <h2>
                {{ art.auctionArt.artName }} - {{ art.auctionArt.artOwnerNickname }}
                <span v-if="isCurrentlyActiveAuction === false" style="color: red; font-size:20px;">(경매 완료)</span>
              </h2>
              <p>{{ art.auctionArt.artDescription }}</p>
              <span class="product_tag" v-for="(tag, index) in art.hashtagList" :key="index">#{{ tag }}</span>
            </div>

            <hr>

            <div>
              <p>입찰 횟수 | {{ art.artBidUserIdList.length }}회</p>
              <p>작품 찜 횟수 | {{ art.artLikedUserIdList.length }}회</p>
            </div>

            <hr>

            <div>
              <p>작품 등록 날짜 | {{ translateLocalDateTime(art.auctionArt.artRegisterDate) }}</p>
              <p>경매 시작 날짜 | {{ translateLocalDateTime(art.auctionArt.auctionStartDate) }}</p>
              <p>경매 종료 날짜 | {{ translateLocalDateTime(art.auctionArt.auctionEndDate) }}</p>
            </div>

            <hr>

            <div v-if="isCurrentlyActiveAuction === true" class="mb-2">
              <h4>시작 경매가 | {{ art.auctionArt.artInitPrice }}원</h4>
              <h4>
                현재 경매가 | {{ art.auctionArt.highestBidPrice }}원
                <span v-if="art.auctionArt.highestBidUserNickname !== null" style="font-size: 20px;">
                  ({{ art.auctionArt.highestBidUserNickname }} - {{ art.auctionArt.highestBidUserSchool }})
                </span>
              </h4>
            </div>
            <div v-else class="mb-2">
              <h4 style="color: blue">
                <font-awesome-icon icon="fa-solid fa-hand-holding-dollar"/> {{ art.auctionArt.highestBidPrice }}원 낙찰 완료
              </h4>
              <span style="color: black; font-size: 20px;">낙찰자 | {{ art.auctionArt.highestBidUserNickname }} ({{ art.auctionArt.highestBidUserSchool }})</span>
            </div>
          </div>

          <div class="card_area">
            <div class="row g-3">
              <div v-if="isOwnWork === true">
                <b-button @click="deleteAuctionArt()" variant="danger">
                  <font-awesome-icon icon="fa-solid fa-circle-xmark" style="margin-right: 5px;"/>
                  작품 삭제하기
                </b-button>
              </div>
              <div v-else>
                <span v-if="isCurrentlyActiveAuction === true">
                  <span v-if="Object.values(art.artLikedUserIdList).includes(currentAuthenticatedUserId) === false">
                    <b-button @click="likeMarking()" variant="outline-danger">
                      <font-awesome-icon icon="fa-solid fa-heart" style="margin-right: 5px;"/> 찜 등록
                    </b-button>
                  </span>
                  <span v-else>
                    <b-button @click="likeCancel()" variant="danger">
                      <font-awesome-icon icon="fa-solid fa-heart" style="margin-right: 5px;"/> 찜 취소
                    </b-button>
                  </span>
                  <b-button variant="outline-primary" :disabled="currentAuthenticatedUserId === art.auctionArt.highestBidUserId" v-b-modal.bidModal>
                    <font-awesome-icon icon="fa-solid fa-gavel" style="margin-right: 5px;"/> 작품 입찰하기
                  </b-button>
                </span>
                <span v-else>
                  <b-button @click="auctionArtPurchase()" variant="success" :disabled="currentAuthenticatedUserId !== art.auctionArt.highestBidUserId">
                    <font-awesome-icon icon="fa-solid fa-money-check-dollar" style="margin-right: 5px;"/> 작품 구매하기
                  </b-button>
                </span>
              </div>
            </div>
          </div>

          <!-- 경매 작품 비드 모달창 -->
          <div class="modal fade" id="bidModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1"
               aria-labelledby="staticBackdropLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
              <div class="modal-content">
                <div class="modal-header">
                  <h5 class="modal-title" id="staticBackdropLabel">경매 비드</h5>
                  <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                  <div class="input-group rounded">
                    <input type="number" class="form-control rounded" placeholder="비드 금액을 입력해주세요" v-model.number="bidPrice" @keyup="bidTracker()"/>
                  </div>
                  <p v-show="bidCheck.isNotMeetCondition" :style="bidCheck.errorCss" v-html="bidCheck.errorMessage"></p>
                  <p v-show="bidCheck.isMeetCondition" :style="bidCheck.successCss" v-html="bidCheck.successMessage"></p>
                  <div class="text-center mt-4">
                    <b-button @click="auctionArtBid()" variant="primary" data-bs-dismiss="modal" :disabled="bidCheck.isNotMeetCondition === true">비드하기</b-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import dayjs from 'dayjs'

export default {
  name: 'AuctionArtDetailComponent',
  components: {},
  props: {
    art: Object
  },
  data() {
    return {
      currentAuthenticatedUserId: this.$store.getters.getUserId,
      bidPrice: this.art.auctionArt.highestBidPrice,
      bidCheck: {
        errorCss: {
          marginLeft: '3px',
          marginTop: '5px',
          textAlign: 'left',
          fontSize: '13px',
          color: 'red'
        },
        successCss: {
          marginLeft: '3px',
          marginTop: '5px',
          textAlign: 'left',
          fontSize: '13px',
          color: 'blue'
        },
        isNotMeetCondition: true,
        isMeetCondition: false,
        errorMessage: `현재 비드 가격은 [${this.art.auctionArt.highestBidPrice}포인트]입니다<br>더 높은 가격으로 비드를 진행해주세요`,
        successMessage: `비드를 진행하면 취소는 절대 불가능합니다<br>신중히 고민해보시고 결정해주세요`
      },
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
    async deleteAuctionArt() {
      const check = confirm('작품을 정말 삭제하시겠습니까?')
      if (check) {
        try {
          await axios.delete(`/api/art/${this.art.auctionArt.artId}`)
          alert('작품이 삭제되었습니다')
          this.$router.push('/')
        } catch (err) {
          alert(err.response.data.message)
        }
      }
    },
    async likeMarking() {
      try {
        let formData = new FormData()
        formData.append('artId', this.art.auctionArt.artId)

        await axios.post('/api/art/like', formData, {
          headers: { 'Content-Type': 'x-www-form-urlencoded' }
        })
        this.$router.go()
      } catch (err) {
        alert(err.response.data.message)
      }
    },
    async likeCancel() {
      try {
        let formData = new FormData()
        formData.append('artId', this.art.auctionArt.artId)

        await axios.post('/api/art/cancel', formData, {
          headers: { 'Content-Type': 'x-www-form-urlencoded' }
        })
        this.$router.go()
      } catch (err) {
        alert(err.response.data.message)
      }
    },
    async auctionArtBid() {
      const check = confirm(`${this.bidPrice}포인트로 비드를 진행하시겠습니까?`)
      if (check) {
        try {
          const jsonData = {
            auctionId: this.art.auctionArt.auctionId,
            userId: this.currentAuthenticatedUserId,
            bidPrice: this.bidPrice
          }

          await axios.post('/api/bid', jsonData)
          alert('비드에 성공했습니다')
          this.$router.go()
        } catch (err) {
          alert(err.response.data.message)
        }
      }
    },
    async auctionArtPurchase() {
      try {
        await axios.post(
          '/api/art/purchase/auction',
          {
            auctionId: this.art.auctionArt.auctionId,
            userId: this.currentAuthenticatedUserId
          }
        )
        alert('구매가 완료되었습니다')
        this.$router.push('/')
      } catch (err) {
        alert(err.response.data.message)
      }
    },
    bidTracker() {
      if (this.bidPrice <= this.art.auctionArt.highestBidPrice) {
        this.bidCheck.isNotMeetCondition = true
        this.bidCheck.isMeetCondition = false
      } else {
        this.bidCheck.isNotMeetCondition = false
        this.bidCheck.isMeetCondition = true
      }
    },
  },
  computed: {
    isOwnWork() {
      return this.currentAuthenticatedUserId === this.art.auctionArt.artOwnerId
    },
    isCurrentlyActiveAuction() {
      return new Date(new Date().getTime() - new Date().getTimezoneOffset() * 60000).toISOString() < this.art.auctionArt.auctionEndDate
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
