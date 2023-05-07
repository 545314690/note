### 创建表

```sql
CREATE TABLE IF NOT EXISTS accident ( atime bigint comment '时间戳',atype string comment '事故类型',atype2 string comment '事故子类型',atype2Alias string comment '事故子类型Alias',city string comment '城市',cityinfo string comment '地域信息',companyFullName string comment '企业全名',companyName string comment '企业名',content string comment '事故内容',county string comment '县',deathnumber int comment '死亡人数',economicType string comment '经济类型',source string comment '来源',id string comment 'id',lat string comment '纬度',lng string comment '经度',originaltime string comment '发生时间',province string comment '省份',sgbh string comment '事故编号',sgjb string comment '事故级别',hangye string comment '行业',lingyu string comment '领域')
COMMENT '事故信息'
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
LINES TERMINATED BY '\n'
STORED AS TEXTFILE;
```

简化

```sql
CREATE TABLE IF NOT EXISTS accident ( atime bigint comment '时间戳',atype string comment '事故类型',atype2 string comment '事故子类型',atype2Alias string comment '事故子类型Alias',city string comment '城市',county string comment '县',deathnumber int comment '死亡人数',economicType string comment '经济类型',source string comment '来源',id string comment 'id',lat string comment '纬度',lng string comment '经度',originaltime string comment '发生时间',province string comment '省份',sgbh string comment '事故编号',sgjb string comment '事故级别',hangye string comment '行业',lingyu string comment '领域')
COMMENT '事故信息'
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
LINES TERMINATED BY '\n'
STORED AS TEXTFILE;
```



### 加载数据

```sql
load data local inpath '/acc.csv' overwrite into table accident;
```

### 查询

```sql
select count(*) as count ,province from accident group by province order by count desc limit 100;
select count(*) as count ,atype from accident group by atype order by count desc limit 100;
```



### 删除表

```sql
drop table accident;
```

```sql
use hdp_lbg_ecdata_dw_defaultdb;
	
CREATE TABLE
IF
	NOT EXISTS hdp_lbg_ecdata_dw_defaultdb.t_person_imei_group_1000 (
	imei string COMMENT 'imei',
	userid string COMMENT 'userid'
	) COMMENT '用户分群1000' PARTITIONED BY(dt String) ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n' STORED AS TEXTFILE;
			
ALTER TABLE hdp_lbg_ecdata_dw_defaultdb.t_person_imei_group_1000 drop  if exists PARTITION ( dt = 20190927 );
ALTER TABLE hdp_lbg_ecdata_dw_defaultdb.t_person_imei_group_1000 ADD
IF
	NOT EXISTS PARTITION ( dt = '20190927' ) location '/home/hdp_lbg_ecdata_dw/resultdata/sketch/test001/t_person_imei_group_1000/20190927';
```

