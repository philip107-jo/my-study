package com.kh;

public class Run {
    public static void main(String[] args) {
        Bus myBus = new Bus("7111번");
        System.out.println("=== 버스 운행 시뮬레이션 시작 ===\n");

        // 2. 정차 상태에서 가속 시도 (실패해야 함)
        myBus.accelerate();

        // 3. 정류장 출발
        myBus.leaveStop();

        // 4. 순차적 가속 및 기어 변속 확인
        myBus.accelerate(); // 25km/h (2단)
        myBus.accelerate(); // 50km/h (3단)
        myBus.accelerate(); // 75km/h (4단)
        myBus.accelerate(); // 100km/h (5단)

        // 5. 달리는 도중에 정류장 정차 시도 (실패해야 함)
        System.out.println();
        myBus.arriveAtStop("강남역");

        // 6. 감속 후 안전하게 정류장 정차
        System.out.println("\n=== 정류장 접근 중 ===");
        myBus.decelerate(); // 75km/h (4단)
        myBus.decelerate(); // 50km/h (3단)
        myBus.decelerate(); // 25km/h (2단)
        myBus.decelerate(); // 0km/h (1단)

        System.out.println();
        myBus.arriveAtStop("강남역");
    }
}
