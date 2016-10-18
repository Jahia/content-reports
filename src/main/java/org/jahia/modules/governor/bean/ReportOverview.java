package org.jahia.modules.governor.bean;

import org.jahia.data.templates.JahiaTemplatesPackage;
import org.jahia.exceptions.JahiaException;
import org.jahia.registries.ServicesRegistry;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.templates.JahiaTemplateManagerService;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jcr.RepositoryException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The ReportOverview Class
 *
 * Created by Juan Carlos Rodas.
 */
public class ReportOverview implements IReport {

    protected DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    protected static final String BUNDLE = "resources.content-governor";
    private static Logger logger = LoggerFactory.getLogger(ReportOverview.class);
    protected JCRSiteNode siteNode;
    private Integer pagesNumber;
    private Integer templatesNumber;
    private Integer usersNumber;


    /**
     * Instantiates a new Report overview.
     *
     * @param siteNode the site node {@link JCRSiteNode}
     */
    public ReportOverview(JCRSiteNode siteNode) throws RepositoryException, JahiaException {
        this.pagesNumber     = 0;
        this.templatesNumber = 0;
        this.usersNumber     = 0;
        this.siteNode  = siteNode;

        /* getting the templates for site */
        JahiaTemplateManagerService templateService = ServicesRegistry.getInstance().getJahiaTemplateManagerService();
        List<JahiaTemplatesPackage>  tpack = templateService.getInstalledModulesForSite(siteNode.getSiteKey(), true, true, false);
        this.templatesNumber = tpack.size();

        /* getting the users for site */
        JahiaUserManagerService userService = ServicesRegistry.getInstance().getJahiaUserManagerService();
        List<String> uList = userService.getUserList(siteNode.getSiteKey());
        this.usersNumber = uList.size();
    }

    /**
     * addItem
     *
     * @param node {@link JCRNodeWrapper}
     * @param contentType {@link SEARCH_CONTENT_TYPE}
     * @throws RepositoryException
     */
    public void addItem(JCRNodeWrapper node, SEARCH_CONTENT_TYPE contentType) throws RepositoryException {
        pagesNumber++;
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
        jsonObject.put("siteName", siteNode.getName());
        jsonObject.put("siteDisplayableName", siteNode.getDisplayableName());
        jsonObject.put("nbPages", pagesNumber);
        jsonObject.put("nbTemplates", templatesNumber);
        jsonObject.put("nbUsers", usersNumber);
        return jsonObject;
    }

}
