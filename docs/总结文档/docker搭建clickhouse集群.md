# 使用 docker 搭建 clickhouse 集群

## 写在前面

这篇文章将记录我使用docker搭建clickhouse集群的过程

## 工具准备

### 服务器准备

这里我们准备三台服务器，

分别配置hostname为hdfs-01、hdfs-02、hdfs-03、hdfs-04



所有服务器的/etc/hosts都加上

```
$ip1 hdfs-01
$ip2 hdfs-02
$ip3 hdfs-03
$ip4 hdfs-04
```

**注意：** 这里的$ip1、$ip2、$ip3、$ip4代表的是你四台服务器的ip，记得以实际值写入到/etc/hosts文件中哦

### 安装 docker

> 17.03是docker版本号，修改为最新的版本号即可

```sh
curl https://releases.rancher.com/install-docker/17.03.sh | sh
```



接下来我们执行下面两条命令进行下载安装包

```sh
docker pull yandex/clickhouse-server
docker pull yandex/clickhouse-client
```

### 安装 zookeeper 集群

可以参照[Zookeeper 集群搭建](https://link.juejin.im/?target=https%3A%2F%2Fwww.jianshu.com%2Fp%2Fa5fda39f20d0)

## 启动clickhouse-server

### 创建对应本地路径

> 在三台服务器

**创建配置存储目录：** `mkdir /etc/clickhouse-server`

**创建数据存储目录：** `mkdir /opt/clickhouse`

### 获取配置

> 在server01服务器

采用非docker方式安装都是有默认配置的，这个时候我们没有默认配置怎么办？

我们可以先按照官方教程的docker命令启动一下

```
docker run -d --name clickhouse-server --ulimit nofile=262144:262144 --volume=/opt/clickhouse/:/var/lib/clickhouse yandex/clickhouse-server
```

> -d参数：当前容器在后台启动
>
> --name参数：当前容器的名字，不传的话docker会随机生成
>
> --ulimit参数：这个参数还不清楚，有了解的朋友可以在评论区告诉我一下
>
> --volume参数：将冒号两侧的路径建立映射，当容器服务读取冒号后面的虚拟机内路径时，会去读冒号前面的本机路径。加这个参数的作用是自定义配置

启动完成了后，我们需要复制容器内的配置文件到本机目录下

```
docker cp clickhouse-server:/etc/clickhouse-server/ /etc/clickhouse-server/
```

### 配置集群

> 在server01服务器

#### 编辑config.xml

执行命令`vim /etc/clickhouse-server/config.xml`编辑config.xml文件

在`remote_servers`这个xml标签后添加如下配置

```
  <!-- If element has 'incl' attribute, then for it's value will be used corresponding substitution from another file.
         By default, path to file with substitutions is /etc/metrika.xml. It could be changed in config in 'include_from' element.
         Values for substitutions are specified in /yandex/name_of_substitution elements in that file.
      -->
    <include_from>/etc/clickhouse-server/metrika.xml</include_from>
复制代码
```

#### 新增metrika.xml

执行命令`vim /etc/clickhouse-server/metrika.xml`新增metrika.xml文件

输入如下文本

```
<yandex>

    <!-- 集群配置 -->
    <clickhouse_remote_servers>
        <cluster_3s_1r>

            <!-- 数据分片1  -->
            <shard>
                <internal_replication>false</internal_replication>
                <replica>
                    <host>server01</host>
                    <port>9000</port>
                    <user>default</user>
                    <password></password>
                </replica>
            </shard>

            <!-- 数据分片2  -->
            <shard>
                <internal_replication>false</internal_replication>
                <replica>
                    <host>server02</host>
                    <port>9000</port>
                    <user>default</user>
                    <password></password>
                </replica>
            </shard>

            <!-- 数据分片3  -->
            <shard>
                <internal_replication>false</internal_replication>
                <replica>
                    <host>server03</host>
                    <port>9000</port>
                    <user>default</user>
                    <password></password>
                </replica>
            </shard>
        </cluster_3s_1r>
    </clickhouse_remote_servers>

    <!-- ZK  -->
    <zookeeper-servers>
        <node index="1">
            <host>server01</host>
            <port>2181</port>
        </node>
        <node index="2">
            <host>server02</host>
            <port>2181</port>
        </node>
        <node index="3">
            <host>server03</host>
            <port>2181</port>
        </node>
    </zookeeper-servers>

    <networks>
        <ip>::/0</ip>
    </networks>

    <!-- 数据压缩算法  -->
    <clickhouse_compression>
        <case>
            <min_part_size>10000000000</min_part_size>
            <min_part_size_ratio>0.01</min_part_size_ratio>
            <method>lz4</method>
        </case>
    </clickhouse_compression>

</yandex>
复制代码
```

#### 传递配置文件

到这里我们的关于集群的配置就全部完成了，接下来要做的是把我们在server01上的配置文件传输到其它服务器上 执行命令

```
scp -r /etc/clickhouse-server server02:/etc/clickhouse-server
scp -r /etc/clickhouse-server server03:/etc/clickhouse-server
复制代码
```

> 可以去对应服务器验证一下

### 启动集群

> 分别在三台服务器

执行docker启动脚本

```
docker run -d \
--name cs \
--ulimit nofile=262144:262144 \
--volume=/opt/clickhouse/:/var/lib/clickhouse  \
--volume=/etc/clickhouse-server/:/etc/clickhouse-server/ \
--add-host server01:$ip1 \
--add-host server02:$ip2 \
--add-host server03:$ip3 \
--hostname $current_hostname \
-p 9000:9000 \
-p 8123:8123 \
-p 9009:9009 \
yandex/clickhouse-server
复制代码
```

**注意1：** 这里的 $ip1、$ip2、$ip3 记得替换为实际值

**注意2：** $current_hostname 为当前服务器的hostname

> --add-host参数：因为我们在配置文件中使用了hostname来指代我们的服务器，为了让容器能够识别，所以需要加此参数
>
> --hostname参数：clickhouse中的`system.clusters`表会显示集群信息，其中is_local的属性如果不配置hostname的话clickhouse无法识别是否是当前本机。is_local都为0的话会影响集群操作，比如create table on cluster cluster_2s_1r .....
>
> --p参数：暴露容器中的端口到本机端口中。![本机端口:](https://juejin.im/equation?tex=%E6%9C%AC%E6%9C%BA%E7%AB%AF%E5%8F%A3%3A)容器端口

### 验证集群搭建

分别查看三台服务器的`system.clusters`，应该显示集群中三台服务器的信息，且`is_local`为正确值

## 启动 clickhouse-client

> 在任意服务器

```
docker run -it \
--rm \
--add-host server01:$ip1 \
--add-host server02:$ip2 \
--add-host server03:$ip3 \
yandex/clickhouse-client \
--host server01 \
--port 9000
复制代码
```

> --host参数：相当于我们通过yum安装clickhouse时，执行命令`clickhouse-client --host server01`后面接的参数host，指定用于连接的clickhouse-server的host
>
> --port参数：相当于我们通过yum安装clickhouse时，执行命令`clickhouse-client --port 9000`后面接的参数port，指定用于连接的clickhouse-server的port

## 写在最后

使用docker搭建服务和我们平时在服务器上搭建的思路有些不太一样，需要时刻注意我们使用docker启动的服务是在一个虚拟机里的，可以理解为“面向虚拟机部署”

一开始部署集群的时候我的is_local这个属性值就一直不正常，后来看了[这篇文章](https://link.juejin.im/?target=https%3A%2F%2Fgithub.com%2Fjneo8%2Fclickhouse-setup)，想着用他的方式试一下

发现is_local的值好了！我一开始还以为是docker中network的问题，后来在改配置的过程中才意识到是参数hostname起到了关键的作用