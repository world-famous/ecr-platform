package com.tianwen.springcloud.microservice.base.controller;

import com.alibaba.fastjson.JSONObject;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.constant.IStateCode;
import com.tianwen.springcloud.commonapi.exception.ParameterException;
import com.tianwen.springcloud.commonapi.log.SystemControllerLog;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryCondition.Operator;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.commonapi.utils.ValidatorUtil;
import com.tianwen.springcloud.datasource.base.AbstractCRUDController;
import com.tianwen.springcloud.microservice.base.api.UserMicroApi;
import com.tianwen.springcloud.microservice.base.constant.IBaseMicroConstants;
import com.tianwen.springcloud.microservice.base.entity.Area;
import com.tianwen.springcloud.microservice.base.entity.ClassInfo;
import com.tianwen.springcloud.microservice.base.entity.Role;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
import com.tianwen.springcloud.microservice.base.service.ClassService;
import com.tianwen.springcloud.microservice.base.service.RoleService;
import com.tianwen.springcloud.microservice.base.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping(value = "/user")
public class UserController extends AbstractCRUDController<UserLoginInfo> implements UserMicroApi
{
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ClassService classService;

    @Autowired
    private AreaController areaController;

    @Autowired
    private RoleController roleController;

    @Value("${ESEARCH_SERVER}")
    private String esearchServer;

    public JSONObject getKeyword()
    {
        JSONObject keyword, fields;

        keyword = new JSONObject();
        keyword.put("type", "keyword");
        keyword.put("ignore_above", 256);
        fields = new JSONObject();
        fields.put("keyword", keyword);

        return fields;
    }

