package com.tianwen.springcloud.ecrapi.constant;

public interface ICommonConstants {
    /*********************** action type start *************************/

    /*
    action type 0 :
     */
    public final int ACTION_UPLOAD = 1;

    /*
    action type 0 :
     */
    public final int ACTION_COLLECT = 2;

    /*
    action type 0 :
     */
    public final int ACTION_RATING = 3;

    /*
    action type 0 :
     */
    public final int ACTION_VOTE = 4;

    /*
    action type 0 :
     */
    public final int ACTION_ADD_FAVOURIT = 5;

    /*
    action type 0 :
     */
    public final int ACTION_REPORT = 6;

    /*
    action type 0 :
     */
    public final int ACTION_DOWNLOAD = 7;

    /*
    action type 0 :
     */
    public final int ACTION_REWARD = 8;

    /*
    action type 0 :
     */
    public final int ACTION_USER_INFO_COMPLETE = 9;

    /*
    action type 0 :
     */
    public final int ACTION_FIRST_LOGIN = 10;


    /**
     *参与悬赏
     */
    public final int ACTION_JOIN_REWARD = 11;

    /**
     * 参与征集
     */
    public final int ACTION_JOIN_COLLECT = 12;

    /**
     *悬赏积分退回
     */
    public final int ACTION_RETURN_FROZEN_SCORE = 13;

    /**
     *参与评比资源
     */
    public final int ACTION_JOIN_ESTIMATE = 14;

    /*********************** action type end *************************/

    /*********************** user role type start *************************/
    /**
     * user login type: 0 visitor
     */
    public final int USER_VISITOR =  0;

    /**
     * user login type: 1 teacher
     */
    public final int USER_TEACHER = 1;

    /**
     * user login type: 2 student
     */
    public final int USER_STUDENT = 2;

    /**
     * user login type: 3 parent
     */
    public final int USER_MANAGER = 3;
    /*********************** user role type end *************************/

    /*********************** optiontype begin *************************/

    final int OPTION_OPTIONTYPE_DOWNLOAD = 1;

    final int OPTION_OPTIONTYPE_PURCHASE = 2;

    final int OPTION_OPTIONTYPE_MODIFY = 3;

    final int OPTION_OPTIONTYPE_ADD = 4;

    final int OPTION_OPTIONTYPE_DELETE = 5;

    final int OPTION_OPTIONTYPE_AUDIT_ALLOW = 6;

    final int OPTION_OPTIONTYPE_FEEDBACKERROR = 7;

    final int OPTION_OPTIONTYPE_FEEDBACK = 8;

    final int OPTION_OPTIONTYPE_COLLECTION_NEW = 9;

    final int OPTION_OPTIONTYPE_COLLECTION_IN = 10;

    final int OPTION_OPTIONTYPE_REWARD_NEW = 11;

    final int OPTION_OPTIONTYPE_REWARD_IN = 12;

    final int OPTION_OPTIONTYPE_UPLOAD = 13;

    final int OPTION_OPTIONTYPE_FAVORITE_ADD = 14;

    final int OPTION_OPTIONTYPE_FAVORITE_DELETE = 15;

    final int OPTION_OPTIONTYPE_CHARGE = 16;

    final int OPTION_OPTIONTYPE_BATCHIMPORT = 17;

    final int OPTION_OPTIONTYPE_PACKAGE_ADD = 18;

    final int OPTION_OPTIONTYPE_PRICESET = 19;

    final int OPTION_OPTIONTYPE_ALLOWSELL = 20;

    final int OPTION_OPTIONTYPE_DENYSELL = 21;

    final int OPTION_OPTIONTYPE_PAY = 22;

    final int OPTION_OPTIONTYPE_PRICEMODIFY = 23;

    final int OPTION_OPTIONTYPE_CATALOG_ADD = 24;

    final int OPTION_OPTIONTYPE_CATALOG_DELETE = 25;

    final int OPTION_OPTIONTYPE_CATALOG_MODIFY = 26;

    final int OPTION_OPTIONTYPE_CATALOG_MOVE = 27;

    final int OPTION_OPTIONTYPE_SUBNAVI_ADD = 28;

    final int OPTION_OPTIONTYPE_SUBNAVI_DELETE = 29;

    final int OPTION_OPTIONTYPE_SUBNAVI_MODIFY = 30;

    final int OPTION_OPTIONTYPE_SCHOOLNAMED = 31;

    final int OPTION_OPTIONTYPE_SCHOOLNORMAL = 32;

    final int OPTION_OPTIONTYPE_PACKAGE_MODIFY = 33;

    final int OPTION_OPTIONTYPE_PACKAGE_DELETE = 34;

    final int OPTION_OPTIONTYPE_AUDIT_DENY = 35;

    final int OPTION_OPTIONTYPE_COLLECTION_MODIFY = 36;

    final int OPTION_OPTIONTYPE_COLLECTION_DELETE = 37;

    final int OPTION_OPTIONTYPE_REWARD_MODIFY = 38;

    final int OPTION_OPTIONTYPE_REWARD_DELETE = 39;

    /*********************** optiontype end *************************/

    /*********************** actiontype start *************************/

    final int ACTION_ACTIONTYPE_NAMEDSCHOOL_CONTENT = 0;

    final int ACTION_ACTIONTYPE_SYNCHRO = 1;

    final int ACTION_ACTIONTYPE_PAPER = 2;

    final int ACTION_ACTIONTYPE_SPECIAL = 3;

