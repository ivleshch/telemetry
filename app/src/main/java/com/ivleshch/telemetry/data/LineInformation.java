package com.ivleshch.telemetry.data;

/**
 * Created by Ivleshch on 30.01.2018.
 */

public class LineInformation {

    private String uid;
    private WorkCenter workCenter;
    private Individual master;
    private Nomenclature nomenclature;
    private Integer quantityPlan;
    private Integer quantityFact;
    private Integer quantityDefect;
    private Integer quantityWaste;
    private String unitQuantity;
    private String unitWeight;
    private Integer standardSpeed;
    private Integer availabilityPercent;
    private Integer performancePercent;
    private Integer qualityPercent;
    private Integer oeePercent;
    private Integer quantityStops;
    private String durationStops;
    private String reasonDescription;

    public Integer getAvailabilityPercent() {
        return availabilityPercent;
    }

    public void setAvailabilityPercent(Integer availabilityPercent) {
        this.availabilityPercent = availabilityPercent;
    }

    public Integer getPerformancePercent() {
        return performancePercent;
    }

    public void setPerformancePercent(Integer performancePercent) {
        this.performancePercent = performancePercent;
    }

    public Integer getQualityPercent() {
        return qualityPercent;
    }

    public void setQualityPercent(Integer qualityPercent) {
        this.qualityPercent = qualityPercent;
    }

    public Integer getOeePercent() {
        return oeePercent;
    }

    public void setOeePercent(Integer oeePercent) {
        this.oeePercent = oeePercent;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public WorkCenter getWorkCenter() {
        return workCenter;
    }

    public void setWorkCenter(WorkCenter workCenter) {
        this.workCenter = workCenter;
    }

    public Individual getMaster() {
        return master;
    }

    public void setMaster(Individual master) {
        this.master = master;
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

    public Integer getQuantityStops() {
        return quantityStops;
    }

    public void setQuantityStops(Integer quantityStops) {
        this.quantityStops = quantityStops;
    }

    public String getDurationStops() {
        return durationStops;
    }

    public void setDurationStops(String durationStops) {
        this.durationStops = durationStops;
    }

    public String getReasonDescription() {
        return reasonDescription;
    }

    public void setReasonDescription(String reasonDescription) {
        this.reasonDescription = reasonDescription;
    }

    public LineInformation(String uid, WorkCenter workCenter, Individual master, Nomenclature nomenclature, Integer quantityPlan, Integer quantityFact, Integer quantityDefect, Integer quantityWaste, String unitQuantity, String unitWeight, Integer standardSpeed, Integer availabilityPercent, Integer performancePercent, Integer qualityPercent, Integer oeePercent, Integer quantityStops, String durationStops, String reasonDescription) {

        this.uid = uid;
        this.workCenter = workCenter;
        this.master = master;
        this.nomenclature = nomenclature;
        this.quantityPlan = quantityPlan;
        this.quantityFact = quantityFact;
        this.quantityDefect = quantityDefect;
        this.quantityWaste = quantityWaste;
        this.unitQuantity = unitQuantity;
        this.unitWeight = unitWeight;
        this.standardSpeed = standardSpeed;
        this.availabilityPercent = availabilityPercent;
        this.performancePercent = performancePercent;
        this.qualityPercent = qualityPercent;
        this.oeePercent = oeePercent;
        this.quantityStops = quantityStops;
        this.durationStops = durationStops;
        this.reasonDescription = reasonDescription;
    }


}
