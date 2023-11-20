<template>
  <div>
    <h2 class="text-center mb-4 fw-bold text-black">내 정보</h2>
    <div :style="divCss">
      <div class="col-10">
        <p :style="textCss">이름 -> <span :style="infoCss">{{ currentUser.name }}</span></p>
      </div>
    </div>

    <div class="row" :style="divCss">
      <div class="col-12">
        <p :style="textCss">닉네임 -> <span :style="infoCss">{{ currentUser.nickname }}</span></p>
      </div>
    </div>

    <div class="row" :style="divCss">
      <div class="col-12">
        <p :style="textCss">아이디 -> <span :style="infoCss">{{ currentUser.loginId }}</span></p>
      </div>
    </div>

    <div class="row" :style="divCss">
      <div class="col-12">
        <p :style="textCss">이메일 -> <span :style="infoCss">{{ currentUser.email }}</span></p>
      </div>
    </div>

    <div class="row" :style="divCss">
      <div class="col-12">
        <p :style="textCss">재학중인 학교명 -> <span :style="infoCss">{{ currentUser.school }}</span></p>
      </div>
    </div>

    <div class="row" :style="divCss">
      <div class="col-12">
        <p :style="textCss">전화번호 -> <span :style="infoCss">{{ currentUser.phone }}</span></p>
      </div>
    </div>

    <div class="row" :style="divCss">
      <div class="col-12">
        <p :style="textCss">주소 -> <span :style="infoCss">[{{
            currentUser.address.postcode
          }}] {{ currentUser.address.defaultAddress }} {{ currentUser.address.detailAddress }}</span></p>
      </div>
    </div>

    <div class="row" :style="divCss">
      <p :style="textCss">총 보유 포인트 -> <span :style="infoCss">{{ currentUser.totalPoint }}포인트</span></p>
      <p :style="textCss">사용 가능 포인트 -> <span :style="infoCss">{{ currentUser.availablePoint }}포인트</span></p>
    </div>

    <div>
      <b-button variant="danger" v-b-modal.changeNicknameModal>닉네임 변경</b-button>
      <b-button variant="danger" v-b-modal.changePasswordModal>비밀번호 변경</b-button>
    </div>

    <!-- 닉네임 변경 모달창 -->
    <div class="modal fade" id="changeNicknameModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1"
         aria-labelledby="staticBackdropLabel" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="staticBackdropLabel">닉네임 변경하기</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <div class="input-group rounded">
              <input type="text" class="form-control rounded" placeholder="변경할 닉네임을 입력해주세요" v-model="changeNickname"/>
            </div>
            <div class="text-center mt-4">
              <b-button @click="changeNicknameProcess()" variant="primary" data-bs-dismiss="modal">변경하기</b-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 비밀번호 변경 모달창 -->
    <div class="modal fade" id="changePasswordModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1"
         aria-labelledby="staticBackdropLabel" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="staticBackdropLabel">비밀번호 변경하기</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <div class="row g-3">
              <div class="input-group rounded">
                <input type="password" class="form-control" placeholder="변경할 비밀번호를 입력해주세요..." v-model="changePassword"
                       @keyup="passwordTracker()"><br>
              </div>
              <p v-show="passwordCheck.isNotMeetCondition" :style="passwordCheck.errorCss">{{
                  passwordCheck.errorMessage
                }}</p>
              <p v-show="passwordCheck.isMeetCondition" :style="passwordCheck.successCss">
                {{ passwordCheck.successMessage }}</p>

              <div class="input-group rounded">
                <input type="password" class="form-control" placeholder="비밀번호 확인란을 입력해주세요..."
                       :disabled="passwordVerification.isDisabled" required v-model="passwordVerificationToken"
                       @keyup="passwordVerificationTracker()"/><br>
              </div>
              <p v-show="passwordVerification.isNotMatchExactly" :style="passwordVerification.errorCss">
                {{ passwordVerification.errorMessage }}</p>
              <p v-show="passwordVerification.isMatchExactly" :style="passwordVerification.successCss">
                {{ passwordVerification.successMessage }}</p>

              <div class="text-center mt-4">
                <b-button @click="changePasswordProcess()" variant="primary" data-bs-dismiss="modal">변경하기</b-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {API_PATH} from "@/apis/api";

export default {
  name: 'UserInformationComponent',
  components: {},
  data() {
    return {
      currentUser: {
        id: '',
        name: '',
        nickname: '',
        loginId: '',
        email: '',
        school: '',
        phone: '',
        address: {
          postcode: '',
          defaultAddress: '',
          detailAddress: '',
        },
        availablePoint: '',
        totalPoint: ''
      },
      textCss: {
        fontSize: '13px'
      },
      infoCss: {
        fontSize: '20px'
      },
      divCss: {
        borderBottom: '1px solid #C6C6C6',
        marginTop: '10px',
        marginBottom: '10px'
      },
      buttonCss: {
        marginBottom: '10px'
      },
      changeNickname: '',
      changePassword: '',
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
        errorMessage: '영문, 숫자, 특수문자를 하나 이상 포함하고 8자 이상 25자 이하여야 합니다',
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
  created() {
    this.fetchData()
  },
  methods: {
    async fetchData() {
      try {
        const response = await this.axios.get(API_PATH.MEMBER.GET_INFORMATION)
        this.currentUser = response.data
      } catch (err) {
        alert(err.response.data.message)
      }
    },
    passwordTracker() {
      const pattern = /^(?=.*[a-zA-Z])(?=.*[!@#$%^&*+=-])(?=.*[0-9]).{8,25}$/
      const currentPassword = this.changePassword

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
      const currentPassword = this.changePassword
      const currentVerificationPassword = this.passwordVerificationToken

      if (currentPassword === currentVerificationPassword) {
        this.passwordVerification.isNotMatchExactly = false
        this.passwordVerification.isMatchExactly = true
      } else {
        this.passwordVerification.isNotMatchExactly = true
        this.passwordVerification.isMatchExactly = false
      }
    },
    async changeNicknameProcess() {
      try {
        await this.axios.patch(API_PATH.MEMBER.UPDATE_NICKNAME, {'value': this.changeNickname})
        alert('닉네임 변경이 완료되었습니다')
        this.$router.go()
      } catch (err) {
        alert(err.response.data.message)
      }
    },
    async changePasswordProcess() {
      try {
        await this.axios.patch(API_PATH.MEMBER.UPDATE_PASSWORD, {'value': this.changePassword})
        alert('비밀번호 변경이 완료되었습니다\n로그인 페이지로 이동합니다')
        this.$store.commit('memberStore/reset')
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
