package com.thewu.eship.dto.ups;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * UPS Rating API Request DTO
 * Based on UPS Rating API v2409
 */
public class UpsRateRequest {

    @JsonProperty("RateRequest")
    private RateRequestContainer rateRequest;

    public static class RateRequestContainer {
        @JsonProperty("Request")
        private Request request;

        @JsonProperty("Shipment")
        private Shipment shipment;

        // Getters and Setters
        public Request getRequest() {
            return request;
        }

        public void setRequest(Request request) {
            this.request = request;
        }

        public Shipment getShipment() {
            return shipment;
        }

        public void setShipment(Shipment shipment) {
            this.shipment = shipment;
        }
    }

    public static class Request {
        @JsonProperty("RequestOption")
        private String requestOption; // "Rate" or "Shop"

        @JsonProperty("TransactionReference")
        private TransactionReference transactionReference;

        // Getters and Setters
        public String getRequestOption() {
            return requestOption;
        }

        public void setRequestOption(String requestOption) {
            this.requestOption = requestOption;
        }

        public TransactionReference getTransactionReference() {
            return transactionReference;
        }

        public void setTransactionReference(TransactionReference transactionReference) {
            this.transactionReference = transactionReference;
        }
    }

    public static class TransactionReference {
        @JsonProperty("CustomerContext")
        private String customerContext;

        // Getters and Setters
        public String getCustomerContext() {
            return customerContext;
        }

        public void setCustomerContext(String customerContext) {
            this.customerContext = customerContext;
        }
    }

    public static class Shipment {
        @JsonProperty("ShipFrom")
        private Address shipFrom;

        @JsonProperty("ShipTo")
        private Address shipTo;

        @JsonProperty("Shipper")
        private Shipper shipper;

        @JsonProperty("Package")
        private List<Package> packages;

        @JsonProperty("Service")
        private Service service;

        // Getters and Setters
        public Address getShipFrom() {
            return shipFrom;
        }

        public void setShipFrom(Address shipFrom) {
            this.shipFrom = shipFrom;
        }

        public Address getShipTo() {
            return shipTo;
        }

        public void setShipTo(Address shipTo) {
            this.shipTo = shipTo;
        }

        public Shipper getShipper() {
            return shipper;
        }

        public void setShipper(Shipper shipper) {
            this.shipper = shipper;
        }

        public List<Package> getPackages() {
            return packages;
        }

        public void setPackages(List<Package> packages) {
            this.packages = packages;
        }

        public Service getService() {
            return service;
        }

        public void setService(Service service) {
            this.service = service;
        }
    }

    public static class Address {
        @JsonProperty("Name")
        private String name;

        @JsonProperty("AddressLine")
        private List<String> addressLine;

        @JsonProperty("City")
        private String city;

        @JsonProperty("StateProvinceCode")
        private String stateProvinceCode;

        @JsonProperty("PostalCode")
        private String postalCode;

        @JsonProperty("CountryCode")
        private String countryCode;

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getAddressLine() {
            return addressLine;
        }

        public void setAddressLine(List<String> addressLine) {
            this.addressLine = addressLine;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getStateProvinceCode() {
            return stateProvinceCode;
        }

        public void setStateProvinceCode(String stateProvinceCode) {
            this.stateProvinceCode = stateProvinceCode;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }
    }

    public static class Shipper {
        @JsonProperty("Name")
        private String name;

        @JsonProperty("ShipperNumber")
        private String shipperNumber;

        @JsonProperty("Address")
        private Address address;

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getShipperNumber() {
            return shipperNumber;
        }

        public void setShipperNumber(String shipperNumber) {
            this.shipperNumber = shipperNumber;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }
    }

    public static class Package {
        @JsonProperty("PackagingType")
        private PackagingType packagingType;

        @JsonProperty("Dimensions")
        private Dimensions dimensions;

        @JsonProperty("PackageWeight")
        private Weight packageWeight;

        // Getters and Setters
        public PackagingType getPackagingType() {
            return packagingType;
        }

        public void setPackagingType(PackagingType packagingType) {
            this.packagingType = packagingType;
        }

        public Dimensions getDimensions() {
            return dimensions;
        }

        public void setDimensions(Dimensions dimensions) {
            this.dimensions = dimensions;
        }

        public Weight getPackageWeight() {
            return packageWeight;
        }

        public void setPackageWeight(Weight packageWeight) {
            this.packageWeight = packageWeight;
        }
    }

    public static class PackagingType {
        @JsonProperty("Code")
        private String code; // "02" = Customer Supplied Package

        @JsonProperty("Description")
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

    public static class Dimensions {
        @JsonProperty("UnitOfMeasurement")
        private UnitOfMeasurement unitOfMeasurement;

        @JsonProperty("Length")
        private String length;

        @JsonProperty("Width")
        private String width;

        @JsonProperty("Height")
        private String height;

        // Getters and Setters
        public UnitOfMeasurement getUnitOfMeasurement() {
            return unitOfMeasurement;
        }

        public void setUnitOfMeasurement(UnitOfMeasurement unitOfMeasurement) {
            this.unitOfMeasurement = unitOfMeasurement;
        }

        public String getLength() {
            return length;
        }

        public void setLength(String length) {
            this.length = length;
        }

        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }
    }

    public static class Weight {
        @JsonProperty("UnitOfMeasurement")
        private UnitOfMeasurement unitOfMeasurement;

        @JsonProperty("Weight")
        private String weight;

        // Getters and Setters
        public UnitOfMeasurement getUnitOfMeasurement() {
            return unitOfMeasurement;
        }

        public void setUnitOfMeasurement(UnitOfMeasurement unitOfMeasurement) {
            this.unitOfMeasurement = unitOfMeasurement;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }
    }

    public static class UnitOfMeasurement {
        @JsonProperty("Code")
        private String code; // "IN" = inches, "CM" = centimeters, "LBS" = pounds, "KGS" = kilograms

        @JsonProperty("Description")
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

    public static class Service {
        @JsonProperty("Code")
        private String code; // Service level code (e.g., "03" = Ground)

        @JsonProperty("Description")
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
    public RateRequestContainer getRateRequest() {
        return rateRequest;
    }

    public void setRateRequest(RateRequestContainer rateRequest) {
        this.rateRequest = rateRequest;
    }
}
