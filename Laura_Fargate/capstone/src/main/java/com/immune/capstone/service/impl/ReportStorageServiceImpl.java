package com.immune.capstone.service.impl;

import com.immune.capstone.config.properties.S3Properties;
import com.immune.capstone.model.Utility;
import com.immune.capstone.service.ReportStorageService;
import com.immune.capstone.utils.ByteDocumentGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;

import static java.util.Objects.isNull;

@Log4j2
@Service
@RequiredArgsConstructor
public class ReportStorageServiceImpl implements ReportStorageService {

    private final ByteDocumentGenerator generator;
    private final S3Properties properties;
    private final S3Client s3Client;

    @Override
    public void save(Utility utility) {
        if (isNull(utility)) {
            return;
        }

        String utilityId = utility.getId();
        var reportOpt = generator.generateDocument(utility.toMap(), utilityId);

        if (reportOpt.isEmpty()) {
            log.warn("Report couldn't be generated");
            return;
        }

        byte[] reportContent = reportOpt.get();
        var inputStream = new ByteArrayInputStream(reportContent);

        var request = PutObjectRequest.builder()
                .bucket(properties.getBucketName())
                .key(utilityId + ".pdf")
                .build();

        s3Client.putObject(request, RequestBody.fromInputStream(inputStream, reportContent.length));
    }
}
