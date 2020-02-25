# Springboot实现微信小程序注册登录及微信登录
##### 运行环境
jdk1.8+eclipse+tomact 8.5+maven3.5+springboot 2.0.1 微信开发者工具

##### 数据库
一张表三个字段
```sql
CREATE TABLE `user` (
  `uid` varchar(50) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```
##### 整体目录结构
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200225151314513.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxNTAyMDk5,size_16,color_FFFFFF,t_70)
##### 实现注册代码

```java
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
```
##### 实现普通登录代码

```java
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
```
#### 实现微信登录代码

```java
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
```
#### 小程序端登录界面
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200225151931705.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxNTAyMDk5,size_16,color_FFFFFF,t_70)
#### 小程序端登录js

```javascript
//普通登录
  doLogin: function (e) {
    var formObject = e.detail.value;
    //console.log(formObject);
    var username = formObject.username;
    var password = formObject.password;
    //简单验证
    if (username.length == 0 || password.length == 0) {
      wx.showToast({
        title: '用户名或密码不能为空',
        icon: 'none',
        duration: 3000
      })
    } else {
      var serverUrl = app.serverUrl;
      wx.request({
        url: serverUrl + '/login',
        method: "POST",
        data: {
          username: username,
          password: password
        },
        header: {
          'content-type': 'application/json' //默认值
        },
        success: function (res) {
          console.log(res.data);
          var status = res.data.status;
          if (status == 200) {
            //登录成功
            wx.showToast({
              title: "登录成功",
              icon: 'success',
              duration: 3000
            })
          } else {
            wx.showToast({
              title: res.data.msg,
              icon: 'none',
              duration: 3000
            })
          }
        }
      })
    }
  },
```
#### 小程序端微信登录js

```javascript
// 微信登录  
  goWxLogin: function (e) {
    console.log(e.detail.errMsg)
    console.log(e.detail.userInfo)
    console.log(e.detail.rawData)

    wx.login({
      success: function (res) {
        console.log(res)
        // 获取登录的临时凭证
        var code = res.code;
        // 调用后端，获取微信的session_key, secret
        var serverUrl = app.serverUrl;
        wx.request({
          url: serverUrl +"/wxLogin?code=" + code,
          method: "POST",
          success: function (result) {
            console.log(result);
            // 保存用户信息到本地缓存，可以用作小程序端的拦截器
            //app.setGlobalUserInfo(e.detail.userInfo);
            wx.redirectTo({
              url: '../register/register',
            })
          }
        })
      }
    })
  },
```
#### 小程序端注册js

```javascript
//注册
  doRegister: function(e){
    var formObject=e.detail.value;
    console.log(formObject);
    var username = formObject.username;
    var password = formObject.password;
    console.log(username.length);
    //简单验证
    if (username.length == 0 || password.length == 0){
      wx.showToast({
        title: '用户名或密码不能为空',
        icon: 'none',
        duration: 3000
      })
    }else{
      var serverUrl=app.serverUrl;
      wx.request({
        url: serverUrl+'/register',
        method: "POST",
        data: {
          username: username,
          password: password
        },
        header:{
          'content-type': 'application/json' //默认值
        },
        success: function(res){
          console.log(res.data);
          var status=res.data.status;
          if(status == 200){
            wx.showToast({
              title: "注册成功",
              icon: 'none',
              duration: 3000
            })
            wx.redirectTo({
              url: '../login/login',
            })
          }else {
            wx.showToast({
              title: res.data.msg,
              icon: 'none',
              duration: 3000
            })
          }
        }
      })
    }
  },
```

