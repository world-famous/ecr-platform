package com.tianwen.springcloud.ecrapi.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.log.SystemControllerLog;
import com.tianwen.springcloud.commonapi.query.Pagination;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.SchoolApi;
import com.tianwen.springcloud.ecrapi.constant.ICommonConstants;
import com.tianwen.springcloud.ecrapi.util.CommonUtil;
import com.tianwen.springcloud.microservice.base.api.SchoolTypeMicroApi;
import com.tianwen.springcloud.microservice.base.entity.*;
import com.tianwen.springcloud.microservice.bussinessassist.entity.Good;
import com.tianwen.springcloud.microservice.operation.entity.OrderStatInfo;
import com.tianwen.springcloud.microservice.resource.entity.UserCountInfo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;
@RestController
@RequestMapping(value = "/school")
public class SchoolRestController extends BaseRestController implements SchoolApi{
    @Autowired
    private GoodRestController goodRestController;

    @Autowired
    private SchoolTypeMicroApi schoolTypeMicroApi;

    public Response getGoodStatusData(QueryTree queryTree)
    {
        Response<School> response = schoolMicroApi.getListByAreaAndConType(queryTree);
        List<School> schools = response.getPageInfo().getList();
        String begin_time = null, end_time = null;

        if (queryTree.getQueryCondition("begin_time") != null)
        {
            try{
                begin_time = queryTree.getQueryCondition("begin_time").getFieldValues()[0].toString();
            }
            catch (Exception e) { e.printStackTrace(); }
        }

        if (queryTree.getQueryCondition("end_time") != null)
        {
            try{
                end_time = queryTree.getQueryCondition("end_time").getFieldValues()[0].toString();
            }
            catch (Exception e) { e.printStackTrace(); }
        }

        for(School school : schools)
        {
            if (school.getOrgname() == null)
                school.setOrgname("");

            List<String> areas = new ArrayList<>();
            if (school.getArea1() != null) areas.add(school.getArea1());
            if (school.getArea2() != null) areas.add(school.getArea2());
            if (school.getArea3() != null) areas.add(school.getArea3());

            try {
                school.setArea1(areas.get(0));
            }
            catch (Exception e) {
                school.setArea1("");
            }

            try {
                school.setArea2(areas.get(1));
            }
            catch (Exception e) {
                school.setArea2("");
            }

            try {
                school.setArea3(areas.get(2));
            }
            catch (Exception e) {
                school.setArea3("");
            }

            List<String> creatorIds = new ArrayList<>();
            List<UserLoginInfo> users;
            String orgid = school.getOrgid();
            int count = 0;

            users = userMicroApi.getByOrg(orgid).getPageInfo().getList();
            if (!CollectionUtils.isEmpty(users))
            {
                for (UserLoginInfo user : users)
                    creatorIds.add(user.getUserId());
                creatorIds.add("");

                QueryTree resTree = new QueryTree();
                resTree.addCondition(new QueryCondition("creatorids", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, creatorIds));
                resTree.addCondition(new QueryCondition("contenttype", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, school.getContypevalue()));
                resTree.addCondition(new QueryCondition("isgoods", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "1"));
                resTree.addCondition(new QueryCondition("begin_time", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, begin_time));
                resTree.addCondition(new QueryCondition("end_time", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, end_time));
                count = resourceMicroApi.getCount(resTree).getResponseEntity().intValue();
            }
            school.setGoodscount(count);
        }

        return response;
    }

    @Override
    public Response getGoodStatus(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token) {
        return getGoodStatusData(queryTree);
    }

