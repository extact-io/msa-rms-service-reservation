package io.extact.msa.rms.reservation.webapi.dto;

import java.time.LocalDateTime;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import io.extact.msa.rms.platform.fw.domain.constraint.BeforeAfterDateTime;
import io.extact.msa.rms.platform.fw.domain.constraint.BeforeAfterDateTime.BeforeAfterDateTimeValidatable;
import io.extact.msa.rms.platform.fw.domain.constraint.Note;
import io.extact.msa.rms.platform.fw.domain.constraint.ReserveEndDateTime;
import io.extact.msa.rms.platform.fw.domain.constraint.ReserveStartDateTimeFuture;
import io.extact.msa.rms.platform.fw.domain.constraint.RmsId;
import io.extact.msa.rms.platform.fw.domain.constraint.ValidationGroups.Add;
import io.extact.msa.rms.reservation.domain.Reservation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "予約登録イベントDTO")
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Getter @Setter
@BeforeAfterDateTime
public class AddReservationEventDto implements BeforeAfterDateTimeValidatable {

    @Schema(required = true)
    @ReserveStartDateTimeFuture(groups = Add.class)
    private LocalDateTime startDateTime;

    @Schema(required = true)
    @ReserveEndDateTime
    private LocalDateTime endDateTime;

    @Schema(required = false)
    @Note
    private String note;

    @Schema(required = true)
    @RmsId
    private int rentalItemId;

    @Schema(required = true)
    @RmsId
    private int userAccountId;

    public Reservation toEntity() {
        return Reservation.ofTransient(startDateTime, endDateTime, note, rentalItemId, userAccountId);
    }
}
