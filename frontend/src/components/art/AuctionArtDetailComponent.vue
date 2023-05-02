<template>
  <div class="container" style="margin-bottom: 50px;">
    <div class="row">
      <div class="col-md-5">
        <div class="row">
          <div class="col-md-12 offset-md-1 mt-5">
            <img :src="require(`/public/images/arts/${auctionArt.art.artStorageName}`)"
                 style="width: 100%; height: 100%;" alt="">
          </div>
        </div>
      </div>

      <div class="col-lg-5 offset-lg-1 mt-4">
        <div class="s_product_text">
          <div class="row g-2 mt-2">
            <div>
              <h2>
                {{ auctionArt.art.artName }} - {{ auctionArt.art.ownerNickname }}
                <span v-if="isCurrentlyActiveAuction === false" style="color: red; font-size:20px;">(경매 완료)</span>
              </h2>
              <p>{{ auctionArt.art.artDescription }}</p>
              <span class="product_tag" v-for="(tag, index) in auctionArt.hashtags" :key="index">#{{ tag }}</span>
            </div>

            <hr>

            <div>
              <p>입찰 횟수 | {{ auctionArt.bidCount }}회</p>
              <p>작품 찜 횟수 | {{ auctionArt.likeMarkingMembers.length }}회</p>
            </div>

            <hr>

            <div>
              <p>작품 등록 날짜 | {{ translateLocalDateTime(auctionArt.art.artRegistrationDate) }}</p>
              <p>경매 시작 날짜 | {{ translateLocalDateTime(auctionArt.art.auctionStartDate) }}</p>
              <p>경매 종료 날짜 | {{ translateLocalDateTime(auctionArt.art.auctionEndDate) }}</p>
            </div>

            <hr>

            <div v-if="isCurrentlyActiveAuction === true" class="mb-2">
              <h4>시작 경매가 | {{ auctionArt.art.artPrice }}원</h4>
              <h4>
                현재 경매가 | {{ auctionArt.art.highestBidPrice }}원
                <span v-if="auctionArt.art.highestBidderNickname !== null" style="font-size: 17px;">
                  ({{ auctionArt.art.highestBidderNickname }} - {{ auctionArt.art.highestBidderSchool }})
                </span>
              </h4>
            </div>
            <div v-else class="mb-2">
              <h4 style="color: blue">
                <font-awesome-icon icon="fa-solid fa-hand-holding-dollar"/> {{ auctionArt.art.highestBidPrice }}원 낙찰 완료
              </h4>
              <span v-if="isForSale" style="color: black; font-size: 20px;">
                낙찰자 | {{ auctionArt.art.highestBidderNickname }} ({{ auctionArt.art.highestBidderSchool }})
              </span>
              <span v-if="isForSale === false" style="color: black; font-size: 20px;">
                구매자 <font-awesome-icon icon="fa-solid fa-user-secret" /> | {{ auctionArt.art.highestBidderNickname }} ({{ auctionArt.art.highestBidderSchool }})
              </span>
            </div>
          </div>

          <div v-if="isAuthenticated" class="card_area">
            <div class="row g-3">
              <div v-if="isOwnWork === true">
                <b-button @click="deleteAuctionArt()" variant="danger">
                  <font-awesome-icon icon="fa-solid fa-circle-xmark" style="margin-right: 5px;"/>
                  작품 삭제하기
                </b-button>
              </div>
              <div v-else>
                <span v-if="isCurrentlyActiveAuction">
                  <span v-if="Object.values(auctionArt.likeMarkingMembers).includes(currentAuthenticatedUserId) === false">
                    <b-button @click="likeMarking()" variant="outline-danger">
                      <font-awesome-icon icon="fa-solid fa-heart" style="margin-right: 5px;"/> 찜 등록
                    </b-button>
                  </span>
                  <span v-else>
                    <b-button @click="likeCancel()" variant="danger">
                      <font-awesome-icon icon="fa-solid fa-heart" style="margin-right: 5px;"/> 찜 취소
                    </b-button>
                  </span>
                  <b-button v-if="isForSale" variant="outline-primary" :disabled="currentAuthenticatedUserId === auctionArt.art.highestBidderId" v-b-modal.bidModal>
                    <font-awesome-icon icon="fa-solid fa-gavel" style="margin-right: 5px;"/> 작품 입찰하기
                  </b-button>
                </span>
                <span v-else>
                  <b-button v-if="isForSale && currentAuthenticatedUserId === auctionArt.art.highestBidderId" @click="auctionArtPurchase()" variant="success">
                    <font-awesome-icon icon="fa-solid fa-money-check-dollar" style="margin-right: 5px;"/> 작품 구매하기
                  </b-button>
                </span>
              </div>
            </div>
          </div>
          <div v-else>
            <p style="color: red;">입찰을 원하신다면 로그인을 진행해주세요</p>
          </div>

          <!-- 경매 작품 입찰 모달창 -->
          <div class="modal fade" id="bidModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1"
               aria-labelledby="staticBackdropLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
              <div class="modal-content">
                <div class="modal-header">
                  <h5 class="modal-title" id="staticBackdropLabel">경매 입찰</h5>
                  <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                  <div class="input-group rounded">
                    <input type="number" class="form-control rounded" placeholder="입찰 금액을 입력해주세요" v-model.number="bidAmount" @keyup="bidTracker()"/>
                  </div>
                  <p v-show="bidCheck.isNotMeetCondition" :style="bidCheck.errorCss" v-html="bidCheck.errorMessage"></p>
                  <p v-show="bidCheck.isMeetCondition" :style="bidCheck.successCss" v-html="bidCheck.successMessage"></p>
                  <div class="text-center mt-4">
                    <b-button @click="auctionArtBid()" variant="primary" data-bs-dismiss="modal" :disabled="bidCheck.isNotMeetCondition === true">입찰하기</b-button>
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
import dayjs from 'dayjs'

