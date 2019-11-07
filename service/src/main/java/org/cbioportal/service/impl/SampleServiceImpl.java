package org.cbioportal.service.impl;

import org.cbioportal.model.CopyNumberSeg;
import org.cbioportal.model.Sample;
import org.cbioportal.model.meta.BaseMeta;
import org.cbioportal.persistence.CopyNumberSegmentRepository;
import org.cbioportal.persistence.SampleListRepository;
import org.cbioportal.persistence.SampleRepository;
import org.cbioportal.service.PatientService;
import org.cbioportal.service.SampleService;
import org.cbioportal.service.StudyService;
import org.cbioportal.service.exception.PatientNotFoundException;
import org.cbioportal.service.exception.SampleNotFoundException;
import org.cbioportal.service.exception.StudyNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SampleServiceImpl implements SampleService {

    private static final String SEQUENCED = "_sequenced";
    private static final String FUSION = "_fusion";

    @Autowired
    private SampleRepository sampleRepository;
    @Autowired
    private StudyService studyService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private SampleListRepository sampleListRepository;
    @Autowired
    private CopyNumberSegmentRepository copyNumberSegmentRepository;
    
    @Override
    public List<Sample> getAllSamples(String keyword, List<String> studyIds, String projection,
                                      Integer pageSize, Integer pageNumber, String sort, String direction) {
        List<Sample> samples = sampleRepository.getAllSamples(keyword, studyIds, projection, pageSize, pageNumber, sort, direction);
        processSamples(samples, projection);
        return samples;
    }

    @Override
    public BaseMeta getMetaSamples(String keyword, List<String> studyIds) {
        return sampleRepository.getMetaSamples(keyword, studyIds);
    }
    
    @Override
    public List<Sample> getAllSamplesInStudy(String studyId, String projection, Integer pageSize, Integer pageNumber,
                                             String sortBy, String direction) throws StudyNotFoundException {

        studyService.getStudy(studyId);
        List<Sample> samples = sampleRepository.getAllSamplesInStudy(studyId, projection, pageSize, pageNumber, sortBy,
            direction);

        processSamples(samples, projection);
        return samples;
    }

    @Override
    public BaseMeta getMetaSamplesInStudy(String studyId) throws StudyNotFoundException {

        studyService.getStudy(studyId);

        return sampleRepository.getMetaSamplesInStudy(studyId);
    }

    @Override
	public List<Sample> getAllSamplesInStudies(List<String> studyIds, String projection, Integer pageSize,
			Integer pageNumber, String sortBy, String direction) {

		return sampleRepository.getAllSamplesInStudies(studyIds, projection, pageSize, pageNumber, sortBy, direction);
	}

    @Override
    public Sample getSampleInStudy(String studyId, String sampleId) throws SampleNotFoundException,
        StudyNotFoundException {

        studyService.getStudy(studyId);
        Sample sample = sampleRepository.getSampleInStudy(studyId, sampleId);

        if (sample == null) {
            throw new SampleNotFoundException(studyId, sampleId);
        }

        processSamples(Arrays.asList(sample), "DETAILED");
        return sample;
    }

    @Override
    public List<Sample> getAllSamplesOfPatientInStudy(String studyId, String patientId, String projection,
                                                      Integer pageSize, Integer pageNumber, String sortBy,
                                                      String direction) throws StudyNotFoundException,
        PatientNotFoundException {

        patientService.getPatientInStudy(studyId, patientId);
        List<Sample> samples = sampleRepository.getAllSamplesOfPatientInStudy(studyId, patientId, projection, pageSize,
            pageNumber, sortBy, direction);

        processSamples(samples, projection);
        return samples;
    }

    @Override
    public BaseMeta getMetaSamplesOfPatientInStudy(String studyId, String patientId) throws StudyNotFoundException,
        PatientNotFoundException {

        patientService.getPatientInStudy(studyId, patientId);

        return sampleRepository.getMetaSamplesOfPatientInStudy(studyId, patientId);
    }

    @Override
    public List<Sample> getAllSamplesOfPatientsInStudy(String studyId, List<String> patientIds, String projection) {

        List<Sample> samples = sampleRepository.getAllSamplesOfPatientsInStudy(studyId, patientIds, projection);

        processSamples(samples, projection);
        return samples;
    }

    @Override
	public List<Sample> getSamplesOfPatientsInMultipleStudies(List<String> studyIds, List<String> patientIds,
			String projection) {
        
        List<Sample> samples = sampleRepository.getSamplesOfPatientsInMultipleStudies(studyIds, patientIds, projection);

        processSamples(samples, projection);
        return samples;
	}

    @Override
    public List<Sample> fetchSamples(List<String> studyIds, List<String> sampleIds, String projection) {

        List<Sample> samples = sampleRepository.fetchSamples(studyIds, sampleIds, projection);
        processSamples(samples, projection);
        return samples;
    }

    @Override
	public List<Sample> fetchSamples(List<String> sampleListIds, String projection) {
        
        List<Sample> samples = sampleRepository.fetchSamples(sampleListIds, projection);
        
        processSamples(samples, projection);
        return samples;
	}

    @Override
    public BaseMeta fetchMetaSamples(List<String> studyIds, List<String> sampleIds) {

        return sampleRepository.fetchMetaSamples(studyIds, sampleIds);
    }

    @Override
	public BaseMeta fetchMetaSamples(List<String> sampleListIds) {
        
        return sampleRepository.fetchMetaSamples(sampleListIds);
	}

    @Override
    public List<Sample> getSamplesByInternalIds(List<Integer> internalIds) {

        return sampleRepository.getSamplesByInternalIds(internalIds);
    }

    private void processSamples(List<Sample> samples, String projection) {

        if (projection.equals("DETAILED")) {
            Map<String, Set<String>> sequencedSampleIdsMap = new HashMap<>();
            List<String> distinctStudyIds = samples.stream().map(Sample::getCancerStudyIdentifier).distinct()
                .collect(Collectors.toList());
            for (String studyId : distinctStudyIds) {
                sequencedSampleIdsMap.put(studyId,
                                          new HashSet<String>(sampleListRepository.getAllSampleIdsInSampleList(studyId + SEQUENCED)));
            }

            Map<String, Set<String>> fusionSampleIdsMap = new HashMap<>();
            distinctStudyIds.forEach( studyId -> {
                fusionSampleIdsMap.put(studyId,
                    new HashSet<String>(sampleListRepository.getAllSampleIdsInSampleList(studyId + FUSION)));
            });

            List<Integer> samplesWithCopyNumberSeg = copyNumberSegmentRepository.fetchSamplesWithCopyNumberSegments(
                samples.stream().map(Sample::getCancerStudyIdentifier).collect(Collectors.toList()), 
                samples.stream().map(Sample::getStableId).collect(Collectors.toList()),
                null
            );
            
            Set<Integer> samplesWithCopyNumberSegMap = new HashSet<>();
            samplesWithCopyNumberSegMap.addAll(samplesWithCopyNumberSeg);
           
            samples.forEach(sample -> {
                sample.setSequenced(sequencedSampleIdsMap.get(sample.getCancerStudyIdentifier())
                    .contains(sample.getStableId()));
                sample.setCopyNumberSegmentPresent(samplesWithCopyNumberSegMap.contains(sample.getInternalId()));
                if (!fusionSampleIdsMap.isEmpty()) {
                    sample.setFusionPresent(fusionSampleIdsMap.get(sample.getCancerStudyIdentifier()).contains(sample.getStableId()));
                }
            });
        }
    }
}
