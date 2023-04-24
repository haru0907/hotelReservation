package com.example.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Integer> {
	//全商品を表示
	List<Hotel> findAllByOrderByHotelCode();
	//値段昇順、降順
	List<Hotel> findAllByOrderByPrice();
	List<Hotel> findAllByOrderByPriceDesc();
	
	List<Hotel> findByPriceLessThanEqual(int price);
	List<Hotel> findByPriceGreaterThanEqual(int price);
	List<Hotel> findByPriceBetween(int minPrice, int maxPrice);
	
	List<Hotel> findByPriceLessThanEqualOrderByPrice(int price);
	List<Hotel> findByPriceGreaterThanEqualOrderByPrice(int price);
	
	List<Hotel> findByPriceBetweenOrderByPrice(int minPrice, int maxPrice);
	
	List<Hotel> findByPriceLessThanEqualOrderByPriceDesc(int price);
	
	
	//デスクの処理
	// select * from item where price <= ? order by price desc
	List<Hotel> findByPriceGreaterThanEqualOrderByPriceDesc(int price);
	// select * from item where price between ? and ? order by price desc
	List<Hotel> findByPriceBetweenOrderByPriceDesc(int minPrice, int maxPrice);
	
	//ホテル名、エリア検索
	List<Hotel> findByNameLikeOrAreaLike(String  hotelsName, String area);
	
	
	//ストリームapiのフィルターで条件を指定
	
	//ホテル名と価格を指定して価格の安い順に並び替え
//	List<Hotel> findByNameLikeOrAreaLikeAndOrderByPriceDesc(String  hotelsName, String area);
	
	
	
//	List<Hotel> findByHotelNameLikeOrAreaLikeAndByPriceLessThanEqual(String  hotelsName, String area,int price);
}