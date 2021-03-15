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
<fmt:message key="cgnt_contentReports.report.loading" var="labelLoading"/>
<fmt:message key="cgnt_contentReports.report.label.total" var="labelTotal"/>
<c:url value="${url.base}${docPath}${renderContext.mainResource.node.path}" var="currentNodePath"/>


<div class="panel">
    <div class="panel-body">

        <!--  <div class="row"><div class="col-md-12"><hr/></div></div>-->
         <br>
         <div class="row">
             <!-- div tabla y contenidos -->
            <div class="col-md-6" >

                <!-- select path where to search links -->
                <div class="row">
                    <div class="col-md-12">
                        <label class="label-form"><fmt:message key="cgnt_contentReports.report.displayReference"/></label>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-2">
                        <button type="button" class="btn btn-default"
                                onclick="callTreeView('pathTxtOrigin',null,'jnt:virtualsite,jnt:page,jnt:navMenuText')">
                            <span class="glyphicon glyphicon-folder-open"></span>
                            &nbsp;<fmt:message key="cgnt_contentReports.report.browse"/>
                        </button>
                    </div>
                    <div class="col-md-10">
                        <input type="text" id="pathTxtOrigin" name="pathTxtOrigin" class="form-control" readonly="true"
                               value="${renderContext.site.path}" >
                    </div>
                </div>

                <!-- select the destination of the searched links -->
                <div class="row">
                    <div class="col-md-12">
                        <label class="label-form"><fmt:message key="cgnt_contentReports.report.pointingReference"/></label>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-2">
                        <button type="button" class="btn btn-default"
                                onclick="callTreeView('pathTxtDestination','/sites/','jnt:virtualsite,jnt:page,jnt:navMenuText','/sites/systemsite')">
                            <span class="glyphicon glyphicon-folder-open"></span>
                            &nbsp;<fmt:message key="cgnt_contentReports.report.browse"/>
                        </button>
                    </div>
                    <div class="col-md-10">
                        <input type="text" id="pathTxtDestination" name="pathTxtDestination" class="form-control" readonly="true"
                               value="${renderContext.site.path}" >
                    </div>
                </div>

                <div class="row"><div class="col-md-12"><hr/></div></div>
                <!-- search button -->
                <div class="row">
                    <div class="col-md-12">
                        <button type="button" class="btn btn-default" onclick="fillReportDisplayLinks('${currentNodePath}', '${labelLoading}', '${labelTotal}')">
                            <span class="glyphicon glyphicon-search"></span> <fmt:message key="cgnt_contentReports.report.search"/>
                        </button>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12">
                        <span><fmt:message key="cgnt_contentReports.report.referencesHint"/></span>
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
                        <h5 class="panel-title" id=""><fmt:message key="cgnt_contentReports.report"/>&nbsp;<fmt:message key="cgnt_contentReports.menu.contentReports.wipContent"/></h5>
                    </div>

                    <table width="100%" class="display reports-data-table" id="displayLinksTable" cellspacing="0">
                        <thead>
                            <tr>
                                <th><fmt:message key="cgnt_contentReports.report.column.type"/></th>
                                <th><fmt:message key="cgnt_contentReports.report.column.referencedPath"/></th>
                                <th><fmt:message key="cgnt_contentReports.report.column.referencePath"/></th>
                                <th><fmt:message key="cgnt_contentReports.report.column.lastModifiedReferenced"/></th>
                                <th><fmt:message key="cgnt_contentReports.report.column.referenceLink"/></th>
                            </tr>
                        </thead>
                        <tbody></tbody>
                    </table>
                </div>
            </div>
        </div>


    </div>
</div>
