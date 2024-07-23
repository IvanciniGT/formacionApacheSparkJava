import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.*;


public class Streams {
    
    public static void main(String[] args){
        List<Integer> numeros = List.of(1,2,3,4,5,6,7,8,9);
        Stream<Integer> numerosComoStream = numeros.stream();

        Stream<Integer> dobles = numerosComoStream.map(  numero -> numero * 2  ); // Realmente lo único que hago es decirle a Java
                                                                                  // Que ANOTE que hay que aplicar esa transformación sobre 
                                                                                  // cada elemento del Stream original.
        Stream<Integer> triplesDeLosDobles = dobles.map( numero -> numero * 3 );  // Solo le pido que ANOTE que hay que aplicar esa transformación
                                                                                  // sobre cada elemento del Stream de partida.

        List<Integer> resultado = triplesDeLosDobles.collect(Collectors.toList()); // AQUI ES DONDE SE DISPARA LA EJECUCIÓN DE TODO EL CÓDIGO ANTERIOR

        System.out.println(resultado); // [6, 12, 18, 24, 30, 36, 42, 48, 54]

        //Set<Integer> resultadoComoSet = triplesDeLosDobles.collect(Collectors.toSet()); // AQUI ES DONDE SE DISPARA LA EJECUCIÓN DE TODO EL CÓDIGO ANTERIOR

        //System.out.println(resultadoComoSet); // [6, 12, 18, 24, 30, 36, 42, 48, 54]

        numeros.stream()
                .map(numero -> numero * 2)
                .map(numero -> numero * 3)
                .forEach( System.out::println );

    }

}
