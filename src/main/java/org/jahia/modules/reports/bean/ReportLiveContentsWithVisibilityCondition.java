/*
 * ==========================================================================================
 * =                   JAHIA'S DUAL LICENSING - IMPORTANT INFORMATION                       =
 * ==========================================================================================
 *
 *                                 http://www.jahia.com
 *
 *     Copyright (C) 2002-2020 Jahia Solutions Group SA. All rights reserved.
 *
 *     THIS FILE IS AVAILABLE UNDER TWO DIFFERENT LICENSES:
 *     1/GPL OR 2/JSEL
 *
 *     1/ GPL
 *     ==================================================================================
 *
 *     IF YOU DECIDE TO CHOOSE THE GPL LICENSE, YOU MUST COMPLY WITH THE FOLLOWING TERMS:
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *     2/ JSEL - Commercial and Supported Versions of the program
 *     ===================================================================================
 *
 *     IF YOU DECIDE TO CHOOSE THE JSEL LICENSE, YOU MUST COMPLY WITH THE FOLLOWING TERMS:
 *
 *     Alternatively, commercial and supported versions of the program - also known as
 *     Enterprise Distributions - must be used in accordance with the terms and conditions
 *     contained in a separate written agreement between you and Jahia Solutions Group SA.
 *
 *     If you are unsure which license is appropriate for your use,
 *     please contact the sales department at sales@jahia.com.
 */
package org.jahia.modules.reports.bean;

import org.apache.commons.collections.map.HashedMap;
import org.jahia.exceptions.JahiaException;
import org.jahia.services.content.JCRNodeIteratorWrapper;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRPropertyWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.content.nodetypes.ExtendedNodeDefinition;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.HashMap;
import java.util.Map;

/**
 * The ReportPagesWithoutDescription Class.
 * <p>
 * Created by Juan Carlos Rodas.
 */
public class ReportLiveContentsWithVisibilityCondition extends QueryReport {
    private static Logger logger = LoggerFactory.getLogger(ReportLiveContentsWithVisibilityCondition.class);
    protected static final String BUNDLE = "resources.content-reports";
    private String searchPath;

    public ReportLiveContentsWithVisibilityCondition(JCRSiteNode siteNode, String searchPath) {
        super(siteNode);
        this.searchPath = searchPath;
    }

    @Override public void addItem(JCRNodeWrapper node) throws RepositoryException {
        Map<String,String> map = new HashMap<>();
        map.put("nodePath", node.getPath());
        map.put("nodeName", node.getName());
        map.put("nodeType", String.join(", ",node.getNodeTypes()));
        map.put("nodeCurrentStatus", node.hasProperty("j:published") &&
                node.getPropertyAsString("j:published")
                    .equalsIgnoreCase("true") ? "live" : "not live");

        this.dataList.add(map);
    }

    @Override public void execute(JCRSessionWrapper session, int offset, int limit)
            throws RepositoryException, JSONException, JahiaException {
        String query = "SELECT * FROM [jnt:content] AS parent \n"
                + "INNER JOIN [jnt:conditionalVisibility] as child ON ISCHILDNODE(child,parent) \n"
                + "WHERE ISDESCENDANTNODE(parent,['" + searchPath + "'])";
        fillReport(session, query, offset, limit);
    }
}
