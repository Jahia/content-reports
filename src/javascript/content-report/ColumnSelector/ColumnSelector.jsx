import React, {useState} from 'react';
import PropTypes from 'prop-types';
import Select from 'react-select';
import {MultiValue, Option} from "./Components";
import css from './ColumnSelector.scss';

export const ColumnSelector = ({columns, setColumns}) => {
    const [values, setValues] = useState(columns.filter(column => column.visible)
        .map(column => {
            return {
                value: column.id,
                label: column.Header
            };
        }));

    const handleChange = selectValues => {
        const updatedColumns = columns.map(column => {
            return {...column, visible: selectValues.find(selectValue => selectValue.value === column.id) !== undefined};
        }).filter(column => column.visible);
        setColumns(updatedColumns);
        setValues(selectValues);
    };

    return (
        <Select
            isMulti
            className={css.selector}
            closeMenuOnSelect={false}
            hideSelectedOptions={false}
            options={
                columns
                    .map(column => {
                        return {
                            value: column.id,
                            label: column.Header
                        };
                    })
            }
            value={values}
            components={{MultiValue, Option}}
            onChange={handleChange}
        />
    );
};

ColumnSelector.propTypes = {
    columns: PropTypes.array.isRequired,
    setColumns: PropTypes.func.isRequired
};

