# Instruction

## Get Start

```
请参考：https://github.com/ont-bizsuite/signing-addon/blob/master/documentation/Instruction.md
```

storage-addon配置说明：
```json
{
  "payerAddress": "AcdBfqe7SG8xn4wfGrtUbbBDxw2x1e8UKm",
  "payerPrivateKey": "",
  "actions": [
    {
      "action": "auth",
      "type": "ontid",
      "onchainRec": true,
      "dataUrl": "https://prod.microservice.ont.io/marketplace-addon/api/v1/param",
      "callbackUrl": "http://marketplace-addon:10661/api/v1/callback",
      "mainNet": false
    },
    {
      "action": "purchase",
      "type": "ontid",
      "onchainRec": true,
      "dataUrl": "https://prod.microservice.ont.io/marketplace-addon/api/v1/param",
      "callbackUrl": "http://marketplace-addon:10661/api/v1/callback",
      "mainNet": false
    },
    {
      "action": "confirm",
      "type": "ontid",
      "onchainRec": true,
      "dataUrl": "https://prod.microservice.ont.io/marketplace-addon/api/v1/param",
      "callbackUrl": "http://marketplace-addon:10661/api/v1/callback",
      "mainNet": false
    }
  ]
}
```

| Field_Name | Type   | Description                   |
|:-----------|:-------|:------------------------------|
| payerAddress     | String | 手续费付款钱包地址                      |
| payerPrivateKey    | String | 钱包私钥                        |
| actions       | List    | 动作集合                        |
| action        | String | 注册的动作名称:auth-上架数据;purchase-购买数据;confirm-订单确认 |
| type     | String | 签名的账户类型：``ontid``或``address``     |
| onchainRec        | boolean | 是否上链动作 |
| dataUrl        | String | OntAuth获取交易参数的接口地址(需定义为Get请求) |
| callbackUrl        | String | signing-server需要回调告知app验签结果的接口地址(需定义为Post请求) |
| mainNet        | boolean | 节点类型：true-主网；false-测试网 |


6.使用SDK开发应用：
```
https://github.com/ont-bizsuite/marketplace-sdk-java
```