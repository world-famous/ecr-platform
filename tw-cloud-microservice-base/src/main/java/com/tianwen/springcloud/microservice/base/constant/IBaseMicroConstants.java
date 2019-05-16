package com.tianwen.springcloud.microservice.base.constant;

/**
 * 
 * 系统常量类
 * 
 * @author jwp
 * @version [版本号, 2011-10-10]
 */
public interface IBaseMicroConstants
{
    /*************** 对应t_e_user_logininfo.status begin *************************/
    /**
     * 用户帐号状态-1:正常
     */
    final String USER_ACCOUNT_STATUS_NORMAL = "1";
    
    /**
     * 用户帐号状态-2:待审核
     */
    final String USER_ACCOUNT_STATUS_TO_AUDIT = "2";
    
    /**
     * 用户帐号状态-4:审核不通过
     */
    final String USER_ACCOUNT_STATUS_NO_PASS = "4";
    
    /**
     * 用户帐号状态-9:删除
     */
    final String USER_ACCOUNT_STATUS_DELETE = "9";
    
    /*************** 对应t_e_user_logininfo.status end *************************/

    /*************** 对应t_e_user_logininfo.roleid. begin ***********************/

    /**
     * 角色ID-1000000000:删除
     */
    final String USER_ROLE_ID_VISITOR = "1000000000";

    /**
     * 角色ID-1000000001:
     */
    final String USER_ROLE_ID_MANAGER = "1000000001";


    /**
     * 角色ID-1000000002:删除
     */
    final String USER_ROLE_ID_TEACHER = "1000000002";

    /**
     * 角色ID-1000000003:删除
     */
    final String USER_ROLE_ID_STUDENT = "1000000003";

    /**
     * 角色ID-1000000004:删除
     */
    final String USER_ROLE_ID_PARENT = "1000000004";

    /**
     * 角色ID-1000000000:删除
     */
    final String USER_ROLE_ID_AREA_RESEARCHER = "TWPAAS1200000000011";

    /**
     * 角色ID-1000000000:删除
     */
    final String USER_ROLE_ID_CITY_RESEARCHER = "TWPAAS1200000000012";

    /*************** 对应t_e_user_logininfo.roleid end *************************/

    /******************* t_e_sys_dict_item.lang begin *************************/

    final String en_US = "en_US";

    final String zh_CN = "zh_CN";

    /********************* t_e_sys_dict_item.lang end *************************/


    /*************** 对应t_e_book.type start *************************/


    final String BOOK_TYPE_NORMAL = "1";


    final String BOOK_TYPE_ASSIST = "0";
    /*************** 对应t_e_book.type end *************************/

    /*************** 对应t_e_score_rule.bussinesstype start*************************/

    /**
     *上传资源
     */
    final String BUSSINESS_TYPE_UPLOAD = "0";

    /**
     *参与活动
     */
    final String BUSSINESS_TYPE_JOIN_ACTIVITY = "1";

    /**
     *评星
     */
    final String BUSSINESS_TYPE_RATING = "2";

    /**
     *点赞
     */
    final String BUSSINESS_TYPE_VOTE = "3";

    /**
     *挑错
     */
    final String BUSSINESS_TYPE_REPORT = "4";

    /**
     *收藏
     */
    final String BUSSINESS_TYPE_FAVOURIT = "5";

    /**
     *完善个人信息
     */
    final String BUSSINESS_TYPE_USER_INFOMATION = "6";

    /**
     *登录
     */
    final String BUSSINESS_TYPE_LOGIN = "7";

    /**
     * 题库积分
     */
    final String BUSSINESS_TYPE_ = "8";

    /*************** 对应t_e_score_rule.bussinesstype end *************************/

    /*************** 对应t_e_theme_information.status start *************************/


    final String THEME_STATUS_VALID = "1";


    final String THEME_STATUS_INVALID = "0";
    /*************** 对应t_e_theme_information.status.end *************************/

    /**
     * 服务名称
     */
    final String SERVICE_NAME = "base-service";

    /*
    * Elasticsearch - index / type
    * */

    final String INDEX_USER = "users";
    final String TYPE_USER = "users";

    final String INDEX_LABEL = "labels";
    final String TYPE_LABEL = "labels";

    final String INDEX_ORG = "organizations";
    final String TYPE_ORG = "organizations";

    final String INDEX_AREA = "areas";
    final String TYPE_AREA = "areas";

    final String INDEX_DICT = "dictitems";
    final String TYPE_DICT = "dictitems";

    final String INDEX_CATALOG = "catalogs";
    final String TYPE_CATALOGS = "catalogs";

    final String INDEX_THEME = "themes";
    final String TYPE_THEME = "themes";

    final String ES_SERVER_ERROR = "不能与ES服务器进行通信";
    final String DICTYPE_PARENT_NOT_EXIST = "不存在父字典ID";
    final String DICTYPE_ID_EXIST = "字典类型ID已经存在";

}