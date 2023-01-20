package de.betoffice.web;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PageParamObjectMapper extends PropertyEditorSupport {

    private ObjectMapper objectMapper;

    public PageParamObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.isEmpty(text)) {
            setValue(null);
        } else {
            PageParam prod = new PageParam();
            try {
                prod = objectMapper.readValue(text, PageParam.class);
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException(e);
            }
            setValue(prod);
        }
    }

}