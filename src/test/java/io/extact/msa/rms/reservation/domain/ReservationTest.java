package io.extact.msa.rms.reservation.domain;

import static io.extact.msa.rms.reservation.domain.ReservationTest.SetPattern.*;
import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.msa.rms.platform.fw.domain.constraint.Note;
import io.extact.msa.rms.platform.fw.domain.constraint.ReserveEndDateTime;
import io.extact.msa.rms.platform.fw.domain.constraint.ReserveStartDateTime;
import io.extact.msa.rms.platform.fw.domain.constraint.ReserveStartDateTimeFuture;
import io.extact.msa.rms.platform.fw.domain.constraint.RmsId;
import io.extact.msa.rms.platform.fw.domain.constraint.ValidationGroups.Add;
import io.extact.msa.rms.platform.fw.domain.constraint.ValidationGroups.Update;
import io.extact.msa.rms.platform.fw.domain.vo.DateTimePeriod;
import io.extact.msa.rms.test.assertj.ConstraintViolationSetAssert;
import io.extact.msa.rms.test.junit5.ValidatorParameterExtension;
import io.extact.msa.rms.test.utils.PropertyTest;

@ExtendWith(ValidatorParameterExtension.class)
class ReservationTest extends PropertyTest {

    @Override
    protected Class<?> getTargetClass() {
        return Reservation.class;
    }

    @Test
    void testSetId() throws Exception {
        Reservation testee = new Reservation();
        testee.setId(100);
        Field id = this.getField("id");

        assertThat(id).isNotNull();
        assertThat(id.get(testee)).isEqualTo(100);
    }

    @Test
    void testGetId() throws Exception {
        Reservation testee = new Reservation();
        Field id = this.getField("id");

        assertThat(id).isNotNull();

        id.set(testee, 100);
        assertThat(testee.getId()).isEqualTo(100);
    }

    @Test
    void testSetStartDateTime() throws Exception {
        Reservation testee = new Reservation();
        LocalDateTime now = LocalDateTime.now().truncatedTo(MINUTES);
        testee.setStartDateTime(now);
        Field startDate = this.getField("startDateTime");

        assertThat(startDate).isNotNull();
        assertThat((LocalDateTime) startDate.get(testee)).isEqualTo(now);
    }

    @Test
    void testGetStartDateTime() throws Exception {
        Reservation testee = new Reservation();
        LocalDateTime now = LocalDateTime.now();
        Field startDateTime = this.getField("startDateTime");

        assertThat(startDateTime).isNotNull();

        startDateTime.set(testee, now);
        assertThat(testee.getStartDateTime()).isEqualTo(now);
    }

    @Test
    void testSetEndDateTime() throws Exception {
        Reservation testee = new Reservation();
        LocalDateTime now = LocalDateTime.now().truncatedTo(MINUTES);
        testee.setEndDateTime(now);
        Field endDate = this.getField("endDateTime");

        assertThat(endDate).isNotNull();
        assertThat((LocalDateTime) endDate.get(testee)).isEqualTo(now);
    }

    @Test
    void testGetEndDateTime() throws Exception {
        Reservation testee = new Reservation();
        LocalDateTime now = LocalDateTime.now();
        Field endDateTime = this.getField("endDateTime");

        assertThat(endDateTime).isNotNull();

        endDateTime.set(testee, now);
        assertThat(testee.getEndDateTime()).isEqualTo(now);
    }

    @Test
    void testSetNote() throws Exception {
        Reservation testee = new Reservation();
        testee.setNote("だって使うんだもん");
        Field note = this.getField("note");

        assertThat(note).isNotNull();
        assertThat(note.get(testee)).isEqualTo("だって使うんだもん");
    }

    @Test
    void testGetNote() throws Exception {
        Reservation testee = new Reservation();
        Field note = this.getField("note");

        assertThat(note).isNotNull();

        note.set(testee, "だって使うんだもん");
        assertThat(testee.getNote()).isEqualTo("だって使うんだもん");
    }

    @Test
    void testSetRentalItemId() throws Exception {
        Reservation testee = new Reservation();
        testee.setRentalItemId(101);
        Field rentalItemId = this.getField("rentalItemId");

        assertThat(rentalItemId).isNotNull();
        assertThat(rentalItemId.get(testee)).isEqualTo(101);
    }

    @Test
    void testGetRentalItemId() throws Exception {
        Reservation testee = new Reservation();
        Field rentalItemId = this.getField("rentalItemId");

        assertThat(rentalItemId).isNotNull();

        rentalItemId.set(testee, 101);
        assertThat(testee.getRentalItemId()).isEqualTo(101);
    }

