# ClickHouse

### 各种存储引擎

https://www.cnblogs.com/huanghanyu/p/12877194.html

####  MergeTree引擎

MergeTree引擎及其家族（*MergeTree）的其他引擎是ClickHouse健壮性最强的表引擎。 MergeTree主要特性：

- 存储按主键排序的数据 允许创建一个小的稀疏索引来帮助快速查询数据
- 允许在指定分区键时使用分区
- 支持数据复制
- 支持数据采样

MergeTree家族的引擎需要使用Date类型的列来指定分区。

PARTITION BY——指点分区字段（可选）。对于按月分区的表，日期字段需格式化为“YYYYMM”，转换函数：toYYYYMM(date_column)。通常**该字段需为Date类型**。 ORDER BY——指定排序字段（可选），tuple类型。例如：ORDER BY (CounterID, EventDate) PRIMARY KEY——指定主键字段（可选）。通常主键默认和排序（ORDER BY）字段相同，不需另外指定。 SAMPLE BY——抽样的表达式（可选），表达式（列）必须被主键包含，例如：SAMPLE BY intHash32(UserID) ORDER BY (CounterID, EventDate, intHash32(UserID)) SETTINGS——控制合并树的其他参数设置（可选）。index_granularity——索引的粒度。索引“标记”之间的数据行数。默认值：8192。

```
ENGINE = MergeTree(PARTITION column, (ORDER BY column), 8192);
```

### ReplacingMergeTree引擎

继承自MergeTree。ReplacingMergeTree与MergeTree的不同之处在于，它删除具有相同主键值的重复条目。重复数据删除仅在合并期间发生，并且合并发生在一个未知的时间。因此，ReplacingMergeTree适合清除后台的重复数据以节省空间，但并不保证没有重复数据。

> ENGINE = ReplacingMergeTree([ver]) [PARTITION BY expr] [ORDER BY expr] [SAMPLE BY expr] [SETTINGS name=value, ...] 说明：ver——列版本，输入类型UInt*、Date或DateTime，可选参数。其他参照MergeTree参数说明。

### SummingMergeTree引擎

继承自MergeTree。不同之处在于，当合并SummingMergeTree表的数据部分时，ClickHouse将所有具有相同主键的行替换为一行，该行包含数字数据类型的列的汇总值。如果主键的组合方式是单个键值对应于大量行，这将显著减少存储容量并加快数据选择。

> ENGINE = SummingMergeTree() [PARTITION BY expr] [ORDER BY expr] [SAMPLE BY expr] [SETTINGS name=value, ...]

体现在select的时候使用sum(columns )和group by 。columns 必须是数字类型，并且不能在主键中。如果不指定columns,ClickHouse用非主键的数字数据类型总结了所有列中的值。

### AggregatingMergeTree引擎

继承自MergeTree，改变了数据部件合并的逻辑。ClickHouse用存储聚合函数状态组合的单个行(在一个数据部分内)替换所有具有相同主键的行。

> ENGINE = AggregatingMergeTree() [PARTITION BY expr] [ORDER BY expr] [SAMPLE BY expr] [SETTINGS name=value, ...]

物化视图示例：

> CREATE MATERIALIZED VIEW test.basic ENGINE = AggregatingMergeTree() PARTITION BY toYYYYMM(StartDate) ORDER BY (CounterID, StartDate) AS SELECT CounterID, StartDate, sumState(Sign) AS Visits, uniqState(UserID) AS Users FROM test.visits GROUP BY CounterID, StartDate; 说明：写入数据到test.visits表 时，同时生产聚合写入到视图。可以通过如下语句查询聚合结果： SELECT StartDate, sumMerge(Visits) AS Visits, uniqMerge(Users) AS Users FROM test.basic GROUP BY StartDate ORDER BY StartDate;

### CollapsingMergeTree引擎

承自MergeTree，将行瓦解/折叠的逻辑添加到数据部件合并算法中。如果一行中的所有字段都是等效的，除具有1和-1值的特定字段符号外，则会异步删除(瓦解/折叠)行等效字段。该引擎可以显著减少存储容量，提高SELECT查询的效率。

> ENGINE = CollapsingMergeTree(sign) [PARTITION BY expr] [ORDER BY expr] [SAMPLE BY expr] [SETTINGS name=value, ...] 说明：sign——如果Sign = 1表示行是对象的状态，我们称之为“state”行。如果符号= -1表示取消具有相同属性的对象的状态，我们称之为“cancel”行。列数据类型- Int8。

> 例如数据： ┌──────────────UserID─┬─PageViews─┬─Duration─┬─Sign─┐ │ 4324182021466249494 │ 5 │ 146 │ 1 │ │ 4324182021466249494 │ 5 │ 146 │ -1 │ │ 4324182021466249494 │ 6 │ 185 │ 1 │ └─────────────────────┴───────────┴──────────┴──────┘
>
> - 表结构如下： CREATE TABLE UAct ( UserID UInt64, PageViews UInt8, Duration UInt8, Sign Int8 ) ENGINE = CollapsingMergeTree(Sign) ORDER BY UserID
> - 我们可以通过聚合获取结果： SELECT UserID, sum(PageViews * Sign) AS PageViews, sum(Duration * Sign) AS Duration FROM UAct GROUP BY UserID HAVING sum(Sign) > 0 ┌──────────────UserID─┬─PageViews─┬─Duration─┐ │ 4324182021466249494 │ 6 │ 185 │ └─────────────────────┴───────────┴──────────┘
> - 如果不需要聚合并希望强制折叠，可以FINAL修饰符。 SELECT * FROM UAct FINAL ┌──────────────UserID─┬─PageViews─┬─Duration─┬─Sign─┐ │ 4324182021466249494 │ 6 │ 185 │ 1 │ └─────────────────────┴───────────┴──────────┴──────┘

### VersionedCollapsingMergeTree引擎

承自MergeTree，并将行折叠的逻辑添加到数据部件合并算法中。VersionedCollapsingMergeTree解决了与CollapsingMergeTree相同的问题，但是使用了另一种折叠算法。它允许使用多个线程以任意顺序插入数据。特定的Version列有助于正确地折叠行，即使它们以错误的顺序插入。折叠合并树只允许严格连续的插入。

- 允许快速写入不断变化的对象状态。
- 删除后台对象的旧状态。它会导致存储容量的显著减少。

### Log引擎家族

这些引擎是为需要使用少量数据(少于100万行)编写许多表的场景而开发的。

### 复制表 & 复制表引擎

#### ReplicatedMergeTree

复制表用于在不同服务器上存储数据的多个副本, 严重依赖Zookeeper(ZooKeeper 3.4.5+)，不同的Zookeeper路径允许支持不同的复制拓扑。由于很难为每个节点的每个表创建自定义路径，因此ClickHouse提供了宏替换机制。宏在每个节点的配置文件中定义（为此目的使用单独的文件是有意义的，例如/etc/clickhouseserver/macros.xml）并在大括号中引用。对于复制表，宏参与两个地方：

- Zookeeper中表的znode的路径
- 副本名称具有相同ZooKeeper路径的表将是特定数据分片的副本。插入可以转到任何副本，ClickHouse接管复制以确保所有副本处于一致状态。插入不强制一致性，复制是异步的。可以使用不同的Zookeper路径模拟不同的复制拓扑。

例如宏配置如下所示：

> <macros> <layer>05</layer> <shard>02</shard> <replica>xxxx</replica> </macros>

在此示例中定义了3个宏： {layer} - ClickHouse集群的昵称，用于区分不同集群之间的数据。 {shard} - 分片编号或符号引用 {replica} - 副本的名称，通常与主机名匹配

这样在创建复制表时就可以直接使用宏定义而不用在不同的服务器创建表时都要修改对应的zk路径和副本（{replica}）标识，例如：

> ENGINE = ReplicatedMergeTree('/clickhouse/tables/{layer}-{shard}/<tablename_of_node_in_zk>','{replica}', <date_partition_column>, (sort columns), 8192)

