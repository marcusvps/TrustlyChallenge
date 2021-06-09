package com.trustly.challenge.services;

import com.trustly.challenge.constants.BusinessConstants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileExtensionService {

    /**
     * Recovers the file extension.
     * @param pathSplit
     * @return
     */
    public static String getFileExtension(String[] pathSplit) {
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
}
