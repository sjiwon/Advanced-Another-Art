<template>
  <div class="container px-4s my-4">
    <div class="row justify-content-center m-auto">
      <div class="col-md-8 d-block p-3 text-black text-center">
        <div class="p-4">
          <h1>아이디 찾기</h1>
        </div>

        <div class="row g-3">
          <div class="col-md-6 offset-md-3">
            <div class="form-floating">
              <input type="text" class="form-control" placeholder="이름" v-model="requestData.name" autofocus>
              <label for="name">이름</label>
            </div>
          </div>

          <div class="col-md-6 offset-md-3">
            <div class="form-floating">
              <input type="email" class="form-control" placeholder="이메일" v-model="requestData.email" @keyup="emailTracker()">
              <label for="email">이메일</label>
            </div>
            <p v-show="emailCheck.isNotMeetCondition" :style="emailCheck.errorCss">{{ emailCheck.errorMessage }}</p>
            <p v-show="emailCheck.isMeetCondition" :style="emailCheck.successCss">{{ emailCheck.successMessage }}</p>
          </div>

          <div class="col-md-6 offset-md-3">
            <b-button @click="findIdProcess()" :style="successFindButtonDisplay" variant="outline-info" class="form-control p-3 mt-1">아이디 찾기</b-button>
            <p v-if="successFindButtonDisplay.display === 'none'" style="color: red;">아이디는 <span style="font-weight: bold">{{ this.findIdResult }}</span>입니다</p>
            <b-button v-if="successFindButtonDisplay.display === 'none'" variant="outline-info" class="form-control p-3 mt-1" @click="$router.push('/login')">로그인 바로가기</b-button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="successFindButtonDisplay.display !== 'none' && emailCheck.isNotMeetCondition === false && emailCheck.isMeetCondition === false" style="margin-bottom: 130px"></div>
    <div v-if="successFindButtonDisplay.display !== 'none' && (emailCheck.isNotMeetCondition === false || emailCheck.isMeetCondition === false)" style="margin-bottom: 89px"></div>
    <div v-if="successFindButtonDisplay.display === 'none'" style="margin-bottom: 89px;"></div>
  </div>
</template>

<script>
export default {
  name: 'FindIdView',
  components: {},
  data() {
    return {
      requestData: {
        name: '',
        email: ''
      },
      emailCheck: {
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
        isNotMeetCondition: false,
        isMeetCondition: false,
        errorMessage: '이메일 형식에 맞춰서 입력해주세요',
        successMessage: '올바른 형식입니다'
      },
      findIdResult: '',
      successFindButtonDisplay: {
        display: 'block'
      }
    }
  },
  methods: {
    emailTracker() {
      const pattern = /^[a-zA-Z0-9+-\_.]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/
      const currentEmail = this.requestData.email

      if (currentEmail === '') {
        this.emailCheck.isNotMeetCondition = false
        this.emailCheck.isMeetCondition = false
        this.findIdResult = ''
      } else {
        if (pattern.test(currentEmail)) {
          this.emailCheck.isNotMeetCondition = false
          this.emailCheck.isMeetCondition = true
        } else {
          this.emailCheck.isNotMeetCondition = true
          this.emailCheck.isMeetCondition = false
          this.findIdResult = ''
        }
      }
    },
    validateInputState() {
      if (this.requestData.name === '' || this.requestData.email === '') {
        this.findIdResult = ''
        return false
      }
    },
    validateEmail() {
      this.findIdResult = ''
      return this.emailCheck.isMeetCondition
    },
    async findIdProcess() {
      if (this.validateInputState() === false) {
        alert('입력이 완료되지 않았습니다\n다시 확인해주세요')
        return false
      }
      if (this.validateEmail() === false) {
        alert('이메일 형식이 올바르지 않습니다\n다시 확인해주세요')
        return false
      }

      this.findIdResult = ''
      try {
        const { name, email } = this.requestData
        const response = await this.axios.get(`/api/member/id?name=${name}&email=${email}`)
        this.findIdResult = response.data

        this.successFindButtonDisplay.display = 'none'
        this.emailCheck.isMeetCondition = false
        this.emailCheck.isNotMeetCondition = false
      } catch (err) {
        this.findIdResult = ''
        alert(err.response.data.message)
      }
    }
  }
}
</script>

<style scoped>

</style>
