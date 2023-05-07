# JanusGraph实践

## 下载安装0.3.1

页面 https://github.com/JanusGraph/janusgraph/releases

下载

```sh
wget https://github.com/JanusGraph/janusgraph/releases/download/v0.3.1/janusgraph-0.3.1-hadoop2.zip
```

解压 

```sh
unzip janusgraph-0.3.1-hadoop2.zip

cd janusgraph-0.3.1-hadoop2
```

## 编辑配置文件

编辑配置文件`conf/gremlin-server/online-janusgraph-hbase-es.properties`

这里我们使用hbase存储、elasticsearch索引

```properties
# JanusGraph configuration sample: HBase and Elasticsearch

# Default:    (no default value)
# Data Type:  String
# Mutability: LOCAL
storage.backend=hbase

# Default:    127.0.0.1
# Data Type:  class java.lang.String[]
# Mutability: LOCAL
storage.hostname=10.9.15.54
storage.port=2182

# Default:    false
# Data Type:  Boolean
# Mutability: MASKABLE
cache.db-cache = true

# Default:    50
# Data Type:  Integer
# Mutability: GLOBAL_OFFLINE
#
# Settings with mutability GLOBAL_OFFLINE are centrally managed in
# JanusGraph's storage backend.  After starting the database for the first
# time, this file's copy of this setting is ignored.  Use JanusGraph's
# Management System to read or modify this value after bootstrapping.
cache.db-cache-clean-wait = 20

# Default:    10000
# Data Type:  Long
# Mutability: GLOBAL_OFFLINE
#
# Settings with mutability GLOBAL_OFFLINE are centrally managed in
# JanusGraph's storage backend.  After starting the database for the first
# time, this file's copy of this setting is ignored.  Use JanusGraph's
# Management System to read or modify this value after bootstrapping.
cache.db-cache-time = 180000

# Size of JanusGraph's database level cache.  Values between 0 and 1 are
# interpreted as a percentage of VM heap, while larger values are
# interpreted as an absolute size in bytes.
#
# Default:    0.3
# Data Type:  Double
# Mutability: MASKABLE
cache.db-cache-size = 0.5

#
# Default:    elasticsearch
# Data Type:  String
# Mutability: GLOBAL_OFFLINE
#
# Settings with mutability GLOBAL_OFFLINE are centrally managed in
# JanusGraph's storage backend.  After starting the database for the first
# time, this file's copy of this setting is ignored.  Use JanusGraph's
# Management System to read or modify this value after bootstrapping.
index.search.backend=elasticsearch

# The hostname or comma-separated list of hostnames of index backend
# servers.  This is only applicable to some index backends, such as
# elasticsearch and solr.
#
# Default:    127.0.0.1
# Data Type:  class java.lang.String[]
# Mutability: MASKABLE
#index.search.hostname=10.126.87.74
index.search.hostname=10.126.88.161:9200,10.126.88.169:9200,10.126.123.194:9200,10.126.123.195:9200,10.126.123.198:9200,10.126.123.199:9200
index.search.elasticsearch.http.auth.type=basic
index.search.elasticsearch.http.auth.basic.username=elastic
index.search.elasticsearch.http.auth.basic.password=ecdata

#gremlin.graph=org.janusgraph.core.ConfiguredGraphFactory
gremlin.graph=org.janusgraph.core.JanusGraphFactory
#graph.graphname=ConfigurationManagementGraph

# 对应habase中表名字，没有它会自动创建，如果指定了，则数据写入Hbase对应的表中，这个地方加上部门的命名空间hdp_lbg_ecdata_dw
storage.hbase.table=hdp_lbg_ecdata_dw:buluo
#storage.hbase.table=buluo
# 在ES中的索引别名
index.search.index-name=search
#是否自定义ID, 默认false
#graph.set-vertex-id=true
# 这个参数批量导入需要设置
#storage.batch-loading=true

# 这个参数 经过调试，这个值比较合理
#ids.block-size=20000000
#ids.renew-timeout=3600000
#storage.buffer-size=20240
# 使插入数据更 robust
storage.read-attempts=100
storage.write-attempts=100
storage.attempt-wait=1000

# 分区数，最好设置为 hbase 机器数 的2到 3倍
storage.hbase.region-count = 10

# hbase 超时时间，这个非常重要，不然导入会因为超时报错
# 需要hbase 服务器端同步设置，取客服端和服务器端的最小值
# 这些参数的只是是 看 janusgraph 源码才发现可以设置的
storage.hbase.ext.hbase.rpc.timeout = 300000
storage.hbase.ext.hbase.client.operation.timeout = 300000
storage.hbase.ext.hbase.client.scanner.timeout.period = 300000
```

