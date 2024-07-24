package com.curso;

public class App {

    public static void main(String[] args) {
        String palabra = "angolsto";//args[0]
        System.out.println("Palabras sugeridas para " + palabra + ": " + PalabrasSugeridas.palabrasSugeridas(palabra));
    }

}
