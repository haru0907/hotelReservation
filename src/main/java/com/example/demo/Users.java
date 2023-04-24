package com.example.demo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class Users {
	@Id // 主キー
	@GeneratedValue(strategy = GenerationType.IDENTITY) // DBの自動連番
	@Column(name = "id")
	private Integer id;
	@Column(name = "name")
	private String name;
	
	@Column(name = "phone")
	private String phone;

	@Column(name = "email")
	private String email;
	
	@Column(name = "password")
	private String password;
	@Column(name = "coupon_code")
	private Integer couponCode;

	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getCouponCode() {
		return couponCode;
	}
	public void setCouponCode(Integer couponCode) {
		this.couponCode = couponCode;
	}

	public Users(String name, String phone, String email, String password, Integer couponCode) {
		super();
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.password = password;
		this.couponCode = couponCode;
	}
	public Users() {
		super();
	
	}
	public Users(Integer id) {
		super();
		this.id = id;
	}
	
	
	
	
	

}
