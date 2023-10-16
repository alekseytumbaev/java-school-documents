import React from 'react';
import PropTypes from 'prop-types';
import {lighten, makeStyles} from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableRow from '@material-ui/core/TableRow';
import Paper from '@material-ui/core/Paper';
import Checkbox from '@material-ui/core/Checkbox';
import {EnhancedTableHead} from './tableHead'
import {EnhancedTableToolbar} from './toolBar'

function descendingComparator(a, b, orderBy) {
    if (b[orderBy] < a[orderBy]) {
        return -1;
    }
    if (b[orderBy] > a[orderBy]) {
        return 1;
    }
    return 0;
}

function getComparator(order, orderBy) {
    return order === 'desc'
        ? (a, b) => descendingComparator(a, b, orderBy)
        : (a, b) => -descendingComparator(a, b, orderBy);
}

function stableSort(array, comparator) {
    const stabilizedThis = array.map((el, index) => [el, index]);
    stabilizedThis.sort((a, b) => {
        const order = comparator(a[0], b[0]);
        if (order !== 0) return order;
        return a[1] - b[1];
    });
    return stabilizedThis.map((el) => el[0]);
}

const useStyles = makeStyles((theme) => ({
    root: {
        width: '90%',
        align: 'center',
    },
    paper: {
        width: '90%',
        //marginBottom: theme.spacing(2),
        align: 'center',
    },
    table: {
        alignSelf: 'center',
        minWidth: 500,
        backgroundColor: "whitesmoke"
    },
    visuallyHidden: {
        border: 0,
        clip: 'rect(0 0 0 0)',
        height: 1,
        margin: -1,
        overflow: 'hidden',
        padding: 0,
        position: 'absolute',
        top: 20,
        width: 1,
    },
}));

export default function EnhancedTable(props) {
    const classes = useStyles();
    const [order, setOrder] = React.useState('asc');
    const [orderBy, setOrderBy] = React.useState('orderBy');
    const [selected, setSelected] = React.useState([]);

    const handleRequestSort = (event, property) => {
        const isAsc = orderBy === property && order === 'asc';
        setOrder(isAsc ? 'desc' : 'asc');
        setOrderBy(property);
    };

    const handleSelectAllClick = (event) => {
        if (event.target.checked) {
            const newSelecteds = props.rows.map((n) => n.id);
            setSelected(newSelecteds);
            return;
        }
        setSelected([]);
    };

    const handleClick = (event, name) => {
        const selectedIndex = selected.indexOf(name);
        let newSelected = [];

        if (selectedIndex === -1) {
            newSelected = newSelected.concat(selected, name);
        } else if (selectedIndex === 0) {
            newSelected = newSelected.concat(selected.slice(1));
        } else if (selectedIndex === selected.length - 1) {
            newSelected = newSelected.concat(selected.slice(0, -1));
        } else if (selectedIndex > 0) {
            newSelected = newSelected.concat(
                selected.slice(0, selectedIndex),
                selected.slice(selectedIndex + 1),
            );
        }

        setSelected(newSelected);
    };

    const isSelected = (name) => selected.indexOf(name) !== -1;

    const formRow = (row, index) => {
        const isItemSelected = isSelected(row.id);
        const labelId = `enhanced-table-checkbox-${index}`;

        return (
            <TableRow
                style={{
                    backgroundColor: lighten('#0000FF', 0.95),
                    fontWeight: 'bold'
                }}
                hover
                onClick={(event) => handleClick(event, row.id)}
                role="checkbox"
                aria-checked={isItemSelected}
                tabIndex={-1}
                key={row.id}
                selected={isItemSelected}
            >
                <TableCell padding="checkbox">
                    <Checkbox
                        checked={isItemSelected}
                        inputProps={{ 'aria-labelledby': labelId }}
                    />
                </TableCell>
                <TableCell component="th" id={labelId} scope="row" padding="none">{row.id}</TableCell>
                <TableCell align="right">{row.type}</TableCell>
                <TableCell align="right">{row.organization}</TableCell>
                <TableCell align="right">{row.date}</TableCell>
                <TableCell align="right">{row.description}</TableCell>
                <TableCell align="right">{row.patient}</TableCell>
                <TableCell align="right">{row.status.name}</TableCell>
            </TableRow>
        );
    }

    return (
        <div className={classes.root}>
            <Paper className={classes.paper}>
                <EnhancedTableToolbar
                    numSelected={selected.length}
                    title={props.toolbar.toolbarTitle}
                    delete={() => props.toolbar.delete(selected)}
                    send={() => props.toolbar.send(selected)}
                    add={props.toolbar.add}
                    rows={props.rows}
                    selected = {selected}
                />
                <TableContainer>
                    <Table
                        className={classes.table}
                        aria-labelledby="tableTitle"
                        size={'medium'}
                        aria-label="enhanced table"
                    >
                        <EnhancedTableHead
                            classes={classes}
                            numSelected={selected.length}
                            order={order}
                            orderBy={orderBy}
                            onSelectAllClick={handleSelectAllClick}
                            onRequestSort={handleRequestSort}
                            rowCount={props.rows.length}
                            dataMap={props.dataMap}
                        />
                        <TableBody>
                            {stableSort(props.rows, getComparator(order, orderBy))
                                .map(formRow)}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Paper>
        </div>
    );
}

EnhancedTable.propTypes = {
    dataMap: PropTypes.array,
    rows: PropTypes.array,
    toolbar: PropTypes.shape({
        toolbarTitle: PropTypes.string,
        delete: PropTypes.func,
        send: PropTypes.func,
        add: PropTypes.func
    }),
};

EnhancedTable.defaultProps = {
    dataMap: [],
    rows: [],
    toolbar: {
        toolbarTitle: '',
        delete: null,
        edit: null,
        add: null
    }
};
