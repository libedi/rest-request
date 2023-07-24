package io.github.libedi.restrequest;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.libedi.restrequest.RestRequestSpec.RestRequestBodySpec;

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
    private boolean isMultipart;

    DefaultRestRequestBodySpec(final URI uri, final HttpMethod method, final Class<T> responseType,
            final ParameterizedTypeReference<T> typeReference) {
        super(uri, method, responseType, typeReference);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RestRequestBodySpec<T> body(final Object body) {
        if (body instanceof MultiValueMap) {
            setParams((MultiValueMap<String, Object>) body);
        } else {
            this.body = body;
        }
        return this;
    }

    @Override
    public RestRequestBodySpec<T> addFile(final String key, final File file) {
        addParam(key, new FileSystemResource(file));
        isMultipart = true;
        return this;
    }

    @Override
    public RestRequestBodySpec<T> addFile(final String key, final Path path) {
        addParam(key, new FileSystemResource(path.toFile()));
        isMultipart = true;
        return this;
    }

    @Override
    public RestRequestBodySpec<T> addFile(final String key, final MultipartFile multipartFile) {
        try {
            addParam(key, new ByteArrayResource(multipartFile.getBytes()) {
                @Override
                public String getFilename() {
                    return multipartFile.getOriginalFilename();
                }
            });
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        isMultipart = true;
        return this;
    }

    @Override
    public RestRequest<T> build() {
        setMultipartData();
        return new RestRequest<>(getUriWithQueryParam(), getMethod(), makeHttpEntity(), getResponseType(),
                getTypeReference());
    }

    private void setMultipartData() {
        if (!isMultipart && getParameter().entrySet().stream().anyMatch(this::hasMultipartFormData)) {
            isMultipart = true;
        }
        if (!isMultipart) {
            return;
        }
        if (body == null) {
            changeMultipartContentType(MediaType.MULTIPART_FORM_DATA_VALUE);
        } else {
            changeMultipartContentType("multipart/mixed");
            try {
                addParam("body", new ObjectMapper().writeValueAsString(body));
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean hasMultipartFormData(final Entry<String, List<Object>> entry) {
        return !CollectionUtils.isEmpty(entry.getValue()) && entry.getValue().get(0) instanceof Resource;
    }

    private void changeMultipartContentType(final String multipartContentType) {
        final MediaType contentType = getHeaders().getContentType();
        if (contentType == null || !StringUtils.startsWithIgnoreCase(contentType.getType(), "multipart")) {
            contentType(multipartContentType);
        }
    }

    private URI getUriWithQueryParam() {
        final UriComponentsBuilder builder = UriComponentsBuilder.fromUri(getUri());
        if (hasQueryParameter()) {
            getParameter().entrySet().stream()
                    .filter(entry -> !hasMultipartFormData(entry))
                    .forEach(entry -> builder.queryParam(entry.getKey(),
                            CollectionUtils.isEmpty(entry.getValue()) ? new Object[0] : entry.getValue().toArray()));
        }
        return builder.build().toUri();
    }

    private boolean hasQueryParameter() {
        return getParameter() != null && body != null && !isAvaliableMultipartMixedData();
    }

    private boolean isAvaliableMultipartMixedData() {
        return isMultipart;
    }

    private HttpEntity<?> makeHttpEntity() {
        return new HttpEntity<>(body == null || isAvaliableMultipartMixedData() ? getParameter() : body, getHeaders());
    }

}
