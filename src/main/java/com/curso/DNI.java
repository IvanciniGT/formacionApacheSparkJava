package com.curso;

import lombok.Getter;

@Getter
public class DNI {

    private final String dniOriginal;
    private String letra;
    private int numero;
    private boolean valido;

    private static final String PATRON_DNI = "^(([0-9]{1,8})|([0-9]{1,2}[.][0-9]{3}[.][0-9]{3})|([0-9]{1,3}[.][0-9]{3}))[ -]?[A-Za-z]$";
    private static final String DIGITOS_CONTROL = "TRWAGMYFPDXBNJZSQVHLCKE";

    public DNI(String dniOriginal){
        this.dniOriginal = dniOriginal;
        this.validarDNI();
    }

    private void validarDNI(){
        if(dniOriginal.matches(PATRON_DNI)){
            String dniLimpio = dniOriginal.replaceAll("[. -]", "");
            this.numero = Integer.parseInt(dniLimpio.substring(0, dniLimpio.length()-1));
            this.letra = dniLimpio.substring(dniLimpio.length()-1).toUpperCase();
            this.valido = letra.equals(calcularDigitoControl(numero));
        }else{
            this.valido = false;
        }
    }

    public static String calcularDigitoControl(int numeroDNI){
        int resto = numeroDNI % 23;
        return DIGITOS_CONTROL.substring(resto, resto+1);
    }

}
