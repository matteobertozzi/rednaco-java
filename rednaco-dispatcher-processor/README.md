# REDNACO Dipatcher Processor

### Routing

Method routing is done via annotations:
 - **@UriPrefix** allows to specify a path prefix to prepend to every uri mapped in the class.
 - **@UriMapping** is a static "direct" mapping
 - **@UriVariableMapping** allows you to extract parts of the path
 - **@UriPatternMapping** allows you to specify a Regex for one or more parts of the path.
```java
@UriPrefix("/v1/module")
public class MyHandler {
  @UriMapping(uri = "/foo", method = UriMethod.POST)
  public MyResponse myMethod(MyRequest req) {
   ...
  }

  @UriVariableMapping(uri = "/obj/{id}/detail")
  public MyResponse myDetail(@UriVariable("id") long id) {
   ...
  }

  @UriPatternMapping(uri = "/files/(.*)")
  public File myFileHandler(@UriPattern(0) String path) {
    ...
  }
}
```

### Session
Session is extracted, verified and permissions enforced via annotations.
 - **@AllowPublicAccess**: The method is public does not even require a session
 - **@RequirePermission**: Requires a session and permission for the specific *module/action*.
 - **@TokenSession**: If specified a session is required to access the method.

```java
public class MyHandler {
  @AllowPublicAccess
  public MyResponse myPublicMethod() {
    // everyone can call call this function, even without authorization
  }

  @AllowPublicAccess
  public MyResponse mySemiPublicMethod(@TokenSession AuthSession session) {
    // everyone with a "valid" session of any type can access this method
  }

  @RequirePermission(module = "test", actions = "TEST_STUFF")
  public MyResponse myProtectedMethod(@TokenSession AuthSession session) {
    // everyone with a valid session of any type
    // with the module "test" and the action "TEST_STUFF"
    // can access this method
  }

  @RequirePermission(module = "test", actions = "WRITE_STUFF")
  public MyResponse myUserOnlyMethod(@TokenSession UserSession session) {
    // users with a valid session
    // with the module "test" and the action "WRITE_STUFF"
    // can access this method
  }
}
```

### Params
You can extract metadata and body easily with annotations and type converters
 - **@QueryParam**: Extract one or a list of values from the query params
 - **@HeaderValue**: Extract one or a list of values from the headers
 - **@MetaParam**: Extract one or a list of values from the headers or query params
```java
public class MyHandler {
  @UriMapping(uri = "/foo")
  public void foo(
      @QueryParam("a") int a,    // Extract and convert the first 'a' from query params
      @QueryParam("b") long[] b, // Extract and convert the 'b' values from query params
      @QueryParam(name = "c", defaultValue = "123") int c
  ) { ... }

  @UriMapping(uri = "/bar")
  public void bar(
      @HeaderValue("X-Aaa") int a,    // Extract and convert the first 'X-Aaa' from the headers
      @HeaderValue("X-Bbb") long[] b, // Extract and convert the 'X-Bbb' values from the headers
      @HeaderValue(name = "X-Ccc", defaultValue = "123") int c
  ) { ... }

  @UriMapping(uri = "/baz")
  public void baz(
      @MetaParam(header = "X-Aaa", query = "a") int a,    // Extract and convert the first 'a' from the headers
      @MetaParam(header = "X-Bbb", query = "b") long[] b, // Extract and convert the 'b' values from the headers
      @MetaParam(header = "X-Ccc", query = "c", defaultValue = "123") int c
  ) { ... }

  @UriMapping(uri = "/stuff", method = UriMethod.POST)
  public void stuff(
    @QueryParam("a") int a,    // Extract and convert the first 'a' from query params
    @HeaderValue("X-Bbb") long[] b, // Extract and convert the 'X-Bbb' values from the headers
    MyObject data // Extract and convert the request body
  ) { ... }
}
```
