
# Apache Spark

Es una reimplementación del motor de procesamiento Map-Reduce de Apache Hadoop.
Nos permite hacer procesamiento de datos según MAP-REDUCE aprovechando toda la potencia conjunta de un cluster de computadoras.

Apache Spark cuenta con varias librerías:
- Spark Core: Librería principal que proporciona la funcionalidad básica de Spark.
- Spark SQL: Librería que permite trabajar con datos estructurados de forma más sencilla que como lo hacemos con Spark Core.
- Spark Streaming: Librería que permite trabajar con datos en tiempo real. DEPRECATED
  La funcionalidad que nos ofrecía esta librería ahora la podemos encontrar en la librería Spark SQL.
- MLlib: Librería que nos permite trabajar con Machine Learning.
- GraphX: Librería que nos permite trabajar con grafos.

# Apache Spark Core

Esta librería es en sí la que ofrece el reemplazo del motor de procesamiento Map-Reduce de Apache Hadoop.

Nos ofrece un tipo de objeto llamado RDD (Resilient Distributed Dataset) que es una colección de elementos sobre la que podemos aplicar programación MapReduce.
Esto parece lo mismo0 que un Stream de Java... Y básicamente en la forma de comunicarme con él lo es.
Pero la diferencia radica en que un RDD es una colección de elementos cuyo procesamiento (y datos) se distribuye en un cluster de computadoras.

