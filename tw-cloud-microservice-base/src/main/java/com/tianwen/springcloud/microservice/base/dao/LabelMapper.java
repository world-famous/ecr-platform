package com.tianwen.springcloud.microservice.base.dao;

import com.tianwen.springcloud.datasource.mapper.MyMapper;
import com.tianwen.springcloud.microservice.base.entity.Label;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface LabelMapper extends MyMapper<Label>
{
    List<Label> getChildrenByParent(Map<String, Object> parentlabelid);

    String getMaxSibling(String parentlabelid);

    List<Label> getSiblings(Label src);

    Long countOneLabelForList(Map<String, Object> label);

    Long countTwoLabelForList(Map<String, Object> label);

    Long countThreeLabelForList(Map<String, Object> labelData);

    List<Label> queryOneLabelForList(Map<String, Object> label);

    List<Label> queryTwoLabelForList(Map<String, Object> label);

    List<Label> queryThreeLabelForList(Map<String, Object> labelData);

    String getLabelStructure(Map<String, Object> labelData);

    /**
     * get label by example
     * @param example
     * @return
     */
    Label getByExample(Label example);

    /**
     * get onelabel ids with querytree
     * @param map
     * @return
     */
    List<String> getOneLabelIds(Map<String, Object> map);

    List<Label> validateLabel(List<Label> labels);

    Integer getChildCount(String labelid);
}
