package com.ureca.juksoon.domain.reservation.service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.juksoon.domain.reservation.dto.ReservationReq;
import com.ureca.juksoon.global.redis.lua.LuaScriptExecutor;
import com.ureca.juksoon.global.exception.GlobalException;
import com.ureca.juksoon.global.response.ResultCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 레디스에 접근하여, 현재 티켓을 가져온다.
 * Lua script를 이용한, 동시성 잡기
 * 생산자 쪽에서 동시성을 잡아주면, 컨슈머쪽에서는 그냥 처리만 하면 된다.
 */

@Component
@RequiredArgsConstructor
public class TicketPublisher {
    private static final String TICKET_PUBLISHER_LUA_SCRIPT = "redis-script/ticket-publish-script.lua";

    private final LuaScriptExecutor luaScriptExecutor;
    private final ObjectMapper objectMapper;

    public Ticket publish(Long userId, Long feedId) {
        String rawJsonTicket = luaScriptExecutor.issueTicket(   //티켓 발급. => redis 플로우 실행
                feedId.toString(),
                userId.toString());

        return parseJsonToTicket(rawJsonTicket);    //레디스가 건내주는 json 객체를 Ticket으로 파싱 해 반환 만약, err이 반환되었다면, err만 채워져 있음
    }

    private Ticket parseJsonToTicket(String rawJsonTicket) {
        try {
            return objectMapper.readValue(rawJsonTicket, Ticket.class);
        } catch (JsonProcessingException e) {
            throw new GlobalException(ResultCode.TICKET_PARSING_FAILED);
        }
    }

    private List<String> getKeys(Long feedId){
        return List.of(feedId.toString());
    }

    private String getArgs(Long userId){
        return userId.toString();
    }
}
