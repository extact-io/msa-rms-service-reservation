package io.extact.msa.rms.reservation.it.persistence;

import static org.assertj.core.api.Assertions.*;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.validation.ConstraintViolationException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.msa.rms.platform.fw.persistence.GenericRepository;
import io.extact.msa.rms.reservation.domain.Reservation;
import io.extact.msa.rms.reservation.external.RentalItemCheckApi;
import io.extact.msa.rms.reservation.external.UserAccountCheckApi;
import io.extact.msa.rms.reservation.it.persistence.AbstractReservationRepositoryValidationTest.RentalItemCheckApiStub;
import io.extact.msa.rms.reservation.it.persistence.AbstractReservationRepositoryValidationTest.UserAccountCheckApiStub;
import io.extact.msa.rms.reservation.persistence.ReservationRepository;
import io.extact.msa.rms.test.junit5.JulToSLF4DelegateExtension;
import io.helidon.microprofile.tests.junit5.AddBean;

@AddBean(RentalItemCheckApiStub.class)
@AddBean(UserAccountCheckApiStub.class)
@ExtendWith(JulToSLF4DelegateExtension.class)
abstract class AbstractReservationRepositoryValidationTest {

    @Test
    void testAddValidate() {
        testAddEntity();
    }

    @Test
    void testUpdateValidate() {
        testUpdateEntity();
    }

    void testAddEntity() {
        var repo = CDI.current().select(ReservationRepository.class).get();
        testOfAddEntity(repo, new Reservation(), 5);
    }

    void testUpdateEntity() {
        var repo = CDI.current().select(ReservationRepository.class).get();
        testOfUpdateEntity(repo, new Reservation(), 5);
    }

    <T> void testOfAddEntity(GenericRepository<T> repository, T entity, int expectedErrorSize) {
        var thrown = catchThrowable(() -> repository.add(entity));
        assertThat(thrown).isInstanceOf(ConstraintViolationException.class);
        assertThat(((ConstraintViolationException) thrown).getConstraintViolations()).hasSize(expectedErrorSize);
    }

    <T> void testOfUpdateEntity(GenericRepository<T> repository, T entity, int expectedErrorSize) {
        var thrown = catchThrowable(() -> repository.update(entity));
        assertThat(thrown).isInstanceOf(ConstraintViolationException.class);
        assertThat(((ConstraintViolationException) thrown).getConstraintViolations()).hasSize(expectedErrorSize);
    }

    static class RentalItemCheckApiStub implements RentalItemCheckApi {
        @Override
        public boolean exists(int itemId) {
            return false;
        }
    }
    static class UserAccountCheckApiStub implements UserAccountCheckApi {
        @Override
        public boolean exists(int userId) {
            return false;
        }
    }
}
