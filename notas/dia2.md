# MapReduce

Es una forma de estructurar un programa. 
Va muy orientado al trabajo con colecciones de datos.
La principal gracia de MapReduce es su capacidad para realizar operaciones de forma paralela.

## En qué consiste MapReduce

La idea es que vamos a partir de una colección de datos, una que soporte programación funcional map-reduce y le aplicaremos una secuencia de operaciones de tipo MAP, para acabar con una operación de tipo REDUCE.

### Operaciones de tipo MAP

Son funciones que nos permiten aplicar una transformación/tratamiento a una colección de datos que soporte programación funcional map-reduce, para generar una nueva colección de datos que soporte programación funcional map-reduce.
Pero hay algo más... son funciones que se ejecutan en modo LAZY (perezoso) y esta es la clave de todo!
El punto es que realmente al aplicar una función de tipo map sobre un conjunto de datos, la función de mapeo suministrada no llega a aplicarse sobre los elementos.

Cuando nosotros digamos:
Sobre esta colección, mapea, usando la función de mapeo "Doblar", lo UNICO que hace el motor de procesamiento MAP REDUCE Que estemos utilizando es ANOTAR que cuando sea necesario, hay que aplicar esa transformación... pero la transformación no se aplica en ese momento.

### Operaciones de tipo REDUCE

Son funciones que nos permiten aplicar una transformación/tratamiento a una colección de datos que soporte programación funcional map-reduce, para generar un resultado que NO ES UNA COLECCIÓN DE DATOS que soporte programación funcional map-reduce.
Son funciones que se ejecutan en modo EAGER (Ansioso). Tan pronto como son invocadas, se ejecutan.
Y actúan como un detonante (efecto dominó). Para ejecutar una función de tipo REDUCE, será necesario los datos de entrada, que a su vez serán los datos de salida de una función de tipo map... y será en ese momento en el que se aplique la función de tipo map... y así sucesivamente

COLECCION QUE SOPORTA MAP-REDUCE
    -> MAP1 -> COLECCION QUE SOPORTA MAP-REDUCE (Anota que hay que aplicar esto...)
    -> MAP2 -> COLECCION QUE SOPORTA MAP-REDUCE (Anota que hay que aplicar esto...)
    -> MAP3 -> COLECCION QUE SOPORTA MAP-REDUCE (Anota que hay que aplicar esto...)
    -> REDUCE -> RESULTADO QUE NO ES UNA COLECCION QUE SOPORTA MAP-REDUCE (Ejecuta todo lo anotado)

Hasta que no hay un reduce no se ejecuta nada.
Una vez ejecutado un reduce, los streams se consumen y no se pueden volver a usar.

Os decía que el hecho de que las funciones de tipo MAP se ejecuten en modo perezoso es la clave de la programación MAP-REDUCE:
- Por un lado, el motor de procesamiento MAP-REDUCE que usemos decidirá cómo (la estrategia más óptima) para ejecutar todas esas transformaciones... que no necesariamente debe ser secuencial.
- Por otro lado, al no ejecutarse nada hasta que no se invoca a la función de reducción, eso nos abre la puerta a en el momento en el que haya que ejecutar los trabajos, decidir dónde ejecutarlos (remoto)

En Java 1.8 se incluye un nuevo tipo de colección. Teníamos muchos: Set, List, Map, Queue, Stack, Vector, Hashtable, Properties, etc. 
En JAVA 1.8 se añade el tipo Stream.

## Stream

Un Stream<T> no es sino una colección de datos que soporta programación funcional map-reduce.
Es similar a un Set<T>, pero con funciones diferentes, con funciones de tipo MAP y REDUCE.

Esa clase se incluye dentro del paquete java.util.stream.

Por lo tanto, en JAVA, vamos a definir una operación de tipo MAP como aquella que aplicada sobre un Stream nos devuelve otro Stream.
Y vamos a definir una operación de tipo REDUCE como aquella que aplicada sobre un Stream nos devuelve un resultado que no es un Stream.

