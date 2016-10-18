package org.jahia.modules.governor.action;

import org.apache.commons.lang.StringUtils;
import org.jahia.exceptions.JahiaException;
import org.jahia.modules.governor.bean.*;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.governor.exception.GovernorException;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * GovernorReportAction Class.
 * Created by Juan Carlos Rodas.
 */
public class GovernorReportAction extends Action {

    /* the logger fot the class */
    private static Logger logger = LoggerFactory.getLogger(GovernorReportAction.class);

    @Override
    public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource, JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
        logger.info("doExecute: begins the GovernorReportAction action.");

        try {
            /*checking the necessary parameters*/
            if (StringUtils.isEmpty(req.getParameter("reportId"))) {
                throw new Exception("The parameter reportId is missing.");
            }

            /* returns the specific report specified by the reportId parameter */
            String reportId = req.getParameter("reportId");
            switch (reportId) {
                case "1":
                    return new ActionResult(HttpServletResponse.SC_OK,
                            null,
                            getReportByAuthor(session,
                                    (req.getParameter("typeSearch").equalsIgnoreCase("pages")) ? IReport.SEARCH_CONTENT_TYPE.PAGE : IReport.SEARCH_CONTENT_TYPE.CONTENT,
                                    (req.getParameter("typeAuthor").equalsIgnoreCase("created")) ? IReport.SEARCH_ACTION_TYPE.CREATION : IReport.SEARCH_ACTION_TYPE.UPDATE,
                                    req.getParameter("pathTxt").replaceAll("'", ""))
                    );

                case "2":
                    return new ActionResult(HttpServletResponse.SC_OK,
                            null,
                            getReportByAllDate(session,
                                    (req.getParameter("typeAuthor").equalsIgnoreCase("created")) ? IReport.SEARCH_ACTION_TYPE.CREATION : IReport.SEARCH_ACTION_TYPE.UPDATE,
                                    req.getParameter("pathTxt").replaceAll("'", ""))
                    );

                case "3":
                    return new ActionResult(HttpServletResponse.SC_OK,
                            null,
                            getReportBeforeDate(session, req.getParameter("pathTxt").replaceAll("'", ""), req.getParameter("date").replaceAll("'", ""))
                    );

                case "4":
                    return new ActionResult(HttpServletResponse.SC_OK,
                            null,
                            getReportByType(session, req.getParameter("pathTxt").replaceAll("'", ""))
                    );

                case "5":
                    return new ActionResult(HttpServletResponse.SC_OK,
                            null,
                            getReportByTypeDetailed(session, req.getParameter("pathTxt").replaceAll("'", ""))
                    );

                case "6":
                    return new ActionResult(HttpServletResponse.SC_OK,
                            null,
                            getReportByStatus(session,
                                    req.getParameter("pathTxt").replaceAll("'", ""))
                    );

                case "7":
                    return new ActionResult(HttpServletResponse.SC_OK,
                            null,
                            getReportByLanguage(session, renderContext)
                    );

                case "8":
                    return new ActionResult(HttpServletResponse.SC_OK,
                            null,
                            getReportByLanguageDetail(session, renderContext, req.getParameter("reqLang"))
                    );

                case "10":
                    return new ActionResult(HttpServletResponse.SC_OK,
                            null,
                            getReportPagesWithoutTitle(session, renderContext)
                    );

                case "11":
                    return new ActionResult(HttpServletResponse.SC_OK,
                            null,
                            getReportPagesWithoutKeywords(session, renderContext)
                    );

                case "12":
                    return new ActionResult(HttpServletResponse.SC_OK,
                            null,
                            getReportPagesWithoutDescription(session, renderContext)
                    );

                case "13":
                    return new ActionResult(HttpServletResponse.SC_OK,
                            null,
                            getContentFromAnotherSite(session, renderContext)
                    );

                case "14":
                    return new ActionResult(HttpServletResponse.SC_OK,
                            null,
                            getOrphanContent(session, renderContext)
                    );

                case "15":
                    return new ActionResult(HttpServletResponse.SC_OK,
                            null,
                            getLockedContent(session, renderContext)
                    );

                case "16":
                    return new ActionResult(HttpServletResponse.SC_OK,
                            null,
                            getContentWaitingPublication(session, renderContext)
                    );

                case "17":
                    return new ActionResult(HttpServletResponse.SC_OK,
                            null,
                            getReportOverview(session, renderContext)
                    );

                case "18":
                    return new ActionResult(HttpServletResponse.SC_OK,
                            null,
                            getReportCustomCacheContent(session, renderContext)
                    );

                case "19":
                    return new ActionResult(HttpServletResponse.SC_OK,
                            null,
                            getReportAclInheritanceStopped(session, renderContext)
                    );

                default:
                    throw new GovernorException("Invalid reportId: " + reportId);
            }

        } catch (GovernorException gex) {
            logger.error("doExecute(), Error,", gex);
            return new ActionResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            logger.error("doExecute(), Error,", ex);
            return new ActionResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * getReportByAuthor
     *
     * @param session {@link JCRSessionWrapper}
     * @param reportType {@link IReport.SEARCH_CONTENT_TYPE}
     * @param actionType {@link IReport.SEARCH_ACTION_TYPE}
     * @param searchPath @String
     * @return {@link JSONObject}
     * @throws JSONException
     * @throws RepositoryException
     */
    private JSONObject getReportByAuthor(JCRSessionWrapper session, IReport.SEARCH_CONTENT_TYPE reportType, IReport.SEARCH_ACTION_TYPE actionType, String searchPath) throws JSONException, RepositoryException {
        String strQuery = "SELECT * FROM ";
        strQuery += (reportType.equals(IReport.SEARCH_CONTENT_TYPE.PAGE) ? "[jnt:page] " : "[jmix:editorialContent] ");
        strQuery += "AS item WHERE ISDESCENDANTNODE(item,['" + searchPath + "'])";
        return fillIreport(session, strQuery, new ReportByAuthor(reportType, actionType, true), null).getJson();
    }

    /**
     * getReportByAllDate
     *
     * @param session {@link JCRSessionWrapper}
     * @param actionType {@link IReport.SEARCH_ACTION_TYPE}
     * @param searchPath {@link String}
     * @return {@link JSONObject}
     * @throws JSONException
     */
    private JSONObject getReportByAllDate(JCRSessionWrapper session, ReportByAuthor.SEARCH_ACTION_TYPE actionType, String searchPath) throws JSONException {
        String pageQueryStr    = "SELECT * FROM [jnt:page] AS item WHERE ISDESCENDANTNODE(item,['" + searchPath + "'])";
        String contentQueryStr = "SELECT * FROM [jmix:editorialContent] AS item WHERE ISDESCENDANTNODE(item,['" + searchPath + "'])";
        ReportByAllDate report = new ReportByAllDate(actionType, true);
        report = (ReportByAllDate) fillIreport(session, pageQueryStr, report, IReport.SEARCH_CONTENT_TYPE.PAGE);
        report = (ReportByAllDate) fillIreport(session, contentQueryStr, report, IReport.SEARCH_CONTENT_TYPE.CONTENT);
        return report.getJson();
    }

    /**
     * getReportBeforeDate
     *
     * @param session {@link JCRSessionWrapper}
     * @param searchPath {@link String}
     * @param strDate {@link String}
     * @return {@link JSONObject}
     * @throws JSONException
     * @throws RepositoryException
     */
    private JSONObject getReportBeforeDate(JCRSessionWrapper session, String searchPath, String strDate) throws JSONException, RepositoryException {
        String strQuery = "SELECT * FROM [jmix:editorialContent] AS item WHERE ISDESCENDANTNODE(item,['" + searchPath + "']) and item.[jcr:lastModified] <= CAST('" + strDate + "T23:59:59.999Z' AS DATE)";
        return fillIreport(session, strQuery, new ReportBeforeDate(true), null).getJson();
    }

    /**
     * getReportByType
     *
     * @param session {@link JCRSessionWrapper}
     * @param searchPath {@link String}
     * @return {@link JSONObject}
     * @throws JSONException
     * @throws RepositoryException
     */
    private JSONObject getReportByType(JCRSessionWrapper session, String searchPath) throws JSONException, RepositoryException {
        String strQuery = "SELECT * FROM [jmix:editorialContent] AS item WHERE ISDESCENDANTNODE(item,['" + searchPath + "'])";
        return fillIreport(session, strQuery, new ReportByType(), null).getJson();
    }

    /**
     * getReportByTypeDetailed
     *
     * @param session {@link JCRSessionWrapper}
     * @param searchPath {@link String}
     * @return {@link JSONObject}
     * @throws JSONException
     * @throws RepositoryException
     */
    private JSONObject getReportByTypeDetailed(JCRSessionWrapper session, String searchPath) throws JSONException, RepositoryException {
        String strQuery = "SELECT * FROM [jmix:editorialContent] AS item WHERE ISDESCENDANTNODE(item,['" + searchPath + "'])";
        return fillIreport(session, strQuery, new ReportByTypeDetailed(), null).getJson();
    }

    /**
     * getReportByStatus
     *
     * @param session {@link JCRSessionWrapper}
     * @param searchPath {@link String}
     * @return {@link JSONObject}
     * @throws JSONException
     * @throws RepositoryException
     */
    private JSONObject getReportByStatus(JCRSessionWrapper session, String searchPath) throws JSONException, RepositoryException {
        String strQuery = "SELECT * FROM [jmix:editorialContent] AS item WHERE ISDESCENDANTNODE(item,['" + searchPath + "'])";
        return fillIreport(session, strQuery, new ReportByStatus(), null).getJson();
    }

    /**
     * getReportByLanguage
     *
     * @param session {@link JCRSessionWrapper}
     * @param renderContext {@link RenderContext}
     * @return {@link JSONObject}
     * @throws JSONException
     * @throws RepositoryException
     */
    private JSONObject getReportByLanguage(JCRSessionWrapper session, RenderContext renderContext) throws JSONException, RepositoryException {
        return fillIreport(session, "", new ReportByLanguage(renderContext.getSite()), null).getJson();
    }

    /**
     * getReportByLanguageDetail
     *
     * @param session {@link JCRSessionWrapper}
     * @param renderContext {@link RenderContext}
     * @param language {@link String}
     * @return {@link JSONObject}
     * @throws JSONException
     * @throws RepositoryException
     */
    private JSONObject getReportByLanguageDetail(JCRSessionWrapper session, RenderContext renderContext, String language) throws JSONException, RepositoryException {
        String strQuery = "SELECT item.* FROM [jmix:editorialContent] AS item WHERE ISDESCENDANTNODE(item,['" + renderContext.getSite().getPath() + "']) ORDER BY item.date ";
        return fillIreport(session, strQuery, new ReportByLanguageDetailed(renderContext.getSite(), language), null).getJson();
    }

    /**
     * getReportPagesWithoutTitle
     *
     * @param session {@link JCRSessionWrapper}
     * @param renderContext {@link RenderContext}
     * @return {@link JSONObject}
     * @throws JSONException
     * @throws RepositoryException
     */
    private JSONObject getReportPagesWithoutTitle(JCRSessionWrapper session, RenderContext renderContext) throws JSONException, RepositoryException {
        String pageQueryStr = "SELECT * FROM [jnt:page] AS item WHERE ISDESCENDANTNODE(item,['"+ renderContext.getSite().getPath() +"'])";
        return fillIreport(session, pageQueryStr, new ReportPagesWithoutTitle(renderContext.getSite()), null).getJson();
    }

    /**
     * getReportPagesWithoutKeywords
     *
     * @param session {@link JCRSessionWrapper}
     * @param renderContext {@link RenderContext}
     * @return {@link JSONObject}
     * @throws JSONException
     * @throws RepositoryException
     */
    private JSONObject getReportPagesWithoutKeywords(JCRSessionWrapper session, RenderContext renderContext) throws JSONException, RepositoryException {
        String pageQueryStr = "SELECT * FROM [jnt:page] AS item WHERE ISDESCENDANTNODE(item,['"+ renderContext.getSite().getPath() +"'])";
        return fillIreport(session, pageQueryStr, new ReportPagesWithoutKeyword(renderContext.getSite()), null).getJson();
    }

    /**
     * getReportPagesWithoutDescription
     *
     * @param session {@link JCRSessionWrapper}
     * @param renderContext {@link RenderContext}
     * @return {@link JSONObject}
     * @throws JSONException
     * @throws RepositoryException
     */
    private JSONObject getReportPagesWithoutDescription(JCRSessionWrapper session, RenderContext renderContext) throws JSONException, RepositoryException {
        String pageQueryStr = "SELECT * FROM [jnt:page] AS item WHERE ISDESCENDANTNODE(item,['"+ renderContext.getSite().getPath() +"'])";
        return fillIreport(session, pageQueryStr, new ReportPagesWithoutDescription(renderContext.getSite()), null).getJson();
    }

    /**
     * getContentFromAnotherSite
     *
     * @param session {@link JCRSessionWrapper}
     * @param renderContext {@link RenderContext}
     * @return {@link JSONObject}
     * @throws JSONException
     * @throws RepositoryException
     */
    private JSONObject getContentFromAnotherSite(JCRSessionWrapper session, RenderContext renderContext) throws JSONException, RepositoryException {
        String pageQueryStr = "SELECT * FROM [jnt:contentReference] AS item WHERE ISDESCENDANTNODE(item,['"+ renderContext.getSite().getPath() +"'])";
        return fillIreport(session, pageQueryStr, new ReportContentFromAnotherSite(renderContext.getSite()), null).getJson();
    }

    /**
     * getOrphanContent
     *
     * @param session {@link JCRSessionWrapper}
     * @param renderContext {@link RenderContext}
     * @return {@link JSONObject}
     * @throws JSONException
     * @throws RepositoryException
     */
    private JSONObject getOrphanContent(JCRSessionWrapper session, RenderContext renderContext) throws JSONException, RepositoryException {
        String pageQueryStr    = "SELECT * FROM [jnt:page] AS item WHERE ISDESCENDANTNODE(item,['"+renderContext.getSite().getPath()+"'])";
        String contentQueryStr = "SELECT * FROM [jmix:editorialContent] AS item WHERE ISDESCENDANTNODE(item,['"+renderContext.getSite().getPath()+"'])";
        ReportOrphanContent report = new ReportOrphanContent(renderContext.getSite(), session);
        report = (ReportOrphanContent)fillIreport(session, pageQueryStr, report, IReport.SEARCH_CONTENT_TYPE.PAGE);
        report = (ReportOrphanContent)fillIreport(session, contentQueryStr, report, IReport.SEARCH_CONTENT_TYPE.CONTENT);
        return report.getJson();
    }

    /**
     * getLockedContent
     *
     * @param session {@link JCRSessionWrapper}
     * @param renderContext {@link RenderContext}
     * @return {@link JSONObject}
     * @throws JSONException
     * @throws RepositoryException
     */
    private JSONObject getLockedContent(JCRSessionWrapper session, RenderContext renderContext) throws JSONException, RepositoryException {
        String pageQueryStr    = "SELECT * FROM [jnt:page] AS item WHERE ISDESCENDANTNODE(item,['"+renderContext.getSite().getPath()+"'])";
        String contentQueryStr = "SELECT * FROM [jmix:editorialContent] AS item WHERE ISDESCENDANTNODE(item,['"+renderContext.getSite().getPath()+"'])";
        ReportLockedContent report = new ReportLockedContent(renderContext.getSite());
        report = (ReportLockedContent)fillIreport(session, pageQueryStr, report, IReport.SEARCH_CONTENT_TYPE.PAGE);
        report = (ReportLockedContent)fillIreport(session, contentQueryStr, report, IReport.SEARCH_CONTENT_TYPE.CONTENT);
        return report.getJson();
    }

    /**
     * getContentWaitingPublication
     *
     * @param session {@link JCRSessionWrapper}
     * @param renderContext {@link RenderContext}
     * @return {@link JSONObject}
     * @throws JSONException
     * @throws RepositoryException
     */
    private JSONObject getContentWaitingPublication(JCRSessionWrapper session, RenderContext renderContext) throws JSONException, RepositoryException {
        String pageQueryStr    = "SELECT * FROM [jnt:page] AS item WHERE ISDESCENDANTNODE(item,['"+renderContext.getSite().getPath()+"'])";
        String contentQueryStr = "SELECT * FROM [jmix:editorialContent] AS item WHERE ISDESCENDANTNODE(item,['"+renderContext.getSite().getPath()+"'])";
        ReportContentWaitingPublication report = new ReportContentWaitingPublication(renderContext.getSite());
        report = (ReportContentWaitingPublication)fillIreport(session, pageQueryStr, report, IReport.SEARCH_CONTENT_TYPE.PAGE);
        report = (ReportContentWaitingPublication)fillIreport(session, contentQueryStr, report, IReport.SEARCH_CONTENT_TYPE.CONTENT);
        return report.getJson();
    }

    /**
     * getReportOverview
     *
     * @param session {@link JCRSessionWrapper}
     * @param renderContext {@link RenderContext}
     * @return {@link JSONObject}
     * @throws JSONException
     * @throws RepositoryException
     * @throws JahiaException
     */
    private JSONObject getReportOverview(JCRSessionWrapper session, RenderContext renderContext) throws JSONException, RepositoryException, JahiaException {
        String pageQueryStr = "SELECT * FROM [jnt:page] AS item WHERE ISDESCENDANTNODE(item,['"+ renderContext.getSite().getPath() +"'])";
        return fillIreport(session, pageQueryStr, new ReportOverview(renderContext.getSite()), null).getJson();
    }

    /**
     * getReportCustomCacheContent
     *
     * @param session {@link JCRSessionWrapper}
     * @param renderContext {@link RenderContext}
     * @return {@link JSONObject}
     * @throws JSONException
     * @throws RepositoryException
     * @throws JahiaException
     */
    private JSONObject getReportCustomCacheContent(JCRSessionWrapper session, RenderContext renderContext) throws JSONException, RepositoryException, JahiaException {
        String pageQueryStr = "SELECT * FROM [jnt:content] AS item WHERE item.[j:expiration] IS NOT NULL AND ISDESCENDANTNODE(item,['"+ renderContext.getSite().getPath() +"'])";
        return fillIreport(session, pageQueryStr, new ReportCustomCacheContent(renderContext.getSite()), null).getJson();
    }

    /**
     * getReportAclInheritanceStopped
     *
     * @param session {@link JCRSessionWrapper}
     * @param renderContext {@link RenderContext}
     * @return {@link JSONObject}
     * @throws JSONException
     * @throws RepositoryException
     * @throws JahiaException
     */
    private JSONObject getReportAclInheritanceStopped(JCRSessionWrapper session, RenderContext renderContext) throws JSONException, RepositoryException, JahiaException {
        String pageQueryStr = "SELECT * FROM [jnt:page] AS item WHERE ISDESCENDANTNODE(item,['"+ renderContext.getSite().getPath() +"'])";
        return fillIreport(session, pageQueryStr, new ReportAclInheritanceStopped(renderContext.getSite()), null).getJson();
    }

    /**
     * getQueryResult
     * <p>The method returns the NodeIterator,
     * result of the query execution.</p>
     *
     * @param queryStr {@link String}
     * @param session {@link JCRSessionWrapper}
     * @return {@link NodeIterator}
     * @throws RepositoryException
     */
    private NodeIterator getQueryResult(String queryStr, JCRSessionWrapper session) throws RepositoryException{
            // Getting the items  nodes.
            Query query = session.getWorkspace().getQueryManager().createQuery(queryStr, Query.JCR_SQL2);
            return  query.execute().getNodes();
    }

    /**
     * fillIreport
     * <p>The method is the in charge to fill the IReport object,
     * with the data obtained from the query.</p>
     *
     * @param session {@link JCRSessionWrapper}
     * @param strQuery {@link String}
     * @param report {@link IReport}
     * @param contentType {@link IReport.SEARCH_CONTENT_TYPE}
     * @return {@link IReport}
     * @throws JSONException
     */
    private IReport fillIreport(JCRSessionWrapper session, String strQuery, IReport report, IReport.SEARCH_CONTENT_TYPE contentType) throws JSONException {
        if(StringUtils.isNotEmpty(strQuery)) {
            try {
             /* filling the content nodes */
                NodeIterator iterator = getQueryResult(strQuery, session);
                while (iterator.hasNext()) {
                    JCRNodeWrapper nodeItem = (JCRNodeWrapper) iterator.next();
                    report.addItem(nodeItem, contentType);
                }
            } catch (RepositoryException rex) {
                logger.error("getAjaxFromQuery: problem executing the jcr:query[" + strQuery + "]", rex);
            }
        }
        return report;
    }


}