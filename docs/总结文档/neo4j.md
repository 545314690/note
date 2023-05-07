# Neo4j图形数据库

## 查询地址

### neo4j自带

http://10.126.87.74:7474/browser/        用户名密码 neo4j/123456

### d3-neo4j

 http://event-track.58corp.com/d3js-neo4j/ctm-query.html

## 简介

**Neo4j的是流行的图形数据库的之一，CQL表示Cypher Query Language。** Neo4j的是用Java语言编写

## 安装

### 下载 并解压

https://neo4j.com/artifact.php?name=neo4j-community-3.3.4-unix.tar.gz

### 修改conf/neo4j.conf配置文件

> Bolt connector 是登录和客户端连接配置
>
> HTTP Connector是web界面配置

```properties
# Bolt connector
dbms.connector.bolt.enabled=true
#dbms.connector.bolt.tls_level=OPTIONAL
dbms.connector.bolt.listen_address=0.0.0.0:7687

# HTTP Connector. There can be zero or one HTTP connectors.
dbms.connector.http.enabled=true
dbms.connector.http.listen_address=0.0.0.0:7474

# 内存根据需要设置
#为最小的堆大小
dbms.memory.heap.initial_size=20g
#为最大的堆大小
dbms.memory.heap.max_size=50g
#为load数据到内存，进行缓存内存大小
dbms.memory.pagecache.size=20g
```

### 配置JAVA_HOME

依赖java运行环境，该版本的neo4j需要jdk1.8或者更高版本

### 启动

```shell
bin/neo4j start
```

### 停止

```shell
bin/neo4j stop
```



## 访问

浏览器打开 http://10.126.87.74:7474/browser/ 输入默认用户名密码neo4j/neo4j，首次登陆需要修改密码

## Neo4j查询语法

> 更多语法请参考
>
> https://neo4j.com.cn/public/cypher/default.html
>
> https://neo4j.com/docs/cypher-manual/current/functions/aggregating/

### 创建索引

> 非常重要，当使用该属性查询的时候

```sql
Create index on: User(userid);
```

### 刪除索引

```sql
Drop index on: User(userid);
```

### 属性唯一性约束

```sql
Create constrain on (a:User) ASSERT a.userid IS UNIQUE
```

### 删除唯一约束

```sql
Drop constrain on (a:User) ASSERT a.userid IS UNIQUE
```

### 插入节点

> 在这里用MERGE关键字没有节点会创建，有会覆盖原有
>
> 可以使用CREATE关键字进行创建

```sql
CREATE (n:User {name:'木子',userid:10001,influence:0.99})
```

### 删除节点

> 该结点如果有边是无法删除的，需要先删除边

```sql
MATCH (e: User) DELETE e
```

### 删除节点以及边

```sql
MATCH (na: User)-[r]-(nb:User) 
DELETE na,r,nb
```

### 删除属性

```CQL
MATCH (u:User) 
REMOVE u.in_degree
```

### 设置节点属性

```CQL
MATCH (u:User) where u.name = 'aaa'
SET u.name = 'bbb'
RETURN u
```

### 将度更新为出入度之和

```sql
MERGE (a:User) SET a.degree = (a.in_degree+a.out_degree)
```



### 查询节点数

```sql
MATCH (n:User) RETURN COUNT(*)
```

### 查询节点

```sql
 MATCH (a:User) WHERE a.name = "木子" RETURN a.name AS name, a.userid AS uid
```

```sql
MATCH (a:User) WHERE a.name = "木子" RETURN a
```

### 查询孤立节点

```cql
match (n:User) where n.biz_name='车' and not (n)–[]-() return n;
```



### 为已有节点添加边

> 为查询熟悉userid创建索引非常重要，否则Node数据量大的时候插入速度非常慢
>
> 在这里用MERGE关键字没有边会创建，有会覆盖原有
>
> 可以使用CREATE关键字进行创建
>
> 注意：如果多线程写入，不同的线程对相同的节点写入边会导致失败

