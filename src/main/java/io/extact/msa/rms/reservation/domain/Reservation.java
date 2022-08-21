package io.extact.msa.rms.reservation.domain;

import static java.time.temporal.ChronoUnit.*;
import static javax.persistence.AccessType.*;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import io.extact.msa.rms.platform.fw.domain.IdProperty;
import io.extact.msa.rms.platform.fw.domain.Transformable;
import io.extact.msa.rms.platform.fw.domain.constraint.BeforeAfterDateTime;
import io.extact.msa.rms.platform.fw.domain.constraint.BeforeAfterDateTime.BeforeAfterDateTimeValidatable;
import io.extact.msa.rms.platform.fw.domain.constraint.Note;
import io.extact.msa.rms.platform.fw.domain.constraint.ReserveEndDateTime;
import io.extact.msa.rms.platform.fw.domain.constraint.ReserveStartDateTime;
import io.extact.msa.rms.platform.fw.domain.constraint.ReserveStartDateTimeFuture;
import io.extact.msa.rms.platform.fw.domain.constraint.RmsId;
import io.extact.msa.rms.platform.fw.domain.constraint.ValidationGroups.Add;
import io.extact.msa.rms.platform.fw.domain.constraint.ValidationGroups.Update;
import io.extact.msa.rms.platform.fw.domain.vo.DateTimePeriod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Access(FIELD)
@Entity
@BeforeAfterDateTime(from = "利用開始日時", to = "利用終了日時")
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Getter @Setter
public class Reservation implements BeforeAfterDateTimeValidatable, Transformable, IdProperty {

    /** id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RmsId(groups = Update.class)
    private Integer id;

    /** 利用開始日時 */
    @ReserveStartDateTime
    @ReserveStartDateTimeFuture(groups = Add.class)
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime startDateTime;

    /** 利用終了日時 */
    @ReserveEndDateTime
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime endDateTime;

    /** メモ */
    @Note
    private String note;

    /** 予約したレンタル品ID */
    @RmsId
    private int rentalItemId;

    /** 予約したユーザのユーザアカウントID */
    @RmsId
    private int userAccountId;

    // ----------------------------------------------------- factory methods

    public static Reservation ofTransient(LocalDateTime startDateTime, LocalDateTime endDateTime, String note, int rentalItemId, int userAccountId) {
        return of(null, startDateTime, endDateTime, note, rentalItemId, userAccountId);
    }

    // ----------------------------------------------------- original setter methods

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = Objects.requireNonNull(startDateTime).truncatedTo(MINUTES);
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = Objects.requireNonNull(endDateTime).truncatedTo(MINUTES);
    }


    // ----------------------------------------------------- service methods

    public DateTimePeriod getReservePeriod() {
        return new DateTimePeriod(startDateTime, endDateTime);
    }


    // ----------------------------------------------------- override methods

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
