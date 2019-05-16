package com.tianwen.springcloud.ecrapi.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.log.SystemControllerLog;
import com.tianwen.springcloud.ecrapi.api.FileServiceRestApi;
import com.tianwen.springcloud.ecrapi.constant.ICommonConstants;
import com.tianwen.springcloud.ecrapi.util.FileUtil;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
import com.tianwen.springcloud.microservice.resource.entity.FileInfo;
import com.tianwen.springcloud.microservice.resource.entity.Resource;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

@RestController
@RequestMapping(value = "/file")
public class FileServiceController extends BaseRestController implements FileServiceRestApi{
    private static final int SORT_RESOURCE_BATCH_ADD = 1;

    @Autowired
    private ResourceRestController resourceRestController;

    @Override
    @ApiOperation(value = "文件上传", notes = "文件上传")
    @ApiImplicitParam(name = "file", value = "文件", required = true, dataType = "MultipartFile", paramType = "body")
    @SystemControllerLog(description = "文件上传")
    public Response uploadFile(@RequestParam("type") int sort, @RequestPart("file") MultipartFile file, @RequestHeader(value = "token") String token) throws IOException {
        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();
        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS)) return loginInfoResponse;

        boolean isEcrDirExist = new File(location).isDirectory();
        if (isEcrDirExist == false) {
            boolean res = new File(location).mkdir();
            if (!res) {
                return new Response<>("", "");
            }
        }

        Response<Resource> resp = null;
        String orgFileName = file.getOriginalFilename();
        if (sort == SORT_RESOURCE_BATCH_ADD) {
            String fileName = FileUtil.getUniqueFileName(location, FileUtil.getFileExtension(orgFileName));
            String fileFullName = location + "/" + fileName;
            File resourceInfoFile = new File(fileFullName);
            file.transferTo(resourceInfoFile);
            resp = resourceRestController.batchUploadByFile(resourceInfoFile, loginInfoResponse.getResponseEntity());
        }

        return resp;
    }

    @Override
    @ApiOperation(value = "文件上传", notes = "文件上传")
    @ApiImplicitParam(name = "contentid", value = "资源Id", required = true, dataType = "String", paramType = "path")
    @SystemControllerLog(description = "文件上传")
    @RequestMapping(value = "/download/{contentid}", method = RequestMethod.GET)
    public void downloadResource(HttpServletResponse httpServletResponse, @PathVariable(value = "contentid") String contentid
            , @RequestParam(value = "token") String token) throws Exception{
        //download file
        FileInfo fileInfo = fileMicroApi.getByContentid(contentid).getResponseEntity();
        Resource resource = resourceMicroApi.get(contentid).getResponseEntity();

        if (fileInfo == null) return;

        httpServletResponse.reset();

        httpServletResponse.setContentType("application/octet-stream");
        httpServletResponse.addHeader("Content-encoding","UTF-8");
        String fileName = URLEncoder.encode(fileInfo.getFilename(), "UTF-8");
        fileName = URLDecoder.decode(fileName, "ISO8859_1");
        httpServletResponse.addHeader("Content-Disposition", "attachment; filename=" + fileName );

        String localpath = fileInfo.getLocalpath();
        URL url = new URL(ecoServerIp+ICommonConstants.ECO_FILE_DOWNLOAD_URL+localpath);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.connect();

        if (HttpURLConnection.HTTP_OK != urlConn.getResponseCode()) {
            return;
        }

        InputStream inputStream = url.openStream();
        httpServletResponse.setContentLength((int) (Double.parseDouble(fileInfo.getSize())));

        OutputStream target = httpServletResponse.getOutputStream();

        int FILE_CHUNK_SIZE = 1024 * 4;
        byte[] chunk = new byte[FILE_CHUNK_SIZE];
        int n = 0;
        while ( (n = inputStream.read(chunk)) != -1 ) {
            target.write(chunk, 0, n);
        }
        target.write(chunk);

        if (n == -1)
            resourceRestController.downloadFinished(resource, token);
    }
}
