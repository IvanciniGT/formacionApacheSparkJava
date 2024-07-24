package com.curso;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static com.curso.PalabrasSugeridas.palabrasSugeridas;
import static org.junit.jupiter.api.Assertions.*;

class PalabrasSugeridasTest {

    @ParameterizedTest
    @DisplayName("Palabras sugeridas de manzana")
    @ValueSource(strings = {"manzana", "MANZANA", "Manzana"})
    void palabrasSugeridasSiExisteTest(String palabra) {
        List<String> palabrasRecibidas = palabrasSugeridas(palabra);
        assertNotNull(palabrasRecibidas);
        assertEquals(1, palabrasRecibidas.size());
        assertEquals("manzana", palabrasRecibidas.get(0));
    }

    @ParameterizedTest
    @DisplayName("Palabras sugeridas de manana")
    @ValueSource(strings = {"manana", "MANANA", "Manana"})
    void palabrasSugeridasSiNoExisteTest(String palabra) {
        List<String> palabrasRecibidas = palabrasSugeridas(palabra);
        System.out.println(palabrasRecibidas);
        assertNotNull(palabrasRecibidas);
        assertEquals(3, palabrasRecibidas.size());
        assertTrue(palabrasRecibidas.contains("manzana"));
        assertTrue(palabrasRecibidas.contains("banana"));
        assertTrue(palabrasRecibidas.contains("manzano"));
        assertTrue(palabrasRecibidas.indexOf("manzana") < palabrasRecibidas.indexOf("manzano"));
        assertTrue(palabrasRecibidas.indexOf("banana") < palabrasRecibidas.indexOf("manzano"));
        assertFalse(palabrasRecibidas.contains("albaricoque"));
    }
}

// Mi diccionario solo tendrá las palabras "manzana", "banana", "manzano" y "albaricoque".
