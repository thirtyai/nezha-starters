package com.thirtyai.nezha.i18n;

import cn.hutool.core.util.StrUtil;
import com.thirtyai.nezha.common.i18n.I18n;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.File;
import java.io.IOException;

/**
 *  Status i18n manager
 *
 * @author nezha i18n
 */
@Slf4j
public class Status extends I18n {

    private static final String I18N_IDENTIFY_NAME = "com.thirtyai.nezha.i18n.Status";

    private static String defaultLanguage = "zh_CN";

    public static String getResourceFolder() {
        return StrUtil.subBefore(I18N_IDENTIFY_NAME, StrUtil.DOT, true).replace(StrUtil.DOT, File.separator) + File.separator;
    }
	/**
	 * 200 = 成功
	 */
	public static final I18n HTTP_Success = new Status( "HTTP_Success", "200", "成功" );
	/**
	 * 404 = 404 未找到
	 */
	public static final I18n HTTP_Not_Found = new Status( "HTTP_Not_Found", "404", "404 未找到" );
	/**
	 * 403 = 403 禁止访问
	 */
	public static final I18n HTTP_Forbidden = new Status( "HTTP_Forbidden", "403", "403 禁止访问" );
	/**
	 * 401 = 401 未授权
	 */
	public static final I18n HTTP_Un_Authentication = new Status( "HTTP_Un_Authentication", "401", "401 未授权" );
	/**
	 * 500 = 500 错误
	 */
	public static final I18n HTTP_Internal_Error = new Status( "HTTP_Internal_Error", "500", "500 错误" );
	/**
	 * 0000 = 空状态
	 */
	public static final I18n Application_None = new Status( "Application_None", "0000", "空状态" );
	/**
	 * 1000 = 成功
	 */
	public static final I18n Application_Success = new Status( "Application_Success", "1000", "成功" );
	/**
	 * 9999 = Fail 错误
	 */
	public static final I18n Application_Fail = new Status( "Application_Fail", "9999", "Fail 错误" );
	/**
	 * 1009 = 没有找到安全Token类型
	 */
	public static final I18n Security_Token_Type_Not_Found = new Status( "Security_Token_Type_Not_Found", "1009", "没有找到安全Token类型" );
	/**
	 * 1008 = 没有找到Token对应的Processor
	 */
	public static final I18n Security_Processor_Not_Found = new Status( "Security_Processor_Not_Found", "1008", "没有找到Token对应的Processor" );
	/**
	 * 1007 = 没有找到 credential
	 */
	public static final I18n Security_Credentials_Not_Found = new Status( "Security_Credentials_Not_Found", "1007", "没有找到 credential" );
	/**
	 * 1011 = 登录名或密码错误
	 */
	public static final I18n Security_LoginName_Password_Error = new Status( "Security_LoginName_Password_Error", "1011", "登录名或密码错误" );
	/**
	 * 1012 = 邮箱或密码错误
	 */
	public static final I18n Security_Email_Password_Error = new Status( "Security_Email_Password_Error", "1012", "邮箱或密码错误" );
	/**
	 * 1013 = 未知错误
	 */
	public static final I18n Security_No_Reason_Error = new Status( "Security_No_Reason_Error", "1013", "未知错误" );
	/**
	 * 1014 = 登录凭证错误，用户不存在
	 */
	public static final I18n Security_User_Not_Exist = new Status( "Security_User_Not_Exist", "1014", "登录凭证错误，用户不存在" );
	/**
	 * 1015 = 访问Token过期
	 */
	public static final I18n Security_Access_Token_Expired = new Status( "Security_Access_Token_Expired", "1015", "访问Token过期" );
	/**
	 * 1016 = 刷新Token过期
	 */
	public static final I18n Security_Refresh_Token_Expired = new Status( "Security_Refresh_Token_Expired", "1016", "刷新Token过期" );
	/**
	 * 1017 = Jwt token 摘要信息为空
	 */
	public static final I18n Security_Jwt_Token_Subject_Is_Null = new Status( "Security_Jwt_Token_Subject_Is_Null", "1017", "Jwt token 摘要信息为空" );
	/**
	 * 1018 = Jwt token 加密异常
	 */
	public static final I18n Security_Jwt_Token_Encode_Error = new Status( "Security_Jwt_Token_Encode_Error", "1018", "Jwt token 加密异常" );
	/**
	 * 1019 = Jwt token 解密异常
	 */
	public static final I18n Security_Jwt_Token_Decode_Error = new Status( "Security_Jwt_Token_Decode_Error", "1019", "Jwt token 解密异常" );
	/**
	 * 1020 = 没有权限访问
	 */
	public static final I18n Security_No_Authority_Access = new Status( "Security_No_Authority_Access", "1020", "没有权限访问" );
	/**
	 * 1030 = key为空
	 */
	public static final I18n Redis_Limit_Key_Is_Blank = new Status( "Redis_Limit_Key_Is_Blank", "1030", "key为空" );
	/**
	 * 1031 = 请求过多，已被限流
	 */
	public static final I18n Redis_Limit_Is_Limit = new Status( "Redis_Limit_Is_Limit", "1031", "请求过多，已被限流" );
	/**
	 * 1032 = 获取锁失败
	 */
	public static final I18n Redis_Lock_Get_Lock_Fail = new Status( "Redis_Lock_Get_Lock_Fail", "1032", "获取锁失败" );
	/**
	 * 1033 = key为空
	 */
	public static final I18n Redis_Lock_Key_Is_Blank = new Status( "Redis_Lock_Key_Is_Blank", "1033", "key为空" );