Hay muchas operaciones de tipo Map y de tipo Reduce que podemos aplicar sobre un Stream.

### Operaciones de tipo Map

#### Map

De aquí reciben el nombre las funciones de tipo Map.
Esta función nos permite aplicar una función de mapeo sobre cada elemento del Stream Original, para dar lugar un nuevo Stream, uno que contiene el resultado de aplicar la función de mapeo a cada elemento del Stream Original.

       COLECCION INICIAL    .map( funcionDeMapeo )    COLECCION FINAL
        1                                                   2
        2                   funcionDeMapeo: x2              4
        3                                                   6
        4                                                   8
        5                                                   10

       Stream<Integer>                                      Stream<Integer>
Las transformaciones no son inplace... No se modifica el conjunto inicial.

#### Filter

Esa función recibe un predicado (ua función que devuelve un boolean)... y genera un nuevo Stream que contendrá los registros del stream original para los que la función predicado devuelva true.

#### limit

Esta función recibe un entero... y devuelve un nuevo Stream que contendrá los primeros n elementos del Stream original.

#### sorted

Esta función recibe un comparador... y devuelve un nuevo Stream que contendrá los elementos del Stream original ordenados según el comparador.

Esos comparadores los podemos generar desde la clase Comparator.comparing( X -> Qué quiero usar para la comparación )

## Funciones de tipo reduce

### collect

Nos permite transformar un Stream a cualquier tipo Collection (List, Map, Set...)

### forEach

Nos permite invocar a una función de tipo Consumer a la que se le suministra secuencialmente(por defecto) cada elemento del Stream.
---

En JAVA 1.8, además de añadir el tipo Stream<T>, se añade una función a todas los tipos Collection que nos permite transformar cualquier Collection (List, Map, Set...) a un Stream.
Esa función es la función .stream()

En el tipo Stream<T> hay una función llamada collect que nos permite transformar un Stream a cualquier tipo Collection (List, Map, Set...)

---

Vamos a montar un programa que sugiera correcciones ortográficas.

Dada una palabra, vamos a mirar a qué palabras se parece del diccionario... si es que la palabra no existe en el diccionario.
Y vamos a devolver las 10 mejores opciones para la palabra:

- manana
  Esa palabra no existe en español... pero se parece a mañana, banana, manzana, manzano, ananá

Os voy a pasar un diccionario (luego)

Para ver si una palabra se parece o no a otra, vamos a usar la distancia de Levenshtein.

La distancia de Levenshtein entre dos palabras es el número mínimo de caracteres que hay que:
- Añadir
- Eliminar
- o Cambiar
en una palabra para que se convierta en la otra.

                    DISTANCIA DE LEVENSHTEIN
manana / manzana            1
manana / ananá              2

Esta función la vamos a aplicar sobre las 650000 palabras de nuestro diccionario... y vamos a devolver las 10 palabras que más se parezcan a la palabra que nos pasen.

---

cuando nos enfrentamos a un algoritmo Map-REDUCE, 
- lo primero es IDENTIFICAR el conjunto de datos de entrada... en nuestro caso: un Stream<String> con las palabras del diccionario
- después, identificar la salida... en nuestro caso: List<String> con las 10 palabras que más se parecen a la palabra que nos pasan
- Y a partir de ahí, ir identificando las transformaciones que debemos aplicar para, partiendo de la entrada, llegar a la salida.


COLECCION INICIAL                                                               COLECCION FINAL
manzana       -> 1          -> 1        -> 1                                          manzana
banana        -> 1             1           1                                          banana
manzano       -> 2             2           2                                          manzano
albaricoque   -> montonón      montonón

Pregunta.... los elementos de la colección final, pueden ir ordenados de cualquier forma? ORDENAR POR LA DISTANCIA DE LEVENSHTEIN ASCENDENTE

