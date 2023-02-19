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
import axios from 'axios'
import dayjs from 'dayjs'

export default {
  name: 'UserPointHistoryComponent',
  components: {},
  data() {
    return {
      columns: [
        { key: 'index', label: 'Index', sortable: true },
        { key: 'pointType', label: 'Point Type', sortable: false },
        { key: 'dealAmount', label: 'Deal Amount', sortable: false },
        { key: 'recordTime', label: 'Record Time', sortable: true }
      ],
      pointHistoryData: []
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
        const response = await axios.get('/api/user/point/history')
        const fetchDataList = response.data
        this.pointHistoryData = []
        for (let i = fetchDataList.length - 1; i >= 0; i--) {
          const jsonData = {
            index: (i + 1),
            pointType: fetchDataList[i].pointType,
            dealAmount: fetchDataList[i].dealAmount,
            recordTime: dayjs(fetchDataList[i].recordTime).format('YYYY년 MM월 DD일 HH시 mm분 ss초')
          }
          this.pointHistoryData.push(jsonData)
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
