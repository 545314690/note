- [一、明细表构建](https://iwiki.58corp.com/wiki/ecdatatech/view/数据技术部/技术方案储备/clickhouse/ClickhouseBitmap合集/画像bitmap应用/#H4E003001660E7EC6886867845EFA)
  - [1.明细表改造](https://iwiki.58corp.com/wiki/ecdatatech/view/数据技术部/技术方案储备/clickhouse/ClickhouseBitmap合集/画像bitmap应用/#H1.660E7EC6886865399020)
  - [2.明细表拆分](https://iwiki.58corp.com/wiki/ecdatatech/view/数据技术部/技术方案储备/clickhouse/ClickhouseBitmap合集/画像bitmap应用/#H2.660E7EC6886862C65206)
- [二、bitmap表构建](https://iwiki.58corp.com/wiki/ecdatatech/view/数据技术部/技术方案储备/clickhouse/ClickhouseBitmap合集/画像bitmap应用/#H4E8C3001bitmap886867845EFA)
  - [1.基于物化视图构建bitmap表](https://iwiki.58corp.com/wiki/ecdatatech/view/数据技术部/技术方案储备/clickhouse/ClickhouseBitmap合集/画像bitmap应用/#H1.57FA4E8E7269531689C656FE67845EFAbitmap8868)
  - [2.手动构建bitmap表](https://iwiki.58corp.com/wiki/ecdatatech/view/数据技术部/技术方案储备/clickhouse/ClickhouseBitmap合集/画像bitmap应用/#H2.624B52A867845EFAbitmap8868)
- [三、性能测试](https://iwiki.58corp.com/wiki/ecdatatech/view/数据技术部/技术方案储备/clickhouse/ClickhouseBitmap合集/画像bitmap应用/#H4E093001602780FD6D4B8BD5)
  - [1.直接基于AggregatingMergeTree表查询](https://iwiki.58corp.com/wiki/ecdatatech/view/数据技术部/技术方案储备/clickhouse/ClickhouseBitmap合集/画像bitmap应用/#H1.76F463A557FA4E8EAggregatingMergeTree886867E58BE2)
  - [2.修改源码或者实现并行查询](https://iwiki.58corp.com/wiki/ecdatatech/view/数据技术部/技术方案储备/clickhouse/ClickhouseBitmap合集/画像bitmap应用/#H2.4FEE65396E907801621680055B9E73B05E76884C67E58BE2)
  - [四、正式性能测试](https://iwiki.58corp.com/wiki/ecdatatech/view/数据技术部/技术方案储备/clickhouse/ClickhouseBitmap合集/画像bitmap应用/#H56DB30016B635F0F602780FD6D4B8BD5)

# 一、明细表构建



## 1.明细表改造



改造宽表为高表,字段为如下,以标签类型分区,以标签类型+标签值为索引

数据写入是以imei hash值路由,保证统一imei只会落到一个分片

```
CREATE TABLE if not exists test.user_lable_string_20201118(  `lable_name` String COMMENT '标签类型',   `lable_value` String COMMENT '标签值',   `imei` UInt64 COMMENT '标签对应唯一数值型id') ENGINE = ReplicatedMergeTree('/clickhouse/tables/user_group/test/user_lable_string_20201118/{cluster2_shard}','{cluster2_replica}')   PARTITION BY lable_name  ORDER BY (lable_name,lable_value)   SETTINGS index_granularity = 81920;
```

好处:

标签解耦,增、删、重跑标签数据可以互不影响,实现实时标签导入

减少分布式表merge时间,实现并行查询(需修改源码或者另外实现)

不足:

增加空间占用

分布式表写入增大zk压力|本地表写入 程序额外处理

## 2.明细表拆分

根据标签值类型的不同将标签明细表分为以下四类

| 标签类型 | 标签示例             | ck表              |
| -------- | -------------------- | ----------------- |
| 字符型   | 性别(男\|女)         | user_lable_string |
| 整数型   | 年龄(1-200)          | user_lable_int    |
| 浮点型   | 收入(66.6)           | user_lable_double |
| 日期型   | 新增日期(2020-11-18) | user_lable_date   |

--定义string类型画像标签明细本地表 

```
 CREATE TABLE if not exists test.user_lable_string_20201118(   `lable_name` String COMMENT '标签类型',   `lable_value` String COMMENT '标签值',   `imei` UInt64 COMMENT '标签对应唯一数值型id') ENGINE = ReplicatedMergeTree('/clickhouse/tables/user_group/test/user_lable_string_20201118/{cluster2_shard}','{cluster2_replica}')   PARTITION BY lable_name   ORDER BY (lable_name,lable_value)   SETTINGS index_granularity = 81920;  
```

 --定义string类型画像标签明细分布式表 

```
 CREATE TABLE if not exists test.user_lable_string (   `lable_name` String,   `lable_value` String,   `imei` UInt64 )   ENGINE = Distributed('cluster2_2shards_2replicas','test','user_lable_string_20201118',cityHash64(imei));   
```

 --定义int类型画像标签明细本地表  

```
CREATE TABLE if not exists test.user_lable_int_20201118(   `lable_name` String,   `lable_value` Int64,   `imei` UInt64 ) ENGINE = ReplicatedMergeTree('/clickhouse/tables/user_group/test/user_lable_int_20201118/{cluster2_shard}','{cluster2_replica}')   PARTITION BY lable_name   ORDER BY (lable_name,lable_value)   SETTINGS index_granularity = 81920;   
```

--定义int类型画像标签明细分布式表  

```
CREATE TABLE if not exists test.user_lable_int (   `lable_name` String,   `lable_value` Int64,   `imei` UInt64 )   ENGINE = Distributed('cluster2_2shards_2replicas','test','user_lable_int_20201118',cityHash64(imei)); 
```

  --定义double类型画像标签明细本地表 

```
 CREATE TABLE if not exists test.user_lable_double_20201118(   `lable_name` String,   `lable_value` Float64,   `imei` UInt64 ) ENGINE = ReplicatedMergeTree('/clickhouse/tables/user_group/test/user_lable_double_20201118/{cluster2_shard}','{cluster2_replica}')   PARTITION BY lable_name   ORDER BY (lable_name,lable_value)   SETTINGS index_granularity = 81920;  
```

 --定义double类型画像标签明细分布式表  

```
CREATE TABLE if not exists test.user_lable_double (   `lable_name` String,   `lable_value` Float64,   `imei` UInt64 )   ENGINE = Distributed('cluster2_2shards_2replicas','test','user_lable_double_20201118',cityHash64(imei));  
```

 --定义date类型画像标签明细本地表 

```
 CREATE TABLE if not exists test.user_lable_date_20201118(   `lable_name` String,   `lable_value` Date,   `imei` UInt64 ) ENGINE = ReplicatedMergeTree('/clickhouse/tables/user_group/test/user_lable_date_20201118/{cluster2_shard}','{cluster2_replica}')   PARTITION BY lable_name   ORDER BY (lable_name,lable_value)   SETTINGS index_granularity = 81920; 
```

  --定义date类型画像标签明细分布式表  

```
CREATE TABLE if not exists test.user_lable_date (   `lable_name` String,   `lable_value` Date,   `imei` UInt64 )   ENGINE = Distributed('cluster2_2shards_2replicas','test','user_lable_date_20201118',cityHash64(imei));
```

好处:

分表,提升查询速度

不足:

生产bitmap时需额外处理不同数据表

# 二、bitmap表构建

## 1.基于物化视图构建bitmap表



```
create materialized view if not exists test.user_lable_string_bitmap_20201118(   `lable_name` String,   `lable_value` String,   `imeiState` AggregateFunction(groupBitmap, UInt64)  )  ENGINE = AggregatingMergeTree()  PARTITION BY lable_name  ORDER BY (lable_name, lable_value)  settings index_granularity=128  as  select  lable_name,  lable_value,  groupBitmapState(imei) as imeiState  from test.user_lable_string_20201118  group by  lable_name,  lable_value;
```

好处:

直接基于明细表构建物化视图,屏蔽bitmap中间表构建逻辑,用户只需关心明细数据的写入即可

不足:

无法便捷的感知数据是否正常,目前测试发现物化视图丢失数据问题严重，升级版本可解决

## 2.手动构建bitmap表



--创建物化视图的视图表  

```
CREATE TABLE if not exists test.user_lable_string_bitmap (   `lable_name` String,   `lable_value` String,   `imeiState` AggregateFunction(groupBitmap, UInt64))   ENGINE = Distributed('cluster2_2shards_2replicas','test','user_lable_string_bitmap_20201118',rand());    insert into test.user_lable_string_bitmap_20201118  select  lable_name,  lable_value,  groupBitmapState(imei) as imeiState  from test.user_lable_string_20201118  group by  lable_name,  lable_value;
```

好处:

数据质量便于控制

不足:

需要额外增加bitmap表生产流程

# 三、性能测试

## 1.直接基于AggregatingMergeTree表查询



对于标签基数较大,且分布较为均匀的数据提效明显,例如年龄

对于标签基于较小,bitmap查询性能不如直接查询宽表

## 2.修改源码或者实现并行查询



1.修改分布式表查询处理逻辑,将并行等待merge修改为并行返回结果

2.使用with as + join方式实现并行返回结果

以下为不同查询方式查询对应标签uv的测试数据,单位为毫秒

| 标签     | 宽表        | 直接查询AggregatingMergeTree | with as + join |
| -------- | ----------- | ---------------------------- | -------------- |
| 性别为女 | 命中缓存747 | 命中缓存1668                 | 命中缓存190    |
| 年龄为20 | 命中缓存602 | 命中缓存838                  | 命中缓存280    |

## 四、正式性能测试

以下耗时皆为毫秒

| 画像分群id | 明细uv                                                       | bitmap uv                                                    | 明细 insert                                                  | bitmap insert                                                | 明细计算耗时 | bitmap计算耗时 | 明细insert耗时 | bitmapinsert耗时 |
| ---------- | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------ | -------------- | -------------- | ---------------- |
| 1738       | select  count(distinct a.imei) FROM  (   SELECT    imei   from    data_usergroup.t_person_imei_total t1   where    t1.province in ('重庆市', '天津市')    and 1 = 1  ) a | select bitmapCardinality(groupBitmapMergeState(imeiState)) from data_usergroup.user_lable_string_bitmap where lable_name='province' and lable_value in ('重庆市', '天津市'); | insert into test.t_person_imei_group_1738 select  max(a.stat_date),  a.imei,  max(a.idfa),  max(a.new_flag),  max(a.first_date),  max(a.last_date),  max(a.reg_cid),  max(a.busstype_like),  max(a.cate1_like),  max(a.cate2_like),  max(a.gender),  max(a.age),  max(a.mobile_size),  max(a.area),  max(a.brand),  max(a.province),  max(a.city),  max(a.pid),  max(a.version),  max(a.city1),  max(a.apn),  max(a.userid),  max(a.city2),  max(a.industry),  max(a.app_name),  max(a.job_cate2),  max(a.job_cate3),  max(a.ltv),  max(a.imei2),  max(a.last2_date),  null,  null,  null FROM  (   SELECT    stat_date,    imei,    idfa,    new_flag,    first_date,    last_date,    reg_cid,    busstype_like,    cate1_like,    cate2_like,    gender,    age,    mobile_size,    area,    brand,    province,    city,    pid,    version,    city1,    apn,    userid,    city2,    industry,    app_name,    job_cate2,    job_cate3,    ltv,    imei2,    last2_date   FROM    data_usergroup.t_person_imei_total t1   where    t1.province in ('重庆市', '天津市')    and 1 = 1  ) a group by  a.imei; | insert into test.t_person_imei_group_1738 select  stat_date,    imei,    idfa,    new_flag,    first_date,    last_date,    reg_cid,    busstype_like,    cate1_like,    cate2_like,    gender,    age,    mobile_size,    area,    brand,    province,    city,    pid,    version,    city1,    apn,    userid,    city2,    industry,    app_name,    job_cate2,    job_cate3,    ltv,    imei2,    last2_date,    NULL,    null,    NULL FROM data_usergroup.t_person_imei_total_v1 a   GLOBAL INNER join  ( select  arrayJoin(bitmapToArray(groupBitmapMergeState(imeiState))) as mapping_id from data_usergroup.user_lable_string_bitmap where lable_name='province' and lable_value in ('重庆市', '天津市') ) b on a.mapping_id=b.mapping_id; | 3438         | 740            | 56544          | 49391            |
| 1793       | select  count(distinct a.imei) FROM  (   SELECT    imei   from    data_usergroup.t_person_imei_total   where    1 = 1  ) a  join (   select    imei   from    data_usergroup.t_person_imei_event_neirong_v2   where    1 = 1    and nr_type1 = '同城部落'    and ((event_name = '访问大部落'))   group by    imei   having    min(     toDate('2020-11-30') - toDate(      if(       visitParamExtractString(event_param, 'laster_date') == '',       '0000-00-00',       visitParamExtractString(event_param, 'laster_date')      )     )    ) between 7    and 13  ) nr_0 on a.imei = nr_0.imei | 该场景仅以total表做全量匹配如果使用bitmap需要先把所有total imei转换为bitmap再将bitmap改为imei去join 没有意思 |                                                              | 该场景仅以total表做全量匹配如果使用bitmap需要先把所有total imei转换为bitmap再将bitmap改为imei去join 没有意思 | 90000        |                |                |                  |
|            |                                                              |                                                              |                                                              |                                                              |              |                |                |                  |