# kafka-basis
> 단순히 카프카를 실습하기 위함도 있지만, 카프카 컨슈머 애플리케이션의 속성을 변화함에 따른 컨슘 관련 성능을 측정하기 위함

## 궁금증
__카프카 컨슈머 만들 때 두가지 종류로 만드는 예제가 있던데, 둘의 차이점이 무엇일까?__
```java
@Bean
public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Container>> kafkaListenerContainerFactory(){
    ConcurrentKafkaListenerContainerFactory<String, Container> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    return factory;
}

@Bean
public ConcurrentKafkaListenerContainerFactory<String, Container> kafkaListenerContainerFactory(){
    ConcurrentKafkaListenerContainerFactory<String, Container> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    return factory;
}
```