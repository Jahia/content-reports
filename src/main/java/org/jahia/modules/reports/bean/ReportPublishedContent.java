/*
 * ==========================================================================================
 * =                   JAHIA'S DUAL LICENSING - IMPORTANT INFORMATION                       =
 * ==========================================================================================
 *
 *                                 http://www.jahia.com
 *
 *     Copyright (C) 2002-2021 Jahia Solutions Group SA. All rights reserved.
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.RepositoryException;

import org.jahia.exceptions.JahiaException;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ReportPublishedContent Class.
 *
 * Created by Werner Assek.
 */
public class ReportPublishedContent extends QueryReport {
    private static Logger logger = LoggerFactory.getLogger(ReportPublishedContent.class);
    protected static final String BUNDLE = "resources.content-reports";
    private String searchPath;
    private String dateBegin;
    private String dateEnd;
    private Map<String, Map<String, Object>> contentMap;
    private long totalContent = 0;



    /**
     * Instantiates a new Report pages without title.
     *
     * @param siteNode the site node {@link JCRSiteNode}
     */
    public ReportPublishedContent(JCRSiteNode siteNode, String searchPath, String dateBegin, String dateEnd) {
        super(siteNode);
        this.dataList  = new ArrayList<>();
        this.searchPath = searchPath;
        if (dateBegin != null && !dateBegin.isEmpty()) {
        	this.dateBegin = dateBegin;
        } else {
        	//set first day of current month
        	Calendar cal = Calendar.getInstance();
        	cal.set(Calendar.DAY_OF_MONTH, 1);
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        	this.dateBegin = sdf.format(cal.getTime());
        }
        if (dateEnd != null && !dateEnd.isEmpty()) {
        	this.dateEnd = dateEnd;
        } else {
        	//set today + 1
        	Calendar cal = Calendar.getInstance();
        	cal.add(Calendar.DAY_OF_MONTH, 1);
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        	this.dateEnd = sdf.format(cal.getTime());
        }
        this.contentMap = new HashMap<String, Map<String, Object>>();
        this.totalContent = 0;
    }

    @Override
    public void execute(JCRSessionWrapper session, int offset, int limit) throws RepositoryException, JSONException, JahiaException {
        String pageQueryStr = "SELECT * FROM [nt:base] as node WHERE node.[j:lastPublished] >=  cast('" + dateBegin + "T00:00:00.000+00:00' as date) and node.[j:lastPublished] <= cast('" + dateEnd + "T00:00:00.000+00:00' as date) and ISDESCENDANTNODE(node, '" + searchPath + "')";
        fillReport(session, pageQueryStr, offset, limit);
        totalContent = getTotalCount(session, pageQueryStr);
        if (logger.isDebugEnabled()) {
        	logger.debug("ReportPublishedContent generated with " + totalContent + " Elements");
        }
    }

	@Override
	public void addItem(JCRNodeWrapper node) throws RepositoryException {
		contentMap.put(node.getIdentifier(), new HashMap<String, Object>());
        Map<String, Object> nodeEntry = contentMap.get(node.getIdentifier());
        nodeEntry.put("uuid",  node.getIdentifier());
        nodeEntry.put("path", node.getPath());
        String displayableName = node.getDisplayableName();
        if (displayableName.length() > 69) {
        	displayableName = displayableName.substring(0,67) + "...";
        }
        nodeEntry.put("title", displayableName);
        nodeEntry.put("type", node.getNodeTypes().toString());
        nodeEntry.put("lastPublished", node.getPropertyAsString("j:lastPublished"));
        nodeEntry.put("user", node.getPropertyAsString("j:lastPublishedBy"));
	}
	
	   /**
     * getJson
     *
     * @return {@link JSONObject}
     * @throws JSONException
     */
    public JSONObject getJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONArray jArray = new JSONArray();
        JSONArray jsonArrayItem;

        for (String content : contentMap.keySet()) {

                jsonArrayItem = new JSONArray();

                jsonArrayItem.put(contentMap.get(content).get("uuid"));
                jsonArrayItem.put(contentMap.get(content).get("path"));
                jsonArrayItem.put(contentMap.get(content).get("title"));
                jsonArrayItem.put(contentMap.get(content).get("type"));
                jsonArrayItem.put(contentMap.get(content).get("lastPublished"));
                jsonArrayItem.put(contentMap.get(content).get("user"));

                jArray.put(jsonArrayItem);

        }

        jsonObject.put("recordsTotal", totalContent);
        jsonObject.put("recordsFiltered", totalContent);
        jsonObject.put("data", jArray);

        return jsonObject;
    }


}
