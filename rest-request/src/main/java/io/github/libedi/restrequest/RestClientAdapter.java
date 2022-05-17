package io.github.libedi.restrequest;

import java.util.Optional;

import org.springframework.http.ResponseEntity;

/**
 * RestRequest를 활용하여 REST API를 호출하는 Adapter
 * 
 * @author "Sangjun,Park"
 *
 */
public interface RestClientAdapter {

	/**
     * RestRequest의 요청 정보로 요청 실행
     * 
     * @param <T>
     * @param restRequest 생성한 RestRequest
     * @return the response as entity
     */
    <T> ResponseEntity<T> execute(final RestRequest<T> restRequest);

	/**
     * RestRequest의 요청 정보로 요청 실행
     * 
     * @param <T>
     * @param restRequest 생성한 RestRequest
     * @return the converted object
     */
    default <T> Optional<T> executeForObject(final RestRequest<T> restRequest) {
        return Optional.ofNullable(execute(restRequest).getBody());
	}
}
