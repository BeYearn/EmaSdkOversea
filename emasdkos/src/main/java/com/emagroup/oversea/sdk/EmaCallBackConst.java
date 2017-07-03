package com.emagroup.oversea.sdk;

/**
 * 回调方法返回值的状态常量定义类
 * 
 * @author EMAGroup 
 * @version 1.0
 */

public class EmaCallBackConst {
	
	//初始化状态 100开始
	public final static int INITSUCCESS = 100;//初始化成功
	public final static int INITFALIED = 101;//初始化失败
	
	//账号状态登录 200开始
	public final static int LOGINSUCCESS = 200;//登录成功
	public final static int LOGINFALIED = 201;//登录失败
	public final static int LOGINCANELL = 202;//登录取消
	public final static int LOGINSWITCHSUCCESS = 203;//切换账号，退出当前账号成功
	public final static int LOGINSWITCHCANELL = 204;//切换账号，退出当前账号失败
	
	//账号状态登出 300开始
	public final static int LOGOUTSUCCESS = 300;//	登出成功
	public final static int LOGOUTFALIED = 301;//登出失败
	
	//支付状态 400开始
	public final static int PAYSUCCESS = 400;//支付成功
	public final static int PAYFALIED = 401;//支付失败
	public final static int PAYCANELI = 402;//支付取消
	public final static int PAYREPEAT = 403;//订单重复
	
	//注册状态 500开始
	public final static int REGISTERSUCCESS = 500;//注册成功
	public final static int REGISTERFALIED = 501;//注册失败

	//切换帐号
	public final static int ACCOUNTSWITCHSUCCESS = 600;
	public final static int ACCOUNTSWITCHFAIL = 601;


	//微博分享回调状态
	public final static int WEIBO_OK = 0;
	public final static int WEIBO_CANCLE = 1;
	public final static int WEIBO_FAIL = 2;


	//微信分享回调状态
	public final static int WEIXIN_OK = 0;
	public final static int WEIXIN_CANCLE = -2;
	public final static int WEIXIN_FAIL = -4;

	//Ema分享回调状态
	public final static int EMA_SHARE_OK = 0;
	public final static int EMA_SHARE_CANCLE = 1;
	public final static int EMA_SHARE_FAIL = 2;


	//接收推送消息
	public final static int RECIVEMSG_MSG = 123;//接收到
}
