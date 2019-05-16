package com.tianwen.springcloud.ecrapi.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.log.SystemControllerLog;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.TeacherApi;
import com.tianwen.springcloud.microservice.base.entity.Area;
import com.tianwen.springcloud.microservice.base.entity.Organization;
import com.tianwen.springcloud.microservice.base.entity.Teacher;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/teacher")
public class TeacherRestController extends BaseRestController implements TeacherApi
{
    @Override
    @ApiOperation(value = "保存老师列表", notes = "保存老师列表")
    @ApiImplicitParam(name = "entity列表", value = "老师信息实体列表", required = true, dataType = "List<String>", paramType = "body")
    @SystemControllerLog(description = "保存老师列表")
    public Response<Teacher> batchRegisterTeacher(@RequestBody List<Teacher> teacherEntityList) {

        for(Teacher teacher:teacherEntityList){
            UserLoginInfo userEntity = new UserLoginInfo();
            Organization orgEntity = new Organization();

            userEntity.setUserId(teacher.getUserid());
            userEntity.setLoginName(teacher.getLoginname());
            userEntity.setRealName(teacher.getRealname());
            userEntity.setSex(teacher.getSex());
            userEntity.setStatus(teacher.getStatus());
            userEntity.setOrgId(teacher.getOrgid());
            userEntity.setLastModifyTime(teacher.getLastmodifytime());
            userEntity.setCreateTime(teacher.getCreatetime());

            orgEntity.setOrgid(teacher.getOrgid());
            orgEntity.setOrgname(teacher.getOrgname());
            orgEntity.setAreaid(teacher.getAreaid());

            if(teacher.getOrderno() == null) {
                teacher.setOrderno(65536);
            }

            if (userMicroApi.get(userEntity.getUserId()).getResponseEntity() != null)
                userMicroApi.update(userEntity);
            else
                userMicroApi.insert(userEntity);

            if (organizationMicroApi.get(teacher.getOrgid()).getResponseEntity() != null)
                organizationMicroApi.update(orgEntity);
            else
                organizationMicroApi.add(orgEntity);

            if(teacherMicroApi.get(teacher.getUserid()).getResponseEntity() != null)
                teacherMicroApi.update(teacher);
            else {
                teacher.setSharerange("1");
                teacher.setIsnamed("0");
                teacherMicroApi.add(teacher);
            }
        }
        return new Response<>(teacherEntityList);
    }

    @Override
    @ApiOperation(value = "获取老师列表", notes = "获取老师列表")
    @ApiImplicitParam(name = "querytree", value = "搜索条件树", required = true, dataType = "Querytree", paramType = "body")
    @SystemControllerLog(description = "获取老师列表")
    public Response<Teacher> getTeacherList(@RequestBody QueryTree queryTree) {
        Response<Teacher> resp = teacherMicroApi.getTeacherList(queryTree);
        List<Teacher> teacherList = resp.getPageInfo().getList();

        if (teacherList != null)
        {
            for (Teacher teacher : teacherList) {
                QueryTree countTree = new QueryTree();
                countTree.addCondition(new QueryCondition("creator", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, teacher.getUserid()));
                countTree.addCondition(new QueryCondition("isgoods", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "1"));
                Long count = resourceMicroApi.getCount(countTree).getResponseEntity();
                if (count == null)
                    count = Long.valueOf(0);
                teacher.setGoodscount(count.intValue());
                if (teacher.getSharerange() == null){
                    teacher.setSharerange("1");
                    teacherMicroApi.update(teacher);
                }
            }
        }

        return resp;
    }

