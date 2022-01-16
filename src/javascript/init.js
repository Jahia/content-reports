import React from 'react';
import {registry} from '@jahia/ui-extender';
import i18next from 'i18next';
import {registerContentReportRoutes, registerContentTypeEntries} from './content-report/register';

const COMP_NAME = 'contentReport';

export default function () {
    registry.add('callback', COMP_NAME, {
        targets: ['jahiaApp-init:23'],
        callback: async () => {
            await i18next.loadNamespaces('content-reports');
            registerContentReportRoutes();
            await registerContentTypeEntries();

            console.log('%c Content report registered routes', 'color: #3c8cba');
        }
    });
}
