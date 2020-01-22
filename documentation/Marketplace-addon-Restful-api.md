# Marketplace-addon-Restful-api

[TOC]

## Marketplace-addon 接口规范：

### 上架数据

```text
url：/api/v1/order/auth
method：POST
```

- 请求：

```json
{
	"dataId": "did:ont:xxxx",
	"symbol": "symbol",
	"name": "name",
	"amount":100,
	"price":50,
	"transferCount":1,
	"accessCount":1,
	"expireTime":0,
	"makerReceiveAddress":"AUcDzN9YRmv9iu7aqMwpNf9SM8FSDPJ8ZB",
	"mpReceiveAddress":"AKruC9ZfrEPCW9GR44jsh1UgfERMoGWP64",
	"ojList":["AKruC9ZfrEPCW9GR44jsh1UgfERMoGWP64","AX4xDuwXPjkqinrujW44xxmwAP1b3fSeWC"],
	"domain":"hello.app.ont",
	"signature":""
}
```

| Field_Name | Type   | Description |
|:-----------|:-------|:------------|
| dataId   | String | 数据的Data ID    |
|symbol|String|预生成的token符号|
|name|String|预生成的token名称|
|amount|Long|预生成的token数量|
|price|Long|token的单价|
|transferCount|Long|允许单个token流转次数|
|accessCount|Long|允许单个token下载次数|
|expireTime|Long|token的过期时间，单位秒，0表示永久有效|
|makerReceiveAddress|String|数据提供者收款地址|
|mpReceiveAddress|String|平台方收款地址|
|ojList|List|仲裁方地址列表|
|domain|String|app对应的域名|
|signature|String|``domain``对应的ONT ID对``auth``字符串的签名|

- 响应：

```json
{
    "action": "authOrder",
    "error": 0,
    "desc": "SUCCESS",
    "result": {
        "qrCode": {
            "dataUrl": "http://192.168.1.129:10661/api/v1/param/3381987f-51b7-4d7f-8174-e15957a56104",
            "chainNet": "Testnet",
            "ons": "mpdemo.app.ont",
            "expire": 1579665745,
            "callbackUrl": "http://192.168.1.129:8099/api/v2/ontid/invoke",
            "id": "be28b98e-3a1c-489e-9239-4f3aaae634d7",
            "type": "ontid",
            "version": "v2.0.0"
        },
        "param": "{\"action\":\"signTransaction\",\"params\":{\"gasLimit\":2000000,\"contractHash\":\"c89b559c4a9e3300444e2b7bb34405c5ac53f42f\",\"functions\":[{\"args\":[{\"name\":\"dataId\",\"value\":\"String:did:ont:xxxx\"},{\"name\":\"index\",\"value\":1},{\"name\":\"symbol\",\"value\":\"String:symbol\"},{\"name\":\"name\",\"value\":\"String:name\"},{\"name\":\"authAmount\",\"value\":100},{\"name\":\"price\",\"value\":50},{\"name\":\"transferCount\",\"value\":1},{\"name\":\"accessCount\",\"value\":1},{\"name\":\"expireTime\",\"value\":0},{\"name\":\"makerTokenHash\",\"value\":\"ByteArray:62b46b80858a9b0bb7b118abb082b06eae7b65d0\"},{\"name\":\"makerReceiveAddress\",\"value\":\"Address:AUcDzN9YRmv9iu7aqMwpNf9SM8FSDPJ8ZB\"},{\"name\":\"mpReceiveAddress\",\"value\":\"Address:AKruC9ZfrEPCW9GR44jsh1UgfERMoGWP64\"},{\"name\":\"OJList\",\"value\":[\"Address:AKruC9ZfrEPCW9GR44jsh1UgfERMoGWP64\",\"Address:AX4xDuwXPjkqinrujW44xxmwAP1b3fSeWC\"]}],\"operation\":\"authOrder\"}],\"payer\":\"AcdBfqe7SG8xn4wfGrtUbbBDxw2x1e8UKm\",\"gasPrice\":500}}",
        "id": "3381987f-51b7-4d7f-8174-e15957a56104"
    },
    "version": "v1"
}
```

