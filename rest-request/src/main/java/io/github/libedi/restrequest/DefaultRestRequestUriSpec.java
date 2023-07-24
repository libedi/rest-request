package io.github.libedi.restrequest;

import java.net.URI;
import java.util.Objects;

import org.springframework.core.ParameterizedTypeReference;

import io.github.libedi.restrequest.RestRequestSpec.RestRequestMethodSpec;
import io.github.libedi.restrequest.RestRequestSpec.RestRequestUriSpec;

/**
 * REST 요청 명세 : URI
 * 
 * @author "Sangjun,Park"
 *
 * @param <T>
 */
public class DefaultRestRequestUriSpec<T> implements RestRequestUriSpec<T> {

    private Class<T> responseType;
    private ParameterizedTypeReference<T> typeReference;

    DefaultRestRequestUriSpec(final Class<T> responseType) {
        this.responseType = Objects.requireNonNull(responseType, () -> "Response type must not be null.");
    }

    DefaultRestRequestUriSpec(final ParameterizedTypeReference<T> typeReference) {
        this.typeReference = Objects.requireNonNull(typeReference, () -> "Response type must not be null.");
    }

    @Override
    public RestRequestMethodSpec<T> uri(final URI uri) {
        return new DefaultRestRequestMethodSpec<>(Objects.requireNonNull(uri, () -> "URI must not be null."),
                responseType, typeReference);
    }

}
