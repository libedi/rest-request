package io.github.libedi.restrequest;

import java.net.URI;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import io.github.libedi.restrequest.RestRequestSpec.RestRequestHeaderSpec;
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
@Getter(AccessLevel.PROTECTED)
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

    protected HttpHeaders getHeaders() {
        return HttpHeaders.readOnlyHttpHeaders(headers);
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
    public S accept(final List<MediaType> acceptableMediaTypes) {
        headers.setAccept(acceptableMediaTypes);
		return (S) this;
	}

	@SuppressWarnings("unchecked")
	@Override
    public S contentType(final MediaType contentType) {
        headers.setContentType(contentType);
		return (S) this;
	}

    @SuppressWarnings("unchecked")
    @Override
    public S authorization(final String authValue) {
        headers.set(HttpHeaders.AUTHORIZATION, authValue);
        return (S) this;
    }

}
