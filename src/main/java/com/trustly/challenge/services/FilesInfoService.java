package com.trustly.challenge.services;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.trustly.challenge.constants.BusinessConstants;
import com.trustly.challenge.dto.FileDTO;
import com.trustly.challenge.exception.BusinessException;
import com.trustly.challenge.extractor.HtmlElementExtractor;
import com.trustly.challenge.util.FileUtil;
import org.apache.commons.lang3.math.NumberUtils;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FilesInfoService {

    /**
     * Defines the number of lines and bytes in the file
     * @param urlFolder
     * @param isForceUpdate
     * @param file
     * @throws BusinessException
     */
    public static void setBytesAndLines(URL urlFolder, boolean isForceUpdate, FileDTO file) throws BusinessException {

        List<String> listBytesAndLines = recoverBytesAndLines(urlFolder,isForceUpdate);

        if(fileOnlyHasSizeInformation(listBytesAndLines)){
            setBytes(file, listBytesAndLines.get(0));
            setLines(file, "0");
        }else{
            setLines(file, listBytesAndLines.get(0));
            setBytes(file, listBytesAndLines.get(1));
        }
    }

    /**
     * Extracts the bytes and lines of the file from the HTML Element.
     * @param urlFolder
     * @param isForceUpdate
     * @return
     * @throws BusinessException
     */
    private static List<String> recoverBytesAndLines(URL urlFolder, boolean isForceUpdate) throws BusinessException {
        List<HtmlElement> linesAndBytesElements = recoverFilesInfos(urlFolder, isForceUpdate);

        String lineWithBytesAndLinesInfos = removeInvalidCharactesInHtmlElement(linesAndBytesElements);

        String[] linesAndBytesSplit = lineWithBytesAndLinesInfos.split(BusinessConstants.REGEX.BLANK_SPACE.getText());

        return Arrays.stream(linesAndBytesSplit)
                .filter(NumberUtils::isParsable)
                .collect(Collectors.toList());
    }

    /**
     * Checks if the file has only the number of bytes.
     * @param listBytesAndLines
     * @return
     */
    private static boolean fileOnlyHasSizeInformation(List<String> listBytesAndLines) {
        return listBytesAndLines.size() == 1;
    }

    /**
     * Removes invalid characters from the line that has the byte and line information.
     * @param linesAndBytesElements
     * @return
     * @throws BusinessException
     */
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

    /**
     * Retrieve byte and line information from a file.
     * @param urlFolder
     * @param isForceUpdate
     * @return
     * @throws BusinessException
     */
    private static List<HtmlElement> recoverFilesInfos(URL urlFolder, boolean isForceUpdate) throws BusinessException {
        return HtmlElementExtractor.
                getHtmlElements(BusinessConstants.CSS.CSS_INFOS_FILES_GIT_HUB.getText(),
                        FileUtil.extractURI(urlFolder),
                        isForceUpdate);
    }

    /**
     * Set Lines of file
     * @param file
     * @param s
     */
    private static void setLines(FileDTO file, String s) {
        Integer lines = Integer.valueOf(s);
        file.setLines(lines);
    }

    /**
     * Set Bytes of file
     * @param file
     * @param s
     */
    private static void setBytes(FileDTO file, String s) {
        float bytes = Float.parseFloat(s);
        file.setBytes(bytes);
    }
}
