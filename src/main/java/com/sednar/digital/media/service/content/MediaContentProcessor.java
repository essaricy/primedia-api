package com.sednar.digital.media.service.content;

import java.io.File;
import java.io.IOException;

public interface MediaContentProcessor<T> {

    <T> T process(Long mediaId, String trackingId, File uploadedFile) throws IOException;

}
