# Flink 1.7.2 dataset transformation 示例

## 源码
- https://github.com/opensourceteams/flink-maven-scala

## 概述
- Flink transformation示例 
- map,flatMap,filter,reduce,groupBy reduceGroup combineGroup Aggregate(sum,max,min)
- distinct join join funtion leftOuterJoin rightOuterJoin fullOuterJoin union first coGroup cross

## transformation


### map
- 对集合元素，进行一一遍历处理
- 示例功能:给集合中的每一一行，都拼接字符串


```aidl
package com.opensourceteams.module.bigdata.flink.example.dataset.transformation.map

import org.apache.flink.api.scala.ExecutionEnvironment

import org.apache.flink.api.scala._

object Run {

  def main(args: Array[String]): Unit = {


    val env = ExecutionEnvironment.getExecutionEnvironment

    val dataSet = env.fromElements("c a b d a c","d c a b c d")


    val dataSet2 = dataSet.map(_.toUpperCase + "字符串连接")
    dataSet2.print()

  }

}



```
- 输出结果

```aidl
C A B D A C字符串连接
D C A B C D字符串连接

```



### flatMap
- 对集合元素，进行一一遍历处理,并把子集合中的数据拉到一个集合中
- 示例功能:把行进行拆分后，再把不同的行拆分之后的元素，汇总到一个集合中


```aidl

package com.opensourceteams.module.bigdata.flink.example.dataset.transformation.flatmap

import org.apache.flink.api.scala.{ExecutionEnvironment, _}

object Run {

  def main(args: Array[String]): Unit = {


    val env = ExecutionEnvironment.getExecutionEnvironment

    val dataSet = env.fromElements("c a b d a c","d c a b c d")


    val dataSet2 = dataSet.flatMap(_.toUpperCase().split(" "))
    dataSet2.print()

  }

}



```
- 输出结果

```aidl
C
A
B
D
A
C
D
C
A
B
C
D

```




### filter
- 对集合元素，进行一一遍历处理,只过滤满足条件的元素
- 示例功能:过滤空格数据


```aidl
package com.opensourceteams.module.bigdata.flink.example.dataset.transformation.filter

import org.apache.flink.api.scala.{ExecutionEnvironment, _}

/**
  * filter 过滤器，对数据进行过滤处理
  */
object Run {

  def main(args: Array[String]): Unit = {


    val env = ExecutionEnvironment.getExecutionEnvironment

    val dataSet = env.fromElements("c a b d a    c","d c   a b c d")


    val dataSet2 = dataSet.flatMap(_.toUpperCase().split(" ")).filter(_.nonEmpty)
    dataSet2.print()

  }

}


```
- 输出结果

```aidl
C
A
B
D
A
C
D
C
A
B
C
D


```




### reduce
- 对集合中所有元素，两两之间进行reduce函数表达式的计算
- 示例功能:统计所有数据的和


```aidl
package com.opensourceteams.module.bigdata.flink.example.dataset.transformation.map

package com.opensourceteams.module.bigdata.flink.example.dataset.transformation.reduce

import org.apache.flink.api.scala.{ExecutionEnvironment, _}


/**
  * 相当于进行所有元素的累加操作，求和操作
  */
object Run {

  def main(args: Array[String]): Unit = {


    val env = ExecutionEnvironment.getExecutionEnvironment

    val dataSet = env.fromElements(3,5,8,9)
    //  3 + 5 + 8 + 9


    val dataSet2 = dataSet.reduce((a,b) => {
      println(s"${a} + ${b} = ${a +b}")
      a + b
    })
    dataSet2.print()

  }

}



```
- 输出结果

```aidl
3 + 5 = 8
8 + 8 = 16
16 + 9 = 25
25


```






### reduce (先groupBy)
- 对集合中所有元素，按指定的key分组，按组执行reduce
- 示例功能:按key分组统计所有数据的和


```aidl
package com.opensourceteams.module.bigdata.flink.example.dataset.transformation.reduce

import org.apache.flink.api.scala.{ExecutionEnvironment, _}


/**
  * 相当于按key进行分组,然后对组内的元素进行的累加操作，求和操作
  */
object ReduceGroupRun2 {

  def main(args: Array[String]): Unit = {


    val env = ExecutionEnvironment.getExecutionEnvironment

    val dataSet = env.fromElements(("a",1),("b",1),("c",1),("a",1),("c",1),("d",1),("f",1),("g",1),("f",1))

    /**
      * (a,1)
      * (b,1)
      * (c,1)
      * (a,1)
      * (c,1)
      * (d,1)
      * (f,1)
      * (g,1)
      */

    val dataSet2 = dataSet.groupBy(0).reduce((x,y) => {
      (x._1,x._2 + y._2)
    }
    )



    dataSet2.print()

  }

}


```
- 输出结果

