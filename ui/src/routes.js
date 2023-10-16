import DocumentList from "./containers/documents/DocumentList";
import DocumentDetails from "./containers/documents/DocumentDetails";


export const routes = [
    {
        path: '/',
        Component: DocumentList,
        layout: 'default',
        name: 'Города',
    },
    {
        path: '/documents/:id',
        Component: DocumentDetails,
        layout: 'default',
        name: 'Город',
    }
]
