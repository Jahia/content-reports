package org.jahia.modules.governor.bean;

import org.apache.commons.lang.StringUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ReportPagesWithoutTitle Class.
 *
 * Created by Juan Carlos Rodas.
 */
public class ReportPagesWithoutTitle implements IReport {

    protected DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    protected static final String BUNDLE = "resources.content-governor";
    protected Locale locale = LocaleContextHolder.getLocale();
    private static Logger logger = LoggerFactory.getLogger(ReportPagesWithoutTitle.class);
    protected JCRSiteNode siteNode;
    protected Map<String, Locale> localeMap;
    protected Map<String, Map<String, Object>> dataMap;

    /**
     * Instantiates a new Report pages without title.
     *
     * @param siteNode the site node {@link JCRSiteNode}
     */
    public ReportPagesWithoutTitle(JCRSiteNode siteNode) {
        this.siteNode = siteNode;
        this.localeMap = new HashMap<>();
        this.dataMap = new HashMap<>();

        for (Locale ilocale : siteNode.getLanguagesAsLocales())
            this.localeMap.put(ilocale.toString(), locale);
    }

    /**
     * addItem
     *
     * @param node {@link JCRNodeWrapper}
     * @param contentType {@link SEARCH_CONTENT_TYPE}
     * @throws RepositoryException
     */
    public void addItem(JCRNodeWrapper node, SEARCH_CONTENT_TYPE contentType) throws RepositoryException {
        Map<String, String> translationsMap = new HashMap<>();
        Integer noTitleCounter = 0;
        for (String lang : this.localeMap.keySet()) {
            translationsMap.put(lang, getTranslationContent(node, lang));
            if (StringUtils.isEmpty(translationsMap.get(lang))) {
                noTitleCounter++;
            }
        }

        // if pages translation without title
        if (noTitleCounter > 0) {
            this.dataMap.put(node.getPath(), new HashMap<String, Object>());
            this.dataMap.get(node.getPath()).put("url", node.getUrl());
            this.dataMap.get(node.getPath()).put("translations", translationsMap);
        }
    }

    /**
     * getJson
     *
     * @return {@link JSONObject}
     * @throws JSONException
     * @throws RepositoryException
     */
    public JSONObject getJson() throws JSONException, RepositoryException {
        JSONObject jsonObject = new JSONObject();
        JSONArray jArray = new JSONArray();
        JSONObject jsonObjectItem;
        JSONArray jArray2;
        JSONObject jsonObjectItem2;

        for (String path : this.dataMap.keySet()) {
            jsonObjectItem = new JSONObject();
            jArray2 = new JSONArray();
            jsonObjectItem.put("nodePath", path);
            jsonObjectItem.put("nodeUrl", (String) this.dataMap.get(path).get("url"));
            for (String langKey : ((HashMap<String, String>) this.dataMap.get(path).get("translations")).keySet()) {
                jsonObjectItem2 = new JSONObject();
                jsonObjectItem2.put("value", ((HashMap<String, String>) this.dataMap.get(path).get("translations")).get(langKey));
                jsonObjectItem2.put("lang", langKey);
                jArray2.put(jsonObjectItem2);
            }
            jsonObjectItem.put("translations", jArray2);
            jArray.put(jsonObjectItem);
        }

        jsonObject.put("siteName", siteNode.getName());
        jsonObject.put("siteDisplayableName", siteNode.getDisplayableName());
        jsonObject.put("items", jArray);
        return jsonObject;
    }

    /**
     * getTranslationContent
     * <code> get the content from jcr:title
     * if exists in the specific lang. </code>
     *
     * @param parentNode {@link JCRNodeWrapper}
     * @param localeLang {@link String}
     * @return the title of the node {@link String}
     * @throws RepositoryException
     */
    private String getTranslationContent(JCRNodeWrapper parentNode, String localeLang) throws RepositoryException {
        NodeIterator ni = parentNode.getI18Ns();
        while (ni.hasNext()) {
            Node translationNode = ni.nextNode();

            if (translationNode.getProperty("jcr:language").getString().equalsIgnoreCase(localeLang) &&
                    translationNode.hasProperty("jcr:title")) {
                return translationNode.getProperty("jcr:title").getString();
            }
        }
        return "";
    }


}
