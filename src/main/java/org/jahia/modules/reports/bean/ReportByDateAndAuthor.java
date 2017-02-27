package org.jahia.modules.reports.bean;


import org.jahia.exceptions.JahiaException;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.jcr.RepositoryException;
import java.util.*;

/**
 * ReportByAllDate Class.
 * <p>
 * Created by Juan Carlos Rodas.
 */
public class ReportByDateAndAuthor extends QueryReport {

    private static final String BUNDLE = "resources.content-reports";
    private Map<Integer, Map<Integer, Map<String, Integer>>> dataMap;
    private Map<String, Map<String, Object>> pageMap;
    private Boolean useSystemUser;
    private SearchActionType actionType;
    private long totalContent = 0;
    private Locale locale = LocaleContextHolder.getLocale();
    private String searchPath;
    private String typeSearch;
    private Boolean searchByDate;
    private String typeDateSearch;
    private String dateBegin;
    private String dateEnd;
    private Boolean searchByAuthor;
    private String searchUsername;
    private String typeAuthorSearch;
    /**
     * The Constructor for the class.
     *
     * @param actionType    {@link SearchActionType}
     * @param useSystemUser {@link Boolean}
     */
    public ReportByDateAndAuthor(JCRSiteNode siteNode, SearchActionType actionType, String searchPath, String typeSearch, Boolean useSystemUser, Boolean searchByDate, String typeDateSearch, String dateBegin, String dateEnd, Boolean searchAuthor, String searchUsername, String typeAuthorSearch) {
        super(siteNode);
        this.searchPath = searchPath;
        this.typeSearch = typeSearch;
        this.searchByDate = searchByDate;
        this.typeDateSearch = typeDateSearch;
        this.dateBegin = dateBegin;
        this.dateEnd = dateEnd;
        this.searchByAuthor = searchAuthor;
        this.searchUsername = searchUsername;
        this.typeAuthorSearch = typeAuthorSearch;
        this.useSystemUser = useSystemUser;
        this.actionType = actionType;
        this.totalContent = 0;
        this.setDataMap(new HashMap<Integer, Map<Integer, Map<String, Integer>>>());
        this.setPageMap(new HashMap<String, Map<String, Object>>());
    }

    @Override
    public void execute(JCRSessionWrapper session, int offset, int limit) throws RepositoryException, JSONException, JahiaException {
        String query;
        String nodetype;
        String searchByDateStatement = "";
        String searchByAuthorStatement = "";
        if (typeSearch.equals("pages")) { nodetype = "jnt:page"; } else { nodetype = "jmix:editorialContent"; }
        if (searchByDate) {
            String dateSearchField;
            if (typeDateSearch.equals("modified")) { dateSearchField = "jcr:lastModified"; } else { dateSearchField = "jcr:created";   }
            if (!dateBegin.isEmpty()) { searchByDateStatement += " AND item.["+dateSearchField+"] >= '"+dateBegin+"T00:00:00.000+00:00' "; }
            if (!dateEnd.isEmpty())  { searchByDateStatement += " AND item.["+dateSearchField+"] <= '"+dateEnd+"T00:00:00.000+00:00'"; }
        }
        if (searchByAuthor) {
            String usernameField;
            if (typeAuthorSearch.equals("modified")) { usernameField = "jcr:lastModifiedBy"; } else { usernameField = "jcr:createdBy";   }
            searchByAuthorStatement = " AND item.["+usernameField+"] = '"+searchUsername+"'";
        }

        query = "SELECT * FROM [ " + nodetype + " ] AS item WHERE ISDESCENDANTNODE(item,['" + searchPath + "']) " + searchByDateStatement + searchByAuthorStatement;
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
        String propertyName = "";
        Date itemDate = null;
        pageMap.put(node.getIdentifier(), new HashMap<String, Object>());
        Map<String, Object> nodeEntry = pageMap.get(node.getIdentifier());
        nodeEntry.put("name",  node.getDisplayableName());
        nodeEntry.put("path", node.getPath());
        nodeEntry.put("type", node.getPrimaryNodeType().getAlias());
        nodeEntry.put("created", node.getPropertyAsString("jcr:created"));
        nodeEntry.put("modified", node.getPropertyAsString("jcr:lastModified"));
        nodeEntry.put("published", node.getPropertyAsString("j:published"));
        if (node.hasProperty("j:locktoken")) {
            nodeEntry.put("locked", !node.getPropertyAsString("j:locktoken").isEmpty());
        } else {
            nodeEntry.put("locked", false);
        }
    }

    /*
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

    */

    /**
     * getJson
     *
     * @return {@link JSONObject}
     * @throws JSONException
     */
    public JSONObject getJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONArray jArray = new JSONArray();
        JSONArray jsonArrayItem;

        for (String content : pageMap.keySet()) {

                /*jsonObjectItem = new JSONObject();
                jsonObjectItem.put("title", pageMap.get(content).get("name"));
                jsonObjectItem.put("created", pageMap.get(content).get("created"));
                jsonObjectItem.put("modified", pageMap.get(content).get("modified"));
                jsonObjectItem.put("published", pageMap.get(content).get("published"));
                jsonObjectItem.put("locked", pageMap.get(content).get("locked"));
                jsonObjectItem.put("path", pageMap.get(content).get("path"));
                jsonObjectItem.put("type", pageMap.get(content).get("type"));
                /* setting each item to the json object */
                jsonArrayItem = new JSONArray();

                jsonArrayItem.put(pageMap.get(content).get("name"));
                jsonArrayItem.put(pageMap.get(content).get("path"));
                jsonArrayItem.put(pageMap.get(content).get("type"));
                jsonArrayItem.put(pageMap.get(content).get("created"));
                jsonArrayItem.put(pageMap.get(content).get("modified"));
                jsonArrayItem.put(pageMap.get(content).get("published"));
                jsonArrayItem.put(pageMap.get(content).get("locked"));

                jArray.put(jsonArrayItem);

        }

        jsonObject.put("recordsTotal", totalContent);
        jsonObject.put("recordsFiltered", totalContent);
        jsonObject.put("data", jArray);

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
    public long getTotalContent() {
        return totalContent;
    }


    public Map<String, Map<String, Object>> getPageMap() {
        return pageMap;
    }

    public void setPageMap(Map<String, Map<String, Object>> pageMap) {
        this.pageMap = pageMap;
    }

}
