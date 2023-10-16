const initialState = {
    data: null,
    refresh: null
};

export default (state = initialState, action) => {

    switch (action.type) {
        case 'ERROR':
            return {
                ...state,
                data: action.payload,
                refresh: action.refresh
            };

        case `CLEAR_ERROR`:
            return {
                ...state,
                data: null,
                refresh: null
            };

        default:
            return state;
    }
};
