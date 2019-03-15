package com.opensourceteams.module.bigdata.flink.example.tableapi.query.aggregate.sum

import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.table.api.scala._
import org.apache.flink.table.api.{TableEnvironment, Types}
import org.apache.flink.table.sources.CsvTableSource

object Run {



  def main(args: Array[String]): Unit = {


    //得到批环境
    val env = ExecutionEnvironment.getExecutionEnvironment

    //创建CsvTableSource
    val csvTableSource = CsvTableSource.builder()
      .path("src/main/resources/data/csv/user.csv")
      .ignoreFirstLine()
      .fieldDelimiter(",")
      .field("name", Types.STRING)
      .field("age", Types.INT)
      .field("sex", Types.STRING)
      .field("salary", Types.INT)
      .build()


    //得到Table环境
    val tableEnv = TableEnvironment.getTableEnvironment(env)
    //注册table
    tableEnv.registerTableSource("user",csvTableSource)




    //查询表数据, select 选择需要的列
    tableEnv.scan("user")
      .groupBy('sex)
      .select('sex,'salary.sum)

      .first(100).print()
  }

}