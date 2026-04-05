package de.betoffice.web;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang3.StringUtils;

import tools.jackson.databind.ObjectMapper;

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
            prod = objectMapper.readValue(text, PageParam.class);
            setValue(prod);
        }
    }

}