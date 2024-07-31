package com.curso.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.util.CollectionAccumulator;
import org.apache.spark.util.LongAccumulator;
import scala.Tuple2;

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

    //private static final String CLUSTER_URL = "spark://192.168.2.120:7077";
                                               //"local[2]";
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
        //conf.setMaster(CLUSTER_URL);
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
        LongAccumulator numeroDePalabrasEliminadas = sc.sc().longAccumulator("Número de palabras eliminadas");
        CollectionAccumulator<String> palabrasEliminadas = sc.sc().collectionAccumulator("Palabras eliminadas");
        Broadcast<List<String>> palabrasProhibidas = sc.broadcast(PALABRAS_PROHIBIDAS);

        //String nombreFichero = "tweets.txt";
        //Path rutaDelFichero = Paths.get(Hashtags.class.getClassLoader().getResource(nombreFichero).toURI());
        Path rutaDelFichero = Paths.get("src/main/resources/tweets.txt");
        Stream<String> lineas = Files.readString(rutaDelFichero).lines();                                  // Para cada tweet
        JavaRDD<String> lineasComoRDD = sc.parallelize( lineas.collect(Collectors.toList() ), 100/*Aqui hay que pasar un JAVA collection... de los de siempre*/);
        List<Tuple2<String, Integer>> hashtags = lineasComoRDD
                .map(    tweet -> tweet.replace("#", " #")                        )  // Separo los hashtags entre si
                .map(    tweet -> tweet.split( "[ .,()_!¿¡'=+*/@;:<>-]" )                    )  // Separo las palabras
                .flatMap( array -> Arrays.asList(array).iterator()                                       )  // Unifico el listado de palabras
                        // La funcion flatmap en Spark Core espera recibir algo diferente a la definida en Streams
                        // En Stream, espera recibir una función que devuelva un Stream
                        // En RDDs espera recibir una función que devuelva un ITERABLE
                .filter(  hashtag -> hashtag.startsWith("#")                                        )  // Me quedo con los hashtags
                .map(    String::toLowerCase                                                       )  // Normalizo
                .map(    hashtag -> hashtag.substring(1)                                 )  // Quito el cuadradito
                .filter(  hashtag -> {
                        boolean mantieneElHashtag = palabrasProhibidas.value()/*PALABRAS_PROHIBIDAS*/.stream().noneMatch( hashtag::contains );
                        if(!mantieneElHashtag) {
                            numeroDePalabrasEliminadas.add(1);
                            palabrasEliminadas.add(hashtag);
                            System.out.println("Contiene palabra prohibida: " + hashtag);
                        }
                        return mantieneElHashtag;
                    }
                )  // Me quedo con los que no contienen palabras prohibidas
                .repartition(1)
                //.collect( Collectors.toList()                                                      ); // Los meto en una lista
                // En el caso de spark, directamente pongo: .collect() ... y me entrega una List<T>

                .mapToPair( hashtag -> new Tuple2<>(hashtag, 1) ) // Añado a cada hashtag un 1
                .reduceByKey( (a, b) -> a + b ) // Sumo los 1s de cada hashtag
                .mapToPair( (tupla) -> new Tuple2<>(tupla._2, tupla._1) )
                .sortByKey(false)
                .mapToPair( (tupla) -> new Tuple2<>(tupla._2, tupla._1) )

                .collect();
        hashtags.forEach(System.out::println);
        System.out.println("Número de palabras eliminadas: " + numeroDePalabrasEliminadas.value());
        System.out.println("Palabras eliminadas: " + palabrasEliminadas.value());
    }

}

