package org.jahia.modules.governor.bean;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.jahia.api.Constants;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.utils.i18n.Messages;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.i18n.LocaleContextHolder;
import javax.jcr.RepositoryException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ReportByStatus Class.
 *
 * Created by Juan Carlos Rodas.
 */
public class ReportByStatus implements IReport {

    private final String PROPERTY_NAME = "name";
    private final String PROPERTY_LIST = "list";
    private final String PROPERTY_ITEMS = "items";
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Map<String, Map<String, Object>> dataMap;
    private static final String BUNDLE = "resources.content-governor";
    private Locale locale = LocaleContextHolder.getLocale();

    /**
     * The class constructor.
     */
    public ReportByStatus() {
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
        if (node.hasProperty(Constants.WORKINPROGRESS))
            addItemDataToList("WIP", "cgnt_contentGovernor.status.workInProgress", "work in progress", node);
        else if (node.getLastPublishedAsDate() != null && node.getLastPublishedAsDate().before(node.getLastModifiedAsDate()))
            addItemDataToList("MNP", "cgnt_contentGovernor.status.modifiedNotPublished", "modified not published", node);
        else if (!node.hasProperty(Constants.PUBLISHED) &&  node.getLastPublishedAsDate() != null)
            addItemDataToList("NVP", "cgnt_contentGovernor.status.neverPublished", "never published", node);
        else if (node.getLastPublishedAsDate() != null && node.hasProperty(Constants.PUBLISHED) && !node.getProperty(Constants.PUBLISHED).getBoolean())
            addItemDataToList("UNP", "cgnt_contentGovernor.status.unpublished", "unpublished", node);
    }

    /**
     * getJson
     *
     * @return {@link JSONObject}
     * @throws JSONException
     * @throws RepositoryException
     */
    public JSONObject getJson() throws JSONException, RepositoryException {
        JSONObject jsonObject = new JSONObject();
        JSONArray jArray = new JSONArray();
        JSONObject jsonObjectItem;

        JSONArray jsonArrayItemDetail;
        JSONObject jsonObjectSubItemDetail;

        for (String key : dataMap.keySet()) {
            jsonObjectItem = new JSONObject();
            jsonObjectItem.put(PROPERTY_NAME, dataMap.get(key).get(PROPERTY_NAME));

            /* part of items detail */
            jsonArrayItemDetail = new JSONArray();
            for (JCRNodeWrapper nodeItem : (ArrayList<JCRNodeWrapper>) dataMap.get(key).get(PROPERTY_LIST)) {
                jsonObjectSubItemDetail = new JSONObject();
                jsonObjectSubItemDetail.put("path", nodeItem.getPath());
                jsonObjectSubItemDetail.put("canonicalPath", nodeItem.getCanonicalPath());
                jsonObjectSubItemDetail.put("identifier", nodeItem.getIdentifier());
                jsonObjectSubItemDetail.put("title", nodeItem.hasProperty("jcr:title") ? nodeItem.getPropertyAsString("jcr:title") : "");
                jsonObjectSubItemDetail.put("displayableName", nodeItem.getDisplayableName());
                jsonObjectSubItemDetail.put("name", nodeItem.getName());
                jsonObjectSubItemDetail.put("displayTitle", nodeItem.hasProperty("jcr:title") ? nodeItem.getPropertyAsString("jcr:title") : nodeItem.getDisplayableName());
                jsonObjectSubItemDetail.put("primaryNodeType", nodeItem.getPrimaryNodeType());
                jsonObjectSubItemDetail.put("type", nodeItem.getPrimaryNodeTypeName().split(":")[1]);
                jsonObjectSubItemDetail.put("path", nodeItem.getPath());
                jsonObjectSubItemDetail.put("identifier", nodeItem.getIdentifier());
                jsonObjectSubItemDetail.put("created", dateFormat.format(nodeItem.getCreationDateAsDate()));
                jsonObjectSubItemDetail.put("lastModified", dateFormat.format(nodeItem.getLastModifiedAsDate()));
                jsonObjectSubItemDetail.put("lastModifiedBy", nodeItem.getModificationUser());
                jsonObjectSubItemDetail.put("published", nodeItem.hasProperty("j:published") ? true : false);
                jsonObjectSubItemDetail.put("lock", nodeItem.isLocked() ? true : false);
                jsonObjectSubItemDetail.put("language", StringUtils.isNotEmpty(nodeItem.getLanguage()) ? nodeItem.getLanguage() : "");
                jsonArrayItemDetail.put(jsonObjectSubItemDetail);
            }
            jsonObjectItem.put(PROPERTY_ITEMS, jsonArrayItemDetail);
            jArray.put(jsonObjectItem);
        }

        jsonObject.put("statusItems", jArray);
        return jsonObject;
    }

    /**
     * addItemDataToList
     * <p>method add a node to a list, divided by status.</p>
     *
     * @param statusName {@link String}
     * @param statusNameKey {@link String}
     * @param defaultNameValue {@link String}
     * @param node {@link JCRNodeWrapper}
     */
    private void addItemDataToList(String statusName, String statusNameKey, String defaultNameValue, JCRNodeWrapper node){
        if(!dataMap.containsKey(statusName))
            dataMap.put(statusName, new HashedMap());

        if(!dataMap.get(statusName).containsKey(PROPERTY_NAME))
            dataMap.get(statusName).put(PROPERTY_NAME,  Messages.get(BUNDLE, statusNameKey, locale));

        if(!dataMap.get(statusName).containsKey(PROPERTY_LIST))
            dataMap.get(statusName).put(PROPERTY_LIST, new ArrayList<JCRNodeWrapper>());

        ((ArrayList)dataMap.get(statusName).get(PROPERTY_LIST)).add(node);
    }

    /**
     * getDataMap
     *
     * @return {@link Map}
     */
    public Map<String, Map<String, Object>> getDataMap() {
        return dataMap;
    }

}
