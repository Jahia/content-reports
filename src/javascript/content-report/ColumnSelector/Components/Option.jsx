import React from 'react';
import PropTypes from 'prop-types';
import {CheckboxChecked, CheckboxUnchecked} from '@jahia/moonstone';
import css from './Option.scss';

export const Option = ({innerProps, isSelected, label}) => {
    return (
        <div {...innerProps}>
            <div className={css.option}>
                {isSelected ? <CheckboxChecked color="#00a0e3"/> : <CheckboxUnchecked color="#00a0e3"/>}
                {label}
            </div>
        </div>
    );
};

Option.propTypes = {
    innerProps: PropTypes.object.isRequired,
    label: PropTypes.string.isRequired,
    isSelected: PropTypes.bool
};
