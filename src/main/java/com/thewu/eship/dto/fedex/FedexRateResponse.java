package com.thewu.eship.dto.fedex;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Response DTO for FedEx Rate Quotes API
 */
@Data
public class FedexRateResponse {

    @JsonProperty("transactionId")
    private String transactionId;

    @JsonProperty("output")
    private Output output;

    @Data
    public static class Output {
        @JsonProperty("rateReplyDetails")
        private List<RateReplyDetail> rateReplyDetails;

        @JsonProperty("alerts")
        private List<Alert> alerts;
    }

    @Data
    public static class RateReplyDetail {
        @JsonProperty("serviceType")
        private String serviceType; // FEDEX_GROUND, STANDARD_OVERNIGHT, etc.

        @JsonProperty("serviceName")
        private String serviceName;

        @JsonProperty("packagingType")
        private String packagingType;

        @JsonProperty("ratedShipmentDetails")
        private List<RatedShipmentDetail> ratedShipmentDetails;

        @JsonProperty("operationalDetail")
        private OperationalDetail operationalDetail;
    }

    @Data
    public static class RatedShipmentDetail {
        @JsonProperty("rateType")
        private String rateType; // PAYOR_ACCOUNT, PAYOR_LIST, etc.

        @JsonProperty("ratedWeightMethod")
        private String ratedWeightMethod;

        @JsonProperty("totalDiscounts")
        private Double totalDiscounts;

        @JsonProperty("totalBaseCharge")
        private Double totalBaseCharge;

        @JsonProperty("totalNetCharge")
        private Double totalNetCharge;

        @JsonProperty("totalNetFedExCharge")
        private Double totalNetFedExCharge;

        @JsonProperty("shipmentRateDetail")
        private ShipmentRateDetail shipmentRateDetail;

        @JsonProperty("currency")
        private String currency;
    }

    @Data
    public static class ShipmentRateDetail {
        @JsonProperty("rateType")
        private String rateType;

        @JsonProperty("rateScale")
        private String rateScale;

        @JsonProperty("rateZone")
        private String rateZone;

        @JsonProperty("pricingCode")
        private String pricingCode;

        @JsonProperty("totalBillingWeight")
        private Weight totalBillingWeight;

        @JsonProperty("totalBaseCharge")
        private Double totalBaseCharge;

        @JsonProperty("totalNetCharge")
        private Double totalNetCharge;

        @JsonProperty("totalNetFedExCharge")
        private Double totalNetFedExCharge;

        @JsonProperty("currency")
        private String currency;

        @JsonProperty("fuelSurchargePercent")
        private Double fuelSurchargePercent;
    }

    @Data
    public static class Weight {
        @JsonProperty("units")
        private String units;

        @JsonProperty("value")
        private Double value;
    }

    @Data
    public static class OperationalDetail {
        @JsonProperty("originLocationIds")
        private List<String> originLocationIds;

        @JsonProperty("commitDays")
        private List<String> commitDays;

        @JsonProperty("serviceCode")
        private String serviceCode;

        @JsonProperty("airportId")
        private String airportId;

        @JsonProperty("scac")
        private String scac;

        @JsonProperty("originServiceArea")
        private String originServiceArea;

        @JsonProperty("destinationServiceArea")
        private String destinationServiceArea;

        @JsonProperty("deliveryDay")
        private String deliveryDay;

        @JsonProperty("publishedDeliveryTime")
        private String publishedDeliveryTime;

        @JsonProperty("transitTime")
        private String transitTime; // ONE_DAY, TWO_DAYS, THREE_DAYS, etc.
    }

    @Data
    public static class Alert {
        @JsonProperty("code")
        private String code;

        @JsonProperty("message")
        private String message;

        @JsonProperty("alertType")
        private String alertType;
    }
}
