package sample.cafekiosk.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * @WebMvcTest로 테스트 진행 시 @EnableJpaAuditing도 같이 실행되지만 JpaAuditing 관련 Bean은 등록되지 않는 상태이기 때문에 오류 발생하며,
 * 이를 해결하기 위해 별도의 Config 파일을 만들어 설정한다.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
