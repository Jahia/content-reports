package org.jahia.modules.reports.bean;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
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
 * The ReportLockedContent Class.
 *
 * Created by Juan Carlos Rodas.
 */
public class ReportLockedContent extends QueryReport {
    private static Logger logger = LoggerFactory.getLogger(ReportLockedContent.class);
    protected static final String BUNDLE = "resources.content-reports";
    private long totalContent;
    private int sortCol;
    private String order;
    private String[] resultFields = {"j:nodename", "jcr:primaryType", "jcr:createdBy", "jcr:lockOwner", "j:nodename"};

    /**
     * Instantiates a new Report pages without title.
     *
     * @param siteNode the site node {@link JCRSiteNode}
     */
    public ReportLockedContent(JCRSiteNode siteNode, int sortCol, String order) {
        super(siteNode);
        this.sortCol = sortCol;
        this.order = order;
    }

    @Override
    public void execute(JCRSessionWrapper session, int offset, int limit) throws RepositoryException, JSONException {
        String orderStatement = " order by item.["+resultFields[sortCol]+"] " + order;
        String pageQueryStr = "SELECT * FROM [jmix:editorialContent] AS item WHERE [jcr:lockOwner] is not null and ISDESCENDANTNODE(item,['" + siteNode.getPath() + "'])" + orderStatement;
        fillReport(session, pageQueryStr, offset, limit);
        totalContent = getTotalCount(session, pageQueryStr);
    }

    /**
     * addItem
     *
     * @param node {@link JCRNodeWrapper}
     * @throws RepositoryException
     */
    public void addItem(JCRNodeWrapper node) throws RepositoryException {

        if(node.isLocked()){
            JCRNodeWrapper itemParentPage = node;
            if(!node.isNodeType("jnt:page")){
                itemParentPage = JCRContentUtils.getParentOfType(node, "jnt:page");
            }

            Map<String, String> nodeMap = new HashedMap();
            nodeMap.put("nodePath", node.getPath());
            nodeMap.put("nodeUrl ", node.getUrl());
            nodeMap.put("nodeName", node.getName());
            nodeMap.put("nodeType", node.getPrimaryNodeTypeName());
            nodeMap.put("nodeTypeTechName", node.getPrimaryNodeTypeName().split(":")[1]);
            nodeMap.put("nodeTypeName", node.getPrimaryNodeType().getName());
            nodeMap.put("nodeTypePrefix", node.getPrimaryNodeType().getPrefix());
            nodeMap.put("nodeTypePrefix", node.getPrimaryNodeType().getPrefix());
            nodeMap.put("nodeTypeAlias", node.getPrimaryNodeType().getAlias());
            nodeMap.put("nodeAuthor", node.getCreationUser());
            nodeMap.put("nodeLockedBy", node.getPropertyAsString("jcr:lockOwner"));
            if (itemParentPage != null) {
                nodeMap.put("nodeUsedInPageName", itemParentPage.getName());
                nodeMap.put("nodeUsedInPageDisplayableName", itemParentPage.getDisplayableName());
                nodeMap.put("nodeUsedInPagePath", itemParentPage.getPath());
                nodeMap.put("nodeUsedInPageUrl", itemParentPage.getUrl());
                nodeMap.put("nodeUsedInPageTitle", (itemParentPage.hasI18N(this.locale) && itemParentPage.getI18N(this.defaultLocale).hasProperty("jcr:title")) ? itemParentPage.getI18N(this.defaultLocale).getProperty("jcr:title").getString() : "");
            }
            nodeMap.put("nodeDisplayableName", node.getDisplayableName());
            nodeMap.put("nodeTitle", (node.hasI18N(this.locale) && node.getI18N(this.defaultLocale).hasProperty("jcr:title")) ? node.getI18N(this.defaultLocale).getProperty("jcr:title").getString() : "");
            nodeMap.put("displayTitle", StringUtils.isNotEmpty(nodeMap.get("nodeTitle")) ? nodeMap.get("nodeTitle") : nodeMap.get("nodeName"));
            this.dataList.add(nodeMap);
        }
    }
    @Override
    public JSONObject getJson() throws JSONException, RepositoryException {

        JSONObject jsonObject = new JSONObject();
        JSONArray jArray = new JSONArray();

        for (Map<String, String> nodeMap : this.dataList) {
            JSONArray item = new JSONArray();
            item.put(nodeMap.get("nodeName"));
            item.put(nodeMap.get("nodeType"));
            item.put(nodeMap.get("nodeAuthor"));
            item.put(nodeMap.get("nodeLockedBy"));
            item.put(nodeMap.get("nodeUsedInPagePath"));
            jArray.put(item);
        }
        jsonObject.put("recordsTotal", totalContent);
        jsonObject.put("recordsFiltered", totalContent);
        jsonObject.put("siteName", siteNode.getName());
        jsonObject.put("siteDisplayableName", siteNode.getDisplayableName());
        jsonObject.put("data", jArray);
        return jsonObject;
    }



}
