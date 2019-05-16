package com.tianwen.springcloud.ecrapi.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.ThemeResRestApi;
import com.tianwen.springcloud.ecrapi.constant.ICommonConstants;
import com.tianwen.springcloud.ecrapi.util.CommonUtil;
import com.tianwen.springcloud.ecrapi.util.FileUtil;
import com.tianwen.springcloud.ecrapi.util.ReadExcel;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
import com.tianwen.springcloud.microservice.resource.api.ThemeResMicroApi;
import com.tianwen.springcloud.microservice.resource.entity.ThemeRes;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gems on 2018.12.21.
 */
@RestController
@RequestMapping(value = "/themeres")
public class ThemeResRestController extends BaseRestController implements ThemeResRestApi{
    @Autowired
    private ThemeResMicroApi themeResMicroApi;
    @Override
    public Response insertThemeRes(@RequestBody List<ThemeRes> list, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();

        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS))
            return loginInfoResponse;

        return themeResMicroApi.insert(list);
    }

    @Override
    public Response removeThemeRes(@RequestBody List<ThemeRes> list, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();

        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS))
            return loginInfoResponse;

        return themeResMicroApi.remove(list);
    }

    @Override
    public Response exportToExcel(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token) throws IOException {
        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();

        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS))
            return loginInfoResponse;

        List<ThemeRes> themeResList = themeResMicroApi.getThemeResListById(queryTree);

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
        titleRow.createCell(0);titleRow.getCell(0).setCellValue("专题ID");
        titleRow.createCell(1);titleRow.getCell(1).setCellValue("资源ID");

        for(int i = 0; i <themeResList.size(); i++)
        {
            ThemeRes data = themeResList.get(i);
            Row row = hssfSheet.createRow(i + 1);
            row.createCell(0);row.getCell(0).setCellValue(data.getThemeid());

            row.createCell(1);row.getCell(1).setCellValue(data.getResourceno());
        }

        hssfWorkbook.write(fileOutputStream);

        Map<String, Object> result = new HashMap<>();
        result.put("downloadUrl", getRootURL() + "/excelFiles/" + fileName);
        return new Response<>(result);
    }

    @Override
    public Response importFromExcel(@RequestPart(value = "themeresfile") MultipartFile file, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();

        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS))
            return loginInfoResponse;

        Integer res = 0;
        if (file != null) {
            String orgFileName = file.getOriginalFilename();
            String fileName = FileUtil.getUniqueFileName(location, FileUtil.getFileExtension(orgFileName));
            String fileFullName = location + "/" + fileName;
            File resourceInfoFile = new File(fileFullName);
            try {
                file.transferTo(resourceInfoFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ReadExcel excel = new ReadExcel();
            List<List<String>> data = excel.read(resourceInfoFile.getPath());
            List<ThemeRes> themeResList = new ArrayList<>();
            for (int i=1; i<data.size(); i++) {
                ThemeRes themeRes = new ThemeRes();
                themeRes.setThemeid(data.get(i).get(0));
                themeRes.setResourceno(data.get(i).get(1));
                themeRes.setResourcetype("");
                themeResList.add(themeRes);
            }

            if (themeResList.size() > 0) {
                return  themeResMicroApi.insert(themeResList);
            }
        }
        return new Response<> (res);
    }
}
