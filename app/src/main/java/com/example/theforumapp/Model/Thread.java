package com.example.theforumapp.Model;

public class Thread {

    private String issueTitle;
    private String deviceModel;
    private String binary;
    private String issueType;
    private String occurrence;
    private String description;
    private String customerId;
    private String engineerId;
    private String osVersion;
    private String downloadUri1;
    private String downloadUri2;
    private String downloadUri3;
    private String id;
    private String salesCode;
    private String serialNumber;


    public Thread() {
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setDownloadUri1(String downloadUri1) {
        this.downloadUri1 = downloadUri1;
    }

    public void setDownloadUri2(String downloadUri2) {
        this.downloadUri2 = downloadUri2;
    }

    public void setDownloadUri3(String downloadUri3) {
        this.downloadUri3 = downloadUri3;
    }

    public String getDownloadUri1() {
        return downloadUri1;
    }

    public String getDownloadUri2() {
        return downloadUri2;
    }

    public String getDownloadUri3() {
        return downloadUri3;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setSalesCode(String salesCode) {
        this.salesCode = salesCode;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSalesCode() {
        return salesCode;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public Thread(String id, String issueTitle, String deviceModel, String binary, String issueType, String occurrence, String description, String customerId, String engineerId, String osVersion, String salesCode, String serialNumber, String downloadUri1, String downloadUri2, String downloadUri3) {
        this.id = id;
        this.issueTitle = issueTitle;
        this.deviceModel = deviceModel;
        this.binary = binary;
        this.issueType = issueType;
        this.occurrence = occurrence;
        this.description = description;
        this.customerId = customerId;
        this.engineerId = engineerId;
        this.osVersion = osVersion;
        this.salesCode = salesCode;
        this.serialNumber = serialNumber;
        this.downloadUri1 = downloadUri1;
        this.downloadUri2 = downloadUri2;
        this.downloadUri3 = downloadUri3;

    }

    public String getIssueTitle() {
        return issueTitle;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public String getBinary() {
        return binary;
    }

    public String getIssueType() {
        return issueType;
    }

    public String getOccurrence() {
        return occurrence;
    }

    public String getDescription() {
        return description;
    }

    public void setIssueTitle(String issueTitle) {
        this.issueTitle = issueTitle;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public void setBinary(String binary) {
        this.binary = binary;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public void setOccurrence(String occurrence) {
        this.occurrence = occurrence;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setEngineerId(String engineerId) {
        this.engineerId = engineerId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getEngineerId() {
        return engineerId;
    }
}
