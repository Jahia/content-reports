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
            governor-style.css"  />

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
            governor-app.js"/>

<c:set var="selectedLang" value="${fn:split(fn:split(currentLocale, '_')[0], '-')[0]}"/>
<input type="hidden" name="selectedLang" id="selectedLang" value="${selectedLang}">
<input type="hidden" name="selectedBaseUrl" id="selectedBaseUrl" value="${url.currentModule}">


<div class="panel panel-primary">
    <div class="panel-heading">
        <fmt:message key="cgnt_contentGovernor"/>
    </div>

    <div class="panel-body">
        <div class="row">
            <!-- Column for menu -->
            <div class="col-md-2" STYLE="/*background-color: #f5f5f5;*/" >
                <div style="overflow-x: hidden; min-height: 100px;">
                    <ul class="nav nav-list">
                        <li><label class="tree-toggler nav-header">> <fmt:message key="cgnt_contentGovernor.menu.contentReports"/></label>
                            <ul class="nav nav-list tree">
                                <li>
                                    <a class="nav-menu-link" href="#" onclick="showView('1')">
                                        <fmt:message key="cgnt_contentGovernor.menu.contentReports.byAuthor"/>
                                    </a>
                                </li>
                                <li>
                                    <label class="tree-toggler nav-header">> <fmt:message key="cgnt_contentGovernor.menu.contentReports.byDate"/></label>
                                    <!--a href="#" class="tree-toggler">
                                        <fmt:message key="cgnt_contentGovernor.menu.contentReports.byDate"/>
                                    </a-->
                                    <ul class="nav nav-list tree">
                                        <li>
                                            <a class="nav-menu-link" href="#" onclick="showView('2')">
                                                <fmt:message key="cgnt_contentGovernor.menu.contentReports.byAllDate" />
                                            </a>
                                        </li>
                                        <li>
                                            <a class="nav-menu-link" href="#" onclick="showView('3')">
                                                <fmt:message key="cgnt_contentGovernor.menu.contentReports.beforeDate" />
                                            </a>
                                        </li>
                                    </ul>
                                </li>

                                <li>
                                    <label class="tree-toggler nav-header">> <fmt:message key="cgnt_contentGovernor.menu.contentReports.byType"/></label>
                                    <!--a href="#" class="tree-toggler">
                                        <fmt:message key="cgnt_contentGovernor.menu.contentReports.byDate"/>
                                    </a-->
                                    <ul class="nav nav-list tree">
                                        <li>
                                            <a class="nav-menu-link" href="#" onclick="showView('4')">
                                                <fmt:message key="cgnt_contentGovernor.menu.contentReports.byType"/>
                                            </a>
                                        </li>
                                        <li>
                                            <a class="nav-menu-link" href="#" onclick="showView('5')">
                                                <fmt:message key="cgnt_contentGovernor.menu.contentReports.byTypeDetailed"/>
                                            </a>
                                        </li>
                                    </ul>
                                </li>


                                <li>
                                    <a class="nav-menu-link" href="#" onclick="showView('6')">
                                        <fmt:message key="cgnt_contentGovernor.menu.contentReports.byStatus"/>
                                    </a>
                                </li>
                                <li>
                                    <a class="nav-menu-link" href="#" onclick="showView('7')">
                                        <fmt:message key="cgnt_contentGovernor.menu.contentReports.byLanguage"/>
                                    </a>
                                </li>

                                <li>
                                    <label class="tree-toggler nav-header">> <fmt:message key="cgnt_contentGovernor.menu.contentReports.byMetadata"/></label>

                                    <ul class="nav nav-list tree">
                                        <li>
                                            <a class="nav-menu-link" href="#" onclick="showView('9')">
                                                <fmt:message key="cgnt_contentGovernor.menu.contentReports.pagesWithoutTitle"/>
                                            </a>
                                        </li>
                                        <li>
                                            <a class="nav-menu-link" href="#" onclick="showView('10')">
                                                <fmt:message key="cgnt_contentGovernor.menu.contentReports.pagesWithoutKeywords"/>
                                            </a>
                                        </li>
                                        <li>
                                            <a class="nav-menu-link" href="#" onclick="showView('11')">
                                                <fmt:message key="cgnt_contentGovernor.menu.contentReports.pagesWithoutDescription"/>
                                            </a>
                                        </li>
                                    </ul>
                                </li>

                                <li>
                                    <a class="nav-menu-link" href="#" onclick="showView('12')">
                                        <fmt:message key="cgnt_contentGovernor.menu.contentReports.contentFromAnotherSite"/>
                                    </a>
                                </li>
                                <li>
                                    <a class="nav-menu-link" href="#" onclick="showView('13')">
                                        <fmt:message key="cgnt_contentGovernor.menu.contentReports.byOrphanContent"/>
                                    </a>
                                </li>
                                <li>
                                    <a class="nav-menu-link" href="#" onclick="showView('14')">
                                        <fmt:message key="cgnt_contentGovernor.menu.contentReports.lockedContent"/>
                                    </a>
                                </li>
                                <li>
                                    <a class="nav-menu-link" href="#" onclick="showView('15')">
                                        <fmt:message key="cgnt_contentGovernor.menu.contentReports.contentWaitingPublication"/>
                                    </a>
                                </li>
                                <li>
                                    <a class="nav-menu-link" href="#" onclick="showView('16')">
                                        <fmt:message key="cgnt_contentGovernor.menu.contentReports.overview"/>
                                    </a>
                                </li>
                                <li>
                                    <a class="nav-menu-link" href="#" onclick="showView('17')">
                                        <fmt:message key="cgnt_contentGovernor.menu.contentReports.customCacheContent"/>
                                    </a>
                                </li>
                                <li>
                                    <a class="nav-menu-link" href="#" onclick="showView('18')">
                                        <fmt:message key="cgnt_contentGovernor.menu.contentReports.aclInheritanceBreak"/>
                                    </a>
                                </li>
                            </ul>
                        </li>
                        <li class="divider"></li>
                    </ul>
                </div>
            </div>

            <!-- Column for report content -->
            <div class="col-md-10" style="border-color: black; border-style: hidden">
                <!-- By Author: report A -->
                <div id="report-1" class="div-report">
                    <template:include view="byAuthor"/>
                </div>
                <!-- By Date: report A -->
                <div id="report-2" class="div-report">
                    <template:include view="byAllDate"/>
                </div>
                <!-- By Date: report B -->
                <div id="report-3" class="div-report">
                    <template:include view="beforeDate"/>
                </div>
                <!-- By Type: simple -->
                <div id="report-4" class="div-report">
                    <template:include view="byType"/>
                </div>
                <!-- By Type: detailed -->
                <div id="report-5" class="div-report">
                    <template:include view="byTypeDetailed"/>
                </div>
                <!-- By Status -->
                <div id="report-6" class="div-report">
                    <template:include view="byStatus"/>
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
                <!-- Content From Another Site -->
                <div id="report-12" class="div-report">
                    <template:include view="contentFromAnotherSite"/>
                </div>
                <!-- Report By Orphand Content -->
                <div id="report-13" class="div-report">
                    <template:include view="byOrphandContent"/>
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
            </div>
        </div>
    </div>
</div>

<!-- include tree folder picker -->
<template:include view="folderPicker"/>
