# rest-request
rest-request는 Spring Web의 RestTemplate을 사용할 경우, 생성해야 할 많은 파라미터를 간편하게 만들어주는 도구입니다.

rest-request는 Spring Webflux의 WebClient에서 영감을 받아, 이와 유사하게 메소드 체이닝 방식으로 HTTP Header, Query Parameter, Form Data, Request Body 등을 생성할 수 있습니다.
~~~java
RestRequest<ResponseType> request = RestRequest.response(ResponseType.class)
                                               .uri("http://www.api.com/resources")
                                               .post()
                                               .addHeader("X-Test-Header-Name", "XTestHeaderValue")
                                               .addParameter("queryParamKey", "queryParamValue")
                                               .body(requestBodyObject)
                                               .build();
ResponsType response = restTemplate.exchange(request.getUri(),
                                             request.getMethod(),
                                             request.getHttpEntity(),
                                             request.getResponseType());
~~~
위와 같이 RestRequest를 통해, RestTemplate에 필요한 모든 정보를 간편하고 가독성 좋게 생성할 수 있습니다.

## RestRequest 생성방법
rest-request는 다음과 같은 순서에 의해 생성합니다.

### 1. 응답 타입
응답 타입은 3가지 방식으로 설정할 수 있습니다.
- 1. 기본 설정 : `Map<String, Object>`
- 2. T타입 설정 : `Class<T>`
- 3. T타입 제네릭 설정 : `ParameterizedTypeReference<T>`
~~~java
// 1. 기본설정 : Map<String, Object>
RestRequest.mapResponse()

// 2. T타입 설정 : Class<T>
RestRequest.response(ResponseType.class)

// 3. T타입 제네릭 설정 : ParameterizedTypeReference<T>
RestRequest.response(new ParameterizedTypeReference<List<ResponseType>>(){})
~~~

### 2. 요청 URI
요청할 URI는 `java.net.URI` 또는 `java.lang.String` 타입의 파라미터를 지원합니다.
~~~java
// java.net.URI 타입
URI uri = URI.create("http://www.api.com/resources");
RestRequest.response(ResponseType.class)
           .uri(uri)

// java.lang.String 타입
RestRequest.response(ResponseType.class)
           .uri("http://www.api.com/resources")
~~~

### 3. HTTP Method
HTTP Method는 직관적인 이름의 메소드명으로 지정할 수 있습니다. 지원하는 메소드는 GET / POST / PUT / PATCH / DELETE 입니다. 여기서 POST / PUT / PATCH 방식은 이후에 Request Body를 설정가능하게 합니다.
~~~java
RestRequest.response(ResponseType.class)
           .uri(uri)
           .get() / .post() / .put() / .patch() / .delete()
~~~

### 4. HTTP Header / Query Parameter / Form Data / Request Body
이후에는 HTTP Header, Query Parameter, Form Data, Request Body를 설정할 수 있습니다.

- HTTP Header : HTTP Header는 addHeader(), accept(), contentType() 메소드로 추가할 수 있습니다.
~~~java
RestRequest.response(ResponseType.class)
           .uri(uri)
           .get()
           .addHeader("headerName", "headerValue")
           .accept(MediaType.APPLICATION_JSON)  // 지원타입 : MediaType, String
           .contentType("application/json")     // 지원타입 : MediaType, String
~~~
- Form Parameter  
addParameter(), putAllParameters() 메소드로 Form Parameter를 추가할 수 있습니다.  
이전에 get() / delete()를 지정했다면, Query Parameter가 생성되고, post() / put() / patch()를 지정했다면, Form Data가 생성됩니다.  
만약, post() / put() / patch()를 지정했더라도, Request Body와 Form Parameter 둘 다 설정되었다면, Query Parameter로 생성됩니다.
~~~java
// Query Parameter 생성
RestRequest.response(ResponseType.class)
           .uri(uri)
           .get()
           .addParameter("paramKey", "paramValue")  // Query Parameter 추가 : key-value 방식
           .putAllParameters(multiValueMap)         // Query Parameter 추가 : MultiValueMap<String, Object>
           .putAllParameters(map)                   // Query Parameter 추가 : Map<String, Object>
           .putAllParameters(object)                // Query Parameter 추가 : Object

// Form Data 생성
RestRequest.response(ResponseType.class)
           .uri(uri)
           .post()
           .addParameter("paramKey", "paramValue")
~~~
- Request Body : body() 메소드로 설정할 수 있습니다. (post() / put() / patch() 지정시에만 body() 메소드를 호출할 수 있습니다.)
~~~java
RestRequest.response(ResponseType.class)
           .put()
           .addParameter("queryParamKey", "queryParamValue")  // Query Parameter로 생성
           .body(requestBodyObject)
~~~

### 5. build()
최종적으로 build()를 호출하여 RestRequest를 생성합니다.  
RestRequest는 아래의 속성들을 불러올 수 있습니다.
- `URI`
- `HttpMethod`
- `HttpEntity`
- `Class<T>`
- `ParameterizedTypeReference<T>`
~~~java
// Class<T> 지정시
RestRequest<ResponseType> request = RestRequest.response(ResponseType.class)
                                               .uri(uri)
                                               .get()
                                               .build();
ResponseType response = restTemplate.exchange(request.getUri(),
                                              request.getMethod(),
                                              request.getHttpEntity(),
                                              request.getResponseType());

// ParameterizedTypeReference<T> 지정시
RestRequest<List<ResponseType>> request = RestRequest.response(new ParameterizedTypeReference<List<ResponseType>>(){})
                                               .uri(uri)
                                               .get()
                                               .build();
List<ResponseType> response = restTemplate.exchange(request.getUri(),
                                                    request.getMethod(),
                                                    request.getHttpEntity(),
                                                    request.getTypeReference());
                  
~~~

## 최소사양
- Java 8 or higher
- Spring Web 4.3 or higher

## 설치
### Maven
~~~xml
<dependency>
  <groupId>io.github.libedi</groupId>
  <artifactId>rest-request</artifactId>
  <version>0.1.1</version>
</dependency>
~~~
### Gradle
~~~groovy
dependencies {
	implementation 'io.github.libedi:rest-request:0.1.1'
}
~~~