## 使用Gremlin控制台进行测试

### gremlin查询语法

> gremlin查询语法 更多请见
>
> http://sql2gremlin.com/
>
> http://tinkerpop.apache.org/docs/current/reference/
>
> http://tinkerpop-gremlin.cn

```groovy
bin/gremin.sh


(base) [work(lisenmiao)@tjtx-87-74 janusgraph-0.3.1-hadoop2]$ bin/gremlin.sh 

         \,,,/
         (o o)
-----oOOo-(3)-oOOo-----
SLF4J: Class path contains multiple SLF4J bindings.
SLF4J: Found binding in [jar:file:/opt/soft/janusgraph-0.3.1-hadoop2/lib/slf4j-log4j12-1.7.12.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: Found binding in [jar:file:/opt/soft/janusgraph-0.3.1-hadoop2/lib/logback-classic-1.1.2.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.
SLF4J: Actual binding is of type [org.slf4j.impl.Log4jLoggerFactory]
plugin activated: janusgraph.imports
plugin activated: tinkerpop.server
plugin activated: tinkerpop.utilities
14:54:48 WARN  org.apache.hadoop.util.NativeCodeLoader  - Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
plugin activated: tinkerpop.hadoop
plugin activated: tinkerpop.spark
plugin activated: tinkerpop.tinkergraph
gremlin> 

```

### 加载配置文件，创建图对象

```java
graph = JanusGraphFactory.open('conf/gremlin-server/http-janusgraph-hbase-es.properties')
graph = JanusGraphFactory.open('conf/gremlin-server/online-janusgraph-hbase-es.properties')
g = graph.traversal()

```

### 删库，会删除整个库，包括es的索引（慎用）

```java
graph = JanusGraphFactory.open('conf/gremlin-server/http-janusgraph-hbase-es.properties') 
JanusGraphFactory.drop(graph)
```

### 查看节点数边数

```groovy
g = graph.traversal()   
g.V().count()
g.E().count()
```

### 遍历删除所有节点

```
g.V().drop().iterate()
g.tx().commit()
```

### 执行计划profile，查看使用了哪些索引

```groovy
gremlin> g.V().hasLabel("User").has('user_type','other').has('biz_name','other').valueMap().profile()
==>Traversal Metrics
Step                                                               Count  Traversers       Time (ms)    % Dur
=============================================================================================================
JanusGraphStep([],[~label.eq(User), user_type.e...                  1975        1975          96.562    74.13
    \_condition=(~label = User AND user_type = other AND biz_name = other)
    \_isFitted=false
    \_query=multiKSQ[1]@2147483647
    \_index=biz_name_comp
    \_orders=[]
    \_isOrdered=true
  optimization                                                                                 0.062
  optimization                                                                                 0.781
PropertyMapStep(value)                                              1975        1975          33.701    25.87
                                            >TOTAL                     -           -         130.264        -
```

### 不能用or查询，不会用到索引 

注意`Query requires iterating over all vertices`

```groovy
gremlin> g.V().hasLabel("User").has('node_id','20200210_14507759205127').or().has('node_id','20200210_25365513613574').limit(10).valueMap().profile()
17:34:31 WARN  org.janusgraph.graphdb.transaction.StandardJanusGraphTx  - Query requires iterating over all vertices [()]. For better performance, use indexes

```

