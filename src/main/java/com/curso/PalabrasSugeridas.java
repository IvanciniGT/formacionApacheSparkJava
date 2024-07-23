package com.curso;

import com.curso.diccionario.Diccionario;

import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

public interface PalabrasSugeridas {

    static List<String> palabrasSugeridas(String palabra) {
        final String palabraNormalizada = Utilidades.normalizarPalabra(palabra);
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

}
