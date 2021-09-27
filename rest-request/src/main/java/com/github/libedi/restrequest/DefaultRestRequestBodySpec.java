package com.github.libedi.restrequest;

import java.net.URI;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.libedi.restrequest.RestRequestSpec.RestRequestBodySpec;

/**
 * REST 요청 명세 : Request Body
 * 
 * @author "Sangjun,Park"
 *
 * @param <T>
 */
public class DefaultRestRequestBodySpec<T> extends DefaultRestRequestFormSpec<T, RestRequestBodySpec<T>>
		implements RestRequestBodySpec<T> {

	private Object body;

	DefaultRestRequestBodySpec(final URI uri, final HttpMethod method, final Class<T> responseType,
			final ParameterizedTypeReference<T> typeReference) {
		super(uri, method, responseType, typeReference);
	}

	@Override
	public RestRequestBodySpec<T> body(final Object body) {
		this.body = body;
		return this;
	}

	@Override
	public RestRequest<T> build() {
		return new RestRequest<>(getUriWithQueryParam(), getMethod(), makeHttpEntity(), getResponseType(),
				getTypeReference());
	}

	private URI getUriWithQueryParam() {
		final UriComponentsBuilder builder = UriComponentsBuilder.fromUri(getUri());
		if (hasQueryParameter()) {
			for (final Entry<String, List<Object>> entry : getParameter().entrySet()) {
				builder.queryParam(entry.getKey(),
						entry.getValue() != null ? entry.getValue().toArray() : new Object[0]);
			}
		}
		return builder.build().toUri();
	}

	private boolean hasQueryParameter() {
		return getParameter() != null && body != null;
	}

	private HttpEntity<?> makeHttpEntity() {
		return new HttpEntity<>(body != null ? body : getParameter(), getHeaders());
	}

}
