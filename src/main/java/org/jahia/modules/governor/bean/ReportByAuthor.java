package org.jahia.modules.governor.bean;

import org.apache.commons.collections.map.HashedMap;
import org.jahia.services.content.JCRNodeWrapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javax.jcr.RepositoryException;
import java.util.HashMap;
import java.util.Map;

/**
 * ReportByAuthor Class.
 *
 * Created by Juan Carlos Rodas.
 */
public class ReportByAuthor implements IReport {

    private final String PROPERTY_COUNT = "itemCount";
    private final String PROPERTY_AUTHOR_DETAIL = "authorDetail";
    private final String PROPERTY_PAGE_DETAIL = "pageDetail";
    private Integer totalItems;
    private Map<String, Map<String, Object>> dataMap;
    private Boolean useSystemUser;
    private SEARCH_CONTENT_TYPE reportType;
    private SEARCH_ACTION_TYPE actionType;

    /**
     * The ReportByAuthor constructor.
     *
     * @param reportType {@link SEARCH_CONTENT_TYPE}
     * @param actionType {@link SEARCH_ACTION_TYPE}
     * @param useSystemUser {@link Boolean}
     */
    public ReportByAuthor(SEARCH_CONTENT_TYPE reportType, SEARCH_ACTION_TYPE actionType, Boolean useSystemUser) {
        this.useSystemUser = useSystemUser;
        this.reportType = reportType;
        this.actionType = actionType;
        this.dataMap = new HashMap<>();
        this.totalItems = 0;
    }

