package com.curso.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

public class SparkCoreBasicos {

    private static final String CLUSTER_URL = "local[2]";
    // ^^^ AQUI habría que poner la URL de un cluster de Spark...
    // En nuestro caso no tenemos aún.
    // Lo que hacemos es usar una utilidad que nos ofrece la librería SparkCore: local[#]
    // El # indica en número de cores que quiero reservar para un cluster de Spark que se va a levantar en automático en mi equipo
    // solo para desarrollo.
    // En lugar de un número, podemos poner *, que significa que se usen todos los cores disponibles en el equipo. NO QUIERO NUNCA!

    public static void main(String[] args) {
        // Paso 1 : Abrir conexión con un cluster de Spark
        // Paso 1.1: Configurar la conexión
        SparkConf conf = new SparkConf();
        conf.setAppName("Spark Core Basicos");
        conf.setMaster(CLUSTER_URL);
        // Paso 1.2: Abrir la conexión
        JavaSparkContext sc = new JavaSparkContext(conf);
        // AQUI Cargaríamos el conjunto de datos....
        // Configuraríamos el map-reduce...
        // Y al acabar cerraríamos la conexión
        sc.close();
    }

}

