-- 레디스 hash는 metaKey : valueKey : value 이런식으로 구성
--[[
티켓 퍼블리셔 구조

{feedId}:meta {  hash
    feedId : {feedId}                         //피드 아이디
    currentTicketCount : {ticketNum}          //현재 티켓 번호
    maxTicketCount : {maxTicketNum}           //최대 티켓 번호
    endTime : {endTime}                       //끝나는 시간
}

]]

local feedId = KEYS[1]                      -- 해당 피드 ID
local userId = ARGV[1]                      -- 티켓 요청한 유저 ID

local metaFeedId = feedId .. ":meta"    --티켓 퍼블리셔 ID : redis 전용 피드 ID "feed:111:meta" 이런식(레디스가 찾으려면 이게 필요)
local bufferFeedId = feedId .. ":buffer"

-- 1) 발급소 존재 확인
if redis.call("EXISTS", metaFeedId) == 0 then  -- 존재 명령어로 redis 전용 피드 ID가 있는지 없는지 판단
  return cjson.encode({err="발급소 없음/마감"})                  -- 에러던짐 없을 시
end

-- 2) 티켓 발급소 있을 시 정보 가져오기
local maxTicketCount = tonumber(redis.call("HGET", metaFeedId, "maxTicketCount"))
local currentTicketCount = tonumber(redis.call("HGET", metaFeedId, "currentTicketCount") or "0")
local endTime = tonumber(redis.call("HGET", metaFeedId, "endTime"))
local nowTs = tonumber(redis.call("TIME")[1])

-- 3) 중복 신청 방지
if redis.call("SISMEMBER", bufferFeedId, userId)==1 then
  return cjson.encode({
      err = "이미 신청하셨습니다" })
end

-- 4) maxTicket 초과 검사
if currentTicketCount >= maxTicketCount then     -- 만약 현재 티켓 수가 더 많다면
  -- 4-1) 티켓 발급소 패쇄한데이
  redis.call("HSET", metaFeedId, "closed", "1")    -- 해당 해시 키-벨류 없앰
  return cjson.encode({err="티켓 발급 실패 : 인원 마감"})            -- 에러던짐 티켓 발급 실패
end

-- 5) 티켓 발급 + 티켓 값 증가
currentTicketCount = currentTicketCount + 1
redis.call("HSET", metaFeedId, "currentTicketCount", currentTicketCount) -- 현재 티켓 수를 가져옴

-- 6) 현재 신청한 피드-사용자 저장 후에 스트림으로 티켓을 받을 시 파쇄
redis.call("SADD", bufferFeedId, userId)

-- 7) 성공 시 티켓 반환 { feedId, userId, currentTicketCount, maxTicketCount} JSON 반환(티켓 반환)
local result = cjson.encode({
    feedId = feedId,
    userId = userId,
    currentTicketCount = currentTicketCount,
})
return result