```sql
MATCH (a:User {userid: 10001}),(b:User {userid: 10002})
MERGE (a)-[r:FOLLOW]->(b)
```

### 查询节点并排序

```sql
MATCH (a: User {userid: '34567145280009'})-[r:FOLLOW]-(b: User) RETURN a,b,r ORDER BY b.totaleffect DESC
```

### IN查询

```sql
MATCH (a:User)-[r:FOLLOW]-(b: User) 
WHERE a.userid IN ['64069963085317','65421507222533']
RETURN a,r,b ORDER BY b.totaleffect DESC LIMIT 50
```

### UNION 语法

```sql
MATCH (a: User {userid: '65421507222533'})-[r:FOLLOW]-(b: User) RETURN a,b,r ORDER BY b.totaleffect DESC limit 100
UNION
MATCH (a: User {userid: '64069963085317'})-[r:FOLLOW]-(b: User) RETURN a,b,r ORDER BY b.totaleffect DESC limit 100
UNION
MATCH (a: User {userid: '64007333289222'})-[r:FOLLOW]-(b: User) RETURN a,b,r ORDER BY b.totaleffect DESC limit 100
UNION
MATCH (a: User {userid: '40254216009743'})-[r:FOLLOW]-(b: User) RETURN a,b,r ORDER BY b.totaleffect DESC limit 100
UNION
MATCH (a: User {userid: '68374104171790'})-[r:FOLLOW]-(b: User) RETURN a,b,r ORDER BY b.totaleffect DESC limit 100
UNION
MATCH (a: User {userid: '53657234709273'})-[r:FOLLOW]-(b: User) RETURN a,b,r ORDER BY b.totaleffect DESC limit 100
UNION
MATCH (a: User {userid: '55618913122322'})-[r:FOLLOW]-(b: User) RETURN a,b,r ORDER BY b.totaleffect DESC limit 100
UNION
MATCH (a: User {userid: '63732445457427'})-[r:FOLLOW]-(b: User) RETURN a,b,r ORDER BY b.totaleffect DESC limit 100
UNION
MATCH (a: User {userid: '68344932466696'})-[r:FOLLOW]-(b: User) RETURN a,b,r ORDER BY b.totaleffect DESC limit 100
UNION
MATCH (a: User {userid: '49211276766733'})-[r:FOLLOW]-(b: User) RETURN a,b,r ORDER BY b.totaleffect DESC limit 100
```

### 二级查询

> 查询节点na关联的节点nb及边，并查询出nb关联的节点nc及边

```sql
match (na:User)-[re]->(nb:User) where na.userid = '65421507222533'  WITH na,re,nb ORDER BY nb.totaleffect DESC limit 100 match (nb:User)-[re2]->(nc:User) return na,re,nb,re2,nc ORDER BY nc.totaleffect DESC limit 100
```

### 分组查询

根据业务线分组group by

```sql
neo4j> MATCH (user:User)
       RETURN COUNT(user.biz_name) AS c, user.biz_name
       ORDER BY c DESC;
+-------------------------+
| c       | user.biz_name |
+-------------------------+
| 2616894 | "other"       |
| 125421  | "情感"          |
| 74072   | "招聘"          |
| 14992   | "房产"          |
| 10469   | "老乡"          |
| 9641    | "神奇工场"        |
| 6944    | "求助咨询"        |
| 4740    | "找房"          |
| 4280    | "同城圈"         |
| 1019    | "车"           |
+-------------------------+

```

### having功能，聚合结果过滤

#### 查询社区人数少于10人的社区编号

```sql
match (n:User)
with n.community as community, count(n.community) as count
where count<10
return community,count
```

### 关系深度运算符

可变数量的关系->节点可以使用-[:TYPE*minHops..maxHops]->。

查询：

如果在1到3的关系中存在路径，将返回开始点和结束点。

