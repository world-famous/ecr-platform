package com.tianwen.springcloud.ecrapi.component;

import com.tianwen.springcloud.ecrapi.constant.ICommonConstants;
import com.tianwen.springcloud.ecrapi.util.SensitiveWordFilter;
import com.tianwen.springcloud.microservice.base.api.ConfigMicroApi;
import com.tianwen.springcloud.microservice.base.entity.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    private ConfigMicroApi configMicroApi;

    @Value("classpath:static/badword.txt")
    Resource resourceFile;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // Initialize sensitive word processor
        try {
            //Config badWordConf = configMicroApi.get("BADWORDS_CONFIG").getResponseEntity();
            //String wordListFile = ecoServerIp + ICommonConstants.ECO_FILE_DOWNLOAD_URL + badWordConf.getConfvalue();
            String filePath = "classpath:static/badword.txt";

            if (resourceFile.exists()) {
                SensitiveWordFilter.initialize(resourceFile.getInputStream());
            }
            else {
                System.out.println("Could not read badword file.");
            }
        }
        catch (Exception ex) {
            System.out.print(ex.getLocalizedMessage());
        }
    }
}