| Field_Name | Type   | Description                   |
|:-----------|:-------|:------------------------------|
| action     | String | 动作标志                      |
| version    | String | 版本号                        |
| error       | int    | 错误码                        |
| desc        | String | 成功为SUCCESS，失败为错误描述 |
| result     | Map | 成功返回二维码相关参数，失败返回""     |
| qrCode     | Map | 二维码相关参数，需要自行填充``signature``作为key     |
| param     | String | 注册DATA ID的交易参数，需要使用``domain``对应的ONT ID对其签名作为``signature``填充至``qrCode``     |
| id     | String | addon为请求生成的唯一标识，可用于查询扫码验签结果     |


### 下单购买

```text
url：/api/v1/order/take
method：POST
```

- 请求：

```json
{
	"authId": "12345678",
	"takerReceiveAddress": "AUcDzN9YRmv9iu7aqMwpNf9SM8FSDPJ8ZB",
	"tokenAmount": 1,
	"oj":"AKruC9ZfrEPCW9GR44jsh1UgfERMoGWP64",
	"domain":"hello.app.ont",
	"signature":""
}
```

| Field_Name | Type   | Description |
|:-----------|:-------|:------------|
| authId   | String | 上架后合约生成的ID    |
| takerReceiveAddress   | String | 需求者的接收地址    |
| tokenAmount   | Long | 购买数量    |
|oj         |String|需求者选择的仲裁者地址|
|domain|String|app对应的域名|
|signature|String|``domain``对应的ONT ID对``purchase``字符串的签名|

- 响应：

```json
{
    "action": "purchaseOrder",
    "error": 0,
    "desc": "SUCCESS",
    "result": {
        "qrCode": {
            "dataUrl": "http://192.168.1.129:10661/api/v1/param/1291797f-93ef-4ad2-8d98-d1c002ea6f0d",
            "chainNet": "Testnet",
            "ons": "mpdemo.app.ont",
            "expire": 1579674180,
            "callbackUrl": "http://192.168.1.129:8099/api/v2/ontid/invoke",
            "id": "8f0f749a-8043-482a-b8d6-507dfe752fb4",
            "type": "ontid",
            "version": "v2.0.0"
        },
        "param": "{\"action\":\"signTransaction\",\"params\":{\"gasLimit\":2000000,\"contractHash\":\"c89b559c4a9e3300444e2b7bb34405c5ac53f42f\",\"functions\":[{\"args\":[{\"name\":\"authId\",\"value\":\"ByteArray:12345678\"},{\"name\":\"takerReceiveAddress\",\"value\":\"Address:AUcDzN9YRmv9iu7aqMwpNf9SM8FSDPJ8ZB\"},{\"name\":\"tokenAmount\",\"value\":1},{\"name\":\"OJ\",\"value\":\"Address:AKruC9ZfrEPCW9GR44jsh1UgfERMoGWP64\"}],\"operation\":\"takeOrder\"}],\"payer\":\"AcdBfqe7SG8xn4wfGrtUbbBDxw2x1e8UKm\",\"gasPrice\":500}}",
        "id": "1291797f-93ef-4ad2-8d98-d1c002ea6f0d"
    },
    "version": "v1"
}
```

| Field_Name | Type   | Description                   |
|:-----------|:-------|:------------------------------|
| action     | String | 动作标志                      |
| version    | String | 版本号                        |
| error       | int    | 错误码                        |
| desc        | String | 成功为SUCCESS，失败为错误描述 |
| result     | Map | 成功返回二维码相关参数，失败返回""     |
| qrCode     | Map | 二维码相关参数，需要自行填充``signature``作为key     |
| param     | String | 消费token的交易参数，需要使用``domain``对应的ONT ID对其签名作为``signature``填充至``qrCode``     |
| id     | String | addon为请求生成的唯一标识，可用于查询扫码验签结果     |


### 确认订单

```text
url：/api/v1/order/confirm
method：POST
```

- 请求：

```json
{
	"orderId": "12345678",
	"domain":"hello.app.ont",
	"signature":""
}
```

| Field_Name | Type   | Description |
|:-----------|:-------|:------------|
| orderId   | Long | 购买后合约生成的订单号    |
|domain|String|app对应的域名|
|signature|String|``domain``对应的ONT ID对``confirm``字符串的签名|

- 响应：

