package com.tianwen.springcloud.ecrapi.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.commonapi.utils.DateUtil;
import com.tianwen.springcloud.ecrapi.constant.ICommonConstants;
import com.tianwen.springcloud.ecrapi.constant.IErrorMessageConstants;
import com.tianwen.springcloud.ecrapi.util.CommonUtil;
import com.tianwen.springcloud.ecrapi.util.ESearchUtil;
import com.tianwen.springcloud.ecrapi.util.HttpClientUtil;
import com.tianwen.springcloud.microservice.activity.api.ActivityMicroApi;
import com.tianwen.springcloud.microservice.activity.constant.IActivityMicroConstants;
import com.tianwen.springcloud.microservice.activity.entity.Activity;
import com.tianwen.springcloud.microservice.base.api.*;
import com.tianwen.springcloud.microservice.base.constant.IBaseMicroConstants;
import com.tianwen.springcloud.microservice.base.entity.*;
import com.tianwen.springcloud.microservice.bussinessassist.api.AuditMicroApi;
import com.tianwen.springcloud.microservice.bussinessassist.api.OptionMicroApi;
import com.tianwen.springcloud.microservice.bussinessassist.api.ResourceBasketMicroApi;
import com.tianwen.springcloud.microservice.bussinessassist.api.ResourceDownloadMicroApi;
import com.tianwen.springcloud.microservice.bussinessassist.constant.IBussinessMicroConstants;
import com.tianwen.springcloud.microservice.bussinessassist.entity.Audit;
import com.tianwen.springcloud.microservice.bussinessassist.entity.Option;
import com.tianwen.springcloud.microservice.operation.api.ChargeMicroApi;
import com.tianwen.springcloud.microservice.operation.api.IntegralMicroApi;
import com.tianwen.springcloud.microservice.operation.api.MemberMicroApi;
import com.tianwen.springcloud.microservice.operation.api.OrderMicroApi;
import com.tianwen.springcloud.microservice.operation.constant.IOperationConstants;
import com.tianwen.springcloud.microservice.operation.entity.AreaInfo;
import com.tianwen.springcloud.microservice.operation.entity.Integral;
import com.tianwen.springcloud.microservice.operation.entity.Member;
import com.tianwen.springcloud.microservice.resource.api.FileMicroApi;
import com.tianwen.springcloud.microservice.bussinessassist.api.GoodMicroApi;
import com.tianwen.springcloud.microservice.resource.api.ResourceMicroApi;
import com.tianwen.springcloud.microservice.resource.api.ThemeResMicroApi;
import com.tianwen.springcloud.microservice.resource.constant.IResourceMicroConstants;
import com.tianwen.springcloud.microservice.resource.entity.ActivityCountInfo;
import com.tianwen.springcloud.microservice.resource.entity.ExportMan;
import com.tianwen.springcloud.microservice.bussinessassist.entity.Good;
import com.tianwen.springcloud.microservice.resource.entity.Resource;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.*;

@RestController
public abstract class BaseRestController {
    @Autowired
    protected ConfigMicroApi configMicroApi;

    @Autowired
    protected ResourceDownloadMicroApi resourceDownloadMicroApi;

    @Autowired
    protected ResourceBasketMicroApi resourceBasketMicroApi;

    @Autowired
    protected OrderMicroApi orderMicroApi;

    @Autowired
    protected ChargeMicroApi chargeMicroApi;

    @Autowired
    protected  SchoolMicroApi schoolMicroApi;

    @Autowired
    protected DictItemMicroApi dictItemMicroApi;

    @Autowired
    protected DictTypeMicroApi dictTypeMicroApi;

    @Autowired
    protected UserMicroApi userMicroApi;

    @Autowired
    protected OrganizationMicroApi organizationMicroApi;

    @Autowired
    protected AreaMicroApi areaMicroApi;

    @Autowired
    protected ActivityMicroApi activityMicroApi;

    @Autowired
    protected ResourceMicroApi resourceMicroApi;

    @Autowired
    protected LabelMicroApi labelMicroApi;

    @Autowired
    protected ScoreRuleMicroApi scoreRuleMicroApi;

    @Autowired
    protected IntegralMicroApi integralMicroApi;

    @Autowired
    protected MemberMicroApi memberMicroApi;

    @Autowired
    protected CatalogMicroApi catalogMicroApi;

    @Autowired
    protected OptionMicroApi optionMicroApi;

    @Autowired
    protected NavigationMicroApi navigationMicroApi;

    @Autowired
    protected GoodMicroApi goodMicroApi;

    @Autowired
    protected FileMicroApi fileMicroApi;

    @Autowired
    protected BookMicroApi bookMicroApi;

    @Autowired
    protected AuditMicroApi auditMicroApi;

    @Autowired
    protected MessageSource messageSource;

    @Autowired
    protected UserRestController userRestController;

    @Autowired
    protected StudentMicroApi studentMicroApi;

    @Autowired
    protected TeacherMicroApi teacherMicroApi;

    @Autowired
    protected TeachingMicroApi teachingMicroApi;

    @Autowired
    protected ClassMicroApi classMicroApi;

    @Autowired
    protected ThemeMicroApi themeMicroApi;

    @Autowired
    protected ThemeResMicroApi themeResMicroApi;

    @Autowired
    protected SynonymMicroApi synonymMicroApi;

    @Autowired
    protected HttpServletRequest httpServletRequest;

    @Autowired
    protected HttpSession httpSession;

    @Value("${ES_SYNC_FLAG}")
    protected String esSyncFlag;

    @Value("${ECR_FILE_LOCATION}")
    protected String ecrFileLocation;

    @Value("${ESEARCH_SERVER}")
    protected String esearchServer;

    @Value("${ECO_SERVER_IP}")
    protected String ecoServerIp;

    @Value("${server.port}")
    protected String serverPort;

    @Value("${ECO_TENANT_ID}")
    protected String ecoTenantID;

    @Value("${spring.http.multipart.location}")
    protected String location;

    @Value("${ECO_PLATFORM_CLIENT_ID}")
    protected String ecoPlatformClientID;

    @Value("${ECO_PLATFORM_CLIENT_SECRET}")
    protected String ecoPlatformClientSecret;

    @Value("${ECO_USER_ROLE_MANAGER}")
    protected String ecoUserRoleManager;

    final String BADWORD_NAME = "BADWORDS_CONFIG";

    protected String getRootURL() {
        return  httpServletRequest.getRequestURL().toString().replace(httpServletRequest.getRequestURI(), "") +  httpServletRequest.getServletContext().getContextPath();
    }

    protected String getSearchRootUrl(){
        return "http://" + esearchServer;
    }