### or查询用法

看出用到了两个索引biz_name_comp、areaname_comp

```groovy
gremlin> 
g.V().hasLabel("User").or(__.has('biz_name','other'),__.has('areaname','北京')).limit(10).valueMap().profile()

==================================================================
gremlin> g.V().hasLabel("User").or(__.has('biz_name','other'),__.has('areaname','北京')).limit(10).valueMap().profile()
==>Traversal Metrics
Step                                                               Count  Traversers       Time (ms)    % Dur
=============================================================================================================
JanusGraphStep([],[~label.eq(User)]).Or(JanusGr...                    10          10         133.346    98.25
    \_condition=(~label = User AND areaname = 北京)
    \_isFitted=false
    \_query=multiKSQ[1]@2147483647
    \_limit=10
    \_index=areaname_comp
    \_orders=[]
    \_isOrdered=true
  optimization                                                                                65.382
  optimization                                                                                 0.310
  optimization                                                                                 2.523
  backend-query                                                                                0.000
    \_query=biz_name_comp:multiKSQ[1]@2147483647
  backend-query                                                                                0.000
    \_query=areaname_comp:multiKSQ[1]@2147483647
PropertyMapStep(value)                                                10          10           2.378     1.75
                                            >TOTAL                     -           -         135.725        -
```

### count 查询，会慢

```groovy
g.V().hasLabel("User").or(__.has('biz_name','other'),__.has('biz_name','招聘')).count()
```

### 查询biz_name为other或者为招聘的用户

```cql
g.V().hasLabel("User").or(__.has('biz_name','other'),__.has('biz_name','招聘')).limit(10).valueMap()
```

### eq查询

```
g.V().has("User", "degree", eq(10)).limit(1).valueMap()
```

### 查询degree大于2000的用户

```groovy
g.V().has("User", "degree", gt(2000)).valueMap()
```

### not查询

```groovy
g.V().has("User", "degree", neq(0)).limit(1).valueMap()
```

### 值范围查询

```groovy
g.V().has("User", "degree", between(5f, 10f)).limit(1).valueMap()
```

### 多条件查询

```
g.V().hasLabel("User").has("biz_name", "other").has("degree", gt(1)).limit(1).valueMap()
```

### order by  ,慢，（不会用到索引）

```groovy
gremlin> g.V().hasLabel("User").has('biz_name','招聘').order().by("degree", decr).limit(5).valueMap('nick_name','degree').profile()
15:16:34 WARN  org.janusgraph.graphdb.transaction.StandardJanusGraphTx  - Query requires iterating over all vertices [(~label = User)]. For better performance, use indexes
```

### 分页查询range

```
g.V().hasLabel("User").range(5, 10).valueMap('nick_name','userid')
```

### group by , 慢，（不会用到索引）

```groovy
g.V().hasLabel("User").groupCount().by("biz_name").order(local).by(values, decr).select(keys).limit(local, 5)
```

### fold打包

将Like某用户的前3个人的nick_name,userid放入list返回

```cql
gremlin> g.V().has('jiazhi_card','价值用户').limit(1).in('Like').limit(3).values('nick_name','userid').fold()
==>[29764343700491,other,48165927038478,独来独往,63602367402513,other]
```

两种方式求Like某人的所有人的degree之和，0代表种子

```CQL
gremlin> g.V().has('jiazhi_card','价值用户').limit(1).in('Like').values('degree').fold(0) {a,b -> a + b}
==>104607.0
gremlin> g.V().has('jiazhi_card','价值用户').limit(1).in('Like').values('degree').fold(0, sum) 
==>104607.0

```

### in操作within

相当in操作

```cql
gremlin> g.V().has('userid', within('53328350553621', '40380675376914', '45841116866829')).values('nick_name')
==>守口如瓶
==>速拼在线zc
==>想要一份保障
```

