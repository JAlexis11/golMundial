package com.utn.golmundial.publicfrontend.model;

import java.util.HashMap;
import java.util.Map;

public class CountryUtil {

    private static final Map<String, String> FLAGS = new HashMap<>();
    private static final Map<String, String> NAMES = new HashMap<>();

    static {

        // ===== Grupo A =====
        add("KOR","🇰🇷","Corea del Sur");
        add("RSA","🇿🇦","Sudáfrica");
        add("MEX","🇲🇽","México");
        add("CZE","🇨🇿","Chequia");

        // ===== Grupo B =====
        add("ESP","🇪🇸","España");
        add("TUR","🇹🇷","Turquía");
        add("EGY","🇪🇬","Egipto");
        add("UAE","🇦🇪","Emiratos Árabes Unidos");

        // ===== Grupo C =====
        add("BRA","🇧🇷","Brasil");
        add("NOR","🇳🇴","Noruega");
        add("NGA","🇳🇬","Nigeria");
        add("HON","🇭🇳","Honduras");

        // ===== Grupo D =====
        add("FRA","🇫🇷","Francia");
        add("COL","🇨🇴","Colombia");
        add("CAN","🇨🇦","Canadá");
        add("IRQ","🇮🇶","Irak");

        // ===== Grupo E =====
        add("GER","🇩🇪","Alemania");
        add("ECU","🇪🇨","Ecuador");
        add("CIV","🇨🇮","Costa de Marfil");
        add("CUW","🇨🇼","Curazao");

        // ===== Grupo F =====
        add("POR","🇵🇹","Portugal");
        add("URU","🇺🇾","Uruguay");
        add("CMR","🇨🇲","Camerún");
        add("UZB","🇺🇿","Uzbekistán");

        // ===== Grupo G =====
        add("ARG","🇦🇷","Argentina");
        add("MAR","🇲🇦","Marruecos");
        add("SWE","🇸🇪","Suecia");
        add("CRC","🇨🇷","Costa Rica");

        // ===== Grupo H =====
        add("ENG","🏴","Inglaterra");
        add("SUI","🇨🇭","Suiza");
        add("IRN","🇮🇷","Irán");
        add("VEN","🇻🇪","Venezuela");

        // ===== Grupo I =====
        add("ITA","🇮🇹","Italia");
        add("USA","🇺🇸","Estados Unidos");
        add("PAR","🇵🇾","Paraguay");
        add("NZL","🇳🇿","Nueva Zelanda");

        // ===== Grupo J =====
        add("NED","🇳🇱","Países Bajos");
        add("JPN","🇯🇵","Japón");
        add("SEN","🇸🇳","Senegal");
        add("IRL","🇮🇪","Irlanda");

        // ===== Grupo K =====
        add("BEL","🇧🇪","Bélgica");
        add("CRO","🇭🇷","Croacia");
        add("AUS","🇦🇺","Australia");
        add("PAN","🇵🇦","Panamá");

        // ===== Grupo L =====
        add("DEN","🇩🇰","Dinamarca");
        add("POL","🇵🇱","Polonia");
        add("TUN","🇹🇳","Túnez");
        add("KSA","🇸🇦","Arabia Saudita");

    }

    private static void add(String code,String flag,String name){
        FLAGS.put(code,flag);
        NAMES.put(code,name);
    }

    public static String getFlag(String code){

        if(code==null)
            return "⚽";

        return FLAGS.getOrDefault(code.toUpperCase(),"⚽");

    }

    public static String getName(String code){

        if(code==null)
            return "";

        return NAMES.getOrDefault(code.toUpperCase(),code);

    }

}