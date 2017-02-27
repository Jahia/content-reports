package org.jahia.modules.reports.bean;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.jahia.exceptions.JahiaException;
import org.jahia.services.content.JCRContentUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.*;

/**
 * ReportContentFromAnotherSite Class.
 *
 * Created by Juan Carlos Rodas.
 */
public class ReportContentFromAnotherSite extends QueryReport {

    private static Logger logger = LoggerFactory.getLogger(ReportContentFromAnotherSite.class);
    protected static final String BUNDLE = "resources.content-reports";

    /**
     * Instantiates a new Report pages without title.
     *
     * @param siteNode the site node {@link JCRSiteNode}
     */
    public ReportContentFromAnotherSite(JCRSiteNode siteNode) {
        super(siteNode);
    }

    @Override
    public void execute(JCRSessionWrapper session, int offset, int limit) throws RepositoryException, JSONException, JahiaException {
        String pageQueryStr = "SELECT * FROM [jnt:contentReference] AS item WHERE ISDESCENDANTNODE(item,['" + siteNode.getPath() + "'])";
        fillReport(session, pageQueryStr, offset, limit);
    }

    /**
     * addItem
     *
     * @param node {@link JCRNodeWrapper}
     * @throws RepositoryException
     */
    public void addItem(JCRNodeWrapper node) throws RepositoryException {
        JCRNodeWrapper referenceNode = node.getProperty("j:node").getValue().getNode();
        JCRSiteNode itemSiteNode = referenceNode.getResolveSite();
        JCRNodeWrapper itemParentPage = JCRContentUtils.getParentOfType(node, "jnt:page");

        if(!this.siteNode.getPath().equals(itemSiteNode.getPath())){
            Map<String, String> nodeMap = new HashedMap();
            nodeMap.put("nodePath", node.getPath());
            nodeMap.put("nodeUrl ", node.getUrl());
            nodeMap.put("nodeName", node.getName());
            nodeMap.put("nodeType", referenceNode.getPrimaryNodeTypeName().split(":")[1]);
            nodeMap.put("nodeTechName", referenceNode.getPrimaryNodeTypeName());
            nodeMap.put("nodeDisplayableName", node.getDisplayableName());
            nodeMap.put("nodeTitle", (node.hasI18N(this.locale) && node.getI18N(this.defaultLocale).hasProperty("jcr:title")) ? node.getI18N(this.defaultLocale).getProperty("jcr:title").getString() : "");
            nodeMap.put("currentSiteName", this.siteNode.getName());
            nodeMap.put("currentSiteDisplayableName", this.siteNode.getDisplayableName());
            nodeMap.put("currentSitePath", this.siteNode.getPath());
            nodeMap.put("currentSiteUrl" , this.siteNode.getHome().getUrl());
            nodeMap.put("sourceSiteName" , itemSiteNode.getName());
            nodeMap.put("sourceSiteDisplayableName", itemSiteNode.getDisplayableName());
            nodeMap.put("sourceSitePath" , itemSiteNode.getPath());
            nodeMap.put("sourceSiteUrl"  , itemSiteNode.getHome().getUrl());
            nodeMap.put("nodeUsedInPageName", itemParentPage.getName());
            nodeMap.put("nodeUsedInPageDisplayableName", itemParentPage.getDisplayableName());
            nodeMap.put("nodeUsedInPagePath", itemParentPage.getPath());
            nodeMap.put("nodeUsedInPageUrl", itemParentPage.getUrl());
            nodeMap.put("nodeUsedInPageTitle", (itemParentPage.hasI18N(this.locale) && itemParentPage.getI18N(this.defaultLocale).hasProperty("jcr:title")) ? itemParentPage.getI18N(this.defaultLocale).getProperty("jcr:title").getString() : "");
            nodeMap.put("referenceNodeTitle", (referenceNode.hasI18N(this.locale) && referenceNode.getI18N(this.defaultLocale).hasProperty("jcr:title")) ? referenceNode.getI18N(this.defaultLocale).getProperty("jcr:title").getString() : referenceNode.getDisplayableName());
            nodeMap.put("displayTitle", StringUtils.isNotEmpty(nodeMap.get("nodeTitle")) ? nodeMap.get("nodeTitle") : nodeMap.get("referenceNodeTitle"));
            this.dataList.add(nodeMap);
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