```cql
match data=(na:PartnerUser{userid:'70846642846223'})-[*1..3]->(nb:PartnerUser) return data
```



## Neo4j高级用法

> 参考
>
> 非官方算法包安装：https://github.com/neo4j-contrib/neo4j-graph-algorithms
>
> 中心度算法(degree-centrality)：https://neo4j.com/docs/graph-algorithms/current/labs-algorithms/degree-centrality/
>
> 中介中心性算法(betweenness-centrality)：https://neo4j.com/docs/graph-algorithms/current/labs-algorithms/betweenness-centrality/
>
> 接近中心度算法(closeness-centrality)：https://neo4j.com/docs/graph-algorithms/current/labs-algorithms/closeness-centrality/
>
> pagerank算法: https://neo4j.com/docs/graph-algorithms/current/algorithms/page-rank/
>
> louvain算法： https://neo4j.com/docs/graph-algorithms/current/algorithms/louvain/

### 非官方算法包安装

#### 下载对应版本算法jar包，放在neo4j plugins目录下

https://github.com/neo4j-contrib/neo4j-graph-algorithms/releases

#### 配置conf/neo4j.conf

```properties
dbms.security.procedures.unrestricted=algo.*
```

#### 重启Neo4j

```shell
bin/neo4j restart
```

#### 校验是否安装成功

在web界面运行

```sql
CALL algo.list()
```

### 中心度算法(centrality)

#### 求入度排行前10的用户，即求前10个粉丝最多的用户

```sql
neo4j> CALL algo.degree.stream("User", "", {direction: "incoming"})
       YIELD nodeId, score
       RETURN 
       algo.asNode(nodeId).userid AS userid,
       algo.asNode(nodeId).nick_name AS name,
       algo.asNode(nodeId).biz_name AS biz_name,
       algo.asNode(nodeId).level AS level,
       algo.asNode(nodeId).user_type AS user_type,
       algo.asNode(nodeId).areaname AS areaname,
       score AS followers
       ORDER BY followers DESC limit 10;
+------------------------------------------------------------------------------------------+
| userid           | name         | biz_name | level    | user_type | areaname | followers |
+------------------------------------------------------------------------------------------+
| "31249224024843" | "梦醒时光073"    | "情感"     | "other"  | "other"   | "商丘"     | 7189.0    |
| "64007333289222" | "小郭子"        | "招聘"     | "观察员LV6" | "黄V"      | "other"  | 6863.0    |
| "68090549642513" | "3壹伍贰7叁4壹18" | "other"  | "other"  | "other"   | "other"  | 6632.0    |
| "68345061350676" | "二八贰3壹八伍八伍八" | "情感"     | "other"  | "other"   | "other"  | 5325.0    |
| "68344971959052" | "叁壹5贰73肆1壹捌" | "other"  | "other"  | "other"   | "other"  | 5166.0    |
| "66187898582289" | "李小希"        | "情感"     | "other"  | "other"   | "深圳"     | 4941.0    |
| "45851981428750" | "3壹伍贰柒34壹壹捌" | "other"  | "other"  | "other"   | "other"  | 4921.0    |
| "52501293173525" | "大树"         | "招聘"     | "观察员LV6" | "黄V"      | "上海"     | 4734.0    |
| "59269464673046" | "化妆师一丹"      | "情感"     | "other"  | "other"   | "榆林"     | 4592.0    |
| "68446371624463" | "思宇"         | "情感"     | "other"  | "other"   | "other"  | 4522.0    |
+------------------------------------------------------------------------------------------+

10 rows available after 3826 ms, consumed after another 0 ms
```

#### 求粉丝数并将结果写入节点属性 **followers**

```sql
CALL algo.degree("User", "FOLLOWS", {direction: "incoming", writeProperty: "followers"})
```

#### 求出度排行前1000的用户，即求前1000个关注他人最多的用户

