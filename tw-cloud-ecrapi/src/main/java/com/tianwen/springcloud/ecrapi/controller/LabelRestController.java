package com.tianwen.springcloud.ecrapi.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.Pagination;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.LabelRestApi;
import com.tianwen.springcloud.ecrapi.constant.ICommonConstants;
import com.tianwen.springcloud.ecrapi.constant.IErrorMessageConstants;
import com.tianwen.springcloud.microservice.base.entity.Label;
import com.tianwen.springcloud.microservice.base.entity.LabelTree;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping(value = "/label")
public class LabelRestController extends BaseRestController implements LabelRestApi
{
    private final Comparator<Label> comp = new Comparator<Label>() {
        @Override
        public int compare(Label o1, Label o2) {
            if (!o1.getParentlabelid().equals(o2.getParentlabelid()) || o1.getSequence().equals(o2.getSequence()))
                return 0;

            int sortmethod = o1.getSortmethod().compareTo(o2.getSortmethod());
            if (sortmethod != 0)
                return sortmethod;

            long c1 = o1.getGoodscount(), c2 = o2.getGoodscount();

            if (c1 > c2)
                return -1;

            if (c1 == c2)
                return 0;

            return 1;
        }
    };

    public Response fixResponse(Response<Label> resp)
    {
        if (resp.getServerResult().getResultCode().equals("1")){
            resp.getServerResult().setResultCode(IErrorMessageConstants.OPERATION_SUCCESS);
            resp.getServerResult().setResultMessage(IErrorMessageConstants.OPERATION_SUCCESS_MESSAGE);
        }
        return resp;
    }

    @Override
    @RequestMapping(value = "/addLabel", method = RequestMethod.POST)
    public Response addLabel(@RequestBody Label entity, @RequestHeader(value = "token") String token)
    {
        if (esSyncFlag.equalsIgnoreCase(ICommonConstants.ES_SAVING_FLAG))
            return new Response<>(IErrorMessageConstants.ERR_CODE_ES_SAVING_MODIFY_DISABLE, IErrorMessageConstants.ERR_MSG_ES_SAVING_MODIFY_DISABLE);

        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();
        if (!org.apache.commons.lang.StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS)) return loginInfoResponse;

