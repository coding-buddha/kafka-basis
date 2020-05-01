package edu.pasudo123.study.kafka.config.pojo;

import edu.pasudo123.study.producer.pojo.NameGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

// https://mkyong.com/spring-boot/spring-boot-junit-5-mockito/
@SpringBootTest(classes = {NameGenerator.class})
@DisplayName("이름 생성기는")
class NameGeneratorTest {

    @Autowired
    private NameGenerator nameGenerator;

    @RepeatedTest(10)
    @DisplayName("이름을 획득한다.")
    void getName() {
        final String name = nameGenerator.getName();

        System.out.println(name);

        assertThat(name.length()).isSameAs(8);
    }
}