package com.ureca.juksoon.domain.reservation.pessimistic;

import com.ureca.juksoon.domain.feed.entity.Feed;
import com.ureca.juksoon.domain.feed.entity.Status;
import com.ureca.juksoon.domain.feed.repository.FeedRepository;
import com.ureca.juksoon.domain.reservation.entity.Reservation;
import com.ureca.juksoon.domain.reservation.entity.ReservationAttemptState;
import com.ureca.juksoon.domain.reservation.repository.ReservationRepository;
import com.ureca.juksoon.domain.user.entity.User;
import com.ureca.juksoon.domain.user.repository.UserRepository;
import com.ureca.juksoon.global.exception.GlobalException;
import com.ureca.juksoon.global.response.CommonResponse;
import com.ureca.juksoon.global.response.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PessimisticReservationService {

    private final FeedRepository feedRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationLogService reservationLogService;

    @Transactional
    public CommonResponse<?> doReservation(Long feedId, Long userId) {

        User findUser = findUser(userId);
        LocalDateTime requestTime = LocalDateTime.now();

        //비관락 시작
        Feed findFeed = findFeedForUpdate(feedId);
        LocalDateTime lockingTime = LocalDateTime.now();

        log.info("userId = {} requestTime = {}", userId, requestTime);
        log.info("userId = {} lockingTime = {}", userId, lockingTime);

        Reservation reservation;

        // 피드 상태 OPEN 체크
        // (findFeed.getStatus() != Status.OPEN || findFeed.getExpiredAt().isBefore(requestTime))
        // expiredAt 이 LocalDateTime 으로 변경되면 사용 가능
        if (findFeed.getStatus() != Status.OPEN) {
            reservation = Reservation.of(findFeed, findUser, ReservationAttemptState.FAIL_CLOSED, requestTime);
            reservationLogService.saveFailReservation(reservation);
            throw new GlobalException(ResultCode.RESERVATION_NOT_OPENED);
        }

        // 예약 가능 인원 체크
        if (findFeed.getMaxUser() <= findFeed.getRegisteredUser()) {
            reservation = Reservation.of(findFeed, findUser, ReservationAttemptState.FAIL_FULL, requestTime);
            reservationLogService.saveFailReservation(reservation);
            throw new GlobalException(ResultCode.RESERVATION_IS_FULL);
        }

        // 중복 예약 예외 처리
        boolean isExist = reservationRepository.existsReservationByUserAndState(findUser, ReservationAttemptState.SUCCESS);

        if (isExist) {
            reservation = Reservation.of(findFeed, findUser, ReservationAttemptState.FAIL_DUPLE, requestTime);
            reservationLogService.saveFailReservation(reservation);
            throw new GlobalException(ResultCode.RESERVATION_DUPLE);
        }

        findFeed.increaseRegisterUser();
        reservation = Reservation.of(findFeed, findUser, ReservationAttemptState.SUCCESS, requestTime);
        reservationRepository.save(reservation);

        return CommonResponse.success("reservation success");
    }


    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(ResultCode.USER_NOT_FOUNT));
    }

    private Feed findFeedForUpdate(Long feedId) {
        return feedRepository.findByIdForUpdate(feedId)
                .orElseThrow(() -> new GlobalException(ResultCode.FEED_NOT_FOUND));
    }
}
