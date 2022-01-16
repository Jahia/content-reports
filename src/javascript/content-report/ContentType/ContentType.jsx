import React from 'react';
import {Header} from '@jahia/moonstone';
import PropTypes from 'prop-types';
import {ContentTable} from '../ContentTable/ContentTable';
import classes from './ContentType.scss'

export const ContentType = ({nodeType, nodeTypeDisplayName, dxContext}) => {
        const {language} = dxContext;

        return (<>
                <div className={classes.root}>
                    <Header title={nodeTypeDisplayName}/>
                    <div className={classes.main}>
                        <div className={classes.container}>
                            <ContentTable language={language} nodeType={nodeType}/>
                        </div>
                    </div>
                </div>
            </>
        );
    }
;

ContentType.propTypes = {
    nodeType: PropTypes.string.isRequired,
    nodeTypeDisplayName: PropTypes.string.isRequired,
    dxContext: PropTypes.object.isRequired
};