    @Override
    public Response<UserLoginInfo> makeESDb() throws IOException {
        String indexUrl = "http://" + esearchServer + "/" + IBaseMicroConstants.INDEX_USER;

        try{
            HttpResponse response;
            HttpClient httpClient = new DefaultHttpClient();
            HttpDelete httpDelete = new HttpDelete(indexUrl);
            response = httpClient.execute(httpDelete);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        String[] keys = {"userid", "loginname", "loginemail", "loginmobile", "staticpassword", "createtime", "currentlogintime", "lastlogintime", "lastmodifytime",
                "status", "lastlockedtime", "lastloginfailedtime", "loginfailedcount", "realname", "islocked", "orgid", "idcardno", "sex", "birthday", "fileid", "token",
                "refreshtoken", "expiresin", "tokenrefreshedtime", "photopath", "desc", "goodscount", "lastuploadtime", "votetimes"};
        String[] types = {"text", "text", "text", "text", "text", "date", "date", "date", "date", "text", "date", "date", "long", "text", "boolean", "text",
                "text", "text", "text", "text", "text", "text", "long", "date", "text", "text", "long", "date", "double"};

        int i, len = keys.length;

        JSONObject fields = new JSONObject();
        JSONObject properties = new JSONObject();
        JSONObject type = new JSONObject();
        JSONObject mapping = new JSONObject();

        for (i = 0; i < len; i++)
        {
            JSONObject item = new JSONObject();

            item.put("type", types[i]);
            if (types[i].equalsIgnoreCase("text")) {
                item.put("fields", getKeyword());
                item.put("search_analyzer", "ik_smart");
                item.put("analyzer", "ik_smart");
            }
            else if (types[i].equalsIgnoreCase("date"))
            {
                item.put("fields", getKeyword());
                item.put("format", "yyyy-MM-dd HH:mm:ss");
            }

            fields.put(keys[i], item);
        }

        properties.put("properties", fields);
        type.put(IBaseMicroConstants.TYPE_USER, properties);
        mapping.put("mappings", type);

        HttpClient httpClient = new DefaultHttpClient();
        HttpPut httpPut = new HttpPut(indexUrl);

        StringEntity paramEntity = new StringEntity(mapping.toJSONString(), ContentType.APPLICATION_JSON);
        httpPut.setEntity(paramEntity);
        httpPut.setHeader("Content-Type", "application/json");
        httpClient.execute(httpPut);

        return new Response<>();
    }

    @Override
    public Response<UserLoginInfo> batchSaveToES(@RequestBody QueryTree queryTree) {
        int start;
        try{
            start = (int)queryTree.getQueryCondition("start").getFieldValues()[0];
        }
        catch (Exception e) { start = 1; }

        int pageSize = 50;
        int pageNo = start / pageSize;

        if (pageNo == 0)
            pageNo = 1;

        QueryTree resQuery = new QueryTree();
        resQuery.getPagination().setNumPerPage(pageSize);
        resQuery.getPagination().setPageNo(pageNo);

        List<UserLoginInfo> dataList = search(resQuery).getPageInfo().getList();

        if (!CollectionUtils.isEmpty(dataList)) {
            for (UserLoginInfo item : dataList)
                saveToES(item);
        }
        else
            return new Response<>(IStateCode.PARAMETER_IS_INVALID, "");

        return new Response<>();
    }

    public Response<UserLoginInfo> saveToES(UserLoginInfo entity) {
        JSONObject json = new JSONObject();

        json.put("userid", entity.getUserId());
        json.put("loginname", entity.getLoginName());
        json.put("loginemail", entity.getLoginEmail());
        json.put("loginmobile", entity.getLoginMobile());
        json.put("staticpassword", entity.getStaticPassword());
        if (entity.getCreateTime() != null)
            json.put("createtime", DateUtils.formatDate(entity.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
        if (entity.getLastLoginFailedTime() != null)
            json.put("lastlogintime", DateUtils.formatDate(entity.getLastLoginTime(), "yyyy-MM-dd HH:mm:ss"));
        if (entity.getLastModifyTime() != null)
            json.put("lastmodifytime", DateUtils.formatDate(entity.getLastModifyTime(), "yyyy-MM-dd HH:mm:ss"));
        json.put("status", entity.getStatus());
        if (entity.getLastLockedTime() != null)
            json.put("lastlockedtime", DateUtils.formatDate(entity.getLastLockedTime(), "yyyy-MM-dd HH:mm:ss"));
        if (entity.getLastLoginFailedTime() != null)
            json.put("lastloginfailedtime", DateUtils.formatDate(entity.getLastLoginFailedTime(), "yyyy-MM-dd HH:mm:ss"));
        json.put("loginfailedcount", entity.getLoginFailedCount());
        json.put("realname", entity.getRealName());
        json.put("islocked", entity.getIsLocked());
        json.put("orgid", entity.getOrgId());
        json.put("idcardno", entity.getIdCardNo());
        json.put("sex", entity.getSex());
        json.put("birthday", entity.getBirthday());
        json.put("fileid", entity.getFileId());
        json.put("token", entity.getToken());
        json.put("refreshtoken", entity.getRefreshtoken());
        json.put("expiresin", entity.getExpiresin());
        if (entity.getTokenrefreshedtime() != null)
            json.put("tokenrefreshedtime", DateUtils.formatDate(entity.getTokenrefreshedtime(), "yyyy-MM-dd HH:mm:ss"));
        json.put("goodscount", entity.getGoodscount());
        if (entity.getLastuploadtime() != null)
            json.put("lastuploadtime", DateUtils.formatDate(entity.getLastuploadtime(), "yyyy-MM-dd HH:mm:ss"));
        json.put("votetimes", entity.getVotetimes());

        HttpPut httpPut = new HttpPut("http://" + esearchServer + "/" + IBaseMicroConstants.INDEX_USER + "/" + IBaseMicroConstants.TYPE_USER + "/" + entity.getUserId());
        HttpClient httpClient = new DefaultHttpClient();

        try
        {
            StringEntity paramEntity = new StringEntity(json.toJSONString(), ContentType.APPLICATION_JSON);
            httpPut.setEntity(paramEntity);
            httpPut.setHeader("Content-Type", "application/json");
            httpPut.setHeader("Accept", "application/json");
            httpClient.execute(httpPut);
        }
        catch (Exception e){
            return new Response<>(IStateCode.HTTP_404, IBaseMicroConstants.ES_SERVER_ERROR);
        }

        return new Response<>();
    }

    public Response<UserLoginInfo> removeFromES(@PathVariable(value = "id") String id) {
        HttpDelete httpDelete = new HttpDelete("http://" + esearchServer + "/" + IBaseMicroConstants.INDEX_USER + "/" + IBaseMicroConstants.TYPE_USER + "/" + id);
        HttpClient httpClient = new DefaultHttpClient();
        try {
            httpClient.execute(httpDelete);
        }
        catch (Exception e){}

        return new Response<>();
    }

    public Response<UserLoginInfo> removeFromES(UserLoginInfo entity) {
        HttpDelete httpDelete = new HttpDelete("http://" + esearchServer + "/" + IBaseMicroConstants.INDEX_USER + "/" + IBaseMicroConstants.TYPE_USER + "/" + entity.getUserId());
        HttpClient httpClient = new DefaultHttpClient();
        try {
            httpClient.execute(httpDelete);
        }
        catch (Exception e){}

        return new Response<>();
    }

    @Override
    public Response<UserLoginInfo> add(@RequestBody UserLoginInfo entity)
    {
        Response<UserLoginInfo> resp = super.add(entity);

        if (resp.getServerResult().getResultCode().equalsIgnoreCase(IStateCode.HTTP_200)){
            saveToES(entity);
        }

        return resp;
    }

    @Override
    @ApiOperation(value = "", notes = "")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "QueryTree", paramType = "body")
    @SystemControllerLog(description = "")
    public Response<UserLoginInfo> update(@RequestBody UserLoginInfo entity)
    {
        Response<UserLoginInfo> resp;
        if (entity.getUserId() == null)
            resp = super.add(entity);
        else
        {
            resp = super.update(entity);
        }


        if (resp.getServerResult().getResultCode().equalsIgnoreCase(IStateCode.HTTP_200)) {
            saveToES(entity);
        }

        return resp;
    }

    @Override
    @ApiOperation(value = "根据ID删除用户信息", notes = "根据ID删除用户信息")
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "String", paramType = "path")
    @SystemControllerLog(description = "根据用户ID删除用户信息")
    public Response<UserLoginInfo> delete(@PathVariable(value = "id") String id)
    {
        UserLoginInfo entity = new UserLoginInfo();
        entity.setUserId(id);
        entity.setStatus(IBaseMicroConstants.USER_ACCOUNT_STATUS_DELETE);

        Response<UserLoginInfo> resp = super.update(entity);
        if (resp.getServerResult().getResultCode().equalsIgnoreCase(IStateCode.HTTP_200)){
            removeFromES(entity);
        }

        return resp;
    }

    @Override
    @ApiOperation(value = "根据实体对象删除用户信息", notes = "根据实体对象删除用户信息")
    @ApiImplicitParam(name = "entity", value = "用户信息实体", required = true, dataType = "UserLoginInfo", paramType = "body")
    @SystemControllerLog(description = "根据实体对象删除用户信息")
    public Response<UserLoginInfo> deleteByEntity(@RequestBody UserLoginInfo entity)
    {
        UserLoginInfo entity1 = new UserLoginInfo();
        entity1.setUserId(entity.getUserId());
        entity1.setStatus(IBaseMicroConstants.USER_ACCOUNT_STATUS_DELETE);
        int ret = userService.updateNotNull(entity);
        Response<UserLoginInfo> resp = new Response<>(entity);
        resp.getServerResult().setResultCode(Integer.toString(ret));
        return resp;
    }
    
    @Override
    @ApiOperation(value = "用户搜索", notes = "用户搜索")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "QueryTree", paramType = "body")
    @SystemControllerLog(description = "用户搜索")
    public Response<UserLoginInfo> search(@RequestBody QueryTree queryTree)
    {
        return userService.search(queryTree);
    }
    
