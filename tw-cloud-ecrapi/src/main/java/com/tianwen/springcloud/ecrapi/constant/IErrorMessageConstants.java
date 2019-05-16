package com.tianwen.springcloud.ecrapi.constant;

public interface IErrorMessageConstants {
    // success code
    public final String OPERATION_SUCCESS = "200";

    //token not correct error code
    public final String ERR_TOKEN_NOT_CORRECT = "-100";

    //error
    public final String ERR_CODE_LOGIN_NAME = "-101";

    //error code
    public final String ERR_TOKEN_VALIDATION_MOVE = "-107";

    //验证 error code
    public final String ERR_TOKEN_TIMEOUT = "-108";

    //parameter invalid code
    public final String ERR_PARAMETER_INVALID = "-109";

    // database save failed
    public final String ERR_DB_SAVE_FAILED = "-111";

    //user score not enough
    public final String ERR_SCORE_NOT_ENOUGH = "-112";

    //not good
    public final String ERR_NOT_GOODS = "-113";

    //user not student
    public final String ERR_CODE_NOT_STUDENT = "-114";

    //file not exist
    public final String ERR_CODE_FILE_NOT_EXIST = "-115";

    //
    public final String ERR_CODE_NOT_JOIN = "-116";

    //error code : vote teacher more one time
    public final String ERR_CODE_VOTE_TEACHER_MORE = "-117";

    //error code : vote teacher more five times on today
    public final String ERR_CODE_VOTE_TEACHER_TODAY = "-118";

    //error
    public final String ERR_CODE_FORMAT_NOT_MATCH = "-119";

    //error
    public final String ERR_CODE_RESOURCE_NOT_EXIST = "-120";

    //error
    public final String ERR_CODE_ORDER_ISPAID_TRUE = "-121";

    //error
    public final String ERR_CODE_GOOD_ISPAID_BY_ORDER = "-122";

    //error
    public final String ERR_CODE_USER_INTEGRALPRICE_NOT_ENOUGH_TO_PAY = "-123";

    //error
    public final String ERR_CODE_FEEDBACK_SCORE_INVALID = "-124";

    //error
    public final String ERR_CODE_GOOD_ISDOWNLOADED = "-125";

    //error
    public final String ERR_CODE_IMAGE_FORMAT_INVALID = "-126";

    //error
    public final String ERR_CODE_QQ_INVALID = "-127";

    //error
    public final String ERR_CODE_PHONENUMBER_INVALID = "-128";

    // Paper_parameter_error
    public final String ERR_CODE_EMAIL_INVALID = "-129";

    // audit bonus too much
    public final String ERR_CODE_BONUS_TOO_MATCH = "-130";

    // my_favorite_size_over
    public final String ERR_CODE_COLLECT_SIZE_OVER = "-131";

    // isallowdownload 0
    public final String ERR_CODE_DOWNLOAD_IS_NOT_ALLOWED = "-132";

    // isallowdownload 0
    public final String ERR_CODE_NOT_ABLE_TO_ADD_BASKET = "-605";

    // iseditable 0
    public final String ERR_CODE_NOT_ABLE_TO_EDIT = "-606";

    // iseditable 0
    public final String ERR_CODE_SAME_ITEM_EXIST = "-607";

    // message is not existed
    public final String ERR_CODE_NEWS_IS_NOT_EXISTED = "-133";

    // es data saving, modify, remove disabled
    public final String ERR_CODE_ES_SAVING_MODIFY_DISABLE = "-188";

    //error
    public final String ERR_CODE_PAPERPRAM_INVALID = "-500";

    // Paper_parameter_error
    public final String ERR_CODE_PAPER_PARAM_DOUBLE = "-501";

    // Paper_parameter_typename_error
    public final String ERR_CODE_PAPER_PARAM_TYPENAME_ISEMPTY = "-502";

    // question_type_duplicate_error
    public final String ERR_CODE_QUESTION_TYPE_ALREADY_EXIST = "-503";

    // question_type_duplicate_error
    public final String ERR_CODE_QUESTION_TYPE_SCORE_ALREADY_EXIST = "-504";

