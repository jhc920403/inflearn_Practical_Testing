package sample.cafekiosk.unit.drink;

public class Americano extends Beverage {

    public Americano() {
        super(BeverageProduct.AMERICANO);
    }

    public Americano(int count) {
        super(BeverageProduct.AMERICANO, count);
    }
}
