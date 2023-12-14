package sample.cafekiosk.unit.drink;

import lombok.Getter;

@Getter
public abstract class Beverage {

    private String name;   // 상품명
    private int price;     // 가격
    private int count;     // 구매 수량

    public Beverage(BeverageProduct beverageProduct) {
        this.name = beverageProduct.getBeverageName();
        this.price = beverageProduct.getBeveragePrice();
        this.count = 1;
    }

    public Beverage(BeverageProduct beverageProduct, int count) {
        this.name = beverageProduct.getBeverageName();
        this.price = beverageProduct.getBeveragePrice();
        this.count = count;
    }
}
