package org.jahia.modules.governor.bean;

import org.jahia.services.content.decorator.JCRSiteNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * ReportPagesWithoutTitle Class.
 *
 * Created by Juan Carlos Rodas.
 */
public class ReportPagesWithoutTitle extends I18NPropertyReport {
    private static Logger logger = LoggerFactory.getLogger(ReportPagesWithoutTitle.class);
    protected static final String BUNDLE = "resources.content-governor";

    /**
     * Instantiates a new Report pages without title.
     *
     * @param siteNode the site node {@link JCRSiteNode}
     */
    public ReportPagesWithoutTitle(JCRSiteNode siteNode, String language) {
        super(siteNode, language, "jnt:page", "jcr:title", "mix:title");
        this.dataMap = new HashMap<>();
    }


}
