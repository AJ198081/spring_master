import {gql, GraphQLClient} from 'graphql-request';

export type CustomerFields = {
    id?: boolean;
    firstName?: boolean;
    lastName?: boolean;
    email?: boolean;
    orders?: boolean;
};

const jwt = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhal91c2VyIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9BRE1JTiJdLCJpYXQiOjE3NjYxNzk2NTAsImV4cCI6MTc2NjI2NjA1MH0.9UXuYrfre3wYj9rY-RXZVPf_cg-gq06yPwFn9fDBIwGCrzCuN86Sk5egCjYygRm-hqrySw--nF3x4rSyqWoTVA";

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