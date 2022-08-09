package org.example;

public class Main {
    public static void main(String[] args) {
        Car car = new Car("Ford", 5, CarType.SPORT);

        CarDto carDto = CarMapper.INSTANCE.carToCarDto(car);
        System.out.println(carDto);

        Car newCar = CarMapper.INSTANCE.carDtoToCar(carDto);
        System.out.println(newCar);
        System.out.println(newCar.equals(car));
    }
}