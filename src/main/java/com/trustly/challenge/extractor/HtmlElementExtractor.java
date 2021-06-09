package com.trustly.challenge.extractor;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.trustly.challenge.exception.BusinessException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Extractor of HTML elements from an HTML page.
 */
public class HtmlElementExtractor {

    private HtmlElementExtractor() {}
    private static final Map<String, List<HtmlElement>> htmlElementsByPage = new HashMap<>();

    /**
     * Extracts HTML elements, based on CSS class and a valid URL.
     * @param cssClass
     * @param url
     * @param isForceUpdate
     * @return
     * @throws BusinessException
     */
    public static List<HtmlElement> getHtmlElements(String cssClass, String url, boolean isForceUpdate) throws BusinessException {
        HtmlPage htmlPage = recoverHtmlPage(url, isForceUpdate);
        return HtmlElementExtractor.getHtmlElements(cssClass,htmlPage,isForceUpdate);
    }

    /**
     * Extracts HTML elements, based on CSS class and a HTML Page.
     * @param cssClass
     * @param htmlPage
     * @param isForceUpdate
     * @return
     */
    public static List<HtmlElement> getHtmlElements(String cssClass,HtmlPage htmlPage, boolean isForceUpdate){
        String key = createHtmlElementKey(cssClass, htmlPage);
        if(!isForceUpdate && isHtmlElementInCache(key)){
            return htmlElementsByPage.get(key);
        }

        List<HtmlElement> elements = (List<HtmlElement>) htmlPage.getByXPath(cssClass);
        htmlElementsByPage.put(key,elements);
        return elements;
    }

    /**
     * Retrieve a page's HTML by URL.
     * @param url - WebPage URL
     * @param isForceUpdate - Choose between forcing HTML update or using cache
     * @return
     * @throws BusinessException
     */
    private static HtmlPage recoverHtmlPage(String url, boolean isForceUpdate) throws BusinessException {
        return HtmlPageExtractor.getHtmlForPage(url, isForceUpdate);
    }

    /**
     * Check if HTML element is already mapped in cache
     * @param key
     * @return
     */
    private static boolean isHtmlElementInCache(String key) {
        return htmlElementsByPage.containsKey(key);
    }

    /**
     * Create the key used in the cache map. HMTLPage + CSS composite
     * @param cssClass
     * @param htmlPage
     * @return
     */
    private static String createHtmlElementKey(String cssClass, HtmlPage htmlPage) {
        return htmlPage + cssClass;
    }
}
