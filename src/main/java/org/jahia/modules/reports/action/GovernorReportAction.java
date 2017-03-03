package org.jahia.modules.reports.action;

import org.apache.commons.lang.StringUtils;
import org.jahia.modules.reports.bean.*;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.reports.exception.GovernorException;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            BaseReport report = getReport(renderContext, req);

            report.execute(session,
                    req.getParameter("start") != null ? Integer.parseInt(req.getParameter("start")) : 0,
                    req.getParameter("length") != null ? Integer.parseInt(req.getParameter("length")) : 10);

            return new ActionResult(HttpServletResponse.SC_OK,null, report.getJson());
        } catch (GovernorException gex) {
            logger.error("doExecute(), Error,", gex);
            return new ActionResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            logger.error("doExecute(), Error,", ex);
            return new ActionResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private BaseReport getReport(RenderContext renderContext, HttpServletRequest req) throws GovernorException {
        String reportId = req.getParameter("reportId");
        String sortColParam = req.getParameter("order[0][column]");
        String orderParam = req.getParameter("order[0][dir]");

        int sortCol;
        String order;
        if (sortColParam == null) {
            sortCol = 0; // sort on created data
        } else {
            sortCol = Integer.parseInt(sortColParam);
        }
        if (orderParam == null) {
            order = ""; // sort on created data
        } else {
            order = orderParam;
        }

        switch (reportId) {
            case "1":
                return new ReportByAuthor(renderContext.getSite(),
                        req.getParameter("pathTxt").replaceAll("'", ""),
                        (req.getParameter("typeSearch").equalsIgnoreCase("pages")) ? BaseReport.SearchContentType.PAGE : BaseReport.SearchContentType.CONTENT,
                        (req.getParameter("typeAuthor").equalsIgnoreCase("created")) ? BaseReport.SearchActionType.CREATION : BaseReport.SearchActionType.UPDATE,
                        true);

            case "2":
                return new ReportByAllDate(renderContext.getSite(),
                        (req.getParameter("typeAuthor").equalsIgnoreCase("created")) ? BaseReport.SearchActionType.CREATION : BaseReport.SearchActionType.UPDATE,
                        req.getParameter("pathTxt").replaceAll("'", ""), true);
            case "3":
                return new ReportBeforeDate(renderContext.getSite(), req.getParameter("pathTxt").replaceAll("'", ""), req.getParameter("date").replaceAll("'", ""), true);
            case "4":
                return new ReportByType(renderContext.getSite(), req.getParameter("pathTxt").replaceAll("'", ""));
            case "5":
                return new ReportByTypeDetailed(renderContext.getSite(), req.getParameter("pathTxt").replaceAll("'", ""));
            case "6":
                return new ReportByStatus(renderContext.getSite(), req.getParameter("pathTxt").replaceAll("'", ""));
            case "7":
                return new ReportByLanguage(renderContext.getSite());
            case "8":
                return new ReportByLanguageDetailed(renderContext.getSite(), req.getParameter("reqLang"));
            case "10":
                return new ReportPagesWithoutTitle(renderContext.getSite(), req.getParameter("language"));
            case "11":
                return new ReportPagesWithoutKeyword(renderContext.getSite(), sortCol, order);
            case "12":
                return new ReportPagesWithoutDescription(renderContext.getSite(), req.getParameter("language"));
            case "13":
                return new ReportContentFromAnotherSite(renderContext.getSite());
            case "14":
                return new ReportOrphanContent(renderContext.getSite());
            case "15":
                return new ReportLockedContent(renderContext.getSite(), sortCol, order);
            case "16":
                return new ReportContentWaitingPublication(renderContext.getSite());
            case "17":
                return new ReportOverview(renderContext.getSite());
            case "18":
                return new ReportCustomCacheContent(renderContext.getSite(), sortCol, order);
            case "19":
                return new ReportAclInheritanceStopped(renderContext.getSite());
            case "20":
                return new ReportByDateAndAuthor(renderContext.getSite(),
                        (req.getParameter("typeAuthor").equalsIgnoreCase("created")) ? BaseReport.SearchActionType.CREATION : BaseReport.SearchActionType.UPDATE,
                        req.getParameter("pathTxt").replaceAll("'", ""), req.getParameter("typeSearch"), true, req.getParameter("searchByDate").equals("true"),
                        req.getParameter("typeDateSearch"), req.getParameter("dateBegin"), req.getParameter("dateEnd"),req.getParameter("searchAuthor").equals("true"),
                        req.getParameter("searchUsername"), req.getParameter("typeAuthorSearch"), sortCol, order);
            case "21":
                return new ReportByUnstranslated(renderContext.getSite(),
                        req.getParameter("selectLanguageBU"), req.getParameter("pathTxt").replaceAll("'", ""), req.getParameter("selectTypeSearch"));
            default:
                throw new GovernorException("Invalid reportId: " + reportId);
        }

    }
}