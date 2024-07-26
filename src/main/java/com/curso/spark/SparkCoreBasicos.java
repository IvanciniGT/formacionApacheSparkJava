package com.curso.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SparkCoreBasicos {

    private static final String CLUSTER_URL = "local[2]";
    private static final List<String> PALABRAS_PROHIBIDAS = List.of("caca", "culo","pedo","pis", "mierda");

    // ^^^ AQUI habría que poner la URL de un cluster de Spark...
    // En nuestro caso no tenemos aún.
    // Lo que hacemos es usar una utilidad que nos ofrece la librería SparkCore: local[#]
    // El # indica en número de cores que quiero reservar para un cluster de Spark que se va a levantar en automático en mi equipo
    // solo para desarrollo.
    // En lugar de un número, podemos poner *, que significa que se usen todos los cores disponibles en el equipo. NO QUIERO NUNCA!

    public static void main(String[] args) throws InterruptedException, URISyntaxException, IOException {
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
        procesarTweets(sc);
        // Antes de cerrar la conexión... y como estoy en desarrollo, voy a pausar la ejecución del hilo principal...
        // Eso me va a adar la oportunidad de ver la UI de Spark en http://localhost:4040
        Thread.sleep(3600*1000);
        sc.close();
    }

    public static void procesarTweets(JavaSparkContext sc) throws URISyntaxException, IOException {
        String nombreFichero = "tweets.txt";
        Path rutaDelFichero = Paths.get(Hashtags.class.getClassLoader().getResource(nombreFichero).toURI());
        Stream<String> lineas = Files.readString(rutaDelFichero).lines();                                  // Para cada tweet
        JavaRDD<String> lineasComoRDD = sc.parallelize( lineas.collect(Collectors.toList() ), 30 /*Aqui hay que pasar un JAVA collection... de los de siempre*/);
        List<String> hashtags = lineasComoRDD
                .map(    tweet -> tweet.replace("#", " #")                        )  // Separo los hashtags entre si
                .map(    tweet -> tweet.split( "[ .,()_!¿¡'=+*/@;:<>-]" )                    )  // Separo las palabras
                .flatMap( array -> Arrays.asList(array).iterator()                                       )  // Unifico el listado de palabras
                        // La funcion flatmap en Spark Core espera recibir algo diferente a la definida en Streams
                        // En Stream, espera recibir una función que devuelva un Stream
                        // En RDDs espera recibir una función que devuelva un ITERABLE
                .filter(  hashtag -> hashtag.startsWith("#")                                        )  // Me quedo con los hashtags
                .map(    String::toLowerCase                                                       )  // Normalizo
                .map(    hashtag -> hashtag.substring(1)                                 )  // Quito el cuadradito
                .filter(  hashtag -> PALABRAS_PROHIBIDAS.stream().noneMatch( hashtag::contains )    )  // Me quedo con los que no contienen palabras prohibidas
                //.collect( Collectors.toList()                                                      ); // Los meto en una lista
                // En el caso de spark, directamente pongo: .collect() ... y me entrega una List<T>
                .collect();

        hashtags.forEach(System.out::println);

    }

}

