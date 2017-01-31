package org.jahia.modules.governor.bean;

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
import org.springframework.context.i18n.LocaleContextHolder;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Juan Carlos Rodas on 5/09/201.
 */
public class ReportOrphanContent extends QueryReport {
    private static Logger logger = LoggerFactory.getLogger(ReportOrphanContent.class);
    protected static final String BUNDLE = "resources.content-governor";

    private Map<String, JCRNodeWrapper> userMap;



    /**
     * Instantiates a new Report pages without title.
     *
     * @param siteNode the site node {@link JCRSiteNode}
     */
    public ReportOrphanContent(JCRSiteNode siteNode) {
        super(siteNode);
        this.siteNode = siteNode;
        this.userMap = new HashedMap();
    }

    @Override
    public void execute(JCRSessionWrapper session, int offset, int limit) throws RepositoryException, JSONException {
        this.fillUserData(session);
        String pageQueryStr = "SELECT * FROM [jnt:page] AS item WHERE ISDESCENDANTNODE(item,['" + siteNode.getPath() + "'])";
        String contentQueryStr = "SELECT * FROM [jmix:editorialContent] AS item WHERE ISDESCENDANTNODE(item,['" + siteNode.getPath() + "'])";
        fillReport(session, pageQueryStr, offset, limit);
        fillReport(session, contentQueryStr, offset, limit);
    }

    /**
     * addItem
     *
     * @param node        {@link JCRNodeWrapper}
     * @throws RepositoryException
     */
    public void addItem(JCRNodeWrapper node) throws RepositoryException {

        if (!node.getCreationUser().equalsIgnoreCase("system") && !this.userMap.containsKey(node.getCreationUser())) {
            JCRNodeWrapper itemParentPage = node;
            if (!node.isNodeType("jnt:page")) {
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
            nodeMap.put("nodeUsedInPageName", itemParentPage.getName());
            nodeMap.put("nodeUsedInPageDisplayableName", itemParentPage.getDisplayableName());
            nodeMap.put("nodeUsedInPagePath", itemParentPage.getPath());
            nodeMap.put("nodeUsedInPageUrl", itemParentPage.getUrl());
            nodeMap.put("nodeUsedInPageTitle", (itemParentPage.hasI18N(this.locale) && itemParentPage.getI18N(this.defaultLocale).hasProperty("jcr:title")) ? itemParentPage.getI18N(this.defaultLocale).getProperty("jcr:title").getString() : "");
            nodeMap.put("nodeDisplayableName", node.getDisplayableName());
            nodeMap.put("nodeTitle", (node.hasI18N(this.locale) && node.getI18N(this.defaultLocale).hasProperty("jcr:title")) ? node.getI18N(this.defaultLocale).getProperty("jcr:title").getString() : "");
            nodeMap.put("displayTitle", StringUtils.isNotEmpty(nodeMap.get("nodeTitle")) ? nodeMap.get("nodeTitle") : nodeMap.get("nodeName"));
            this.dataList.add(nodeMap);
        }
    }

    /**
     * fillUserData
     * <p>fill the user map with the user nodes from jahia. </p>
     *
     * @throws RepositoryException
     */
    private void fillUserData(JCRSessionWrapper session) throws RepositoryException {
        String queryStr = "SELECT * FROM [jnt:user] ";
        // Getting the items  nodes.
        Query query = session.getWorkspace().getQueryManager().createQuery(queryStr, Query.JCR_SQL2);
        NodeIterator iterator = query.execute().getNodes();
        while (iterator.hasNext()) {
            JCRNodeWrapper nodeItem = (JCRNodeWrapper) iterator.next();
            this.userMap.put(nodeItem.getName(), nodeItem);
        }
    }



}
