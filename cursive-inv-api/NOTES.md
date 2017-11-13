Constructing an OpenAPI description as a string: something like:

```java
Set<String> resourcePackages = null;

// TODO: do we need this object at all?
OpenAPI openApi = new OpenAPI();
// TODO: what happens if we call these *after* OpenAPIContext.read()?
// TODO: how much of this can we parse from annotations?
openApi.info(new Info());
openApi.paths(new Paths());
// openApi.schema(???);

SwaggerConfiguration oasConfig = new SwaggerConfiguration()
.openAPI(openApi)
.resourcePackages(resourcePackages);
GenericOpenApiContextBuilder<?> ctxBuilder = new GenericOpenApiContextBuilder<>();
ctxBuilder.setOpenApiConfiguration(oasConfig);
OpenApiContext context = ctxBuilder.buildContext(false);
OpenAPI openApi2 = context.read();

Yaml.pretty(openApi2);
Json.pretty(openApi2);
```
