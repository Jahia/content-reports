/*
 * ==========================================================================================
 * =                            JAHIA'S ENTERPRISE DISTRIBUTION                             =
 * ==========================================================================================
 *
 *                                  http://www.jahia.com
 *
 * JAHIA'S ENTERPRISE DISTRIBUTIONS LICENSING - IMPORTANT INFORMATION
 * ==========================================================================================
 *
 *     Copyright (C) 2002-2020 Jahia Solutions Group. All rights reserved.
 *
 *     This file is part of a Jahia's Enterprise Distribution.
 *
 *     Jahia's Enterprise Distributions must be used in accordance with the terms
 *     contained in the Jahia Solutions Group Terms &amp; Conditions as well as
 *     the Jahia Sustainable Enterprise License (JSEL).
 *
 *     For questions regarding licensing, support, production usage...
 *     please contact our team at sales@jahia.com or go to http://www.jahia.com/license.
 *
 * ==========================================================================================
 */
package org.jahia.modules.reports.bean;

import org.jahia.exceptions.JahiaException;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;

/**
 * Short description of the class
 *
 * @author nonico
 */
public abstract class ReportByContentVisibility extends QueryReport {
    protected static Logger logger  = LoggerFactory.getLogger(ReportByContentVisibility.class);
    protected static final String BUNDLE = "resources.content-reports";
    protected String searchPath;

    public ReportByContentVisibility(JCRSiteNode siteNode, String searchPath) {
        super(siteNode);
        this.searchPath = searchPath;
    }

    @Override public void execute(JCRSessionWrapper session, int offset, int limit)
            throws RepositoryException, JSONException, JahiaException {
        logger.debug("Building jcr sql query");
        String query = "SELECT * FROM [jnt:content] AS parent \n"
                + "INNER JOIN [jnt:conditionalVisibility] as child ON ISCHILDNODE(child,parent) \n"
                + "WHERE ISDESCENDANTNODE(parent,['" + searchPath + "'])";
        logger.debug(query);
        fillReport(session, query, offset, limit);
    }
}
