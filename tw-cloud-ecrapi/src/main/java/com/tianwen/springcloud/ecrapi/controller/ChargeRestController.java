package com.tianwen.springcloud.ecrapi.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.ChargeRestApi;
import com.tianwen.springcloud.ecrapi.constant.ICommonConstants;
import com.tianwen.springcloud.ecrapi.util.CommonUtil;
import com.tianwen.springcloud.microservice.base.entity.Area;
import com.tianwen.springcloud.microservice.base.entity.ScoreRule;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
import com.tianwen.springcloud.microservice.operation.entity.Charge;
import com.tianwen.springcloud.microservice.operation.entity.ChargeStatInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;


@RestController
@RequestMapping(value = "/charge")
public class ChargeRestController extends BaseRestController implements ChargeRestApi
{
    @Override
    public Response<Integer> getExchange(@RequestHeader(value = "token") String token) {
        QueryTree exchangeTree = new QueryTree();
        exchangeTree.addCondition(new QueryCondition("scoretype", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "6"));
        List<ScoreRule> ruleList = scoreRuleMicroApi.search(exchangeTree).getPageInfo().getList();
        int score = 100;
        if (!CollectionUtils.isEmpty(ruleList))
            score = ruleList.get(0).getScore();
        return new Response<>(score);
    }

