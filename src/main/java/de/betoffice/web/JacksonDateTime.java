package de.betoffice.web;

import java.text.SimpleDateFormat;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

@Configuration
public class JacksonDateTime { // implements ApplicationListener<ContextRefreshedEvent> {

//    @Bean
//    @Primary
//    public ObjectMapper scmsObjectMapper() {
//        ObjectMapper mapper = JsonMapper.builder() // or different mapper for other format
//                .addModule(new ParameterNamesModule())
//                .addModule(new Jdk8Module())
//                .addModule(new JavaTimeModule())
//                // and possibly other configuration, modules, then:
//                .build();
//
//        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
//        return mapper;
//
//        // com.fasterxml.jackson.databind.ObjectMapper responseMapper = new
//        // com.fasterxml.jackson.databind.ObjectMapper();
//        // return responseMapper;
//    }

    // @Override
    // public void onApplicationEvent(ContextRefreshedEvent event) {
    // ObjectMapper mapper = JsonMapper.builder() // or different mapper for other
    // format
    // .addModule(new ParameterNamesModule())
    // .addModule(new Jdk8Module())
    // .addModule(new JavaTimeModule())
    // // and possibly other configuration, modules, then:
    // .build();
    //
    // mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    // }

}