```json
{
    "action": "confirm",
    "error": 0,
    "desc": "SUCCESS",
    "result": {
        "qrCode": {
            "dataUrl": "http://192.168.1.129:10661/api/v1/param/1a369d68-73b1-4496-aeab-f0dddca32579",
            "chainNet": "Testnet",
            "ons": "mpdemo.app.ont",
            "expire": 1579675086,
            "callbackUrl": "http://192.168.1.129:8099/api/v2/ontid/invoke",
            "id": "ca678370-ded4-4ddd-9b28-5f3e475304e2",
            "type": "ontid",
            "version": "v2.0.0"
        },
        "param": "{\"action\":\"signTransaction\",\"params\":{\"gasLimit\":2000000,\"contractHash\":\"c89b559c4a9e3300444e2b7bb34405c5ac53f42f\",\"functions\":[{\"args\":[{\"name\":\"orderId\",\"value\":\"ByteArray:12345678\"}],\"operation\":\"confirm\"}],\"payer\":\"AcdBfqe7SG8xn4wfGrtUbbBDxw2x1e8UKm\",\"gasPrice\":500}}",
        "id": "1a369d68-73b1-4496-aeab-f0dddca32579"
    },
    "version": "v1"
}
```

| Field_Name | Type   | Description                   |
|:-----------|:-------|:------------------------------|
| action     | String | 动作标志                      |
| version    | String | 版本号                        |
| error       | int    | 错误码                        |
| desc        | String | 成功为SUCCESS，失败为错误描述 |
| result     | Map | 成功返回二维码相关参数，失败返回""     |
| qrCode     | Map | 二维码相关参数，需要自行填充``signature``作为key     |
| param     | String | 消费token的交易参数，需要使用``domain``对应的ONT ID对其签名作为``signature``填充至``qrCode``     |
| id     | String | addon为请求生成的唯一标识，可用于查询扫码验签结果     |


### 申请仲裁

```text
url：/api/v1/order/token/balance/{tokenId}
method：GET
```

- 请求：

| Field_Name | Type   | Description |
|:-----------|:-------|:------------|
| tokenId   | Long | 查询token的ID    |

- 响应：

```json
{
    "action": "getTokenBalance",
    "error": 0,
    "desc": "SUCCESS",
    "result": {
        "accessCount": 9,
        "transferCount": 10,
        "expireTime": 1
    },
    "version": "v1"
}
```

| Field_Name | Type   | Description                   |
|:-----------|:-------|:------------------------------|
| action     | String | 动作标志                      |
| version    | String | 版本号                        |
| error       | int    | 错误码                        |
| desc        | String | 成功为SUCCESS，失败为错误描述 |
| result     | Map | 成功返回token信息，失败返回""     |
| accessCount     | Long | token允许的下载次数     |
| transferCount     | Long | token允许的流转次数    |
| expireTime    | Long | token的过期时间     |


### 查询二维码的扫码结果

```text
url：/api/v1/result/{id}
method：GET
```

- 请求：

| Field_Name | Type   | Description |
|:-----------|:-------|:------------|
|id|String|二维码id|

- 响应：

```json
{
    "action": "purchaseOrderResult",
    "error": 0,
    "desc": "SUCCESS",
    "result": {
        "result": "2",
        "tokenId": [
            6,
            6
        ],
        "orderId": "1efeb11772a9c4c454f95d8518d42aac0b39eafb"
    },
    "version": "v1"
}
```

```json
{
    "action": "authOrderResult",
    "error": 0,
    "desc": "SUCCESS",
    "result": {
        "result": "2",
        "authId": "35d3bcafa3e84d34be6ffff8713fde7d5e6f9e30"
    },
    "version": "v1"
}
```

| Field_Name | Type   | Description                   |
|:-----------|:-------|:------------------------------|
| action     | String | 二维码对应的动作标志                      |
| version    | String | 版本号                        |
| error       | int    | 错误码                        |
| desc        | String | 成功为SUCCESS，失败为错误描述 |
| result     | Map | 成功返回，失败返回""     |
| result        | String | null-未扫码；0-交易失败；1-交易发送成功；2-上链成功 |
| orderId        | String | 合约生成的订单号 |
| tokenId        | List | 以Data ID生成的Token ID，第一位表示起始ID，第二位表示结束ID |
| authId        | String | 合约生成的授权上架编号 |