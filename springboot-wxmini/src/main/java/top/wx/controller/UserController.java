package top.wx.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import top.wx.common.HttpClientUtil;
import top.wx.common.JsonResult;
import top.wx.pojo.User;
import top.wx.service.Userservice;

@RestController
public class UserController {
	
	@Autowired
	private Userservice userservice;
		
	//用户注册
	@PostMapping("/register")
	public JsonResult register(@RequestBody User user) {
		System.out.println("进来了……");
		System.out.println(user.getUsername());
		//判断用户名和密码不为空
		if(StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword()) ) {
			return JsonResult.errorMsg("用户名和密码不能为空");
		}
			
		//判断用户名是否存在
		if(!userservice.findUsernameIsExist(user.getUsername())) {
			userservice.saveUser(user);
		}else {
			return JsonResult.errorMsg("用户名已存在，请换一个再试");
		}
		user.setPassword("");//不显示密码	
		return JsonResult.buildData(user);
	}
	
	//用户登录
	@PostMapping("/login")
	public JsonResult login(@RequestBody User user) {
		String username = user.getUsername();
		String password = user.getPassword();
		
		//判断用户名和密码不为空
		if(StringUtils.isBlank(username) || StringUtils.isBlank(password) ) {
			return JsonResult.errorMsg("用户名和密码不能为空");
		}
			
		//判断用户名是否存在  返回值类型为User
		User userReslut=userservice.queryUserForLogin(username,password);
		if(userReslut != null) {
			userReslut.setPassword("");
			return JsonResult.buildData(userReslut);
		}else {
			return JsonResult.errorMsg("用户名或密码不正确");
		}		
	}
	
	//微信登录
	@PostMapping("/wxLogin")
	public JsonResult wxLogin(String code) {
		
		System.out.println("code:" + code);	
//		登录凭证校验。通过 wx.login 接口获得临时登录凭证 code 后传到开发者服务器调用此接口完成登录流程
//		请求地址
//		GET https://api.weixin.qq.com/sns/jscode2session?
//				appid=APPID&
//				secret=SECRET&
//				js_code=JSCODE&
//				grant_type=authorization_code
		
		String url = "https://api.weixin.qq.com/sns/jscode2session";
		Map<String, String> param = new HashMap<>();
		param.put("appid", "你的appid");
		param.put("secret", "你的开发者秘钥");
		param.put("js_code", code);
		param.put("grant_type", "authorization_code");
		
		//发起get请求
		String wxResult = HttpClientUtil.doGet(url, param);
		System.out.println(wxResult);
					
		return JsonResult.buildData("微信登录成功");
	}
}
