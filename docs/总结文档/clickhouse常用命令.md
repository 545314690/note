# Clickhouse常用命令

ck小集群

![1567130515633](D:\58工作内容\总结文档\ck小集群.png)

## ck版本19.4.1.1>>>>后升级为19.5.3.1

```sh
[work(lisenmiao)@tjtx-89-75 ~]$ clickhouse-server -V
ClickHouse server version 19.4.1.1.
	
```



## 1.创建分片表

```sql
CREATE
	TABLE
		data_usergroup.t_person_custom_group_imei_shard on cluster cluster2_2shards_2replicas(
		stat_date Date,
		imei String,
		idfa Nullable(String),
		new_flag Nullable(String),
		first_date Nullable(Date),
		last_date Nullable(Date),
		reg_cid Nullable(String),
		busstype_like Nullable(String),
		cate1_like Nullable(String),
		cate2_like Nullable(String),
		gender Nullable(String),
		age Nullable(String),
		mobile_size Nullable(String),
		area Nullable(String),
		brand Nullable(String),
		province Nullable(String),
		city Nullable(String),
		pid Nullable(String),
		version Nullable(String),
		city1 Nullable(String),
		apn Nullable(String),
         userid Nullable(String),
         last2_date Nullable(Date),
		group_id UInt32) ENGINE = ReplicatedReplacingMergeTree('/clickhouse/tables/user_group/t_person_custom_group_imei_shard/{cluster2_shard}',
		'{cluster2_replica}') PARTITION BY group_id
	ORDER BY
		(group_id,imei) SETTINGS index_granularity = 8192;
```

> ORDER BY 里面是用来去重的字段
>
> PARTITION BY 是分区字段
>
> cluster2_2shards_2replicas 是集群名
>
> cluster2_shard  是分片名
>
> cluster2_replica 是副本名

## 2.创建分布式表

```sql
CREATE
	TABLE
		data_usergroup.t_person_custom_group_imei on cluster cluster2_2shards_2replicas (
		stat_date Date,
		imei String,
		idfa Nullable(String),
		new_flag Nullable(String),
		first_date Nullable(Date),
		last_date Nullable(Date),
		reg_cid Nullable(String),
		busstype_like Nullable(String),
		cate1_like Nullable(String),
		cate2_like Nullable(String),
		gender Nullable(String),
		age Nullable(String),
		mobile_size Nullable(String),
		area Nullable(String),
		brand Nullable(String),
		province Nullable(String),
		city Nullable(String),
		pid Nullable(String),
		version Nullable(String),
		city1 Nullable(String),
		apn Nullable(String),
         userid Nullable(String),
         last2_date Nullable(Date),  
		group_id UInt32) ENGINE = Distributed(cluster2_2shards_2replicas,
		data_usergroup,
		t_person_custom_group_imei_shard,
		sipHash64(group_id,imei));
		
```

## 3.手动合并表

> 对于分布式表，一定是对分片表进行操作
>
> 参考：https://clickhouse.yandex/docs/en/query_language/misc/#misc_operations-optimize

```sql
optimize table data_usergroup.t_person_custom_group_imei_shard on cluster cluster2_2shards_2replicas;
optimize table data_usergroup.t_person_imei_total2_shard on cluster cluster2_2shards_2replicas;

optimize table data_usergroup.t_sketch_label_shard on cluster cluster2_2shards_2replicas;
```



### 可以查看系统表中的合并进程，来确定该表是否合并完成

> ~~需要去每个机器上确认没有合并进程，才代表合并完毕~~
>
> **经测试要多次执行手动合并才能将重复数据合并完毕**，感觉挺坑！

```sql
tjtx-89-75.58os.org :) select * from `system`.merges where database='data_usergroup' and `table` = 't_person_imei_total2_shard';

SELECT *
FROM system.merges 
WHERE (database = 'data_usergroup') AND (table = 't_person_imei_total2_shard')

┌─database───────┬─table──────────────────────┬───────elapsed─┬──────────progress─┬─num_parts─┬─source_part_names──────────────────────────────────────┬─result_part_name───────┬─partition_id─┬─is_mutation─┬─total_size_bytes_compressed─┬─total_size_marks─┬─bytes_read_uncompressed─┬─rows_read─┬─bytes_written_uncompressed─┬─rows_written─┬─columns_written─┬─memory_usage─┬─thread_number─┐
│ data_usergroup │ t_person_imei_total2_shard │ 171.745356287 │ 0.827107344655075 │         2 │ ['19800101_238_107524_32','19800101_107525_107741_21'] │ 19800101_238_107741_33 │ 19800101     │           0 │                  7398052826 │            20957 │             39397962680 │ 171668274 │                38458981847 │    171561579 │              17 │      7063578 │             6 │
└────────────────┴────────────────────────────┴───────────────┴───────────────────┴───────────┴────────────────────────────────────────────────────────┴────────────────────────┴──────────────┴─────────────┴─────────────────────────────┴──────────────────┴─────────────────────────┴───────────┴────────────────────────────┴──────────────┴─────────────────┴──────────────┴───────────────┘

1 rows in set. Elapsed: 0.003 sec. 
```





## 4.清空表

```sql
truncate table data_usergroup.t_person_custom_group_imei on cluster cluster2_2shards_2replicas;	
truncate table data_usergroup.t_person_custom_group_imei_shard on cluster cluster2_2shards_2replicas;


```

## 5.删除表

```sql
drop table data_usergroup.t_person_custom_group_imei on cluster cluster2_2shards_2replicas;	
drop table data_usergroup.t_person_custom_group_imei_shard on cluster cluster2_2shards_2replicas;
```

## 6.杀死ck执行中查询进程

```sql
SELECT query_id, query from system.processes;
#查找正在运行的查询从该列表中，找到query_id查询我要杀死然后执行

KILL QUERY where query_id = '7f5d9e56-d643-438b-8a12-679d5866e71c';
```

