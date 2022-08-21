package io.extact.msa.rms.reservation.service;

import static io.extact.msa.rms.reservation.ReservationComponentFactoryTestUtils.*;
import static io.extact.msa.rms.test.assertj.ToStringAssert.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.msa.rms.platform.fw.exception.BusinessFlowException;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException.CauseType;
import io.extact.msa.rms.platform.fw.persistence.file.io.PathResolver;
import io.extact.msa.rms.platform.test.PathResolverParameterExtension;
import io.extact.msa.rms.reservation.domain.Reservation;

@ExtendWith(PathResolverParameterExtension.class)
class ReservationServiceTest {

    private ReservationService service;

    @BeforeEach
    void setup(PathResolver pathResolver) throws Exception {
        service = newReservationService(pathResolver);
    }

    @Test
    void testFindByRentalItemAndStartDate() {
        var expect = List.of(
                Reservation.of(1, LocalDateTime.of(2020, 4, 1, 10, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0), "メモ1", 3, 1),
                Reservation.of(2, LocalDateTime.of(2020, 4, 1, 16, 0, 0), LocalDateTime.of(2020, 4, 1, 18, 0, 0), "メモ2", 3, 2)
            );
        var actual = service.findByRentalItemAndStartDate(3, LocalDate.of(2020, 4, 1));
        assertThatToString(actual).containsExactlyElementsOf(expect);
    }

    @Test
    void testFindByRentalItemAndStartDateOnEmpty() {
        var actual = service.findByRentalItemAndStartDate(903, LocalDate.of(2020, 4, 1)); // rentalItemId:903が存在しない
        assertThat(actual).isEmpty();

        actual = service.findByRentalItemAndStartDate(1, LocalDate.of(2020, 7, 10)); // 2020/7/10の予約はない
        assertThat(actual).isEmpty();

        actual = service.findByRentalItemAndStartDate(903, LocalDate.of(2020, 7, 10)); // 上記の両方
        assertThat(actual).isEmpty();
    }

    @Test
    void testFindOverlappedReservation() {
        var expect = Reservation.of(1, LocalDateTime.of(2020, 4, 1, 10, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0), "メモ1", 3, 1);
        var actual = service.findOverlappedReservation(3, LocalDateTime.of(2020, 4, 1, 9, 0, 0), LocalDateTime.of(2020, 4, 1, 13, 0, 0)).get();
        assertThatToString(actual).isEqualTo(expect); // 2020/4/1 10:00-12:00の予約と重複
    }

    @Test
    void testFindOverlappedReservationOnEmpty() {
        var actual = service.findOverlappedReservation(1, LocalDateTime.of(2020, 4, 1, 13, 0, 0), LocalDateTime.of(2020, 4, 1, 15, 0, 0));
        assertThat(actual).isEmpty(); // 期間重複の予約なし

        actual = service.findOverlappedReservation(903, LocalDateTime.of(2020, 4, 1, 11, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0));
        assertThat(actual).isEmpty(); // 期間重複の予約なし(該当レンタル品なし)
    }

    @Test
    void testFindOverlappedReservations() {
        var expect = List.of(
                Reservation.of(1, LocalDateTime.of(2020, 4, 1, 10, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0), "メモ1", 3, 1));
        var actual = service.findOverlappedReservations(LocalDateTime.of(2020, 4, 1, 9, 0, 0), LocalDateTime.of(2020, 4, 1, 13, 0, 0));
        assertThatToString(actual).containsExactlyElementsOf(expect); // 2020/4/1 10:00-12:00の予約と重複
    }

    @Test
    void testFindOverlappedReservationsOnEmpty() {
        var actual = service.findOverlappedReservations(LocalDateTime.of(2020, 4, 1, 13, 0, 0), LocalDateTime.of(2020, 4, 1, 15, 0, 0));
        assertThat(actual).isEmpty();
    }

    @Test
    void testHasRentalItemWith() {
        // 該当あり
        var actual = service.hasRentalItemWith(3);
        assertThat(actual).isTrue();
        // 該当なし
        actual = service.hasRentalItemWith(999);
        assertThat(actual).isFalse();
    }

    @Test
    void testHasUserAccountwith() {
        // 該当あり
        var actual = service.hasUserAccountWith(1);
        assertThat(actual).isTrue();
        // 該当なし
        actual = service.hasUserAccountWith(999);
        assertThat(actual).isFalse();
    }