    // sensitive word detected error
    public final String ERR_CODE_SENSITIVE_WORD_DETECTED = "-171";

    /**
     *
     */
    public final String ERR_MSG_NOT_JOIN = "不能参与活动！";

    /**
     * user score not enough
     */
    public final String ERR_MSG_SCORE_NOT_ENOUGH = "积分不够!";

    /**
     * not goods
     */
    public final String ERR_MSG_NOT_GOODS="还没生成商品!";

    /**
     * user not exist
     */
    public final String ERR_MSG_USER_NOT_EXIST="此用户不存在！";

    /**
     * role incorrect
     */
    public final String ERR_MSG_USER_ROLE_INCORRECT="请使用正确类型登录！";

    /*
    * resource not exist
    * */
    public final String ERR_MSG_RESOURCE_NOT_EXIST = "资源不存在!";

    /*
    * resource file not exist
    * */
    public final String ERR_MSG_FILE_NOT_EXIST = "文件不存在!";

    /*
    * navigation not exist
    * */
    public final String ERR_MSG_SUBNAVI_NOT_EXIST = "Couldn't find that subject navigation!";

    /*
    * catalog not exist
    * */
    public final String ERR_MSG_GOODS_NOT_EXIST = "商品不存在。";

    /**
     * content type is not correct
     */
    public final String ERR_MSG_CONTENT_TYPE_NOT_CORRECT="资源类型错";

    /*
    * schoolsection not exist
    * */
    public final String ERR_MSG_SCHOOLSECTION_NOT_EXIST = "学段名错";

    /*
    * catalog not exist
    * */
    public final String ERR_MSG_SUBJECT_NOT_EXIST = "学科名错";


    /*
    * catalog not exist
    * */
    public final String ERR_MSG_GRADE_NOT_EXIST = "学年名错";


    /*
    * catalog not exist
    * */
    public final String ERR_MSG_EDITIONTYPE_NOT_EXIST = "版本名错";

    /*
    * bookmodel not exist
    * */
    public final String ERR_MSG_BOOKMODEL_NOT_EXIST = "册别名错";

    /*
    * onelabel not exist
    * */
    public final String ERR_MSG_ONELABEL_NOT_EXIST = "一级标签名错";

    /*
    * twolabel not exist
    * */
    public final String ERR_MSG_TWOLABEL_NOT_EXIST = "二级标签名错";


    /*
    * catalog not exist
    * */
    public final String ERR_MSG_CATALOG_NOT_EXIST = "章节名错";

    /*
    token not correct
     */
    public final String ERR_MSG_TOKEN_NOT_CORRECT = "令牌不正确。";

    /*
    token timeout
     */
    public final String ERR_MSG_TOKEN_TIMEOUT = "您的会话超时，请重新登录！";

    /*
    didn't log in
     */
    public final String ERR_MSG_NOT_LOGIN = "请先登录。";

    /**
     * permission error
     */
    public final String ERR_MSG_PERMISSION = "您没有权限。";

    /**
     * parameter invalide
     */
    public final String ERR_MSG_PARAMETER_INVALID = "参数非法";

    /**
     * parameter activityid is empty
     */
    public final String ERR_MSG_PARAMETER_ACTIVITY_ID = "Parameter activityid is Empty!";


    /*
    save failed
     */
    public final String ERR_MSG_NOT_SAVED = "保存失败";

    /*
    resource name duplicated
     */
    public final String ERR_MSG_DUPLICATE_ACTIVITY_NAME= "活动名重复";

    /*
    resource name duplicated
     */
    public final String ERR_MSG_DUPLICATE_ACTIVITY_THEME= "悬赏主题重复";

    /*
    resource name duplicated
     */
    public final String ERR_MSG_RESOURCE_ALREADY_EXIST= "登录文件重复!";

    //评星 error message
    public final String ERR_MSG_RATING = "只能评星一次!";

    //点赞 error message
    public final String ERR_MSG_VOTE = "只能点赞一次!";

    //点赞 error message
    public final String ERR_MSG_OVER_VOTE_TODAY = "每天只能点赞五次!";

