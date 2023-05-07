1. 求一棵树两个节点的最近的公共父节点.

   https://www.nowcoder.com/practice/e0cc33a83afe4530bcec46eba3325116?tpId=117&tqId=1024325&tab=answerKey

2. HTTP 301 302有啥区别？

   https://www.cnblogs.com/zhuzhenwei918/p/7582620.html

   301 永久重定向   nginx  ->rewrite	.比较常用的场景是使用域名跳转。

   302 临时重定向   nginx->redirect    比如未登陆的用户访问用户中心重定向到登录页面。

   共同点的，就是用户都可以看到url替换为了一个新的，然后发出请求。

3. **设计一个短链接算法；**

   https://blog.csdn.net/lz0426001/article/details/52370177

4. md5长度是多少？

   MD5本质是hash函数，并不是用来加密的，而是散列，**标准的md5函数是生成128位的**

5. 线程安全的问题，ThreadLocal如果引用一个static变量是不是线程安全.

   https://my.oschina.net/u/4316562/blog/4278609

   不安全

6. 写一个题，找一个无序数组的中位数

   https://www.cnblogs.com/shizhh/p/5746151.html

   容易想的思路：排序，求中间值

   比较容易写的思路：

   首先将数组的前（n+1）／2个元素建立一个最小堆。（PriorityQueue）

   然后，对于下一个元素，和堆顶的元素比较，如果小于等于，丢弃之，接着看下一个元素。如果大于，则用该元素取代堆顶，再调整堆，接着看下一个元素。重复这个步骤，直到数组为空。

   当数组都遍历完了，那么，如果数组长度为奇数，堆顶的元素即是中位数。如果是偶数，则堆顶的前两个元素的平均值即中位数。

   可以看出，长度为（n＋1）／2的最小堆是解决方案的精华之处。如果

7. 写了个快排，然后让我找到无序数组第k大的一个数，我说先排序再找，实际上可以用快排的partition函数。

   ->可以用容量为K的最小堆实现，原理同第六题

8. 快排的时间复杂度，最坏情况呢，最好情况呢，堆排序的时间复杂度呢，建堆的复杂度是多少。

   快排：在最糟糕得情况下时间复杂度是O(n²)，平均的复杂度是O(nlogn)

   

   堆排序：初始化堆（建堆）的复杂度为O(n)。调整堆的复杂度为O(n*log n)。所以，总体复杂度为O(n*log n)

   https://www.cnblogs.com/lylhome/p/13276081.html

   

9. 操作系统了解么，Linux和windows

10. 说说Linux的磁盘管理

11. Linux有哪些进程通信方式

    https://blog.csdn.net/gatieme/article/details/50908749

    # 进程间通信各种方式效率比较

    ------

    | 类型             | 无连接 | 可靠 | 流控制 | 记录消息类型 | 优先级 |
    | ---------------- | ------ | ---- | ------ | ------------ | ------ |
    | 普通PIPE         | N      | Y    | Y      |              | N      |
    | 流PIPE           | N      | Y    | Y      |              | N      |
    | 命名PIPE(FIFO)   | N      | Y    | Y      |              | N      |
    | 消息队列         | N      | Y    | Y      |              | Y      |
    | 信号量           | N      | Y    | Y      |              | Y      |
    | 共享存储         | N      | Y    | Y      |              | Y      |
    | UNIX流SOCKET     | N      | Y    | Y      |              | N      |
    | UNIX数据包SOCKET | Y      | Y    | N      |              | N      |

    > 注:无连接: 指无需调用某种形式的OPEN,就有发送消息的能力流控制:
    >
    > 如果系统资源短缺或者不能接收更多消息,则发送进程能进行流量控制

12. Linux的共享内存如何实现。

    https://blog.csdn.net/Al_xin/article/details/38602093

13. 共享内存实现的具体步骤

14. socket网络编程，说一下TCP的三次握手和四次挥手

    

15. 如何把docker讲的很清楚。

    https://www.ruanyifeng.com/blog/2018/02/docker-tutorial.html

16. cgroup在linux的具体实现。

    https://tech.meituan.com/2015/03/31/cgroups.html

    cgroups 是Linux内核提供的一种可以限制单个进程或者多个进程所使用资源的机制，可以对 cpu，内存等资源实现精细化的控制，目前越来越火的轻量级容器 Docker 就使用了 cgroups 提供的资源限制能力来完成cpu，内存等部分的资源控制。

    ### 概念及原理

    #### cgroups子系统

    cgroups 的全称是control groups，cgroups为每种可以控制的资源定义了一个子系统。典型的子系统介绍如下：

    1. cpu 子系统，主要限制进程的 cpu 使用率。
    2. cpuacct 子系统，可以统计 cgroups 中的进程的 cpu 使用报告。
    3. cpuset 子系统，可以为 cgroups 中的进程分配单独的 cpu 节点或者内存节点。
    4. memory 子系统，可以限制进程的 memory 使用量。
    5. blkio 子系统，可以限制进程的块设备 io。
    6. devices 子系统，可以控制进程能够访问某些设备。
    7. net_cls 子系统，可以标记 cgroups 中进程的网络数据包，然后可以使用 tc 模块（traffic control）对数据包进行控制。
    8. freezer 子系统，可以挂起或者恢复 cgroups 中的进程。
    9. ns 子系统，可以使不同 cgroups 下面的进程使用不同的 namespace。

