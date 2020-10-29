package edu.pasudo123.study.common.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Container {

    private Long currentNumber;
    private String name;
    private Integer hh;
    private Integer mm;
    private Integer ss;

    @Builder
    public Container(final Long currentNumber,
                     final String name,
                     final Integer hh,
                     final Integer mm,
                     final Integer ss) {
        this.currentNumber = currentNumber;
        this.name = name;
        this.hh = hh;
        this.mm = mm;
        this.ss = ss;
    }
}
