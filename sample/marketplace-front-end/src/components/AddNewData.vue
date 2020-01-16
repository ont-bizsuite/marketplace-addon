<template>
  <div class="addBox">
    <h2>{{$t('common.add_data')}}</h2>
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
    <div class="formBox">
      <el-form
        :model="dynamicValidateForm"
        ref="dynamicValidateForm"
        label-width="100px"
        class="demo-dynamic"
      >
        <!-- 商品名 -->
        <el-form-item
          prop="data.name"
          :label="tableLang.name"
          :rules="[
            { required: true, message: tableLang.nameTip },
            { min: 1, max: 15, message: tableLang.lengthTips }
          ]"
        >
          <el-input v-model="dynamicValidateForm.data.name"></el-input>
        </el-form-item>
        <!-- 描述 -->
        <el-form-item
          prop="data.desc"
          :label="tableLang.desc"
          :rules="[
            { required: true, message: tableLang.descTip },
            { min: 1, max: 15, message: tableLang.lengthTips }
          ]"
        >
          <el-input v-model="dynamicValidateForm.data.desc"></el-input>
        </el-form-item>
        <!-- 图片地址 -->
        <el-form-item prop="data.img" :label="tableLang.imgAddress">
          <el-input v-model="dynamicValidateForm.data.img"></el-input>
        </el-form-item>
        <!-- 数据源 -->
        <el-form-item prop="data.dataSource" :label="tableLang.dataSource">
          <el-input v-model="dynamicValidateForm.dataSource"></el-input>
        </el-form-item>
        <!-- 标签 -->
        <el-form-item
          v-for="(tag, index) in dynamicValidateForm.tags"
          :label="tableLang.tag + (index + 1)"
          :key="tag.key"
          :prop="'tags.' + index + '.value'"
          :rules="[{
            required: true, message: tableLang.tagTip, trigger: 'blur'
        },{ min: 1, max: 15, message: tableLang.lengthTips }
        ]"
        >
          <el-input v-model="tag.value"></el-input>
          <el-button @click.prevent="removetag(tag)">{{$t('common.delete')}}</el-button>
        </el-form-item>
        <!-- 认证方 -->
        <el-form-item :label="tableLang.certifier">
          <el-select
            style="float: left;"
            v-model="dynamicValidateForm.certifier"
            :placeholder="tableLang.cerTips"
          >
            <el-option
              v-for="item in certifierArr"
              :key="item.id"
              :label="item.ontid"
              :value="item.ontid"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm('dynamicValidateForm')">{{$t('common.add')}}</el-button>
          <el-button @click="addtag">{{$t('common.add_new_tag')}}</el-button>
          <el-button @click="resetForm('dynamicValidateForm')">{{$t('common.reset')}}</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
function uuid(len, radix) {
  var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');
  var uuid = [], i;
  radix = radix || chars.length;

  if (len) {
    for (i = 0; i < len; i++) uuid[i] = chars[0 | Math.random() * radix];
  } else {
    var r;
    uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
    uuid[14] = '4';
    for (i = 0; i < 36; i++) {
      if (!uuid[i]) {
        r = 0 | Math.random() * 16;
        uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
      }
    }
  }

  return uuid.join('');
}
import { client } from 'ontology-dapi'
import { sha256 } from 'js-sha256'

