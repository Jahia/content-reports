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
import org.jahia.services.content.decorator.JCRSiteNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.jcr.nodetype.PropertyDefinition;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Short description of the class
 *
 * @author tdubreucq
 */
public class ReportDisplayLinks  extends QueryReport {
    private static Logger logger = LoggerFactory.getLogger(ReportDisplayLinks.class);
    protected static final String BUNDLE = "resources.content-reports";
    private long totalContent;
    private String originPath;
    private String destinationPath;

    public ReportDisplayLinks(JCRSiteNode siteNode, String originPath, String destinationPath) {
        super(siteNode);
        this.originPath = originPath;
        this.destinationPath = destinationPath;
    }

    @Override
    public void execute(JCRSessionWrapper session, int offset, int limit) throws RepositoryException, JSONException {

        List<JCRNodeWrapper> referencesNodes = new ArrayList<>();
        JCRNodeWrapper originNode;
        originNode = session.getNode(originPath);
        checkChildNodes(originNode, referencesNodes);
        referencesNodes = referencesNodes.stream().distinct().collect(Collectors.toList());
        totalContent = referencesNodes.size();

        for (JCRNodeWrapper nodeToAdd : referencesNodes) {
            addItem(nodeToAdd);
        }

    }

    private void checkChildNodes(JCRNodeWrapper originNode, List<JCRNodeWrapper> referencesNodes)
            throws RepositoryException {
        if (getReference(originNode, destinationPath) != null) {
            referencesNodes.add(originNode);
        }
        for (JCRNodeWrapper childNode : originNode.getNodes()) {
            if (getReference(childNode, destinationPath) != null) {
                referencesNodes.add(childNode);
            }
            checkChildNodes(childNode, referencesNodes);
        }


    }

    private boolean isReferenceType (PropertyDefinition definitionToCheck) {
        return definitionToCheck.getRequiredType() == PropertyType.REFERENCE
                || definitionToCheck.getRequiredType() == PropertyType.WEAKREFERENCE;
    }

    private JCRNodeWrapper getReference (JCRNodeWrapper node, String path) throws RepositoryException {
        PropertyIterator propIt = node.getProperties() ;
        while (propIt.hasNext()) {
            try {
                Property actualProperty = propIt.nextProperty();
                if (isReferenceType(actualProperty.getDefinition())) {
                    if (actualProperty.getDefinition().isMultiple()) {
                        //TODO BACKLOG-8925
                    } else {
                        if (isReferenceType(actualProperty.getDefinition())) {
                            if (actualProperty.getNode().getPath().startsWith(path + "/")) {
                                return (JCRNodeWrapper) actualProperty.getNode();
                            }
                        }
                    }
                }
            } catch (NoSuchElementException e) {
                return null;
            }
        }
        return null;
    }



    /**
     * addItem
     *
     * @param node {@link JCRNodeWrapper}
     * @throws RepositoryException
     */
    public void addItem(JCRNodeWrapper node) throws RepositoryException {

        Map<String, String> nodeMap = new HashedMap();
        JCRNodeWrapper referencedNode = getReference(node, destinationPath);

        nodeMap.put("nodeTypeName", referencedNode.getPrimaryNodeTypeName());
        nodeMap.put("nodePath", referencedNode.getPath());
        nodeMap.put("lastModified", referencedNode.getLastModifiedAsDate().toString());
        nodeMap.put("referencePath", JCRContentUtils.getParentOfType(node,"jnt:page").getPath());

        this.dataList.add(nodeMap);
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
