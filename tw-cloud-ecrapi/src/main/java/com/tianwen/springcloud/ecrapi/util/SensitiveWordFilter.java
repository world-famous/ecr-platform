package com.tianwen.springcloud.ecrapi.util;

import com.tianwen.springcloud.ecrapi.constant.ICommonConstants;
import com.tianwen.springcloud.microservice.base.entity.Config;

import java.io.*;
import java.net.URL;
import java.util.*;

public class SensitiveWordFilter {
    private static ArrayList<String> wordsList = new ArrayList<>();

    private String encoding = "UTF-8";
    private List<String> sensitiveWordList;

    public SensitiveWordFilter() {
        sensitiveWordList = new ArrayList<>();
    }

    public static void initialize(String fileUrl) {
        wordsList.clear();
        BufferedReader buffer = null;
        try {
            URL url = new URL(fileUrl);

            buffer = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            String line;

            while ((line = buffer.readLine()) != null) {
                String word = line.trim();
                if (!word.isEmpty() && !wordsList.contains(word)) wordsList.add(word);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (null != buffer) {
                try {
                    buffer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void initialize(InputStream stream) {
        wordsList.clear();
        BufferedReader buffer = null;
        try {
            buffer = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String line;

            while ((line = buffer.readLine()) != null) {
                String word = line.trim();
                if (!word.isEmpty() && !wordsList.contains(word)) wordsList.add(word);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (null != buffer) {
                try {
                    buffer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String process(String str) {
        String filterdStr = str;

        sensitiveWordList = new ArrayList<>();
        StringBuilder buffer = new StringBuilder(filterdStr);
        HashMap<Integer, Integer> hash = new HashMap<>(wordsList.size());

        for (int i = 0; i < wordsList.size(); i++) {
            String temp = wordsList.get(i).trim();
            if (temp.isEmpty()) continue;
            int findIndexSize = 0;
            for (int start = -1; (start = buffer.indexOf(temp, findIndexSize)) > -1;) {
                findIndexSize = start + temp.length();
                Integer mapStart = hash.get(start);
                if (mapStart == null || (mapStart != null && findIndexSize > mapStart)) {
                    hash.put(start, findIndexSize);
                }
            }
        }

        Collection<Integer> values = hash.keySet();
        for (Integer startIndex : values) {
            Integer endIndex = hash.get(startIndex);
            String sensitive = buffer.substring(startIndex, endIndex);
            if (!sensitive.contains("*")) {
                sensitiveWordList.add(sensitive);
            }
            for (int i = startIndex; i < endIndex; i ++) {
                buffer.replace(i, i + 1, "*");
            }
        }

        hash.clear();

        return buffer.toString();
    }

    public List<String> getSensitiveWordList() {
        return sensitiveWordList;
    }
}