Al trabajar con Streams de Java, vimos que podemos hacer cálculos en paralelo, pero estos cálculos se hacen en la misma máquina (Abriendo threads para los cores de la misma máquina). Cuando trabajamos con RDDs de Spark, los cálculos se hacen en paralelo en distintas máquinas, abriendo procesos en distintas máquinas del cluster.

    Computadora cliente                                 Cluster de máquinas con Spark Instalado
    con                                                 Máquina maestra                     Máquinas trabajadoras

    JVM             ------- spark core ------------->   JVM                                  JVM (n)
                             |                            ^                                  ^      
    Mi app (su código)-------+                            Vamos a tener corriendo Apache Spark
    Y voy a solicitar la ejecución de mi programa

    Mi programa hace una conexión con el cluster por tcp/ip. Esa conexión la hace contra la computadora maestra del cluster.
    A ella le manda:
    - El procesamiento que hay que hacer (el algoritmo MAP REDUCE)
    - Los datos que debe procesar

    Esa máquina maestra reparte los datos entre las máquinas trabajadoras del cluster. Del conjunto original hace m particiones. Y las va mandando de 1 en 1 a las n máquinas trabajadoras. Además, a TODAS les envía el algoritmo MAP REDUCE que deben ejecutar. Todos los nodos trabajadores ejecutan el mismo algoritmo, pero sobre distintos datos.
    Cuando van acabando van devolviendo los resultados a la máquina maestra, que los consolida. La máquina maestra va juntando los resultados y cuando todos los nodos trabajadores han acabado, devuelve el resultado final a la computadora cliente.

    n Máquinas trabajadoras
    m Paquetes (Subconjuntos de datos)

    Cada paquete se procesa en paralelo en una máquina trabajadora distinta. Para cada uno, se abre un proceso JAVA distinto, con un único thread, que se asocia a un core de la máquina trabajadora.

    Si tengo m < n. Mi cluster tiene nodos(cores) parados
    Si tengo m > n. Hay paquetes que se encolan... Y según ciertos nodos vayan acabando, irán recibiendo nuevos paquetes.

    Qué nos interesa? 
     n > m ... Estamos perdiendo (desaprovechando potencia de cálculo)
     n = m ... Esto está bien... Aprovecho al máximo la potencia de cálculo del cluster
     n < m ... PERO ESTO ES LO  QUE QUEREMOS
        Querremos MAS PAQUETES QUE NODOS... De hecho queremos MUCHOS más paquetes que nodos.
        Por qué?
            Hay factores en contra:
            - Más comunicaciones por red Y ESTO VA EN CONTRA DEL RENDIMIENTO (marginalmente)
            Hay muchos factores (e importantes a favor):
            - Que pasa si tengo 10 cores y hago 10 paquetes de 1000 datos cada uno?
                Y de repente... uno de los procesos falla, cuando va por procesando el dato 999 de su paquete.
                Spark es listo ( Rdd: RESILIENT distributed dataset): RESILIENTE: Si el proceso falla, se vuelve a lanzar en otra máquina trabajadora... y los datos no se pierden... PERO OJO, el procesamiento SI SE PIERDE.
            - Mi cluster posiblemente no será SOLO PARA MI. Habrá otros procesos corriendo en él. 
              Puede haber máquinas ocupadas... la cuenta no es sencilla.
            - Mi cluster... MI CLUSTER????¿ Dónde se va a ajecutar esto? NPI
              Una de las grandes ventajas que veíamos en Toda esta movida era la flexibilidad... Podía coger un cluster muy pequeño en un momento dado.. 
              y cuando reciba carga gorda de trabajo, podría ampliar el cluster. Y NO QUIERO ESTAR CAMBIANDO CODIGO
            - Puede ser que una máquina en un momento dado esté ocupada... Y las otras las pongo a trabajar. Si hago el mismo número de paquetes que de máquinas trabajadoras:

                Tengo 10 máquinas (1 ocupada) y hago 10 paquetes.
                 9 máquinas se ponen a trabajar...
                 1 se queda sin hacer lo mio... está haciendo otra cosa...
                 Tengo un paquete pte de procesar... Y no tengo máquinas trabajadoras libres para procesarlo.
                 Al rato, la máquina que estaba ocupada queda libre... y toma el último paquete que tenía...
                 Al poquito las otras 9 acaban.... y yo sigo esperando como un pringao a que acabe la última!
                 Mientras tengo 9 máquinas paradas.
                 si huviera hecho paquetes más pequeños... y muchos más, habría aprovechado mejor la potencia de cálculo del cluster.

      Pero cuidado... si me paso, y hago demasiadas particiones... VOY A PENALIZAR MUCHO LA COMUNICACIÓN POR RED... Y VOY A PERDER RENDIMIENTO.

      Y lo de la comunicación por red NO ES MOCO DE PAVO!!!! Eso es complejo y puede resultar DRAMATICO en un entorno Spark.
      Asociado a cada paquete de trabajo... no se mandan solo los datos...
      - Se manda también el algoritmo MAP REDUCE que se va a ejecutar
      - Que no tenga que cruzar los datos que mando con otro conjunto de datos!... Que ese otro conjunto de datos NO SE PARTICIONA y como me descuide se manda entero 
        cada vez que se ejecuta un paquete de trabajo.

        TABLA de datos:
        Personas    |                                                                    CP
        ID | Nombre | Apellido | Edad | Sexo | Profesión | Salario | CP                  ID | CP | Ciudad...

        Y quiero hacer UN JOIN entre ambas tablas... QUIERO ENRIQUECER LA TABLA DE PERSONAS CON LA CIUDAD A LA QUE PERTENECEN.
            Personas: 1M de datos      CPs: 10K de datos
        
            - Puedo particionar la tabla de personas para mandarla a las máquinas trabajadoras? SI
                Genero 10 paquetes de 100K de datos cada uno
            - Puedo particionar la tabla de CPs para mandarla a las máquinas trabajadoras? NO
                Cada nodo trabajador tiene que tener la tabla de CPs entera... A priori no tengpo npi de qué CPs voy a necesitar en cada nodo trabajador.
                Hombre (o mujer!!!) se puede ordenar previamente por CPs la tabla de personas y hacer el particionado en base a eso...
                Sería buena idea? CUANTO TARDO EN HACER UN SORT de 1M de datos? Vete a por café! Hoy y mañana... que cuando vuelvas lo mismo sigue ordenando.
                LA REALIDAD ES QUE UN SORT TARDA UN HUEVO.

En un entorno de producción, cuantas copias se hace de un dato? Al menos 3.
El dato es lo único que aporta valor.
Si necesito guardar 3Tbs... necesito 3 HDD de 3Tbs... de los caros.
Y luego viene las Copias de seguridad...
Y al final para guardar 3Tbs necesito 18Tbs de espacio en disco.
Eso los datos... si encima genero INDICES... Se me puede multiplicar por 3 el espacio en disco.... Y al final necesitaría 54Tbs de espacio en disco.
Y aún así, las BBDD crean indices... y pago gustoso esa pasta por tener un buen rendimiento en las consultas.
Aquí no vamos a estar haciendo SORT alegremente. MUCHO OJO CON LOS SORTS.
Y MUCHO MAS OJOO con los sorts encubiertos!
Si me voy a SQL.. que operaciones implican un SORT?
- ORDER BY
- Cualquier JOIN
- GROUP BY
- DISTINCT
- UNION

