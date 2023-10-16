import React from 'react';
import { render } from 'react-dom';
import {HashRouter} from "react-router-dom";
import {Provider} from 'react-redux';
import Router from "./Router";
import {store} from './configureStore'


render(<Provider store={store}>
    <HashRouter>
        <Router/>
    </HashRouter>
</Provider>, document.getElementById('app'));