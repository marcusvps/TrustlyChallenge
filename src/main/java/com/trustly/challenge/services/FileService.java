package com.trustly.challenge.services;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.trustly.challenge.dto.FileDTO;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.*;

import static com.trustly.challenge.extractor.HtmlExtractor.*;

@Controller
public class FileService {

    public Map<String, List<FileDTO>> getFilesInUrl(String url) throws Exception {
        List<HtmlElement> htmlElements = getHtmlElements("//span/a[@class='js-navigation-open Link--primary']", url);
        Map<String, List<FileDTO>> mapFile = new HashMap<>();

        htmlElements.forEach(htmlElement -> {
            identifyFiles(htmlElement,mapFile);


        });
        return mapFile;
    }

    public void identifyFiles(HtmlElement htmlElement, Map<String, List<FileDTO>> mapFile) {
        String[] pathSplit = htmlElement.getTextContent().split("/");
        int lastPosition = pathSplit.length - 1;
        String extension = pathSplit[lastPosition];
        List<HtmlElement> filesInSubFolder = extractFilesInSubFolder(htmlElement);
        boolean isFolder = !filesInSubFolder.isEmpty();
        if (!isFolder) {
            addFileInMap(mapFile, extension);
        }else{
            for (HtmlElement element : filesInSubFolder) {
                identifyFiles(element, mapFile);
            }
        }



    }


    private List<HtmlElement> extractFilesInSubFolder(HtmlElement htmlElement) {
        try {
            URL urlFolder = htmlElement.click().getWebResponse().getWebRequest().getUrl();
            return getHtmlElements("//span/a[@class='js-navigation-open Link--primary']", urlFolder.toURI().toString());

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
