package org.jahia.modules.governor.bean;

import org.apache.commons.lang.StringUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TODO Comment me
 *
 * @author toto
 */
public abstract class QueryReport extends BaseReport {
    private static Logger logger = LoggerFactory.getLogger(QueryReport.class);
    protected List<Map<String, String>> dataList;

    public QueryReport(JCRSiteNode siteNode) {
        super(siteNode);
        this.dataList = new ArrayList<>();
    }

    /**
     * addItem
     * <p>add item to the iReport class,
     * custom implementation in each child class.</p>
     *
     * @param node {@link JCRNodeWrapper}
     * @throws RepositoryException
     */
    public abstract void addItem(JCRNodeWrapper node) throws RepositoryException;

    /**
     * getQueryResult
     * <p>The method returns the NodeIterator,
     * result of the query execution.</p>
     *
     * @param queryStr {@link String}
     * @param session {@link JCRSessionWrapper}
     * @param offset
     * @param limit
     * @return {@link NodeIterator}
     * @throws RepositoryException
     */
    protected NodeIterator getQueryResult(String queryStr, JCRSessionWrapper session, int offset, int limit) throws RepositoryException{
        // Getting the items  nodes.
        Query query = session.getWorkspace().getQueryManager().createQuery(queryStr, Query.JCR_SQL2);
        query.setOffset(offset);
        query.setLimit(limit);
        return  query.execute().getNodes();
    }

    protected long getQueryResultCount(String queryStr, JCRSessionWrapper session) throws RepositoryException{
        // Getting the items  nodes.
        Query query = session.getWorkspace().getQueryManager().createQuery(queryStr, Query.JCR_SQL2);
        return  query.execute().getNodes().getSize();
    }


    /**
     * fillReport
     * <p>The method is the in charge to fill the BaseReport object,
     * with the data obtained from the query.</p>
     *
     * @param session {@link JCRSessionWrapper}
     * @param strQuery {@link String}
     * @param offset
     * @param limit
     * @return {@link BaseReport}
     * @throws JSONException
     */
    protected void fillReport(JCRSessionWrapper session, String strQuery, int offset, int limit) throws JSONException {
        if(StringUtils.isNotEmpty(strQuery)) {
            try {
             /* filling the content nodes */
                NodeIterator iterator = getQueryResult(strQuery, session, offset, limit);
                while (iterator.hasNext()) {
                    JCRNodeWrapper nodeItem = (JCRNodeWrapper) iterator.next();
                    addItem(nodeItem);
                }
            } catch (RepositoryException rex) {
                logger.error("getAjaxFromQuery: problem executing the jcr:query[" + strQuery + "]", rex);
            }
        }
    }

    /**
     * getTotalCount
     *
     * @param session {@link JCRSessionWrapper}
     * @param strQuery {@link String}
     * @return {@link BaseReport}
     * @throws JSONException
     */
    protected long getTotalCount(JCRSessionWrapper session, String strQuery) throws JSONException {
        if(StringUtils.isNotEmpty(strQuery)) {
            try {
             /* filling the content nodes */
                return getQueryResultCount(strQuery, session);

            } catch (RepositoryException rex) {
                logger.error("getAjaxFromQuery: problem executing the jcr:query[" + strQuery + "]", rex);
            }
        }
        return 0;
    }

    /**
     * getJson
     *
     * @return {@link JSONObject}
     * @throws JSONException
     * @throws RepositoryException
     */
    public JSONObject getJson() throws JSONException, RepositoryException {

        JSONObject jsonObject = new JSONObject();
        JSONArray jArray = new JSONArray();

        for (Map<String, String> nodeMap : this.dataList) {
            jArray.put(new JSONObject(nodeMap));
        }
        jsonObject.put("siteName", siteNode.getName());
        jsonObject.put("siteDisplayableName", siteNode.getDisplayableName());
        jsonObject.put("items", jArray);
        return jsonObject;
    }



}
