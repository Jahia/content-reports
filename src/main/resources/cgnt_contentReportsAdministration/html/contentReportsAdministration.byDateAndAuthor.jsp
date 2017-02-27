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
<c:set var="principalGridLabel" value="${labelReport} ${labelDate}"/>
<c:url value="${url.base}${docPath}${renderContext.mainResource.node.path}" var="currentNodePath"/>

<div class="panel">
    <div class="panel-body">
        <div class="row">
            <!-- div tabla y contenidos -->
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
                                <input class="form-check-input" type="radio" name="typeOfSearch" value="pages" checked="checked">
                                <fmt:message key="cgnt_contentReports.report.typeSearch.pagesOnly"/>
                            </label>
                        </div>
                        <div class="form-check">
                            <label class="form-check-label">
                                <input class="form-check-input" type="radio" name="typeOfSearch"  value="ccntent">
                                <fmt:message key="cgnt_contentReports.report.typeSearch.allContent"/>
                            </label>
                        </div>
                    </div>
                </div>

                <div class="row"><div class="col-md-12"><hr/></div></div>

                <!-- select of period of time -->
                <div class="row">
                    <div class="col-md-12">
                        <input class="form-check-input" type="checkbox" id="dateAndAuthorSearchByDate" name="searchByDate"  checked="checked">
                        <fmt:message key="cgnt_contentReports.report.label.selectDateRange"/>
                    </div>
                </div>
                <div class="row searchableByDate">
                    <div class="col-md-12">
                        <div class="form-check">
                            <label class="form-check-label">
                                <input class="form-check-input" type="radio" name="typeDateSearch" value="created" checked="checked">
                                <fmt:message key="cgnt_contentReports.report.date.created"/>
                            </label>
                        </div>
                        <div class="form-check">
                            <label class="form-check-label">
                                <input class="form-check-input" type="radio" name="typeDateSearch"  value="modified">
                                <fmt:message key="cgnt_contentReports.report.date.modified" />
                            </label>
                        </div>
                    </div>
                </div>
                <div class="row searchableByDate">
                    <div class="col-md-12">
                        <fmt:message key="cgnt_contentReports.report.label.startDate"/>
                        <input type="text" class="datepicker form-control" value="" id="dateBegin" name="dateBegin" data-date-format="yyyy-mm-dd" placeholder="yyy-mm-dd">
                    </div>
                </div>
                <div class="row searchableByDate">
                    <div class="col-md-12">
                        <fmt:message key="cgnt_contentReports.report.label.endDate"/>
                        <input type="text" class="datepicker form-control" value="" id="dateEnd" name="dateEnd" data-date-format="yyyy-mm-dd" placeholder="yyy-mm-dd">
                    </div>
                </div>

                <div class="row"><div class="col-md-12"><hr/></div></div>

                <!-- Select an author -->


                <div class="row">
                    <div class="col-md-12">
                        <input class="form-check-input" type="checkbox" id="selectAuthor" name="searchAuthor"  checked="checked">
                        <fmt:message key="cgnt_contentReports.report.column.selectAuthor"/>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12 searchableByAuthor">
                        <fmt:message key="cgnt_contentReports.report.username"/>
                        <input type="text" class="form-control" value="" id="searchUsername" name="searchUsername">
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12 searchableByAuthor">
                        <div class="form-check">
                            <label class="form-check-label">
                                <input class="form-check-input" type="radio" name="typeAuthorSearch" value="created" checked="checked">
                                <fmt:message key="cgnt_contentReports.report.typeAuthor.creator"/>
                            </label>
                        </div>
                        <div class="form-check">
                            <label class="form-check-label">
                                <input class="form-check-input" type="radio" name="typeAuthorSearch"  value="modified">
                                <fmt:message key="cgnt_contentReports.report.typeAuthor.lastModifier" />
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
                        <button type="button" class="btn btn-default" onclick="callTreeView('pathTxtRDADA')">
                            <span class="glyphicon glyphicon-folder-open"></span>
                            &nbsp;<fmt:message key="cgnt_contentReports.report.browse"/>
                        </button>
                    </div>
                    <div class="col-md-10">
                        <input type="text" id="pathTxtRDADA" name="pathTxtRDADA" class="form-control" readonly="true"  value="${renderContext.site.path}" >
                    </div>
                </div>

                <div class="row"><div class="col-md-12"><hr/></div></div>

                <!-- search button -->
                <div class="row">
                    <div class="col-md-12">
                        <button type="button" class="btn btn-default" onclick="fillReportByAllDateAndAuthor('${currentNodePath}', '${principalGridLabel}', '${labelTotal}', '${labelLoading}')">
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
                        <h5 class="panel-title" id="rba-principal-grid-rda"><fmt:message key="cgnt_contentReports.report"/>&nbsp;<fmt:message key="cgnt_contentReports.menu.contentReports.byDate"/></h5>
                    </div>
                    <div>&nbsp;</div>
                    <table width="100%" class="display reports-data-table" id="byAllDateAndAuthorTable" cellspacing="0">
                        <thead>
                            <tr>
                                <th><fmt:message key="cgnt_contentReports.report.column.title"/></th>
                                <th><fmt:message key="cgnt_contentReports.report.column.path"/></th>
                                <th><fmt:message key="cgnt_contentReports.report.column.type"/></th>
                                <th><fmt:message key="cgnt_contentReports.report.column.created"/></th>
                                <th><fmt:message key="cgnt_contentReports.report.column.modified"/></th>
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
                                <th></th>
                            </tr>
                        </tfoot>
                        <tbody></tbody>
                    </table>
                </div>
            </div>
        </div>

        <div class="row"><div class="col-md-12"><hr/></div></div>


    </div>
</div>
