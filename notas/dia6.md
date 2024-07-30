
# Montar una función para validar un DNI.

Me llega un DNI como texto, y he de decir si es bueno o no.
Aprovechando ese trabajo, extraer el número del DNI y el carácter de control.

  23.000.023-T
  
    23000023 | 23
             +-----------
           0    1.000.001 (pasamos de este dato)
           ^
        RESTO Esto es lo que interesa: [0-22]
              El ministerio de Interior ofrece una tabla con los dígitos de control asociados a cada RESTO
                El dígito asociado al resto 0 es T.

Entradas válidas:
  - 23.000.023-T
  - 23.000.023 t
  - 23.000.023t
  - 23000023t
  - 23000023T
  - 2300023-T
  - 02300023-T
  - 02.300.023-T

Entradas no válidas:
    - 23.000.023
    - 23.00.0023T
    - 23.000.023$t
    - federico


    class DNI{
        boolean valido;
        String numero;
        String letra;
    }

    public DNI validarDNI(String dni){
        // Ver si el texto suministrado cumple con un patrón de DNI
        // Caso que no: ACABADO !!!
        // Caso que sí: Extraer el número y la letra
        // Replace ". -" -> ""
            // Todo hasta el último no incluido: NUMERO
            // El último la LETRA
    }


# EXPRESIONES REGULARES

Usamos la sintaxis PERL, que es una sintaxis para trabajar con expresiones regulares que se
implementó en el lenguaje PERL y que hoy en día han heredado todos los lenguajes.

Se basa en el concepto de PATRON.

Qué es un PATRON: Una secuencia de subpatrones.

Que es un SUBPATRON: Una secuencia de caracteres seguidos de un modificador de cantidad

    Secuencia de caracteres                 Cómo se interpreta
        'hola'                                Debe aparecer literalmente 'hola'
        '[hola]'                              Debe aparecer una 'h', una 'o', una 'l' o una 'a' (una de ellas)
        '[a-z]'                               Debe aparecer una letra minúscula (según ASCII entre la a y la z)
        '[A-Z]'                               Debe aparecer una letra mayúscula (según ASCII entre la A y la Z)
        '[0-9]'                               Debe aparecer un dígito (según ASCII entre el 0 y el 9)   
        '[a-zA-Z0-9áíñÑ]'                     Debe aparecer una letra o un dígito
        .                                     Debe aparecer cualquier carácter excepto el salto de línea

    Modificador de cantidad
        No poner nada                         Debe aparecer 1 vez   
        ?                                     Debe aparecer 0 o 1 vez
        *                                     Debe aparecer 0 o más veces
        +                                     Debe aparecer 1 o más veces
        {n}                                   Debe aparecer n veces
        {n,}                                  Debe aparecer n o más veces
        {n,m}                                 Debe aparecer entre n y m veces

    Otros
        ^                                     Comienza por
        $                                     Termina por
        ()                                    Agrupar subpatrones
        |                                     OR

DNI:
        ^
        [0-9]{1,8}                                          23000000
        [0-9]{1,2}[.][0-9]{3}[.][0-9]{3}                    2.300.000
        [0-9]{1,3}[.][0-9]{3}                               23.000
        [ -]?[A-Za-z]$

^(([0-9]{1,8})|([0-9]{1,2}[.][0-9]{3}[.][0-9]{3})|([0-9]{1,3}[.][0-9]{3}))[ -]?[A-Za-z]$

La página regex101.com es una página que nos permite probar expresiones regulares.

---

# Librería SPARK SQL

Al usar esta librería, todo cambia!
Incluso la forma de conectarnos al cluster CAMBIA!
- Con spark core, usábamos un SparkConf y un JavaSparkContext.
- Con spark sql, usamos un SparkSession, que se configura mediante un patrón BUILDER

El otro gran cambio al trabajar con esta librería es que ya no manejamos el objeto JavaRDD... ni programación MapReduce.
En su lugar trabajaremos con objetos de tipo Dataset<Row>

Un Dataset<Row> es un objeto que representa una tabla de datos (como si fuera una tabla de una base de datos)
Cada "Row" es una fila de la tabla.... que dentro tendrá columnas.

Al igual que en una BBDD Relacional, los Dataset<Row> tiene un ESQUEMA ASOCIADO, que define las columnas de la tabla. Para cada columna:
- Nombre
- Tipo de dato
- Si puede ser nulo o no

El objeto Dataset<Row> No tiene ya las funciones MapReduce a las que nos hemos acostumbrado. En su lugar posee funciones equivalentes a las que encontramos en SQL:
- select
- filter
- groupBy
- orderBy
- ...

A cualquiera de esas funciones, le podemos pasar las columnas sobre las que opera.... Con 2 sintaxis diferentes:
- Como texto (String) el nombre de la columna
    > miDataset.select("columna1", "columna2");
- Como objeto Column... usando una función que ofrece la propia libreria SQL:
    > miDataset.select(col("columna1"), col("columna2")); 

Claro... la primera es más simple... para qué la segunda?
La segunda opción nos devuelve un objeto de tipo Column... que tiene sus propias propiedades y métodos... 
que nos permiten hacer cosas más complejas que con la primera opción.

// Recuperar una columna en mayúsculas
miDataset.select(upper(col("columna1")));
// Sumar 5 a una columna
miDataset.select(col("columna1").plus(5));
// Ordenar descendentemente por una columna
miDataset.orderBy(col("columna1").desc());

O uso una sintaxis o la otra... pero no puedo mezclarlas.... es un ROLLO
// Recuperar 5 columnas... sumando 2 de ellas
miDataset.select(col("columna1"), col("columna2"), col("columna1").plus(col("columna2")), col("columna3"), col("columna4"));