    public void makeResultWindow(int size, String index)
    {
        JSONObject maxwindow = new JSONObject();
        JSONObject settings = new JSONObject();

        maxwindow.put("max_result_window", size);
        settings.put("index", maxwindow);

        String sizeUrl = "http://" + esearchServer + "/" + index + "/_settings";

        HttpClient httpClient = new DefaultHttpClient();
        HttpPut httpPut = new HttpPut(sizeUrl);

        try {
            StringEntity paramEntity = new StringEntity(settings.toJSONString(), ContentType.APPLICATION_JSON);
            httpPut.setEntity(paramEntity);
            httpPut.setHeader("Content-Type", "application/json");
            httpClient.execute(httpPut);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<JSONObject> makeMatchConditions(String type, List<String> values)
    {
        List<JSONObject> result = new ArrayList<>();

        for (String value : values) {
            JSONObject item = makeMatchConditions(type, value);
            if (item != null)
                result.add(item);
        }

        return result;
    }

    public JSONObject getBoolConditions(String operator){
        JSONArray conditions = new JSONArray();
        JSONObject oprObj = new JSONObject();
        JSONObject boolObj = new JSONObject();

        oprObj.put(operator, conditions);
        boolObj.put("bool", oprObj);

        return boolObj;
    }

    public JSONObject makeMatchConditions(String type, String value)
    {
        List<JSONObject> result = new ArrayList<>();

        if (StringUtils.isEmpty(value))
            return null;

        JSONObject item = new JSONObject();
        item.put(type, value);

        JSONObject match = new JSONObject();
        match.put("match", item);

        JSONObject query = new JSONObject();
        query.put("query", match);
        query.put("min_score", ICommonConstants.ES_MIN_SCORE);

        JSONObject function_score = new JSONObject();
        function_score.put("function_score", query);

        return function_score;
    }

    public List<String> getMatchIds(String index, String  type, List<String> values, String srcField, String targetField, String method)
    {
        List<String> result = new ArrayList<>();

        String value = StringUtils.join(values, " ");
        String sql = "select " + targetField + " from " + index + " where " + srcField + " " + method +" '" + value + "'";
        JSONObject query = new JSONObject();

        HttpPost httpPost = new HttpPost("http://" + esearchServer + "/_xpack/sql?format=json");
        HttpClient httpClient = new DefaultHttpClient();

        query.put("query", sql);

        String retVal = "";
        int i, len;

        try {
            StringEntity entity = new StringEntity(query.toJSONString(), ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "application/json");
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null)
                retVal = EntityUtils.toString(httpEntity, HTTP.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JSONObject resData = JSONObject.parseObject(retVal);
            JSONArray rows = resData.getJSONArray("rows");
            if (rows == null)
                rows = new JSONArray();
            len = rows.size();
            for (i = 0; i < len ;i++)
            {
                JSONArray array = rows.getJSONArray(i);
                result.add(array.getString(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public JSONObject getNameSortScript(String key, String value){
        JSONObject script = new JSONObject();
        script.put("script", "doc['" + key + ".keyword'].value.equalsIgnoreCase(\"" + value + "\") == true ? 1 : 0");
        script.put("type", "number");
        script.put("order", "desc");

        JSONObject sort = new JSONObject();
        sort.put("_script", script);

        return sort;
    }

    public JSONObject getQueryString(String field, String value){
        JSONObject query = new JSONObject();
        String searchValue = value;
        query.put("query", searchValue);
        List<String> fields = new ArrayList<>();
        fields.add(field);
        query.put("fields", fields);

        JSONObject query_string = new JSONObject();
        query_string.put("query_string", query);

        return query_string;
    }

    protected UserLoginInfo getUserDetailData(String userid)
    {
        UserLoginInfo userInfo = userMicroApi.getUserInfo(userid).getResponseEntity();

        if (userInfo != null) {
            //member info
            Member member = memberMicroApi.getByUserId(userid).getResponseEntity();
            userInfo.setMember(member);

            //subject navigation info
            Teacher teacherInfo = teacherMicroApi.get(userid).getResponseEntity();
            Student studentInfo = studentMicroApi.get(userid).getResponseEntity();
            List<ClassInfo> classes = new ArrayList<>();
            if (teacherInfo != null) {
                QueryTree queryTree = new QueryTree();
                queryTree.addCondition(new QueryCondition("teacherid", QueryCondition.Prepender.AND, QueryCondition.Operator.FUZZY_MATCH, userid));
                List<Teaching> teachingList = teachingMicroApi.getTeachingList(queryTree).getPageInfo().getList();
                if (!CollectionUtils.isEmpty(teachingList)) {
                    for (Teaching teaching : teachingList) {
                        ClassInfo classInfo = classMicroApi.getClassById(teaching.getClassid()).getResponseEntity();
                        if (classInfo != null) {
                            classInfo.setSubjectid(teaching.getSubjectid());
                            classInfo.setSubject(teaching.getSubject());
                            classes.add(classInfo);
                        }
                    }

                    userInfo.setClassinfo(classes);
                }
            } else if (studentInfo != null) {
                ClassInfo classInfo = classMicroApi.getClassById(studentInfo.getClassid()).getResponseEntity();
                classes.add(classInfo);
                userInfo.setClassinfo(classes);
            }

            if (userInfo.getClassinfo() == null)
                userInfo.setClassinfo(new ArrayList<>());

            // area info
            List<Area> areas = new ArrayList<>();
            if (!StringUtils.isEmpty(userInfo.getOrgId())) {
                Organization organization = organizationMicroApi.get(userInfo.getOrgId()).getResponseEntity();
                if (organization != null && !StringUtils.isEmpty(organization.getAreaid())) {
                    String areaid = organization.getAreaid();
                    while (!StringUtils.isEmpty(areaid) && !areaid.equals("-1")) {
                        Area area = areaMicroApi.get(areaid).getResponseEntity();
                        if (!StringUtils.isEmpty(area.getAreaid()))
                            areas.add(0, area);
                        areaid = area.getParentareaid();
                    }
                }
                userInfo.setAreaList(areas);
            }
            // delete password info
            userInfo.setStaticPassword("");
        }
        return userInfo;
    }

    protected Response<UserLoginInfo> getUserByToken(String token) {
        Hashtable<String, String> tokenCache = userRestController.getTokenCache();

        Response<UserLoginInfo> response = userMicroApi.getByToken(token);
        UserLoginInfo userLoginInfo = response.getResponseEntity();
        if (userLoginInfo == null) {

            if (tokenCache.get(token) != null) {
                response.getServerResult().setResultCode(IErrorMessageConstants.ERR_TOKEN_VALIDATION_MOVE);
                response.getServerResult().setResultMessage(IErrorMessageConstants.ERR_MSG_TOKEN_VALIDATION_MOVE);
                tokenCache.remove(token);
            }
            else {
                response.getServerResult().setResultCode(IErrorMessageConstants.ERR_TOKEN_NOT_CORRECT);
                response.getServerResult().setResultMessage(IErrorMessageConstants.ERR_MSG_TOKEN_NOT_CORRECT);
            }
        }

        if (userLoginInfo != null && System.currentTimeMillis() > userLoginInfo.getTokenrefreshedtime().getTime() + userLoginInfo.getExpiresin().longValue()) {
            response.getServerResult().setResultCode(IErrorMessageConstants.ERR_TOKEN_TIMEOUT);
            response.getServerResult().setResultMessage(IErrorMessageConstants.ERR_MSG_TOKEN_TIMEOUT);
            tokenCache.remove(token);
        }
        else if (userLoginInfo != null) {
            long spentTime = System.currentTimeMillis() - userLoginInfo.getTokenrefreshedtime().getTime();
            long expires_in = userLoginInfo.getExpiresin();
            if (spentTime > expires_in / 2) {
                if (ICommonConstants.SERVER_SYSTEM_TYPE == 1) {
                    JSONObject params = new JSONObject();
                    params.put("client_id", ecoPlatformClientID);
                    params.put("client_secret", ecoPlatformClientSecret);
                    params.put("grant_type", "refresh_token");
                    params.put("refresh_token", userLoginInfo.getRefreshtoken());

                    // refresh by eco api
                    List<Header> headers = new ArrayList<>();
                    headers.add(new BasicHeader("access-token", userLoginInfo.getToken()));
                    headers.add(new BasicHeader("tenant-Id", userLoginInfo.getBranchCode()));

                    String strValue = HttpClientUtil.doPost2(ecoServerIp + ICommonConstants.ECO_PLATFORM_CLASS_INFO, headers, params, "utf-8");
                    JSONObject result = JSONObject.parseObject(strValue);

                    userLoginInfo.setExpiresin(result.getLong("expires_in"));
                    userLoginInfo.setToken(result.getString("access_token"));
                    userLoginInfo.setRefreshtoken(result.getString("refresh_token"));
                }

                userLoginInfo.setTokenrefreshedtime(new Date(System.currentTimeMillis()));
                userMicroApi.update(userLoginInfo);
            }
        }
        return response;
    }

    protected int checkRole(UserLoginInfo userLoginInfo) {
        String roleid = userLoginInfo.getRoleid();

        int res;

        if (StringUtils.equals(IBaseMicroConstants.USER_ROLE_ID_TEACHER, roleid))
            res = ICommonConstants.USER_TEACHER;
        else if (StringUtils.equals(IBaseMicroConstants.USER_ROLE_ID_STUDENT, roleid))
            res = ICommonConstants.USER_STUDENT;
        else if (StringUtils.equals(IBaseMicroConstants.USER_ROLE_ID_MANAGER, roleid))
            res = ICommonConstants.USER_MANAGER;
        else
            res = ICommonConstants.USER_VISITOR;

        return res;
    }

    protected void addSharerangeKeyCondition(QueryTree queryTree, UserLoginInfo user) {
        boolean isUser = (!StringUtils.equals(user.getRoleid(), IBaseMicroConstants.USER_ROLE_ID_MANAGER));
        if (isUser)
            queryTree.addCondition(new QueryCondition("isUser", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, ""));
        if (user.getCastle() != null)
            queryTree.addCondition(new QueryCondition("usercastleid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, user.getCastle()));
        if (user.getCity() != null)
            queryTree.addCondition(new QueryCondition("usercityid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, user.getCity()));
        if (user.getArea() != null)
            queryTree.addCondition(new QueryCondition("userpartid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, user.getArea()));
        if (user.getOrgId() != null)
            queryTree.addCondition(new QueryCondition("userorgsharerangeid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, user.getOrgId()));
    }

    protected QueryTree fixLabelParam(QueryTree queryTree)
    {
        QueryCondition corseCond = queryTree.getQueryCondition("onelabelkey");
        if (corseCond != null) {
            String labelname = corseCond.getFieldValues()[0].toString();
            QueryTree queryTree1 = new QueryTree();
            queryTree1.addCondition(new QueryCondition("labelname", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, labelname));
            List<String> onelabelids = labelMicroApi.getOneLabelIds(queryTree1).getPageInfo().getList();
            queryTree.addCondition(new QueryCondition("onelabelids", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, onelabelids));
        }

        String oneid = null, twoid = null, threeid = null;
        Map<String, Object> map = new HashMap<>();
        QueryCondition labelCond;

        labelCond = queryTree.getQueryCondition("onelabel");
        if (labelCond != null)
        {
            String labelname = labelCond.getFieldValues()[0].toString();
            List<Label> oneLabel = labelMicroApi.getOneLabelList(map).getPageInfo().getList();

            for(Label one : oneLabel)
                if (one.getLabelname().equals(labelname))
                {
                    String[] ids = {one.getLabelid()};
                    labelCond.setFieldValues(ids);
                    oneid = one.getLabelid();
                }
            if (!labelname.isEmpty() && oneid == null)
            {
                String[] ids = {"\n"};
                labelCond.setFieldValues(ids);
            }
            labelCond.setFieldName("onelabelid");
        }

        labelCond = queryTree.getQueryCondition("twolabel");
        if (labelCond != null)
        {
            map.put("parentlabelid", oneid);
            String labelname = labelCond.getFieldValues()[0].toString();
            List<Label> twoLabel = labelMicroApi.getTwoLabelList(map).getPageInfo().getList();

            for(Label two : twoLabel)
                if (two.getLabelname().equals(labelname))
                {
                    String[] ids = {two.getLabelid()};
                    labelCond.setFieldValues(ids);
                    twoid = two.getLabelid();
                }
            if (!labelname.isEmpty() && twoid == null)
            {
                String[] ids = {"\n"};
                labelCond.setFieldValues(ids);
            }
            labelCond.setFieldName("twolabelid");
        }

        if (twoid == null)
            queryTree.getConditions().remove(labelCond);

        labelCond = queryTree.getQueryCondition("threelabel");
        if (labelCond != null)
        {
            map.put("parentlabelid", twoid);
            String labelname = labelCond.getFieldValues()[0].toString();
            List<Label> threeLabel = labelMicroApi.getThreeLabelList(map).getPageInfo().getList();

            for(Label three : threeLabel)
                if (three.getLabelname().equals(labelname))
                {
                    String[] ids = {three.getLabelid()};
                    labelCond.setFieldValues(ids);
                    threeid = three.getLabelid();
                }
            if (!labelname.isEmpty() && threeid == null)
            {
                String[] ids = {"\n"};
                labelCond.setFieldValues(ids);
            }
            labelCond.setFieldName("threelabelid");
        }

        if (threeid == null)
            queryTree.getConditions().remove(labelCond);

        return queryTree;
    }

    protected Resource generateContentNo(Resource resource)
    {
        String contentno = resourceMicroApi.getPrefix().getResponseEntity();
        if (contentno == null) contentno = "";
        String lastContentNo, contentNo;

        QueryTree query = new QueryTree();
        Resource resInfo;
        query.addCondition(new QueryCondition("contentid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, resource.getContentid()));
        resInfo = resourceMicroApi.get(resource.getContentid()).getResponseEntity();

        if (resInfo == null)
            return resource;

        resInfo = validateResource(resInfo);

        String tempStr = null;
        try {
            tempStr = resInfo.getSchoolsectionid().substring(resInfo.getSchoolsectionid().length() - 2);
            contentno += tempStr;
            contentno += resInfo.getSubjectid().substring(resInfo.getSubjectid().length() - 2);
            contentno += tempStr;
            tempStr = resInfo.getGradeid().substring(resInfo.getGradeid().length() - 2);
            contentno += tempStr;
            tempStr = resInfo.getBookmodelid().substring(resInfo.getBookmodelid().length() - 2);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        if (tempStr != null)
            contentno += tempStr;

        contentno += resInfo.getContenttype();
        contentno += DateUtil.getCurrentTime(DateUtil.FORMAT_YYYYMMDD);

        query.getConditions().clear();
        query.addCondition(new QueryCondition("contenttype", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, resInfo.getContenttype()));
        contentNo = resourceMicroApi.getMaximumContentNo(query).getResponseEntity();

        try {
            lastContentNo = contentNo.substring(contentNo.length() - 5);
        }
        catch (Exception e) {
            lastContentNo = null;
        }

        if (lastContentNo == null)
            lastContentNo = "00000";
        else
        {
            lastContentNo = lastContentNo.substring(lastContentNo.length() - 5);
            lastContentNo = CommonUtil.increment(lastContentNo);
        }
        contentno += lastContentNo;
        resource.setContentno(contentno);
        resourceMicroApi.update(resource);
        return resource;
    }

    private void logIntegralDetail(String operationtype, String scoretype, String objectid, String incometype, Integer score, Member objector) {
        if (objector == null) return;

        Integer updateScore = 0;
        if (StringUtils.equals(incometype, "0")) {
            updateScore = objector.getUsedintegral()+score;
        } else {
            updateScore = objector.getUsedintegral()-score;
        }

        Integral integral = new Integral();
        integral.setOperationtype(operationtype);
        integral.setObjectid(objectid);
        integral.setIntegralvalue(score);
        integral.setIncometype(incometype);
        integral.setUserid(objector.getUserid());
        integral.setUserintegralvalue(updateScore);
        integral.setScoretype(scoretype);
        integralMicroApi.add(integral);
    }

    protected void doAction(Integer actiontype, Resource resource, UserLoginInfo operator, Integer score) {
        ScoreRule rule = new ScoreRule();
        String bussinesstype = "";
        String scoretype = IOperationConstants.SCORE_TYPE_RESOURCE;
        if (resource != null && actiontype == ICommonConstants.ACTION_UPLOAD ||
                actiontype == ICommonConstants.ACTION_COLLECT ||
                actiontype == ICommonConstants.ACTION_REWARD
                && !StringUtils.isEmpty(resource.getContenttype())) {
            if (resource.getSharerange() != null)
                rule.setSharerange(resource.getSharerange().equals(IResourceMicroConstants.RES_SHARE_RANGE_ALL_NET)?"1":"2");
            rule.setContenttype(resource.getContenttype());
        }

        String userid = operator.getUserId();
        String incometype = IOperationConstants.INCOME_TYPE_ADD, operationtype = "";
        switch (actiontype) {
            case ICommonConstants.ACTION_UPLOAD:
                bussinesstype = IBaseMicroConstants.BUSSINESS_TYPE_UPLOAD;
                operationtype = IOperationConstants.OPERATION_UPLOAD;
                scoretype = IOperationConstants.SCORE_TYPE_RESOURCE;
                userid = resource.getCreator();
                break;
            case ICommonConstants.ACTION_COLLECT:
                operationtype = IOperationConstants.OPERATION_UPLOAD_COLLECTION;
                scoretype = IOperationConstants.SCORE_TYPE_ACTIVITY;
                bussinesstype = IBaseMicroConstants.BUSSINESS_TYPE_JOIN_ACTIVITY;
                userid = resource.getCreator();
                break;
            case ICommonConstants.ACTION_RATING:
                scoretype = IOperationConstants.SCORE_TYPE_RESOURCE;
                operationtype = IOperationConstants.OPERATION_RATING;
                bussinesstype = IBaseMicroConstants.BUSSINESS_TYPE_RATING;
                break;
            case ICommonConstants.ACTION_VOTE:
                scoretype = IOperationConstants.SCORE_TYPE_RESOURCE;
                operationtype = IOperationConstants.OPERATION_VOTE;
                bussinesstype = IBaseMicroConstants.BUSSINESS_TYPE_VOTE;
                break;
            case ICommonConstants.ACTION_ADD_FAVOURIT:
                scoretype = IOperationConstants.SCORE_TYPE_RESOURCE;
                operationtype = IOperationConstants.OPERATION_FAVOURITE;
                bussinesstype = IBaseMicroConstants.BUSSINESS_TYPE_FAVOURIT;
                break;
            case ICommonConstants.ACTION_REPORT:
                operationtype = IOperationConstants.OPERATION_ERROR_REPORT;
                bussinesstype = IBaseMicroConstants.BUSSINESS_TYPE_REPORT;
                break;
            case ICommonConstants.ACTION_DOWNLOAD:
                operationtype = IOperationConstants.OPERATION_DOWNLOAD;
                scoretype = IOperationConstants.SCORE_TYPE_RESOURCE;
                incometype = IOperationConstants.INCOME_TYPE_USE;
                if (resource != null && score == null && resource.getGoodinfo() != null) {
                    Good goodInfo = (Good) resource.getGoodinfo();
                    score = goodInfo.getGoodprice();
                }
                break;
            case ICommonConstants.ACTION_USER_INFO_COMPLETE:
                operationtype = IOperationConstants.OPERATION_USER_INFO_COMPLETE;
                bussinesstype = IBaseMicroConstants.BUSSINESS_TYPE_USER_INFOMATION;
                break;
            case ICommonConstants.ACTION_REWARD:
                scoretype = IOperationConstants.SCORE_TYPE_ACTIVITY;
                operationtype = IOperationConstants.OPERATION_CREATE_REWARD;
                bussinesstype = IBaseMicroConstants.BUSSINESS_TYPE_JOIN_ACTIVITY;
                break;
            case ICommonConstants.ACTION_JOIN_REWARD:
                scoretype = IOperationConstants.SCORE_TYPE_ACTIVITY;
                operationtype = IOperationConstants.OPERATION_UPLOAD_REWARD;
                bussinesstype = IBaseMicroConstants.BUSSINESS_TYPE_JOIN_ACTIVITY;
                userid = resource.getCreator();
                break;
            case ICommonConstants.ACTION_JOIN_COLLECT:
                scoretype = IOperationConstants.SCORE_TYPE_ACTIVITY;
                operationtype = IOperationConstants.OPERATION_UPLOAD_COLLECTION;
                bussinesstype = IBaseMicroConstants.BUSSINESS_TYPE_JOIN_ACTIVITY;
                userid = resource.getCreator();
                break;
            case ICommonConstants.ACTION_JOIN_ESTIMATE:
                scoretype = IOperationConstants.SCORE_TYPE_ACTIVITY;
                operationtype = IOperationConstants.OPERATION_UPLOAD_ESTIMATE;
                bussinesstype = IBaseMicroConstants.BUSSINESS_TYPE_JOIN_ACTIVITY;
                userid = resource.getCreator();
                break;
            case ICommonConstants.ACTION_RETURN_FROZEN_SCORE:
                scoretype = IOperationConstants.SCORE_TYPE_ACTIVITY;
                operationtype = IOperationConstants.OPERATION_RETURN_FROZEN_INTEGRAL;
                bussinesstype = IBaseMicroConstants.BUSSINESS_TYPE_JOIN_ACTIVITY;
                break;
            case ICommonConstants.ACTION_FIRST_LOGIN:
                scoretype = IOperationConstants.SCORE_TYPE_NORMAL;
                operationtype = IOperationConstants.OPERATION_USER_LOGIN;
                bussinesstype = IBaseMicroConstants.BUSSINESS_TYPE_LOGIN;
                break;
            default:
                break;
        }

        Member operationaccount = memberMicroApi.getByUserId(userid).getResponseEntity();
        if (operationaccount != null) {
            Member example = new Member();
            example.setMemberid(operationaccount.getMemberid());

            rule.setBussinesstype(bussinesstype);
            rule.setScoretype(scoretype);
            if (!StringUtils.isEmpty(bussinesstype)) {
                ScoreRule scoreRule = scoreRuleMicroApi.getByScoreRule(rule).getResponseEntity();
                if (scoreRule != null && score == null)
                    score = scoreRule.getScore();
            }

            if (actiontype == ICommonConstants.ACTION_VOTE || actiontype == ICommonConstants.ACTION_ADD_FAVOURIT
                    || actiontype == ICommonConstants.ACTION_RATING) {
                String operateType = "";
                if (actiontype == ICommonConstants.ACTION_VOTE)
                    operateType = IOperationConstants.OPERATION_VOTE;
                else if (actiontype == ICommonConstants.ACTION_ADD_FAVOURIT)
                    operateType = IOperationConstants.OPERATION_FAVOURITE;
                else if (actiontype == ICommonConstants.ACTION_RATING)
                    operateType = IOperationConstants.OPERATION_RATING;

                Map<String, Object> map = new HashMap<>();
                map.put("userid", operator.getUserId());
                map.put("operationtype", operateType);
                Integer todaybonusScore = integralMicroApi.getTodayBonusScore(map).getResponseEntity();
                if (todaybonusScore >= 100)
                    score = 0;
            }

            if (score != null && score > 0) {
                int updatescore;
                if (StringUtils.equals(incometype, IOperationConstants.INCOME_TYPE_ADD)) {
                    updatescore = operationaccount.getUseintegral() + score;
                } else {
                    updatescore = operationaccount.getUseintegral() - score;
                }
                example.setUseintegral(updatescore);

                memberMicroApi.update(example);

                Integral integral = new Integral();
                integral.setOperationtype(operationtype);
                if (resource != null)
                    integral.setObjectid(resource.getContentid());
                integral.setIntegralvalue(score);
                integral.setIncometype(incometype);
                integral.setUserid(userid);
                integral.setUserintegralvalue(updatescore);
                integral.setScoretype(scoretype);
                integralMicroApi.add(integral);
            }
        }
    }

    protected void giveBonusFirstLogin(UserLoginInfo userInfo) {
        String userId = userInfo.getUserId();
        Response<Integer> response = integralMicroApi.getTodayLoginTimes(userId);
        // Check first login : if value > 1 then logged in serveral times
        if (response.getResponseEntity().intValue() < 1) {
            doAction(ICommonConstants.ACTION_FIRST_LOGIN, new Resource(), userInfo, null);
            String commnet = "请您接受今天的礼物! - "+ICommonConstants.ACTION_FIRST_LOGIN+"分" ;
            List<String> receivers = new ArrayList<>();
            receivers.add(userInfo.getUserId());
            saveMessage("1","系统",commnet,receivers,ecoPlatformClientID,userInfo.getToken());
        }
    }

    protected void setUerScore(String userid, Integer score) {
        Member member = memberMicroApi.getByUserId(userid).getResponseEntity();
        if (member == null) return;
        Member example = new Member();
        example.setMemberid(member.getMemberid());
        example.setUseintegral(score);
        memberMicroApi.update(example);
    }

    protected Response<ExportMan> getExportManList(QueryTree queryTree) {
        Response response = resourceMicroApi.getExportManList(queryTree);
        List<ExportMan> exportMans = response.getPageInfo().getList();
        if (!CollectionUtils.isEmpty(exportMans)) {
            for (ExportMan exportMan : exportMans) {
                String userid = exportMan.getUserid();
                UserLoginInfo userinfo = userMicroApi.get(userid).getResponseEntity();
                if (userinfo != null)
                    exportMan.setUsername(userinfo.getRealName());
            }
        }
        return response;
    }

    //activity validate
    protected void validateActivityList(Response<Activity> response) {
        List<Activity> activityList = response.getPageInfo().getList();
        if (CollectionUtils.isEmpty(activityList)) return;
        for (Activity activity : activityList) {
            ActivityCountInfo activityCountInfo = resourceMicroApi.getActivityInfo(activity.getActivityid()).getResponseEntity();
            if (activityCountInfo != null) {
                activity.setViewtimes(activityCountInfo.getViewtimes());
                activity.setDowntimes(activityCountInfo.getDowntimes());
                activity.setAnswercount(activityCountInfo.getAnswercount());
                activity.setJoinercount(activityCountInfo.getJoinercount());
                activity.setAnsweruserid(activityCountInfo.getCreator());
            }
            UserLoginInfo user = userMicroApi.get(activity.getCreator()).getResponseEntity();
            if (user != null && StringUtils.equals(activity.getIsanonymity(), IActivityMicroConstants.ACTIVITY_NOT_ANONYMITY))
                activity.setCreatorname(user.getRealName());
        }
    }

    protected void validateActivityList2(Response<Activity> response) {
        List<Activity> activityList = response.getPageInfo().getList();
        if (CollectionUtils.isEmpty(activityList)) return;

        List<String> activityids = new ArrayList<>();
        List<String> creatorids = new ArrayList<>();
        for (Activity activity : activityList) {
            if (null != activity) {
                activityids.add(activity.getActivityid());
                creatorids.add(activity.getCreator());
            }
        }
        List<ActivityCountInfo> countinfos = resourceMicroApi.validateActivityList(activityids).getPageInfo().getList();
        if (!CollectionUtils.isEmpty(countinfos)) {
            for (ActivityCountInfo countInfo : countinfos) {
                Activity activity = getActivity(countInfo.getActivityid(), activityList);
                if (null != activity) {
                    activity.setJoinercount(countInfo.getJoinercount());
                    activity.setAnsweruserid(countInfo.getCreator());
                    activity.setAnswercount(countInfo.getAnswercount());
                    activity.setDowntimes(countInfo.getDowntimes());
                    activity.setViewtimes(countInfo.getViewtimes());
                }
            }
        }

        if (!CollectionUtils.isEmpty(creatorids)) {
            creatorids.add("");
            QueryTree queryTree  = new QueryTree();
            queryTree.addCondition(new QueryCondition("userids", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, creatorids));
            List<UserLoginInfo> userLoginInfos = userMicroApi.getList(queryTree).getPageInfo().getList();
            if (!CollectionUtils.isEmpty(userLoginInfos)) {
                for (UserLoginInfo user : userLoginInfos) {
                    Activity activity = getActivity4Creator(user.getUserId(), activityList);
                    activity.setCreatorname(user.getRealName());
                }
            }
        }
        response.getPageInfo().setList(activityList);
    }

    private Activity getActivity4Creator(String userid, List<Activity> activityList) {
        for (Activity activity : activityList) {
            if (StringUtils.equals(userid, activity.getCreator())) {
                return activity;
            }
        }
        return null;
    }

    private Activity getActivity(String activityid, List<Activity> activityList) {
        for (Activity activity : activityList) {
            if (StringUtils.equals(activityid, activity.getActivityid())) {
                return activity;
            }
        }
        return null;
    }

    protected void logOptionEntity(String objectid, String optionedid, int actiontype, String token){
        Option option = new Option();
        UserLoginInfo userLoginInfo = getUserByToken(token).getResponseEntity();
        if (userLoginInfo != null)
            option.setCreator(userLoginInfo.getRealName() + "-" + userLoginInfo.getLoginName());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        option.setOptiontime(timestamp);
        option.setObjectid(objectid);
        option.setOptionedid(optionedid);

        Activity activity = null; Resource resource = null;
        String optiontype = null, optionname = null;
        switch (actiontype){
            case ICommonConstants.OPTION_OPTIONTYPE_DOWNLOAD:
                resource = resourceMicroApi.get(objectid).getResponseEntity();
                if (resource != null)
                    option.setObjecttype(resource.getContenttype());
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_DOWNLOAD;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_DOWNLOAD;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_ADD:
                resource = resourceMicroApi.get(objectid).getResponseEntity();
                if (resource != null)
                    option.setObjecttype(resource.getContenttype());
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_DOWNLOAD;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_DOWNLOAD;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_PURCHASE:
                resource = resourceMicroApi.get(objectid).getResponseEntity();
                if (resource != null)
                    option.setObjecttype(resource.getContenttype());
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_PURCHASE;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_PURCHASE;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_MODIFY:
                resource = resourceMicroApi.get(objectid).getResponseEntity();
                if (resource != null)
                    option.setObjecttype(resource.getContenttype());
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_MODIFY;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_MODIFY;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_DELETE:
                resource = resourceMicroApi.get(objectid).getResponseEntity();
                if (resource != null)
                    option.setObjecttype(resource.getContenttype());
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_DELETE;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_DELETE;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_AUDIT_ALLOW:
                resource = resourceMicroApi.get(objectid).getResponseEntity();
                if (resource != null)
                    option.setObjecttype(resource.getContenttype());
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_AUDIT_ALLOW;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_AUDIT_ALLOW;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_FEEDBACKERROR:
                resource = resourceMicroApi.get(objectid).getResponseEntity();
                if (resource != null)
                    option.setObjecttype(resource.getContenttype());
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_FEEDBACKERROR;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_FEEDBACKERROR;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_FEEDBACK:
                resource = resourceMicroApi.get(objectid).getResponseEntity();
                if (resource != null)
                    option.setObjecttype(resource.getContenttype());
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_FEEDBACK;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_FEEDBACK;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_COLLECTION_NEW:
                activity = activityMicroApi.get(objectid).getResponseEntity();
                if (activity != null) {
                    option.setObjecttype(activity.getContenttype());
                }
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_COLLECTION_NEW;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_COLLECTION_NEW;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_COLLECTION_IN:
                activity = activityMicroApi.get(objectid).getResponseEntity();
                if (activity != null) {
                    option.setObjecttype(activity.getContenttype());
                }
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_COLLECTION_IN;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_COLLECTION_IN;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_REWARD_NEW:
                activity = activityMicroApi.get(objectid).getResponseEntity();
                if (activity != null) {
                    option.setObjecttype(activity.getContenttype());
                }
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_REWARD_NEW;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_REWARD_NEW;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_UPLOAD:
                resource = resourceMicroApi.get(objectid).getResponseEntity();
                if (resource != null)
                    option.setObjecttype(resource.getContenttype());
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_UPLOAD;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_UPLOAD;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_FAVORITE_ADD:
                resource = resourceMicroApi.get(objectid).getResponseEntity();
                if (resource != null)
                    option.setObjecttype(resource.getContenttype());
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_FAVORITE_ADD;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_FAVORITE_ADD;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_FAVORITE_DELETE:
                resource = resourceMicroApi.get(objectid).getResponseEntity();
                if (resource != null)
                    option.setObjecttype(resource.getContenttype());
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_FAVORITE_DELETE;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_FAVORITE_DELETE;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_CHARGE:
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_CHARGE;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_CHARGE;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_BATCHIMPORT:
                resource = resourceMicroApi.get(objectid).getResponseEntity();
                if (resource != null)
                    option.setObjecttype(resource.getContenttype());
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_BATCHIMPORT;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_BATCHIMPORT;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_PACKAGE_ADD:
                resource = resourceMicroApi.get(objectid).getResponseEntity();
                if (resource != null)
                    option.setObjecttype(resource.getContenttype());
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_PACKAGE_ADD;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_PACKAGE_ADD;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_PACKAGE_DELETE:
                resource = resourceMicroApi.get(objectid).getResponseEntity();
                if (resource != null)
                    option.setObjecttype(resource.getContenttype());
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_PACKAGE_DELETE;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_PACKAGE_DELETE;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_PACKAGE_MODIFY:
                resource = resourceMicroApi.get(objectid).getResponseEntity();
                if (resource != null)
                    option.setObjecttype(resource.getContenttype());
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_PACKAGE_MODIFY;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_PACKAGE_MODIFY;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_PRICESET:
                resource = resourceMicroApi.get(objectid).getResponseEntity();
                if (resource != null)
                    option.setObjecttype(resource.getContenttype());
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_PRICESET;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_PRICESET;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_PRICEMODIFY:
                resource = resourceMicroApi.get(objectid).getResponseEntity();
                if (resource != null)
                    option.setObjecttype(resource.getContenttype());
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_PRICEMODIFY;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_PRICEMODIFY;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_ALLOWSELL:
                resource = resourceMicroApi.get(objectid).getResponseEntity();
                if (resource != null)
                    option.setObjecttype(resource.getContenttype());
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_ALLOWSELL;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_ALLOWSELL;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_DENYSELL:
                resource = resourceMicroApi.get(objectid).getResponseEntity();
                if (resource != null)
                    option.setObjecttype(resource.getContenttype());
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_DENYSELL;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_DENYSELL;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_PAY:
                resource = resourceMicroApi.get(objectid).getResponseEntity();
                if (resource != null)
                    option.setObjecttype(resource.getContenttype());
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_PAY;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_PAY;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_CATALOG_ADD:
                resource = resourceMicroApi.get(objectid).getResponseEntity();
                if (resource != null)
                    option.setObjecttype(resource.getContenttype());
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_CATALOG_ADD;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_CATALOG_ADD;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_CATALOG_DELETE:
                resource = resourceMicroApi.get(objectid).getResponseEntity();
                if (resource != null)
                    option.setObjecttype(resource.getContenttype());
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_CATALOG_DELETE;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_CATALOG_DELETE;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_CATALOG_MODIFY:
                resource = resourceMicroApi.get(objectid).getResponseEntity();
                if (resource != null)
                    option.setObjecttype(resource.getContenttype());
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_CATALOG_MODIFY;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_CATALOG_MODIFY;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_CATALOG_MOVE:
                resource = resourceMicroApi.get(objectid).getResponseEntity();
                if (resource != null)
                    option.setObjecttype(resource.getContenttype());
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_CATALOG_MOVE;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_CATALOG_MOVE;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_SUBNAVI_ADD:
                resource = resourceMicroApi.get(objectid).getResponseEntity();
                if (resource != null)
                    option.setObjecttype(resource.getContenttype());
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_SUBNAVI_ADD;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_SUBNAVI_ADD;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_SUBNAVI_DELETE:
                resource = resourceMicroApi.get(objectid).getResponseEntity();
                if (resource != null)
                    option.setObjecttype(resource.getContenttype());
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_SUBNAVI_DELETE;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_SUBNAVI_DELETE;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_SUBNAVI_MODIFY:
                resource = resourceMicroApi.get(objectid).getResponseEntity();
                if (resource != null)
                    option.setObjecttype(resource.getContenttype());
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_SUBNAVI_MODIFY;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_SUBNAVI_MODIFY;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_REWARD_IN:
                activity = activityMicroApi.get(objectid).getResponseEntity();
                if (activity != null) {
                    option.setObjecttype(activity.getContenttype());
                }
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_REWARD_IN;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_REWARD_IN;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_SCHOOLNAMED:
                resource = resourceMicroApi.get(objectid).getResponseEntity();
                if (resource != null)
                    option.setObjecttype(resource.getContenttype());
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_SCHOOLNAMED;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_SCHOOLNAMED;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_SCHOOLNORMAL:
                resource = resourceMicroApi.get(objectid).getResponseEntity();
                if (resource != null)
                    option.setObjecttype(resource.getContenttype());
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_SCHOOLNORMAL;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_SCHOOLNORMAL;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_COLLECTION_DELETE:
                activity = activityMicroApi.get(objectid).getResponseEntity();
                if (activity != null) {
                    option.setObjecttype(activity.getContenttype());
                }
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_COLLECTION_DELETE;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_COLLECTION_DELETE;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_COLLECTION_MODIFY:
                activity = activityMicroApi.get(objectid).getResponseEntity();
                if (activity != null) {
                    option.setObjecttype(activity.getContenttype());
                }
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_COLLECTION_MODIFY;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_COLLECTION_MODIFY;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_REWARD_DELETE:
                activity = activityMicroApi.get(objectid).getResponseEntity();
                if (activity != null) {
                    option.setObjecttype(activity.getContenttype());
                }
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_REWARD_DELETE;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_REWARD_DELETE;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_REWARD_MODIFY:
                activity = activityMicroApi.get(objectid).getResponseEntity();
                if (activity != null) {
                    option.setObjecttype(activity.getContenttype());
                }
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_REWARD_MODIFY;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_REWARD_MODIFY;
                break;
            case ICommonConstants.OPTION_OPTIONTYPE_AUDIT_DENY:
                resource = resourceMicroApi.get(objectid).getResponseEntity();
                if (resource != null)
                    option.setObjecttype(resource.getContenttype());
                optiontype = IBussinessMicroConstants.OPTION_OPTIONTYPE_AUDIT_DENY;
                optionname = IBussinessMicroConstants.OPTION_OPTIONNAME_AUDIT_DENY;
                break;
            default:
                break;
        }
        option.setOptiontype(optiontype);
        option.setOptionname(optionname);

        optionMicroApi.add(option);
    }

    protected Resource validateResource(Resource resource) {
        if (resource == null) return null;
        JSONObject searchJSON = new JSONObject();
        String subjectid = resource.getSubjectid();
        if(subjectid != null){
            String[] subjectids = subjectid.split(",");
            if(subjectids.length > 1){
                subjectid = subjectids[0];
            }
        }

        searchJSON.put("schoolsectionid", resource.getSchoolsectionid());
        searchJSON.put("subjectid", subjectid);
        searchJSON.put("gradeid", resource.getGradeid());
        searchJSON.put("editiontypeid", resource.getEditiontypeid());
        searchJSON.put("bookmodelid", resource.getBookmodelid());
        searchJSON.put("onelabelid", resource.getOnelabelid());
        searchJSON.put("twolabelid", resource.getTwolabelid());
        searchJSON.put("threelabelid", resource.getThreelabelid());
        searchJSON.put("creator", resource.getCreator());

        searchJSON.put("lang", IBaseMicroConstants.zh_CN);

         ESearchInfo resInfo = dictItemMicroApi.getESearchInfo(searchJSON).getResponseEntity();

        resource.setSchoolsection(resInfo.getSchoolsection());
        resource.setSubject(resInfo.getSubject());
        resource.setGrade(resInfo.getGrade());
        resource.setEditiontype(resInfo.getEditiontype());
        resource.setBookmodel(resInfo.getBookmodel());

        resource.setCreatorname(resInfo.getCreator());
        resource.setOnelabel(resInfo.getOnelabelname());
        resource.setTwolabel(resInfo.getTwolabelname());
        resource.setThreelabel(resInfo.getThreelabelname());

        if (StringUtils.equals(IResourceMicroConstants.RES_SOURCE_GOODS, resource.getIsgoods())) {
            Good good = goodMicroApi.getByContentid(resource.getContentid()).getResponseEntity();
            if (null != good) {
                resource.setGoodinfo(good);
            }
        }

        Audit auditinfo = auditMicroApi.getByContentid(resource.getContentid()).getResponseEntity();
        resource.setAuditinfo(auditinfo);

        return resource;
    }

    protected void validateNavigationNames(Resource resource) {
        String schoolsectionid = resource.getSchoolsectionid();
        String subjectid = resource.getSubjectid();
        String gradeid = resource.getGradeid();
        String editiontypeid = resource.getEditiontypeid();
        String bookmodelid = resource.getBookmodelid();
        if (!StringUtils.isEmpty(schoolsectionid)) {
            resource.setSchoolsection(getNaviNameById(schoolsectionid, "SCHOOL_SECTION"));
        }
        if (!StringUtils.isEmpty(subjectid)) {
            resource.setSubject(getNaviNameById(subjectid, "SUBJECT"));
        }
        if (!StringUtils.isEmpty(gradeid)) {
            resource.setGrade(getNaviNameById(gradeid, "SCHOOL_SECTION"));
        }
        if (!StringUtils.isEmpty(editiontypeid)) {
            resource.setEditiontype(getNaviNameById(editiontypeid, "EDITION"));
        }
        if (!StringUtils.isEmpty(bookmodelid)) {
            resource.setBookmodel(getNaviNameById(bookmodelid, "VOLUME"));
            if (resource.getBookmodel().isEmpty())
                resource.setBookmodel(getNaviNameById(bookmodelid, "TERM"));
        }
    }

    public void fixNavigationParam(Navigation navigation)
    {
        if (StringUtils.isNotEmpty(navigation.getSchoolsectionid()))
            navigation.setSchoolsectionid("," + navigation.getSchoolsectionid() + ",");
        if (StringUtils.isNotEmpty(navigation.getSubjectid()))
            navigation.setSubjectid("," + navigation.getSubjectid() + ",");
        if (StringUtils.isNotEmpty(navigation.getGradeid()))
            navigation.setGradeid("," + navigation.getGradeid() + ",");
        if (StringUtils.isNotEmpty(navigation.getEditiontypeid()))
            navigation.setEditiontypeid("," + navigation.getEditiontypeid() + ",");
        if (StringUtils.isNotEmpty(navigation.getBookmodelid()))
            navigation.setBookmodelid("," + navigation.getBookmodelid() + ",");
    }

    private String getNaviNameById(String dictId, String dictType) {
        String result = "";
        DictItem dictItem = new DictItem();
        dictItem.setDictvalue(dictId);
        dictItem.setDicttypeid(dictType);
        dictItem.setLang(IBaseMicroConstants.zh_CN);
        DictItem item = dictItemMicroApi.getByDictInfo(dictItem).getResponseEntity();
        if (item != null) {
            result = item.getDictname();
        }
        return result;
    }

    protected Resource validateNaviInfoByCatalogids(Resource resource, List<String> catalogids) {
        if (CollectionUtils.isEmpty(catalogids))
            return resource;

        List<Object> subjectnaviinfos = catalogMicroApi.getSubNaviInfoList(catalogids).getPageInfo().getList();
        resource.setSubnaviInfos(subjectnaviinfos);
        return resource;
    }

    protected DictItem getDictItemByName(String dictname, String parentid) {
        DictItem example = new DictItem();
        example.setLang(IBaseMicroConstants.zh_CN);
        example.setParentdictid(parentid==null?"0":parentid);
        example.setDictname(dictname);
        return dictItemMicroApi.getByDictInfo(example).getResponseEntity();
    }

    protected DictItem getDictItemById(String dictid) {
        DictItem example = new DictItem();
        example.setLang(IBaseMicroConstants.zh_CN);
        example.setDictid(dictid);
        return dictItemMicroApi.getByDictInfo(example).getResponseEntity();
    }

    protected Label getLabelByName(String labelname) {
        Label example = new Label();
        example.setLabelname(labelname);
        return labelMicroApi.getByExample(example).getResponseEntity();
    }

    protected String getSharerangeKey(UserLoginInfo userLoginInfo) {
        String sharerangekey = "";
        if (!StringUtils.isEmpty(userLoginInfo.getCastle()))
            sharerangekey += userLoginInfo.getCastle() + ",";
        if (!StringUtils.isEmpty(userLoginInfo.getCity()))
            sharerangekey += userLoginInfo.getCity() + ",";
        if (!StringUtils.isEmpty(userLoginInfo.getArea()))
            sharerangekey += userLoginInfo.getArea() + ",";
        if (!StringUtils.isEmpty(userLoginInfo.getOrgId()))
            sharerangekey += userLoginInfo.getOrgId();

        return sharerangekey;
    }

    protected Response saveMessage(String messageType, String sender, String content, List<String>receivers, String appId, String access_token){
//        JSONObject newsInfo = new JSONObject();
//        newsInfo.put("messageType",messageType);
//        newsInfo.put("sender",sender);
//        newsInfo.put("content",content);
//        newsInfo.put("receivers",receivers);
//        newsInfo.put("appId",appId);
//
//        String strValue;
//        List<Header> headers = new ArrayList<>();
//        headers.add(new BasicHeader("tenant-Id", ecoTenantID));
//        headers.add(new BasicHeader("access-token", access_token));
//
//        try
//        {
//            strValue = HttpClientUtil.doPost2(ecoServerIp + ICommonConstants.ECO_PLATFORM_MESSAGE_INFO, headers, newsInfo, "utf-8");
//            JSONObject jsonResult = JSONObject.parseObject(strValue);
//            JSONObject serverResult = jsonResult.getJSONObject("serverResult");
//
//            if (serverResult != null) {
//                if (!serverResult.getString("resultCode").equalsIgnoreCase(IErrorMessageConstants.OPERATION_SUCCESS)) {
//                    return new Response(serverResult.getString("resultMessage"));
//                }
//                return new Response(serverResult);
//            }
//        }
//        catch (Exception e) { e.printStackTrace(); }

        return new Response();
    }

    public boolean matchRoleId2User(String userid, String roleId)
    {
        List<Role> roleList = userMicroApi.getUserRole(userid).getPageInfo().getList();
        boolean isteacher = false;

        if (roleList == null)
            roleList = new ArrayList<>();

        for(Role role : roleList)
            if (role.getRoleid().equalsIgnoreCase(roleId))
            {
                isteacher = true;
                break;
            }

        return isteacher;
    }

    public boolean matchRoleName2User(String userid, String roleName)
    {
        List<Role> roleList = userMicroApi.getUserRole(userid).getPageInfo().getList();
        boolean isteacher = false;

        if (roleList == null)
            roleList = new ArrayList<>();

        for(Role role : roleList)
            if (role.getRolename().equalsIgnoreCase(roleName))
            {
                isteacher = true;
                break;
            }

        return isteacher;
    }

    protected List<AreaInfo> parseToAreaInfoList(List<Area> areaList) {
        List<AreaInfo> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(areaList)) {
            for (Area area : areaList) {
                AreaInfo areaInfo = new AreaInfo();
                areaInfo.setAreaid(area.getAreaid());
                areaInfo.setAreaname(area.getAreaname());
                areaInfo.setUserids(area.getUserids());

                result.add(areaInfo);
            }
        }
        return result;
    }

    protected void fixResourceQueryTree(QueryTree queryTree){
        String[] keys = {"catalogids", "themeids"};
        int j, len_keys = keys.length;
        for (j = 0; j < len_keys; j++) {
            QueryCondition catCond = queryTree.getQueryCondition(keys[j]);
            if (catCond != null) {
                queryTree.getConditions().remove(catCond);
                if (catCond.getFieldValues() != null && catCond.getFieldValues().length > 0) {
                    Object[] values = catCond.getFieldValues();
                    List<String> ids = new ArrayList<>();
                    int i, len = values.length;
                    for (i = 0; i < len; i++) {
                        String parent = null;
                        if (catCond.getFieldValues()[i] != null)
                            parent = catCond.getFieldValues()[i].toString();
                        if (!StringUtils.isEmpty(parent)) {
                            List<String> allItems;
                            if (j == 0)
                                allItems = catalogMicroApi.getMyAllCatalogs(parent).getPageInfo().getList();
                            else
                                allItems = themeMicroApi.getMyAllThemes(parent).getPageInfo().getList();
                            if (allItems == null)
                                allItems = new ArrayList<>();
                            allItems.add("");
                            ids.addAll(allItems);
                        }
                    }
                    queryTree.addCondition(new QueryCondition(keys[j], QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, ids));
                }
            }
        }
    }

    protected void validateGood (Good good){
        Resource resource = getResourceByProductId(good);
        validateResource(resource);
        resource.setGoodinfo(null);
    }

    protected void validateGoods (List<Good> goods){
        if (goods != null){
            for (Good good : goods)
                validateGood(good);
        }
    }

    protected Resource getResourceByProductId (Good good){
        if (good.getGoodtype().equalsIgnoreCase(IBussinessMicroConstants.GOOD_TYPE_RESOURCE)) {
            Resource resource = resourceMicroApi.get(good.getProductid()).getResponseEntity();
            good.setResource(resource);
            if (resource != null)
                resource.setGoodinfo(good);
            return resource;
        }
        return null;
    }

    protected Good getSimpleGoodByContentId (Resource resource){
        Good goodInfo = goodMicroApi.getByContentid(resource.getContentid()).getResponseEntity();
        if (goodInfo != null)
            goodInfo.setResource(resource);
        resource.setGoodinfo(goodInfo);
        return goodInfo;
    }

    protected Good getGoodFromHashMap (Resource resource){
        JSONObject goodJSON = new JSONObject();
        if(resource.getIsgoods() == null || resource.getIsgoods() == "0") return null;
        Map<String, Object> goodMap = (Map<String, Object>)resource.getGoodinfo();
        Set<Map.Entry<String, Object>> entrySet = goodMap.entrySet();
        for(Map.Entry<String, Object> entry : entrySet)
            goodJSON.put(entry.getKey(), entry.getValue());

        Good goodinfo = goodJSON.toJavaObject(Good.class);
        return goodinfo;
    }

    protected void fixGoodQueryTree(QueryTree queryTree){
        if (queryTree.getQueryCondition("activityids") != null || queryTree.getQueryCondition("activityid") != null || queryTree.getQueryCondition("activitytype") != null){
            QueryCondition idsCond = queryTree.getQueryCondition("activityids");
            QueryCondition idCond = queryTree.getQueryCondition("activityid");
            QueryCondition typeCond = queryTree.getQueryCondition("activitytype");

            QueryTree activityQuery = new QueryTree();
            if (idsCond != null) {
                activityQuery.addCondition(idsCond);
                queryTree.getConditions().remove(idsCond);
            }
            if (idCond != null) {
                activityQuery.addCondition(idCond);
                queryTree.getConditions().remove(idCond);
            }
            if (typeCond != null) {
                activityQuery.addCondition(typeCond);
                queryTree.getConditions().remove(typeCond);
            }
            List<String> contentids;
            PageInfo<String> page  = resourceMicroApi.getResIdsByActivity(queryTree).getPageInfo();
            if (page != null)
                 contentids = page.getList();
            else {
                contentids = new ArrayList<>();
                contentids.add("");
                contentids.add("");
            }
            queryTree.addCondition(new QueryCondition("contentid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, contentids));
        }
    }
}