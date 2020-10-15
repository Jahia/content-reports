package org.jahia.modules.reports.service;

import junit.framework.TestCase;
import org.jahia.services.content.JCRNodeIteratorWrapper;
import org.jahia.services.content.JCRNodeWrapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;
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
 */
@RunWith(PowerMockRunner.class)
public class LiveConditionServiceTest {
    Logger logger = LoggerFactory.getLogger(LiveConditionServiceTest.class);
    JCRNodeWrapper node = mock(JCRNodeWrapper.class);
    JCRNodeWrapper conditionalVisibilityNode = mock(JCRNodeWrapper.class);
    JCRNodeWrapper condition = mock(JCRNodeWrapper.class);
    JCRNodeIteratorWrapper conditions = mock(JCRNodeIteratorWrapper.class);
    LiveConditionService service;

    @Before public void setup() throws RepositoryException {
        service = new LiveConditionService();
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




}