export default {
  name: 'AuctionArtDetailComponent',
  props: {
    auctionArt: Object
  },
  data() {
    return {
      currentAuthenticatedUserId: this.$store.getters['memberStore/getMemberId'],
      bidAmount: this.auctionArt.art.highestBidPrice,
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
        errorMessage: `현재 최고 입찰가는 [${this.auctionArt.art.highestBidPrice}포인트]입니다<br>더 높은 가격으로 입찰을 진행해주세요`,
        successMessage: '입찰이 진행되면 취소가 불가능합니다<br>신중히 고민해보시고 결정해주세요'
      }
    }
  },
  methods: {
    translateLocalDateTime(date) {
      return dayjs(date).format('YYYY년 MM월 DD일 HH시 mm분')
    },
    async deleteAuctionArt() {
      const check = confirm('작품을 정말 삭제하시겠습니까?')
      if (check) {
        try {
          await this.axiosWithAccessToken.delete(`/api/art/${this.auctionArt.art.artId}`)
          alert('작품이 삭제되었습니다')
          this.$router.push('/')
        } catch (err) {
          alert(err.response.data.message)
        }
      }
    },
    async likeMarking() {
      try {
        await this.axiosWithAccessToken.post(`/api/art/${this.auctionArt.art.artId}/like`)
        this.$router.go()
      } catch (err) {
        alert(err.response.data.message)
      }
    },
    async likeCancel() {
      try {
        await this.axiosWithAccessToken.delete(`/api/art/${this.auctionArt.art.artId}/like`)
        this.$router.go()
      } catch (err) {
        alert(err.response.data.message)
      }
    },
    async auctionArtBid() {
      const check = confirm(`${this.bidAmount}포인트로 입찰을 진행하시겠습니까?`)
      if (check) {
        try {
          const bidRequest = {
            bidAmount: this.bidAmount
          }
          await this.axiosWithAccessToken.post(`/api/auction/${this.auctionArt.art.auctionId}/bid`, bidRequest)
          alert('입찰에 성공했습니다')
          this.$router.go()
        } catch (err) {
          alert(err.response.data.message)
        }
      }
    },
    async auctionArtPurchase() {
      const check = confirm('구매를 확정시겠습니까?')
      if(check) {
        try {
          await this.axiosWithAccessToken.post(`/api/art/${this.auctionArt.art.artId}/purchase`);
          alert('구매가 완료되었습니다')
          this.$router.push('/')
        } catch (err) {
          alert(err.response.data.message)
        }
      }
    },
    bidTracker() {
      if (this.bidAmount <= this.auctionArt.art.highestBidPrice) {
        this.bidCheck.isNotMeetCondition = true
        this.bidCheck.isMeetCondition = false
      } else {
        this.bidCheck.isNotMeetCondition = false
        this.bidCheck.isMeetCondition = true
      }
    }
  },
  computed: {
    isOwnWork() {
      return this.currentAuthenticatedUserId === this.auctionArt.art.artOwnerId
    },
    isCurrentlyActiveAuction() {
      return new Date(new Date().getTime() - new Date().getTimezoneOffset() * 60000).toISOString() < this.auctionArt.art.auctionEndDate
    },
    isAuthenticated() {
      return this.$store.getters['memberStore/isAuthenticated'] === true
    },
    isForSale() {
      return this.auctionArt.art.artStatus === 'FOR_SALE'
    },
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
