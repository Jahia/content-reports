package org.jahia.modules.governor.bean;

import org.apache.commons.collections.map.HashedMap;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import javax.jcr.RepositoryException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * ReportPagesWithoutKeyword Class.
 *
 * Created by Juan Carlos Rodas.
 */
public class ReportPagesWithoutKeyword implements IReport {

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final String BUNDLE = "resources.content-governor";
    private Locale locale = LocaleContextHolder.getLocale();
    private Locale defaultLocale;
    private static Logger logger = LoggerFactory.getLogger(ReportPagesWithoutKeyword.class);
    private JCRSiteNode siteNode;
    private Map<String, Locale> localeMap;
    private Map<String, Map<String, Object>> dataMap;


    /**
     * Instantiates a new Report pages without keyword.
     *
     * @param siteNode the site node {@link JCRSiteNode}
     */
    public ReportPagesWithoutKeyword(JCRSiteNode siteNode) {
        this.siteNode = siteNode;
        this.localeMap = new HashMap<>();
        this.dataMap = new HashMap<>();
        for (Locale ilocale : siteNode.getLanguagesAsLocales())
            this.localeMap.put(ilocale.toString(), locale);

        this.defaultLocale = this.localeMap.get(siteNode.getDefaultLanguage());
    }

    /**
     * addItem
     *
     * @param node {@link JCRNodeWrapper}
     * @param contentType {@link SEARCH_CONTENT_TYPE}
     * @throws RepositoryException
     */
    public void addItem(JCRNodeWrapper node, SEARCH_CONTENT_TYPE contentType) throws RepositoryException {
        // if pages translation without title
        if (isWithoutKeywordsContent(node)) {
            this.dataMap.put(node.getPath(), new HashedMap());
            this.dataMap.get(node.getPath()).put("url", node.getUrl());
            this.dataMap.get(node.getPath()).put("name", node.getName());
            this.dataMap.get(node.getPath()).put("displayableName", node.getDisplayableName());
            this.dataMap.get(node.getPath()).put("title", (node.hasI18N(this.defaultLocale) && node.getI18N(this.defaultLocale).hasProperty("jcr:title")) ? node.getI18N(this.defaultLocale).getProperty("jcr:title").getString() : "");
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

        for (String path : this.dataMap.keySet()) {
            jsonObjectItem = new JSONObject();
            jsonObjectItem.put("nodePath", path);
            jsonObjectItem.put("nodeUrl", (String) this.dataMap.get(path).get("url"));
            jsonObjectItem.put("nodeName", (String) this.dataMap.get(path).get("name"));
            jsonObjectItem.put("nodeDisplayableName", (String) this.dataMap.get(path).get("displayableName"));
            jsonObjectItem.put("nodeTitle", (String) this.dataMap.get(path).get("title"));
            jArray.put(jsonObjectItem);
        }

        jsonObject.put("siteName", siteNode.getName());
        jsonObject.put("siteDisplayableName", siteNode.getDisplayableName());
        jsonObject.put("items", jArray);
        return jsonObject;
    }

    /**
     * isWithoutKeywordsContent
     * <p> check if the node contains the
     * key j:keyword in the metadata content.</p>
     *
     * @param parentNode {@link JCRNodeWrapper}
     * @return boolean {@link Boolean}
     * @throws RepositoryException
     */
    private Boolean isWithoutKeywordsContent(JCRNodeWrapper parentNode) throws RepositoryException {
        if(parentNode.isNodeType("jmix:keywords") &&  parentNode.hasProperty("j:keywords"))
            return Boolean.FALSE;

        return Boolean.TRUE;
    }

}
