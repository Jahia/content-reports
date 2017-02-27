package org.jahia.modules.reports.bean;

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
 * ReportBeforeDate Class.
 *
 * Created by Juan Carlos Rodas.
 */
public class ReportBeforeDate extends QueryReport {

    private Map<String, Integer> dataMap;

    private String searchPath;
    private String strDate;
    private Boolean useSystemUser;

    /**
     * The ReportBeforeDate Constructor.
     *
     * @param useSystemUser
     */
    public ReportBeforeDate(JCRSiteNode siteNode, String path, String date, Boolean useSystemUser){
        super(siteNode);
        this.searchPath = path;
        this.strDate = date;
        this.dataMap = new HashMap<String, Integer>();
        this.useSystemUser = useSystemUser;
    }

    @Override
    public void execute(JCRSessionWrapper session, int offset, int limit) throws RepositoryException, JSONException, JahiaException {
        String strQuery = "SELECT * FROM [jmix:editorialContent] AS item WHERE ISDESCENDANTNODE(item,['" + searchPath + "']) and item.[jcr:lastModified] <= CAST('" + strDate + "T23:59:59.999Z' AS DATE)";
        fillReport(session, strQuery, offset, limit);
    }

    /**
     * addItem
     *
     * @param node @{@link JCRNodeWrapper}
     * @throws RepositoryException
     */
    public void addItem(JCRNodeWrapper node) throws RepositoryException {
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

            dataList.add(nodeMap);


            /*setting the counter*/
            if(dataMap.containsKey(dateFormat.format(itemDate)))
                dataMap.put(dateFormat.format(itemDate), dataMap.get(dateFormat.format(itemDate)) + 1);
            else
                dataMap.put(dateFormat.format(itemDate), 1);
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
        JSONArray jsonArrayLabels = new JSONArray();;
        JSONArray jsonArrayValues = new JSONArray();

        /* filling the chart data */
        for (String dateKey: dataMap.keySet()) {
            jsonArrayLabels.put(dateKey);
            jsonArrayValues.put(dataMap.get(dateKey));
        }

        /* filling the table data */
        for (Map<String, String> itemMap :dataList){
            jArray.put(new JSONObject(itemMap));
        }

        jsonObject.put("items",jArray);
        jsonObject.put("chartLabels",jsonArrayLabels);
        jsonObject.put("chartValues",jsonArrayValues);

        return jsonObject;
    }

}
