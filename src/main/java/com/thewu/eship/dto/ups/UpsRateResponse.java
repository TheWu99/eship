package com.thewu.eship.dto.ups;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * UPS Rating API Response DTO
 * Based on UPS Rating API v2409
 */
public class UpsRateResponse {

    @JsonProperty("RateResponse")
    private RateResponseContainer rateResponse;

    public static class RateResponseContainer {
        @JsonProperty("Response")
        private Response response;

        @JsonProperty("RatedShipment")
        private List<RatedShipment> ratedShipment;

        // Getters and Setters
        public Response getResponse() {
            return response;
        }

        public void setResponse(Response response) {
            this.response = response;
        }

        public List<RatedShipment> getRatedShipment() {
            return ratedShipment;
        }

        public void setRatedShipment(List<RatedShipment> ratedShipment) {
            this.ratedShipment = ratedShipment;
        }
    }

    public static class Response {
        @JsonProperty("ResponseStatus")
        private ResponseStatus responseStatus;

        @JsonProperty("Alert")
        private List<Alert> alert;

        @JsonProperty("TransactionReference")
        private TransactionReference transactionReference;

        // Getters and Setters
        public ResponseStatus getResponseStatus() {
            return responseStatus;
        }

        public void setResponseStatus(ResponseStatus responseStatus) {
            this.responseStatus = responseStatus;
        }

        public List<Alert> getAlert() {
            return alert;
        }

        public void setAlert(List<Alert> alert) {
            this.alert = alert;
        }

        public TransactionReference getTransactionReference() {
            return transactionReference;
        }

        public void setTransactionReference(TransactionReference transactionReference) {
            this.transactionReference = transactionReference;
        }
    }

    public static class ResponseStatus {
        @JsonProperty("Code")
        private String code;

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

    public static class Alert {
        @JsonProperty("Code")
        private String code;

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

    public static class RatedShipment {
        @JsonProperty("Service")
        private Service service;

        @JsonProperty("TotalCharges")
        private MonetaryValue totalCharges;

        @JsonProperty("BaseServiceCharge")
        private MonetaryValue baseServiceCharge;

        @JsonProperty("TransportationCharges")
        private MonetaryValue transportationCharges;

        @JsonProperty("ServiceOptionsCharges")
        private MonetaryValue serviceOptionsCharges;

        @JsonProperty("BillingWeight")
        private BillingWeight billingWeight;

        @JsonProperty("TimeInTransit")
        private TimeInTransit timeInTransit;

        @JsonProperty("GuaranteedDelivery")
        private GuaranteedDelivery guaranteedDelivery;

        @JsonProperty("RatedPackage")
        private List<RatedPackage> ratedPackage;

        // Getters and Setters
        public Service getService() {
            return service;
        }

        public void setService(Service service) {
            this.service = service;
        }

        public MonetaryValue getTotalCharges() {
            return totalCharges;
        }

        public void setTotalCharges(MonetaryValue totalCharges) {
            this.totalCharges = totalCharges;
        }

        public MonetaryValue getBaseServiceCharge() {
            return baseServiceCharge;
        }

        public void setBaseServiceCharge(MonetaryValue baseServiceCharge) {
            this.baseServiceCharge = baseServiceCharge;
        }

        public MonetaryValue getTransportationCharges() {
            return transportationCharges;
        }

        public void setTransportationCharges(MonetaryValue transportationCharges) {
            this.transportationCharges = transportationCharges;
        }

        public MonetaryValue getServiceOptionsCharges() {
            return serviceOptionsCharges;
        }

        public void setServiceOptionsCharges(MonetaryValue serviceOptionsCharges) {
            this.serviceOptionsCharges = serviceOptionsCharges;
        }

        public BillingWeight getBillingWeight() {
            return billingWeight;
        }

        public void setBillingWeight(BillingWeight billingWeight) {
            this.billingWeight = billingWeight;
        }

        public TimeInTransit getTimeInTransit() {
            return timeInTransit;
        }

        public void setTimeInTransit(TimeInTransit timeInTransit) {
            this.timeInTransit = timeInTransit;
        }

        public GuaranteedDelivery getGuaranteedDelivery() {
            return guaranteedDelivery;
        }

        public void setGuaranteedDelivery(GuaranteedDelivery guaranteedDelivery) {
            this.guaranteedDelivery = guaranteedDelivery;
        }

        public List<RatedPackage> getRatedPackage() {
            return ratedPackage;
        }

        public void setRatedPackage(List<RatedPackage> ratedPackage) {
            this.ratedPackage = ratedPackage;
        }
    }

