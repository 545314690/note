# Sketch标签API接口说明

## API接口地址basePath

basePath :  http://ecdata-sketch.58dns.org/userportraitback/rest-api/

## 标签元数据接口列表

| 名字                       | 地址                         | 请求方式 | 参数                  | 返回值       |
| -------------------------- | ---------------------------- | -------- | --------------------- | ------------ |
| 根据标签字段查询           | label/findByField            | GET      | Sting field           | 标签列表     |
| 根据标签字段类型查询       | label/findByType             | GET      | String field          | 标签列表     |
| 根据dpid更新标签bitmap时间 | label/updateBitMapDateByDpId | PUT      | Long dpId,String date | 更新成功行数 |
| 根据dpid更新标签时间       | label/updateLabelDateByDpId  | PUT      | Long dpId,String date | 更新成功行数 |

### 请求示例

curl http://ecdata-sketch.58dns.org/userportraitback/rest-api/label/findByField?field=age

### 返回

```
{
"msg": "成功",
"data": [
{
"createDate": 1608865493000,
"creator": "lisenmiao",
"dpTaskId": 111,
"field": "age",
"hiveTableName": "test",
"id": 1,
"name": "年龄",
"type": "int",
"updateDate": 1608865493000,
"valid": true
}
],
"code": 20000
}
```

