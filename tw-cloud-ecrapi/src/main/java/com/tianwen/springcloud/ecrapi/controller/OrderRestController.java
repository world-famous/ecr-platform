package com.tianwen.springcloud.ecrapi.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.log.SystemControllerLog;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.OrderRestApi;
import com.tianwen.springcloud.ecrapi.constant.ICommonConstants;
import com.tianwen.springcloud.ecrapi.constant.IErrorMessageConstants;
import com.tianwen.springcloud.ecrapi.util.CommonUtil;
import com.tianwen.springcloud.microservice.base.entity.Area;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
import com.tianwen.springcloud.microservice.bussinessassist.entity.ResourceBasket;
import com.tianwen.springcloud.microservice.bussinessassist.entity.ResourceDownload;
import com.tianwen.springcloud.microservice.operation.constant.IOperationConstants;
import com.tianwen.springcloud.microservice.operation.entity.Integral;
import com.tianwen.springcloud.microservice.operation.entity.Member;
import com.tianwen.springcloud.microservice.operation.entity.Order;
import com.tianwen.springcloud.microservice.operation.entity.OrderStatInfo;
import com.tianwen.springcloud.microservice.bussinessassist.entity.Good;
import com.tianwen.springcloud.microservice.resource.entity.Resource;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/order")
public class OrderRestController extends BaseRestController implements OrderRestApi {

