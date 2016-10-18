package org.jahia.modules.governor.bean;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.jahia.services.content.JCRContentUtils;
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
import java.util.*;

/**
 * ReportAclInheritanceStopped Class.
 *
 * Created by Juan Carlos Rodas.
 */
public class ReportAclInheritanceStopped implements IReport {

    protected DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    protected static final String BUNDLE = "resources.content-governor";
    protected Locale locale = LocaleContextHolder.getLocale();
    protected Locale defaultLocale;
    private static Logger logger = LoggerFactory.getLogger(ReportAclInheritanceStopped.class);
    protected JCRSiteNode siteNode;
    List<Map<String, String>> dataList;
    protected Map<String, Locale> localeMap;


    /**
     * Instantiates a new Report pages without title.
     *
     * @param siteNode the site node
     */
    public ReportAclInheritanceStopped(JCRSiteNode siteNode) throws RepositoryException {
        this.siteNode  = siteNode;
        this.localeMap = new HashMap<>();
        this.dataList  = new ArrayList<>();

        // filling the locale map.
        for (Locale ilocale : siteNode.getLanguagesAsLocales())
            this.localeMap.put(ilocale.toString(), locale);

        // setting the default locale for the site.
        this.defaultLocale = this.localeMap.get(siteNode.getDefaultLanguage());
    }

    /**
     * addItem
     *
     * @param node {@link JCRNodeWrapper}
     * @param contentType {@link IReport.SEARCH_CONTENT_TYPE}
     * @throws RepositoryException
     */
    public void addItem(JCRNodeWrapper node, SEARCH_CONTENT_TYPE contentType) throws RepositoryException {
        //adding the node to list if Acl Inheritance is Break
        if(node.getAclInheritanceBreak()){
            Map<String, String> nodeMap = new HashedMap();
            nodeMap.put("nodePath", node.getPath());
            nodeMap.put("nodeUrl", node.getUrl());
            nodeMap.put("nodeName", node.getName());
            nodeMap.put("nodeType", node.getPrimaryNodeTypeName());
            nodeMap.put("expiration", node.getPropertyAsString("j:expiration"));
            nodeMap.put("nodeTypeTechName", node.getPrimaryNodeTypeName().split(":")[1]);
            nodeMap.put("nodeTypeName", node.getPrimaryNodeType().getName());
            nodeMap.put("nodeTypePrefix", node.getPrimaryNodeType().getPrefix());
            nodeMap.put("nodeTypePrefix", node.getPrimaryNodeType().getPrefix());
            nodeMap.put("nodeTypeAlias", node.getPrimaryNodeType().getAlias());
            nodeMap.put("nodeAuthor", node.getCreationUser());
            nodeMap.put("nodeLockedBy", node.getLockOwner());
            nodeMap.put("nodeDisplayableName", node.getDisplayableName());
            nodeMap.put("nodeTitle", (node.hasI18N(this.locale) && node.getI18N(this.defaultLocale).hasProperty("jcr:title")) ? node.getI18N(this.defaultLocale).getProperty("jcr:title").getString() : "");
            nodeMap.put("displayTitle", StringUtils.isNotEmpty(nodeMap.get("nodeTitle")) ? nodeMap.get("nodeTitle") : nodeMap.get("nodeName"));
            this.dataList.add(nodeMap);
        }
    }

    /**
     * getJson
     *
     * @return JSONObject {@link JSONObject}
     * @throws JSONException
     * @throws RepositoryException
     */
    public JSONObject getJson() throws JSONException, RepositoryException {

        JSONObject jsonObject = new JSONObject();
        JSONArray jArray = new JSONArray();
        JSONObject jsonObjectItem;

        for (Map<String, String> nodeMap : this.dataList) {
            jsonObjectItem = new JSONObject();
            for (String key: nodeMap.keySet()) {
                jsonObjectItem.put(key, nodeMap.get(key));
            }
            jArray.put(jsonObjectItem);
        }
        jsonObject.put("siteName", siteNode.getName());
        jsonObject.put("siteDisplayableName", siteNode.getDisplayableName());
        jsonObject.put("items", jArray);
        return jsonObject;
    }

}
