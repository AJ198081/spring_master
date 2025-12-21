import {gql, GraphQLClient} from 'graphql-request';

export type CustomerFields = {
    id?: boolean;
    firstName?: boolean;
    lastName?: boolean;
    email?: boolean;
    orders?: boolean;
};

const jwt = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhal9hZG1pbiIsImF1dGhvcml0aWVzIjpbIlJPTEVfQURNSU4iXSwiaWF0IjoxNzY2MjgzOTQ3LCJleHAiOjE3NjYzNzAzNDd9.DxzY2tCgVVec3quJ4mT9F_BMK3UiOpLU3lCycioDij6QGJHJmhGI34_b1eu8Bdc32M0zRoY1SWIYGgJKf-WouA";

const graphQLClient = new GraphQLClient('http://localhost:11000/graphql-query', {
    headers: {
        authorization: `Bearer ${jwt}`
    }
});

export const getAllCustomer = async () => {

    const query = gql`query {
        allCustomers {
            id
            firstName
            orders {
                orderStatus
                totalPrice
                auditMetaData {
                    createdDate
                }
            }
        }
    } `;

    const customersResponse = await graphQLClient.request(query);
    return customersResponse.allCustomers;
}

export const getTotalNumberOfCustomers = async (customerFields: CustomerFields) => {
    const query = gql`query {
        allCustomers {
            ${Object.keys(customerFields)
                    .filter(key => customerFields[key as keyof CustomerFields])
                    .join('\n            ')}
        }
    }`;
    
    const {allCustomers} = await graphQLClient.request(query);
    return allCustomers.length;
}

export const getCustomerCreatedDate = async (id: string) => {
    const query = gql`query {
        customerById(id: "${id}") {
            auditMetaData {
                createdDate
            }
        }
    }`;
   const customer = await graphQLClient.request(query);

   return customer.customerById.auditMetaData.createdDate;
}