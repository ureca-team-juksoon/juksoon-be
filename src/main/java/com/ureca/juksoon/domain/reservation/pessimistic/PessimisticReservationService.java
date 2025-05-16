package com.ureca.juksoon.domain.reservation.pessimistic;

import com.ureca.juksoon.domain.feed.entity.Feed;
import com.ureca.juksoon.domain.feed.entity.Status;
import com.ureca.juksoon.domain.feed.repository.FeedRepository;
import com.ureca.juksoon.domain.reservation.dto.ReservationRes;
import com.ureca.juksoon.domain.reservation.entity.Reservation;
import com.ureca.juksoon.domain.reservation.repository.ReservationRepository;
import com.ureca.juksoon.domain.user.entity.User;
import com.ureca.juksoon.domain.user.repository.UserRepository;
import com.ureca.juksoon.global.exception.GlobalException;
import com.ureca.juksoon.global.response.ResultCode;
import com.ureca.juksoon.global.util.DateTimeParserUtil;
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

    @Transactional
    public ReservationRes doReservation(Long feedId, Long userId) {

        User findUserReference = findUserReference(userId);
        LocalDateTime requestTime = LocalDateTime.now();

        //비관락 시작
        Feed findFeed = findFeedForUpdate(feedId);

        // 피드 상태 체크 & 만료 시간 체크
        checkReservationState(findFeed, requestTime);

        // 예약 가능 인원 체크
        checkReservationUser(findFeed);

        // 중복 예약 예외 처리
        checkReservationDuple(findFeed, findUserReference);

        findFeed.increaseRegisterUser();
        Reservation reservation = Reservation.of(findFeed, findUserReference, requestTime);
        reservationRepository.save(reservation);

        return ReservationRes.toEntity(reservation);
    }

    private void checkReservationState(Feed findFeed, LocalDateTime requestTime) {
        String feedExpiredAt = findFeed.getExpiredAt();

        if (findFeed.getStatus() != Status.OPEN || requestTime.isAfter(DateTimeParserUtil.toLocalDateTime(feedExpiredAt))) {
            throw new GlobalException(ResultCode.RESERVATION_NOT_OPENED);
        }
    }

    private void checkReservationUser(Feed findFeed) {
        if (findFeed.getMaxUser() <= findFeed.getRegisteredUser()) {
            throw new GlobalException(ResultCode.RESERVATION_IS_FULL);
        }
    }

    private void checkReservationDuple(Feed findFeed, User findUserReference) {
        boolean isExist = reservationRepository.existsReservationByUserAndFeed(findUserReference, findFeed);

        if (isExist) {
            throw new GlobalException(ResultCode.RESERVATION_DUPLE);
        }
    }

    private User findUserReference(Long userId) {
        return userRepository.getReferenceById(userId);

    }

    private Feed findFeedForUpdate(Long feedId) {
        return feedRepository.findByIdForUpdate(feedId)
                .orElseThrow(() -> new GlobalException(ResultCode.FEED_NOT_FOUND));
    }
}
