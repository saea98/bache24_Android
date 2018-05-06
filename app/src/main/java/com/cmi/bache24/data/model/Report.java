package com.cmi.bache24.data.model;

import java.util.Date;
import java.util.List;

/**
 * Created by omar on 12/1/15.
 */
public class Report /*implements B24DebugInterface*/ {

    private String reportId;
    private ReportStatus status;
    private String date;

    private String latitude;
    private String longitude;
    private String description;
    private int delegationId;
    private String colonia;
    private String calle;
    private String numero;
    private int avenidaId;
    private int etapaId;
    private String address;
    private String ticket;
    private String vialidad;

    private String picture1;
    private String picture2;
    private String picture3;
    private String picture4;

    private List<Picture> pictures;
    private List<PushRecord> pushHistory;
    private ReportOrigin origin;
    private boolean urgent;
    private String salesForceTicket;

    private Date creationDate;
    private int reportVersion;
    private String cause;
    private boolean hasPrimary;
    private boolean isPrimary;
    private int roadType;

    private boolean isTemp = false;

    public enum ReportStatus {
        NEW_REPORT,
        WAITING_FOR_RESPONSE,
        SOLVED
    }

    public enum ReportOrigin {
        APP,
        CMS,
        O72,
        SMS
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDelegationId() {
        return delegationId;
    }

    public void setDelegationId(int delegationId) {
        this.delegationId = delegationId;
    }

    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public int getAvenidaId() {
        return avenidaId;
    }

    public void setAvenidaId(int avenidaId) {
        this.avenidaId = avenidaId;
    }

    public int getEtapaId() {
        return etapaId;
    }

    public void setEtapaId(int etapaId) {
        this.etapaId = etapaId;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }

    public String getPicture1() {
        return picture1;
    }

    public void setPicture1(String picture1) {
        this.picture1 = picture1;
    }

    public String getPicture2() {
        return picture2;
    }

    public void setPicture2(String picture2) {
        this.picture2 = picture2;
    }

    public String getPicture3() {
        return picture3;
    }

    public void setPicture3(String picture3) {
        this.picture3 = picture3;
    }

    public String getPicture4() {
        return picture4;
    }

    public void setPicture4(String picture4) {
        this.picture4 = picture4;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public List<PushRecord> getPushHistory() {
        return pushHistory;
    }

    public void setPushHistory(List<PushRecord> pushHistory) {
        this.pushHistory = pushHistory;
    }

    public ReportOrigin getOrigin() {
        return origin;
    }

    public void setOrigin(ReportOrigin origin) {
        this.origin = origin;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }

    public String getSalesForceTicket() {
        return salesForceTicket;
    }

    public void setSalesForceTicket(String salesForceTicket) {
        this.salesForceTicket = salesForceTicket;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getVialidad() {
        return vialidad;
    }

    public void setVialidad(String vialidad) {
        this.vialidad = vialidad;
    }

    public int getReportVersion() {
        return reportVersion;
    }

    public void setReportVersion(int reportVersion) {
        this.reportVersion = reportVersion;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public boolean hasPrimary() {
        return hasPrimary;
    }

    public void setHasPrimary(boolean hasPrimary) {
        this.hasPrimary = hasPrimary;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    // B24DebugInterface implementation

    /*public B24Debug debugObject() {
        B24Debug debugObject = new B24Debug();

        debugObject.setReportState(this.etapaId); //6);
        debugObject.setReportID(this.ticket);
        debugObject.setHasImage1(this.picture1 != null);
        debugObject.setHasImage2(this.picture2 != null);
        debugObject.setHasImage3(this.picture3 != null);
        debugObject.setReportComments(this.description);

        return debugObject;
    }*/

    public String getReportID() {
        return this.ticket;
    }

    public String getReportToken() {
        return null;
    }

    public int getRoadType() {
        return roadType;
    }

    public void setRoadType(int roadType) {
        this.roadType = roadType;
    }

    public boolean isTemp() {
        return isTemp;
    }

    public void setTemp(boolean temp) {
        isTemp = temp;
    }
}
