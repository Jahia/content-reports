package org.jahia.modules.reports.bean;

import org.apache.commons.collections.map.HashedMap;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.Map;

/**
 * ReportPagesWithoutKeyword Class.
 *
 * Created by Juan Carlos Rodas.
 */
public class ReportPagesWithoutKeyword extends QueryReport {
    private static Logger logger = LoggerFactory.getLogger(ReportPagesWithoutKeyword.class);
    private static final String BUNDLE = "resources.content-reports";
    private long totalContent;
    private String sortOrder;
    private int sortCol;
    private String[] resultFields = {"jcr:title", "j:nodename"};

    /**
     * Instantiates a new Report pages without keyword.
     *
     * @param siteNode the site node {@link JCRSiteNode}
     */
    public ReportPagesWithoutKeyword(JCRSiteNode siteNode, int sortCol, String order) {
        super(siteNode);
        this.sortCol = sortCol;
        this.sortOrder = order;
    }

    @Override
    public void execute(JCRSessionWrapper session, int offset, int limit) throws RepositoryException, JSONException {
        String orderStatement = " order by item.["+resultFields[sortCol]+"] " + sortOrder;

        String query = "SELECT * FROM [jnt:page] AS item WHERE ISDESCENDANTNODE(item,['" + siteNode.getPath() + "']) and [j:keywords] is null" + orderStatement;
        fillReport(session, query, offset, limit);
        totalContent = getTotalCount(session, query);

    }

    /**
     * addItem
     *
     * @param node {@link JCRNodeWrapper}
     * @throws RepositoryException
     */
    public void addItem(JCRNodeWrapper node) throws RepositoryException {
        // if pages translation without title
        HashedMap map = new HashedMap();
        map.put("nodePath", node.getPath());
        map.put("nodeUrl", node.getUrl());
        map.put("nodeName", node.getName());
        map.put("nodeDisplayableName", node.getDisplayableName());
        map.put("nodeTitle", (node.hasI18N(this.defaultLocale) && node.getI18N(this.defaultLocale).hasProperty("jcr:title")) ? node.getI18N(this.defaultLocale).getProperty("jcr:title").getString() : "");
        this.dataList.add(map);
    }

    public JSONObject getJson() throws JSONException, RepositoryException {

        JSONObject jsonObject = new JSONObject();
        JSONArray jArray = new JSONArray();

        for (Map<String, String> nodeMap : this.dataList) {
            JSONArray item = new JSONArray();
            item.put(nodeMap.get("nodeTitle"));
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
