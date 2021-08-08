package com.sednar.digital.media.util;

import com.sednar.digital.media.resource.v1.model.BatchDto;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class BatchResultUtil {

    public static BatchDto getBatchDto(List<Long> successList, List<Long> failureList) {
        return BatchDto.builder()
                .success(successList.size())
                .failed(failureList.size())
                .total(successList.size() + failureList.size())
                .successList(successList)
                .failureList(failureList)
                .build();
    }

}
