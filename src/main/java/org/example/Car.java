package org.example;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = {"make", "numberOfSeats"})
public class Car {
    private String make;
    private int numberOfSeats;
    private CarType type;
}
