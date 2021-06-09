package com.trustly.challenge.services;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.trustly.challenge.constants.BusinessConstants;
import com.trustly.challenge.dto.FileDTO;
import com.trustly.challenge.exception.BusinessException;
import com.trustly.challenge.extractor.HtmlElementExtractor;
import com.trustly.challenge.util.FileUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FileService {

    private FileService(){}

    private static final Map<String, Map<String, List<FileDTO>>> mapSitesCache = new HashMap<>();

    /**
     * Identify and retrieve files within a github repository.
     * @param url
     * @param isForceUpdate
     * @return
     * @throws BusinessException
     */
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

    /**
     * Fills the file list with everything found on the HtmlPage
     * @param filesDTO
     * @param mapFilesHtmlElements
     */
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

       /**
     * Browse folders and subfolders identifying files
     * @param htmlElement
     * @param mapFile
     * @param isForceUpdate
     * @throws BusinessException
     */
    private static void identifyFiles(HtmlElement htmlElement, Map<String, List<FileDTO>> mapFile, boolean isForceUpdate) throws BusinessException {
        try {
            String[] pathSplit = htmlElement.getTextContent().split("/");
            String extension = FileExtensionService.getFileExtension(pathSplit);
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



    /**
     * Recovers the file that are inside subfolders
     * @param urlFolder
     * @param isForceUpdate
     * @return
     * @throws BusinessException
     */
    private static List<HtmlElement> extractFilesInSubFolder(URL urlFolder, boolean isForceUpdate) throws BusinessException {
            return HtmlElementExtractor
                    .getHtmlElements(BusinessConstants.CSS.CSS_FILES_DIV_GIT_HUB.getText(),
                            FileUtil.extractURI(urlFolder),
                            isForceUpdate);
        
    }

    /**
     * Add Site URL to cache map
     * @param url
     * @param mapFilesHtmlElements
     */
    private static void saveSiteWithFilesInCache(String url, Map<String, List<FileDTO>> mapFilesHtmlElements) {
        mapSitesCache.put(url, mapFilesHtmlElements);
    }


    /**
     * Add the file to the file map.
     * @param mapFile
     * @param extension
     * @param urlFolder
     * @param isForceUpdate
     * @throws BusinessException
     */
    private static void addFileInMap(Map<String, List<FileDTO>> mapFile, String extension, URL urlFolder, boolean isForceUpdate) throws BusinessException {
        
            FileDTO file = new FileDTO();
            file.setExtension(extension);
            FilesInfoService.setBytesAndLines(urlFolder, isForceUpdate, file);
            if (isAFileExtensionNotMapped(mapFile, extension)) {
                mapFile.put(extension, new ArrayList<>());
            }
            addFileInAlreadyMappedExtension(mapFile, extension, file);
    }

    /**
     * Checks if the file extension has already been mapped
     * @param mapFile
     * @param extension
     * @param file
     */
    private static void addFileInAlreadyMappedExtension(Map<String, List<FileDTO>> mapFile, String extension, FileDTO file) {
        mapFile.get(extension).add(file);
    }

    /**
     *
     * @param mapFile
     * @param extension
     * @return
     */
    private static boolean isAFileExtensionNotMapped(Map<String, List<FileDTO>> mapFile, String extension) {
        return !mapFile.containsKey(extension);
    }

    /**
     * Check if Site URL is already mapped in cache
     * @param url
     * @return
     */
    private static boolean isSiteInCache(String url) {
        return mapSitesCache.containsKey(url);
    }
}
