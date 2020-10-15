package org.jahia.modules.reports.service;

import org.jahia.services.content.JCRNodeIteratorWrapper;
import org.jahia.services.content.JCRNodeWrapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.jcr.RepositoryException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

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

public class ExpiredConditionServiceTest {

    JCRNodeWrapper node = mock(JCRNodeWrapper.class);
    JCRNodeWrapper conditionalVisibilityNode = mock(JCRNodeWrapper.class);
    JCRNodeWrapper condition = mock(JCRNodeWrapper.class);
    JCRNodeIteratorWrapper conditions = mock(JCRNodeIteratorWrapper.class);
    ExpiredConditionService service;

    @Before public void setup() throws RepositoryException {
        service = new ExpiredConditionService();
        when(node.getNode(anyString())).thenReturn(conditionalVisibilityNode);
        when(conditionalVisibilityNode.getNode(anyString())).thenReturn(condition);
        when(conditionalVisibilityNode.getNodes()).thenReturn(conditions);
        when(conditions.nextNode()).thenReturn(condition);
    }

    @Test public void test_nodeWithoutConditionalVisibilityChildNode() throws RepositoryException {
        when(node.getNode(any())).thenReturn(null);
        Map<String, String> conditions = service.getConditions(node);
        Assert.assertEquals("It should be empty", Collections.emptyMap(), conditions);
    }

    @Test public void test_nodeWithChildNodeNotConditionalVisibilityType() throws RepositoryException {
        when(conditionalVisibilityNode.getNodeTypes()).thenReturn(Collections.emptyList());
        Map<String, String> conditions = service.getConditions(node);
        Assert.assertEquals("It should be empty", Collections.emptyMap(), conditions);
    }

    @Test public void test_nodeWithConditionalVisibility_withoutStartEndDateCondition() throws RepositoryException {
        when(condition.getNodeTypes()).thenReturn(Arrays.asList("nodeType1", "nodeType2"));
        Map<String, String> conditions = service.getConditions(node);
        Assert.assertEquals("It should be empty", Collections.emptyMap(), conditions);
    }

    @Test public void test_getLatestDateTime_withOneDate() {
        LocalDateTime now = LocalDateTime.now();
        Map<String,LocalDateTime> conditions = new HashMap<>();
        conditions.put("condition0", now);
        String expectedLatestDate = conditions.get("condition0").format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"));
        Map<String, String> latestDateTime = service.getLatestDateTime(conditions);
        Assert.assertEquals("It should show the latest date time", expectedLatestDate, latestDateTime.values().iterator().next());
    }

    @Test public void test_getLatestDateTime_withMultipleDates() {
        LocalDateTime now = LocalDateTime.now();
        Map<String,LocalDateTime> conditions = new HashMap<>();
        conditions.put("condition0", now);
        conditions.put("condition1", now.plusDays(1));
        conditions.put("condition2", now.plusDays(2));
        conditions.put("condition3", now.plusDays(3));
        String expectedLatestDate = conditions.get("condition3").format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"));
        Map<String, String> latestDateTime = service.getLatestDateTime(conditions);
        Assert.assertEquals("It should show the latest date time", expectedLatestDate, latestDateTime.values().iterator().next());
    }

    @Test public void test_emptyCollectionDates() {
        Assert.assertEquals("It should be empty", Collections.emptyMap(), service.getLatestDateTime(Collections.singletonMap(null, null)));
    }
}