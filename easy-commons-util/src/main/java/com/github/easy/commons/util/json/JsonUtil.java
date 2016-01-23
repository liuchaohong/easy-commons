package com.github.easy.commons.util.json;

import java.io.IOException;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;

public class JsonUtil {
	private static final Log LOGGER = LogFactory.getLog(JsonUtil.class);
	
    private static ObjectMapper mapper = new ObjectMapper(); // can reuse, share

    // globally
    static {
//    	mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public JsonUtil() {

    }

    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            String str = mapper.writeValueAsString(obj);
            return str;
        } catch (JsonGenerationException e) {
             LOGGER.info(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (JsonMappingException e) {
             LOGGER.info(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (IOException e) {
             LOGGER.info(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


    public static <T> T toObject(String content, Class<T> valueType) {
        if (StringUtils.isEmpty(content)) {
            return null;
        }
        try {
            return mapper.readValue(content, valueType);
        } catch (JsonParseException e) {
            LOGGER.info(e.getMessage(), e);
            throw new RuntimeException("cannot parse:"+content,e);
        } catch (JsonMappingException e) {
            LOGGER.info(e.getMessage(), e);
            throw new RuntimeException("cannot parse:"+content,e);
        } catch (IOException e) {
            LOGGER.info(e.getMessage(), e);
            throw new RuntimeException("cannot parse:"+content,e);
        }
    }

    @SuppressWarnings("unchecked")
    public static Collection toCollectionObject(String content, Class<? extends Collection> collectionType, Class beanType) {
        if (StringUtils.isEmpty(content) || collectionType == null || beanType == null) {
            return null;
        }

        try {
            return mapper.readValue(content, TypeFactory.defaultInstance().constructCollectionType(collectionType, beanType));
        } catch (JsonParseException e) {
            LOGGER.info(e.getMessage(), e);
            throw new RuntimeException("cannot parse:"+content,e);
        } catch (JsonMappingException e) {
            LOGGER.info(e.getMessage(), e);
            throw new RuntimeException("cannot parse:"+content,e);
        } catch (IOException e) {
            LOGGER.info(e.getMessage(), e);
            throw new RuntimeException("cannot parse:"+content,e);
        }

    }

    public static void main(String[] args) {
    }

}

