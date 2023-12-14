package sample.cafekiosk.spring.domain.product;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum ProductStatus {

    SELLING("판매중")
    , HOLD("보류")
    , SOLD_OUT("매진");

    private final String text;

    public static List<ProductStatus> forDisplay() {
        return List.of(SELLING, HOLD);
    }
}
