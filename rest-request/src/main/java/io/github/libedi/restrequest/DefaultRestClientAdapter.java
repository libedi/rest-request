package io.github.libedi.restrequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * RestRequest를 활용하여 RestTemplate을 사용하게 해주는 Adapter
 * 
 * @author "Sangjun,Park"
 *
 */
public class DefaultRestClientAdapter implements RestClientAdapter {

	private final RestTemplate restTemplate;

	/**
	 * RestClientAdapter 생성. 내부적으로 RestTemplate 기본 생성자를 통해 작동한다.
	 */
	public DefaultRestClientAdapter() {
		restTemplate = new RestTemplate();
	}

	/**
	 * RestClientAdapter 생성.
	 * 
	 * @param restTemplate
	 * @throws IllegalArgumentException restTemplate 파라미터가 null인 경우
	 */
	public DefaultRestClientAdapter(final RestTemplate restTemplate) {
		if (restTemplate == null) {
			throw new IllegalArgumentException("RestTemplate must not be null.");
		}
		this.restTemplate = restTemplate;
	}


	@Override
    public <T> ResponseEntity<T> send(final RestRequest<T> restRequest) {
        if (restRequest == null) {
            throw new IllegalArgumentException("RestRequest must not be null.");
        }
        if (restRequest.getTypeReference() == null) {
            return restTemplate.exchange(restRequest.getUri(), restRequest.getMethod(), restRequest.getHttpEntity(),
                    restRequest.getResponseType());
		}
        return restTemplate.exchange(restRequest.getUri(), restRequest.getMethod(), restRequest.getHttpEntity(),
                restRequest.getTypeReference());
	}

}
