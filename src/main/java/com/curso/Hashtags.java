package com.curso;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Hashtags {

    private static final List<String> PALABRAS_PROHIBIDAS = List.of("caca", "culo","pedo","pis", "mierda");

    public static void main(String[] args) throws URISyntaxException, IOException {
        String nombreFichero = "tweets.txt";
        Path rutaDelFichero = Paths.get(Hashtags.class.getClassLoader().getResource(nombreFichero).toURI());
        List<String> hashtags = Files.readString(rutaDelFichero).lines()                                       // Para cada tweet
                        .map(    tweet -> tweet.replace("#", " #")                        )  // Separo los hashtags entre si
                        .map(    tweet -> tweet.split( "[ .,()_!¿¡'=+*/@;:<>-]" )                    )  // Separo las palabras
                        .flatMap( Arrays::stream                                                            )  // Unifico el listado de palabras
                        .filter(  hashtag -> hashtag.startsWith("#")                                        )  // Me quedo con los hashtags
                        .map(    String::toLowerCase                                                       )  // Normalizo
                        .map(    hashtag -> hashtag.substring(1)                                 )  // Quito el cuadradito
                        .filter(  hashtag -> PALABRAS_PROHIBIDAS.stream().noneMatch( hashtag::contains )    )  // Me quedo con los que no contienen palabras prohibidas
                        .collect( Collectors.toList()                                                      ); // Los meto en una lista

        hashtags.forEach(System.out::println);


/*

        filter (los que empiecen por #)                     FILTER                                                           Stream<String[]> (hashtags)

        ESTO SE PUEDE HACER mediante la función FLATMAP (que es un map + flatten). En el caso de Streams (JAVA pelao')
        A la función flatmap le debemos suministrar una función que reciba un dato de tipo String[] -> Stream<String>

        vvvv
        map (NORMALIZAR)
        map (QUITAR EL CUADRADITO)
        vvvv
    ¿Qué más? Filtrar los que contengan palabras prohibidas.... ¿COMO? Mediante un PREDICADO suministrado al FILTER

        hashtag -> palabrasProhibidas.stream().noneMatch( hashtag::contains )
                */
    }

}
