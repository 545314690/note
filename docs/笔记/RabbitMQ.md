




### 职业规划

1.技术专家
2.业务专家
3.技术管理
4.回家卖煎饼



## 亿级流量系统架构之如何支撑百亿级数据的存储与计算

### 1.


1、离线计算与实时计算的拆分

2、分库分表解决数据扩容问题

3.读写分离降低数据库负载

4.flink、spark streaming 实时计算


在这套架构对应的早期业务背景下，每天新增数据大概是亿级左右，但是分库分表之后，单表数据量在百万级别，单台数据库服务器的高峰期写入压力在 2000/s，查询压力在 100/s，
库集群承载的总高峰写入压力在 1 万 / s，查询压力在 500/s，有需要还可以随时扩容更多的数据库服务器，承载更多的数据量，更高的写入并发与查询并发。
而且，因为做了读写分离，因此每个数据库服务器的 CPU 负载和 IO 负载都不会在高峰期打满，避免数据库服务器的负载过高。
而通过实时计算引擎，可以保证当天更新的实时数据主要几秒钟就可以完成一个微批次的计算，反馈到用户看到的数据报表中。

此外，昨天以前的海量数据都是走 Hadoop 与 Spark 生态的离线存储与计算。经过性能优化之后，每天凌晨花费一两个小时，算好昨天以前所有的数据即可。
最后实时与离线的计算结果在同一个 MySQL 数据库中融合，此时用户如果对业务系统做出操作，实时数据报表在几秒后就会刷新，如果要看昨天以前的数据可以随时选择时间范围查看即
可，暂时性是满足了业务的需求。
早期的几个月里，日增上亿数据，离线与实时两条链路中的整体数据量级达到了百亿级别，无论是存储扩容，还是高效计算，这套架构基本是撑住了。

5.计算与存储分离

6.动静分离
不变的数据可以cache

7.冷热数据分离

热数据基于缓存集群 + 数据库集群来承载高并发的每秒十万级别的查询!
冷数据基于 HBase+Elasticsearch + 纯内存自研的查询引擎，解决了海量历史数据的高性能毫秒级的查询
经实践，整个效果非常的好。用户对热数据的查询基本多是几十毫秒的响应速度，对冷数据的查询基本都是 200 毫秒以内的响应速度。







