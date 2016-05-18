package VRAPI;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.schema.AlternateTypeRules.newRule;

/**
 * Created by gebo on 25/04/2016.
 */
@Configuration
@EnableSwagger2
public class API {

    @Bean
    public Docket api(){

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                  .apis(RequestHandlerSelectors.any())
                  .paths(Predicates.not(PathSelectors.regex("/error.*")))
                  .build()
                .apiInfo(apiInfo())
                .pathMapping("/");

//                .directModelSubstitute(LocalDate.class, String.class)
//                .genericModelSubstitutes(ResponseEntity.class)
//                .alternateTypeRules(
//                        newRule(typeResolver.resolve(DeferredResult.class,
//                                typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
//                                typeResolver.resolve(WildcardType.class)))
//                .useDefaultResponseMessages(false)
//                .globalResponseMessage(RequestMethod.GET,
//                        newArrayList(new ResponseMessageBuilder()
//                                .code(500)
//                                .message("500 message")
//                                .responseModel(new ModelRef("Error"))
//                                .build()))
//                .securitySchemes(newArrayList(apiKey()))
//                .securityContexts(newArrayList(securityContext()))
//                .enableUrlTemplating(true)
//                .globalOperationsParameters()
                //.tags(new Tag("ZUK Service", "All API Operations related to ZUK"));
    }

//    @Autowired
//    private TypeResolver typeResolver;


    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Vertec REST API",
                "This API is used for getting data relevant to ZUK",
                "0.0", "Free to use for Zuhlke emplyees", "Sam and Gergely", "", ""
        );
    }

//    private ApiKey apiKey(){
//        return new ApiKey("mykey", "api_key", "header");
//    }
//
//    private SecurityContext securityContext(){
//        return SecurityContext.builder()
//                .securityReferences(defaultAuth())
//                .forPaths(PathSelectors.regex(".anyPath.*"))
//                .build();
//    }
//
//    List<SecurityReference> defaultAuth(){
//        AuthorizationScope authorizationScope
//                = new AuthorizationScope("global", "accessEverything");
//        AuthorizationScope[] authorisationScopes = new AuthorizationScope[1];
//        authorisationScopes[0] = authorizationScope;
//        return newArrayList(
//                new SecurityReference("mykey", authorisationScopes));
//    }
//
//    @Bean
//    SecurityConfiguration security(){
//        return new SecurityConfiguration(
//                "test-app-client-id",
//                "test-app-realm",
//                "test-app",
//                "apikey" );
//
//    }

    @Bean
    UiConfiguration uiConfig(){
        return new UiConfiguration(
                "validationUrl"
        );

    }
}
