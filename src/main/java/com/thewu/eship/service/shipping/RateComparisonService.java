package com.thewu.eship.service.shipping;

import com.thewu.eship.dto.shipping.CarrierType;
import com.thewu.eship.dto.shipping.RateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for comparing rates across multiple carriers.
 * Provides analysis and recommendations for best shipping options.
 */
@Service
public class RateComparisonService {

    private static final Logger log = LoggerFactory.getLogger(RateComparisonService.class);

    /**
     * Compare rates and find the cheapest option
     */
    public RateDTO findCheapestRate(List<RateDTO> rates) {
        if (rates == null || rates.isEmpty()) {
            return null;
        }

        return rates.stream()
                .min(Comparator.comparing(RateDTO::getRate))
                .orElse(null);
    }

    /**
     * Compare rates and find the fastest option
     */
    public RateDTO findFastestRate(List<RateDTO> rates) {
        if (rates == null || rates.isEmpty()) {
            return null;
        }

        return rates.stream()
                .filter(rate -> rate.getDeliveryDays() != null)
                .min(Comparator.comparing(RateDTO::getDeliveryDays))
                .orElse(null);
    }

    /**
     * Find best value (balance of cost and speed)
     */
    public RateDTO findBestValue(List<RateDTO> rates) {
        if (rates == null || rates.isEmpty()) {
            return null;
        }

        // Calculate value score: lower is better
        // Score = (normalized_price * 0.6) + (normalized_days * 0.4)
        List<RateDTO> validRates = rates.stream()
                .filter(rate -> rate.getDeliveryDays() != null)
                .collect(Collectors.toList());

        if (validRates.isEmpty()) {
            return findCheapestRate(rates);
        }

        double minPrice = validRates.stream().mapToDouble(RateDTO::getRate).min().orElse(0);
        double maxPrice = validRates.stream().mapToDouble(RateDTO::getRate).max().orElse(100);
        int minDays = validRates.stream().mapToInt(RateDTO::getDeliveryDays).min().orElse(1);
        int maxDays = validRates.stream().mapToInt(RateDTO::getDeliveryDays).max().orElse(10);

        double priceRange = maxPrice - minPrice;
        double daysRange = maxDays - minDays;

        if (priceRange == 0)
            priceRange = 1;
        if (daysRange == 0)
            daysRange = 1;

        Map<RateDTO, Double> scores = new HashMap<>();
        for (RateDTO rate : validRates) {
            double normalizedPrice = (rate.getRate() - minPrice) / priceRange;
            double normalizedDays = (rate.getDeliveryDays() - minDays) / (double) daysRange;
            double score = (normalizedPrice * 0.6) + (normalizedDays * 0.4);
            scores.put(rate, score);
        }

        return scores.entrySet().stream()
                .min(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /**
     * Group rates by carrier for comparison
     */
    public Map<CarrierType, List<RateDTO>> groupByCarrier(List<RateDTO> rates) {
        if (rates == null || rates.isEmpty()) {
            return new HashMap<>();
        }

        return rates.stream()
                .collect(Collectors.groupingBy(RateDTO::getCarrier));
    }

    /**
     * Compare UPS vs FedEx rates and provide analysis
     */
    public RateComparisonResult compareUpsVsFedex(List<RateDTO> allRates) {
        Map<CarrierType, List<RateDTO>> ratesByCarrier = groupByCarrier(allRates);

        List<RateDTO> upsRates = ratesByCarrier.getOrDefault(CarrierType.UPS, new ArrayList<>());
        List<RateDTO> fedexRates = ratesByCarrier.getOrDefault(CarrierType.FEDEX, new ArrayList<>());

        RateComparisonResult result = new RateComparisonResult();

        // Overall best rates
        result.setCheapestOverall(findCheapestRate(allRates));
        result.setFastestOverall(findFastestRate(allRates));
        result.setBestValueOverall(findBestValue(allRates));

        // UPS best rates
        if (!upsRates.isEmpty()) {
            result.setUpsCheapest(findCheapestRate(upsRates));
            result.setUpsFastest(findFastestRate(upsRates));
        }

        // FedEx best rates
        if (!fedexRates.isEmpty()) {
            result.setFedexCheapest(findCheapestRate(fedexRates));
            result.setFedexFastest(findFastestRate(fedexRates));
        }

        // Calculate savings
        if (result.getUpsCheapest() != null && result.getFedexCheapest() != null) {
            double upsCheapest = result.getUpsCheapest().getRate();
            double fedexCheapest = result.getFedexCheapest().getRate();
            result.setPriceDifference(Math.abs(upsCheapest - fedexCheapest));
            result.setCheaperCarrier(upsCheapest < fedexCheapest ? CarrierType.UPS : CarrierType.FEDEX);
        }

        // Generate recommendation
        result.setRecommendation(generateRecommendation(result));

        log.info("Rate comparison complete: {} UPS rates, {} FedEx rates",
                upsRates.size(), fedexRates.size());

        return result;
    }

    /**
     * Compare rates from all carriers (UPS, FedEx, DHL) and provide comprehensive
     * analysis
     */
    public RateComparisonResult compareAllCarriers(List<RateDTO> allRates) {
        Map<CarrierType, List<RateDTO>> ratesByCarrier = groupByCarrier(allRates);

        List<RateDTO> upsRates = ratesByCarrier.getOrDefault(CarrierType.UPS, new ArrayList<>());
        List<RateDTO> fedexRates = ratesByCarrier.getOrDefault(CarrierType.FEDEX, new ArrayList<>());
        List<RateDTO> dhlRates = ratesByCarrier.getOrDefault(CarrierType.DHL, new ArrayList<>());

        RateComparisonResult result = new RateComparisonResult();

        // Overall best rates
        result.setCheapestOverall(findCheapestRate(allRates));
        result.setFastestOverall(findFastestRate(allRates));
        result.setBestValueOverall(findBestValue(allRates));

        // UPS best rates
        if (!upsRates.isEmpty()) {
            result.setUpsCheapest(findCheapestRate(upsRates));
            result.setUpsFastest(findFastestRate(upsRates));
        }

        // FedEx best rates
        if (!fedexRates.isEmpty()) {
            result.setFedexCheapest(findCheapestRate(fedexRates));
            result.setFedexFastest(findFastestRate(fedexRates));
        }

        // DHL best rates
        if (!dhlRates.isEmpty()) {
            result.setDhlCheapest(findCheapestRate(dhlRates));
            result.setDhlFastest(findFastestRate(dhlRates));
        }

        // Calculate savings between all carriers
        result.setPriceDifference(calculatePriceDifference(result));
        result.setCheaperCarrier(findCheaperCarrier(result));

        // Generate comprehensive recommendation
        result.setRecommendation(generateComprehensiveRecommendation(result));

        log.info("Rate comparison complete: {} UPS rates, {} FedEx rates, {} DHL rates",
                upsRates.size(), fedexRates.size(), dhlRates.size());

        return result;
    }

    /**
     * Calculate price difference between all carriers
     */
    private Double calculatePriceDifference(RateComparisonResult result) {
        List<Double> prices = new ArrayList<>();

        if (result.getUpsCheapest() != null) {
            prices.add(result.getUpsCheapest().getRate());
        }
        if (result.getFedexCheapest() != null) {
            prices.add(result.getFedexCheapest().getRate());
        }
        if (result.getDhlCheapest() != null) {
            prices.add(result.getDhlCheapest().getRate());
        }

        if (prices.size() < 2) {
            return null;
        }

        double min = prices.stream().min(Double::compareTo).orElse(0.0);
        double max = prices.stream().max(Double::compareTo).orElse(0.0);
        return max - min;
    }

    /**
     * Find the cheapest carrier among all
     */
    private CarrierType findCheaperCarrier(RateComparisonResult result) {
        Double upsPrice = result.getUpsCheapest() != null ? result.getUpsCheapest().getRate() : Double.MAX_VALUE;
        Double fedexPrice = result.getFedexCheapest() != null ? result.getFedexCheapest().getRate() : Double.MAX_VALUE;
        Double dhlPrice = result.getDhlCheapest() != null ? result.getDhlCheapest().getRate() : Double.MAX_VALUE;

        if (upsPrice <= fedexPrice && upsPrice <= dhlPrice) {
            return CarrierType.UPS;
        } else if (fedexPrice <= upsPrice && fedexPrice <= dhlPrice) {
            return CarrierType.FEDEX;
        } else {
            return CarrierType.DHL;
        }
    }

    /**
     * Generate comprehensive recommendation for all carriers
     */
    private String generateComprehensiveRecommendation(RateComparisonResult result) {
        StringBuilder recommendation = new StringBuilder();

        if (result.getCheapestOverall() != null) {
            recommendation.append(String.format("Best price: %s %s at $%.2f",
                    result.getCheapestOverall().getCarrier(),
                    result.getCheapestOverall().getService(),
                    result.getCheapestOverall().getRate()));

            if (result.getCheapestOverall().getDeliveryDays() != null) {
                recommendation.append(String.format(" (%d days). ",
                        result.getCheapestOverall().getDeliveryDays()));
            } else {
                recommendation.append(". ");
            }
        }

        if (result.getFastestOverall() != null &&
                !result.getFastestOverall().equals(result.getCheapestOverall())) {
            recommendation.append(String.format("Fastest delivery: %s %s at $%.2f (%d days). ",
                    result.getFastestOverall().getCarrier(),
                    result.getFastestOverall().getService(),
                    result.getFastestOverall().getRate(),
                    result.getFastestOverall().getDeliveryDays()));
        }

        // Price comparison summary
        if (result.getPriceDifference() != null && result.getPriceDifference() > 0) {
            recommendation.append(String.format("%s offers the most competitive rate, ",
                    result.getCheaperCarrier()));
            recommendation.append(String.format("saving up to $%.2f compared to other carriers.",
                    result.getPriceDifference()));
        }

        return recommendation.toString();
    }

    /**
     * Generate human-readable recommendation
     */
    private String generateRecommendation(RateComparisonResult result) {
        StringBuilder recommendation = new StringBuilder();

        if (result.getCheapestOverall() != null) {
            recommendation.append(String.format("Best price: %s %s at $%.2f",
                    result.getCheapestOverall().getCarrier(),
                    result.getCheapestOverall().getService(),
                    result.getCheapestOverall().getRate()));

            if (result.getCheapestOverall().getDeliveryDays() != null) {
                recommendation.append(String.format(" (%d days). ",
                        result.getCheapestOverall().getDeliveryDays()));
            } else {
                recommendation.append(". ");
            }
        }

        if (result.getFastestOverall() != null &&
                !result.getFastestOverall().equals(result.getCheapestOverall())) {
            recommendation.append(String.format("Fastest delivery: %s %s at $%.2f (%d days). ",
                    result.getFastestOverall().getCarrier(),
                    result.getFastestOverall().getService(),
                    result.getFastestOverall().getRate(),
                    result.getFastestOverall().getDeliveryDays()));
        }

        if (result.getPriceDifference() != null) {
            recommendation.append(String.format("%s is $%.2f cheaper than %s for the cheapest option.",
                    result.getCheaperCarrier(),
                    result.getPriceDifference(),
                    result.getCheaperCarrier() == CarrierType.UPS ? CarrierType.FEDEX : CarrierType.UPS));
        }

        return recommendation.toString();
    }

    /**
     * Result of rate comparison
     */
    public static class RateComparisonResult {
        private RateDTO cheapestOverall;
        private RateDTO fastestOverall;
        private RateDTO bestValueOverall;

        private RateDTO upsCheapest;
        private RateDTO upsFastest;

        private RateDTO fedexCheapest;
        private RateDTO fedexFastest;

        private RateDTO dhlCheapest;
        private RateDTO dhlFastest;

        private Double priceDifference;
        private CarrierType cheaperCarrier;

        private String recommendation;

        // Getters and Setters
        public RateDTO getCheapestOverall() {
            return cheapestOverall;
        }

        public void setCheapestOverall(RateDTO cheapestOverall) {
            this.cheapestOverall = cheapestOverall;
        }

        public RateDTO getFastestOverall() {
            return fastestOverall;
        }

        public void setFastestOverall(RateDTO fastestOverall) {
            this.fastestOverall = fastestOverall;
        }

        public RateDTO getBestValueOverall() {
            return bestValueOverall;
        }

        public void setBestValueOverall(RateDTO bestValueOverall) {
            this.bestValueOverall = bestValueOverall;
        }

        public RateDTO getUpsCheapest() {
            return upsCheapest;
        }

        public void setUpsCheapest(RateDTO upsCheapest) {
            this.upsCheapest = upsCheapest;
        }

        public RateDTO getUpsFastest() {
            return upsFastest;
        }

        public void setUpsFastest(RateDTO upsFastest) {
            this.upsFastest = upsFastest;
        }

        public RateDTO getFedexCheapest() {
            return fedexCheapest;
        }

        public void setFedexCheapest(RateDTO fedexCheapest) {
            this.fedexCheapest = fedexCheapest;
        }

        public RateDTO getFedexFastest() {
            return fedexFastest;
        }

        public void setFedexFastest(RateDTO fedexFastest) {
            this.fedexFastest = fedexFastest;
        }

        public Double getPriceDifference() {
            return priceDifference;
        }

        public void setPriceDifference(Double priceDifference) {
            this.priceDifference = priceDifference;
        }

        public CarrierType getCheaperCarrier() {
            return cheaperCarrier;
        }

        public void setCheaperCarrier(CarrierType cheaperCarrier) {
            this.cheaperCarrier = cheaperCarrier;
        }

        public String getRecommendation() {
            return recommendation;
        }

        public void setRecommendation(String recommendation) {
            this.recommendation = recommendation;
        }

        public RateDTO getDhlCheapest() {
            return dhlCheapest;
        }

        public void setDhlCheapest(RateDTO dhlCheapest) {
            this.dhlCheapest = dhlCheapest;
        }

        public RateDTO getDhlFastest() {
            return dhlFastest;
        }

        public void setDhlFastest(RateDTO dhlFastest) {
            this.dhlFastest = dhlFastest;
        }
    }
}
