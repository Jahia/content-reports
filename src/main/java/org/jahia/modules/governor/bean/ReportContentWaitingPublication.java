package org.jahia.modules.governor.bean;

import org.jahia.services.content.JCRContentUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.workflow.Workflow;
import org.jahia.services.workflow.WorkflowService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import javax.jcr.RepositoryException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The ReportContentWaitingPublication Class.
 *
 * Created by Juan Carlos Rodas.
 */
public class ReportContentWaitingPublication implements IReport {

    protected DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    protected static final String BUNDLE = "resources.content-governor";
    protected Locale locale = LocaleContextHolder.getLocale();
    protected Locale defaultLocale;
    private static Logger logger = LoggerFactory.getLogger(ReportContentWaitingPublication.class);
    protected JCRSiteNode siteNode;
    List<WaitingPublicationElement> dataList;
    protected Map<String, Locale> localeMap;


    /**
     * Instantiates a new Report pages without title.
     *
     * @param siteNode the site node {@link JCRSiteNode}
     */
    public ReportContentWaitingPublication(JCRSiteNode siteNode) throws RepositoryException {
        this.siteNode  = siteNode;
        this.localeMap = new HashMap<>();
        this.dataList  = new ArrayList<>();

        for (Locale ilocale : siteNode.getLanguagesAsLocales())
            this.localeMap.put(ilocale.toString(), locale);

        this.defaultLocale = this.localeMap.get(siteNode.getDefaultLanguage());
    }

