package conceptos;
// En java 1.8 aparece un paquete nuevo en JAVA: java.util.function
// Este paquete contiene interfaces que nos permiten apuntar a Funciones: Interfaces Funcionales
// - Function<T, R>         Es una función que recibe un argumento de tipo T y devuelve un resultado de tipo R
    // .apply(T t)            Es un método que recibe un argumento de tipo T y devuelve un resultado de tipo R
// - Consumer<T>            Es una función que recibe un argumento de tipo T y no devuelve nada         (setters)
    // .accept(T t)           Es un método que recibe un argumento de tipo T y no devuelve nada
// - Supplier<R>            Es una función que no recibe argumentos y devuelve un resultado de tipo R   (getters)
    // .get()                 Es un método que no recibe argumentos y devuelve un resultado de tipo R
// - Predicate<T>           Es una función que recibe un argumento de tipo T y devuelve un booleano     (is..., has...)
    // .test(T t)             Es un método que recibe un argumento de tipo T y devuelve un booleano
// - BiFunction<T, U, R>    Es una función que recibe dos argumentos de tipo T y U y devuelve un resultado de tipo R
    // .apply(T t, U u)       Es un método que recibe dos argumentos de tipo T y U y devuelve un resultado de tipo R
import java.util.function.Function;
public class Funcional {

    public static void main(String[] args) {
        Function<String, String> miFuncion = Funcional::funcionGeneradoraDeSaludoFormal; // En JAVA 1.8 aparece un nuevo operador ::
                                                                              // que nos permite referenciar una función dentro de un contexto
        System.out.println(miFuncion.apply("Juan")); // Hola, Juan. ¿Cómo estás?
        imprimirSaludo(Funcional::funcionGeneradoraDeSaludoFormal, "Juan"); // Hola, Juan. ¿Cómo estás?
        imprimirSaludo(Funcional::funcionGeneradoraDeSaludoInformal, "Juan"); // Qué onda, Juan. ¿Cómo va todo?
        // Pero en java 1,8 aparece un nuevo operador adicional: "->", que nos permite definir Expresiones Lambda
        // Expresión lambda???
        // Expresión? Un trozo de código que devuelve un valor
        System.out.println("Hola, mundo"); // Statement: Instrucción, Enunciado, Declaración, Sentencia (Frase)
        int numero = 5 + 7; // otro statement
                    /////// Expresión: Un trozo de código que devuelve un valor
        // Expresión lambda: Es un trozo de código que devuelve un valor... qué valor? Una referencia a una función anónima creada en RAM.
        // Las expresiones lambda son una sintaxis alternativa para definir funciones.

        Function<String,Object> miFuncion2 = (String nombre) -> {
            return "Hola, " + nombre + ". ¿Cómo pasa way/tio?";
        }; // Además de quitarme el nombre de la función, me he quitado el tipo de dato que devuelve... y eso? JAVA lo infiere de lo que retorna la función
        // Pero no solo eso:
        Function<String,Object> miFuncion3 = (nombre) -> { // Java también lo infiere.. en este caso de la variable
            //////
            return "Hola, " + nombre + ". ¿Cómo pasa way/tio?";
        };
        Function<String,Object> miFuncion4 = nombre -> {
            return "Hola, " + nombre + ". ¿Cómo pasa way/tio?";
        };
        Function<String,Object> miFuncion5 = nombre -> "Hola, " + nombre + ". ¿Cómo pasa way/tio?";
        // Hasta la introducción de la programación funcional, para qué creábamos funciones?
        // - Reutilizar código
        // - Mejorar la legibilidad
        // Pero con la llegada de la programación funcional, tenemos un tercer motivo para crear funciones:
        // - Pasarlas como argumentos a otras funciones, que requieren de funciones como argumentos
        // Y hay veces donde no quiero reutilizar esa función que acabo de crear con la única finalidad de poder pasarla como argumento a otra función
        // Y hay veces en las que definir esa función de forma tradicional me complica la legibilidad del código... y en ese caso uso una expresión lambda

        System.out.println(miFuncion2.apply("Juan")); // Hola, Juan. ¿Cómo estás?
        System.out.println(miFuncion3.apply("Juan")); // Hola, Juan. ¿Cómo estás?
        System.out.println(miFuncion4.apply("Juan")); // Hola, Juan. ¿Cómo estás?
        System.out.println(miFuncion5.apply("Juan")); // Hola, Juan. ¿Cómo estás?
        var texto2 = "hola"; // Desde java 11
        // Que tipo de dato tiene la variable texto2? String... Lo pongo? No hace falta, JAVA lo infiere.
        //texto2=4; // Esto no cuela... En JS si... El var de JS es distinto del var de java... ya que java es un lenguaje de tipado fuerte.
        imprimirSaludo(nombre -> "Hola, " + nombre + ". ¿Cómo pasa way/tio?", "Juan"); // Hola, Juan. ¿Cómo pasa way/tio?
        // Serialización de un objeto: Convertir el objeto en bytes para poder enviarlo por la red, guardarlo en un fichero, etc.
        // Las expresiones lambda generan funciones que por defecto son SERIALIZABLES, 
        // cosa que las funciones que yo creo de forma tradicional no son serializables... Lo único que puedo serializar es la clase que 
        // contiene la definición de las funciones: implementado la interfaz Serializable
        // ESTO AHORA PARECE poco relevante... Con Spark, todo lo que vamos a hacer es crear funciones que necesitamos mandar a nodos remotos de un cluster
        // Para que sean esos nodos remotos los que ejecuten esas funciones... 
        // Y para que eso funcione, esas funciones tienen que ser serializables.
    }

    static void imprimirSaludo(Function<String, String> funcionGeneradoraDeSaludos, String nombre) {
        System.out.println(funcionGeneradoraDeSaludos.apply(nombre));
    }

    static String funcionGeneradoraDeSaludoFormal(String nombre) {
        return "Hola, " + nombre + ". ¿Cómo estás?";
    }

    static String funcionGeneradoraDeSaludoInformal(String nombre) {
        return "Qué onda, " + nombre + ". ¿Cómo va todo?";
    }

}