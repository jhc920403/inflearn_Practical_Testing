package sample.cafekiosk.spring.api.service.product.response;

import lombok.Builder;
import lombok.Getter;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

@Getter
public class ProductResponse {

    private Long id;
    private String productNo;
    private ProductType productType;
    private ProductStatus productStatus;
    private String productName;
    private int productPrice;

    @Builder
    private ProductResponse(
            Long id
            , String productNo
            , ProductType productType
            , ProductStatus productStatus
            , String productName
            , int productPrice
    ) {
        this.id = id;
        this.productNo = productNo;
        this.productType = productType;
        this.productStatus = productStatus;
        this.productName = productName;
        this.productPrice = productPrice;
    }

    public static ProductResponse of(Product product) {
        return ProductResponse.builder()
                .productNo(product.getProductNo())
                .productType(product.getProductType())
                .productStatus(product.getProductStatus())
                .productName(product.getProductName())
                .productPrice(product.getProductPrice())
                .build();
    }
}
