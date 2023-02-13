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
<template:addResources resources="reports.js" type="javascript" />
<fmt:message key="cgnt_contentReports.report" var="labelReport"/>
<fmt:message key="cgnt_contentReports.menu.contentReports.byAuthor" var="labelAuthor"/>
<fmt:message key="cgnt_contentReports.report.label.total" var="labelTotal"/>
<fmt:message key="cgnt_contentReports.report.label.yes" var="labelYes"/>
<fmt:message key="cgnt_contentReports.report.label.no" var="labelNo"/>
<fmt:message key="cgnt_contentReports.report.column.created" var="labelCreated"/>
<fmt:message key="cgnt_contentReports.menu.contentReports.detailsBy" var="labelDetailsBy"/>
<fmt:message key="cgnt_contentReports.report.label.contentCreated" var="labelCreatedBy"/>
<fmt:message key="cgnt_contentReports.report.loading" var="labelLoading"/>
<c:set var="principalGridLabel" value="${labelReport} ${labelAuthor}"/>
<c:url value="${url.base}${docPath}${renderContext.mainResource.node.path}" var="currentNodePath"/>

<div class="panel">
    <div class="panel-body">
        <div class="row">
            <!-- div table and contents -->
            <div class="col-md-6" >

                <!-- select type of search -->
                <div class="row">
                    <div class="col-md-12">
                        <label class="label-form"> <fmt:message key="cgnt_contentReports.report.typeSearch"/> </label>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12">
                        <div class="form-check">
                            <label class="form-check-label">
                                <input class="form-check-input" type="radio" name="typeSearch" value="pages" checked="checked">
                                <fmt:message key="cgnt_contentReports.report.typeSearch.pagesOnly"/>
                            </label>
                        </div>
                        <div class="form-check">
                            <label class="form-check-label">
                                <input class="form-check-input" type="radio" name="typeSearch"  value="ccntent">
                                <fmt:message key="cgnt_contentReports.report.typeSearch.allContent"/>
                            </label>
                        </div>
                    </div>
                </div>

                <div class="row"><div class="col-md-12"><hr/></div></div>

                <!-- select type of search -->
                <div class="row">
                    <div class="col-md-12">
                        <label class="label-form"> <fmt:message key="cgnt_contentReports.report.typeAuthor"/> </label>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12">
                        <div class="form-check">
                            <label class="form-check-label">
                                <input class="form-check-input" type="radio" name="typeAuthor" value="created" checked="checked">
                                <fmt:message key="cgnt_contentReports.report.typeAuthor.creator"/>
                            </label>
                        </div>
                        <div class="form-check">
                            <label class="form-check-label">
                                <input class="form-check-input" type="radio" name="typeAuthor"  value="modified">
                                <fmt:message key="cgnt_contentReports.report.typeAuthor.lastModifier"/>
                            </label>
                        </div>
                    </div>
                </div>

                <div class="row"><div class="col-md-12"><hr/></div></div>

                <!-- select path -->
                <div class="row">
                    <div class="col-md-12">
                        <label class="label-form"> <fmt:message key="cgnt_contentReports.report.selectPath"/> </label>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-2">
                        <button type="button" class="btn btn-default" onclick="callTreeView('pathTxt',null,'jnt:virtualsite,jnt:page,jnt:navMenuText')">
                            <span class="glyphicon glyphicon-folder-open"></span>
                            &nbsp;<fmt:message key="cgnt_contentReports.report.browse"/>
                        </button>
                    </div>
                    <div class="col-md-10">
                        <input type="text" id="pathTxt" name="pathTxt" class="form-control" readonly="true"  value="${renderContext.site.path}">
                    </div>
                </div>

                <div class="row"><div class="col-md-12"><hr/></div></div>

                <!-- search button -->
                <div class="row">
                    <div class="col-md-12">
                        <button type="button" class="btn btn-default" onclick="fillReportByAuthor('${currentNodePath}', '${principalGridLabel}', '${labelDetailsBy}', '${labelCreatedBy}' , '${labelTotal}', '${labelYes}', '${labelNo}', '${labelCreated}', '${labelLoading}')">
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
                        <h5 class="panel-title" id="rba-principal-grid"><fmt:message key="cgnt_contentReports.report"/>&nbsp;<fmt:message key="cgnt_contentReports.menu.contentReports.byAuthor"/></h5>
                    </div>
                    <div>&nbsp;</div>
                    <table width="100%" class="display reports-data-table" id="byAuthorTable" cellspacing="0">
                        <thead>
                            <tr>
                                <th><fmt:message key="cgnt_contentReports.report.column.author"/></th>
                                <th id="column-ContentAuthor"><fmt:message key="cgnt_contentReports.report.column.created"/></th>
                                <th>%</th>
                            </tr>
                        </thead>
                        <tfoot>
                        <tr>
                            <th></th>
                            <th></th>
                            <th></th>
                        </tr>
                        </tfoot>
                        <tbody></tbody>
                    </table>
                </div>
            </div>
        </div>

        <div class="row"><div class="col-md-12"><hr></div></div>

        <div class="row">
            <!-- div table -->
            <div class="col-md-12" >
                    <canvas id="canvas"></canvas>
            </div>
        </div>


    </div>