    @Override
    @ApiOperation(value = "获取名师列表", notes = "获取名师列表")
    @ApiImplicitParam(name = "querytree", value = "搜索条件树", required = true, dataType = "Querytree", paramType = "body")
    @SystemControllerLog(description = "获取名师列表")
    public Response<Teacher> getNamedTeacherList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token")String token) {
        queryTree.addCondition(new QueryCondition("isnamed", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "1"));
        UserLoginInfo userLoginInfo = userMicroApi.getByToken(token).getResponseEntity();
        if (userLoginInfo != null) {
            if (!StringUtils.isEmpty(userLoginInfo.getOrgId())) {
                String userareaid = organizationMicroApi.get(userLoginInfo.getOrgId()).getResponseEntity().getAreaid();
                if (!StringUtils.isEmpty(userareaid)) {
                    Area firstArea = areaMicroApi.get(userareaid).getResponseEntity();
                    if (firstArea != null) {
                        String firstparentid = firstArea.getParentareaid();
                        if (!StringUtils.isEmpty(firstparentid) && firstparentid != "-1") {
                            Area secArea = areaMicroApi.get(firstparentid).getResponseEntity();
                            if (secArea != null) {
                                String secparentid = secArea.getParentareaid();
                                if (!StringUtils.isEmpty(secparentid) && secparentid != "-1")
                                    userareaid = firstparentid + "," + secparentid;
                                else if (!StringUtils.isEmpty(secparentid) && secparentid == "-1")
                                    userareaid = userareaid + "," + firstparentid;
                            }
                        }
                    }
                } else
                    userareaid = null;
                queryTree.addCondition(new QueryCondition("userareaid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, userareaid));
            }
        }
        Response<Teacher> response = teacherMicroApi.getTeacherList(queryTree);
        List<Teacher> teacherList = response.getPageInfo().getList();
        if (!CollectionUtils.isEmpty(teacherList)) {
            for (Teacher teacher : teacherList) {
                QueryTree countTree = new QueryTree();
                countTree.addCondition(new QueryCondition("creator", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, teacher.getUserid()));
                countTree.addCondition(new QueryCondition("isgoods", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "1"));
                Long count = resourceMicroApi.getCount(countTree).getResponseEntity();
                if (count == null)
                    count = Long.valueOf(0);
                teacher.setGoodscount(count.intValue());
                if (teacher.getSharerange() == null){
                    teacher.setSharerange("1");
                    teacherMicroApi.update(teacher);
                }
            }
        }
        return response;
    }

    @Override
    @ApiOperation(value = "根据id列表删除老师信息实体列表", notes = "根据id列表删除老师信息实体列表")
    @ApiImplicitParam(name = "id列表", value = "老师id列表", required = true, dataType = "List<String>", paramType = "body")
    @SystemControllerLog(description = "根据id列表删除老师信息实体列表")
    public Response<Teacher> batchDeleteTeacher(@RequestBody List<String> orgidList) {
        for(String orgid:orgidList){
            teacherMicroApi.delete(orgid);
        }
        return new Response<>();
    }

    @Override
    @ApiOperation(value = "配置名师/普通师列表", notes = "配置名师/普通师列表")
    @ApiImplicitParam(name = "entity列表", value = "老师id列表", required = true, dataType = "List<Teacher>", paramType = "body")
    @SystemControllerLog(description = "配置名师/普通师列表")
    public Response<Teacher> batchSetTeacherNamed(@RequestBody List<Teacher> teacherEntityList) {
        return teacherMicroApi.batchUpdate(teacherEntityList);
    }

    @Override
    @ApiOperation(value = "配置老师区域列表", notes = "配置老师区域列表")
    @ApiImplicitParam(name = "entity列表", value = "老师信息实体列表", required = true, dataType = "List<Teacher>", paramType = "body")
    @SystemControllerLog(description = "配置老师区域列表")
    public Response<Organization> batchSetArea2Teacher(@RequestBody List<Organization> teacherEntityList, @RequestHeader(value = "token") String token) {
        return organizationMicroApi.batchUpdate(teacherEntityList);
    }

    @Override
    @ApiOperation(value = "根据用户id获取老师信息实体", notes = "根据用户id获取老师信息实体")
    @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "String", paramType = "path")
    @SystemControllerLog(description = "根据用户id获取老师信息实体")
    public Response<Teacher> getByUserId(@PathVariable(value = "userid") String userid) {
        Response<Teacher> response = teacherMicroApi.getByUserid(userid);
        Teacher teacher = response.getResponseEntity();
        if (teacher != null) {
            QueryTree queryTree = new QueryTree();
            queryTree.addCondition(new QueryCondition("creator", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, userid));
            queryTree.addCondition(new QueryCondition("isgoods", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "1"));
            Long count = resourceMicroApi.getCount(queryTree).getResponseEntity();
            if (count == null)
                count = Long.valueOf(0);
            teacher.setGoodscount(count.intValue());
            if (teacher.getSharerange() == null){
                teacher.setSharerange("1");
                teacherMicroApi.update(teacher);
            }
        }
        return response;
    }

    @Override
    @ApiOperation(value = "根据用户id获取顺序号", notes = "根据用户id获取顺序号")
    @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "String", paramType = "path")
    @SystemControllerLog(description = "根据用户id获取顺序号")
    public Response<Integer> getOrdernoByUserid(@PathVariable(value = "userid") String userid) {
        return teacherMicroApi.getOrdernoByUserid(userid);
    }

    @Override
    @ApiOperation(value = "配置顺序号", notes = "配置顺序号")
    @ApiImplicitParam(name = "entity", value = "老师信息实体", required = true, dataType = "Teacher", paramType = "body")
    @SystemControllerLog(description = "配置顺序号")
    public Response<Teacher> setOrdernoByUserid(@RequestBody Teacher teacher) {
        return teacherMicroApi.setOrdernoByUserid(teacher);
    }

    @Override
    @ApiOperation(value = "配置老师范围", notes = "配置老师范围")
    @ApiImplicitParam(name = "entity列表", value = "老师信息实体列表", required = true, dataType = "List<String>", paramType = "body")
    @SystemControllerLog(description = "配置老师范围")
    public Response<Teacher> batchSetSharerange2Teacher(@RequestBody List<Teacher> teacherList) {
        return teacherMicroApi.batchUpdate(teacherList);
    }

    @Override
    @ApiOperation(value = "根据用户id获取老师信息实体", notes = "根据用户id获取老师信息实体")
    @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "String", paramType = "path")
    @SystemControllerLog(description = "根据用户id获取老师信息实体")
    public Response<Teacher> getTeacherDetail(@PathVariable(value = "userid") String userid) {
        Response<Teacher> response = teacherMicroApi.getByUserid(userid);
        Teacher teacher = response.getResponseEntity();
        if (teacher.getSharerange() == null){
            teacher.setSharerange("1");
            teacherMicroApi.update(teacher);
        }
        if (teacher != null) {

        }
        return response;
    }
}