Y el martes, veremos que en Spark (con la librería SQL) podemos tirar queries SQL... Y que detrás de cada query SQL hay un plan de ejecución...
Y que ese plan de ejecución puede implicar SORTS. Y SPARK no es una BBDD... y no tiene los datos preordenados... y la podemos liar parda.
Y que mi programa tarde la vida! LA VIDA = 10x lo que debería!

---

Por cierto... hablando de almacenamiento.

Imaginad que tengo que guardar el DNI de una persona. Hablo de DNI (Español)... en una BBDD...
Cómo lo guardo:

CREATE TABLE personas (                                         CUANTO OCUPA, en bytes?
id INT PRIMARY KEY,
dni CHAR(9),    -- Lo guardo todo como texto                
dni VARCHAR(9), -- Lo guardo todo como texto
dni INT,        -- solo guardo el número sin la letra
);


Cuánto ocupa guardar 1 caracter? DEPENDE del juego de caracteres que estemos usando: ASCII, UTF-8, UTF-16, UTF-32, ISO-8859-1...
En 1 byte cuantos datos diferentes puedo llegar a representar? 2^8 = 256... Qué significa cada combinación de bits posible

SECUENCIA DE BITS en 1 byte             QUÉ SIGNIFICA? Eso depende del TIPO DE DATO
Entero          Entero sin signo    Caracter ASCII
0000 0000?                              -128                0                   
0000 0001?                              -127                1                   
.....                                                                           a, e, k, ( ...
1111 1111?                               127                255

En ASCII puedo guardar (representar) 256 caracteres diferentes... Ná más...

Cuántos caracteres usa la humanidad? Más de 150.000
Todo ellos están recogiéndose en UNICODE... UNICODE es un estándar que recoge todos los caracteres que se usan en la humanidad.
Eso me entra en 1 byte? NO
Y en 2 bytes? 256 x 256 = 65.536... tampoco
Y en 4 bytes? 256 x 256 x 256 x 256 = 4.294.967.296... Ahí si.

Y UNICODE define transformacioones... formas de representar en bits los caracteres...
UTF-8 Usa de 1 a 4 bytes para representar un caracter
Los caracteres sencillos usan 1 byte (a, 9, .)
Los caractyeres no tan sencillos, pero no tan poco comunes usan 2 bytes (ñ, á, é, í, ó, ú)
Los caracteres más raros usan 4 bytes (caracteres chinos, japoneses, árabes, cirílicos...)
UTF-16 Usa de 2 a 4 bytes para representar un caracter
Los caracteres sencillos usan 2 bytes
Los caracteres más raros usan 4 bytes
UTF-32 Usa 4 bytes para representar un caracter

BUENO... que estamos guardando DNIs (1234567890a-z)
Son caracteres simplones... si no me vuelvo loco y elijo UTF-32...
Lo normal es que esto ocupe 9 bytes en total.... 1 por caracter.
Aunque ... todos los DNIs tienen 9 caracteres? El del rey es el 15.
Almacenado como CHAR(9) ocuparían siempre 9 bytes... con independencia de que el DNI tenga 9 caracteres o menos (se completa con espacios en blanco)
Almacenado como VARCHAR(9) ocuparían de 1 a 9 bytes según el número de caracteres que tenga el DNI.... lo normal es que ocupe 8-9 bytes.
Almacenado como número? Pues 4 bytes...
Otra cosa en este caso es que quiera guardar o no la letra:
- Si no la guardo... tendré gasto de CPU cuando vaya a usar el dato... para calcular la letra desde el número. Y eso sí, ahorro espacio en disco.
- Si la guardo... ahorro CPU... pero gasto espacio en disco.... Cada DNI pasa a ocupar 5 bytes.

        En cualquiera de los casos, paso de 8-9 bytes a 4-5 bytes... y si tengo 1M de datos... pues me ahorro 4-5Mb de espacio en disco.
        Espacio en disco que multiplico luego por 5 al menos... (3 copias del dato + backups) -> AHORRO es de: 20-25Mb de espacio en disco SOLO POR UN CAMPO en un conjunto de datos.

        Y el tema no es solo el ahorro en almacenamiento:
        - Ahorro en tiempo al leer los datos de disco
        - Ahorro en tiempo al mandar los datos por red
        - Ahorro en tiempo al procesar los datos
        - Ahorro en tiempo al guardar los datos en disco

Si trabajo con 4 datos, (o 4 millones) estas cosas ni me las planteo...
Pero estamos en un mundo donde supuestamente vamos a trabajar con huevón de datos. Y ME LAS TENGO QUE VOLVER A PLANTEAR.
Hablo de volver a plantear porque antes SI NOS LO PLANTEÁBAMOS. Hace 30 años... Pero después hemos vivido en el mundo de la abundancia...
Y hemos perdido la costumbre de optimizar todas estas cosas.

Y esto tiene una consecuencia inmediata.
Los formatos de texto plano CSV, XML, JSON, YAML... son buenas elecciones para almacenar datos (a gran escala) NI DE COÑA
Se guarda todo como texto.... y eso implica que el fichero resultante me ocupa 2-3 veces más al menos que si lo guardo en BINARIO.
SIEMPRE QUIERO UN FORMATO BINARIO para guardar datos a gran escala, donde en base al tipo de dato, se puedo optimizar el espacio en disco MUCHISIMO!
Que ya hemos dicho que es caro! a rabiar!

Y ahí es donde salen AVRO y PARQUET. Ambos son formatos binarios que permiten guardar datos de forma optimizada en cuanto a almacenamiento.... y a la forma de trabajar con ellos posteriormente.
- AVRO      Es un formato orientado a filas (como csv)
- PARQUET   Es un formato orientado a columnas


> Ejemplo:
| ID | Nombre | DNI       |
|----|--------|-----------|
| 1  | Pepe   | 12345678A |
| 2  | Juan   | 87654321B |
| 3  | María  | 12345678A |
| 4  | Ana    | 87654321B |

En AVRO, esos datos se guardarían en disco:
METADATOS1Pepe12345678A2Juan87654321B3María12345678A4Ana87654321B... pero en binario
En PARQUET se guardarían en disco:
METADATOS1234PepeJuanMaríaAna12345678A87654321B12345678A87654321B... pero en binario
En ambos casos, antes de los datos, se guardan metadatos
Esos metadatos incluyen:
AVRO: Posicion en bytes en la que comienza cada fila
PARQUET: Posición en bytes en la que comienza cada columna
Si al procesar los datos, quiero hacerlo fila a fila, AVRO es más eficiente.
Si al procesar los datos, quiero hacerlo columna a columna, PARQUET es más eficiente.

En general, para ALMACENAR PERSISTENTEMENTE la información, PARQUET es la elección más eficiente.
Pero hay veces que hacemos otras cosas:

    Sease una red social llamada X (antes Twitter) donde la gente manda tweets... y queremos calcular los trending topics.

    MOVIL de un polluelo o polluela donde tengo la app.. y un formulario... y escriben su mierda... y la mandan... A DONDE LA MANDAN?
    - A mi programa que calcula los trending topics?
    - A mi programa que publica los tweets en mi muro?
    - A mi programa que extrae las menciones para enviar notificaciones?
    - A mi programa que analiza si el tweet es politicamente correcto (cumple con la normativa WOKE!!!!)

    Lo manda a una cola de mensajería (KAFKA).
    De ese KAFKA estará leyendo el resto de programas.
    Y los datos se procesan por columnas o por filas? Los proceso TWEET a TWEET... Por filas: AVRO es lo que dejaré en el KAFKA.

---
Retomando el Apache Spark... Todos los programas que vamos a montar serán IGUALES !!!! (tendrán la misma estructura)
- Paso 1: Abrir una conexión con el cluster
- Paso 2: Cargar los datos
- Paso 3: Definir el algoritmo MAP REDUCE a aplicar sobre los datos
- Paso 4: Esperar comodamente mientras el cluster hace su mierda
- Paso 5: Recoger los resultados
- Paso 6: Cerrar la conexión con el cluster

Los datos, ya hemos dicho que al trabajar con Spark Core los vamos a representar/gestionar/almacenar en un JavaRDD.
Sobre ese JavaRDD es sobre el que configuramos el algoritmo MAP REDUCE que queremos aplicar.

Pero antes necesito abrir una conexión con el cluster. Y para ello necesito un SparkContext / JavaSparkContext que se creará en base a un SparkConf .
En la configuración diremos:
- A qué cluster nos vamos a conectar (URL)
- Qué nombre le vamos a dar a nuestra aplicación dentro del cluster (en mi cluster puede haber muchos trabajos corriendo... y quiero poder identificar el mío)
- Y otros...
  Una vez creada la configuración, con esos datos, crearemos una conexión (contexto) con el cluster.
  Al contexto es al que le pediré que genere un RDD(JavaRDD) a partir de los datos que le pase.
  Sobre ese JavaRDD configuro el algoritmo MAP REDUCE que quiero aplicar.
  Y le pido que lo ejecute.
  Cierra la conexión con el cluster.

---

VOLVEMOS A JAVA PELAO. Para aplicar operaciones Map Reduce, tenemos: Stream<T>, que trabaja con genéricos (como las listas, mapas...).
Y si quiero tener una lista de enteros:
List<int> miListaDeNumeros

# Mierderio de JAVA. Parte III.
En JAVA tenemos esa mierda llamada: tipos primitivos y tipos de objeto... y a nivel de la JVM se trata de forma distinta.
Antiguamente (antes de java 5) para tipos de datos simples, lo que teníamos era el tipo primitivo.
Pero todo cambia con la aparición de los genéricos en Java 5.
Y resulta que ahora, en una colección: List<T> puedo personalizarla para el tipo concreto que quiera... pero los genericos no admiten tipos primitivos.
Y mierdean el código de JAVA creando unos nuevos tipos de datos, paralelos a los primitivos, que son los tipos de objeto.
- Integer -> int
- Long -> long
- Short -> short
- Byte -> byte
  Y para que no se me complique y mande definitivamente JAVA a la mierda, se inevtan un rollo llamado autoboxing y unboxing, por el cual JAVA se encarga
  de convertir de un tipo a otro automáticamente.

Y todo esto... nos la trae un poco al peiro cuando programamos... ya que solemos trabajar con pocos datos!
Y no nos damos cuenta de la cantidad de mierda/trabajo computacional/desgaste de memoria que hay debajo.

Pero... todo esto cambia si vamos a trabajar con volumenes de datos grandes... ese mierderío pesa! y no poco!

Y por eso, el Java 1.8, además de aparecer el tipo Stream<T>, se crean una serie de tipos de Streams especializados, para trabajar con tipos primitivos.
Y poder hacer operaciones MapReduce sin el mierderío del autoboxing y unboxing.... y sus correspondientes penalizaciones en tiempo y memoria:
- IntStream    != Stream<Integer>
- LongStream   != Stream<Long>
- DoubleStream != Stream<Double>
  ^^^                 ^^^
  Tipos simplon    Tipos de objeto (que son mucho más pesados... si son 4 datos me da igual... pero si son 4 millones... me importa y mucho)
  primitivos

En Spark Core, ocurre algo muy parecido. Tenemos JavaRDD<T>... pero también tenemos otros tipos de RDDs especializados para trabajar con tipos primitivos:
- JavaRDD<T>      != JavaIntRDD
- JavaRDD<T>      != JavaLongRDD
- JavaRDD<T>      != JavaDoubleRDD

Hay otro tipo de RDD que tenemos en Spark y que usamos MUY A MENUDO: JavaPairRDD<K, V>
En cada registro, en lugar de contener un único dato, contiene una pareja de datos ("clave", valor).
Pongo "clave" entre comillas porque no es una clave como la que encontramos en un Map<K, V>... no es única! Puede haber varias parejas con la misma clave.
Lo que me ofrece esa clave (y este tipo de RDD) es la posibilidad de hacer algunas operaciones MAP REDUCE adicionales:
- Agrupar los datos por clave
- Ordenar los datos por clave
  ...


---

En qué lenguaje está programado Apache Spark? SCALA

Qué es SCALA? Un lenguaje de programación.

.java  ----> compilarlos con javac  ----> .class (bytecode) ----> JVM
.scala ----> compilarlos con scalac ----> .class (bytecode) ----> JVM
.kt   ----> compilarlos con kotlinc ----> .class (bytecode) ----> JVM

Scala ofrece una sintaxis alternativa a Java... y añade funcionalidades que Java no tiene (Ya dijimos que JAVA es un lenguaje pensado con la parte baja (muy baja) de la espalda)
De hecho... no es la única alternativa a JAVA para hacer bytecode que corra sobre una JVM...

Tango Kotlin como Scala son lenguajes que compilan a bytecode ... y que corren sobre la JVM.
Pero son lenguajes mucho más pensados que JAVA (que ofrecen una gramática más potente).
De hecho en Android JAVA murió... hoy en día es Kotlin el lenguaje que se usa para programar en Android.

Y ofrece 2 APIs medio paralelas... una pensada para invocarse desde Scala y otra pensada para invocarse desde Java.
scala           java
RDD             JavaRDD
SparkContext    JavaSparkContext
