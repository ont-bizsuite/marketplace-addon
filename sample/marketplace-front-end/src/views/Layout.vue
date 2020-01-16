<template>
  <div class="layout">
    <el-container>
      <el-header>
        <Theheader></Theheader>
      </el-header>
      <el-main>
        <router-view/>
        <Commoditylist v-if='this.$route.path == "/"'></Commoditylist>
      </el-main>
    </el-container>
    <QrModel />
  </div>
</template>

<script>
import Theheader from '../components/Theheader'
import Commoditylist from '../components/Commoditylist'
import QrModel from '../components/QrModel'
export default {
  components: {
    Theheader,
    Commoditylist,
    QrModel
  },
  methods: {
    open() {
      let qrparams = {
        params: {
          a: 'haha',
          b: 'hehe'
        },
        isShow: true
      }
      this.$store.dispatch('changeQrcode', qrparams)
    }
  },
}
</script>

<style lang='less' scoped>
.layout {
  width: 80%;
  margin: 0 auto;
  height: 100%;
  .el-container {
    height: 100%;
  }
}
</style>
