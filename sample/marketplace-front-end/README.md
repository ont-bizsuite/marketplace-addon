# Marketplace-web

### Project setup
```
npm install
```

#### Compiles and hot-reloads for development
```
npm run serve
```

#### Compiles and minifies for production
```
npm run build
```

#### Compiles and minifies for test environment
```
npm run build:dev
```

#### Run your tests
```
npm run test
```

#### Lints and fixes files
```
npm run lint
```

#### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).


### API

#### 商品查询api

```
url:    /api/v1/dataset

method: POST

传递参数说明:   
    
    params = {
        pageIndex: 页码,
        pageSize: 每页返回条数,
        queryParams: [
            percent: 100,
            text: '查询参数'
        ]  
    }
    
    注意：params需转换成json格式方可使用
```