> 更多信息可以在[Documentation on KILL QUERY](https://clickhouse.yandex/docs/en/query_language/misc/#kill-query)找到

## 7.A,B,C三个表，求交集、并集

```sql
select 		
	count(distinct d.imei) as abc, -- 并集a∪b∪c
	count(distinct if(length(a.imei)>0 and length(b.imei)>0 and length(c.imei)>0,d.imei,null)) as a_b_c, -- a∩b∩c 
	count(distinct if(length(a.imei)>0 and length(b.imei)>0, d.imei,null)) as a_b, -- a∩b 
	count(distinct if(length(a.imei)>0 and length(c.imei)>0, d.imei,null)) as a_c, -- a∩c
	count(distinct if(length(a.imei)>0 and length(b.imei)=0 and length(c.imei)=0,d.imei,null))  as a_1,  -- a独占
	count(distinct if(length(a.imei)=0 and length(b.imei)>0 and length(c.imei)=0,d.imei,null)) as b_1,  -- b独占
	count(distinct if(length(a.imei)=0 and length(b.imei)=0 and length(c.imei)>0,d.imei,null)) as c_1 -- c独占
from 
	(
	select imei from  t_person_imei_group_69
	union all
	select imei from  t_person_imei_group_72
	union all
	select imei from  t_person_imei_group_73
	) d 
	left join t_person_imei_group_69 a on d.imei = a.imei 
	left join t_person_imei_group_72 b on d.imei = b.imei 
	left join t_person_imei_group_73 c on d.imei = c.imei;

```

## 8.增加字段，

> ### 注意：
>
> 分布式表需要先更改分片表，再更改分布式表（视图），改分布式表须在每台节点都执行
>
> ### 分布式表操作建议：
>
> 使用on cluster关键字在分片表增加字段
>
> 使用on cluster关键字删除分布式表（视图）
>
> 使用on cluster关键字重建分布式表（视图）

```sql
ALTER TABLE data_usergroup.t_person_imei_total_shard ADD COLUMN userid Nullable(String) AFTER apn;
ALTER TABLE data_usergroup.t_person_imei_total ADD COLUMN userid Nullable(String) AFTER apn;


ALTER TABLE data_usergroup.t_person_custom_group_imei_shard ADD COLUMN userid Nullable(String) AFTER apn;
ALTER TABLE data_usergroup.t_person_custom_group_imei ADD COLUMN userid Nullable(String) AFTER apn;
```

### 分片表直接在集群执行

```sql
ALTER TABLE data_usergroup.t_person_imei_total_shard on cluster cluster2_2shards_2replicas ADD COLUMN IF NOT EXISTS last2_date Nullable(Date) AFTER userid;
```

### 分布式复制表（带副本）需要分别在单机执行

分布式复制表无副本也可以在集群执行

```sql
ALTER TABLE data_usergroup.t_person_imei_total  ADD COLUMN IF NOT EXISTS last2_date Nullable(Date) AFTER userid;
```



## 9.删除字段

> ### 注意：
>
> 分布式表需要先更改分片表，再更改分布式表（视图），改分布式表须在每台节点都执行
>
> ### 分布式表操作建议：
>
> 使用on cluster关键字在分片表增加字段
>
> 使用on cluster关键字删除分布式表（视图）
>
> 使用on cluster关键字重建分布式表（视图）

```sql
ALTER TABLE data_usergroup.t_person_imei_group_0_shard DROP COLUMN IF EXISTS user_id1
ALTER TABLE data_usergroup.t_person_imei_group_0 DROP COLUMN IF EXISTS user_id1
```

### 或者直接在集群执行

## 10.聚合，空值、脏数据聚合

```sql
select count(1) as value,if(city1 is null  or city1 = '-' or city1 = '\\N' ,'未知',city1)  as name from data_usergroup.t_person_imei_total group by name order by value desc;
```



## 11.重命名表

```sql
rename table data_usergroup.t_person_imei_total2_shard to data_usergroup.t_person_imei_total3_shard on cluster cluster2_2shards_2replicas;
rename table data_usergroup.t_person_imei_total2 to data_usergroup.t_person_imei_total3 on cluster cluster2_2shards_2replicas;
```

```shell
tjtx-89-75.58os.org :) rename table data_usergroup.t_person_imei_total2_shard to data_usergroup.t_person_imei_total3_shard on cluster cluster2_2shards_2replicas;

RENAME TABLE data_usergroup.t_person_imei_total2_shard TO data_usergroup.t_person_imei_total3_shard ON CLUSTER cluster2_2shards_2replicas

┌─host─────────┬─port─┬─status─┬─error─┬─num_hosts_remaining─┬─num_hosts_active─┐
│ 10.126.84.90 │ 9000 │      0 │       │                   3 │                3 │
└──────────────┴──────┴────────┴───────┴─────────────────────┴──────────────────┘
┌─host─────────┬─port─┬─status─┬─error─┬─num_hosts_remaining─┬─num_hosts_active─┐
│ 10.126.89.77 │ 9000 │      0 │       │                   2 │                2 │
└──────────────┴──────┴────────┴───────┴─────────────────────┴──────────────────┘
┌─host─────────┬─port─┬─status─┬─error─┬─num_hosts_remaining─┬─num_hosts_active─┐
│ 10.126.89.76 │ 9000 │      0 │       │                   1 │                1 │
└──────────────┴──────┴────────┴───────┴─────────────────────┴──────────────────┘
┌─host─────────┬─port─┬─status─┬─error─┬─num_hosts_remaining─┬─num_hosts_active─┐
│ 10.126.89.75 │ 9000 │      0 │       │                   0 │                0 │
└──────────────┴──────┴────────┴───────┴─────────────────────┴──────────────────┘

4 rows in set. Elapsed: 1.079 sec. 

tjtx-89-75.58os.org :) rename table data_usergroup.t_person_imei_total2 to data_usergroup.t_person_imei_total3 on cluster cluster2_2shards_2replicas;

RENAME TABLE data_usergroup.t_person_imei_total2 TO data_usergroup.t_person_imei_total3 ON CLUSTER cluster2_2shards_2replicas

┌─host─────────┬─port─┬─status─┬─error─┬─num_hosts_remaining─┬─num_hosts_active─┐
│ 10.126.84.90 │ 9000 │      0 │       │                   3 │                0 │
│ 10.126.89.77 │ 9000 │      0 │       │                   2 │                0 │
│ 10.126.89.75 │ 9000 │      0 │       │                   1 │                0 │
│ 10.126.89.76 │ 9000 │      0 │       │                   0 │                0 │
└──────────────┴──────┴────────┴───────┴─────────────────────┴──────────────────┘

4 rows in set. Elapsed: 0.109 sec. 

tjtx-89-75.58os.org :) show create table data_usergroup.t_person_imei_total3;

SHOW CREATE TABLE data_usergroup.t_person_imei_total3

┌─statement──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│ CREATE TABLE data_usergroup.t_person_imei_total3 ( stat_date Date,  imei String,  idfa Nullable(String),  new_flag Nullable(String),  first_date Nullable(Date),  last_date Nullable(Date),  reg_cid Nullable(String),  busstype_like Nullable(String),  cate1_like Nullable(String),  cate2_like Nullable(String),  gender Nullable(String),  age Nullable(String),  mobile_size Nullable(String),  area Nullable(String),  brand Nullable(String),  province Nullable(String),  city Nullable(String),  pid Nullable(String),  version Nullable(String),  city1 Nullable(String),  apn Nullable(String),  userid Nullable(String)) ENGINE = Distributed(cluster2_2shards_2replicas, data_usergroup, t_person_imei_total2_shard, sipHash64(imei)) │
└────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘

1 rows in set. Elapsed: 0.002 sec. 

tjtx-89-75.58os.org :) show create table data_usergroup.t_person_imei_total3_shard;

SHOW CREATE TABLE data_usergroup.t_person_imei_total3_shard

┌─statement──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│ CREATE TABLE data_usergroup.t_person_imei_total3_shard ( stat_date Date,  imei String,  idfa Nullable(String),  new_flag Nullable(String),  first_date Nullable(Date),  last_date Nullable(Date),  reg_cid Nullable(String),  busstype_like Nullable(String),  cate1_like Nullable(String),  cate2_like Nullable(String),  gender Nullable(String),  age Nullable(String),  mobile_size Nullable(String),  area Nullable(String),  brand Nullable(String),  province Nullable(String),  city Nullable(String),  pid Nullable(String),  version Nullable(String),  city1 Nullable(String),  apn Nullable(String),  userid Nullable(String)) ENGINE = ReplicatedReplacingMergeTree('/clickhouse/tables/user_group/t_person_imei_total2_shard/{cluster2_shard}', '{cluster2_replica}') PARTITION BY stat_date ORDER BY imei SETTINGS index_granularity = 8192 │
└────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘

1 rows in set. Elapsed: 0.002 sec. 

```



> 注意：重命名完了之后，zk的路径不变，不知道有啥影响吗



## 12.插入结果集到表（加userid字段到t_person_imei_total2表）

> 加userid字段到t_person_imei_total2表
>
> tb_app_userid是userid和imei的映射字典表

```sql
INSERT INTO data_usergroup.t_person_imei_total2 SELECT
a.stat_date,
a.imei,
a.idfa,
a.new_flag,
a.first_date,
a.last_date,
a.reg_cid,
a.busstype_like,
a.cate1_like,
a.cate2_like,
a.gender,
a.age,
a.mobile_size,
a.area,
a.brand,
a.province,
a.city,
a.pid,
a.version,
a.city1,
a.apn,
b.userid 
FROM
	data_usergroup.t_person_imei_total a
	LEFT JOIN data_usergroup.tb_app_userid b ON a.imei = b.imei;
```

## 13.Mutations更新和删除

> 参考 https://clickhouse.yandex/docs/en/query_language/alter/#alter-mutations
>
> 注意：Mutations（突变）
>
> 1. 该命令从18.12.14版本开始可用。过滤器expr必须是UInt8类型。此查询将指定列的值更新为筛选器expr接受非零值的行的相应表达式的值。使用CAST操作符将值转换为列类型。不支持更新主键或分区键计算中使用的列。
>
> 2. 更新和删除命令是异步执行的，可以通过查看表 system.mutations 来查看命令的是否执行完毕
> select * from system.mutations where table='test_update';
>
> 3. ```sql
>   ALTER TABLE [db.]table DELETE WHERE filter_expr
>   ```
> ```
> 
> ```
>
> ```
> 
> ```
>
> ```
> 
> ```
>
> ```
> 
> ​```sql
> ALTER TABLE [db.]table UPDATE column1 = expr1 [, ...] WHERE filter_expr
> ```
>
> filter_expr必须存在
>
> 4. 不允许更新主键和分区字段值
>
> 5. 一个查询可以包含由逗号分隔的多个命令。
>
> 6. 对于*MergeTree表，通过重写整个数据部分来执行突变。不存在原子性——一旦发生了突变，部分就会被替换掉，在突变期间开始执行的SELECT查询将会看到来自已经发生突变的部分的数据，以及来自尚未发生突变的部分的数据。
>
> 7. 突变完全按照它们的产生顺序排列，并按顺序应用于每个部分。对于insert，突变也是部分排序的——**在提交突变之前插入到表中的数据将发生突变，之后插入的数据不会发生突变**。注意，突变不会以任何方式阻止插入。
>
> 8. 在添加了突变项之后，一个突变查询立即返回(对于向ZooKeeper添加了复制表的情况，对于向文件系统添加了非复制表的情况)。突变本身使用系统配置文件设置异步执行。要跟踪突变的进程，可以使用这个[`system.mutations`](https://clickhouse.yandex/docs/en/operations/system_tables/#system_tables-mutations)。即使重新启动ClickHouse服务器，成功提交的突变也将继续执行。提交后无法回滚该突变，但如果由于某种原因该突变被阻止，则可以使用[`KILL MUTATION`](https://clickhouse.yandex/docs/en/query_language/misc/#kill-mutation)查询取消该突变。
>
> 9. 完成的突变项不会立即删除(保留的条目数量由finished_mutations_to_keep参数决定)。旧的突变项将被删除。
> ```
> 
> ```
>
> ```
> 
> ```
>
> ```
> 
> ```

### 1.根据where条件更新示例

```sql
-- 创建测试库
create database test;
-- 创建测试表
CREATE TABLE test.aa(
`stat_date` Date,
`a` Nullable(String),
`b` Nullable(String)
) ENGINE = MergeTree() PARTITION BY stat_date
ORDER BY
stat_date SETTINGS index_granularity = 8192;
-- 插入一条数据	
insert into test.aa values('2019-01-01','a','b');
-- 查询数据
select * from test.aa;
-- 增加一列
ALTER TABLE test.aa ADD COLUMN age Int32 AFTER b;
-- 更新字段值
ALTER TABLE test.aa UPDATE b='c' where a='a';
--查询数据
select * from test.aa;
```

### 2.根据where条件删除数据

> 如果是分布式表，需要的是删除每个分片表上的数据

```sql
ALTER TABLE data_usergroup.t_person_custom_only_imei_shard  ON cluster cluster2_2shards_2replicas DELETE WHERE group_id=15
ALTER TABLE data_usergroup.t_person_custom_group_imei_shard  ON cluster cluster2_2shards_2replicas DELETE WHERE group_id=15
```

### 3.更新删除测试

> TODO: join更新问题未解决

```sql
CREATE TABLE userid_test(
  stat_date Date,
  imei String,
  name String,
  last_date Date,
  last2_date Date
) ENGINE = MergeTree() PARTITION BY stat_date ORDER BY imei SETTINGS index_granularity = 8192;

insert into userid_test values('2019-9-20','aaa','aaa','2019-9-20','2019-9-10');
insert into userid_test values('2019-9-23','aaa','aaa','2019-9-22','2019-9-22');
insert into userid_test values('2019-9-23','123','123','2019-9-22','2019-9-22');

CREATE TABLE userid_test2(
  stat_date Date,
  imei String,
  name String,
  last_date Date,
  last2_date Date
) ENGINE = MergeTree() PARTITION BY stat_date ORDER BY imei SETTINGS index_granularity = 8192;


insert into userid_test2 values('2019-9-24','aaa','aaa','2019-9-24','2019-9-24');
insert into userid_test2 values('2019-9-23','123','123','2019-9-22','2019-9-22');
-- 不能更新分区和主键字段
ALTER TABLE userid_test2 update imei='456' where imei='123';  -- not ok  不能更新主键字段
ALTER TABLE userid_test2 update stat_date=toDate('0000-00-00') where imei='123';  -- not ok  不能更新分区字段

ALTER TABLE userid_test2 delete where imei='123';  -- ok 
ALTER TABLE userid_test2 update last2_date=toDate('0000-00-00') where imei='123'; -- ok
-- 对比下面两个语句：
ALTER TABLE userid_test delete where imei in (select imei from userid_test a join userid_test2 b on a.imei=b.imei); -- not ok 使用on不行，不知道为何
ALTER TABLE userid_test delete where imei in (select imei from userid_test join userid_test2 using imei); -- ok 使用using是可以的

ALTER TABLE userid_test delete where imei in (select imei from  userid_test2); -- ok

ALTER TABLE userid_test2 UPDATE last2_date=toDate('0000-00-00') where imei='123';-- ok 需要toDate()转换为Date格式
ALTER TABLE userid_test2 UPDATE name='ccc' where imei='aaa'; -- ok
ALTER TABLE userid_test2 UPDATE name='333' where imei in (select imei from userid_test join userid_test2 using imei); --ok                                                                                                                                      
```

### 4.删除、更新分布式表数据

> 需要在集群操作分片表
>
> data_usergroup.t_person_imei_total_yesterday_tmp2_shard为分片表

```sql
ALTER TABLE data_usergroup.t_person_imei_total_yesterday_tmp2_shard  on cluster cluster2_2shards_2replicas delete where imei='111';
```

### 5.查看Mutations是否运行完成

> 根据记录的is_done字段来监测是否运行完成（1代表完成）

```sql
SELECT *
FROM system.mutations 
WHERE (database = 'data_usergroup') AND (table = 't_person_imei_total2_shard')

┌─database───────┬─table──────────────────────┬─mutation_id─┬─command───────────────────────────────────────────────────────────────────────────────────┬─────────create_time─┬─block_numbers.partition_id─┬─block_numbers.number─┬─parts_to_do─┬─is_done─┬─latest_failed_part─┬────latest_fail_time─┬─latest_fail_reason─┐
│ data_usergroup │ t_person_imei_total2_shard │ 0000000000  │ DELETE WHERE imei IN (SELECT imei FROM data_usergroup.t_person_imei_total_yesterday_tmp ) │ 2019-10-16 16:44:31 │ ['19800101']               │ [990248]             │           0 │       1 │                    │ 0000-00-00 00:00:00 │                    │
│ data_usergroup │ t_person_imei_total2_shard │ 0000000001  │ DELETE WHERE imei IN (SELECT imei FROM data_usergroup.t_person_imei_total_yesterday_tmp ) │ 2019-10-16 16:58:02 │ ['19800101']               │ [990249]             │           0 │       1 │                    │ 0000-00-00 00:00:00 │                    │
│ data_usergroup │ t_person_imei_total2_shard │ 0000000002  │ DELETE WHERE imei IN (SELECT imei FROM data_usergroup.t_person_imei_total_yesterday_tmp ) │ 2019-10-16 17:41:57 │ ['19800101']               │ [990275]             │           0 │       1 │                    │ 0000-00-00 00:00:00 │                    │
│ data_usergroup │ t_person_imei_total2_shard │ 0000000003  │ DELETE WHERE imei IN (SELECT imei FROM data_usergroup.t_person_imei_total_yesterday_tmp ) │ 2019-10-16 20:29:25 │ ['19800101']               │ [990300]             │           0 │       1 │                    │ 0000-00-00 00:00:00 │                    │
│ data_usergroup │ t_person_imei_total2_shard │ 0000000004  │ DELETE WHERE imei IN (SELECT imei FROM data_usergroup.t_person_imei_total_yesterday_tmp ) │ 2019-10-16 20:44:36 │ ['19800101']               │ [990326]             │           0 │       1 │                    │ 0000-00-00 00:00:00 │                    │
│ data_usergroup │ t_person_imei_total2_shard │ 0000000005  │ DELETE WHERE imei IN (SELECT imei FROM data_usergroup.t_person_imei_total_yesterday_tmp ) │ 2019-10-17 07:24:49 │ ['19800101']               │ [990352]             │           0 │       1 │                    │ 0000-00-00 00:00:00 │                    │
│ data_usergroup │ t_person_imei_total2_shard │ 0000000006  │ DELETE WHERE imei IN (SELECT imei FROM data_usergroup.t_person_imei_total_yesterday_tmp ) │ 2019-10-18 07:26:33 │ ['19800101']               │ [990377]             │           0 │       1 │                    │ 0000-00-00 00:00:00 │                    │
│ data_usergroup │ t_person_imei_total2_shard │ 0000000007  │ DELETE WHERE imei IN (SELECT imei FROM data_usergroup.t_person_imei_total_yesterday_tmp ) │ 2019-10-18 16:27:12 │ ['19800101']               │ [990403]             │           0 │       1 │                    │ 0000-00-00 00:00:00 │                    │
└────────────────┴────────────────────────────┴─────────────┴───────────────────────────────────────────────────────────────────────────────────────────┴─────────────────────┴────────────────────────────┴──────────────────────┴─────────────┴─────────┴────────────────────┴─────────────────────┴────────────────────┘

8 rows in set. Elapsed: 0.009 sec. 

```



## 14.导出数据为CSV

> format_csv_delimiter 列分隔符
>
> query 查询语句
>
> " > data.csv"  重定向到data.csv
>
> 更多格式（json、xml等）参考：https://clickhouse.yandex/docs/zh/interfaces/formats/

```sql
clickhouse-client --format_csv_delimiter="|" --query="select * from data_usergroup.t_person_action_detail where stat_date = '2019-09-08' FORMAT CSV" > data.csv
```

### 导出TSV  \t分隔的

```sql
clickhouse-client -m -u web --password GzTIluaB --query="select imei,userid from data_usergroup.t_person_imei_group_243 FORMAT TSV" > t_person_imei_group_243.txt
```



## 15.导入CSV数据

```sql
clickhouse-client --format_csv_delimiter="|" --query="INSERT INTO test.csv FORMAT CSV" < data.csv
```

## 16.查看集群

> is_local代表运行查询语句的是本地节点

```sql
tjtx-89-77.58os.org :) select * from system.clusters;

