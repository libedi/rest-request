package io.github.libedi.restrequest;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

import io.github.libedi.restrequest.RestRequestSpec.RestRequestFormSpec;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * REST 요청 명세 : Query Params / Form Data
 * 
 * @author "Sangjun,Park"
 *
 * @param <T>
 * @param <S>
 */
@Getter(AccessLevel.PACKAGE)
class DefaultRestRequestFormSpec<T, S extends RestRequestFormSpec<T, S>>
		extends AbstractRestRequestHeaderSpec<T, S>
		implements RestRequestFormSpec<T, S> {

	private MultiValueMap<String, Object> parameter;

	DefaultRestRequestFormSpec(final URI uri, final HttpMethod method, final Class<T> responseType,
			final ParameterizedTypeReference<T> typeReference) {
		super(uri, method, responseType, typeReference);
	}

	@SuppressWarnings("unchecked")
	@Override
	public S addParameter(final String key, final Object value) {
		if (parameter == null) {
			parameter = new LinkedMultiValueMap<>();
		}
		parameter.add(key, value);
		return (S) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public S putAllParameters(final MultiValueMap<String, Object> multiValueMap) {
		Objects.requireNonNull(multiValueMap, () -> "Parameter must not be null.");

		if (parameter == null) {
			parameter = new LinkedMultiValueMap<>(multiValueMap);
		} else {
			parameter.putAll(multiValueMap);
		}
		return (S) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public S putAllParameters(final Map<String, Object> map) {
		Objects.requireNonNull(map, () -> "Parameter must not be null.");

		for (final Entry<String, Object> entry : map.entrySet()) {
			addParameter(entry.getKey(), entry.getValue());
		}
		return (S) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public S putAllParameters(final Object object) {
		Objects.requireNonNull(object, () -> "Parameter must not be null.");
		Assert.isTrue((object instanceof Collection) == false, "Parameter must not be Collection.");

		ReflectionUtils.doWithFields(object.getClass(),
                field -> handleParameterValue(field, object),
				field -> Modifier.isStatic(field.getModifiers()) == false);
		return (S) this;
	}

    private void handleParameterValue(final Field field, final Object object)
            throws IllegalArgumentException, IllegalAccessException {
        field.setAccessible(true);
        final String name = field.getName();
        final Object value = field.get(object);

        if (value instanceof Collection) {
            ((Collection<?>) value).forEach(el -> addParameter(name, el));
        } else if (value != null && value.getClass().isArray()) {
            Arrays.stream((Object[]) value).forEach(el -> addParameter(name, el));
        } else {
            addParameter(name, value);
        }
    }

    @Override
	public RestRequest<T> build() {
		return new RestRequest<>(getUriWithQueryParam(), getMethod(), new HttpEntity<>(getHeaders()), getResponseType(),
				getTypeReference());
	}

	private URI getUriWithQueryParam() {
		final UriComponentsBuilder builder = UriComponentsBuilder.fromUri(getUri());
		if (getParameter() != null) {
			for (final Entry<String, List<Object>> entry : getParameter().entrySet()) {
				builder.queryParam(entry.getKey(),
						entry.getValue() != null ? entry.getValue().toArray() : new Object[0]);
			}
		}
		return builder.build().toUri();
	}

}
