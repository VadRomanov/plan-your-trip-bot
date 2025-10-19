package com.planyourtrip.bot.service.command.summary.impl;

import com.planyourtrip.bot.config.http.FeignErrorDecoder;
import com.planyourtrip.bot.constant.BotAnswer;
import com.planyourtrip.bot.dto.ResponseFileDto;
import com.planyourtrip.bot.dto.domain.AccommodationDto;
import com.planyourtrip.bot.dto.domain.NoteDto;
import com.planyourtrip.bot.dto.domain.TicketDto;
import com.planyourtrip.bot.dto.domain.TripSummaryDto;
import com.planyourtrip.bot.service.command.summary.SummaryService;
import com.planyourtrip.bot.service.core.TripCoreClient;
import com.planyourtrip.bot.utils.SerializationHelper;
import com.planyourtrip.bot.utils.TextUtils;
import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class SummaryServiceImpl implements SummaryService {
    private final TripCoreClient tripCoreClient;
    private final RetryTemplate retryTemplate;
    private final FeignErrorDecoder feignErrorDecoder;
    private final SerializationHelper serializationHelper;

    private static final String DEFAULT_SUMMARY_FILENAME = "trip_summary.pdf";

    @Override
    @SneakyThrows
    public ResponseFileDto getTripSummaryPdf(long id) {
        log.debug("Get trip summary pdf by {}", id);
        try (var response = retryTemplate.execute(retryCallback ->
                tripCoreClient.getTripSummary(id, MediaType.APPLICATION_PDF_VALUE))) {
            checkResponseStatus(response);
            var fileName = getFileNameFromResponse(response);
            ResponseFileDto responseFile = ResponseFileDto.builder()
                    .fileName(fileName)
                    .body(IOUtils.toByteArray(response.body().asInputStream()))
                    .build();
            log.debug("Get trip summary pdf by {}", id);
            return responseFile;
        }
    }

    @Override
    @SneakyThrows
    public String getTripSummaryText(long id) {
        log.debug("Get trip summary text by {}", id);
        try (var response = retryTemplate.execute(retryCallback ->
                tripCoreClient.getTripSummary(id, MediaType.APPLICATION_JSON_VALUE))) {
            checkResponseStatus(response);
            log.debug("Get trip summary text by {}", id);
            var tripSummary =
                    serializationHelper.canDeserializeAndGet(IOUtils.toByteArray(response.body().asInputStream()),
                            TripSummaryDto.class);
            return mapTripSummaryDtoToText(tripSummary);
        }
    }

    private String getFileNameFromResponse(Response response) {
        var contentDisposition = response.headers().get(HttpHeaders.CONTENT_DISPOSITION);
        if (contentDisposition != null && !contentDisposition.isEmpty()) {
            var headerValue = contentDisposition.iterator().next();
            var fileNameDecoded = headerValue.substring(headerValue.indexOf("filename=") + 9);
            return TextUtils.decodeBase64String(fileNameDecoded);
        } else {
            return DEFAULT_SUMMARY_FILENAME;
        }
    }

    @SneakyThrows
    private void checkResponseStatus(Response response) {
        var sendMessageHttpCode = response.status();
        if (HttpStatusCode.valueOf(sendMessageHttpCode).isError()
                && sendMessageHttpCode != HttpStatus.BAD_REQUEST.value()) {
            throw feignErrorDecoder.decode(response.request().url(), response);
        }
    }

    private String mapTripSummaryDtoToText(TripSummaryDto tripSummary) {
       //todo: перенести в BotAnswer
        var tickets = tripSummary.getTickets();
        var accommodations = tripSummary.getAccommodations();
        var notes = tripSummary.getNotes();
        String pattern = """
                <b>%s</b>%s%s%s
                """;
        return String.format(pattern,
                String.format("%s%s%s", tripSummary.getName(),
                        tripSummary.isExpired() ? BotAnswer.EXPIRED : Strings.EMPTY,
                        TextUtils.getRange(tripSummary.getStartDate(), tripSummary.getEndDate(), BotAnswer.FROM,
                                BotAnswer.TILL)),

                tickets.isEmpty() ? Strings.EMPTY : String.format("""
                        
                        <b>Билеты:</b>
                        %s
                        """, tickets.stream()
                        .map(TicketDto::toString)),
                accommodations.isEmpty() ? Strings.EMPTY : String.format("""
                        
                        <b>Размещения:</b>
                        %s
                        """, accommodations.stream()
                        .map(AccommodationDto::toString)),
                notes.isEmpty() ? Strings.EMPTY : String.format("""
                        
                        <b>Заметки:</b>
                        %s
                        """, notes.stream()
                        .map(NoteDto::toString))
        );
    }

}
