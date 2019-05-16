package com.tianwen.springcloud.ecrapi.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.OrderMethod;
import com.tianwen.springcloud.commonapi.query.Pagination;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.ESearchRestApi;
import com.tianwen.springcloud.ecrapi.constant.IErrorMessageConstants;
import com.tianwen.springcloud.ecrapi.util.ESearchUtil;
import com.tianwen.springcloud.ecrapi.util.SensitiveWordFilter;
import com.tianwen.springcloud.microservice.base.constant.IBaseMicroConstants;

import com.tianwen.springcloud.microservice.base.entity.DictItem;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
import com.tianwen.springcloud.microservice.bussinessassist.entity.Good;
import com.tianwen.springcloud.microservice.resource.constant.IResourceMicroConstants;
import com.tianwen.springcloud.microservice.resource.entity.Resource;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.poi.util.StringUtil;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/esearch")
public class ESearchRestController extends BaseRestController implements ESearchRestApi {


    final int DB_TYPE_USER                      = 1;
    final int DB_TYPE_ORG                       = 2;
    final int DB_TYPE_LABEL                     = 3;
    final int DB_TYPE_CATALOG               = 4;
    final int DB_TYPE_DICTITEM               = 5;
    final int DB_TYPE_AREA                      = 6;
    final int DB_TYPE_THEME                    = 7;
    final int DB_TYPE_CONTENT               = 8;
    final int DB_TYPE_GOOD                     = 9;
    final int DB_TYPE_RES_CON_CAT       = 10;
    final int DB_TYPE_RES_CON_THEME   = 11;

