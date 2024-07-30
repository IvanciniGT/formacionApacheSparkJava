package com.curso.spark;

import com.curso.DNI;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;

import static org.apache.spark.sql.functions.*;

public class IntroSQL {

    public static void main(String[] args){
        // Abrir una conexión al cluster de Spark
        SparkSession conexion = SparkSession.builder()
                .appName("IntroSQL")
                .master("local[2]")
                .getOrCreate();

        // Generar un dataset
        // Cuando trabajábamos con SparkCore, el objeto JavaSparkContext era el que nos permitía crear RDDs
        // Aplicar operaciones sobre el dataset
        // Con SparkSQL, el objeto SparkSession es el que nos permite crear Dataset<Row>
        Dataset<Row> datos = conexion.read().json("src/main/resources/personas.json");
        // Lo primero, vamos a revisar que to do se ha cargado bien:
        datos.show(); // Solo lo hago en desarrollo
        // Imprimir su esquema asociado
        datos.printSchema();
        // Si me interesase operar con el Schema por defecto que se genera, podría hacerlo con el método schema()

        // Empezar a jugar con nuestro dataset
        Dataset<Row> soloNombreYApellidos = datos.select("nombre","apellidos");
        soloNombreYApellidos.show();
        Dataset<Row> soloNombreYApellidos2 = datos.select(col("nombre"),col("edad").plus(10));
        soloNombreYApellidos2.show();

        datos.filter(col("edad").gt(30)).show();
        datos.groupBy("nombre").count().show();
        datos.groupBy("nombre")
                .sum("edad")
                .orderBy(col("sum(edad)").desc())
                .show();

        // Nos permite referirnos durante la sesión actual a un dataset con un nombre
        datos.createOrReplaceTempView("personas");
        // Para qué? Para poder ejecutar queries con sintaxis SQL sobre el dataset
        conexion.sql("SELECT nombre, sum(edad) FROM personas GROUP BY nombre ORDER BY sum(edad) DESC").show();
        // SparkSQL es una capa por encima de SparkCore.
        // Transforma las operaciones SQL en operaciones MAP REDUCE de SparkCore
        // Evitándome tener que escribir código MapReduce, que es complejo, al menos al compararlo con SQL

        // Y ahora, llega la hora de la verda!
        // Vamos a filtrar quedándonos con los que tienen DNI VALIDO!
        // Qué tal va eso en SQL????
        //  2 opciones: LA mala... y la BUENA en este caso...
        // Os cuento la mala, no porque la fuésemos a usar nunca para algo como esto... simplemente para enseñaros algo que podemos hacer.
        // OPCION MALA en este caso, pero NECESARIA en otros:
        // CONVERTIR EL DATASET a un RDD, al que poder aplicar programación MAP REDUCE
        JavaRDD<Row> datosComoRDD = datos.toJavaRDD();
        JavaRDD<Row> personasConDNIValido = datosComoRDD.filter(
                ( Row fila ) -> {
                   int indiceDeLaColumnaDNI = fila.fieldIndex("dni");
                   String dni = fila.getString(indiceDeLaColumnaDNI);
                   return new DNI(dni).isValido();
                }
        );
        // Convertir de vuelta el JavaRDD a un Dataset<Row>
        Dataset<Row> personasConDNIValidoComoDataset = conexion.createDataFrame(personasConDNIValido, datos.schema());
        personasConDNIValidoComoDataset.show();

        // En muchas ocasiones nos interesa hacer cosas como esa. O incluso es necesario hacerlo.
        // En este caso concreto no sería necesario... Habría una opción más sencilla: Registrar una función nueva en SQL
                // User Defined Functions
        conexion.udf().register("esValido",
                                (String dni) -> new DNI(dni).isValido(),
                                DataTypes.BooleanType);
        // Y ahora, puedo usar esa función en mis queries SQL
        conexion.sql("SELECT * FROM personas WHERE esValido(dni)").show();
        // Los UDF son muyt potentes... y una forma sencilla de añadir operaciones MAS COMPLEJAS AL SQL.
        // No en todos los casos me sirven, ni me resuelven la papeleta, pero en muchos casos sí.
        // Y en los que no, siempre me quedará la opción de convertir a RDD y trabajar con MAP REDUCE.

        // A por los Códigos Postales
        Dataset<Row> cps = conexion.read()
                .option("header", "true")
                .option("delimiter", ",")
                .csv("src/main/resources/cps.csv");

        cps.show();

        // Vamos a enriquecer la información de las personas con la información asociada al cp
        // Para ello, vamos a hacer un JOIN entre los dos datasets
        Dataset<Row> resultado = datos.join(broadcast(cps),"cp"); // Solo porque las 2 tablas tienen una columna con el mismo nombre
        // Si no fuese así, tendríamos que hacer un join con la sintaxis completa:


        datos = datos.repartitionByRange(10, col("cp"));
        cps = cps.repartitionByRange(5, col("cp"));

        datos.join(cps, datos.col("cp").equalTo(cps.col("cp"))).show();
        // Otra opcion sería usar SQL puro
        cps.createOrReplaceTempView("cps");
        conexion.sql("SELECT * FROM personas JOIN cps ON personas.cp = cps.cp").show();

        // Guardar el resultado en un fichero / BBDD

        resultado.write()
                .mode("overwrite")
                .json("personas_con_cp.json");
        resultado.write()
                .option("header", "true")
                .mode("overwrite")
                .csv("personas_con_cp.csv");


        // Cerrar la conexión al cluster de Spark
        conexion.close();
    }

}
