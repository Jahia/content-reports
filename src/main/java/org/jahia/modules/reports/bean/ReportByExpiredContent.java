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

import org.jahia.modules.reports.service.ConditionService;
import org.jahia.modules.reports.service.ExpiredConditionService;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.HashMap;
import java.util.Map;

/**
 * The ReportByExpiredContent class
 *
 * @author nonico
 */
public class ReportByExpiredContent extends ReportByContentVisibility {
    private static Logger logger = LoggerFactory.getLogger(ReportByExpiredContent.class);
    private ConditionService conditionService;


    public ReportByExpiredContent(JCRSiteNode siteNode, String searchPath) {
        super(siteNode, searchPath);
        this.conditionService = new ExpiredConditionService();
    }

    @Override public void addItem(JCRNodeWrapper node) throws RepositoryException {

        Map<String, String> expiredConditions = conditionService.getConditions(node);
        if (expiredConditions.size() == 1) {
            Map<String, String> map = new HashMap<>();
            map.put("name", node.getName());
            map.put("parentPath", node.getParent().getPath());
            map.put("path", node.getPath());
            map.put("type", String.join("<br/>", node.getNodeTypes()));
            map.put("expiresOn", expiredConditions.values().iterator().next());
            this.dataList.add(map);
        }
    }
}
