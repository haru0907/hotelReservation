package com.example.demo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name = "favorite")
public class Favorite {
	@Id // 主キー
	@GeneratedValue(strategy = GenerationType.IDENTITY) // DBの自動連番
	@Column(name = "login_code")
	private Integer loginCode;
	@Column(name = "area")
	private String area;

	@Column(name = "hotel_name")
	private String hotelName;

	@Column(name = "price")
	private Integer price;

	
	//コンスラクト
	
	public Favorite(Integer loginCode, String area, String hotelName, Integer price) {
		super();
		this.loginCode = loginCode;
		this.area = area;
		this.hotelName = hotelName;
		this.price = price;
	}
	public Favorite( String area, String hotelName, Integer price) {
		super();
		this.area = area;
		this.hotelName = hotelName;
		this.price = price;
	}
	public Favorite() {
		super();
	
	}

	//ゲッターセッター
	public Integer getLoginCode() {
		return loginCode;
	}

	public void setLoginCode(Integer loginCode) {
		this.loginCode = loginCode;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getHotelName() {
		return hotelName;
	}

	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}
	
	
}
