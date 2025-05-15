package com.ureca.juksoon.global.event.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * {feedId}:meta {  hash
 *     feedId : {feedId}                         //피드 아이디
 *     currentTicketCount : {ticketNum}          //현재 티켓 번호
 *     maxTicketCount : {maxTicketNum}           //최대 티켓 번호
 *     endTime : {endTime}                       //끝나는 시간
 * }
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreationFeedEvent {
    private Long feedId;
    private Integer currentTicketCount;
    private Integer maxTicketCount;
    private String endTime;
}
