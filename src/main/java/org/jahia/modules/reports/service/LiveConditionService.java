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

import org.jahia.api.Constants;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.visibility.VisibilityService;

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
    private final String DAYOFWEEKCONDITION_NT = "jnt:dayOfWeekCondition";
    private final String STARTENDDATECONDITION_NT = "jnt:startEndDateCondition";
    private final String TIMEOFDAYCONDITION_NT = "jnt:timeOfDayCondition";
    public static final String CONDITIONALVISIBILITY_PROP = "j:conditionalVisibility";
    public static final String CURRENTSTATUS = "currentStatus";
    public static final String ISCONDITIONMATCHED = "isConditionMatched";

    @Override public Map<String, String> getConditions(JCRNodeWrapper node) throws RepositoryException {
        JCRNodeWrapper conditionalVisibilityNode = node.getNode(CONDITIONALVISIBILITY_PROP);
        if (conditionalVisibilityNode == null) {
            return Collections.emptyMap();
        }

        if (!conditionalVisibilityNode.getNodeTypes().contains(Constants.JAHIANT_CONDITIONAL_VISIBILITY)) {
            return Collections.emptyMap();
        }

        Map<String, String> conditionsMap = new HashMap<>();

        boolean matchedAllConditions = true;
        for (JCRNodeWrapper childNode : conditionalVisibilityNode.getNodes()) {
            for (String nodeType : childNode.getNodeTypes()) {
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
                        matchedAllConditions = checkDayOfWeek(childNode) && matchedAllConditions;
                        break;
                    case STARTENDDATECONDITION_NT:
                        if (VisibilityService.getInstance().matchesConditions(node)) {
                            StringBuilder dateConditionBuilder = new StringBuilder("Visible");
                            String start = childNode.hasProperty("start") ? childNode.getPropertyAsString("start") : "";
                            String end = childNode.hasProperty("end") ? childNode.getPropertyAsString("end") : "";
                            LocalDateTime startDate;
                            LocalDateTime endDate;
                            if (!start.isEmpty()) {
                                startDate = LocalDateTime.parse(start, DateTimeFormatter.ISO_DATE_TIME);
                                dateConditionBuilder.append(" starting from ").append(startDate.format(DATETIME_FORMAT));
                            }
                            if (!end.isEmpty()) {
                                endDate = LocalDateTime.parse(end, DateTimeFormatter.ISO_DATE_TIME);
                                dateConditionBuilder.append(" until ").append(endDate.format(DATETIME_FORMAT));
                            }
                            conditionsMap.put(childNode.getName(), dateConditionBuilder.toString());
                            matchedAllConditions = checkStartEndDate(childNode) && matchedAllConditions;
                        }
                        break;
                    case TIMEOFDAYCONDITION_NT:
                        StringBuilder timeConditionBuilder = new StringBuilder("Visible");
                        String startHour = childNode.hasProperty("startHour") ? childNode.getPropertyAsString("startHour") : "";
                        String startMinute = childNode.hasProperty("startMinute") ? childNode.getPropertyAsString("startMinute") : "00";
                        String endHour = childNode.hasProperty("endHour") ? childNode.getPropertyAsString("endHour") : "";
                        String endMinute = childNode.hasProperty("endMinute") ? childNode.getPropertyAsString("endMinute") : "00";
                        if (startHour.isEmpty() && endHour.isEmpty()) {
                            timeConditionBuilder.append(" any time of the day");
                        } else if (startHour.isEmpty()) {
                            timeConditionBuilder.append(" until ").append(endHour).append(":").append(endMinute);
                        } else if (endHour.isEmpty()) {
                            timeConditionBuilder.append(" from ").append(startHour).append(":").append(startMinute);
                        } else {
                            timeConditionBuilder.append(" from ").append(startHour).append(":").append(startMinute)
                                    .append(" until ").append(endHour).append(":").append(endMinute);
                        }
                        conditionsMap.put(childNode.getName(), timeConditionBuilder.toString());
                        matchedAllConditions = checkTimeOfDay(startHour, startMinute, endHour, endMinute) && matchedAllConditions;
                        break;
                    default:
                        break;
                }
            }
        }
        boolean isPublished = node.hasProperty(Constants.PUBLISHED) && node.getProperty(Constants.PUBLISHED).getBoolean();
        boolean isVisibleInLive = VisibilityService.getInstance().matchesConditions(node) && isPublished;
        conditionsMap.put(CURRENTSTATUS, isVisibleInLive ? "visible" : "not visible");
        conditionsMap.put(ISCONDITIONMATCHED, String.valueOf(matchedAllConditions));
        return conditionsMap;
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
        String start = node.hasProperty("start") ? node.getPropertyAsString("start") : "";
        String end = node.hasProperty("end") ? node.getPropertyAsString("end") : "";
        if (start.isEmpty() && end.isEmpty()) {
            return false;
        } else if (start.isEmpty()) {
            return LocalDateTime.parse(end, DateTimeFormatter.ISO_DATE_TIME).isAfter(now);
        } else if (end.isEmpty()) {
            return LocalDateTime.parse(start, DateTimeFormatter.ISO_DATE_TIME).isBefore(now);
        } else {
            LocalDateTime startDate = LocalDateTime.parse(start, DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime endDate = LocalDateTime.parse(end, DateTimeFormatter.ISO_DATE_TIME);
            return startDate.isBefore(now) && endDate.isAfter(now);
        }
    }

    private boolean checkTimeOfDay(String startHour, String startMinute, String endHour, String endMinute) {
        LocalTime now = LocalTime.now();
        LocalTime startTime;
        LocalTime endTime;
        if (startHour.isEmpty() && endHour.isEmpty()) {
            return true;
        } else if (startHour.isEmpty()) {
            endTime = LocalTime.of(Integer.parseInt(endHour), Integer.parseInt(endMinute));
            return endTime.isAfter(now);
        } else if (endHour.isEmpty()) {
            startTime = LocalTime.of(Integer.parseInt(startHour), Integer.parseInt(startMinute));
            return startTime.isBefore(now);
        } else {
            startTime = LocalTime.of(Integer.parseInt(startHour), Integer.parseInt(startMinute));
            endTime = LocalTime.of(Integer.parseInt(endHour), Integer.parseInt(endMinute));
            return startTime.isBefore(now) && endTime.isAfter(now);
        }
    }
}