export default {
  data() {
    return {
      tableLang: {
        name: this.$t('common.commodity_name'),
        nameTip: this.$t('common.please_enter') + this.$t('common.commodity_name'),
        desc: this.$t('common.description'),
        descTip: this.$t('common.please_enter') + this.$t('common.description'),
        imgAddress: this.$t('common.img_address'),
        dataSource: this.$t('common.data_source'),
        tag: this.$t('common.tag'),
        tagTip: this.$t('common.tag_not_empty'),
        certifier: this.$t('common.certifier'),
        cerTips: this.$t('common.cerTip'),
        lengthTips: this.$t('common.length_tips')
      },
      dynamicValidateForm: {
        tags: [{
          value: ''
        }],
        price: '',
        coin: 'ONG',
        data: {
          dataId: null,
          name: null,
          desc: null,
          token: null,
          keywords: [],
          img: null,
          metadata: null
        },
        certifier: '',
        judger: '',
        dataSource: ''
      },
      accountid: '',
      metadata: {
        context: '',
        type: 'Dataset',
        identifier: '',  // uuid----dataId
        name: '',   // name----name
        description: '', //  description
        keywords: '',
        publisher: {
          type: 'Person',
          identifier: '',   // ONT ID
          dateCreated: new Date()
        },
        datePublished: new Date()
      },
      ont_id: null,
      certifierArr: [
      ],
      judgerArr: []
    };
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          console.log(this.dynamicValidateForm)
          this.addNewData()
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
    },
    removetag(item) {
      var index = this.dynamicValidateForm.tags.indexOf(item)
      if (index !== 0) {
        this.dynamicValidateForm.tags.splice(index, 1)
      }
    },
    addtag() {
      this.dynamicValidateForm.tags.push({
        value: '',
        key: Date.now()
      });
    },
    toIndex() {
      this.$router.push({ path: '/' })
    },
    async addNewData() {
      try {
        console.log('asdfasdfasdf', this.dataParams)
        // return
        let res = await this.$store.dispatch('addCommodity', this.dataParams)
        console.log('addnewdata', res)
        if (res && res.data.desc === 'SUCCESS') {
          this.$message({
            message: this.$t('common.add_suc'),
            type: 'success',
            center: true,
            duration: 2000
          })
          this.$refs['dynamicValidateForm'].resetFields();
        } else {
          console.log('err1')
          this.$message({
            message: this.$t('common.add_fail'),
            type: 'error',
            center: true,
            duration: 2000
          })
        }

      } catch (error) {
        console.log('err2')
        this.$message({
          message: this.$t('common.add_fail'),
          type: 'error',
          center: true,
          duration: 2000
        })
      }
    },
    toDataId() {
      this.dynamicValidateForm.data.dataId = uuid()
    }
  },
  async mounted() {
    // this.accountid = await client.api.asset.getAccount()
    this.ont_id = sessionStorage.getItem("user_ontid")
    try {
      let res = await this.$store.dispatch('getCertifier')
      if (res.data && res.data.desc === 'SUCCESS') {
        this.certifierArr = res.data.result
        this.dynamicValidateForm.certifier = this.certifierArr[0].ontid
      } else {
        this.certifierArr = []
        this.dynamicValidateForm.certifier = ''
      }
    } catch (error) {
      this.certifierArr = []
      this.dynamicValidateForm.certifier = ''
    }

    try {
      let res = await this.$store.dispatch('getJudger')
      if (res.data && res.data.desc === 'SUCCESS') {
        this.judgerArr = res.data.result
        this.dynamicValidateForm.judger = this.judgerArr[0].ontid
      } else {
        this.judgerArr = []
        this.dynamicValidateForm.judger = ''
      }
    } catch (error) {
      this.judgerArr = []
      this.dynamicValidateForm.judger = ''
    }
  },
  computed: {
    dataParams() {
      let cpas = {
        coin: '',
        price: null,
        ontid: '',
        data: {
          dataId: '',
          desc: '',
          img: '',
          keywords: [],
          metadata: '',
          name: "",
        }
      }
      cpas.coin = this.dynamicValidateForm.coin
      cpas.price = this.dynamicValidateForm.price
      cpas.ontid = this.ont_id
      cpas.certifier = this.dynamicValidateForm.certifier
      // cpas.judger = this.dynamicValidateForm.judger
      cpas.dataSource = this.dynamicValidateForm.dataSource
      console.log('this.dynamicValidateForm.data.desc', this.dynamicValidateForm.data.desc);
      this.dynamicValidateForm.tags.map(item => {
        cpas.data.keywords.push(item.value)
      })
      cpas.data.desc = this.dynamicValidateForm.data.desc
      cpas.data.dataId = this.dynamicValidateForm.data.dataId
      cpas.data.img = this.dynamicValidateForm.data.img
      cpas.data.name = this.dynamicValidateForm.data.name
      this.metadata.identifier = this.dynamicValidateForm.data.dataId
      this.metadata.name = this.dynamicValidateForm.data.name
      this.metadata.description = this.dynamicValidateForm.data.desc
      this.metadata.publisher.identifier = this.ont_id

      this.metadata.keywords = [...cpas.data.keywords].join()
      console.log('this.metadata', this.metadata);
      this.dynamicValidateForm.data.metadata = JSON.stringify(this.metadata)
      cpas.data.metadata = this.dynamicValidateForm.data.metadata
      return cpas
    }
  },
}
</script>

<style lang='less' scoped>
.addBox {
  margin: 0 auto;
  width: 80%;
  .formBox {
    .el-input {
      width: 80%;
      float: left;
    }
  }
}
</style>
