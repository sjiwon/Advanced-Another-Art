<template>
  <div class="container px-4s my-5">
    <div class="p-2">
      <h1 class="mb-3 text-center text-black">작품 등록</h1>
    </div>

    <div class="row justify-content-center">
      <hr class="my-4 col-md-12 border border-1 border-dark" style="opacity: 0.2;">
    </div>

    <div id="wrapper" class="container-fluid">
      <div class="row m-auto">
        <div class="col-md-6 position-static p-3">
          <div class="row">
            <div class="mb-6">
              <h4><label for="artworkType" class="form-label">판매 유형</label></h4>
              <div class="btn-group" style="border: none;" role="group">
                <b-button :class="{ selectedBackgroundColor: isAuctionSelected, notSelectedBackground: isGeneralSelected }" @click="changeSaleType('auction')">
                  <font-awesome-icon icon="fa-solid fa-gavel" style="height: 20px; margin-top: 10px;"/>
                  <h5>경매 작품</h5>
                </b-button>
                <b-button :class="{ selectedBackgroundColor: isGeneralSelected, notSelectedBackground: isAuctionSelected }" @click="changeSaleType('general')">
                  <font-awesome-icon icon="fa-solid fa-won-sign" style="height: 20px; margin-top: 10px;"/>
                  <h5>일반 작품</h5>
                </b-button>
              </div>
            </div>
          </div>

          <div class="row">
            <div class="mb-6">
              <div class="col-md-12 row">
                <h4><label for="name" class="form-label">작품명</label></h4>
                <div class="col-md-8 mb-2">
                  <input type="text" class="form-control p-3" placeholder="작품명" v-model="registerData.name" @keyup="currentNameApiState()" required/>
                </div>
                <div class="col-md-4">
                  <b-button variant="outline-info" class="form-control p-3 mt-1" :disabled="duplicateApiCheck.nameDisabled" @click="nameDuplicateCheck()">중복 체크</b-button>
                </div>
              </div>
            </div>

            <div class="mb-6">
              <h4><label for="tag" class="form-label">해시태그</label></h4>
              <b-form-tags tag-variant="danger" tag-pills remove-on-delete
                           class="form-control p-3"
                           v-model="registerData.hashtagList"
                           placeholder="해시태그 입력 후 엔터를 눌러주세요..."></b-form-tags>
            </div>

            <div class="mb-6">
              <h4><label for="description" class="form-label">작품 설명</label><br></h4>
              <div class="row">
                <div class="col-md-12">
                  <div class="form-floating">
                    <textarea class="form-control p-3" style="height: 150px;" v-model="registerData.description" required></textarea>
                  </div>
                </div>
              </div>
            </div>

            <div v-if="isAuctionSelected === true">
              <div class="mb-6">
                <h4>
                  <label for="price" class="form-label">경매 시작 가격</label>
                  <font-awesome-icon icon="fa-solid fa-sack-dollar" style="height: 30px; margin-left: 10px;"/>
                </h4>
                <div class="col-md-12">
                  <input type="number" class="form-control form-control-lg p-3" v-model="registerData.initPrice" required/>
                </div>
              </div>

              <div class="mb-6">
                <h4><label for="" class="form-label">경매 시작 날짜</label></h4>
                <div class="row">
                  <div class="col-md-12">
                    <input type="text" onfocus="(this.type='datetime-local')" class="form-control p-3" placeholder="경매 시작 날짜" v-model="registerData.startDate" required>
                  </div>
                </div>
              </div>

              <div class="mb-2">
                <h4><label for="" class="form-label">경매 종료 날짜</label></h4>
                <div class="row">
                  <div class="col-md-12">
                    <input type="text" onfocus="(this.type='datetime-local')" class="form-control p-3" placeholder="경매 종료 날짜" v-model="registerData.endDate" required>
                  </div>
                </div>
              </div>
            </div>
            <div v-if="isGeneralSelected === true">
              <div class="mb-2">
                <h4>
                  <label for="price" class="form-label">작품 가격</label>
                  <font-awesome-icon icon="fa-solid fa-sack-dollar" style="height: 30px; margin-left: 10px;"/>
                </h4>
                <div class="col-md-12">
                  <input type="number" class="form-control form-control-lg p-3" min="1000" v-model="registerData.initPrice" required/>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="col-md-6 position-static d-block p-3">
          <h4>작품 미리보기</h4>
          <div class="row">
            <div class="col-md-6">
              <div class="card mt-3">
                <label for="imageFile" class="input-button" style="cursor: pointer;">
                  <button type="button" class="btn btn-outline-dark importar">
                    <h3 style="position: relative; top: 30%;">이미지 등록</h3>
                  </button>
                </label>
                <input type="file" id="imageFile" @change="upload()" accept="image/*" style="display: none;"/>
                <div class="card-body pt-4">
                  <h6 class="card-text">이미지 파일을 추가하면 오른쪽 공간에 이미지가 표시됩니다.</h6>
                </div>
              </div>
            </div>

            <div v-if="previewImg === ''" class="col-md-6 mt-3">
              <div class="row">
                <div class="mb-4 text-black-50 text-center" style="height: 100%; padding: 50% 0">
                  <h5>등록된 이미지가 없습니다.</h5>
                </div>
              </div>
            </div>
            <div v-else class="col-md-6 mt-3">
              <div class="row">
                <img :src="previewImg" class="mb-4" alt="">
              </div>
            </div>

          </div>
        </div>

        <div class="row">
          <hr class="my-4 col-md-12 offset-md-0 border border-1 border-dark" style="opacity: 0.1;">
        </div>
        <div class="row">
          <b-button @click="artRegistrationProcess()" variant="primary" class="form-control-lg form-control">작품 등록</b-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'ArtRegistrationView',
  components: {},
  data() {
    return {
      saleType: 'auction',
      isAuctionSelected: true,
      isGeneralSelected: false,
      previewImg: '',
      registerData: {
        saleType: 'auction',
        userId: this.$store.getters.getUserId,
        name: '',
        description: '',
        initPrice: '',
        file: '',
        hashtagList: [],
        startDate: '',
        endDate: ''
      },
      duplicateApiCheck: {
        nameCheck: false,
        nameDisabled: false
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
    changeSaleType(saleType) {
      this.saleType = saleType
      this.registerData.saleType = saleType
      if (saleType === 'auction') {
        this.isAuctionSelected = true
        this.isGeneralSelected = false
      } else if (saleType === 'general') {
        this.isAuctionSelected = false
        this.isGeneralSelected = true
      }
      this.registerData = {
        saleType: saleType,
        userId: this.$store.getters.getUserId,
        name: '',
        description: '',
        initPrice: '',
        file: '',
        hashtagList: [],
        startDate: '',
        endDate: ''
      }
    },
    upload() {
      let file = document.getElementById("imageFile");
      this.previewImg = URL.createObjectURL(file.files[0]);
    },

    currentNameApiState() {
      this.duplicateApiCheck.nameCheck = false
      this.duplicateApiCheck.nameDisabled = false
    },
    async nameDuplicateCheck() {
      const name = this.registerData.name
      if (name === '') {
        alert('작품명을 입력해주세요')
        return false;
      }

      try {
        let formData = new FormData();
        formData.append('name', name)

        await axios.post('/api/art/duplicate-check', formData, {
          headers: { 'Content-Type': 'x-www-form-urlencoded' }
        })
        alert('사용 가능한 작품명입니다')
        this.duplicateApiCheck.nameCheck = true
        this.duplicateApiCheck.nameDisabled = true
      } catch (err) {
        alert(err.response.data.message)
      }
    },

    validateInputName() {
      return this.registerData.name !== ''
    },
    validateInputDescription() {
      return this.registerData.description !== ''
    },
    validateAuctionInputDate() {
      return !(this.registerData.startDate === '' || this.registerData.endDate === '');
    },
    validateInputPrice() {
      return this.registerData.initPrice !== ''
    },
    validateInputPriceMinimum() {
      return this.registerData.initPrice >= 1000
    },
    validateDate() {
      return this.registerData.startDate < this.registerData.endDate
    },
    validateEndDate() {
      return new Date(new Date().getTime() - new Date().getTimezoneOffset() * 60000).toISOString() <= this.registerData.endDate
    },
    validateFileUpload() {
      let file = document.getElementById("imageFile");
      return file.files[0] !== undefined;
    },
    validateApiResultState() {
      return this.duplicateApiCheck.nameCheck === true
    },
    async artRegistrationProcess() {
      if (this.validateInputName() === false) {
        alert('작품명을 입력해주세요')
        return false
      }
      if (this.validateInputDescription() === false) {
        alert('작품 설명을 입력해주세요')
        return false
      }
      if (this.validateInputPrice() === false) {
        if (this.registerData.saleType === 'auction') {
          alert('경매 시작 가격을 입력해주세요')
          return false
        } else {
          alert('작품 가격을 입력해주세요')
          return false
        }
      } else if (this.validateInputPriceMinimum() === false) {
        alert('작품은 최소 1000원 이상이여야 합니다')
        return false
      }
      if (this.registerData.saleType === 'auction') {
        if (this.validateAuctionInputDate() === false) {
          alert('경매 작품은 시작날짜/종료날짜가 필수입니다')
          return false
        } else if (this.validateDate() === false) {
          alert('시작 날짜와 종료 날짜를 정확하게 선택해주세요')
          return false
        } else if (this.validateEndDate() === false) {
          alert('종료 날짜는 현재 시간보다 이후로 설정해주세요')
          return false
        }
      }
      if (this.validateFileUpload() === false) {
        alert('파일을 업로드해주세요')
        return false
      }
      if (this.validateApiResultState() === false) {
        alert('중복체크를 진행해주세요')
        return false
      }

      try {
        let formData = new FormData()
        let file = document.getElementById("imageFile");
        formData.append('file', file.files[0])
        formData.append('saleType', this.registerData.saleType)
        formData.append('userId', this.registerData.userId)
        formData.append('name', this.registerData.name)
        formData.append('description', this.registerData.description)
        formData.append('initPrice', this.registerData.initPrice)
        formData.append('hashtagList', this.registerData.hashtagList)
        formData.append('startDate', this.registerData.startDate)
        formData.append('endDate', this.registerData.endDate)

        await axios.post('/api/art', formData, {
          headers: {'Content-Type': 'multipart/form-data'}
        })
        alert('작품 등록이 완료되었습니다')
        this.$router.push('/')
      } catch (err) {
        alert(err.response.data.message)
      }
    }
  }
}
</script>

<style scoped>
.selectedBackgroundColor {
  opacity: 1;
  background-color: darkturquoise;
}

.notSelectedBackground {
  opacity: 0.5;
  background-color: white;
}

.importar {
  width: 100%;
  height: 300px;
  pointer-events: none;
  background: rgb(250, 250, 253) url('@/assets/camera.png') no-repeat 50% 50%;
  background-size: 100px;
  border: 1px solid rgb(230, 229, 239);
  opacity: 0.5;
}
</style>