### inject插入

```cql
gremlin> g.V().has('userid', within('53328350553621', '40380675376914', '45841116866829')).values('nick_name').inject('daniel')
==>daniel
==>守口如瓶
==>速拼在线zc
==>想要一份保障
```

### 查询符合条件的节点以及它们之间的关系

```cql
g.V().has('jiazhi_card','价值用户').limit(100).aggregate('s')
 .outE().otherV().where(within('s')).path()
```



## 启动gremlin server服务

通过控制台的方式只能做测试查询，实际上我们需要启动gremlin server服务，提供http或者websocket服务

> 不建议通过该服务进行大量数据导入操作，因为会很慢

### 编辑配置文件

```shell
cp conf/gremlin-server/gremlin-server.yaml  conf/gremlin-server/online-gremlin-server.yaml
vim conf/gremlin-server/online-gremlin-server.yaml
```

内容如下

> 指定使用配置文件`graph: conf/gremlin-server/online-janusgraph-hbase-es.properties`
>
> host 开启外网访问
>
> port 外网访问端口
>
> scriptEvaluationTimeout grovvy脚本执行超时时间

```yaml
host: 0.0.0.0
port: 8182
scriptEvaluationTimeout: 3000000
channelizer: org.apache.tinkerpop.gremlin.server.channel.WsAndHttpChannelizer
graphManager: org.janusgraph.graphdb.management.JanusGraphManager
graphs: {
   graph: conf/gremlin-server/online-janusgraph-hbase-es.properties
}
scriptEngines: {
  gremlin-groovy: {
    plugins: { org.janusgraph.graphdb.tinkerpop.plugin.JanusGraphGremlinPlugin: {},
               org.apache.tinkerpop.gremlin.server.jsr223.GremlinServerGremlinPlugin: {},
               org.apache.tinkerpop.gremlin.tinkergraph.jsr223.TinkerGraphGremlinPlugin: {},
               org.apache.tinkerpop.gremlin.jsr223.ImportGremlinPlugin: {classImports: [java.lang.Math], methodImports: [java.lang.Math#*]},
               org.apache.tinkerpop.gremlin.jsr223.ScriptFileGremlinPlugin: {files: [scripts/empty-sample.groovy]}}}}
serializers:
  - { className: org.apache.tinkerpop.gremlin.driver.ser.GryoMessageSerializerV3d0, config: { ioRegistries: [org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistry] }}
  - { className: org.apache.tinkerpop.gremlin.driver.ser.GryoMessageSerializerV3d0, config: { serializeResultToString: true }}
  - { className: org.apache.tinkerpop.gremlin.driver.ser.GraphSONMessageSerializerV3d0, config: { ioRegistries: [org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistry] }}
  # Older serialization versions for backwards compatibility:
  - { className: org.apache.tinkerpop.gremlin.driver.ser.GryoMessageSerializerV1d0, config: { ioRegistries: [org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistry] }}
  - { className: org.apache.tinkerpop.gremlin.driver.ser.GryoLiteMessageSerializerV1d0, config: {ioRegistries: [org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistry] }}
  - { className: org.apache.tinkerpop.gremlin.driver.ser.GryoMessageSerializerV1d0, config: { serializeResultToString: true }}
  - { className: org.apache.tinkerpop.gremlin.driver.ser.GraphSONMessageSerializerGremlinV2d0, config: { ioRegistries: [org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistry] }}
  - { className: org.apache.tinkerpop.gremlin.driver.ser.GraphSONMessageSerializerGremlinV1d0, config: { ioRegistries: [org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistryV1d0] }}
  - { className: org.apache.tinkerpop.gremlin.driver.ser.GraphSONMessageSerializerV1d0, config: { ioRegistries: [org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistryV1d0] }}
processors:
  - { className: org.apache.tinkerpop.gremlin.server.op.session.SessionOpProcessor, config: { sessionTimeout: 28800000 }}
  - { className: org.apache.tinkerpop.gremlin.server.op.traversal.TraversalOpProcessor, config: { cacheExpirationTime: 600000, cacheMaxSize: 1000 }}
metrics: {
  consoleReporter: {enabled: true, interval: 180000},
  csvReporter: {enabled: true, interval: 180000, fileName: /tmp/gremlin-server-metrics.csv},
  jmxReporter: {enabled: true},
  slf4jReporter: {enabled: true, interval: 180000},
  gangliaReporter: {enabled: false, interval: 180000, addressingMode: MULTICAST},
  graphiteReporter: {enabled: false, interval: 180000}}
maxInitialLineLength: 40960
maxHeaderSize: 81920
maxChunkSize: 81920
maxContentLength: 6553600
maxAccumulationBufferComponents: 10240
resultIterationBatchSize: 6400
writeBufferLowWaterMark: 327680
writeBufferHighWaterMark: 655360

```

