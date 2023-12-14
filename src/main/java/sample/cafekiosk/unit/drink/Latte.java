package sample.cafekiosk.unit.drink;

public class Latte extends Beverage {

    public Latte() {
        super(BeverageProduct.LATTE);
    }

    public Latte(int count) {
        super(BeverageProduct.LATTE, count);
    }
}
