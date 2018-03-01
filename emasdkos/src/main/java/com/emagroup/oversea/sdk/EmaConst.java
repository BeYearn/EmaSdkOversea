package com.emagroup.oversea.sdk;

/**
 * EMA平台供外部调用的一些常量
 *
 */
public class EmaConst {

	//handler回调
	public static final int EMA_LOGIN_URL_DONE = 10001;         //这个里面五位数起

	//permission请求码
	public static final int REQUEST_CODE_READPHONESTATE_PERMISSION = 20001;

	//---------------------------------------------------------------------------------广播控制菊花窗开关

	public static final String EMA_BC_PROGRESS_ACTION = "broadcast.progress.close";
	public static final String EMA_BC_PROGRESS_STATE = "progressState";
	public static final String EMA_BC_PROGRESS_CLOSE = "bc_progress_close";
	public static final String EMA_BC_PROGRESS_START = "bc_progress_start";

	//--------------------------------------------------------------------------------从渠道那里传来的uid和nikeName

	public static final String ALLIANCE_UID = "allianceUid";
	public static final String NICK_NAME = "nickName";

}