### gremlin-server.sh配置

### 

```sh
vim bin/gremlin-server.sh
```

#### 修改1：内存，JAVA_OPTIONS中的内存配置

```sh
# Set Java options
if [ "$JAVA_OPTIONS" = "" ] ; then
    JAVA_OPTIONS="-Xms32m -Xmx512m -javaagent:$LIB/jamm-0.3.0.jar -Dgremlin.io.kryoShimService=org.janusgraph.hadoop.serialize.JanusGraphKryoShimService"
fi

```

#### 修改2：DHADOOP_USER_NAME环境变量设置

hbase的访问用户必须有权限访问你配置的表，我们编辑脚本bin/gremlin-server.sh，找到如下内容添加用户的环境变量

-DHADOOP_USER_NAME=hdp_lbg_ecdata_dw

```sh
if [ "$1" = "-i" ]; then
  shift
  exec $JAVA -DHADOOP_USER_NAME=hdp_lbg_ecdata_dw -Djanusgraph.logdir="$JANUSGRAPH_LOGDIR" -Dlog4j.configuration=conf/gremlin-server/log4j-server.properties $JAVA_OPTIONS -cp $CP:$CLASSPATH org.apache.tinkerpop.gremlin.server.util.GremlinServerInstall "$@"
else
  ARGS="$@"
  if [ $# = 0 ] ; then
    ARGS="conf/gremlin-server/gremlin-server.yaml"
  fi
  exec $JAVA -DHADOOP_USER_NAME=hdp_lbg_ecdata_dw -Djanusgraph.logdir="$JANUSGRAPH_LOGDIR" -Dlog4j.configuration=conf/gremlin-server/log4j-server.properties $JAVA_OPTIONS -cp $CP:$CLASSPATH org.apache.tinkerpop.gremlin.server.GremlinServer $ARGS
fi

```

#### java代码环境变量设置

如果通过java代码访问，则需要设置

```
System.setProperty("HADOOP_USER_NAME","hdp_lbg_ecdata_dw");
```

### 启动gremlin server服务

```sh
bin/gremlin-server.sh conf/gremlin-server/online-gremlin-server.yaml 

4928 [gremlin-server-boss-1] INFO  org.apache.tinkerpop.gremlin.server.GremlinServer  - Gremlin Server configured with worker thread pool of 1, gremlin pool of 32 and boss thread pool of 1.
4928 [gremlin-server-boss-1] INFO  org.apache.tinkerpop.gremlin.server.GremlinServer  - Channel started at port 8182.

```

### 访问http服务

http://ecdata-janusgraph.58corp.com/?gremlin=g.V().next()

## IBM导入工具使用

克隆

```sh
git clone https://github.com/IBM/janusgraph-utils.git
cd janusgraph-utils/
```

### 修改

由于janusgraph-utils依赖的janusgraph版本过低，且有部分不满足我们需求，所以做了部分修改

#### pom修改

修改janusgraph-all版本，和httpclient版本

