package com.immune.capstone.utils;

import java.util.Map;
import java.util.Optional;

public interface ByteDocumentGenerator {

    /**
     * File generator.
     * @param fileContent map for simple table content (header row + one content row)
     * @param title document title
     */
    Optional<byte[]> generateDocument(Map<String, String> fileContent, String title);

}
