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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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

        checkValidation(findFeed, findUser, requestTime);

        findFeed.increaseRegisterUser();
        Reservation reservation = Reservation.of(findFeed, findUser, ReservationAttemptState.SUCCESS, requestTime);
        reservationRepository.save(reservation);

        return CommonResponse.success("reservation success");
    }

    private void checkValidation(Feed findFeed, User findUser, LocalDateTime requestTime) {
        // 피드 상태 체크 & 만료 시간 체크
        checkReservationState(findFeed, findUser, requestTime);

        // 예약 가능 인원 체크
        checkReservationUser(findFeed, findUser, requestTime);

        // 중복 예약 예외 처리
        checkDupleReservation(findFeed, findUser, requestTime);
    }

    private void checkReservationState(Feed findFeed, User findUser, LocalDateTime requestTime) {
        String feedExpiredAt = findFeed.getExpiredAt();
        LocalDate localDate = LocalDate.parse(feedExpiredAt);
        LocalTime localTime = LocalTime.of(0, 0, 0, 0);
        LocalDateTime expiredAt = LocalDateTime.of(localDate, localTime);

        if (findFeed.getStatus() != Status.OPEN || expiredAt.isAfter(LocalDateTime.now())) {
            Reservation reservation = Reservation.of(findFeed, findUser, ReservationAttemptState.FAIL_CLOSED, requestTime);
            reservationLogService.saveFailReservation(reservation);
            throw new GlobalException(ResultCode.RESERVATION_NOT_OPENED);
        }
    }

    private void checkReservationUser(Feed findFeed, User findUser, LocalDateTime requestTime) {
        if (findFeed.getMaxUser() <= findFeed.getRegisteredUser()) {
            Reservation reservation = Reservation.of(findFeed, findUser, ReservationAttemptState.FAIL_FULL, requestTime);
            reservationLogService.saveFailReservation(reservation);
            throw new GlobalException(ResultCode.RESERVATION_IS_FULL);
        }
    }

    private void checkDupleReservation(Feed findFeed, User findUser, LocalDateTime requestTime) {
        boolean isExist = reservationRepository.existsReservationByUserAndStateAndFeed(findUser, ReservationAttemptState.SUCCESS, findFeed);

        if (isExist) {
            Reservation reservation = Reservation.of(findFeed, findUser, ReservationAttemptState.FAIL_DUPLE, requestTime);
            reservationLogService.saveFailReservation(reservation);
            throw new GlobalException(ResultCode.RESERVATION_DUPLE);
        }
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
