package org.cbioportal.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class FractionGenomeAltered implements Serializable {

    private String profileId;
    private String sampleId;
    private BigDecimal value;

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getSampleId() {
        return sampleId;
    }

    public void setSampleId(String sampleId) {
        this.sampleId = sampleId;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
