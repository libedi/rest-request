package io.github.libedi.restrequest.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

import io.github.libedi.restrequest.RestRequest;
import lombok.Builder;
import lombok.Getter;

public class RestRequestTest {

	@Getter
	@Builder
	private static class TestBody {
		private String id;
		private List<String> list;
	}

	@Test
	public void formUri() {
		// given
		Class<String> responseType = String.class;
		String uri = "http://localhost:8080/test";
		MediaType accept = MediaType.APPLICATION_FORM_URLENCODED;
		MediaType contentType = MediaType.APPLICATION_JSON;
		String customHeaderName = "X-Test-Header";
		String customHeaderValue = "XTestHeaderValue";
        String basicUsername = "username";
        String basicPassword = "password";
		String paramKey = "paramKey";
		String paramValue = "paramValue";
		
		Charset charset = StandardCharsets.ISO_8859_1;
		HttpHeaders expectedHeader = new HttpHeaders();
		expectedHeader.add(customHeaderName, customHeaderValue);
		expectedHeader.add(HttpHeaders.ACCEPT, accept.toString());
		expectedHeader.add(HttpHeaders.CONTENT_TYPE, contentType.toString());
        expectedHeader.set(HttpHeaders.AUTHORIZATION, "Basic " + new String(
                Base64.getEncoder().encode((basicUsername + ":" + basicPassword).getBytes(charset)), charset));
		
		URI expectedUri = UriComponentsBuilder.fromUriString(uri)
				.queryParam(paramKey, paramValue)
				.build().toUri();

		// when
        RestRequest<String> actual = RestRequest.resp(responseType)
				.uri(uri)
				.get()
				.addHeader(customHeaderName, customHeaderValue)
				.accept(accept)
				.contentType(contentType)
                .basicAuth(basicUsername, basicPassword)
                .addParam(paramKey, paramValue)
				.build();

		// then
		assertThat(actual).isNotNull();
		assertThat(actual.getUri()).isEqualTo(expectedUri);
		assertThat(actual.getMethod()).isEqualTo(HttpMethod.GET);
		assertThat(actual.getHttpEntity()).isNotNull().satisfies(entity -> {
			assertThat(entity.getBody()).isNull();
			assertThat(entity.getHeaders()).isEqualTo(expectedHeader);
		});
		assertThat(actual.getResponseType()).isEqualTo(responseType);
		assertThat(actual.getTypeReference()).isNull();
	}
	
	@Test
	public void requestBody() {
		// given
		ParameterizedTypeReference<List<String>> typeReference = new ParameterizedTypeReference<List<String>>() {};
		String uri = "http://localhost:8080/test";
		String accept = MediaType.APPLICATION_JSON_VALUE;
		String contentType = MediaType.APPLICATION_XML_VALUE;
		String customHeaderName = "X-Test-Header";
		String customHeaderValue = "XTestHeaderValue";
        String bearerToken = "bearerToken";
		String paramKey = "paramKey";
		String paramValue = "paramValue";
		TestBody body = TestBody.builder()
				.id("testId")
				.list(Arrays.asList("a", "b", "c"))
				.build();
		
		HttpHeaders expectedHeader = new HttpHeaders();
		expectedHeader.add(customHeaderName, customHeaderValue);
		expectedHeader.add(HttpHeaders.ACCEPT, accept);
		expectedHeader.add(HttpHeaders.CONTENT_TYPE, contentType);
        expectedHeader.set(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);
		
		URI expectedUri = UriComponentsBuilder.fromUriString(uri)
				.queryParam(paramKey, paramValue)
				.build().toUri();

		// when
        RestRequest<List<String>> actual = RestRequest.resp(typeReference)
				.uri(uri)
				.post()
				.addHeader(customHeaderName, customHeaderValue)
				.accept(accept)
				.contentType(contentType)
                .bearerToken(bearerToken)
                .addParam(paramKey, paramValue)
				.body(body)
				.build();
		
		// then
		assertThat(actual).isNotNull();
		assertThat(actual.getUri()).isEqualTo(expectedUri);
		assertThat(actual.getMethod()).isEqualTo(HttpMethod.POST);
		assertThat(actual.getHttpEntity()).isNotNull().satisfies(entity -> {
			assertThat(entity.getHeaders()).isEqualTo(expectedHeader);
			assertThat(entity.getBody()).usingRecursiveComparison().isEqualTo(body);
		});
		assertThat(actual.getResponseType()).isNull();
		assertThat(actual.getTypeReference()).isEqualTo(typeReference);
	}
}
