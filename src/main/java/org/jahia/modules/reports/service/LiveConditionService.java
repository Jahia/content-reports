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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Short description of the class
 *
 * @author nonico
 */
public class LiveConditionService implements ConditionService {
    private final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
    private final String CONDITIONALVISIBILITY_NT = "jnt:conditionalVisibility";
    private final String DAYOFWEEKCONDITION_NT = "jnt:dayOfWeekCondition";
    private final String STARTENDDATECONDITION_NT = "jnt:startEndDateCondition";
    private final String TIMEOFDAYCONDITION_NT = "jnt:timeOfDayCondition";
    public static final String CONDITIONALVISIBILITY_PROP = "j:conditionalVisibility";
    private final String FORCED_MATCH_ALL_PROP = "j:forceMatchAllConditions";
    public static final String CURRENTSTATUS = "currentStatus";
    public static final String ISCONDITIONMATCHED = "isConditionMatched";

    @Override public Map<String, String> getConditions(JCRNodeWrapper node) throws RepositoryException {
        JCRNodeWrapper conditionalVisibilityNode = node.getNode(CONDITIONALVISIBILITY_PROP);
        if (conditionalVisibilityNode == null) {
            return Collections.emptyMap();
        }

        if (!conditionalVisibilityNode.getNodeTypes().contains(CONDITIONALVISIBILITY_NT)) {
            return Collections.emptyMap();
        }

        Map<String, String> conditionsMap = new HashMap<>();
        List<Boolean> matchConditions = new ArrayList<>();

        boolean isConditionMatched = true;
        for (JCRNodeWrapper childNode : conditionalVisibilityNode.getNodes()) {
            for (String nodeType : childNode.getNodeTypes()) {
                boolean isMatched = false;
                switch (nodeType) {
                    case DAYOFWEEKCONDITION_NT:
                        String dayOfWeek = Arrays.stream(childNode.getPropertyAsString("dayOfWeek").split(" ").clone())
                                .map(String::toUpperCase)
                                .map(DayOfWeek::valueOf)
                                .sorted()
                                .map(Enum::toString)
                                .map(String::toLowerCase)
                                .collect(Collectors.joining(", "));
                        conditionsMap.put(childNode.getName(), String.format("Visible on [ %s ]", dayOfWeek));
                        isMatched = checkDayOfWeek(childNode);
                        isConditionMatched = isMatched && isConditionMatched;
                        break;
                    case STARTENDDATECONDITION_NT:
                        LocalDateTime startDate = LocalDateTime
                                .parse(childNode.getPropertyAsString("start"), DateTimeFormatter.ISO_DATE_TIME);
                        LocalDateTime endDate = LocalDateTime.parse(childNode.getPropertyAsString("end"), DateTimeFormatter.ISO_DATE_TIME);
                        conditionsMap.put(childNode.getName(),
                                String.format("Visible from %s to %s", startDate.format(DATETIME_FORMAT), endDate.format(DATETIME_FORMAT)));
                        isMatched = checkStartEndDate(childNode);
                        isConditionMatched = isMatched && isConditionMatched;
                        break;
                    case TIMEOFDAYCONDITION_NT:
                        String endHour = childNode.getPropertyAsString("endHour");
                        String startHour = childNode.getPropertyAsString("startHour");
                        String endMinute = childNode.getPropertyAsString("endMinute");
                        String startMinute = childNode.getPropertyAsString("startMinute");
                        LocalTime startTime = LocalTime.of(Integer.parseInt(startHour), Integer.parseInt(startMinute));
                        LocalTime endTime = LocalTime.of(Integer.parseInt(endHour), Integer.parseInt(endMinute));
                        conditionsMap
                                .put(childNode.getName(), String.format("Visible from %s to %s", startTime.toString(), endTime.toString()));
                        isMatched = checkTimeOfDay(childNode);
                        isConditionMatched = isMatched && isConditionMatched;
                        break;
                    default:
                        break;
                }
                matchConditions.add(isMatched);
            }
        }

        boolean forceMatchAll = Boolean.parseBoolean(node.getPropertyAsString(FORCED_MATCH_ALL_PROP));
        boolean currentStatus = getCurrentStatus(matchConditions, forceMatchAll);
        conditionsMap.put(CURRENTSTATUS, String.valueOf(currentStatus));
        conditionsMap.put(ISCONDITIONMATCHED, String.valueOf(isConditionMatched));
        return conditionsMap;
    }

    /**
     * Determine if all conditions needs to be matched or
     * just needed to satisfy one or more condition(s)
     * @param matchedConditions
     * @param forceMatchAll
     * @return
     */
    private boolean getCurrentStatus(List<Boolean> matchedConditions, boolean forceMatchAll) {
        boolean isMatched = false;
        for (boolean condition : matchedConditions) {
            isMatched = forceMatchAll || condition;
            if (forceMatchAll && !condition) {
                return false;
            } else if (!forceMatchAll && condition) {
                return true;
            }
        }
        return isMatched;
    }

    private boolean checkDayOfWeek(JCRNodeWrapper node) throws RepositoryException {
        if (node.getNodeTypes().size() != 1 || !node.getNodeTypes().contains(DAYOFWEEKCONDITION_NT)) {
            return false;
        }
        return Arrays.stream(node.getPropertyAsString("dayOfWeek").split(" "))
                .anyMatch(day -> day.equalsIgnoreCase(LocalDate.now().getDayOfWeek().toString()));
    }

    private boolean checkStartEndDate(JCRNodeWrapper node) throws RepositoryException {
        if (node.getNodeTypes().size() != 1 || !node.getNodeTypes().contains(STARTENDDATECONDITION_NT)) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = LocalDateTime.parse(node.getPropertyAsString("start"), DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime endDate = LocalDateTime.parse(node.getPropertyAsString("end"), DateTimeFormatter.ISO_DATE_TIME);
        return startDate.isBefore(now) && endDate.isAfter(now);
    }

    private boolean checkTimeOfDay(JCRNodeWrapper node) throws RepositoryException {
        if (node.getNodeTypes().size() != 1 || !node.getNodeTypes().contains(TIMEOFDAYCONDITION_NT)) {
            return false;
        }
        LocalTime now = LocalTime.now();
        String endHour = node.getPropertyAsString("endHour");
        String startHour = node.getPropertyAsString("startHour");
        String endMinute = node.getPropertyAsString("endMinute");
        String startMinute = node.getPropertyAsString("startMinute");
        LocalTime startTime = LocalTime.of(Integer.parseInt(startHour), Integer.parseInt(startMinute));
        LocalTime endTime = LocalTime.of(Integer.parseInt(endHour), Integer.parseInt(endMinute));
        return startTime.isBefore(now) && endTime.isAfter(now);
    }
}
