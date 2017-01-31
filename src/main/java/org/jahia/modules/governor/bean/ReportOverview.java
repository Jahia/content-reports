package org.jahia.modules.governor.bean;

import org.jahia.data.templates.JahiaTemplatesPackage;
import org.jahia.exceptions.JahiaException;
import org.jahia.registries.ServicesRegistry;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.query.QueryWrapper;
import org.jahia.services.templates.JahiaTemplateManagerService;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The ReportOverview Class
 *
 * Created by Juan Carlos Rodas.
 */
public class ReportOverview extends BaseReport {
    private static Logger logger = LoggerFactory.getLogger(ReportOverview.class);
    protected static final String BUNDLE = "resources.content-governor";

    protected DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private Integer pagesNumber;
    private Integer templatesNumber;
    private Integer usersNumber;


    /**
     * Instantiates a new Report overview.
     *
     * @param siteNode the site node {@link JCRSiteNode}
     */
    public ReportOverview(JCRSiteNode siteNode) {
        super(siteNode);

        this.pagesNumber     = 0;
        this.templatesNumber = 0;
        this.usersNumber     = 0;
    }

    @Override
    public void execute(JCRSessionWrapper session, int offset, int limit) throws RepositoryException, JSONException, JahiaException {
        /* getting the templates for site */
        JahiaTemplateManagerService templateService = ServicesRegistry.getInstance().getJahiaTemplateManagerService();
        List<JahiaTemplatesPackage>  tpack = templateService.getInstalledModulesForSite(siteNode.getSiteKey(), true, true, false);
        this.templatesNumber = tpack.size();

        /* getting the users for site */
        JahiaUserManagerService userService = ServicesRegistry.getInstance().getJahiaUserManagerService();
        List<String> uList = userService.getUserList(siteNode.getSiteKey());
        this.usersNumber = uList.size();

        String pageQueryStr = "SELECT [rep:count(item,skipChecks=1)] FROM [jnt:page] AS item WHERE ISDESCENDANTNODE(item,['" + siteNode.getPath() + "'])";
        QueryWrapper q = session.getWorkspace().getQueryManager().createQuery(pageQueryStr, Query.JCR_SQL2);
        this.pagesNumber = (int) q.execute().getRows().nextRow().getValue("count").getLong();
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
