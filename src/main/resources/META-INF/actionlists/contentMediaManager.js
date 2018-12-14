actionsRegistry.add("content-reports", actionsRegistry.get("router"), {
    buttonLabel: 'content-reports:label.contentManager.leftMenu.manage.contentReports.title',
    mode: "apps",
    iframeUrl : ":context/cms/:frame/:workspace/:lang/sites/:site.content-reports.html",
    target : ["leftMenuManageActions"],
    requiredPermission: "contentReports",
    requireModuleInstalledOnSite: 'content-reports',
    buttonIcon: '<svg viewBox="0 0 24 24">' +
                    '<path d="M22,21H2V3H4V19H6V10H10V19H12V6H16V19H18V14H22V21Z" />' +
                '</svg>'
});