    public static void setDefaultLanguage(String local) {
        defaultLanguage = local;
    }

    private static Status self;

    public static Status self() {
        if (self == null) {
            self = new Status();
        }
        return self;
    }

    private Status() {
        super(I18N_IDENTIFY_NAME, "init", "init", "init");
    }

    private Status(String name, String code, String desc) {
        super(I18N_IDENTIFY_NAME, name, code, desc);
        this.setDefaultLang(defaultLanguage);
    }

    static {
        try {
            String path = Status.class.getProtectionDomain().getCodeSource().getLocation().toString();
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath*:/" + getResourceFolder() + "status/*.yml");
            baseInit(I18N_IDENTIFY_NAME, path, resources);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	/**
	 * 200 = 成功
	 */
	public static final String HTTP_SUCCESS_NAME = "HTTP_Success." + I18N_IDENTIFY_NAME;
	/**
	 * 404 = 404 未找到
	 */
	public static final String HTTP_NOT_FOUND_NAME = "HTTP_Not_Found." + I18N_IDENTIFY_NAME;
	/**
	 * 403 = 403 禁止访问
	 */
	public static final String HTTP_FORBIDDEN_NAME = "HTTP_Forbidden." + I18N_IDENTIFY_NAME;
	/**
	 * 401 = 401 未授权
	 */
	public static final String HTTP_UN_AUTHENTICATION_NAME = "HTTP_Un_Authentication." + I18N_IDENTIFY_NAME;
	/**
	 * 500 = 500 错误
	 */
	public static final String HTTP_INTERNAL_ERROR_NAME = "HTTP_Internal_Error." + I18N_IDENTIFY_NAME;
	/**
	 * 0000 = 空状态
	 */
	public static final String APPLICATION_NONE_NAME = "Application_None." + I18N_IDENTIFY_NAME;
	/**
	 * 1000 = 成功
	 */
	public static final String APPLICATION_SUCCESS_NAME = "Application_Success." + I18N_IDENTIFY_NAME;
	/**
	 * 9999 = Fail 错误
	 */
	public static final String APPLICATION_FAIL_NAME = "Application_Fail." + I18N_IDENTIFY_NAME;
	/**
	 * 1009 = 没有找到安全Token类型
	 */
	public static final String SECURITY_TOKEN_TYPE_NOT_FOUND_NAME = "Security_Token_Type_Not_Found." + I18N_IDENTIFY_NAME;
	/**
	 * 1008 = 没有找到Token对应的Processor
	 */
	public static final String SECURITY_PROCESSOR_NOT_FOUND_NAME = "Security_Processor_Not_Found." + I18N_IDENTIFY_NAME;
	/**
	 * 1007 = 没有找到 credential
	 */
	public static final String SECURITY_CREDENTIALS_NOT_FOUND_NAME = "Security_Credentials_Not_Found." + I18N_IDENTIFY_NAME;
	/**
	 * 1011 = 登录名或密码错误
	 */
	public static final String SECURITY_LOGINNAME_PASSWORD_ERROR_NAME = "Security_LoginName_Password_Error." + I18N_IDENTIFY_NAME;
	/**
	 * 1012 = 邮箱或密码错误
	 */
	public static final String SECURITY_EMAIL_PASSWORD_ERROR_NAME = "Security_Email_Password_Error." + I18N_IDENTIFY_NAME;
	/**
	 * 1013 = 未知错误
	 */
	public static final String SECURITY_NO_REASON_ERROR_NAME = "Security_No_Reason_Error." + I18N_IDENTIFY_NAME;
	/**
	 * 1014 = 登录凭证错误，用户不存在
	 */
	public static final String SECURITY_USER_NOT_EXIST_NAME = "Security_User_Not_Exist." + I18N_IDENTIFY_NAME;
	/**
	 * 1015 = 访问Token过期
	 */
	public static final String SECURITY_ACCESS_TOKEN_EXPIRED_NAME = "Security_Access_Token_Expired." + I18N_IDENTIFY_NAME;
	/**
	 * 1016 = 刷新Token过期
	 */
	public static final String SECURITY_REFRESH_TOKEN_EXPIRED_NAME = "Security_Refresh_Token_Expired." + I18N_IDENTIFY_NAME;
	/**
	 * 1017 = Jwt token 摘要信息为空
	 */
	public static final String SECURITY_JWT_TOKEN_SUBJECT_IS_NULL_NAME = "Security_Jwt_Token_Subject_Is_Null." + I18N_IDENTIFY_NAME;
	/**
	 * 1018 = Jwt token 加密异常
	 */
	public static final String SECURITY_JWT_TOKEN_ENCODE_ERROR_NAME = "Security_Jwt_Token_Encode_Error." + I18N_IDENTIFY_NAME;
	/**
	 * 1019 = Jwt token 解密异常
	 */
	public static final String SECURITY_JWT_TOKEN_DECODE_ERROR_NAME = "Security_Jwt_Token_Decode_Error." + I18N_IDENTIFY_NAME;
	/**
	 * 1020 = 没有权限访问
	 */
	public static final String SECURITY_NO_AUTHORITY_ACCESS_NAME = "Security_No_Authority_Access." + I18N_IDENTIFY_NAME;
	/**
	 * 1030 = key为空
	 */
	public static final String REDIS_LIMIT_KEY_IS_BLANK_NAME = "Redis_Limit_Key_Is_Blank." + I18N_IDENTIFY_NAME;
	/**
	 * 1031 = 请求过多，已被限流
	 */
	public static final String REDIS_LIMIT_IS_LIMIT_NAME = "Redis_Limit_Is_Limit." + I18N_IDENTIFY_NAME;
	/**
	 * 1032 = 获取锁失败
	 */
	public static final String REDIS_LOCK_GET_LOCK_FAIL_NAME = "Redis_Lock_Get_Lock_Fail." + I18N_IDENTIFY_NAME;
	/**
	 * 1033 = key为空
	 */
	public static final String REDIS_LOCK_KEY_IS_BLANK_NAME = "Redis_Lock_Key_Is_Blank." + I18N_IDENTIFY_NAME;

}