    @Override
    public Response<Good> getSearchResult(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token) {
        int i, len = 0, totalSize = 0;

        String[] customfilters = {"contenttype", "schoolsectionid", "subjectid", "gradeid", "editiontypeid", "bookmodelid", "catalogids"};

        String[] times = {"viewtimes", "collectiontimes", "clicktimes", "downtimes", "hotvalue"};

        fixResourceQueryTree(queryTree);

        Pagination pagination = queryTree.getPagination();
        String searchkey = "";
        if (!CollectionUtils.isEmpty(queryTree.getConditions()))
        {
            QueryCondition queryCondition = queryTree.getQueryCondition("searchkey");
            if (queryCondition != null)
                searchkey = queryCondition.getFieldValues()[0].toString();
        }

        List<String> searchData = new ArrayList<>();
        if (StringUtils.isEmpty(searchkey))
            searchData.add(searchkey);
        else {
            searchData = synonymMicroApi.getByHint(searchkey).getPageInfo().getList();
        }

//     my info
//        Response<UserLoginInfo> userResp = getUserByToken(token);
//        List<ClassInfo> classInfos = null;
//        UserLoginInfo userLoginInfo;
//        try {
//            userLoginInfo = getUserDetailData(userResp.getResponseEntity().getUserId());
//            if (userLoginInfo != null)
//                classInfos = userLoginInfo.getClassinfo();
//        }
//        catch (Exception e) { e.printStackTrace(); }

//     search all query
        JSONObject shouldCond = new JSONObject();
        JSONObject boolCond = new JSONObject();
        JSONArray shouldConditions = new JSONArray();
        JSONArray totalConditions = new JSONArray();

//     hot, vote, rate, down, view if searchkey is number
        try{
            int value = Integer.parseInt(searchkey);
            for (i = 0; i < times.length; i++)
            {
                JSONObject cond = new JSONObject();
                cond.put(times[i], value);

                JSONObject term = new JSONObject();
                term.put("match", cond);

                shouldConditions.add(term);
            }
        }
        catch (Exception e){}

//     content type
        QueryCondition conTypeQuery = queryTree.getQueryCondition(customfilters[0]);
        if (conTypeQuery != null) {
            String contentType = conTypeQuery.getFieldValues()[0].toString();
            if (!StringUtils.isEmpty(contentType))
                totalConditions.add(ESearchUtil.makeSimpleCondition("match", "contenttype", contentType));
        }

        if (queryTree.getQueryCondition("catalogids") != null) {
            QueryCondition chapterCond = queryTree.getQueryCondition("catalogids");
            String value = "";

            try {
                Object[] values = chapterCond.getFieldValues();
                value = StringUtils.join(values, " ");
            } catch (Exception e) {
            }

            queryTree.getConditions().remove(chapterCond);

            if (!value.isEmpty()) {
                String query = "select resourceno from res_con_catalog where chapterid like '" + value + "'";
                try {
                    List<String> contentids = ESearchUtil.getObjectIdsByQuery(esearchServer, query);
                    totalConditions.add(makeMatchConditions("contentid", StringUtils.join(contentids, " ")));
                }
                catch (Exception e){
                    return ESearchUtil.notFoundServerError();
                }
            }
        }

        String[] dicttypeids = {"SCHOOL_SECTION", "SUBJECT", "SCHOOL_SECTION", "EDITION", "VOLUME"};
        for (i = 1; i <= 5; i++){
            String key = customfilters[i];
            String value = "";

            QueryCondition condition = queryTree.getQueryCondition(key);
            if (condition != null){
                Object[] values = condition.getFieldValues();
                if (values != null && values.length > 0 && values[0] != null)
                    value = values[0].toString();
            }

            if (!StringUtils.isEmpty(value)){
                QueryTree dictQuery = new QueryTree();
                dictQuery.addCondition(new QueryCondition("dicttypeid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, dicttypeids[i - 1]));
                dictQuery.addCondition(new QueryCondition("dictvalue", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, value));
                dictQuery.addCondition(new QueryCondition("lang", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, IBaseMicroConstants.zh_CN));
                List<DictItem> dictItems = dictItemMicroApi.getList(dictQuery).getPageInfo().getList();
                if (!CollectionUtils.isEmpty(dictItems) && !StringUtils.isEmpty(dictItems.get(0).getDictname()))
                    totalConditions.add(ESearchUtil.makeSimpleCondition("match", customfilters[i], dictItems.get(0).getDictvalue()));
            }
        }

        if (!StringUtils.isEmpty(searchkey)) {
            shouldConditions.addAll(ESearchUtil.getDictMatchIds(esearchServer, IBaseMicroConstants.INDEX_DICT, searchData));

            //description
            for (String value : searchData) {
                shouldConditions.add(getQueryString("name", value));
                shouldConditions.add(getQueryString("description", value));
            }

            //creatorname
            List<String> userids = getMatchIds(IBaseMicroConstants.INDEX_USER, IBaseMicroConstants.TYPE_USER, searchData, "realname", "userid", "like");
            shouldConditions.addAll(makeMatchConditions("creator", userids));

            //orgname
            List<String> orgids = getMatchIds(IBaseMicroConstants.INDEX_ORG, IBaseMicroConstants.TYPE_ORG, searchData, "orgname", "orgid", "like");
            List<String> userids_org = getMatchIds("users", "users", orgids, "orgid", "userid", "like");
            shouldConditions.addAll(makeMatchConditions("creator", userids_org));

            //areaname
            List<String> area1 = getMatchIds(IBaseMicroConstants.INDEX_AREA, IBaseMicroConstants.TYPE_AREA, searchData, "areaname", "areaid", "like");
            List<String> area2 = getMatchIds(IBaseMicroConstants.INDEX_AREA, IBaseMicroConstants.TYPE_AREA, area1, "parentareaid", "areaid", "like");
            List<String> area3 = getMatchIds(IBaseMicroConstants.INDEX_AREA, IBaseMicroConstants.TYPE_AREA, area2, "parentareaid", "areaid", "like");
            area1.addAll(area2);
            area1.addAll(area3);
            List<String> orgids_area = getMatchIds(IBaseMicroConstants.INDEX_ORG, IBaseMicroConstants.TYPE_ORG, area1, "areaid", "orgid", "like");
            List<String> userids_area = getMatchIds(IBaseMicroConstants.INDEX_USER, IBaseMicroConstants.TYPE_USER, orgids_area, "orgid", "userid", "like");
            shouldConditions.addAll(makeMatchConditions("creator", userids_area));

            List<String> labels = getMatchIds(IBaseMicroConstants.INDEX_LABEL, IBaseMicroConstants.TYPE_LABEL, searchData, "labelname", "labelid", "=");
            //onelabel
            shouldConditions.addAll(makeMatchConditions("onelabelid", labels));
            //twolabel
            shouldConditions.addAll(makeMatchConditions("twolabelid", labels));
            //threelabel
            shouldConditions.addAll(makeMatchConditions("threelabelid", labels));
            //search goods
        }

        if (queryTree.getQueryCondition("remarks") != null){
            JSONObject remarksObject = getBoolConditions("must");
            JSONArray remarksArray = remarksObject.getJSONObject("bool").getJSONArray("must");

            JSONObject existField = new JSONObject();
            existField.put("field", "remarks");
            existField.put("boost", 1);

            JSONObject exist = new JSONObject();
            exist.put("exists", existField);

            JSONObject valueObject = getBoolConditions("must_not");
            JSONArray valueArray = valueObject.getJSONObject("bool").getJSONArray("must_not");
            JSONObject keyword = new JSONObject();
            keyword.put("value", "");
            keyword.put("boost", 1);
            JSONObject term = new JSONObject();
            term.put("remarks.keyword", keyword);
            JSONObject valueItem = new JSONObject();
            valueItem.put("term", term);
            valueArray.add(valueItem);

            remarksArray.add(exist);
            remarksArray.add(valueObject);

            totalConditions.add(remarksObject);
        }
        // check user share range
        Response<UserLoginInfo> userResponse = getUserByToken(token);
        UserLoginInfo user = userResponse.getResponseEntity();
        JSONArray shareShould = new JSONArray();
        JSONObject shareBool = new JSONObject();
        JSONObject shareSearchkey = new JSONObject();
        if(user != null){

            if (user.getCastle() != null) {
                String value = user.getCastle();
                JSONArray must = new JSONArray();

                JSONObject bool = new JSONObject();

                JSONObject searchkeyObj = new JSONObject();

                must.add(ESearchUtil.getQueryString("sharerangekey", value));
                must.add(ESearchUtil.getMatchConditions("sharerange", "2"));
                bool.put("must", must);
                searchkeyObj.put("bool", bool);
                shareShould.add(searchkeyObj);
            }
            if (user.getCity() != null ) {
                String value = user.getCity();
                JSONArray must = new JSONArray();

                JSONObject bool = new JSONObject();

                JSONObject searchkeyObj = new JSONObject();

                must.add(ESearchUtil.getQueryString("sharerangekey", value));
                must.add(ESearchUtil.getMatchConditions("sharerange", "3"));
                bool.put("must", must);
                searchkeyObj.put("bool", bool);
                shareShould.add(searchkeyObj);

            }
            if (user.getArea() != null) {
                String value = user.getArea();
                JSONArray must = new JSONArray();

                JSONObject bool = new JSONObject();

                JSONObject searchkeyObj = new JSONObject();

                must.add(ESearchUtil.getQueryString("sharerangekey", value));
                must.add(ESearchUtil.getMatchConditions("sharerange", "4"));
                bool.put("must", must);
                searchkeyObj.put("bool", bool);
                shareShould.add(searchkeyObj);

            }
            if (user.getOrgId() != null) {
                String value = user.getOrgId();
                JSONArray must = new JSONArray();

                JSONObject bool = new JSONObject();

                JSONObject searchkeyObj = new JSONObject();

                must.add(ESearchUtil.getQueryString("sharerangekey", value));
                must.add(ESearchUtil.getMatchConditions("sharerange", "5"));
                bool.put("must", must);
                searchkeyObj.put("bool", bool);
                shareShould.add(searchkeyObj);
            }

        }


        JSONArray must = new JSONArray();
        JSONObject bool = new JSONObject();
        JSONObject searchkeyObj = new JSONObject();
        must.add(ESearchUtil.getMatchConditions("sharerange", "1"));
        bool.put("must", must);
        searchkeyObj.put("bool", bool);
        shareShould.add(searchkeyObj);

        if(shareShould.size() > 0){
            shareBool.put("should", shareShould);
            shareSearchkey.put("bool", shareBool);
            totalConditions.add(shareSearchkey);
        }
        // end check user shagerange

        totalConditions.add(ESearchUtil.makeSimpleCondition("match", "isgoods", "1"));
        totalConditions.add(ESearchUtil.makeSimpleCondition("match", "status", "1 2 3 4 5 6 7 9"));

        boolCond.put("should", shouldConditions);
        shouldCond.put("bool", boolCond);
        totalConditions.add(shouldCond);

        JSONObject totalBool = new JSONObject();
        totalBool.put("must", totalConditions);

        JSONObject totalQuery = new JSONObject();
        totalQuery.put("bool", totalBool);

        JSONObject searchQuery = new JSONObject();

        int size = pagination.getNumPerPage();
        if (size > 10000) size = 10000;
        searchQuery.put("from", pagination.getStart());

        searchQuery.put("size", size);
        searchQuery.put("query", totalQuery);

        JSONArray sortArray = new JSONArray();
//     add sort
/*        if (queryTree.getQueryCondition("sortconids") == null) {
            Date cur = new Date(System.currentTimeMillis());
            int month = cur.getMonth();
            if (month >= 2 && month <= 7)
                sortArray.add(getNameSortScript("bookmodelid", "2000000002"));
            else
                sortArray.add(getNameSortScript("bookmodelid", "2000000001"));
        }*/

        if (!CollectionUtils.isEmpty(queryTree.getOrderMethods()))
        {
            OrderMethod method = queryTree.getOrderMethods().get(0);
            String orderBy = "", sortMethod = "";
            if (method.getField() != null)
                orderBy = method.getField();

            if (orderBy.equals("推荐排序"))
                sortMethod = "clicktimes";
            else if (orderBy.equals("最新上传"))
                sortMethod = "lastupdatetime";
            else if (orderBy.equals("最受欢迎"))
                sortMethod = "ratesum";
            else if (orderBy.equals("最新最热"))
                sortMethod = "hotvalue";
            else if (orderBy.equals("最热资源"))
                sortMethod = "hotvalue";
            else if (orderBy.equals("orderBycreatetime"))
                sortMethod = "createtime";

            if (!sortMethod.isEmpty())
            {
                sortArray.add(getNameSortScript("name", searchkey));

                JSONObject sort = new JSONObject();
                sort.put(sortMethod, "desc");
                sortArray.add(sort);
                searchQuery.put("sort", sortArray);
            }
        }

        HttpPost httpPost = new HttpPost(getSearchRootUrl() +  "/" + IResourceMicroConstants.INDEX_CONTENT + "/" +
                IResourceMicroConstants.TYPE_CONTENT + "/_search");
        String retVal;
        JSONObject searchRes;
        JSONArray resData;
        List<Good> goodList = new ArrayList<>();





        try
        {
            retVal = ESearchUtil.doRequestOperation(httpPost, searchQuery);
            searchRes = JSONObject.parseObject(retVal);
            JSONObject resultData = searchRes.getJSONObject("hits");
            resData = resultData.getJSONArray("hits");
            if (resData == null)
                resData = new JSONArray();
            len = resData.size();
            totalSize = resultData.getInteger("total");

            for (i = 0; i < len; i++)
            {
                String id = resData.getJSONObject(i).get("_id").toString();
                Resource resource = resourceMicroApi.get(id).getResponseEntity();
                if (resource == null || resource.getStatus().equals(IResourceMicroConstants.RES_STATUS_DELETE)) continue;
                Good good = goodMicroApi.getByContentid(resource.getContentid()).getResponseEntity();
                if (good == null) {
                    good = new Good();
                    good.setGoodprice(resource.getScore());
                    good.setGoodtype(IResourceMicroConstants.GOOD_TYPE_RESROUCE);
                    good.setProductid(resource.getContentid());
                    good.setAllowpaymenttype(1);
                    good.setOnshelftime(new Timestamp(System.currentTimeMillis()));
                    good.setDownshelftime(new Timestamp(System.currentTimeMillis() + 30 * 86400));
                    good.setUserid(resource.getCreator());
                    good.setCreatetime(new Timestamp(System.currentTimeMillis()));
                    good.setLastmodifytime(new Timestamp(System.currentTimeMillis()));
                    good.setStatus("1");
                    good = goodMicroApi.add(good).getResponseEntity();
                }
                good.setResource(resource);
                goodList.add(good);
                validateResource(resource);
            }
        } catch(Exception e){
            return ESearchUtil.notFoundServerError();
        }

        Response<Good> resp = new Response<>(goodList);
        resp.getPageInfo().setTotal(totalSize);
        return resp;
    }

    private Response makeDb(int indexType, Integer start){
        while(true)
        {
            QueryTree queryTree = new QueryTree();
            queryTree.addCondition(new QueryCondition("start", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, start));

            Response resp;
            switch (indexType){
                case DB_TYPE_USER:
                    resp = userMicroApi.batchSaveToES(queryTree);
                    break;
                case DB_TYPE_ORG:
                    resp = organizationMicroApi.batchSaveToES(queryTree);
                    break;
                case DB_TYPE_AREA:
                    resp = areaMicroApi.batchSaveToES(queryTree);
                    break;
                case DB_TYPE_CATALOG:
                    resp = catalogMicroApi.batchSaveToES(queryTree);
                    break;
                case DB_TYPE_LABEL:
                    resp = labelMicroApi.batchSaveToES(queryTree);
                    break;
                case DB_TYPE_DICTITEM:
                    resp = dictItemMicroApi.batchSaveToES(queryTree);
                    break;
                case DB_TYPE_THEME:
                    resp = themeMicroApi.batchSaveToES(queryTree);
                    break;
                case DB_TYPE_CONTENT:
                    resp = resourceMicroApi.batchSaveToES(queryTree);
                    break;
                case DB_TYPE_GOOD:
                    resp = goodMicroApi.batchSaveToES(queryTree);
                    break;
                case DB_TYPE_RES_CON_CAT:
                    resp = resourceMicroApi.batchSaveConToES(queryTree);
                    break;
                case DB_TYPE_RES_CON_THEME:
                    resp = themeResMicroApi.batchSaveConToES(queryTree);
                    break;
                default:
                    resp = new Response(IErrorMessageConstants.ERR_PARAMETER_INVALID, IErrorMessageConstants.ERR_MSG_PARAMETER_INVALID);
                    break;
            }

            if (!resp.getServerResult().getResultCode().equalsIgnoreCase(IErrorMessageConstants.OPERATION_SUCCESS))
                break;
            start += 50;
        }

        return new Response();
    }

    // apis for catalogs

    @Override
    public Response initCatalogs() throws Exception {
        return catalogMicroApi.makeESDb();
    }

    @Override
    public Response setCatalogSize(@PathVariable(value = "size") Integer size) {
        makeResultWindow(size, IBaseMicroConstants.INDEX_CATALOG);
        return new Response();
    }

    @Override
    public Response makeCatalogs(@PathVariable(value = "start") Integer start) {
        return makeDb(DB_TYPE_CATALOG, start);
    }

    // apis for labels

    @Override
    public Response initLabels() throws Exception {
        return labelMicroApi.makeESDb();
    }

    @Override
    public Response setLabelSize(@PathVariable(value = "size") Integer size) {
        makeResultWindow(size, IBaseMicroConstants.INDEX_LABEL);
        return new Response();
    }

    @Override
    public Response makeLabels(@PathVariable(value = "start") Integer start) {
        return makeDb(DB_TYPE_LABEL, start);
    }

    // apis for users

    @Override
    public Response initUsers() throws Exception {
        return userMicroApi.makeESDb();
    }

    @Override
    public Response setUserSize(@PathVariable(value = "size") Integer size) {
        makeResultWindow(size,IBaseMicroConstants.INDEX_USER);
        return new Response();
    }

    @Override
    public Response makeUsers(@PathVariable(value = "start") Integer start) {
        return makeDb(DB_TYPE_USER, start);
    }

    // apis for dictitems

    @Override
    public Response initDictItems() throws Exception {
        return dictItemMicroApi.makeESDb();
    }

    @Override
    public Response setDictSize(@PathVariable(value = "size") Integer size) {
        makeResultWindow(size, IBaseMicroConstants.INDEX_DICT);
        return new Response();
    }

    @Override
    public Response makeDictItems(@PathVariable(value = "start") Integer start) {
        return makeDb(DB_TYPE_DICTITEM, start);
    }

    // apis for organizations

    @Override
    public Response initOrganizations() throws Exception {
        return organizationMicroApi.makeESDb();
    }

    @Override
    public Response setOrgSize(@PathVariable(value = "size") Integer size) {
        makeResultWindow(size, IBaseMicroConstants.INDEX_ORG);
        return new Response();
    }

    @Override
    public Response makeOrganizations(@PathVariable(value = "start") Integer start) {
        return makeDb(DB_TYPE_ORG, start);
    }

    // apis for areas

    @Override
    public Response initAreas() throws Exception {
        return areaMicroApi.makeESDb();
    }

    @Override
    public Response setAreaSize(@PathVariable(value = "size") Integer size) {
        makeResultWindow(size, IBaseMicroConstants.INDEX_AREA);
        return new Response();
    }

    @Override
    public Response makeAreas(@PathVariable(value = "start") Integer start) {
        return makeDb(DB_TYPE_AREA, start);
    }

    // apis for contents

    @Override
    public Response initContents() {
        return resourceMicroApi.makeESDb();
    }

    @Override
    public Response setContentSize(@PathVariable(value = "size") Integer size) {
        makeResultWindow(size, IResourceMicroConstants.INDEX_CONTENT);
        return new Response();
    }

    @Override
    public Response makeContents(@PathVariable(value = "start") Integer start) {
        return makeDb(DB_TYPE_CONTENT, start);
    }

    // apis for connection between contents and catalogs

    @Override
    public Response initResConCat() {
        return resourceMicroApi.makeConESDb();
    }

    @Override
    public Response setResConSize(@PathVariable(value = "size") Integer size) {
        makeResultWindow(size, IResourceMicroConstants.INDEX_CON_RES_CAT);
        return new Response();
    }

    @Override
    public Response makeResConCat(@PathVariable(value = "start") Integer start) {
        return makeDb(DB_TYPE_RES_CON_CAT, start);
    }

    // apis for goods

    @Override
    public Response initGoods() {
        return goodMicroApi.makeESDb();
    }

    @Override
    public Response setGoodSize(@PathVariable(value = "size") Integer size) {
        makeResultWindow(size, IResourceMicroConstants.INDEX_GOOD);
        return new Response();
    }

    @Override
    public Response makeGoods(@PathVariable(value = "start") Integer start) {
        return makeDb(DB_TYPE_GOOD, start);
    }

    // apis for themes

    @Override
    public Response initThemes() {
        return themeMicroApi.makeESDb();
    }

    @Override
    public Response setThemeSize(@PathVariable(value = "size") Integer size) {
        makeResultWindow(size, IBaseMicroConstants.INDEX_THEME);
        return new Response();
    }

    @Override
    public Response makeThemes(@PathVariable(value = "start") Integer start) {
        return makeDb(DB_TYPE_THEME, start);
    }

    @Override
    public Response initResConThemes() {
        return themeResMicroApi.makeConESDb();
    }

    @Override
    public Response setResConThemeSize(@PathVariable(value = "size") Integer size) {
        makeResultWindow(size, IResourceMicroConstants.INDEX_CON_RES_THEME);
        return new Response();
    }

    @Override
    public Response makeResConThemes(@PathVariable(value = "start") Integer start) {
        return makeDb(DB_TYPE_RES_CON_THEME, start);
    }
}
