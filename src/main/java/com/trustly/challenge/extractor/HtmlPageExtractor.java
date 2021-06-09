package com.trustly.challenge.extractor;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.trustly.challenge.exception.BusinessException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class HtmlPageExtractor {
    private static final WebClient client = new WebClient();
    private static final Map<String, HtmlPage> htmlPages = new HashMap<>();


    private HtmlPageExtractor() {}


    public static HtmlPage getHtmlForPage(String url, boolean isForceUpdate) throws BusinessException {
        if (!isForceUpdate && isHtmlPageInCache(url)) {
            return htmlPages.get(url);
        } else {
            try {
                enableCss(false);
                enableJavascript(false);

                HtmlPage page = client.getPage(url);
                htmlPages.put(url, page);

                return page;
            } catch (IOException | FailingHttpStatusCodeException e) {
                throw new BusinessException("It was not possible to retrieve the HTML of the page: " + url);
            }
        }
    }

    private static boolean isHtmlPageInCache(String url) {
        return htmlPages.containsKey(url);
    }

    private static void enableJavascript(boolean statusJs) {
        client.getOptions().setJavaScriptEnabled(statusJs);
    }

    private static void enableCss(boolean statusCSS) {
        client.getOptions().setCssEnabled(statusCSS);
    }




}