SELECT *
FROM system.clusters 

┌─cluster────────────────────┬─shard_num─┬─shard_weight─┬─replica_num─┬─host_name────┬─host_address─┬─port─┬─is_local─┬─user────┬─default_database─┐
│ cluster2_2shards_2replicas │         1 │            1 │           1 │ 10.126.84.90 │ 10.126.84.90 │ 9000 │        0 │ default │                  │
│ cluster2_2shards_2replicas │         1 │            1 │           2 │ 10.126.89.75 │ 10.126.89.75 │ 9000 │        0 │ default │                  │
│ cluster2_2shards_2replicas │         2 │            1 │           1 │ 10.126.89.76 │ 10.126.89.76 │ 9000 │        1 │ default │                  │
│ cluster2_2shards_2replicas │         2 │            1 │           2 │ 10.126.89.77 │ 10.126.89.77 │ 9000 │        1 │ default │                  │
│ cluster2_4shards_0replicas │         1 │            1 │           1 │ 10.126.84.90 │ 10.126.84.90 │ 9000 │        0 │ default │                  │
│ cluster2_4shards_0replicas │         2 │            1 │           1 │ 10.126.89.75 │ 10.126.89.75 │ 9000 │        0 │ default │                  │
│ cluster2_4shards_0replicas │         3 │            1 │           1 │ 10.126.89.76 │ 10.126.89.76 │ 9000 │        0 │ default │                  │
│ cluster2_4shards_0replicas │         4 │            1 │           1 │ 10.126.89.77 │ 10.126.89.77 │ 9000 │        1 │ default │                  │
└────────────────────────────┴───────────┴──────────────┴─────────────┴──────────────┴──────────────┴──────┴──────────┴─────────┴──────────────────┘

