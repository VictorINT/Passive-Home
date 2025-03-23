package com.passivehouse.PassiveHouse.models;

public class RaspberryPiMessage {
    private String content;
    private String sensorData;

    // Getters and setters
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getSensorData() { return sensorData; }
    public void setSensorData(String sensorData) { this.sensorData = sensorData; }
}

