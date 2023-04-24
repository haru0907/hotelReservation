package com.example.demo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "hotel")

public class Hotel {

	@Id // 主キー
	@GeneratedValue(strategy = GenerationType.IDENTITY) // DBの自動連番
	@Column(name = "hotel_code")
	private Integer hotelCode;

	@Column(name = "area")
	private String area;

	@Column(name = "name")
	private String name;

	@Column(name = "price")
	private Integer price;

	public Integer getHotelCode() {
		return hotelCode;
	}

	public void setHotelCode(Integer hotelCode) {
		this.hotelCode = hotelCode;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}
	

	

}
