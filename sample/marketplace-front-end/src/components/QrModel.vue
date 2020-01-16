<template>
  <div>
    <el-dialog :title="$t('common.qrcode')" :visible.sync="isShow" width="30%" center>
      <img class="qrcoe_url" :src="url" alt />
      <span slot="footer" class="dialog-footer">
        <el-button @click="close">{{$t('common.cancel')}}</el-button>
        <el-button type="primary" @click="close">{{$t('common.sure')}}</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import QRCode from 'qrcode'
import { mapState } from 'vuex'

export default {
  data() {
    return {
    }
  },
  computed: {
    ...mapState({
      url: state => state.qrcodeParams.qrcodeUrl,
    }),
    isShow: {
      get() {
        return this.$store.state.qrcodeParams.isShow
      },
      set() {
        this.$store.commit('CHANGE_MODEL_STATE', false)
      }
    }
  },
  methods: {
    close() {
      this.$store.commit('CHANGE_MODEL_STATE', false)
    }
  },
}
</script>

<style lang='less' scoped>
.qrcoe_url {
  display: block;
  width: 200px;
  height: 200px;
  background: #000;
  margin: 0 auto;
}
</style>
