package com.tianwen.springcloud.microservice.activity.constant;

/**
 * 
 * 系统常量类
 * 
 * @author jwp
 * @version [版本号, 2011-10-10]
 */
public interface IActivityMicroConstants
{



    /*************** 对应t_e_collection_activity.status begin *************************/

    /**
     * 活动状态-0:结束
     */
    final String ACTIVITY_STATUS_END = "0";

    /**
     * 活动状态-1:启始
     */
    final String ACTIVITY_STATUS_STARTED = "1";

    /**
     * 活动状态-2：悬赏 已有答案
     */
    final String ACTIVITY_STATUS_ANSWERED = "2";

    /*************** 对应t_e_collection_activity.status end *************************/

/*************** 对应t_e_collection_activity.activitytype begin *************************/

    /**
     * 活动类型-1:
     */
    final String ACTIVITY_TYPE_COLLECTION = "1";

    /**
     * 活动类型-2:
     */
    final String ACTIVITY_TYPE_REWARD = "2";

    /**
     * 活动类型-3:
     */
    final String ACTIVITY_TYPE_ESTIMATE= "3";

    /*************** 对应t_e_collection_activity.activitytype end *************************/

/*************** 对应t_e_collection_activity.isanonymity begin *************************/

    /**
     * 活动-0:
     */
    final String ACTIVITY_NOT_ANONYMITY = "0";

    /**
     * 活动-1:
     */
    final String ACTIVITY_ANONYMITY = "1";

    /*************** 对应t_e_collection_activity.isanonymity end *************************/

    /**
     * 服务名称
     */
    final String SERVICE_NAME = "activity-service";
}