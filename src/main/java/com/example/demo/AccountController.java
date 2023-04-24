package com.example.demo;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AccountController {
	@Autowired
	HttpSession session;
	@Autowired
	UserRepository userRepository;
	@Autowired
	HotelRepository hotelRepository;
	@Autowired
	CouponRepository couponRepository;
	@Autowired
	OrderRepository orderRepository;

	/**
	 * ログイン画面を表示
	 */
	@RequestMapping("/")
	public String login() {
	// セッション情報をクリア
		session.invalidate();
		return "signIn";
	}

	/**
	 * ログインを実行
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ModelAndView doLogin(
			@RequestParam("email") String email,
			@RequestParam("inputPassword") String inputPassword, ModelAndView mv) {
		// ログインが空の場合にエラーとする
		if (email == null || email.length() == 0) {
			mv.addObject("message", "メールアドレスを入力してください");
			mv.setViewName("account");
			return mv;
		}
		//パスワードが空の場合にエラーとする
		if (inputPassword == null || inputPassword.length() == 0) {
			mv.addObject("message1", "パスワードを入力してください");
			mv.setViewName("account");
			return mv;
		}

		//dbと一致してたら値を入れる。一致してなかったら0が入る
		List<Users> users = userRepository.findByEmail(email);
		/*
		 * 	データベースから間違っていたデータを取ろうとしてた
		 	そもそもそんなデータがないよでエラー
			if (!(users.equals(users))) 
		 * 
		 * */
		
		if (users.size() == 0) {
			mv.addObject("message2", "メールアドレスが登録されていません");
			mv.setViewName("account");
			return mv;
		}
		//List usersからインデックス0番の情報を取り出す。
		Users user = users.get(0);
		//userからパスワードを取ってきてる。
		String password = user.getPassword();
		//メールアドレスとパスワードの一致の処理
		if (!(password.equals(inputPassword))) {
			mv.addObject("message3", "メールアドレスとパスワードが一致しません");
			mv.setViewName("account");
			return mv;
		} else {	
			//user情報をセッションスコープにセット
			session.setAttribute("name", user.getName());
			session.setAttribute("phone", user.getPhone());
			session.setAttribute("email", user.getEmail());
			session.setAttribute("id", user.getId());
			session.setAttribute("coupon", user.getCouponCode());
			//全ホテル検索
			List<Hotel> hotelList = hotelRepository.findAll();
			mv.addObject("hotels", hotelList);
			mv.setViewName("hotelTop");
			return mv;
		}
	}
	/**
	 * 新規登録画面表示
	 */
	@RequestMapping("/newcustomer")
	public String signUp() {
		return "newCustomer";
	}
	/**
	 * 新規登録処理
	 */
	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public ModelAndView doSignUp(
			@RequestParam("name") String name,
			@RequestParam("phone") String phone,
			@RequestParam("email") String email,
			@RequestParam("password") String password,
			ModelAndView mv) {
		//名前が空の場合エラー
		if (name == null || name.length() == 0) {
			mv.addObject("message", "名前を入力してください");
			mv.setViewName("newCustomer");
			return mv;
		}
		//電話番号が未入力、11桁未満エラー
		if (phone.length() <11||phone == null) {
			mv.addObject("message", "正しく電話番号を入力してください");
			mv.setViewName("newCustomer");
			return mv;
		}
		//メールアドレスの正規表現
		//あんまり理解してないから使わんほうがいいかも
		String ptnStr ="^[\\w!#%&’/=~`\\*\\+\\?\\{\\}\\^\\$\\-\\|]+(\\.[\\w!#%&’/=~`\\*\\+\\?\\{\\}\\^\\$\\-\\|]+)*@[\\w!#%&’/=~`\\*\\+\\?\\{\\}\\^\\$\\-\\|]+(\\.[\\w!#%&’/=~`\\*\\+\\?\\{\\}\\^\\$\\-\\|]+)*$";
		//正規表現をコンパイルしてるクラス	
		//正規表現を表すクラス
		Pattern ptn = Pattern.compile(ptnStr);
		// マッチさせたい文字列に対するMatcherをPatternから作る
		Matcher mc = ptn.matcher(email);
		// Matcherに正規表現にマッチするかを聞いて、マッチしたかで処理を分ける
		if(!mc.matches()) {
			mv.addObject("message", "無効なメールアドレスです");
			mv.setViewName("newCustomer");
			return mv;
		} 
		//メールアドレスが既に存在している場合にエラー
		List<Users> users = userRepository.findByEmail(email);
		if (!(users.size() == 0)) {
			mv.addObject("message", "メールアドレスが既に登録されています");
			mv.setViewName("newCustomer");
			return mv;
		}		
		//パスワードが6桁未満の場合または未入力エラーとする
		if (password.length() <6||password == null) {
			mv.addObject("message", "6桁以上のパスワードを入力してください");
			mv.setViewName("newCustomer");
			return mv;
		}		
		//User情報をDBに登録
		Users login = new Users(name,phone,email,password,4);
		userRepository.saveAndFlush(login);		
		mv.addObject("message", "ユーザー登録完了です!!!楽しい旅を!!!");
		mv.setViewName("account");
		return mv;
	}
	// ユーザー情報管理
	@RequestMapping(value = "/user/info")
	public ModelAndView userPage( ModelAndView mv) {
		Integer user=(Integer) session.getAttribute("id");	
		//userPK取得
		Users users =userRepository.findById(user).get();
		mv.addObject("users", users);
		//couponPK取得
		mv.addObject("coupon", couponRepository.getById(users.getCouponCode()));
		mv.setViewName("userInfo");
		return mv;
	}
