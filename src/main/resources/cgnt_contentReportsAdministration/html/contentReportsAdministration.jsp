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
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>
<template:addResources type="css" resources="
            bootstrap/bootstrap.css,
            bootstrap/sweetalert.css,
            datatables/jquery.dataTables.min.css,
            datatables/buttons.dataTables.min.css,
            bootstrap/datepicker.css,
            content-report-style.css"  />

<template:addResources type="javascript" resources="
            jquery.min.js,
            datatables/jquery.dataTables.min.js,
            bootstrap/bootstrap.js,
            bootstrap/bootstrap-datepicker.js,
            bootstrap/sweetalert.js,
            bootstrap-config-grid.js,
            chartjs/Chart.bundle.js,
            datatables/dataTables.buttons.min.js,
            datatables/buttons.flash.min.js,
            datatables/jszip.min.js,
            datatables/pdfmake.min.js,
            datatables/vfs_fonts.js,
            datatables/buttons.html5.min.js,
            datatables/buttons.print.min.js,
            content-report-app.js"/>

<c:set var="selectedLang" value="${fn:split(fn:split(currentLocale, '_')[0], '-')[0]}"/>
<input type="hidden" name="selectedLang" id="selectedLang" value="${selectedLang}">
<input type="hidden" name="selectedBaseUrl" id="selectedBaseUrl" value="${url.currentModule}">
<input type="hidden" id="baseEdit" value="${url.context}${url.baseEdit}">
<input type="hidden" id="siteKey" value="${renderContext.site.siteKey}">
<input type="hidden" id="contentManagerUrl" value="${url.context}/cms/contentmanager/${renderContext.site.siteKey}/${selectedLang}/browse">
<input type="hidden" id="contextPath" value="${pageContext.request.contextPath}">

