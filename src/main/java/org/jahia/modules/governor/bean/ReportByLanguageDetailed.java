package org.jahia.modules.governor.bean;

import org.jahia.api.Constants;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jcr.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ReportByLanguageDetailed Class.
 *
 * Created by Juan Carlos Rodas.
 */
public class ReportByLanguageDetailed implements IReport {

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private JCRSiteNode siteNode;
    private List<Map<String, String>> listMap;
    private Map<String, String> itemMap;
    private NodeLangInformation langInformation;
    private String language;
    private static Logger logger = LoggerFactory.getLogger(ReportByLanguageDetailed.class);

    /**
     * The constructor for the class.
     *
     * @param siteNode {@link JCRSiteNode}
     * @param language {@link String}
     */
    public ReportByLanguageDetailed(JCRSiteNode siteNode, String language) {
        this.siteNode = siteNode;
        this.listMap = new ArrayList<>();
        this.language = language;
    }

    /**
     * addItem
     *
     * @param node {@link JCRNodeWrapper}
     * @param contentType {@link SEARCH_CONTENT_TYPE}
     * @throws RepositoryException
     */
    public void addItem(JCRNodeWrapper node, SEARCH_CONTENT_TYPE contentType) throws RepositoryException {
        this.langInformation = getLanguageInformation(node);

        /* adding the item information */
        this.itemMap = new HashMap<>();
        this.itemMap.put("title", node.hasProperty("jcr:title") ? node.getPropertyAsString("jcr:title") : "");
        this.itemMap.put("displayableName", node.getDisplayableName());
        this.itemMap.put("name", node.getName());
        this.itemMap.put("displayTitle", node.hasProperty("jcr:title") ? node.getPropertyAsString("jcr:title") : node.getDisplayableName());
        this.itemMap.put("primaryNodeTypeAlias", node.getPrimaryNodeType().getAlias());
        this.itemMap.put("primaryNodeTypeLocalName", node.getPrimaryNodeType().getLocalName());
        this.itemMap.put("primaryNodeTypeName", node.getPrimaryNodeType().getName());
        this.itemMap.put("primaryNodeTypeItemName", node.getPrimaryNodeType().getPrimaryItemName());
        this.itemMap.put("primaryNodeTypePrefix", node.getPrimaryNodeType().getPrefix());
        this.itemMap.put("type", node.getPrimaryNodeTypeName().split(":")[1]);
        this.itemMap.put("path", node.getPath());
        this.itemMap.put("identifier", node.getIdentifier());

        /* language data */
        this.itemMap.put("languages",this.langInformation.getLanguagesAsString());
        this.itemMap.put("uniqueLang",this.langInformation.isUniqueLang());
        this.itemMap.put("publishable", siteNode.getInactiveLiveLanguages().contains(language) ? "true" : "false");

        /* adding the lang information if exists. */
        if(this.langInformation.getNode() != null) {
            this.itemMap.put("created", dateFormat.format(this.langInformation.getNode().hasProperty(Constants.JCR_CREATED) ? this.langInformation.getNode().getProperty(Constants.JCR_CREATED).getDate().getTime() : node.getCreationDateAsDate()));
            this.itemMap.put("lastModified", dateFormat.format(this.langInformation.getNode().getProperty(Constants.JCR_LASTMODIFIED).getDate().getTime()));
            this.itemMap.put("lastModifiedBy", this.langInformation.getNode().getProperty(Constants.JCR_LASTMODIFIEDBY).getString());
            this.itemMap.put("published", this.langInformation.getNode().hasProperty("j:published") ? "true" : "false");
            this.itemMap.put("lock", this.langInformation.getNode().isLocked() ? "true" : "false");
            this.itemMap.put("language", this.langInformation.getNode().getProperty(Constants.JCR_LANGUAGE).getString());
        }

        /* adding the node lang information to map */
        this.listMap.add(itemMap);
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

        /* get all items from list */
        for (Map<String, String> itemMap : this.listMap) {
            jsonObjectItem = new JSONObject();

            /* adding each item to json  */
            for (String keyMap : itemMap.keySet())
                jsonObjectItem.put(keyMap, itemMap.get(keyMap));

            jArray.put(jsonObjectItem);
        }

        jsonObject.put("language", language);
        jsonObject.put("items", jArray);
        return jsonObject;
    }

    /**
     * getLanguageInformation
     * <p>returns the NodeLangInformation object if exists.</p>
     * @param parentNode
     * @return
     * @throws RepositoryException
     */
    private NodeLangInformation getLanguageInformation(JCRNodeWrapper parentNode) throws RepositoryException {
        NodeLangInformation langInformation = new NodeLangInformation();
        NodeIterator ni = parentNode.getI18Ns();

        while (ni.hasNext()){
            Node translationNode = ni.nextNode();
            langInformation.getLanguages().add(translationNode.getProperty("jcr:language").getString());

            if (translationNode.getProperty("jcr:language").getString().equalsIgnoreCase(this.language))
                langInformation.setNode(translationNode);
        }

        return langInformation;
    }

    /**
     * the NodeLangInformation class.
     */
    class NodeLangInformation{

        private List<String> languageList;
        private Node node;

        /* the constructor for the class. */
        public NodeLangInformation(){
            this.languageList =  new ArrayList<>();
        }

        /**
         * getLanguagesAsString
         * <p>returns all the languages for some node as string.</p>
         *
         * @return
         */
        public String getLanguagesAsString(){
            String langString = "";
            for (String lang: languageList) {
                if(!langString.equals("")) langString += ",";
                langString += lang;
            }
            return langString;

        }

        /**
         * isUniqueLang
         * <p> return true if is unique language, otherwise return false.</p>
         *
         * @return {@link String}
         */
        public String isUniqueLang(){
            return languageList.size() == 1 ? "true" : "false";
        }


        /**
         * Getter for property 'languages'.
         *
         * @return Value for property 'languages'.
         */
        public List<String> getLanguages() {
            return this.languageList;
        }

        /**
         * Setter for property 'languages'.
         *
         * @param languages Value to set for property 'languages'.
         */
        public void setLanguages(List<String> languages) {
            this.languageList = languages;
        }

        /**
         * Getter for property 'node'.
         *
         * @return Value for property 'node'.
         */
        public Node getNode() {
            return node;
        }

        /**
         * Setter for property 'node'.
         *
         * @param node Value to set for property 'node'.
         */
        public void setNode(Node node) {
            this.node = node;
        }
    }

}