8 rows in set. Elapsed: 0.002 sec.
```

## 17.json格式字段查询示例

> 更多json提取函数参考 https://clickhouse.yandex/docs/zh/query_language/functions/json_functions/

```sql
-- 建表
CREATE TABLE json_test(
  stat_date Date,
  imei String,
  parmas Nullable(String)
) ENGINE = MergeTree() PARTITION BY stat_date ORDER BY imei SETTINGS index_granularity = 8192;
-- 插入测试数据
insert into json_test values('2019-9-24','aaa','{"name":"测试2","age":1,"dt":"2019-09-20"}');
insert into json_test values('2019-9-24','aaa','{"name":"测试","age":1,"dt":"2019-08-20"}');
insert into json_test values('2019-9-24','aaa','{"name":"中文测试","age":1,"dt":"2019-02-01"}');
insert into json_test values('2019-9-24','aaa','{"name":"hello1","age":2,"dt":"2019-02-01"}');
insert into json_test values('2019-9-24','bbb','{"name":"hello2","age":3,"dt":"2019-02-02"}');
insert into json_test values('2019-9-24','ccc','{"name":"hello3","age":4,"dt":"2018-01-02"}');
insert into json_test values('2019-9-24','ddd','{"name":"hello4","age":4,"dt":""}');
insert into json_test values('2019-9-24','eee','{"name":"hellof","age":5}');
--查询
select * from  json_test;

-- 查询json中String类型字段name
select * from  json_test where visitParamExtractString(parmas,'name') = 'hello';

select * from  json_test where visitParamExtractString(parmas,'name') = '中文测试';
-- 提取UInt类型字段age
select * from  json_test where visitParamExtractUInt(parmas,'age') = 5;

-- 查询json中dt字段并转换为Date类型
select toDate(if(visitParamExtractString(parmas,'dt') =='','0000-00-00',visitParamExtractString(parmas,'dt'))) as dt from json_test;
-- stat_date - dt 相差的天数
select  stat_date - toDate(if(visitParamExtractString(parmas,'dt') =='','0000-00-00',visitParamExtractString(parmas,'dt'))) as dt from json_test;
```

## 18.join

> t_person_imei_total （3.7亿）
>
> t_person_imei_total_yesterday_tmp （21871662）
>
> 注意join的时候一定小表写在右侧

LEFT JOIN 

大表在右边（3.7亿），直接内存溢出，查询不出结果

```sql
tjtx-84-90.58os.org :) select count(1) from (SELECT b.*,a.last_date as last2_date
:-] FROM 
:-] data_usergroup.t_person_imei_total_yesterday_tmp a 
:-] left JOIN data_usergroup.t_person_imei_total  AS b USING (imei));

SELECT count(1)
FROM 
(
    SELECT 
        b.*, 
        a.last_date AS last2_date
    FROM data_usergroup.t_person_imei_total_yesterday_tmp AS a 
    LEFT JOIN data_usergroup.t_person_imei_total AS b USING (imei)
) 

↘ Progress: 469.66 million rows, 133.85 GB (10.02 million rows/s., 2.86 GB/s.) ███████████████████████████████████████████████████████████▏                                                                                                                                31Received exception from server (version 19.5.3):
Code: 241. DB::Exception: Received from localhost:9000, 127.0.0.1. DB::Exception: Memory limit (for query) exceeded: would use 95.89 GiB (attempt to allocate chunk of 12884901888 bytes), maximum: 93.13 GiB. 

0 rows in set. Elapsed: 46.961 sec. Processed 469.66 million rows, 133.85 GB (10.00 million rows/s., 2.85 GB/s.) 

```

改为左表嵌套子查询RIGHT JOIN 查询结果正确

```sql
tjtx-84-90.58os.org :) select count(1) from (SELECT b.*,a.last_date as last2_date FROM  (     SELECT last_date,imei     FROM data_usergroup.t_person_imei_total ) AS a  RIGHT JOIN data_usergroup.t_person_imei_total_yesterday_tmp AS b USING (imei));

SELECT count(1)
FROM 
(
    SELECT 
        b.*, 
        a.last_date AS last2_date
    FROM 
    (
        SELECT 
            last_date, 
            imei
        FROM data_usergroup.t_person_imei_total 
    ) AS a 
    RIGHT JOIN data_usergroup.t_person_imei_total_yesterday_tmp AS b USING (imei)
) 

┌─count(1)─┐
│ 21871662 │
└──────────┘

1 rows in set. Elapsed: 56.512 sec. Processed 393.03 million rows, 17.75 GB (6.95 million rows/s., 314.05 MB/s.) 

```

改为左、右表都是嵌套子查询RIGHT JOIN 查询结果正确

```sql

tjtx-84-90.58os.org :) select count(1) from (SELECT b.*,a.last_date as last2_date FROM  (     SELECT last_date,imei     FROM data_usergroup.t_person_imei_total ) AS a  RIGHT JOIN (select * from  data_usergroup.t_person_imei_total_yesterday_tmp) AS b USING (imei));

SELECT count(1)
FROM 
(
    SELECT 
        b.*, 
        a.last_date AS last2_date
    FROM 
    (
        SELECT 
            last_date, 
            imei
        FROM data_usergroup.t_person_imei_total 
    ) AS a 
    RIGHT JOIN 
    (
        SELECT *
        FROM data_usergroup.t_person_imei_total_yesterday_tmp 
    ) AS b USING (imei)
) 

┌─count(1)─┐
│ 21871662 │
└──────────┘

1 rows in set. Elapsed: 54.038 sec. Processed 393.03 million rows, 17.75 GB (7.27 million rows/s., 328.43 MB/s.)
```

改为直接RIGHT JOIN 查询，结果错误，且耗时长

```sql
tjtx-84-90.58os.org :) select count(1) from (SELECT b.*,a.last_date as last2_date
:-] FROM 
:-] data_usergroup.t_person_imei_total a 
:-] RIGHT JOIN data_usergroup.t_person_imei_total_yesterday_tmp  AS b USING (imei));

SELECT count(1)
FROM 
(
    SELECT 
        b.*, 
        a.last_date AS last2_date
    FROM data_usergroup.t_person_imei_total AS a 
    RIGHT JOIN data_usergroup.t_person_imei_total_yesterday_tmp AS b USING (imei)
) 

┌─count(1)─┐
│ 43743324 │
└──────────┘

1 rows in set. Elapsed: 89.960 sec. Processed 436.77 million rows, 30.77 GB (4.86 million rows/s., 342.03 MB/s.) 
```



## 19.系统参数设置

### replication_alter_partitions_sync

> 对于非复制的表，所有ALTER查询都是同步执行的。对于可复制表，查询只是将适当操作的指令添加到ZooKeeper，而操作本身将尽快执行。但是，查询可以等待这些操作在所有副本上完成。
>
> 对于ALTER ... ATTACH|DETACH|DROP操作，可以设置replication_alter_partitions_sync是否等待。候选0,1,2
>
> 0 	不等待
>
> 1	等待自己完成（默认）
>
> 2	等待所有分片完成
>
> 参考：https://clickhouse.yandex/docs/en/query_language/alter/

/etc/clickhouse-server/users.xml文件中

配置`<replication_alter_partitions_sync>2</replication_alter_partitions_sync>`

```xml
<?xml version="1.0"?>
<yandex>
    <!-- Profiles of settings. -->
    <profiles>
        <!-- Default settings. -->
        <default>
            <replication_alter_partitions_sync>2</replication_alter_partitions_sync>
            <!-- Maximum memory usage for processing single query, in bytes. -->
            <max_memory_usage_for_all_queries>100000000000</max_memory_usage_for_all_queries>
            <max_memory_usage>100000000000</max_memory_usage>
            <max_bytes_before_external_group_by>50000000000</max_bytes_before_external_group_by>
            <!-- Use cache of uncompressed blocks of data. Meaningful only for processing many of very short queries. -->
            <use_uncompressed_cache>0</use_uncompressed_cache>

            <!-- How to choose between replicas during distributed query processing.
                 random - choose random replica from set of replicas with minimum number of errors
                 nearest_hostname - from set of replicas with minimum number of errors, choose replica
                  with minumum number of different symbols between replica's hostname and local hostname
                  (Hamming distance).
                 in_order - first live replica is choosen in specified order.
            -->
            <load_balancing>random</load_balancing>
                <log_queries>1</log_queries>
        </default>

        <!-- Profile that allows only read queries. -->
        <readonly>
            <readonly>1</readonly>
        </readonly>
    </profiles>
    ....省略

```



查询是否生效

```sql
tjtx-89-77.58os.org :) select * from system.settings where name ='replication_alter_partitions_sync';

SELECT *
FROM system.settings 
WHERE name = 'replication_alter_partitions_sync'

┌─name──────────────────────────────┬─value─┬─changed─┬─description───────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│ replication_alter_partitions_sync │ 1     │       0 │ Wait for actions to manipulate the partitions. 0 - do not wait, 1 - wait for execution only of itself, 2 - wait for everyone. │
└───────────────────────────────────┴───────┴─────────┴───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘

