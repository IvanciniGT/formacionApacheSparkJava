package com.curso.spark;

import org.apache.spark.sql.SparkSession;

public class IntroSQL {

    public static void main(String[] args){
        // Abrir una conexión al cluster de Spark
        SparkSession conexion = SparkSession.builder()
                .appName("IntroSQL")
                .master("local[2]")
                .getOrCreate();

        // Generar un dataset
        // Cuando trabajábamos con SparkCore, el objeto JavaSparkContext era el que nos permitía crear RDDs
        // Aplicar operaciones sobre el dataset
        // Con SparkSQL, el objeto SparkSession es el que nos permite crear Dataset<Row>
        conexion.read().

        // Cerrar la conexión al cluster de Spark
        conexion.close();
    }


}
