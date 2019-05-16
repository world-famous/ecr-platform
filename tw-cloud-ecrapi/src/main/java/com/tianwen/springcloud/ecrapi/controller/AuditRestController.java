package com.tianwen.springcloud.ecrapi.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.OrderMethod;
import com.tianwen.springcloud.commonapi.query.Pagination;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.AuditRestApi;
import com.tianwen.springcloud.ecrapi.constant.ICommonConstants;
import com.tianwen.springcloud.ecrapi.constant.IErrorMessageConstants;
import com.tianwen.springcloud.ecrapi.util.CommonUtil;
import com.tianwen.springcloud.microservice.activity.entity.Activity;
import com.tianwen.springcloud.microservice.base.entity.DictItem;
import com.tianwen.springcloud.microservice.base.entity.School;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
import com.tianwen.springcloud.microservice.bussinessassist.entity.Audit;
import com.tianwen.springcloud.microservice.resource.constant.IResourceMicroConstants;
import com.tianwen.springcloud.microservice.resource.entity.ResAuditInfo;
import com.tianwen.springcloud.microservice.resource.entity.Resource;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
@RequestMapping(value = "/audit")
public class AuditRestController extends BaseRestController implements AuditRestApi
{
    private static final String ACTIVITY_BONUS = "活动积分是 ";