        UserLoginInfo user = loginInfoResponse.getResponseEntity();
        entity.setCreator(user.getUserId());
        Response<Label> resp = labelMicroApi.insert(entity);
        return fixResponse(resp);
    }

    @Override
    @RequestMapping(value = "/editLabel", method = RequestMethod.POST)
    public Response editLabel(@RequestBody Label entity, @RequestHeader(value = "token") String token)
    {
        if (esSyncFlag.equalsIgnoreCase(ICommonConstants.ES_SAVING_FLAG))
            return new Response<>(IErrorMessageConstants.ERR_CODE_ES_SAVING_MODIFY_DISABLE, IErrorMessageConstants.ERR_MSG_ES_SAVING_MODIFY_DISABLE);

        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();
        if (!org.apache.commons.lang.StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS)) return loginInfoResponse;

        Response<Label> resp = labelMicroApi.update(entity);
        return fixResponse(resp);
    }

    @Override
    public Response moveLabel(@RequestBody Map<String, Object> labelData, @RequestHeader(value = "token") String token) {
        if (esSyncFlag.equalsIgnoreCase(ICommonConstants.ES_SAVING_FLAG))
            return new Response<>(IErrorMessageConstants.ERR_CODE_ES_SAVING_MODIFY_DISABLE, IErrorMessageConstants.ERR_MSG_ES_SAVING_MODIFY_DISABLE);

        Response<Label> resp = labelMicroApi.move(labelData);
        return resp;
    }

    @Override
    @RequestMapping(value = "/deleteLabel/{labelid}", method = RequestMethod.GET)
    public Response deleteLabel(@PathVariable(value = "labelid") String labelid, @RequestHeader(value = "token") String token)
    {
        if (esSyncFlag.equalsIgnoreCase(ICommonConstants.ES_SAVING_FLAG))
            return new Response<>(IErrorMessageConstants.ERR_CODE_ES_SAVING_MODIFY_DISABLE, IErrorMessageConstants.ERR_MSG_ES_SAVING_MODIFY_DISABLE);

        Response<Label> resp = labelMicroApi.delete(labelid);
        return fixResponse(resp);
    }

    @Override
    @RequestMapping(value = "/getLabelList", method = RequestMethod.POST)
    public Response getLabelList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token)
    {
        UserLoginInfo user = getUserByToken(token).getResponseEntity();

        if (queryTree.getPagination() == null)
            queryTree.setPagination(new Pagination());

//        queryTree.addCondition(new QueryCondition("getalldata", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "1"));

        Response<Label> resp = labelMicroApi.getList(queryTree);
        List<LabelTree> result = new ArrayList<>();
        List<Label> labels = resp.getPageInfo().getList();

        if (labels != null && !labels.isEmpty())
        {
            for(Label label : labels)
            {
                LabelTree item = new LabelTree();
                item.setLabel(label.getLabelname());
                getGoodsCount(label, user);
                item.setLabelData(label);
                result.add(item);
            }
        }

        Response finalResp = new Response(result);
        finalResp.getPageInfo().setTotal(resp.getPageInfo().getTotal());

        return finalResp;
    }

    public Label getGoodsCount(Label label, UserLoginInfo user)
    {
        if (label == null) return label;
        QueryTree queryTree = new QueryTree();
        queryTree.addCondition(new QueryCondition("labelkey", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, label.getLabelid()));
        queryTree.addCondition(new QueryCondition("isgoods", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "1"));

        queryTree.getPagination().setNumPerPage(1);

/*        if (user != null && !StringUtils.equals(user.getRoleid(), IBaseMicroConstants.USER_ROLE_ID_MANAGER)) {
            addSharerangeKeyCondition(queryTree, user);
        }*/

        Long cnt = resourceMicroApi.getList(queryTree).getPageInfo().getTotal();
        label.setGoodscount(cnt==null?0:cnt);
        return label;
    }

    public List<Label> getGoodsCount(List<Label> labelData, UserLoginInfo user)
    {
        for (Label label : labelData)
            getGoodsCount(label, user);
        return labelData;
    }

    @Override
    @RequestMapping(value = "/getLabelTree", method = RequestMethod.POST)
    public Response getLabelTree(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token)
    {
        Response<UserLoginInfo> userResponse = getUserByToken(token);
        UserLoginInfo user = userResponse.getResponseEntity();

        List<LabelTree> result = new ArrayList<>();
        Map<String, Object> conditions = new HashMap<>();
        if (queryTree.getQueryCondition("labeltype") != null)
        {
            Object[] values = queryTree.getQueryCondition("labeltype").getFieldValues();
            if (values != null && values.length > 0)
                conditions.put("labeltype", values[0].toString());
        }
        if (queryTree.getQueryCondition("labelname") != null)
        {
            Object[] values = queryTree.getQueryCondition("labelname").getFieldValues();
            if (values != null && values.length > 0)
                conditions.put("labelname", values[0].toString());
        }
        if (queryTree.getQueryCondition("businesstype") != null)
        {
            Object[] values = queryTree.getQueryCondition("businesstype").getFieldValues();
            if (values != null && values.length > 0)
                conditions.put("businesstype", values[0].toString());
        }

        List<Label> oneList = labelMicroApi.getOneLabelList(conditions).getPageInfo().getList();
        getGoodsCount(oneList, user);
        oneList.sort(comp);
        for(Label one : oneList)
        {
            LabelTree oneNode = new LabelTree(one.getLabelname());
            Label oneLabel = one;
            oneLabel.setOnelabel(one.getLabelname());oneLabel.setTwolabel("");oneLabel.setThreelabel("");
            oneNode.setLabelData(oneLabel);
            conditions.put("onelabelid", one.getLabelid());
            List<Label> twoList = labelMicroApi.getTwoLabelList(conditions).getPageInfo().getList();
            getGoodsCount(twoList, user);
            twoList.sort(comp);
            for (Label two : twoList)
            {
                LabelTree twoNode = new LabelTree(two.getLabelname());
                Label twoLabel = two;
                twoLabel.setOnelabel(one.getLabelname());twoLabel.setTwolabel(two.getLabelname());twoLabel.setThreelabel("");
                twoNode.setLabelData(twoLabel);
                conditions.put("twolabelid", two.getLabelid());
                List<Label> threeList = labelMicroApi.getThreeLabelList(conditions).getPageInfo().getList();
                getGoodsCount(threeList, user);
                threeList.sort(comp);
                for(Label three : threeList)
                {
                    LabelTree threeNode = new LabelTree(three.getLabelname());
                    Label threeLabel = three;
                    threeLabel.setOnelabel(one.getLabelname());threeLabel.setTwolabel(two.getLabelname());threeLabel.setThreelabel(three.getLabelname());
                    threeNode.setLabelData(threeLabel);
                    twoNode.addChildren(threeNode);
                }
                oneNode.addChildren(twoNode);
            }
            result.add(oneNode);
        }

        return new Response<>(result);
    }

    @Override
    public Response getOneLabelList(@RequestBody Map<String, Object> param, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> userResponse = getUserByToken(token);
        UserLoginInfo user = userResponse.getResponseEntity();

        if (param.get("numPerPage") != null && param.get("pageNo") != null)
        {
            int numPerPage = (int)param.get("numPerPage");
            int pageNo = (int)param.get("pageNo");
            Pagination pagination = new Pagination();

            pagination.setNumPerPage(numPerPage);
            pagination.setPageNo(pageNo);
            param.put("start", pagination.getStart());
        }

        Response<Label> resp = labelMicroApi.getOneLabelList(param);
        List<Label> data = resp.getPageInfo().getList();
        getGoodsCount(resp.getPageInfo().getList(), user);
        data.sort(comp);
        return resp;
    }

    @Override
    public Response getTwoLabelList(@RequestBody Map<String, Object> param, @RequestHeader(value = "token") String token) {

        Response<UserLoginInfo> userResponse = getUserByToken(token);
        UserLoginInfo user = userResponse.getResponseEntity();

        if (param.get("numPerPage") != null && param.get("pageNo") != null)
        {
            int numPerPage = (int)param.get("numPerPage");
            int pageNo = (int)param.get("pageNo");
            Pagination pagination = new Pagination();

            pagination.setNumPerPage(numPerPage);
            pagination.setPageNo(pageNo);
            param.put("start", pagination.getStart());
        }

        Response<Label> resp = labelMicroApi.getTwoLabelList(param);
        List<Label> data = resp.getPageInfo().getList();
        getGoodsCount(resp.getPageInfo().getList(), user);
        data.sort(comp);
        return resp;
    }

    @Override
    public Response getThreeLabelList(@RequestBody Map<String, Object> param, @RequestHeader(value = "token") String token) {

        Response<UserLoginInfo> userResponse = getUserByToken(token);
        UserLoginInfo user = userResponse.getResponseEntity();

        if (param.get("numPerPage") != null && param.get("pageNo") != null)
        {
            int numPerPage = (int)param.get("numPerPage");
            int pageNo = (int)param.get("pageNo");
            Pagination pagination = new Pagination();

            pagination.setNumPerPage(numPerPage);
            pagination.setPageNo(pageNo);
            param.put("start", pagination.getStart());
        }

        Response<Label> resp = labelMicroApi.getThreeLabelList(param);
        List<Label> data = resp.getPageInfo().getList();
        getGoodsCount(resp.getPageInfo().getList(), user);
        data.sort(comp);
        return resp;
    }

    @Override
    public Response getLabelStructure(@RequestBody Map<String, Object> labelData) {
        return labelMicroApi.getLabelStructure(labelData);
    }

    //author:han
    @Override
    @RequestMapping(value = "/getLabelById/{labelid}", method = RequestMethod.GET)
    public Response getLabelById(@PathVariable(value = "labelid") String labelid, @RequestHeader(value = "token") String token)
    {
        return labelMicroApi.get(labelid);
    }
    //end:han

}
