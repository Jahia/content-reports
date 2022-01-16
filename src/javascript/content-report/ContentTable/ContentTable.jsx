import React, {useEffect, useState} from 'react';
import {useQuery} from "@apollo/react-hooks";
import {useSortBy, useTable} from 'react-table';
import {ContentValuesQuery} from '../ContentType/ContentType.gql-queries'
import PropTypes from 'prop-types';
import {SortIndicator, Table, TableBody, TableBodyCell, TableHead, TableHeadCell, TableRow} from '@jahia/moonstone';
import {ColumnSelector} from '../ColumnSelector/ColumnSelector';

export const useDefaultColumns = (properties, localStorage, nodeType) => {
    const storedHeaders = localStorage.getItem('content-report-columns-' + nodeType.replace(':', '_'));

    const allColumns = [
        {
            Header: 'Name',
            id: 'nodeName',
            visible: true,
            accessor: row => row.displayName
        },
        {
            Header: 'Created by',
            id: 'createdBy',
            visible: true,
            accessor: row => row?.properties.find(rowProperty => rowProperty.name === 'jcr:createdBy')?.value
        },
        {
            Header: 'Last modified on',
            id: 'lastModified',
            visible: true,
            accessor: row => row?.properties.find(rowProperty => rowProperty.name === 'jcr:lastModified')?.value
        }
    ].concat(properties ? properties.map(property => {
        return {
            Header: property.definition.displayName,
            id: property.name,
            visible: false,
            accessor: row => row.properties.find(rowProperty => rowProperty.name === property.name)?.value
        }
    }) : []).map(header => {
        if (storedHeaders) {
            if (JSON.parse(storedHeaders).includes(header.id)) {
                return {...header, visible: true};
            } else if (!JSON.parse(storedHeaders).includes(header.id)) {
                return {...header, visible: false};
            }
        }
        return header;
    });

    const filteredColumns = allColumns
        .filter(header => header.visible);
    return {allColumns, filteredColumns};
};

export const ContentTable = ({nodeType, language}) => {
        const localStorage = window.localStorage;

        const {data, error, loading} = useQuery(ContentValuesQuery, {variables: {nodeType: nodeType, language: language}});

        const {allColumns, filteredColumns} = useDefaultColumns(data?.jcr?.nodesByCriteria?.nodes[0].properties, localStorage, nodeType);
        const [columns, setColumns] = useState(filteredColumns);

        useEffect(() => {
            if (filteredColumns.length !== columns.length) {
                setColumns(filteredColumns);
            }
        }, [filteredColumns]);
        const handleSetColumns = updatedColumns => {
            setColumns(updatedColumns);
            localStorage.setItem('content-report-columns-' + nodeType.replace(':', '_'), JSON.stringify(updatedColumns.filter(header => header.visible).map(header => header.id)));
        };

        const {
            getTableProps,
            getTableBodyProps,
            headerGroups,
            rows,
            prepareRow
        } = useTable(
            {
                data: data ? data?.jcr?.nodesByCriteria?.nodes : [],
                columns,
                initialState: {
                    sortBy: [
                        {id: 'lastModified', desc: true}
                    ]
                },
            },
            useSortBy
        );

        const renderSortIndicator = (isSorted, isSortedDesc) => {
            const direction = isSortedDesc ? 'descending' : 'ascending';
            return <SortIndicator isSorted={isSorted} direction={direction}/>;
        };

        if (error || loading) {
            return <></>
        }
        return (<>
                <ColumnSelector columns={allColumns} setColumns={handleSetColumns}/>
                <Table {...getTableProps()}>
                    <TableHead>
                        {headerGroups.map(headerGroup => (
                            // A key is included in headerGroup.getHeaderGroupProps

                            <TableRow {...headerGroup.getHeaderGroupProps()}>
                                {headerGroup.headers.map(column => (
                                    // A key is included in column.getHeaderProps

                                    <TableHeadCell {...column.getHeaderProps(column.getSortByToggleProps())}
                                                   iconEnd={column.canSort && renderSortIndicator(column.isSorted, column.isSortedDesc)}
                                                   width={column.customWidth}>
                                        {column.render('Header')}
                                    </TableHeadCell>
                                ))}
                            </TableRow>
                        ))}
                    </TableHead>
                    <TableBody {...getTableBodyProps()}>
                        {rows.map(row => {
                            prepareRow(row);
                            return (
                                // A key is included in row.getRowProps

                                <TableRow {...row.getRowProps()}>
                                    {row.cells.map(cell => (
                                        // A key is included in cell.getCellProps

                                        <TableBodyCell {...cell.getCellProps()} width={cell.column.customWidth}>
                                            {cell.render('Cell')}
                                        </TableBodyCell>
                                    ))}
                                </TableRow>
                            );
                        })}
                    </TableBody>
                </Table>
            </>
        );
    }
;

ContentTable.propTypes = {
    nodeType: PropTypes.string.isRequired,
    language: PropTypes.string.isRequired
};