<div class="panel panel-primary">
    <div class="panel-heading">
        <fmt:message key="cgnt_contentReports"/>
    </div>

    <div class="panel-body">
        <div class="">
            <c:if test="${param.hideMenu ne 'true'}">
            <!-- Column for menu -->
            <div class="col-md-2" STYLE="margin-top: 15px;" >
                <div style="overflow-x: hidden; min-height: 100px;">
                    <ul class="nav nav-list">
                        <li style="position: fixed; width: 150px"><label class="tree-toggler nav-header" style="cursor:default;pointer-events:none;"><fmt:message key="cgnt_contentReports.menu.contentReports"/></label>
                            <ul class="nav nav-list tree forceVisible">

                                <li>
                                    <a class="nav-menu-link" href="#" onclick="showView('20')">
                                        <fmt:message key="cgnt_contentReports.menu.contentReports.byAuthorAndDate"/>
                                    </a>
                                </li>
                                <li>
                                    <a class="nav-menu-link" href="#" onclick="showView('23')">
                                        <fmt:message key="cgnt_contentReports.menu.contentReports.references"/>
                                    </a>
                                </li>
                                <li>
                                    <a class="nav-menu-link" href="#" onclick="showView('22')">
                                        <fmt:message key="cgnt_contentReports.menu.contentReports.wipContent"/>
                                    </a>
                                </li>
                                <li>
                                    <a class="nav-menu-link" href="#" onclick="showView('15')">
                                        <fmt:message key="cgnt_contentReports.menu.contentReports.contentWaitingPublication"/>
                                    </a>
                                </li>
                                <li>
                                    <a class="nav-menu-link" href="#" onclick="showView('24')">
                                        <fmt:message key="cgnt_contentReports.menu.contentReports.markedForDeletion"/>
                                    </a>
                                </li>
                                <li>
                                    <a class="nav-menu-link" href="#" onclick="showView('25')">
                                        <fmt:message key="cgnt_contentReports.menu.contentReports.publishedContent"/>
                                    </a>
                                </li>                              
                                <!--li>
                                    <a class="nav-menu-link" href="#" onclick="showView('5')">
                                        <fmt:message key="cgnt_contentReports.menu.contentReports.byTypeDetailed"/>
                                    </a>
                                </li-->
                                <li>
                                    <label class="tree-toggler nav-header"><fmt:message key="cgnt_contentReports.menu.contentReports.byLanguage"/></label>

                                    <ul class="nav nav-list tree">
                                        <li>
                                            <a class="nav-menu-link" href="#" onclick="showView('9')">
                                                <fmt:message key="cgnt_contentReports.menu.contentReports.pagesWithoutTitle"/>
                                            </a>
                                        </li>
                                        <li>
                                            <a class="nav-menu-link" href="#" onclick="showView('21')">
                                                <fmt:message key="cgnt_contentReports.menu.contentReports.pagesUnstranslated"/>
                                            </a>
                                        </li>

                                    </ul>
                                </li>


                                <li>
                                    <label class="tree-toggler nav-header"><fmt:message key="cgnt_contentReports.menu.contentReports.byMetadata"/></label>

                                    <ul class="nav nav-list tree">

                                        <li>
                                            <a class="nav-menu-link" href="#" onclick="showView('10')">
                                                <fmt:message key="cgnt_contentReports.menu.contentReports.pagesWithoutKeywords"/>
                                            </a>
                                        </li>
                                        <li>
                                            <a class="nav-menu-link" href="#" onclick="showView('11')">
                                                <fmt:message key="cgnt_contentReports.menu.contentReports.pagesWithoutDescription"/>
                                            </a>
                                        </li>
                                    </ul>
                                </li>
                                <li>
                                    <label class="tree-toggler nav-header"><fmt:message key="cgnt_contentReports.menu.contentReports.system"/></label>

                                    <ul class="nav nav-list tree">
                                        <li>
                                            <a class="nav-menu-link" href="#" onclick="showView('14')">
                                                <fmt:message key="cgnt_contentReports.menu.contentReports.lockedContent"/>
                                            </a>
                                        </li>
                                        <li>
                                            <a class="nav-menu-link" href="#" onclick="showView('17')">
                                                <fmt:message key="cgnt_contentReports.menu.contentReports.customCacheContent"/>
                                            </a>
                                        </li>
                                        <li>
                                            <a class="nav-menu-link" href="#" onclick="showView('18')">
                                                <fmt:message key="cgnt_contentReports.menu.contentReports.aclInheritanceBreak"/>
                                            </a>
                                        </li>
                                        <li>
                                            <a class="nav-menu-link" href="#" onclick="showView('16')">
                                                <fmt:message key="cgnt_contentReports.menu.contentReports.overview"/>
                                            </a>
                                        </li>
                                    </ul>
                                </li>
                            </ul>
                        </li>
                    </ul>
                </div>
            </div>

            </c:if>

            <!-- Column for report content -->
            <div class="${param.hideMenu ne 'true' ? 'col-md-10' : 'col-md-12'}" style="border-left: 1px solid rgba(0,0,0,.14);padding-left: 0 !important;min-height: 100%;position: absolute;right: 0;background: #fafafa;">

                <!-- By Type: detailed -->
                <div id="report-5" class="div-report">
                    <template:include view="byTypeDetailed"/>
                </div>

                <!-- By Language -->
                <div id="report-7" class="div-report">
                    <template:include view="byLanguage"/>
                </div>
                <!-- Without Title -->
                <div id="report-9" class="div-report">
                    <template:include view="withoutTitle"/>
                </div>
                <!-- Without Keyword -->
                <div id="report-10" class="div-report">
                    <template:include view="withoutKeywords"/>
                </div>
                <!-- Without Description -->
                <div id="report-11" class="div-report">
                    <template:include view="withoutDescription"/>
                </div>
                <!-- Display Links -->
                <div id="report-23" class="div-report">
                    <template:include view="displayLinks"/>
                </div>

                <!-- Report By Locked Content -->
                <div id="report-14" class="div-report">
                    <template:include view="lockedContent"/>
                </div>
                <!-- Report Content Waiting Publication-->
                <div id="report-15" class="div-report">
                    <template:include view="contentWaitingPublication"/>
                </div>
                <!-- Report Overview-->
                <div id="report-16" class="div-report">
                    <template:include view="overview"/>
                </div>

                <!-- Report Custom Cache Content-->
                <div id="report-17" class="div-report">
                    <template:include view="byCustomCacheContent"/>
                </div>
                <!-- Report Acl Inheritance Break-->
                <div id="report-18" class="div-report">
                    <template:include view="aclInheritanceBreak"/>
                </div>
                <!-- Report Author and page-->
                <div id="report-20" class="div-report">
                    <template:include view="byDateAndAuthor"/>
                </div>
                <!-- Report untranslated pages-->
                <div id="report-21" class="div-report">
                    <template:include view="byUntranslated"/>
                </div>
                <!-- Report By WIP Content -->
                <div id="report-22" class="div-report">
                    <template:include view="wipContent"/>
                </div>
                <!-- Report for marked for deletion content -->
                <div id="report-24" class="div-report">
                    <template:include view="markedForDeletion"/>
                </div>
                <!-- Report for marked for published content -->
                <div id="report-25" class="div-report">
                    <template:include view="publishedcontent"/>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- include tree folder picker -->
<template:include view="folderPicker"/>
