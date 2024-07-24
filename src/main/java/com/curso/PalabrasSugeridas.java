package com.curso;

import com.curso.diccionario.Diccionario;
import com.curso.diccionario.Utilidades;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public interface PalabrasSugeridas {

    int MAX_PALABRAS_SUGERIDAS = 10;
    int DISTANCIA_MAXIMA_ADMISIBLE = 2;

    static List<String> palabrasSugeridas(String palabraOriginal) {
        final String palabraNormalizada = Utilidades.normalizarPalabra(palabraOriginal);
        //Diccionario dict = new DiccionarioImpl(); //SOLID. Me acabo de cagar en la D de SOLID
                                              // D: Dependency Inversion Principle
                                              // Mis clases (mi código) no debe crear instancias de los objetos que necesita...
                                              // sino que le deben ser suministradas
        Optional<Diccionario> potencialDiccionario = ServiceLoader.load(Diccionario.class).findFirst();
        if(potencialDiccionario.isEmpty()) {
            throw new RuntimeException("No se encontró ningún Diccionario donde buscar");
        }
        List<String> sugerencias;
        Diccionario diccionario = potencialDiccionario.get();
        if (diccionario.existePalabra(palabraNormalizada)) {
            sugerencias= List.of(palabraNormalizada);
        }else{
            sugerencias = diccionario.palabrasExistentes().stream()                                                                                // Para cada palabra
                     .filter(   palabra         -> Math.abs(palabra.length() - palabraNormalizada.length()) <= DISTANCIA_MAXIMA_ADMISIBLE        )  // Me quedo con las de longitud similar
                     .map(     palabra         -> new PalabraPuntuada(palabra, Utilidades.distanciaLevenshtein( palabra, palabraNormalizada ) ) )  // Calculo la distancia de Levenshtein
                     .filter(   palabraPuntuada -> palabraPuntuada.puntuacion <= DISTANCIA_MAXIMA_ADMISIBLE                                      )  // Me quedo con las que están a una distancia admisible
                     .sorted(  Comparator.comparing(palabraPuntuada -> palabraPuntuada.puntuacion )                                             )  // Las ordeno por distancia
                     .limit(   MAX_PALABRAS_SUGERIDAS                                                                                           )  // Me quedo con las N primeras (Mejores alternativas)
                     .map(     palabraPuntuada -> palabraPuntuada.palabra                                                                       )  // Me quedo solo con la palabra (descarto la puntuación)
                     .collect( Collectors.toList()                                                                                              ); // Las meto en una lista
                                // Desde Java 16: .toList();
        }

        return sugerencias;
    }

    class PalabraPuntuada {
        private final String palabra;
        private final int puntuacion;

        public PalabraPuntuada(String palabra, int puntuacion) {
            this.palabra = palabra;
            this.puntuacion = puntuacion;
        }
    }

}
/*
Potencial implementación de la función filter de Stream (Obviando por ahora el tema del modo perezoso)

public List<T> filter(Predicate<T> predicado) {
    List<T> resultado = new ArrayList<>();
    for(T elemento : <COLECCION>) {
        if(predicado.test(elemento)) {
            resultado.add(elemento);
        }
    }
    return resultado;
}
 */