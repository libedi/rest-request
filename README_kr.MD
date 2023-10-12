# **rest-request**
***rest-request***는 Spring Web의 `RestTemplate`을 사용할 경우, 생성해야 할 많은 파라미터를 간편하게 만들어주는 도구입니다.

***rest-request***는 Spring Webflux의 `WebClient`에서 영감을 받아, 이와 유사하게 메소드 체이닝 방식으로 HTTP Header, Query Parameter, Form Data, Request Body 등을 생성할 수 있습니다.
~~~java
RestRequest<ResponseType> request = RestRequest.resp(ResponseType.class)
                                               .uri("http://www.api.com/resources")
                                               .post()
                                               .addHeader("X-Test-Header-Name", "XTestHeaderValue")
                                               .addParam("queryParamKey", "queryParamValue")
                                               .body(requestBodyObject)
                                               .build();
ResponsType response = restTemplate.exchange(request.getUri(),
                                             request.getMethod(),
                                             request.getHttpEntity(),
                                             request.getResponseType());
~~~
위와 같이 `RestTemplate`에 필요한 모든 정보를 간편하고 가독성 좋게 생성할 수 있습니다.

## **`RestRequest` 생성방법**
***rest-request***는 요청 정보를 생성하는 `RestRequest` 객체를 제공하며, 다음과 같은 순서에 의해 생성합니다.

### **1. 응답 타입**
응답 타입은 4가지 방식으로 설정할 수 있습니다.
- 1. 기본 설정 : `Map<String, Object>`
- 2. T타입 설정 : `Class<T>`
- 3. T타입 제네릭 설정 : `ParameterizedTypeReference<T>`
- 4. Void타입 : `Class<Void>`
~~~java
// 1. 기본설정 : Map<String, Object>
RestRequest.mapResp()

// 2. T타입 설정 : Class<T>
RestRequest.resp(ResponseType.class)

// 3. T타입 제네릭 설정 : ParameterizedTypeReference<T>
RestRequest.resp(new ParameterizedTypeReference<List<ResponseType>>(){})

// 4. Void타입 설정 : Class<Void>
RestRequest.nonResp()
~~~

### **2. 요청 URI**
요청할 URI는 `java.net.URI` 또는 `java.lang.String` 타입의 파라미터를 지원합니다.
~~~java
// java.net.URI 타입
URI uri = URI.create("http://www.api.com/resources");
RestRequest.resp(ResponseType.class)
           .uri(uri)

// java.lang.String 타입
RestRequest.resp(ResponseType.class)
           .uri("http://www.api.com/resources")
~~~

### **3. HTTP Method**
HTTP Method는 직관적인 이름의 메소드명으로 지정할 수 있습니다. 지원하는 메소드는 GET / POST / PUT / PATCH / DELETE 입니다. 여기서 POST / PUT / PATCH 방식은 이후에 Request Body를 설정가능하게 합니다.
~~~java
RestRequest.resp(ResponseType.class)
           .uri(uri)
           .get() / .post() / .put() / .patch() / .delete()
~~~

### **4. HTTP Headers / Query Parameters / Form Datas / Request Body**
이후에는 HTTP Headers, Query Parameter, Form Datas, Request Body를 설정할 수 있습니다.

- **HTTP Header**  
HTTP Header는 **`addHeader()`, `accept()`, `contentType()`, `authorization()`, `basicAuth()`, `bearerToken()`** 메소드로 추가할 수 있습니다.
    ~~~java
    RestRequest.resp(ResponseType.class)
               .uri(uri)
               .get()
               .addHeader("headerName", "headerValue")
               .accept(MediaType.APPLICATION_JSON)  // 지원타입 : MediaType, String
               .contentType("application/json")     // 지원타입 : MediaType, String
               .authorization("authValue")
               .basicAuth("username", "password")
               .bearerToken("tokenValue")
    ~~~
- **Form Parameter**  
**`addParam()`, `setParams()`** 메소드로 Form Parameter를 추가할 수 있습니다.  
이전에 `get()` / `delete()`를 지정했다면, Query Parameter가 생성되고, `post()` / `put()` / `patch()`를 지정했다면, Form Data가 생성됩니다.  
만약, `post()` / `put()` / `patch()`를 지정했더라도, Request Body와 Form Parameter 둘 다 설정되었다면, Query Parameter로 생성됩니다.
    ~~~java
    // Query Parameter 생성
    RestRequest.resp(ResponseType.class)
               .uri(uri)
               .get()
               .addParam("paramKey", "paramValue")  // Query Parameter 추가 : key-value 방식
               .setParams(multiValueMap)            // Query Parameter 추가 : MultiValueMap<String, Object>
               .setParams(map)                      // Query Parameter 추가 : Map<String, Object>
               .setParams(object)                   // Query Parameter 추가 : Object

    // Form Data 생성
    RestRequest.resp(ResponseType.class)
               .uri(uri)
               .post()
               .addParam("paramKey", "paramValue")
    ~~~
- **Request Body**  
**`body()`** 메소드로 설정할 수 있습니다. (`post()` / `put()` / `patch()` 지정시에만 `body()` 메소드를 호출할 수 있습니다.)
    ~~~java
    RestRequest.resp(ResponseType.class)
               .uri(uri)
               .put()
               .addParam("queryParamKey", "queryParamValue")  // Query Parameter로 생성
               .body(requestBodyObject)
    ~~~