    @Override
    public Response exportToExcel(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token) throws Exception{
        List<School> schools = getGoodStatusData(queryTree).getPageInfo().getList();

        Map<String, Object> result = new HashMap<>();

        String homePath = httpSession.getServletContext().getRealPath("/") + "excelFiles/";
        boolean isDirExist = new File(homePath).isDirectory();
        if (isDirExist == false) {
            boolean res = new File(homePath).mkdir();
            if (!res) {
                return new Response<>("", "");
            }
        }
        String fileName = CommonUtil.convertToMd5(new Timestamp(System.currentTimeMillis()).toString(), true) + ".xls";
        File chartFile = new File(homePath + fileName);
        if (!chartFile.createNewFile()) return null;

        FileOutputStream fileOutputStream = new FileOutputStream(chartFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet hssfSheet = hssfWorkbook.createSheet();

        HSSFRow titleRow = hssfSheet.createRow(0);
        titleRow.createCell(0);titleRow.getCell(0).setCellValue("省份");
        titleRow.createCell(1);titleRow.getCell(1).setCellValue("城市");
        titleRow.createCell(2);titleRow.getCell(2).setCellValue("区域");
        titleRow.createCell(3);titleRow.getCell(3).setCellValue("学校");
        titleRow.createCell(4);titleRow.getCell(4).setCellValue("类别");
        titleRow.createCell(5);titleRow.getCell(5).setCellValue("数量");

        for(int i = 0; i <schools.size(); i++)
        {
            School school = schools.get(i);
            HSSFRow row = hssfSheet.createRow(i + 1);
            row.createCell(0);row.getCell(0).setCellValue(school.getArea1());
            row.createCell(1);row.getCell(1).setCellValue(school.getArea2());
            row.createCell(2);row.getCell(2).setCellValue(school.getArea3());
            row.createCell(3);row.getCell(3).setCellValue(school.getOrgname());
            row.createCell(4);row.getCell(4).setCellValue(school.getContypename());
            row.createCell(5);row.getCell(5).setCellValue(school.getGoodscount());
        }

        hssfWorkbook.write(fileOutputStream);

        result.put("downloadUrl", getRootURL() + "/excelFiles/" + fileName);
        return new Response<>(result);
    }

    @Override
    @RequestMapping(value = "/batchRegisterSchoolList", method = RequestMethod.POST)
    @ApiOperation(value = "保存ECR学校学校", notes = "保存ECR学校学校")
    @ApiImplicitParam(name = "entity列表", value = "学校信息实体列表", required = true, dataType = "List<School>", paramType = "body")
    @SystemControllerLog(description = "保存ECR学校学校")
    public Response<School> batchRegisterSchoolList(@RequestBody List<School> schoolEntityList, @RequestHeader(value = "token") String token){
        UserLoginInfo userLoginInfo = getUserByToken(token).getResponseEntity();
        for(School rawEntity : schoolEntityList){
            if (rawEntity.getOrderno() == null)
                rawEntity.setOrderno(65536);
            Organization orgEntity = new Organization();
            School schoolEntity = new School();
            SchoolType schoolTypeEntity = new SchoolType();

            rawEntity.setCreatorid(userLoginInfo.getUserId());
            orgEntity.setFromSchool(rawEntity);
            schoolEntity.setFromSchool(rawEntity);
            String[] areaids = rawEntity.getAreaid().split(",");

            orgEntity.setAreaid(areaids[2]);

            if (rawEntity.getSchooltypeid() != null) {
                String[] schooltypeids = rawEntity.getSchooltypeid().split(",");
                for (String schooltypeid : schooltypeids) {
                    Map<String, String> map = new HashMap<>();
                    map.put("schoolid", rawEntity.getOrgid());
                    map.put("schooltypeid", schooltypeid);
                    schoolTypeMicroApi.connectSchoolType(map);
                }

                String[] schooltypenames = rawEntity.getSchooltypename().split(",");
                for (int i = 0; i < schooltypenames.length; i++) {
                    if (schooltypeids.length > i) {
                        schoolTypeEntity.setSchooltypeid(schooltypeids[i]);
                        schoolTypeEntity.setSchooltypename(schooltypenames[i]);
                    }

                    SchoolType schoolType = schoolTypeMicroApi.get(schoolTypeEntity.getSchooltypeid()).getResponseEntity();
                    if (schoolType != null)
                        schoolTypeMicroApi.update(schoolTypeEntity);
                    else
                        schoolTypeMicroApi.add(schoolTypeEntity);
                }
            }

            if (schoolMicroApi.get(schoolEntity.getOrgid()).getResponseEntity() != null){
                organizationMicroApi.update(orgEntity);
                schoolMicroApi.update(schoolEntity);
            }
            else{
                organizationMicroApi.add(orgEntity);
                schoolMicroApi.add(schoolEntity);
            }
        }
        return new Response<>(schoolEntityList);
    }

    @Override
    @ApiOperation(value = "获取学校类型列表", notes = "获取学校类型列表")
    @SystemControllerLog(description = "获取学校类型列表")
    public Response<SchoolType> getSchoolTypeList() {
        return schoolMicroApi.getSchoolTypeList();
    }

    @Override
    @ApiOperation(value = "获取学校列表", notes = "获取学校列表")
    @ApiImplicitParam(name = "querytree", value = "搜索条件树", required = true, dataType = "Querytree", paramType = "body")
    @SystemControllerLog(description = "获取学校列表")
    public Response<School> getSchoolList(@RequestBody QueryTree queryTree) {
        return schoolMicroApi.getSchoolList(queryTree);
    }

    @Override
    @ApiOperation(value = "根据id列表删除学校信息实体列表", notes = "根据id列表删除学校信息实体列表")
    @ApiImplicitParam(name = "id列表", value = "学校id列表", required = true, dataType = "List<String>", paramType = "body")
    @SystemControllerLog(description = "根据id列表删除学校信息实体列表")
    public Response<School> batchDeleteSchool(@RequestBody List<String> orgidList) {
        for(String orgid : orgidList) {
            schoolMicroApi.delete(orgid);
            organizationMicroApi.delete(orgid);
        }
        return new Response<>();
    }

    @Override
    @ApiOperation(value = "配置名学/普通学列表", notes = "配置名学/普通学列表")
    @ApiImplicitParam(name = "entity列表", value = "学校信息实体列表", required = true, dataType = "List<School>", paramType = "body")
    @SystemControllerLog(description = "配置名学/普通学列表")
    public Response<School> batchSetSchoolNamed(@RequestBody List<School> schoolEntityList, @RequestHeader(value = "token")String token) {
        for (School school : schoolEntityList){
            if (StringUtils.equals(school.getIsnamed() , "1"))
                logOptionEntity(school.getOrgid(),school.getOrgid(), ICommonConstants.OPTION_OPTIONTYPE_SCHOOLNAMED, token);
            else if (StringUtils.equals(school.getIsnamed() , "0"))
                logOptionEntity(school.getOrgid(),school.getOrgid(), ICommonConstants.OPTION_OPTIONTYPE_SCHOOLNORMAL, token);
        }
        return schoolMicroApi.batchUpdate(schoolEntityList);
    }

    @Override
    @ApiOperation(value = "根据用户id获取顺序号", notes = "根据用户id获取顺序号")
    @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "String", paramType = "path")
    @SystemControllerLog(description = "根据用户id获取顺序号")
    public Response<Integer> getOrdernoByOrgid(@PathVariable(value = "userid") String userid) {
        return schoolMicroApi.getOrdernoByOrgid(userid);
    }

