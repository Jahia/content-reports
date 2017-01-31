package org.jahia.modules.governor.bean;

import org.jahia.services.content.decorator.JCRSiteNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * The ReportPagesWithoutDescription Class.
 * <p>
 * Created by Juan Carlos Rodas.
 */
public class ReportPagesWithoutDescription extends I18NPropertyReport {
    private static Logger logger = LoggerFactory.getLogger(ReportPagesWithoutDescription.class);
    protected static final String BUNDLE = "resources.content-governor";

    /**
     * Instantiates a new Report pages without title.
     *
     * @param siteNode the site node {@link JCRSiteNode}
     */
    public ReportPagesWithoutDescription(JCRSiteNode siteNode, String language) {
        super(siteNode, language, "jnt:page","jcr:description", "mix:title");
        this.dataMap = new HashMap<>();
    }
}
