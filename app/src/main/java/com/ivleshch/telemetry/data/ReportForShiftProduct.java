package com.ivleshch.telemetry.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Ivleshch on 31.01.2018.
 */

public class ReportForShiftProduct extends RealmObject{

    @PrimaryKey
    private String uid;
    private String uidDocument;
    private Nomenclature nomenclature;
    private Integer quantityPlan;
    private Integer quantityFact;
    private Integer quantityDefect;
    private Integer quantityWaste;
    private String unitQuantity;
    private String unitWeight;
    private Integer standardSpeed;
    private Integer availability;
    private Integer performance;
    private Integer quality;
    private Integer oee;

    public Integer getAvailability() {
        return availability;
    }

    public void setAvailability(Integer availability) {
        this.availability = availability;
    }

    public Integer getPerformance() {
        return performance;
    }

    public void setPerformance(Integer performance) {
        this.performance = performance;
    }

    public Integer getQuality() {
        return quality;
    }

    public void setQuality(Integer quality) {
        this.quality = quality;
    }

    public Integer getOee() {
        return oee;
    }

    public void setOee(Integer oee) {
        this.oee = oee;
    }

    public String getUidDocument() {
        return uidDocument;
    }

    public void setUidDocument(String uidDocument) {
        this.uidDocument = uidDocument;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Nomenclature getNomenclature() {
        return nomenclature;
    }

    public void setNomenclature(Nomenclature nomenclature) {
        this.nomenclature = nomenclature;
    }

    public Integer getQuantityPlan() {
        return quantityPlan;
    }

    public void setQuantityPlan(Integer quantityPlan) {
        this.quantityPlan = quantityPlan;
    }

    public Integer getQuantityFact() {
        return quantityFact;
    }

    public void setQuantityFact(Integer quantityFact) {
        this.quantityFact = quantityFact;
    }

    public Integer getQuantityDefect() {
        return quantityDefect;
    }

    public void setQuantityDefect(Integer quantityDefect) {
        this.quantityDefect = quantityDefect;
    }

    public Integer getQuantityWaste() {
        return quantityWaste;
    }

    public void setQuantityWaste(Integer quantityWaste) {
        this.quantityWaste = quantityWaste;
    }

    public String getUnitQuantity() {
        return unitQuantity;
    }

    public void setUnitQuantity(String unitQuantity) {
        this.unitQuantity = unitQuantity;
    }

    public String getUnitWeight() {
        return unitWeight;
    }

    public void setUnitWeight(String unitWeight) {
        this.unitWeight = unitWeight;
    }

    public Integer getStandardSpeed() {
        return standardSpeed;
    }

    public void setStandardSpeed(Integer standardSpeed) {
        this.standardSpeed = standardSpeed;
    }

}