```json
[
{
    "nombre": "Federico",
},
  {}
]
```


JSON: JavaScript Object Notation

let miVariable =  ???? <- Es JSON

---

# OJO A LOS JOINS

Spark, aunque por momentos nos puede parecer que es una BBDD, no lo es en absoluto!
Spark es una libraría de procesamiento Map-Reduce, que permite TRANSFORMAR DATOS.

Gracias a la libraría SparkSQL, podemos usar una sintaxis muy parecida a SQL para trabajar con los datos.
PERO SPARK NO ES UNA BBDD EN NINGUN CASO!

Cuando una BBDD hace un join, para que ese join sea eficiente, necesitamos que los datos estén ordenados por la columna que se va a usar en el join.
Por eso en las BBDD creamos INDICES en las columnas que se van a usar en los joins.

Un índice es una copia PRE-ORDENADA de los datos.

La cuestión es que en SPARK no existe el concepto de índice... y por tanto no podemos hacer esos joins tan eficientes,
al menos usando la misma estrategia de joins que usa una BBDD relacional.

En ocasiones, me interesa mucho más cargar los datos en una BBDD antes de enriquecerlos y posteriormente ALLI hacer el JOIN!
Va a tardar INFINITAMENTE MENOS!
Y entran las variantes de ETL:
- ETLT:
  - Extract de una BBDD o fichero
  - Transformación previa de los datos
  - Load en una BBDD
  - Los enriquezco con un JOIN

Los joins son un tema COMPLEJO DE NARICES !!!!!


Imaginad que tengo una tabla A con 1M de registros y una tabla B con 1M de registros.
Y quiero hacer un JOIN entre ambas tablas.... y quiero aprovechar la potencia de cálculo de mi cluster de Spark.
Eso implica repartir los datos entre los trabajadores del cluster... y que cada trabajador haga el JOIN de su parte de los datos.

Puedo particionar la tabla 1 del join, para mandar un trozo a cada nodo del cluster? SI
Puedo particionar la tabla 2 del join, para mandar un trozo a cada nodo del cluster? NO: UPS !!!!!!
    Por qué?
        Depende del tipo de JOIN (a nivel conceptual), podré partirla o no... en muchos casos NO.

Tengo la tabla clientes: 1M de registros
Tengo la tabla CPS: 50k de registros
Puedo partir la tabla clientes en 100 trozos... y mandar un trozo a cada nodo del cluster? De tamaño 10k
Pero tengo garantías de qué CPs están en cada trozo? NO o SI
Como podría tener esa garantía? ORDENANDO POR CODIGO POSTAL y haciendo el particionado en base a esta columna...
pero... que tal se me va a dar ORDENAR 1M de registros? MAL
Para una BBDD no hay problema... porque los datos lo va a tener PRE-ORDENADOS (mediante el uso del un índice)... pero eso en Spark no existe.
La cuestión es más bien:
- ME INTERESA GARANTIZAR ESO? Puede ser que si... puede ser que no.

Mi tabla CPS: 50k datos... Ocupa mucho en RAM? NO... ocupa bastante poco.
Si tengo la tabla en RAM, qué tipo de operación puedo usar (estrategia) para hacer el JOIN? Un lookup.
Voy procesando cada fila de la tabla de clientes... saco su cp... y entro a la tabla de cps con ese cpo para recuperar sus datos.
Es como si en java entrase por clave en un HashMap... que tal va eso de rápido? COMO UN TIRO !
Eso lo puedo hacer debido a que la tabla CPS es pequeña y cabe en RAM.

En un caso como ese me interesaría que la tabla CPs se mande completa a cada nodo del cluster...
y que la query se resuelva (el join) mediante LOOKUP.... Y eso es lo que hace SPARK en esos casos.... 
siempre y cuando SPARK considere que la tabla es pequeña y cabe en RAM.
AUN ASI... esa tabla CPs se va a mandar a cada nodo trabajador cada vez que se envíe una partición de la tabla clientes a un nodo...
Y eso será eficiente? NO... y lo podemos mejorar? BROADCAST 

Al hacer un broadcast de una tabla, lo que hacemos es mandar la tabla a cada nodo del cluster... y que se quede en RAM.... y a su vez es una indicación
a Spark de que esa tabla se va a usar en un JOIN mediante LOOKUP.

PERO OJO!
Eso solo será eficiente si la tabla 2 es pequeña y cabe en RAM.

Si la tabla no cabe en RAM tengo un problema... se empezará a hacer swapping... y eso es un problema GRAVE DE RENDIMIENTO.
Si la tabla no es pequeña, aunque entre en RAM, tengo un problema... la he de mandar a COMPLETA a todos los nodos... y eso es un problema de RED ( y cae el rendimiento)

Si la tabla es grande, me interesa particionar por el campo de unión... y hacer el JOIN mediante estrategia de MERGE:

|id | nombre | apellido | cp |                    | cp | municipio |
|---|--------|----------|----|                    |----|-----------|
| 1 | Federico | Pérez | 28001 |                  | 28001 | Madrid | 
| 2 | Juan | Pérez | 28001 |                      | 28002 | Barcelona | 
| 3 | María | Pérez | 28002 |                     | 28003 | Valencia | 
| 4 | Federico | Pérez | 28002 |                 
| 5 | Juan | Pérez | 28003 |                     
| 6 | María | Pérez | 28003 |                   

Me puede interesar, si voy a usar siempre la tabla de CPs para hacer este tipo de joins, tenerla pre-particionada por el campo de unión.
Y con las mismas, cuando sea que exporte la tabla personas de una BBDD me puede interesar exportarla preordenada por el campo de unión.


ivan.osuna.ayuste@gmail.com