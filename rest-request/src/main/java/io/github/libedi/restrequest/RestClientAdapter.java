package io.github.libedi.restrequest;

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
	 * @param request 생성한 RestRequest
	 * @return the response as entity
	 */
	<T> ResponseEntity<T> execute(final RestRequest<T> request);

	/**
	 * RestRequest의 요청 정보로 요청 실행
	 * 
	 * @param <T>
	 * @param request 생성한 RestRequest
	 * @return the converted object
	 */
	default <T> T executeForObject(final RestRequest<T> request) {
		return execute(request).getBody();
	}
}
