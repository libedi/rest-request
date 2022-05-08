# rest-request
***rest-request*** is a tool that makes it easy to create many parameters that need to be generated when using Spring Web's `RestTemplate`.  

***rest-request*** is inspired by Spring Webflux's WebClient, which similarly allows method-chaining methods to create HTTP Headers, Query Parameters, Form Data, Request Body, and more.
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
With `RestRequest` as above, you can easily and readably generate all the information you need for `RestTemplate`.

## How to create `RestRequest`
***rest-request*** is generated in the following order:

### 1. Response Type
There are three types of responses:
- 1. Preference : `Map<String, Object>`
- 2. T type : `Class<T>`
- 3. Generic T type : `ParameterizedTypeReference<T>`
- 4. Void type : `Class<Void>`
~~~java
// 1. Preference : Map<String, Object>
RestRequest.mapResponse()

// 2. T type : Class<T>
RestRequest.response(ResponseType.class)

// 3. Generic T type : ParameterizedTypeReference<T>
RestRequest.response(new ParameterizedTypeReference<List<ResponseType>>(){})

// 4. Void type : Class<Void>
RestRequest.nonResponse()
~~~

### 2. Request URI
The URI to request supports paramters of the `java.net.URI` or `String` type.
~~~java
// java.net.URI type
URI uri = URI.create("http://www.api.com/resources");
RestRequest.response(ResponseType.class)
           .uri(uri)

// String type
RestRequest.response(ResponseType.class)
           .uri("http://www.api.com/resources")
~~~

### 3. HTTP Method
HTTP Method can be specified by a method name with an intuitive name. The methods that supports it are GET / POST / PUT / PATCH / DELETE. Here, the POST / PUT / PATCH method allows you to set the Request Body in the near time.
~~~java
RestRequest.response(ResponseType.class)
           .uri(uri)
           .get() / .post() / .put() / .patch() / .delete()
~~~

### 4. HTTP Headers / Query Parameters / Form Datas / Request Body
Afterwards, you can set HTTP Headers, Query Parameters, Form Datas, and Request Body.

- HTTP Header : HTTP Header can be added as an `addHeader()`, `accept()`, and `contentType()` methods.
~~~java
RestRequest.response(ResponseType.class)
           .uri(uri)
           .get()
           .addHeader("headerName", "headerValue")
           .accept(MediaType.APPLICATION_JSON)  // Support type : MediaType, String
           .contentType("application/json")     // Support type : MediaType, String
~~~
- Form Parameter  
You can add Form Parameters with the `addParameter()`, `putAllParamters()` methods.  
If you previously specified `get()` / `delete()`, Query Parameter is generated, and if `post()` / `put()` / `patch()` is specified, Form Data is generated.  
If both Request Body and Form Parameter are set, they are generated as Query Parameters, even if `post()` / `put()` / `patch()` is specified.
~~~java
// Generate a Query Parameter
RestRequest.response(ResponseType.class)
           .uri(uri)
           .get()
           .addParameter("paramKey", "paramValue")  // Add Query Parameter : key-value
           .putAllParameters(multiValueMap)         // Add Query Parameter : MultiValueMap<String, Object>
           .putAllParameters(map)                   // Add Query Parameter : Map<String, Object>
           .putAllParameters(object)                // Add Query Parameter : Object

// Generate Form Datas
RestRequest.response(ResponseType.class)
           .uri(uri)
           .post()
           .addParameter("paramKey", "paramValue")
~~~
- Request Body : You can set it as a `body()` method. (You can call the `body()` method only when specifying `post()` / `put()` / `patch()` methods.)
~~~java
RestRequest.response(ResponseType.class)
           .put()
           .addParameter("queryParamKey", "queryParamValue")  // Generate Query Parameter
           .body(requestBodyObject)
~~~

### 5. build()
Finally, call `build()` method to generate `RestRequest`.  
`RestRequest` can load the following properties:
- `URI`
- `HttpMethod`
- `HttpEntity`
- `Class<T>`
- `ParameterizedTypeReference<T>`
~~~java
// Class<T> when specifying
RestRequest<ResponseType> request = RestRequest.response(ResponseType.class)
                                               .uri(uri)
                                               .get()
                                               .build();
ResponseType response = restTemplate.exchange(request.getUri(),
                                              request.getMethod(),
                                              request.getHttpEntity(),
                                              request.getResponseType());

// ParameterizedTypeReference<T> when specifying
RestRequest<List<ResponseType>> request = RestRequest.response(new ParameterizedTypeReference<List<ResponseType>>(){})
                                               .uri(uri)
                                               .get()
                                               .build();
List<ResponseType> response = restTemplate.exchange(request.getUri(),
                                                    request.getMethod(),
                                                    request.getHttpEntity(),
                                                    request.getTypeReference());
                  
~~~

## RestClientAdapter
***rest-request*** provides `RestClientAdapter`, a class that works with `RestRequest` and `RestTemplate`.  
`RestClientAdapter` can be generated as a Bean in two ways:
~~~java
@Configuration
public class WebConfig {
    @Bean
    @ConditionalOnMissingBean(RestTemplate.class)
    public RestClientAdapter restClientAdapter() {
        return new DefaultRestClientAdpater();
    }
    
    @Bean
    @ConditionalOnBean(RestTemplate.class)
    public RestClientAdapter restClientAdapter(final RestTemplate restTemplate) {
        return new DefaultRestClientAdapter(restTemplate);
    }
}
~~~
`DefaultRestClientAdapter` default constructor is internally generated using `new RestTemplate()`.  
Or, if you have `RestTemplate` generated by Bean, you're injected with that `RestTemplate`.  
  
`RestClientAdapter` can be used as follows:
~~~java
@Service
public class WebService {
    @Autowired
    private RestClientAdpater restClient;
    
    public ResponseEntity<Resource> getSome(ResourceDto dto) {
        return restClient.execute(RestRequest.response(Resource.class)
                                             .uri("http://www.api.com/resources")
                                             .get()
                                             .putAllParameters(dto)
                                             .build());
    }
    
    public Resource postSome(Resource resource) {
        return restClient.executeForObject(RestRequest.response(Resource.class)
                                                      .uri("http://www.api.com/resources")
                                                      .post()
                                                      .body(resource)
                                                      .build());
    }
}
~~~

## Requirements
- Java 8 or higher
- Spring Web 3.2 or higher

## Install
### Maven
~~~xml
<dependency>
    <groupId>io.github.libedi</groupId>
    <artifactId>rest-request</artifactId>
    <version>0.4.0</version>
</dependency>
~~~
### Gradle
~~~groovy
dependencies {
    implementation 'io.github.libedi:rest-request:0.4.0'
}
~~~
