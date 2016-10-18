package org.jahia.modules.governor.bean;

import org.jahia.services.content.JCRNodeWrapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javax.jcr.RepositoryException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ReportBeforeDate Class.
 *
 * Created by Juan Carlos Rodas.
 */
public class ReportBeforeDate implements IReport {

    private Map<String, Integer> dataMap;
    private List<Map<String, String>> dataList;
    private Boolean useSystemUser;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * The ReportBeforeDate Constructor.
     *
     * @param useSystemUser
     */
    public ReportBeforeDate(Boolean useSystemUser){
        this.setDataList(new ArrayList<Map<String, String>>());
        this.setDataMap(new HashMap<String, Integer>());
        this.useSystemUser = useSystemUser;
    }

    /**
     * addItem
     *
     * @param node @{@link JCRNodeWrapper}
     * @param contentType @{@link IReport.SEARCH_CONTENT_TYPE}
     * @throws RepositoryException
     */
    public void addItem(JCRNodeWrapper node, SEARCH_CONTENT_TYPE contentType) throws RepositoryException {
        String propertyName = "jcr:lastModifiedBy";
        Date itemDate = node.getLastModifiedAsDate();
        Map<String, String> nodeMap;

        if(itemDate != null){
            String userName = node.getPropertyAsString(propertyName);
            if(userName.equalsIgnoreCase("system") && !useSystemUser)
                return;

            Calendar calendar = new GregorianCalendar();
            calendar.setTime(itemDate);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH) + 1;
            nodeMap = new HashMap<>();

            nodeMap.put("date", dateFormat.format(itemDate));
            nodeMap.put("nodeName", node.hasProperty("jcr:title") ? node.getPropertyAsString("jcr:title") : node.getDisplayableName());
            nodeMap.put("type", node.getPrimaryNodeTypeName());
            nodeMap.put("typeName", node.getPrimaryNodeTypeName().split(":")[1]);

            getDataList().add(nodeMap);


            /*setting the counter*/
            if(getDataMap().containsKey(dateFormat.format(itemDate)))
                getDataMap().put(dateFormat.format(itemDate), getDataMap().get(dateFormat.format(itemDate)) + 1);
            else
                getDataMap().put(dateFormat.format(itemDate), 1);
        }
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
        JSONObject jsonObjectItem = null;
        JSONArray jsonArrayLabels = new JSONArray();;
        JSONArray jsonArrayValues = new JSONArray();

        /* filling the chart data */
        for (String dateKey: dataMap.keySet()) {
            jsonArrayLabels.put(dateKey);
            jsonArrayValues.put(dataMap.get(dateKey));
        }

        /* filling the table data */
        for (Map<String, String> itemMap : getDataList()){
            jsonObjectItem = new JSONObject();
            jsonObjectItem.put("date", itemMap.get("date"));
            jsonObjectItem.put("nodeName", itemMap.get("nodeName"));
            jsonObjectItem.put("type", itemMap.get("type"));
            jsonObjectItem.put("typeName", itemMap.get("typeName"));
            jArray.put(jsonObjectItem);
        }

        jsonObject.put("items",jArray);
        jsonObject.put("chartLabels",jsonArrayLabels);
        jsonObject.put("chartValues",jsonArrayValues);

        return jsonObject;
    }

    /**
     * getDataMap
     *
     * @return {@link Map}
     */
    public Map<String, Integer> getDataMap() {
        return dataMap;
    }

    /**
     * setDataMap
     *
     * @param dataMap {@link Map}
     */
    public void setDataMap(Map<String, Integer> dataMap) {
        this.dataMap = dataMap;
    }

    /**
     * getDataList
     *
     * @return {@link List}
     */
    public List<Map<String, String>> getDataList() {
        return dataList;
    }

    /**
     * setDataList
     *
     * @param dataList {@link List}
     */
    public void setDataList(List<Map<String, String>> dataList) {
        this.dataList = dataList;
    }
}