```

## 20.删除分区

> 使用on cluster删分区是不靠谱的，有可能某个节点会删不掉分区，更靠谱的是在每个分片表分别删除分区

```sql
ALTER TABLE data_usergroup.t_person_imei_event_neirong_v2_shard ON cluster cluster2_2shards_2replicas DROP PARTITION ('1980-01-01');
```

## 21.clickhouse-client 使用

```sql
clickhouse-client -m -u web --password GzTIluaB
```

## 22.查看表压缩后占内存大小（多少M）

```sql
SELECT 
sum(data_compressed_bytes/1000/1000)
FROM `system`.columns group by table;
```

## 23.groupUniqArray列转行函数

> groupUniqArray 将列转为数组行
>
> arrayStringConcat 将Array转为字符串

```sql
select arrayStringConcat(groupUniqArray(event_name),',') as event_name
from data_usergroup.t_person_imei_event_neirong_v2  where stat_date >='2019-12-12' and stat_date <= '2019-12-12' group by imei
```

## 24.arrayJoin 行转列函数

> arrayJoin 将数组行转为列
>
> 

```sql
SELECT arrayJoin([1, 2, 3] AS src) AS dst, 'Hello', src
┌─dst─┬─\'Hello\'─┬─src─────┐
│   1 │ Hello     │ [1,2,3] │
│   2 │ Hello     │ [1,2,3] │
│   3 │ Hello     │ [1,2,3] │
└─────┴───────────┴─────────┘
```

## 25.splitByChar 用字符切割

> 如果字段被设置为Nullable类型的，需要转换为String，再进行切割
>
> 

```sql
select splitByChar(',','ab,cd,ed');

splitByChar(',', 'ab,cd,ed')|
----------------------------|
['ab','cd','ed']            |
```

转换类型 CAST

```sql
select arrayJoin(splitByChar(',', CAST(if(app_name is null or app_name='\\N','未知',app_name), 'String')) AS src) AS name,count(1)  as value from data_usergroup.t_person_imei_total group by name
```



# 用户画像表结构

> 集群1：cluster2_2shards_2replicas  2分片2副本
>
> 集群2：cluster2_4shards_0replicas  4分片0副本（存储临时数据）

## 1.total表

### 介绍

存储用户基础信息，采用分布式表`ReplicatedReplacingMergeTree`引擎，每天写入昨日访问用户数据，如果新数据的imei在原表中已存在，则覆盖原有记录，目的是更新属性。

### 分片表

```sql
CREATE TABLE if not exists data_usergroup.t_person_imei_total_shard  on cluster cluster2_2shards_2replicas(
 stat_date Date,
  imei String,
  idfa Nullable(String),
  new_flag Nullable(String),
  first_date Nullable(Date),
  last_date Nullable(Date),
  reg_cid Nullable(String),
  busstype_like Nullable(String),
  cate1_like Nullable(String),
  cate2_like Nullable(String),
  gender Nullable(String),
  age Nullable(String),
  mobile_size Nullable(String),
  area Nullable(String),
  brand Nullable(String),
  province Nullable(String),
  city Nullable(String),
  pid Nullable(String),
  version Nullable(String),
  city1 Nullable(String),
  apn Nullable(String),
  userid Nullable(String),
  city2 Nullable(String),
  last2_date Nullable(Date)
) ENGINE = ReplicatedReplacingMergeTree('/clickhouse/tables/user_group/t_person_imei_total_shard/{cluster2_shard}', '{cluster2_replica}') PARTITION BY stat_date ORDER BY imei SETTINGS index_granularity = 8192

```



### 分布式表

```sql
CREATE TABLE if not exists data_usergroup.t_person_imei_total  on cluster cluster2_2shards_2replicas(
  stat_date Date,
  imei String,
  idfa Nullable(String),
  new_flag Nullable(String),
  first_date Nullable(Date),
  last_date Nullable(Date),
  reg_cid Nullable(String),
  busstype_like Nullable(String),
  cate1_like Nullable(String),
  cate2_like Nullable(String),
  gender Nullable(String),
  age Nullable(String),
  mobile_size Nullable(String),
  area Nullable(String),
  brand Nullable(String),
  province Nullable(String),
  city Nullable(String),
  pid Nullable(String),
  version Nullable(String),
  city1 Nullable(String),
  apn Nullable(String),
  userid Nullable(String),
    city2 Nullable(String),
  last2_date Nullable(Date)
) ENGINE = Distributed(
  cluster2_2shards_2replicas,
  data_usergroup,
  t_person_imei_total2_shard,
  sipHash64(imei)
)
```

### total表v2

> 后面为了加了一个userid字段，并将历史数据中该字段更新，新建了t_person_imei_total2_shard表，改变了t_person_imei_total视图的映射
>
> 又追加last2_date字段

```sql
-- 删原视图
drop table data_usergroup.t_person_imei_total on cluster cluster2_2shards_2replicas;
-- 创建新视图，指向t_person_imei_total2_shard分片
CREATE TABLE data_usergroup.t_person_imei_total ON CLUSTER cluster2_2shards_2replicas
(
    stat_date Date, 
    imei String, 
    idfa Nullable(String), 
    new_flag Nullable(String), 
    first_date Nullable(Date), 
    last_date Nullable(Date), 
    reg_cid Nullable(String), 
    busstype_like Nullable(String), 
    cate1_like Nullable(String), 
    cate2_like Nullable(String), 
    gender Nullable(String), 
    age Nullable(String), 
    mobile_size Nullable(String), 
    area Nullable(String), 
    brand Nullable(String), 
    province Nullable(String), 
    city Nullable(String), 
    pid Nullable(String), 
    version Nullable(String), 
    city1 Nullable(String), 
    apn Nullable(String), 
    userid Nullable(String),
    city2 Nullable(String),
    last2_date Nullable(Date)
)
ENGINE = Distributed(cluster2_2shards_2replicas, data_usergroup, t_person_imei_total2_shard, sipHash64(imei))
```

### total表修改

#### 增加了字段industry、app_name、job_cate2、job_cate3

```sql
// 分片表在一台机器执行
ALTER TABLE data_usergroup.t_person_imei_total2_shard on cluster cluster2_2shards_2replicas ADD COLUMN industry Nullable(String) AFTER city2;
ALTER TABLE data_usergroup.t_person_imei_total2_shard on cluster cluster2_2shards_2replicas ADD COLUMN app_name Nullable(String) AFTER industry;
ALTER TABLE data_usergroup.t_person_imei_total2_shard on cluster cluster2_2shards_2replicas ADD COLUMN job_cate2 Nullable(String) AFTER app_name;
ALTER TABLE data_usergroup.t_person_imei_total2_shard on cluster cluster2_2shards_2replicas ADD COLUMN job_cate3 Nullable(String) AFTER job_cate2;

//分布式表在每台机器执行
ALTER TABLE data_usergroup.t_person_imei_total ADD COLUMN industry Nullable(String) AFTER city2;
ALTER TABLE data_usergroup.t_person_imei_total ADD COLUMN app_name Nullable(String) AFTER industry;
ALTER TABLE data_usergroup.t_person_imei_total ADD COLUMN job_cate2 Nullable(String) AFTER app_name;
ALTER TABLE data_usergroup.t_person_imei_total ADD COLUMN job_cate3 Nullable(String) AFTER job_cate2;
```

#### 其他相关表

```sql
drop table data_usergroup.t_person_imei_total_yesterday_tmp on cluster cluster2_2shards_2replicas;	
drop table data_usergroup.t_person_imei_total_yesterday_tmp_shard on cluster cluster2_2shards_2replicas;

ALTER TABLE data_usergroup.t_person_imei_total_yesterday_tmp_final_shard on cluster cluster2_4shards_0replicas ADD COLUMN industry Nullable(String) AFTER city2;
ALTER TABLE data_usergroup.t_person_imei_total_yesterday_tmp_final_shard on cluster cluster2_4shards_0replicas ADD COLUMN app_name Nullable(String) AFTER industry;
ALTER TABLE data_usergroup.t_person_imei_total_yesterday_tmp_final_shard on cluster cluster2_4shards_0replicas ADD COLUMN job_cate2 Nullable(String) AFTER app_name;
ALTER TABLE data_usergroup.t_person_imei_total_yesterday_tmp_final_shard on cluster cluster2_4shards_0replicas ADD COLUMN job_cate3 Nullable(String) AFTER job_cate2;