    public static class Service {
        @JsonProperty("Code")
        private String code;

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

    public static class MonetaryValue {
        @JsonProperty("CurrencyCode")
        private String currencyCode;

        @JsonProperty("MonetaryValue")
        private String monetaryValue;

        // Getters and Setters
        public String getCurrencyCode() {
            return currencyCode;
        }

        public void setCurrencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
        }

        public String getMonetaryValue() {
            return monetaryValue;
        }

        public void setMonetaryValue(String monetaryValue) {
            this.monetaryValue = monetaryValue;
        }
    }

    public static class BillingWeight {
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
        private String code;

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

    public static class TimeInTransit {
        @JsonProperty("PickupDate")
        private String pickupDate;

        @JsonProperty("ServiceSummary")
        private ServiceSummary serviceSummary;

        // Getters and Setters
        public String getPickupDate() {
            return pickupDate;
        }

        public void setPickupDate(String pickupDate) {
            this.pickupDate = pickupDate;
        }

        public ServiceSummary getServiceSummary() {
            return serviceSummary;
        }

        public void setServiceSummary(ServiceSummary serviceSummary) {
            this.serviceSummary = serviceSummary;
        }
    }

    public static class ServiceSummary {
        @JsonProperty("EstimatedArrival")
        private EstimatedArrival estimatedArrival;

        // Getters and Setters
        public EstimatedArrival getEstimatedArrival() {
            return estimatedArrival;
        }

        public void setEstimatedArrival(EstimatedArrival estimatedArrival) {
            this.estimatedArrival = estimatedArrival;
        }
    }

    public static class EstimatedArrival {
        @JsonProperty("Arrival")
        private Arrival arrival;

        @JsonProperty("BusinessDaysInTransit")
        private String businessDaysInTransit;

        @JsonProperty("Pickup")
        private Pickup pickup;

        // Getters and Setters
        public Arrival getArrival() {
            return arrival;
        }

        public void setArrival(Arrival arrival) {
            this.arrival = arrival;
        }

        public String getBusinessDaysInTransit() {
            return businessDaysInTransit;
        }

        public void setBusinessDaysInTransit(String businessDaysInTransit) {
            this.businessDaysInTransit = businessDaysInTransit;
        }

        public Pickup getPickup() {
            return pickup;
        }

        public void setPickup(Pickup pickup) {
            this.pickup = pickup;
        }
    }

    public static class Arrival {
        @JsonProperty("Date")
        private String date;

        @JsonProperty("Time")
        private String time;

        // Getters and Setters
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

    public static class Pickup {
        @JsonProperty("Date")
        private String date;

        @JsonProperty("Time")
        private String time;

        // Getters and Setters
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

    public static class GuaranteedDelivery {
        @JsonProperty("BusinessDaysInTransit")
        private String businessDaysInTransit;

        @JsonProperty("DeliveryByTime")
        private String deliveryByTime;

        // Getters and Setters
        public String getBusinessDaysInTransit() {
            return businessDaysInTransit;
        }

        public void setBusinessDaysInTransit(String businessDaysInTransit) {
            this.businessDaysInTransit = businessDaysInTransit;
        }

        public String getDeliveryByTime() {
            return deliveryByTime;
        }

        public void setDeliveryByTime(String deliveryByTime) {
            this.deliveryByTime = deliveryByTime;
        }
    }

    public static class RatedPackage {
        @JsonProperty("TotalCharges")
        private MonetaryValue totalCharges;

        @JsonProperty("BaseServiceCharge")
        private MonetaryValue baseServiceCharge;

        @JsonProperty("Weight")
        private String weight;

        @JsonProperty("BillingWeight")
        private BillingWeight billingWeight;

        // Getters and Setters
        public MonetaryValue getTotalCharges() {
            return totalCharges;
        }

        public void setTotalCharges(MonetaryValue totalCharges) {
            this.totalCharges = totalCharges;
        }

        public MonetaryValue getBaseServiceCharge() {
            return baseServiceCharge;
        }

        public void setBaseServiceCharge(MonetaryValue baseServiceCharge) {
            this.baseServiceCharge = baseServiceCharge;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public BillingWeight getBillingWeight() {
            return billingWeight;
        }

        public void setBillingWeight(BillingWeight billingWeight) {
            this.billingWeight = billingWeight;
        }
    }

    // Main DTO Getters and Setters
    public RateResponseContainer getRateResponse() {
        return rateResponse;
    }

    public void setRateResponse(RateResponseContainer rateResponse) {
        this.rateResponse = rateResponse;
    }
}
