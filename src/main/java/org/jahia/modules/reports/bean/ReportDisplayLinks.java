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
 *     Copyright (C) 2002-2018 Jahia Solutions Group. All rights reserved.
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

import org.jahia.services.content.JCRContentUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRValueWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.jcr.*;
import javax.jcr.nodetype.PropertyDefinition;

/**
 * Short description of the class
 *
 * @author tdubreucq
 */
public class ReportDisplayLinks extends BaseReport {
    protected static final String BUNDLE = "resources.content-reports";
    private String originPath;
    private String destinationPath;
    private JSONArray dataList = new JSONArray();

    public ReportDisplayLinks(JCRSiteNode siteNode, String originPath, String destinationPath) {
        super(siteNode);
        this.originPath = originPath;
        this.destinationPath = destinationPath;
    }

    @Override
    public void execute(JCRSessionWrapper session, int offset, int limit) throws RepositoryException, JSONException {
        JCRNodeWrapper originNode;
        originNode = session.getNode(originPath);
        linkReferencesToNode(originNode);
    }

    private void linkReferencesToNode (JCRNodeWrapper node) throws RepositoryException {
        PropertyIterator propIt = node.getProperties() ;
        while (propIt.hasNext()) {
            Property actualProperty = propIt.nextProperty();
            PropertyDefinition actualPropertyDefinition = actualProperty.getDefinition();
            if (actualPropertyDefinition.getRequiredType() == PropertyType.REFERENCE || actualPropertyDefinition.getRequiredType() == PropertyType.WEAKREFERENCE) {
                if (actualProperty.isMultiple()) {
                    Value[] values = actualProperty.getValues();
                    for (Value value : values) {
                        JCRValueWrapper propertyValue = (JCRValueWrapper) value;
                        addItem(node, propertyValue.getNode());
                    }
                } else {
                    addItem(node, (JCRNodeWrapper) actualProperty.getNode());
                }
            }
        }

        for (JCRNodeWrapper childNode : node.getNodes()) {
            linkReferencesToNode(childNode);
        }
    }

    private void addItem(JCRNodeWrapper referenceNode, JCRNodeWrapper referencedNode) throws RepositoryException {
        if (referencedNode != null && referencedNode.getPath().startsWith(destinationPath + "/")) {
            JSONArray dataItem = new JSONArray();
            dataItem.put(referencedNode.getPrimaryNodeTypeName());
            dataItem.put(referencedNode.getPath());
            dataItem.put(referencedNode.getLastModifiedAsDate().toString());
            dataItem.put(JCRContentUtils.getParentOfType(referenceNode, "jnt:page").getPath());
            dataList.put(dataItem);
        }
    }

    @Override
    public JSONObject getJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("recordsTotal", dataList.length());
        jsonObject.put("recordsFiltered", dataList.length());
        jsonObject.put("siteName", siteNode.getName());
        jsonObject.put("siteDisplayableName", siteNode.getDisplayableName());
        jsonObject.put("data", dataList);
        return jsonObject;
    }
}