```sql
CALL algo.degree.stream("User", "FOLLOW", {direction: "outgoing"})
YIELD nodeId, score
RETURN algo.asNode(nodeId).name AS name, score AS following
ORDER BY following DESC limit 1000
```

#### 求关注他人数并将结果写入节点属性 **following**

```sql
CALL algo.degree("User", "FOLLOW", {direction: "outgoing", writeProperty: "following"})
```

#### 求度最高的

```
CALL algo.degree.stream("User", "FOLLOW", {direction: "both"})
YIELD nodeId, score
RETURN algo.asNode(nodeId).name AS name, score AS following
ORDER BY following DESC limit 1000
```

自定义**边的**分数属性，假如边有权重weight，使用边的**weight**属性作为分数

```sql
CALL algo.degree.stream("User", "FOLLOW", {direction: "incoming", weightProperty: "weight"})
YIELD nodeId, score
RETURN algo.asNode(nodeId).name AS name, score AS weightedFollowers
ORDER BY weightedFollowers DESC limit 1000
```

### 中介中心性(betweenness)

> 计算特别慢，可以在安装目录tail -f logs/neo4j.log来看计算进度 

#### 求被关注中心度排行前10的用户

```sql
CALL algo.betweenness.stream('User','FOLLOW',{direction:'out'})
YIELD nodeId, centrality

MATCH (user:User) WHERE id(user) = nodeId

RETURN user.name AS user,centrality
ORDER BY centrality DESC limit 10;
```

#### 结果写入属性

```sql
CALL algo.betweenness('User','', {direction:'in',write:true, writeProperty:'betweenness'})
YIELD nodes, minCentrality, maxCentrality, sumCentrality, loadMillis, computeMillis, writeMillis;
```



### 接近中心度算法(closeness-centrality)

> 运行慢

```sql
CALL algo.closeness.stream('User', 'FOLLOW')
YIELD nodeId, centrality

RETURN algo.asNode(nodeId).name AS node, centrality
ORDER BY centrality DESC
LIMIT 20;
```

#### 将结果写入属性

```sql
CALL algo.closeness('User', 'FOLLOW', {write:true, writeProperty:'closeness'})
YIELD nodes,loadMillis, computeMillis, writeMillis;
```

### PageRank算法(page-rank)

> https://neo4j.com/docs/graph-algorithms/current/algorithms/page-rank/

#### 计算pagerank值top10的用户

```sql
CALL algo.pageRank.stream('User', 'FOLLOW', {iterations:20, dampingFactor:0.85})
YIELD nodeId, score

RETURN algo.asNode(nodeId).name AS user,score
ORDER BY score DESC limit 10
```

#### 将结果写回数据

```sql
CALL algo.pageRank('User', 'FOLLOW',
  {iterations:20, dampingFactor:0.85, write: true,writeProperty:"pagerank"})
YIELD nodes, iterations, loadMillis, computeMillis, writeMillis, dampingFactor, write, writeProperty
```

#### 带边权重的pagerank，假设边的权重属性是weight

```sql
CALL algo.pageRank.stream('User', 'FOLLOW', {
  iterations:20, dampingFactor:0.85, weightProperty: "weight"
})
YIELD nodeId, score
RETURN algo.asNode(nodeId).name AS user,score
ORDER BY score DESC limit 10
```

#### 将结果写回数据

```sql
CALL algo.pageRank('User', 'FOLLOW',{
  iterations:20, dampingFactor:0.85, write: true, writeProperty:"pagerank", weightProperty: "weight"
})
YIELD nodes, iterations, loadMillis, computeMillis, writeMillis, dampingFactor, write, writeProperty
```

#### 个性化的PageRank

> 以User A为中心的pageRank，没太看懂

```sql
MATCH (userA:User {name: "User A"})

CALL algo.pageRank.stream('User', 'FOLLOW', {iterations:20, dampingFactor:0.85, sourceNodes: [userA]})
YIELD nodeId, score

RETURN algo.asNode(nodeId).name AS user,score
ORDER BY score DESC limit 10
```