```aidl
(d,1)
(a,2)
(f,2)
(b,1)
(c,2)
(g,1)

```








### groupBy   (class Fields)
- 对集合中所有元素，按用例类中的属性，进行分组
- 示例功能:按key分组统计所有数据的和


```aidl
package com.opensourceteams.module.bigdata.flink.example.dataset.transformation.groupByClassFields

import org.apache.flink.api.scala.{ExecutionEnvironment, _}


/**
  * 相当于按key进行分组,然后对组内的元素进行的累加操作，求和操作
  */
object ReduceGroupRun {

  def main(args: Array[String]): Unit = {


    val env = ExecutionEnvironment.getExecutionEnvironment

    val dataSet = env.fromElements("a","b","c","a","c","d","f","g","f")

    /**
      * (a,1)
      * (b,1)
      * (c,1)
      * (a,1)
      * (c,1)
      * (d,1)
      * (f,1)
      * (g,1)
      */

    val dataSet2 = dataSet.map(WordCount(_,1)).groupBy("word").reduce((x,y) => WordCount(x.word, x.count + y.count))



    dataSet2.print()

  }

  case class WordCount(word:String,count:Int)

}



```
- 输出结果

```aidl
WordCount(d,1)
WordCount(a,2)
WordCount(f,2)
WordCount(b,1)
WordCount(c,2)
WordCount(g,1)

```



### groupBy   (key Selector)
- 对集合中所有元素，按key 选择器进行分组
- 示例功能:按key分组统计所有数据的和


```aidl
package com.opensourceteams.module.bigdata.flink.example.dataset.transformation.groupByKeySelector

import org.apache.flink.api.scala.{ExecutionEnvironment, _}


/**
  * 相当于按key进行分组,然后对组内的元素进行的累加操作，求和操作
  */
object ReduceGroupRun {

  def main(args: Array[String]): Unit = {


    val env = ExecutionEnvironment.getExecutionEnvironment

    val dataSet = env.fromElements("a","b","c","a","c","d","f","g","f")

    /**
      * (a,1)
      * (b,1)
      * (c,1)
      * (a,1)
      * (c,1)
      * (d,1)
      * (f,1)
      * (g,1)
      */

    val dataSet2 = dataSet.map((_,1)).groupBy(_._1).reduce((x,y) => (x._1,x._2 +y._2))



    dataSet2.print()

  }

}


```
- 输出结果

```aidl
WordCount(d,1)
WordCount(a,2)
WordCount(f,2)
WordCount(b,1)
WordCount(c,2)
WordCount(g,1)

```




### reduceGroup
- 对集合中所有元素，按指定的key分组，把相同key的元素，做为参数，调用reduceGroup()函数
- 示例功能:按key分组统计所有数据的和


```aidl

package com.opensourceteams.module.bigdata.flink.example.dataset.transformation.reduceGroup

import org.apache.flink.api.scala.{ExecutionEnvironment, _}
import org.apache.flink.util.Collector



/**
  * 相同的key的元素，都一次做为参数传进来了
  */
object ReduceGroupRun {

  def main(args: Array[String]): Unit = {


    val env = ExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    val dataSet = env.fromElements("a","a","c","b","a")



    /**
      * 中间数据
      * (a,1)
      * (a,1)
      * (c,1)
      * (b,1)
      * (a,1)
      */
    val result = dataSet.map((_,1)).groupBy(0).reduceGroup(


      (in, out: Collector[(String,Int)]) =>{

        var count = 0 ;
        var word = "";
        while (in.hasNext){

          val next  = in.next()
          word = next._1
          count = count + next._2

        }
        out.collect((word,count))
      }


    )


    result.print()


  }

}



```
- 输出结果

```aidl
(a,3)
(b,1)
(c,1)

```









### combineGroup 
- 对集合中所有元素，按指定的key分组，把相同key的元素，做为参数，调用combineGroup()函数,会在本地进行合并
- 示例功能:按key分组统计所有数据的和


