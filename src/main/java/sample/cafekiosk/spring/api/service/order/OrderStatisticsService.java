package sample.cafekiosk.spring.api.service.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sample.cafekiosk.spring.api.service.mail.MailService;
import sample.cafekiosk.spring.domain.order.Order;
import sample.cafekiosk.spring.domain.order.OrderStatus;
import sample.cafekiosk.spring.repository.OrderRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderStatisticsService {

    private final OrderRepository orderRepository;
    private final MailService mailService;

    public boolean sendOrderStatisticsMail(LocalDate orderDate, String email) {
        // 해당 일자에 결제 완료된 주문 조회
        List<Order> orders = orderRepository.findOrdersBy(
                // 주문 등록시간을 기준으로
                orderDate.atStartOfDay()
                , orderDate.plusDays(1).atStartOfDay()
                , OrderStatus.PAYMENT_COMPLETED
        );

        // 총 매출 합계를 계산
        int totalAmount = orders.stream()
                .mapToInt(Order::getTotalPrice)
                .sum();

        /**
         * 메일 전송
         * - 메일은 외부 네트워크 대역으로 테스트 어려움이 발생하게 된다.
         * - 외부 네트워크에 존재하는 의존성을 바탕으로 실행하는 것이 아니라 Mock을 생성하여 테스트 가능하다.
         */
        boolean result = mailService.sendMail(
                "no-reply@cafekiosk.com"
                , email
                , String.format("[매출통계] %s", orderDate)
                , String.format("총 매출 합계는 %s원 입니다.", totalAmount)
        );

        if (!result) {
            throw new IllegalArgumentException("매출 통계 메일 전송에 실패했습니다.");
        }

        return true;
    }
}
