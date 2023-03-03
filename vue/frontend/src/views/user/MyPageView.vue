<template>
  <div class="container">
    <div class="row p-5">
      <div class="col-md-12 justify-content-center row mx-auto">
        <div class="col-md-2 position-static p-3 text-center" :style="leftMenuCss">
          <h3>마이 페이지</h3>
          <div>
            <b-button
              :style="menuButtonCss[0]"
              @click="$router.push('/mypage')"
              @mouseenter="mouseOverEvent(0)"
              @mouseleave="mouseLeaveEvent(0)">
              내 정보
            </b-button>
          </div>
          <div>
            <b-button
              :style="menuButtonCss[1]"
              @click="$router.push('/mypage/winning-auction')"
              @mouseenter="mouseOverEvent(1)"
              @mouseleave="mouseLeaveEvent(1)">
              경매 작품 낙찰 내역
            </b-button>
          </div>
          <div>
            <b-button
              :style="menuButtonCss[2]"
              @click="$router.push('/mypage/art/purchase')"
              @mouseenter="mouseOverEvent(2)"
              @mouseleave="mouseLeaveEvent(2)">
              작품 구매 내역
            </b-button>
          </div>
          <div>
            <b-button
              :style="menuButtonCss[3]"
              @click="$router.push('/mypage/art/sold')"
              @mouseenter="mouseOverEvent(3)"
              @mouseleave="mouseLeaveEvent(3)">
              작품 판매 내역
            </b-button>
          </div>
          <div>
            <b-button
              :style="menuButtonCss[4]"
              @click="$router.push('/mypage/point/charge')"
              @mouseenter="mouseOverEvent(4)"
              @mouseleave="mouseLeaveEvent(4)">
              포인트 충전
            </b-button>
          </div>
          <div>
            <b-button
              :style="menuButtonCss[5]"
              @click="$router.push('/mypage/point/history')"
              @mouseenter="mouseOverEvent(5)"
              @mouseleave="mouseLeaveEvent(5)">
              포인트 히스토리
            </b-button>
          </div>
        </div>
        <div class="col-md-9 position-static p-3">
          <router-view></router-view>
        </div>
        <div style="margin-bottom: 52px;"></div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MyPageView',

  data() {
    return {
      leftMenuCss: {
        borderRadius: '30px',
        marginRight: '15px',
        backgroundColor: '#E6FFFF',
        height: '320px'
      },
      menuButtonCss: [
        {
          fontSize: '13px',
          fontWeight: '',
          backgroundColor: '#E6FFFF'
        },
        {
          fontSize: '13px',
          fontWeight: '',
          backgroundColor: '#E6FFFF'
        },
        {
          fontSize: '13px',
          fontWeight: '',
          backgroundColor: '#E6FFFF'
        },
        {
          fontSize: '13px',
          fontWeight: '',
          backgroundColor: '#E6FFFF'
        },
        {
          fontSize: '13px',
          fontWeight: '',
          backgroundColor: '#E6FFFF'
        },
        {
          fontSize: '13px',
          fontWeight: '',
          backgroundColor: '#E6FFFF'
        }
      ],
      rightViewCss: {
        borderRadius: '30px',
        marginLeft: '10px',
        backgroundColor: '#EBF7FF'
      }
    }
  },

  methods: {
    mouseOverEvent(index) {
      this.menuButtonCss[index].fontSize = '16px'
      this.menuButtonCss[index].fontWeight = 'bold'
    },
    mouseLeaveEvent(index) {
      this.menuButtonCss[index].fontSize = '13px'
      this.menuButtonCss[index].fontWeight = ''
    }
  }
}
</script>

<style scoped>

</style>
