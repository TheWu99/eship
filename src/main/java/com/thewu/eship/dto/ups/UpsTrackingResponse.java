package com.thewu.eship.dto.ups;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * UPS Tracking API Response DTO
 * Based on UPS Tracking API v1
 */
public class UpsTrackingResponse {

    @JsonProperty("trackResponse")
    private TrackResponseContainer trackResponse;

    public static class TrackResponseContainer {
        @JsonProperty("shipment")
        private List<Shipment> shipment;

        // Getters and Setters
        public List<Shipment> getShipment() {
            return shipment;
        }

        public void setShipment(List<Shipment> shipment) {
            this.shipment = shipment;
        }
    }

    public static class Shipment {
        @JsonProperty("inquiryNumber")
        private String inquiryNumber;

        @JsonProperty("package")
        private List<Package> packages;

        @JsonProperty("deliveryDate")
        private List<DeliveryDate> deliveryDate;

        @JsonProperty("deliveryTime")
        private DeliveryTime deliveryTime;

        // Getters and Setters
        public String getInquiryNumber() {
            return inquiryNumber;
        }

        public void setInquiryNumber(String inquiryNumber) {
            this.inquiryNumber = inquiryNumber;
        }

        public List<Package> getPackages() {
            return packages;
        }

        public void setPackages(List<Package> packages) {
            this.packages = packages;
        }

        public List<DeliveryDate> getDeliveryDate() {
            return deliveryDate;
        }

        public void setDeliveryDate(List<DeliveryDate> deliveryDate) {
            this.deliveryDate = deliveryDate;
        }

        public DeliveryTime getDeliveryTime() {
            return deliveryTime;
        }

        public void setDeliveryTime(DeliveryTime deliveryTime) {
            this.deliveryTime = deliveryTime;
        }
    }

    public static class Package {
        @JsonProperty("trackingNumber")
        private String trackingNumber;

        @JsonProperty("activity")
        private List<Activity> activity;

        @JsonProperty("deliveryDate")
        private List<DeliveryDate> deliveryDate;

        @JsonProperty("deliveryTime")
        private DeliveryTime deliveryTime;

        @JsonProperty("currentStatus")
        private Status currentStatus;

        @JsonProperty("service")
        private Service service;

        // Getters and Setters
        public String getTrackingNumber() {
            return trackingNumber;
        }

        public void setTrackingNumber(String trackingNumber) {
            this.trackingNumber = trackingNumber;
        }

        public List<Activity> getActivity() {
            return activity;
        }

        public void setActivity(List<Activity> activity) {
            this.activity = activity;
        }

        public List<DeliveryDate> getDeliveryDate() {
            return deliveryDate;
        }

        public void setDeliveryDate(List<DeliveryDate> deliveryDate) {
            this.deliveryDate = deliveryDate;
        }

        public DeliveryTime getDeliveryTime() {
            return deliveryTime;
        }

        public void setDeliveryTime(DeliveryTime deliveryTime) {
            this.deliveryTime = deliveryTime;
        }

        public Status getCurrentStatus() {
            return currentStatus;
        }

        public void setCurrentStatus(Status currentStatus) {
            this.currentStatus = currentStatus;
        }

        public Service getService() {
            return service;
        }

        public void setService(Service service) {
            this.service = service;
        }
    }

    public static class Activity {
        @JsonProperty("location")
        private Location location;

        @JsonProperty("status")
        private Status status;

        @JsonProperty("date")
        private String date;

        @JsonProperty("time")
        private String time;

        // Getters and Setters
        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }

    public static class Location {
        @JsonProperty("address")
        private Address address;

        // Getters and Setters
        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }
    }

    public static class Address {
        @JsonProperty("city")
        private String city;

        @JsonProperty("stateProvince")
        private String stateProvince;

        @JsonProperty("postalCode")
        private String postalCode;

        @JsonProperty("country")
        private String country;

        // Getters and Setters
        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getStateProvince() {
            return stateProvince;
        }

        public void setStateProvince(String stateProvince) {
            this.stateProvince = stateProvince;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }

    public static class Status {
        @JsonProperty("type")
        private String type;

        @JsonProperty("description")
        private String description;

        @JsonProperty("code")
        private String code;

        // Getters and Setters
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    public static class DeliveryDate {
        @JsonProperty("type")
        private String type;

        @JsonProperty("date")
        private String date;

        // Getters and Setters
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }

    public static class DeliveryTime {
        @JsonProperty("type")
        private String type;

        @JsonProperty("startTime")
        private String startTime;

        @JsonProperty("endTime")
        private String endTime;

        // Getters and Setters
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }
    }

    public static class Service {
        @JsonProperty("code")
        private String code;

        @JsonProperty("description")
        private String description;

        // Getters and Setters
        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    // Main DTO Getters and Setters
    public TrackResponseContainer getTrackResponse() {
        return trackResponse;
    }

    public void setTrackResponse(TrackResponseContainer trackResponse) {
        this.trackResponse = trackResponse;
    }
}
