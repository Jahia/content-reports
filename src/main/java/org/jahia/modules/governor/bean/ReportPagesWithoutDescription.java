package org.jahia.modules.governor.bean;

import org.apache.commons.lang.StringUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * The ReportPagesWithoutDescription Class.
 * <p>
 * Created by Juan Carlos Rodas.
 */
public class ReportPagesWithoutDescription extends QueryReport {
    private static Logger logger = LoggerFactory.getLogger(ReportPagesWithoutDescription.class);
    protected static final String BUNDLE = "resources.content-governor";

    protected Map<String, Map<String, Object>> dataMap;


    /**
     * Instantiates a new Report pages without title.
     *
     * @param siteNode the site node {@link JCRSiteNode}
     */
    public ReportPagesWithoutDescription(JCRSiteNode siteNode) {
        super(siteNode);
        this.dataMap = new HashMap<>();
    }

    @Override
    public void execute(JCRSessionWrapper session, int offset, int limit) throws RepositoryException, JSONException {
        fillReport(session, "SELECT * FROM [jnt:page] AS item WHERE ISDESCENDANTNODE(item,['" + siteNode.getPath() + "'])", offset, limit);
    }

    /**
     * addItem
     *
     * @param node {@link JCRNodeWrapper}
     * @throws RepositoryException
     */
    public void addItem(JCRNodeWrapper node) throws RepositoryException {
        Map<String, String> translationsMap = new HashMap<>();
        Integer noTitleCounter = 0;
        for (String lang : this.localeMap.keySet()) {
            translationsMap.put(lang, getTranslationContent(node, lang));
            if (StringUtils.isEmpty(translationsMap.get(lang))) {
                noTitleCounter++;
            }
        }

        // if pages translation without title
        if (noTitleCounter > 0) {
            this.dataMap.put(node.getPath(), new HashMap<String, Object>());
            this.dataMap.get(node.getPath()).put("url", node.getUrl());
            this.dataMap.get(node.getPath()).put("name", node.getName());
            this.dataMap.get(node.getPath()).put("displayableName", node.getDisplayableName());
            this.dataMap.get(node.getPath()).put("title", (node.hasI18N(this.defaultLocale) && node.getI18N(this.defaultLocale).hasProperty("jcr:title")) ? node.getI18N(this.defaultLocale).getProperty("jcr:title").getString() : "");
            this.dataMap.get(node.getPath()).put("translations", translationsMap);
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

        for (String path : this.dataMap.keySet()) {
            jsonObjectItem = new JSONObject();
            jArray2 = new JSONArray();
            jsonObjectItem.put("nodePath", path);
            jsonObjectItem.put("nodeUrl", (String) this.dataMap.get(path).get("url"));
            jsonObjectItem.put("nodeName", (String) this.dataMap.get(path).get("name"));
            jsonObjectItem.put("nodeDisplayableName", (String) this.dataMap.get(path).get("displayableName"));
            jsonObjectItem.put("nodeTitle", (String) this.dataMap.get(path).get("title"));
            for (String langKey : ((HashMap<String, String>) this.dataMap.get(path).get("translations")).keySet()) {
                jsonObjectItem2 = new JSONObject();
                jsonObjectItem2.put("value", ((HashMap<String, String>) this.dataMap.get(path).get("translations")).get(langKey));
                jsonObjectItem2.put("lang", langKey);
                jArray2.put(jsonObjectItem2);
            }
            jsonObjectItem.put("translations", jArray2);
            jArray.put(jsonObjectItem);
        }

        jsonObject.put("siteName", siteNode.getName());
        jsonObject.put("siteDisplayableName", siteNode.getDisplayableName());
        jsonObject.put("items", jArray);
        return jsonObject;
    }

    /**
     * getTranslationContent
     * <p> get the content from jcr:description
     * if exists in the specific lang. </p>
     *
     * @param parentNode {@link JCRNodeWrapper}
     * @param localeLang {@link String}
     * @return nodeDescription {@link String}
     * @throws RepositoryException
     */
    private String getTranslationContent(JCRNodeWrapper parentNode, String localeLang) throws RepositoryException {
        NodeIterator ni = parentNode.getI18Ns();
        while (ni.hasNext()) {
            Node translationNode = ni.nextNode();

            if (translationNode.getProperty("jcr:language").getString().equalsIgnoreCase(localeLang) &&
                    translationNode.hasProperty("jcr:description")) {
                return translationNode.getProperty("jcr:description").getString();
            }
        }
        return "";
    }


}
