package com.curso.diccionario;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DiccionarioImpl implements Diccionario {

    private final static Map<String, List<String>> palabras = cargarPalabras(); // por no tener multiples idiomas

    private static Map<String, List<String>> cargarPalabras() {
        // Con Java 11, la lectura se hace guay
        try {
            Path pathDelDiccionario = Path.of(DiccionarioImpl.class.getClassLoader().getResource("diccionario.txt").toURI());
            return Files.readString(pathDelDiccionario).lines()   // Manzana=Fruta del manzano
                    .map( linea -> linea.split("=" ) ) // [Manzana, Fruta del manzano]
                    .map( array -> {
                                        array[0] = Utilidades.normalizarPalabra(array[0]);   // [manzana, Fruta del manzano]
                                        return array;
                    })
                    .collect(Collectors.toMap( array -> array[0], // Lo que quiero usar de clave en el Map
                                               array -> List.of(array[1].split("|")),
                                               (definiciones1, definiciones2) -> {
                                                    /*List<String> definicionesAgrupadas = new ArrayList<>();
                                                    definicionesAgrupadas.addAll(definiciones1);
                                                    definicionesAgrupadas.addAll(definiciones2);
                                                    return definicionesAgrupadas;*/
                                                    return Stream.concat(definiciones1.stream(), definiciones2.stream())
                                                                 .collect(Collectors.toList());
                                               }
                            )
                    ); // Lo que quiero usar de valor en el Map
        }catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
            // Notificarla de alguna forma!
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public DiccionarioImpl() {
    }

    @Override
    public Set<String> palabrasExistentes() {
        return palabras.keySet();
    }

    @Override
    public boolean existePalabra(String palabra) {
        return palabras.containsKey(palabra);
    }
}
// new DiccionarioImpl()