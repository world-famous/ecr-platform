package com.tianwen.springcloud.ecrapi.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.log.SystemControllerLog;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.FeedbackRestApi;
import com.tianwen.springcloud.ecrapi.constant.ICommonConstants;
import com.tianwen.springcloud.ecrapi.constant.IErrorMessageConstants;
import com.tianwen.springcloud.ecrapi.util.CommonUtil;
import com.tianwen.springcloud.microservice.base.entity.Area;
import com.tianwen.springcloud.microservice.base.entity.Organization;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
import com.tianwen.springcloud.microservice.bussinessassist.api.FeedBackMicroApi;
import com.tianwen.springcloud.microservice.bussinessassist.entity.FeedBack;
import com.tianwen.springcloud.microservice.resource.entity.Resource;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/feedback")
public  class FeedbackRestController extends BaseRestController implements FeedbackRestApi {
    @Autowired
    private FeedBackMicroApi feedBackMicroApi;

    @Override
    @ApiOperation(value = "资源报错/资源举报上传", notes = "资源报错/资源举报上传")
    @ApiImplicitParam(name = "entity", value = "资源报错/资源举报信息实体", required = true, dataType = "Feedback", paramType = "body")
    @SystemControllerLog(description = "资源报错/资源举报上传")
    public Response upLoadFeedback(@RequestBody FeedBack feedBack, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();
        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS)) return loginInfoResponse;

        if(StringUtils.equals(feedBack.getType(),"2") && StringUtils.isEmpty(feedBack.getUserphone()))
            return new Response(IErrorMessageConstants.ERR_CODE_PHONENUMBER_INVALID,IErrorMessageConstants.ERR_MSG_PHONENUMBER_INVALID);
        if (StringUtils.equals(feedBack.getType(),"2") && StringUtils.isEmpty(feedBack.getUseremail()))
            return new Response(IErrorMessageConstants.ERR_CODE_EMAIL_INVALID,IErrorMessageConstants.ERR_MSG_EMAIL_INVALID);
        UserLoginInfo user = loginInfoResponse.getResponseEntity();
        Resource resource = resourceMicroApi.get(feedBack.getObjectid()).getResponseEntity();
        feedBack.setObjecttype(resource.getContenttype());
        feedBack.setUsername(user.getRealName() + "#" + user.getLoginName());
        Timestamp createtime = new Timestamp(System.currentTimeMillis());
        Timestamp lastmodifytime = new Timestamp(System.currentTimeMillis());
        feedBack.setCreatetime(createtime);
        feedBack.setLastmodifytime(lastmodifytime);
        feedBack.setIsscored("0");
        if (StringUtils.equals(feedBack.getType(),"1"))
            logOptionEntity(feedBack.getObjectid(),feedBack.getObjectid(), ICommonConstants.OPTION_OPTIONTYPE_FEEDBACKERROR, token);
        else
            logOptionEntity(feedBack.getObjectid(),feedBack.getObjectid(), ICommonConstants.OPTION_OPTIONTYPE_FEEDBACK, token);
        return feedBackMicroApi.insert(feedBack);
    }

    @Override
    @ApiOperation(value = "获取资源报错/资源举报列表", notes = "获取资源报错/资源举报列表")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "Querytree", paramType = "body")
    @SystemControllerLog(description = "获取资源报错/资源举报列表")
    public Response<FeedBack> getFeedbackList(@RequestBody QueryTree queryTree) {
        Response<FeedBack> response = feedBackMicroApi.getList(queryTree);

        List<FeedBack> feedBackList = response.getPageInfo().getList();
        if (!CollectionUtils.isEmpty(feedBackList)) {
            for (FeedBack feedBack : feedBackList) {
                Resource resource = resourceMicroApi.get(feedBack.getObjectid()).getResponseEntity();
                validateResource(resource);
                feedBack.setResource(resource);
//                String[]userloginname = feedBack.getUsername().split("-");
                String[]userloginname = feedBack.getUsername().split("#");
                String realuserloginname = "";
                if(userloginname.length > 1){
                    realuserloginname = userloginname[1];
                } else {
                    realuserloginname = userloginname[0];
                }
                UserLoginInfo userLoginInfo = userMicroApi.getByLoginName(realuserloginname).getResponseEntity();
                if (userLoginInfo != null) {
                    Organization organization = organizationMicroApi.get(userLoginInfo.getOrgId()).getResponseEntity();
                    if (organization !=null) {
                        feedBack.setSchool(organization.getOrgname());
                        Area areadist = areaMicroApi.get(organization.getAreaid()).getResponseEntity();
                        if (areadist != null && !StringUtils.equals(areadist.getParentareaid(), "-1")) {
                            feedBack.setDistrict(areadist.getAreaname());
                            String parentid = areadist.getParentareaid();
                            Area areacity = areaMicroApi.get(parentid).getResponseEntity();

                            if (areacity != null && !StringUtils.equals(areacity.getParentareaid(), "-1")) {
                                feedBack.setCity(areacity.getAreaname());
                                Area areacastle = areaMicroApi.get(areacity.getParentareaid()).getResponseEntity();
                                feedBack.setCastle(areacastle.getAreaname());
                            } else if (areacity != null) {
                                feedBack.setCastle(areacity.getAreaname());
                                feedBack.setCity(feedBack.getDistrict());
                            }
                        } else if (areadist != null) {
                            feedBack.setCastle(areadist.getAreaname());
                        }
                    }
                }
            }
        }

        return response;
    }

    @Override
    @ApiOperation(value = "根据id列表删除资源报错/资源举报信息", notes = "根据id列表删除资源报错/资源举报信息")
    @ApiImplicitParam(name = "id列表", value = "资源报错/资源举报id列表", required = true, dataType = "List<String>", paramType = "body")
    @SystemControllerLog(description = "根据id列表删除资源报错/资源举报信息")
    public Response<FeedBack> batchDelete(@RequestBody List<String> feedbackids) {
        for(String feedbackid: feedbackids){
            FeedBack feedBack = feedBackMicroApi.get(feedbackid).getResponseEntity();
            if (feedBack != null){
                feedBack.setStatus("-1");
                feedBackMicroApi.update(feedBack);
            }
        }
        return new Response<>();
    }

    //    Author : GOD 2019-2-19 Bug ID: #804, 805
    @Override
    @ApiOperation(value = "根据id审核资源报错/资源举报信息", notes = "根据id审核资源报错/资源举报信息")
    @ApiImplicitParam(name = "id", value = "资源报错/资源举报id", required = true, dataType = "String", paramType = "body")
    @SystemControllerLog(description = "根据id审核资源报错/资源举报信息")
    public Response auditFeedback(@RequestBody String feedbackid, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();
        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS)) return loginInfoResponse;

        UserLoginInfo user = loginInfoResponse.getResponseEntity();
        FeedBack feedBack = feedBackMicroApi.get(feedbackid).getResponseEntity();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        feedBack.setAudittime(timestamp);
        feedBack.setAudituser(user.getRealName() + "#" + user.getLoginName());
        feedBack.setStatus("1");

        feedBackMicroApi.update(feedBack);

        return new Response<String>(feedBack.getReplycontent());
    }
    //    Author : GOD 2019-2-19 Bug ID: #804, 805

    @Override
    @ApiOperation(value = "导出获取资源报错/资源举报列表", notes = "导出获取资源报错/资源举报列表")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "Querytree", paramType = "body")
    @SystemControllerLog(description = "导出获取资源报错/资源举报列表")
    public Response<Map<String, Object>> exportToExcel(@RequestBody QueryTree queryTree) throws Exception{
        List<FeedBack> feedBackList = feedBackMicroApi.getList(queryTree).getPageInfo().getList();


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
        java.io.File chartFile = new java.io.File(homePath + fileName);
        if (!chartFile.createNewFile()) return null;

        FileOutputStream fileOutputStream = new FileOutputStream(chartFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet hssfSheet = hssfWorkbook.createSheet();

        HSSFRow topRow = hssfSheet.createRow(0);
        topRow.createCell(0);
        topRow.createCell(1);
        topRow.createCell(2);
        hssfSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
        if (feedBackList.get(0).getType() == "1")
            topRow.getCell(0).setCellValue("资源报错");
        else
            topRow.getCell(0).setCellValue("资源举报");

        HSSFRow titleRow = hssfSheet.createRow(1);
        titleRow.createCell(0);titleRow.getCell(0).setCellValue("挑错ID");
        titleRow.createCell(1);titleRow.getCell(1).setCellValue("举报类型");
        titleRow.createCell(2);titleRow.getCell(2).setCellValue("商品名称");
        titleRow.createCell(3);titleRow.getCell(3).setCellValue("详情页面");
        titleRow.createCell(4);titleRow.getCell(4).setCellValue("字段");
        titleRow.createCell(5);titleRow.getCell(5).setCellValue("学科");
        titleRow.createCell(6);titleRow.getCell(6).setCellValue("教材版本");
        titleRow.createCell(7);titleRow.getCell(7).setCellValue("年级");
        titleRow.createCell(8);titleRow.getCell(8).setCellValue("册别");
        titleRow.createCell(9);titleRow.getCell(9).setCellValue("章");
        titleRow.createCell(10);titleRow.getCell(10).setCellValue("节");
        titleRow.createCell(11);titleRow.getCell(11).setCellValue("知识点");
        titleRow.createCell(12);titleRow.getCell(12).setCellValue("错误问题描述");
        titleRow.createCell(13);titleRow.getCell(13).setCellValue("挑错人姓名");
        titleRow.createCell(14);titleRow.getCell(14).setCellValue("挑错人联系电话");
        titleRow.createCell(15);titleRow.getCell(15).setCellValue("所属学校");
        titleRow.createCell(16);titleRow.getCell(16).setCellValue("举报时间");
        titleRow.createCell(17);titleRow.getCell(17).setCellValue("处理状态");
        titleRow.createCell(18);titleRow.getCell(18).setCellValue("处理人");
        titleRow.createCell(19);titleRow.getCell(19).setCellValue("处理时间");
        titleRow.createCell(20);titleRow.getCell(20).setCellValue("奖励积分");


        for(int i = 0; i <feedBackList.size(); i++)
        {
            FeedBack data = feedBackList.get(i);
            Resource resource = resourceMicroApi.get(data.getObjectid()).getResponseEntity();
            validateResource(resource);
            HSSFRow row = hssfSheet.createRow(i + 2);
            row.createCell(0);row.getCell(0).setCellValue(data.getFeedbackid());
            row.createCell(1);if (data.getType() == "2") row.getCell(1).setCellValue(data.getSubtype());
            row.createCell(2);row.getCell(2).setCellValue(resource.getName());
            row.createCell(3);row.getCell(3).setCellValue(data.getReplycontent());
            row.createCell(4);row.getCell(4).setCellValue(resource.getSchoolsection());
            row.createCell(5);row.getCell(5).setCellValue(resource.getSubject());
            row.createCell(6);row.getCell(6).setCellValue(resource.getBookmodel());
            row.createCell(7);row.getCell(7).setCellValue(resource.getGrade());
            row.createCell(8);row.getCell(8).setCellValue(resource.getBookmodel());
            row.createCell(9);row.getCell(9).setCellValue(resource.getChapter());
            row.createCell(10);row.getCell(10).setCellValue(resource.getSection());
            row.createCell(11);row.getCell(11).setCellValue("");
            row.createCell(12);row.getCell(12).setCellValue(data.getRemark());
            row.createCell(13);row.getCell(13).setCellValue(data.getUsername());
            row.createCell(14);row.getCell(14).setCellValue(data.getUserphone());
            row.createCell(15);row.getCell(15).setCellValue(data.getSchool());
            row.createCell(16);row.getCell(16).setCellValue(data.getCreatetime());
            row.createCell(17);row.getCell(17).setCellValue(data.getStatus());
            row.createCell(18);row.getCell(18).setCellValue(data.getAudituser());
            row.createCell(19);row.getCell(19).setCellValue(data.getAudittime());
            row.createCell(20);row.getCell(20).setCellValue(data.getScore());
        }

        hssfWorkbook.write(fileOutputStream);

        result.put("downloadUrl", getRootURL() + "/excelFiles/" + fileName);
        return new Response<>(result);
    }

    //    Author : GOD 2019-2-19 Bug ID: #804, 805
    @Override
    @ApiOperation(value = "获取区域列表", notes = "获取区域列表")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "Querytree", paramType = "body")
    @SystemControllerLog(description = "获取区域列表")
    public Response setScore2Feedback(@RequestBody FeedBack feedBack, @RequestHeader(value = "token") String token) {
        if (feedBack.getScore() < 0)
            return new Response(IErrorMessageConstants.ERR_CODE_FEEDBACK_SCORE_INVALID, IErrorMessageConstants.ERR_MSG_FEEDBACK_SCORE_INVALID);

        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();
        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS)) return loginInfoResponse;

        UserLoginInfo user = loginInfoResponse.getResponseEntity();

        feedBack.setIsscored("1");

        feedBackMicroApi.update(feedBack);

        FeedBack feedBack1 = feedBackMicroApi.get(feedBack.getFeedbackid()).getResponseEntity();

        String replyComment =null;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        String[] creatorids = feedBack1.getUsername().split("#");
        UserLoginInfo feedbackCreator = userMicroApi.getByLoginName(creatorids[1]).getResponseEntity();
        if (feedbackCreator == null) return null;

        if (feedBack1.getScore() != 0) {
            Resource resource = resourceMicroApi.get(feedBack1.getObjectid()).getResponseEntity();
            if (resource != null) {
                doAction(ICommonConstants.ACTION_REPORT, resource, feedbackCreator, feedBack1.getScore());
            }

            replyComment = feedbackCreator.getRealName() + "老师，感谢您于" + DateUtils.formatDate(timestamp, "yyyy-MM-dd")
                    + "对提出的资源修改意见，奖励您" + feedBack1.getScore() + "积分";

        }
        else
            replyComment = feedbackCreator.getRealName() + "老师，感谢您于" + DateUtils.formatDate(timestamp,"yyyy-MM-dd")
                    +"对提出的资源举报，我们会尽快核实处理，谢谢";

        String[] username = feedBack1.getUsername().split("#");
        UserLoginInfo userLoginInfo = userMicroApi.getByLoginName(username[1]).getResponseEntity();
        List<String> receivers = new ArrayList<>();
        receivers.add(userLoginInfo.getUserId());
        saveMessage("1",user.getRealName(), replyComment,receivers,ecoPlatformClientID,token);

        return feedBackMicroApi.update(feedBack1);
    }
    //    Author : GOD 2019-2-19 Bug ID: #804, 805
}
