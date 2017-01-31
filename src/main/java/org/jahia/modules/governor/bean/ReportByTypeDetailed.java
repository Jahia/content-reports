package org.jahia.modules.governor.bean;

import org.apache.commons.collections.map.HashedMap;
import org.jahia.exceptions.JahiaException;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javax.jcr.RepositoryException;
import java.util.*;

/**
 * The ReportByTypeDetailed Class.
 *
 * Created by Juan Carlos Rodas.
 */
public class ReportByTypeDetailed extends BaseReport {

    private Map<String, Map<String, Object>> dataMap;
    private Integer totalCount = 0;

    /**
     * The class constructor.
     */
    public ReportByTypeDetailed(JCRSiteNode siteNode, String path) {
        super(siteNode);
        this.dataMap = new HashMap<>();
    }

    @Override
    public void execute(JCRSessionWrapper session, int offset, int limit) throws RepositoryException, JSONException, JahiaException {
//        String strQuery = "SELECT * FROM [jmix:editorialContent] AS item WHERE ISDESCENDANTNODE(item,['" + searchPath + "'])";
//        return fillIreport(session, strQuery, new ReportByTypeDetailed(), null).getJson();
    }

    /**
     * addItem
     *
     * @param node {@link JCRNodeWrapper}
     * @param contentType {@link SearchContentType}
     * @throws RepositoryException
     */
    public void addItem(JCRNodeWrapper node, SearchContentType contentType) throws RepositoryException {

        if (!getDataMap().containsKey(node.getPrimaryNodeTypeName())) {
            getDataMap().put(node.getPrimaryNodeTypeName(), new HashedMap());
            getDataMap().get(node.getPrimaryNodeTypeName()).put("type", node.getPrimaryNodeTypeName().split(":")[1]);
            getDataMap().get(node.getPrimaryNodeTypeName()).put("techName", node.getPrimaryNodeTypeName());
            getDataMap().get(node.getPrimaryNodeTypeName()).put("itemCount", 0);
        }

        /* settind the data */
        getDataMap().get(node.getPrimaryNodeTypeName()).put("itemCount", (Integer)getDataMap().get(node.getPrimaryNodeTypeName()).get("itemCount") + 1);
        this.totalCount ++;
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
        dataMap = sortByComparator(dataMap, SORT_DESC);

        for (String key : dataMap.keySet()) {
            jsonObjectItem = new JSONObject();
            jsonObjectItem.put("type",  dataMap.get(key).get("type"));
            jsonObjectItem.put("techName", dataMap.get(key).get("techName"));
            jsonObjectItem.put("itemCount", dataMap.get(key).get("itemCount"));
            jsonObjectItem.put("percentaje", (Float.parseFloat(((Integer)dataMap.get(key).get("itemCount") * 100) + "") / totalCount));

            /* setting each item to the json object */
            jArray.put(jsonObjectItem);
        }

        jsonObject.put("totalItems", totalCount);
        jsonObject.put("items", jArray);
        return jsonObject;
    }

    /**
     * getDataMap
     *
     * @return {@link Map}
     */
    public Map<String, Map<String, Object>> getDataMap() {
        return dataMap;
    }

    /**
     * sortByComparator
     * <p> returns a map ordered by itemCount. </p>
     *
     * @param unsortMap {@link Map}
     * @param order {@link Boolean}
     * @return {@link Map}
     */
    private static Map<String, Map<String, Object>> sortByComparator(Map<String, Map<String, Object>> unsortMap, final boolean order)
    {
        List<Map.Entry<String, Map<String, Object>>> list = new java.util.LinkedList<Map.Entry<String, Map<String, Object>>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<String, Map<String, Object>>>(){
            public int compare(Map.Entry<String, Map<String, Object>> o1,
                               Map.Entry<String, Map<String, Object>> o2){
                if (order)
                    return ((Integer)o1.getValue().get("itemCount")).compareTo(((Integer)o2.getValue().get("itemCount")));
                else
                    return ((Integer)o2.getValue().get("itemCount")).compareTo(((Integer)o1.getValue().get("itemCount")));
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Map<String, Object>> sortedMap = new LinkedHashMap<String, Map<String, Object>>();
        for (Map.Entry<String, Map<String, Object>> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }


}