// 每个节点执行
ALTER TABLE data_usergroup.t_person_imei_total_yesterday_tmp_final ADD COLUMN industry Nullable(String) AFTER city2;
ALTER TABLE data_usergroup.t_person_imei_total_yesterday_tmp_final ADD COLUMN app_name Nullable(String) AFTER industry;
ALTER TABLE data_usergroup.t_person_imei_total_yesterday_tmp_final ADD COLUMN job_cate2 Nullable(String) AFTER app_name;
ALTER TABLE data_usergroup.t_person_imei_total_yesterday_tmp_final ADD COLUMN job_cate3 Nullable(String) AFTER job_cate2;
```



## 2.product表

### 介绍

存储用户每日访问产品信息，采用分布式表`ReplicatedMergeTree`引擎，每天写入增量数据。

### 分片表

```sql
CREATE TABLE if not exists data_usergroup.t_person_imei_base_product_shard  on  cluster cluster2_2shards_2replicas(
  stat_date Date comment '访问日期',
  imei String comment 'imei',
  user_flag Nullable(String) comment '新老用户',
  is_buluo Nullable(String) comment '是否访问部落',
  is_kuang Nullable(String) comment '是否访问神奇矿',
  is_toutiao Nullable(String) comment '是否访问头条',
  diaoqi_qudaos Nullable(String) comment '调起渠道',
  time Nullable(Float32) comment '访问时长'
) ENGINE = ReplicatedMergeTree('/clickhouse/tables/user_group/t_person_imei_base_product_shard/{cluster2_shard}', '{cluster2_replica}') PARTITION BY stat_date ORDER BY imei SETTINGS index_granularity = 8192
```



### 分布式表

```sql
CREATE TABLE if not exists data_usergroup.t_person_imei_base_product  on cluster cluster2_2shards_2replicas(
  stat_date Date comment '访问日期',
  imei String comment 'imei',
  user_flag Nullable(String) comment '新老用户',
  is_buluo Nullable(String) comment '是否访问部落',
  is_kuang Nullable(String) comment '是否访问神奇矿',
  is_toutiao Nullable(String) comment '是否访问头条',
  diaoqi_qudaos Nullable(String) comment '调起渠道',
  time Nullable(Float32) comment '访问时长'
) ENGINE = Distributed(cluster2_2shards_2replicas, data_usergroup, t_person_imei_base_product_shard, sipHash64(imei))
```



## 3.effect表

### 介绍

存储用户访问渠道信息，pv、uv、cash、400num等，采用分布式表`ReplicatedMergeTree`引擎，每天写入增量数据。

### 分片表

```sql
CREATE TABLE if not exists data_usergroup.t_person_imei_base_effect_shard  on cluster cluster2_2shards_2replicas(
 stat_date Date,
  imei String,
  channel_id Nullable(String),
  channel_name Nullable(String),
  channel_media Nullable(String),
  channel_type Nullable(String),
  pv Nullable(Float32),
  huangye_pv Nullable(Float32),
  zhaopin_pv Nullable(Float32),
  chuangxin_pv Nullable(Float32),
  tuiguang_pv Nullable(Float32),
  fangchan_pv Nullable(Float32),
  ershouche_pv Nullable(Float32),
  vppv Nullable(Float32),
  huangye_vppv Nullable(Float32),
  fangchan_vppv Nullable(Float32),
  zhaopin_vppv Nullable(Float32),
  ershouche_vppv Nullable(Float32),
  ershouchuangxin_vppv Nullable(Float32),
  ershoutuiguang_vppv Nullable(Float32),
  resume_num Nullable(Float32),
  cash Nullable(Float32),
  jingzhun_cash Nullable(Float32),
  huangye_jingzhun_cash Nullable(Float32),
  fangchan_jingzhun_cash Nullable(Float32),
  zhaopin_jingzhun_cash Nullable(Float32),
  ershouche_jingzhun_cash Nullable(Float32),
  ershouchuangxin_jingzhun_cash Nullable(Float32),
  ershoutuiguang_jingzhun_cash Nullable(Float32),
  jingxuan_cash Nullable(Float32),
  fangchan_jingxuan_cash Nullable(Float32),
  ershouche_jingxuan_cash Nullable(Float32),
  zhaopin_youxuan_cash Nullable(Float32),
  ershouche_cpcall_cash Nullable(Float32),
  call_400_num Nullable(Float32),
  hy_call_400_num Nullable(Float32),
  escx_call_400_num Nullable(Float32),
  esc_call_400_num Nullable(Float32)
) ENGINE = ReplicatedMergeTree('/clickhouse/tables/user_group_test/t_person_imei_base_effect_shard/{cluster2_shard}', '{cluster2_replica}') PARTITION BY stat_date ORDER BY imei SETTINGS index_granularity = 8192

```



### 分布式表

```sql
CREATE TABLE if not exists data_usergroup.t_person_imei_base_effect  on cluster cluster2_2shards_2replicas(
 stat_date Date,
  imei String,
  channel_id Nullable(String),
  channel_name Nullable(String),
  channel_media Nullable(String),
  channel_type Nullable(String),
  pv Nullable(Float32),
  huangye_pv Nullable(Float32),
  zhaopin_pv Nullable(Float32),
  chuangxin_pv Nullable(Float32),
  tuiguang_pv Nullable(Float32),
  fangchan_pv Nullable(Float32),
  ershouche_pv Nullable(Float32),
  vppv Nullable(Float32),
  huangye_vppv Nullable(Float32),
  fangchan_vppv Nullable(Float32),
  zhaopin_vppv Nullable(Float32),
  ershouche_vppv Nullable(Float32),
  ershouchuangxin_vppv Nullable(Float32),
  ershoutuiguang_vppv Nullable(Float32),
  resume_num Nullable(Float32),
  cash Nullable(Float32),
  jingzhun_cash Nullable(Float32),
  huangye_jingzhun_cash Nullable(Float32),
  fangchan_jingzhun_cash Nullable(Float32),
  zhaopin_jingzhun_cash Nullable(Float32),
  ershouche_jingzhun_cash Nullable(Float32),
  ershouchuangxin_jingzhun_cash Nullable(Float32),
  ershoutuiguang_jingzhun_cash Nullable(Float32),
  jingxuan_cash Nullable(Float32),
  fangchan_jingxuan_cash Nullable(Float32),
  ershouche_jingxuan_cash Nullable(Float32),
  zhaopin_youxuan_cash Nullable(Float32),
  ershouche_cpcall_cash Nullable(Float32),
  call_400_num Nullable(Float32),
  hy_call_400_num Nullable(Float32),
  escx_call_400_num Nullable(Float32),
  esc_call_400_num Nullable(Float32)
) ENGINE = Distributed(cluster2_2shards_2replicas, data_usergroup, t_person_imei_base_effect_shard, sipHash64(imei))

```



## 4.action表

### 介绍

存储用户行为信息，pv、uv、cash、400num等，采用分布式表`ReplicatedMergeTree`引擎，每天写入增量数据。

### 分片表

```sql
CREATE TABLE if not exists data_usergroup.t_person_action_detail_shard on cluster cluster2_2shards_2replicas (
 stat_date Date,
  imei String,
  type1 Nullable(String),
  type2 Nullable(String),
  action_count Nullable(UInt32)
) ENGINE = ReplicatedMergeTree('/clickhouse/tables/user_group/t_person_action_detail_shard/{cluster2_shard}', '{cluster2_replica}') PARTITION BY stat_date ORDER BY imei SETTINGS index_granularity = 8192
```



### 分布式表

```sql
CREATE TABLE if not exists data_usergroup.t_person_action_detail  on cluster cluster2_2shards_2replicas(
 stat_date Date,
  imei String,
  type1 Nullable(String),
  type2 Nullable(String),
  action_count Nullable(UInt32)
) ENGINE = Distributed(cluster2_2shards_2replicas, data_usergroup, t_person_action_detail_shard, sipHash64(imei))
```



## 5.tb_app_useri表

### 介绍

imei和userid对应表，为了更新total表历史数据的userid字段，仅用了一次

### 分片表

```sql
CREATE TABLE data_usergroup.tb_app_userid_shard on cluster cluster2_2shards_2replicas(
    stat_date Date,
  productorid Nullable(String),
  imei String,
  userid Nullable(String)
) ENGINE = ReplicatedMergeTree('/clickhouse/tables/user_group/tb_app_userid_shard/{cluster2_shard}', '{cluster2_replica}') PARTITION BY stat_date ORDER BY imei SETTINGS index_granularity = 8192
```

### 分布式表

```sql
CREATE TABLE data_usergroup.tb_app_userid on cluster cluster2_2shards_2replicas(
    stat_date Date,
  productorid Nullable(String),
  imei String,
  userid Nullable(String)
) ENGINE = Distributed(
  cluster2_2shards_2replicas,
  data_usergroup,
  tb_app_userid_shard,
  sipHash64(imei)
)
```

## 6.t_person_custom_only_imei

### 介绍

自定义用户集表（临时表），仅存储用户导入的imei和自定义集合id

### 分片表

```sql
CREATE TABLE data_usergroup.t_person_custom_only_imei_shard   on cluster cluster2_2shards_2replicas( group_id UInt32, imei String ) ENGINE = ReplicatedReplacingMergeTree ( '/clickhouse/tables/user_group/t_person_custom_only_imei_shard/{cluster2_shard}', '{cluster2_replica}' ) PARTITION BY group_id 
ORDER BY
	( group_id, imei ) SETTINGS index_granularity = 8192
```

### 分布式表

```sql
CREATE TABLE data_usergroup.t_person_custom_only_imei   on cluster cluster2_2shards_2replicas( group_id UInt32, imei String ) ENGINE = Distributed ( cluster2_2shards_2replicas, data_usergroup, t_person_custom_only_imei_shard, sipHash64 ( group_id, imei ) )
```

## 7.t_person_custom_group_imei

### 介绍

自定义用户集表（最终表），保存t_person_custom_only_imei表和total表join的结果

### 分片表

```sql
CREATE TABLE data_usergroup.t_person_custom_group_imei_shard  on cluster cluster2_2shards_2replicas (
stat_date Date,
imei String,
idfa Nullable ( String ),
new_flag Nullable ( String ),
first_date Nullable ( Date ),
last_date Nullable ( Date ),
reg_cid Nullable ( String ),
busstype_like Nullable ( String ),
cate1_like Nullable ( String ),
cate2_like Nullable ( String ),
gender Nullable ( String ),
age Nullable ( String ),
mobile_size Nullable ( String ),
area Nullable ( String ),
brand Nullable ( String ),
province Nullable ( String ),
city Nullable ( String ),
pid Nullable ( String ),
version Nullable ( String ),
city1 Nullable ( String ),
apn Nullable ( String ),
userid Nullable ( String ),
city2 Nullable(String),
last2_date Nullable(Date),  
group_id UInt32 
) ENGINE = ReplicatedReplacingMergeTree ( '/clickhouse/tables/user_group/t_person_custom_group_imei_shard/{cluster2_shard}', '{cluster2_replica}' ) PARTITION BY group_id 
ORDER BY
	( group_id, imei ) SETTINGS index_granularity = 8192
