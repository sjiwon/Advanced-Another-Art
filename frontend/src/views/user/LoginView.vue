<template>
  <div class="container px-4s my-4">
    <div class="row justify-content-center m-auto">
      <div class="col-md-8 d-block p-3 text-black text-center">
        <div class="p-4">
          <h1>로그인</h1>
        </div>

        <div class="row g-3">
          <div class="col-md-6 offset-md-3">
            <div class="form-floating">
              <input type="text" class="form-control" placeholder="ID" v-model="loginData.loginId"
                     @keyup.enter="login()" autofocus>
              <label for="loginId">ID</label>
            </div>
          </div>
          <div class="col-md-6 offset-md-3">
            <div class="form-floating">
              <input type="password" class="form-control" placeholder="Password" v-model="loginData.loginPassword"
                     @keyup.enter="login()">
              <label for="loginPassword">Password</label>
            </div>
          </div>
          <div class="col-md-6 offset-md-3">
            <b-button @click="login()" variant="primary" class="form-control p-3 mt-1">로그인</b-button>
          </div>
        </div>

        <div class="row justify-content-center">
          <hr class="my-4 col-md-8 border border-1 border-dark" style="opacity: 0.2;">
        </div>

        <div class="text-center">
          <b-button @click="$router.push('/find-id')">아이디 찾기</b-button>
          <b-button @click="$router.push('/change-password')">비밀번호 재설정</b-button>
          <b-button @click="$router.push('/signup')">회원가입</b-button>
        </div>
        <br>
      </div>
    </div>
  </div>
</template>

<script>
import {API_PATH} from "@/apis/api";

export default {
  name: 'LoginView',

  data() {
    return {
      css: {
        topMargin: {
          margin: '100px'
        },
        inputMargin: {
          margin: '5px'
        }
      },
      loginData: {
        loginId: '',
        loginPassword: ''
      }
    }
  },

  methods: {
    validateLoginRequest() {
      const {loginId, loginPassword} = this.loginData
      return (loginId !== '') && (loginPassword !== '')
    },
    async login() {
      if (this.validateLoginRequest() === false) {
        alert('아이디나 비밀번호를 다시 확인해주세요')
        return false
      }

      try {
        const response = await this.axios.post(API_PATH.MEMBER.LOGIN, this.loginData)
        const loginResponse = {
          'memberId': response.data.id,
          'nickname': response.data.nickname,
          'accessToken': response.headers.get('Authorization').split(' ')[1]
        }
        this.$store.commit('memberStore/loginSuccess', loginResponse)
        alert('로그인에 성공하였습니다')
        this.$router.push('/')
      } catch (err) {
        console.log(err)
        this.$store.commit('memberStore/reset')
        alert(err.response.data.message)
      }
    }
  }
}
</script>

<style scoped>

</style>
