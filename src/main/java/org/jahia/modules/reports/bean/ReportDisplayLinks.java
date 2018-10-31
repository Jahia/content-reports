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

import org.apache.commons.collections.map.HashedMap;
import org.jahia.services.content.JCRContentUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRValueWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.jcr.nodetype.PropertyDefinition;
import java.util.*;

/**
 * Short description of the class
 *
 * @author tdubreucq
 */
public class ReportDisplayLinks  extends QueryReport {
    private static Logger logger = LoggerFactory.getLogger(ReportDisplayLinks.class);
    protected static final String BUNDLE = "resources.content-reports";
    private long totalContent = 0;
    private String originPath;
    private String destinationPath;
    private HashMap<JCRNodeWrapper,List<JCRNodeWrapper>> referencesNodes;

    public ReportDisplayLinks(JCRSiteNode siteNode, String originPath, String destinationPath) {
        super(siteNode);
        this.originPath = originPath;
        this.destinationPath = destinationPath;
        this.referencesNodes = new HashMap<>();
    }

    @Override
    public void execute(JCRSessionWrapper session, int offset, int limit) throws RepositoryException, JSONException {

        JCRNodeWrapper originNode;
        originNode = session.getNode(originPath);
        checkChildNodes(originNode, referencesNodes);

        for (Map.Entry<JCRNodeWrapper, List<JCRNodeWrapper>> actualEntry : referencesNodes.entrySet()) {
            JCRNodeWrapper actualNode = actualEntry.getKey();
            addItem(actualNode);
        }

    }

    private void checkChildNodes(JCRNodeWrapper originNode, HashMap<JCRNodeWrapper,List<JCRNodeWrapper>> referencesNodes)
            throws RepositoryException {

        linkReferencesToNode(originNode,referencesNodes);

        for (JCRNodeWrapper childNode : originNode.getNodes()) {
            checkChildNodes(childNode, referencesNodes);
        }
    }

    private boolean isReferenceType (PropertyDefinition definitionToCheck) {
        return definitionToCheck.getRequiredType() == PropertyType.REFERENCE
                || definitionToCheck.getRequiredType() == PropertyType.WEAKREFERENCE;
    }

    private void linkReferencesToNode (JCRNodeWrapper node, HashMap<JCRNodeWrapper,List<JCRNodeWrapper>> referencesNodes) throws RepositoryException {
        List<JCRNodeWrapper> references = new ArrayList<>();
        PropertyIterator propIt = node.getProperties() ;
        while (propIt.hasNext()) {
            Property actualProperty = propIt.nextProperty();
            if (isReferenceType(actualProperty.getDefinition())) {
                if (actualProperty.getDefinition().isMultiple()) {
                    Value[] values = actualProperty.getValues();
                    for (Value value : values) {
                        JCRValueWrapper propertyValue = (JCRValueWrapper) value;
                        if (propertyValue.getNode().getPath().startsWith(destinationPath + "/")) {
                            references.add(propertyValue.getNode());
                        }

                    }
                } else if (isReferenceType(actualProperty.getDefinition()) &&
                        actualProperty.getNode().getPath().startsWith(destinationPath + "/")) {
                    references.add((JCRNodeWrapper) actualProperty.getNode());
                }
            }
        }
        if (!references.isEmpty()) {
            referencesNodes.put(node,references);
        }
    }



    /**
     * addItem
     *
     * @param node {@link JCRNodeWrapper}
     * @throws RepositoryException
     */
    public void addItem(JCRNodeWrapper node) throws RepositoryException {

        Map<String, String> nodeMap = new HashedMap();

        for (Map.Entry<JCRNodeWrapper, List<JCRNodeWrapper>> actualEntry : referencesNodes.entrySet()) {
            if (actualEntry.getKey() == node) {
                List<JCRNodeWrapper> referencedNodes = actualEntry.getValue();
                for (JCRNodeWrapper referencedNode : referencedNodes) {
                    nodeMap.put("nodeTypeName", referencedNode.getPrimaryNodeTypeName());
                    nodeMap.put("nodePath", referencedNode.getPath());
                    nodeMap.put("lastModified", referencedNode.getLastModifiedAsDate().toString());
                    nodeMap.put("referencePath", JCRContentUtils.getParentOfType(node, "jnt:page").getPath());
                    this.dataList.add(nodeMap);
                    totalContent = totalContent + 1;
                }
            }
        }
    }

    @Override
    public JSONObject getJson() throws JSONException, RepositoryException {

        JSONObject jsonObject = new JSONObject();
        JSONArray jArray = new JSONArray();

        for (Map<String, String> nodeMap : this.dataList) {
            JSONArray item = new JSONArray();
            item.put(nodeMap.get("nodeTypeName"));
            item.put(nodeMap.get("nodePath"));
            item.put(nodeMap.get("lastModified"));
            item.put(nodeMap.get("referencePath"));
            jArray.put(item);
        }
        jsonObject.put("recordsTotal", totalContent);
        jsonObject.put("recordsFiltered", totalContent);
        jsonObject.put("siteName", siteNode.getName());
        jsonObject.put("siteDisplayableName", siteNode.getDisplayableName());
        jsonObject.put("data", jArray);
        return jsonObject;
    }
}
