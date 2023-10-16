import {createStore, compose, combineReducers} from 'redux';
import {connectRouter} from 'connected-react-router';
import {reducer as formReducer} from 'redux-form';
import {createBrowserHistory} from 'history';
import errorReducer from './reducers/error-reducer';

import abstractEntityReducer from './reducers/abstract-entity-reducer';

const history = createBrowserHistory();
const initialState = {};

let composeEnhancers = compose;

const reducers = {
    router: connectRouter(history),
    documentState: abstractEntityReducer('documents'),
    error: errorReducer,
    form: formReducer
};

const store = createStore(
    combineReducers(reducers),
    initialState,
    composeEnhancers()
);

export {store, history};