```aidl

package com.opensourceteams.module.bigdata.flink.example.dataset.transformation.combineGroup

import org.apache.flink.api.scala.{ExecutionEnvironment, _}
import org.apache.flink.util.Collector



/**
  * 相同的key的元素，都一次做为参数传进来了
  */
object Run {

  def main(args: Array[String]): Unit = {


    val env = ExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    val dataSet = env.fromElements("a","a","c","b","a")



    /**
      * 中间数据
      * (a,1)
      * (a,1)
      * (c,1)
      * (b,1)
      * (a,1)
      */
    val result = dataSet.map((_,1)).groupBy(0).combineGroup(


      (in, out: Collector[(String,Int)]) =>{

        var count = 0 ;
        var word = "";
        while (in.hasNext){

          val next  = in.next()
          word = next._1
          count = count + next._2

        }
        out.collect((word,count))
      }


    )


    result.print()


  }

}



```
- 输出结果

```aidl
(a,3)
(b,1)
(c,1)

```



### Aggregate sum
- 按key分组 对Tuple2(String,Int) 中value进行求和操作



```aidl
package com.opensourceteams.module.bigdata.flink.example.dataset.transformation.aggregate.sum

import org.apache.flink.api.scala.{ExecutionEnvironment, _}


/**
  * 相当于按key进行分组,然后对组内的元素进行的累加操作，求和操作
  */
object Run {

  def main(args: Array[String]): Unit = {


    val env = ExecutionEnvironment.getExecutionEnvironment

    val dataSet = env.fromElements(("a",3),("b",1),("c",5),("a",1),("c",1),("d",1),("f",1),("g",1),("f",1))

    /**
      * (a,1)
      * (b,1)
      * (c,1)
      * (a,1)
      * (c,1)
      * (d,1)
      * (f,1)
      * (g,1)
      */

    val dataSet2 = dataSet.sum(1)



    dataSet2.print()

  }

}



```
- 输出结果

```aidl
(f,15)

```



### Aggregate max 
- 按key分组 对Tuple2(String,Int) 中value进行求最大值操作



```aidl

package com.opensourceteams.module.bigdata.flink.example.dataset.transformation.aggregate.max

import org.apache.flink.api.scala.{ExecutionEnvironment, _}


/**
  * 相当于按key进行分组,然后对组内的元素进行的累加操作，求和操作
  */
object Run {

  def main(args: Array[String]): Unit = {


    val env = ExecutionEnvironment.getExecutionEnvironment

    val dataSet = env.fromElements(("a",3),("b",1),("c",5),("a",1),("c",1),("d",1),("f",1),("g",1),("f",1))

    /**
      * (a,1)
      * (b,1)
      * (c,1)
      * (a,1)
      * (c,1)
      * (d,1)
      * (f,1)
      * (g,1)
      */

    val dataSet2 = dataSet.max(1)



    dataSet2.print()

  }

}




```
- 输出结果

```aidl
(f,5)

```



### Aggregate min
- 按key分组 对Tuple2(String,Int) 中value进行求最小值操作



```aidl

package com.opensourceteams.module.bigdata.flink.example.dataset.transformation.aggregate.min

import org.apache.flink.api.scala.{ExecutionEnvironment, _}


/**
  * 相当于按key进行分组,然后对组内的元素进行的累加操作，求和操作
  */
object Run {

  def main(args: Array[String]): Unit = {


    val env = ExecutionEnvironment.getExecutionEnvironment

    val dataSet = env.fromElements(("a",3),("b",1),("c",5),("a",1),("c",1),("d",1),("f",1),("g",1),("f",1))

    /**
      * (a,1)
      * (b,1)
      * (c,1)
      * (a,1)
      * (c,1)
      * (d,1)
      * (f,1)
      * (g,1)
      */

    val dataSet2 = dataSet.min(1)



    dataSet2.print()

  }

}




```
- 输出结果

```aidl
(f,1)

```



### Aggregate sum (groupBy)
- 按key分组 对Tuple2(String,Int) 中的所有元素进行求和操作
- 示例功能:按key分组统计所有数据的和


```aidl
package com.opensourceteams.module.bigdata.flink.example.dataset.transformation.aggregate.sum

import org.apache.flink.api.scala.{ExecutionEnvironment, _}


/**
  * 相当于按key进行分组,然后对组内的元素进行的累加操作，求和操作
  */
object Run {

  def main(args: Array[String]): Unit = {


    val env = ExecutionEnvironment.getExecutionEnvironment

    val dataSet = env.fromElements(("a",1),("b",1),("c",1),("a",1),("c",1),("d",1),("f",1),("g",1),("f",1))

    /**
      * (a,1)
      * (b,1)
      * (c,1)
      * (a,1)
      * (c,1)
      * (d,1)
      * (f,1)
      * (g,1)
      */

    val dataSet2 = dataSet.groupBy(0).sum(1)



    dataSet2.print()

  }

}



```
- 输出结果

