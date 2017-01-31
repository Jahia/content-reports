package org.jahia.modules.governor.bean;

import org.apache.commons.lang.StringUtils;
import org.jahia.exceptions.JahiaException;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * BaseReport Interface.
 * Created by Juan Carlos Rodas.
 */
public abstract class BaseReport {
    /* the logger fot the class */
    private static Logger logger = LoggerFactory.getLogger(BaseReport.class);

    public static final boolean SORT_ASC = true;
    public static final  boolean SORT_DESC = false;

    /**
     *  the enum SEARCH_DATE_TYPE
     */
    public enum SEARCH_DATE_TYPE{ALL, BEFORE_DATE}

    /**
     * the enum SearchActionType
     */
    public enum SearchActionType {UPDATE, CREATION}

    /**
     * the enum SearchContentType
     */
    public enum SearchContentType {PAGE, CONTENT}

    protected JCRSiteNode siteNode;

    protected DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    protected Locale locale = LocaleContextHolder.getLocale();
    protected Locale defaultLocale;
    protected Map<String, Locale> localeMap;

    public BaseReport(JCRSiteNode siteNode) {
        this.siteNode = siteNode;

        this.localeMap = new HashMap<>();

        for (Locale ilocale : siteNode.getLanguagesAsLocales())
            this.localeMap.put(ilocale.toString(), locale);

        this.defaultLocale = this.localeMap.get(siteNode.getDefaultLanguage());
    }

    public abstract void execute(JCRSessionWrapper session, int offset, int limit) throws RepositoryException, JSONException, JahiaException;

    /**
     * getJson
     * <p>get json to the iReport class,
     * custom implementation in each child class,
     * return a specific json for each report.</p>
     *
     * @return {@link JSONObject}
     * @throws JSONException
     * @throws RepositoryException
     */
    public abstract JSONObject getJson() throws JSONException, RepositoryException;

}
