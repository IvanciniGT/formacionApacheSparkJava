package com.curso.spark;

import com.curso.diccionario.Diccionario;
import com.curso.diccionario.DiccionarioImpl;
import com.curso.diccionario.Utilidades;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

public class PalabrasSugeridas {

    private static final int MAX_PALABRAS_SUGERIDAS = 10;
    private static final int DISTANCIA_MAXIMA_ADMISIBLE = 2;
//    private static final String CLUSTER_URL = "local[2]";
    //private static final String CLUSTER_URL = "spark://192.168.2.120:7077";


    public static void main(String[] args) throws InterruptedException, URISyntaxException, IOException {
        SparkConf conf = new SparkConf();
        conf.setAppName("Spark Core Basicos");
        //conf.setMaster(CLUSTER_URL); //ESTO TAMPOCO LO VAMOS A USAR!
        // conf.setJars() ESTO NO LO VAMOS A USAR NUNCA JAMAS
        JavaSparkContext sc = new JavaSparkContext(conf);

        String palabraAIdentificar = "anhivo";

        List<String> sugerencias = palabrasSugeridas(palabraAIdentificar, sc);

        System.out.println("Palabra original: " + palabraAIdentificar);
        sugerencias.forEach(sugerencia -> System.out.println("Sugerencia: " + sugerencia));

        Thread.sleep(3600*1000);
        sc.close();
    }

    static List<String> palabrasSugeridas(String palabraOriginal, JavaSparkContext sc) {
        final String palabraNormalizada = Utilidades.normalizarPalabra(palabraOriginal);
        /*Optional<Diccionario> potencialDiccionario = ServiceLoader.load(Diccionario.class).findFirst();
        if(potencialDiccionario.isEmpty()) {
            throw new RuntimeException("No se encontró ningún Diccionario donde buscar");
        }
        Diccionario diccionario = potencialDiccionario.get();
        */
        Diccionario diccionario = new DiccionarioImpl();
        List<String> sugerencias;
        if (diccionario.existePalabra(palabraNormalizada)) {
            sugerencias= List.of(palabraNormalizada);
        }else{
            sugerencias = sc.parallelize(new ArrayList<>(diccionario.palabrasExistentes()))
                     .filter(   palabra         -> Math.abs(palabra.length() - palabraNormalizada.length()) <= DISTANCIA_MAXIMA_ADMISIBLE        )  // Me quedo con las de longitud similar
                     .map(     palabra         -> new PalabraPuntuada(palabra, Utilidades.distanciaLevenshtein( palabra, palabraNormalizada ) ) )  // Calculo la distancia de Levenshtein
                     .filter(   palabraPuntuada -> palabraPuntuada.puntuacion <= DISTANCIA_MAXIMA_ADMISIBLE                                      )  // Me quedo con las que están a una distancia admisible
                     .sortBy(  palabraPuntuada -> palabraPuntuada.puntuacion, true, 1                                     )  // Las ordeno por distancia
                     .map(     palabraPuntuada -> palabraPuntuada.palabra                                                                       )  // Me quedo solo con la palabra (descarto la puntuación)
                     //.limit(   MAX_PALABRAS_SUGERIDAS                                                                                           )  // Me quedo con las N primeras (Mejores alternativas)
                     //.collect( Collectors.toList()                                                                                              ); // Las meto en una lista
                    .take(MAX_PALABRAS_SUGERIDAS);
        }

        return sugerencias;
    }

    static class PalabraPuntuada implements Serializable {
        private final String palabra;
        private final int puntuacion;

        public PalabraPuntuada(String palabra, int puntuacion) {
            this.palabra = palabra;
            this.puntuacion = puntuacion;
        }
    }

}