package com.curso;

import com.curso.diccionario.Diccionario;

import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

public interface PalabrasSugeridas {

    static List<String> palabrasSugeridas(String palabra) {
        final String palabraNormalizada = normalizarPalabra(palabra);
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
            // TODO: Implementar sugerencias
        }

        return sugerencias;
    }

    static String normalizarPalabra(String palabra) {
        return palabra.toLowerCase();
    }
    private static int distanciaLevenshtein(String a, String b) {
        int [] costs = new int [b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }

}
