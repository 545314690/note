# Sketch API接口说明

## API接口地址basePath

basePath :  http://ecdata-sketch.58dns.org/userportraitback/rest-api/

## 接口列表

### 根据用户名获取配置的分群列表：getUserGroupList

参数：oa

请求示例：

curl  http://ecdata-sketch.58dns.org/userportraitback/rest-api/getUserGroupList?oa=test

返回示例：

```json
{
"msg": "成功",
"data": [
{
"calculateAssess": false,    //是否计算效果分布
"calculateCostSeconds": 56,  //计算用户数耗时（s)
"calculateDate": 1597796833000, //本次计算开始时间
"calculateDistribution": true, //是否基础属性分布
"calculateSum": false, //是否计算行为特征分布
"calculateSumCostSeconds": 357, //计算分布耗时 (s)
"calculateType": 1,  //计算类型 0：单次  1：例行
"configJson": "{}",  //配置条件 json
"createDate": 1597658506000,  //创建时间
"creator": "test", //创建人
"enable": true,  //例行任务是否开启
"exportStatus": 0, //导出状态
"id": 1349, //分群id
"name": "其余用户(兴趣低)", //分群名
"sharedOaList": "oa2,oa1", //共享的人
"status": 9,  //计算状态：见status字典
"updateDate": 1597798080000, //更新时间
"uploadWos": false, //是否需要下载
"uploadYunChuang": true, //是否上传云窗
"userCount": 14203, //分群用户数
    "exportDate": 1597806241000, //导出时间
"exportFileUrl": "http://prod2.wos.58dns.org/RTPoNmLkRVA/eventtrack/1597806230000_Sketch_20200818_1362.txt" //导出文件地址
,
"config": { //分群配置
"labelList": [//标签列表
{
"actionType": "label",
"calculateType": "VALUE",
"cate1": [],
"cate2": [],
"field": "pl_nr_defined_infoid_post_3d", //标签名
"fieldType": "int",  //标签类型
"metricName": "发帖次数",  //指标名
"operator": ">",  //操作符
"type": "num",  
"value1": "3",  //value1，  该标签的含义为   "pl_nr_defined_infoid_post_3d标签下发帖次数>3"
"value2": "6"  //value2  ,若“operator=区间”，则value2启用，标签含义为“pl_nr_defined_infoid_post_3d标签下发帖次数为3-6次”
}
],
"basicConfig": {//基本属性配置
"age": "", //年龄
"appName": [],//竞品app 列表，多选 或的关系
"brand": "", //手机品牌
"busstypeLike": "", //业务线
"cate1Like": [],
"cate2Like": [],
"city": "",
"city1": [
""
],//定位城市，且可以多选
"customUserSetId": "", //自定义用户集的id
"customUserSetTableName": "data_usergroup.t_person_custom_group_imei_",
"gender": "", //性别
"identityName": [],
"identityProduct": "",
"industry": "",//行业
"jobCate2": [],
"jobCate3": [],
"ltvConfig": { //lvt配置，当field不为空，为“ltv”时，该条件启用
"calculateType": "=",
"field": "",
"operator": "=",
"value1": "0",
"value2": "0"
},
"mobileSize": [], //手机型号，多选或的关系
"pid": [ //
""
],
"product": "",
"province": [
""
],
"version": [
""
]
}
}]
}
```

#### 计算状态：status字典

```sql
 /**
     * 计算状态
     */
    NOT_CALCULATED("未开始",0),
    CALCULATING_COUNT("正在计算用户数",1),
    CALCULATE_COUNT_SUCCESS("计算用户数完成",2),
    CALCULATE_COUNT_ERROR("计算用户数出错",3),
    WAITING_CALCULATE_SUM("等待计算分布",10),
    CALCULATING_SUM("正在计算分布",4),
    CALCULATE_SUM_SUCCESS("计算分布完成",5),
    CALCULATING_SUM_ERROR("计算分布出错",6),
    UPLOADING_TO_DP("数据表生成中",7),
    UPLOADING_TO_DP_ERROR("数据表生成失败",8),
    CALCULATE_SUCCESS("计算完成",9),
    CONFIG_ERROR("规则配置错误",11);
```

### 根据id获取分群：getUserGroupById

参数：id

请求示例：

curl  http://ecdata-sketch.58dns.org/userportraitback/rest-api/getUserGroupById?id=1

返回示例：

```json
{
"msg": "成功",
"data": {
"calculateAssess": false,
"calculateCostSeconds": 0,
"calculateDate": 1597806145000,
"calculateDistribution": true,
"calculateSum": false,
"calculateSumCostSeconds": 10,
"calculateType": 0,
"configJson": "{}",
"createDate": 1597806144000,
"creator": "lisenmiao",
"exportDate": 1597806241000,
"exportFileUrl": "http://prod2.wos.58dns.org/RTPoNmLkRVA/eventtrack/1597806230000_Sketch_20200818_1362.txt",
"exportStatus": 2,
"id": 1362,
"name": "imei2测试",
"status": 9,
"updateDate": 1597806241000,
"uploadWos": true,
"uploadYunChuang": true,
"userCount": 2267634
},
"code": 20000
}
```



## 分群计算成功发送WMB广播消息

### 消息主题id：105106

### 消息体格式 WmbConsumer body:

```json
{
 "msg":"success",
 "hiveTable":"hdp_lbg_ecdata_dw_defaultdb.t_person_imei_group_1314",
 "updateDate":1597903980000,
 "userCount":2855214,
 "calculateDate":1597903746000,
 "groupId":1314,
 "name":"【20200804下午LTV测试】选取部落用户",
 "hdfsUri":"/home/hdp_lbg_ecdata_dw/resultdata/sketch/test001/t_person_imei_group_1314",
"exportFileUrl": "http://prod2.wos.58dns.org/RTPoNmLkRVA/eventtrack/1597806230000_Sketch_20200818_1362.txt"
}
```

