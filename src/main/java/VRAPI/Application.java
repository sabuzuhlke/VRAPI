package VRAPI;


import VRAPI.ResourceController.OrganisationController;
import VRAPI.ResourceController.ResourceController;
import com.google.common.base.Predicates;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@ComponentScan(basePackageClasses = {ResourceController.class, OrganisationController.class})
@EnableSwagger2
@Configuration
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public Docket api(){

		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(Predicates.not(PathSelectors.regex("/error.*")))
				.build()
				.apiInfo(apiInfo())
				.pathMapping("/");
	}


	private ApiInfo apiInfo() {
		return new ApiInfo(
				"Vertec REST API",
				"This API is used for getting data relevant to ZUK",
				"0.0", "Free to use for Zuhlke emplyees", "Sam and Gergely", "", ""
		);
	}

	@Bean
	UiConfiguration uiConfig(){
		return new UiConfiguration(
				"validationUrl"
		);

	}

}
