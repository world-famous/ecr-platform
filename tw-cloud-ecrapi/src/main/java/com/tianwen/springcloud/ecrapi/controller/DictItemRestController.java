package com.tianwen.springcloud.ecrapi.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.constant.IStateCode;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.DictItemRestApi;
import com.tianwen.springcloud.ecrapi.constant.IErrorMessageConstants;
import com.tianwen.springcloud.microservice.base.entity.DictItem;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(value = "/dictitem")
public class DictItemRestController extends BaseRestController implements DictItemRestApi
{
    @Override
    public Response<DictItem> getItemList(@RequestBody QueryTree queryTree) {
        return dictItemMicroApi.getList(queryTree);
    }

    @Override
    public Response<DictItem> insertItem(@RequestBody DictItem entity) {
        QueryTree keyQuery = new QueryTree();
        keyQuery.addCondition(new QueryCondition("dicttypeid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, entity.getDicttypeid()));
        keyQuery.addCondition(new QueryCondition("dictvalue", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, entity.getDictvalue()));
        List<DictItem> keyitems = dictItemMicroApi.search(keyQuery).getPageInfo().getList();
        if (!CollectionUtils.isEmpty(keyitems))
            return new Response<>(IErrorMessageConstants.ERR_CODE_SAME_ITEM_EXIST, IErrorMessageConstants.ERR_MSG_SAME_ITEM_EXIST);

        QueryTree sortQuery = new QueryTree();
        sortQuery.addCondition(new QueryCondition("sortno", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, entity.getSortno()));
        sortQuery.addCondition(new QueryCondition("dicttypeid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, entity.getDicttypeid()));
        List<DictItem> sortitems = dictItemMicroApi.search(sortQuery).getPageInfo().getList();
        if (!CollectionUtils.isEmpty(sortitems))
            return new Response<>(IErrorMessageConstants.ERR_CODE_SAME_ITEM_EXIST, IErrorMessageConstants.ERR_MSG_SAME_ITEM_EXIST);

        if (entity.getIseditable() == null)
            entity.setIseditable("1");

        DictItem param = new DictItem();
        param.setParentdictid(entity.getParentdictid());
        param.setDictname(entity.getDictname());
        List<DictItem> exist = dictItemMicroApi.searchByEntity(param).getPageInfo().getList();
        if (!CollectionUtils.isEmpty(exist))
        {
            Response<DictItem> resp = new Response<>();
            resp.getServerResult().setResultCode(IStateCode.PARAMETER_IS_DUPLICATE);
            return resp;
        }

        return dictItemMicroApi.insert(entity);
    }

    @Override
    public Response<DictItem> modifyItem(@RequestBody DictItem entity) {
        if (entity != null && entity.getIseditable().equalsIgnoreCase("0"))
            return new Response<>(IErrorMessageConstants.ERR_CODE_NOT_ABLE_TO_EDIT, IErrorMessageConstants.ERR_MSG_NOT_ABLE_TO_EDIT);

        QueryTree keyQuery = new QueryTree();
        keyQuery.addCondition(new QueryCondition("dicttypeid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, entity.getDicttypeid()));
        keyQuery.addCondition(new QueryCondition("dictvalue", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, entity.getDictvalue()));
        keyQuery.addCondition(new QueryCondition("lang", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, entity.getLang()));
        List<DictItem> keyitems = dictItemMicroApi.search(keyQuery).getPageInfo().getList();
        if (CollectionUtils.isEmpty(keyitems))
            return new Response<>(IErrorMessageConstants.ERR_MSG_SAME_ITEM_NOT_EXIST, IErrorMessageConstants.ERR_MSG_SAME_ITEM_NOT_EXIST);

        QueryTree sortQuery = new QueryTree();
        sortQuery.addCondition(new QueryCondition("sortno", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, entity.getSortno()));
        sortQuery.addCondition(new QueryCondition("dicttypeid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, entity.getDicttypeid()));
        List<DictItem> sortitems = dictItemMicroApi.search(sortQuery).getPageInfo().getList();
        if (CollectionUtils.isEmpty(sortitems))
            return new Response<>(IErrorMessageConstants.ERR_MSG_SAME_ITEM_NOT_EXIST, IErrorMessageConstants.ERR_MSG_SAME_ITEM_NOT_EXIST);

        return dictItemMicroApi.modify(entity);
    }

    @Override
    public Response<DictItem> removeItem(@PathVariable(value = "dictid") String dictid) {
        return dictItemMicroApi.remove(dictid);
    }
}
