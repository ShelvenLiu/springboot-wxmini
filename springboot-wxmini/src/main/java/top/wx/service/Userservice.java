package top.wx.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import top.wx.Mapper.UserMapper;
import top.wx.pojo.User;

@Service
public class Userservice {
	
	@Autowired
	private UserMapper userMapper;
	
	public boolean findUsernameIsExist(String username) {
		//User user=new User();
		User user=userMapper.findUsernameIsExist(username);
		
		return user == null ? false : true;
	}

	public void saveUser(User user) {
		user.setUid(UUID.randomUUID().toString());
		userMapper.saveUser(user);
	}

	public User queryUserForLogin(String username, String password) {
		return userMapper.queryUserForLogin(username,password);
	}
}
