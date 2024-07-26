# BigData

Tratamiento de:
- Grandes volúmenes de datos
- Datos con una ventana de tiempo útil muy pequeña
- Datos muy complejos
Cuando las técnicas tradicionales ya no me son útiles.

> Imaginad que tengo un USB limpito de 16Gbs. Y quiero meter en él un archivo de 5 Gbs. Puedo?

Depende del formato (sistema de archivos) del usb... 
FAT-16? No, no puedo. Está limitado a 2 Gbs.
FAT-32? No, no puedo. Está limitado a 4 Gbs.
NTFS, EXT4, zFS? Si...

Y si tengo un archivo de 2 Pb: 2 Exabytes? qué sistema de archivos uso?

> ClashRoyale. Había un modo 2v2

En un segundo quizás hago 2 movimientos. 
Un movimiento implica que de mi dispositivo deben mandarse al final 3 mensajes a los dispositivos de los contrincantes y compañero. 
Si hago 2 mov / segundo: 6 mensajes/segundo
Pero somos 4 personas: 24 mensajes/segundo
Y si en un momento dado del tiempo hay 50k partidas jugando? 1.2M mensajes/segundo

Qué servidor soporta el envío de 1.2M mensajes/segundo?

> Quiero hacer la lista de la compra: Producto | Cantidad

Qué programa uso? Bloc de notas / Excel... si voy a comprar 200 cosas
Y si tengo un listado de 300.000 cosas? El excel se hace caquita
Siempre me quedará el MySQL. Y si quiero meter 10M de productos? El mysql empieza a hacer aguas
Siempre me quedará el MS SQL Server. Y si quiero meter 100M de productos? El MS SQL Server empieza a hacer aguas
Siempre me quedará el Oracle. Y si quiero meter 1B de productos? El Oracle empieza a hacer aguas

Y ahora qué? Ahora cambio de estrategia. Me voy a un enfoque BIG-DATA.
BigData implica una combinación de hardware y software.

Básicamente es utilizar la capacidad de cómo puto conjunta de un montón de máquinas de mierda (commodity hardware)!

Para hacer qué? Dependerá de mi escenario:
- Analizar datos o no
- Almacenar datos o no
- Transmitir datos o no

Los primeros que se encontraron de verdad con este problema fueron los de Google.... y le dieron solución:
- GFS (Google File System) Un sistema de archivos distribuido. Cada archivo se descompone (trocea) en bloques y esos bloques se distribuyen por los nodos del cluster (por un conjunto de máquinas de mierda). 
  Ventajas:
  - Almacenamiento de archivos gigantes
  - IO: Velocidad de lectura y escritura es enorme
- BigTable... que se basaba en un modelo de nuevo de programación: MapReduce

La gente de google tuvo a bien publicar en sendors papers el funcionamiento de su GFS y BigTable.
Y un hombrecillo realizó una implementación de estos papers en código abierto: Apache Hadoop

# Apache Hadoop

Nos ofrece un sistema de archivos distribuido (HDFS) y un motor de procesamiento distribuido (MapReduce).

Básicamente, Hadoop es el equivalente a un Sistema Operativo para un cluster de máquinas.
Me permite ejecutar un proceso de tratamiento de datos usando la CPU, RAM y HDD de todas las máquinas del cluster.

Al final, la funcionalidad clave de unn SO es controlar la CPU, RAM y HDD de un ordenador. Hadoop hace lo mismo pero para un cluster de máquinas.

Esto está guay!... o no!
Quiero decir.. al final un SO... vale pa' na'.
Y dentro del mundo BigData, encontramos un montón de Apps que ruedan sobre Apache Hadoop:
- Sistemas de mensajería: Kafka
- BBDD: HBase, Cassandra, Hive
- Machine learning: Mahout
- Transformación de datos: Spark, Storm
- ...

El problema es que el motor de procesamiento MapReduce que lleva incorporado Apache Hadoop es muy lento. Cada conjunto de datos antes de mandarse a cada nodo del cluster se guarda en disco. Y al llegar a un nodo se guarda en disco. Y al finalizar el proceso se guarda en disco. Y todo esto es muy lento.

    Cluster de Apache Hadoop:
        Maquina 1
            SO (Linux) + Hadoop (HDFS + MapReduce) + Spark
        Maquina 2
            SO (Linux) + Hadoop (HDFS + MapReduce) + Spark
        Maquina N
            SO (Linux) + Hadoop (HDFS + MapReduce) + Spark
        Una de esas máquinas se nombrará MAESTRA DEL CLUSTER... y recibirá peticiones:
        - Guardame este archivo
        - Recuperame este archivo
        - Procesamé estos datos... con este tratamiento concreto
        Y el Hadoop de ese nodo maestro, reparte el trabajo y lo manda al resto de Hadoops de las máquinas trabajadoras.

