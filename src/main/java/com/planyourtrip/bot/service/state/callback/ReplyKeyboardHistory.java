package com.planyourtrip.bot.service.state.callback;

import com.planyourtrip.bot.utils.ReplyKeyboardBuilder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ReplyKeyboardHistory {
    private static final Map<Long, Deque<State>> KEYBOARDS = new ConcurrentHashMap<>();

    public void saveState(long chatId, State state) {
        var stack = KEYBOARDS.computeIfAbsent(chatId, k -> new ArrayDeque<>());
        stack.push(state);
    }

    public State restoreState(long chatId) {
        var stack = KEYBOARDS.get(chatId);
        return stack.isEmpty() ? null : stack.pop();
    }

    public boolean isEmpty(long chatId) {
        var stack = KEYBOARDS.get(chatId);
        if (stack == null) {
            return true;
        } else {
            return stack.isEmpty();
        }
    }

    @Data
    @Accessors(chain = true)
    public static class State {
        private String text;
        private List<ReplyKeyboardBuilder.KeyboardButton> keyboard;
    }
}
