package io.extact.msa.rms.reservation.it.service;

import static io.extact.msa.rms.reservation.external.ApiType.*;
import static io.extact.msa.rms.test.assertj.ToStringAssert.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import javax.inject.Inject;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.msa.rms.reservation.domain.Reservation;
import io.extact.msa.rms.reservation.it.external.stub.CheckApiRemoteStubApplication;
import io.extact.msa.rms.reservation.it.external.stub.RentalItemCheckApiRemoteStub;
import io.extact.msa.rms.reservation.it.external.stub.UserAccountCheckApiRemoteStub;
import io.extact.msa.rms.reservation.service.ReservationService;
import io.extact.msa.rms.test.junit5.JulToSLF4DelegateExtension;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.HelidonTest;

@HelidonTest
@AddBean(RentalItemCheckApiRemoteStub.class)
@AddBean(UserAccountCheckApiRemoteStub.class)
@AddBean(CheckApiRemoteStubApplication.class)
@AddConfig(key = PROP_NAME, value = REAL)
@AddConfig(key = "server.port", value = "7001") // for RemoteStub Server
@ExtendWith(JulToSLF4DelegateExtension.class)
@TestMethodOrder(OrderAnnotation.class)
abstract class AbstractReservationServiceCrudTest {

    @Inject
    private ReservationService reservationService;

    @Test
    @Order(1)
    void testGet() {
        var expected = Reservation.of(1, LocalDateTime.of(2020, 4, 1, 10, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0), "メモ1", 3, 1);
        var actual = reservationService.get(1).get();
        assertThatToString(actual).isEqualTo(expected);
    }

    @Test
    @Order(2)
    void testFindAll() {
        var actual = reservationService.findAll();
        assertThat(actual).hasSize(3);
    }

    @Test
    @Order(3)
    void testUpate() {
        var expected = Reservation.of(2, LocalDateTime.of(2099, 5, 1, 0, 0), LocalDateTime.of(2099, 5, 1, 23, 59), "update", 3, 1);

        var updateReservation = reservationService.get(2).get();
        updateReservation.setStartDateTime(LocalDateTime.of(2099, 5, 1, 0, 0));
        updateReservation.setEndDateTime(LocalDateTime.of(2099, 5, 1, 23, 59));
        updateReservation.setNote("update");
        updateReservation.setRentalItemId(3);
        updateReservation.setUserAccountId(1);
        var actual = reservationService.update(updateReservation);

        assertThatToString(actual).isEqualTo(expected);
    }

    @Test
    @Order(4)
    void testAdd() {
        var r = Reservation.ofTransient(LocalDateTime.of(2099, 4, 2, 10, 0, 0), LocalDateTime.of(2099, 4, 2, 12, 0, 0), "メモ4", 3, 1);
        var expected = Reservation.of(4, r.getStartDateTime(), r.getEndDateTime(), r.getNote(), r.getRentalItemId(), r.getUserAccountId());
        var actual = reservationService.add(r);
        assertThatToString(actual).isEqualTo(expected);
    }

    @Test
    @Order(5)
    void testDelete() {
        reservationService.delete(4);
        var actual = reservationService.findAll();
        assertThat(actual).hasSize(3);
    }
}
