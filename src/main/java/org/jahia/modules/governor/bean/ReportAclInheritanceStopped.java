package org.jahia.modules.governor.bean;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
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
 * <p>
 * Created by Juan Carlos Rodas.
 */
public class ReportAclInheritanceStopped extends QueryReport {
    private static Logger logger = LoggerFactory.getLogger(ReportAclInheritanceStopped.class);
    protected static final String BUNDLE = "resources.content-governor";
    private long totalContent;

    /**
     * Instantiates a new Report pages without title.
     *
     * @param siteNode the site node
     */
    public ReportAclInheritanceStopped(JCRSiteNode siteNode) {
        super(siteNode);
    }

    @Override
    public void execute(JCRSessionWrapper session, int offset, int limit) throws RepositoryException, JSONException {
        String queryStr = "SELECT * FROM [jnt:acl] AS item WHERE [j:inherit]=false and ISDESCENDANTNODE(item,['" + siteNode.getPath() + "'])";
        fillReport(session, queryStr, offset, limit);
        totalContent = getTotalCount(session, queryStr);
    }

    /**
     * addItem
     *
     * @param node {@link JCRNodeWrapper}
     * @throws RepositoryException
     */
    public void addItem(JCRNodeWrapper node) throws RepositoryException {
        //adding the node to list if Acl Inheritance is Break
        node = node.getParent();
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

    public JSONObject getJson() throws JSONException, RepositoryException {

        JSONObject jsonObject = new JSONObject();
        JSONArray jArray = new JSONArray();

        for (Map<String, String> nodeMap : this.dataList) {
            JSONArray item = new JSONArray();
            item.put(nodeMap.get("nodeName"));
            item.put(nodeMap.get("nodePath"));
            jArray.put(item);        }

        jsonObject.put("recordsTotal", totalContent);
        jsonObject.put("recordsFiltered", totalContent);
        jsonObject.put("siteName", siteNode.getName());
        jsonObject.put("siteDisplayableName", siteNode.getDisplayableName());
        jsonObject.put("data", jArray);
        return jsonObject;
    }



}
