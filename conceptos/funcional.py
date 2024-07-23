
def saluda():
    print("Hola")

saluda()

texto = "hola"
print(texto)

miFuncion = saluda  # Aquí solo referencio a la función saluda... desde mi variable miFuncion
miFuncion()         # Aquí llamo a la función saluda... desde mi variable miFuncion

def funcion_generadora_de_saludo_formal(nombre):
    return "Buenos días " + nombre

def funcion_generadora_de_saludo_informal(nombre):
    return "¿Qué pasa " + nombre + "?"

def imprimir_saludo(funcion_generadora_de_saludos, nombre):
    print(funcion_generadora_de_saludos(nombre))

imprimir_saludo(funcion_generadora_de_saludo_formal, "Juan")
imprimir_saludo(funcion_generadora_de_saludo_informal, "Juan")
