package io.extact.msa.rms.reservation.it.webapi;

import static io.extact.msa.rms.platform.test.PlatformTestUtils.*;
import static io.extact.msa.rms.reservation.external.ApiType.*;
import static io.extact.msa.rms.test.assertj.ToStringAssert.*;
import static org.assertj.core.api.Assertions.*;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.ws.rs.core.Response.Status;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.msa.rms.platform.core.jaxrs.converter.RmsTypeParameterFeature;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException.CauseType;
import io.extact.msa.rms.reservation.it.external.stub.CheckApiRemoteStubApplication;
import io.extact.msa.rms.reservation.it.external.stub.RentalItemCheckApiRemoteStub;
import io.extact.msa.rms.reservation.it.external.stub.UserAccountCheckApiRemoteStub;
import io.extact.msa.rms.reservation.webapi.ReservationResource;
import io.extact.msa.rms.reservation.webapi.dto.AddReservationEventDto;
import io.extact.msa.rms.reservation.webapi.dto.ReservationResourceDto;
import io.extact.msa.rms.test.junit5.JulToSLF4DelegateExtension;
import io.extact.msa.rms.test.utils.ClearOpenTelemetryContextCdiExtension;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.AddExtension;
import io.helidon.microprofile.tests.junit5.HelidonTest;

/**
 * ネットワーク越しにReservationResourceをテストするテストケース。
 * <pre>
 * ・テストドライバ：RestClient(ReservationResource)
 *     ↓ HTTP
 * ・実物：RestResource(ReservationResource)
 * ・実物：RestClient(RentalItemCheckApiRestClient/ect.)
 *     ↓ HTTP
 * ・スタブ：RestResource(RentalItemCheckApiRemoteStub/..)
 * </pre>
 */
@HelidonTest
@AddExtension(ClearOpenTelemetryContextCdiExtension.class)
@AddBean(RentalItemCheckApiRemoteStub.class)
@AddBean(UserAccountCheckApiRemoteStub.class)
@AddBean(CheckApiRemoteStubApplication.class)
@AddConfig(key = PROP_NAME, value = REAL)
@AddConfig(key = "server.port", value = "7001") // for Real(ReservationResource) and RemoteStub Server port
@ExtendWith(JulToSLF4DelegateExtension.class)
@TestMethodOrder(OrderAnnotation.class)
class ReservationResourceTest {

    private static final int NO_SIDE_EFFECT = 1; // 副作用なし
    private ReservationResource reservationResource;

    @BeforeEach
    void setup() throws Exception {
        this.reservationResource = RestClientBuilder.newBuilder()
                .baseUri(new URI("http://localhost:7001/reservations"))
                .register(RmsTypeParameterFeature.class)
                .build(ReservationResource.class);
    }

    @Test
    @Order(NO_SIDE_EFFECT)
    void testGetAll() {
        var actual = reservationResource.getAll();
        assertThat(actual).hasSize(3);
    }

