<template>
  <div class="order_box">
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
    <el-tabs v-model="activeName" type="card" @tab-click="handleClick">
      <el-tab-pane :label="$t('center.buyer')" name="first">
        <Buyer></Buyer>
      </el-tab-pane>
      <el-tab-pane :label="$t('center.seller')" name="second">
        <Seller></Seller>
      </el-tab-pane>
      <!-- <el-tab-pane :label="$t('center.certification')" name="third">
        <Certifier></Certifier>
      </el-tab-pane>
      <el-tab-pane :label="$t('center.arbitration')" name="fourth">
        <Judger></Judger>
      </el-tab-pane> -->
    </el-tabs>
  </div>
</template>

<script>
import Buyer from './Buyer'
import Seller from './Seller'
import Certifier from './Certifier'
import Judger from './Judger'
export default {
  components: {
    Buyer, Seller, Certifier, Judger
  },
  data() {
    return {
      activeName: 'first'
    };
  },
  methods: {
    handleClick(tab, event) {
    },
    toIndex() {
      this.$router.push({ path: '/' })
    }
  }
}
</script>

<style lang='less' scoped>
.order_box {
  margin: 100px auto;
}
</style>