```aidl
(d,1)
(a,2)
(f,2)
(b,1)
(c,2)
(g,1)

```






### Aggregate max (groupBy)   等于  maxBy
- 按key分组 对Tuple2(String,Int) 中value 进行求最大值
- 示例功能:按key分组统计最大值


```aidl


package com.opensourceteams.module.bigdata.flink.example.dataset.transformation.aggregate.max

import org.apache.flink.api.scala.{ExecutionEnvironment, _}


/**
  * 相当于按key进行分组,然后对组内的元素进行的累加操作，求和操作
  */
object Run {

  def main(args: Array[String]): Unit = {


    val env = ExecutionEnvironment.getExecutionEnvironment

    val dataSet = env.fromElements(("a",2),("b",1),("c",4),("a",1),("c",1),("d",1),("f",1),("g",1),("f",1))

    /**
      * (a,1)
      * (b,1)
      * (c,1)
      * (a,1)
      * (c,1)
      * (d,1)
      * (f,1)
      * (g,1)
      */

    val dataSet2 = dataSet.groupBy(0).max(1)



    dataSet2.print()

  }

}


```
- 输出结果

```aidl
(d,1)
(a,2)
(f,1)
(b,1)
(c,4)
(g,1)

```






### Aggregate min (groupBy) 等于minBy
- 按key分组 对Tuple2(String,Int) 中value 进行求最小值
- 示例功能:按key分组统计最小值


```aidl


package com.opensourceteams.module.bigdata.flink.example.dataset.transformation.aggregate.max

import org.apache.flink.api.scala.{ExecutionEnvironment, _}


/**
  * 相当于按key进行分组,然后对组内的元素进行的累加操作，求和操作
  */
object Run {

  def main(args: Array[String]): Unit = {


    val env = ExecutionEnvironment.getExecutionEnvironment

    val dataSet = env.fromElements(("a",2),("b",1),("c",4),("a",1),("c",1),("d",1),("f",1),("g",1),("f",1))

    /**
      * (a,1)
      * (b,1)
      * (c,1)
      * (a,1)
      * (c,1)
      * (d,1)
      * (f,1)
      * (g,1)
      */

    val dataSet2 = dataSet.groupBy(0).min(1)



    dataSet2.print()

  }

}


```
- 输出结果

```aidl
(d,1)
(a,1)
(f,1)
(b,1)
(c,1)
(g,1)
```





### distinct 去重
- 按指定的例，去重


```aidl
package com.opensourceteams.module.bigdata.flink.example.dataset.transformation.aggregate.distinct

import org.apache.flink.api.scala.{ExecutionEnvironment, _}


/**
  * 相当于按key进行分组,然后对组内的元素进行的累加操作，求和操作
  */
object Run {

  def main(args: Array[String]): Unit = {


    val env = ExecutionEnvironment.getExecutionEnvironment

    val dataSet = env.fromElements(("a",3),("b",1),("c",5),("a",1),("c",1),("d",1),("f",1),("g",1),("f",1))

    /**
      * (a,1)
      * (b,1)
      * (c,1)
      * (a,1)
      * (c,1)
      * (d,1)
      * (f,1)
      * (g,1)
      */

    val dataSet2 = dataSet.distinct(1)



    dataSet2.print()

  }

}



```
- 输出结果

```aidl

(a,3)
(b,1)
(c,5)

```






### join 
- 连接


```aidl


package com.opensourceteams.module.bigdata.flink.example.dataset.transformation.join

import org.apache.flink.api.scala.{ExecutionEnvironment, _}


object Run {

  def main(args: Array[String]): Unit = {


    val env = ExecutionEnvironment.getExecutionEnvironment

    val dataSet = env.fromElements(("a",3),("b",1),("c",5),("a",1),("c",1),("d",1),("f",1),("g",1),("f",1))
    val dataSet2 = env.fromElements(("d",1),("f",1),("g",1),("f",1))


    //全外连接
    val dataSet3 = dataSet.join(dataSet2).where(0).equalTo(0)



    dataSet3.print()

  }

}


```
- 输出结果

