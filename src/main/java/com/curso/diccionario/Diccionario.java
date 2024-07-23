package com.curso.diccionario;

import java.util.Set;

public interface Diccionario {

    Set<String> palabrasExistentes();

    boolean existePalabra(String palabra);
}