```xml
<dependency>
      <groupId>org.janusgraph</groupId>
      <artifactId>janusgraph-all</artifactId>
      <version>0.3.1</version>
      <exclusions>
        <exclusion>
          <artifactId>httpclient</artifactId>
          <groupId>org.apache.httpcomponents</groupId>
        </exclusion>
      </exclusions>
    </dependency>
    <!-- https://mvnrepository.com/artifact/org.apache.httpcomponents/httpclient -->

    <dependency>
      <groupId>org.apache.httpcomponents</groupId>
      <artifactId>httpclient</artifactId>
      <version>4.5.5</version>
    </dependency>
```

#### 修改代码com.ibm.janusgraph.utils.generator.GSONUtil 第104行

将node_id类型设置为String（原来为Intger）

> node_id唯一，为我们的节点全局唯一id，建立了唯一索引，导入边的时候需要通过这个node_id查询出节点，为了加快查询速度

```java
        //manually add node_id as a unique propertyKey and index
        PropertyKeyBean nodeIdKey = new PropertyKeyBean("node_id", "String");
```

#### 有几个jce的jar包可能下不下来，需要找能下下来的机器下载到本地仓库

### 编译

```sh
cd janusgraph-utils/
mvn package
```

### 使用

#### 编辑配置文件生成schema

```
vim csv-conf/user.json
```

```json
{
  "VertexTypes": [
    {
      "name": "User",
      "columns": {
        "userid": {"dataType":"String",  "composit":true, "mixedIndex":"search"},
        "biz_name": {"dataType":"String",  "composit":true, "mixedIndex":"search"},
        "areaname": {"dataType":"String",  "composit":true, "mixedIndex":"search"},
        "nick_name": {"dataType":"String",  "composit":true, "mixedIndex":"search"},
        "level": {"dataType":"String",  "composit":true, "mixedIndex":"search"},
        "user_type": {"dataType":"String",  "composit":true, "mixedIndex":"search"},
        "alleffect": {"dataType":"Float",  "composit":true, "mixedIndex":"search"},
        "degree": {"dataType":"Float",  "composit":true, "mixedIndex":"search"},
        "indegree": {"dataType":"Float",  "composit":true, "mixedIndex":"search"},
        "outdegree": {"dataType":"Float",  "composit":true, "mixedIndex":"search"},
        "comment_degree": {"dataType":"Float",  "composit":true, "mixedIndex":"search"},
        "comment_indegree": {"dataType":"Float",  "composit":true, "mixedIndex":"search"},
        "comment_outdegree": {"dataType":"Float",  "composit":true, "mixedIndex":"search"},
        "like_degree": {"dataType":"Float",  "composit":true, "mixedIndex":"search"},
        "like_indegree": {"dataType":"Float",  "composit":true, "mixedIndex":"search"},
        "like_outdegree": {"dataType":"Float",  "composit":true, "mixedIndex":"search"},
        "follow_degree": {"dataType":"Float",  "composit":true, "mixedIndex":"search"},
        "follow_indegree": {"dataType":"Float",  "composit":true, "mixedIndex":"search"},
        "follow_outdegree": {"dataType":"Float",  "composit":true, "mixedIndex":"search"},
        "betweenness": {"dataType":"Float",  "composit":true, "mixedIndex":"search"},
        "closeness": {"dataType":"Float",  "composit":true, "mixedIndex":"search"},
        "shuafen_card": {"dataType":"String",  "composit":true, "mixedIndex":"search"},
        "xifen_card": {"dataType":"String",  "composit":true, "mixedIndex":"search"},
        "zhongjie_card": {"dataType":"String",  "composit":true, "mixedIndex":"search"},
        "jiazhi_card": {"dataType":"String",  "composit":true, "mixedIndex":"search"},
        "dt": {"dataType":"String",  "composit":true, "mixedIndex":"search"}
      },
      "row": 10
    }
  ],
  "EdgeTypes": [
    {
      "name": "Follow",
      "multiplicity": "SIMPLE",
      "selfRef": false,
      "columns": {
        "values": {
          "dataType":"Float","composit":true
        }
      },
      "relations": [
        {"left": "User", "right": "User", "row": 10, "selfRef":false }
      ]
    },
    {
      "name": "Comment",
      "multiplicity": "SIMPLE",
      "columns": {
        "values": {
          "dataType":"Float","composit":true
        }
      },
      "relations": [
        {"left": "User", "right": "User", "row": 10 }
      ]
    },
    {
      "name": "Like",
      "multiplicity": "SIMPLE",
      "columns": {
        "values": {
          "dataType":"Float","composit":true
        }
      },
      "relations": [
        {"left": "User", "right": "User", "row": 10 }
      ]
    },
    {
      "name": "All",
      "multiplicity": "SIMPLE",
      "columns": {
        "values": {
          "dataType":"Float","composit":true
        }
      },
      "relations": [
        {"left": "User", "right": "User", "row": 10 }
      ]
    }
  ]
}
```