#### 将结果写回数据

```sql
MATCH (userA:User {name: "User A"})
CALL algo.pageRank('User', 'FOLLOW',
{iterations:20, dampingFactor:0.85, sourceNodes: [siteA], write: true, writeProperty:"ppr"})
YIELD nodes, iterations, loadMillis, computeMillis, writeMillis, dampingFactor, write, writeProperty
RETURN *
```

### 社区发现算法louvain，用于层次聚类

```sql
CALL algo.louvain.stream('User', 'FOLLOW', {
 direction: 'BOTH'
}) YIELD nodeId, community, communities
RETURN algo.asNode(nodeId).name as name, community, communities
ORDER BY name ASC
```

#### 将结果写入节点属性

> innerIterations 迭代次数设为20次

```sql
CALL algo.louvain('User', '', {
 direction: 'BOTH',
 writeProperty: 'community',
 innerIterations: 20
}) YIELD modularity, modularities;
```

```cql
+-----------------------------------------------------------------------------------------------------------------------------+
| modularity         | modularities                                                                                           |
+-----------------------------------------------------------------------------------------------------------------------------+
| 0.4688855149121618 | [0.5707717460106788, 0.4611418263966956, 0.46833518334338226, 0.46889772861729195, 0.4688855149121618] |
+-----------------------------------------------------------------------------------------------------------------------------+

1 row available after 4840 ms, consumed after another 0 ms
neo4j> 

```



#### 查询节点数前100的社区

```sql
MATCH (user:User)
       RETURN COUNT(user.community) AS c, user.community
       ORDER BY c DESC limit 100;
```



> 
>
> 官网上加了个graph: 'huge'
>
> 
>
> 我加上反而没有结果，去掉是OK的，感觉应该是节点和边过多的时候需要加的吧

```sql
CALL algo.louvain.stream('User', 'FOLLOW', {
 graph: 'huge',
 direction: 'BOTH'
}) YIELD nodeId, community, communities
RETURN algo.asNode(nodeId).name as name, community, communities
ORDER BY name ASC
```

使用includeIntermediateCommunities参数，结果包含社区communities

```sql
CALL algo.louvain.stream('User', 'FOLLOW', {
 direction: 'BOTH',
 includeIntermediateCommunities: true
}) YIELD nodeId, community, communities
RETURN algo.asNode(nodeId).name as name, community, communities
ORDER BY community ASC limit 100
```

将结果写入属性

```sql
CALL algo.beta.louvain('User', 'FOLLOW', {
 direction: 'BOTH',
 writeProperty: 'community'
}) YIELD communities, modularity, modularities
```

根据边的属性weight计算权重

```sql
CALL algo.beta.louvain.stream('User', 'FOLLOW', {
 direction: 'BOTH',
 weightProperty: 'weight'
}) YIELD nodeId, community, communities
RETURN algo.asNode(nodeId).name as name, community, communities
ORDER BY name ASC
```



## 与gephi整合

> 参考https://neo4j.com/docs/labs/apoc/current/export/gephi/
>
> https://tbgraph.wordpress.com/2017/04/01/neo4j-to-gephi/

```sql
match path = (:Person)-[:ACTED_IN]->(:Movie)
WITH path LIMIT 1000
with collect(path) as paths
call apoc.gephi.add('http://10.252.180.74:8899','工作区1', paths) yield nodes, relationships, time
return nodes, relationships, time
```

## 客户端使用

### cypher-shell的使用

> 参考 https://neo4j.com/docs/stable/shell-starting.html

#### 启动cypher-shell

```python
(base) [work@tjtx-87-74 neo4j-community-3.5.14]$ ./bin/cypher-shell -u neo4j -p 123456
Connected to Neo4j 3.5.14 at bolt://localhost:7687 as user neo4j.
Type :help for a list of available commands or :exit to exit the shell.
Note that Cypher queries must end with a semicolon.
neo4j> 

```

