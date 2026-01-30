package com.thewu.eship.dto.dhl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Response object from DHL Rating API
 */
@Data
public class DhlRateResponse {

    @JsonProperty("products")
    private List<Product> products;

    @JsonProperty("exchangeRates")
    private List<ExchangeRate> exchangeRates;

    @JsonProperty("warnings")
    private List<Warning> warnings;

    @Data
    public static class Product {
        @JsonProperty("productName")
        private String productName;

        @JsonProperty("productCode")
        private String productCode;

        @JsonProperty("localProductCode")
        private String localProductCode;

        @JsonProperty("localProductCountryCode")
        private String localProductCountryCode;

        @JsonProperty("networkTypeCode")
        private String networkTypeCode;

        @JsonProperty("isCustomerAgreement")
        private Boolean isCustomerAgreement;

        @JsonProperty("weight")
        private Weight weight;

        @JsonProperty("totalPrice")
        private List<Price> totalPrice;

        @JsonProperty("totalPriceBreakdown")
        private List<PriceBreakdown> totalPriceBreakdown;

        @JsonProperty("detailedPriceBreakdown")
        private List<DetailedPriceBreakdown> detailedPriceBreakdown;

        @JsonProperty("serviceCodeMutuallyExclusiveGroups")
        private List<ServiceCodeGroup> serviceCodeMutuallyExclusiveGroups;

        @JsonProperty("serviceCodeDependencyRuleGroups")
        private List<ServiceCodeGroup> serviceCodeDependencyRuleGroups;

        @JsonProperty("pickupCapabilities")
        private PickupCapabilities pickupCapabilities;

        @JsonProperty("deliveryCapabilities")
        private DeliveryCapabilities deliveryCapabilities;

        @JsonProperty("items")
        private List<Item> items;

        @JsonProperty("pricingDate")
        private String pricingDate;
    }

    @Data
    public static class Weight {
        @JsonProperty("volumetric")
        private Double volumetric;

        @JsonProperty("provided")
        private Double provided;

        @JsonProperty("unitOfMeasurement")
        private String unitOfMeasurement;
    }

    @Data
    public static class Price {
        @JsonProperty("price")
        private Double price;

        @JsonProperty("priceCurrency")
        private String priceCurrency;

        @JsonProperty("priceBreakdown")
        private List<PriceComponent> priceBreakdown;
    }

    @Data
    public static class PriceComponent {
        @JsonProperty("typeCode")
        private String typeCode;

        @JsonProperty("price")
        private Double price;

        @JsonProperty("priceType")
        private String priceType;

        @JsonProperty("priceBreakdown")
        private List<PriceComponent> priceBreakdown;
    }

    @Data
    public static class PriceBreakdown {
        @JsonProperty("typeCode")
        private String typeCode;

        @JsonProperty("price")
        private Double price;

        @JsonProperty("priceType")
        private String priceType;

        @JsonProperty("priceBreakdown")
        private List<PriceComponent> priceBreakdown;
    }

    @Data
    public static class DetailedPriceBreakdown {
        @JsonProperty("currencyType")
        private String currencyType;

        @JsonProperty("priceCurrency")
        private String priceCurrency;

        @JsonProperty("breakdown")
        private List<PriceBreakdown> breakdown;
    }

    @Data
    public static class ServiceCodeGroup {
        @JsonProperty("serviceCodeRuleName")
        private String serviceCodeRuleName;

        @JsonProperty("serviceCodes")
        private List<ServiceCode> serviceCodes;
    }

    @Data
    public static class ServiceCode {
        @JsonProperty("serviceCode")
        private String serviceCode;
    }

    @Data
    public static class PickupCapabilities {
        @JsonProperty("nextBusinessDay")
        private Boolean nextBusinessDay;

        @JsonProperty("localCutoffDateAndTime")
        private String localCutoffDateAndTime;

        @JsonProperty("gmtCutoffTime")
        private String gmtCutoffTime;

        @JsonProperty("pickupEarliest")
        private String pickupEarliest;

        @JsonProperty("pickupLatest")
        private String pickupLatest;

        @JsonProperty("originServiceAreaCode")
        private String originServiceAreaCode;

        @JsonProperty("originFacilityAreaCode")
        private String originFacilityAreaCode;

        @JsonProperty("pickupAdditionalDays")
        private Integer pickupAdditionalDays;

        @JsonProperty("pickupDayOfWeek")
        private Integer pickupDayOfWeek;
    }

    @Data
    public static class DeliveryCapabilities {
        @JsonProperty("deliveryTypeCode")
        private String deliveryTypeCode;

        @JsonProperty("estimatedDeliveryDateAndTime")
        private String estimatedDeliveryDateAndTime;

        @JsonProperty("destinationServiceAreaCode")
        private String destinationServiceAreaCode;

        @JsonProperty("destinationFacilityAreaCode")
        private String destinationFacilityAreaCode;

        @JsonProperty("deliveryAdditionalDays")
        private Integer deliveryAdditionalDays;

        @JsonProperty("deliveryDayOfWeek")
        private Integer deliveryDayOfWeek;

        @JsonProperty("totalTransitDays")
        private Integer totalTransitDays;
    }

    @Data
    public static class Item {
        @JsonProperty("number")
        private Integer number;

        @JsonProperty("breakdown")
        private List<PriceBreakdown> breakdown;
    }

    @Data
    public static class ExchangeRate {
        @JsonProperty("currentExchangeRate")
        private Double currentExchangeRate;

        @JsonProperty("currency")
        private String currency;

        @JsonProperty("baseCurrency")
        private String baseCurrency;
    }

    @Data
    public static class Warning {
        @JsonProperty("message")
        private String message;
    }
}
