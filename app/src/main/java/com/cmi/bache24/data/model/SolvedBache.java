package com.cmi.bache24.data.model;

import java.util.List;

/**
 * Created by omar on 2/14/16.
 */
public class SolvedBache {

    private String comentario_supervision;
    private String comentario_atencion;
    private String material;
    private String imagen1;
    private String imagen2;
    private String imagen3;
    private String imagen4;
    private String imagen5;
    private String imagen6;
    private List<Mts> metros;

    public String getComentario_supervision() {
        return comentario_supervision;
    }

    public void setComentario_supervision(String comentario_supervision) {
        this.comentario_supervision = comentario_supervision;
    }

    public String getComentario_atencion() {
        return comentario_atencion;
    }

    public void setComentario_atencion(String comentario_atencion) {
        this.comentario_atencion = comentario_atencion;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getImagen1() {
        return imagen1;
    }

    public void setImagen1(String imagen1) {
        this.imagen1 = imagen1;
    }

    public String getImagen2() {
        return imagen2;
    }

    public void setImagen2(String imagen2) {
        this.imagen2 = imagen2;
    }

    public String getImagen3() {
        return imagen3;
    }

    public void setImagen3(String imagen3) {
        this.imagen3 = imagen3;
    }

    public String getImagen4() {
        return imagen4;
    }

    public void setImagen4(String imagen4) {
        this.imagen4 = imagen4;
    }

    public String getImagen5() {
        return imagen5;
    }

    public void setImagen5(String imagen5) {
        this.imagen5 = imagen5;
    }

    public String getImagen6() {
        return imagen6;
    }

    public void setImagen6(String imagen6) {
        this.imagen6 = imagen6;
    }

    public List<Mts> getMetros() {
        return metros;
    }

    public void setMetros(List<Mts> metros) {
        this.metros = metros;
    }

    public String toJson() {

        if (metros == null)
            return "";

        String json = "";

        String metrosJson = "";

        for (int i = 0; i < metros.size(); i++) {
//            metrosJson += "\"baches_metros" + (i + 1) + "\": \"" + metros.get(i).getValue() + "\",";
            metrosJson += "\"" + metros.get(i).getName()  + "\": \"" + metros.get(i).getValue() + "\",";
        }

        metrosJson = metrosJson.substring(0, metrosJson.length() - 1);

        json = "{\n" +
                metrosJson + "," +
                "\n" +
                "  \"baches_comentario_supervision\": \"" + this.comentario_supervision + "\",\n" +
                "  \"baches_comentario_atencion\": \"" + this.comentario_atencion + "\",\n" +
                "  \"material\":\"" + this.material + "\",\n" +
                "  \"bache_imagen1\": \"" + this.imagen1 + "\",\n" +
                "  \"bache_imagen2\": \"" + this.imagen2 + "\",\n" +
                "  \"bache_imagen3\": \"" + this.imagen3 + "\",\n" +
                "  \"bache_imagen4\": \"" + this.imagen4 + "\",\n" +
                "  \"bache_imagen5\": \"" + this.imagen5 + "\",\n" +
                "  \"bache_imagen6\": \"" + this.imagen6 + "\"\n" +
                "}";

        return json;
    }
}
