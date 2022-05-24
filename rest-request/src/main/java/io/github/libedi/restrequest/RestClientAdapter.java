package io.github.libedi.restrequest;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.http.ResponseEntity;

/**
 * RestRequest를 활용하여 REST API를 호출하는 Adapter
 * 
 * @author "Sangjun,Park"
 *
 */
public interface RestClientAdapter {

	/**
     * RestRequest의 요청 정보로 요청 전송
     * 
     * @param <T>
     * @param restRequest 생성한 RestRequest
     * @return the response as entity
     */
    <T> ResponseEntity<T> send(final RestRequest<T> restRequest);

	/**
     * RestRequest의 요청 정보로 요청 전송
     * 
     * @param <T>
     * @param restRequest 생성한 RestRequest
     * @return the converted object
     */
    default <T> Optional<T> sendForBody(final RestRequest<T> restRequest) {
        return Optional.ofNullable(send(restRequest).getBody());
	}

    /**
     * RestRequest의 요청 정보로 비동기 요청 전송
     * 
     * @param <T>
     * @param restRequest 생성한 RestRequest
     * @return the new CompletableFuture with ResponseEntity as return value
     */
    default <T> CompletableFuture<ResponseEntity<T>> sendAsync(final RestRequest<T> restRequest) {
        return CompletableFuture.supplyAsync(() -> send(restRequest));
    }

    /**
     * RestRequest의 요청 정보로 비동기 요청 전송
     * 
     * @param <T>
     * @param restRequest 생성한 RestRequest
     * @param executor    the executor to use for asynchronous execution
     * @return the new CompletableFuture with ResponseEntity as return value
     */
    default <T> CompletableFuture<ResponseEntity<T>> sendAsync(final RestRequest<T> restRequest,
            final Executor executor) {
        return CompletableFuture.supplyAsync(() -> send(restRequest), executor);
    }
}