### 分布式表 & Distributed引擎

分布式表用于使用单个表接口访问位于不同服务器的表（数据分片）。分布式表由“分布式”引擎定义，实际上是分片表上的接口。建议进行客户端分割，并将数据插入ClickHouse节点上的本地切分表中。但是，也可以直接插入到分布式表中。 在一个ClickHouse系统中，可以使用不同的集群，例如，有些表可以不复制地分片，有些表可以复制，等等。还可以在“子集群”中存储一些数据，但可以通过全局分布式表访问它们。

将分布式表视为一个接口更容易。建议进行客户端分段并将数据插入到ClickHouse节点上的本地分片表中。但是，也可以直接插入分布式表中，ClickHouse使用分片键顶部的哈希函数分发数据。

集群配置可以动态更新。因此，如果向分布式表中添加新节点，则不需要重新启动服务器。如果需要将单个节点表扩展到多个服务器，则过程如下：

1. 在新服务器上创建分片表
2. 必要时重新分发数据（手动更好，但ClickHouse也支持重新分片）
3. 定义集群配置并将其放在每个ClickHouse节点上
4. 创建分布式表以访问多个分片中的数据

> ENGINE = Distributed(<cluster>, <database>, <shard table> [, sharding_key])

### MySQL 引擎

对存储在远程 MySQL 服务器上的数据执行 `SELECT` 查询。

###  Kafka引擎

ClickHouse结合Kafka使用。

谓词下推（predicate push down）





# MergeTree原理详解之索引

https://mp.weixin.qq.com/s/v03LEORF8IUHL2JspxoLvQ

在ClickHouse众多的表引擎中，MergeTree引擎最为强大，在生产环境中的绝大多数场景都会使用此系列的表引擎。

值得注意的是**只有MergeTree系列的表引擎才支持主键索引，数据分区，数据副本，数据采样这样的特性**，只有此系列的表引擎才支持ALTER操作。

MergeTree表引擎在写入一批数据的时候，数据总会以数据片段的形式写入磁盘，并且数据片段不可修改。为了避免片段过多，clickhouse会通过后台的的线程，定期合并这些数据片段，属于不同分区的数据片段会被合并成一个新的片段，这就是MergeTree的基本特点。

MergeTree家族中还有其他表引擎是在它的基础上进行扩展，例如ReplacingMergeTree表引擎具有删除重复数据的特性；SummingMergeTree表引擎则会按照排序键自动聚合数据。加上Replicated前缀，又会得到一组支持数据副本的表引擎，例如ReplicatedMergeTree、ReplicatedReplacingMergeTRee、ReplicatedSummingMergeTree等。可以看出MergeTree是根基，只有吃透了MergeTree表引擎的原理，就能掌握该系列引擎的精髓。

下面将开启我们MergeTree原理详解系列的第一篇索引

## 表引擎语法结构

```
CREATE TABLE [IF NOT EXISTS] [db.]table_name [ON CLUSTER cluster]
(
    name1 [type1] [DEFAULT|MATERIALIZED|ALIAS expr1],
    name2 [type2] [DEFAULT|MATERIALIZED|ALIAS expr2],
    ...
    INDEX index_name1 expr1 TYPE type1(...) GRANULARITY value1,
    INDEX index_name2 expr2 TYPE type2(...) GRANULARITY value2
) ENGINE = MergeTree()
[PARTITION BY expr]
[ORDER BY expr]
[PRIMARY KEY expr]
[SAMPLE BY expr]
[SETTINGS name=value, ...]
```

代码解释：

1.ENGINE：创建MergeTree的表引擎指定ENGINE=MergeTree()2.ORDER BY语句：排序键，用于指定在一个数据片段内数据以何种标准排序，**默认情况下是主键与排序键相同**。排序键可以指定单个列字段，也可以多个列字段。3.PARTITION BY：分区键，用于指定表数据以何种标准进行分区。分区键可以指定单个列字段，也可以是通过元祖形式使用使用多个列字段，还可以支持使用列表达式。**若不声明分区键则clickhouse会生成一个名为all的分区**。合理使用分区可以有效减少查询数据文件的扫描范围。4.PRIMARY KEY：主键，声明后会按照主键字段生成一级索引，用于加速表查询(**这里使用的是稀疏索引，在后面会讲到**)。默认情况下主键与排序键相同。所以在一般情况下，我们不用可以去通过PRIMARY KEY声明，直接使用ORDER BY指定主键。在一般情况下，单个数据片段内，数据与一级索引以相同的规则升序排列。MergeTree主键允许存在重复数据(可以用ReplacingMergeTree去重)5.SAMPLE BY：抽样表达式，用于声明数据以何种标准进行采样。若使用了此配置选项则在主键的配置中也需要声明同样的表达式。抽样表达式需要配合SAMPLE BY子查询使用，这项功能对于选取抽样数据十分有用。6.TTL：指定表级别的数据存活策略。7.SETTINGS：配置项：index_granularity:默认8192，表示索引的粒度，即MergeTree的索引在默认情况下，每间隔8192行才生成一个索引。通常不需要修改此参数。index_granularity_bytes:默认10MB(10**1024**1024)表示自适应间隔大小的特性，即根据每一批写入数据的体量大小，动态划分间隔大小。设置为0表示不启用自适应功能。

## MergeTree的存储结构