    @Test
    @Order(9)
    void testAdd() {
        var addDto = AddReservationEventDto.of(LocalDateTime.of(2050, 4, 1, 10, 0, 0), LocalDateTime.of(2050, 4, 1, 12, 0, 0), "メモ4", 3, 1);
        var expect = ReservationResourceDto.of(4, LocalDateTime.of(2050, 4, 1, 10, 0, 0), LocalDateTime.of(2050, 4, 1, 12, 0, 0), "メモ4", 3, 1);
        var actual = reservationResource.add(addDto);
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    @Order(9)
    void testAddOnDuplicate() {
        var addDto = AddReservationEventDto.of(LocalDateTime.of(2099, 4, 1, 10, 0, 0), LocalDateTime.of(2099, 4, 1, 13, 0, 0), "メモ4", 3, 1); // 期間重複あり
        var thrown = catchThrowable(() -> reservationResource.add(addDto));
        assertGenericErrorInfo(thrown, Status.CONFLICT, BusinessFlowException.class, CauseType.DUPRICATE);
    }

    @Test
    @Order(NO_SIDE_EFFECT)
    void testAddOnParameterError() {
        var thrown = catchThrowable(() -> reservationResource.add(new AddReservationEventDto())); // paramter error
        assertValidationErrorInfo(thrown, 4);
    }

    @Test
    @Order(NO_SIDE_EFFECT)
    void testAddOnBeforeAfterDateTimeError() {
        var addDto = AddReservationEventDto.of(LocalDateTime.of(2099, 4, 1, 12, 0, 0), LocalDateTime.of(2099, 4, 1, 10, 0, 0), "メモ4", 3, 1);
        var thrown = catchThrowable(() -> reservationResource.add(addDto)); // startDateTime > endDateTime エラー
        assertValidationErrorInfo(thrown, 1);
    }

    @Test
    void testUpdate() {
        var update = ReservationResourceDto.of(1, LocalDateTime.of(2099, 1, 1, 0, 0), LocalDateTime.of(2099, 12, 31, 23, 59), "update", 1, 2);
        var actual = reservationResource.update(update);
        assertThatToString(actual).isEqualTo(update);
    }

    @Test
    @Order(NO_SIDE_EFFECT)
    void testUpdateOnNotFound() {
        var update = ReservationResourceDto.of(999, LocalDateTime.of(2099, 1, 1, 0, 0), LocalDateTime.of(2099, 12, 31, 23, 59), "update", 1, 1);
        var thrown = catchThrowable(() -> reservationResource.update(update));
        assertGenericErrorInfo(thrown, Status.NOT_FOUND, BusinessFlowException.class, CauseType.NOT_FOUND);
    }

    @Test
    @Order(NO_SIDE_EFFECT)
    void testUpdateOnRentalItemParameterError() {
        var update = ReservationResourceDto.of(-1, null, null, "update", -1, -1);
        var thrown = catchThrowable(() -> reservationResource.update(update));
        assertValidationErrorInfo(thrown, 5);
    }

    @Test
    void testDelete() {
        var beforeSize = reservationResource.getAll().size();
        reservationResource.delete(3);
        var afterSize = reservationResource.getAll().size();
        assertThat(afterSize).isEqualTo(beforeSize - 1); // 1件削除されていること
    }

    @Test
    @Order(NO_SIDE_EFFECT)
    void testDeleteOnParameterError() {
        var thrown = catchThrowable(() -> reservationResource.delete(-1)); // parameter error
        assertValidationErrorInfo(thrown, 1);
    }

    @Test
    @Order(NO_SIDE_EFFECT)
    void testDeleteOnNotFound() {
        var thrown = catchThrowable(() -> reservationResource.delete(999)); // 該当なし
        assertGenericErrorInfo(thrown, Status.NOT_FOUND, BusinessFlowException.class, CauseType.NOT_FOUND);
    }

    @Test
    void testCancel() {
        var beforeSize = reservationResource.getAll().size();
        reservationResource.cancel(2, 2);
        var afterSize = reservationResource.getAll().size();
        assertThat(afterSize).isEqualTo(beforeSize - 1); // 1件削除されていること
    }

    @Test
    @Order(NO_SIDE_EFFECT)
    void testCancelOnNotFound() {
        var thrown = catchThrowable(() -> reservationResource.cancel(9, 1)); // キャンセル対象なし
        assertGenericErrorInfo(thrown, Status.NOT_FOUND, BusinessFlowException.class, CauseType.NOT_FOUND);
    }

    @Test
    @Order(NO_SIDE_EFFECT)
    void testCancelOnParameterError() {
        var thrown = catchThrowable(() -> reservationResource.cancel(-1, -1)); // parameter error
        assertValidationErrorInfo(thrown, 2);
    }

    // --------------------------------------------------------- for find method tests.

    @Test
    @Order(NO_SIDE_EFFECT)
    void testFindByRentalItemAndStartDate() {
        var actual = reservationResource.findByRentalItemAndStartDate(3, LocalDate.of(2020, 4, 1));
        assertThat(actual).hasSize(2);
    }

    @Test
    @Order(NO_SIDE_EFFECT)
    void testFindByRentalItemAndStartDateOnNotFound() {
        var actual = reservationResource.findByRentalItemAndStartDate(3, LocalDate.of(2019, 4, 1)); // 該当なし
        assertThat(actual).isEmpty();
    }

    @Test
    @Order(NO_SIDE_EFFECT)
    void testFindByRentalItemAndStartDateOnParameterError() {
        var thrown = catchThrowable(() -> reservationResource.findByRentalItemAndStartDate(-1, LocalDate.of(2020, 4, 1))); // parameter error
        assertValidationErrorInfo(thrown, 1);
    }

    @Test
    @Order(NO_SIDE_EFFECT)
    void testFindByReserverId() {
        var actual = reservationResource.findByReserverId(1);
        assertThat(actual).hasSize(2);
    }

    @Test
    @Order(NO_SIDE_EFFECT)
    void testFindByReserverIdOnNotFound() {
        var actual = reservationResource.findByReserverId(9);
        assertThat(actual).isEmpty();
    }

    @Test
    @Order(NO_SIDE_EFFECT)
    void testFindByReserverIdOnParameterError() {
        var thrown = catchThrowable(() -> reservationResource.findByReserverId(-1)); // parameter error
        assertValidationErrorInfo(thrown, 1);
    }

    @Test
    @Order(NO_SIDE_EFFECT)
    void testFindByRentalItemId() {
        var actual = reservationResource.findByRentalItemId(3);
        assertThat(actual).hasSize(3);
    }

    @Test
    @Order(NO_SIDE_EFFECT)
    void testFindByRentalItemIdOnNotFound() {
        var actual = reservationResource.findByRentalItemId(9);
        assertThat(actual).isEmpty();
    }

    @Test
    @Order(NO_SIDE_EFFECT)
    void testFindByRentalItemIdOnParameterError() {
        var thrown = catchThrowable(() -> reservationResource.findByRentalItemId(-1)); // parameter error
        assertValidationErrorInfo(thrown, 1);
    }

    @Test
    @Order(NO_SIDE_EFFECT)
    void testFindOverlappedReservation() {
        var expect = ReservationResourceDto.of(1, LocalDateTime.of(2020, 4, 1, 10, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0), "メモ1", 3, 1);
        var actual = reservationResource.findOverlappedReservation(3, LocalDateTime.of(2020, 4, 1, 9, 0, 0), LocalDateTime.of(2020, 4, 1, 13, 0, 0));
        assertThatToString(actual).isEqualTo(expect); // 2020/4/1 10:00-12:00の予約と重複
    }

    @Test
    @Order(NO_SIDE_EFFECT)
    void testFindOverlappedReservationOnNotFound() {
        var actual = reservationResource.findOverlappedReservation(1, LocalDateTime.of(2020, 4, 1, 13, 0, 0), LocalDateTime.of(2020, 4, 1, 15, 0, 0));
        assertThat(actual).isNull(); // 期間重複の予約なし
    }

    @Test
    @Order(NO_SIDE_EFFECT)
    void testFindOverlappedReservationOnParameterError() {
        var thrown = catchThrowable(() -> reservationResource.findOverlappedReservation(-1, null, null)); // parameter error
        assertValidationErrorInfo(thrown, 3);
    }

    @Test
    @Order(NO_SIDE_EFFECT)
    void testFindOverlappedReservations() {
        var actual = reservationResource.findOverlappedReservations(LocalDateTime.of(2020, 4, 1, 9, 0, 0), LocalDateTime.of(2020, 4, 1, 13, 0, 0));
        assertThat(actual).hasSize(1); // 2020/4/1 10:00-12:00の予約と重複
    }

    @Test
    @Order(NO_SIDE_EFFECT)
    void testFindOverlappedReservationsOnNotFound() {
        var actual = reservationResource.findOverlappedReservations(LocalDateTime.of(2020, 4, 1, 13, 0, 0), LocalDateTime.of(2020, 4, 1, 15, 0, 0));
        assertThat(actual).isEmpty(); // 期間重複の予約なし
    }

    @Test
    @Order(NO_SIDE_EFFECT)
    void testFindOverlappedReservationsOnParameterError() {
        var thrown = catchThrowable(() -> reservationResource.findOverlappedReservations(null, null)); // parameter error
        assertValidationErrorInfo(thrown, 2);
    }

    @Test
    @Order(NO_SIDE_EFFECT)
    void testHasRentalItemWith() {
        var actual = reservationResource.hasRentalItemWith(3); // 該当あり
        assertThat(actual).isTrue();
        actual = reservationResource.hasRentalItemWith(999); // 該当なし
        assertThat(actual).isFalse();
    }

    @Test
    @Order(NO_SIDE_EFFECT)
    void testHasRentalItemWithOnParameterError() {
        var thrown = catchThrowable(() -> reservationResource.hasRentalItemWith(-1)); // parameter error
        assertValidationErrorInfo(thrown, 1);
    }

    @Test
    @Order(NO_SIDE_EFFECT)
    void testHasUserAccountWith() {
        var actual = reservationResource.hasUserAccountWith(1); // 該当あり
        assertThat(actual).isTrue();
        actual = reservationResource.hasUserAccountWith(999); // 該当なし
        assertThat(actual).isFalse();
    }

    @Test
    @Order(NO_SIDE_EFFECT)
    void testHasUserAccountWithOnParameterError() {
        var thrown = catchThrowable(() -> reservationResource.hasUserAccountWith(-1)); // parameter error
        assertValidationErrorInfo(thrown, 1);
    }
}
