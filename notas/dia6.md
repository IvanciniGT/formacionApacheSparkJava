
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