即可输入查询命令进行查询

```python
neo4j> CALL algo.degree.stream("User", "", {direction: "incoming"})
       YIELD nodeId, score
       RETURN algo.asNode(nodeId).nick_name AS name, score AS followers
       ORDER BY followers DESC limit 10;
+--------------------------+
| name         | followers |
+--------------------------+
| "梦醒时光073"    | 7189.0    |
| "小郭子"        | 6863.0    |
| "3壹伍贰7叁4壹18" | 6632.0    |
| "二八贰3壹八伍八伍八" | 5325.0    |
| "叁壹5贰73肆1壹捌" | 5166.0    |
| "李小希"        | 4941.0    |
| "3壹伍贰柒34壹壹捌" | 4921.0    |
| "大树"         | 4734.0    |
| "化妆师一丹"      | 4592.0    |
| "思宇"         | 4522.0    |
+--------------------------+

10 rows available after 3928 ms, consumed after another 0 ms
```

#### 运行查询脚本并写入结果到文件

编辑查询语句test.txt

```cql
CALL algo.degree.stream("User", "", {direction: "incoming"})
       YIELD nodeId, score
       RETURN 
       algo.asNode(nodeId).userid AS userid,
       algo.asNode(nodeId).nick_name AS name,
       algo.asNode(nodeId).biz_name AS biz_name,
       algo.asNode(nodeId).level AS level,
       algo.asNode(nodeId).user_type AS user_type,
       algo.asNode(nodeId).areaname AS areaname,
       score AS followers
       ORDER BY followers DESC limit 10;
```

编辑并运行脚本run.sh

```shell
cat test.txt | ./bin/cypher-shell  -u neo4j -p 123456  > test.ret.txt &

# 或者
neo4j-shell -file export.cql > result.txt
```



#### 批量运行脚本

cat closeness.txt

```sql
CALL algo.closeness.stream('User', '')
YIELD nodeId, centrality
RETURN
algo.asNode(nodeId).userid AS userid,
algo.asNode(nodeId).nick_name AS name,
algo.asNode(nodeId).biz_name AS biz_name,
algo.asNode(nodeId).level AS level,
algo.asNode(nodeId).user_type AS user_type,
algo.asNode(nodeId).areaname AS areaname,
centrality
ORDER BY centrality DESC
LIMIT 10;
```

cat betweenness.txt

```cql
CALL algo.betweenness.stream('User','',{direction:'in'})
YIELD nodeId, centrality
MATCH (user:User) WHERE id(user) = nodeId
RETURN user.userid AS userid,
 user.nick_name AS nick_name,
 user.biz_name AS biz_name,
 user.level AS level,
 user.user_type AS user_type,
 user.areaname AS areaname,
centrality
ORDER BY centrality DESC limit 10;
```

编辑run-batch.sh

```shell
cat closeness.txt | ./bin/cypher-shell  -u neo4j -p 123456  > closeness.result.txt
cat betweenness.txt | ./bin/cypher-shell  -u neo4j -p 123456  > betweenness.result.txt
```

sh run-batch.sh &

#### 导出节点

##### 编辑脚本 export.cql

```cql
MATCH (user:User)
RETURN
	user.userid AS userid, 
	user.nick_name AS nick_name, 
	user.biz_name AS biz_name, 
	user.level AS level, 
	user.user_type AS user_type, 
	user.in_degree AS in_degree,
	user.out_degree AS out_degree,
	user.degree AS degree,	
	user.betweenness AS betweenness,
	user.closeness AS closeness,
	user.community AS community
```

##### 运行命令

```shell
cat export.cql | ./bin/cypher-shell -u neo4j -p 123456 --format plain > output_file.csv	
```



### python远程连接工具cycli 

安装cycli 

```python
pip install cycli
```

连接之后可以运行查询语句

```shell
# 查看帮助
cycli --help
# 连接
cycli -h 10.126.87.74 -u neo4j -p 123456
```

