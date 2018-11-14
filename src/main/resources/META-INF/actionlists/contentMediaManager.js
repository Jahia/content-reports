actionsRegistry.add("content-reports", actionsRegistry.get("router"), {
    buttonLabel: 'content-reports:label.contentManager.leftMenu.manage.contentReports.title',
    mode: "apps",
    iframeUrl : ":context/cms/:frame/:workspace/:lang/sites/:site.content-reports.html",
    target : ["leftMenuManageActions"],
    requiredPermission: "contentReports",
    requireModuleInstalledOnSite: 'content-reports',
    icon: 'chart-bar'
});
