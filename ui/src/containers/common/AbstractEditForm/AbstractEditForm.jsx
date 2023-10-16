import React from 'react';
import PropTypes from 'prop-types';
import {useDispatch, useSelector} from 'react-redux';
import {useHistory} from 'react-router-dom';
import {Form} from 'react-final-form';

import * as api from '../../../api/cruds-api';
import Loader from "../../Loader";
import Button from '@material-ui/core/Button';
import Modal from "@material-ui/core/Modal";

export default function AbstractEditForm({module, stateName, newItem, renderFields, onCloseRoute}) {

    const {data} = useSelector(state => state.error);

    const dispatch = useDispatch();
    const history = useHistory();


    const {isLoadingItem, item} = useSelector(state => stateName ? state[`${stateName}`] : state[`${module}State`]);
    if (isLoadingItem) {
        return <Loader size="sm"/>;
    }

    const onClose = () => {
        dispatch({type: `CLEAR_${module.toUpperCase()}`});
        history.push(onCloseRoute || `/${module}`);
    }

    const onSubmit = (values) => {
        api.save(dispatch, module, values, onClose);
    };

    function renderErrorModal() {
        return (
            <Modal
                open
                onClose={() => {
                    dispatch({type: `CLEAR_ERROR`});
                }}
            >
                <div>
                    {data && data.errors && data.errors.map((error, idx) => (<li key={idx}>{error}</li>))}
                </div>
            </Modal>
        );
    }

    const initialValues = newItem;

    const FieldsComponent = renderFields;
    return (
        <div style={{padding: '100px'}}>
            {data && data.errors && renderErrorModal()}
            <Form className="form-box"
                initialValues={initialValues}
                onSubmit={onSubmit}
                render={({handleSubmit, form, values}) => (
                    <form onSubmit={handleSubmit}>
                        <FieldsComponent item={values} form={form}/>
                        <Button style={{border: "solid"}} onClick={handleSubmit}>{'Создать'}</Button>
                        <Button style={{border: "solid"}} onClick={onClose}>Отменить</Button>
                    </form>
                )}
            />
        </div>
    )
}

AbstractEditForm.propTypes = {
    module: PropTypes.string.isRequired,
    stateName: PropTypes.string,
    newItem: PropTypes.object.isRequired,
    renderFields: PropTypes.func.isRequired,
    onCloseRoute: PropTypes.string,
    validateItems: PropTypes.func
};
