package io.github.libedi.restrequest;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * REST 요청 명세
 * 
 * @author "Sangjun,Park"
 *
 * @param <T> 응답 타입
 */
public interface RestRequestSpec<T> {

	/**
	 * RestRequest 생성
	 * 
	 * @return
	 */
	RestRequest<T> build();

	/**
	 * REST 요청 명세 : URI
	 * 
	 * @author "Sangjun,Park"
	 *
	 * @param <T> 응답 타입
	 */
	interface RestRequestUriSpec<T> {

		/**
		 * 요청 URI 설정
		 * 
		 * @param uri
		 * @return
		 */
		RestRequestMethodSpec<T> uri(URI uri);

		/**
		 * 요청 URI 설정
		 * 
		 * @param uri
		 * @return
		 */
		default RestRequestMethodSpec<T> uri(final String uri) {
			return uri(URI.create(Objects.requireNonNull(uri, () -> "URI must not be null.")));
		}

		/**
		 * 요청 URI 설정
		 * 
		 * @param uri
		 * @param uriVariables
		 * @return
		 */
		default RestRequestMethodSpec<T> uri(final String uri, final Object... uriVariables) {
			return uri(UriComponentsBuilder.fromUriString(Objects.requireNonNull(uri, () -> "URI must not be null."))
					.buildAndExpand(uriVariables).encode().toUri());
		}
	}

	/**
	 * REST 요청 명세: HTTP Method
	 * 
	 * @author "Sangjun,Park"
	 *
	 * @param <T> 응답 타입
	 */
	interface RestRequestMethodSpec<T> {

		/**
		 * GET 방식으로 호출
		 * 
		 * @return
		 */
		RestRequestFormSpec<T, ?> get();

		/**
		 * POST 방식으로 호출
		 * 
		 * @return
		 */
		RestRequestBodySpec<T> post();

		/**
		 * PUT 방식으로 호출
		 * 
		 * @return
		 */
		RestRequestBodySpec<T> put();

		/**
		 * PATCH 방식으로 호출
		 * 
		 * @return
		 */
		RestRequestBodySpec<T> patch();

		/**
		 * DELETE 방식으로 호출
		 * 
		 * @return
		 */
		RestRequestFormSpec<T, ?> delete();
	}

	/**
	 * REST 요청 명세 : HTTP Header
	 * 
	 * @author "Sangjun,Park"
	 *
	 * @param <T> 응답 타입
	 * @param <S> 메소드 반환 타입
	 */
	interface RestRequestHeaderSpec<T, S extends RestRequestHeaderSpec<T, S>> extends RestRequestSpec<T> {

		/**
		 * HTTP Header 추가
		 * 
		 * @param headerName
		 * @param headerValue
		 * @return
		 */
		S addHeader(String headerName, String headerValue);

        /**
         * HTTP Header 추가
         * 
         * @param headerName
         * @param headerValue
         * @return
         */
        default S addHeader(final MediaType headerName, final String headerValue) {
            return addHeader(Objects.requireNonNull(headerName, () -> "Header name must not be null.").toString(),
                    headerValue);
        }

		/**
		 * HTTP Header 추가
		 * 
		 * @param headerName
		 * @param headerValues
		 * @return
		 */
		S addHeaders(String headerName, String... headerValues);

        /**
         * HTTP Header 추가
         * 
         * @param headerName
         * @param headerValues
         * @return
         */
        default S addHeaders(final MediaType headerName, final String... headerValues) {
            return addHeaders(Objects.requireNonNull(headerName, () -> "Header name must not be null.").toString(),
                    headerValues);
        }

        /**
         * HTTP Header 설정 : Accept
         * 
         * @param acceptableMediaTypes
         * @return
         */
        S accept(List<MediaType> acceptableMediaTypes);

        /**
         * HTTP Header 설정 : Accept
         * 
         * @param acceptableMediaType
         * @return
         */
        default S accept(final MediaType acceptableMediaType) {
            return accept(Arrays.asList(
                    Objects.requireNonNull(acceptableMediaType, () -> "acceptableMediaType must not be null.")));
        }

		/**
         * HTTP Header 설정 : Accept
         * 
         * @param acceptableMediaType
         * @return
         */
        default S accept(final String acceptableMediaType) {
            return accept(MediaType.valueOf(acceptableMediaType));
        }

        /**
         * HTTP Header 설정 : Content-Type
         * 
         * @param contentType
         * @return
         */
        S contentType(@Nullable MediaType contentType);

		/**
         * HTTP Header 설정 : Content-Type
         * 
         * @param contentType
         * @return
         */
        default S contentType(final String contentType) {
            return contentType(StringUtils.hasText(contentType) ? MediaType.valueOf(contentType) : null);
		}

        /**
         * HTTP Header 설정 : Authorization
         * 
         * @param authValue
         * @return
         */
        S authorization(String authValue);

        /**
         * HTTP Header 설정 : Basic Authorization
         * 
         * @param username
         * @param password
         * @return
         */
        S basicAuth(String username, String password);

        /**
         * HTTP Header 설정 : Bearer Token
         * 
         * @param token
         * @return
         */
        S bearerToken(String token);
	}

	/**
	 * REST 요청 명세 : Query Params / Form Data
	 * 
	 * @author "Sangjun,Park"
	 *
	 * @param <T> 응답 타입
	 * @param <S> 메소드 반환 타입
	 */
	interface RestRequestFormSpec<T, S extends RestRequestFormSpec<T, S>> extends RestRequestHeaderSpec<T, S> {

		/**
		 * 파라미터 추가
		 * 
		 * @param key   파라미터 key
		 * @param value 파라미터 value
		 * @return
		 * @throws NullPointerException key가 null인 경우
		 */
		S addParameter(String key, Object value);

		/**
		 * 파라미터 일괄 추가
		 * 
		 * @param multiValueMap
		 * @return
		 * @throws NullPointerException multiValueMap 파라미터가 null인 경우
		 */
		S putAllParameters(MultiValueMap<String, Object> multiValueMap);

		/**
		 * 파라미터 일괄 추가
		 * 
		 * @param map
		 * @return
		 * @throws NullPointerException map 파라미터가 null인 경우
		 */
		S putAllParameters(Map<String, Object> map);

		/**
		 * 파라미터 일괄 추가
		 * 
		 * @param object
		 * @return
		 * @throws NullPointerException     object 파라미터가 null인 경우
		 * @throws IllegalArgumentException object 파라미터가 Collection인 경우
		 */
		S putAllParameters(Object object);
	}

	/**
	 * REST 요청 명세 : Request Body
	 * 
	 * @author "Sangjun,Park"
	 *
	 * @param <T> 응답 타입
	 */
	interface RestRequestBodySpec<T> extends RestRequestFormSpec<T, RestRequestBodySpec<T>> {

		/**
		 * 요청 body 설정
		 * 
		 * @param body
		 * @return
		 */
		RestRequestBodySpec<T> body(Object body);
	}

}
