package com.github.libedi.restrequest;

import java.net.URI;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;

import com.github.libedi.restrequest.RestRequestSpec.RestRequestBodySpec;
import com.github.libedi.restrequest.RestRequestSpec.RestRequestFormSpec;
import com.github.libedi.restrequest.RestRequestSpec.RestRequestMethodSpec;

/**
 * REST 요청 명세 : HTTP Method
 * 
 * @author "Sangjun,Park"
 *
 * @param <T>
 */
public class DefaultRestRequestMethodSpec<T> implements RestRequestMethodSpec<T> {

	private final URI uri;
	private final Class<T> responseType;
	private final ParameterizedTypeReference<T> typeReference;

	DefaultRestRequestMethodSpec(final URI uri, final Class<T> responseType,
			final ParameterizedTypeReference<T> typeReference) {
		this.uri = uri;
		this.responseType = responseType;
		this.typeReference = typeReference;
	}

	@Override
	public RestRequestFormSpec<T, ?> get() {
		return new DefaultRestRequestFormSpec<>(uri, HttpMethod.GET, responseType, typeReference);
	}

	@Override
	public RestRequestBodySpec<T> post() {
		return new DefaultRestRequestBodySpec<>(uri, HttpMethod.POST, responseType, typeReference);
	}

	@Override
	public RestRequestBodySpec<T> put() {
		return new DefaultRestRequestBodySpec<>(uri, HttpMethod.PUT, responseType, typeReference);
	}

	@Override
	public RestRequestBodySpec<T> patch() {
		return new DefaultRestRequestBodySpec<>(uri, HttpMethod.PATCH, responseType, typeReference);
	}

	@Override
	public RestRequestFormSpec<T, ?> delete() {
		return new DefaultRestRequestFormSpec<>(uri, HttpMethod.DELETE, responseType, typeReference);
	}

}
