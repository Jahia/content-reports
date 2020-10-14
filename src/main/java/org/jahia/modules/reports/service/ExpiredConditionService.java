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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Short description of the class
 *
 * @author nonico
 */
public class ExpiredConditionService implements ConditionService {
    private final String CONDITIONALVISIBILITY_NT = "jnt:conditionalVisibility";
    private final String STARTENDDATECONDITION_NT = "jnt:startEndDateCondition";
    private final String CONDITIONALVISIBILITY_PROP = "j:conditionalVisibility";

    @Override public Map<String, String> getConditions(JCRNodeWrapper node) throws RepositoryException {
        JCRNodeWrapper conditionVisibilityNode = node.getNode(CONDITIONALVISIBILITY_PROP);
        if (conditionVisibilityNode == null) {
            return Collections.emptyMap();
        }
        if (!conditionVisibilityNode.getNodeTypes().contains(CONDITIONALVISIBILITY_NT)) {
            return Collections.emptyMap();
        }

        Map<String, String> conditionsMap = new HashMap<>();
        for (JCRNodeWrapper childNode : conditionVisibilityNode.getNodes()) {
            for (String nodeType : childNode.getNodeTypes()) {
                if (STARTENDDATECONDITION_NT.equals(nodeType)) {
                    conditionsMap.put(childNode.getName(), childNode.getPropertyAsString("end"));
                }
            }
        }
        return conditionsMap;
    }


}
