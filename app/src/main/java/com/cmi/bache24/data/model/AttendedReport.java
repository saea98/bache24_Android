package com.cmi.bache24.data.model;

import java.util.List;

/**
 * Created by omar on 2/14/16.
 */
public class AttendedReport /*implements B24DebugInterface*/ {

    private String token;
    private String ticket;
    private int etapa;
    private int noBaches;
    private String image1;
    private String image2;
    private String image3;
    private String descripcion;
    private List<SolvedBache> bachesList;
    private String causa;
    private String stretch;
    private String orientation;
    private String physicalPlace;

    // PARA NUEVOS ESTADOS 7 y 8

    private String bacheJustificacion;
    private String bacheCompromiso;
    private String bacheReasignacion;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public int getEtapa() {
        return etapa;
    }

    public void setEtapa(int etapa) {
        this.etapa = etapa;
    }

    public int getNoBaches() {
        return noBaches;
    }

    public void setNoBaches(int noBaches) {
        this.noBaches = noBaches;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<SolvedBache> getBachesList() {
        return bachesList;
    }

    public void setBachesList(List<SolvedBache> bachesList) {
        this.bachesList = bachesList;
    }

    public String getCausa() {
        return causa;
    }

    public void setCausa(String causa) {
        this.causa = causa;
    }

    public String getStretch() {
        return stretch;
    }

    public void setStretch(String stretch) {
        this.stretch = stretch;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getPhysicalPlace() {
        return physicalPlace;
    }

    public void setPhysicalPlace(String physicalPlace) {
        this.physicalPlace = physicalPlace;
    }

    public String arregloToJson() {

        if (bachesList == null) {
            return "[]";
        }

        String json = "[";

        for (int i = 0; i < bachesList.size(); i++) {
            String a = bachesList.get(i).toJson();
            if (!a.isEmpty())
                json += a + ",";
        }

        json = json.substring(0, json.length() - 1);

        json += "]";

        return json;
    }

    // B24DebugInterface implementation

    /*public B24Debug debugObject() {
        B24Debug debugObject = new B24Debug();

        debugObject.setReportState(this.etapa);
        debugObject.setReportID(this.ticket);
        debugObject.setReportSquadToken(this.token);
        debugObject.setHasImage1(this.image1 != null);
        debugObject.setHasImage2(this.image2 != null);
        debugObject.setHasImage3(this.image3 != null);
        debugObject.setReportBumpsArray(this.arregloToJson());
        debugObject.setReportBumpsCount(this.noBaches);
        debugObject.setReportComments(this.descripcion);
        debugObject.setStretch(this.stretch);
        debugObject.setOrientation(this.orientation);
        debugObject.setPhysicalPlace(this.physicalPlace);

        return debugObject;
    }*/

    public String getReportID() {
        return this.ticket;
    }

    public String getReportToken() {
        return this.token;
    }

    public String getBacheJustificacion() {
        return bacheJustificacion;
    }

    public void setBacheJustificacion(String bacheJustificacion) {
        this.bacheJustificacion = bacheJustificacion;
    }

    public String getBacheCompromiso() {
        return bacheCompromiso;
    }

    public void setBacheCompromiso(String bacheCompromiso) {
        this.bacheCompromiso = bacheCompromiso;
    }

    public String getBacheReasignacion() {
        return bacheReasignacion;
    }

    public void setBacheReasignacion(String bacheReasignacion) {
        this.bacheReasignacion = bacheReasignacion;
    }
}
