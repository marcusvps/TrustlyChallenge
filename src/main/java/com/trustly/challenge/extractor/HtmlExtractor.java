package com.trustly.challenge.extractor;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class HtmlExtractor {
    private static final WebClient client = new WebClient();
    private static final Map<String, HtmlPage> htmlPages = new HashMap<>();
    private static final Map<HtmlPage, List<HtmlElement>> htmlElementsByPage = new HashMap<>();

    private HtmlExtractor() {}

    /**
     *
     * @param url
     * @return
     * @throws Exception
     */
    public static HtmlPage getHtmlForPage(String url) throws Exception {
        if(htmlPages.containsKey(url)){
            return htmlPages.get(url);
        }
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        try {
            HtmlPage page = client.getPage(url);
            htmlPages.put(url,page);
            return page;
        } catch (Exception e) {
            throw new Exception("Not found");
        }
    }

    /**
     *
     * @param cssClass
     * @param htmlPage
     * @return
     */
    public static List<HtmlElement> getHtmlElements(String cssClass,HtmlPage htmlPage){
        if(htmlElementsByPage.containsKey(htmlPage)){
            return htmlElementsByPage.get(htmlPage);
        }
        List<HtmlElement> elements = (List<HtmlElement>) htmlPage.getByXPath(cssClass);
        htmlElementsByPage.put(htmlPage,elements);
        return elements;
    }


    /**
     *
     * @param cssClass
     * @param url
     * @return
     * @throws Exception
     */
    public static List<HtmlElement> getHtmlElements(String cssClass,String url) throws Exception {
        HtmlPage htmlPage = HtmlExtractor.getHtmlForPage(url);
        return HtmlExtractor.getHtmlElements(cssClass,htmlPage);
    }

}



