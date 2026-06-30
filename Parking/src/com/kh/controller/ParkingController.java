package com.kh.controller;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.kh.file.FileManager;
import com.kh.model.vo.Car;

public class ParkingController {
    private Car[] cars=new Car[20];;
    private int count=0;;
    private FileManager fm=new FileManager();
    
    
    
    // 4. addCar 내부에서 중복 체크 수행
    public boolean addCar(Car car) {
        if (count >= cars.length) {
            return false; // 주차장 만차
        }
        // 중복 차량 검사
        if (searchCar(car.getCarNumber()) != null) {
            return false; // 이미 존재하는 차량번호
        }
        
        cars[count] = car;
        count++;
        return true;
    }

    public Car searchCar(String carNumber) {
        for (int i = 0; i < count; i++) {
            if (cars[i].getCarNumber().equals(carNumber)) {
                return cars[i];
            }
        }
        return null;
    }

    public boolean updateCar(String carNumber, String ownerName, String phone) {
        Car car = searchCar(carNumber);
        if (car != null) {
            car.setOwnerName(ownerName);
            car.setPhone(phone);
            return true;
        }
        return false;
    }

    // 1. View에서 하던 시간/요금 계산을 Controller로 이동 (결과를 담아줄 DTO나 배열 대신 View에 전송할 수 있도록 처리)
    // 출차 성공 시 계산된 [이용시간 분, 이용시간 초, 요금]을 반환하고 배열에서 삭제합니다.
    public long[] processReceipt(String carNumber) {
        Car car = searchCar(carNumber);
        if (car == null) return null;

        String entryTimeStr = car.getEntryTime();
        if (entryTimeStr.length() == 5) entryTimeStr += ":00"; // 초 단위 예외 처리

        String exitTimeStr = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime entry = LocalTime.parse(entryTimeStr, formatter);
        LocalTime exit = LocalTime.parse(exitTimeStr, formatter);
        
        long seconds = Duration.between(entry, exit).toSeconds();
        if (seconds < 0) seconds += 24 * 60 * 60; // 자정 처리

        long minutes = seconds / 60;
        long remSeconds = seconds % 60;
        long fee = (seconds / 10) * 100; // 10초당 100원 계산

        // 출차 확정 및 배열에서 제거
        deleteCar(carNumber);

        // 결과 반환 [분, 초, 요금]
        return new long[]{minutes, remSeconds, fee};
    }

    public boolean deleteCar(String carNumber) {
        int index = -1;
        for (int i = 0; i < count; i++) {
            if (cars[i].getCarNumber().equals(carNumber)) {
                index = i;
                break;
            }
        }
        if (index == -1) return false;

        for (int i = index; i < count - 1; i++) {
            cars[i] = cars[i + 1];
        }
        cars[count - 1] = null;
        count--;
        return true;
    }

    // 3. 이름 변경: getAllCars() -> getCars()
    public Car[] getCars() {
        return cars;
    }

    public int getCount() {
        return count;
    }

    public void saveFile() {
        fm.save(cars, count);
    }

    public void loadFile() {
        Car[] loadedCars = fm.load();
        if (loadedCars != null) {
            this.cars = loadedCars;
            this.count = 0;
            for (Car car : cars) {
                if (car != null) this.count++;
            }
        }
    }
}