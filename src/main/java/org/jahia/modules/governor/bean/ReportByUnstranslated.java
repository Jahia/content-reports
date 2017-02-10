package org.jahia.modules.governor.bean;


import org.apache.jackrabbit.core.NodeImpl;
import org.jahia.exceptions.JahiaException;
import org.jahia.services.content.JCRNodeIteratorWrapper;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * ReportByAllDate Class.
 * <p>
 * Created by Juan Carlos Rodas.
 */
public class ReportByUnstranslated extends QueryReport {

    private static final String BUNDLE = "resources.content-governor";
    private Map<Integer, Map<Integer, Map<String, Integer>>> dataMap;
    private Map<String, Map<String, Object>> pageMap;
    private Boolean useSystemUser;
    private Integer totalPages = 0;
    private Integer totalContent = 0;
    private Locale locale = LocaleContextHolder.getLocale();
    private String searchPath;
    private String typeSearch;


    String searchLanguage;

    public ReportByUnstranslated(JCRSiteNode siteNode, String searchLanguage, String searchPath, String typeSearch) {
        super(siteNode);
        this.searchPath = searchPath;
        this.searchLanguage = searchLanguage;
        this.typeSearch = typeSearch;

        this.setDataMap(new HashMap<Integer, Map<Integer, Map<String, Integer>>>());
        this.setPageMap(new HashMap<String, Map<String, Object>>());
    }

    @Override
    public void execute(JCRSessionWrapper session, int offset, int limit) throws RepositoryException, JSONException, JahiaException {
        String query;
        String nodetype;
        if (typeSearch.equals("pages")) { nodetype = "jnt:page"; } else { nodetype = "jmix:editorialContent"; }

        query = "SELECT * FROM [" + nodetype + "] AS item WHERE ISDESCENDANTNODE(item,['" + searchPath + "']) ";
        fillReport(session, query, offset, 1000); // temporary limit until a real server side pagination is implemented
        setTotalContent(pageMap.size());
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
        if (node.hasNode("j:translation_"+searchLanguage)) {
            return;
        }

        boolean hasJTranslationNodes = false;
        NodeIterator iterator = node.getRealNode().getNodes();
        while (iterator.hasNext()) {
            if (((NodeImpl)iterator.next()).getName().contains("j:translation_")) {
                hasJTranslationNodes = true;
            }

        }
        if (!hasJTranslationNodes)
            return;

        pageMap.put(node.getIdentifier(), new HashMap<String, Object>());
        Map<String, Object> nodeEntry = pageMap.get(node.getIdentifier());
        nodeEntry.put("name",  node.getDisplayableName());
        nodeEntry.put("path", node.getPath());
        nodeEntry.put("type", node.getPrimaryNodeType().getAlias());

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

        for (String content : pageMap.keySet()) {

                jsonObjectItem = new JSONObject();
                jsonObjectItem.put("title", pageMap.get(content).get("name"));
                jsonObjectItem.put("path", pageMap.get(content).get("path"));
                jsonObjectItem.put("type", pageMap.get(content).get("type"));
                /* setting each item to the json object */
                jArray.put(jsonObjectItem);

        }

        jsonObject.put("totalContent", jArray.length());
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

    public Map<String, Map<String, Object>> getPageMap() {
        return pageMap;
    }

    public void setPageMap(Map<String, Map<String, Object>> pageMap) {
        this.pageMap = pageMap;
    }

}
