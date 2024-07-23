package conceptos;
import java.util.List;

public class Colecciones {
    
    public static void main(String[] args) {
        List<Integer> numeros = List.of(1,2,3,4,5,6,7,8,9);

        // Pre java 1.5, esa lista la iterábamos con un bucle for
        for (int i = 0; i < numeros.size(); i++) {
            System.out.println(numeros.get(i));
        }
        // en java 1.5 aparece el concepto de Iterable... y un nuevo tipo de bucle: el bucle for each
        for (Integer numero : numeros) {
            System.out.println(numero);
        }
        // En java 1.8, en todas las colecciones aparece la función forEach, que admite como argumento 
        // una función que se ejecutará por cada elemento de la colección
        numeros.forEach(Colecciones::imprimirDoble); // Method reference
        numeros.forEach(numero -> System.out.println(numero * 2)); // Lambda

        // Estos bucles (internos) son mucho más eficientes que los bucles tradicionales (externos)
        // En cualquier caso nanosegundos... tampoco nos volvemos locos (microoptimización... que salvo en casos muy especiales, pasamos de ella.)
        // Desde que aparece en Java 1.8 la programación funcional, la mayor parte del api de java está migrando a programación funcional
        // Cada vez tenemos más y más clases que definen funciones que reciben funciones como argumentos o que devuelven funciones
    }

    public static void imprimirDoble(int numero) { // Consumer<Integer>
        System.out.println(numero * 2);
    }

}
