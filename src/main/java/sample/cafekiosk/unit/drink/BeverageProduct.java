package sample.cafekiosk.unit.drink;

public enum BeverageProduct {

    AMERICANO("아메리카노", 4000)
    , LATTE("라떼", 5000);

    private final String beverageName;
    private final int beveragePrice;

    BeverageProduct(String beverageName, int beveragePrice) {
        this.beverageName = beverageName;
        this.beveragePrice = beveragePrice;
    }

    public String getBeverageName() {
        return this.beverageName;
    }

    public int getBeveragePrice() {
        return this.beveragePrice;
    }
}
