window.jahia.i18n.loadNamespaces('content-reports');

window.jahia.uiExtender.registry.add('adminRoute', 'content-reports', {
    targets: ['jcontent:10'],
    isSelectable: false,
    icon: window.jahia.moonstone.toIconComponent('Pie'),
    label: 'content-reports:label.contentreports',
    requireModuleInstalledOnSite: 'content-reports',
    requiredPermission: 'contentReports',
    iframeUrl: window.contextJsParameters.contextPath + '/cms/editframe/default/$lang/sites/$site-key.content-reports.html'
});

window.jahia.uiExtender.registry.add('adminRoute', 'content-reports-byAuthorAndDate', {
    targets: ['jcontent-content-reports'],
    isSelectable: true,
    label: 'content-reports:label.byAuthorAndDate',
    requireModuleInstalledOnSite: 'content-reports',
    requiredPermission: 'contentReports',
    iframeUrl: window.contextJsParameters.contextPath + '/cms/editframe/default/$lang/sites/$site-key.content-reports.html?hideMenu=true&view=20'
});

window.jahia.uiExtender.registry.add('adminRoute', 'content-reports-references', {
    targets: ['jcontent-content-reports'],
    isSelectable: true,
    label: 'content-reports:label.references',
    requireModuleInstalledOnSite: 'content-reports',
    requiredPermission: 'contentReports',
    iframeUrl: window.contextJsParameters.contextPath + '/cms/editframe/default/$lang/sites/$site-key.content-reports.html?hideMenu=true&view=23'
});

window.jahia.uiExtender.registry.add('adminRoute', 'content-reports-wipContent', {
    targets: ['jcontent-content-reports'],
    isSelectable: true,
    label: 'content-reports:label.wipContent',
    requireModuleInstalledOnSite: 'content-reports',
    requiredPermission: 'contentReports',
    iframeUrl: window.contextJsParameters.contextPath + '/cms/editframe/default/$lang/sites/$site-key.content-reports.html?hideMenu=true&view=22'
});

window.jahia.uiExtender.registry.add('adminRoute', 'content-reports-contentWaitingPublication', {
    targets: ['jcontent-content-reports'],
    isSelectable: true,
    label: 'content-reports:label.contentWaitingPublication',
    requireModuleInstalledOnSite: 'content-reports',
    requiredPermission: 'contentReports',
    iframeUrl: window.contextJsParameters.contextPath + '/cms/editframe/default/$lang/sites/$site-key.content-reports.html?hideMenu=true&view=15'
});

window.jahia.uiExtender.registry.add('adminRoute', 'content-reports-markedForDeletion', {
    targets: ['jcontent-content-reports'],
    isSelectable: true,
    label: 'content-reports:label.markedForDeletion',
    requireModuleInstalledOnSite: 'content-reports',
    requiredPermission: 'contentReports',
    iframeUrl: window.contextJsParameters.contextPath + '/cms/editframe/default/$lang/sites/$site-key.content-reports.html?hideMenu=true&view=24'
});

window.jahia.uiExtender.registry.add('adminRoute', 'content-reports-language', {
    isSelectable: false,
    targets: ['jcontent-content-reports'],
    label: 'content-reports:label.byLanguage',
    requireModuleInstalledOnSite: 'content-reports',
    requiredPermission: 'contentReports',
    iframeUrl: window.contextJsParameters.contextPath + '/cms/editframe/default/$lang/sites/$site-key.content-reports.html?hideMenu=true'
});

window.jahia.uiExtender.registry.add('adminRoute', 'content-reports-language-pagesWithoutTitle', {
    targets: ['jcontent-content-reports-language'],
    isSelectable: true,
    label: 'content-reports:label.pagesWithoutTitle',
    requireModuleInstalledOnSite: 'content-reports',
    requiredPermission: 'contentReports',
    iframeUrl: window.contextJsParameters.contextPath + '/cms/editframe/default/$lang/sites/$site-key.content-reports.html?hideMenu=true&view=9'
});

window.jahia.uiExtender.registry.add('adminRoute', 'content-reports-language-pagesUntranslated', {
    targets: ['jcontent-content-reports-language'],
    isSelectable: true,
    label: 'content-reports:label.pagesUntranslated',
    requireModuleInstalledOnSite: 'content-reports',
    requiredPermission: 'contentReports',
    iframeUrl: window.contextJsParameters.contextPath + '/cms/editframe/default/$lang/sites/$site-key.content-reports.html?hideMenu=true&view=21'
});

window.jahia.uiExtender.registry.add('adminRoute', 'content-reports-visibility-conditions', {
    targets: ['jcontent-content-reports'],
    isSelectable: false,
    label: 'content-reports:label.byVisibilityConditions',
    requireModuleInstalledOnSite: 'content-reports',
    requiredPermission: 'contentReports',
    iframeUrl: window.contextJsParameters.contextPath + '/cms/editframe/default/$lang/sites/$site-key.content-reports.html?hideMenu=true'
});

window.jahia.uiExtender.registry.add('adminRoute', 'content-reports-visibility-conditions-liveContents', {
    targets: ['jcontent-content-reports-visibility-conditions'],
    isSelectable: true,
    label: 'content-reports:label.liveContents',
    requireModuleInstalledOnSite: 'content-reports',
    requiredPermission: 'contentReports',
    iframeUrl: window.contextJsParameters.contextPath + '/cms/editframe/default/$lang/sites/$site-key.content-reports.html?hideMenu=true&view=25'
});

