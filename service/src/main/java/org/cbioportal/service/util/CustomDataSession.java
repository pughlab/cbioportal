<<<<<<<< HEAD:service/src/main/java/org/cbioportal/service/util/CustomDataSession.java
package org.cbioportal.service.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cbioportal.session_service.domain.Session;
import org.cbioportal.session_service.domain.SessionType;
========
package org.cbioportal.webparam;
>>>>>>>> f4546d7e88 (Move StudyViewFilter to webparams modules):webparam/src/main/java/org/cbioportal/webparam/CustomDataSession.java

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cbioportal.session_service.domain.Session;
import org.cbioportal.session_service.domain.SessionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomDataSession extends Session {

    private static final Logger LOG = LoggerFactory.getLogger(CustomDataSession.class);
    private CustomAttributeWithData data;

    @Override
    public void setData(Object data) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.data = mapper.readValue(mapper.writeValueAsString(data), CustomAttributeWithData.class);
        } catch (IOException e) {
            LOG.error("Error occurred", e);
        }
    }

    @Override
    public CustomAttributeWithData getData() {
        return data;
    }

    @JsonIgnore
    @Override
    public String getSource() {
        return super.getSource();
    }

    @JsonIgnore
    @Override
    public SessionType getType() {
        return super.getType();
    }

}
