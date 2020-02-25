package top.wx.common;

public class JsonResult {
	
	//响应状态码
	private Integer status;
	
	//响应消息
	private String msg;
	
	//响应数据
	private Object data;
	
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	public JsonResult(Integer status, String msg, Object data) {
		super();
		this.status = status;
		this.msg = msg;
		this.data = data;
	}
	
	public JsonResult(Object data) {
		super();
		this.status = 200;
		this.msg = "OK";
		this.data = data;
	}
	
	public JsonResult() {
		super();
	}

	public static JsonResult build(Integer status,String msg,Object data) {
		return new JsonResult(status,msg,data);
	}
	
	public static JsonResult buildData(Object data) {
		return new JsonResult(data);
	}
	
    public static JsonResult errorMsg(String msg) {
        return new JsonResult(500, msg, null);
    }
	
	
}
