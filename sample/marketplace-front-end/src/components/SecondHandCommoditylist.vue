<template>
  <div class="commodity_box">
    <div style="overflow: hidden; margin-bottom: 20px;">
      <el-button
        @click="$router.go(-1)"
        type="primary"
        plain
        style="float: left"
      >{{$t('common.back_to_prev')}}</el-button>
      <el-button
        @click="toIndex()"
        type="primary"
        plain
        style="float: right"
      >{{$t('common.to_home')}}</el-button>
    </div>
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
              v-for="(item, idx) in scope.row.keywords"
              :key="idx"
            >{{item}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" :label="tableLang.coin" width="100">ONG</el-table-column>
        <el-table-column prop="price" align="center" :label="tableLang.price" width="100"></el-table-column>
        <el-table-column prop="createTime" align="center" :label="tableLang.date" width="240"></el-table-column>
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
export default {
  data() {
    return {
      recordCount: 0,
      searchText: '',
      tableData: [],
      pageSize: 10,
      currentPage: 0,
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
      this.$router.push({ path: 'orderdetail' });
    },
    searchClick() {
      this.currentPage = 0
      this.getSearch()
    },
    async getSearch(pageNum = 0) {
      let params = {
        pageNum,
        pageSize: this.pageSize,
        queryParams: [
          {
            percent: 100,
            text: this.searchText
          }
        ]
      }
      // this.$store.dispatch('getCommodityList', params)
      try {
        let res = await this.$store.dispatch('getSecondHandCommodityList', params)
        console.log(res)

        if (res.status === 200 && res.data.desc === 'SUCCESS') {
          this.tableData = res.data.result.recordList
          this.tableData.map((item, idx) => {
            item.price = item.price * Math.pow(10, -9)
          })
          this.recordCount = res.data.result.recordCount
          console.log('this.tableData', this.tableData)
        } else {
          this.tableData = []
        }
      } catch (error) {
        this.tableData = []
      }
    },
    handleCurrentChange(val) {
      this.getSearch(val - 1)
      this.currentPage = val - 1
    },
    indexMethod(index) {
      return this.currentPage * this.pageSize + index + 1;
    },
    toIndex() {
      this.$router.push({ path: '/' })
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
