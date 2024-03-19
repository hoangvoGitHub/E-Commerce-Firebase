package com.example.dreamteam;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecificationMapper {
    public static List<ProductSpecificationModel> toProductSpecificationModelList(List<ProductSpecification> input) {
        ArrayList<ProductSpecificationModel> output = new ArrayList<>();
        for (ProductSpecification specification : input) {
            output.add(new ProductSpecificationModel(ProductSpecificationModel.SPECIFICATION_TITLE,
                    specification.getTitle()
            ));
            for (ProductSpecification.SpecificationValue value : specification.getValue()) {
                output.add(new ProductSpecificationModel(ProductSpecificationModel.SPECIFICATION_BODY,
                        value.getFeature(),
                        value.getFeatureValue()
                ));

            }

        }
        return output;
    }
}
