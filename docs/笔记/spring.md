# Spring 

## spring mvc从请求进来到响应出去的过程

![这里写图片描述](https://img-blog.csdn.net/20170301213745588?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdWhnYWdudQ==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

* #### 如何保证调用服务的速率是均匀的

  漏桶算法和令牌桶算法

##  1.Spring 循环依赖问题

https://zhuanlan.zhihu.com/p/62382615

https://www.cnblogs.com/jajian/p/10241932.html

#### Spring 如何解决的

- Spring 为了解决单例的循环依赖问题，使用了 **三级缓存** ，递归调用时发现 Bean 还在创建中即为循环依赖

- A 创建过程中需要 B，于是 **A 将自己放到三级缓里面** ，去实例化 B

- B 实例化的时候发现需要 A，于是 B 先查一级缓存，没有，再查二级缓存，还是没有，再查三级缓存，找到了！

- - **然后把三级缓存里面的这个 A 放到二级缓存里面，并删除三级缓存里面的 A**
  - B 顺利初始化完毕，**将自己放到一级缓存里面**（此时B里面的A依然是创建中状态）

- 然后回来接着创建 A，此时 B 已经创建结束，直接从一级缓存里面拿到 B ，然后完成创建，**并将自己放到一级缓存里面**

  #### 总结

- setter方式 - 单例(singleton)，默认方式，可以解决循环依赖

- 构造方法方式 ，不能解决循环依赖（无法实例化）

- setter方式 - 原型(prototype)  ，不能解决循环依赖 （缓存中只放单例模式的bean，因为原型模式不知道什么时候创建合适）

##  3.spring ioc如何理解的？处理了哪些问题？

#### [IoC](https://snailclimb.gitee.io/javaguide/#/docs/system-design/framework/spring/Spring常见问题总结?id=ioc)

IoC（Inverse of Control:控制反转）是一种**设计思想**，就是 **将原本在程序中手动创建对象的控制权，交由Spring框架来管理。** IoC 在其他语言中也有应用，并非 Spring 特有。 **IoC 容器是 Spring 用来实现 IoC 的载体， IoC 容器实际上就是个Map（key，value）,Map 中存放的是各种对象。**

将对象之间的相互依赖关系交给 IoC 容器来管理，并由 IoC 容器完成对象的注入。这样可以很大程度上简化应用的开发，把应用从复杂的依赖关系中解放出来。 **IoC 容器就像是一个工厂一样，当我们需要创建一个对象的时候，只需要配置好配置文件/注解即可，完全不用考虑对象是如何被创建出来的。**

管理各个实例之间的依赖关系，对象与对象之间松散耦合，也利于功能的复用。

#### [AOP](https://snailclimb.gitee.io/javaguide/#/docs/system-design/framework/spring/Spring常见问题总结?id=aop)

AOP(Aspect-Oriented Programming:面向切面编程)能够将那些与业务无关，**却为业务模块所共同调用的逻辑或责任（例如事务处理、日志管理、权限控制等）封装起来**，便于**减少系统的重复代码**，**降低模块间的耦合度**，并**有利于未来的可拓展性和可维护性**。

**Spring AOP就是基于动态代理的**，如果要代理的对象，实现了某个接口，那么Spring AOP会使用**JDK Proxy**，去创建代理对象，而对于没有实现接口的对象，就无法使用 JDK Proxy 去进行代理了，这时候Spring AOP会使用**Cglib** ，这时候Spring AOP会使用 **Cglib** 生成一个被代理对象的子类来作为代理.

也可以使用 AspectJ ,Spring AOP 已经集成了AspectJ ，AspectJ 应该算的上是 Java 生态系统中最完整的 AOP 框架了。

##    4.spring 自动装配如何实现的？

在Spring中，我们有4中方式可以装配Bean的属性。

1，byName
通过byName方式自动装配属性时，是在定义Bean的时候，在property标签中设置autowire属性为byName，那么Spring会自动寻找一个与该属性名称相同或id相同的Bean，注入进来。

2，byType  

通过byType方式自动注入属性时，是在定义Bean的时候，在property标签中设置autowire属性为byType，那么Spring会自动寻找一个与该属性类型相同的Bean，注入进来。
3，constructor
通过构造器自动注入。在定义Bean时，在bean标签中，设置autowire属性为constructor，那么，Spring会寻找与该Bean的构造函数各个参数类型相匹配的Bean，通过构造函数注入进来。
4，autodetect
自动装配。如果想进行自动装配，但不知道使用哪种类型的自动装配，那么就可以使用autodetect，让容器自己决定。这是通过在定义Bean时，设置bean标签的autowire属性为autodetect来实现的。设置为autodetect时，Spring容器会首先尝试构造器注入，然后尝试按类型注入。
默认情况下，Spring是不进行自动装配的。我们可以在xml中，设置beans标签的default-autowire属性为byName，byType等，来设置所有bean都进行自动装配。

## FactoryBean 和 BeanFactory区别

https://www.cnblogs.com/aspirant/p/9082858.html

BeanFactory，以Factory结尾，表示它是一个工厂类(接口)， **它负责生产和管理bean的一个工厂**。在Spring中，**BeanFactory是IOC容器的核心接口，它的职责包括：实例化、定位、配置应用程序中的对象及建立这些对象间的依赖。BeanFactory只是个接口，并不是IOC容器的具体实现，但是Spring容器给出了很多种实现，如 DefaultListableBeanFactory、XmlBeanFactory、ApplicationContext等，其中**XmlBeanFactory就是常用的一个，该实现将以XML方式描述组成应用的对象及对象间的依赖关系。



FactoryBean是一个接口，当在IOC容器中的Bean实现了FactoryBean后，通过getBean(String BeanName)获取到的Bean对象并不是FactoryBean的实现类对象，而是这个实现类中的getObject()方法返回的对象。要想获取FactoryBean的实现类，就要getBean(&BeanName)，在BeanName之前加上&。

## Spring Bean 生命周期？

https://crossoverjie.top/2018/03/21/spring/spring-bean-lifecycle/

Spring 只帮我们管理单例模式 Bean 的**完整**生命周期，对于 prototype 的 bean ，Spring 在创建好交给使用者之后则不会再管理后续的生命周期。

![img](https://pic2.zhimg.com/80/v2-5137fbc40dc0028787214b248ce2168c_720w.jpg?source=1940ef5c)

### InitializingBean, DisposableBean 接口

还可以实现 `InitializingBean,DisposableBean` 这两个接口，也是在初始化以及销毁阶段调

### 实现 *Aware 接口

`*Aware` 接口可以用于在初始化 bean 时获得 Spring 中的一些对象，如获取 `Spring 上下文`等。

ApplicationContextAware 等

### BeanPostProcessor 增强处理器

实现 BeanPostProcessor 接口，Spring 中所有 bean 在做初始化时都会调用该接口中的两个方法，可以用于对一些特殊的 bean 进行处理

postProcessBeforeInitialization  预初始化 初始化之前调用

postProcessAfterInitialization 后初始化  bean 初始化完成调用

# Spring Boot

## [Spring Boot 自动装配](https://www.cnblogs.com/ashleyboy/p/9425179.html)

### @Configuration和@Bean

使用注解@Configuration,向Spring表明这是一个配置类，类里的含有@Bean注解的方法都会被Spring调用，返回对象将会为Spring容器管理的Bean，注解@Bean可以给Bean指定一个名称，如@Bean(“xxxBean”),如不指定，则将会以该方法名作为Bean的名称

```java
@Configuration
public class TestConfiguration {

    @Bean
    public  EncodingConvert createUTF8EncodingConvert(){
        return  new UTF8EncodingConvert();
    }

    @Bean
    public  EncodingConvert createGBKEncodingConvert(){
        return  new GBKEncodingConvert();
    }
}
```

## 条件装配

Spting Boot提供一系列@ConditionalOnXXX的注解用于不同场景下的Bean装配。基本上通过注解名称就能明白用途，@ConditionalOnXXX注解可以作用于类或者方法上。

1.作用用于类上，需要和 @Configuration注解一起使用，决定该配置类是否生效

2.作用于方法上，需要和@Bean注解一起使用，判断该@Bean是否生成

### Bean条件装配

Spring Boot可以通过有没有指定Bean来决定是否配置当前Bean，

使用@ConditionalOnBean，在当前上下文中存在某个对象时，才会实例化当前Bean；

使用@ConditionalOnMissingBean，在当前上下文中不存在某个对象时，才会实例化当前Bean。

### Class条件装配

Class条件装配是按照某个类是否在Classpath中来判断是否需要配置Bean。

@ConditionalOnClass：表示classpath有指定的类时，配置才生效

@ConditionalOnMissingClass：表示当classpath中没有指定类，则配置生效

### Environment装配

Spring Boot可以根据Environment属性来决定是否实例化Bean，通过@ConditionalOnProperty注解来实现。根据注解属性name读取Spring Boot的Environment的变量包含的属性 ，再根据属性值与注解属性havingValue的值比较，判断否实例化Bean，如果没有指定注解属性havingValue，name只要environment属性值不为false，都会实例化Bean。MatchIfMissing=true，表示如果evironment没有包含xxx.xxx.enabled属性,也会实例化Bean，默认是false。

### 其他条件装配注解：

@ConditionalOnExpression ：当表达式为true时，才会实例化一个Bean，支持SpEL表达式

@ConditionalOnNotWebApplication：表示不是web应用，才会实例化一个Bean

## 自定义条件装配（实现Condition接口）

当Spring Boot提供的一些列@ConditionalOnXXX注解无法满足需求时，也可以手动构造一个Condition实现，使用注解@Conditional来引用Condition实现。

重写matches方法，返回true或false来判断是否装配



# EnableAutoConfiguration注解的工作原理

https://www.jianshu.com/p/464d04c36fb1


