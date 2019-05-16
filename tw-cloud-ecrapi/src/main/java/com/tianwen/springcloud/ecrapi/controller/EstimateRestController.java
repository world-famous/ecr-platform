package com.tianwen.springcloud.ecrapi.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.log.SystemControllerLog;
import com.tianwen.springcloud.commonapi.query.OrderMethod;
import com.tianwen.springcloud.commonapi.query.Pagination;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.commonapi.utils.DateUtil;
import com.tianwen.springcloud.ecrapi.api.EstimateRestApi;
import com.tianwen.springcloud.ecrapi.constant.ICommonConstants;
import com.tianwen.springcloud.ecrapi.constant.IErrorMessageConstants;
import com.tianwen.springcloud.ecrapi.util.CommonUtil;
import com.tianwen.springcloud.ecrapi.util.SensitiveWordFilter;
import com.tianwen.springcloud.microservice.activity.constant.IActivityMicroConstants;
import com.tianwen.springcloud.microservice.activity.entity.Activity;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
import com.tianwen.springcloud.microservice.bussinessassist.api.TeacherVoteMicroApi;
import com.tianwen.springcloud.microservice.bussinessassist.entity.TeacherVote;
import com.tianwen.springcloud.microservice.resource.constant.IResourceMicroConstants;
import com.tianwen.springcloud.microservice.resource.entity.ExportMan;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
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


@RestController
@RequestMapping(value = "/estimate")
public class EstimateRestController extends BaseRestController implements EstimateRestApi
{
    @Autowired
    private TeacherVoteMicroApi teacherVoteMicroApi;

    @Override
    public Response<Activity> get(@PathVariable(value = "id") String id) {
        return activityMicroApi.get(id);
    }

