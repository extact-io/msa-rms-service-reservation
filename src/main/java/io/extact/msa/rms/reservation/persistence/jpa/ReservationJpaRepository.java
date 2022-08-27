package io.extact.msa.rms.reservation.persistence.jpa;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import io.extact.msa.rms.platform.core.extension.EnabledIfRuntimeConfig;
import io.extact.msa.rms.platform.fw.domain.vo.DateTimePeriod;
import io.extact.msa.rms.platform.fw.persistence.GenericRepository.ApiType;
import io.extact.msa.rms.platform.fw.persistence.jpa.JpaCrudRepository;
import io.extact.msa.rms.reservation.domain.Reservation;
import io.extact.msa.rms.reservation.persistence.ReservationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
@EnabledIfRuntimeConfig(propertyName = ApiType.PROP_NAME, value = ApiType.JPA)
public class ReservationJpaRepository extends JpaCrudRepository<Reservation> implements ReservationRepository {

    private static final String JPQL_SELECT_BY_RENTAL_ID = "select r from Reservation r where r.rentalItemId = ?1 order by r.id";

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Reservation> findByRentalItemAndStartDate(int rentalItemId, LocalDate startDate) {
        return em.createQuery(JPQL_SELECT_BY_RENTAL_ID, Reservation.class)
                    .setParameter(1, rentalItemId)
                    .getResultList().stream()
                    .filter(reservation -> reservation.getStartDateTime().toLocalDate().equals(startDate))
                    .toList();
    }

    @Override
    public List<Reservation> findByReserverId(int reserverId) {
        var jpql = "select r from Reservation r where r.userAccountId = ?1 order by r.id";
        return em.createQuery(jpql, Reservation.class)
                    .setParameter(1, reserverId)
                    .getResultList();
    }

    @Override
    public List<Reservation> findByRentalItemId(int rentalItemId) {
        return em.createQuery(JPQL_SELECT_BY_RENTAL_ID, Reservation.class)
                    .setParameter(1, rentalItemId)
                    .getResultList();
    }

    @Override
    public List<Reservation> findOverlappedReservations(int rentalItemId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        var period = new DateTimePeriod(startDateTime, endDateTime);
        return em.createQuery(JPQL_SELECT_BY_RENTAL_ID, Reservation.class)
                    .setParameter(1, rentalItemId)
                    .getResultList().stream()
                    .filter(reservation -> reservation.getReservePeriod().isOverlappedBy(period))
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
        var period = new DateTimePeriod(from, to);
        return findAll().stream()
                .filter(reservation -> reservation.getReservePeriod().isOverlappedBy(period))
                .toList();
    }

    @Override
    public EntityManager getEntityManage() {
        return this.em;
    }

    @Override
    public Class<Reservation> getTargetClass() {
        return Reservation.class;
    }
}