    @Override
    @RequestMapping(value = "/chargeToAccount", method = RequestMethod.POST)
    public Response chargeToAccount(@RequestBody Map<String, Object> param, @RequestHeader(value = "token") String token)
    {
        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();
        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS)) return loginInfoResponse;
        String userid = loginInfoResponse.getResponseEntity().getUserId();

        param.put("userid", userid);
        param.put("exchange", getExchange(token).getResponseEntity());

        Response<Charge> resp = chargeMicroApi.chargeToAccount(param);
        Charge charge = resp.getResponseEntity();
        try {
            if (charge != null) {
                logOptionEntity(userid, charge.getChargeid(), ICommonConstants.OPTION_OPTIONTYPE_CHARGE, token);
                double exchange = Double.parseDouble(param.get("exchange").toString());
                double cost = Double.parseDouble(param.get("cost").toString());
                String commnet = "您已充值"+ exchange * cost +"分" ;
                List<String> receivers = new ArrayList<>();
                receivers.add(userid);
                saveMessage("1","系统",commnet,receivers,ecoPlatformClientID,token);
            }
        }
        catch (Exception e) { e.printStackTrace(); }
        return resp;
    }

    @Override
    @RequestMapping(value = "/orderCharge", method = RequestMethod.POST)
    public Response orderChange(@RequestBody Map<String, Object> param, @RequestHeader(value = "token") String token)
    {
        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();
        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS)) return loginInfoResponse;
        String userid = loginInfoResponse.getResponseEntity().getUserId();

        param.put("userid", userid);
        param.put("exchange", getExchange(token).getResponseEntity());

        Response<Charge> resp = chargeMicroApi.placeCharge(param);

        return resp;
    }

    @Override
    @RequestMapping(value = "/getChargeItemList", method = RequestMethod.POST)
    public Response<Charge> getChargeItemList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token)
    {
        QueryCondition lnCond = queryTree.getQueryCondition("loginname");
        QueryCondition rnCond = queryTree.getQueryCondition("realname");

        if (lnCond != null && rnCond != null)
        {
            String lname = null, rname = null;
            try{
                lname = lnCond.getFieldValues()[0].toString();
            }
            catch (Exception e) {
                lname = "";
            }
            try{
                rname = rnCond.getFieldValues()[0].toString();
            }
            catch (Exception e) {
                rname = "";
            }
            if (!StringUtils.isEmpty(lname) || !StringUtils.isEmpty(rname)) {
                List<String> userids = userMicroApi.getUserIdsByQueryTree(queryTree).getPageInfo().getList();
                    if (!CollectionUtils.isEmpty(userids)) {
                    userids.add("");
                    queryTree.addCondition(new QueryCondition("userids", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, userids));
                }
            }
        }

        Response<Charge> resp = chargeMicroApi.getChargeItemList(queryTree);
        for(Charge charge : resp.getPageInfo().getList())
        {
            UserLoginInfo user = userMicroApi.get(charge.getUserid()).getResponseEntity();
            if (user != null) {
                charge.setLoginname(user.getLoginName());
                charge.setRealname(user.getRealName());
                charge.setChargername(user.getRealName());
            }
        }

        return resp;
    }

    @Override
    public Response<Map<String, Object>> getChargeStatistics(@RequestHeader(value = "token") String token) {
        List<String> userids = userMicroApi.getUserIdsByQueryTree(new QueryTree()).getPageInfo().getList();
        QueryTree queryTree = new QueryTree();
        if (!CollectionUtils.isEmpty(userids)) {
            userids.add("");
            queryTree.addCondition(new QueryCondition("userids", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, userids));
        }
        return chargeMicroApi.getChargeStatus(queryTree);
    }

    @Override
    public Response<List<ChargeStatInfo>> getChargeByArea(@RequestBody Map<String, Object> param) {
        List<Area> areas = areaMicroApi.getAreaInfoList().getPageInfo().getList();
        return new Response<List<ChargeStatInfo>>(chargeMicroApi.getChargeStatByArea(parseToAreaInfoList(areas)).getPageInfo().getList());
    }

    @Override
    public Response<List<UserLoginInfo>> getTopTenUsers(@RequestHeader(value = "token") String token) {
        Map<String, Double> scoreList = new HashMap<>();
        List <Charge> chargeList = chargeMicroApi.getAllData(new QueryTree()).getPageInfo().getList();
        List <UserLoginInfo> resultData = new ArrayList<>();

        for(Charge charge : chargeList)
        {
            String userid = charge.getUserid();
            if (!StringUtils.isEmpty(userid))
            {
                UserLoginInfo user = userMicroApi.get(userid).getResponseEntity();
                if (user != null)
                {
                    Double count = scoreList.get(userid);

                    if (count == null)
                        count = 0.0;

                    count += charge.getCost();// * charge.getExchange();

                    scoreList.put(userid, count);
                }
            }
        }

        Set<Map.Entry<String, Double>> items = scoreList.entrySet();

        for(Map.Entry<String, Double> item : items) {
            UserLoginInfo user = userMicroApi.get(item.getKey()).getResponseEntity();
            user.setChargeScore(scoreList.get(item.getKey()));
            resultData.add(user);
        }

        if (resultData.size() > 10)
            resultData = resultData.subList(0, 9);

        resultData.sort(new Comparator<UserLoginInfo>() {
                @Override
                public int compare(UserLoginInfo o1, UserLoginInfo o2) {
                    double first = o1.getChargeScore(), second = o2.getChargeScore();
                    if (first > second) return -1;
                    if ( Double.compare(first, second) == 0 ) return 0;
                    return 1;
                }
            }
        );

        return new Response<List<UserLoginInfo>>(resultData);
    }

    @Override
    public Response<Map<String, Object>> exportToExcel() throws Exception{
        Map<String, Object> param = new HashMap<>();

        QueryTree areaQuery = new QueryTree();
        areaQuery.addCondition(new QueryCondition("parentareaid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "-1"));
        List<Area> areas = areaMicroApi.getList(areaQuery).getPageInfo().getList();
        List<String> areaidList = new ArrayList<>();
        for (Area area : areas)
            areaidList.add(area.getAreaid());
        param.put("areaids", areaidList);

        List<ChargeStatInfo> statistics = getChargeByArea(param).getResponseEntity();
        Map<String, Object> result = new HashMap<>();

        Boolean isEcrDirExist = new File(ecrFileLocation).isDirectory();
        if (isEcrDirExist == false) {
            boolean res = new File(ecrFileLocation).mkdir();
            if (!res) {
                return new Response<>("", "");
            }
        }

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

        String templateFile = getRootURL() + "/GoodStatusTemplate/BarChartTemplate_6.xls";
        if (templateFile.charAt(1) == ':' && templateFile.charAt(2) == '/')
            templateFile = "file://" + templateFile;

        URL url = new URL(templateFile);
        InputStream inputStream = url.openStream();

        HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
        HSSFSheet sheet = workbook.getSheet("Data");

        for (int i = 1; i <= statistics.size(); i++)
        {
            HSSFRow row = sheet.createRow(i);
            ChargeStatInfo data = statistics.get(i - 1);
            HSSFCell area = row.createCell(0); area.setCellValue(data.getName());
            HSSFCell today = row.createCell(1); today.setCellValue(Double.parseDouble(String.valueOf(data.getToday())));
            HSSFCell lastweek = row.createCell(2); lastweek.setCellValue(Double.parseDouble(String.valueOf(data.getLastweek())));
            HSSFCell lastmonth = row.createCell(3); lastmonth.setCellValue(Double.parseDouble(String.valueOf(data.getLastmonth())));
            HSSFCell average = row.createCell(4); average.setCellValue(data.getAverage());
        }

        inputStream.close();
        FileOutputStream outputStream = new FileOutputStream(chartFile);
        workbook.write(outputStream);
        outputStream.close();

        result.put("downloadUrl", getRootURL() + "/excelFiles/" + fileName);

        return new Response<>(result);
    }
}