</div>


<!-- nModal Window: Author Detail -->
<div id="authorDetailModel" class="nModal fade window-detail-nModal" role="dialog">
    <div class="nModal-dialog">

        <!-- nModal content-->
        <div class="nModal-content">
            <div class="nModal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <!--h4 class="nModal-title">nModal Header</h4-->
            </div>
            <div class="nModal-body">
                <div class="panel panel-primary panel-primary-datatables filterable">

                    <div class="panel-heading">
                        <h5 class="panel-title" id="rba-secondary-grid-detail"></h5>
                    </div>
                    <div>&nbsp;</div>
                    <table width="100%" class="display reports-data-table" id="byAuthorDetailTable" cellspacing="0">
                        <thead>
                        <tr>
                            <th><fmt:message key="cgnt_contentReports.report.column.type"/></th>
                            <th><fmt:message key="cgnt_contentReports.report.column.items"/></th>
                            <th>%</th>
                        </tr>
                        </thead>
                        <tfoot>
                        <tr>
                            <th></th>
                            <th></th>
                            <th></th>
                        </tr>
                        </tfoot>
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



<!-- nModal Window: Page Detail -->
<div id="authorPageModel" class="nModal fade window-detail-nModal" role="dialog">
    <div class="nModal-dialog">

        <!-- nModal content-->
        <div class="nModal-content">
            <div class="nModal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <!--h4 class="nModal-title">nModal Header</h4-->
            </div>
            <div class="nModal-body">
                <div class="panel panel-primary panel-primary-datatables filterable">

                    <div class="panel-heading">
                        <h5 class="panel-title" id="rba-secondary-grid-page"></h5>
                    </div>
                    <div>&nbsp;</div>
                    <table width="100%" class="display reports-data-table" id="byAuthorPageTable" cellspacing="0">
                        <thead>
                            <tr>
                                <th><fmt:message key="cgnt_contentReports.report.column.title"/></th>
                                <th><fmt:message key="cgnt_contentReports.report.column.type"/></th>
                                <th><fmt:message key="cgnt_contentReports.report.column.created"/></th>
                                <th><fmt:message key="cgnt_contentReports.report.column.lastModified"/></th>
                                <th><fmt:message key="cgnt_contentReports.report.column.published"/></th>
                                <th><fmt:message key="cgnt_contentReports.report.column.lock"/></th>
                            </tr>
                        </thead>
                        <tfoot>
                            <tr>
                                <th></th>
                                <th></th>
                                <th></th>
                                <th></th>
                                <th></th>
                                <th></th>
                            </tr>
                        </tfoot>
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
