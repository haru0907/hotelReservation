package com.example.demo;

import java.util.HashMap;
import java.util.Map;

public class Cart {
	//カート作成
	//名前を付けれる配列
	//番号ふってる
	private Map<Integer, Hotel> favorite = new HashMap<>();

	public Map<Integer, Hotel> getFavorite() {
		return favorite;

	}

	//privateだから10行目favoriteをいじれない
	public void addFavorite(Hotel hotel) {
		//ホテルコードをfavoriteに追加
		favorite.put(hotel.getHotelCode(), hotel);

	}

	public void deleteFavorite(int hotelCode) {
		favorite.remove(hotelCode);

	}


}
