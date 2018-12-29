/**
 * Intelenz de Venezuela @author intelenz services com.intelenz.serviceinstance.services.config SwaggerConfiguration.java
 * <p>
 * 2:32:38 p. m. 4 oct. 2017
 */
package com.empresa.consumo.masivo.gestion.config;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfiguration {

    
    /**
     * Publish a bean to generate swagger2 endpoints
     *
     * @return a swagger configuration bean
     * @throws XmlPullParserException
     * @throws IOException
     * @throws FileNotFoundException
     */

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                
                .build();
    }


    /**
     *
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     * @throws XmlPullParserException
     */
    /*private ApiInfo apiInfo() {

        return new ApiInfoBuilder()
                .title("Process Model")
                .description("Microservice for process modeling")
                .termsOfServiceUrl("https://source.intelenz.com/")
                .license("Intelenz " + model.getArtifactId() + " licensing")
                // .licenseUrl("https://help.github.com/articles/open-source-licensing/")
                .contact(new Contact("Back-end Team Intelenz", "www.example.com", "lplaz@intelenz.com"))
                .build();
    }*/

    /**
     * Documentation appearence
     * @return
     */
    /*@Bean
    UiConfiguration uiConfig() {
        return new UiConfiguration("validatorUrl", "list", "alpha", "schema",
                UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS, false, true, 60000L);
    }*/

    /**
     * Method to Only select apis that matches the given Predicates.
     * @return
     */
    private Predicate<String> paths() {
        // Match all paths except /error
        return Predicates.and(
                //PathSelectors.regex("/.*"),
                PathSelectors.ant("/api/users/*"),
                //PathSelectors.regex("/api/services/*"),
                Predicates.not(PathSelectors.regex("/error.*")));
    }
}
