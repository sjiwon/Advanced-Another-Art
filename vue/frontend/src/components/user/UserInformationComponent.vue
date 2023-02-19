<template>
  <div>
    <h2 class="text-center mb-4 fw-bold text-black">내 정보</h2>
    <div :style="divCss">
      <div class="col-10">
        <p :style="textCss">이름 | <span :style="infoCss">{{ currentUser.name }}</span></p>
      </div>
    </div>

    <div class="row" :style="divCss">
      <div class="col-10">
        <p :style="textCss">닉네임 | <span :style="infoCss">{{ currentUser.nickname }}</span></p>
      </div>
      <div class="col-2" :style="buttonCss">
        <b-button variant="danger" v-b-modal.changeNicknameModal>수정</b-button>
      </div>
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

    <div class="row" :style="divCss">
      <div class="col-12">
        <p :style="textCss">아이디 | <span :style="infoCss">{{ currentUser.loginId }}</span></p>
      </div>
    </div>

    <div class="row" :style="divCss">
      <div class="col-12">
        <p :style="textCss">이메일 | <span :style="infoCss">{{ currentUser.email }}</span></p>
      </div>
    </div>

    <div class="row" :style="divCss">
      <div class="col-12">
        <p :style="textCss">재학중인 학교명 | <span :style="infoCss">{{ currentUser.school }}</span></p>
      </div>
    </div>

    <div class="row" :style="divCss">
      <div class="col-12">
        <p :style="textCss">전화번호 | <span :style="infoCss">{{ currentUser.phoneNumber }}</span></p>
      </div>
    </div>

    <div class="row" :style="divCss">
      <div class="col-12">
        <p :style="textCss">주소 | <span :style="infoCss">[{{ currentUser.address.postcode }}] {{ currentUser.address.defaultAddress }} {{ currentUser.address.detailAddress }}</span></p>
      </div>
    </div>

    <div class="row" :style="divCss">
      <p :style="textCss">총 보유 포인트 | <span :style="infoCss">{{ currentUser.totalPoint }}포인트</span></p>
      <p :style="textCss">사용 가능 포인트 | <span :style="infoCss">{{ currentUser.availablePoint }}포인트</span></p>
    </div>

    <div>
      <b-button @click="$router.push('/change-password')" variant="danger">비밀번호 변경</b-button>
    </div>
  </div>
</template>

<script>
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
        phoneNumber: '',
        address: '',
        availablePoint: '',
        totalPoint: ''
      },
      textCss: {
        fontSize: '15px'
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
      changeNickname: ''
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    async fetchData() {
      try {
        const response = await this.axiosWithAccessToken.get('/api/member/info')
        this.currentUser = response.data
      } catch (err) {
        alert(err.response.data.message)
      }
    },
    async changeNicknameProcess() {
      try {
        const changeNickname = {
          'changeNickname': this.changeNickname
        }

        await this.axiosWithAccessToken.patch('/api/member/nickname', changeNickname)
        alert('닉네임 변경이 완료되었습니다')
        this.$router.go()
      } catch (err) {
        alert(err.response.data.message)
      }
    }
  }
}
</script>

<style scoped>

</style>
