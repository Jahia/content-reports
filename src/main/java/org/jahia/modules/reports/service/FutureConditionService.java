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
package org.jahia.modules.reports.service;

import org.jahia.services.content.JCRNodeWrapper;

import javax.jcr.RepositoryException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Short description of the class
 *
 * @author nonico
 */
public class FutureConditionService implements ConditionService {
    private final DateTimeFormatter DATETIME_FORMAT = new DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ofPattern("HH:mm"))
            .appendLiteral(" on ")
            .append(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
            .toFormatter();
    private final String CONDITIONALVISIBILITY_NT = "jnt:conditionalVisibility";
    private final String STARTENDDATECONDITION_NT = "jnt:startEndDateCondition";
    private final String CONDITIONALVISIBILITY_PROP = "j:conditionalVisibility";

    @Override public Map<String, String> getConditions(JCRNodeWrapper node) throws RepositoryException {
        JCRNodeWrapper conditionalVisibilityNode = node.getNode(CONDITIONALVISIBILITY_PROP);

        if (conditionalVisibilityNode == null) {
            return Collections.emptyMap();
        }

        if (!conditionalVisibilityNode.getNodeTypes().contains(CONDITIONALVISIBILITY_NT)) {
            return Collections.emptyMap();
        }

        Map<String, String> conditionsMap = new HashMap<>();
        for (JCRNodeWrapper childNode : conditionalVisibilityNode.getNodes()) {
            for (String nodeType : childNode.getNodeTypes()) {
                if (STARTENDDATECONDITION_NT.equals(nodeType)) {
                    conditionsMap.put(childNode.getName(), childNode.getPropertyAsString("start"));
                }
            }
        }
        return conditionsMap;
    }
}