    public final String ERR_MSG_BONUS_TOO_MATCH = "不能超过活动的奖励积分！";

    // success message
    public final String OPERATION_SUCCESS_MESSAGE = "操作成功";

    /**
     * user not have permission
     */
    public final String ERR_MSG_NOT_HAVE_PERMISSION = "您没有权限!";

    /**
     * already exist dict name
     */
    String ERR_MSG_DUPLICATE_DICT_NAME = "已有名！";

    /**
     * format not match
     */
    public final String ERR_MSG_FORMAT_NOT_MATCH = "文件类型错了!";

    // database save failed
    public final String ERR_MESAGE_DB_SAVE_FAILED = "保存失败";

    //other user logined as that user
    public final String ERR_MSG_TOKEN_VALIDATION_MOVE = "其他用户已用您的id登录。";

    // order is already paid
    public final String ERR_MSG_ORDER_ISPAID_TRUE = "这个订单已支付";

    // good is already paid by order, so good cannot be restored in resbasket
    public final String ERR_MSG_GOOD_ISPAID_BY_ORDER = "这个产品已支付";

    // balance of user is not enough to pay a order
    public final String ERR_MSG_USER_INTEGRALPRICE_NOT_ENOUGH_TO_PAY = "积分不充分，请先充值";

    public final String ERR_MSG_FEEDBACK_SCORE_INVALID = "积分不可能是负数";

    // why resource is not added to favourite
    public final String ERR_MSG_GOOD_ISDOWNLOADED = "这个产品已下载";

    // if create a feedback, the format of image can be allowed
    public final String ERR_MSG_IMAGE_FORMAT_INVALID = "只可能上传画像";

    // if create a feedback, the QQ is invalid
    public final String ERR_MSG_QQ_INVALID = "请确认你的QQ";

    // if create a feedback, the phonenumber is invalid
    public final String ERR_MSG_PHONENUMBER_INVALID = "请确认你的手机号码";

    // Paper_parameter_error
    public final String ERR_MSG_EMAIL_INVALID = "请确认你的邮箱";

    // already add to basket
    public final String ERR_MSG_ALREADY_BASKETED = "这个资源已收藏";

    // paper_parameter is invalid
    public final String ERR_MSG_PAPERPRAM_INVALID = "卷参配置记录没有";

    // Paper_parameter_error
    public final String ERR_MSG_PAPER_PARAM_DOUBLE = "相同卷参大类下,参数名称不能重复";

    // paper_parameter_typename_isempty
    public final String ERR_MSG_PAPER_PARAM_TYPENAME_ISEMPTY = "选参数名称";

    // question_type_duplicate
    public final String ERR_MSG_QUESTION_TYPE_ALREADY_EXIST = "题型重复!";

    // question_type_duplicate
    public final String ERR_MSG_QUESTION_TYPE_SCORE_ALREADY_EXIST = "题型分数重复!";

    public final String ERR_MSG_DOWNLOAD_IS_NOT_ALLOWED = "该资源不可下载";

    public final String ERR_MSG_NOT_ABLE_TO_ADD_BASKET = "不能添加到资源篮。";

    public final String ERR_MSG_NOT_ABLE_TO_EDIT = "不能编辑。";

    // invalid login_name
    public final String ERR_MSG_LOGIN_NAME = "用户名或密码错误";

    // my_favorite_size_over
    public final String ERR_MSG_COLLECT_SIZE_OVER = "已超过收藏上限!";

    // message is not existed
    public final String ERR_MSG_NEWS_IS_NOT_EXISTED = "没有这个消息";

    // same item exist
    public final String ERR_MSG_SAME_ITEM_EXIST = "参数重复";

    public final String ERR_MSG_SAME_ITEM_NOT_EXIST = "没有这个参数";

    // es data saving, modify, remove disabled
    public final String ERR_MSG_ES_SAVING_MODIFY_DISABLE = "系统更新中，请不要进行增删改操作。";

    public final String ERR_MSG_SENSITIVE_WORD_DETECTED = "敏感词发现";
}