```aidl

((d,1),(d,1))
((f,1),(f,1))
((f,1),(f,1))
((f,1),(f,1))
((f,1),(f,1))
((g,1),(g,1))

```




### join (Function)
- 连接


```aidl



package com.opensourceteams.module.bigdata.flink.example.dataset.transformation.joinFunction

import org.apache.flink.api.scala.{ExecutionEnvironment, _}


object Run {

  def main(args: Array[String]): Unit = {


    val env = ExecutionEnvironment.getExecutionEnvironment

    val dataSet = env.fromElements(("a",3),("b",1),("c",5),("a",1),("c",1),("d",1),("f",2),("g",5))
    val dataSet2 = env.fromElements(("g",1),("f",1))


    //全外连接
    val dataSet3 = dataSet.join(dataSet2).where(0).equalTo(0){
      (x,y) => (x._1,x._2+ y._2)
    }

    


    dataSet3.print()

  }

}



```
- 输出结果

```aidl

(f,3)
(g,6)

```






### leftOuterJoin 
- 左外连接,左边的Dataset中的每一个元素，去连接右边的元素


```aidl


package com.opensourceteams.module.bigdata.flink.example.dataset.transformation.leftOuterJoin

import org.apache.flink.api.scala.{ExecutionEnvironment, _}


object Run {

  def main(args: Array[String]): Unit = {


    val env = ExecutionEnvironment.getExecutionEnvironment

    val dataSet = env.fromElements(("a",3),("b",1),("c",5),("a",1),("c",1),("d",1),("f",2),("g",5))
    val dataSet2 = env.fromElements(("g",1),("f",1))


    //全外连接
    val dataSet3 = dataSet.leftOuterJoin(dataSet2).where(0).equalTo(0){
      (x,y) => {
        var count = 0;
        if(y != null ){
          count = y._2
        }
        (x._1,x._2+ count)
      }
    }




    dataSet3.print()

  }

}



```
- 输出结果

```aidl

(d,1)
(a,3)
(a,1)
(f,3)
(b,1)
(c,5)
(c,1)
(g,6)

```





### rightOuterJoin 
- 右外连接,左边的Dataset中的每一个元素，去连接左边的元素


```aidl

package com.opensourceteams.module.bigdata.flink.example.dataset.transformation.rightOuterJoin

import org.apache.flink.api.scala.{ExecutionEnvironment, _}


object Run {

  def main(args: Array[String]): Unit = {


    val env = ExecutionEnvironment.getExecutionEnvironment

    val dataSet = env.fromElements(("a",3),("b",1),("c",5),("a",1),("c",1),("d",1),("f",2),("g",5))
    val dataSet2 = env.fromElements(("g",1),("f",1))


    //全外连接
    val dataSet3 = dataSet.rightOuterJoin(dataSet2).where(0).equalTo(0){
      (x,y) => {
        var count = 0;
        if(x != null ){
          count = x._2
        }
        (x._1,y._2 + count)
      }
    }




    dataSet3.print()

  }

}



```
- 输出结果

```aidl

(f,2)
(g,2)

```




### fullOuterJoin 
- 全外连接,左右两边的元素，全部连接


```aidl

package com.opensourceteams.module.bigdata.flink.example.dataset.transformation.fullOuterJoin

import org.apache.flink.api.scala.{ExecutionEnvironment, _}


object Run {

  def main(args: Array[String]): Unit = {


    val env = ExecutionEnvironment.getExecutionEnvironment

    val dataSet = env.fromElements(("a",3),("b",1),("c",5),("a",1),("c",1),("d",1),("f",2),("g",5))
    val dataSet2 = env.fromElements(("g",1),("f",1))


    //全外连接
    val dataSet3 = dataSet.fullOuterJoin(dataSet2).where(0).equalTo(0){
      (x,y) => {
        var countY = 0;
        if(y != null ){
          countY = y._2
        }


        var countX = 0;
        if(x != null ){
          countX = x._2
        }
        (x._1,countX + countY)
      }
    }




    dataSet3.print()

  }

}



```
- 输出结果

```aidl

(f,2)
(g,2)

```




### union 
-  连接


