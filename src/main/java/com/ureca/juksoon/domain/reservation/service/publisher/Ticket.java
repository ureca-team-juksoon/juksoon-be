package com.ureca.juksoon.domain.reservation.service.publisher;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    private String err;     //에러 발생시 여기가 채워짐
    private Long feedId;    //에러 발생 X 시 여기가 채워짐
    private Long userId;
    private Integer currentTicketCount;

    public Map<String, String> convertStreamMessageBody(){
        return Map.of(
                "feedId", feedId.toString(),
                "userId", userId.toString(),
                "currentTicketCount", currentTicketCount.toString()
        );
    }
}