ColeccionInicial        Stream<String>
    vvvv
 .filter( palabra -> Math.abs(palabra.length() - palabraOriginal.length()) <= 3 )                          Stream<String>
    vvvv
 .map( palabra -> new PalabraPuntuada(palabra, distanciaDeLevenshtein( palabra, palabraOriginal ) ) )      Stream<PalabraPuntuada> 
                                                                        // Necesitaríamos algo que incluyera la palabra y su distancia
    vvvv
 .filter( palabraPuntuada -> palabraPuntuada.puntuacion <= 3 )                                             Stream<PalabraPuntuada>
    vvvv
 .sorted( Collectors.comparing( palabraPuntuada -> palabraPuntuada.puntuacion ) )                          Stream<PalabraPuntuada>
    vvvv
 .limit( 10 )                                                                                              Stream<PalabraPuntuada>
    vvvv
 .map( palabraPuntuada -> palabraPuntuada.palabra )                                                        Stream<String>
    vvvv
 .collect( Collectors.toList() )                                                                           List<String>
    vvvv
ColeccionFinal                                                                                             List<String>


En el código tendremos una función llamada: distanciaDeLevenshtein( String palabra1, String palabra2 ) que nos devolverá la distancia de Levenshtein entre las dos palabras.

class PalabraPuntuada {
    String palabra;
    Integer puntuacion;

    PalabraPuntuada( String palabra, Integer puntuacion ) {
        this.palabra = palabra;
        this.puntuacion = puntuacion;
    }
}

El mapeo y el ordenado son lo más pesado con diferencia... podemos optimizarlo?

camo NO EXISTE
albaricoque ALTERNATIVA POSIBLE... La ofrecería yo? NO... es muy distinta

Las sugerencias que vaís a ofrecer, deben ser RAZONABLES.
Podemos aceptar que la persona se haya equivocado al pulsar una tecla de la palabra... 2... pero si se ha equivocado en 3 teclas... 

    persona
    prrslnw

Tomaremos una decisión. Si una palabra tiene una distancia de Levenshtein mayor que N (por ejemplo 3) no la vamos a ofrecer como sugerencia.    

camo
????????


---

# MAVEN

Es una herramienta de automatización de trabajos habituales en proyectos de software, principalmente JAVA.

Cuando trabajamos con maven: 

    proyecto/
        src/
            main/
                java/
                resources/
            test/
                java/
                resources/
        target/
            classes/
            test-classes/
        pom.xml

Los trabajos que automatizamos en maven se denominan goals.
Por defecto maven trae una serie de goals definidos:
- compile           Compila lo que hay en src/main/java y lo deja en target/classes
                    Y además, copia lo que hay en src/main/resources a target/classes 
- test-compile      Compila lo que hay en src/test/java y lo deja en target/test-classes
                    Y además, copia lo que hay en src/test/resources a target/test-classes
- test              Ejecuta los test que existen en target/test-classes
- package           Empaqueta el proyecto: .jar, .war, .ear, .pom
- install           Copia el empaquetado a la carpeta .m2... para qué? Para que mi proyecto pueda ser usado como dependencia en otros proyectos
- clean             Borra la carpeta target
Todo goal se ejecuta mediante un plugin.

En el archivo pom.xml definimos la configuración de nuestro proyecto para maven:
- Coordenadas del proyecto: LO QUE IDENTIFICA EL PROYECTO
  - groupId
  - artifactId
  - version
- Metadatos:
  - Nombre
  - Descripción
  - URL
  - Licencia
  - Autor
- plugins que usaremos para automatizar trabajos
- Configuraciones para esos plugins
- Dependencias que necesitamos para nuestro proyecto

## Carpeta .m2

Una carpeta que maven crea en nuestra carpeta de usuario:
c:\Usuarios\miusuario
/home/miusuario

En la que maven va descargando de mi repositorio de artefactos las dependencias que necesito para mis proyectos.