```aidl

package com.opensourceteams.module.bigdata.flink.example.dataset.transformation.union

import org.apache.flink.api.scala.{ExecutionEnvironment, _}


object Run {

  def main(args: Array[String]): Unit = {


    val env = ExecutionEnvironment.getExecutionEnvironment

    val dataSet = env.fromElements(("a",1),("g",1),("f",1))
    val dataSet2 = env.fromElements(("d",1),("f",1),("g",1),("f",1))


    //全外连接
    val dataSet3 = dataSet.union(dataSet2)



    dataSet3.print()

  }

}


```
- 输出结果

```aidl

(a,1)
(d,1)
(g,1)
(f,1)
(f,1)
(g,1)
(f,1)


```




### first n 
-  前面几条数据


```aidl

package com.opensourceteams.module.bigdata.flink.example.dataset.transformation.first

import org.apache.flink.api.scala.{ExecutionEnvironment, _}


object Run {

  def main(args: Array[String]): Unit = {


    val env = ExecutionEnvironment.getExecutionEnvironment

    val dataSet = env.fromElements(("a",3),("b",1),("c",5),("a",1),("c",1),("d",1),("f",1),("g",1),("f",1))


    //全外连接
    val dataSet3 = dataSet.first(3)



    dataSet3.print()

  }

}


```
- 输出结果

```aidl

(a,3)
(b,1)
(c,5)

```





### coGroup
-  相当于，取出两个数据集的所有去重的key,然后，再把第一个DataSet中的这个key的所有元素放到可迭代对象中，再把第二个DataSet中的这个key的所有元素放到可迭代对象中


```aidl

package com.opensourceteams.module.bigdata.flink.example.dataset.transformation.cogroup

import java.lang

import org.apache.flink.api.common.functions.CoGroupFunction
import org.apache.flink.api.scala.{ExecutionEnvironment, _}
import org.apache.flink.util.Collector


object Run {

  def main(args: Array[String]): Unit = {


    val env = ExecutionEnvironment.getExecutionEnvironment

    val dataSet = env.fromElements(("a",1),("g",1),("a",1))
    val dataSet2 = env.fromElements(("a",1),("f",1))


    //全外连接
    val dataSet3 = dataSet.coGroup(dataSet2).where(0).equalTo(0)
    {
      new CoGroupFunction[(String,Int),(String,Int), Collector[(String,Int)]] {
        override def coGroup(first: lang.Iterable[(String, Int)], second: lang.Iterable[(String, Int)], out: Collector[Collector[(String, Int)]]): Unit = {
          println("==============开始")
          println("first")
          println(first)
          val iteratorFirst = first.iterator()
          while (iteratorFirst.hasNext()){
            println(iteratorFirst.next())
          }

          println("second")
          println(second)
          val iteratorSecond = second.iterator()
          while (iteratorSecond.hasNext()){
            println(iteratorSecond.next())
          }
          println("==============结束")

        }
      }
    }


    dataSet3.print()

  }

}


```
- 输出结果

```aidl

==============开始
first
org.apache.flink.runtime.util.NonReusingKeyGroupedIterator$ValuesIterator@3500e7b0
(a,1)
(a,1)
second
org.apache.flink.runtime.util.NonReusingKeyGroupedIterator$ValuesIterator@41230ea2
(a,1)
==============结束
==============开始
first
org.apache.flink.runtime.util.NonReusingKeyGroupedIterator$ValuesIterator@14602d0a
(g,1)
second
[]
==============结束
==============开始
first
[]
second
org.apache.flink.runtime.util.NonReusingKeyGroupedIterator$ValuesIterator@2b0a15b5
(f,1)
==============结束

Process finished with exit code 0


```







### cross
-  交叉连接


```aidl
package com.opensourceteams.module.bigdata.flink.example.dataset.transformation.cross

import org.apache.flink.api.scala.{ExecutionEnvironment, _}


object Run {

  def main(args: Array[String]): Unit = {


    val env = ExecutionEnvironment.getExecutionEnvironment

    val dataSet = env.fromElements(("a",1),("g",1),("f",1))
    val dataSet2 = env.fromElements(("d",1),("f",1),("g",1),("f",1))


    //全外连接
    val dataSet3 = dataSet.cross(dataSet2)



    dataSet3.print()

  }

}


```
- 输出结果

```aidl
((a,1),(d,1))
((a,1),(f,1))
((a,1),(g,1))
((a,1),(f,1))
((g,1),(d,1))
((g,1),(f,1))
((g,1),(g,1))
((g,1),(f,1))
((f,1),(d,1))
((f,1),(f,1))
((f,1),(g,1))
((f,1),(f,1))



```