    @Override
    @ApiOperation(value = "配置顺序号", notes = "配置顺序号")
    @ApiImplicitParam(name = "entity", value = "老师信息实体", required = true, dataType = "Teacher", paramType = "body")
    @SystemControllerLog(description = "配置顺序号")
    public Response<School> setOrdernoByOrgid(@RequestBody School school) {
        return schoolMicroApi.setOrdernoByOrgid(school);
    }

    @Override
    @ApiOperation(value = "配置学校区域信息列表", notes = "配置学校区域信息列表")
    @ApiImplicitParam(name = "entity列表", value = "学校信息实体列表", required = true, dataType = "List<School>", paramType = "body")
    @SystemControllerLog(description = "配置学校区域信息列表")
    public Response<Organization> batchSetArea2School(@RequestBody List<Organization> schoolEntityList, @RequestHeader(value = "token") String token) {
        for(int i=0;i<schoolEntityList.size();i++) {
            schoolEntityList.get(i).setLastmodifierid(userMicroApi.getByToken(token).getResponseEntity().getUserId());
        }
        return organizationMicroApi.batchUpdate(schoolEntityList);
    }

    @Override
    @ApiOperation(value = "根据区域id获取名学信息实体列表", notes = "根据区域id获取名学信息实体列表")
    @ApiImplicitParam(name = "querytree", value = "搜索条件树", required = true, dataType = "Querytree", paramType = "body")
    @SystemControllerLog(description = "根据区域id获取名学信息实体列表")
    public Response<Map<String, Object>> getNamedSchoolInfoListByAreaId(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token) {
        QueryCondition areaCond = queryTree.getQueryCondition("areaid");
        String areaid = null;
        if (areaCond != null) {
            areaid = areaCond.getFieldValues()[0].toString();
            if (StringUtils.isEmpty(areaid))
                areaid = null;
        }

        QueryTree namedTree = new QueryTree();
        namedTree.addCondition(new QueryCondition("isnamed", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "1"));
        List<School> namedList = schoolMicroApi.getAllSchoolList(namedTree).getPageInfo().getList();
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> areaRelation = new HashMap<>();
        areaRelation.put("bigareaid", areaid);

        for(School named : namedList)
        {
            String subarea = named.getAreaid();
            if (StringUtils.isEmpty(subarea))
                continue;
            areaRelation.put("smallareaid", subarea);
            boolean family = areaMicroApi.getAreaRelation(areaRelation).getResponseEntity();
            if (areaid == null || family) {
                Map<String, Object> item = new HashMap<>();
                item.put("orgid", named.getOrgid());
                item.put("orgname", named.getOrgname());
                item.put("photopath", named.getPhotopath());
                QueryTree teacherTree = new QueryTree();
                teacherTree.addCondition(new QueryCondition("orgid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, named.getOrgid()));
                teacherTree.setPagination(new Pagination());
                teacherTree.getPagination().setNumPerPage(1);
                //long total = goodMicroApi.getList(teacherTree).getPageInfo().getTotal();
                long total = goodMicroApi.getCount(teacherTree).getResponseEntity();

                item.put("goodscount", total);

                result.add(item);
            }
        }

        Collections.sort(result, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> u1, Map<String, Object> u2) {
                if((long)u1.get("goodscount") < (long)u2.get("goodscount"))
                    return 1;
                else if((long)u1.get("goodscount") == (long)u2.get("goodscount"))
                    return 0;
                else
                    return -1;
            }
        });

        List<Map<String, Object>> pageList;
        Pagination pagination = queryTree.getPagination();
        int start = pagination.getStart(), pageSize = pagination.getNumPerPage();
        if (start > result.size())
            pageList = new ArrayList<>();
        else
            pageList = result.subList(start ,start + pageSize > result.size() ? result.size() : start + pageSize);

        Response<Map<String, Object>> resp = new Response<>(pageList);

        resp.getPageInfo().setTotal(result.size());

        Map<String, Object> resEntity = resp.getResponseEntity();
        if(resEntity == null)
            resEntity = new HashMap<>();
        resEntity.put("areaId", areaid);
        resp.setResponseEntity(resEntity);

        return resp;
    }