window.jahia.uiExtender.registry.add('adminRoute', 'content-reports-visibility-conditions-expiredContents', {
    targets: ['jcontent-content-reports-visibility-conditions'],
    isSelectable: true,
    label: 'content-reports:label.expiredContents',
    requireModuleInstalledOnSite: 'content-reports',
    requiredPermission: 'contentReports',
    iframeUrl: window.contextJsParameters.contextPath + '/cms/editframe/default/$lang/sites/$site-key.content-reports.html?hideMenu=true&view=26'
});

window.jahia.uiExtender.registry.add('adminRoute', 'content-reports-visibility-conditions-futureContents', {
    targets: ['jcontent-content-reports-visibility-conditions'],
    isSelectable: true,
    label: 'content-reports:label.futureContents',
    requireModuleInstalledOnSite: 'content-reports',
    requiredPermission: 'contentReports',
    iframeUrl: window.contextJsParameters.contextPath + '/cms/editframe/default/$lang/sites/$site-key.content-reports.html?hideMenu=true&view=27'
});

window.jahia.uiExtender.registry.add('adminRoute', 'content-reports-metadata', {
    targets: ['jcontent-content-reports'],
    isSelectable: false,
    label: 'content-reports:label.byMetadata',
    requireModuleInstalledOnSite: 'content-reports',
    requiredPermission: 'contentReports',
    iframeUrl: window.contextJsParameters.contextPath + '/cms/editframe/default/$lang/sites/$site-key.content-reports.html?hideMenu=true'
});

window.jahia.uiExtender.registry.add('adminRoute', 'content-reports-metadata-pagesWithoutKeywords', {
    targets: ['jcontent-content-reports-metadata'],
    isSelectable: true,
    label: 'content-reports:label.pagesWithoutKeywords',
    requireModuleInstalledOnSite: 'content-reports',
    requiredPermission: 'contentReports',
    iframeUrl: window.contextJsParameters.contextPath + '/cms/editframe/default/$lang/sites/$site-key.content-reports.html?hideMenu=true&view=10'
});

window.jahia.uiExtender.registry.add('adminRoute', 'content-reports-metadata-pagesWithoutDescription', {
    targets: ['jcontent-content-reports-metadata'],
    isSelectable: true,
    label: 'content-reports:label.pagesWithoutDescription',
    requireModuleInstalledOnSite: 'content-reports',
    requiredPermission: 'contentReports',
    iframeUrl: window.contextJsParameters.contextPath + '/cms/editframe/default/$lang/sites/$site-key.content-reports.html?hideMenu=true&view=11'
});

window.jahia.uiExtender.registry.add('adminRoute', 'content-reports-system', {
    targets: ['jcontent-content-reports'],
    isSelectable: false,
    label: 'content-reports:label.system',
    requireModuleInstalledOnSite: 'content-reports',
    requiredPermission: 'contentReports',
    iframeUrl: window.contextJsParameters.contextPath + '/cms/editframe/default/$lang/sites/$site-key.content-reports.html?hideMenu=true'
});

window.jahia.uiExtender.registry.add('adminRoute', 'content-reports-system-lockedContent', {
    targets: ['jcontent-content-reports-system'],
    isSelectable: true,
    label: 'content-reports:label.lockedContent',
    requireModuleInstalledOnSite: 'content-reports',
    requiredPermission: 'contentReports',
    iframeUrl: window.contextJsParameters.contextPath + '/cms/editframe/default/$lang/sites/$site-key.content-reports.html?hideMenu=true&view=14'
});

window.jahia.uiExtender.registry.add('adminRoute', 'content-reports-system-customCacheContent', {
    targets: ['jcontent-content-reports-system'],
    isSelectable: true,
    label: 'content-reports:label.customCacheContent',
    requireModuleInstalledOnSite: 'content-reports',
    requiredPermission: 'contentReports',
    iframeUrl: window.contextJsParameters.contextPath + '/cms/editframe/default/$lang/sites/$site-key.content-reports.html?hideMenu=true&view=17'
});

window.jahia.uiExtender.registry.add('adminRoute', 'content-reports-system-aclInheritanceBreak', {
    targets: ['jcontent-content-reports-system'],
    isSelectable: true,
    label: 'content-reports:label.aclInheritanceBreak',
    requireModuleInstalledOnSite: 'content-reports',
    requiredPermission: 'contentReports',
    iframeUrl: window.contextJsParameters.contextPath + '/cms/editframe/default/$lang/sites/$site-key.content-reports.html?hideMenu=true&view=18'
});

window.jahia.uiExtender.registry.add('adminRoute', 'content-reports-system-overview', {
    targets: ['jcontent-content-reports-system'],
    isSelectable: true,
    label: 'content-reports:label.overview',
    requireModuleInstalledOnSite: 'content-reports',
    requiredPermission: 'contentReports',
    iframeUrl: window.contextJsParameters.contextPath + '/cms/editframe/default/$lang/sites/$site-key.content-reports.html?hideMenu=true&view=16'
});
