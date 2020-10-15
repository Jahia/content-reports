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

import org.jahia.exceptions.JahiaException;
import org.jahia.modules.reports.service.ConditionService;
import org.jahia.modules.reports.service.LiveConditionService;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.json.JSONException;

import javax.jcr.RepositoryException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.jahia.modules.reports.service.LiveConditionService.*;

/**
 * The ReportLiveContents Class.
 *
 * @author nonico
 */
public class ReportLiveContents extends ReportByContentVisibility {
    private final ConditionService conditionService;

    public ReportLiveContents(JCRSiteNode siteNode, String searchPath) {
        super(siteNode, searchPath);
        this.conditionService = new LiveConditionService();
    }

    @Override public void addItem(JCRNodeWrapper node) throws RepositoryException {
        Map<String,String> map = new HashMap<>();
        map.put("name", node.getName());
        map.put("path", node.getPath());
        map.put("type", String.join("<br/>",node.getNodeTypes()));
        map.put("currentStatus", node.hasProperty("j:published") &&
                node.getPropertyAsString("j:published")
                    .equalsIgnoreCase("true") ? "live" : "not live");

        Map<String, String> liveConditions = conditionService.getConditions(node);
        List<String> conditions = liveConditions.entrySet().stream()
                .filter(entry -> !entry.getKey().equalsIgnoreCase(ISCONDITIONMATCHED))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        map.put("listOfConditions", conditions.isEmpty() ? null : String.join("<br/>", conditions));
        map.put("isConditionMatched", liveConditions.getOrDefault("isConditionMatched", "false"));
        this.dataList.add(map);
    }
}
