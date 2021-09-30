package io.github.libedi.restrequest;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;

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
		RestRequestMethodSpec<T> uri(URI uri, Object... uriVariables);

		/**
		 * 요청 URI 설정
		 * 
		 * @param uri
		 * @param uriVariables
		 * @return
		 */
		default RestRequestMethodSpec<T> uri(final String uri, final Object... uriVariables) {
			return uri(URI.create(Objects.requireNonNull(uri, () -> "URI must not be null.")), uriVariables);
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
		 * @param headerValues
		 * @return
		 */
		S addHeaders(String headerName, String... headerValues);

		/**
		 * HTTP Header 추가 : Accept
		 * 
		 * @param acceptableMediaType
		 * @return
		 */
		S accept(String acceptableMediaType);

		/**
		 * HTTP Header 추가 : Accept
		 * 
		 * @param acceptableMediaType
		 * @return
		 */
		default S accept(final MediaType acceptableMediaType) {
			return accept(acceptableMediaType.toString());
		}

		/**
		 * HTTP Header 추가 : Content-Type
		 * 
		 * @param contentType
		 * @return
		 */
		S contentType(String contentType);

		/**
		 * HTTP Header 추가 : Content-Type
		 * 
		 * @param contentType
		 * @return
		 */
		default S contentType(MediaType contentType) {
			return contentType(contentType.toString());
		}
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
		 * @param key
		 * @param value
		 * @return
		 */
		S addParameter(String key, Object value);

		/**
		 * 파라미터 일괄 추가
		 * 
		 * @param multiValueMap
		 * @return
		 */
		S putAllParameters(MultiValueMap<String, Object> multiValueMap);

		/**
		 * 파라미터 일괄 추가
		 * 
		 * @param map
		 * @return
		 */
		S putAllParameters(Map<String, Object> map);

		/**
		 * 파라미터 일괄 추가
		 * 
		 * @param object
		 * @return
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
