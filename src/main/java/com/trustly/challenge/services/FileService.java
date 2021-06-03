package com.trustly.challenge.services;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.trustly.challenge.dto.FileDTO;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.trustly.challenge.extractor.HtmlExtractor.*;

public class FileService {
    private static Map<String, Map<String, List<FileDTO>>> mapSitesCache = new HashMap();

    public static List<FileDTO> getFilesInUrl(String url, boolean isForceUpdate) throws Exception {
        List<FileDTO> retorno = new ArrayList<>();
        List<HtmlElement> htmlElements = getHtmlElements("//span/a[@class='js-navigation-open Link--primary']", url,isForceUpdate);
        Map<String, List<FileDTO>> mapFile = new HashMap<>();
        Map<String, List<FileDTO>> mapResponse = new HashMap<>();

        if(!isForceUpdate && mapSitesCache.containsKey(url)){
            mapResponse = mapSitesCache.get(url);
        }else{
            htmlElements.forEach(htmlElement -> {
                identifyFiles(htmlElement,mapFile,isForceUpdate);
            });
            mapSitesCache.put(url,mapFile);
            mapResponse = mapFile;
        }

        mapResponse.forEach((extension, files) -> {
            float bytesTotal = 0l;
            Integer rows = 0;
            for (FileDTO file : files) {
                bytesTotal += file.getBytes();
                rows += file.getLines();
            }

            FileDTO file = new FileDTO();
            file.setExtension(extension);
            file.setCount(files.size());
            file.setBytes(bytesTotal);
            file.setLines(rows);
            retorno.add(file);
        });

        return retorno;
    }

    public static void identifyFiles(HtmlElement htmlElement, Map<String, List<FileDTO>> mapFile, boolean isForceUpdate) {
        try {
            String[] pathSplit = htmlElement.getTextContent().split("/");
            String extension = getFileExtension(pathSplit);
            URL urlFolder = htmlElement.click().getWebResponse().getWebRequest().getUrl();
            List<HtmlElement> filesInSubFolder = extractFilesInSubFolder(htmlElement,urlFolder,isForceUpdate);
            boolean isFolder = !filesInSubFolder.isEmpty();
            if (!isFolder) {
                addFileInMap(mapFile, extension,urlFolder,isForceUpdate);
            }else{
                for (HtmlElement element : filesInSubFolder) {
                    identifyFiles(element, mapFile,isForceUpdate);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }




    }

    private static String getFileExtension(String[] pathSplit) {
        int lastPosition = pathSplit.length - 1;
        String fileName = pathSplit[lastPosition];
        Pattern patter = Pattern.compile("\\.[^.\\\\/:*?\"<>|\\r\\n]+$");
        Matcher matcher = patter.matcher(fileName);
        if(matcher.find()){
            return matcher.group();
        }else{
            return fileName;
        }

    }


    private static List<HtmlElement> extractFilesInSubFolder(HtmlElement htmlElement,URL urlFolder,boolean isForceUpdate) {
        try {
            return getHtmlElements("//span/a[@class='js-navigation-open Link--primary']", urlFolder.toURI().toString(),isForceUpdate);


        } catch (Exception e) {
            System.out.println("Erro + " + e);
            return null;
        }

    }


    private static void addFileInMap(Map<String, List<FileDTO>> mapFile, String extension, URL urlFolder, boolean isForceUpdate) {

        try {
            FileDTO file = new FileDTO();
            file.setExtension(extension);
            List<HtmlElement> linesAndBytesElements =
                    getHtmlElements("//div[@class='text-mono f6 flex-auto pr-3 flex-order-2 flex-md-order-1']", urlFolder.toURI().toString(), isForceUpdate);
            String textWithElements = linesAndBytesElements.get(0).getTextContent().replace("\n","").trim();
            String[] split = textWithElements.split(" ");
            float bytes = Float.parseFloat(split[13]);
            Integer lines = Integer.valueOf(split[0]);
            file.setLines(lines);
            file.setBytes(bytes);

            if (!mapFile.containsKey(extension)) {
                mapFile.put(extension, new ArrayList<>());
            }
            mapFile.get(extension).add(file);
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
