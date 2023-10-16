import Toolbar from "@material-ui/core/Toolbar";
import clsx from "clsx";
import Typography from "@material-ui/core/Typography";
import Tooltip from "@material-ui/core/Tooltip";
import PropTypes from "prop-types";
import React from "react";
import {lighten, makeStyles} from "@material-ui/core/styles";
import Button from "@material-ui/core/Button";

const useToolbarStyles = makeStyles((theme) => ({
    root: {
        paddingLeft: theme.spacing(2),
        paddingRight: theme.spacing(1),
    },
    highlight:
        theme.palette.type === 'light'
            ? {
                color: theme.palette.primary.main,
                backgroundColor: lighten(theme.palette.primary.light, 0.85),
            }
            : {
                color: theme.palette.text.primary,
                backgroundColor: theme.palette.primary.dark,
            },
    title: {
        flex: '1 1 100%',
    },
}));

export const EnhancedTableToolbar = (props) => {
    const classes = useToolbarStyles();
    const { numSelected, rows, selected } = props;

    return (
        <Toolbar
            className={clsx(classes.root, {
                [classes.highlight]: numSelected > 0,
            })}
        >
            {numSelected > 0 ? (
                <Typography className={classes.title} color="inherit" variant="subtitle1" component="div">
                    {numSelected} выбрано
                </Typography>
            ) : (
                <Typography className={classes.title} variant="h6" id="tableTitle" component="div">
                    {props.title}
                </Typography>
            )}

            {numSelected === 0 && (
                <Tooltip title="Создать новый документ">
                    <Button onClick={props.add}>Создать</Button>
                </Tooltip>
            )}

            {numSelected > 0 && (
                <div className="row">
                    <div hidden={numSelected > 1 || rows.filter(data => data.id === selected[0])[0].status.code !== 'NEW'} className="col-6">
                        <Tooltip title="Отправить на обработку">
                            <Button onClick={props.send}>Отправить</Button>
                        </Tooltip>
                    </div>
                    <div hidden={rows.filter(data => selected.includes(data.id) && data.status.code === 'IN_PROCESS' ).length > 0} className="col-6">
                        <Tooltip title="Удалить">
                            <Button onClick={props.delete}>Удалить</Button>
                        </Tooltip>
                    </div>
                </div>
            )}
        </Toolbar>
    );
};

EnhancedTableToolbar.propTypes = {
    numSelected: PropTypes.number.isRequired,
    title: PropTypes.string.isRequired,
    delete: PropTypes.func,
    edit: PropTypes.func,
    add: PropTypes.func
};
