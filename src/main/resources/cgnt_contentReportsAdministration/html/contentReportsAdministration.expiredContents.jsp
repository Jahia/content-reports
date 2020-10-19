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

                <!-- search button -->
                <div class="row">
                    <div class="col-md-12">
                        <h1><fmt:message key="cgnt_contentReports.menu.contentReports.expiredContents"/></h1>
                    </div>
                </div>
            </div>
        </div>
        <br>
        <div class="row">
            <div class="col-md-6" >
                <!-- select path -->
                <div class="row">
                    <div class="col-md-12">
                        <label class="label-form"> <fmt:message key="cgnt_contentReports.report.selectPath"/> </label>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-2">
                        <button type="button" class="btn btn-default"
                                onclick="callTreeView('searchPathExpiredContents',null,'jnt:virtualsite,jnt:page')">
                            <span class="glyphicon glyphicon-folder-open"></span>
                            &nbsp;<fmt:message key="cgnt_contentReports.report.browse"/>
                        </button>
                    </div>
                    <div class="col-md-10">
                        <input type="text" id="searchPathExpiredContents" name="searchPathExpiredContents" class="form-control" readonly="true"
                               value="${renderContext.site.path}" >
                    </div>
                </div>

                <div class="row"><div class="col-md-12"><hr/></div></div>

                <!-- search button -->
                <div class="row">
                    <div class="col-md-12">
                        <button type="button" class="btn btn-default" onclick="fillReportByExpiredContents('${currentNodePath}')">
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
                        <h5 class="panel-title" id="rba-principal-grid-rdau"><fmt:message key="cgnt_contentReports.report"/>&nbsp;<fmt:message key="cgnt_contentReports.menu.contentReports.byDate"/></h5>
                    </div>
                    <div>&nbsp;</div>
                    <table width="100%" class="display reports-data-table" id="expiredContentTable" cellspacing="0">
                        <thead>
                        <tr>
                            <th><fmt:message key="cgnt_contentReports.report.column.name"/></th>
                            <th><fmt:message key="cgnt_contentReports.report.column.path"/></th>
                            <th><fmt:message key="cgnt_contentReports.report.column.type"/></th>
                            <th><fmt:message key="cgnt_contentReports.report.column.expiredDate"/></th>
                        </tr>
                        </thead>
                        <tfoot>
                        <tr>
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