17. 多线程用过哪些.

18. Java的集合类哪些是线程安全

    Vector

    Stack

    Hashtable

    java.util.concurrent包下所有的集合类
     ArrayBlockingQueue、ConcurrentHashMap、ConcurrentLinkedQueue、ConcurrentLinkedDeque......

19. 分别说说这些集合类，hashmap怎么实现的

20. MySQL索引的实现，innodb的索引，b+树索引是怎么实

现的，为什么用b+树做索引节点，一个节点存了多少数据，怎么规定大小，与磁盘页对应。

mysql 页：单个叶子节点（页）中的记录数=16K

在计算机中磁盘存储数据最小单元是扇区，一个扇区的大小是512字节，而文件系统（例如XFS/EXT4）他的最小单元是块，一个块的大小是4k，而对于我们的InnoDB存储引擎也有自己的最小储存单元——页（Page），一个页的大小是16K。

19. MySQL的事务隔离级别，分别解决什么问题。

    读未提交：有脏读、幻读、不可重复读问题

    读已提交：解决脏读

    可重复读：解决脏读、不可重复读、MVCC解决幻读。

    串行化读：最高隔离级别，最安全，效率低

    ![img](https://upload-images.jianshu.io/upload_images/1627454-eceded962ef591d1.png?imageMogr2/auto-orient/strip|imageView2/2/w/686/format/webp)

**注意：可重复读隔离级别通过MVCC解决了幻读**

19. Redis了解么，如果Redis有1亿个key，使用keys命令是否会影响线上服务

    https://stor.51cto.com/art/201904/595194.htm

    - (1)运维人员进行keys *操作，该操作比较耗时，又因为redis是单线程的，所以redis被锁住。
    - (2)此时QPS比较高，又来了几万个对redis的读写请求，因为redis被锁住，所以全部Hang在那。
    - (3)因为太多线程Hang在那，CPU严重飙升，造成redis所在的服务器宕机
    - (4)所有的线程在redis那取不到数据，一瞬间全去数据库取数据，数据库就宕机了。

    如果有使用类似keys正则命令需求，使用scan命令代替

20. Redis的持久化方式，aof和rdb，具体怎么实现，追加日志和备份文件，底层实现原理。

    https://juejin.cn/post/6844903939339452430

    aof:  可以减少数据丢失或不丢失

    appendonly   被执行的写命令写到AOF文件的末尾，记录数据的变化

    ### 命令追加(append)

    开启AOF持久化功能后，服务器每执行一个写命令，都会把该命令以协议格式先追加到`aof_buf`缓存区的末尾，而不是直接写入文件，避免每次有命令都直接写入硬盘，减少硬盘IO次数

    ### 文件写入(write)和文件同步(sync)

    对于何时把`aof_buf`缓冲区的内容写入保存在AOF文件中，Redis提供了多种策略

    - `appendfsync always`：将`aof_buf`缓冲区的所有内容写入并同步到AOF文件，每个写命令同步写入磁盘
    - `appendfsync everysec`：将`aof_buf`缓存区的内容写入AOF文件，每秒同步一次，该操作由一个线程专门负责
    - `appendfsync no`：将`aof_buf`缓存区的内容写入AOF文件，什么时候同步由操作系统来决定

    `appendfsync`选项的默认配置为`everysec`，即每秒执行一次同步

    

    rdb: 适合数据备份

    fork的子进程要完成内存快照+copyonwrite

21. Redis的list是怎么实现的，我说用ziplist+quicklist实现的，ziplist压缩空间，quicklist实现链表。

    在新的Redis 版本中，直接使用的是quicklist 数据结构保存：

    ![在这里插入图片描述](https://img-blog.csdnimg.cn/2018120212500438.PNG?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzE3MzA1MjQ5,size_16,color_FFFFFF,t_70)

    quicklist 是 ziplist 和 linkedlist 的混合体，它将 linkedlist 按段切分，每一段使用 ziplist 来紧凑存储，多个 ziplist 之间使用双向指针串接起来。

    quicklist 内部默认单个 ziplist 长度为 8k 字节，超出了这个字节数，就会新起一个 ziplist。ziplist 的最大长度由配置文件的参数list-max-ziplist-size决定。

22. sortedset怎么实现的，skiplist的数据结构。

    ziplist或skiplist+dict（hash）实现

    ![有序集存储结构](https://user-gold-cdn.xitu.io/2017/11/20/15fd6c4ccc8a04c7?w=977&h=657&f=png&s=21629)

23. 了解什么消息队列，rmq和kafka

第一题：写一个层序遍历  ---BFS。

https://www.runoob.com/data-structures/binary-search-level-traverse.html

**通过引入一个队列来支撑层序遍历：**

- 如果根节点为空，无可遍历；
- 如果根节点不为空：
  - 先将根节点入队；
  - 只要队列不为空：
    - 出队队首节点，并遍历；
    - 如果队首节点有左孩子，将左孩子入队；
    - 如果队首节点有右孩子，将右孩子入队；

第二题：写一个插入树节点到一颗排序树的插入方法，使用递归方式找到插入位置即可。

递归和非递归方式https://blog.csdn.net/qq_35181209/article/details/52798166



第三题：一个有向图用邻接矩阵表示，并且是有权图，现在问怎么判断图中有没有环。
我说直接dfs走到原点即为有环，刚开始写的时候我又问了一嘴是不是只要找到一个就行，面试官说是的，然后我说这样应该用bfs，有一次访问到原节点就是有环了。
面试官问我不用递归能不能做这个题，其实我都还没开始写。然后我就说没有思路，他提示我拓扑图。我没明白拓扑图能带来什么好处。现在一想，好像当访问过程中找不到下一个节点时就说明有环。做一个访问标记应该就可以。
第四题：一个二叉树，找到二叉树中最长的一条路径。
我先用求树高的方式求出了根节点的左右子树高度，加起来便是。
然后面试官提示需要考虑某个子树深度特别大的情况，于是我用遍历的方式刷新最大值，用上面那个方法遍历完整个树即可
堆中数据获取最小的K个数.（算法类问题）

Java面试题：
1.多线程 用countDownLatch 三个线程对某个数进行累加 

2. sql 多表关联 求年龄大于20岁的 某个班级的学生

3. 链表反转

4. 区间合并 [1,3]  [2,4] [7,10]

   https://leetcode-cn.com/problems/merge-intervals/

5. 手写死锁

6. 看事务代码

7. 整数开根号，保留m位

   http://bigdatadecode.club/%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84%E7%AE%97%E6%B3%95%E4%B9%8Bn%E7%9A%84%E5%B9%B3%E6%96%B9%E6%A0%B9%E4%BF%9D%E7%95%99m%E4%BD%8D%E5%B0%8F%E6%95%B0.html

抖音电商-后端岗位
一面
详细介绍项目，技术难点，方案优劣比较
分布式事务处理方式，以及结合着本身业务，为什么选择对应的方案。
Mysql慢查询优化。从索引优化，到mysql本身自己的缓存。到最后借助其他缓存中间件。
算法题，输入一棵二叉搜索树，将该二叉搜索树转换成一个排序的双向链表。要求不能创建任何新的结点，只能调整树中结点指针的指向。

https://zhuanlan.zhihu.com/p/39025518

中序遍历

![img](https://pic4.zhimg.com/80/v2-dfed873e672f0cb9aa0f6cd729fc19df_720w.jpg)



微服务治理，限流策略。
秒杀系统设计。大概思路首先解决并发大流量直接打到mysql，库存加减操作，与订单系统如何交互，必要的时候所有数据全部使用缓存，防攻击等。
二面

介绍项目，分布式事务解决方案，幂等处理，消息可靠性保证。
服务发现，注册中心cp or ap，具体原理。

Zookeeper一致性协议
Redis线程模型。

https://www.cnblogs.com/mrmirror/p/13587311.html

## redis为什么效率这么高？[#](https://www.cnblogs.com/mrmirror/p/13587311.html#3632559489)

------

- 纯内存操作。

- 核心是基于非阻塞的 IO 多路复用机制。

- C 语言实现，语言更接近操作系统，执行速度相对会更快。

- 单线程反而避免了多线程的频繁上下文切换问题，预防了多线程可能产生的竞争问题。

  

Map结构扩容方式，

rdb和aof。

Epoll和poll模型

https://www.cnblogs.com/aspirant/p/9166944.html?share_token=627a0701-2fc1-468e-b742-5d602ac73842

算法题，无序数组长度n，所存的数字0~n-1，找到第一个重复

的数字。空间（O1）,时间（o1）。

## 一. 思路

1. 最直接的想法就是构造一个容量为N的辅助数组B，原数组A中每个数对应B中下标，首次命中，B中对应元素+1。如果某次命中时，B中对应的不为0，说明，前边已经有一样数字了，那它就是重复的了。
   举例：a{2,3,1,4,4,5}，刚开始b是{0,0,0,0,0,0}，开始扫描a。
   a[0] = 2 {0,0,1,0,0,0}
   a[1] = 3 {0,0,1,1,0,0}
   a[2] = 1 {0,1,1,1,0,0}
   a[3] = 4 {0,1,1,1,1,0}
   a[4] = 4 {0,1,1,1,2,0}, 到这一步，就已经找到了重复数字。
   a[5] = 5 {0,1,1,1,2,1}
   时间复杂度O（n），空间复杂度O（n），算法优点是简单快速，比用set更轻量更快，不打乱原数组顺序。

软性问题，团队如何分工，以及平时有哪些团队方面的工作。
三面
介绍项目，分布式解决方案，状态机设计。
服务治理，更加深入的探讨限流的策略，从服务注册中心以及rpc客户端和服务端配合，结合着负载均衡策略。
软性问题，个人本次看机会的最大的诉求是什么，平时怎么带的团队。