    @Override
    public Response<com.tianwen.springcloud.microservice.resource.entity.Audit> getAuditList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token) {
        Response<com.tianwen.springcloud.microservice.resource.entity.Audit> resp = resourceMicroApi.getAuditList(queryTree);
        if (resp.getPageInfo() != null) {
            List<com.tianwen.springcloud.microservice.resource.entity.Audit> data = resp.getPageInfo().getList();
            for (com.tianwen.springcloud.microservice.resource.entity.Audit audit : data) {
                Resource resource = resourceMicroApi.get(audit.getObjectid()).getResponseEntity();
                if (resource != null) {
                    resource = validateResource(resource);
                    audit.setResource(resource);
                    String userid = audit.getAudituser();
                    if (!StringUtils.isEmpty(userid))
                    {
                        UserLoginInfo user = userMicroApi.get(userid).getResponseEntity();
                        if (user != null)
                            audit.setAudituser(user.getRealName());
                        else
                            audit.setAudituser("");
                    }
                }
            }
        }
        return  resp;
    }

    @Override
    @RequestMapping(value = "/allowResource", method = RequestMethod.POST)
    public Response allowResource(@RequestBody Map<String, Object> param, @RequestHeader(value = "token") String token)
    {
        Response<UserLoginInfo> userResponse = getUserByToken(token);
        UserLoginInfo userLoginInfo = userResponse.getResponseEntity();
        if (userLoginInfo == null) return userResponse;

        Integer score = (Integer) param.get("auditscore");

        List<String> contentids = (List<String>) param.get("contentids");
        if (!CollectionUtils.isEmpty(contentids)) {
            for (String contentid : contentids) {
                Resource resource = resourceMicroApi.get(contentid).getResponseEntity();
                boolean isreward = StringUtils.equals(resource.getSourceid(), IResourceMicroConstants.RES_SOURCE_REWARD);
                boolean iscollection = StringUtils.equals(resource.getSourceid(), IResourceMicroConstants.RES_SOURCE_COLLECTION);
                boolean isestimate = StringUtils.equals(resource.getSourceid(), IResourceMicroConstants.RES_SOURCE_ESTIMATE);
                if ( !StringUtils.isEmpty(resource.getActivityid()) && score != null) {
                    Activity rewardActivity = activityMicroApi.get(resource.getActivityid()).getResponseEntity();
                    if (rewardActivity != null) {
                        UserLoginInfo activityCreator = userMicroApi.get(rewardActivity.getCreator()).getResponseEntity();
                        if (activityCreator != null) {
                            int activityscore = 0;
                            if (rewardActivity.getBonuspoints() != null)
                                activityscore = rewardActivity.getBonuspoints();

                            rewardActivity.getBonuspoints(); // TODO: strange code
                            if (score > activityscore)
                                return new Response(IErrorMessageConstants.ERR_CODE_BONUS_TOO_MATCH,
                                        ACTIVITY_BONUS + String.valueOf(activityscore) + "。" + IErrorMessageConstants.ERR_MSG_BONUS_TOO_MATCH);
                            else {
                                int actiontype = 0;
                                if (isreward)
                                    actiontype = ICommonConstants.ACTION_JOIN_REWARD;
                                else if (iscollection)
                                    actiontype = ICommonConstants.ACTION_JOIN_COLLECT;
                                else if (isestimate)
                                    actiontype = ICommonConstants.ACTION_JOIN_ESTIMATE;

                                doAction(actiontype, resource, userLoginInfo, score);
                                if (isreward) {
                                    logOptionEntity(rewardActivity.getActivityid(), rewardActivity.getActivityid(), ICommonConstants.OPTION_OPTIONTYPE_REWARD_IN, token);
                                    String commnet = "您的悬赏资源审核通过，获得积分" + score;
                                    List<String> receivers = new ArrayList<>();
                                    receivers.add(resource.getCreator());
                                    saveMessage("1", activityCreator.getRealName(), commnet, receivers, ecoPlatformClientID, token);
                                } else {
                                    logOptionEntity(rewardActivity.getActivityid(), rewardActivity.getActivityid(), ICommonConstants.OPTION_OPTIONTYPE_COLLECTION_IN, token);
                                    String commnet = "您的征集资源审核通过，获得积分" + score;
                                    List<String> receivers = new ArrayList<>();
                                    receivers.add(resource.getCreator());
                                    saveMessage("1", activityCreator.getRealName(), commnet, receivers, ecoPlatformClientID, token);
                                }
                            }
                        }
                    }
                } else {
                    doAction(ICommonConstants.ACTION_UPLOAD, resource, userLoginInfo, score);
                    String commnet = "您的资源审核通过，获得积分" + score;
                    List<String> receivers = new ArrayList<>();
                    receivers.add(resource.getCreator());
                    saveMessage("1",userLoginInfo.getRealName(),commnet,receivers,ecoPlatformClientID,token);
                }

                String creator = resource.getCreator();
                UserLoginInfo creatorinfo = userMicroApi.get(creator).getResponseEntity();
                if (creatorinfo != null) {
                    UserLoginInfo userInfo = new UserLoginInfo();
                    userInfo.setUserId(creator);
                    userInfo.setBirthday(null);
                    userMicroApi.update(userLoginInfo);
                }
            }
        }

        return auditResource(param, true, token);
    }

    @Override
    @RequestMapping(value = "/denyResource", method = RequestMethod.POST)
    public Response<Audit> denyResource(@RequestBody Map<String, Object> param, @RequestHeader(value = "token") String token)
    {
        return auditResource(param, false, token);
    }

    public List<Map<String, Object>> getAuditStatisticsData(QueryTree queryTree)
    {
        QueryTree subjectQuery = new QueryTree();
        subjectQuery.addCondition(new QueryCondition("dicttypeid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "SUBJECT"));

        List<DictItem> subjectItems = dictItemMicroApi.search(subjectQuery).getPageInfo().getList();
        if (subjectItems == null)
            subjectItems = new ArrayList<>();

        Map<String, String> subjects = new HashMap<>();
        for(DictItem subject : subjectItems)
            subjects.put(subject.getDictvalue(), subject.getDictname());

        List<Map<String, Object>> result = new ArrayList<>();
        QueryCondition auditstatus = queryTree.getQueryCondition("status");
        OrderMethod orderMethod = null;
        int step = 2;
        String realstatus = "";

        if (queryTree.getOrderMethods() != null && queryTree.getOrderMethods().size() > 0)
            orderMethod = queryTree.getOrderMethods().get(0);

        if (auditstatus != null)
        {
            realstatus = auditstatus.getFieldValues()[0].toString();
            if (realstatus.equals("3") || realstatus.equals("4"))
                step = 1;
        }

        QueryTree schoolQuery = new QueryTree();
        QueryCondition orgCond = queryTree.getQueryCondition("orgname");
        if (orgCond != null) {
            queryTree.getConditions().remove(orgCond);
            schoolQuery.addCondition(orgCond);
        }
        List<School> schools = schoolMicroApi.getSchoolList(schoolQuery).getPageInfo().getList();

        for(School school : schools)
        {
            String orgName = school.getOrgname();
            if (orgName == null)
                orgName = "";
            if (step == 2 || realstatus.equals("4")) {
                Map<String, Object> deny = new HashMap<>();
                deny.put("id", school.getOrgid());
                deny.put("name", orgName);
                deny.put("status", "4");
                deny.put("count", 0);
                result.add(deny);
            }

            if (step == 2 || realstatus.equals("3")) {
                Map<String, Object> allow = new HashMap<>();
                allow.put("id", school.getOrgid());
                allow.put("name", orgName);
                allow.put("status", "3");
                allow.put("count", 0);
                result.add(allow);
            }
        }

        for (Map<String, Object> item : result)
        {
            String orgid = item.get("id").toString();
            String status = item.get("status").toString();
            QueryTree itemQuery = new QueryTree();

            itemQuery.getConditions().addAll(queryTree.getConditions());

            if (step == 2)
                itemQuery.addCondition(new QueryCondition("status", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, status));

            QueryTree userQuery = new QueryTree();
            userQuery.addCondition(new QueryCondition("orgid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, orgid));
            List<String> userids = userMicroApi.getUserIdsByQueryTree(userQuery).getPageInfo().getList();
            itemQuery.addCondition(new QueryCondition("creatorids", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, userids));

            List<ResAuditInfo> audits = resourceMicroApi.getResourceCountByAudit(itemQuery).getPageInfo().getList();
            List<String> subname = new ArrayList<>();
            List<Integer> subcount = new ArrayList<>();
            List<String> schoolsection = new ArrayList<>();

            if (audits == null)
                audits = new ArrayList<>();

            int sum = 0, count;
            for(ResAuditInfo audit : audits) {
                count = audit.getCount() == null ? 0 : audit.getCount().intValue();
                subname.add(subjects.get(audit.getSubjectid()));
                subcount.add(count);
                schoolsection.add(audit.getSchoolsection());
                sum += count;
            }

            item.put("subname", subname);
            item.put("subcount", subcount);
            item.put("schoolsection", schoolsection);
            item.put("count", sum);
        }

        if (orderMethod != null)
        {
            Map<String, Object> p1, p2;
//            Author : GOD 2019-2-14 Bug ID: #740

            if(orderMethod.getField().equalsIgnoreCase("count")) {
                for (int i = 0; i < result.size() - 1; i++)
                    for (int j = i + 1; j < result.size(); j++)
                    {
                        Integer s1, s2;
                        OrderMethod.Method method = orderMethod.getMethod();
                        s1 = (Integer)result.get(i).get(orderMethod.getField());
                        s2 = (Integer)result.get(j).get(orderMethod.getField());

                        if ((method == OrderMethod.Method.ASC && s1 > s2)
                                || (method == OrderMethod.Method.DESC && s1 < s2))
                        {
                            p1 = result.get(i);
                            p2 = result.get(j);
                            result.set(i, p2);
                            result.set(j, p1);
                        }
                    }
            } else {
                for (int i = 0; i < result.size() - 1; i++)
                    for (int j = i + 1; j < result.size(); j++)
                    {
                        String s1, s2;
                        OrderMethod.Method method = orderMethod.getMethod();
                        s1 = result.get(i).get(orderMethod.getField()).toString();
                        s2 = result.get(j).get(orderMethod.getField()).toString();

                        if ((method == OrderMethod.Method.ASC && s1.compareToIgnoreCase(s2) > 0)
                                || (method == OrderMethod.Method.DESC && s1.compareToIgnoreCase(s2) < 0))
                        {
                            p1 = result.get(i);
                            p2 = result.get(j);
                            result.set(i, p2);
                            result.set(j, p1);
                        }
                    }
            }
//            Author : GOD 2019-2-14 Bug ID: #740
        }

        return result;
    }

    //    Author : GOD 2019-2-18 Bug ID: #781
    public List<Long> getAuditStatisticsDataDetail(QueryTree queryTree)
    {
        QueryTree subjectQuery = new QueryTree();
        subjectQuery.addCondition(new QueryCondition("dicttypeid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "SUBJECT"));

        List<DictItem> subjectItems = dictItemMicroApi.search(subjectQuery).getPageInfo().getList();
        if (subjectItems == null)
            subjectItems = new ArrayList<>();

        Map<String, String> subjects = new HashMap<>();
        for(DictItem subject : subjectItems)
            subjects.put(subject.getDictvalue(), subject.getDictname());

        QueryCondition auditstatus = queryTree.getQueryCondition("status");

        QueryTree itemQuery = new QueryTree();

        itemQuery.getConditions().addAll(queryTree.getConditions());

        QueryTree userQuery = new QueryTree();
        userQuery.addCondition(new QueryCondition("orgid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, queryTree.getQueryCondition("orgid").getFieldValues()[0].toString()));
        List<String> userids = userMicroApi.getUserIdsByQueryTree(userQuery).getPageInfo().getList();
        itemQuery.addCondition(new QueryCondition("creatorids", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, userids));

        List<Long> audits = resourceMicroApi.getResourceCountByAuditSharerange(itemQuery).getPageInfo().getList();

        return audits;
    }
    //    Author : GOD 2019-2-18 Bug ID: #781

    @Override
    @RequestMapping(value = "/getAuditStatistics", method = RequestMethod.POST)
    public Response<Map<String, Object>> getAuditStatistics(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token)
    {
        List<Map<String, Object>> allList = getAuditStatisticsData(queryTree), pageList;
        //Pagination
        Pagination pagination = queryTree.getPagination();

        int count = allList.size();
        int start = pagination.getStart(), pageSize = pagination.getNumPerPage();
        if (start > allList.size())
            pageList = new ArrayList<>();
        else
            pageList = allList.subList(start ,start + pageSize <= allList.size() ? start + pageSize : allList.size());

        Response<Map<String, Object>> resp = new Response<>(pageList);
        PageInfo pageInfo = resp.getPageInfo();

        pageInfo.setPageNum(pagination.getPageNo());
        pageInfo.setPageSize(pagination.getNumPerPage());
        pageInfo.setTotal(count);

        return resp;
    }

    //    Author : GOD 2019-2-18 Bug ID: #781
    @Override
    @RequestMapping(value = "/getAuditStatisticsDetail", method = RequestMethod.POST)
    public Response<Long> getAuditStatisticsDetail(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token)
    {
        List<Long> detailData = getAuditStatisticsDataDetail(queryTree);

        Response<Long> resp = new Response<>(detailData);

        return resp;
    }
    //    Author : GOD 2019-2-18 Bug ID: #781

    @Override
    @RequestMapping(value = "/getAuditUserList", method = RequestMethod.GET)
    public Response<UserLoginInfo> getAuditUserList(@RequestHeader(value = "token") String token)
    {
        List<String> userids = auditMicroApi.getAuditorList().getPageInfo().getList();
        if (userids == null) userids = new ArrayList<>();

        List<UserLoginInfo> result = new ArrayList<>();
        for(String userid : userids)
        {
            UserLoginInfo userLoginInfo = userMicroApi.get(userid).getResponseEntity();
            if (userLoginInfo != null)
                result.add(userLoginInfo);
        }

        return new Response<>(result);
    }

    @Override
    public Response<Audit> getByContentid(@PathVariable(value = "contentid") String contentid) {
        return auditMicroApi.getByContentid(contentid);
    }

    @Override
    public Response<Map<String, Object>> exportToExcel(@RequestBody QueryTree queryTree) throws Exception{
        List<Map<String, Object>> statistics = getAuditStatisticsData(queryTree);
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
        Workbook hssfWorkbook = new HSSFWorkbook();
        Sheet hssfSheet = hssfWorkbook.createSheet();

        Row titleRow = hssfSheet.createRow(0);
        titleRow.createCell(0);titleRow.getCell(0).setCellValue("学校名称");
        titleRow.createCell(1);titleRow.getCell(1).setCellValue("状态");
        titleRow.createCell(2);titleRow.getCell(2).setCellValue("资源数");

        for(int i = 0; i <statistics.size(); i++)
        {
            Map<String, Object> data = statistics.get(i);
            Row row = hssfSheet.createRow(i + 1);
            row.createCell(0);row.getCell(0).setCellValue(data.get("name").toString());

            String status = data.get("status").toString();
            if (status.equals("3"))
                status = "已审核";
            else if (status.equals("4"))
                status = "已驳回";
            else
                status = "";
            row.createCell(1);row.getCell(1).setCellValue(status);

            row.createCell(2);row.getCell(2).setCellValue(data.get("count").toString());
        }

        hssfWorkbook.write(fileOutputStream);

        result.put("downloadUrl", getRootURL() + "/excelFiles/" + fileName);
        return new Response<>(result);
    }

    private Response auditResource(Map<String, Object> param, boolean isPass, String token) {
        Response<UserLoginInfo> userResponse = getUserByToken(token);
        UserLoginInfo operator = userResponse.getResponseEntity();
        if (operator==null) return userResponse;

        List<String> contentids = (List<String>)param.get("contentids");
        List<Audit> result = new ArrayList<>();
        String resultCode = IErrorMessageConstants.OPERATION_SUCCESS;
        for(String contentid : contentids)
        {
            Audit entity = new Audit();
            Resource resource = resourceMicroApi.get(contentid).getResponseEntity();
            if (resource.getFileInfo() == null && isPass) {
                resultCode = IErrorMessageConstants.ERR_CODE_FILE_NOT_EXIST;
                continue;
            }
            String userid = operator.getUserId();

            resource = generateContentNo(resource);
            resource.setStatus(isPass ? "3" : "4");
            resourceMicroApi.update(resource);

            entity.setObjectid(resource.getContentid());
            entity.setObjecttype("1");
            entity.setAudituser(userid);
            entity.setAudittime(new Timestamp(System.currentTimeMillis()));
            entity.setAudittype(param.get("audittype").toString());
            if (param.get("remark") != null)
                entity.setRemark(param.get("remark").toString());
            entity.setResult(isPass ? "1" : "0");
            entity = auditMicroApi.insert(entity).getResponseEntity();
            result.add(entity);

            if (entity != null) {
                if (isPass)
                    logOptionEntity(resource.getContentid(), entity.getAuditid(), ICommonConstants.OPTION_OPTIONTYPE_AUDIT_ALLOW, token);
                else {
                    String commnet = "您的资源审核不通过了";
                    List<String> receivers = new ArrayList<>();
                    receivers.add(resource.getCreator());
                    saveMessage("1",operator.getRealName(),commnet,receivers,ecoPlatformClientID,token);
                    logOptionEntity(resource.getContentid(), entity.getAuditid(), ICommonConstants.OPTION_OPTIONTYPE_AUDIT_DENY, token);
                }
            }
        }

        Response response = new Response(result);
        if (isPass && StringUtils.equals(resultCode, IErrorMessageConstants.ERR_CODE_FILE_NOT_EXIST)) {
//            messageSource.getMessage(resultCode)
            response.getServerResult().setResultCode(resultCode);
            response.getServerResult().setResultMessage(IErrorMessageConstants.ERR_MSG_FILE_NOT_EXIST);
        }
        return response;
    }
}
