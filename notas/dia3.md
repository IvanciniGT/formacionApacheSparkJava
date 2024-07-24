```java
// Dia 1
class PalabraPuntuada {
    public String palabra;
    public int puntuacion;

    public PalabraPuntuada(String palabra, int puntuacion) {
        this.palabra = palabra;
        this.puntuacion = puntuacion;
    }
}
    
...
// Dia 2 al 100 tengo gente por ahñi escribiendo cosas como esta:
PalabraSugerida p1 = new PalabraPuntuada("hola", 10);
System.out.println(p1.palabra);
System.out.println(p1.puntuacion);
p1.puntuacion = 20;
System.out.println(p1.puntuacion);

// Dia 101 y digo: Quiero forzar aq que la puntuacion no pueda ser negativa
    class PalabraPuntuada {
        public String palabra;
        private int puntuacion;

        public PalabraPuntuada(String palabra, int puntuacion) {
            this.palabra = palabra;
            this.setPuntuacion( puntuacion );
        }

        public int getPuntuacion() {
            return puntuacion;
        }
                
        public void setPuntuacion(int puntuacion) {
            if (puntuacion < 0) { // Solo aqu puedo meter esta condición
                throw new IllegalArgumentException("La puntuacion no puede ser negativa");
            }
            this.puntuacion = puntuacion;
        }
    }
    // El día 102 tengo a 1M de personas kalasnikov en mano buscándome porque su código no compila
```



En JAVA eso es una mala práctica. Las variables deben accederse mediante getters y setters. 
Ya que JAVA lo pensaron como el culo y si el día de mañana quieres meter algún control sobre las variables no puedes sin joder el API de la clase... 
La buena práctica es desde el día 0 meter todas las variables privadas y getters y setters para todo... por si aca!
En otros lenguajes existe el concepto de properties: PYTHON, C#, JS, TS.
```java
    class PalabraPuntuada {
        private String palabra;
        private int puntuacion;

        public PalabraPuntuada(String palabra, int puntuacion) {
            this.palabra = palabra;
            this.puntuacion = puntuacion;
        }

        public String getPalabra() {
            return palabra;
        }

        public int getPuntuacion() {
            return puntuacion;
        }
        
        public void setPalabra(String palabra) {
            this.palabra = palabra;
        }
        
        public void setPuntuacion(int puntuacion) {
            this.puntuacion = puntuacion;
        }
    }
```

---

ángel Un ángel... emisario del señor !
Ángel Nombre propio

---

# Pruebas de rendimiento en JAVA

Java es un lenguaje compilado o interpretado?
Java es raro... Es las 2 cosas a la vez.

.java -> compilamos --> .class -> interpretado por la JVM (en tiempo de ejecución)
                        byte-code

La interpretación es un proceso rápido o lento? LENTISIMO. Básicamente es VOLVER A COMPILAR EL CODIGO

Compilación = Proceso para traducir de un lenguaje X a otro lenguaje de más bajo nivel

En general los lenguajes interpretados son muchísimo más lentos que los compilados.

En JAVA 1.2 se añadió al JIT (Just In Time Compiler) el HotSpot.
El JIT es quien interpreta el código byte-code dentro de la JVM y lo compila a código máquina.
El HotSpot es una caché de código máquina que se va generando a medida que se va ejecutando el código.
Cuando esa cache está a pleno rendimiento, mi programa se ejecuta a la velocidad de un lenguaje compilado (como C o C++).

Las apps en JAVA van como un tiro.. siempre y cuando hayamos "calentado el JIT" (llenado la caché de código máquina).

Para hacer una prueba de rendimiento hemos de hacer un warm-up del JIT. Es decir, ejecutar el código varias veces para que el JIT compile el código a código máquina.


Quién lleva el código a la CPU? Un HILO de ejecución (THREAD).
Nuestro código usa 1 HILO... el main... el que se abre asociado al proceso.
Si quiero usar varios cores... necesito repartir el trabajo entre varios hilos.

---

Principal uso de Spark en el BANCO: Desarrollar ETLs

ETL es un proceso por el cuál:
- Extraemos datos de una fuente
- Los Transformamos
- Los Cargamos en un destino (Load)

Hay muchas variantes de ETLs:
- ETL
- ELT
- ETLT
- TELT

En la mayor parte de los casos, en el banco el destino es: 
- BBDD
- Ficheros Parquet (alternativa a loc cvs)

Programas que vamos a dejar en ejecución por las noches: 2:00am... Y recibirán 1M de datos.

La gracia de esto es la ESCALABILIDAD.

ESCALABILIDAD: Capacidad de ajustar la infra a las necesidades de cada momento
-> Ahorro de costes
-> Optimización del rendimiento

