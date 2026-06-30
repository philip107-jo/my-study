package com.kh.model.vo;

public class Car {

	private String carNumber;	// 차량번호
	private String ownerName;	// 차주명
	private String phone;		// 연락처
	private String entryTime;	// 입차시간

	public Car() {
	}

	public Car(String carNumber, String ownerName, String phone, String entryTime) {
		this.carNumber = carNumber;
		this.ownerName = ownerName;
		this.phone = phone;
		this.entryTime = entryTime;
	}

	public String getCarNumber() {
		return carNumber;
	}

	public void setCarNumber(String carNumber) {
		this.carNumber = carNumber;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEntryTime() {
		return entryTime;
	}

	public void setEntryTime(String entryTime) {
		this.entryTime = entryTime;
	}

	@Override
	public String toString() {
		return "차량번호 : " + carNumber
			 + ", 차주명 : " + ownerName
			 + ", 연락처 : " + phone
			 + ", 입차시간 : " + entryTime;
	}
}