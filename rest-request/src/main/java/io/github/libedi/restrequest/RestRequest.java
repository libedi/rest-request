package io.github.libedi.restrequest;

import java.io.Serializable;
import java.net.URI;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

/**
 * REST 요청 생성을 위한 RestRequest
 * 
 * @author "Sangjun,Park"
 *
 * @param <T>
 */
public class RestRequest<T> implements Serializable {

    private static final long serialVersionUID = 6077646798610598311L;

    private final URI uri;
    private final HttpMethod method;
    private final HttpEntity<?> httpEntity;
    private final Class<T> responseType;
    private final ParameterizedTypeReference<T> typeReference;

    RestRequest(final URI uri, final HttpMethod method, final HttpEntity<?> httpEntity, final Class<T> responseType,
            final ParameterizedTypeReference<T> typeReference) {
        this.uri = uri;
        this.method = method;
        this.httpEntity = httpEntity;
        this.responseType = responseType;
        this.typeReference = typeReference;
    }

    /**
     * Map&lt;String, Object&gt; 응답 타입
     * 
     * @return
     */
    public static DefaultRestRequestUriSpec<Map<String, Object>> mapResp() {
        return resp(new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    /**
     * T 타입의 응답 타입
     * 
     * @param <T>
     * @param responseType
     * @return
     */
    public static <T> DefaultRestRequestUriSpec<T> resp(final Class<T> responseType) {
        return new DefaultRestRequestUriSpec<>(responseType);
    }

    /**
     * T 타입의 제네릭 응답 타입
     * 
     * @param <T>
     * @param typeReference
     * @return
     */
    public static <T> DefaultRestRequestUriSpec<T> resp(final ParameterizedTypeReference<T> typeReference) {
        return new DefaultRestRequestUriSpec<>(typeReference);
    }

    /**
     * 응답값이 없는 경우
     * 
     * @return
     */
    public static DefaultRestRequestUriSpec<Void> nonResp() {
        return new DefaultRestRequestUriSpec<>(Void.class);
    }

    public URI getUri() {
        return uri;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public HttpEntity<?> getHttpEntity() {
        return httpEntity;
    }

    public Class<T> getResponseType() {
        return responseType;
    }

    public ParameterizedTypeReference<T> getTypeReference() {
        return typeReference;
    }

}
