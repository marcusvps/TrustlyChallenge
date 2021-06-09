package com.trustly.challenge.util;

import com.trustly.challenge.exception.BusinessException;

import java.net.URISyntaxException;
import java.net.URL;

public class FileUtil {

    /**
     * Retrieves a file URL from an HTML page
     * @param urlFolder
     * @return
     * @throws BusinessException
     */
    public static String extractURI(URL urlFolder) throws BusinessException {
        try {
            return urlFolder.toURI().toString();
        } catch (URISyntaxException e) {
            throw new BusinessException("URL File with the wrong format");
        }
    }
}
