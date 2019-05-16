package com.tianwen.springcloud.ecrapi.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.base.response.ServerResult;
import com.tianwen.springcloud.commonapi.log.SystemControllerLog;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.UserRestApi;
import com.tianwen.springcloud.ecrapi.constant.ICommonConstants;
import com.tianwen.springcloud.ecrapi.constant.IErrorMessageConstants;
import com.tianwen.springcloud.ecrapi.util.CommonUtil;
import com.tianwen.springcloud.ecrapi.util.HttpClientUtil;
import com.tianwen.springcloud.ecrapi.util.captcha.color.SingleColorFactory;
import com.tianwen.springcloud.ecrapi.util.captcha.predefined.*;
import com.tianwen.springcloud.ecrapi.util.captcha.service.Captcha;
import com.tianwen.springcloud.ecrapi.util.captcha.service.ConfigurableCaptchaService;
import com.tianwen.springcloud.ecrapi.util.captcha.word.RandomWordFactory;
import com.tianwen.springcloud.microservice.activity.entity.UserActivityCountInfo;
import com.tianwen.springcloud.microservice.base.entity.*;
import com.tianwen.springcloud.microservice.operation.entity.Member;
import com.tianwen.springcloud.microservice.resource.entity.UserCountInfo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;

@RestController
@RequestMapping(value = "/user")
public class UserRestController extends BaseRestController implements UserRestApi
{
    private static ConfigurableCaptchaService cs = new ConfigurableCaptchaService();
    private static Random random = new Random();
    private Hashtable<String, String> memCache = new Hashtable<>(1000);
    private Hashtable<String, String> tokenCache = new Hashtable<>(1000);
    static {
        cs.setColorFactory(new SingleColorFactory(new Color(25, 60, 170)));
        RandomWordFactory wf = new RandomWordFactory();

        wf.setCharacters("23456789abcdefghigkmnpqrstuvwxyzABCDEFGHIGKLMNPQRSTUVWXYZ");
        wf.setMaxLength(4);
        wf.setMinLength(4);
        cs.setWordFactory(wf);
    }

    @Override
    public Response<Map<String, Object>> requestCaptcha() throws Exception{
        cs.setHeight(60);
        cs.setWidth(200);
        RandomWordFactory rwf = new RandomWordFactory();
        rwf.setMinLength(4);
        rwf.setMaxLength(4);
        cs.setWordFactory(rwf);
        switch (random.nextInt(5)) {
            case 0:
                cs.setFilterFactory(new CurvesRippleFilterFactory(cs.getColorFactory()));
                break;
            case 1:
                cs.setFilterFactory(new MarbleRippleFilterFactory());
                break;
            case 2:
                cs.setFilterFactory(new DoubleRippleFilterFactory());
                break;
            case 3:
                cs.setFilterFactory(new WobbleRippleFilterFactory());
                break;
            case 4:
                cs.setFilterFactory(new DiffuseRippleFilterFactory());
                break;
            default:
                break;
        }

        Captcha cap = cs.getCaptcha();
        String capCode = cap.getChallenge();
        String verifyKey =CommonUtil.convertToMd5(capCode, true);
        String homePath = httpSession.getServletContext().getRealPath("/") + "capimage/";

        boolean isDirExist = new File(homePath).isDirectory();
        if (isDirExist == false) {
            boolean res = new File(homePath).mkdir();
            if (!res) {
                return new Response<>("", "");
            }
        }

        File file = new File(homePath + verifyKey + ".png");

        memCache.put(verifyKey, capCode);
        ImageIO.write(cap.getImage(), "png", file);

        Map<String, Object>result = new HashMap<String, Object>();
        result.put("verifykey", verifyKey);
        result.put("captchaUrl", getRootURL() + "/capimage/" + verifyKey + ".png");

        return new Response<>(result);
    }

    public String insertClass(String classId, String access_token,String userTenantId)
    {
        String resultMessage = IErrorMessageConstants.OPERATION_SUCCESS_MESSAGE;
        JSONObject params = new JSONObject();
        String strValue;

        params.put("classId", classId);

        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("access-token", access_token));
        headers.add(new BasicHeader("tenant-Id", userTenantId));

        strValue = HttpClientUtil.doPost2(ecoServerIp+ICommonConstants.ECO_PLATFORM_CLASS_INFO, headers, params, "utf-8");
        JSONObject classInfo = JSONObject.parseObject(strValue);
        JSONObject serverResult = classInfo.getJSONObject("serverResult");
        if (!serverResult.getString("resultCode").equalsIgnoreCase(IErrorMessageConstants.OPERATION_SUCCESS)) {
            return serverResult.getString("resultMessage");
        }

        ClassInfo classData = new ClassInfo();
        JSONObject classJson = classInfo.getJSONObject("responseEntity");

