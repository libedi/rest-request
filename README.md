# **rest-request**
***rest-request*** is a tool that makes it easy to create many parameters that need to be generated when using Spring Web's `RestTemplate`.  

***rest-request*** is inspired by Spring Webflux's `WebClient`, which similarly allows method-chaining methods to create HTTP Headers, Query Parameters, Form Data, Request Body, and more.
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
As above, you can easily and readably generate all the information you need for `RestTemplate`.

## **How to create `RestRequest`**
***rest-request*** provides `RestRequest` object that creates request information, and generated in the following order:

### **1. Response Type**
There are four types of responses:
- 1. Preference : `Map<String, Object>`
- 2. T type : `Class<T>`
- 3. Generic T type : `ParameterizedTypeReference<T>`
- 4. Void type : `Class<Void>`
~~~java
// 1. Preference : Map<String, Object>
RestRequest.mapResp()

// 2. T type : Class<T>
RestRequest.resp(ResponseType.class)

// 3. Generic T type : ParameterizedTypeReference<T>
RestRequest.resp(new ParameterizedTypeReference<List<ResponseType>>(){})

// 4. Void type : Class<Void>
RestRequest.nonResp()
~~~

### **2. Request URI**
The URI to request supports paramters of the `java.net.URI` or `String` type.
~~~java
// java.net.URI type
URI uri = URI.create("http://www.api.com/resources");
RestRequest.resp(ResponseType.class)
           .uri(uri)

// String type
RestRequest.resp(ResponseType.class)
           .uri("http://www.api.com/resources")
~~~

### **3. HTTP Method**
HTTP Method can be specified by a method name with an intuitive name. The methods that supports it are GET / POST / PUT / PATCH / DELETE. Here, the POST / PUT / PATCH method allows you to set the Request Body in the near time.
~~~java
RestRequest.resp(ResponseType.class)
           .uri(uri)
           .get() / .post() / .put() / .patch() / .delete()
~~~

### **4. HTTP Headers / Query Parameters / Form Datas / Request Body**
Afterwards, you can set HTTP Headers, Query Parameters, Form Datas, and Request Body.

- **HTTP Header**  
HTTP Header can be added as an **`addHeader()`, `accept()`, `contentType()`, `authorization()`, `basicAuth()`** and **`bearerToken()`** methods.
    ~~~java
    RestRequest.resp(ResponseType.class)
               .uri(uri)
               .get()
               .addHeader("headerName", "headerValue")
               .accept(MediaType.APPLICATION_JSON)  // Support type : MediaType, String
               .contentType("application/json")     // Support type : MediaType, String
               .authorization("authValue")
               .basicAuth("username", "password")
               .bearerToken("tokenValue")
    ~~~
- **Form Parameter**  
You can add Form Parameters with the **`addParam()`, `setParams()`** methods.  
If you previously specified `get()` / `delete()`, Query Parameter is generated, and if `post()` / `put()` / `patch()` is specified, Form Data is generated.  
If both Request Body and Form Parameter are set, they are generated as Query Parameters, even if `post()` / `put()` / `patch()` is specified.
    ~~~java
    // Generate a Query Parameter
    RestRequest.resp(ResponseType.class)
               .uri(uri)
               .get()
               .addParam("paramKey", "paramValue")  // Add Query Parameter : key-value
               .setParams(multiValueMap)            // Add Query Parameter : MultiValueMap<String, Object>
               .setParams(map)                      // Add Query Parameter : Map<String, Object>
               .setParams(object)                   // Add Query Parameter : Object

    // Generate Form Datas
    RestRequest.resp(ResponseType.class)
               .uri(uri)
               .post()
               .addParam("paramKey", "paramValue")
    ~~~
- **Request Body**  
You can set it as a **`body()`** method. (You can call the `body()` method only when specifying `post()` / `put()` / `patch()` methods.)
    ~~~java
    RestRequest.resp(ResponseType.class)
               .put()
               .addParam("queryParamKey", "queryParamValue")  // Generate Query Parameter
               .body(requestBodyObject)
    ~~~
- **Attach File**  
You can set it as a **`addFile()`** method. The supported parameter types are `File`, `Path`, and `MultipartFile`. If the Content Type header is not set, it is automatically set to the value `multipart/form-data`.
    ~~~java
    RestRequest.resp(ResponseType.class)
               .post()
               .addFile("file1", new File("test.txt"))
               .addFile("file2", Paths.get("test.txt"))
               .addFile("file3", multipartFile)
    ~~~
    If Request Body is set and Spring version is 5.2 or higher, it is set to `multipart/mixed`. However, since Request Body is set as a key called `body` in the form data, it is recommended to set it directly using `addParam()` if possible. (You can call the `addFile()` method only when specifying `post()` / `put()` / `patch()` methods.)
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
Finally, call **`build()`** method to generate `RestRequest`.  
`RestRequest` can load the following properties:
- `URI`
- `HttpMethod`
- `HttpEntity`
- `Class<T>`
- `ParameterizedTypeReference<T>`
~~~java
// Class<T> when specifying
RestRequest<ResponseType> request = RestRequest.resp(ResponseType.class)
                                               .uri(uri)
                                               .get()
                                               .build();
ResponseType response = restTemplate.exchange(request.getUri(),
                                              request.getMethod(),
                                              request.getHttpEntity(),
                                              request.getResponseType());

// ParameterizedTypeReference<T> when specifying
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
***rest-request*** provides **`RestClientAdapter`**, a class that works with `RestRequest` and `RestTemplate`.  
**`RestClientAdapter`** can be generated as a Bean in two ways:
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
  
**`RestClientAdapter`** can be used as follows:
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

## **Requirements**
- Java 8 or higher
- Spring Web 4.3 or higher

## **Installation**
- ### **Maven**
~~~xml
<dependency>
    <groupId>io.github.libedi</groupId>
    <artifactId>rest-request</artifactId>
    <version>2.1.1</version>
</dependency>
~~~
- ### **Gradle**
~~~groovy
implementation 'io.github.libedi:rest-request:2.1.1'
~~~
