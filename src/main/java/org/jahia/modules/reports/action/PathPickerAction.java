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
package org.jahia.modules.reports.action;

import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.services.content.*;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.jahia.utils.Patterns;
import org.jahia.utils.i18n.Messages;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * PathPickerAction Class.
 * Created by Juan Carlos Rodas.
 */
public class PathPickerAction extends Action {

    /* the logger for the class. */
    private static Logger logger = LoggerFactory.getLogger(PathPickerAction.class);
    private static final String BUNDLE = "resources.content-reports";
    private Locale locale = LocaleContextHolder.getLocale();


    @Override
    public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource, JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
        logger.info("doExecute: begins the PathPickerAction action.");

        JCRNodeWrapper rootNode = renderContext.getSite();
        String nodeTypes = Optional.ofNullable(req.getParameter("nodeTypes")).orElse("jnt:page");
        List<String> excludedNodes =
                Optional.ofNullable(req.getParameter("excludedNodes")).map(excludeNodes -> Arrays.asList(Patterns.COMMA.split(excludeNodes))).orElse(null);
        if (req.getParameter("path") != null) {
            rootNode = session.getNode(req.getParameter("path"));
        }
        boolean selectable = JCRContentUtils.isNodeType(rootNode,nodeTypes);

        try {
            return new ActionResult(HttpServletResponse.SC_OK, null, new JSONObject(getSitePathJson(rootNode, nodeTypes, excludedNodes, selectable)));

        }catch (Exception ex) {
            logger.error("doExecute(), Error,", ex);
            return new ActionResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * getSitePathJson
     * <p>The method returns a json with the tree under a specific node.</p>
     *
     *
     * @param node @JCRNodeWrapper
     * @return jsonString @String
     * @throws RepositoryException
     */
    protected String getSitePathJson(JCRNodeWrapper node, String type, List<String> excludedNodes, boolean selectable) throws RepositoryException {
        StringBuilder jsonBuilder = new StringBuilder("{");

        if(node.getPath().equals("/sites")) {
            jsonBuilder.append("text:'").append(Messages.get(BUNDLE, "cgnt_contentReports.report.label.websites",locale)).append("',");
        } else {
            jsonBuilder.append("text:'").append(node.getDisplayableName().replaceAll("'", "")).append("',");
        }
        jsonBuilder.append("href:'").append(node.getPath()).append("',");
        if (!selectable) {
            jsonBuilder.append("selectable:false").append(",");
        }


        /* getting the folder child nodes */
        List<JCRNodeWrapper> childNodeList = JCRContentUtils.getChildrenOfType(node, type);

        if (excludedNodes != null) {
            childNodeList = childNodeList
                    .stream()
                    .filter(nodeToFilter -> !excludedNodes.contains(nodeToFilter.getPath()))
                    .collect(Collectors.toList());
        }

        jsonBuilder.append("tags: ['").append(childNodeList.size()).append("'],");
        if(childNodeList.size() > 0){
            jsonBuilder.append("nodes:[");
            for(int index = 0; index < childNodeList.size(); index++){
                if(index > 0) jsonBuilder.append(",");
                jsonBuilder.append(getSitePathJson(childNodeList.get(index), type, excludedNodes,true));
            }
            jsonBuilder.append("]");
        }
        jsonBuilder.append("}");

        return jsonBuilder.toString();
    }

}
