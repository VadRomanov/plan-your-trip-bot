package com.planyourtrip.bot.service.command.summary.impl;

import com.planyourtrip.bot.dto.ResponseFileDto;
import com.planyourtrip.bot.exception.BusinessException;
import com.planyourtrip.bot.exception.ResponseCode;
import com.planyourtrip.bot.service.command.summary.SummaryService;
import com.planyourtrip.bot.service.core.TripCoreClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class SummaryServiceImpl implements SummaryService {
    private final TripCoreClient tripCoreClient;
    private final RetryTemplate retryTemplate;

    private static final String DEFAULT_SUMMARY_FILENAME = "trip_summary.pdf";

    @Override
    public ResponseFileDto getTripSummaryPdf(long id) {
        log.debug("Get trip summary pdf by {}", id);
        var response = retryTemplate.execute(retryCallback ->
                tripCoreClient.getTripSummary(id, MediaType.APPLICATION_PDF_VALUE));
        if (!Objects.equals(response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE),
                MediaType.APPLICATION_PDF_VALUE)) {
            throw BusinessException.builder(ResponseCode.INTERNAL_ERROR)
                    .message("unexpected type of response")
                    .build();
        }
        var contentDisposition = response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
        var fileName = nonNull(contentDisposition)
                ? contentDisposition.substring(contentDisposition.indexOf("filename=") + 9)
                : DEFAULT_SUMMARY_FILENAME;
        var responseFile = ResponseFileDto.builder()
                .fileName(fileName)
                .body((byte[]) response.getBody())
                .build();
        log.debug("Get trip summary pdf by {}", id);
        return responseFile;
    }

    @Override
    public String getTripSummaryText(long id) {
        log.debug("Get trip summary text by {}", id);
        var response = retryTemplate.execute(retryCallback ->
                tripCoreClient.getTripSummary(id, MediaType.TEXT_HTML_VALUE));
        if (!Objects.equals(response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE), MediaType.TEXT_HTML_VALUE)) {
            throw BusinessException.builder(ResponseCode.INTERNAL_ERROR)
                    .message("unexpected type of response")
                    .build();
        }
        log.debug("Get trip summary text by {}", id);
        return (String) response.getBody();
    }
}
