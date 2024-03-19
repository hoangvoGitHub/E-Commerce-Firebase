package com.example.dreamteam;

import java.util.List;

public class ProductSpecification {
    private String title;
    private List<SpecificationValue> value;

    public ProductSpecification(String title, List<SpecificationValue> value) {
        this.title = title;
        this.value = value;
    }

    public ProductSpecification() {
        this.title = "title";
        this.value = List.of();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<SpecificationValue> getValue() {
        return value;
    }

    public void setValue(List<SpecificationValue> value) {
        this.value = value;
    }

    public static class SpecificationValue {
        public String feature;
        public String feature_value;

        public SpecificationValue(String feature, String feature_value) {
            this.feature = feature;
            this.feature_value = feature_value;
        }

        public String getFeature() {
            return feature;
        }

        public void setFeature(String feature) {
            this.feature = feature;
        }

        public String getFeatureValue() {
            return feature_value;
        }

        public void setFeatureValue(String feature_value) {
            this.feature_value = feature_value;
        }
    }



}


