package com.planyourtrip.bot.service.command.summary;

import com.planyourtrip.bot.dto.ResponseFileDto;

public interface SummaryService {
    ResponseFileDto getTripSummaryPdf(long id);

    String getTripSummaryText(long id);
}
