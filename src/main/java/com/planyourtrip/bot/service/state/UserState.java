package com.planyourtrip.bot.service.state;

import com.planyourtrip.bot.constant.CommandType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserState {
    private CommandType responsibleCommand;
    private String state;
    private Map<String, Object> stateEntities;
}
