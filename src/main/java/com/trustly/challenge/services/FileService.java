package com.trustly.challenge.services;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.trustly.challenge.constants.BusinessConstants;
import com.trustly.challenge.dto.FileDTO;
import com.trustly.challenge.exception.BusinessException;
import com.trustly.challenge.extractor.HtmlElementExtractor;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;



public class FileService {

    private FileService(){}

    private static final Map<String, Map<String, List<FileDTO>>> mapSitesCache = new HashMap<>();

    public static List<FileDTO> getFilesInUrl(String url, boolean isForceUpdate) throws BusinessException {
        List<FileDTO> filesDTO = new ArrayList<>();
        Map<String, List<FileDTO>> mapFilesHtmlElements = new HashMap<>();

        List<HtmlElement> filesInGitHubHtmlElements =
                HtmlElementExtractor.getHtmlElements(BusinessConstants.CSS.CSS_FILES_DIV_GIT_HUB.getText(), url,isForceUpdate);

        if(!isForceUpdate && isSiteInCache(url)){
            mapFilesHtmlElements = mapSitesCache.get(url);
        }else{
            for (HtmlElement htmlElement : filesInGitHubHtmlElements) {
                identifyFiles(htmlElement, mapFilesHtmlElements, isForceUpdate);
            }
            saveSiteWithFilesInCache(url, mapFilesHtmlElements);
        }

        fillListWithFiles(filesDTO, mapFilesHtmlElements);
        return filesDTO;
    }

    private static void fillListWithFiles(List<FileDTO> filesDTO, Map<String, List<FileDTO>> mapFilesHtmlElements) {
        mapFilesHtmlElements.forEach((extension, files) -> {
            float bytesTotal = 0L;
            Integer rows = 0;

            for (FileDTO file : files) {
                bytesTotal += file.getBytes();
                rows += file.getLines();
            }

            filesDTO.add(new FileDTO(extension,files.size(),rows,bytesTotal));
        });
    }

    private static void saveSiteWithFilesInCache(String url, Map<String, List<FileDTO>> mapFilesHtmlElements) {
        mapSitesCache.put(url, mapFilesHtmlElements);
    }


    public static void identifyFiles(HtmlElement htmlElement, Map<String, List<FileDTO>> mapFile, boolean isForceUpdate) throws BusinessException {
        try {
            String[] pathSplit = htmlElement.getTextContent().split("/");
            String extension = getFileExtension(pathSplit);
            URL urlFolder = htmlElement.click().getWebResponse().getWebRequest().getUrl();
            
            List<HtmlElement> filesInSubFolder = extractFilesInSubFolder(urlFolder,isForceUpdate);
            boolean isFolder = !filesInSubFolder.isEmpty();
            
            if (!isFolder) {
                addFileInMap(mapFile, extension,urlFolder,isForceUpdate);
            }else{
                for (HtmlElement element : filesInSubFolder) {
                    identifyFiles(element, mapFile,isForceUpdate);
                }
            }
        } catch (IOException e) {
            throw new BusinessException("Unable to click on element: " + htmlElement.getTextContent());
        } catch (IndexOutOfBoundsException e){
            throw new BusinessException("To many request to GitHub");
        }




    }

    private static String getFileExtension(String[] pathSplit) {
        int lastPosition = pathSplit.length - 1;
        String fileName = pathSplit[lastPosition];
        Pattern patter = Pattern.compile(BusinessConstants.REGEX.REGEX_FILE_EXTENSION.getText());
        Matcher matcher = patter.matcher(fileName);
        if(matcher.find()){
            return matcher.group();
        }else{
            return fileName;
        }
    }


    private static List<HtmlElement> extractFilesInSubFolder(URL urlFolder, boolean isForceUpdate) throws BusinessException {
            return HtmlElementExtractor
                    .getHtmlElements(BusinessConstants.CSS.CSS_FILES_DIV_GIT_HUB.getText(),
                            extractURI(urlFolder),
                            isForceUpdate);
        
    }

    private static String extractURI(URL urlFolder) throws BusinessException {
        try {
            return urlFolder.toURI().toString();
        } catch (URISyntaxException e) {
            throw new BusinessException("URL File with the wrong format");
        }
    }


    private static void addFileInMap(Map<String, List<FileDTO>> mapFile, String extension, URL urlFolder, boolean isForceUpdate) throws BusinessException {
        
            FileDTO file = new FileDTO();
            file.setExtension(extension);
            setBytesAndLines(urlFolder, isForceUpdate, file);
            if (isAFileExtensionNotMapped(mapFile, extension)) {
                mapFile.put(extension, new ArrayList<>());
            }
            addFileInAlreadyMappedExtension(mapFile, extension, file);

    }

    private static void addFileInAlreadyMappedExtension(Map<String, List<FileDTO>> mapFile, String extension, FileDTO file) {
        mapFile.get(extension).add(file);
    }

    private static boolean isAFileExtensionNotMapped(Map<String, List<FileDTO>> mapFile, String extension) {
        return !mapFile.containsKey(extension);
    }

    private static void setBytesAndLines(URL urlFolder, boolean isForceUpdate, FileDTO file) throws BusinessException {

        List<String> listBytesAndLines = recoverBytesAndLines(urlFolder,isForceUpdate);

        if(fileOnlyHasSizeInformation(listBytesAndLines)){
            setBytes(file, listBytesAndLines.get(0));
            setLines(file, "0");
        }else{
            setLines(file, listBytesAndLines.get(0));
            setBytes(file, listBytesAndLines.get(1));
        }
    }

    private static List<String> recoverBytesAndLines(URL urlFolder, boolean isForceUpdate) throws BusinessException {
        List<HtmlElement> linesAndBytesElements = recoverFilesInfos(urlFolder, isForceUpdate);

        String lineWithBytesAndLinesInfos = removeInvalidCharactesInHtmlElement(linesAndBytesElements);

        String[] linesAndBytesSplit = lineWithBytesAndLinesInfos.split(BusinessConstants.REGEX.BLANK_SPACE.getText());

        return Arrays.stream(linesAndBytesSplit)
        .filter(NumberUtils::isParsable)
        .collect(Collectors.toList());
    }

    private static boolean fileOnlyHasSizeInformation(List<String> listBytesAndLines) {
        return listBytesAndLines.size() == 1;
    }

    private static String removeInvalidCharactesInHtmlElement(List<HtmlElement> linesAndBytesElements) throws BusinessException {
        if(!linesAndBytesElements.isEmpty()){
            try {
                return linesAndBytesElements
                        .get(0)
                        .getTextContent()
                        .replace("\n", "")
                        .replace("\t", "").trim();
            } catch (IndexOutOfBoundsException e) {
                throw new BusinessException("Failed to retrieve file information.");
            }
        }
        return "";

    }

    private static List<HtmlElement> recoverFilesInfos(URL urlFolder, boolean isForceUpdate) throws BusinessException {
        return HtmlElementExtractor.
                getHtmlElements(BusinessConstants.CSS.CSS_INFOS_FILES_GIT_HUB.getText(),
                                extractURI(urlFolder),
                                isForceUpdate);
    }


    private static void setLines(FileDTO file, String s) {
        Integer lines = Integer.valueOf(s);
        file.setLines(lines);
    }

    private static void setBytes(FileDTO file, String s) {
        float bytes = Float.parseFloat(s);
        file.setBytes(bytes);
    }

    private static boolean isSiteInCache(String url) {
        return mapSitesCache.containsKey(url);
    }
}
