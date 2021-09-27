package com.github.libedi.restrequest;

import java.net.URI;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import com.github.libedi.restrequest.RestRequestSpec.RestRequestHeaderSpec;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * REST 요청 명세 : HTTP Header
 * 
 * @author "Sangjun,Park"
 *
 * @param <T>
 * @param <S>
 */
@Getter(AccessLevel.PACKAGE)
public abstract class AbstractRestRequestHeaderSpec<T, S extends RestRequestHeaderSpec<T, S>>
		implements RestRequestHeaderSpec<T, S> {

	private final URI uri;
	private final HttpMethod method;
	private final HttpHeaders headers;
	private final Class<T> responseType;
	private final ParameterizedTypeReference<T> typeReference;

	AbstractRestRequestHeaderSpec(final URI uri, final HttpMethod method, final Class<T> responseType,
			final ParameterizedTypeReference<T> typeReference) {
		this.uri = uri;
		this.method = method;
		this.responseType = responseType;
		this.typeReference = typeReference;
		this.headers = new HttpHeaders();
	}

	@SuppressWarnings("unchecked")
	@Override
	public S addHeader(final String headerName, final String headerValue) {
		headers.add(headerName, headerValue);
		return (S) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public S addHeaders(final String headerName, final String... headerValues) {
		for (final String headerValue : headerValues) {
			headers.add(headerName, headerValue);
		}
		return (S) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public S accept(final String acceptableMediaType) {
		headers.add(HttpHeaders.ACCEPT, acceptableMediaType);
		return (S) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public S contentType(final String contentType) {
		headers.add(HttpHeaders.CONTENT_TYPE, contentType);
		return (S) this;
	}

}