    final int ACTION_ACTIONTYPE_MICRO = 4;

    final int ACTION_ACTIONTYPE_ENGLISH = 5;

    final int ACTION_ACTIONTYPE_STEM = 6;

    final int ACTION_ACTIONTYPE_BEAUTY = 7;

    final int ACTION_ACTIONTYPE_LOGO = 8;

    final int ACTION_ACTIONTYPE_SUBJECT = 9;

    final int ACTION_ACTIONTYPE_FINESCHOOL = 10;

    final int ACTION_ACTIONTYPE_COLLECTION = 11;

    final int ACTION_ACTIONTYPE_REWARD = 12;

    final int ACTION_ACTIONTYPE_ESTIMATE = 13;

    final int ACTION_ACTIONTYPE_UPLAOD = 14;

    /*********************** actiontype start *************************/

    /*********************** content type start *************************/

    /**
     * 课件
     */
    final String CONTENT_TYPE_CURRICULUM = "ppt,pptx,pdf";

    /**
     *教学设计
     */
    final String CONTENT_TYPE_TEACHING_PLAN = "doc,docx,pdf";

    /**
     *拓展文本
     */
    final String CONTENT_TYPE_EXTEND_TEXT = "doc,docx,pdf";

    /**
     *试卷
     */
    final String CONTENT_TYPE_PAPER = "doc,docx,pdf";

    /**
     *图片
     */
    final String CONTENT_TYPE_IMAGE = "jpg,png,gif";

    /**
     *视频
     */
    final String CONTENT_TYPE_VIDEO = "mp4,flv,wmv,avi";

    /**
     *音频
     */
    final String CONTENT_TYPE_AUDIO = "mp3,wma";

    /**
     *动画(SWF)
     */
    final String CONTENT_TYPE_CARTOON1 = "flv,swf";

    /**
     *动画(H5)
     */
    final String CONTENT_TYPE_CARTOON2 = "zhtml";

    /**
     *电子书
     */
    final String CONTENT_TYPE_EBOOK = "epub,pdf";

    /**
     *压缩文件
     */
    final String CONTENT_TYPE_ZIP = "zip";

    /**
     *其他
     */
    final String CONTENT_TYPE_OTHER = "*";

    /**
     *试题
     */
    final String CONTENT_TYPE_QUESTION1 = "*";

    /**
     *试题(结构化)
     */
    final String CONTENT_TYPE_QUESTION2 = "*";

    /**
     *教材
     */
    final String CONTENT_TYPE_BOOK = "*";

    /*********************** content type start *************************/

    /*Elasticsearch query filter weight*/
    final int ES_WEIGHT_BOOKMODEL = 100;
    final int ES_WEIGHT_CONTENT_TYPE = 200;
    final int ES_WEIGHT_PERSON_CLASS_INFO = 300;
    final double ES_MIN_SCORE = 1.0;

    final String RESPONSE_RESULT_SUCCESS = "200";
    final String ERROR_CODE_USER_NOT_EXIST = "1003";

    final String ES_SAVING_FLAG  = "1";
    final String ES_NOT_SAVING_FLAG  = "0";

    /**
     * ECO平台有关
     */
    final String ECO_PLATFORM_AUTH_URL = "/openapi-uc/oauth/token";
    final String ECO_PLATFORM_REGISTER_URL = "/openapi-base/base/register";
    final String ECO_PLATFORM_USERBYTOKEN_URL = "/openapi-uc/uc/getUserByToken";
    final String ECO_PLATFORM_UPDATETOKEN_URL = "/openapi-uc/uc/oauth/token";
    final String ECO_PLATFORM_STUDENT_INFO = "/openapi-base/base/getStudentInfo";
    final String ECO_PLATFORM_TEACHER_INFO = "/openapi-base/base/getTeacherInfo";
//    final String ECO_PLATFORM_TEACHER_INFO = "/openapi-uc/uc/search";
    final String ECO_PLATFORM_CLASS_INFO = "/openapi-base/base/getClass";
    final String ECO_PLATFORM_SCHOOL_INFO = "/openapi-base/base/getSchool";
    final String ECO_PLATFORM_TEACHING_INFO = "/openapi-base/base/queryTeachs";
    final String ECO_PLATFORM_ORGANIZATION_INFO = "/openapi-uc/org";
    final String ECO_FILE_UPlOAD_URL = "/twasp/fs/fs/file/upload";
    final String ECO_PLATFORM_MESSAGE_INFO = "/message-hub/saveMessage";
    final String ECO_FILE_DOWNLOAD_URL = "/fs/media/";

//    final String ECO_PLATFORM_CLIENT_ID = "be4d7f4dfd884838b5b9cc611dcf81f2";
//    final String ECO_PLATFORM_CLIENT_SECRET = "8c10cb3089a01210";
    final int SERVER_SYSTEM_TYPE = 1;    /* 0 : ECR, 1: ECO */
    final int OPTION_LIMIT = 5000;

//    final String ECO_USER_ROLE_MANAGER = "TWPAAS1200000000005";  // 超级管理员
    final String ECO_USER_ROLE_TEACHER = "1000000002";  // 教师
    final String ECO_USER_ROLE_STUDENT = "1000000003";  // 学生

    // development
    //final String ECO_PLATFORM_CLIENT_ID = "26fe988bcba34f03b08909f831859586";
    //final String ECO_PLATFORM_CLIENT_SECRET = "cec5ab1d0c58adf7";
}
