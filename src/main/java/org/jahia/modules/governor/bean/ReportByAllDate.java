package org.jahia.modules.governor.bean;


import org.jahia.exceptions.JahiaException;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.i18n.LocaleContextHolder;
import org.jahia.utils.i18n.Messages;

import javax.jcr.RepositoryException;
import java.util.*;

/**
 * ReportByAllDate Class.
 * <p>
 * Created by Juan Carlos Rodas.
 */
public class ReportByAllDate extends QueryReport {

    private static final String BUNDLE = "resources.content-governor";
    private Map<Integer, Map<Integer, Map<String, Integer>>> dataMap;
    private Boolean useSystemUser;
    private SearchActionType actionType;
    private Integer totalPages = 0;
    private Integer totalContent = 0;
    private Locale locale = LocaleContextHolder.getLocale();
    private String searchPath;

    /**
     * The Constructor for the class.
     *
     * @param actionType    {@link SearchActionType}
     * @param useSystemUser {@link Boolean}
     */
    public ReportByAllDate(JCRSiteNode siteNode, SearchActionType actionType, String searchPath, Boolean useSystemUser) {
        super(siteNode);
        this.searchPath = searchPath;
        this.useSystemUser = useSystemUser;
        this.actionType = actionType;
        this.totalPages = 0;
        this.totalContent = 0;
        this.setDataMap(new HashMap<Integer, Map<Integer, Map<String, Integer>>>());
    }

    @Override
    public void execute(JCRSessionWrapper session, int offset, int limit) throws RepositoryException, JSONException, JahiaException {
        String pageQueryStr = "SELECT * FROM [jnt:page] AS item WHERE ISDESCENDANTNODE(item,['" + searchPath + "'])";
        String contentQueryStr = "SELECT * FROM [jmix:editorialContent] AS item WHERE ISDESCENDANTNODE(item,['" + searchPath + "'])";
        fillReport(session, pageQueryStr, offset, limit);
        fillReport(session, contentQueryStr, offset, limit);
    }

    /**
     * addItem
     *
     * @param node {@link JCRNodeWrapper}
     * @throws RepositoryException
     */
    public void addItem(JCRNodeWrapper node) throws RepositoryException {
        String propertyName = "";
        Date itemDate = null;

        if (actionType.equals(SearchActionType.CREATION)) {
            propertyName = "jcr:createdBy";
            itemDate = node.getCreationDateAsDate();
        } else if (actionType.equals(SearchActionType.UPDATE)) {
            propertyName = "jcr:lastModifiedBy";
            itemDate = node.getLastModifiedAsDate();
        }

        if (node.hasProperty(propertyName)) {
            String userName = node.getPropertyAsString(propertyName);
            if (userName.equalsIgnoreCase("system") && !useSystemUser)
                return;

            Calendar calendar = new GregorianCalendar();
            calendar.setTime(itemDate);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;

            if (!getDataMap().containsKey(year))
                getDataMap().put(year, new HashMap());

            if (!getDataMap().get(year).containsKey(month))
                getDataMap().get(year).put(month, new HashMap());

            SearchContentType contentType = node.isNodeType("jnt:page") ? SearchContentType.PAGE : SearchContentType.CONTENT;

             /*setting the counter*/
            if (getDataMap().get(year).get(month).containsKey(contentType.toString())) {
                getDataMap().get(year).get(month).put(contentType.toString(), (getDataMap().get(year).get(month).get(contentType.toString()) + 1));
            } else {
                getDataMap().get(year).get(month).put(contentType.toString(), 1);
            }

            if (SearchContentType.PAGE.equals(contentType)) {
                setTotalPages(getTotalPages() + 1);
            } else {
                setTotalContent(getTotalContent() + 1);
            }

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
        JSONObject jsonObjectItem;

        for (Integer yearKey : dataMap.keySet()) {
            for (Integer monthKey : dataMap.get(yearKey).keySet()) {
                jsonObjectItem = new JSONObject();
                jsonObjectItem.put("year", yearKey);
                //jsonObjectItem.put("month", monthKey);
                jsonObjectItem.put("month", Messages.get(BUNDLE, "cgnt_contentGovernor.month." + monthKey, locale));
                jsonObjectItem.put("pages", dataMap.get(yearKey).get(monthKey).get(SearchContentType.PAGE.toString()));
                jsonObjectItem.put("content", dataMap.get(yearKey).get(monthKey).get(SearchContentType.CONTENT.toString()));
                /* setting each item to the json object */
                jArray.put(jsonObjectItem);
            }
        }

        jsonObject.put("totalPages", getTotalPages());
        jsonObject.put("totalContent", getTotalContent());
        jsonObject.put("items", jArray);

        return jsonObject;
    }

    /**
     * getDataMap
     *
     * @return {@link Map}
     */
    public Map<Integer, Map<Integer, Map<String, Integer>>> getDataMap() {
        return dataMap;
    }

    /**
     * setDataMap
     *
     * @param dataMap {@link Map}
     */
    public void setDataMap(Map<Integer, Map<Integer, Map<String, Integer>>> dataMap) {
        this.dataMap = dataMap;
    }

    /**
     * getTotalPages
     *
     * @return {@link Integer}
     */
    public Integer getTotalPages() {
        return totalPages;
    }

    /**
     * setTotalContent
     *
     * @param totalContent {@link Integer}
     */
    public void setTotalContent(Integer totalContent) {
        this.totalContent = totalContent;
    }

    /**
     * getTotalContent
     *
     * @return {@link Integer}
     */
    public Integer getTotalContent() {
        return totalContent;
    }

    /**
     * setTotalPages
     *
     * @param totalPages {@link Integer}
     */
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

}
