package io.extact.msa.rms.reservation.persistence.file;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.extact.msa.rms.platform.core.extension.EnabledIfRuntimeConfig;
import io.extact.msa.rms.platform.fw.domain.vo.DateTimePeriod;
import io.extact.msa.rms.platform.fw.persistence.GenericRepository.ApiType;
import io.extact.msa.rms.platform.fw.persistence.file.AbstractFileRepository;
import io.extact.msa.rms.platform.fw.persistence.file.io.FileAccessor;
import io.extact.msa.rms.platform.fw.persistence.file.producer.EntityArrayConverter;
import io.extact.msa.rms.reservation.domain.Reservation;
import io.extact.msa.rms.reservation.persistence.ReservationRepository;

@ApplicationScoped
@EnabledIfRuntimeConfig(propertyName = ApiType.PROP_NAME, value = ApiType.FILE)
public class ReservationFileRepository extends AbstractFileRepository<Reservation> implements ReservationRepository {

    @Inject
    public ReservationFileRepository(FileAccessor fileAccessor, EntityArrayConverter<Reservation> converter) {
        super(fileAccessor, converter);
    }

    @Override
    public List<Reservation> findByRentalItemAndStartDate(int rentalItemId, LocalDate startDate) {
        return this.load().stream()
                .map(this.getConverter()::toEntity)
                .filter(reservation -> reservation.getStartDateTime().toLocalDate().equals(startDate))
                .filter(reservation -> reservation.getRentalItemId() == rentalItemId)
                .toList();
    }

    @Override
    public List<Reservation> findByReserverId(int reserverId) {
        return this.load().stream()
                .map(this.getConverter()::toEntity)
                .filter(reservation -> reservation.getUserAccountId() == reserverId)
                .toList();
    }

    @Override
    public List<Reservation> findByRentalItemId(int rentalItemId) {
        return this.load().stream()
                .map(this.getConverter()::toEntity)
                .filter(reservation -> reservation.getRentalItemId() == rentalItemId)
                .toList();
    }

    //@Override
    public List<Reservation> findOverlappedReservations(int rentalItemId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        var conditionOfPeriod = new DateTimePeriod(startDateTime, endDateTime);
        return this.load().stream()
                .map(this.getConverter()::toEntity)
                .filter(reservation -> reservation.getRentalItemId() == rentalItemId)
                .filter(reservation -> reservation.getReservePeriod().isOverlappedBy(conditionOfPeriod))
                .toList();
    }

    @Override
    public Reservation findOverlappedReservation(int rentalItemId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return this.findOverlappedReservations(rentalItemId, startDateTime, endDateTime).stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Reservation> findOverlappedReservations(LocalDateTime from, LocalDateTime to) {
        var conditionOfPeriod = new DateTimePeriod(from, to);
        return this.load().stream()
                .map(this.getConverter()::toEntity)
                .filter(reservation -> reservation.getReservePeriod().isOverlappedBy(conditionOfPeriod))
                .toList();
    }
}
