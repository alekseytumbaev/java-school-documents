import React from 'react';
import {Field} from 'react-final-form';
import PropTypes from 'prop-types';

import TextField from "@material-ui/core/TextField";

export const FieldInputText = (props) => (
    <Field name={props.name}>
        {({input: { name, onChange, value, ...restInput },
              meta, ...rest}) => {

            return (
                <TextField
                    {...rest}
                    name={name}
                    helperText={meta.touched ? meta.error : undefined}
                    error={meta.error && meta.touched}
                    inputProps={restInput}
                    onChange={onChange}
                    value={value}
                    margin={'normal'}
                    fullWidth={true}
                    label={props.label}
                />)
        }}
    </Field>
);

FieldInputText.propTypes = {
    name: PropTypes.string.isRequired
};
