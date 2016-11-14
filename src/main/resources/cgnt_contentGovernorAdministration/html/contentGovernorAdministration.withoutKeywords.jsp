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
<fmt:message key="cgnt_contentGovernor.report" var="labelReport"/>
<fmt:message key="cgnt_contentGovernor.menu.contentReports.byDate" var="labelDate"/>
<fmt:message key="cgnt_contentGovernor.report.label.total" var="labelTotal"/>
<fmt:message key="cgnt_contentGovernor.report.label.yes" var="labelYes"/>
<fmt:message key="cgnt_contentGovernor.report.label.no" var="labelNo"/>
<fmt:message key="cgnt_contentGovernor.menu.contentReports.detailsBy" var="labelDetailsBy"/>
<fmt:message key="cgnt_contentGovernor.report.label.contentCreated" var="labelCreatedBy"/>
<fmt:message key="cgnt_contentGovernor.report.loading" var="labelLoading"/>
<fmt:message key="cgnt_contentGovernor.report.label.addKeywords" var="labelAddKeywords"/>
<c:url value="${url.base}${docPath}${renderContext.mainResource.node.path}" var="currentNodePath"/>


<div class="panel">
    <div class="panel-body">
        <div class="row">
            <!-- div tabla y contenidos -->
            <div class="col-md-6" >

                <!-- search button -->
                <div class="row">
                    <div class="col-md-12">
                        <button type="button" class="btn btn-default" onclick="fillReportPageWithoutKeywords('${currentNodePath}', '${labelLoading}', '${labelAddKeywords}')">
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
                        <h5 class="panel-title" id=""><fmt:message key="cgnt_contentGovernor.report"/>&nbsp;<fmt:message key="cgnt_contentGovernor.menu.contentReports.pagesWithoutKeywords"/></h5>
                    </div>

                    <table width="100%" class="display governor-data-table" id="pageWithoutKeywordsTable" cellspacing="0">
                        <thead>
                            <tr>
                                <th><fmt:message key="cgnt_contentGovernor.report.column.title"/></th>
                                <th><fmt:message key="cgnt_contentGovernor.report.column.pagePath"/></th>
                                <th><fmt:message key="cgnt_contentGovernor.report.column.action"/></th>
                            </tr>
                        </thead>
                        <tbody></tbody>
                    </table>
                </div>
            </div>
        </div>


    </div>
</div>



<!-- nModal Window: add keyeords -->
<div id="addKeywordsPageModel" class="nModal fade window-detail-nModal" role="dialog">
    <div class="nModal-dialog">

        <!-- nModal content-->
        <div class="nModal-content">
            <div class="nModal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="nModal-body">
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <h5 class="panel-title" id="title-addKeywords-modal"></h5>
                    </div>
                    <div>&nbsp;</div>
                    <div  class="panel-body" style="margin-left: 10px">
                        <div class="row">
                            <div class="col-md-5">
                                <label class="label-form"> <fmt:message key="cgnt_contentGovernor.report.label.keywords"/></label>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-md-5">
                                <input type="hidden" id="input-node-path-keywords" name="input-node-path-keywords" />

                                <!-- sheepIt Form -->
                                <div id="sheepItForm">

                                    <!-- Form template-->
                                    <div id="sheepItForm_template">
                                        <label for="sheepItForm_#index#_keyword"><fmt:message key="cgnt_contentGovernor.report.label.keyword"/> <span id="sheepItForm_label"></span></label>
                                        <input id="sheepItForm_#index#_keyword" name="node[keywords][#index#][keyword]" type="text" size="10" maxlength="100" class="form-control" style="width: 90%"/>
                                        <a id="sheepItForm_remove_current">
                                            <img class="delete" src="<c:url value="${url.currentModule}/images/cross.png"/>" width="16" height="16" border="0">
                                        </a>
                                    </div>
                                    <!-- /Form template-->

                                    <div>&nbsp;</div>
                                    <!-- No forms template -->
                                    <div id="sheepItForm_noforms_template">
                                        <fmt:message key="cgnt_contentGovernor.report.label.noKeywords"/>
                                    </div>
                                    <!-- /No forms template-->

                                    <div>&nbsp;</div>
                                    <!-- Controls -->
                                    <div id="sheepItForm_controls" style="float: left">
                                        <div id="sheepItForm_add">
                                            <a><span><fmt:message key="cgnt_contentGovernor.report.label.addKeyword"/></span></a>
                                        </div>
                                        <div id="sheepItForm_remove_all">
                                            <a><span><fmt:message key="cgnt_contentGovernor.report.label.removeAll"/></span></a>
                                        </div>
                                    </div>
                                    <!-- /Controls -->

                                </div>
                                <!-- /sheepIt Form -->

                            </div>
                        </div>

                        <!-- search button -->
                        <div class="row">
                            <div class="col-md-5">&nbsp;</div>
                        </div>

                        <!-- search button -->
                        <div class="row">
                            <div class="col-md-5">
                                <button type="button" class="btn btn-default" onclick="modalSaveKeywords('${currentNodePath}', '${labelLoading}', '${labelAddKeywords}')">
                                    <fmt:message key="cgnt_contentGovernor.button.save"/>
                                </button>
                            </div>
                        </div>
                    </div>

                </div>

            </div>
            <div class="nModal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>

    </div>
</div>



<template:addResources>
    <script type="text/javascript">
        var sheepItForm = {};

        $(document).ready(function() {

            sheepItForm = $('#sheepItForm').sheepIt({
                separator: '',
                allowRemoveLast: false,
                allowRemoveCurrent: true,
                allowRemoveAll: true,
                allowAdd: true,
                allowAddN: false,
                maxFormsCount: 10,
                minFormsCount: 0,
                iniFormsCount: 1
            });

        });

    </script>
</template:addResources>


