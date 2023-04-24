package com.example.demo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "coupon")
public class Coupon {
	@Id // 主キー
	@GeneratedValue(strategy = GenerationType.IDENTITY) // DBの自動連番
	@Column(name = "coupon_no")
	private Integer couponNo;

	@Column(name = "value")
	private Float value;

	@Column(name = "name")
	private String name;
	
	public Coupon(Integer couponNo, Float value) {
		super();
		this.couponNo = couponNo;
		this.value = value;
	}
	

	public Coupon(Float value) {
		super();
		this.value = value;
	}

	public Coupon() {
		super();
		
	}

	public Integer getCouponNo() {
		return couponNo;
	}

	public void setCouponNo(Integer couponNo) {
		this.couponNo = couponNo;
	}

	public Float getValue() {
		return value;
	}

	public void setValue(Float value) {
		this.value = value;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}
	
	

}
