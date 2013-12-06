/** Copyright (c) 2013 Memorial Sloan-Kettering Cancer Center.
**
** This library is free software; you can redistribute it and/or modify it
** under the terms of the GNU Lesser General Public License as published
** by the Free Software Foundation; either version 2.1 of the License, or
** any later version.
**
** This library is distributed in the hope that it will be useful, but
** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
** documentation provided hereunder is on an "as is" basis, and
** Memorial Sloan-Kettering Cancer Center 
** has no obligations to provide maintenance, support,
** updates, enhancements or modifications.  In no event shall
** Memorial Sloan-Kettering Cancer Center
** be liable to any party for direct, indirect, special,
** incidental or consequential damages, including lost profits, arising
** out of the use of this software and its documentation, even if
** Memorial Sloan-Kettering Cancer Center 
** has been advised of the possibility of such damage.  See
** the GNU Lesser General Public License for more details.
**
** You should have received a copy of the GNU Lesser General Public License
** along with this library; if not, write to the Free Software Foundation,
** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
**/
package org.mskcc.cbio.portal.model;

import java.util.Map;
import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 * Encapsulates Patient Data.
 *
 * @author Benjamin Gross
 */
public class Patient {

    private int internalId;
    private String stableId;

    private String sampleId;
	private Map<String, ClinicalData> clinicalDataMap;
    private static final Logger logger = Logger.getLogger(Patient.class);

    public Patient(String stableId)
    {
        this(stableId, stableId, new HashMap<String, ClinicalData>());
    }

    public Patient(int internalId, String stableId)
    {
        this(stableId, stableId, new HashMap<String, ClinicalData>());
        this.internalId = internalId;
    }

    public Patient(String stableId, String sampleId, Map<String, ClinicalData> clinicalDataMap)
    {
        this.stableId = stableId;
        this.sampleId = sampleId;
		this.clinicalDataMap = clinicalDataMap;
    }

    public int getInternalId()
    {
        return internalId;
    }

    public String getStableId()
    {
        return stableId;
    }

    public String getSampleId()
    {
        return sampleId;
    }

    public Double getOverallSurvivalMonths()
    { 
		return getDoubleValue(ClinicalAttribute.OS_MONTHS);
	}
    public String getOverallSurvivalStatus()
    {
		return getStringValue(ClinicalAttribute.OS_STATUS);
	}
    public Double getDiseaseFreeSurvivalMonths()
    {
		return getDoubleValue(ClinicalAttribute.DFS_MONTHS);
	}
    public String getDiseaseFreeSurvivalStatus()
    {
		return getStringValue(ClinicalAttribute.DFS_STATUS);
	}
    public Double getAgeAtDiagnosis()
    {
		return getDoubleValue(ClinicalAttribute.AGE_AT_DIAGNOSIS);
	}

	private Double getDoubleValue(String attribute)
    {
		ClinicalData data = clinicalDataMap.get(attribute);
        if (data == null || data.getAttrVal().length() == 0 ||
                data.getAttrVal().equals(ClinicalAttribute.NA) ||
                data.getAttrVal().equals(ClinicalAttribute.MISSING)) {
            return null;
        }
        try {
            return Double.valueOf(data.getAttrVal());
        } catch (NumberFormatException e) {
            logger.warn("Can't handle clinical attribute of case: " + sampleId);
            return null;
        }
	}

	private String getStringValue(String attribute)
    {
		ClinicalData data = clinicalDataMap.get(attribute);
		return (data == null || data.getAttrVal().length() == 0 ||
				data.getAttrVal().equals(ClinicalAttribute.NA) ||
				data.getAttrVal().equals(ClinicalAttribute.MISSING)) ? null : data.getAttrVal();
	}
}
