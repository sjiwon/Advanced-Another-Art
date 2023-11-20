<template>
  <div>
    <h2 class="text-center mb-4 fw-bold text-black">포인트 히스토리</h2>
    <div v-if="pointHistoryData.length !== 0" class="text-center">
      <b-table
        striped hover
        :items="pointHistoryData"
        :fields="columns"
        responsive="sm">
      </b-table>
    </div>
  </div>
</template>

<script>
import dayjs from 'dayjs'
import {API_PATH} from "@/apis/api";

export default {
  name: 'UserPointHistoryComponent',
  components: {},
  data() {
    return {
      columns: [
        {key: 'index', label: 'Index', sortable: true},
        {key: 'pointType', label: '포인트 타입', sortable: false},
        {key: 'dealAmount', label: '거래량', sortable: false},
        {key: 'recordDate', label: '거래 날짜', sortable: true}
      ],
      pointHistoryData: []
    }
  },

  created() {
    this.fetchData()
  },

  methods: {
    async fetchData() {
      try {
        const response = await this.axios.get(API_PATH.MEMBER.GET_POINT_RECORDS)
        const fetchDataList = response.data.result

        this.pointHistoryData = []
        for (let i = 0; i < fetchDataList.length; i++) {
          const jsonData = {
            index: fetchDataList.length - i,
            pointType: fetchDataList[i].pointType,
            dealAmount: fetchDataList[i].amount,
            recordDate: dayjs(fetchDataList[i].recordDate).format('YYYY년 MM월 DD일 HH시 mm분 ss초')
          };
          this.pointHistoryData.push(jsonData);
        }
      } catch (err) {
        alert(err.response.data.message)
      }
    }
  }
}
</script>

<style scoped>

</style>
