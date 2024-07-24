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