//ユーザー情報更新
	@RequestMapping(value = "/user/info/edit", method = RequestMethod.POST)
	public ModelAndView userEdit(
			@RequestParam ("name")String name,
			@RequestParam ("email")String email,
			@RequestParam ("phone")String phone,
			@RequestParam ("password")String password,
			@RequestParam ("newPassword")String newPassword,
			@RequestParam ("confirmPassword")String confirmPassword,
			ModelAndView mv) {
		//検索条件の取得
		Integer user=(Integer) session.getAttribute("id");	
		//userPK取得
		Users users =userRepository.findById(user).get();
		//couponNameもってくるため
		mv.addObject("coupon", couponRepository.getById(users.getCouponCode()));
		//名前が空の場合エラー
		if (name == null || name.length() == 0) {
			mv.addObject("users", users);
			mv.addObject("message", "名前を入力してください");
			mv.setViewName("userInfo");
			return mv;
		}
		//電話番号が未入力、11桁未満エラー
		if (phone.length() <11||phone == null) {
			mv.addObject("users", users);
			mv.addObject("message", "正しく電話番号を入力してください");
			mv.setViewName("userInfo");
			return mv;
		}
		//メールアドレスが空の場合エラー
		if (email == null || email.length() == 0) {
			mv.addObject("users", users);
			mv.addObject("message", "メールアドレスを入力してください");
			mv.setViewName("userInfo");
			return mv;
		}
		//メールアドレスが正規表現に適合しない場合エラー
		String ptnStr ="^[\\w!#%&’/=~`\\*\\+\\?\\{\\}\\^\\$\\-\\|]+(\\.[\\w!#%&’/=~`\\*\\+\\?\\{\\}\\^\\$\\-\\|]+)*@[\\w!#%&’/=~`\\*\\+\\?\\{\\}\\^\\$\\-\\|]+(\\.[\\w!#%&’/=~`\\*\\+\\?\\{\\}\\^\\$\\-\\|]+)*$";
		Pattern ptn = Pattern.compile(ptnStr);
		Matcher mc = ptn.matcher(email);
		if(!mc.matches()) {
			mv.addObject("users", users);
			mv.addObject("message", "無効なメールアドレスです");
			mv.setViewName("userInfo");
			return mv;
		} 
		//現在のパスワードが空の場合エラー
		if (password == null || password.length() == 0) {
			mv.addObject("users", users);
			mv.addObject("message", "現在のパスワードを入力してください");
			mv.setViewName("userInfo");
			return mv;
		}
		//現在のパスワードがDBと一致しない場合エラー		
		if (!(users.getPassword().equals(password))) {
			mv.addObject("users", users);
			mv.addObject("message", "現在のパスワードを正しく入力してください");
			mv.setViewName("userInfo");
			return mv;
		}
		//新たな現在のパスワードが空の場合、6桁未満エラー
		if (newPassword == null||newPassword.length() <6) {
			mv.addObject("users", users);
			mv.addObject("message", "6桁以上の新しいパスワードを入力してください");
			mv.setViewName("userInfo");
			return mv;
		}
		//新たなパスワードと確認パスが一致しない場合エラー
		if (!(newPassword.equals(confirmPassword))) {
			mv.addObject("users", users);
			mv.addObject("message", "新しいパスワードと再入力されたパスワードが一致しません");
			mv.setViewName("userInfo");
			return mv;
		}	
		users.setName(name);
		users.setEmail(email);
		users.setPhone(phone);
		users.setPassword(newPassword);
		userRepository.saveAndFlush(users);
		mv.addObject("message", "登録情報の変更が完了しました");	
		mv.addObject("users", users);
		mv.setViewName("userInfo");
		return mv;
	}
	//予約確認画面出力
	@RequestMapping(value = "/reserved/hotel")
	public ModelAndView userHotel( ModelAndView mv) {
		Integer user=(Integer) session.getAttribute("id");	
		//userPK取得
		Users users =userRepository.findById(user).get();
		mv.addObject("users", users);
		//ordersのPK取得
		Order orders =orderRepository.findById(users.getId()).get();
		mv.addObject("orders", orders);
		//ホテルコード取得
		mv.addObject("hotels", hotelRepository.findById(orders.getHotelCode()).get());
		mv.setViewName("reserved");
		return mv;
	}		
}