#### 生成CSV文件模板

```
mkdir ./user
sh run.sh gencsv csv-conf/user.json ./user
```

#### 查看模板并将CSV文件替换为要导入的文件，保留json文件

```sh
(base) [work(lisenmiao)@tjtx-87-74 janusgraph-utils]$ ls ./user
datamapper.json  schema.json  User_All_User_edges.csv  User_Comment_User_edges.csv  User.csv  User_Follow_User_edges.csv  User_Like_User_edges.csv

```

#### 导入CSV文件

> 这里指定刚刚生成的文件夹和json配置文件
>
> 同时指定了online-janusgraph-hbase-es.properties配置文件位置

```sh
sh run.sh import /opt/soft/janusgraph-0.3.1-hadoop2/conf/gremlin-server/online-janusgraph-hbase-es.properties \
    ./user ./user/schema.json ./user/datamapper.json
```

## 遇到的问题：

### 试图自定义节点的id

因为想先导入节点的，再导入边而不需要查询节点，直接指定节点id

编辑配置文件`conf/gremlin-server/online-janusgraph-hbase-es.properties`

```properties
#是否自定义ID, 默认false
graph.set-vertex-id=true
```

发现必须手动指定节点id，但是id不能为字符串类型，节点的值长度不能太长，长度不是Long类型的值，而且指定之后，janusgraph又作为种子重新生成了id

### 试图通过janusgraph-server服务导入数据

通过http请求导入，超级慢

### and多条件查询 的使用？？？

原本以为多条件是写多个has，像这样

```cql
g.V().has('dt','20200309').has('shuafen_card','刷粉用户').limit(50)
```

结果发现查询出来的数据量根本不对，或者是0条数据，于是在第一个has后面写了limit，后面的结果也受limit数字的影响，将刷粉用户的条件写在了前面，这样才能才对，原因未知

```cql
g.V().has('shuafen_card','刷粉用户').limit(500).has('dt','20200309').limit(50)
```



### 索引的使用？？？

查询**一定要用到索引**，否则慢到你怀疑人生

由于20200309这天没有刷分用户，导致nodes为空列表，运算后面的边的时候，就会使用全量数据而不用索引，原因未知

本来是这样的查询，巨卡

```cql
nodes = g.V().has('shuafen_card','刷粉用户').limit(500).has('dt','20200309').limit(50).toList();edges = g.V(nodes).aggregate('node').outE().as('edge').inV().where(within('node')).select('edge').toList();[nodes,edges]
```

于是做个判断，nodes为空，就返回

```cql
nodes = g.V().has('shuafen_card','刷粉用户').limit(500).has('dt','20200309').limit(50).toList();if(nodes.size()==0){[[],[]]}else{edges = g.V(nodes).aggregate('node').outE().as('edge').inV().where(within('node')).select('edge').toList();[nodes,edges]}
```

