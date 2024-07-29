
# Máquinas virtuales en un Cluster de Spark:
                                
DONDE EJECUTO LA APP                                CLUSTER
                                                    <---------------------------------------------------------->
                                                    
Computador(MiPC)   ---> datos + funciones ---->     Master    --- Subconjuntos + funciones ---->    Worker1 (JVM)
(JVM)                                               (JVM)                                           Worker2 (JVM)
                                                                                                    WorkerN (JVM)

  ^^^^
  miapp.jar
  (.class)
  



JVM 1                                                                                           JVM 2   
---------------------------------- Mandarlo por red ------------------------------------------------------------------->
PalabraPuntuada (Intancia A)                                                                      PalabraPuntuada (Instancia A')
 palabra: "archivo"  --->   (serialización) ---> Envío por red ---> (deserialización)  ---->        palabra: "archivo"
 puntuacion: 1                                                                                      puntuacion: 1


Serialización? Qué hace?
Convertir a Bytes el QUE? Qué contiene el mensaje que quiero enviar por red?
    - Tipo de Objeto que deseo crear: PalabraPuntuada
    - Atributos de la clase PalabraPuntuada: 
        palabra: "archivo"
        puntuacion: 1               

Deserialización? Qué hace?Por la red, en bytes:
    "PalabraPuntuada"
    "palabra"
    "archivo"
    "puntuacion"
    1

La JVM 2 recibe esos datos... Y lo que hace?
1. Crear una instancia del tipo Suministrado: PalabraPuntuada
2. Establecer las propiedades de la instancia con los valores recibidos por red (No se hace a nivel de constructor: Se hace mediante reflexión)

public class ServicioUsuarios{
    //@Autowired
    private final RepositorioUsuarios repoUsuarios;

    public ServicioUsuarios(RepositorioUsuarios repoUsuarios){
        this.repoUsuarios = repoUsuarios;
    }
}

# Pregunta: Puede la JVM2 crear una instancia de PalabraPuntuada?

¿Qué necesita para hacerlo? El byteCode (La clase) de PalabraPuntuada

Es una clase estandar de JAVA? NO... entonces la JVM2: Exception: NoSuchClassDefFoundError

Hemos mándado el código (byte-code) de nuestra clase a la JVM2? NO

Necesitamos en la JVM2 (en TODOS NUESTROS WORKERS y MAESTRO tener el archivo .jar que contiene la clase PalabraPuntuada) UPS !!!
Para ello, necesitamos no sólo el archivo .jar... sino también que esté dado de alta en el CLASSPATH de la JVM2

# NOTAS:

## En mi programa quiero hardcodeado la URL del cluster de Spark?

No... de hecho puedo ni saber la ruta.... y no quiero saberla además!

## El nombre de mi .jar va a ser constante?

mijar-v1.2.3.jar

## CONSECUENCIAS:

Una cosa es desarrollar en local... y otra cosa es desplegar en un cluster.
Al trabajar en local, hay cosas de las que pasamos:
- URL -> local
- Los jar.. por estar en local, TODO, ya tengo los jar.

Pero al desplegar en un cluster real:
- Necesito una URL REAL de cluster
- Necesito los jar en el cluster

Spark tiene una herramienta que se llama spark-submit (es un script... disponible en versión Windows y Linux/Posix) que se encarga de lanzar el programa en el cluster.
$ spark-submit --class mi.clase.Main --master spark://miurl:7077 mijar-v1.2.3.jar

Ese comando se encarga de:
- Copiar el jar al cluster (a todos los nodos)
- Lanzar el programa en el cluster concreto (A la URL concreta)

# Acumulator de Spark

Es un tipo de objeto del api de SparkCore que permite a los workers escribir en una variable que está en la JVM que emite el trabajo.
Lo podéis ver como una variable COMPARTIDA entre los workers y el driver.
La JVM que emite el trabajo puede acceder al valor de la variable en cualquier momento.
Los workers tan solo pueden escribir en la variable.
 Cuando un worker acumula en la variable, el resultado de esa acumulación se propaga por red a la JVM que emite el trabajo.
    EMISOR: LEE la variable
    WORKER: ESCRIBE en la variable 

Hay distintos tipos de Acumulators:
- LongAccumulator
- DoubleAccumulator
- CollectionAccumulator

# Broadcast de Spark

Estamos partiendo el trabajo en 100 paquetes... que están siendo consumidos por 2 ejecutores.
Cada vez que mandamos un paquete a un ejecutor, se envía:
- Partición correspondiente
- Código de las funciones MapReduce que debe ejecutar
- Los acumuladores       ^^^^^^^^^^^^^^^^^
- LOS DATOS que necesitan esas FUNCIONES para trabajar! 
  En nuestro caso, qué datos especiales necesitan nuestras funciones? PALABRAS_PROHIBIDAS
  Que tenemos 5.... pero podrían ser 5000... y queremos que cada vez que se envíe un paquete de datos (a cada nodo le llegan 50 paquetes) se envíen las 5000 palabras prohibidas?
  QUE LOCURA !!!! Nos la vamos a pasar esperando envíos por red...  
Para resolver estas situaciones, en Spark existen los Broadcast.-

## Qué es un Broadcast:

Una variable que defino en el nodo que emite el trabajo... y que se envía a todos los nodos que van a ejecutar el trabajo (una única vez).
Conceptualmente es lo contrario a un Accumulator.
    EMISOR: ESCRIBE la variable
    WORKER: LEE la variable

## CALCULO DE TRENDING TOPICS:

Fiesta en la playa  #BeachParty#SunFun#CacaSun, bronceado extremo!
#CaféEnLaMañana con un libro... #Relax pero mi café está frío :(
Noche de estudio #StudyHard#NoSleep, ¿quién inventó los exámenes?
        vvvvv
beachparty
sunfun
caféenlamañana
relax
studyhard
nosleep
sunfun
sunfun
    vvvvv
beachparty              1
sunfun                  1 \
sunfun                  1 / 2 - 3
sunfun                  1     /
caféenlamañana          1
relax                   1
studyhard               1
nosleep                 1
    vvvvv
sunfun 3
beachparty 1
caféenlamañana 1
relax 1
studyhard 1
nosleep 1


---


sunfun 1 \ 2   \
sunfun 1 /      \
sunfun 1 \      / 4
sunfun 1 /  2  /

sunfun 1 \ 2 -- 3 --- 4
sunfun 1 /    /    /
sunfun 1 ----/    /
sunfun 1 --------/

---

# Spark SQL

Es una libnrería montada por encima de Spark Core que nos permite trabajar con datos 
ESTRUCTURADOS mediante una sintaxis muy parecida a SQL.... de hecho , incluso me permite ejecutar sentencias SQL.


TABLA DE HASTAGS: con una única columna: hashtag

    > SELECT hashtag, count(*) FROM hashtags GROUP BY hashtag ORDER BY count(*) DESC LIMIT 10

Para algunos problemas, una sintaxis de tipo SQL nos ayuda un HUEVO... y además, SQL es un lenguaje que conocemos todos los programadores.

    .mapToPair( hashtag -> new Tuple2<>(hashtag, 1) ) 
    .reduceByKey( (a, b) -> a + b ) 
    .mapToPair( Tuple2::swap )
    .sortByKey(false)
    .mapToPair( Tuple2::swap )

El plantear un algoritmo mediante MAP-REDUCE es complicado... y en algunos casos (la gran mayoría) es más sencillo hacerlo mediante SQL.
Para todos estos casos, me interesa usar Spark SQL, que al final lo que hace es traducir esa sintaxis SQL a un programa MapReduce, en automático.

Eso si... no todo puedo representarlo mediante SQL... y en esos casos, tendré que usar Spark Core.