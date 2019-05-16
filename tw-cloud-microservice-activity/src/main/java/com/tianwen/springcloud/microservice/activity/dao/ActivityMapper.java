package com.tianwen.springcloud.microservice.activity.dao;

import com.tianwen.springcloud.datasource.mapper.MyMapper;
import com.tianwen.springcloud.microservice.activity.entity.Activity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ActivityMapper extends MyMapper<Activity> {

    /**
     *
     *
     * @param map
     * @return List<Activity>
     * @see
     */
    List<Activity> queryActivityForList(Map<String, Object> mapList);

    Long countForActivityList(Map<String, Object> map);

    /**
     *
     * @param map
     * @return
     */
    List<String> getActivityCreatorIds(Map<String, Object> map);

    /**
     *
     * @param map
     * @return
     */
    List<String> getIds(Map<String, Object> map);

    //author:han 韩哲国
    /**
     *
     * @param map
     * @return
     */
    List<String> getIdsExtra(Map<String, Object> map);
    //end han

    /**
     *
     * @param map
     * @return
     */
    Long getCountByCreator(Map<String, String> map);

    /**
     *  get activity status
     * @param activityid
     * @return
     */
    String getActivityStatus(String activityid);
}