```



### 分布式表

```sql
CREATE TABLE data_usergroup.t_person_custom_group_imei  on cluster cluster2_2shards_2replicas (
stat_date Date,
imei String,
idfa Nullable ( String ),
new_flag Nullable ( String ),
first_date Nullable ( Date ),
last_date Nullable ( Date ),
reg_cid Nullable ( String ),
busstype_like Nullable ( String ),
cate1_like Nullable ( String ),
cate2_like Nullable ( String ),
gender Nullable ( String ),
age Nullable ( String ),
mobile_size Nullable ( String ),
area Nullable ( String ),
brand Nullable ( String ),
province Nullable ( String ),
city Nullable ( String ),
pid Nullable ( String ),
version Nullable ( String ),
city1 Nullable ( String ),
apn Nullable ( String ),
userid Nullable ( String ),
city2 Nullable(String),
last2_date Nullable(Date),  
group_id UInt32 
) ENGINE = Distributed ( cluster2_2shards_2replicas, data_usergroup, t_person_custom_group_imei_shard, sipHash64 ( group_id, imei ) )
```

## 8.t_person_imei_group_XXX

### 介绍

用户分群结果表，用户建立分群后，建立该表，`XXX` 与分群id对应，并将该分群中的用户基础信息，包括在分群时间段内的总uv、总vppv、总cash额提取到该表中，提供分群分布展示

### 分片表

```sql
CREATE TABLE data_usergroup.t_person_imei_group_101_shard   on cluster cluster2_2shards_2replicas(
stat_date Date,
imei String,
idfa Nullable ( String ),
new_flag Nullable ( String ),
first_date Nullable ( Date ),
last_date Nullable ( Date ),
reg_cid Nullable ( String ),
busstype_like Nullable ( String ),
cate1_like Nullable ( String ),
cate2_like Nullable ( String ),
gender Nullable ( String ),
age Nullable ( String ),
mobile_size Nullable ( String ),
area Nullable ( String ),
brand Nullable ( String ),
province Nullable ( String ),
city Nullable ( String ),
pid Nullable ( String ),
version Nullable ( String ),
city1 Nullable ( String ),
apn Nullable ( String ),
userid Nullable ( String ),
city2 Nullable(String),
last2_date Nullable(Date),    
sum_pv Nullable ( Float32 ) COMMENT '总uv',
sum_vppv Nullable ( Float32 ) COMMENT '总vppv',
sum_cash Nullable ( Float32 ) COMMENT '总cash' 
) ENGINE = ReplicatedReplacingMergeTree ( '/clickhouse/tables/user_group/t_person_imei_group_101_shard/{cluster2_shard}', '{cluster2_replica}' ) PARTITION BY stat_date 
ORDER BY
	imei SETTINGS index_granularity = 8192
```



### 分布式表

```sql
CREATE TABLE data_usergroup.t_person_imei_group_101  on cluster cluster2_2shards_2replicas (
stat_date Date,
imei String,
idfa Nullable ( String ),
new_flag Nullable ( String ),
first_date Nullable ( Date ),
last_date Nullable ( Date ),
reg_cid Nullable ( String ),
busstype_like Nullable ( String ),
cate1_like Nullable ( String ),
cate2_like Nullable ( String ),
gender Nullable ( String ),
age Nullable ( String ),
mobile_size Nullable ( String ),
area Nullable ( String ),
brand Nullable ( String ),
province Nullable ( String ),
city Nullable ( String ),
pid Nullable ( String ),
version Nullable ( String ),
city1 Nullable ( String ),
apn Nullable ( String ),
userid Nullable ( String ),
city2 Nullable(String),
last2_date Nullable(Date),  
sum_pv Nullable ( Float32 ) comment '总uv',
sum_vppv Nullable ( Float32 ) comment '总vppv',
sum_cash Nullable ( Float32 ) comment '总cash' 
) ENGINE = Distributed ( cluster2_2shards_2replicas, data_usergroup, t_person_imei_group_101_shard, sipHash64 ( imei ) )
```

## 9.t_person_user_identity表

### 介绍

存储业务身份信息，每日全量更新

### 分片表

```sql
CREATE TABLE data_usergroup.t_person_user_identity_shard   on cluster cluster2_2shards_2replicas(
userid Nullable ( String ) comment '用户user_id',
imei String comment '用户imei号',
type1 Nullable ( String ) comment '身份所属业务模块',
identity_name Nullable ( String ) comment '最新身份name'
) ENGINE = ReplicatedMergeTree ( '/clickhouse/tables/user_group/t_person_user_identity_shard/{cluster2_shard}', '{cluster2_replica}' ) 
ORDER BY
	imei SETTINGS index_granularity = 8192;
```

### 分布式表

```sql
CREATE TABLE data_usergroup.t_person_user_identity   on cluster cluster2_2shards_2replicas(
userid Nullable ( String ) comment '用户user_id',
imei String comment '用户imei号',
type1 Nullable ( String ) comment '身份所属业务模块',
identity_name Nullable ( String ) comment '最新身份name'
) ENGINE = Distributed ( cluster2_2shards_2replicas, data_usergroup, t_person_user_identity_shard, sipHash64 ( imei ) )
```

## 10.t_person_imei_event_neirong表

### 介绍

存储互动信息，每日增量更新

### 分片表

```sql
CREATE TABLE data_usergroup.t_person_imei_event_neirong_shard   on cluster cluster2_2shards_2replicas(
dt Date comment '计算时间',
imei String comment '用户imei号',
userid Nullable ( String ) comment '用户user_id',
stat_date Date comment '发生日期',
hour  UInt8  comment '发生小时',
timestamp UInt64 comment '发生timestamp',
business_type Nullable ( String ) comment '业务类型',
nr_type1 Nullable ( String ) comment '身份所属业务模块',
event_type	 Nullable ( String ) comment '事件类型',
event_id	 Nullable ( String ) comment '事件id',
event_name	 Nullable ( String ) comment '事件名',
event_obj	 Nullable ( String ) comment '事件对象',
event_param	 Nullable ( String ) comment '事件参数',
event_value	 Nullable ( String ) comment '事件值'
) ENGINE = ReplicatedMergeTree ( '/clickhouse/tables/user_group/t_person_imei_event_neirong_shard/{cluster2_shard}', '{cluster2_replica}' ) 
PARTITION BY dt 
ORDER BY
	imei SETTINGS index_granularity = 8192;
```

### 分布式表

```sql
CREATE TABLE data_usergroup.t_person_imei_event_neirong   on cluster cluster2_2shards_2replicas(
dt Date comment '计算时间',
imei String comment '用户imei号',
userid Nullable ( String ) comment '用户user_id',
stat_date Date comment '发生日期',
hour  UInt8  comment '发生小时',
timestamp UInt64 comment '发生timestamp',
business_type Nullable ( String ) comment '业务类型',
nr_type1 Nullable ( String ) comment '身份所属业务模块',
event_type	 Nullable ( String ) comment '事件类型',
event_id	 Nullable ( String ) comment '事件id',
event_name	 Nullable ( String ) comment '事件名',
event_obj	 Nullable ( String ) comment '事件对象',
event_param	 Nullable ( String ) comment '事件参数',
event_value	 Nullable ( String ) comment '事件值'
) ENGINE = Distributed ( cluster2_2shards_2replicas, data_usergroup, t_person_imei_event_neirong_shard, sipHash64 ( imei ) )
```

```sql
select  stat_date - toDate(if(visitParamExtractString(event_param,'laster_date') =='','0000-00-00',visitParamExtractString(event_param,'laster_date'))) as diff_date from data_usergroup.t_person_imei_event_neirong limit 10;
```

```sql
select  sum(toFloat32(visitParamExtractString(event_value,'number') )) as sum_number from data_usergroup.t_person_imei_event_neirong group by imei limit 10;
```

## 11.t_person_imei_total_yesterday_tmp表

### 介绍

临时存储昨天活跃用户，非复制表（集群cluster2_4shards_0replicas）, 每日全量（先清空再写入）

### 分片表

```sql
CREATE TABLE if not exists data_usergroup.t_person_imei_total_yesterday_tmp_shard on cluster cluster2_4shards_0replicas(
 stat_date Date,
  imei String,
  idfa Nullable(String),
  new_flag Nullable(String),
  first_date Nullable(Date),
  last_date Nullable(Date),
  reg_cid Nullable(String),
  busstype_like Nullable(String),
  cate1_like Nullable(String),
  cate2_like Nullable(String),
  gender Nullable(String),
  age Nullable(String),
  mobile_size Nullable(String),
  area Nullable(String),
  brand Nullable(String),
  province Nullable(String),
  city Nullable(String),
  pid Nullable(String),
  version Nullable(String),
  city1 Nullable(String),
  apn Nullable(String),
  userid Nullable(String),
  city2 Nullable(String)
) ENGINE = MergeTree() PARTITION BY stat_date ORDER BY imei SETTINGS index_granularity = 8192

