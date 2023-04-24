package com.example.demo;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HotelController {
	@Autowired
	HttpSession session;
	@Autowired
	HotelRepository hotelRepository;
	@Autowired
	OrderRepository orderRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	CouponRepository couponRepository;
	// 予約画面出力
	@RequestMapping(value = "/hotels/{hotelsCode}/reserve", method = RequestMethod.GET)
	public ModelAndView reserve(@PathVariable("hotelsCode") Integer hotelsCode, ModelAndView mv) {
		//コードに対応したホテルをセッションに登録
		Hotel hotel = hotelRepository.findById(hotelsCode).get();
		session.setAttribute("hotel", hotel);
		/*
		 * なんだこれ同じことしてね？？
		 */
		session.setAttribute("hotelCode", hotel.getHotelCode());	
		//userが持っているクーポン情報を取得	
		Integer user=(Integer) session.getAttribute("id");	
		Users users =userRepository.findById(user).get();
		//クーポン情報取得
		Coupon coupon = couponRepository.getById(users.getCouponCode()); 	
		mv.addObject("userCoupon",coupon);	 	
		mv.setViewName("rsvInput");
		return mv;
	}

	// 予約内容確認画面出力
	@RequestMapping(value = "/hotels/confirm", method = RequestMethod.POST)
	public ModelAndView reserveFinish(
			@RequestParam("date") String date,
			@RequestParam("time") String time,
			@RequestParam(name="couponUse",defaultValue = "2") Integer couponUse,ModelAndView mv) {
		//orders情報をセッションに登録
		session.setAttribute("date", date);
		session.setAttribute("time", time);		
		//usersが保持しているクーポン(更新された方)情報取得	
		Integer user=(Integer) session.getAttribute("id");	
		Users users =userRepository.findById(user).get();
		//usersが所持するCouponの行を取得
		Coupon coupon = couponRepository.getById(users.getCouponCode());
		//hotel情報取得
		Hotel hotel =(Hotel)session.getAttribute("hotel"); 
		//hotelからhotelPriceを取得
		Integer hotelPirce = hotel.getPrice();	
		/*
		 *クーポン計算処理
		 * */
		//計算結果入れ物
		Integer price = 0;
		//使用の時は割引計算
		if(couponUse==1) {
			price=(int) (coupon.getValue() * hotelPirce);
		}else {
			price=hotelPirce;
		}
		session.setAttribute("price", price);
		mv.addObject("price",price);
		mv.setViewName("confirm");
		return mv;
	}

	// 予約ボタン押下時
	// 予約完了画面出力&顧客情報をDBに登録&クーポン配布
	@RequestMapping(value = "/hotels/reservefinish")
	public ModelAndView confirm(ModelAndView mv) {
		Order order = new Order(
				(Integer)session.getAttribute("id"), 
				(Integer)session.getAttribute("hotelCode"),
				(String)session.getAttribute("date"),
				(String)session.getAttribute("time")
				);
		//orders情報をDBに登録
		orderRepository.saveAndFlush(order);
		//userPK取得
		Users users =userRepository.findById(order.getId()).get();	
		mv.addObject("users", userRepository.findById(order.getId()).get());
		//ordersPK取得
		mv.addObject("orders", orderRepository.findById(order.getOrderCode()).get());
		//hotelPK取得
		mv.addObject("hotels", hotelRepository.findById(order.getHotelCode()).get());
		couponGatya(users);
		//ユーザーが新たに獲得したクーポンを検索
		Coupon coupon =couponRepository.getById(users.getCouponCode());
		//ユーザーが新たに獲得したクーポンを表示
		mv.addObject("coupon",coupon.getName());
		//mv.addObject("users",userRepository.findById(order.getOrderCode()).get());
		//違いnullが帰ってきそうな時はfindbyIdはnullチェックしてくれる
		//100％nullじゃないときはgetByIdでok	
		//mv.addObject("users", userRepository.getById(order.getOrderCode()));
		mv.setViewName("completed");
		return mv;
	}
	//クーポンガチャ
	public void couponGatya(Users users){
		Random rand = new Random();
		int num = rand.nextInt(10);
		//100%offクーポン配布
		if(num<2) {
		//usersのクーポンを1に変更
		users.setCouponCode(1);	
		//50%off クーポン配布
		}else if(num<5){
		users.setCouponCode(2);		
		//30%off クーポン配布	
		}else {
		users.setCouponCode(3);		
		}
		userRepository.saveAndFlush(users);
	}
	// TOP表示
	@RequestMapping(value = "/hotels", method = RequestMethod.GET)
	public ModelAndView hotels(
			@RequestParam(name = "sort", defaultValue = "") String sort,
			@RequestParam(name = "max_price", defaultValue = "") String maxPrice,
			@RequestParam(name = "min_price", defaultValue = "") String minPrice,
			@RequestParam(name = "keyword", defaultValue = "") String keyword,
			ModelAndView mv) {	
		System.out.printf(keyword);
		// 商品リスト
		List<Hotel> hotelList = null;
		// sortパラメータ判定
		if ("".equals(sort)) {
			// sortパラメータなし
			// 価格パラメータ判定			
			//上限、下限、曖昧検索があるとき
			if (!("".equals(maxPrice))&&!("".equals(minPrice))&&!("".equals(keyword))) {
				//曖昧で指定した条件と範囲内の金額のホテルの表示
				int bigPrice = Integer.parseInt(maxPrice);
				int smallPrice = Integer.parseInt(minPrice);
				hotelList=hotelRepository.findByNameLikeOrAreaLike( "%"+ keyword+"%", "%"+ keyword+"%");
				//ストリーム機能を使う準備。filterで条件を追加
				Stream<Hotel> stream = hotelList.stream().
						//条件、下限値の追加
						filter(hotel -> hotel.getPrice()>=smallPrice).
						//条件、上限値の追加
						filter(hotel -> hotel.getPrice()<=bigPrice);
				//終端処理（List型に合わせてる）
				hotelList = stream.toList();	
				
				//下限、曖昧検索があるとき。曖昧で指定した条件と下限の金額のホテルの表示
			} else if ("".equals(maxPrice)&&!("".equals(minPrice))&&!("".equals(keyword))) {				
				int smallPrice = Integer.parseInt(minPrice);
				hotelList=hotelRepository.findByNameLikeOrAreaLike( "%"+ keyword+"%", "%"+ keyword+"%");
				Stream<Hotel> stream = hotelList.stream().
						//条件、下限値の追加
						filter(hotel -> hotel.getPrice()>=smallPrice);
				hotelList = stream.toList();			
				
				//上限、曖昧検索があるとき。曖昧で指定した条件と上限の金額のホテルの表示
			} else if (!("".equals(maxPrice))&&("".equals(minPrice))&&!("".equals(keyword))) {
				int bigPrice = Integer.parseInt(maxPrice);
				hotelList=hotelRepository.findByNameLikeOrAreaLike( "%"+ keyword+"%", "%"+ keyword+"%");
				Stream<Hotel> stream = hotelList.stream().
						//条件、上限値の追加
						filter(hotel -> hotel.getPrice()<=bigPrice);
				hotelList = stream.toList();	
				
				//上限、下限があるとき。上限、下限を指定して検索。
			}else if (!("".equals(maxPrice))&&!("".equals(minPrice))&&("".equals(keyword))) {
				//最大値、最小値を指定して検索
				hotelList = hotelRepository.findByPriceBetween(Integer.parseInt(minPrice), Integer.parseInt(maxPrice));		
				
				//上限のみある時、上限を指定して検索
			}else if (!("".equals(maxPrice))&&("".equals(minPrice))&&("".equals(keyword))) {
				// maxPrice以下の商品を検索
				hotelList = hotelRepository.findByPriceLessThanEqual(Integer.parseInt(maxPrice));
				
				//下限のみある時、下限を指定して検索
			} else if ("".equals(maxPrice)&&!("".equals(minPrice))&&("".equals(keyword))) {
				// minPrice以上の商品を検索
				hotelList = hotelRepository.findByPriceGreaterThanEqual(Integer.parseInt(minPrice));
		
				//曖昧のみある時。曖昧の結果全表示
			} else if("".equals(maxPrice)&&("".equals(minPrice))&&!("".equals(keyword))) {
				hotelList=hotelRepository.findByNameLikeOrAreaLike("%"+ keyword+"%","%"+ keyword+"%");	
			
				//全部なし。ホテル全表示
			}else {
				// 全商品を商品コード昇順で取得
				//hotelList = hotelRepository.findByNameLikeOrAreaLike();			
				hotelList = hotelRepository.findAll();
			}		
		} else if ("price_asc".equals(sort)) {
			// 価格昇順
			// 価格パラメータ判定
			//上限、下限、曖昧検索があるとき
			if (!("".equals(maxPrice))&&!("".equals(minPrice))&&!("".equals(keyword))) {
				//曖昧で指定した条件と範囲内の金額のホテルの表示
				int bigPrice = Integer.parseInt(maxPrice);
				int smallPrice = Integer.parseInt(minPrice);
				hotelList=hotelRepository.findByNameLikeOrAreaLike( "%"+ keyword+"%", "%"+ keyword+"%");
				Stream<Hotel> stream = hotelList.stream().filter(hotel -> hotel.getPrice()>=smallPrice).
						filter(hotel -> hotel.getPrice()<=bigPrice).
						sorted(Comparator.comparing(Hotel::getPrice));
				hotelList = stream.toList();					
				//下限、曖昧検索があるとき。曖昧で指定した条件と下限の金額のホテルの表示
			} else if ("".equals(maxPrice)&&!("".equals(minPrice))&&!("".equals(keyword))) {			
				int smallPrice = Integer.parseInt(minPrice);
				hotelList=hotelRepository.findByNameLikeOrAreaLike( "%"+ keyword+"%", "%"+ keyword+"%");
				//ストリーム機能を使う準備、条件を追加
				Stream<Hotel> stream = hotelList.stream().
						filter(hotel -> hotel.getPrice()>=smallPrice).
						sorted(Comparator.comparing(Hotel::getPrice));
				hotelList = stream.toList();					
				//上限、曖昧検索があるとき。曖昧で指定した条件と上限の金額のホテルの表示
			} else if (!("".equals(maxPrice))&&("".equals(minPrice))&&!("".equals(keyword))) {
				int bigPrice = Integer.parseInt(maxPrice);
				hotelList=hotelRepository.findByNameLikeOrAreaLike( "%"+ keyword+"%", "%"+ keyword+"%");
				Stream<Hotel> stream = hotelList.stream().
						filter(hotel -> hotel.getPrice()<=bigPrice).
						sorted(Comparator.comparing(Hotel::getPrice));
				hotelList = stream.toList();					
				//上限、下限があるとき。上限、下限を指定して検索。
			}else if (!("".equals(maxPrice))&&!("".equals(minPrice))&&("".equals(keyword))) {
				//最大値、最小値を指定して検索
				hotelList = hotelRepository.findByPriceBetweenOrderByPrice(Integer.parseInt(minPrice), Integer.parseInt(maxPrice));					
				//上限のみある時、上限を指定して検索
			}else if (!("".equals(maxPrice))&&("".equals(minPrice))&&("".equals(keyword))) {
				// maxPrice以下の商品を検索
				hotelList = hotelRepository.findByPriceLessThanEqualOrderByPrice(Integer.parseInt(maxPrice));				
				//下限のみある時、下限を指定して検索
			} else if ("".equals(maxPrice)&&!("".equals(minPrice))&&("".equals(keyword))) {
				// minPrice以上の商品を検索
				hotelList = hotelRepository.findByPriceGreaterThanEqualOrderByPrice(Integer.parseInt(minPrice));		
				//曖昧のみある時。曖昧の結果全表示
			} else if("".equals(maxPrice)&&("".equals(minPrice))&&!("".equals(keyword))) {				
				hotelList=hotelRepository.findByNameLikeOrAreaLike("%"+ keyword+"%","%"+ keyword+"%");	
				Stream<Hotel> stream = hotelList.stream().
							sorted(Comparator.comparing(Hotel::getPrice));
				hotelList = stream.toList();	
			//全部なし。安い順で全表示
			}else {
				// 全商品を商品コード昇順で取得
				hotelList = hotelRepository.findAllByOrderByPrice();				
			}		
		} else if ("price_desc".equals(sort)) {
			// 価格降順
			// 価格パラメータ判定
			//上限、下限、曖昧検索があるとき
			if (!("".equals(maxPrice))&&!("".equals(minPrice))&&!("".equals(keyword))) {
				//曖昧で指定した条件と範囲内の金額のホテルの表示
				int bigPrice = Integer.parseInt(maxPrice);
				int smallPrice = Integer.parseInt(minPrice);
				hotelList=hotelRepository.findByNameLikeOrAreaLike( "%"+ keyword+"%", "%"+ keyword+"%");
				//ストリーム機能を使う準備、条件を追加
				Stream<Hotel> stream = hotelList.stream().
						filter(hotel -> hotel.getPrice()>=smallPrice).
						filter(hotel -> hotel.getPrice()<=bigPrice).
						sorted(Comparator.comparing(Hotel::getPrice).reversed());
				hotelList = stream.toList();		
				
				//下限、曖昧検索があるとき。曖昧で指定した条件と下限の金額のホテルの表示
			} else if ("".equals(maxPrice)&&!("".equals(minPrice))&&!("".equals(keyword))) {
				int smallPrice = Integer.parseInt(minPrice);
				
				
				
				hotelList=hotelRepository.findByNameLikeOrAreaLike( "%"+ keyword+"%", "%"+ keyword+"%");
				
				Stream<Hotel> stream = hotelList.stream().
						filter(hotel -> hotel.getPrice()>=smallPrice).
						sorted(Comparator.comparing(Hotel::getPrice).reversed());
						hotelList = stream.toList();	
						
						
						
				//上限、曖昧検索があるとき。曖昧で指定した条件と上限の金額のホテルの表示
			} else if (!("".equals(maxPrice))&&("".equals(minPrice))&&!("".equals(keyword))) {
				int bigPrice = Integer.parseInt(maxPrice);
				hotelList=hotelRepository.findByNameLikeOrAreaLike( "%"+ keyword+"%", "%"+ keyword+"%");
				//ストリーム機能を使う準備、条件を追加
				Stream<Hotel> stream = hotelList.stream().
						filter(hotel -> hotel.getPrice()<=bigPrice).
						sorted(Comparator.comparing(Hotel::getPrice).reversed());
				hotelList = stream.toList();					
				//上限、下限があるとき。上限、下限を指定して検索。
			}else if (!("".equals(maxPrice))&&!("".equals(minPrice))&&("".equals(keyword))) {
				//最大値、最小値を指定して検索
				hotelList = hotelRepository.findByPriceBetweenOrderByPriceDesc(Integer.parseInt(minPrice), Integer.parseInt(maxPrice));					
				//上限のみある時、上限を指定して検索
			}else if (!("".equals(maxPrice))&&("".equals(minPrice))&&("".equals(keyword))) {
				// maxPrice以下の商品を検索
				hotelList = hotelRepository.findByPriceLessThanEqualOrderByPriceDesc(Integer.parseInt(maxPrice));				
				//下限のみある時、下限を指定して検索
			} else if ("".equals(maxPrice)&&!("".equals(minPrice))&&("".equals(keyword))) {
				// minPrice以上の商品を検索
				hotelList = hotelRepository.findByPriceGreaterThanEqualOrderByPriceDesc(Integer.parseInt(minPrice));		
				//曖昧のみある時。曖昧の結果全表示
			} else if("".equals(maxPrice)&&("".equals(minPrice))&&!("".equals(keyword))) {
				hotelList=hotelRepository.findByNameLikeOrAreaLike("%"+ keyword+"%","%"+ keyword+"%");	
				Stream<Hotel> stream = hotelList.stream().
						sorted(Comparator.comparing(Hotel::getPrice).reversed());
				hotelList = stream.toList();	
			//全部なし。高い順で全表示
			}else {
				hotelList = hotelRepository.findAllByOrderByPriceDesc();			
			}
		}		
		mv.addObject("hotels", hotelList);
		System.out.printf(keyword);
		mv.addObject("keyword", keyword);
		mv.addObject("maxPrice", maxPrice);
		mv.addObject("minPrice", minPrice);		
		mv.setViewName("hotelTop");
		return mv;
	}
}
