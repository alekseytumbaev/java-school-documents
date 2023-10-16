const initialState = {
    isLoading: false,
    isLoadingItem: false,
    isLoadingCount: false,
    list: [],
    item: {}
};

export default entityType =>
    (state = initialState, action) => {
        const entity = entityType.toUpperCase();
        switch (action.type) {
            case `GET_${entity}_LIST_START`:
                return {
                    ...state,
                    list: null,
                    isLoading: true
                };

            case `GET_${entity}_LIST`:
                return {
                    ...state,
                    list: action.list,
                    isLoading: false,
                    totalPages: action.totalPages,
                    totalElements: action.totalElements,
                    pagination: action.pagination
                };

            case `GET_${entity}_START`:
                return {
                    ...state,
                    isLoadingItem: true
                };

            case `GET_${entity}`:
                return {
                    ...state,
                    isLoadingItem: false,
                    item: action.item
                };

            case `SAVE_${entity}`:
                return {
                    ...state,
                    isLoadingItem: false,
                    item: action.item
                };

            case `SAVE_${entity}_ERROR`:
                return {
                    ...state,
                    isLoadingItem: false
                };

            case `SEND_${entity}`:
                return {
                    ...state,
                    isLoadingItem: false,
                    item: action.item
                };

            case `SEND_${entity}_ERROR`:
                return {
                    ...state,
                    isLoadingItem: false
                };

            case `CLEAR_${entity}`:
                return {
                    ...state,
                    item: {}
                };

            default:
                return state;
        }
    }
