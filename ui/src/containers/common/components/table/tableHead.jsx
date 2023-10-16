import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import TableCell from "@material-ui/core/TableCell";
import Checkbox from "@material-ui/core/Checkbox";
import TableSortLabel from "@material-ui/core/TableSortLabel";
import PropTypes from "prop-types";
import React from "react";
import {lighten} from "@material-ui/core/styles";

export function EnhancedTableHead(props) {
    const {classes, onSelectAllClick, order, orderBy, numSelected, rowCount, onRequestSort, dataMap} = props;
    const createSortHandler = (property) => (event) => {
        onRequestSort(event, property);
    };
    return (
        <TableHead>
            <TableRow  style={{
                backgroundColor: lighten('#0000FF', 0.60),
                fontWeight: 'bold'
            }}>
                <TableCell padding="checkbox">
                    <Checkbox

                        indeterminate={numSelected > 0 && numSelected < rowCount}
                        checked={rowCount > 0 && numSelected === rowCount}
                        onChange={onSelectAllClick}
                        inputProps={{'aria-label': 'select all desserts'}}
                    />
                </TableCell>
                {dataMap.map((headCell) => (
                    <TableCell
                        style={{
                            backgroundColor: lighten('#0000FF', 0.60),
                            color: 'black',
                            fontWeight: 'bold'
                        }}
                        key={headCell.property}
                        align={headCell.numeric ? 'right' : 'left'}
                        padding={headCell.disablePadding ? 'none' : 'default'}
                        sortDirection={orderBy === headCell.property ? order : false}
                    >
                        <TableSortLabel
                            active={orderBy === headCell.property}
                            direction={orderBy === headCell.property ? order : 'asc'}
                            onClick={createSortHandler(headCell.property)}
                        >
                            {headCell.label}
                            {orderBy === headCell.property ? (
                                <span className={classes.visuallyHidden}>
                  {order === 'desc' ? 'sorted descending' : 'sorted ascending'}
                </span>
                            ) : null}
                        </TableSortLabel>
                    </TableCell>
                ))}
            </TableRow>
        </TableHead>
    );
}

EnhancedTableHead.propTypes = {
    classes: PropTypes.object.isRequired,
    numSelected: PropTypes.number.isRequired,
    onRequestSort: PropTypes.func.isRequired,
    onSelectAllClick: PropTypes.func.isRequired,
    order: PropTypes.oneOf(['asc', 'desc']).isRequired,
    orderBy: PropTypes.string.isRequired,
    rowCount: PropTypes.number.isRequired,
    dataMap: PropTypes.array.isRequired
};
