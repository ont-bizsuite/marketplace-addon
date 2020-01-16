<template>
  <div class="commodity_box">
    <div class="search_box">
      <el-input
        :placeholder="$t('commoditylist.content')"
        v-model="searchText"
        class="input-with-select"
      >
        <el-button slot="append" icon="el-icon-search" @click="searchClick()"></el-button>
      </el-input>
    </div>
    <div class="list_box">
      <el-table :data="tableData" border style="width: 100%" :empty-text="$t('common.no_data')">
        <el-table-column type="index" align="center" width="50" :index="indexMethod"></el-table-column>
        <el-table-column prop="name" align="center" :label="tableLang.name" width="240"></el-table-column>
        <el-table-column align="center" :label="tableLang.tags">
          <template slot-scope="scope">
            <el-tag
              style="margin-right: 10px;"
              v-for="(item, idx) in scope.row.tags"
              :key="idx"
            >{{item}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" :label="tableLang.coin" width="100">ONG</el-table-column>
        <el-table-column prop="price" align="center" :label="tableLang.price" width="200"></el-table-column>
        <el-table-column prop="date" align="center" :label="tableLang.date" width="240"></el-table-column>
        <el-table-column :label="tableLang.operating" align="center" width="140">
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
    <div class="paginatio">
      <el-pagination
        @current-change="handleCurrentChange"
        background
        layout="prev, pager, next"
        :total="recordCount"
        :page-size="pageSize"
      ></el-pagination>
    </div>
  </div>
</template>

<script>
import moment from 'moment'
export default {
  data() {
    return {
      recordCount: 0,
      searchText: '',
      tableData: [],
      pageSize: 10,
      currentPage: 1,
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
    handleClick(row) {
      console.log(row)
      sessionStorage.setItem('orderData', JSON.stringify(row))
      this.$router.push({ path: 'orderdetail', query: { commodityId: row.id } });
    },
    searchClick() {
      this.currentPage = 1
      this.getSearch()
    },
    async getSearch(pageNum = 1) {
      let params = {
        pageNumber: pageNum,
        pageSize: this.pageSize,
        tag: this.searchText
      }
      try {
        let res = await this.$store.dispatch('getCommodityList', params)
        if (res.status === 200 && res.data.desc === 'SUCCESS') {
          this.tableData = res.data.result.list
          this.tableData.map((item, idx) => {
            item.date = moment(item.date * 1000).format('LLL')
            item.price = item.price * Math.pow(10, -9)
          })
          this.recordCount = res.data.result.count
          console.log('this.tableData', this.tableData)
        } else {
          this.tableData = []
        }
      } catch (error) {
        this.tableData = []
      }
    },
    handleCurrentChange(val) {
      this.getSearch(val)
      this.currentPage = val
    },
    indexMethod(index) {
      return this.currentPage + index;
    }
  },
  mounted() {
    this.getSearch()
  },
}
</script>

<style lang='less' scoped>
.commodity_box {
  .search_box {
    padding: 0 10%;
    max-width: 40%;
    margin: 0 auto;
  }
  .list_box {
    margin: 0 auto;
  }
  .list_box,
  .paginatio {
    margin-top: 40px;
  }
}
</style>
