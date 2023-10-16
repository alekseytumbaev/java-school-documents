import _ from "lodash";
import axios from 'axios';
import {store} from "../configureStore";

const axiosInstance = axios.create({
    baseURL: window.config.apiUrl,
    withCredentials: false
});

axiosInstance.interceptors.response.use(
    (response) => {

        if (response.data && response.data.status && response.data.status.code !== 'OK') {
            store.dispatch({
                type: 'AJAX_ERROR',
                error: response.data.status.message
            });
            return Promise.reject(response.data.status.message);
        }

        if (response.config.method !== 'get') {
            store.dispatch({
                type: 'AJAX_INFO',
                info: response.data.message
            })
        }

        return Promise.resolve(response);
    },
    (error) => {
        let err;
        if (error.response && error.response.status === 401) {
            setTimeout(() => window.location.reload(), 2000);
            err = "Данные для авторизации запроса просрочены либо невалидны. Страница будет перезагружена.";
        } else {
            err = _.get(error, 'response.data.status.message', 'Что-то пошло не так');
        }
        store.dispatch({
            type: 'AJAX_ERROR',
            error: err
        });
        return Promise.reject(error);
    }
);

export default axiosInstance;
