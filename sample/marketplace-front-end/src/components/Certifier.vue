<template>
  <div>
    <div class="list_box">
      <el-table :data="tableData" border style="width: 100%" :empty-text="$t('common.no_data')">
        <el-table-column type="index" align="center" width="50" :index="indexMethod"></el-table-column>
        <el-table-column prop="data.name" align="center" :label="tableLang.name" width="180"></el-table-column>
        <el-table-column align="center" :label="tableLang.tags">
          <template slot-scope="scope">
            <el-tag
              style="margin-right: 10px;"
              v-for="(item, idx) in scope.row.data.keywords"
              :key="idx"
            >{{item}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" align="center" :label="tableLang.date" width="200"></el-table-column>
        <el-table-column :label="tableLang.operating" align="center" width="100">
          <template slot-scope="scope">
            <el-button
              @click="handleClick(scope.row)"
              type="text"
              size="small"
            >{{$t('common.enter_dateil')}}</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import { client } from 'ontology-dapi'

export default {
  data() {
    return {
      tableData: [],
      ontid: '',
      tableLang: {
        name: this.$t('common.commodity_name'),
        tags: this.$t('common.tags'),
        coin: this.$t('common.coin'),
        price: this.$t('common.price'),
        date: this.$t('common.date'),
        operating: this.$t('common.operating'),
      }
    }
  },
  methods: {
    indexMethod(idx) {
      return idx + 1
    },
    async  getCertData() {
      let params = {
        ontid: this.ontid,
        pageNum: 0,
        pageSize: 10
      }
      try {
        let res = await this.$store.dispatch('getCertData', params)
        console.log('certifier', res)
        if (res.status === 200 && res.data.desc === 'SUCCESS') {
          this.tableData = res.data.result.recordList
        } else {
          this.tableData = []
        }
      } catch (error) {
        this.tableData = []
      }
    },
    handleClick(row) {
      sessionStorage.setItem('isCert', 0)
      this.$router.push({ path: 'commoditydetail', query: { commodityId: row.id } });
    }
  },
  async mounted() {
    this.ontid = sessionStorage.getItem("user_ontid")
    await this.getCertData()
  }
}
</script>

<style>
</style>
