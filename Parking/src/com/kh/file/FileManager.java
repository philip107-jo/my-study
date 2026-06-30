package com.kh.file;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.kh.model.vo.Car;

public class FileManager {
    private final String FILE_NAME = "parking_data.txt";

    public void save(Car[] cars, int count) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (int i = 0; i < count; i++) {
                Car car = cars[i];
                // 쉼표(,) 구분자로 데이터를 한 줄씩 저장
                bw.write(car.getCarNumber() + "," +
                         car.getOwnerName() + "," +
                         car.getPhone() + "," +
                         car.getEntryTime());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("[오류] 파일 저장 실패: " + e.getMessage());
        }
    }

    public Car[] load() {
        Car[] loadedCars = new Car[20];
        File file = new File(FILE_NAME);
        
        if (!file.exists()) {
            return loadedCars; // 파일이 없으면 빈 배열 반환
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int idx = 0;
            while ((line = br.readLine()) != null && idx < 20) {
                String[] tokens = line.split(",");
                if (tokens.length == 4) {
                    loadedCars[idx] = new Car(tokens[0], tokens[1], tokens[2], tokens[3]);
                    idx++;
                }
            }
        } catch (IOException e) {
            System.out.println("[오류] 파일 로드 실패: " + e.getMessage());
        }
        return loadedCars;
    }
}