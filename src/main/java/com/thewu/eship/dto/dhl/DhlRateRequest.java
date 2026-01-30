package com.thewu.eship.dto.dhl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Request object for DHL Rating API
 */
@Data
public class DhlRateRequest {

    @JsonProperty("customerDetails")
    private CustomerDetails customerDetails;

    @JsonProperty("accounts")
    private List<Account> accounts;

    @JsonProperty("productCode")
    private String productCode;

    @JsonProperty("localProductCode")
    private String localProductCode;

    @JsonProperty("valueAddedServices")
    private List<ValueAddedService> valueAddedServices;

    @JsonProperty("productsAndServices")
    private List<ProductService> productsAndServices;

    @JsonProperty("payerCountryCode")
    private String payerCountryCode;

    @JsonProperty("plannedShippingDateAndTime")
    private String plannedShippingDateAndTime;

    @JsonProperty("unitOfMeasurement")
    private String unitOfMeasurement = "metric";

    @JsonProperty("isCustomsDeclarable")
    private Boolean isCustomsDeclarable = false;

    @JsonProperty("monetaryAmount")
    private List<MonetaryAmount> monetaryAmount;

    @JsonProperty("requestAllValueAddedServices")
    private Boolean requestAllValueAddedServices = false;

    @JsonProperty("estimatedDeliveryDate")
    private EstimatedDeliveryDate estimatedDeliveryDate;

    @JsonProperty("getAdditionalInformation")
    private List<AdditionalInfo> getAdditionalInformation;

    @JsonProperty("returnStandardProductsOnly")
    private Boolean returnStandardProductsOnly = false;

    @JsonProperty("nextBusinessDay")
    private Boolean nextBusinessDay = false;

    @JsonProperty("productTypeCode")
    private String productTypeCode;

    @JsonProperty("packages")
    private List<Package> packages;

    @Data
    public static class CustomerDetails {
        @JsonProperty("shipperDetails")
        private ShipperDetails shipperDetails;

        @JsonProperty("receiverDetails")
        private ReceiverDetails receiverDetails;
    }

    @Data
    public static class ShipperDetails {
        @JsonProperty("postalCode")
        private String postalCode;

        @JsonProperty("cityName")
        private String cityName;

        @JsonProperty("countryCode")
        private String countryCode;

        @JsonProperty("provinceCode")
        private String provinceCode;

        @JsonProperty("addressLine1")
        private String addressLine1;

        @JsonProperty("addressLine2")
        private String addressLine2;

        @JsonProperty("addressLine3")
        private String addressLine3;

        @JsonProperty("countyName")
        private String countyName;
    }

    @Data
    public static class ReceiverDetails {
        @JsonProperty("postalCode")
        private String postalCode;

        @JsonProperty("cityName")
        private String cityName;

        @JsonProperty("countryCode")
        private String countryCode;

        @JsonProperty("provinceCode")
        private String provinceCode;

        @JsonProperty("addressLine1")
        private String addressLine1;

        @JsonProperty("addressLine2")
        private String addressLine2;

        @JsonProperty("addressLine3")
        private String addressLine3;

        @JsonProperty("countyName")
        private String countyName;
    }

    @Data
    public static class Account {
        @JsonProperty("typeCode")
        private String typeCode = "shipper";

        @JsonProperty("number")
        private String number;
    }

    @Data
    public static class ValueAddedService {
        @JsonProperty("serviceCode")
        private String serviceCode;

        @JsonProperty("value")
        private Double value;

        @JsonProperty("currency")
        private String currency;

        @JsonProperty("method")
        private String method;
    }

    @Data
    public static class ProductService {
        @JsonProperty("productCode")
        private String productCode;

        @JsonProperty("localProductCode")
        private String localProductCode;
    }

    @Data
    public static class MonetaryAmount {
        @JsonProperty("typeCode")
        private String typeCode;

        @JsonProperty("value")
        private Double value;

        @JsonProperty("currency")
        private String currency;
    }

    @Data
    public static class EstimatedDeliveryDate {
        @JsonProperty("isRequested")
        private Boolean isRequested = true;

        @JsonProperty("typeCode")
        private String typeCode = "QDDC";
    }

    @Data
    public static class AdditionalInfo {
        @JsonProperty("isRequested")
        private Boolean isRequested = true;

        @JsonProperty("typeCode")
        private String typeCode;
    }

    @Data
    public static class Package {
        @JsonProperty("typeCode")
        private String typeCode = "3BX"; // Customer-provided box

        @JsonProperty("weight")
        private Double weight;

        @JsonProperty("dimensions")
        private Dimensions dimensions;
    }

    @Data
    public static class Dimensions {
        @JsonProperty("length")
        private Double length;

        @JsonProperty("width")
        private Double width;

        @JsonProperty("height")
        private Double height;
    }
}
