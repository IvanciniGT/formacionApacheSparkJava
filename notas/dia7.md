Los 4 lenguajes desde los que puedo llamar a Spark: 
- Py
- Scala
- Java
- R

En Databricks (el servicio de pago ofrecido en clouds por la gente que crea Spark) tenemos el concepto de Tabla Delta,
que es una extensión del concepto de Dataset de Spark.
Y esas tablas delta si admiten algo asi como una preordenación / índice.
En HDFS, cuando guardamos un archivo, los archivos se parten en trozos... y esos trozos de guardan en diferentes máquinas... incluso repetidos.
100Mb -> 10 trozos de 10Mb y cada trozo guardarlo en 3 máquinas diferentes.
Las tablas delta lo que hacen particionar la tabla de datos de forma que en cada partición encontremos 
datos similares entre si en base a una columna de preordenación.



----
# Particionados

Hay 2 particionados: 
- Los de los ficheros
- Los de los datos al aplicar los map-reduce

Imagina que tienes un fichero de 100Mb y lo particionas en 10 trozos de 10Mb.
En el cluster luego tengo 20 cores para procesar información.
Si he hecho ese tamaño de particionado... tengo la mitad de las máquinas que no pueden operar sobre los datos... hasta que no hayan sido leídos

Con respecto a los ficheros:
- Los ficheros al final se guardan en un Volumen de almacenamiento.  
  Cómo es la infra FISICA de ese volumen de almacenamiento?
   HDFS: Donde se usan 50 HDD repartidos en 15 servidores 
   NFS: Donde tengo 5 HDD en RAID 5 en un servidor
        Puedo pedir 10 particiones... pero hay 5 agujas de HDD a escribir. Si escriben un fichero, no están escribiendo otro.

A parte: 
    - Tipo de soporte de almacenamiento:
        HDD
        SSD
        NVMe

    Con HDD quien me limita la velocidad? El disco duro
    Si tengo NVME, quien me limita la velocidad? La red
        Si... tengo 17 NVME... pero tengo una red de 10Gb... no puedo escribir más rápido de 1Gb


---

# Cual es la política que se sigue en la REALIDAD

UNA MIERDA GORDA !!!

De hecho esa política la aplicamos desde hace 30 años cada vez que usamos JAVA o PYTHON... o la que usa en cualquier CLOUD!
```java
String palabra = "hola";
palabra = "adios"; // Llegados a este punto, cuantos String hay en RAM? 2 Strings.. Uno de ellos se marcará como garbage y se eliminaré cuando sea que entre o no el GC.
```

El mismo programa hecho en C o C++ no tiene ese problema. En esos lenguajes una variable SI ES UNA CAJITA DONDE METO COSAS y reemplazo una por otra.
Eso significa que el mismo programa hecho en JAVA o PYTHON o JS necesita el doble de RAM que si lo hago en C o C++

JAVA, Python, JS hacen un muy muy muy mal uso de la RAM (muy poco eficiente).
Entonces, por qué son los lenguajes más usados del mundo?
Porque son sencillos. Es mucho más fácil programar en Java que en C o C++.
Al final hecho cuentas y digo:
    App hecha en JAVA: 250 h / 50 €/hora = 12.500 €
    App hecha en C++:  300 h / 60 €/hora = 18.000 €
                                        - ----------
                                            5.500 € de diferencia
    Y me quedo con JAVA
Y cuanto me cuestan 2 pastilla de RAM pal servidor: 500€
JAVA