    @Override
    @ApiOperation(value = "发起评比活动", notes = "发起评比活动")
    @ApiImplicitParam(name = "estimateActivity", value = "评比活动信息", required = true, dataType = "Activity", paramType = "body")
    @SystemControllerLog(description = "发起评比活动")
    public Response addEstimateActivity(@RequestBody Activity estimateActivity, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();
        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS)) return loginInfoResponse;

        /**
         * @author: jong
         * @date: 2019-02-08,11
         * @comment: check sensitive words
         */
        SensitiveWordFilter swFilter = new SensitiveWordFilter();
        estimateActivity.setActivityname(swFilter.process(estimateActivity.getActivityname()));
        /*
         * ----------------------------------------------------------------------
         */

        UserLoginInfo user = loginInfoResponse.getResponseEntity();
        estimateActivity.setActivitytype(IActivityMicroConstants.ACTIVITY_TYPE_ESTIMATE);
        estimateActivity.setCreator(user.getUserId());
        Response<Activity> response = activityMicroApi.add(estimateActivity);
        Activity addedActivity = response.getResponseEntity();
        if (addedActivity == null) {
            return new Response(IErrorMessageConstants.ERR_PARAMETER_INVALID, IErrorMessageConstants.ERR_MSG_DUPLICATE_ACTIVITY_NAME);
        }

        return response;
    }

    @Override
    @ApiOperation(value = "bei评比活动", notes = "发起评比活动")
    @ApiImplicitParam(name = "estimateActivity", value = "评比活动信息", required = true, dataType = "Activity", paramType = "body")
    @SystemControllerLog(description = "发起评比活动")
    public Response<Activity> delete(@PathVariable(value = "id")String id) {
        return  activityMicroApi.delete(id);
    }

    @Override
    public Response<Activity> batchRemoveActivity(@RequestBody String[] ids) {
        return activityMicroApi.batchRemove(ids);
    }

    @Override
    public Response<Activity> endEstimateActivity(@PathVariable(value = "activityid") String activityid){
        return activityMicroApi.end(activityid);
    }

    @Override
    public Response editEstimateActivity(@RequestBody Activity estimateActivity, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> userResponse = getUserByToken(token);
        UserLoginInfo userLoginInfo = userResponse.getResponseEntity();
        if (userLoginInfo == null) return userResponse;

        /**
         * ---------------------------------------------------------------------->>
         * @author: jong
         * @date: 2019-02-17
         * filter activity name in edit
         */
        SensitiveWordFilter swFilter = new SensitiveWordFilter();
        estimateActivity.setActivityname(swFilter.process(estimateActivity.getActivityname()));
        /*
         * ----------------------------------------------------------------------<<
         */

        estimateActivity.setCreator(userLoginInfo.getUserId());
        return activityMicroApi.edit(estimateActivity);
    }

    @Override
    public Response <Activity> getEstimateActivityList(@RequestBody QueryTree queryTree) {
        queryTree.addCondition(new QueryCondition("orderBycreatetime", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, ""));
        queryTree.addCondition(new QueryCondition("activitytype", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL,  IActivityMicroConstants.ACTIVITY_TYPE_ESTIMATE));
        Response<Activity> response = activityMicroApi.getList(queryTree);
        validateActivityList(response);
        return response;
    }

    @Override
    public Response getEstimateTeacherList(@RequestBody QueryTree queryTree) {
        queryTree.addCondition(new QueryCondition("activitytype", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, IActivityMicroConstants.ACTIVITY_TYPE_ESTIMATE));
        //queryTree.addCondition(new QueryCondition("isgoods", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, IResourceMicroConstants.RES_SOURCE_GOODS));
        List<String> userids = resourceMicroApi.getCreators(queryTree).getPageInfo().getList();
        queryTree.addCondition(new QueryCondition("userids", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, userids));
        Response<UserLoginInfo> response = userMicroApi.getList(queryTree);
        getTeacherData(response);
        return response;
    }

    @Override
    public Response<ExportMan> getEstimateExporterList(@RequestBody QueryTree queryTree) {
        queryTree.getConditions().add(new QueryCondition("sourceid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, IResourceMicroConstants.RES_SOURCE_ESTIMATE));
        return getExportManList(queryTree);
    }

    @Override
    public Response<Activity> getRecentEstimateList(@RequestBody QueryTree queryTree) {
        QueryCondition queryCondition = new QueryCondition("activitytype", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL,  IActivityMicroConstants.ACTIVITY_TYPE_ESTIMATE);
        if (queryTree.getConditions() == null)
            queryTree.setConditions(new ArrayList<>());
        queryTree.getConditions().add(queryCondition);

        OrderMethod orderMethod = new OrderMethod("orderBycreatetime", OrderMethod.Method.DESC);
        if (queryTree.getOrderMethods() == null)
            queryTree.setOrderMethods(new ArrayList<>());
        queryTree.getOrderMethods().add(orderMethod);

        Response<Activity> response = activityMicroApi.getList(queryTree);
        validateActivityList(response);

        return response;
    }

    @Override
    public Response getMyEstimateList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();
        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS)) return loginInfoResponse;

        UserLoginInfo user = loginInfoResponse.getResponseEntity();
        Map<String, String> map = new HashMap<>();
        map.put("activitytype", IActivityMicroConstants.ACTIVITY_TYPE_ESTIMATE);
        map.put("userid", user.getUserId());
        List<String> activityids = resourceMicroApi.getActivitIds(map).getPageInfo().getList();
        List<Activity> resultList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(activityids)) {
            for (String activityid : activityids) {
                Activity activity = activityMicroApi.get(activityid).getResponseEntity();
                resultList.add(activity);
            }
        }
        Pagination pagination = queryTree.getPagination();
        Map<String, Object> pageMap = new HashMap<>();
        pageMap.put("list", resultList);
        pageMap.put("start", pagination.getStart());
        pageMap.put("numperpage", pagination.getNumPerPage());
        pageMap.put("pageno", pagination.getPageNo());

        return activityMicroApi.regetPageInfoWithList(pageMap);
    }

    @Override
    public Response voteTeacher(@PathVariable(value = "userid") String userid, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> userResponse = getUserByToken(token);
        UserLoginInfo user = userResponse.getResponseEntity();
        if (user == null) {
            return userResponse;
        }

        UserLoginInfo teacher = userMicroApi.get(userid).getResponseEntity();

        int todayCount;
        QueryTree queryTree = new QueryTree();
        QueryCondition teacherCond = new QueryCondition("teacherid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, teacher.getUserId());
        queryTree.addCondition(teacherCond);
        queryTree.addCondition(new QueryCondition("userid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, user.getUserId()));
        todayCount = teacherVoteMicroApi.getCount(queryTree).getResponseEntity();
        if (todayCount > 0)
            return new Response(IErrorMessageConstants.ERR_CODE_VOTE_TEACHER_MORE, IErrorMessageConstants.ERR_MSG_VOTE);

        queryTree.getConditions().remove(teacherCond);
        queryTree.addCondition(new QueryCondition("today", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, ""));
        todayCount = teacherVoteMicroApi.getCount(queryTree).getResponseEntity();

        if (todayCount > 4)
            return new Response(IErrorMessageConstants.ERR_CODE_VOTE_TEACHER_TODAY, IErrorMessageConstants.ERR_MSG_OVER_VOTE_TODAY);

        {
            int updateVotetimes;

            if (teacher.getVotetimes() == null)
                updateVotetimes = 1;
            else
                updateVotetimes = teacher.getVotetimes() + 1;
            UserLoginInfo example = new UserLoginInfo();
            example.setUserId(userid);
            example.setVotetimes(updateVotetimes);
            userMicroApi.update(example);

            TeacherVote teacherVote = new TeacherVote();
            teacherVote.setUserid(user.getUserId());
            teacherVote.setTeacherid(teacher.getUserId());
            teacherVote.setVotetime(new Timestamp(System.currentTimeMillis()));
            teacherVoteMicroApi.add(teacherVote);

            return new Response<>(updateVotetimes);
        }
    }

    @Override
    public Response exportTeacherDetail(@RequestBody QueryTree queryTree) throws Exception {
        queryTree.getConditions().add(new QueryCondition("activitytype", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, IActivityMicroConstants.ACTIVITY_TYPE_ESTIMATE));
        List<String> userids = resourceMicroApi.getCreators(queryTree).getPageInfo().getList();
        queryTree.getConditions().add(new QueryCondition("userids", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, userids));
        Response<UserLoginInfo> response = userMicroApi.getList(queryTree);

        List<UserLoginInfo> teacherData = getTeacherData(response);
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

        HSSFRow topRow = hssfSheet.createRow(1);
        topRow.createCell(0);
        topRow.createCell(1);
        topRow.createCell(2);
        hssfSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
        topRow.getCell(0).setCellValue("资源排行");

        HSSFRow titleRow = hssfSheet.createRow(1);
        titleRow.createCell(0);titleRow.getCell(0).setCellValue("姓名");
        titleRow.createCell(1);titleRow.getCell(1).setCellValue("区域");
        titleRow.createCell(2);titleRow.getCell(2).setCellValue("学校");
        titleRow.createCell(3);titleRow.getCell(3).setCellValue("年级");
        titleRow.createCell(4);titleRow.getCell(4).setCellValue("任教学科");
        titleRow.createCell(5);titleRow.getCell(5).setCellValue("参与评比时间");
        titleRow.createCell(6);titleRow.getCell(6).setCellValue("得票数");

        if (teacherData != null) {
            for(int i = 0; i <teacherData.size(); i++)
            {
                UserLoginInfo teacher = teacherData.get(i);
                HSSFRow row = hssfSheet.createRow(i + 2);
                row.createCell(0);row.getCell(0).setCellValue(teacher.getRealName());
                row.createCell(1);row.getCell(1).setCellValue(teacher.getAreaName());
                row.createCell(2);row.getCell(2).setCellValue(teacher.getOrgName());
                row.createCell(3);row.getCell(3).setCellValue("");
                row.createCell(4);row.getCell(4).setCellValue("");
                row.createCell(5);row.getCell(5).setCellValue(DateUtils.formatDate(teacher.getLastuploadtime(), DateUtil.FORMAT_YYYY_MM_DD_SECONDS));
                row.createCell(6);row.getCell(6).setCellValue(teacher.getVotetimes());
            }
        }

        hssfWorkbook.write(fileOutputStream);

        result.put("downloadUrl", getRootURL() + "/excelFiles/" + fileName);
        return new Response<>(result);
    }

    private List<UserLoginInfo> getTeacherData(Response<UserLoginInfo> response) {
        List<UserLoginInfo> teacherList = null;
        if (response.getPageInfo() != null) {
            teacherList = response.getPageInfo().getList();
            if (!CollectionUtils.isEmpty(teacherList)) {
                for (UserLoginInfo teacher : teacherList) {
                    Integer contribution = integralMicroApi.getUserContribution(teacher.getUserId()).getResponseEntity();
                    teacher.setContribution(contribution);
                }
            }
        }

        return teacherList;
    }
}
