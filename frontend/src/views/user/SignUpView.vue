<template>
  <div class="container px-4s my-5">
    <div class="row justify-content-center m-auto">
      <div class="col-md-10 d-block p-3 text-black text-center">
        <div class="p-4">
          <h1>회원가입</h1>
        </div>

        <div class="row g-4">
          <div class="col-md-6 offset-md-3">
            <input type="text" class="form-control p-3" placeholder="이름" required v-model="checkInputData.name"/>
          </div>

          <div class="col-md-6 offset-md-3">
            <div class="row">
              <div class="col-md-8 mb-2">
                <input type="text" class="form-control p-3" placeholder="닉네임" required v-model="checkInputData.nickname" @keyup="currentNicknameApiState()"/>
              </div>
              <div class="col-md-4">
                <b-button variant="outline-info" class="form-control p-3 mt-1" :disabled="duplicateApiCheck.nickNameDisabled" @click="nickNameDuplicateCheck()">중복 체크</b-button>
              </div>
            </div>
          </div>

          <div class="col-md-6 offset-md-3">
            <div class="row">
              <div class="col-md-8 mb-2">
                <input type="text" class="form-control p-3" placeholder="전화번호" required min="10" max="11" v-model="checkInputData.phone" @keyup="currentPhoneNumberApiState()"/>
              </div>
              <div class="col-md-4">
                <b-button variant="outline-info" class="form-control p-3 mt-1" :disabled="duplicateApiCheck.phoneDisabled" @click="phoneDuplicateCheck()">중복 체크</b-button>
              </div>
            </div>
          </div>
        </div>

        <div class="row justify-content-center">
          <hr class="my-4 col-md-8 border border-1 border-dark" style="opacity: 0.2;">
        </div>

        <div class="row g-3">
          <div class="col-md-6 offset-md-3">
            <div class="row">
              <div class="col-md-8 mb-2">
                <input type="text" class="form-control p-3" placeholder="아이디" required v-model="checkInputData.loginId" @keyup="currentLoginIdApiState()"/>
              </div>
              <div class="col-md-4">
                <b-button variant="outline-info" class="form-control p-3 mt-1" :disabled="duplicateApiCheck.loginIdDisabled" @click="loginIdDuplicateCheck()">중복 체크</b-button>
              </div>
            </div>
          </div>

          <div class="col-md-6 offset-md-3">
            <input type="password" class="form-control p-3" placeholder="비밀번호" required v-model="checkInputData.password" @keyup="passwordTracker()"/>
            <p v-show="passwordCheck.isNotMeetCondition" :style="passwordCheck.errorCss">{{ passwordCheck.errorMessage }}</p>
            <p v-show="passwordCheck.isMeetCondition" :style="passwordCheck.successCss">{{ passwordCheck.successMessage }}</p>
          </div>

          <div class="col-md-6 offset-md-3">
            <input type="password" class="form-control p-3" placeholder="비밀번호 확인" :disabled="passwordVerification.isDisabled" required v-model="passwordVerificationToken"
                   @keyup="passwordVerificationTracker()"/>
            <p v-show="passwordVerification.isNotMatchExactly" :style="passwordVerification.errorCss">{{ passwordVerification.errorMessage }}</p>
            <p v-show="passwordVerification.isMatchExactly" :style="passwordVerification.successCss">{{ passwordVerification.successMessage }}</p>
          </div>
        </div>

        <div class="row justify-content-center">
          <hr class="my-4 col-md-8 border border-1 border-dark" style="opacity: 0.2;">
        </div>

        <div class="row g-2">
          <div class="col-md-6 offset-md-3">
            <div class="row">
              <div class="col-md-8 mb-2">
                <input type="search" class="form-control p-3" placeholder="재학중인 학교명" required disabled :style="css.backGroundWhite" v-model="currentSelectSchool"/>
              </div>
              <div class="col-md-4">
                <b-button variant="secondary" class="form-control p-3 mt-1" v-b-modal.schoolModal>학교 찾기</b-button>
              </div>
            </div>
          </div>
          <!-- 학교 찾기 모달창 -->
          <div class="modal fade" id="schoolModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
              <div class="modal-content h-598">
                <div class="modal-header">
                  <h4>학교 찾기</h4>
                  <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                  <div class="input-group rounded text-center">
                    <input type="search" class="form-control rounded" placeholder="학교 이름을 입력하세요" autofocus @keyup.enter="schoolSearch(aboutSchool.univSearchKeyword)"
                           v-model="aboutSchool.univSearchKeyword"/>
                    <b-button variant="primary" @click="schoolSearch(aboutSchool.univSearchKeyword)">검색</b-button>
                  </div>
                  <div v-for="(univ, index) in aboutSchool.univSearchResult" :key="index" class="mt-3">
                    <b-button class="btn btn-outline-dark pt-3" style="width: 450px; background-color: white"
                              @click="selectSchool(univ.schoolName, univ.campusName, univ.region)" data-bs-dismiss="modal" aria-label="Close">
                      <h5>{{ univ.schoolName }} <small>({{ univ.campusName }} - {{ univ.region }})</small></h5>
                      <span>{{ univ.adres }}</span>
                    </b-button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="col-md-6 offset-md-3">
            <div class="row">
              <div class="col-md-8 mb-2">
                <input type="text" class="form-control p-3" placeholder="이메일" required v-model="checkInputData.email" @keyup="emailTracker()"/>
                <p v-show="emailCheck.isNotMeetCondition" :style="emailCheck.errorCss">{{ emailCheck.errorMessage }}</p>
                <p v-show="emailCheck.isMeetCondition" :style="emailCheck.successCss">{{ emailCheck.successMessage }}</p>
              </div>
              <div class="col-md-4">
                <b-button variant="outline-info" class="form-control p-3 mt-1" :disabled="duplicateApiCheck.emailDisabled" @click="emailDuplicateCheck()">중복 체크</b-button>
              </div>
            </div>
          </div>
        </div>

        <div class="row justify-content-center">
          <hr class="my-4 col-md-8 border border-1 border-dark" style="opacity: 0.2;">
        </div>

        <div class="row g-4">
          <div class="col-md-6 offset-md-3">
            <div class="row">
              <div class="col-md-8 mb-2">
                <input type="text" class="form-control p-3" placeholder="우편 번호" required disabled :style="css.backGroundWhite" v-model="checkInputData.postcode"/>
              </div>
              <div class="col-md-4 mb-2">
                <b-button @click="searchAddress()" variant="secondary" class="form-control p-3 mt-1">주소 검색</b-button>
              </div>
            </div>
            <input type="text" class="form-control p-3" placeholder="주소" required disabled :style="css.addressMarginWithBackgroundWhite" v-model="checkInputData.defaultAddress"/>
            <input type="text" class="form-control p-3" placeholder="상세 주소" required :style="css.addressMarginWithBackgroundWhite" v-model="checkInputData.detailAddress"/>
            <input type="text" class="form-control p-3" placeholder="참고 항목" :style="css.addressMarginWithBackgroundWhite" v-model="checkInputData.extraAddress"/>
          </div>
        </div>

        <div class="row justify-content-center">
          <hr class="my-4 col-md-8 border border-1 border-dark" style="opacity: 0.2;">
        </div>

        <div class="col-md-6 offset-md-3">
          <b-button @click="signUpProcess()" variant="primary" class="form-control p-3 mt-1">회원 가입</b-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SignUpView',
  components: {},
  data() {
    return {
      css: {
        addressMarginWithBackgroundWhite: {
          marginBottom: '10px',
          backgroundColor: 'white'
        },
        backGroundWhite: {
          backgroundColor: 'white'
        }
      },
      passwordVerificationToken: '',
      checkInputData: {
        name: '',
        nickname: '',
        phone: '',
        loginId: '',
        password: '',
        school: '',
        email: '',
        postcode: '',
        defaultAddress: '',
        detailAddress: '',
        extraAddress: ''
      },
      aboutSchool: {
        univSearchKeyword: '',
        univSearchResult: []
      },
      currentSelectSchool: '',
      duplicateApiCheck: {
        nicknameCheck: false,
        nickNameDisabled: false,
        phoneCheck: false,
        phoneDisabled: false,
        loginIdCheck: false,
        loginIdDisabled: false,
        emailCheck: false,
        emailDisabled: false
      },
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
      }
    }
  },
  methods: {
    currentNicknameApiState() {
      this.duplicateApiCheck.nickNameDisabled = false
      this.duplicateApiCheck.nicknameCheck = false
    },
    async nickNameDuplicateCheck() {
      const nickname = this.checkInputData.nickname
      if (nickname === '') {
        alert('닉네임을 입력해주세요')
        return false
      }

      try {
        await this.axios.get(`/api/member/check-duplicates?resource=nickname&value=${nickname}`)
        alert('사용 가능한 닉네임입니다')
        this.duplicateApiCheck.nickNameDisabled = true
        this.duplicateApiCheck.nicknameCheck = true
      } catch (err) {
        alert(err.response.data.message)
      }
    },
    currentPhoneNumberApiState() {
      this.duplicateApiCheck.phoneDisabled = false
      this.duplicateApiCheck.phoneCheck = false
    },
    async phoneDuplicateCheck() {
      const phone = this.checkInputData.phone
      if (phone === '') {
        alert('전화번호를 입력해주세요')
        return false
      }

      try {
        await this.axios.get(`/api/member/check-duplicates?resource=phone&value=${phone}`)
        alert('사용 가능한 전화번호입니다')
        this.duplicateApiCheck.phoneDisabled = true
        this.duplicateApiCheck.phoneCheck = true
      } catch (err) {
        alert(err.response.data.message)
      }
    },
    currentLoginIdApiState() {
      this.duplicateApiCheck.loginIdDisabled = false
      this.duplicateApiCheck.loginIdCheck = false
    },
    async loginIdDuplicateCheck() {
      const loginId = this.checkInputData.loginId
      if (loginId === '') {
        alert('아이디를 입력해주세요')
        return false
      }

      try {
        await this.axios.get(`/api/member/check-duplicates?resource=loginId&value=${loginId}`)
        alert('사용 가능한 아이디입니다')
        this.duplicateApiCheck.loginIdDisabled = true
        this.duplicateApiCheck.loginIdCheck = true
      } catch (err) {
        alert(err.response.data.message)
      }
    },
    async emailDuplicateCheck() {
      const email = this.checkInputData.email
      if (email === '') {
        alert('이메일 입력해주세요')
        return false
      }

      try {
        await this.axios.get(`/api/member/check-duplicates?resource=email&value=${email}`)
        alert('사용 가능한 이메일입니다')
        this.duplicateApiCheck.emailDisabled = true
        this.duplicateApiCheck.emailCheck = true
      } catch (err) {
        alert(err.response.data.message)
      }
    },
    passwordTracker() {
      const pattern = /^(?=.*[a-zA-Z])(?=.*[!@#$%^&*+=-])(?=.*[0-9]).{8,25}$/
      const currentPassword = this.checkInputData.password

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
      const currentPassword = this.checkInputData.password
      const currentVerificationPassword = this.passwordVerificationToken

      if (currentPassword === currentVerificationPassword) {
        this.passwordVerification.isNotMatchExactly = false
        this.passwordVerification.isMatchExactly = true
      } else {
        this.passwordVerification.isNotMatchExactly = true
        this.passwordVerification.isMatchExactly = false
      }
    },
    emailTracker() {
      this.duplicateApiCheck.emailDisabled = false
      this.duplicateApiCheck.emailCheck = false

      const pattern = /^[a-zA-Z0-9+-\_.]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/
      const currentEmail = this.checkInputData.email

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
    async schoolSearch(searchKeyword) {
      const key = 'ac28e0697af24886fdf4a130fe263b13'

      try {
        const response = await this.externalAxios.get(`http://www.career.go.kr/cnet/openapi/getOpenApi?apiKey=${key}&svcType=api&svcCode=SCHOOL&contentType=json&gubun=univ_list&searchSchulNm=${searchKeyword}`)
        const schoolList = response.data.dataSearch.content
        this.aboutSchool.univSearchResult = []
        for (let i = 0; i < schoolList.length; i++) {
          this.aboutSchool.univSearchResult.push(schoolList[i])
        }
      } catch (err) {
        alert(err.response.data.message)
      }
    },
    selectSchool(school, campusName, region) {
      this.checkInputData.school = school
      this.currentSelectSchool = school + ' (' + campusName + ' - ' + region + ')'
    },
    searchAddress() {
      new window.daum.Postcode({
        oncomplete: (data) => {
          if (this.extraAddress !== '') {
            this.checkInputData.extraAddress = ''
          }
          if (data.userSelectedType === 'R') {
            // 사용자가 도로명 주소를 선택했을 경우
            this.checkInputData.defaultAddress = data.roadAddress
          } else {
            // 사용자가 지번 주소를 선택했을 경우(J)
            this.checkInputData.defaultAddress = data.jibunAddress
          }

          // 사용자가 선택한 주소가 도로명 타입일때 참고항목을 조합한다.
          if (data.userSelectedType === 'R') {
            // 법정동명이 있을 경우 추가한다. (법정리는 제외)
            // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
            if (data.bname !== '' && /[동|로|가]$/g.test(data.bname)) {
              this.checkInputData.extraAddress += data.bname
            }
            // 건물명이 있고, 공동주택일 경우 추가한다.
            if (data.buildingName !== '' && data.apartment === 'Y') {
              this.checkInputData.extraAddress +=
                this.checkInputData.extraAddress !== ''
                  ? `, ${data.buildingName}`
                  : data.buildingName
            }
            // 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
            if (this.checkInputData.extraAddress !== '') {
              this.checkInputData.extraAddress = `(${this.checkInputData.extraAddress})`
            }
          } else {
            this.checkInputData.extraAddress = ''
          }
          // 우편번호를 입력한다.
          this.checkInputData.postcode = data.zonecode
        }
      }).open()
    },
    validatePassword() {
      return this.passwordCheck.isMeetCondition
    },
    validatePasswordVerification() {
      return this.passwordVerification.isMatchExactly
    },
    validateEmail() {
      return this.emailCheck.isMeetCondition
    },
    validateInputState() {
      const inputData = this.checkInputData
      const keys = Object.keys(inputData)
      for (let i = 0; i < keys.length; i++) {
        const key = keys[i]
        const value = inputData[key]
        if (value === '') {
          return false
        }
      }
      return true
    },
    validateApiResultState() {
      const nicknameCheck = this.duplicateApiCheck.nicknameCheck
      const phoneCheck = this.duplicateApiCheck.phoneCheck
      const loginIdCheck = this.duplicateApiCheck.loginIdCheck
      const emailCheck = this.duplicateApiCheck.emailCheck

      return !(nicknameCheck === false || phoneCheck === false || loginIdCheck === false || emailCheck === false)
    },
    async signUpProcess() {
      if (this.validateInputState() === false) {
        alert('입력이 완료되지 않았습니다\n다시 확인해주세요')
        return false
      }
      if (this.validatePassword() === false) {
        alert('비밀번호가 조건을 충족하지 않습니다\n다시 확인해주세요\n(영문, 숫자, 특수문자를 하나 이상 포함하고 8자 이상 25자 이하여야 합니다)')
        return false
      }
      if (this.validatePasswordVerification() === false) {
        alert('비밀번호 확인란이 일치하지 않습니다\n다시 확인해주세요')
        return false
      }
      if (this.validateEmail() === false) {
        alert('이메일 형식이 올바르지 않습니다\n다시 확인해주세요')
        return false
      }
      if (this.validateApiResultState() === false) {
        alert('중복 체크가 완료되지 않았습니다\n다시 확인해주세요')
        return false
      }

      try {
        const signUpData = {
          name: this.checkInputData.name,
          nickname: this.checkInputData.nickname,
          loginId: this.checkInputData.loginId,
          password: this.checkInputData.password,
          email: this.checkInputData.email,
          school: this.checkInputData.school,
          phone: this.checkInputData.phone,
          postcode: this.checkInputData.postcode,
          defaultAddress: this.checkInputData.defaultAddress,
          detailAddress: this.checkInputData.detailAddress + ' ' + this.checkInputData.extraAddress
        }
        await this.axios.post('/api/member', signUpData)
        alert('회원가입이 완료되었습니다')
        this.$router.push('/')
      } catch (err) {
        alert(err.response.data.message)
      }
    }
  }
}
</script>

<style scoped>

</style>
