package io.extact.msa.rms.reservation.service;


import static io.extact.msa.rms.platform.fw.exception.BusinessFlowException.CauseType.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import io.extact.msa.rms.platform.fw.exception.BusinessFlowException;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException.CauseType;
import io.extact.msa.rms.platform.fw.persistence.GenericRepository;
import io.extact.msa.rms.platform.fw.service.GenericService;
import io.extact.msa.rms.reservation.domain.Reservation;
import io.extact.msa.rms.reservation.external.RentalItemCheckApi;
import io.extact.msa.rms.reservation.external.UserAccountCheckApi;
import io.extact.msa.rms.reservation.persistence.ReservationRepository;

@Transactional(TxType.REQUIRED)
@ApplicationScoped
public class ReservationService implements GenericService<Reservation> {

    private ReservationRepository repository;
    private RentalItemCheckApi itemApi;
    private UserAccountCheckApi userApi;

    @Inject
    public ReservationService(ReservationRepository reservationRepository, RentalItemCheckApi itemApi,
            UserAccountCheckApi userApi) {
        this.repository = reservationRepository;
        this.itemApi = itemApi;
        this.userApi = userApi;
    }

    public List<Reservation> findByRentalItemAndStartDate(int itemId, LocalDate startDate) {
        return repository.findByRentalItemAndStartDate(itemId, startDate);
    }

    public List<Reservation> findByReserverId(int reserverId) {
        return repository.findByReserverId(reserverId);
    }

    public List<Reservation> findByRentalItemId(int itemId) {
        return repository.findByRentalItemId(itemId);
    }

    public Optional<Reservation> findOverlappedReservation(int itemId, LocalDateTime from, LocalDateTime to) {
        var r = repository.findOverlappedReservation(itemId, from, to);
        return Optional.ofNullable(r);
    }

    public List<Reservation> findOverlappedReservations(LocalDateTime from, LocalDateTime to) {
        return repository.findOverlappedReservations(from, to);
    }

    public boolean hasRentalItemWith(int itemId) {
        return !findByRentalItemId(itemId).isEmpty();
    }

    public boolean hasUserAccountWith(int userAccountId) {
        return !findByReserverId(userAccountId).isEmpty();
    }

    public void cancel(int reservationId, int cancelUserId) throws BusinessFlowException {
        var reservation = repository.get(reservationId);
        if (reservation == null) {
            throw new BusinessFlowException("Reservation does not exist for reservationId", NOT_FOUND);
        }
        // キャンセルはレンタル品を予約した人しか取り消せないことのチェック
        if (reservation.getUserAccountId() != cancelUserId) {
            throw new BusinessFlowException(
                    String.format("Others' reservations cannot be deleted. reserverId=%s, cancelUserId=%s",
                            reservation.getUserAccountId(),
                            cancelUserId),
                    CauseType.FORBIDDEN);
        }
        repository.delete(reservation);
    }

    @Override
    public Reservation add(Reservation entity) {
        valiateRelation(entity);
        return GenericService.super.add(entity);
    }

    @Override
    public Reservation update(Reservation entity) {
        valiateRelation(entity);
        return GenericService.super.update(entity);
    }

    @Override
    public Consumer<Reservation> getDuplicateChecker() {
        return (targetReservation) -> {
            var foundReservations = repository.findOverlappedReservations(
                    targetReservation.getRentalItemId(),
                    targetReservation.getStartDateTime(),
                    targetReservation.getEndDateTime());
            if (!foundReservations.isEmpty() &&
                    (targetReservation.getId() == null
                            || foundReservations.stream().anyMatch(r -> !r.isSameId(targetReservation)))) {
                throw new BusinessFlowException("Already reserved.", DUPRICATE);
            }
        };
    }

    @Override
    public GenericRepository<Reservation> getRepository() {
        return repository;
    }

    private void valiateRelation(Reservation entity) {
        var existsItem = itemApi.exists(entity.getRentalItemId());
        if (!existsItem) {
            throw new BusinessFlowException("RentalItem does not exist for rentalItemId.", CauseType.NOT_FOUND);
        }
        var existsUser = userApi.exists(entity.getUserAccountId());
        if (!existsUser) {
            throw new BusinessFlowException("UserAccount does not exist for userAccountId.", CauseType.NOT_FOUND);
        }
    }
}
