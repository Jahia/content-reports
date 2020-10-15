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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
 */public class FutureConditionServiceTest {

    JCRNodeWrapper node = mock(JCRNodeWrapper.class);
    JCRNodeWrapper conditionalVisibilityNode = mock(JCRNodeWrapper.class);
    JCRNodeWrapper condition = mock(JCRNodeWrapper.class);
    JCRNodeIteratorWrapper conditions = mock(JCRNodeIteratorWrapper.class);
    FutureConditionService service;

    @Before public void setup() throws RepositoryException {
        service = new FutureConditionService();
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

     @Test public void test_getEarliestDate_withOneDate() {
         LocalDateTime now = LocalDateTime.now();
         Map<String, LocalDateTime> conditions = new HashMap<>();
         conditions.put("condition0", now);
         String expectedStartDate = now.format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"));
         Map<String, String> earliestStartDate = service.getEarliestStartDate(conditions);
         Assert.assertEquals("It should be equal", expectedStartDate, earliestStartDate.values().iterator().next());
     }

    @Test public void test_getEarliestDate_withMultipleDates() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, LocalDateTime> conditions = new HashMap<>();
        conditions.put("condition0", now);
        conditions.put("condition1", now.plusDays(1));
        conditions.put("condition2", now.plusDays(2));
        conditions.put("condition3", now.plusDays(3));
        String expectedStartDate = conditions.get("condition0").format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"));
        Map<String, String> earliestStartDate = service.getEarliestStartDate(conditions);
        Assert.assertEquals("It should be equal", expectedStartDate, earliestStartDate.values().iterator().next());
    }

    @Test public void test_emptyCollectionDates() {
        Assert.assertEquals("It should be empty", Collections.emptyMap(),
                service.getEarliestStartDate(Collections.singletonMap(null, null)));
    }
}