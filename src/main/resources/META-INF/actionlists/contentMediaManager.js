contextJsParameters['config'].actions = Object.assign(contextJsParameters['config'].actions, {
    "content-reports" : {
        component: "routerAction",
        mode: "apps",
        iframeUrl : ":context/cms/:frame/:workspace/:lang/sites/:site.content-reports.html",
        target : ["leftMenuManageActions"],
        requiredPermission: "contentReports",
        requireModuleInstalledOnSite: 'content-reports',
        labelKey: 'content-reports:label.contentManager.leftMenu.manage.contentReports.title',
        icon: 'chart-bar'
    }
});
