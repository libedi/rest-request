package io.github.libedi.restrequest.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.libedi.restrequest.RestRequest;
import lombok.Builder;
import lombok.Getter;

public class RestRequestTest {

    @Getter
    @Builder
    private static class TestBody {
        private final String id;
        private final List<String> list;
    }

    @DisplayName("application/x-www-form-urlencoded 방식 테스트")
    @Test
    public void formUri() {
        // given
        final Class<String> responseType = String.class;
        final String uri = "http://localhost:8080/test";
        final MediaType accept = MediaType.APPLICATION_FORM_URLENCODED;
        final MediaType contentType = MediaType.APPLICATION_JSON;
        final String customHeaderName = "X-Test-Header";
        final String customHeaderValue = "XTestHeaderValue";
        final String basicUsername = "username";
        final String basicPassword = "password";
        final String paramKey = "paramKey";
        final String paramValue = "paramValue";

        final Charset charset = StandardCharsets.ISO_8859_1;
        final HttpHeaders expectedHeader = new HttpHeaders();
        expectedHeader.add(customHeaderName, customHeaderValue);
        expectedHeader.add(HttpHeaders.ACCEPT, accept.toString());
        expectedHeader.add(HttpHeaders.CONTENT_TYPE, contentType.toString());
        expectedHeader.set(HttpHeaders.AUTHORIZATION, "Basic " + new String(
                Base64.getEncoder().encode((basicUsername + ":" + basicPassword).getBytes(charset)), charset));

        final URI expectedUri = UriComponentsBuilder.fromUriString(uri)
                .queryParam(paramKey, paramValue)
                .build()
                .toUri();

        // when
        final RestRequest<String> actual = RestRequest.resp(responseType)
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

    @DisplayName("application/json 방식")
    @Test
    public void requestBody() {
        // given
        final ParameterizedTypeReference<List<String>> typeReference = new ParameterizedTypeReference<List<String>>() {
        };
        final String uri = "http://localhost:8080/test";
        final String accept = MediaType.APPLICATION_JSON_VALUE;
        final String contentType = MediaType.APPLICATION_XML_VALUE;
        final String customHeaderName = "X-Test-Header";
        final String customHeaderValue = "XTestHeaderValue";
        final String bearerToken = "bearerToken";
        final String paramKey = "paramKey";
        final String paramValue = "paramValue";
        final TestBody body = TestBody.builder()
                .id("testId")
                .list(Arrays.asList("a", "b", "c"))
                .build();

        final HttpHeaders expectedHeader = new HttpHeaders();
        expectedHeader.add(customHeaderName, customHeaderValue);
        expectedHeader.add(HttpHeaders.ACCEPT, accept);
        expectedHeader.add(HttpHeaders.CONTENT_TYPE, contentType);
        expectedHeader.set(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);

        final URI expectedUri = UriComponentsBuilder.fromUriString(uri)
                .queryParam(paramKey, paramValue)
                .build()
                .toUri();

        // when
        final RestRequest<List<String>> actual = RestRequest.resp(typeReference)
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

    @DisplayName("multipart/form-data 방식 첨부파일 테스트")
    @Test
    public void multipartData() throws Exception {
        // given
        final Path attachPath = Paths.get(new ClassPathResource("file/test.txt").getURI());
        final File attachFile = attachPath.toFile();
        final MockMultipartFile multipartFile = new MockMultipartFile("file3", "test.txt", MediaType.TEXT_PLAIN_VALUE,
                new FileInputStream(attachFile));

        // when
        final RestRequest<String> actual = RestRequest.resp(String.class)
                .uri("http://www.test.com/upload")
                .post()
                .addFile("file1", attachFile)
                .addFile("file2", attachPath)
                .addFile("file3", multipartFile)
                .build();

        // then
        assertThat(actual.getHttpEntity()).satisfies(http -> {
            assertThat(http.getHeaders().getContentType()).isEqualTo(MediaType.MULTIPART_FORM_DATA);
            assertThat(http.getBody()).isInstanceOf(MultiValueMap.class);
            @SuppressWarnings("unchecked")
            final MultiValueMap<String, Object> form = (MultiValueMap<String, Object>) http.getBody();
            assertThat(form.get("file1")).first().isInstanceOf(FileSystemResource.class);
            assertThat(form.get("file1")).first().usingRecursiveComparison()
                    .isEqualTo(new FileSystemResource(attachFile));
            assertThat(form.get("file2")).first().isInstanceOf(FileSystemResource.class);
            assertThat(form.get("file2")).first().usingRecursiveComparison()
                    .isEqualTo(new FileSystemResource(attachPath.toFile()));
            assertThat(form.get("file3")).first().isInstanceOf(ByteArrayResource.class);
            assertThat(form.get("file3")).first().usingRecursiveComparison()
                    .isEqualTo(new ByteArrayResource(multipartFile.getBytes()) {
                        @Override
                        public String getFilename() {
                            return multipartFile.getOriginalFilename();
                        }
                    });
        });
    }

    @DisplayName("multipart/mixed 방식 첨부파일 테스트: body + file")
    @Test
    public void multipartMixedDataWithBodyAndFile() throws Exception {
        // given
        final Path attachPath = Paths.get(new ClassPathResource("file/test.txt").getURI());
        final TestBody body = TestBody.builder()
                .id("testId")
                .list(Arrays.asList("a", "b", "c"))
                .build();

        // when
        final RestRequest<String> actual = RestRequest.resp(String.class)
                .uri("http://www.test.com/upload")
                .post()
                .body(body)
                .addFile("attach", attachPath)
                .build();

        // then
        assertThat(actual.getHttpEntity()).satisfies(http -> {
            assertThat(http.getHeaders().getContentType()).isEqualTo(MediaType.valueOf("multipart/mixed"));
            assertThat(http.getBody()).isInstanceOf(MultiValueMap.class);
            @SuppressWarnings("unchecked")
            final MultiValueMap<String, Object> form = (MultiValueMap<String, Object>) http.getBody();
            assertThat(form.get("attach")).first().isInstanceOf(FileSystemResource.class);
            assertThat(form.get("attach")).first().usingRecursiveComparison()
                    .isEqualTo(new FileSystemResource(attachPath.toFile()));
            assertThat(form.get("body")).first().asString().isEqualTo(new ObjectMapper().writeValueAsString(body));
        });
    }

    @DisplayName("multipart/mixed 방식 첨부파일 테스트: parameter")
    @Test
    public void multipartMixedDataWithMultiValueMap() throws Exception {
        // given
        final Path attachPath = Paths.get(new ClassPathResource("file/test.txt").getURI());
        final TestBody body = TestBody.builder()
                .id("testId")
                .list(Arrays.asList("a", "b", "c"))
                .build();

        // when
        final RestRequest<String> actual = RestRequest.resp(String.class)
                .uri("http://www.test.com/upload")
                .post()
                .contentType("multipart/mixed")
                .addParam("requestBody", new ObjectMapper().writeValueAsString(body))
                .addFile("attach", attachPath)
                .build();

        // then
        assertThat(actual.getHttpEntity()).satisfies(http -> {
            assertThat(http.getHeaders().getContentType()).isEqualTo(MediaType.valueOf("multipart/mixed"));
            assertThat(http.getBody()).isInstanceOf(MultiValueMap.class);
            @SuppressWarnings("unchecked")
            final MultiValueMap<String, Object> form = (MultiValueMap<String, Object>) http.getBody();
            assertThat(form.get("attach")).first().isInstanceOf(FileSystemResource.class);
            assertThat(form.get("attach")).first().usingRecursiveComparison()
                    .isEqualTo(new FileSystemResource(attachPath.toFile()));
            assertThat(form.get("requestBody")).first().asString()
                    .isEqualTo(new ObjectMapper().writeValueAsString(body));
        });
    }
}
