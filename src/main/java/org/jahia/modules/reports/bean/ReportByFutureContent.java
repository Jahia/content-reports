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
import org.jahia.modules.reports.service.FutureConditionService;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;

import javax.jcr.RepositoryException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The ReportByFutureContent class
 *
 * @author nonico
 */
public class ReportByFutureContent extends ReportByContentVisibility {

    private ConditionService conditionService;

    public ReportByFutureContent(JCRSiteNode siteNode, String searchPath) {
        super(siteNode, searchPath);
        conditionService = new FutureConditionService();
    }

    @Override public void addItem(JCRNodeWrapper node) throws RepositoryException {
        Map<String, String> futureConditions = conditionService.getConditions(node);
        if (futureConditions.size() == 1) {
            Map<String, String> map = new HashMap<>();
            map.put("name", node.getName());
            map.put("parentPath", node.getParent().getPath());
            map.put("path", node.getPath());
            map.put("type", String.join("<br/>", node.getNodeTypes()));
            map.put("liveDate", futureConditions.values().stream().iterator().next());
            this.dataList.add(map);
        }
    }
}