    /**
     * hasActiveWorkflows
     * <p> returns true if the node have some active workflow,
     * otherwise return false.</p>
     *
     * @param node {@link JCRNodeWrapper}
     * @return {@link Boolean}
     */
    private Boolean hasActiveWorkflows(JCRNodeWrapper node){
        for (String lang : localeMap.keySet()) {
            List<Workflow> wlist = WorkflowService.getInstance().getActiveWorkflows(node, localeMap.get(lang), localeMap.get(lang));
            if(wlist.size() > 0 ) return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * addItem
     *
     * @param node {@link JCRNodeWrapper}
     * @param contentType {@link SEARCH_CONTENT_TYPE}
     * @throws RepositoryException
     */
    public void addItem(JCRNodeWrapper node, SEARCH_CONTENT_TYPE contentType) throws RepositoryException {
        if(hasActiveWorkflows(node)){
            JCRNodeWrapper itemParentPage = node;
            if(!node.isNodeType("jnt:page")){
                itemParentPage = JCRContentUtils.getParentOfType(node, "jnt:page");
            }
            dataList.add(new WaitingPublicationElement(node, localeMap));
        }
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

        JSONArray jArray2;
        JSONObject jsonObjectItem2;

        for (WaitingPublicationElement element : this.dataList) {
            jsonObjectItem = new JSONObject();
            jArray2 = new JSONArray();
            jsonObjectItem.put("nodeName", element.getName());
            jsonObjectItem.put("nodeUrl", element.getUrl());
            jsonObjectItem.put("nodePath", element.getPath());
            jsonObjectItem.put("nodeTechName", element.getTechName());
            jsonObjectItem.put("nodeTitle", element.getTitle());
            jsonObjectItem.put("nodeType", element.getType());
            for (String key: element.getElementMap().keySet()) {
                jsonObjectItem2 = new JSONObject();
                jsonObjectItem2.put("lang", key);

                for (String key2: element.getElementMap().get(key).keySet()) {
                    jsonObjectItem2.put(key2, element.getElementMap().get(key).get(key2));
                }
                jArray2.put(jsonObjectItem2);

            }
            jsonObjectItem.put("locales", jArray2);
            jArray.put(jsonObjectItem);
        }

        jsonObject.put("siteName", siteNode.getName());
        jsonObject.put("siteDisplayableName", siteNode.getDisplayableName());
        jsonObject.put("items", jArray);
        return jsonObject;
    }

    /* WaitingPublicationElement Class.*/
    class WaitingPublicationElement{

        private String path;
        private String url;
        private String title;
        private String name;
        private String type;
        private String techName;
        private Map<String, Map<String, String>> elementMap;
        private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


        /* the class constructor. */
        WaitingPublicationElement(JCRNodeWrapper node, Map<String, Locale> localeMap) throws RepositoryException {
            this.elementMap = new HashMap();
            this.path  = node.getPath() ;
            this.url   = node.getUrl();
            this.title = (node.hasI18N(defaultLocale) && node.getI18N(defaultLocale).hasProperty("jcr:title")) ? node.getI18N(defaultLocale).getProperty("jcr:title").getString() : node.getName();
            this.name  = node.getName();
            this.type  = node.getPrimaryNodeTypeName().split(":")[1];
            this.techName = node.getPrimaryNodeTypeName();

            for (String lang : localeMap.keySet()) {
                List<Workflow> wfList = WorkflowService.getInstance().getActiveWorkflows(node, localeMap.get(lang), localeMap.get(lang));
                this.elementMap.put(lang, new HashMap());
                this.elementMap.get(lang).put("wfStarted", getWorkflowData( wfList, "startTime"));
                this.elementMap.get(lang).put("wfName", getWorkflowData( wfList, "name"));
                this.elementMap.get(lang).put("wfDName", getWorkflowData( wfList, "displayableName"));
                this.elementMap.get(lang).put("wfStartUser", getWorkflowData( wfList, "startUser"));
                this.elementMap.get(lang).put("wfProvider", getWorkflowData( wfList, "provider"));
                this.elementMap.get(lang).put("wfComments", getWorkflowData( wfList, "comments"));
            }
        }

        /**
         * getWorkflowData
         * <p>return the information by workflow as string.</p>
         *
         * @param wList
         * @param field
         * @return
         */
        private String getWorkflowData(List<Workflow> wList, String field){
            StringBuilder sb = new StringBuilder();

            if(wList != null) {
                for (Workflow wf : wList) {
                    if (sb.length() > 0) { sb.append(","); }
                    if(field.equalsIgnoreCase("startTime"))
                        sb.append(dateFormat.format(wf.getStartTime()));
                    if(field.equalsIgnoreCase("name"))
                        sb.append(wf.getName());
                    if(field.equalsIgnoreCase("displayableName"))
                        sb.append(wf.getDisplayName());
                    if(field.equalsIgnoreCase("startUser"))
                        sb.append(wf.getStartUser());
                    if(field.equalsIgnoreCase("provider"))
                        sb.append(wf.getProvider());
                    if(field.equalsIgnoreCase("comments"))
                        sb.append(wf.getComments());
                    break;
                }
            }

            return sb.toString();
        }

        /**
         * Getter for property 'path'.
         *
         * @return Value for property 'path'.
         */
        public String getPath() {
            return path;
        }

        /**
         * Setter for property 'path'.
         *
         * @param path Value to set for property 'path'.
         */
        public void setPath(String path) {
            this.path = path;
        }

        /**
         * Getter for property 'url'.
         *
         * @return Value for property 'url'.
         */
        public String getUrl() {
            return url;
        }

        /**
         * Setter for property 'url'.
         *
         * @param url Value to set for property 'url'.
         */
        public void setUrl(String url) {
            this.url = url;
        }

        /**
         * Getter for property 'title'.
         *
         * @return Value for property 'title'.
         */
        public String getTitle() {
            return title;
        }

        /**
         * Setter for property 'title'.
         *
         * @param title Value to set for property 'title'.
         */
        public void setTitle(String title) {
            this.title = title;
        }

        /**
         * Getter for property 'name'.
         *
         * @return Value for property 'name'.
         */
        public String getName() {
            return name;
        }

        /**
         * Setter for property 'name'.
         *
         * @param name Value to set for property 'name'.
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Getter for property 'type'.
         *
         * @return Value for property 'type'.
         */
        public String getType() {
            return type;
        }

        /**
         * Setter for property 'type'.
         *
         * @param type Value to set for property 'type'.
         */
        public void setType(String type) {
            this.type = type;
        }

        /**
         * Getter for property 'techName'.
         *
         * @return Value for property 'techName'.
         */
        public String getTechName() {
            return techName;
        }

        /**
         * Setter for property 'techName'.
         *
         * @param techName Value to set for property 'techName'.
         */
        public void setTechName(String techName) {
            this.techName = techName;
        }


        /**
         * Getter for property 'elementMap'.
         *
         * @return Value for property 'elementMap'.
         */
        public Map<String, Map<String, String>> getElementMap() {
            return elementMap;
        }

        /**
         * Setter for property 'elementMap'.
         *
         * @param elementMap Value to set for property 'elementMap'.
         */
        public void setElementMap(Map<String, Map<String, String>> elementMap) {
            this.elementMap = elementMap;
        }
    }

}