```



### 分布式表

```sql
CREATE TABLE if not exists data_usergroup.t_person_imei_total_yesterday_tmp on cluster cluster2_4shards_0replicas(
  stat_date Date,
  imei String,
  idfa Nullable(String),
  new_flag Nullable(String),
  first_date Nullable(Date),
  last_date Nullable(Date),
  reg_cid Nullable(String),
  busstype_like Nullable(String),
  cate1_like Nullable(String),
  cate2_like Nullable(String),
  gender Nullable(String),
  age Nullable(String),
  mobile_size Nullable(String),
  area Nullable(String),
  brand Nullable(String),
  province Nullable(String),
  city Nullable(String),
  pid Nullable(String),
  version Nullable(String),
  city1 Nullable(String),
  apn Nullable(String),
  userid Nullable(String),
  city2 Nullable(String)
) ENGINE = Distributed(
  cluster2_4shards_0replicas,
  data_usergroup,
  t_person_imei_total_yesterday_tmp_shard,
  sipHash64(imei)
)
```



## 12.t_person_imei_total_yesterday_tmp_final表

### 介绍

临时存储昨天活跃用户，非复制表（集群cluster2_4shards_0replicas）, 每日全量（先清空再写入）

是t_person_imei_total_yesterday_tmp与t_person_imei_total表join结果表，目的是为了计算t_person_imei_total中last2_date字段，逻辑是原t_person_imei_total表中的last_date字段赋值给t_person_imei_total_yesterday_tmp中的last2_date字段

### 数据生成SQL

```sql
insert into data_usergroup.t_person_imei_total_yesterday_tmp_final SELECT b.*,a.last_date as last2_date FROM(SELECT last_date,imei FROM data_usergroup.t_person_imei_total) AS a RIGHT JOIN data_usergroup.t_person_imei_total_yesterday_tmp AS b USING (imei);
```



### 分片表

```sql
CREATE TABLE if not exists data_usergroup.t_person_imei_total_yesterday_tmp_final_shard  on cluster cluster2_4shards_0replicas(
 stat_date Date,
  imei String,
  idfa Nullable(String),
  new_flag Nullable(String),
  first_date Nullable(Date),
  last_date Nullable(Date),
  reg_cid Nullable(String),
  busstype_like Nullable(String),
  cate1_like Nullable(String),
  cate2_like Nullable(String),
  gender Nullable(String),
  age Nullable(String),
  mobile_size Nullable(String),
  area Nullable(String),
  brand Nullable(String),
  province Nullable(String),
  city Nullable(String),
  pid Nullable(String),
  version Nullable(String),
  city1 Nullable(String),
  apn Nullable(String),
  userid Nullable(String),
  last2_date Nullable(Date)
) ENGINE = MergeTree() PARTITION BY stat_date ORDER BY imei SETTINGS index_granularity = 8192;
```



### 分布式表

```sql
CREATE TABLE if not exists data_usergroup.t_person_imei_total_yesterday_tmp_final  on cluster cluster2_4shards_0replicas(
  stat_date Date,
  imei String,
  idfa Nullable(String),
  new_flag Nullable(String),
  first_date Nullable(Date),
  last_date Nullable(Date),
  reg_cid Nullable(String),
  busstype_like Nullable(String),
  cate1_like Nullable(String),
  cate2_like Nullable(String),
  gender Nullable(String),
  age Nullable(String),
  mobile_size Nullable(String),
  area Nullable(String),
  brand Nullable(String),
  province Nullable(String),
  city Nullable(String),
  pid Nullable(String),
  version Nullable(String),
  city1 Nullable(String),
  apn Nullable(String),
  userid Nullable(String),
  last2_date Nullable(Date)
) ENGINE = Distributed(
  cluster2_4shards_0replicas,
  data_usergroup,
  t_person_imei_total_yesterday_tmp_final_shard,
  sipHash64(imei)
)
```

## 13.t_person_custom_only_imei_xxx

### 介绍

自定义用户集表（临时表），仅存储用户导入的imei和自定义集合id

### 分片表

```sql
CREATE TABLE if not exists data_usergroup.t_person_custom_only_imei_100_shard on cluster cluster2_4shards_0replicas(imei String ) ENGINE = MergeTree () 
ORDER BY
	imei SETTINGS index_granularity = 8192
```

### 分布式表

```sql
CREATE TABLE if not exists data_usergroup.t_person_custom_only_imei_100 on cluster cluster2_4shards_0replicas(imei String ) ENGINE = Distributed ( cluster2_4shards_0replicas, data_usergroup, t_person_custom_only_imei_100_shard, sipHash64 ( imei ) )
```

## 14.t_person_custom_group_imei_xxx

### 介绍

自定义用户集表（最终表），保存t_person_custom_only_imei表和total表join的结果

### 分片表

```sql
CREATE TABLE if not exists data_usergroup.t_person_custom_group_imei_100_shard  on cluster cluster2_2shards_2replicas (
stat_date Date,
imei String,
idfa Nullable ( String ),
new_flag Nullable ( String ),
first_date Nullable ( Date ),
last_date Nullable ( Date ),
reg_cid Nullable ( String ),
busstype_like Nullable ( String ),
cate1_like Nullable ( String ),
cate2_like Nullable ( String ),
gender Nullable ( String ),
age Nullable ( String ),
mobile_size Nullable ( String ),
area Nullable ( String ),
brand Nullable ( String ),
province Nullable ( String ),
city Nullable ( String ),
pid Nullable ( String ),
version Nullable ( String ),
city1 Nullable ( String ),
apn Nullable ( String ),
userid Nullable ( String ),
city2 Nullable(String),
last2_date Nullable(Date)
) ENGINE = ReplicatedMergeTree ( '/clickhouse/tables/user_group/t_person_custom_group_imei_100_shard/{cluster2_shard}', '{cluster2_replica}' ) PARTITION BY stat_date 
ORDER BY
	(imei ) SETTINGS index_granularity = 8192
```



### 分布式表

```sql
CREATE TABLE if not exists data_usergroup.t_person_custom_group_imei_100  on cluster cluster2_2shards_2replicas (
stat_date Date,
imei String,
idfa Nullable ( String ),
new_flag Nullable ( String ),
first_date Nullable ( Date ),
last_date Nullable ( Date ),
reg_cid Nullable ( String ),
busstype_like Nullable ( String ),
cate1_like Nullable ( String ),
cate2_like Nullable ( String ),
gender Nullable ( String ),
age Nullable ( String ),
mobile_size Nullable ( String ),
area Nullable ( String ),
brand Nullable ( String ),
province Nullable ( String ),
city Nullable ( String ),
pid Nullable ( String ),
version Nullable ( String ),
city1 Nullable ( String ),
apn Nullable ( String ),
userid Nullable ( String ),
city2 Nullable(String),
last2_date Nullable(Date)
) ENGINE = Distributed ( cluster2_2shards_2replicas, data_usergroup, t_person_custom_group_imei_100_shard, sipHash64 (imei) )
```

## 15.全量刷新province字段，brand字段

```sql
CREATE
	TABLE
		data_usergroup.t_person_imei_total2  on cluster cluster2_2shards_2replicas(`stat_date` Date,
		`imei` String,
		`idfa` Nullable(String),
		`new_flag` Nullable(String),
		`first_date` Nullable(Date),
		`last_date` Nullable(Date),
		`reg_cid` Nullable(String),
		`busstype_like` Nullable(String),
		`cate1_like` Nullable(String),
		`cate2_like` Nullable(String),
		`gender` Nullable(String),
		`age` Nullable(String),
		`mobile_size` Nullable(String),
		`area` Nullable(String),
		`brand` Nullable(String),
		`province` Nullable(String),
		`city` Nullable(String),
		`pid` Nullable(String),
		`version` Nullable(String),
		`city1` Nullable(String),
		`apn` Nullable(String),
		`userid` Nullable(String),
		`city2` Nullable(String),
		`industry` Nullable(String),
		`app_name` Nullable(String),
		`job_cate2` Nullable(String),
		`job_cate3` Nullable(String),
		`last2_date` Nullable(Date)) ENGINE = Distributed(cluster2_2shards_2replicas,
		data_usergroup,
		t_person_imei_total2_shard,
		sipHash64(imei))

CREATE
	TABLE
		data_usergroup.t_person_imei_total2_shard  on cluster cluster2_2shards_2replicas(`stat_date` Date,
		`imei` String,
		`idfa` Nullable(String),
		`new_flag` Nullable(String),
		`first_date` Nullable(Date),
		`last_date` Nullable(Date),
		`reg_cid` Nullable(String),
		`busstype_like` Nullable(String),
		`cate1_like` Nullable(String),
		`cate2_like` Nullable(String),
		`gender` Nullable(String),
		`age` Nullable(String),
		`mobile_size` Nullable(String),
		`area` Nullable(String),
		`brand` Nullable(String),
		`province` Nullable(String),
		`city` Nullable(String),
		`pid` Nullable(String),
		`version` Nullable(String),
		`city1` Nullable(String),
		`apn` Nullable(String),
		`userid` Nullable(String),
		`city2` Nullable(String),
		`industry` Nullable(String),
		`app_name` Nullable(String),
		`job_cate2` Nullable(String),
		`job_cate3` Nullable(String),
		`last2_date` Nullable(Date)) ENGINE = ReplicatedReplacingMergeTree('/clickhouse/tables/user_group/t_person_imei_total3_shard/{cluster2_shard}',
		'{cluster2_replica}') PARTITION BY stat_date
	ORDER BY
		imei SETTINGS index_granularity = 8192


insert into data_usergroup.t_person_imei_total2
select
	a.stat_date,
	a.imei,
	a.idfa,
	a.new_flag,
	a.first_date,
	a.last_date,
	a.reg_cid,
	a.busstype_like,
	a.cate1_like,
	a.cate2_like,
	a.gender,
	a.age,
	a.mobile_size,
	a.area,
	c.brand,
	b.province,
	a.city,
	a.pid,
	a.version,
	a.city1,
	a.apn ,
	a.userid,
	a.city2,
	a.industry,
	a.app_name,
	a.job_cate2,
	a.job_cate3,
	a.last2_date
from
	data_usergroup.t_person_imei_total a
left join data_usergroup.t_city_province_sketch b on
	a.city1 = b.city1_id
left join data_usergroup.t_crawler_phone_size c on
	a.mobile_size = c.model_code
```