# MapReduce

Es un modelo de programación (una forma de escribir programas) basada en programación funcional (NO SE PUEDE HACER DE OTRA FORMA) que nos permite paralelizar el procesamiento de datos usando la potencia computacional de un cluster de máquinas.

# Apache Spark

Es una reimplementación del motor de procesamiento MapReduce de Apache Hadoop que trabaja sobre la memoria RAM de las máquinas del cluster, lo que hace que sea mucho más rápido que el motor de procesamiento MapReduce que venía con Apache Hadoop.
Framework para el tratamiento paralelizado de volúmenes grandes de información proveniente de distintas fuentes.

---

El modelo de programación MAP-REDUCE se soporta de forma nativa por el lenguaje de programación JAVA? SI, desde Java 1.8
Antes imposible, ya que hemos dicho que se basa en programación FUNCIONAL... que no se soportó en JAVA hasta la versión 1.8.
En esa versión además se incluyó el paquete java.util.stream. Donde se define la clase Stream que nos permite trabajar paralelizadamente con colecciones de datos mediante programación funcional.

De hecho, Spark (el core de Spark) lo único que nos ofrece es una reimplementación de la clase Stream de JAVA, que al ejecutar el script map-reduce lo hace sobre una infraestructura remota y no local: RDD (Resilient Distributed Dataset)

Al final el trabajar con Spark es muy sencillo (o no). Es como trabajar con JAVA y la clase Stream. Pero en lugar de trabajar con la clase Stream, lo haremos con la clase RDD.
De hecho un programa JAVA que use la clase Stream (MAP-REDUCE) puedo convertirlo a un programa Spark, cambiando 1/2 palabras.

El problema de Spark (el principal... luego están los secundarios) no es spark en si... es la programación funcional y el modelo de programación MapReduce. Que no es sencillo de entender.

---

¿Qué vamos a hacer en la formación?
- Entender qué es eso de la programación funcional: LUNES
- Entender el concepto de MapReduce... y cómo se montan programas MapReduce (desde JAVA, con Streams) MARTES / MIÉRCOLES
- Migraremos los programas a Apache Spark, con RDDs VIERNES / LUNES
- Más adelante, tiraremos todo lo que hagamos a la basura, y empezaremos a usar una libraría que tiene dentro Apache Spark: SQL  MARTES
- Instalaremos un cluster REAL de Apache Spark y los programas que hayamos creado y ejecutado en nuestra computadora, los lanzaremos en el cluster MIERCOLES

---

# Programación Funcional

Paradigmas de programación:
- Imperativo            Cuando damos instrucciones que la computadora procesa secuencialmente.
                        Cuando necesitamos romper la secuencialidad, usamos estructuras de control (if, for, while) 
- Procedural            Cuando el lenguaje me permite agrupar instrucciones en procedimientos/funciones/métodos/subrutinas
                        Y posteriormente invocar esos procedimientos/funciones/métodos/subrutinas 
- Funcional             Cuando el lenguaje me permite que una variable apunte a una función
                        Y posteriormente ejecutar la función desde la variable
                        Entonces digo que el lenguaje soporta programación funcional.
                        El tema no es lo que es la programación funcional... Si no lo que me permite hacer cuando el lenguaje me ofrece esas características.
                        Desde este momento puedo:
                        - Crear funciones que admitan funciones como argumentos.
                        - Crear funciones que devuelvan funciones.
- Orientado a objetos   Cuando el lenguaje me permite definir mis propios tipos de datos... con sus características y funciones asociadas.
                        String
                        ZoneTime
                        List<?> 
                        + Usuario
- Declarativo           Herramientas como Springboot o Angular, que me permiten definir el comportamiento de mi aplicación sin tener que programar.

¿Qué es una variable?

Una variable, al menos en Java, JS, Python, no es un cajoncito donde meto cosas... Ni de coña.
Eso colaría como definición de variable en lenguajes como C, C++, Fortran, Ada

En JS, Python, JAVA una variable es una referencia a un dato en memoria RAM.

```java
String texto = "hola";
```
    "hola"          Crea en memoria un objeto de tipo String con valor: "hola"
    String texto    Crea una variable en memoria de tipo String. 
                    En JAVA, que es un lenguaje de tipado fuerte o estático, la variables también tienen tipo.
    =               Pego el postit al lado del valor en memoria. Asigno la variable texto al dato "hola"

