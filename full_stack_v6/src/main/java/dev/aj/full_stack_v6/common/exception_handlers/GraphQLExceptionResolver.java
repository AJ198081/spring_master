package dev.aj.full_stack_v6.common.exception_handlers;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.graphql.execution.ErrorType;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
public class GraphQLExceptionResolver implements DataFetcherExceptionResolver {

    @Override
    public @NonNull Mono<List<GraphQLError>> resolveException(Throwable exception, DataFetchingEnvironment environment) {

        Mono<List<GraphQLError>> errorResponse = Mono.just(List.of(
                GraphQLError.newError()
                        .message(exception.getMessage())
                        .errorType(ErrorType.UNAUTHORIZED)
                        .build()
        ));

        GraphQLError graphQLError = GraphqlErrorBuilder.newError(environment)
                .build();


        log.info("Error: {}", errorResponse);
        log.info("GraphQLError: {}", graphQLError);

        return errorResponse;
    }

}
