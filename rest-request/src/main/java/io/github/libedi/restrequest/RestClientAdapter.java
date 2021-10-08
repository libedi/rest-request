package io.github.libedi.restrequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * RestRequest를 활용하여 RestTemplate을 사용하게 해주는 Adapter
 * 
 * @author "Sangjun,Park"
 *
 */
public class RestClientAdapter {

	private final RestTemplate restTemplate;

	/**
	 * RestClientAdapter 생성. 내부적으로 RestTemplate 기본 생성자를 통해 작동한다.
	 */
	public RestClientAdapter() {
		restTemplate = new RestTemplate();
	}

	/**
	 * RestClientAdapter 생성.
	 * 
	 * @param restTemplate
	 * @throws IllegalArgumentException restTemplate 파라미터가 null인 경우
	 */
	public RestClientAdapter(final RestTemplate restTemplate) {
		if (restTemplate == null) {
			throw new IllegalArgumentException("RestTemplate must not be null.");
		}
		this.restTemplate = restTemplate;
	}

	/**
	 * RestRequest의 요청 정보로 RestTemplate 실행
	 * 
	 * @param <T>
	 * @param request 생성한 RestRequest
	 * @return the response as entity
	 * @throws RestClientException
	 */
	public <T> ResponseEntity<T> execute(final RestRequest<T> request) {
		if (request.getTypeReference() == null) {
			return restTemplate.exchange(request.getUri(), request.getMethod(), request.getHttpEntity(),
					request.getResponseType());
		}
		return restTemplate.exchange(request.getUri(), request.getMethod(), request.getHttpEntity(),
				request.getTypeReference());
	}

}