        if (classJson == null)
            return resultMessage;

        classData.setClassid(classJson.getString("classId"));
        classData.setName(classJson.getString("name"));
        {
            String sectionName = classJson.getString("section");
            QueryTree queryTree = new QueryTree();
            queryTree.addCondition(new QueryCondition("dictvalue", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, sectionName));
            queryTree.addCondition(new QueryCondition("dicttypeid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "SCHOOL_SECTION"));
            List<DictItem> items = dictItemMicroApi.getList(queryTree).getPageInfo().getList();
            if (CollectionUtils.isEmpty(items))
                return resultMessage;
            classData.setSchoolsectionid(items.get(0).getDictid());
        }
        {
            String gradeName = classJson.getString("grade");
            QueryTree queryTree = new QueryTree();
            queryTree.addCondition(new QueryCondition("dictvalue", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, gradeName));
            queryTree.addCondition(new QueryCondition("parentdictid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, classData.getSchoolsectionid()));
            queryTree.addCondition(new QueryCondition("dicttypeid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "SCHOOL_SECTION"));
            List<DictItem> items = dictItemMicroApi.getList(queryTree).getPageInfo().getList();
            if (CollectionUtils.isEmpty(items))
                return resultMessage;
            classData.setGradeid(items.get(0).getDictvalue());
        }
        classData.setDescription(classJson.getString("classDescription"));
        classData.setLastnamemodifytime(classJson.getTimestamp("classNameModifyTime"));
        classData.setCreatetime(classJson.getTimestamp("createTime"));
        classData.setCreator(classJson.getString("creatorId"));
        classData.setLastmodifytime(classJson.getTimestamp("lastmodifyTime"));
        classData.setOrgid(classJson.getString("orgId"));
        classData.setSchoolstartdate(classJson.getTimestamp("schoolStartDate"));
        classData.setStatus(classJson.getString("status"));
        classData.setExtinfo(classJson.getJSONObject("extInfo"));

        if (!StringUtils.isEmpty(classData.getClassid()))
        {
            if (classMicroApi.get(classData.getClassid()).getResponseEntity() != null)
                classMicroApi.update(classData).getResponseEntity();
            else
                classMicroApi.add(classData).getResponseEntity();
        }
        params.remove("classId");

        return resultMessage;
    }

    public String insertTeacher(String userId, String access_token,String userTenantId)
    {
        String resultMessage = IErrorMessageConstants.OPERATION_SUCCESS_MESSAGE;

        if (ICommonConstants.SERVER_SYSTEM_TYPE == 0)
        {
            Response<Teacher> resp = new Response<>();

            Teacher teacher = new Teacher();
            teacher.setUserid(userId);
            teacher.setJob_status("0");
            teacher.setCreatetime(new Timestamp(System.currentTimeMillis()));
            teacher.setLastmodifytime(new Timestamp(System.currentTimeMillis()));
            teacher.setOrderno(1);
            if (teacherMicroApi.get(teacher.getUserid()).getResponseEntity() == null)
                resp = teacherMicroApi.add(teacher);

            return resp.getServerResult().getResultCode();
        }

        JSONObject params = new JSONObject();
        String strValue;

        params.put("id", userId);

        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("access-token", access_token));
        headers.add(new BasicHeader("tenant-Id", userTenantId));

        strValue = HttpClientUtil.doPost2(ecoServerIp + ICommonConstants.ECO_PLATFORM_TEACHER_INFO, headers, params, "utf-8");
        JSONObject jsonResult = JSONObject.parseObject(strValue);
        JSONObject serverResult = jsonResult.getJSONObject("serverResult");
        if (!serverResult.getString("resultCode").equalsIgnoreCase(IErrorMessageConstants.OPERATION_SUCCESS)) {
            return serverResult.getString("resultMessage");
        }

        JSONObject teacherInfo = jsonResult.getJSONObject("responseEntity");
        Teacher teacher = new Teacher();

        teacher.setUserid(userId);
        teacher.setOrgid(teacherInfo.getString("orgId"));
        teacher.setJob_status(teacherInfo.getString("jobStatus"));
        teacher.setCreatetime(teacherInfo.getTimestamp("createTime"));
        teacher.setLastmodifytime(teacherInfo.getTimestamp("lastModifyTime"));
        teacher.setExtinfo(teacherInfo.getJSONObject("extInfo"));

        if (!StringUtils.isEmpty(teacher.getUserid()))
        {
            if (teacherMicroApi.get(teacher.getUserid()).getResponseEntity() != null)
                teacher = teacherMicroApi.update(teacher).getResponseEntity();
            else {
                teacher.setOrderno(0);
                teacher = teacherMicroApi.add(teacher).getResponseEntity();
            }
        }
        params.remove("id");

        if (!StringUtils.isEmpty(teacher.getUserid()))
        {
            headers.add(new BasicHeader("x-user-account", teacher.getUserid()));
            headers.add(new BasicHeader("current-org-id", teacher.getOrgid()));
            params.put("userId", teacher.getUserid());
            strValue = HttpClientUtil.doPost2(ecoServerIp + ICommonConstants.ECO_PLATFORM_TEACHING_INFO, headers, params, "utf-8");
            JSONObject result = JSONObject.parseObject(strValue);
            JSONObject pageInfo = result.getJSONObject("pageInfo");
            JSONArray teachingList = pageInfo.getJSONArray("list");

            if (teachingList != null) {
                for (int i = 0; i < teachingList.size(); i++) {
                    Teaching teaching = new Teaching();
                    JSONObject teachingInfo = teachingList.getJSONObject(i);
                    teaching.setTeachid(teachingInfo.getString("teachId"));
                    teaching.setTeacher(teachingInfo.getString("userId"));
                    teaching.setClassid(teachingInfo.getString("classId"));
                    teaching.setSubjectid(teachingInfo.getString("subjectId"));
                    teaching.setCreatetime(teachingInfo.getTimestamp("createTime"));
                    if (!StringUtils.isEmpty(teaching.getTeachid())) {
                        if (teachingMicroApi.get(teaching.getTeachid()).getResponseEntity() != null)
                            teaching = teachingMicroApi.update(teaching).getResponseEntity();
                        else
                            teaching = teachingMicroApi.add(teaching).getResponseEntity();

                        resultMessage = insertClass(teaching.getClassid(), access_token,userTenantId);
                        if (!resultMessage.equalsIgnoreCase(IErrorMessageConstants.OPERATION_SUCCESS_MESSAGE)) {
                            return resultMessage;
                        }
                    }
                }
                params.remove("userId");
            }
        }

        return resultMessage;
    }

    @Override
    public Response getTeacherById(@PathVariable(value = "userid") String userid) {
        UserLoginInfo teacher = userMicroApi.get(userid).getResponseEntity();
        if (teacher == null) return null;
        Integer contribution = integralMicroApi.getUserContribution(teacher.getUserId()).getResponseEntity();
        teacher.setContribution(contribution);
        UserCountInfo countInfo = resourceMicroApi.getCountInfoByUserId(userid).getResponseEntity();
        if (countInfo != null) {
            teacher.setTotalcollectiontimes(countInfo.getCollectiontimes());
            teacher.setTotalvotetimes(countInfo.getVotetimes());
            teacher.setTotaldowntimes(countInfo.getDowntimes());
            teacher.setTotalviewtimes(countInfo.getViewtimes());
        }
        else {
            teacher.setTotalcollectiontimes(0);
            teacher.setTotalvotetimes(0);
            teacher.setTotaldowntimes(0);
            teacher.setTotalviewtimes(0);
        }
        QueryTree countTree = new QueryTree();
        countTree.addCondition(new QueryCondition("creator", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, userid));
        countTree.addCondition(new QueryCondition("isgoods", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "1"));
        Long value = resourceMicroApi.getCount(countTree).getResponseEntity();
        if (value == null)
            value = Long.valueOf(0);
        teacher.setGoodscount(value.intValue());
        return new Response(teacher);
    }

    @Override
    public Response autoLogin(@RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> userResp = getUserByToken(token);
        if (userResp.getServerResult().getResultCode().equalsIgnoreCase(IErrorMessageConstants.OPERATION_SUCCESS))
        {
            UserLoginInfo userInfo = userResp.getResponseEntity();
            this.giveBonusFirstLogin(userInfo);
            userInfo.setCurrentLoginTime(new Date(System.currentTimeMillis()));
            userMicroApi.update(userInfo);
            userResp.setResponseEntity(userInfo);
        }
        return userResp;
    }

    public String insertStudent(String userId, String access_token,String userTenantId)
    {
        String resultMessage;
        JSONObject params = new JSONObject();
        String strValue;

        params.put("id", userId);

        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("access-token", access_token));
        headers.add(new BasicHeader("tenant-Id", userTenantId));

        strValue = HttpClientUtil.doPost2(ecoServerIp + ICommonConstants.ECO_PLATFORM_STUDENT_INFO, headers, params, "utf-8");
        JSONObject studentInfo = JSONObject.parseObject(strValue);
        JSONObject serverResult = studentInfo.getJSONObject("serverResult");
        if (!serverResult.getString("resultCode").equalsIgnoreCase(IErrorMessageConstants.OPERATION_SUCCESS)) {
            return serverResult.getString("resultMessage");
        }

        Student student;
        student = studentInfo.getJSONObject("responseEntity").toJavaObject(Student.class);
        if (!StringUtils.isEmpty(student.getUserid())) {
            if (studentMicroApi.get(student.getUserid()).getResponseEntity() != null)
                student = studentMicroApi.update(student).getResponseEntity();
            else
                student = studentMicroApi.add(student).getResponseEntity();
        }
        params.remove("id");

        resultMessage = insertClass(student.getClassid(), access_token,userTenantId);

        return resultMessage;
    }

    public String insertSchool(String orgId, String access_token,String  userTenantId)
    {
        String resultMessage = IErrorMessageConstants.OPERATION_SUCCESS_MESSAGE;

        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("access-token", access_token));
        headers.add(new BasicHeader("tenant-Id", userTenantId));


        String strValue = HttpClientUtil.doGet2(ecoServerIp + ICommonConstants.ECO_PLATFORM_SCHOOL_INFO + "/" + orgId, "", headers.toArray(new Header[headers.size()]));
        JSONObject jsonResult = JSONObject.parseObject(strValue);
        JSONObject serverResult = jsonResult.getJSONObject("serverResult");
        if (!serverResult.getString("resultCode").equalsIgnoreCase(IErrorMessageConstants.OPERATION_SUCCESS)) {
            return serverResult.getString("resultMessage");
        }

        JSONObject schoolInfo = jsonResult.getJSONObject("responseEntity");
        School school = new School();

        school.setOrgid(schoolInfo.getString("orgId"));
        school.setClassnamingrule(schoolInfo.getString("classNamingRule"));
        school.setCreatorid(schoolInfo.getString("creatorId"));
        school.setCreatetime(schoolInfo.getTimestamp("createTime"));
        school.setLastmodifytime(schoolInfo.getTimestamp("lastModifyTime"));
        school.setPhotopath(null);
        school.setDescription(null);
        school.setOrderno(null);

        if (!StringUtils.isEmpty(school.getOrgid()))
        {
            Response<School> resp;
            if (schoolMicroApi.get(school.getOrgid()).getResponseEntity() != null)
                resp = schoolMicroApi.update(school);
            else {
                school.setIsnamed("0");
                resp = schoolMicroApi.add(school);
            }

            resultMessage = resp.getServerResult().getResultMessage();
        }

        return resultMessage;
    }

    String insertOrganization(String orgId, String access_token,String userTenantId)
    {
        String resultMessage = IErrorMessageConstants.OPERATION_SUCCESS_MESSAGE;

        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("access-token", access_token));
        headers.add(new BasicHeader("tenant-Id", userTenantId));
        String strValue = HttpClientUtil.doGet2(ecoServerIp + ICommonConstants.ECO_PLATFORM_ORGANIZATION_INFO + "/" + orgId, "", headers.toArray(new Header[headers.size()]));
        JSONObject jsonResult = JSONObject.parseObject(strValue);
        JSONObject serverResult = jsonResult.getJSONObject("serverResult");
        if(serverResult == null)
            return "Organization_info get error";
        if (!serverResult.getString("resultCode").equalsIgnoreCase(IErrorMessageConstants.OPERATION_SUCCESS)) {
            return serverResult.getString("resultMessage");
        }

        JSONObject orgInfo = jsonResult.getJSONObject("responseEntity");
        Organization org = new Organization();

        org.setOrgid(orgInfo.getString("orgId"));
        org.setOrgcode(orgInfo.getString("orgCode"));
        org.setOrgname(orgInfo.getString("orgName"));
        org.setParentorgid(orgInfo.getString("parentOrgId"));
        org.setOrgtype(orgInfo.getString("orgType"));
        org.setOrgaddr(orgInfo.getString("orgAddr"));
        org.setZipcode(orgInfo.getString("zipCode"));
        org.setOrgmanager(orgInfo.getString("orgManager"));
        org.setLinkman(orgInfo.getString("linkMan"));
        org.setLinktel(orgInfo.getString("linkTel"));
        org.setEmail(orgInfo.getString("email"));
        org.setWeburl(orgInfo.getString("webUrl"));
        org.setStatus(orgInfo.getString("status"));
        org.setLinenum(orgInfo.getInteger("lineNum"));
        org.setCreatetime(orgInfo.getTimestamp("createTime"));
        org.setLastmodifytime(orgInfo.getTimestamp("lastModifyTime"));
        org.setDescription(orgInfo.getString("description"));
        org.setCreatorid(orgInfo.getString("creatorId"));
        org.setLastmodifierid(orgInfo.getString("lastModifierId"));
        org.setAreaid(orgInfo.getString("areaId"));
        org.setTenantid(orgInfo.getString("tenantId"));
        org.setLogofileid(orgInfo.getString("logoFileId"));
        org.setOrgdomain(orgInfo.getString("orgDomain"));
        org.setSynurl(orgInfo.getString("synUrl"));
        org.setSyncode(orgInfo.getString("synCode"));

        if (!StringUtils.isEmpty(org.getOrgid()))
        {
            Response<Organization> resp;
            if (organizationMicroApi.get(org.getOrgid()).getResponseEntity() != null)
                resp = organizationMicroApi.update(org);
            else
                resp = organizationMicroApi.add(org);

            resultMessage = resp.getServerResult().getResultMessage();
        }

        return resultMessage;
    }

    String registerMember(String userid)
    {
        String resultMessage = IErrorMessageConstants.OPERATION_SUCCESS;

        Member memberData = memberMicroApi.getByUserId(userid).getResponseEntity();
        if (memberData == null || StringUtils.isEmpty(memberData.getMemberid()))
        {
            memberData = new Member();
            memberData.setMemberno("3984704849");
            memberData.setUserid(userid);
            memberData.setStarttime(new Timestamp(System.currentTimeMillis()));
            memberData.setEndtime(new Timestamp(System.currentTimeMillis() + 1000 * 86400 * 365));
            memberData.setMemberlevel("1");
            memberData.setMembertype("40");
            memberData.setMerchantno("234893MCT2390");
            memberData.setStatus("0");
            memberData.setTotintegral(0);memberData.setFrozenintegral(0);
            memberData.setUseintegral(0);memberData.setUsedintegral(0);
            memberData.setCreatetime(new Timestamp(System.currentTimeMillis()));
            memberData.setLastmodifytime(new Timestamp(System.currentTimeMillis()));
            Response<Member> resp = memberMicroApi.add(memberData);
            resultMessage = resp.getServerResult().getResultMessage();
        }

        return resultMessage;
    }

    @Override
    @ApiOperation(value = "用户登录", notes = "用户登录")
    @ApiImplicitParam(name = "param", value = "用户登录信息", required = true, dataType = "Map<String, Object>", paramType = "body")
    @SystemControllerLog(description = "用户登录")
    public Response<UserLoginInfo> loginUser(@RequestBody  Map<String, Object> param) throws Exception{
        ServerResult serverResult = new ServerResult();
        Response resp = new Response<>();

        String loginname =  param.get("username").toString();
        String password =  param.get("password").toString();
        String captcha =  param.get("capcha").toString();
        String verifyKey =  param.get("verifykey").toString();
        boolean autologin;
        String role;

        if (param.get("kind") == null)
            role = "";
        else
            role = param.get("kind").toString();

        if (role.isEmpty()) role = "manager";

        if (param.get("autologin") == null)
            autologin = false;
        else
            autologin = !param.get("autologin").toString().equals("0");

        if (!captcha.equals(memCache.get(verifyKey))) {
            serverResult.setResultCode("-4");
            serverResult.setResultMessage("验证码错误.");
        }
        else if (ICommonConstants.SERVER_SYSTEM_TYPE == 1) /* ECO MODE*/ {
            Map<String, String> map = new HashMap<>();
            map.put("client_id", ecoPlatformClientID);
            map.put("client_secret", ecoPlatformClientSecret);
            map.put("grant_type", "password");
            map.put("username", loginname);
            map.put("password", password);

            resp = ecoLogin(map, role);

            File file = new File(httpSession.getServletContext().getRealPath("/") + "capimage/" + verifyKey + ".png");
            file.delete();

            memCache.remove(verifyKey);

            return resp;
        }
        else {
            QueryTree userQuery = new QueryTree();
            userQuery.addCondition(new QueryCondition("loginname", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, loginname));
            userQuery.getPagination().setNumPerPage(1);
            List<UserLoginInfo> users = userMicroApi.getList(userQuery).getPageInfo().getList();

            UserLoginInfo user;

            if (!CollectionUtils.isEmpty(users))
                user = users.get(0);
            else
                user = null;

            if (user == null || StringUtils.isEmpty(user.getUserId())) {
                return new Response<>(IErrorMessageConstants.ERR_CODE_LOGIN_NAME, IErrorMessageConstants.ERR_MSG_LOGIN_NAME);
            }
            if (user == null) {
                serverResult.setResultCode("-1");
                serverResult.setResultMessage("登录失败");
            } else if (!password.equals(user.getStaticPassword())) {
                serverResult.setResultCode("-1");
                serverResult.setResultMessage("登录失败");

                user.setLastLoginFailedTime(new Timestamp(System.currentTimeMillis()));

                Integer failCount = user.getLoginFailedCount();
                if (failCount == null)
                    user.setLoginFailedCount(1);
                else
                    user.setLoginFailedCount(failCount.intValue() + 1);
                //user.setBirthday(null);
                userMicroApi.update(user);
            } else if (!role.isEmpty() && !matchRoleName2User(user.getUserId(), role)) {
                serverResult.setResultCode("-5");
                serverResult.setResultMessage("用户角色过错");
            } else {
                registerMember(user.getUserId());

                if (role.equals("teacher"))
                    insertTeacher(user.getUserId(), null,user.getBranchCode());

                // Before update current login time check last logged in time and give first login bonus
                giveBonusFirstLogin(user);

                user.setLastLoginTime(user.getCurrentLoginTime());
                user.setCurrentLoginTime(new Timestamp(System.currentTimeMillis()));

                Timestamp current = new Timestamp(System.currentTimeMillis());
                String access_token = CommonUtil.convertToMd5(current.toString(), true);
                user.setToken(access_token);
                user.setTokenrefreshedtime(new Date(System.currentTimeMillis()));

                if (autologin == false)
                    user.setExpiresin(new Long(43200000));
                else
                    user.setExpiresin(new Long(1000 * 3600 * 24 * 365 * 10));

                //user.setBirthday(null);
                userMicroApi.update(user);

                tokenCache.put(access_token, user.getUserId());

                serverResult.setResultCode(IErrorMessageConstants.OPERATION_SUCCESS);
                serverResult.setResultMessage(IErrorMessageConstants.OPERATION_SUCCESS_MESSAGE);

                resp.setResponseEntity(user);

                memCache.remove(verifyKey);
            }
        }

        resp.setServerResult(serverResult);
        return resp;
    }

    @Override
    @ApiOperation(value = "用户登录", notes = "微信QQ登录")
    public Response<UserLoginInfo> loginSSO(@RequestBody Map<String, Object> param) {
        String role = "";
        String accredit_code =  param.get("accredit_code").toString();

        if (param.get("kind") != null)
            role = param.get("kind").toString();

        if (ICommonConstants.SERVER_SYSTEM_TYPE == 1) {
            // Get weixin access_token
            Map<String, String> map = new HashMap<>();

            map.put("client_id", ecoPlatformClientID);
            map.put("client_secret", ecoPlatformClientSecret);
            map.put("grant_type", "client_credentials");
            map.put("accredit_code", accredit_code);

            return ecoLogin(map, role);
        }

        // wechat login is not supported
        ServerResult serverResult = new ServerResult();
        Response resp = new Response<>();

        serverResult.setResultCode("-1");
        serverResult.setResultMessage("我们不支持微信QQ登录。");

        resp.setServerResult(serverResult);
        return resp;
    }

    private Response<UserLoginInfo> ecoLogin(Map<String, String> params, String userRole) {
        Response resp = new Response<>();
        ServerResult serverResult = new ServerResult();

        String retVal = HttpClientUtil.doPost(ecoServerIp + ICommonConstants.ECO_PLATFORM_AUTH_URL, null, params, "utf-8");
        JSONObject jsStr = JSONObject.parseObject(retVal);
        String errCode = jsStr.getString("errorCode")
                ;

        if (errCode == null) {
            String access_token = jsStr.getString("access_token"); // 1a469689464ef57dfe1f5ded219e1092
            long expires_in = jsStr.getLong("expires_in"); // 43199995
            String refresh_token = jsStr.getString("refresh_token"); // 6be669ed13936b4e11ed9faa04c2b919
            retVal = HttpClientUtil.doGet(ecoServerIp + ICommonConstants.ECO_PLATFORM_USERBYTOKEN_URL + "/" + access_token, "");
            JSONObject jsonObject = JSONObject.parseObject(retVal);
            JSONObject jsonUser = jsonObject.getJSONObject("responseEntity");
            String  userTenantId =jsonUser.getString("branchCode");


            // check user role
            boolean isRoleExist = true;
            JSONArray roleIdList = jsonUser.getJSONArray("roleIdList");
            List<String> roleArray = roleIdList.toJavaList(String.class);


            if (userRole.equals("teacher")) {
                // teacher
                if (!roleArray.contains(ICommonConstants.ECO_USER_ROLE_TEACHER)) {
                    isRoleExist = false;
                    serverResult.setResultCode("-1");
                    serverResult.setResultMessage(IErrorMessageConstants.ERR_MSG_USER_ROLE_INCORRECT);
                }
            } else if (userRole.equals("student")) {
                //student
                if (!roleArray.contains(ICommonConstants.ECO_USER_ROLE_STUDENT)) {
                    isRoleExist = false;
                    serverResult.setResultCode("-1");
                    serverResult.setResultMessage(IErrorMessageConstants.ERR_MSG_USER_ROLE_INCORRECT);
                }
            } else if (userRole.equals("manager")){
                // manager
                if (!roleArray.contains(ecoUserRoleManager)) {
                    isRoleExist = false;
                    serverResult.setResultCode("-1");
                    serverResult.setResultMessage(IErrorMessageConstants.ERR_MSG_USER_NOT_EXIST);
                }
            }
            else {
                // determine user role automatically
                if (roleArray.contains(ICommonConstants.ECO_USER_ROLE_TEACHER)) {
                    userRole = "teacher";
                }
                else if (roleArray.contains(ICommonConstants.ECO_USER_ROLE_STUDENT)) {
                    userRole = "student";
                }
                else {
                    isRoleExist = false;
                    serverResult.setResultCode("-1");
                    serverResult.setResultMessage(IErrorMessageConstants.ERR_MSG_USER_ROLE_INCORRECT);
                }
            }

            // if this user is or not registered in ECR platform
            QueryTree userQuery = new QueryTree();
            userQuery.addCondition(new QueryCondition("userid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, jsonUser.getString("userId")));
            userQuery.addCondition(new QueryCondition("loginname", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, jsonUser.getString("loginName")));
            userQuery.getPagination().setNumPerPage(1);
            List<UserLoginInfo> users = userMicroApi.getList(userQuery).getPageInfo().getList();

            UserLoginInfo user;
            boolean isUserExist = true;

            if (!CollectionUtils.isEmpty(users))
                user = users.get(0);
            else
                user = null;

            if (isRoleExist) {
                if (user == null) {
                    isUserExist = false;
                    user = new UserLoginInfo();

                    user.setUserId(jsonUser.getString("userId"));
                    user.setRealName(jsonUser.getString("realName"));
                    user.setLoginName(jsonUser.getString("loginName"));
                    user.setLoginEmail(jsonUser.getString("loginEmail"));
                    user.setLoginMobile(jsonUser.getString("loginMobile"));
                    user.setStaticPassword(jsonUser.getString("staticPassword"));
                    user.setOrgId(jsonUser.getString("orgId"));
                    user.setBirthday(jsonUser.getString("birthday"));
                    user.setImagePath(jsonUser.getString("imagePath"));
                    user.setFileId(jsonUser.getString("fileId"));
                    user.setIdCardNo(jsonUser.getString("idCardNo"));
                    user.setSex(jsonUser.getString("sex"));
                    user.setBranchCode(jsonUser.getString("branchCode"));
                } else {
                    user.setRealName(jsonUser.getString("realName"));
                    user.setLoginName(jsonUser.getString("loginName"));
                    user.setLoginEmail(jsonUser.getString("loginEmail"));
                    user.setLoginMobile(jsonUser.getString("loginMobile"));
                    user.setStaticPassword(jsonUser.getString("staticPassword"));
                    user.setOrgId(jsonUser.getString("orgId"));
                    user.setBirthday(jsonUser.getString("birthday"));
                    user.setImagePath(jsonUser.getString("imagePath"));
                    user.setFileId(jsonUser.getString("fileId"));
                    user.setIdCardNo(jsonUser.getString("idCardNo"));
                    user.setSex(jsonUser.getString("sex"));
                    user.setBranchCode(jsonUser.getString("branchCode"));
                }

                // Give bonus login before set current login time - which means give bonus login for the first login of one day
                giveBonusFirstLogin(user);

                user.setLastLoginTime(user.getCurrentLoginTime());
                user.setCurrentLoginTime(new Timestamp(System.currentTimeMillis()));
                user.setToken(access_token);
                //user.setExpiresin(expires_in);
                user.setExpiresin(new Long(1000 * 3600 * 24 * 365 * 10));
                user.setRefreshtoken(refresh_token);
                user.setTokenrefreshedtime(new Date(System.currentTimeMillis()));

                if (isUserExist) {
                    userMicroApi.update(user);
                } else {
                    userMicroApi.insert(user);
                }
                long timeout = user.getExpiresin() / 1000;
                httpSession.setMaxInactiveInterval((int) timeout);

                tokenCache.put(access_token, user.getUserId());

                roleArray.add("");
                Map<String, Object> roleData = new HashMap<>();
                roleData.put("userid", user.getUserId());
                roleData.put("roleids", roleArray);
                userMicroApi.deleteUserRole(user.getUserId());
                userMicroApi.insertUserRole(roleData);

                String resultMessage = IErrorMessageConstants.OPERATION_SUCCESS_MESSAGE;
                if (userRole.equals("teacher")) {
                    // teacher
                    resultMessage = insertTeacher(user.getUserId(), access_token,userTenantId);
                } else if (userRole.equals("student")) {
                    //student
                    resultMessage = insertStudent(user.getUserId(), access_token,userTenantId);
                }

                if (!resultMessage.equalsIgnoreCase(IErrorMessageConstants.OPERATION_SUCCESS_MESSAGE)) {
                    serverResult.setResultCode("-1");
                    serverResult.setResultMessage(resultMessage);
                }

                String orgId = user.getOrgId();
                insertOrganization(orgId, access_token,userTenantId);
                insertSchool(orgId, access_token,userTenantId);

                if (!resultMessage.equalsIgnoreCase(IErrorMessageConstants.OPERATION_SUCCESS_MESSAGE)) {
                    serverResult.setResultCode("-1");
                    serverResult.setResultMessage(resultMessage);
                } else {
                    registerMember(user.getUserId());

                    serverResult.setResultCode(IErrorMessageConstants.OPERATION_SUCCESS);
                    serverResult.setResultMessage(IErrorMessageConstants.OPERATION_SUCCESS_MESSAGE);

                    resp.setResponseEntity(user);
                }
            }
        } else if (errCode.equalsIgnoreCase("8002")) {
            serverResult.setResultCode("-1");
            serverResult.setResultMessage(IErrorMessageConstants.ERR_MSG_USER_NOT_EXIST);
        } else {
            serverResult.setResultCode("-1");
            serverResult.setResultMessage(jsStr.getString("errorMesage"));
        }

        resp.setServerResult(serverResult);

        return resp;
    }

    private JSONObject setBadWordData(UserLoginInfo user) {
        JSONObject json = (JSONObject)JSONObject.toJSON(user);

        List<String> badwords = new ArrayList<>();

        Config badWordConf = configMicroApi.get(BADWORD_NAME).getResponseEntity();

        String wordListFile = ecoServerIp+ICommonConstants.ECO_FILE_DOWNLOAD_URL + badWordConf.getConfvalue();
        try {
            URL url = new URL(wordListFile);

            BufferedReader buffer = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            String line;

            while ((line = buffer.readLine()) != null)
                badwords.add(line);
        }
        catch (Exception e){
        }
        json.put("badwords", StringUtils.join(badwords, ","));

        return json;
    }

    @Override
    public Response logoutUser(@RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> userResp = getUserByToken(token);
        Response response = new Response();

        if (userResp.getServerResult().getResultCode().equalsIgnoreCase(IErrorMessageConstants.OPERATION_SUCCESS))
        {
            UserLoginInfo user = userResp.getResponseEntity();
            user.setExpiresin(new Long(43200000));
            user.setToken("");
            userMicroApi.update(user);
            tokenCache.remove(token);
        }

        return response;
    }

    @Override
    public Response<UserLoginInfo> getUserInfo(@PathVariable(value = "userid") String userid) {
        UserLoginInfo user = getUserDetailData(userid);
        return new Response<>(user);
    }

    @Override
    public Response<UserActivityCountInfo> getUserCountInfo(@PathVariable(value = "userid") String userid) {
        UserActivityCountInfo info = new UserActivityCountInfo();
        info.setUserid(userid);
        Map<String, Long> countInfo = resourceMicroApi.getJoinCountInfoByUser(userid).getResponseEntity();
        info.setCollectionjoincount(countInfo.get("collectionjoincount"));
        info.setRewardjoincount(countInfo.get("rewardjoincount"));
        info.setEstimatejoincount(countInfo.get("estimatejoincount"));
        info.setGoodansweredcount(countInfo.get("goodansweredcount"));
        info.setRewardcreatecount(activityMicroApi.getCountByCreator(userid).getResponseEntity());
        return new Response<>(info);
    }

    @Override
    public Response<UserLoginInfo> changePassword(@RequestBody Map<String, Object> param) {
        return userMicroApi.changePassword(param);
    }

    @Override
    public Response updateUser(@RequestBody UserLoginInfo entity, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> userResp = getUserByToken(token);
        if (!userResp.getServerResult().getResultCode().equalsIgnoreCase(IErrorMessageConstants.OPERATION_SUCCESS))
            return userResp;

        entity = userMicroApi.update(entity).getResponseEntity();
        return getUserInfo(entity.getUserId());
    }

    @Override
    public Response<UserLoginInfo> resetPassword(@RequestBody Map<String, Object> param) {
        return userMicroApi.resetPassword(param);
    }

    @Override
    public Response getResourceInfo(@RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> userResp = getUserByToken(token);
        if (!userResp.getServerResult().getResultCode().equalsIgnoreCase(IErrorMessageConstants.OPERATION_SUCCESS))
            return userResp;

        UserLoginInfo userInfo = userResp.getResponseEntity();
        QueryTree queryTree = new QueryTree();
        queryTree.addCondition(new QueryCondition("creator", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, userInfo.getUserId()));

        return resourceMicroApi.getResourceCountInfo(queryTree);
    }

    public Hashtable<String, String> getTokenCache() {
        return new Hashtable<>(tokenCache);
    }

    public void setTokenCache(Hashtable<String, String> tokenCache) {
        this.tokenCache = new Hashtable<>(tokenCache);
    }
}

