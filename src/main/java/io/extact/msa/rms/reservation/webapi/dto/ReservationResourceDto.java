package io.extact.msa.rms.reservation.webapi.dto;

import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import io.extact.msa.rms.platform.fw.domain.constraint.BeforeAfterDateTime;
import io.extact.msa.rms.platform.fw.domain.constraint.BeforeAfterDateTime.BeforeAfterDateTimeValidatable;
import io.extact.msa.rms.platform.fw.domain.constraint.Note;
import io.extact.msa.rms.platform.fw.domain.constraint.ReserveEndDateTime;
import io.extact.msa.rms.platform.fw.domain.constraint.ReserveStartDateTime;
import io.extact.msa.rms.platform.fw.domain.constraint.RmsId;
import io.extact.msa.rms.reservation.domain.Reservation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "予約DTO")
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Getter @Setter
@BeforeAfterDateTime(from = "利用開始日時", to = "利用終了日時")
public class ReservationResourceDto implements BeforeAfterDateTimeValidatable {

    @Schema(required = true)
    @RmsId
    private Integer id;

    @Schema(required = true)
    @ReserveStartDateTime
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

    public static ReservationResourceDto from(Reservation entity) {
        if (entity == null) {
            return null;
        }
        var dto = new ReservationResourceDto();
        dto.setId(entity.getId());
        dto.setStartDateTime(entity.getStartDateTime());
        dto.setEndDateTime(entity.getEndDateTime());
        dto.setNote(entity.getNote());
        dto.setRentalItemId(entity.getRentalItemId());
        dto.setUserAccountId(entity.getUserAccountId());
        return dto;
    }

    public Reservation toEntity() {
        return Reservation.of(id, startDateTime, endDateTime, note, rentalItemId, userAccountId);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
