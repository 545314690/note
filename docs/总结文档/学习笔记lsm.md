## 树

### 树中使用的术语

- **根（Root）**：树中最顶端的节点，根没有父节点。
- **子节点（Child）**：节点所拥有子树的根节点称为该节点的子节点。
- **父节点（Parent）**：如果节点拥有子节点，则该节点为子节点的父节点。
- **兄弟节点（Sibling）**：与节点拥有相同父节点的节点。
- **子孙节点（Descendant）**：节点向下路径上可达的节点。
- **叶节点（Leaf）**：没有子节点的节点。
- **内节点（Internal Node）**：至少有一个子节点的节点。
- **度（Degree）**：节点拥有子树的数量。
- **边（Edge）**：两个节点中间的链接。
- **路径（Path）**：从节点到子孙节点过程中的边和节点所组成的序列。
- **层级（Level）**：根为 Level 0 层，根的子节点为 Level 1 层，以此类推。
- **高度（Height）/深度（Depth）**：树中层的数量。比如只有 Level 0,Level 1,Level 2 则高度为 3。

| **类别**                                                     | **树名称**                                                   |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| **[二叉查找树](http://www.cnblogs.com/gaochundong/p/binary_search_tree.html)****[（Binary Search Tree）](http://www.cnblogs.com/gaochundong/p/binary_search_tree.html)** | [二叉查找树](http://www.cnblogs.com/gaochundong/p/binary_search_tree.html)，笛卡尔树，T 树 |
| **[自平衡二叉查找树](http://www.cnblogs.com/gaochundong/p/self_balancing_binary_search_tree.html)****[（Self-balancing Binary Search Tree）](http://www.cnblogs.com/gaochundong/p/self_balancing_binary_search_tree.html)** | AA 树，[AVL 树](http://www.cnblogs.com/gaochundong/p/self_balancing_binary_search_tree.html)， [红黑树（Red-Black Tree）](http://www.cnblogs.com/gaochundong/p/self_balancing_binary_search_tree.html)， 伸展树（Splay Tree） |
| **B 树****（B-Tree）**                                       | [2-3 树](http://www.cnblogs.com/gaochundong/p/balanced_search_tree.html)，[2-3-4 树](http://www.cnblogs.com/gaochundong/p/balanced_search_tree.html)， B 树，B+ 树，B* 树 |
| **字典树****（Trie-Tree）**                                  | 后缀树，基数树，三叉查找树，快速前缀树                       |
| **空间数据分割树****（Spatial Data Partitioning Tree）**     | R 树，R+ 树，R* 树， 线段树，优先 R 树                       |

### 二叉树

**二叉树（Binary Tree）是一种特殊的树类型，其每个节点最多只能有两个子节点。**这两个子节点分别称为当前节点的左孩子（left child）和右孩子（right child）。

![img](https://images0.cnblogs.com/i/175043/201406/291012414148876.gif)

**完全二叉树和满二叉树**

完全二叉树（Complete Binary Tree）：深度为 h，有 n 个节点的二叉树，当且仅当其每一个节点都与深度为 h 的满二叉树中，序号为 1 至 n 的节点对应时，称之为完全二叉树。

满二叉树（Full Binary Tree）：一棵深度为 h，且有 2h - 1 个节点称之为满二叉树。

![img](https://images0.cnblogs.com/i/175043/201407/100055326144706.jpg)



|                | ***\*完全二叉树\**** | **满二叉树**    |
| -------------- | -------------------- | --------------- |
| **总节点数 k** | 2h-1 <= k < 2h - 1   | k = 2h - 1      |
| **树高 h**     | h = log2k + 1        | h = log2(k + 1) |

满二叉树的查找次数为树的深度（二分查找法），所以查询复杂度为O(logn)

### **二叉查找树（Binary Search Tree）**

二叉查找树（BST：Binary Search Tree）是一种特殊的二叉树，它改善了二叉树节点查找的效率。二叉查找树有以下性质：

对于任意一个节点 n，

- 其左子树（left subtree）下的每个后代节点（descendant node）的值都小于节点 n 的值；
- 其右子树（right subtree）下的每个后代节点的值都大于节点 n 的值。

所谓节点 n 的子树，可以将其看作是以节点 n 为根节点的树。子树的所有节点都是节点 n 的后代，而子树的根则是节点 n 本身。

### 平衡树

### 红黑树

 R-B Tree，全称是Red-Black Tree，又称为“红黑树”，它一种特殊的二叉查找树。红黑树的每个节点上都有存储位表示节点的颜色，可以是红(Red)或黑(Black)。

**红黑树的特性**:
**（1）每个节点或者是黑色，或者是红色。**
**（2）根节点是黑色。**
**（3）每个叶子节点（NIL）是黑色。 [注意：这里叶子节点，是指为空(NIL或NULL)的叶子节点！]**
**（4）如果一个节点是红色的，则它的子节点必须是黑色的。****（不能有连续的两个红色节点）**
**（5）从一个节点到该节点的子孙节点的所有路径上包含相同数目的黑节点。**

**注意**：
(01) 特性(3)中的叶子节点，是只为空(NIL或null)的节点。
(02) 特性(5)，确保没有一条路径会比其他路径长出俩倍。因而，红黑树是相对是接近平衡的二叉树。

红黑树示意图如下：

[![img](https://images0.cnblogs.com/i/497634/201403/251730074203156.jpg)](https://images0.cnblogs.com/i/497634/201403/251730074203156.jpg)

#### 右旋和右旋

##### 左旋动图

![img](https://img-blog.csdnimg.cn/20181125155223734.gif)

![img](https://img-blog.csdnimg.cn/2018112515545758.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NjAwMDI3,size_16,color_FFFFFF,t_70)

##### 右旋动图

![img](https://img-blog.csdnimg.cn/20181125160017452.gif)

![img](https://img-blog.csdnimg.cn/20181125160235220.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NjAwMDI3,size_16,color_FFFFFF,t_70)

#### 红黑树的变换规则

所有**插入的点默认都是红色**，否则全黑色就是普通二叉树了，下一步也就无法按照规律变换以达到自平衡。

1. 变色规则

   当前结点是红色，父结点是红色，且它的叔叔结点也是红色(自红，父红，叔叔红)

   1. 把父结点设为黑色
   2. 把叔叔结点设为黑色
   3. 把祖父结点设为红色
   4. 把指针结点定义到祖父结点设为当前要操作的，分析的点变换的规则（此时可能是要左右旋）

2. 左旋

- 当前结点是右子树，且是红色

- 父结点是红色

- 叔叔结点是黑色（**右红，父红，叔叔黑**）

- 以**父结点**左旋

  3 右旋
  当前节结点是左子树，红色，父结点红色，叔叔黑色。（**左红，父红，叔叔黑**）

- 把父结点变为黑色
- 把祖父变为红色
- 以**祖父为结点**右旋

#### B树

#### B+数

#### 为什么mysql用B+树做索引而不用B-树或红黑树

B-树、B+树、红黑树，都是平衡查找树，那么查询效率上讲，平均都是O(logn)。使用什么哪种数据结构，肯定是出于提高数据库的查询效率的考虑

- 红黑树是对内存操作，B数是对磁盘操作

- B树是多路树，红黑树是二叉树！红黑树一个节点只能存出一个值，B树一个节点可以存储多个值，红黑树的深度会更大,定位时 红黑树的查找次数会大一些。

  在大规模数据存储的时候，红黑树往往出现由于树的深度过大而造成磁盘IO读写过于频繁，进而导致效率低下的情况。为什么会出现这样的情况，我们知道要获取磁盘上数据，必须先通过磁盘移动臂移动到数据所在的柱面，然后找到指定盘面，接着旋转盘面找到数据所在的磁道，最后对数据进行读写。磁盘IO代价主要花费在查找所需的柱面上，树的深度过大会造成磁盘IO频繁读写。根据磁盘查找存取的次数往往由树的高度所决定，所以，只要我们通过某种较好的树结构减少树的结构尽量减少树的高度，B树可以有多个子女，从几十到上千，可以降低树的高度。

- B+树相对B数更适合区间访问

  B-树和B+树最重要的一个区别就是B+树只有叶节点存放数据，其余节点用来索引，而B-树是每个索引节点都会有Data域。
  这就决定了B+树更适合用来存储外部数据，也就是所谓的磁盘数据。B+树所有的Data域在叶子节点，一般来说都会进行一个优化，就是将所有的叶子节点用指针串起来。这样遍历叶子节点就能获得全部数据，这样就能进行区间访问啦。



## MySQL

### 索引相关

#### 1.数据库索引有哪些优缺点？

**B+树索引：**不再需要进行全表扫描，只需要对树进行搜索即可，所以查找速度快很多

**哈希索引：**哈希索引能以 O(1) 时间进行查找，无法用于排序与分组，并且只支持精确查找，无法用于部分查找和范围查找。

**全文索引：**MyISAM 存储引擎支持全文索引，MySQL InnoDB从5.6开始已经支持全文索引，用于查找文本中的关键词。全文索引导致磁盘资源的大量占用且必须修改查询语句。

#### 2.为什么不用二叉查找树作为数据库索引？

​    二叉查找树：查找到指定数据，效率其实很高logn。但是数据库索引文件有可能很大，关系型数据存储了上亿条数据，索引文件大则上G，不可能全部放入内存中，而是需要的时候换入内存，方式是磁盘页。一般来说树的一个节点就是一个磁盘页。如果使用二叉查找树，那么每个节点存储一个元素，查找到指定元素，需要进行大量的磁盘IO，效率很低。
​    B树：通过单一节点包含多个data，大大降低了树的高度，大大减少了磁盘IO次数。

#### 3.为什么数据库索引不用红黑树而用B+树？

​    红黑树当插入删除元素的时候会进行频繁的变色与旋转，来保证红黑树的性质，浪费时间。但是当数据量较小，数据完全可以放入内存中，不需要进行磁盘IO时，红黑树时间复杂度比B+树低。

#### 4.MySQL索引为什么用B+树？

​    B+树索引并不能直接找到具体的行，只是找到被查找行所在的页，然后DB通过把整页读入内存，再在内存中查找。B+树的高度一般为2-4层，所以查找记录时最多只需要2-4次IO，相对二叉平衡树已经大大降低了。范围查找时，能通过叶子节点的指针获取数据。例如查找大于等于3的数据，当在叶子节点中查到3时，通过3的尾指针便能获取所有数据，而不需要再像二叉树一样再获取到3的父节点。

#### 5.B+树较B树的优势

- 单一节点存储的元素更多，使得查询的IO次数更少，所以也就使得它更适合做为数据库MySQL的底层数据结构了。
- 所有的查询都要查找到叶子节点，查询性能是稳定的，而B树，每个节点都可以查找到数据，所以不稳定。
- 所有的叶子节点形成了一个有序链表，更加便于查找。

### 最左匹配

**对于联合索引来说，要遵守最左前缀法则**

举列来说索引含有字段id、name、school，可以直接用id字段，也可以id、name这样的顺序，但是name;school都无法使用这个索引。所以在***创建联合索引***的时候一定要注意索引字段顺序，常用的查询字段放在最前面。

## 排序算法

## 动态规划

#### 问题描述

- 动态规划问题的一般形式就是求最值

  动态规划其实是运筹学的一种最优化方法，只不过在计算机问题上应用比较多，比如说让你求**最长**递增子序列呀，**最小**编辑距离呀等等。

- 存在「重叠子问题」

  动态规划的穷举有点特别，因为这类问题**存在「重叠子问题」**，如果暴力穷举的话效率会极其低下，所以需要「备忘录」或者「DP table」来优化穷举过程，避免不必要的计算

#### 动态规划三要素

- 重叠子问题
- 最优子结构
- 状态转移方程





## 多线程与高并发

#### 什么是JUC

JUC（java.util.concurrent）是在并发编程中使用的工具类

#### 线程

 •- thread yield方法是暂时放弃执行状态，进入等待队列但也许马上又会开始运行，runable状态(包括ready 和running)，
 • thread join  在t1中调用t2.jion是等待t2执行完成t1再执行，用做线程等待

- Object的wait和notify/notifyAll方法只能在同步代码块里用，wait等待并释放锁，notify唤醒线程但不释放锁，notify的同时再调用wait进行锁释放，其它线程才可获得锁

#### java的锁

![img](https://awps-assets.meituan.net/mit-x/blog-images-bundle-2018b/7f749fc8.png)

> 锁的讲解见：
>
> https://tech.meituan.com/2018/11/15/java-lock.html

### 互斥锁和自旋锁

最底层的两种就是会「互斥锁和自旋锁」，有很多高级的锁都是基于它们实现的

- **互斥锁**加锁失败后，线程会**释放 CPU** ，给其他线程；
- **自旋锁**加锁失败后，线程会**忙等待**，直到它拿到锁；

**对于互斥锁加锁失败而阻塞的现象，是由操作系统内核实现的**。当加锁失败时，内核会将线程置为「睡眠」状态，等到锁被释放后，内核会在合适的时机唤醒线程，当这个线程成功获取到锁后，于是就可以继续执行。（会有**两次线程上下文切换的成本**）

自旋锁是通过 CPU 提供的 `CAS` 函数（*Compare And Set*），在「用户态」完成加锁和解锁操作，不会主动产生线程上下文切换，所以相比互斥锁来说，会快一些，开销也小一些。

CAS 函数就把这两个步骤合并成一条硬件级指令，形成**原子指令**，这样就保证了这两个步骤是不可分割的，要么一次性执行完两个步骤，要么两个步骤都不执行。

 • 自旋锁（spinlock）：是指当一个线程在获取锁的时候，如果锁已经被其它线程获取，那么该线程将循环等待，然后不断的判断锁是否能够被成功获取，直到获取到锁才会退出循环。

 • 对于互斥锁，如果资源已经被占用，资源申请者只能进入睡眠状态。但是自旋锁不会引起调用者睡眠，如果自旋锁已经被别的执行单元保持，调用者就一直循环在那里看是否该自旋锁的保持者已经释放了锁

自旋等待的时间必须要有一定的限度，如果自旋超过了限定次数（默认是10次，可以使用-XX:PreBlockSpin来更改）没有成功获得锁，就应当挂起线程。

自旋锁在JDK1.4.2中引入，使用-XX:+UseSpinning来开启。JDK 6中变为默认开启，并且引入了自适应的自旋锁（适应性自旋锁）。

自适应意味着自旋的时间（次数）不再固定，而是由前一次在同一个锁上的自旋时间及锁的拥有者的状态来决定。如果在同一个锁对象上，自旋等待刚刚成功获得过锁，并且持有锁的线程正在运行中，那么虚拟机就会认为这次自旋也是很有可能再次成功，进而它将允许自旋等待持续相对更长的时间。如果对于某个锁，自旋很少成功获得过，那在以后尝试获取这个锁时将可能省略掉自旋过程，直接阻塞线程，避免浪费处理器资源。

#### 应用场景

 • 自旋锁不会发生线程上下文切换，但是等待时间过长会消耗cpu资源。
 • 当加锁代码运行时间短，线程少的时候用自旋锁，当线程多，运行时间长时，用sync  OS锁

 • 产生异常而不处理，锁会被释放，其他线程可获得锁

#### synchronized  

• synchronized 保证了原子性和可见性，加了之后不需要加voltile

 • synchronized 方法和synchronized this效果一样都是锁定当前对象
 • synchronized 静态方法和synchronized T.class一样
 • 对象的加锁的方法不影响无锁方法运行，可以同时运行
 • synchronized 锁是可重入锁，在synchronized f1中调用synchronized f2，是同一个线程，f2无需竞争锁，直接获得锁，锁+1，运行完f2去掉一个锁，锁-1，运行完f1，锁-1，释放锁

 • jdk5后 synchronized 锁改进，锁升级分4步骤，一开始无锁，synchronized  首次调用，markword对象头记录锁类型，记录调用线程ID，使用偏向锁，当产生线程争用，会变为自旋锁(10次)，再升级为OS重量级锁。

1. 无锁
2. 偏向锁
3. 自旋锁
4. 重量级锁

#### volatile

• volatile保证线程可见性和禁止指令重排。不保证原子性。引起可见性问题的主要原因是每个线程拥有自己的一个高速缓存区——线程工作内存。volatile变量不会被缓存在寄存器或者对其他处理器不可见的地方，因此在读取volatile类型的变量时总会返回最新写入的值。
 • DCL Double Check Lock单例模式需要加volatile，为了防止指令重排序，(申请内存，初始化值，内存地址赋值给对象)

##### volatile是怎么保障内存可见性以及防止指令重排序的

> https://blog.csdn.net/lsunwing/article/details/83154208?share_token=2de59892-186e-4b8a-883d-583fc3b315d3

1.对其他核心立即可见，这个的意思是，当一个CPU核心A修改完volatile变量，并且立即同步回主存，如果CPU核心B的工作内存中也缓存了这个变量，那么B的这个变量将立即失效，当B想要修改这个变量的时候，B必须从主存重新获取变量的值。

2.指令有序性（内存屏障）

在单例模式中，Instance inst = new Instance();  这一句，就不是原子操作，它可以分成三步原子指令：

1，分配内存地址；

2，new一个Instance对象；

3，将内存地址赋值给inst；

CPU为了提高执行效率，这三步操作的顺序可以是123，也可以是132，如果是132顺序的话，当把内存地址赋给inst后，inst指向的内存地址上面还没有new出来单例对象，这时候，如果就拿到inst的话，它其实就是空的，会报空指针异常。这就是为什么双重检查单例模式中，单例对象要加上volatile关键字。

内存屏障有三种类型和一种伪类型：

a、lfence：即读屏障(Load Barrier)，在读指令前插入读屏障，可以让高速缓存中的数据失效，重新从主内存加载数据，以保证读取的是最新的数据。
b、sfence：即写屏障(Store Barrier)，在写指令之后插入写屏障，能让写入缓存的最新数据写回到主内存，以保证写入的数据立刻对其他线程可见。
c、mfence，即全能屏障，具备ifence和sfence的能力。
d、Lock前缀：Lock不是一种内存屏障，但是它能完成类似全能型内存屏障的功能。

 

#### 并发三特性总结

| 特性   | volatile     | synchronized | Lock     | Atomic   |
| :----- | :----------- | :----------- | :------- | :------- |
| 原子性 | 无法保障     | 可以保障     | 可以保障 | 可以保障 |
| 可见性 | 可以保障     | 可以保障     | 可以保障 | 可以保障 |
| 有序性 | 一定程度保障 | 可以保障     | 可以保障 | 无法保障 |

#### AtomicLong

 • Atomic类是基于UnSafe类的CAS compareAndSet方法实现线程安全(无锁或者乐观锁)，通过系统原语实现，不能被打断，不会在compare相等的时候再被其他线程修改，

#### CAS 的ABA问题

一般类型AtomicInteger等基本类型无需关心。对象引用类型可能有问题，ABA问题的解决思路就是在变量前面添加版本号，每次变量更新的时候都把版本号加一，这样变化过程就从“A－B－A”变成了“1A－2B－3A”。

- JDK从1.5开始提供了AtomicStampedReference类来解决ABA问题，具体操作封装在compareAndSet()中。compareAndSet()首先检查当前引用和当前标志与预期引用和预期标志是否相等，如果都相等，则以原子方式将引用值和标志的值设置为给定的更新值。

- ### AtomicReference

  **CAS只能保证一个共享变量的原子操作**。对一个共享变量执行操作时，CAS能够保证原子操作，但是对多个共享变量操作时，CAS是无法保证操作的原子性的。

  - Java从1.5开始JDK提供了AtomicReference类来保证引用对象之间的原子性，可以把多个变量放在一个对象里来进行CAS操作。AtomicReferenc的compareAndSet()方法可以使得它与期望的一个值进行比较，如果他们是相等的，AtomicReference里的对象会被设置成一个新的引用。

#### LongAdder

DoubleAccumulator、LongAccumulator、DoubleAdder、LongAdder是JDK1.8新增的部分，是对AtomicLong等类的改进。

LongAccumulator与LongAdder在高并发环境下比AtomicLong更高效。

LongAdder中会维护一组（一个或多个）变量，这些变量加起来就是要以原子方式更新的long型变量。当更新方法add(long)在线程间竞争时，该组变量可以**动态增长**以**减缓竞争**。方法sum()返回当前在维持总和的变量上的总和。 （这种机制特别像分段锁机制）

与AtomicLong相比，LongAdder更多地用于收集统计数据，而不是细粒度的同步控制。在低并发环境下，两者性能很相似。但在高并发环境下，LongAdder有着明显更高的吞吐量，**但是有着更高的空间复杂度**（缺点就是内存占用偏高点）。

#### RerentrantLock（可重入锁）

- 可重入，需要手动解锁，锁定几次，要解锁几次

  需要lock()和unlock()方法配合try/finally语句块来完成

- 可使用tryLock 进行尝试锁定，等待指定时间未获得锁，返回false，获得返回true

```
boolean tryLock(long time, TimeUnit unit) throws InterruptedException;
`time`:等待锁定的最长时间
`unit`: 时间单位
```



- 等待可被自己或其他线程打断，调用lockInterruptibly()加锁，使用线程对象的，Thread.interrupt();中断

  ```
  void lockInterruptibly() throws InterruptedException;
  ```

  可实现公平锁，Lock lock = new ReentrantLock(true);默认非公平。

  

  ##### Condition

  Lock可以看作是一种广义的内置锁，Condition则可以看作是一种广义的内置条件队列。每个内置锁只能有一个相关联的条件队列（条件变量等待队列）。 一个Condition和一个Lock关联在一起，就像一个条件队列和一个内置锁关联一样。

  创建一个Condition，可以在关联的Lock上调用`Lock.newCondition()`方法 。

  Condition比内置条件队列提供了更丰富的功能：在每个锁上加锁存在多个等待、条件等待是可中断的或不可中断的、基于限时的等待，以及公平的或非公平的队列操作。

  每个Lock可以拥有任意数量的Condition对象。Condition对象继承了相关的Lock对象的公平性，对于公平的锁，线程会依照FIFO顺序从`Condition.await`中释放。

  Condition的接口如下：

  ```java
  public interface Condition{
  	void await() throws InterruptedException;
      boolean await(long time, TimeUnit unit) throws InterruptedException;
      long awaitNanos(long nanosTimeout) throws InterruptedException;
      void awaitUninterruptibly();
      boolean awaitUntil(Date deadline) throws InterruptedException;
      void signal();
      void signalAll();
  }
  ```

  注意，在Condition对象中，与（等待—唤醒机制中介绍的）内置锁中`wait`、`notify`、`notifyAll`方法相对应的是`await`、`signal`、`signaAll`方法。因为Condition也继承了Object，所以它也包含了wait、notify和notifyAll方法，在使用时一定要使用正确的版本。

#### Synchronized和ReentrantLock的区别

##### 两者的共同点：

- 协调多线程对共享对象、变量的访问
- 可重入，同一线程可以多次获得同一个锁
- 都保证了可见性和互斥性
- Java中，synchronized关键字和Lock的实现类都是悲观锁。

##### 两者的不同点：

- `ReentrantLock`显示获得、释放锁，`synchronized`隐式获得释放锁

- `ReentrantLock`可响应中断、可轮回，`synchronized`是不可以响应中断的，为处理锁的不可用性提供了更高的灵活性

- `ReentrantLock`是`API`级别的，`synchronized`是`JVM`级别的

- `ReentrantLock`可以实现公平锁

- `ReentrantLock`通过`Condition`可以绑定多个条件，不同condition可以管理不同的线程，一个condition代表一个线程等待队列，可以指定某些线程被叫醒，基于2个Condition可以实现生产者消费者模型

  ![img](https://p0.meituan.net/travelcube/412d294ff5535bbcddc0d979b2a339e6102264.png)

#### 公平锁 VS 非公平锁

公平锁是指多个线程按照申请锁的顺序来获取锁，线程直接进入队列中排队，队列中的第一个线程才能获得锁。公平锁的优点是等待锁的线程不会饿死。缺点是整体吞吐效率相对非公平锁要低，等待队列中除第一个线程以外的所有线程都会阻塞，CPU唤醒阻塞线程的开销比非公平锁大。

非公平锁是多个线程加锁时直接尝试获取锁，获取不到才会到等待队列的队尾等待。但如果此时锁刚好可用，那么这个线程可以无需阻塞直接获取到锁，所以非公平锁有可能出现后申请锁的线程先获取锁的场景。非公平锁的优点是可以减少唤起线程的开销，整体的吞吐效率高，因为线程有几率不阻塞直接获得锁，CPU不必唤醒所有线程。缺点是处于等待队列中的线程可能会饿死，或者等很久才会获得锁。

#### 悲观锁和乐观锁

- 悲观锁适合写操作多的场景，先加锁可以保证写操作时数据正确。
- 乐观锁适合读操作多的场景，不加锁的特点能够使其读操作的性能大幅提升。

#### 读写锁ReadWriteLock（独享锁-写 VS 共享锁-读）

```java
public class Counter {
    private final ReadWriteLock rwlock = new ReentrantReadWriteLock();
    private final Lock rlock = rwlock.readLock();
    private final Lock wlock = rwlock.writeLock();
    private int[] counts = new int[10];

    public void inc(int index) {
        wlock.lock(); // 加写锁
        try {
            counts[index] += 1;
        } finally {
            wlock.unlock(); // 释放写锁
        }
    }

    public int[] get() {
        rlock.lock(); // 加读锁
        try {
            return Arrays.copyOf(counts, counts.length);
        } finally {
            rlock.unlock(); // 释放读锁
        }
    }
}
```

把读写操作分别用读锁和写锁来加锁，在读取时，多个线程可以同时获得读锁，这样就大大提高了并发读的执行效率。

只允许一个线程写入，允许多个线程在没有写入时同时读取，适合读多写少的场景

使用`ReadWriteLock`时，适用条件是同一个数据，有大量线程读取，但仅有少数线程修改。

#### [如果不给Read操作上锁行不行？]

锁的目的不是读的数据是错的，是保证连续读逻辑上一致的：

```
int x = obj.x;
// 这里线程可能中断
int y = obj.y;
```

假设obj的x，y是[0,1]，某个写线程修改成[2,3]，你读到的要么是[0,1]，要么是[2,3]，但是没有锁，你读到的可能是[0,3]，不加读锁读的过程中，可能被修改

#### CountDownLatch（门闩）

实例见 https://www.cnblogs.com/dolphin0520/p/3920397.html

CountDownLatch类位于java.util.concurrent包下，利用它可以实现类似计数器的功能。比如有一个任务A，它要等待其他4个任务执行完毕之后才能执行，此时就可以利用CountDownLatch来实现这种功能了，类似功能可以通过thread的join实现线程等待，CountDownLatch可以在一个线程中多次CountDown，join不能

CountDownLatch类只提供了一个构造器：

```java
public CountDownLatch(int count) {  };  //参数count为计数值
```

```java
public void await() throws InterruptedException { };   //调用await()方法的线程会被挂起，它会等待直到count值为0才继续执行
public boolean await(long timeout, TimeUnit unit) throws InterruptedException { };  //和await()类似，只不过等待一定的时间后count值还没变为0的话就会继续执行
public void countDown() { };  //将count值减1
```

CountDownLatch无法进行重复使用

#### CyclicBarrier（栅栏）

实例见  https://www.cnblogs.com/dolphin0520/p/3920397.html

字面意思回环栅栏，通过它可以实现让一组线程等待至某个状态之后再全部同时执行。叫做回环是因为当所有等待线程都被释放以后，CyclicBarrier可以被重用。我们暂且把这个状态就叫做barrier，当调用await()方法之后，线程就处于barrier了。

CyclicBarrier类位于java.util.concurrent包下，CyclicBarrier提供2个构造器：

```java
public CyclicBarrier(int parties, Runnable barrierAction) {
}
 
public CyclicBarrier(int parties) {
}
```

　　参数parties指让parties个线程或者任务等待至barrier状态；参数barrierAction为当这些线程都达到barrier状态时会执行的内容。会随机选择一个线程执行barrierAction的内容，执行完之后才会接着执行parties个线程await后面的内容

　　然后CyclicBarrier中最重要的方法就是await方法，它有2个重载版本：

```java
public int await() throws InterruptedException, BrokenBarrierException { };
public int await(long timeout, TimeUnit unit)throws InterruptedException,BrokenBarrierException,TimeoutException { };
```

 　第一个版本比较常用，用来挂起当前线程，直至所有线程都到达barrier状态再同时执行后续任务；

　第二个版本是让这些线程等待至一定的时间，如果还有线程没有到达barrier状态就直接让到达barrier的线程执行后续任务。

CyclicBarrier是可以重用的，初次的N个线程越过barrier状态后，又可以用来进行新一轮的使用

#### Semaphore

Semaphore 是 synchronized 的加强版，作用是控制线程的并发数量（限流）

```java
Semaphore semaphore = new Semaphore(1);// 同步关键类，构造方法传入的数字是多少，则同一个时刻，只运行多少个进程同时运行制定代码
Semaphore semaphore = new Semaphore(int permits , boolean isFair) //是否公平锁，isFair 为 true，则表示公平，先启动的线程先获得锁。
```

```java
public void doSomething() {
        try {
            /**
             * 在 semaphore.acquire() 和 semaphore.release()之间的代码，同一时刻只允许制定个数的线程进入，
             * 因为semaphore的构造方法是1，则同一时刻只允许一个线程进入，其他线程只能等待。
             * */
            semaphore.acquire();
            System.out.println(Thread.currentThread().getName() + ":doSomething start-" + getFormatTimeStr());
            Thread.sleep(2000);
            System.out.println(Thread.currentThread().getName() + ":doSomething end-" + getFormatTimeStr());
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally{
            semaphore.release();
        }
    }
```

semaphore.acquire() 获得一个信号，可用信号数-1

semaphore.release() 释放一个信号，可用信号数+1

方法 tryAcquire() 、 tryAcquire(int permits)、 tryAcquire(int permits , long timeout , TimeUint unit) 的使用：

　　tryAcquire 方法，是 acquire 的扩展版，tryAcquire 作用是尝试得获取通路，如果未传参数，就是尝试获取一个通路，如果传了参数，就是尝试获取 permits 个 通路 、在指定时间 timeout 内 尝试 获取 permits 个通路。

#### Phaser

#### Exchanger

Exchanger的作用就是为了两个线程之间交换数据，他提供了一个内部方法exchange，这个内部方法就好比是一个同步点，只有两个方法都到达同步点，才可以交换数据

1.一定 是两个线程

2.如果一个线程A先调用了exchange，则A阻塞，等到B调用exchange才开始交换

#### LockSupport

> https://www.cnblogs.com/qingquanzi/p/8228422.html?share_token=c680257c-9bce-4eff-afd8-888ce918b7a9

在没有LockSupport之前，线程的挂起和唤醒咱们都是通过Object的wait和notify/notifyAll方法实现。

LockSupport 内部通过Unsafe类实现

```
LockSupport.park(); //当前线程阻塞（停车）
LockSupport.unpark(Thread thread); //将thread线程放行
```

```
public class TestObjWait {

    public static void main(String[] args)throws Exception {
        final Object obj = new Object();
        Thread A = new Thread(new Runnable() {
            @Override
            public void run() {
                int sum = 0;
                for(int i=0;i<10;i++){
                    sum+=i;
                }
                LockSupport.park();
                System.out.println(sum);
            }
        });
        A.start();
        //睡眠一秒钟，保证线程A已经计算完成，阻塞在wait方法，使用unpark方法可以不用睡眠，先调用unpark
        //Thread.sleep(1000);
        LockSupport.unpark(A);
    }
}
```



##### **LockSupport比Object的wait/notify有两大优势**：

①LockSupport不需要在同步代码块里 。所以线程间也不需要维护一个共享的同步对象了，实现了线程间的解耦。

②unpark函数可以先于park调用，所以不需要担心线程间的执行的先后顺序。

多次调用unpark方法和调用一次unpark方法效果一样，因为都是直接将_counter赋值为1，而不是加1

#### AQS（AbstractQueuedSynchronizer）

##### AQS内部使用的是volatile state和CAS完成对State值的修改

Sync是AQS的一个子类，这种结构在CountDownLatch、ReentrantLock、Semaphore、ReentrantReadWriteLock里面也都存在。他们是基于AQS实现的，用法是通过继承AQS实现其模版方法，然后将子类作为同步组件的内部类。

##### volatile state字段

AQS使用一个整数state（int类型，32位）以表示状态，并通过getState、setState及compareAndSetState等protected类型方法进行状态转换。巧妙的使用state，可以表示任何状态，如：

**【ReentrantLock】**：**state用于记录锁的持有状态和重入次数**，state=0表示没有线程持有锁；state=1表示有一个线程持有锁；state=N表示exclusiveOwnerThread这个线程N次重入了这个锁。

**【ReentrantReadWriteLock】**：**state用于记录读写锁的占用状态和持有线程数量（读锁）、重入次数（写锁）**，state的高16位记录持有读锁的线程数量，低16位记录写锁线程重入次数，如果这16位的值是0，表示没有线程占用锁，否则表示有线程持有锁。另外针对读锁，每个线程获取到的读锁次数由本地线程变量中的HoldCounter记录。

**【Semaphore】：****state用于计数。**state=N表示还有N个信号量可以分配出去，state=0表示没有信号量了，此时所有需要acquire信号量的线程都等着；

**【CountDownLatch】：state也用于计数**，每次countDown都减一，减到0的时候唤醒被await阻塞的线程。

在独享锁中这个值通常是0或者1（如果是重入锁的话state值就是重入的次数），在共享锁中state就是持有锁的数量。

##### AQS-state-ReentrantReadWriteLock

但是在ReentrantReadWriteLock中有读、写两把锁，所以需要在一个整型变量state上分别描述读锁和写锁的数量（或者也可以叫状态）。于是将state变量“按位切割”切分成了两个部分，高16位表示读锁状态（读锁个数），低16位表示写锁状态（写锁个数）。如下图所示：

![img](https://awps-assets.meituan.net/mit-x/blog-images-bundle-2018b/8793e00a.png)

#### **ThreadLocal**

1.ThreadLocal类封装了getMap()、Set()、Get()、Remove()4个核心方法。

2.通过\**getMap()获取\**每个子线程Thread持有自己的ThreadLocalMap实例, 因此它们是不存在并发竞争的。可以理解为每个线程有自己的变量副本。

3.ThreadLocalMap中Entry[]数组存储数据，初始化长度16，后续每次都是2倍扩容。主线程中定义了几个变量，Entry[]才有几个key。Entry是弱引用(WeakReference).Entry其中Key即是ThreadLocal变量本身，Value则是具体该线程中的变量副本值

4.`Entry`的key是对ThreadLocal的弱引用，当抛弃掉ThreadLocal对象时，垃圾收集器会忽略这个key的引用而清理掉ThreadLocal对象， 防止了内存泄漏。

**最佳实践：在ThreadLocal使用前后都调用remove清理，同时对异常情况也要在finally中清理。**

#### java 的引用

- 强引用(StrongReference)

  **强引用**是使用最普遍的引用。如果一个对象具有强引用，那**垃圾回收器**绝不会回收它。如下：

  ```java
      Object strongReference = new Object();
  ```

  当**内存空间不足**时，`Java`虚拟机宁愿抛出`OutOfMemoryError`错误，使程序**异常终止**，也不会靠随意**回收**具有**强引用**的**对象**来解决内存不足的问题。 如果强引用对象**不使用时**，需要弱化从而使`GC`能够回收

  ->strongReference=null;

- 软引用(SoftReference)

  如果一个对象只具有**软引用**，则**内存空间充足**时，**垃圾回收器**就**不会**回收它；如果**内存空间不足**了，就会**回收**这些对象的内存。只要垃圾回收器没有回收它，该对象就可以被程序使用。

  > 软引用可用来实现内存敏感的高速缓存。

  ```java
      // 强引用
      String strongReference = new String("abc");
      // 软引用
      String str = new String("abc");
      SoftReference<String> softReference = new SoftReference<String>(str);
  ```

- 弱引用(WeakReference)

  ThreadLocal里使用的是弱引用

  只要垃圾回收就会回收弱引用对象

  ```java
  String str = new String("abc");
  WeakReference<String> weakReference = new WeakReference<>(str);
  ```

- 虚引用(PhantomReference)

在任何时候都可能被垃圾回收器回收，程序员基本不使用

**应用场景：**

**虚引用**主要用来**跟踪对象**被垃圾回收器**回收**的活动。 **虚引用**与**软引用**和**弱引用**的一个区别在于：

> 虚引用必须和引用队列(ReferenceQueue)联合使用。当垃圾回收器准备回收一个对象时，如果发现它还有虚引用，就会在回收对象的内存之前，把这个虚引用加入到与之关联的引用队列中。可以通过监控这个队列，虚引用回收时收到通知。

```java
String str = new String("abc");
ReferenceQueue queue = new ReferenceQueue();
// 创建虚引用，要求必须与一个引用队列关联
PhantomReference pr = new PhantomReference(str, queue);
```
> Java中4种引用的级别和强度由高到低依次为：强引用 -> 软引用 -> 弱引用 -> 虚引用
>
> 通过表格来说明一下，如下：

| 引用类型 | 被垃圾回收时间 | 用途               | 生存时间          |
| -------- | -------------- | ------------------ | ----------------- |
| 强引用   | 从来不会       | 对象的一般状态     | JVM停止运行时终止 |
| 软引用   | 当内存不足时   | 对象缓存           | 内存不足时终止    |
| 弱引用   | 正常垃圾回收时 | 对象缓存           | 垃圾回收后终止    |
| 虚引用   | 正常垃圾回收时 | 跟踪对象的垃圾回收 | 垃圾回收后终止    |

### java集合

#### Map

##### HashTable

所有方法均加sync锁，线程安全

##### HashMap

无锁，线程不安全

##### ConcurrentHashMap

 无序的线程安全  ，对应HashMap

##### ConcurrentSkipListMap

 有序的线程安全。对应线程不安全的TreeMap

底层是通过跳表（SkipList）(用空间换时间)来实现的。跳表是一个链表，但是通过使用“跳跃式”查找的方式使得插入、读取数据时复杂度变成了O（logn）。

![img](https://upload-images.jianshu.io/upload_images/263562-b5b87844c6ac8496.gif?imageMogr2/auto-orient/strip|imageView2/2/w/960/format/webp)

**ConcurrentSkipListMap线程安全的原理与非阻塞队列ConcurrentBlockingQueue的原理一样：利用底层的插入、删除的CAS原子性操作，通过死循环不断获取最新的结点指针来保证不会出现竞态条件。**





#### Collection

##### List

ArrayList

LinkedList

CopyOnWriteList

 读无锁，写加sync锁，适合读多写少场景

##### Set

##### Queue

#### Queue类图

![img](https://imgconvert.csdnimg.cn/aHR0cDovL3NpMS5nbzJ5ZC5jb20vL2dldC1pbWFnZS8wZ1JMY2F0b2kycQ?x-oss-process=image/format,png)



##### `ArrayBlockingQueue` 数组队列

##### `LinkedBlockingQueue`

是一个双向链表的队列。常用于 “工作窃取算法”

##### PriorityQueue

带优先级顺序的队列。默认使用的是最小堆，按照最小排最先，可以自定义比较器，可变为最大堆

##### DelayQueue

是一个支持延时获取元素的无界阻塞队列。内部用 `PriorityQueue` 实现，可实现按时间顺序进行任务调度

##### `PriorityBlockingQueue`

 是一个支持优先级的无界阻塞队列，和 `DelayWorkQueue` 类似

##### SynchronousQueue

 容量为0，不能调用add添加元素，只能用put进行阻塞式往里加，由另一个线程take，实现2个线程之间的数据传递

##### LinkedTransferQueue

 使用transfer方法传递数据，对方没有取走之前阻塞，可实现多个线程间传递

`算是 LinkedBolckingQueue 和 SynchronousQueue 和合体。SynchronousQueue` 内部无法存储元素，当要添加元素的时候，需要阻塞，不够完美，`LinkedBolckingQueue` 则内部使用了大量的锁，性能不高

![img](https://imgconvert.csdnimg.cn/aHR0cDovL3NpMS5nbzJ5ZC5jb20vL2dldC1pbWFnZS8wZ1JMY2NQbGNRNA?x-oss-process=image/format,png)

![img](https://imgconvert.csdnimg.cn/aHR0cDovL3NpMS5nbzJ5ZC5jb20vL2dldC1pbWFnZS8wZ1JMY2ZFeUJmNg?x-oss-process=image/format,png)



#### 线程池

https://tech.meituan.com/2020/04/02/java-pooling-pratice-in-meituan.html

##### ThreadPoolExecutor线程池的7个参数

1.**corePoolSize**：

（核心线程数大小：不管它们创建以后是不是空闲的。线程池需要保持 corePoolSize 数量的线程，除非设置了 allowCoreThreadTimeOut。）

2.**maximumPoolSize**：

（最大线程数：线程池中最多允许创建 maximumPoolSize 个线程。）

如果workerCount >= corePoolSize && workerCount < maximumPoolSize，且线程池内的阻塞队列已满，则创建并启动一个线程来执行新提交的任务，当队列满了，才启用。

3.**keepAliveTime**：

（存活时间：如果经过 keepAliveTime 时间后，超过核心线程数的线程还没有接受到新的任务，那就回收。）

4.**unit**：

（keepAliveTime 的时间单位。）

5.**workQueue**：

（存放待执行任务的队列：当提交的任务数超过核心线程数大小后，再提交的任务就存放在这里。它仅仅用来存放被 execute 方法提交的 Runnable 任务。所以这里就不要翻译为工作队列了，好吗？不要自己给自己挖坑。）

6.**threadFactory**：

（线程工程：用来创建线程工厂。比如这里面可以自定义线程名称，当进行虚拟机栈分析时，看着名字就知道这个线程是哪里来的，不会懵逼。）

7.**handler** ：

（拒绝策略：当队列里面放满了任务、最大线程数的线程都在工作时，这时继续提交的任务线程池就处理不了，应该执行怎么样的拒绝策略。）

##### 问题一：线程池被创建后里面有线程吗？如果没有的话，你知道有什么方法对线程池进行预热吗？

```
prestartAllCoreThreads() //预热所有core线程
prestartCoreThread() //预热一个core线程
```

##### 可动态修改参数（线上可通过配置中心）

```
setCorePoolSize
setMaximumPoolSize

```

动态修改队列大小？

LinkedBlockingQueue 粘贴一份出来，修改个名字，然后把 Capacity 参数的 final 修饰符去掉，并提供其对应的 get/set 方法。使用你自己定义的Queue构建ThreadPoolExecutor

##### 问题二：核心线程数会被回收吗？需要什么设置？

allowCoreThreadTimeOut 该值默认为 false。设置为true即可被回收

```
allowCoreThreadTimeOut(true)
```

##### 拒绝策略

![img](https://p0.meituan.net/travelcube/9ffb64cc4c64c0cb8d38dac01c89c905178456.png)

#### JDK实现的几个线程池，不建议使用，生产中一定自己实现线程池

**newFixedThreadPool(int nThreads)**

 创建一个重用固定数量线程的线程池

```java
    public static ExecutorService newFixedThreadPool(int nThreads) {
        return new ThreadPoolExecutor(nThreads, nThreads,
                                      0L, TimeUnit.MILLISECONDS,
                                      new LinkedBlockingQueue<Runnable>());
    }
```

newWorkStealingPool(int parallelism)

工作窃取线程池，内部使用ForkJoinPool实现，能够合理的使用CPU,进行并发运行任务

```java
    public static ExecutorService newWorkStealingPool() {
        return new ForkJoinPool
            (Runtime.getRuntime().availableProcessors(),
             ForkJoinPool.defaultForkJoinWorkerThreadFactory,
             null, true);
    }
    public static ExecutorService newWorkStealingPool(int parallelism) {
        return new ForkJoinPool
            (parallelism,
             ForkJoinPool.defaultForkJoinWorkerThreadFactory,
             null, true);
    }

```



**newSingleThreadExecutor()**

池中就一个线程。通过这个线程来处理所有的任务

```java
    public static ExecutorService newSingleThreadExecutor() {
        return new FinalizableDelegatedExecutorService
            (new ThreadPoolExecutor(1, 1,
                                    0L, TimeUnit.MILLISECONDS,
                                    new LinkedBlockingQueue<Runnable>()));
    }
```

newCachedThreadPool()

使用SynchronousQueue，队列不存储线程，来线程即执行。

```java
    public static ExecutorService newCachedThreadPool() {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                      60L, TimeUnit.SECONDS,
                                      new SynchronousQueue<Runnable>());
    }
```

**newSingleThreadScheduledExecutor()**

创建一个单线程的线程池，此线程池的的线程可以定时周期性的运行任务

**newScheduledThreadPool(int corePoolSize)**

创建一个固定大小的线程池。此线程池支持定时以及周期性执行任务的需求

#### ForkJoinPool

核心思想是将大的任务拆分成多个小任务（即fork），然后在将多个小任务处理汇总到一个结果上（即join），非常像MapReduce处理原理

![](https://img-blog.csdn.net/20171128190912485?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvbml5dWVsaW4xOTkw/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

ForkJoinTask

ForkJoinTask：我们要使用ForkJoin框架，必须首先创建一个ForkJoin任务。它提供在任务中执行fork()和join()操作的机制，通常情况下我们不需要直接继承ForkJoinTask类，而只需要继承它的子类，Fork/Join框架提供了以下两个子类：

- RecursiveAction：用于没有返回结果的任务。 
- RecursiveTask ：用于有返回结果的任务。

##### java8 ParallelStreams

内部使用共享线程池ForkJoinPool.commonPool()

我们可以创建自己的线程池，可以避免共享线程池

ForkJoinPool forkJoinPool = new ForkJoinPool(<numThreads>);