    @Override
    @ApiOperation(value = "获取用户列表", notes = "获取用户列表")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "QueryTree", paramType = "body")
    @SystemControllerLog(description = "获取用户列表")
    public Response<UserLoginInfo> getList(@RequestBody QueryTree queryTree)
    {
        Response<UserLoginInfo> resp = userService.search(queryTree);
        List<UserLoginInfo> users = resp.getPageInfo().getList();
        if (!CollectionUtils.isEmpty(users)){
            for(UserLoginInfo user : users){
                QueryTree classQuery = new QueryTree();
                classQuery.addCondition(new QueryCondition("orgid", QueryCondition.Prepender.AND, Operator.EQUAL, user.getOrgId()));
                classQuery.addCondition(new QueryCondition("lang", QueryCondition.Prepender.AND, Operator.EQUAL, IBaseMicroConstants.zh_CN));
                List<ClassInfo> classes;
                classes = classService.search(classQuery).getPageInfo().getList();
                user.setClassinfo(classes);

                String roleids = user.getRoleid();
                List<String> roleIdList = CollectionUtils.arrayToList(StringUtils.split(roleids, ","));

                QueryTree roleQuery = new QueryTree();
                roleQuery.addCondition(new QueryCondition("roleid", QueryCondition.Prepender.AND, Operator.IN, roleIdList));
                List<Role> roles = roleController.search(roleQuery).getPageInfo().getList();
                user.setRoleList(roles);

                String areaids = user.getAreaId();
                List<String> areaIdList = CollectionUtils.arrayToList(StringUtils.split(areaids, ","));

                QueryTree areaQuery = new QueryTree();
                areaQuery.addCondition(new QueryCondition("areaid", QueryCondition.Prepender.AND, Operator.IN, areaIdList));
                List<Area> areas = areaController.search(areaQuery).getPageInfo().getList();

                user.setAreaList(areas);
            }
        }

        return resp;
    }

    @Override
    @ApiOperation(value = "", notes = "")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "QueryTree", paramType = "body")
    @SystemControllerLog(description = "")
    public Response<UserLoginInfo> get(@PathVariable(value = "userid") String userid)
    {
        UserLoginInfo userInfo = userService.getById(userid);
        Response<UserLoginInfo> resp = new Response<>(userInfo);

        return resp;
    }

    @Override
    @ApiOperation(value = "", notes = "")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "QueryTree", paramType = "body")
    @SystemControllerLog(description = "")
    public Response<UserLoginInfo> insert(@RequestBody UserLoginInfo entity)
    {
        entity.setStatus("1");
        int ret = userService.save(entity);

        Response<UserLoginInfo> resp = new Response<>(entity);
        resp.getServerResult().setResultCode(Integer.toString(ret));

        return resp;
    }

    @Override
    @ApiOperation(value = "", notes = "")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "QueryTree", paramType = "body")
    @SystemControllerLog(description = "")
    public Response<UserLoginInfo> registerUser(@RequestBody UserLoginInfo entity)
    {
        entity.setStatus("2");
        int ret = userService.save(entity);

        Response<UserLoginInfo> resp = new Response<>(entity);
        resp.getServerResult().setResultCode(Integer.toString(ret));

        return resp;
    }

    @Override
    public Response<UserLoginInfo> getByToken(@PathVariable(value = "token") String token) {
        UserLoginInfo userLoginInfo = userService.getUserByToken(token);
        return new Response<>(userLoginInfo);
    }

    @Override
    public Response<UserLoginInfo> getByLoginName(@PathVariable(value = "loginname") String loginname) {
        UserLoginInfo userLoginInfo = userService.getUserByLoginName(loginname);

        return new Response<>(userLoginInfo);
    }

    @Override
    public Response<UserLoginInfo> getByRealName(@PathVariable(value = "realname") String realname) {
        UserLoginInfo userLoginInfo = userService.getUserByRealName(realname);

        return new Response<>(userLoginInfo);
    }

    @Override
    public Response<UserLoginInfo> getByOrg(@PathVariable(value = "orgid") String orgid) {
        List<UserLoginInfo> userList = userService.getByOrg(orgid);

        return new Response<>(userList);
    }

    @Override
    public Response<String> getByArea(@PathVariable(value = "areaid") String areaid) {
        List<String> userList = userService.getByArea(areaid);
        if (userList != null) {
            userList.add("");
            userList.add("");
        }
        return new Response<>(userList);
    }

    @Override
    public Response<String> getUserIdsByQueryTree(@RequestBody QueryTree queryTree) {
        return userService.getUserIdsByQueryTree(queryTree);
    }

    //author:han
    @Override
    public Response<String> getUserIdsByQueryTreeExtra(@RequestBody QueryTree queryTree) {
        return userService.getUserIdsByQueryTreeExtra(queryTree);
    }
    //end han

    @Override
    public Response<UserLoginInfo> getUserInfo(@PathVariable(value = "userid") String userid) {
        QueryTree queryTree = new QueryTree();
        queryTree.addCondition(new QueryCondition("userid", QueryCondition.Prepender.AND, Operator.EQUAL, userid));
        List<UserLoginInfo> userData = getList(queryTree).getPageInfo().getList();
        if (CollectionUtils.isEmpty(userData))
            return new Response<>();

        UserLoginInfo user = userData.get(0);

        QueryTree roleTree = new QueryTree();
        roleTree.addCondition(new QueryCondition("userid", QueryCondition.Prepender.AND, Operator.EQUAL, userid));

        List<Role> roles = roleService.getList(roleTree);
        List<String> uniqueIds = new ArrayList<>();
        user.setRoleList(new ArrayList<>());

        if (roles != null)
            for (Role role : roles)
                if (uniqueIds.indexOf(role.getRoleid()) == -1)
                {
                    user.getRoleList().add(role);
                    uniqueIds.add(role.getRoleid());
                }

        return new Response<>(user);
    }

    @Override
    public Response<Role> getUserRole(@PathVariable(value = "userid") String userid) {
        QueryTree queryTree = new QueryTree();
        queryTree.addCondition(new QueryCondition("userid", QueryCondition.Prepender.AND, Operator.EQUAL, userid));
        List<Role> roles = roleService.getList(queryTree);
        return new Response<>(roles);
    }

    @Override
    public Response<UserLoginInfo> deleteUserRole(@PathVariable(value = "userid") String userid) {
        userService.deleteUserRole(userid);

        return new Response<>();
    }

    @Override
    public Response<UserLoginInfo> insertUserRole(@RequestBody Map<String, Object> param) {
        List<String> roleIdList = (List<String>)param.get("roleids");
        String userId = param.get("userid").toString();

        for (String roleId : roleIdList) {
            if (StringUtils.isEmpty(roleId))
                continue;
            Map<String, Object> userRole = new HashMap<>();
            userRole.put("roleid", roleId);
            userRole.put("userid", userId);
            userService.insertUserRole(userRole);
        }

        return new Response<>();
    }

    @Override
    public Response<UserLoginInfo> getUserByName(@RequestBody String username) {
        return userService.getByName(username);
    }

    @Override
    public Response<UserLoginInfo> changePassword(@RequestBody Map<String, Object> param) {
        Object userId = param.get("userid");
        UserLoginInfo user = new UserLoginInfo();

        if (userId != null)
        {
            user = userService.getById(userId.toString());
            if (user != null)
            {
                Object oldpass = param.get("oldpassword"), newpass = param.get("newpassword");
                if (oldpass != null && newpass != null)
                {
                    String oldkey = oldpass.toString(), newkey = newpass.toString();
                    if (user.getStaticPassword().equals(oldkey)) {
                        user.setStaticPassword(newkey);
                        userService.updateNotNull(user);
                    }
                }
            }
        }

        return new Response<>(user);
    }

    @Override
    @ApiOperation(value = "", notes = "")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "QueryTree", paramType = "body")
    @SystemControllerLog(description = "")
    public Response<UserLoginInfo> resetPassword(@RequestBody Map<String, Object> param)
    {
        List<String> userids = (List<String>)param.get("userids");

        for(String userid : userids)
        {
            UserLoginInfo userLoginInfo = new UserLoginInfo();
            userLoginInfo.setUserId(userid);
            userLoginInfo.setStaticPassword("123456");
            userService.updateNotNull(userLoginInfo);
        }

        return new Response();
    }

    @Override
    @ApiOperation(value = "", notes = "")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "QueryTree", paramType = "body")
    @SystemControllerLog(description = "")
    public Response<UserLoginInfo> lockUser(@RequestBody Map<String, Object> param)
    {
        List<String> userids = (List<String>)param.get("userids");

        for(String userid : userids)
        {
            UserLoginInfo userLoginInfo = new UserLoginInfo();
            userLoginInfo.setUserId(userid);
            if (param.get("islocked").toString().equals("1"))
            {
                userLoginInfo.setIsLocked(true);
                userLoginInfo.setLastLockedTime(new Date(System.currentTimeMillis()));
            }
            else
                userLoginInfo.setIsLocked(false);

            userService.updateNotNull(userLoginInfo);
        }

        return new Response();
    }

    @Override
    @ApiOperation(value = "", notes = "")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "QueryTree", paramType = "body")
    @SystemControllerLog(description = "")
    public Response<UserLoginInfo> allowUser(@RequestBody Map<String, Object> param)
    {
        List<String> userids = (List<String>)param.get("userids");

        for(String userid : userids)
        {
            UserLoginInfo userLoginInfo = new UserLoginInfo();
            userLoginInfo.setUserId(userid);
            userLoginInfo.setStatus(param.get("status").toString());

            userService.updateNotNull(userLoginInfo);
        }

        return new Response();
    }

    @Override
    public void validate(MethodType methodType, Object p)
    {
        switch (methodType)
        {
            case ADD:
                UserLoginInfo entity = (UserLoginInfo)p;
                validateAdd(entity);
                break;
            case DELETE:
                break;
            case GET:
                break;
            case SEARCH:
                break;
            case UPDATE:
                break;
            case BATCHADD:
                break;
            case BATCHDELETEBYENTITY:
                break;
            case BATCHUPDATE:
                break;
            case DELETEBYENTITY:
                break;
            case GETBYENTITY:
                break;
            default:
                break;
        }
    }
    
    private void validateAdd(UserLoginInfo entity)
    {
        if (null == entity)
        {
            throw new ParameterException(IStateCode.PARAMETER_IS_EMPTY, "请求体为空");
        }
        ValidatorUtil.parameterValidate(entity);
        // 校验重复
        QueryTree queryTree = new QueryTree();
        queryTree.where("loginname", Operator.EQUAL, entity.getLoginName()).and("status",
            Operator.NOT_EQUAL,
            IBaseMicroConstants.USER_ACCOUNT_STATUS_DELETE);
        if (StringUtils.isNotBlank(entity.getOrgId()))
            queryTree.and("orgid", Operator.EQUAL, entity.getOrgId());

        String userid = userService.getLastId();
        if (userid != null && !userid.isEmpty())
            userid = "user000000001";

        entity.setUserId(userid);
        entity.setCreateTime(new Date());
        entity.setCurrentLoginTime(null);
        entity.setLastLockedTime(null);
        entity.setLastLoginFailedTime(null);
        entity.setLastLoginTime(null);
        entity.setLastModifyTime(null);
    }
}
