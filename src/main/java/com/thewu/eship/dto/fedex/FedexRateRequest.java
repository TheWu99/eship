package com.thewu.eship.dto.fedex;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Request DTO for FedEx Rate Quotes API
 */
@Data
public class FedexRateRequest {

    @JsonProperty("accountNumber")
    private AccountNumber accountNumber;

    @JsonProperty("requestedShipment")
    private RequestedShipment requestedShipment;

    @Data
    public static class AccountNumber {
        @JsonProperty("value")
        private String value;
    }

    @Data
    public static class RequestedShipment {
        @JsonProperty("shipper")
        private Party shipper;

        @JsonProperty("recipient")
        private Party recipient;

        @JsonProperty("pickupType")
        private String pickupType; // DROPOFF_AT_FEDEX_LOCATION, CONTACT_FEDEX_TO_SCHEDULE, USE_SCHEDULED_PICKUP

        @JsonProperty("serviceType")
        private String serviceType; // Optional: specific service like FEDEX_GROUND, STANDARD_OVERNIGHT

        @JsonProperty("rateRequestType")
        private List<String> rateRequestType; // LIST, ACCOUNT

        @JsonProperty("requestedPackageLineItems")
        private List<RequestedPackageLineItem> requestedPackageLineItems;
    }

    @Data
    public static class Party {
        @JsonProperty("address")
        private Address address;
    }

    @Data
    public static class Address {
        @JsonProperty("streetLines")
        private List<String> streetLines;

        @JsonProperty("city")
        private String city;

        @JsonProperty("stateOrProvinceCode")
        private String stateOrProvinceCode;

        @JsonProperty("postalCode")
        private String postalCode;

        @JsonProperty("countryCode")
        private String countryCode;

        @JsonProperty("residential")
        private Boolean residential;
    }

    @Data
    public static class RequestedPackageLineItem {
        @JsonProperty("weight")
        private Weight weight;

        @JsonProperty("dimensions")
        private Dimensions dimensions;
    }

    @Data
    public static class Weight {
        @JsonProperty("units")
        private String units; // LB, KG

        @JsonProperty("value")
        private Double value;
    }

    @Data
    public static class Dimensions {
        @JsonProperty("length")
        private Integer length;

        @JsonProperty("width")
        private Integer width;

        @JsonProperty("height")
        private Integer height;

        @JsonProperty("units")
        private String units; // IN, CM
    }
}
