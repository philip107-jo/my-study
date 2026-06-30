package com.kh.view;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import com.kh.controller.ParkingController;
import com.kh.model.vo.Car;

public class ParkingView {
    private Scanner sc;
    private ParkingController pc;

    public ParkingView() {
        sc = new Scanner(System.in);
        pc = new ParkingController();
    }

    public void mainMenu() {
        pc.loadFile();
        printMessage("==========================================\n");
        printMessage("        주차장 관리 프로그램\n");
        printMessage("==========================================\n");
        printMessage("데이터를 불러왔습니다.\n\n");

        while (true) {
            int currentCars = pc.getCount();
            int maxCapacity = 20;
            int remainingSpots = maxCapacity - currentCars;

            System.out.println("현재 주차 차량 : " + currentCars + "대");
            System.out.println("남은 자리 : " + remainingSpots + "대\n");
            System.out.println("1. 차량 등록");
            System.out.println("2. 차량 조회");
            System.out.println("3. 차량 정보 수정");
            System.out.println("4. 차량 출차");
            System.out.println("5. 전체 차량 조회");
            System.out.println("6. 종료\n");
            System.out.print("메뉴 선택 > ");
            
            int menu = -1;
            try {
                menu = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                printMessage("숫자로 메뉴를 선택해주세요.\n\n");
                continue;
            }

            switch (menu) {
                case 1:
                    registerCar(maxCapacity);
                    break;
                case 2:
                    searchCar();
                    break;
                case 3:
                    modifyCar();
                    break;
                case 4:
                    exitCar();
                    break;
                case 5:
                    printAllCars(pc.getCars(), pc.getCount()); // getCars()로 호출
                    break;
                case 6:
                    System.out.println("\n데이터를 저장합니다...");
                    pc.saveFile();
                    System.out.println("저장이 완료되었습니다.");
                    System.out.println("프로그램을 종료합니다.");
                    return;
                default:
                    printMessage("잘못된 메뉴 선택입니다.\n\n");
            }
        }
    }

    // 2. UML에 명시된 inputCar() 기능을 정상 구현
    public Car inputCar() {
        String carNumber = inputCarNumber();
        System.out.print("차주명 : ");
        String ownerName = sc.nextLine();
        System.out.print("전화번호 : ");
        String phone = sc.nextLine();
        
        // 테스트용 초 단위 포함 자동 시간 등록
        String entryTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.println("\n입차시간 : " + entryTime + " (자동 등록)");
        
        return new Car(carNumber, ownerName, phone, entryTime);
    }

    private void registerCar(int maxCapacity) {
        if (pc.getCount() >= maxCapacity) {
            printMessage("주차장이 가득 찼습니다.\n\n");
            return;
        }

        System.out.println("\n====== 차량 등록 ======");
        Car car = inputCar(); // inputCar() 메서드 활용

        // 4. 중복 체크를 View에서 하지 않고, Controller의 addCar 결과를 받아 판별
        if (pc.addCar(car)) {
            printMessage("차량이 등록되었습니다.\n\n");
        } else {
            // 이미 존재하거나 등록 실패 시
            printMessage("이미 등록된 차량이거나 등록에 실패했습니다.\n\n");
        }
    }

    private void searchCar() {
        System.out.println("\n조회할 차량번호 : ");
        String carNumber = sc.nextLine();
        Car car = pc.searchCar(carNumber);

        if (car != null) {
            printCar(car);
        } else {
            printMessage("등록된 차량이 없습니다.\n\n");
        }
    }

    private void modifyCar() {
        System.out.println("\n수정할 차량번호 : ");
        String carNumber = sc.nextLine();
        
        if (pc.searchCar(carNumber) == null) {
            printMessage("존재하지 않는 차량입니다.\n\n");
            return;
        }

        System.out.println("새 차주명 : ");
        String ownerName = sc.nextLine();
        System.out.println("새 전화번호 : ");
        String phone = sc.nextLine();

        if (pc.updateCar(carNumber, ownerName, phone)) {
            printMessage("수정이 완료되었습니다.\n\n");
        }
    }

    // 1. 계산 로직이 전부 빠져나가 아주 슬림해진 출차 View
    private void exitCar() {
        System.out.println("\n출차할 차량번호 : ");
        String carNumber = sc.nextLine();
        
        Car car = pc.searchCar(carNumber);
        if (car == null) {
            printMessage("존재하지 않는 차량입니다.\n\n");
            return;
        }

        String entryTime = car.getEntryTime();
        
        // Controller에게 계산 및 삭제 비즈니스 로직 요청
        long[] receipt = pc.processReceipt(carNumber);

        if (receipt != null) {
            String exitTimeStr = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            System.out.println("\n=====================");
            System.out.println("입차시간 : " + entryTime);
            System.out.println("출차시간 : " + exitTimeStr);
            System.out.println("\n총 이용시간");
            System.out.println(receipt[0] + "분 " + receipt[1] + "초");
            System.out.println("\n주차요금");
            System.out.println(receipt[2] + "원");
            System.out.println("=====================");
            printMessage("출차 완료되었습니다.\n\n");
        }
    }

    public String inputCarNumber() {
        System.out.print("차량번호 : ");
        return sc.nextLine();
    }

    public void printCar(Car car) {
        System.out.println("=====================");
        System.out.println("차량번호 : " + car.getCarNumber());
        System.out.println("차주명 : " + car.getOwnerName());
        System.out.println("전화번호 : " + car.getPhone());
        System.out.println("입차시간 : " + car.getEntryTime());
        System.out.println("=====================\n");
    }

    public void printAllCars(Car[] cars, int count) {
        if (count == 0) {
            System.out.println("현재 주차된 차량이 없습니다.\n");
            return;
        }
        System.out.println("========== 차량 목록 ==========");
        for (int i = 0; i < count; i++) {
            System.out.println("[" + (i + 1) + "]\n");
            System.out.println("차량번호 : " + cars[i].getCarNumber());
            System.out.println("차주명 : " + cars[i].getOwnerName());
            System.out.println("전화번호 : " + cars[i].getPhone());
            System.out.println("입차시간 : " + cars[i].getEntryTime());
            System.out.println("--------------------------");
        }
        System.out.println("===========================\n");
    }

    public void printMessage(String message) {
        System.out.print(message);
    }
}