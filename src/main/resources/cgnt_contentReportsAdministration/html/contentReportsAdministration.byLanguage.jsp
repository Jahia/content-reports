<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="ui" uri="http://www.jahia.org/tags/uiComponentsLib" %>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions" %>
<%@ taglib prefix="query" uri="http://www.jahia.org/tags/queryLib" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>
<%@ taglib prefix="s" uri="http://www.jahia.org/tags/search" %>
<%@ taglib prefix="html" uri="http://www.springframework.org/tags/form" %>
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>
<%--@elvariable id="vfsFactory" type="org.jahia.modules.external.vfs.factory.VFSMountPointFactory"--%>
<template:addResources resources="reports.js" type="javascript" />
<fmt:message key="cgnt_contentReports.report" var="labelReport"/>
<fmt:message key="cgnt_contentReports.menu.contentReports.byDate" var="labelDate"/>
<fmt:message key="cgnt_contentReports.report.label.total" var="labelTotal"/>
<fmt:message key="cgnt_contentReports.report.label.yes" var="labelYes"/>
<fmt:message key="cgnt_contentReports.report.label.no" var="labelNo"/>
<fmt:message key="cgnt_contentReports.menu.contentReports.detailsBy" var="labelDetailsBy"/>
<fmt:message key="cgnt_contentReports.report.label.contentCreated" var="labelCreatedBy"/>
<fmt:message key="cgnt_contentReports.report.loading" var="labelLoading"/>
<fmt:message key="cgnt_contentReports.report.publish" var="labelPublish"/>
<c:url value="${url.base}${docPath}${renderContext.mainResource.node.path}" var="currentNodePath"/>


<div class="panel">
    <div class="panel-body">
        <div class="row">
            <!-- div tabla y contenidos -->
            <div class="col-md-6" >

                <!-- search button -->
                <div class="row">
                    <div class="col-md-12">
                        <button type="button" class="btn btn-default" onclick="fillReportByLanguage('${currentNodePath}', '${labelYes}', '${labelNo}', '${labelLoading}', '${labelPublish}')">
                            <span class="glyphicon glyphicon-search"></span> <fmt:message key="cgnt_contentReports.report.search"/>
                        </button>
                    </div>
                </div>

                <div class="row"><div class="col-md-12"><hr/></div></div>
            </div>
        </div>

        <div class="row">
            <!-- div tabla -->
            <div class="col-md-12">
                <div class="panel panel-primary panel-primary-datatables filterable">

                    <div class="panel-heading">
                        <h5 class="panel-title" id=""><fmt:message key="cgnt_contentReports.report"/>&nbsp;<fmt:message key="cgnt_contentReports.menu.contentReports.byLanguage"/></h5>
                    </div>

                    <table width="100%" class="display reports-data-table" id="byLanguageTable" cellspacing="0">
                        <thead>
                            <tr>
                                <th><fmt:message key="cgnt_contentReports.report.column.language"/></th>
                                <th><fmt:message key="cgnt_contentReports.report.column.code"/></th>
                                <th><fmt:message key="cgnt_contentReports.report.column.availableEdit"/></th>
                                <th><fmt:message key="cgnt_contentReports.report.column.availableLive"/></th>
                            </tr>
                        </thead>
                        <tbody></tbody>
                    </table>
                </div>
            </div>
        </div>


    </div>
</div>




<!-- nModal Window: Page Language Detail -->
<div id="languageDetailPageModel" class="nModal fade window-detail-nModal" role="dialog">
    <div class="nModal-dialog">

        <!-- nModal content-->
        <div class="nModal-content">
            <div class="nModal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="nModal-body">
                <div class="panel panel-primary panel-primary-datatables filterable">
                    <div class="panel-heading">
                        <h5 class="panel-title" id="language-detail-grid-page"></h5>
                    </div>
                    <div>&nbsp;</div>
                    <table width="100%" class="display reports-data-table" id="byLanguageDetailTable" cellspacing="0">
                        <thead>
                            <tr>
                                <th><fmt:message key="cgnt_contentReports.report.column.name"/></th>
                                <th><fmt:message key="cgnt_contentReports.report.column.langContent"/></th>
                                <th><fmt:message key="cgnt_contentReports.report.column.type"/></th>
                                <th><fmt:message key="cgnt_contentReports.report.column.created"/></th>
                                <th><fmt:message key="cgnt_contentReports.report.column.lastModified"/></th>
                                <th><fmt:message key="cgnt_contentReports.report.column.by"/></th>
                                <th><fmt:message key="cgnt_contentReports.report.column.published"/></th>
                                <th><fmt:message key="cgnt_contentReports.report.column.lock"/></th>
                            </tr>
                        </thead>
                        <tbody></tbody>
                    </table>
                </div>

            </div>
            <div class="nModal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>

    </div>
</div>
