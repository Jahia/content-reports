package org.jahia.modules.governor.bean;

import org.apache.commons.collections.map.HashedMap;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import javax.jcr.RepositoryException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * ReportPagesWithoutKeyword Class.
 *
 * Created by Juan Carlos Rodas.
 */
public class ReportPagesWithoutKeyword extends QueryReport {
    private static Logger logger = LoggerFactory.getLogger(ReportPagesWithoutKeyword.class);
    private static final String BUNDLE = "resources.content-governor";

    protected Map<String, Map<String, Object>> dataMap;

    /**
     * Instantiates a new Report pages without keyword.
     *
     * @param siteNode the site node {@link JCRSiteNode}
     */
    public ReportPagesWithoutKeyword(JCRSiteNode siteNode) {
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
        // if pages translation without title
        if (isWithoutKeywordsContent(node)) {
            this.dataMap.put(node.getPath(), new HashedMap());
            this.dataMap.get(node.getPath()).put("nodePath", node.getPath());
            this.dataMap.get(node.getPath()).put("nodeUrl", node.getUrl());
            this.dataMap.get(node.getPath()).put("nodeName", node.getName());
            this.dataMap.get(node.getPath()).put("nodeDisplayableName", node.getDisplayableName());
            this.dataMap.get(node.getPath()).put("nodeTitle", (node.hasI18N(this.defaultLocale) && node.getI18N(this.defaultLocale).hasProperty("jcr:title")) ? node.getI18N(this.defaultLocale).getProperty("jcr:title").getString() : "");
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

        for (String path : this.dataMap.keySet()) {
            jArray.put(new JSONObject(this.dataMap.get(path)));
        }

        jsonObject.put("siteName", siteNode.getName());
        jsonObject.put("siteDisplayableName", siteNode.getDisplayableName());
        jsonObject.put("items", jArray);
        return jsonObject;
    }

    /**
     * isWithoutKeywordsContent
     * <p> check if the node contains the
     * key j:keyword in the metadata content.</p>
     *
     * @param parentNode {@link JCRNodeWrapper}
     * @return boolean {@link Boolean}
     * @throws RepositoryException
     */
    private Boolean isWithoutKeywordsContent(JCRNodeWrapper parentNode) throws RepositoryException {
        if(parentNode.isNodeType("jmix:keywords") &&  parentNode.hasProperty("j:keywords"))
            return Boolean.FALSE;

        return Boolean.TRUE;
    }

}
