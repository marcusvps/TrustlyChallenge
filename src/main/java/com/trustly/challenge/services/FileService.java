package com.trustly.challenge.services;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.trustly.challenge.dto.FileDTO;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.*;

import static com.trustly.challenge.extractor.HtmlExtractor.*;

@Controller
public class FileService {

    public Map<String, List<FileDTO>> getFilesInUrl(String url, boolean isForceUpdate) throws Exception {
        List<HtmlElement> htmlElements = getHtmlElements("//span/a[@class='js-navigation-open Link--primary']", url,isForceUpdate);
        Map<String, List<FileDTO>> mapFile = new HashMap<>();

        htmlElements.forEach(htmlElement -> {
            identifyFiles(htmlElement,mapFile,isForceUpdate);


        });
        return mapFile;
    }

    public void identifyFiles(HtmlElement htmlElement, Map<String, List<FileDTO>> mapFile, boolean isForceUpdate) {
        String[] pathSplit = htmlElement.getTextContent().split("/");
        int lastPosition = pathSplit.length - 1;
        String extension = pathSplit[lastPosition];
        List<HtmlElement> filesInSubFolder = extractFilesInSubFolder(htmlElement,isForceUpdate);
        boolean isFolder = !filesInSubFolder.isEmpty();
        if (!isFolder) {
            addFileInMap(mapFile, extension);
        }else{
            for (HtmlElement element : filesInSubFolder) {
                identifyFiles(element, mapFile,isForceUpdate);
            }
        }



    }


    private List<HtmlElement> extractFilesInSubFolder(HtmlElement htmlElement,boolean isForceUpdate) {
        try {
            URL urlFolder = htmlElement.click().getWebResponse().getWebRequest().getUrl();
            return getHtmlElements("//span/a[@class='js-navigation-open Link--primary']", urlFolder.toURI().toString(),isForceUpdate);

        } catch (Exception e) {
            System.out.println("erro + " + e);
            return null;
        }

    }


    private void addFileInMap(Map<String, List<FileDTO>> mapFile, String extension) {
        FileDTO file = new FileDTO();
        file.setExtension(extension);
        if (!mapFile.containsKey(extension)) {
            mapFile.put(extension, new ArrayList<>());
        } else {
            mapFile.get(extension).add(file);
        }
    }
}
