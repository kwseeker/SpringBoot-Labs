package top.kwseeker.spring.basic.beans.factorybean.pojo;

public class Car {

    private String make;
    private Integer year;

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "Car{" +
                "make='" + make + '\'' +
                ", year=" + year +
                '}';
    }

    public static class CarBuilder {

        private String make;
        private Integer year;

        public static CarBuilder car() {
            return new CarBuilder();
        }

        public Car factory() {
            Car car = new Car();
            car.setMake(make);
            car.setYear(year);
            return car;
        }

        public String getMake() {
            return make;
        }

        public void setMake(String make) {
            this.make = make;
        }

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }
    }
}
