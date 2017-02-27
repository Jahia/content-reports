package org.jahia.modules.reports.bean;

import org.jahia.exceptions.JahiaException;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javax.jcr.RepositoryException;
import java.util.HashMap;
import java.util.Map;

/**
 * ReportByType Class.
 *
 * Created by Juan Carlos Rodas.
 */
public class ReportByType extends BaseReport {

    private Map<String, String> dataMap;

    /**
     * The class constructor.
     */
    public ReportByType(JCRSiteNode siteNode, String path) {
        super(siteNode);
        this.dataMap = new HashMap<>();
    }

    @Override
    public void execute(JCRSessionWrapper session, int offset, int limit) throws RepositoryException, JSONException, JahiaException {
//        String strQuery = "SELECT * FROM [jmix:editorialContent] AS item WHERE ISDESCENDANTNODE(item,['" + searchPath + "'])";
//        return fillIreport(session, strQuery, new ReportByType(), null).getJson();

    }

    /**
     * addItem
     *
     * @param node {@link JCRNodeWrapper}
     * @param contentType {@link SearchContentType}
     * @throws RepositoryException
     */
    public void addItem(JCRNodeWrapper node, SearchContentType contentType) throws RepositoryException {
        String type = node.getPrimaryNodeTypeName().split(":")[1];

        if (!getDataMap().containsKey(type))
            getDataMap().put(type, node.getPrimaryNodeTypeName());

    }

    /**
     * getJson
     *
     * @return {@link JSONObject}
     * @throws JSONException
     */
    public JSONObject getJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONArray jArray = new JSONArray();
        JSONObject jsonObjectItem;


        for (String key : dataMap.keySet()) {
            jsonObjectItem = new JSONObject();
            jsonObjectItem.put("type", key);
            jsonObjectItem.put("techName", dataMap.get(key));
            /* setting each item to the json object */
            jArray.put(jsonObjectItem);
        }

        jsonObject.put("items", jArray);
        return jsonObject;
    }

    /**
     * getDataMap
     *
     * @return {@link Map}
     */
    public Map<String, String> getDataMap() {
        return dataMap;
    }

}
