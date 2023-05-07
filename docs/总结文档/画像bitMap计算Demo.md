bitmap说明 不了解的建议先看一下:

https://www.sohu.com/a/300039010_114877

一下文字可直接粘贴于dbeaver中运行

--建表可能需要写权限,可直接在10.126.89.77节点执行查询操作,已经建好测试total表

--或者可以在实时集群(任意节点)拿同镇数据测试查询性能 imei对应的UInt类型的映射取数逻辑如下

```
select toUInt64(visitParamExtractString(backup,'imei_mapping_id'))
from realtime.dw_tb_app_action_tongzhen_new
where stat_date='2020-08-07'
limit 10;
```

--建表

```
create table total(
uid UInt64 COMMENT '用户唯一标识  需为UInt类型',
name String COMMENT '用户属性 名字',
age Int8 COMMENT '用户属性 年龄',
area String COMMENT '用户属性 地域'
)ENGINE=Memory();
```

--导数

```
insert into total values(1,'a',20,'北京');
insert into total values(2,'b',18,'上海');
insert into total values(3,'c',20,'北京');
insert into total values(4,'d',18,'上海');
insert into total values(5,'e',18,'北京');
insert into total values(6,'a',20,'上海');
insert into total values(7,'a',18,'北京');
insert into total values(8,'a',20,'上海');
```

--名字为a的uid 记为标签A

```
select 
uid
from total
where name='a';
```

--年龄为20的uid 记为标签B

```
select 
uid
from total
where age=20;
```

--地域为上海的uid 记为标签C

```
SELECT
uid
from total
where area='上海';
```

--正排 计算 (A&B)|C ( (名字为a并且年龄为20)或地域在北京 ) uv uid:[1,2,4,6,8] uv为 5

```
select 
count(distinct uid)
from total
where 1=1
and name='a'
and age=20
or area ='上海';
```

--bitmap计算 (A&B)|C  uv 5  相关函数说明请参考下面的示例

```
select 
bitmapOrCardinality(bitmapAnd(a,b),c) as uv
from
(
select 
1 as j1,
groupBitmapState(uid) as a
from total
where name='a'
) a 
inner join 
(
select 
1 as j2,
groupBitmapState(uid) as b
from total
where age=20
) b 
on a.j1=b.j2
inner join 
(
select 
1 as j3,
groupBitmapState(uid) as c
from total
where area='上海'
) c
on a.j1=c.j3;
```

--groupBitmapState udaf 聚合构建bitMap

```
select 
groupBitmapState(uid)
from total
where area='上海';
```

-- bitmapBuild 基于UInt数组直接构建bitMap

```
select bitmapBuild([1,2,3]);
```

--bitmapToArray 将bitMap转换为数组

```
select bitmapToArray(bitmapBuild([1,2,3]));
```

--bitmapSubsetInRange 截取bitMap指定范围元素并生成新bitMap  含头不含尾  目前版本不支持

```
select bitmapToArray(bitmapSubsetInRange(bitmapBuild([1,2,3]), toUInt32(2), toUInt32(3))); --2
```

--bitmapSubsetLimit(bitmap, range_start, limit) 截取bitMap以range_start元素为起始,最大长度为limit 目前版本不支持

```
SELECT bitmapToArray(bitmapSubsetInRange(bitmapBuild([1,2,3]), toUInt32(1), toUInt32(3))); --  1 2 3
```

--bitmapContains(haystack, needle) 判断needle 是否在haystack 中,返回1 或 0  目前版本不支持

```
SELECT bitmapContains(bitmapBuild([1,2,3]), toUInt32(2)) AS res;
```

--bitmapHasAny(bitmap,bitmap) 如果两个bitmap中有任意值返回 1 目前版本不支持

```
select bitmapHasAny(bitmapBuild([1,2,3]),bitmapBuild([3,4,5]))
```

--bitmapHasAll(bitmap,bitmap) 第一个bitmap包含第二个bitmap  返回1 目前版本不支持

```
select bitmapHasAll(bitmapBuild([1,2,3]),bitmapBuild([1,2]))
```

--bitmapAnd(bitmap1,bitmap2) 两个bitmap与运算

```
select bitmapToArray(bitmapAnd(bitmapBuild([1,2,3]),bitmapBuild([3,4,5])));   --3
```

--bitmapOr(bitmap1,bitmap2) 两个bitmap或运算

```
select bitmapToArray(bitmapOr(bitmapBuild([1,2,3]),bitmapBuild([3,4,5])));   --[1,2,3,4,5]
```

--bitmapXor(bitmap1,bitmap2) 两个bitmap异或运算

```
select bitmapToArray(bitmapXor(bitmapBuild([1,2,3]),bitmapBuild([3,4,5])));   --[1,2,4,5]
```

--bitmapAndnot(bitmap1,bitmap2) 两个bitmap与非运算?

```
select bitmapToArray(bitmapAndnot(bitmapBuild([1,2,3]),bitmapBuild([3,4,5])));   --[1,2]
```

--bitmapCardinality(bitmap) 返回bitmap基数(uv)

```
SELECT bitmapCardinality(bitmapBuild([1, 2, 3, 5]));  --4
```

--bitmapMin(bitmap) 返回bitmap 最小值 目前版本不支持

```
SELECT bitmapMin(bitmapBuild([1, 2, 3, 5]));  --1
```

--bitmapMax(bitmap) 返回bitmap 最大值 目前版本不支持

```
SELECT bitmapMax(bitmapBuild([1, 2, 3, 5]));  --5
```

--bitmapAndCardinality(bitmap1,bitmap2) 两个位图与运算直接返回基数(uv)

```
SELECT bitmapAndCardinality(bitmapBuild([1,2,3]),bitmapBuild([2,3,4])); -- 2
```

--bitmapOrCardinality(bitmap1,bitmap2) 两个位图或运算直接返回基数(uv)

```
SELECT bitmapOrCardinality(bitmapBuild([1,2,3]),bitmapBuild([2,3,4])); -- 4
```

--bitmapXorCardinality(bitmap1,bitmap2) 两个位图异或运算直接返回基数(uv)

```
SELECT bitmapXorCardinality(bitmapBuild([1,2,3]),bitmapBuild([2,3,4])); -- 2
```

--bitmapAndnotCardinality(bitmap1,bitmap2) 两个位图与非运算直接返回基数(uv)?

```
SELECT bitmapAndnotCardinality(bitmapBuild([1,2,3]),bitmapBuild([2,3,4])); -- 1
```

官方文档: https://clickhouse.tech/docs/zh/sql-reference/functions/bitmap-functions/