    @Test
    void testFindByReserverId() {
        // 1件ヒットパターン
        var actual = service.findByReserverId(2);
        assertThat(actual).hasSize(1);
        // 2件ヒットパターン
        actual = service.findByReserverId(1);
        assertThat(actual).hasSize(2);
        // 0件ヒットパターン
        actual = service.findByReserverId(3);
        assertThat(actual).isEmpty();
    }

    @Test
    void testFindByRentalItemId() {
        // 3件ヒットパターン
        var actual = service.findByRentalItemId(3);
        assertThat(actual).hasSize(3);
        // 0件ヒットパターン
        actual = service.findByRentalItemId(1);
        assertThat(actual).isEmpty();
    }

    @Test
    void testGet() {
        var expect = Reservation.of(1, LocalDateTime.of(2020, 4, 1, 10, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0), "メモ1", 3, 1);
        var actual = service.get(1).get();
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    void testGetNull() {
        var actual = service.get(100);
        assertThat(actual).isEmpty(); // 該当IDの予約なし
    }

    @Test
    void testAdd() {
        var addReservation = Reservation.ofTransient(LocalDateTime.of(2020, 4, 2, 10, 0, 0), LocalDateTime.of(2020, 4, 2, 12, 0, 0), "メモ4", 3, 1);
        var expect = Reservation.of(4, LocalDateTime.of(2020, 4, 2, 10, 0, 0), LocalDateTime.of(2020, 4, 2, 12, 0, 0), "メモ4", 3, 1);
        var actual = service.add(addReservation);
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    void testAddOnDuplicate() {
        // 2020/4/1 16:00-18:00 で既に予約あり
        var addReservation = Reservation.of(null, LocalDateTime.of(2020, 4, 1, 17, 0, 0), LocalDateTime.of(2020, 4, 1, 19, 0, 0), "メモ4", 3, 1);
        var thrown = catchThrowable(() -> service.add(addReservation));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.DUPRICATE);
    }

    @Test
    void testAddOnItemNotExists() {
        var addReservation = Reservation.ofTransient(LocalDateTime.of(2020, 4, 2, 10, 0, 0), LocalDateTime.of(2020, 4, 2, 12, 0, 0), "メモ4", 999, 1);
        var thrown = catchThrowable(() -> service.add(addReservation));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.NOT_FOUND);
    }

    @Test
    void testAddOnUserNotExists() {
        var addReservation = Reservation.ofTransient(LocalDateTime.of(2020, 4, 2, 10, 0, 0), LocalDateTime.of(2020, 4, 2, 12, 0, 0), "メモ4", 3, 999);
        var thrown = catchThrowable(() -> service.add(addReservation));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.NOT_FOUND);
    }

    @Test
    void testUpdate() {
        var update = service.get(1).get();
        update.setNote("UPDATE");
        var result = service.update(update);
        assertThat(result.getNote()).isEqualTo("UPDATE");
        assertThatToString(update).isEqualTo(service.get(1).get());
    }

    @Test
    void testUpdateOnNotFound() {
        var update = Reservation.of(999, LocalDateTime.now(), LocalDateTime.now().plusHours(1), "memo", 1, 1);
        var thrown = catchThrowable(() -> service.update(update));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.NOT_FOUND);
    }

    @Test
    void testUpdateOnItemNotExists() {
        var update = service.get(1).get();
        update.setRentalItemId(999);
        var thrown = catchThrowable(() -> service.update(update));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.NOT_FOUND);
    }

    @Test
    void testUpdateOnUserNotExists() {
        var update = service.get(1).get();
        update.setUserAccountId(999);
        var thrown = catchThrowable(() -> service.update(update));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.NOT_FOUND);
    }

    @Test
    void testDelete() {
        service.delete(1);
        assertThat(service.get(1)).isEmpty();
    }

    @Test
    void testDeleteOnNotFound() {
        var thrown = catchThrowable(() -> service.delete(999));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.NOT_FOUND);
    }

    @Test
    void testCancel() {
        service.cancel(2, 2);
        var actual = service.get(2);
        assertThat(actual).isEmpty();
    }

    @Test
    void testCancelOnNotFound() {
        var thrown = catchThrowable(() -> service.cancel(4, 2));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.NOT_FOUND);
    }

    @Test
    void testCancelOnOperationForbidden() {
        var thrown = catchThrowable(() -> service.cancel(2, 1));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.FORBIDDEN);
    }
}
