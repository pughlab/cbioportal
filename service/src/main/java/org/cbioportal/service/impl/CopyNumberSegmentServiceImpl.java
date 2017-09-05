package org.cbioportal.service.impl;

import org.cbioportal.model.CopyNumberSeg;
import org.cbioportal.model.GeneticProfile;
import org.cbioportal.model.meta.BaseMeta;
import org.cbioportal.persistence.CopyNumberSegmentRepository;
import org.cbioportal.service.CopyNumberSegmentService;
import org.cbioportal.service.SampleService;
import org.cbioportal.service.exception.SampleNotFoundException;
import org.cbioportal.service.exception.StudyNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CopyNumberSegmentServiceImpl implements CopyNumberSegmentService {

    @Autowired
    private CopyNumberSegmentRepository copyNumberSegmentRepository;
    @Autowired
    private SampleService sampleService;

    @Override
    @PreAuthorize("hasPermission(#studyId, 'CancerStudy', 'read')")
    public List<CopyNumberSeg> getCopyNumberSegmentsInSampleInStudy(String studyId, String geneticProfileId, String sampleId,
                                                                    String projection, Integer pageSize,
                                                                    Integer pageNumber, String sortBy,
                                                                    String direction) throws SampleNotFoundException, 
        StudyNotFoundException {
        
        sampleService.getSampleInStudy(studyId, sampleId);

        return copyNumberSegmentRepository.getCopyNumberSegmentsInSampleInStudy(geneticProfileId, sampleId, projection, pageSize,
            pageNumber, sortBy, direction);
    }

    @Override
    @PreAuthorize("hasPermission(#studyId, 'CancerStudy', 'read')")
    public BaseMeta getMetaCopyNumberSegmentsInSampleInStudy(String studyId, String geneticProfileId, String sampleId)
        throws SampleNotFoundException, StudyNotFoundException {
        sampleService.getSampleInStudy(studyId, sampleId);
        
        return copyNumberSegmentRepository.getMetaCopyNumberSegmentsInSampleInStudy(geneticProfileId, sampleId);
    }

    @Override
    @PreAuthorize("hasPermission(#geneticProfileIds, 'List<GeneticProfileId>', 'read')")
    public List<CopyNumberSeg> fetchCopyNumberSegments(List<String> geneticProfileIds, List<String> sampleIds, 
                                                       String projection) {
        
        List<CopyNumberSeg> copyNumberSeg = copyNumberSegmentRepository.fetchCopyNumberSegments(geneticProfileIds, sampleIds, projection);
        return copyNumberSeg;
    }

    @Override
    @PreAuthorize("hasPermission(#geneticProfileIds, 'List<GeneticProfileId>', 'read')")
    public BaseMeta fetchMetaCopyNumberSegments(List<String> geneticProfileIds, List<String> sampleIds) {
        
        return copyNumberSegmentRepository.fetchMetaCopyNumberSegments(geneticProfileIds, sampleIds);
    }

    @Override
    @PreAuthorize("hasPermission(#geneticProfileId, 'GeneticProfile', 'read')")
    public List<CopyNumberSeg> getCopyNumberSegmentsBySampleListId(String geneticProfileId, String sampleListId, 
                                                                   String projection) {
        
        return copyNumberSegmentRepository.getCopyNumberSegmentsBySampleListId(geneticProfileId, sampleListId, projection);
    }
}
