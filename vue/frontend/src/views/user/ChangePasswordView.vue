<template>
  <div class="container px-4s my-4">
    <div class="row justify-content-center m-auto">
      <div class="col-md-8 d-block p-3 text-black text-center">
        <div class="p-4">
          <h1>비밀번호 재설정</h1>
        </div>

        <div class="row g-3">
          <div class="col-md-6 offset-md-3">
            <div class="form-floating">
              <input type="text" class="form-control" placeholder="이름" :disabled="userAuthenticated === true" v-model="authenticatedData.name" autofocus>
              <label for="name">이름</label>
            </div>
          </div>

          <div class="col-md-6 offset-md-3">
            <div class="form-floating">
              <input type="text" class="form-control" placeholder="아이디" :disabled="userAuthenticated === true" v-model="authenticatedData.loginId">
              <label for="loginId">아이디</label>
            </div>
          </div>

          <div class="col-md-6 offset-md-3">
            <div class="form-floating">
              <input type="email" class="form-control" placeholder="이메일" :disabled="userAuthenticated === true" v-model="authenticatedData.email" @keyup="emailTracker()">
              <label for="email">이메일</label>
            </div>
            <p v-show="emailCheck.isNotMeetCondition" :style="emailCheck.errorCss">{{ emailCheck.errorMessage }}</p>
            <p v-show="emailCheck.isMeetCondition" :style="emailCheck.successCss">{{ emailCheck.successMessage }}</p>
          </div>

          <div class="col-md-6 offset-md-3">
            <b-button @click="userAuthenticationProcess()" :style="successFindButtonDisplay" variant="outline-info" class="form-control p-3 mt-1">사용자 인증</b-button>
          </div>
        </div>

        <div v-if="userAuthenticated === true" class="row justify-content-center">
          <hr class="my-4 col-md-8 border border-1 border-dark" style="opacity: 0.2;">
        </div>

        <div v-if="userAuthenticated === true" class="row g-3">
          <div class="col-md-6 offset-md-3">
            <div class="form-floating">
              <input type="text" class="form-control" placeholder="아이디" v-model="changeRequestData.loginId" autofocus>
              <label for="name">아이디</label>
            </div>
          </div>

          <div class="col-md-6 offset-md-3">
            <div class="form-floating">
              <input type="password" class="form-control" placeholder="변경할 비밀번호" v-model="changeRequestData.changePassword" @keyup="passwordTracker()">
              <label for="password">변경할 비밀번호</label>
              <p v-show="passwordCheck.isNotMeetCondition" :style="passwordCheck.errorCss">{{ passwordCheck.errorMessage }}</p>
              <p v-show="passwordCheck.isMeetCondition" :style="passwordCheck.successCss">{{ passwordCheck.successMessage }}</p>
            </div>
          </div>

          <div class="col-md-6 offset-md-3">
            <div class="form-floating">
              <input type="password" class="form-control" placeholder="비밀번호 확인" :disabled="passwordVerification.isDisabled" required v-model="passwordVerificationToken"
                     @keyup="passwordVerificationTracker()"/>
              <label for="passwordCheck">비밀번호 확인</label>
              <p v-show="passwordVerification.isNotMatchExactly" :style="passwordVerification.errorCss">{{ passwordVerification.errorMessage }}</p>
              <p v-show="passwordVerification.isMatchExactly" :style="passwordVerification.successCss">{{ passwordVerification.successMessage }}</p>
            </div>
          </div>

          <div class="col-md-6 offset-md-3">
            <b-button @click="changePasswordProcess()" variant="outline-info" class="form-control p-3 mt-1">비밀번호 변경</b-button>
          </div>
        </div>

      </div>
    </div>

    <div v-if="userAuthenticated === false" style="margin-bottom: 35px;"></div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'ChangePasswordView',
  components: {},
  data() {
    return {
      authenticatedData: {
        name: '',
        loginId: '',
        email: '',
      },
      changeRequestData: {
        loginId: '',
        changePassword: ''
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
      successFindButtonDisplay: {
        display: 'block'
      },
      userAuthenticated: false,
      passwordCheck: {
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
        errorMessage: '영문, 숫자, 특수문자를 하나 이상 포함하고 8자 이상이여야 합니다',
        successMessage: '사용 가능한 비밀번호입니다'
      },
      passwordVerificationToken: '',
      passwordVerification: {
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
        isDisabled: true,
        isNotMatchExactly: false,
        isMatchExactly: false,
        errorMessage: '비밀번호가 일치하지 않습니다',
        successMessage: '비밀번호가 일치합니다'
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
    emailTracker() {
      const pattern = /^[a-zA-Z0-9+-\_.]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/
      let currentEmail = this.authenticatedData.email

      if (currentEmail === '') {
        this.emailCheck.isNotMeetCondition = false
        this.emailCheck.isMeetCondition = false
      } else {
        if (pattern.test(currentEmail)) {
          this.emailCheck.isNotMeetCondition = false
          this.emailCheck.isMeetCondition = true
        } else {
          this.emailCheck.isNotMeetCondition = true
          this.emailCheck.isMeetCondition = false
        }
      }
    },
    validateInputState() {
      if (this.authenticatedData.name === '' || this.authenticatedData.loginId === '' || this.authenticatedData.email === '') {
        return false
      }
    },
    validateEmail() {
      return this.emailCheck.isMeetCondition
    },
    async userAuthenticationProcess() {
      if (this.validateInputState() === false) {
        alert('입력이 완료되지 않았습니다\n다시 확인해주세요');
        return false;
      }
      if (this.validateEmail() === false) {
        alert('이메일 형식이 올바르지 않습니다\n다시 확인해주세요')
        return false
      }

      try {
        await axios.post('/api/user/change/password/authentication', this.authenticatedData)
        this.successFindButtonDisplay.display = 'none'
        this.emailCheck.isMeetCondition = false
        this.emailCheck.isNotMeetCondition = false
        this.userAuthenticated = true
      } catch (err) {
        this.userAuthenticated = false
        alert(err.response.data.message)
      }
    },
    passwordTracker() {
      const pattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}/
      let currentPassword = this.changeRequestData.changePassword

      if (currentPassword === '') {
        this.passwordCheck.isNotMeetCondition = false
        this.passwordCheck.isMeetCondition = false
      } else {
        if (pattern.test(currentPassword)) {
          this.passwordCheck.isNotMeetCondition = false
          this.passwordCheck.isMeetCondition = true
          this.passwordVerification.isDisabled = false
        } else {
          this.passwordCheck.isNotMeetCondition = true
          this.passwordCheck.isMeetCondition = false
          this.passwordVerification.isDisabled = true
          this.passwordVerificationToken = ''
        }
      }
    },
    passwordVerificationTracker() {
      let currentPassword = this.changeRequestData.changePassword
      let currentVerificationPassword = this.passwordVerificationToken

      if (currentPassword === currentVerificationPassword) {
        this.passwordVerification.isNotMatchExactly = false
        this.passwordVerification.isMatchExactly = true
      } else {
        this.passwordVerification.isNotMatchExactly = true
        this.passwordVerification.isMatchExactly = false
      }
    },

    validateSecondInputState() {
      if (this.changeRequestData.loginId === '' || this.changeRequestData.changePassword === '' || this.passwordVerificationToken === '') {
        return false
      }
    },
    validatePassword() {
      return this.passwordCheck.isMeetCondition
    },
    validatePasswordVerification() {
      return (this.passwordVerification.isMatchExactly) && (this.changeRequestData.changePassword === this.passwordVerificationToken)
    },
    async changePasswordProcess() {
      if (this.validateSecondInputState() === false) {
        alert('입력이 완료되지 않았습니다\n다시 확인해주세요');
        return false;
      }
      if (this.validatePassword() === false) {
        alert('비밀번호가 조건을 충족하지 않습니다\n다시 확인해주세요\n-> 영문, 숫자, 특수문자를 하나 이상 포함하고 8자 이상이여야 합니다')
        return false
      }
      if (this.validatePasswordVerification() === false) {
        alert('비밀번호와 비밀번호 확인란이 일치하지 않습니다\n다시 확인해주세요')
        return false
      }

      try {
        await axios.post('/api/user/change/password', this.changeRequestData)
        alert('비밀번호 변경이 완료되었습니다\n로그인 페이지로 이동합니다')
        this.$router.push('/login')
      } catch (err) {
        alert(err.response.data.message)
      }
    }
  }
}
</script>

<style scoped>

</style>
