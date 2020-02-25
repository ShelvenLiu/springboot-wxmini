package top.wx.Mapper;

import top.wx.pojo.User;

public interface UserMapper {

	public User findUsernameIsExist(String username);

	public void saveUser(User user);

	public User queryUserForLogin(String username, String password);

	
	
}
