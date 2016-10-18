package org.jahia.modules.governor.bean;

import org.jahia.services.content.JCRNodeWrapper;
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
public class ReportByType implements IReport {

    private Map<String, String> dataMap;

    /**
     * The class constructor.
     */
    public ReportByType() {
        this.dataMap = new HashMap<>();
    }

    /**
     * addItem
     *
     * @param node {@link JCRNodeWrapper}
     * @param contentType {@link SEARCH_CONTENT_TYPE}
     * @throws RepositoryException
     */
    public void addItem(JCRNodeWrapper node, SEARCH_CONTENT_TYPE contentType) throws RepositoryException {
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