    @Override
    @ApiOperation(value = "根据产品id列表上传订单", notes = "根据产品id列表上传订单")
    @ApiImplicitParam(name = "id列表", value = "产品id列表", required = true, dataType = "List<String>", paramType = "body")
    @SystemControllerLog(description = "根据产品id列表上传订单")
    public Response addToOrder(@RequestBody List<String> contentids, @RequestHeader(value = "token") String token) {
        Order order = new Order();
        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();
        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS)) return loginInfoResponse;

        UserLoginInfo userLoginInfo = loginInfoResponse.getResponseEntity();

        Map<String, Object>map = new HashMap<>();
        map.put("userid", userLoginInfo.getUserId());
        map.put("ispaid", "1");
        List<String> contentids4User = orderMicroApi.getContentids4User(map).getPageInfo().getList();

        int price = 0, orderamount = contentids.size();
        for (String contentid : contentids){
            ResourceDownload resourceDownload = new ResourceDownload();
            resourceDownload.setObjectid(contentid);
            resourceDownload.setUserid(userLoginInfo.getUserId());
            Response responseDownload = resourceDownloadMicroApi.searchByEntity(resourceDownload);

            boolean ispaid = false;
            for (String contentid4User : contentids4User){
                if (StringUtils.equals(contentid, contentid4User)){
                    ispaid = true;
                    break;
                }
            }

            Good good = goodMicroApi.getByContentid(contentid).getResponseEntity();
            if (good != null && CollectionUtils.isEmpty(responseDownload.getPageInfo().getList()) && ispaid == false)
                price += good.getGoodprice();

            Map<String, Object> param = new HashMap<>();
            param.put("contentid", contentid);
            param.put("userid", userLoginInfo.getUserId());

            Response<ResourceBasket> resp = resourceBasketMicroApi.remove(param);
            if (resp.getServerResult().getResultCode().equals("1")) {
                resp.getServerResult().setResultCode(IErrorMessageConstants.OPERATION_SUCCESS);
                resp.getServerResult().setResultMessage(IErrorMessageConstants.OPERATION_SUCCESS_MESSAGE);
            }
        }

        order.setUserid(userLoginInfo.getUserId());
        order.setCreatetime(new Timestamp(System.currentTimeMillis()));
        order.setLastmodifytime(new Timestamp(System.currentTimeMillis()));
        order.setIspaid("0");
        order.setContentids(contentids);
        order.setOrderamount(orderamount);
        order.setIntegralprice(price);

        return orderMicroApi.insert(order);
    }

    @Override
    @ApiOperation(value = "获取我的订单列表", notes = "获取我的订单列表")
    @ApiImplicitParam(name = "querytree", value = "搜索条件树", required = true, dataType = "Querytree", paramType = "body")
    @SystemControllerLog(description = "获取我的订单列表")
    public Response getMyOrderList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();
        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS)) return loginInfoResponse;

        UserLoginInfo userLoginInfo = loginInfoResponse.getResponseEntity();

        Object[] object = queryTree.getConditions().get(0).getFieldValues();
        String searchkey = object[0].toString();
        if(!StringUtils.isEmpty(searchkey)) {
            Response response = resourceMicroApi.getIdsByQueryTree(queryTree);
            if (response.getPageInfo() != null) {
                queryTree.addCondition(new QueryCondition("contentids", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, response.getPageInfo().getList()));
            }
        }
        else{
            queryTree.getConditions().remove(0);
        }
        queryTree.addCondition(new QueryCondition("userid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, userLoginInfo.getUserId()));

        return orderMicroApi.getList(queryTree);
    }

    @Override
    @ApiOperation(value = "根据订单id支付", notes = "根据订单id支付")
    @ApiImplicitParam(name = "id", value = "", required = true, dataType = "String", paramType = "path")
    @SystemControllerLog(description = "根据订单id支付")
    public Response payOrder(@PathVariable(value = "orderid") String orderid, @RequestHeader(value = "token") String token) {
        Order order = orderMicroApi.get(orderid).getResponseEntity();
        if (order != null) {
            if (StringUtils.equals(order.getIspaid(), "1")) {
                return new Response(IErrorMessageConstants.ERR_CODE_ORDER_ISPAID_TRUE, IErrorMessageConstants.ERR_MSG_ORDER_ISPAID_TRUE);
            }
            order.setIspaid("1");
            Timestamp currenttime = new Timestamp(System.currentTimeMillis());
            order.setPaidtime(currenttime);
            Member member = memberMicroApi.getByUserId(order.getUserid()).getResponseEntity();
            if (member != null) {
                int myResPrice = 0, orderPrice;

                UserLoginInfo userInfo = getUserByToken(token).getResponseEntity();
                String userid = null;
                if (userInfo != null)
                    userid = userInfo.getUserId();

                List<String> contentids = orderMicroApi.getContentidsByOrder(orderid).getPageInfo().getList();
                if (!contentids.isEmpty()) {
                    for (String contentid : contentids) {
                        Resource resource = resourceMicroApi.get(contentid).getResponseEntity();
                        if (resource.getCreator() != null && resource.getCreator().equalsIgnoreCase(userid)) {
                            Good good = goodMicroApi.getByContentid(contentid).getResponseEntity();
                            if (good != null)
                                myResPrice += good.getGoodprice();
                        }
                        else{
                            ResourceDownload resourceDownload = new ResourceDownload();
                            resourceDownload.setUserid(order.getUserid());
                            resourceDownload.setObjectid(contentid);
                            resourceDownload.setContenttype(resource.getContenttype());
                            resourceDownload.setDowntime(new Timestamp(System.currentTimeMillis()));
                            resourceDownload.setObjectcode(resource.getContentno() == null ? "" : resource.getContentno());

                            Good goodInfo = getSimpleGoodByContentId(resource);
                            resourceDownload.setStatus(goodInfo.getStatus());
                            resourceDownload.setObjecttype("1");
                            resourceDownloadMicroApi.insert(resourceDownload).getResponseEntity();
                            Integer downTimes = resource.getDowntimes();
                            resource.setDowntimes(downTimes + 1);
                            //Author : GOD 2019-2-14 Bug ID:#647
                            resource.setGoodinfo(null);
                            //Author : GOD 2019-2-14 Bug ID:#647
                            resourceMicroApi.download(resource);
                            logOptionEntity(contentid, orderid, ICommonConstants.OPTION_OPTIONTYPE_PAY, token);
                        }
                    }
                }

                orderPrice = order.getIntegralprice() - myResPrice;

                int useintegral = member.getUseintegral() - orderPrice;
                if (useintegral < 0 )
                    return new Response(IErrorMessageConstants.ERR_CODE_USER_INTEGRALPRICE_NOT_ENOUGH_TO_PAY, IErrorMessageConstants.ERR_MSG_USER_INTEGRALPRICE_NOT_ENOUGH_TO_PAY);
                member.setUseintegral(useintegral);
                memberMicroApi.update(member);

                if (order.getIntegralprice()>0) {
                    Integral integral = new Integral();
                    integral.setObjectid(order.getOrderid());
                    integral.setOperationtype(IOperationConstants.OPERATION_BUY_GOOD);
                    integral.setScoretype(IOperationConstants.SCORE_TYPE_RESOURCE);
                    integral.setIncometype(IOperationConstants.INCOME_TYPE_USE);
                    integral.setUserid(order.getUserid());
                    integral.setUserintegralvalue(useintegral);
                    integral.setIntegralvalue(orderPrice);
                    integralMicroApi.add(integral);
                }
            }
        }

        assert order != null;
        String commnet = "支付订单扣除"+order.getIntegralprice()+"积分" ;
        List<String> receivers = new ArrayList<>();
        receivers.add(order.getUserid());
        saveMessage("1","系统",commnet,receivers,ecoPlatformClientID,token);

        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();
        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS))
            return loginInfoResponse;

        return orderMicroApi.update(order);
    }

    @Override
    @ApiOperation(value = "根据订单id获取产品列表", notes = "根据订单id获取产品列表")
    @ApiImplicitParam(name = "id", value = "订单id", required = true, dataType = "String", paramType = "path")
    @SystemControllerLog(description = "根据订单id获取产品列表")
    public Response<Good> getGoodsListByOrder(@RequestBody QueryTree queryTree) {
        QueryCondition orderidCondition = queryTree.getQueryCondition("orderid");

        List<Good> goodList = new ArrayList<>();
        if (orderidCondition != null) {
            String orderid = orderidCondition.getFieldValues()[0].toString();
            List<String> contentids = orderMicroApi.getContentidsByOrder(orderid).getPageInfo().getList();
            if (!CollectionUtils.isEmpty(contentids)) {
                for (String contentid : contentids) {
                    Good good = goodMicroApi.getByContentid(contentid).getResponseEntity();

                    if (good != null) {
                        Resource resource = getResourceByProductId(good);
                        if (resource != null) {
                            validateResource(resource);
                        }
                        goodList.add(good);
                    }
                }
            }
        }

        return new Response<Good>(goodList);
    }

    @Override
    @ApiOperation(value = "获取订单列表", notes = "获取订单列表")
    @ApiImplicitParam(name = "querytree", value = "搜索条件树", required = true, dataType = "Querytree", paramType = "body")
    @SystemControllerLog(description = "获取订单列表")
    public Response<Order> getOrderList(@RequestBody QueryTree queryTree) {
        if (!CollectionUtils.isEmpty(queryTree.getConditions())) {
            List<String> userids = userMicroApi.getUserIdsByQueryTree(queryTree).getPageInfo().getList();
            queryTree.getConditions().add(new QueryCondition("userids", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, userids));
        }
        Response resp = orderMicroApi.getList(queryTree);
        List<Order>orders = resp.getPageInfo().getList();
        if (!CollectionUtils.isEmpty(orders)) {
            for (Order order : orders) {
                String userid=order.getUserid();
                if (!StringUtils.isEmpty(userid)) {
                    UserLoginInfo loginInfo = userMicroApi.get(userid).getResponseEntity();
                    if (loginInfo != null) {
                        order.setRealname(loginInfo.getRealName());
                    }
                }
            }
        }
        return resp;
    }

    @Override
    @ApiOperation(value = "", notes = "")
    @ApiImplicitParam(name = "", value = "", required = true, dataType = "", paramType = "")
    @SystemControllerLog(description = "")
    public Response<Map<String, Object>> getOrderStatistics() {
        return orderMicroApi.getOrderStatics();
    }

    @Override
    @ApiOperation(value = "", notes = "")
    @ApiImplicitParam(name = "", value = "", required = true, dataType = "", paramType = "")
    @SystemControllerLog(description = "")
    public Response<OrderStatInfo> getOrderByArea() {
        return getOrderStatisticsByArea();
    }

    private Response<OrderStatInfo> getOrderStatisticsByArea(){
        List<Area> areas = areaMicroApi.getAreaInfoList().getPageInfo().getList();
        return orderMicroApi.getOrderStatistics(parseToAreaInfoList(areas));
    }

    @Override
    @ApiOperation(value = "", notes = "")
    @ApiImplicitParam(name = "", value = "", required = true, dataType = "", paramType = "")
    @SystemControllerLog(description = "")
    public Response<UserLoginInfo> getTopTenUsers() {
        List <UserLoginInfo> resultData = new ArrayList<>();
        List<String> userids = orderMicroApi.getAllCreators().getPageInfo().getList();

        if (!userids.isEmpty()) {
            for (String userid : userids) {
                UserLoginInfo user = userMicroApi.get(userid).getResponseEntity();
                if (user != null) {
                    Integer orderCount = orderMicroApi.countOrderForUser(userid).getResponseEntity();

                    if (orderCount != null) user.setOrdercount(orderCount);
                    else continue;

                    if (resultData.isEmpty()) {
                        resultData.add(user);
                        continue;
                    }
                    int size = resultData.size()  < 10 ?  resultData.size() : 10;
                    boolean compare = false;
                    for (int i = 0; i < size ; i++) {
                        UserLoginInfo compareuser = resultData.get(i);
                        if (compareuser != null && compareuser.getOrdercount() <= user.getOrdercount()) {
                            resultData.add(i,user);
                            compare = true;
                            break;
                        }
                    }
                    if (compare == false && size < 10)
                        resultData.add(size,user);
                }
            }
        }

        return new Response<UserLoginInfo>(resultData);
    }

    @Override
    @ApiOperation(value = "", notes = "")
    @ApiImplicitParam(name = "", value = "", required = true, dataType = "", paramType = "")
    @SystemControllerLog(description = "")
    public Response<Map<String, Object>>getTopThreeAreaByDate(){
        List<Map<String, Object>> resultMapList = new ArrayList<>();
        List<OrderStatInfo> mapList = getOrderStatisticsByArea().getPageInfo().getList();
        if (!mapList.isEmpty()){
            List<Map<String, Object>> todayList = sortData(mapList,"today");
            List<Map<String, Object>> weekList = sortData(mapList,"lastweek");
            List<Map<String, Object>> monthList = sortData(mapList,"lastmonth");
            List<Map<String, Object>> totalList = sortData(mapList,"total");
            if (!todayList.isEmpty() && !weekList.isEmpty() && !monthList.isEmpty() && !todayList.isEmpty()){
                for (Map<String, Object> map : totalList)
                    resultMapList.add(map);
                for (Map<String, Object> map : todayList)
                    resultMapList.add(map);
                for (Map<String, Object> map : weekList)
                    resultMapList.add(map);
                for (Map<String, Object> map : monthList)
                    resultMapList.add(map);
            }
        }
        return new Response<>(resultMapList);
    }
    public List<Map<String, Object>> sortData(List<OrderStatInfo>maplist, String date){
        List<Map<String, Object>>resultList = new ArrayList<>();
        for (OrderStatInfo map : maplist){
            Map<String, Object> datamap = new HashMap<>();
            if (resultList.isEmpty()){
                Map<String, Object> temp = new HashMap<>();
                temp.put("name", map.getName());
                temp.put("today", map.getToday());
                temp.put("lastweek", map.getLastweek());
                temp.put("lastmonth", map.getLastmonth());
                temp.put("total", map.getTotal());
                resultList.add(temp);
                continue;
            }

            int size = resultList.size()  < 3 ?  resultList.size() : 3;
            boolean compare = false;
            int dateCount = 0;
            if (StringUtils.equals("today", date)) dateCount = map.getToday();
            if (StringUtils.equals("total", date)) dateCount = map.getTotal();
            if (StringUtils.equals("lastweek", date)) dateCount = map.getLastweek();
            if (StringUtils.equals("lastmonth", date)) dateCount = map.getLastmonth();
            for (int i=0; i<size; i++){
                Map<String, Object> compareMap = resultList.get(i);
                if (compareMap != null && (int)compareMap.get(date) < dateCount){
                    datamap.put("name",map.getName());
                    datamap.put(date, dateCount);
                    resultList.add(i,datamap);
                    compare = true;
                    break;
                }
            }
            if (compare == false && size < 3) {
                datamap.put("name", map.getName());
                datamap.put(date, dateCount);
                resultList.add(size, datamap);
            }
        }
        return resultList;
    }

    @Override
    @ApiOperation(value = "", notes = "")
    @ApiImplicitParam(name = "", value = "", required = true, dataType = "", paramType = "")
    @SystemControllerLog(description = "")
    public Response<Map<String, Object>> export2Excel()throws Exception {
        Map<String, Object> result = new HashMap<>();

        String fileName = CommonUtil.convertToMd5(new Timestamp(System.currentTimeMillis()).toString(), true) + ".xls";

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

        File chartFile = new File(homePath + fileName);
        boolean createResult = chartFile.createNewFile();

        if (createResult == false)
            return new Response(IErrorMessageConstants.ERR_CODE_FILE_NOT_EXIST, IErrorMessageConstants.ERR_MSG_FILE_NOT_EXIST);

        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("static/GoodStatusTemplate/BarChartTemplate_6.xls");
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet hssfSheet = workbook.createSheet();

        HSSFRow topRow = hssfSheet.createRow(0);
        topRow.createCell(0);
        topRow.createCell(1);
        topRow.createCell(2);
        hssfSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
        topRow.getCell(0).setCellValue("订单统计");

        HSSFRow titleRow = hssfSheet.createRow(1);
        titleRow.createCell(0);titleRow.getCell(0).setCellValue("地域名");
        titleRow.createCell(1);titleRow.getCell(1).setCellValue("总订单");
        titleRow.createCell(2);titleRow.getCell(2).setCellValue("今天订单");
        titleRow.createCell(3);titleRow.getCell(3).setCellValue("上周订单");
        titleRow.createCell(4);titleRow.getCell(4).setCellValue("上月订单");

        List<OrderStatInfo> orderStatistics = getOrderStatisticsByArea().getPageInfo().getList();

        for (int i =0; i<orderStatistics.size(); i++){
            OrderStatInfo map = orderStatistics.get(i);
            HSSFRow row = hssfSheet.createRow(i + 2);
            row.createCell(0);row.getCell(0).setCellValue(map.getName().toString());
            row.createCell(1);row.getCell(1).setCellValue(String.valueOf(map.getTotal()));
            row.createCell(2);row.getCell(2).setCellValue(String.valueOf(map.getToday()));
            row.createCell(3);row.getCell(3).setCellValue(String.valueOf(map.getLastweek()));
            row.createCell(4);row.getCell(4).setCellValue(String.valueOf(map.getLastmonth()));
        }
        inputStream.close();
        FileOutputStream outputStream = new FileOutputStream(chartFile);
        workbook.write(outputStream);
        outputStream.close();

        result.put("downloadUrl", getRootURL() + "/excelFiles/" + fileName);

        return new Response<>(result);
    }

}
