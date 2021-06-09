package com.trustly.challenge.extractor;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.trustly.challenge.exception.BusinessException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HtmlElementExtractor {

    private HtmlElementExtractor() {}
    private static final Map<String, List<HtmlElement>> htmlElementsByPage = new HashMap<>();


    public static List<HtmlElement> getHtmlElements(String cssClass, String url, boolean isForceUpdate) throws BusinessException {
        HtmlPage htmlPage = recoverHtmlPage(url, isForceUpdate);
        return HtmlElementExtractor.getHtmlElements(cssClass,htmlPage,isForceUpdate);
    }

    public static List<HtmlElement> getHtmlElements(String cssClass,HtmlPage htmlPage, boolean isForceUpdate){
        String key = createHtmlElementKey(cssClass, htmlPage);
        if(!isForceUpdate && isHtmlElementInCache(key)){
            return htmlElementsByPage.get(key);
        }

        List<HtmlElement> elements = (List<HtmlElement>) htmlPage.getByXPath(cssClass);
        htmlElementsByPage.put(key,elements);
        return elements;
    }

    private static HtmlPage recoverHtmlPage(String url, boolean isForceUpdate) throws BusinessException {
        return HtmlPageExtractor.getHtmlForPage(url, isForceUpdate);
    }

    private static boolean isHtmlElementInCache(String key) {
        return htmlElementsByPage.containsKey(key);
    }

    private static String createHtmlElementKey(String cssClass, HtmlPage htmlPage) {
        return htmlPage + cssClass;
    }
}