    @Test
    void testSetUserAccountId() throws Exception {
        Reservation testee = new Reservation();
        testee.setUserAccountId(102);
        Field userAccountId = this.getField("userAccountId");

        assertThat(userAccountId).isNotNull();
        assertThat(userAccountId.get(testee)).isEqualTo(102);
    }

    @Test
    void testGetUserAccountId() throws Exception {
        Reservation testee = new Reservation();
        Field userAccountId = this.getField("userAccountId");

        assertThat(userAccountId).isNotNull();

        userAccountId.set(testee, 102);
        assertThat(testee.getUserAccountId()).isEqualTo(102);
    }

    @Test
    void testDateTimePeriod() {
        Reservation reserved = new Reservation();
        reserved.setStartDateTime(LocalDateTime.of(2020, 1, 1, 0, 1));
        reserved.setEndDateTime(LocalDateTime.of(2020, 12, 31, 23, 59));

        DateTimePeriod reservedPeriod = reserved.getReservePeriod();
        assertThat(reservedPeriod.getStartDateTime()).isEqualTo(LocalDateTime.of(2020, 1, 1, 0, 1));
        assertThat(reservedPeriod.getEndDateTime()).isEqualTo(LocalDateTime.of(2020, 12, 31, 23, 59));
    }

    @Test
    void testNewInstance() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now();

        Reservation testee = Reservation.of(1, startDate, endDate, "note", 2, 3);

        assertThat(testee.getId()).isEqualTo(1);
        assertThat(testee.getStartDateTime()).isEqualTo(startDate);
        assertThat(testee.getEndDateTime()).isEqualTo(endDate);
        assertThat(testee.getNote()).isEqualTo("note");
        assertThat(testee.getRentalItemId()).isEqualTo(2);
        assertThat(testee.getUserAccountId()).isEqualTo(3);
    }


    // ----------------------------------------------------- constraints tests

    @Test
    void testConstraintAnnoteToClass() {
        Class<?>[] expected = {
                RmsId.class,
                ReserveStartDateTime.class,
                ReserveStartDateTimeFuture.class,
                ReserveEndDateTime.class,
                Note.class,
        };
        var actual = getFieldAnnotations();
        assertThat(actual).contains(expected);
    }

    @Test
    void testBefoeAterValidate(Validator validator) {
        Reservation r = createAllOKReservation(ALL);
        r.setStartDateTime(LocalDateTime.now());
        r.setEndDateTime(r.getStartDateTime().minusYears(1));

        Set<ConstraintViolation<Reservation>> result = validator.validate(r);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasMessageEndingWith("BeforeAfterDateTime.message");
    }

    @Test
    void testPropetyValidationForAdd(Validator validator) {

        // エラーがないこと
        Reservation r = createAllOKReservation(ALL);
        r.setStartDateTime(LocalDateTime.now().plusHours(1));
        Set<ConstraintViolation<Reservation>> result = validator.validate(r);
        ConstraintViolationSetAssert.assertThat(result)
            .hasNoViolations();

        // 開始日時に過去を設定してテスト
        r.setStartDateTime(LocalDateTime.now().minusHours(1));
        // Group指定がなし→バリデーションSkip→バリデーションエラーなし
        result = validator.validate(r);
        ConstraintViolationSetAssert.assertThat(result)
        .hasNoViolations();
        // Groupを指定→バリデーションエラーあり
        result = validator.validate(r, Add.class);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("startDateTime")
            .hasMessageEndingWith("Future.message");
    }

    @Test
    void testPropetyValidationForUpdate(Validator validator) {

        // エラーがないこと
        Reservation r = createAllOKReservation(ALL);
        Set<ConstraintViolation<Reservation>> result = validator.validate(r, Update.class);
        ConstraintViolationSetAssert.assertThat(result)
            .hasNoViolations();

        // IDに-1を設定してテスト
        r.setId(-1);
        // Group指定がなし→バリデーションSkip→バリデーションエラーなし
        result = validator.validate(r);
        ConstraintViolationSetAssert.assertThat(result)
        .hasNoViolations();
        // Groupを指定→バリデーションエラーあり
        result = validator.validate(r, Update.class);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("id")
            .hasMessageEndingWith("Min.message");
    }

    private Reservation createAllOKReservation(SetPattern setPattern) {
        Reservation r = new Reservation();
        r.setId(1);
        if (setPattern != NONE_START_DATETIME) {
            r.setStartDateTime(LocalDateTime.now().plusMinutes(1));
        }
        if (setPattern != NONE_END_DATETIME) {
            r.setEndDateTime(LocalDateTime.now().plusDays(1));
        }
        r.setNote("note");
        r.setRentalItemId(1);
        r.setUserAccountId(1);
        return r;
    }

    enum SetPattern {
        ALL,
        NONE_START_DATETIME,
        NONE_END_DATETIME
    }
}