A las 15:00, cuantas máquinas necesito calculando datos? NINGUNA
A las 2:00 un día que lleguen 100k registros.. Con 2 me sobra
Y si me llegan 200m de registros? Métele máquinas: 10 máquinas

Y normalmente esas máquinas las contrato en un cloud... y pago por lo que uso!

---
COLECCION DE PALABRAS PROHIBIDAS
caca culo pedo pis mierda

COLECCION DE TWEETS                                                                                                     Stream<String> (tweets)
Fiesta en la playa  #BeachParty#SunFun#CacaSun, bronceado extremo!
#CaféEnLaMañana con un libro... #Relax pero mi café está frío :(
Noche de estudio #StudyHard#NoSleep, ¿quién inventó los exámenes?
En la playa con mis amigos #summerLove#goodVibes
En navidades estudiando #mierdaDeNavidad#odioLaNavidad #odioElTurrón#odioMiVida. PD: No me gusta el turrón.
En la bolera con los primos #mierdaGorda#bowling#goodvibes
                    vvvv
PASO 1: EXTRAER LOS HASHTAGS DE CADA TWEET:
    replace("#", " #")                                  MAP                                                             Stream<String> (tweets) 
    split( "[ .,()_!¿¡'=+-*/@;:<>-]" )                  MAP                                                             Stream<String[]> (palabras)
        "Fiesta"
        "en"
        "la"
        "playa"
        "#BeachParty"
        "#SunFun"
        "#CacaSun"
        "bronceado"
        "extremo"
    filter (los que empiecen por #)                     FILTER                                                           Stream<String[]> (hashtags)
        ["#BeachParty", "#SunFun", "#CacaSun"]
        ["#CaféEnLaMañana", "#Relax"]
        ["#StudyHard", "#NoSleep"]
        ["#summerLove", "#goodVibes"]
        ["#mierdaDeNavidad", "#odioLaNavidad", "#odioElTurrón", "#odioMiVida"]
                    vvvv
    No estaría guay... conseguir aqui algo como:                                                                        Stream<String> (hashtag)
        #BeachParty
        #SunFun
        #CacaSun
        #CaféEnLaMañana
        #Relax
        #StudyHard
        #NoSleep
        #summerLove
        #goodVibes
    ESTO SE PUEDE HACER mediante la función FLATMAP (que es un map + flatten). En el caso de Streams (JAVA pelao')
    A la función flatmap le debemos suministrar una función que reciba un dato de tipo String[] -> Stream<String>
        ["#BeachParty", "#SunFun", "#CacaSun"]  -> Stream<String>
        ["#CaféEnLaMañana", "#Relax"]           -> Otro Stream<String>
        ["#StudyHard", "#NoSleep"]              -> Stream<String>
        ["#summerLove", "#goodVibes"]           -> Stream<String>
        ["#mierdaDeNavidad", "#odioLaNavidad", "#odioElTurrón", "#odioMiVida"] -> Stream<String>
    Lo primero que hace flatmap es aplicar la función de transformación a cada elemento del stream original: map
    Y espera la función flatmap, que esos objetos nuevos sean Streams... Y LOS CONSOLIDA (los aplana en un único Stream: flatten)
                    vvvv
    map (NORMALIZAR)
    map (QUITAR EL CUADRADITO)
                    vvvv
    ¿Qué más? Filtrar los que contengan palabras prohibidas.... ¿COMO? Mediante un PREDICADO suministrado al FILTER

        List<String> palabrasProhibidas = List.of("caca", "culo", "pedo", "pis", "mierda");
        
        (String hashtag) -> {
            //if(palabrasProhibidas.contains(hashtag) // ME TEMO QUE ES TODO LO CONTRARIO !
                // NO QUIERO VER SI EL HASTAG ESTA CONTENIDO EN LA LISTA DE PALABRAS PROHIBIDAS
                // QUIERO VER SI EL HASHTAG CONTIENE ALGUNA DE LAS PALABRAS QUE ESTAN EN LA LISTA DE PALABRAS PROHIBIDAS
   
            // Programacion imperativa
            boolean meVale = True;
            for(String palabraProhibida : palabrasProhibidas) {
                if (hashtag.contains(palabraProhibida)) {
                    meVale = False;
                    break;
                }
            }
            return meVale;

            // Podemos montar esto mismo con MAP-REDUCE
            return palabrasProhibidas.stream().filter( palabraProhibida -> hashtag.contains(palabraProhibida) ).count() == 0;

            return palabrasProhibidas.stream().noneMatch( hashtag::contains );
        }

        hashtag -> palabrasProhibidas.stream().noneMatch( hashtag::contains )

RESULTADO: List<Hashtag> Daremos un mapa con 10 entradas
BeachParty -> 10
SunFun     -> 8
goodVibes  -> 2
~~cacaSun    -> 1~~
~~mierdaDeNavidad -> 1~~
---