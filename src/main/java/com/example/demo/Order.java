package com.example.demo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {
	@Id // 主キー
	@GeneratedValue(strategy = GenerationType.IDENTITY) // DBの自動連番
	@Column(name = "order_code")
	private Integer orderCode;

	@Column(name = "id")
	private Integer id;

	@Column(name = "hotel_code")
	private Integer hotelCode;
	
	@Column(name = "check_day")
	private String checkDay;

	@Column(name = "check_time")
	private String checkTime;

	public Integer getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(Integer orderCode) {
		this.orderCode = orderCode;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getHotelCode() {
		return hotelCode;
	}
	

	public String getCheckDay() {
		return checkDay;
	}

	public void setCheckDay(String checkDay) {
		this.checkDay = checkDay;
	}

	public String getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(String checkTime) {
		this.checkTime = checkTime;
	}

	public void setHotelCode(Integer hotelCode) {
		this.hotelCode = hotelCode;
	}

	public Order(Integer orderCode, Integer id, Integer hotelCode) {
		super();
		this.orderCode = orderCode;
		this.id = id;
		this.hotelCode = hotelCode;
	}

	public Order(Integer id, Integer hotelCode) {
		super();
		this.id = id;
		this.hotelCode = hotelCode;
	}

	public Order(String checkDay, String checkTime) {
		super();
		this.checkDay = checkDay;
		this.checkTime = checkTime;
	}
	public Order() {
		super();
		
	}

	public Order(Integer id, Integer hotelCode, String checkDay, String checkTime) {
		super();
		this.id = id;
		this.hotelCode = hotelCode;
		this.checkDay = checkDay;
		this.checkTime = checkTime;
	}
	
	
	
	
	

	

}
