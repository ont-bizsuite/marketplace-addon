<template>
  <div class="dataid_detail">
    <div style="overflow: hidden; margin-bottom: 20px;margin-top: 20px;">
      <el-button
        @click="$router.go(-1)"
        type="primary"
        plain
        style="float: left"
      >{{$t('common.back_to_prev')}}</el-button>
      <el-button
        @click="$router.push({path: '/'})"
        type="primary"
        plain
        style="float: right"
      >{{$t('common.to_home')}}</el-button>
    </div>
    <div class="id_detail">
      <div class="id_box">DataID: {{dataId}}</div>
      <div class="id_msg">
        <p>
          {{$t('common.create_time')}}：
          <span>{{idDetail.createdTime}}</span>
        </p>
        <p>
          {{$t('common.height')}}：
          <span>{{idDetail.blockHeight}}</span>
        </p>
        <p>
          {{$t('common.hash')}}：
          <span>{{idDetail.txHash}}</span>
        </p>
      </div>
    </div>
    <div class="tokenid_box">
      <h2>TokenID List</h2>
      <el-table :data="tableData" border style="width: 100%" :empty-text="$t('common.no_data')">
        <el-table-column type="index" align="center" width="50" :index="indexMethod"></el-table-column>
        <el-table-column prop="tokenId" align="center" label="TokenID" width="180"></el-table-column>
        <el-table-column prop="blockHeight" align="center" label="高度" width="180"></el-table-column>
        <el-table-column prop="createdTime" align="center" label="创建时间" width="180"></el-table-column>
        <!-- <el-table-column prop="transferTimes" align="center" label="流转次数" width="100"></el-table-column> -->
        <el-table-column prop="txHash" align="center" label="txHash"></el-table-column>
        <el-table-column label="操作" align="center" width="140">
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
export default {
  data() {
    return {
      dataId: '',
      tableData: [],
      idDetail: {
        blockHeight: '',
        createdTime: '',
        txHash: ''
      }
    }
  },
  async mounted() {
    this.dataId = this.$route.query.dataId
    let result = await this.$store.dispatch('getDataIdDetail', this.dataId)
    console.log('result', result)
    this.idDetail.blockHeight = result.data.result.blockHeight
    this.idDetail.createdTime = result.data.result.createdTime
    this.idDetail.txHash = result.data.result.txHash
    this.tableData = result.data.result.tokens
  },
  methods: {
    indexMethod(index) {
      return index + 1;
    },
    handleClick(data) {
      console.log(data.tokenId)
      this.$router.push({ name: 'TokenDetail', query: { tokenId: data.tokenId } })
    }
  },
}
</script>

<style lang='less' scoped>
.dataid_detail {
  text-align: left;
  .id_detail {
    .id_box {
      font-size: 24px;
      color: #000;
    }
    .id_msg {
      p {
        font-size: 18px;
        span {
          color: #409eff;
        }
      }
    }
  }
  .tokenid_box {
    padding-top: 40px;
    h2 {
      text-align: center;
    }
  }
}
</style>
