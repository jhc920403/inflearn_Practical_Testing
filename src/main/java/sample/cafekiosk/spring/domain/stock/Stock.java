package sample.cafekiosk.spring.domain.stock;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Stock {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STOCK_ID")
    private Long id;

    @Column(name = "PRODUCT_NO")
    private String productNo;

    @Column(name = "QUANTITY")
    private int quantity;

    @Builder
    public Stock(String productNo, int quantity) {
        this.productNo = productNo;
        this.quantity = quantity;
    }

    public static Stock create(String productNo, int quantity) {
        return Stock.builder()
                .productNo(productNo)
                .quantity(quantity)
                .build();
    }

    public boolean isQuantityLessThan(int quantity) {
        return this.quantity < quantity;
    }

    public void deductQuantity(int quantity) {
        if(isQuantityLessThan(quantity)) {
            throw new IllegalArgumentException("차감할 재고 수량이 없습니다.");
        }

        this.quantity -= quantity;
    }
}
