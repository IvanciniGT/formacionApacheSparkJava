package com.curso;

import java.util.List;

public class App {

    public static void main(String[] args) {
        String palabra = "angolsto";//args[0]

        List<String> palabras = List.of("mazani", "benana", "manfano", "alvaricoqe");
        for(int i = 0; i < 200; i++){
            for(String p : palabras){
                PalabrasSugeridas.palabrasSugeridas(p);
            }
        }

        long tin = System.nanoTime();
        List<String> palabrasRecibidas = PalabrasSugeridas.palabrasSugeridas(palabra);
        long tout = System.nanoTime();
        System.out.println("Palabras sugeridas para " + palabra + ": " + palabrasRecibidas);

        System.out.println("Tiempo de ejecución: " + (tout - tin)/(1000*1000) + " milisegundos");
    }

}
