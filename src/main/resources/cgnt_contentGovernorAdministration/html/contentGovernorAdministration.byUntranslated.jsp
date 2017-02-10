<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ page import="org.jahia.services.content.decorator.JCRSiteNode,
                 org.jahia.services.render.Resource" %>
<%@ page import="org.jahia.utils.LanguageCodeConverters" %>
<%@ page import="java.util.Comparator" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.TreeSet" %>
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
<fmt:message key="cgnt_contentGovernor.report" var="labelReport"/>
<fmt:message key="cgnt_contentGovernor.menu.contentReports.byDate" var="labelDate"/>
<fmt:message key="cgnt_contentGovernor.report.label.total" var="labelTotal"/>
<fmt:message key="cgnt_contentGovernor.report.label.yes" var="labelYes"/>
<fmt:message key="cgnt_contentGovernor.report.label.no" var="labelNo"/>
<fmt:message key="cgnt_contentGovernor.menu.contentReports.detailsBy" var="labelDetailsBy"/>
<fmt:message key="cgnt_contentGovernor.report.label.contentCreated" var="labelCreatedBy"/>
<fmt:message key="cgnt_contentGovernor.report.loading" var="labelLoading"/>
<c:set var="principalGridLabel" value="${labelReport} ${labelDate}"/>
<c:url value="${url.base}${docPath}${renderContext.mainResource.node.path}" var="currentNodePath"/>

<c:set var="site" value="${renderContext.mainResource.node.resolveSite}"/>
<c:set var="uiLocale" value="${renderContext.UILocale}"/>
<c:set var="siteKey" value="${site.name}"/>

<%
    JCRSiteNode site = (JCRSiteNode) pageContext.getAttribute("site");
    Resource r = (Resource) request.getAttribute("currentResource");
    final Locale currentLocale = (Locale) pageContext.getAttribute("uiLocale");
    Set<Locale> siteLocales = new TreeSet<Locale>(new Comparator<Locale>() {
        public int compare(Locale o1, Locale o2) {
            return o1.getDisplayName(currentLocale).compareTo(o2.getDisplayName(currentLocale));
        }
    });

    siteLocales.addAll(site.getLanguagesAsLocales());
    siteLocales.addAll(site.getInactiveLanguagesAsLocales());
    request.setAttribute("siteLocales", siteLocales);
    request.setAttribute("availableLocales", LanguageCodeConverters.getSortedLocaleList(currentLocale));
%>

<div class="panel">
    <div class="panel-body">
        <div class="row">
            <!-- div tabla y contenidos -->
            <div class="col-md-6" >

                <!-- select type of search -->
                <div class="row">
                    <div class="col-md-12">
                        <label class="label-form"> <fmt:message key="cgnt_contentGovernor.report.typeSearch"/> </label>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12">
                        <div class="form-check">
                            <label class="form-check-label">
                                <input class="form-check-input" type="radio" name="typeOfSearchU" value="pages" checked="checked">
                                <fmt:message key="cgnt_contentGovernor.report.typeSearch.pagesOnly"/>
                            </label>
                        </div>
                        <div class="form-check">
                            <label class="form-check-label">
                                <input class="form-check-input" type="radio" name="typeOfSearchU"  value="ccntent">
                                <fmt:message key="cgnt_contentGovernor.report.typeSearch.allContent"/>
                            </label>
                        </div>
                    </div>
                </div>

                <div class="row"><div class="col-md-12"><hr/></div></div>
                <!-- select language -->
                <div class="row">
                    <div class="col-md-12">
                        <label class="label-form"> <fmt:message key="cgnt_contentGovernor.report.selectLanguage"/> </label>
                        <select id="selectLanguageBU" name="selectLanguageBU">
                            <c:forEach var="locale" items="${siteLocales}">
                                <c:set var="langAsString">${locale}</c:set>
                                    <option value="${locale}"><%= ((Locale) pageContext.getAttribute("locale")).getDisplayName(
                                            currentLocale)%> (${locale})</option>


                            </c:forEach>
                        </select>
                    </div>
                </div>

                <div class="row"><div class="col-md-12"><hr/></div></div>

                <!-- select path -->
                <div class="row">
                    <div class="col-md-12">
                        <label class="label-form"> <fmt:message key="cgnt_contentGovernor.report.selectPath"/> </label>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-2">
                        <button type="button" class="btn btn-default" onclick="callTreeView('pathTxtRDAU')">
                            <span class="glyphicon glyphicon-folder-open"></span>
                            &nbsp;<fmt:message key="cgnt_contentGovernor.report.browse"/>
                        </button>
                    </div>
                    <div class="col-md-10">
                        <input type="text" id="pathTxtRDAU" name="pathTxtRDAU" class="form-control" readonly="true"  value="${renderContext.site.path}" >
                    </div>
                </div>

                <div class="row"><div class="col-md-12"><hr/></div></div>

                <!-- search button -->
                <div class="row">
                    <div class="col-md-12">
                        <button type="button" class="btn btn-default" onclick="fillReportByUntranslated('${currentNodePath}', '${principalGridLabel}', '${labelTotal}', '${labelLoading}')">
                            <span class="glyphicon glyphicon-search"></span> <fmt:message key="cgnt_contentGovernor.report.search"/>
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
                        <h5 class="panel-title" id="rba-principal-grid-rdau"><fmt:message key="cgnt_contentGovernor.report"/>&nbsp;<fmt:message key="cgnt_contentGovernor.menu.contentReports.byDate"/></h5>
                    </div>
                    <div>&nbsp;</div>
                    <table width="100%" class="display governor-data-table" id="byAllUntranslated" cellspacing="0">
                        <thead>
                            <tr>
                                <th><fmt:message key="cgnt_contentGovernor.report.column.title"/></th>
                                <th><fmt:message key="cgnt_contentGovernor.report.column.pagePath"/></th>
                                <th><fmt:message key="cgnt_contentGovernor.report.column.type"/></th>
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

        <div class="row"><div class="col-md-12"><hr/></div></div>


    </div>
</div>
