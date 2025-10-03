package com.planyourtrip.bot.service;

import com.planyourtrip.bot.dto.ResponseDto;

public interface SendService {

    void sendResponse(ResponseDto responseDto);

    void deleteMessage(long chatId, int messageId);

}
