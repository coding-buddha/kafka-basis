package edu.pasudo123.study.producer.pojo;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class NameGenerator {
    private final Random random;
    private final List<String> alphabet;

    public NameGenerator(){
        random = new Random();
        alphabet = IntStream.rangeClosed('a', 'z')
                .mapToObj(number -> String.valueOf((char)number))
                .collect(Collectors.toList());
    }

    public String getName() {
        return random.ints(8, 0, 'z' - 'a')
                .mapToObj(alphabet::get)
                .collect(Collectors.joining());
    }
}
