<template>
  <div>
    <h2 class="text-center mb-4 fw-bold text-black">포인트 충전</h2>
    <div class="col-md-8 mx-auto">
      <div class="col-md-6 mx-auto p-2" :style="pointBoxCss">
        <div class="text-center" style="margin-top: 15px;">
          <p>현재 포인트 | <span style="font-size: 15px; font-weight: bold;">{{ currentUser.totalPoint }}포인트</span></p>
        </div>
        <div class="text-center">
          <p>충전 후 포인트 | <span style="font-size: 15px; font-weight: bold;">{{ resultPoint + importRequestData.amount }}포인트</span></p>
        </div>
      </div>

      <div class="col-md-6 mx-auto p-md-2">
        <input type="number" class="form-control form-control-lg p-2" v-model="importRequestData.amount" :disabled="isAutoIncrease"/>
      </div>

      <div class="col-md-10 offset-md-3 mx-auto">
        <div class="btn-group" role="group" style="height: 100px; width: 100%;">
          <b-button @click="increasePoint(5000)" variant="outline-dark">+5000</b-button>
          <b-button @click="increasePoint(10000)" variant="outline-dark">+10000</b-button>
          <b-button @click="increasePoint(30000)" variant="outline-dark">+30000</b-button>
          <b-button @click="increasePoint(50000)" variant="outline-dark">+50000</b-button>
          <b-button @click="customIncrease()" variant="outline-dark">직접 입력</b-button>
        </div>
      </div>

      <div class="row justify-content-center">
        <hr class="my-4 col-md-10 border border-1 border-dark" style="opacity: 0.2;">
      </div>

      <div class="col-md-6 mx-auto">
        <b-button @click="requestPay()" size="lg" class="form-control" variant="primary">충전하기</b-button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
const { IMP } = window;

export default {
  name: 'UserPointChargeComponent',
  components: {},
  data() {
    return {
      pointBoxCss: {
        border: '3px solid #A091B7',
        backgroundColor: '#FFE8FF',
        borderRadius: '20px'
      },
      currentUser: {
        id: '',
        name: '',
        nickname: '',
        loginId: '',
        email: '',
        school: '',
        phoneNumber: '',
        address: '',
        birth: '',
        availablePoint: '',
        totalPoint: ''
      },
      resultPoint: 0,
      isAutoIncrease: true,
      importRequestData: {
        pg: 'uplus',
        pay_method: 'card',
        merchant_uid: 'merchant_' + new Date().getTime(),
        name: 'AnotherArt 포인트 충전',
        amount: 0,
        currency: 'KRW',
        language: 'ko',
        buyer_name: '',
        buyer_tel: '',
        buyer_email: '',
        buyer_addr: '',
        buyer_postcode: ''
      }
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
        const response = await axios.get('/api/user/info')
        this.currentUser = response.data
        this.resultPoint = this.currentUser.totalPoint
        this.importRequestData.buyer_name = this.currentUser.name
        this.importRequestData.buyer_tel = this.currentUser.phoneNumber
        this.importRequestData.buyer_email = this.currentUser.email
        this.importRequestData.buyer_addr = this.currentUser.address.defaultAddress + ' ' + this.currentUser.address.detailAddress
        this.importRequestData.buyer_postcode = this.currentUser.address.postCode
      } catch (err) {
        alert(err.response.data.message)
      }
    },
    increasePoint(pointHistory) {
      this.importRequestData.amount += pointHistory
      this.isAutoIncrease = true
    },
    customIncrease() {
      this.importRequestData.amount = 0
      this.isAutoIncrease = false
    },
    requestPay() {
      IMP.init("imp36060541");
      IMP.request_pay(this.importRequestData, async (response) => {
          if (response.success) {
            try {
              await axios.post('/api/user/point/charge', {
                userId: this.$store.getters.getUserId,
                dealAmount: this.importRequestData.amount
              });
              alert('결제가 완료되었습니다')
              this.$router.push('/mypage/point/history')
            } catch (err) {
              alert(err.response.data.message)
            }
          } else {
            let msg = `결제에 실패하였습니다\n- ${response.error_msg}`
            alert(msg)
          }
        }
      )
    },
  }
}
</script>

<style scoped>

</style>