    /**
     * addItem
     * @param node {@link JCRNodeWrapper}
     * @param contentType {@link SEARCH_CONTENT_TYPE}
     * @throws RepositoryException
     */
    public void addItem(JCRNodeWrapper node, SEARCH_CONTENT_TYPE contentType) throws RepositoryException {

        String propertyName = "";
        if (actionType.equals(SEARCH_ACTION_TYPE.CREATION))
            propertyName = "jcr:createdBy";
        else if (actionType.equals(SEARCH_ACTION_TYPE.UPDATE))
            propertyName = "jcr:lastModifiedBy";

        if (node.hasProperty(propertyName)) {
            String userName = node.getPropertyAsString(propertyName);
            if (userName.equalsIgnoreCase("system") && !useSystemUser)
                return;

            if (!getDataMap().containsKey(userName))
                getDataMap().put(userName, new HashedMap());

            /*setting the counter*/
            if (getDataMap().get(userName).containsKey(PROPERTY_COUNT)) {
                getDataMap().get(userName).put(PROPERTY_COUNT, ((Integer) getDataMap().get(userName).get(PROPERTY_COUNT) + 1));
            } else {
                getDataMap().get(userName).put(PROPERTY_COUNT, 1);
            }

            /*setting the author detail*/
            if (!getDataMap().get(userName).containsKey(PROPERTY_AUTHOR_DETAIL))
                getDataMap().get(userName).put(PROPERTY_AUTHOR_DETAIL, new ReportByAuthorDetail());

            ((ReportByAuthorDetail) getDataMap().get(userName).get(PROPERTY_AUTHOR_DETAIL)).addAuthorDetailItem(node);


             /*setting the page author detail*/
            if (!getDataMap().get(userName).containsKey(PROPERTY_PAGE_DETAIL))
                getDataMap().get(userName).put(PROPERTY_PAGE_DETAIL, new ReportByAuthorPageDetail());

            ((ReportByAuthorPageDetail) getDataMap().get(userName).get(PROPERTY_PAGE_DETAIL)).addAuthorPageDetailItem(node);


            setTotalItems(getTotalItems() + 1);
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

        ReportByAuthorDetail authorDetail;
        JSONObject jsonObjectItemAuthorDetail;
        JSONArray jsonArrayItemAuthorDetail;
        JSONObject jsonObjectSubItemAuthorDetail;

        ReportByAuthorPageDetail authorPageDetail;
        JSONObject jsonObjectItemPageDetail;
        JSONArray jsonArrayItemPageDetail;
        JSONObject jsonObjectSubItemPageDetail;

        for (String key : dataMap.keySet()) {
            jsonObjectItem = new JSONObject();
            jsonObjectItem.put("user", key);
            jsonObjectItem.put("itemCount", dataMap.get(key).get(PROPERTY_COUNT));
            jsonObjectItem.put("percentaje", (Float.parseFloat(((Integer) dataMap.get(key).get(PROPERTY_COUNT) * 100) + "") / totalItems));

            /* part of author detail */
            authorDetail = (ReportByAuthorDetail) dataMap.get(key).get(PROPERTY_AUTHOR_DETAIL);
            jsonObjectItemAuthorDetail = new JSONObject();
            jsonObjectItemAuthorDetail.put("totalCount", authorDetail.getTotalAuthorDetailItems());

            jsonArrayItemAuthorDetail = new JSONArray();
            for (String keyAuthorDetail : authorDetail.getDetailAuthorMap().keySet()) {
                jsonObjectSubItemAuthorDetail = new JSONObject();
                jsonObjectSubItemAuthorDetail.put("type", keyAuthorDetail);
                jsonObjectSubItemAuthorDetail.put("typeName", (keyAuthorDetail.split(":")[1]));
                jsonObjectSubItemAuthorDetail.put("itemCount", authorDetail.getDetailAuthorMap().get(keyAuthorDetail));
                jsonObjectSubItemAuthorDetail.put("percentaje", (Float.parseFloat((authorDetail.getDetailAuthorMap().get(keyAuthorDetail) * 100) + "") / authorDetail.getTotalAuthorDetailItems()));
                jsonArrayItemAuthorDetail.put(jsonObjectSubItemAuthorDetail);
            }
            jsonObjectItemAuthorDetail.put("items", jsonArrayItemAuthorDetail);
            jsonObjectItem.put("itemAuthorDetails", jsonObjectItemAuthorDetail);

            /* part of page details */
            authorPageDetail = (ReportByAuthorPageDetail) dataMap.get(key).get(PROPERTY_PAGE_DETAIL);
            jsonObjectItemPageDetail = new JSONObject();
            jsonArrayItemPageDetail = new JSONArray();
            for (String keyPageDetail : authorPageDetail.getDetailAuthorPageMap().keySet()) {
                jsonObjectSubItemPageDetail = new JSONObject();
                jsonObjectSubItemPageDetail.put("title", keyPageDetail);
                for (String keyPageDetailItem : authorPageDetail.getDetailAuthorPageMap().get(keyPageDetail).keySet()) {
                    jsonObjectSubItemPageDetail.put(keyPageDetailItem, authorPageDetail.getDetailAuthorPageMap().get(keyPageDetail).get(keyPageDetailItem));
                }
                jsonArrayItemPageDetail.put(jsonObjectSubItemPageDetail);
            }
            jsonObjectItemPageDetail.put("items", jsonArrayItemPageDetail);
            jsonObjectItem.put("itemAuthorPageDetails", jsonObjectItemPageDetail);


            /* setting each item to the json object */
            jArray.put(jsonObjectItem);
        }

        jsonObject.put("reportType", reportType.name());
        jsonObject.put("totalItems", totalItems);
        jsonObject.put("items", jArray);

        return jsonObject;
    }

    /**
     * getTotalItems
     *
     * @return {@link Integer}
     */
    public Integer getTotalItems() {
        return totalItems;
    }

    /**
     * setTotalItems
     *
     * @param totalItems {@link Integer}
     */
    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
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
     * ReportByAuthorDetail Class.
     */
    public class ReportByAuthorDetail {

        private Integer totalAuthorDetailItems;
        private Map<String, Integer> detailAuthorMap;

        /* The constructor for the class. */
        public ReportByAuthorDetail() {
            this.setTotalAuthorDetailItems(0);
            this.setDetailAuthorMap(new HashMap<String, Integer>());
        }

        /**
         * addAuthorDetailItem
         * <p>add detail item for specific author.</p>
         *
         * @param detailNode {@link JCRNodeWrapper}
         * @throws RepositoryException
         */
        public void addAuthorDetailItem(JCRNodeWrapper detailNode) throws RepositoryException {
            String primaryNodeTypeName = detailNode.getPrimaryNodeTypeName();

            /*setting the counter*/
            if (getDetailAuthorMap().containsKey(primaryNodeTypeName)) {
                getDetailAuthorMap().put(primaryNodeTypeName, getDetailAuthorMap().get(primaryNodeTypeName) + 1);
            } else {
                getDetailAuthorMap().put(primaryNodeTypeName, 1);
            }
            setTotalAuthorDetailItems(getTotalAuthorDetailItems() + 1);
        }

        /**
         * getTotalAuthorDetailItems
         *
         * @return {@link Integer}
         */
        public Integer getTotalAuthorDetailItems() {
            return totalAuthorDetailItems;
        }

        /**
         * setTotalAuthorDetailItems
         *
         * @param totalAuthorDetailItems {@link Integer}
         */
        public void setTotalAuthorDetailItems(Integer totalAuthorDetailItems) {
            this.totalAuthorDetailItems = totalAuthorDetailItems;
        }

        /**
         * getDetailAuthorMap
         *
         * @return {@link Map}
         */
        public Map<String, Integer> getDetailAuthorMap() {
            return detailAuthorMap;
        }

        /**
         * setDetailAuthorMap
         *
         * @param detailAuthorMap {@link Map}
         */
        public void setDetailAuthorMap(Map<String, Integer> detailAuthorMap) {
            this.detailAuthorMap = detailAuthorMap;
        }
    }

    /**
     * ReportByAuthorPageDetail Class
     */
    public class ReportByAuthorPageDetail {

        private Integer totalAuthorPageItems;
        private Map<String, Map<String, String>> detailAuthorPageMap;

        /* the constructor for the class */
        public ReportByAuthorPageDetail() {
            this.setTotalAuthorPageItems(0);
            this.setDetailAuthorPageMap(new HashMap<String, Map<String, String>>());
        }

        /**
         * addAuthorPageDetailItem
         * <p>add detail item for specific author page item.</p>
         *
         * @param detailNode {@link JCRNodeWrapper}
         * @throws RepositoryException
         */
        public void addAuthorPageDetailItem(JCRNodeWrapper detailNode) throws RepositoryException {
            String nodeTitle = detailNode.getName();

            /*setting the key if not exists*/
            if (!getDetailAuthorPageMap().containsKey(nodeTitle))
                getDetailAuthorPageMap().put(nodeTitle, new HashedMap());

            /*setting the data from the node*/
            getDetailAuthorPageMap().get(nodeTitle).put("jcrtitle", detailNode.hasProperty("jcr:title") ? detailNode.getPropertyAsString("jcr:title") : nodeTitle);
            getDetailAuthorPageMap().get(nodeTitle).put("type", detailNode.getPrimaryNodeTypeName());
            getDetailAuthorPageMap().get(nodeTitle).put("typeName", detailNode.getPrimaryNodeTypeName().split(":")[1]);
            getDetailAuthorPageMap().get(nodeTitle).put("created", detailNode.hasProperty("jcr:created") ? detailNode.getPropertyAsString("jcr:created") : "");
            getDetailAuthorPageMap().get(nodeTitle).put("modified", detailNode.hasProperty("jcr:lastModified") ? detailNode.getPropertyAsString("jcr:lastModified") : "");
            getDetailAuthorPageMap().get(nodeTitle).put("published", detailNode.hasProperty("j:published") ? detailNode.getPropertyAsString("j:published") : "false");
            getDetailAuthorPageMap().get(nodeTitle).put("locked", detailNode.hasProperty("j:locktoken") ? "true" : "false");
        }

        /**
         * getTotalAuthorPageItems
         *
         * @return {@link Integer}
         */
        public Integer getTotalAuthorPageItems() {
            return totalAuthorPageItems;
        }

        /**
         * setTotalAuthorPageItems
         *
         * @param totalAuthorPageItems {@link Integer}
         */
        public void setTotalAuthorPageItems(Integer totalAuthorPageItems) {
            this.totalAuthorPageItems = totalAuthorPageItems;
        }

        /**
         * getDetailAuthorPageMap
         *
         * @return {@link Map}
         */
        public Map<String, Map<String, String>> getDetailAuthorPageMap() {
            return detailAuthorPageMap;
        }

        /**
         * setDetailAuthorPageMap
         *
         * @param detailAuthorPageMap {@link Map}
         */
        public void setDetailAuthorPageMap(Map<String, Map<String, String>> detailAuthorPageMap) {
            this.detailAuthorPageMap = detailAuthorPageMap;
        }
    }


}