    @Override
    @ApiOperation(value = "根据学校id获取产品统计列表", notes = "根据学校id获取产品统计列表")
    @ApiImplicitParam(name = "id", value = "学校id", required = true, dataType = "String", paramType = "path")
    @SystemControllerLog(description = "根据学校id获取产品统计列表")
    public Response getGoodsStatisticsBySchoolId(@PathVariable(value = "orgid") String orgid, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> userResponse = getUserByToken(token);
        UserLoginInfo user = userResponse.getResponseEntity();
        if (user == null) return userResponse;

        Map<String, Object> result = new HashMap<>();

        Organization organization = organizationMicroApi.get(orgid).getResponseEntity();
        School school = schoolMicroApi.get(orgid).getResponseEntity();

        QueryTree teacherTree = new QueryTree();
        teacherTree.addCondition(new QueryCondition("orgid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, orgid));
        List<String> creatorids = userMicroApi.getUserIdsByQueryTree(teacherTree).getPageInfo().getList();

        result.put("orgname", organization.getOrgname());
        result.put("photopath", school.getPhotopath());
        result.put("description", school.getDescription());

        QueryTree goodTree = new QueryTree();

        goodTree.getPagination().setNumPerPage(1);

        goodTree.addCondition(new QueryCondition("creatorids", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, creatorids));
        goodTree.addCondition(new QueryCondition("isgoods", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "1"));
        addSharerangeKeyCondition(goodTree, user);

        UserCountInfo userCountInfo = resourceMicroApi.getResourceCountInfo(goodTree).getResponseEntity();
        if (userCountInfo == null)
            userCountInfo = new UserCountInfo();

        QueryTree countTree = new QueryTree();
        countTree.addCondition(new QueryCondition("orgid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, orgid));
        Long total = goodMicroApi.getList(countTree).getPageInfo().getTotal();

        if (userCountInfo.getViewtimes() == null)
            userCountInfo.setViewtimes(Integer.valueOf(0));
        result.put("totalviewtimes", userCountInfo.getViewtimes());
        if (userCountInfo.getVotetimes() == null)
            userCountInfo.setVotetimes(Integer.valueOf(0));
        result.put("totalclicktimes", userCountInfo.getVotetimes());
        if (userCountInfo.getCollectiontimes() == null)
            userCountInfo.setCollectiontimes(Integer.valueOf(0));
        result.put("totalcollectiontimes", userCountInfo.getCollectiontimes());
        if (userCountInfo.getDowntimes() == null)
            userCountInfo.setDowntimes(Integer.valueOf(0));
        result.put("totaldowntimes", userCountInfo.getDowntimes());
        result.put("total", total);

        return new Response<>(result);
    }

    @Override
    @ApiOperation(value = "根据学校id获取产品列表", notes = "根据学校id获取产品列表")
    @ApiImplicitParam(name = "querytree", value = "搜索条件树", required = true, dataType = "Querytree", paramType = "body")
    @SystemControllerLog(description = "根据学校id获取产品列表")
    public Response<Good> getGoodsList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token) {
        return goodRestController.getGoodsList(queryTree, token);
    }

    @Override
    @ApiOperation(value = "根据学校id获取学段列表", notes = "根据学校id获取学段列表")
    @ApiImplicitParam(name = "id", value = "学校id", required = true, dataType = "String", paramType = "path")
    @SystemControllerLog(description = "根据学校id获取学段列表")
    public Response<DictItem> getSchoolSectionBySchoolid(@PathVariable(value = "schoolid") String schoolid) {
        return schoolMicroApi.getSchoolSectionBySchoolid(schoolid);
    }
}
