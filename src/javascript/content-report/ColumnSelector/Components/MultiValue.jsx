import React from 'react';
import PropTypes from 'prop-types';
import css from './MultiValue.scss';

export const MultiValue = ({index, getValue}) => {

    const selectedElements = getValue();
    const title = selectedElements.map(element => element.value).join(', ');
    const length = selectedElements.length;
    const label = `${length} header${length !== 1 ? 's' : ''} selected`;

    return (
        <>{index === 0 ?
            <div className={css.label} title={title}>
                {label}
            </div> : null}
        </>
    );
};

MultiValue.propTypes = {
    index: PropTypes.number.isRequired,
    getValue: PropTypes.func.isRequired
};
