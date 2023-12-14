package sample.cafekiosk.unit.order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sample.cafekiosk.unit.drink.Beverage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Order {

    private final UUID uuid;
    private final LocalDateTime orderDateTime;
    private final Map<String, Beverage> beverages;
}
