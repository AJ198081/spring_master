import {ApolloClient, gql, HttpLink, InMemoryCache} from "@apollo/client";

const jwtToken: string = "";

export const apolloClient = new ApolloClient({
    link: new HttpLink({
        uri:
            "http://localhost:11000/graphql",
        headers: {
          "Authorization": `Bearer ${jwtToken}`
        }
    }),
    cache: new InMemoryCache(),
});

export const query = (query: string) => gql`{
    ${query}
}`;