MergeTree表引擎中的数据拥有物理存储，数据会按照分区目录的形式存储到磁盘上，物理结构如下：![图片](https://mmbiz.qpic.cn/mmbiz_png/DElzlJBianyW9PUznpaYfgbZK4Bz4rVa4UTicTjhI7gl7MHs8GUnia0TrF6s9ZCwKhibFgkMeupiadPRbGnQFlKyZ5Q/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

解释：

1.parition:分区目录，partition_n目录下的各类数据文件都是以分区形式被组织存放的，属于相同分区的数据最终会被合并到一个分区目录内。2.checksums.txt:校验文件，使用二进制存储，保存了各类文件的size大小和size的哈希值，用于快速校验文件的完整性和正确性。3.columns.txt:列信息文件，使用文本文件存储，用于保存分区下的列字段信息，例如：

```
$ cat columns.txt
columns format version: 1
4 columns:
'ID' String
'URL' String
'Code' String
'EventTime' Date
```

4.count.txt:计数文件，文本文件存储，用于记录当前数据分区目录下数据的总行数。

```
$ cat count.txt 
8
```

5.primary.idx:索引文件，使用二进制格式存储。用于存放稀疏索引，一张MergeTree表只能声明一次一级索引(通过order by或者primary key)。借助稀疏索引在数据查询的时候能够排除主键范围之外的数据文件，从而减少数据扫描范围，加速查询速度。6.[column].bin:数据文件，使用压缩格式存储，默认使用lz4压缩格式，用于存储某一列的数据。由于MergeTree采用列式存储，每个列字段都有独立的bin数据文件，并以列字段命名。7.[column].mrk:列字段标记，使用二进制格式存储。标记文件中保存了bin文件中数据的偏移量信息，标记文件与稀疏文件对齐，又与bin文件一一对应，所以MergeTree通过标记文件建立了primary.idx稀疏索引与bin数据文件的映射关系。映射步骤下：首先通过primary.idx找到对应数据的偏移量信息(.mrk)，再通过偏移量直接从bin文件中读取数据。由于.mrk标记文件与.bin文件一一对应，所以MergeTree中的每个列字段都会拥有与其对应的.mrk文件。8.[column].mrk2:如使用了自适应大小的索引间隔，则标记文件会以.mrk2命名。工作原理和作用和.mrk标记文件相同。9.partition.dat和minmax_[column].idx:若使用了分区键则会额外生成partition.dat和minmax索引文件，均使用二进制格式存储。partition.dat用于保存当前分区下分区表达式最终生成值，minmax索引文件用于记录当前分区字段对应原始数据的最小值和最大值。在分区索引作用下，进行数据查询时候能够快速跳过不必要的数据分区目录，从而减少最终需要扫描的数据范围。10.skp_idx_[column].idx和skp_idx_[column].mrk:若在建表语句中声明了二级索引则会额外生成相应的二级索引与标记文件，它们同样用二进制存储。二级索引在clickhouse中又称之为**跳数索引** ，目前拥有minmax,set,ngrambf_v1和tokenbf_v1四种类型。这些索引的目标和一级稀疏索引相同，为了进一步减少所需要扫描的数据范围，以加速整个查询过程。
数据分区



在MergeTree中，数据是以分区目录的形式进行组织的，每个分区独立分开存储。借助这种形式，在对MergeTree进行数据查询时，可以有效跳过无用的数据文件，只使用最小的分区目录子集。

### 分区规则

MergeTree数据分区规则由ID决定，而具体到每个数据分区所对应的ID则是由分区键的取值决定的，分区键支持使用任何一个或者一组字段表达式声明，其业务语义可以是年月日或者组织单位等任何一种规则，针对取值数据类型的不同，分区ID有四种规则：

1.**不指定分区键**：不使用partition by 声明任何分区表达式，则分区ID默认取名为all，所有数据写入all分区。2.**使用整型**：若分区键取值属于整型（兼容Uint64包含有符号整型和无符号整型）且无法转换为日期类型YYYYMMDD格式，则直接按照整型的字符串形式输出，作为分区ID的取值。3.**使用日期类型**：若分区键取值属于日期类型，或者可以转为YYYYMMDD格式的整型则按照使用YYYYMMDD进行格式化后输出，并作为分区ID的取值。4.**使用其他类型**：若分区键取值不属于整型或者日期类型，如String，float则通过128位的Hash算法取其Hash值作为分区ID的取值。数据在写入时，会对照分区ID落入相应的数据分区。

### 分区目录命名规则

通过上面我们已经知道分区ID的生成规则。但是我们实际看到的分区目录可能是这样的202012_1_1_0。那这又是啥意思呢？一个完整分区目录的命名公式如下： **PartitionID_MinBlockNum_MaxBlockNum_Level**

1.PartitionID:分区ID。上面说过的2.MinBlockNum和MaxBlockNum:最小数据块编码和最大数据块编码(**这里的数据块编号很容易和下面介绍的压缩数据块混淆，它们没有任何关系**)，这里的BlockNum是一个整型的自增长编号。一个MergeTree表，在内部全局累加，从1开始每当新创建一个分区目录，计数器就加1，**对一个新分区MinBlockNum和MaxBlockNum一样**，当分区目录发生合并的时候，新产生的合并目录MinBlockNum和MaxBlockNum有另外的取值规则，下面会讲到。3.Level:合并的层级，也可以理解为某个分区被合并过的次数，数值越大则合并的次数越多。对于一个新创建的分区目录，初始值是0，从此以分区为单位若相同分区发生合并动作，则在相应分区内计数器加1。

示例：202007_1_1_0

202007_3_3_0

202007_1_3_1

202008_2_2_0

### 分区目录的合并过程

1.MergeTree的分区目录是在数据写入过程中被创建的。也就是一张新建的表，如果没有任何数据，那么也不会有任何的分区存在。

2.**MergeTree的分区目录伴随着每一批数据的写入(一次insert语句)，mergetree都会生成一批新的分区目录，**即便不同批次写入的数据属于相同分区，也会生成不同的分区目录**。**

3.每次insert都会产生一个分区，那么就存在多个相同分区的情况，clickhouse会通过后台任务再将相同分区的多个目录合并成一个新的目录(写入10-15分钟，也可以手动执行optimize查询语句)。**已经存在的旧目录并不会立即被删除，而是在之后的某个时刻通过后台被删除(默认8分钟)**。

4.同属于一个分区的多个目录，在合并之后会形成一个全新的目录，目录中的索引和数据文件也会相应的进行合并。新目录名称的合并规则如下：MinBlockNum:取同一分区内所有目录中最小的MinBlockNum值 MaxBlockNum:取同一分区内所有目录中最大的MaxBlockNum值 Level:取同一分区内最大Level值并加1![图片](https://mmbiz.qpic.cn/mmbiz_png/DElzlJBianyW9PUznpaYfgbZK4Bz4rVa4kxiaZMDB6qtWxOUtA1P4ZnwNar72CmKR6CEXsibWdCCMBhaCFibylzOyQ/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

### 一级索引

MergeTree的主键使用primary key定义，待主键定义之后，MergeTree会依据index_granularity间隔(默认8192行)，为数据表生成一级并保存至primary.idx文件内，索引数据按照primary key排序，相比使用primary key定义，更为常见的是通过order by指代主键。在此情况下，primary key和order by定义相同，索引文件primary.idx和数据.bin会按照完全相同的规则排序。

**注意：order by和primary key定义有差异的场景是SummingMergeTree引擎**

### 稀疏索引

primary.idx文件内的一级索引采用稀疏索引实现。在稀疏索引中每一行索引标记对应的是一段数据，而不是一行。这样做的好处就是只使用少量的索引标记就能够记录大量的数据，数据量越大这种优势就越明显。**默认的索引粒度是8192行，也就是说ClickHouse只需要12208行索引标记就能为1亿行数剧记录提供索引**。稀疏索引占用空间小，**所以primary.idx内的索引会保存在内存中**，速度极快。就像这样：![图片](https://mmbiz.qpic.cn/mmbiz_png/DElzlJBianyW9PUznpaYfgbZK4Bz4rVa41Ek1DIekkynNwfGct7ic4p4ciczMNShumZwvVpxj6oBbjlS80aGBQPDw/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

### 索引粒度

我们前面已多次讲到index_granularity这个参数，它表示索引粒度。索引粒度对MergeTree而言是一个非常重要的概念。数据以index_granularity(8192)标记每隔多少行产生1个索引。**MergeTree使用MarkRange表示一个具体的区间，并通过start和end表示其具体的范围**。index_granularity不但作用于一级索引还会引响标记文件和数据文件。**因为只有一级索引文件是无法完成查询工作的，需要借助标记来定位数据**，所以一级索引和数据标记的间隔粒度相同，彼此对齐，而数据文件也会按照index_granularity的间隔粒度生成压缩数据块，后面会具体讲到。

### 索引生成规则

由于是稀疏索引，所以MergeTree需要间隔index_granularity行数据才会生成一条索引记录，其索引值会依据声明的主键字段获取。如图：![图片](https://mmbiz.qpic.cn/mmbiz_png/DElzlJBianyW9PUznpaYfgbZK4Bz4rVa4abnZ2B5N1wOc3Kwo2bicNBfVflvQqZkYLj11SCuyQMAsMfTicJZCVptQ/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)从上图可以看出，第0行userId为101，第8192行userId取值1688，第16384行，userId取值3266，最终索引数据将会是10116883842。这样的稀疏索引存储是非常紧凑的，索引值前后相连。可见ClickHouse对于每一处细节都拿捏的非常到位，不浪费任何一个字节空间。如果有多个主键则会进行拼接，如图![图片](https://mmbiz.qpic.cn/mmbiz_png/DElzlJBianyW9PUznpaYfgbZK4Bz4rVa4XaWapBGYfAnxCdibtMC4Pz7oKvibj4o5GMAPj57ZTqeIf4kRDjWHeQtg/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

索引值最终会被写入primary.idx文件进行保存。

### MarkRange

MarkRange在ClickHouse中是用于标记区间的对象。前面介绍到ClickHouse会按照index_granularity的间隔粒度，将一段完整的数据划分成多个小的间隔数据段，那么一个数据段就对应一个MarkRange，MarkRange和索引编号对应，使用start和end两个属性表示其区间范围。简单举个例子：假设现在有一份测试数据，共192行记录。主键ID为string类型，从U000开始，依次为U001,U002直到U192。index_granularity设置为3，根据索引生成规则，primary.idx文件中就会有如下内容：![图片](https://mmbiz.qpic.cn/mmbiz_png/DElzlJBianyW9PUznpaYfgbZK4Bz4rVa4Os23l8IJ4ujNysGJqZwQELgk8QA8yEkFibWhjju3Ldicevqj3kdHQcvg/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)解释：192行的数据记录，根据index_granularity=3会分为64个小的MarkRange，两个相邻的MarkRange相距的步长为1，最大的MarkRange区间为[U000,+inf)表示所有数据。如图所示：![图片](https://mmbiz.qpic.cn/mmbiz_png/DElzlJBianyW9PUznpaYfgbZK4Bz4rVa4ibJ7Y8Aa3xVR3WSibT5KqCmVTDW6ibib0efIjEMdCOleWqibzvibF2uI4MIQ/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

### 索引查询流程

在了解了索引的粒度，生成规则，MarkRange等等一些概念后，接下来讲讲ClickHouse是怎么运用这一套完成整个索引的查询过程。索引查询其实就是两个数值区间的交集判断。其中，一个区间是由基于主键的查询条件转换而来的条件区间；而另一个区间是刚才所讲述的与MarkRange对应的数值区间。整个索引查询过程大致可以分为3个步骤：(1)将查询条件转为条件区间：

```
WHERE ID = 'U001'
['U001','U001']

WHERE ID &gt; 'U000'
['U000',+inf)

WHERE ID LIKE 'U003'
['U003','U004')

WHERE ID &lt; 'U189'
(-inf,'U189')
```

(2)递归交集判断:以递归的形式，依次对MarkRange的数值区间与条件区间做交集判断。从最大的[U000,+inf]开始：

•如果不存在交集，则直接通过剪枝算法优化此整段MarkRange。•如果存在交集，且MarkRange步长大于8(end-start)，则将此区间进一步拆分成8个子区间(由merge_tree_coarse_index_granularity指定，默认是8)，并重复此规则，继续做递归交集判断。•如果存在交集，且MarkRange不可再分解(步长小于8)，则记录MarkRange并返回。(3)合并MarkRange区间:将最终匹配的MarkRange聚在一起，合并它们的范围。

以查询WHERE ID='U003'为例，最终只需要读取[U000,U003]和[U003,U006]两个区间的数据，它们对应MarkRange(start:0,end:2)范围，其它无用的区间都被裁减掉。可能有人会问，这里为什么是两个区间，因为MarkRange转换的数值区间是闭区间，所以会额外匹配到邻近的一个区间。

### 二级索引

MergeTree支持二级索引，也叫做跳数索引，是由数据的聚合信息构建而成。根据索引类型的不同，其聚合信息的内容也不同，但它的目的和一级索引是一致的，都是为了帮助查询时减少数据扫描范围。

在旧版本中跳数索引默认是关闭的，需要设置allow_experimental_data_skipping_indices=1(新版本中已经取消)。二级索引需要在create语句中定义，支持用元组和表达式的形式声明，其完整的定义语法如下所示：

```
INDEX index_name expr TYPE index_type(...) GRANULARITY granularity
```

和一级索引一样，若在创建表语句中声明了二级索引则会生成相应的索引与标记文件(skpi_idx_[Column].idx与skip_idx_[Column].mrk)

### granularity与index_granularity关系

不同的二级索引之间，除了他们自身独有的参数之外，还都共同拥有granularity参数。granularity定义了一行跳数索引能够跳过多少个index_granularity区间的数据。以minmax为例如下图所示![图片](https://mmbiz.qpic.cn/mmbiz_png/DElzlJBianyW9PUznpaYfgbZK4Bz4rVa4EwCTV3RIcOpibrk3vXwdkh7icXe125YzSRSJZdLCmj2cUrVxIibMapRQA/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)MergeTree从第0段分区开始，依次获取聚合信息。当获取到第3个分区时(granularity=3)，则汇总并会生成第一行minmax索引(前3段minmax极值汇总后取值为[1,9])。

### 跳数索引的类型

目前跳数支持四种跳数索引，分别是minmax,set,ngrambf_v1,tokenbf_v1。一张表支持同时声明多个跳数索引。

### minmax

minmax索引记录了一段数据内的最小值和最大值，其索引的作用类似于分区目录的minmax索引，能够快速跳过无用的数据区间。granularity=5表示计算涉及5个index_granularity区间的数据

### set

set索引直接记录了声明字段或表达式的取值(唯一值，无重复)，其完整形式为set(max_rows)，其中的max_rows是一个阈值，表示在一个index_granularity内，索引最多记录的数据行数。若max_rows=0表示无限制。

例如:ix_length length(id) * 8 TYPE set(100) GRANULARITY 5表示set索引值会取ID的长度乘以8当作唯一值，每个索引内最多有100条记录

### ngrambf_v1

ngrambf_v1(n, size_of_bloom_filter_in_bytes, number_of_hash_functions, random_seed)

存储一个包含数据块中所有 n元短语（ngram） 的 布隆过滤器 。只可用在字符串上。可用于优化 equals ， like ，in，notIn，equals，notEquals 的性能。

n：短语长度，依据n的长度将数据切割为token短语。size_of_bloom_filter_in_bytes：布隆过滤器大小，单位字节。number_of_hash_functions：布隆过滤器中使用的哈希函数的个数 random_seed：布隆过滤器的随机种子。

```
INDEX c(ID, Code) TYPE ngrambf_v1(3, 256, 2, 0) GRANULARITY 5
```

### tokenbf_v1

tokenbf_v1(size_of_bloom_filter_in_bytes, number_of_hash_functions, random_seed)

它的作用跟ngrambf_v1类似，同样也是一种布隆过滤器索引，不同的是它会自动按照非字符的、数字的字符串分割token。

```
INDEX d ID TYPE tokenbf_v1(256, 2, 0) GRANULARITY 5
```

当我们定义了跳数索引，它会在我们前面讲到的通过主键索引筛选出来粗糙的MarkRange的基础上使用跳数索引进一步过滤。

# ClickHouse bitmap 及QA

https://new.qq.com/omn/20200806/20200806A0PQ7X00.html

在 ClickHouse 提供的聚合函数中，有一种是 groupBitmap 函数，它可以提供一个位图，我们要做的就是将数据聚合到这个位图中。

### 建表示例

```sql
-- 高表：分片表 以label_name分区和排序
CREATE TABLE data_usergroup.user_label_string_20210315 
(`label_name` String COMMENT '标签类型', `label_value` String COMMENT '标签值', `mapping_id` UInt64 COMMENT '标签对应唯一数值型id') 
ENGINE = ReplicatedMergeTree('/clickhouse/tables/portrait/data_usergroup/user_label_string_20210315/{dw_shard_two}', '{dw_replica}') 
PARTITION BY label_name ORDER BY label_name SETTINGS index_granularity = 81920

-- 高表：分布式表
CREATE TABLE data_usergroup.user_label_string 
(`label_name` String COMMENT '标签类型', `label_value` String COMMENT '标签值', `mapping_id` UInt64 COMMENT '标签对应唯一数值型id') 
ENGINE = Distributed(hdp_lbg_ecdata_dw_cluster_two, data_usergroup, user_label_string_20210315, rand())
-- bitmap表：分片表。以label_name分区，以label_name, label_value为主键
CREATE TABLE data_usergroup.user_label_string_bitmap_20210315 
(`label_name` String, `label_value` String, `state` AggregateFunction(groupBitmap, UInt64)) 
ENGINE = ReplicatedAggregatingMergeTree('/clickhouse/tables/portrait/data_usergroup/user_label_string_bitmap_20210315/{dw_shard_two}', '{dw_replica}') 
PARTITION BY label_name ORDER BY (label_name, label_value) SETTINGS index_granularity = 128
-- bitmap表：分布式表
CREATE TABLE data_usergroup.user_label_string_bitmap 
(`label_name` String, `label_value` String, `state` AggregateFunction(groupBitmap, UInt64)) 
ENGINE = Distributed(hdp_lbg_ecdata_dw_cluster_two, data_usergroup, user_label_string_bitmap_20210315, rand())
```

>最开始不是两种方案吗,一种利用物化视图自动聚合,只不过我们的机器不给力,老出问题,所以放弃了这种方式
>
>
>
>要是采用物化视图需要build,每个标签手动聚合一次 或者一批标签聚合一次
>
>
>现在采用的是用sql对明细表汇总,把汇总的结果插入到bitmap表（insert into select xxx ）
>
>
>就不用单独在触发merge了

### 查询示例

查询的是shard表

```sql
SELECT
  sum(user_number) AS user_number
FROM
  data_usergroup.agent_table
  RIGHT JOIN (
    select
      'agent' AS agentname,
      ifNull(
        bitmapCardinality(bitmapAnd(bitmapAnd(a, b), c)),
        toUInt64(0)
      ) as user_number
    from
      (
        select
          1 as ida,
          groupBitmapMergeState(state) as a
        from
          data_usergroup.user_label_int_bitmap_20210315
        where
          label_name = 'age'
          and (label_value = 0)
      ) as a
      join (
        select
          1 as idb,
          groupBitmapMergeState(state) as b
        from
          data_usergroup.user_label_string_bitmap_20210315
        where
          label_name = 'province'
          and (label_value in('北京市'))
      ) as b on ida = idb
      join (
        select
          1 as idc,
          groupBitmapMergeState(state) as c
        from
          data_usergroup.user_label_int_bitmap_20210315
        where
          label_name = 'gender'
          and (label_value = 1)
      ) as c on ida = idc
  ) USING (agentname)
```



### **Q&A**

#### **Q：ClickHouse的优缺点？**

**A：**ClickHouse 是一个技术相对简单，对于 Hadoop 的依赖比较小，目前就是依赖Zookeeper 进行数据容灾方面的依赖。它的性能非常强悍，底层是用 C++ 实现的，我们知道C++ 是编译型语言，没有像“Hadoop”大量用“JAVA”中间使用虚拟机从而带来的性能损耗。

第二方面 ClickHouse 整个技术用得比较激进，各种先进的技术都会快速的拿来验证，如果适用就继续用、如果不适用就快速的换，社区更新、迭代得比较快，所以发版频率非常高。

整个使用门槛非常低，支持 SQL ，虽然它的 SQL 有些特别，但是很容易理解，数据分析人员或者是没有特别多的开发背景，学习成本很低。

它的缺点是整个社区时间比较短，开源到现在时间并不长，积累使用经验并不多，随着云化后会积累大量的使用经验，也乐意把经验分享给大家，弥补这方面的不足。

#### **Q：自动故障转移切换时如何做？**

**A：**ClickHouse 是支持数据容灾的，可以配置多副本。整个数据级分为多个 Shard，每个 Shard 内部可以分多个副本，可以指定两副本、三副本，对不重要的数据指定一副本，通常用两副本用磁盘做 RAID-50 就足够了，如果出现一个副本所在机器不可用，其他的副本就会去支撑读写，副本间可以看做是完全均等没有差异的，如果整个 Shard 所有的机器都宕机了，整个集群是可以继续提供读写服务的，只是读会出现部分数据缺失，这部分数据等机器恢复会自动的回来，这是故障切换。

#### **Q：单次查询允许打开文件数，有这个参数吗？**

**A：**在配置文件中是没有这个设置的，但是系统层面为这个进程把这个FD打开，设置更大的FD。

#### **Q：什么样的数据需要存在 COS 上？**

**A：**COS是腾讯云提供的一个大容量、高性能的对象存储，广泛应用于大数据的技术栈中。我们很多用户的数据在 COS 上，COS 上也沉淀大量的数据，如果这个数据能够被 ClickHouse 分析将为用户带来很大的价值，我们也会做一些工作把数据链路打通，也是在规划中。

#### **Q：数据量大、分区多时，重启ClickHouse很慢，有什么优化建议吗？**

**A：**确实会出现这个问题，数据量比较大时重启比较慢，特别是 IO 带宽比较低的情况下，如果有特别重要的表，这表必须要快速的写，建议先把节点上不重要的表先给 move 要其他的目录，先把这个表写入，之后再把其他的表加入。

#### **Q：当并发的写入数据请求数比较多时，ClickHouse会报错，会有什么配置优化建议？**

A：首先要评估整个集群的负载是否达到了上限，如果达到建议先扩容，如果没有达到还出现这样的情况就看写入方式是否合理。

首先尽量大批次的写入，写入的 QPS 官方建议是 1 到 2，以一秒钟写一个、两个的频度写入，每次写入的数据尽量多，比如 64K/条，一定是大批量。

第三个点是操作层面写入的数据一定要做好预处理，如果是用 GBDC 自己手写写入，每一个 insert 中包含的数据分区尽量是同一个分区、不要跨分区。配置层面建议调大后台的 Merge 限制数，这些都无法解决的话，可能要观察整个系统的 LWait ，通常是磁盘开关瓶颈的问题，如果这样情况就要去做磁盘的升级。

还有一种方案，就是 QQ 音乐做的读写分离，当写请求和读请求分离开，不需要历史数据可以做 AB 切换的方式上线数据，如果需要历史数据，可以通过工具把新创建好的数据拷贝过去的方式，降低对写请求的影响。

#### **Q：ClickHouse是否存在丢数据的情况？**

**A：**这个问题是大家担心的情况，大家对数据完整性要求非常高，如果绝对来说肯定会存在这样的情况，整个机房机器全坏了、所有的集群所有节点磁盘都坏了，那肯定会出现丢数据的情况。通常这样的小概率事件就不会考虑了。

ClickHouse 有内在的机制允许数据做多副本，可以配置两副本、三个副本，通常是三副本已经足够了，但大多数情况建议用两副本，后面一些数据可以备份到 COS 上去，查询的请求可以购买一些计算节点，通过计算节点访问 COS 中的数据，COS 数据可以提供很高的可靠性。

#### **Q：ZooKeeper 性能瓶颈是如何优化的？**

**A：**ZooKeeper稳定性与可靠性对ClickHouse集群至关重要。从一下几个方面考虑：

建议数据规模超过TB级别，采用SSD盘的机器部署ZK集群；

ZK配置上，要确保及时清理不需要的数据

腾讯云ClickHouse服务会降低对ZK的依赖，敬请期待。

#### **Q：物化视图和 MergeTree 表存储一样的数据，查询性能有区别吗？**

**A:**性能上没有区别，如果物化视图没有关联目标表，系统会创建一个隐藏的目标表，通过show tables命令也是可见的。查询数据最终会落到关联的目标表上。在这个上下文中，物化视相当于传统数据库中的触发器。

#### **Q：在数据量上30亿的情况下，一般选择什么规格的机器比较好？**

**A:**选择机器类型，要综合你的查询情况。例如，如果你查询数据量都比较少，就不需要太多机器。在腾讯云上，我们建议采用大数据机型或者高IO机型。

#### **Q：在深度分页的场景下，ClickHouse 应该如何做？**

**A:**总体而言，复杂查询情况，尽量减少查询所需读取的数据量。使用索引，以及预聚合等加速查询。

#### **Q：两个亿行级别的表关联查询，怎么写高效？**

**A:**QQ音乐的例子可以借鉴，能否对数据合理组织，让数据的逻辑分片和ClickHouse的分片一致，从而将GLOBAL IN/JOINs 操作转为节点内的IN/JOINs操作。

#### **Q：ClickHouse在哪些数据场景下更有优势呢？宽表多的场景呢？**

**A:**ClickHouse 删除处理结构化的流式数据，例如 事件数据，监控数据，日志数据等。ClickHouses非常适合处理宽表场景。在具体使用过程中，也建议尽量使用宽表。

#### **Q：MySQL 读写太慢了，迁移到 ClickHouse 是不是就会解决了？**

**A:**可能还需要了解具体的场景，如果你的业务需要有事物支持，那么ClickHouse无法支持。除此，ClickHouse 在性能方面具有非常明显的优势。

#### **Q：Docker 容器中的 ClickHouse能用于生产吗?**

**A:**可以的。据我了解到，腾讯公司内部有不少业务部门的ClickHouse集群部署在容器中。

#### **Q：ClickHouse 8123 端口 Read timed out 怎么优化呢？**

**A:**从2个方面来看：

ClickHouse进程所在机器负载情况如何，网卡，网卡，磁盘是否已出现瓶颈。

在客户端，在适当调整Timeout值后，仍然出现超时，可以看看客户端所在机器负载情况，以及到ClickHouse机器的网络状况。

#### **Q：大的历史表（T级别）方便存 ClickHouse吗，存了后可以快速读写吗？**

**A:**有不少工具都可以用于将历史数据写入到ClickHouse中。例如可以使用物化视图将数据从KAFKA导入到ClickHouse, 可以使用 clickhouse-mysql-data-reader

将MYSQL数据库中的作存量、 增量导入。也可以使用JDBC 将其他数据源数据导入，例如 https://github.com/ClickHouse/clickhouse-jdbc。

数据导入到ClickHouse后，大部分查询相比HIVE，SPARKSQL而言，具有非常明显的性能优势。在QQ音乐的案例里，他们绝大部分查询响应时间是小于1s的。

#### **Q：如果做了用户资源的配置，超出配额会产生什么现象，拒绝连接还是查询变慢？**

**A:**抛出异常。





# 为什么ClickHouse这么快？

https://mp.weixin.qq.com/s/SxIbm8sebnBpPVCadhKfig

交互式分析之 ClickHouse





## **OLAP交互式分析简介**

交互式分析，也称 OLAP（Online Analytical Processing），它赋予用户对海量数据进行多维度、交互式的统计分析能力，以充分利用数据的价值进行量化运营、辅助决策等，帮助用户提高生产效率。

交互式分析主要应用于统计报表、即席查询（Ad Hoc）等领域，前者查询模式较固定，后者即兴进行探索分析。

代表场景例如：移动互联网中 PV、UV、活跃度等典型实时报表；互联网内容领域中人群洞察、关联分析等即席查询。



交互式分析是数据分析的一种重要方式，与离线分析、流式分析、检索分析一起，共同组成完整的数据分析解决方案，在互联网、物联网快速发展的背景下，从不同维度满足用户对海量数据的全方位分析需求。

相比专注于事务处理的传统关系型数据库，交互式分析解决了 PB 级数据分析带来的性能、扩展性问题。

相比离线分析长达 T+1 的时效性、流式分析较为固定的分析模式、检索分析受限的分析性能，交互式分析的分钟级时效性、灵活多维度的分析能力、超高性能的扫描分析性能，可以大幅度提高数据分析的效率，拓展数据分析的应用范围。

![图片](https://mmbiz.qpic.cn/mmbiz_png/VY8SELNGe95CjTRicfIZ7aSS3BFvBUnCWtNIOUaQKUicI35kRN7zofDiagR72tKZKInA6eTKGEiafpLQLP5JkZH4XA/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

从数据访问特性角度来看，交互式分析场景具有如下典型特点：

- 大多数访问是读请求。
- 写入通常为追加写，较少更新、删除操作。
- 读写不关注事务、强一致等特性。
- 查询通常会访问大量的行，但仅部分列是必须的。
- 查询结果通常明显小于访问的原始数据，且具有可理解的统计意义。



## **百花齐放下的 ClickHouse**



近十年，交互式分析领域经历了百花齐放式的发展，大量解决方案爆发式涌现，尚未有产品达到类似 Oracle/MySQL 在关系型数据库领域中绝对领先的状态。

业界提出的开源或闭源的交互式解决方案，主要从大数据、NoSQL 两个不同的方向进行演进，以期望提供用户最好的交互式分析体验。

下图所示是不同维度下的代表性解决方案，供大家参考了解：

![图片](https://mmbiz.qpic.cn/mmbiz_png/VY8SELNGe95CjTRicfIZ7aSS3BFvBUnCWObKfOHZeFg0vHTs8Csn6ubJqSFD8TZbmhfKHF0z8mtAyuPanichsYDQ/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

其中，ClickHouse 作为一款 PB 级的交互式分析数据库，最初是由号称 “ 俄罗斯 Google ” 的 Yandex 公司开发，主要作为世界第二大 Web 流量分析平台 Yandex.Metrica（类 Google Analytic、友盟统计）的核心存储，为 Web 站点、移动 App 实时在线的生成流量统计报表。



自 2016 年开源以来，ClickHouse 凭借其数倍于业界顶尖分析型数据库的极致性能，成为交互式分析领域的后起之秀，发展速度非常快，Github 上获得 12.4K Star，DB-Engines 排名近一年上升 26 位，并获得思科、Splunk、腾讯、阿里等顶级企业的采用。

下面是 ClickHouse 及其他开源 OLAP 产品的发展趋势统计：

![图片](https://mmbiz.qpic.cn/mmbiz_png/VY8SELNGe95CjTRicfIZ7aSS3BFvBUnCW2F0YIyJOcPcktoKxrbQwOoYYQRXmrNTicavFz0HibkyU3laUFvvzMgrw/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

性能是衡量 OLAP 数据库的关键指标，我们可以通过 ClickHouse 官方测试结果感受下 ClickHouse 的极致性能，其中绿色代表性能最佳，红色代表性能较差，红色越深代表性能越弱。



从测试结果看，ClickHouse 几乎在所有场景下性能都最佳，并且从所有查询整体看，ClickHouse 领先图灵奖得主 Michael Stonebraker 所创建的 Vertica 达 6 倍，领先 Greenplum 达到 18 倍。

![图片](https://mmbiz.qpic.cn/mmbiz_png/VY8SELNGe95CjTRicfIZ7aSS3BFvBUnCWstXmDy7MBvaAMKDGN4KC9LKg9a8pNcrorMSZcpfvibztiaJpZDRKHuFw/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

更多测试结果可参考 OLAP 系统第三方评测 ，尽管该测试使用了无索引的表引擎（或称表类型），ClickHouse 仍然在单表模式下体现了强劲的领先优势。

##  

## ClickHouse 架构





### **集群架构**



![图片](https://mmbiz.qpic.cn/mmbiz_png/VY8SELNGe95CjTRicfIZ7aSS3BFvBUnCWhxDgC4V4nHfgBotuQl0fIYtCiafCzyefhtAfKQhRKribqvpsn6fUAMgg/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

ClickHouse 采用典型的分组式的分布式架构，具体集群架构如上图所示：

- **Shard：**集群内划分为多个分片或分组（Shard 0 … Shard N），通过 Shard 的线性扩展能力，支持海量数据的分布式存储计算。
- **Node：**每个 Shard 内包含一定数量的节点（Node，即进程），同一 Shard 内的节点互为副本，保障数据可靠。ClickHouse 中副本数可按需建设，且逻辑上不同 Shard 内的副本数可不同。
- **ZooKeeper Service：**集群所有节点对等，节点间通过 ZooKeeper 服务进行分布式协调。

### **数据模型**



ClickHouse 采用经典的表格存储模型，属于结构化数据存储系统。我们分别从面向用户的逻辑数据模型和面向底层存储的物理数据模型进行介绍。

#### **①逻辑数据模型**



从用户使用角度看，ClickHouse 的逻辑数据模型与关系型数据库有一定的相似：一个集群包含多个数据库，一个数据库包含多张表，表用于实际存储数据。

与传统关系型数据库不同的是，ClickHouse 是分布式系统，如何创建分布式表呢？

**ClickHouse 的设计是：**先在每个 Shard 每个节点上创建本地表（即 Shard 的副本），本地表只在对应节点内可见；然后再创建分布式表，映射到前面创建的本地表。



这样用户在访问分布式表时，ClickHouse 会自动根据集群架构信息，把请求转发给对应的本地表。



创建分布式表的具体样例如下：

```
# 首先，创建本地表
CREATE TABLE table_local ON CLUSTER cluster_test
(
    OrderKey             UInt32,        # 列定义
    OrderDate            Date,
    Quantity             UInt8,
    TotalPrice           UInt32,
    ……
)
ENGINE = MergeTree()                    # 表引擎
PARTITION BY toYYYYMM(OrderDate)        # 分区方式
ORDER BY (OrderDate, OrderKey);         # 排序方式
SETTINGS index_granularity = 8192;       # 数据块大小

# 然后，创建分布式表
CREATE TABLE table_distribute ON CLUSTER cluster_test AS table_local
ENGINE = Distributed(cluster_test, default, table_local, rand())   # 关系映射引擎
```



其中部分关键概念介绍如下，分区、数据块、排序等概念会在物理存储模型部分展开介绍：

- **MergeTree：**ClickHouse 中使用非常多的表引擎，底层采用 LSM Tree 架构，写入生成的小文件会持续 Merge。
- **Distributed：**ClickHouse 中的关系映射引擎，它把分布式表映射到指定集群、数据库下对应的本地表上。



更直观的，ClickHouse 中的逻辑数据模型如下：

![图片](https://mmbiz.qpic.cn/mmbiz_png/VY8SELNGe95CjTRicfIZ7aSS3BFvBUnCW4O6o29iaKSk1xYfO2ANG0CmOb7AqkhUV2epqcSDzfiaibTqlY5NicU2VWQ/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

#### **②物理存储模型** 



![图片](https://mmbiz.qpic.cn/mmbiz_png/VY8SELNGe95CjTRicfIZ7aSS3BFvBUnCWaoXIeoRmqtY8n2I6sibOQkEdbPlRs74n9njlkiaqT3Zus2iabljqkMQWQ/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

接下来，我们来介绍每个分片副本内部的物理存储模型，具体如下：

- **数据分区：**每个分片副本的内部，数据按照 PARTITION BY 列进行分区，分区以目录的方式管理，本文样例中表按照时间进行分区。
- **列式存储：**每个数据分区内部，采用列式存储，每个列涉及两个文件，分别是存储数据的 .bin 文件和存储偏移等索引信息的 .mrk2 文件。
- **数据排序：**每个数据分区内部，所有列的数据是按照 ORDER BY 列进行排序的。可以理解为：对于生成这个分区的原始记录行，先按 ORDER BY 列进行排序，然后再按列拆分存储。
- **数据分块：**每个列的数据文件中，实际是分块存储的，方便数据压缩及查询裁剪，每个块中的记录数不超过 index_granularity，默认 8192。
- **主键索引：**主键默认与 ORDER BY 列一致，或为 ORDER BY 列的前缀。由于整个分区内部是有序的，且切割为数据块存储，ClickHouse 抽取每个数据块第一行的主键，生成一份稀疏的排序索引，可在查询时结合过滤条件快速裁剪数据块。



## ClickHouse 核心特性



ClickHouse 为什么会有如此高的性能，获得如此快速的发展速度？下面我们来从 ClickHouse 的核心特性角度来进一步介绍。





### **列存储**

ClickHouse 采用列存储，这对于分析型请求非常高效。

**一个典型且真实的情况是：**如果我们需要分析的数据有 50 列，而每次分析仅读取其中的 5 列，那么通过列存储，我们仅需读取必要的列数据。

相比于普通行存，可减少 10 倍左右的读取、解压、处理等开销，对性能会有质的影响。

这是分析场景下，列存储数据库相比行存储数据库的重要优势。这里引用 ClickHouse 官方一个生动形象的动画，方便大家理解。



**行存储：**从存储系统读取所有满足条件的行数据，然后在内存中过滤出需要的字段，速度较慢。

![图片](https://mmbiz.qpic.cn/mmbiz_gif/MOwlO0INfQqeQxSHEDMwRAP7HRNZMJUHGUVk9W91BrhCMGm3MeibEEbjLRlZ48U41T7gSJB3bE1IMgHBsvWopkA/640?wx_fmt=gif&wxfrom=5&wx_lazy=1)

**列存储：**仅从存储系统中读取必要的列数据，无用列不读取，速度非常快。

![图片](https://mmbiz.qpic.cn/mmbiz_gif/MOwlO0INfQqeQxSHEDMwRAP7HRNZMJUHcxRSgiayKG49zgUnDvmj0EzgXCZaiaYRmCKtDUYOra1Fr0gbltuoxkvw/640?wx_fmt=gif&wxfrom=5&wx_lazy=1)



### **向量化执行**

> 一文了解 ClickHouse 的向量化执行 https://mp.weixin.qq.com/s/nuw8yWIwykkQ5mPj0jb8Ew

在支持列存的基础上，ClickHouse 实现了一套面向向量化处理的计算引擎，大量的处理操作都是向量化执行的。

相比于传统火山模型中的逐行处理模式，向量化执行引擎采用批量处理模式，对内存中的列式数据，一个batch调用一次SIMD指，可以大幅减少函数调用开销，降低指令、数据的 Cache Miss，提升 CPU 利用效率。

并且 ClickHouse 可利用CPU 的 SIMD 指令（即用单条指令操作多条数据）进一步加速执行效率。这部分是 ClickHouse 优于大量同类 OLAP 产品的重要因素。

以商品订单数据为例，查询某个订单总价格的处理过程，由传统的按行遍历处理的过程，转换为按 Block 处理的过程。

具体如下图：

![图片](https://mmbiz.qpic.cn/mmbiz_png/VY8SELNGe95CjTRicfIZ7aSS3BFvBUnCWZia11AtL6KL5BGp3akNOvDr5sSVnttupH0GK2Jx7nK5XPAtlIdDqzeg/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)



### **编码压缩**

由于 ClickHouse 采用列存储，相同列的数据连续存储，且底层数据在存储时是经过排序的，这样数据的局部规律性非常强，有利于获得更高的数据压缩比。

此外，ClickHouse 除了支持 LZ4、ZSTD 等通用压缩算法外，还支持 Delta、DoubleDelta、Gorilla 等专用编码算法，用于进一步提高数据压缩比。

其中 DoubleDelta、Gorilla 是 Facebook 专为时间序数据而设计的编码算法，理论上在列存储环境下，可接近专用时序存储的压缩比，详细可参考 Gorilla 论文。

![图片](https://mmbiz.qpic.cn/mmbiz_png/VY8SELNGe95CjTRicfIZ7aSS3BFvBUnCWMB6mx2ax6wlsox5qGUNaUMereibjg9KV2crLRedibFK7KvPpGfibXF7VA/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

在实际场景下，ClickHouse 通常可以达到 10:1 的压缩比，大幅降低存储成本。

同时，超高的压缩比又可以降低存储读取开销、提升系统缓存能力，从而提高查询性能。



### **多索引**



列存用于裁剪不必要的字段读取，而索引则用于裁剪不必要的记录读取。ClickHouse 支持丰富的索引，从而在查询时尽可能的裁剪不必要的记录读取，提高查询性能。

ClickHouse 中最基础的索引是主键索引。前面我们在物理存储模型中介绍，ClickHouse 的底层数据按建表时指定的 ORDER BY 列进行排序，并按 index_granularity 参数切分成数据块，然后抽取每个数据块的第一行形成一份稀疏的排序索引。

用户在查询时，如果查询条件包含主键列，则可以基于稀疏索引进行快速的裁剪。

这里通过下面的样例数据及对应的主键索引进行说明：

![图片](https://mmbiz.qpic.cn/mmbiz_png/VY8SELNGe95CjTRicfIZ7aSS3BFvBUnCWKd00psWWXRhz5b13JlgJ2tbpnVwcJcrvPftoMgYLL4bJZEXSiaNwDNQ/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

样例中的主键列为 CounterID、Date，这里按每 7 个值作为一个数据块，抽取生成了主键索引 Marks 部分。



当用户查询 CounterID equal ‘h’ 的数据时，根据索引信息，只需要读取 Mark number 为 6 和 7 的两个数据块。



ClickHouse 支持更多其他的索引类型，不同索引用于不同场景下的查询裁剪，具体汇总如下，更详细的介绍参考 ClickHouse 官方文档：

![图片](https://mmbiz.qpic.cn/mmbiz_png/VY8SELNGe95CjTRicfIZ7aSS3BFvBUnCWnOuYl5MIkC0a4iaicibdQbe5DcXIYHPNwG2srs5rI1riaA7dtyphVneJ7A/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)



### **物化视图（Cube/Rollup）**

OLAP 分析领域有两个典型的方向：

- **一是 ROLAP，**通过列存、索引等各类技术手段，提升查询时性能。
- **另一是 MOLAP，**通过预计算提前生成聚合后的结果数据，降低查询读取的数据量，属于计算换性能方式。



前者更为灵活，但需要的技术栈相对复杂；后者实现相对简单，但要达到的极致性能，需要生成所有常见查询对应的物化视图，消耗大量计算、存储资源。



物化视图的原理如下图所示，可以在不同维度上对原始数据进行预计算汇总：

![图片](https://mmbiz.qpic.cn/mmbiz_png/VY8SELNGe95CjTRicfIZ7aSS3BFvBUnCWuWxTq0d51Akia4jQkz72TJJ9SJw7Pxz6AxkQk8qc0HsOKibRcy5AibprA/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

ClickHouse 一定程度上做了两者的结合，在尽可能采用 ROLAP 方式提高性能的同时，支持一定的 MOLAP 能力，具体实现方式为 MergeTree 系列表引擎和 MATERIALIZED VIEW。



事实上，Yandex.Metrica 的存储系统也经历过使用纯粹 MOLAP 方案的发展过程，具体参考 ClickHouse的发展历史。

用户在使用时，可优先按照 ROLAP 思路进行调优，例如主键选择、索引优化、编码压缩等。

当希望性能更高时，可考虑结合 MOLAP 方式，针对高频查询模式，建立少量的物化视图，消耗可接受的计算、存储资源，进一步换取查询性能。

### **其他特性**



除了前面所述，ClickHouse 还有非常多其他特性，抽取列举如下，更多详细内容可参考 ClickHouse官方文档：

- **SQL 方言：**在常用场景下，兼容 ANSI SQL，并支持 JDBC、ODBC 等丰富接口。
- **权限管控：**支持 Role-Based 权限控制，与关系型数据库使用体验类似。
- **多机多核并行计算：**ClickHouse 会充分利用集群中的多节点、多线程进行并行计算，提高性能。
- **近似查询：**支持近似查询算法、数据抽样等近似查询方案，加速查询性能。
- **Colocated Join：**数据打散规则一致的多表进行 Join 时，支持本地化的 Colocated Join，提升查询性能。
- ……



## ClickHouse 的不足



前面介绍了大量 ClickHouse 的核心特性，方便读者了解 ClickHouse 高性能、快速发展的背后原因。

当然，ClickHouse 作为后起之秀，远没有达到尽善尽美，还有不少需要待完善的方面，典型代表如下：



### **分布式管控**



分布式系统通常包含三个重要组成部分：

- **存储引擎**
- **计算引擎**
- **分布式管控层**



ClickHouse 有一个非常突出的高性能存储引擎，但在分布式管控层显得较为薄弱，使得运营、使用成本偏高。

主要体现在：

#### **①分布式表**

ClickHouse 对分布式表的抽象并不完整，在多数分布式系统中，用户仅感知集群和表，对分片和副本的管理透明，而在 ClickHouse 中，用户需要自己去管理分片、副本。



例如前面介绍的建表过程：用户需要先创建本地表（分片的副本），然后再创建分布式表，并完成分布式表到本地表的映射。



#### **②弹性伸缩**



ClickHouse 集群自身虽然可以方便的水平增加节点，但并不支持自动的数据均衡。

例如，当包含 6 个节点的线上生产集群因存储或计算压力大，需要进行扩容时，我们可以方便的扩容到 10 个节点。

但是数据并不会自动均衡，需要用户给已有表增加分片或者重新建表，再把写入压力重新在整个集群内打散，而存储压力的均衡则依赖于历史数据过期。

ClickHouse在弹性伸缩方面的不足，大幅增加了业务在进行水平伸缩时运营压力。

基于 ClickHouse 的当前架构，实现自动均衡相对复杂，导致相关问题的根因在于 ClickHouse 分组式的分布式架构：同一分片的主从副本绑定在一组节点上。

更直接的说，分片间数据打散是按照节点进行的，自动均衡过程不能简单的搬迁分片到新节点，会导致路由信息错误。

而创建新表并在集群中进行全量数据重新打散的方式，操作开销过高。

![图片](https://mmbiz.qpic.cn/mmbiz_png/VY8SELNGe95CjTRicfIZ7aSS3BFvBUnCWiaVDQZ3PWMILiaDiaElbu5yA4oc15cSTujsy1ZavrFKZSX4orEAR5Y1ibA/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

#### **③故障恢复**

与弹性伸缩类似，在节点故障的情况下，ClickHouse 并不会利用其它机器补齐缺失的副本数据。需要用户先补齐节点后，然后系统再自动在副本间进行数据同步。

### **计算引擎**



虽然 ClickHouse 在单表性能方面表现非常出色，但是在复杂场景仍有不足，缺乏成熟的 MPP 计算引擎和执行优化器。

例如：多表关联查询、复杂嵌套子查询等场景下查询性能一般，需要人工优化；缺乏 UDF 等能力，在复杂需求下扩展能力较弱等。

这也和 OLAP 系统第三方评测的结果相符。这对于性能如此出众的存储引擎来说，非常可惜。

### **实时写入**

ClickHouse 采用类 LSM Tree 架构，并且建议用户通过批量方式进行写入，每个批次不少于 1000 行 或 每秒钟不超过一个批次，从而提高集群写入性能。

实际测试情况下，32 vCPU&128G 内存的情况下，单节点写性能可达 50 MB/s~200 MB/s，对应 5w~20w TPS。

但 ClickHouse 并不适合实时写入，原因在于 ClickHouse 并非典型的 LSM Tree 架构，它没有实现 Memory Table 结构，每批次写入直接落盘作为一棵 Tree（如果单批次过大，会拆分为多棵 Tree），每条记录实时写入会导致底层大量的小文件，影响查询性能。



这使得 ClickHouse 不适合有实时写入需求的业务，通常需要在业务和 ClickHouse 之间引入一层数据缓存层，实现批量写入。



*参考资料：*

- *采用文档：*

  *https://clickhouse.tech/docs/zh/introduction/adopters/*

- *ClickHouse 官方测试结果:* 

  *https://clickhouse.tech/benchmark/dbms/*

- *OLAP 系统第三方评测:*

  *http://www.clickhouse.com.cn/topic/5c453371389ad55f127768ea*

- *专用编码算法支持：*

  *https://clickhouse.tech/docs/zh/sql-reference/statements/create/#codecs*

- *Gorilla 论文:*

  *http://www.vldb.org/pvldb/vol8/p1816-teller.pdf*

- *索引支持：*

  *https://clickhouse.tech/docs/en/engines/table-engines/mergetree-family/mergetree/#table_engine-mergetree-data_skipping-indexes*

- *MergeTree系列表引擎：*

  *https://clickhouse.tech/docs/en/engines/table-engines/mergetree-family/mergetree/*

- *MATERIALIZED VIEW：*

  *https://clickhouse.tech/docs/en/sql-reference/statements/create/view/#materialized*

- *ClickHouse的发展历史：*

  *https://clickhouse.tech/docs/en/introduction/history/*

- *ClickHouse官方文档：*

  *https://clickhouse.tech/docs/en/*