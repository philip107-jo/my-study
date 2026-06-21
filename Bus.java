package com.kh;
public class Bus{
    private String busNumber;
    private int speed;
    private int gear;
    private String currentStop;
    private boolean isStopped;

    public Bus(String busNumber){
        this.busNumber = busNumber;
        this.speed = 0;
        this.gear = 1;
        this.currentStop = "차고지";
        this.isStopped = true;
    }
    public void leaveStop(){
       if(this.isStopped) {
           this.isStopped = false;
           System.out.println(this.busNumber + "버스" + this.currentStop + "를 떠나 주행 시작합니다.");
           this.currentStop = "주행중";
       }
           else{
               System.out.println("이미 주행중인 버스입니다!");
           }
    }
    public void accelerate() {
        if (this.isStopped) {
            System.out.println(" 경고: 정류장에 정차 중일 때는 가속할 수 없습니다. 먼저 출발 하세요.");
            return;
        }

        this.speed += 25; // 속도 변화를 쉽게 보기 위해 한 번에 25씩 증가
        System.out.print("가속! 현재 속도: " + this.speed + "km/h -> ");

        // 속도가 변했으므로 기어 자동 업데이트 호출
        updateGear();
    }

    // [기능 3] 속도 감속 (정차를 위해 필요한 기능 추가)
    public void decelerate() {
        if (this.isStopped) {
            System.out.println(" 이미 정차 중입니다.");
            return;
        }

        this.speed -= 25;
        if (this.speed < 0) {
            this.speed = 0;
        }
        System.out.print("감속! 현재 속도: " + this.speed + "km/h -> ");

        // 속도가 변했으므로 기어 자동 업데이트 호출
        updateGear();
    }

    // [기능 4] 정류장 도착
    public void arriveAtStop(String stopName) {
        if (this.speed == 0) {
            this.isStopped = true;
            this.currentStop = stopName;
            System.out.println(" [" + this.busNumber + "] " + stopName + " 정류장에 정상 정차했습니다. 문이 열립니다.");
        } else {
            System.out.println("️ 위험: 현재 속도 " + this.speed + "km/h! 달리는 중에는 정류장에 정차할 수 없습니다. 속도를 줄이세요!");
        }
    }

    // [기능 5] 기어 자동 변속 내부 로직 (외부에서 호출 못 하도록 private 설정)
    private void updateGear() {
        if (this.speed <= 20) {
            this.gear = 1;
        } else if (this.speed <= 40) {
            this.gear = 2;
        } else if (this.speed <= 60) {
            this.gear = 3;
        } else if (this.speed <= 80) {
            this.gear = 4;
        } else {
            this.gear = 5;
        }
        System.out.println("[현재 기어: " + this.gear + "단]");
    }
}
