package sample.cafekiosk.learning;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.Collection;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

public class GuavaLearningTest {

    @Test
    @DisplayName("주어진 개수만큼 List를 파티셔닝한다.")
    void partitionLeaningTest1() {
        // Given
        List<Integer> integers = List.of(1, 2, 3, 4, 5, 6);

        // When
        List<List<Integer>> partition = Lists.partition(integers, 3);

        // Then
        assertThat(partition).hasSize(2)
                .isEqualTo(List.of(
                        List.of(1, 2, 3)
                        , List.of(4, 5, 6)
                ));
    }

    @Test
    @DisplayName("주어진 개수만큼 List를 파티셔닝한다.")
    void partitionLeaningTest2() {
        // Given
        List<Integer> integers = List.of(1, 2, 3, 4, 5, 6);

        // When
        List<List<Integer>> partition = Lists.partition(integers, 4);

        // Then
        assertThat(partition).hasSize(2)
                .isEqualTo(List.of(
                        List.of(1, 2, 3, 4)
                        , List.of(5, 6)
                ));
    }

    @Test
    @DisplayName("멀티맵 기능 확인")
    void multiMapLearningTest1() {
        // Given
        Multimap<String, String> multimap = ArrayListMultimap.create();
        multimap.put("커피", "아메리카노");
        multimap.put("커피", "카페라떼");
        multimap.put("커피", "카푸치노");
        multimap.put("베이커리", "크루아상");
        multimap.put("베이커리", "식빵");

        // When
        Collection<String> strings = multimap.get("커피");

        // Then
        assertThat(strings).hasSize(3)
                .isEqualTo(List.of("아메리카노", "카페라떼", "카푸치노"));
    }

    @TestFactory
    @DisplayName("멀티맵 기능 확인")
    Collection<DynamicTest> multiMapLearningTest2() {
        // Given
        Multimap<String, String> multimap = ArrayListMultimap.create();
        multimap.put("커피", "아메리카노");
        multimap.put("커피", "카페라떼");
        multimap.put("커피", "카푸치노");
        multimap.put("베이커리", "크루아상");
        multimap.put("베이커리", "식빵");

        return List.of(
                DynamicTest.dynamicTest("1개 value 삭제", () -> {
                    // When
                    multimap.remove("커피", "카푸치노");

                    // Then
                    Collection<String> result = multimap.get("커피");
                    assertThat(result).hasSize(2)
                            .isEqualTo(List.of("아메리카노", "카페라떼", "카푸치노"));
                })
                , DynamicTest.dynamicTest("1개 Key 삭제", () -> {
                    // When
                    multimap.removeAll("커피");

                    // Then
                    Collection<String> result = multimap.get("커피");
                    assertThat(result).isEmpty();
                })
        );
    }
}
