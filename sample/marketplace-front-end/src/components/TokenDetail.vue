<template>
  <div class="tokenid_detail">
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
    <div class="id_box">
      <div class="tk_id">
        TokenID:
        <span>{{tokenId}}</span>
      </div>
      <p>
        DataID:
        <span>{{idData.dataId}}</span>
      </p>
      <p>
        {{$t('common.create_time')}}:
        <span>{{idData.createdTime}}</span>
      </p>
      <p>
        {{$t('common.height')}}:
        <span>{{idData.blockHeight}}</span>
      </p>
      <p>
        {{$t('common.hash')}}:
        <span>{{idData.txHash}}</span>
      </p>
      <p>
        {{$t('common.transferTimes')}}:
        <span>{{idData.transferTimes}}</span>
      </p>
    </div>
    <div class="table_box">
      <div class="detail">{{$t('common.transferDetail')}}</div>
      <el-table :data="tableData" stripe style="width: 100%" :empty-text="$t('common.no_data')">
        <el-table-column align="center" prop="fromAddress" :label="tableLang.sell" width="360"></el-table-column>
        <el-table-column align="center" prop="toAddress" :label="tableLang.buy" width="360"></el-table-column>
        <el-table-column align="center" prop="txHash" :label="tableLang.hash" width="360"></el-table-column>
        <el-table-column align="center" prop="createdTime" width="160" :label="tableLang.createdTime"></el-table-column>
        <el-table-column align="center" prop="blockHeight" :label="tableLang.height"></el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      tokenId: '',
      idData: {
        createdTime: '',
        dataId: '',
        blockHeight: '',
        transferTimes: '',
        txHash: ''
      },
      tableData: [],
      tableLang: {
        sell: this.$t('center.sell'),
        buy: this.$t('center.buy'),
        hash: this.$t('common.hash'),
        createdTime: this.$t('common.create_time'),
        height: this.$t('common.height')
      }
    }
  },
  async mounted() {
    this.tokenId = this.$route.query.tokenId
    let result = await this.$store.dispatch('getTokenIdDetail', this.tokenId)
    console.log('result', result)
    this.idData.createdTime = result.data.result.createdTime
    this.idData.dataId = result.data.result.dataId
    this.idData.blockHeight = result.data.result.blockHeight
    this.idData.transferTimes = result.data.result.transferTimes
    this.idData.txHash = result.data.result.txHash
    this.tableData = result.data.result.tokenTransferResp
  }
}
</script>

<style lang='less' scoped>
.tokenid_detail {
  text-align: left;
  .id_box {
    .tk_id {
      font-size: 24px;
    }
    p {
      font-size: 18px;
      span {
        color: #409eff;
      }
    }
  }
  .table_box {
    .detail {
      font-size: 24px;
      text-align: center;
      margin-top: 40px;
    }
  }
}
</style>
