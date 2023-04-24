package com.example.demo;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CartController {
	@Autowired
	HttpSession session;
	@Autowired
	HotelRepository hotelRepository;
	@Autowired
	FavoriteRepository favoriteRepository;

	/**
	 * カート内容を表示
	 */
	@RequestMapping("/favorite")
	public ModelAndView cartList(ModelAndView mv) {
		Cart favorite = getFavoriteFromSession();

		mv.addObject("favorite", favorite.getFavorite());

		mv.setViewName("favorite");
		return mv;
	}

	/**
	 * 指定した商品をカートに追加。
	 */
	@RequestMapping("/favorite/add/{hotelsCode}")
	public ModelAndView addFavorite(@PathVariable(name = "hotelsCode") Integer hotelsCode, ModelAndView mv) {
		// セッションスコープからカート情報を取得する
		Cart favorite = getFavoriteFromSession();
		// 商品コードをキーに商品情報を取得し、カートに追加する
		Hotel hotel = hotelRepository.findById(hotelsCode).get();
		
//		//LoginID
//		Integer id = (Integer)session.getAttribute("loginCode");
//		//登録
//		Favorite fav = new Favorite(id,hotel.getArea(),hotel.getHotelName(),hotel.getPrice());
//		favoriteRepository.saveAndFlush(fav);		
		favorite.addFavorite(hotel);
		mv.addObject("favorite", favorite.getFavorite());
		mv.setViewName("favorite");
		return mv;
	}

	/**
	 * 指定した商品をカートから削除
	 */
	@RequestMapping("/favorite/delete/{hotelsCode}")
	public ModelAndView deleteFavorite(@PathVariable(name = "hotelsCode") Integer hotelsCode, ModelAndView mv) {
		// セッションスコープからカート情報を取得する
		Cart favorite = getFavoriteFromSession();

		// カート情報から削除
		favorite.deleteFavorite(hotelsCode);
		mv.addObject("favorite", favorite.getFavorite());
		mv.setViewName("favorite");
		return mv;
	}

	/**
	 * セッションスコープからカート情報を取得。
	 * カートが存在しない場合は、セッションスコープに追加した上で空のカート情報を返却。
	 */
	private Cart getFavoriteFromSession() {
		//セッションスコープからカート情報を取得。
		Cart favorite = (Cart) session.getAttribute("favorite");
		//空だった場合HashMap作成
		if (favorite == null) {
			favorite = new Cart();
			session.setAttribute("favorite", favorite);
		}
		return favorite;
	}

}