- **파일 첨부**  
**`addFile()`** 메소드로 설정할 수 있습니다. 지원하는 파라미터 형식은 `File`, `Path`, `MultipartFile`입니다. Content Type 헤더가 설정되어 있지 않으면, 자동으로 `multipart/form-data`값으로 설정합니다.
    ~~~java
    RestRequest.resp(ResponseType.class)
               .uri(uri)
               .post()
               .addFile("file1", new File("test.txt"))
               .addFile("file2", Paths.get("test.txt"))
               .addFile("file3", multipartFile)
    ~~~
    Request Body가 설정되어 있으면 `multipart/mixed`로 설정됩니다. 다만, Request Body는 form data에 `body`라는 key로 설정되므로, 가능하면 `addParam()`을 이용하여 직접 설정하시길 권장드립니다. (`post()` / `put()` / `patch()` 지정시에만 `addFile()` 메소드를 호출할 수 있습니다.)
    ~~~java
    RestRequest.resp(ResponseType.class)
               .uri(uri)
               .post()
               .contentType(MediaType.MULTIPART_MIXED)
               .addParam("json", new ObjectMapper().writeValueAsString(body))
               .addFile("attach", Paths.get("test.txt"))
               .build();
    ~~~

### **5. build()**
최종적으로 **`build()`** 를 호출하여 `RestRequest`를 생성합니다.  
`RestRequest`는 아래의 속성들을 불러올 수 있습니다.
- `URI`
- `HttpMethod`
- `HttpEntity`
- `Class<T>`
- `ParameterizedTypeReference<T>`
~~~java
// Class<T> 지정시
RestRequest<ResponseType> request = RestRequest.resp(ResponseType.class)
                                               .uri(uri)
                                               .get()
                                               .build();
ResponseType response = restTemplate.exchange(request.getUri(),
                                              request.getMethod(),
                                              request.getHttpEntity(),
                                              request.getResponseType());

// ParameterizedTypeReference<T> 지정시
RestRequest<List<ResponseType>> request = RestRequest.resp(new ParameterizedTypeReference<List<ResponseType>>(){})
                                                     .uri(uri)
                                                     .get()
                                                     .build();
List<ResponseType> response = restTemplate.exchange(request.getUri(),
                                                    request.getMethod(),
                                                    request.getHttpEntity(),
                                                    request.getTypeReference());
~~~

## **RestClientAdapter**
***rest-request***는 `RestRequest`와 `RestTemplate`을 연동하는 클래스인 **`RestClientAdapter`** 를 제공합니다.  
**`RestClientAdapter`** 는 다음과 같이 두가지 방식을 통해 Bean으로 생성할 수 있습니다.
~~~java
@Configuration
public class WebConfig {
    @Bean
    @ConditionalOnMissingBean(RestTemplate.class)
    public RestClientAdapter restClientAdapter() {
        return new RestClientAdpater();
    }
    
    @Bean
    @ConditionalOnBean(RestTemplate.class)
    public RestClientAdapter restClientAdapter(final RestTemplate restTemplate) {
        return new RestClientAdapter(restTemplate);
    }
}
~~~
`DefaultRestClientAdapter` 기본 생성자는 내부적으로 `new RestTemplate()`을 사용하여 생성합니다.  
혹은 Bean으로 생성한 `RestTemplate`이 있다면, 해당 `RestTemplate`을 주입받아 생성합니다.  
  
**`RestClientAdpater`** 는 다음과 같이 사용할 수 있습니다.
~~~java
@Service
public class WebService {
    @Autowired
    private RestClientAdpater restClient;
    
    public ResponseEntity<Resource> getSome(ResourceDto dto) {
        return restClient.send(RestRequest.resp(Resource.class)
                                          .uri("http://www.api.com/resources")
                                          .get()
                                          .setParams(dto)
                                          .build());
    }
    
    public Optional<Resource> postSome(Resource resource) {
        return restClient.sendForBody(RestRequest.resp(Resource.class)
                                                 .uri("http://www.api.com/resources")
                                                 .post()
                                                 .body(resource)
                                                 .build());
    }

    public CompletableFuture<ResponseEntity<Resource>> getSomeAsync(ResourceDto dto) {
        return restClient.sendAsync(RestRequest.resp(Resource.class)
                                               .uri("http://www.api.com/resources")
                                               .get()
                                               .setParams(dto)
                                               .build());
    }

    public CompletableFuture<ResponseEntity<Resource>> getSomeAsync(ResourceDto dto, Executor executor) {
        return restClient.sendAsync(RestRequest.resp(Resource.class)
                                               .uri("http://www.api.com/resources")
                                               .get()
                                               .setParams(dto)
                                               .build(),
                                    executor);
    }
}
~~~

## **최소사양**
- Java 8 이상
- Spring Web 5.3.25 이상

## **설치**
- ### **Maven**
~~~xml
<dependency>
    <groupId>io.github.libedi</groupId>
    <artifactId>rest-request</artifactId>
    <version>2.1.2</version>
</dependency>
~~~
- ### **Gradle**
~~~groovy
implementation 'io.github.libedi:rest-request:2.1.2'
~~~
