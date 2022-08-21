package io.extact.msa.rms.reservation.webapi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;

import io.extact.msa.rms.platform.core.validate.ValidateGroup;
import io.extact.msa.rms.platform.core.validate.ValidateParam;
import io.extact.msa.rms.platform.fw.domain.constraint.ValidationGroups.Add;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException;
import io.extact.msa.rms.reservation.service.ReservationService;
import io.extact.msa.rms.reservation.webapi.dto.AddReservationEventDto;
import io.extact.msa.rms.reservation.webapi.dto.ReservationResourceDto;

@Path("/reservations")
@ApplicationScoped
@ValidateParam
public class ReservationResourceImpl implements ReservationResource {

    private ReservationService reservationService;

    @Inject
    public ReservationResourceImpl(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Override
    public List<ReservationResourceDto> getAll() {
        return reservationService.findAll().stream()
                .map(ReservationResourceDto::from)
                .toList();
    }

    @Override
    public List<ReservationResourceDto> findByRentalItemAndStartDate(Integer itemId, LocalDate startDate) {
        return reservationService.findByRentalItemAndStartDate(itemId, startDate).stream()
                .map(ReservationResourceDto::from)
                .toList();
    }

    @Override
    public List<ReservationResourceDto> findByReserverId(Integer reserverId) {
        return reservationService.findByReserverId(reserverId).stream()
                .map(ReservationResourceDto::from)
                .toList();
    }

    @Override
    public List<ReservationResourceDto> findByRentalItemId(Integer itemId) {
        return reservationService.findByRentalItemId(itemId).stream()
                .map(ReservationResourceDto::from)
                .toList();
    }

    @Override
    public List<ReservationResourceDto> findOverlappedReservations(LocalDateTime from, LocalDateTime to) {
        return reservationService.findOverlappedReservations(from, to).stream()
                .map(ReservationResourceDto::from)
                .toList();
    }

    @Override
    public ReservationResourceDto findOverlappedReservation(Integer itemId, LocalDateTime from, LocalDateTime to) {
        return reservationService.findOverlappedReservation(itemId, from, to)
                .map(ReservationResourceDto::from)
                .orElse(null);
    }

    @Override
    public boolean hasRentalItemWith(Integer itemId) {
        return reservationService.hasRentalItemWith(itemId);
    }

    @Override
    public boolean hasUserAccountWith(Integer userAccountId) {
        return reservationService.hasUserAccountWith(userAccountId);
    }

    @ValidateGroup(groups = Add.class) // for @ReserveStartDateTimeFuture
    @Override
    public ReservationResourceDto add(AddReservationEventDto dto) throws BusinessFlowException {
        return reservationService.add(dto.toEntity()).transform(ReservationResourceDto::from);
    }

    @Override
    public ReservationResourceDto update(ReservationResourceDto dto) {
        return reservationService.update(dto.toEntity()).transform(ReservationResourceDto::from);
    }

    @Override
    public void delete(Integer reservationId) throws BusinessFlowException {
        reservationService.delete(reservationId);
    }

    @Override
    public void cancel(Integer reservationId, Integer reserverId) throws BusinessFlowException {
        reservationService.cancel(reservationId, reserverId);
    }
}
