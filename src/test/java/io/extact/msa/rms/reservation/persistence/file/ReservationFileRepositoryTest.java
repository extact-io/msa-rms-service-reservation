package io.extact.msa.rms.reservation.persistence.file;

import static io.extact.msa.rms.reservation.ReservationComponentFactoryTestUtils.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.msa.rms.platform.fw.persistence.file.io.PathResolver;
import io.extact.msa.rms.platform.test.PathResolverParameterExtension;
import io.extact.msa.rms.platform.test.PlatformTestUtils;
import io.extact.msa.rms.reservation.domain.Reservation;
import io.extact.msa.rms.reservation.persistence.AbstractReservationRepositoryTest;
import io.extact.msa.rms.reservation.persistence.ReservationRepository;

@ExtendWith(PathResolverParameterExtension.class)
class ReservationFileRepositoryTest extends AbstractReservationRepositoryTest {

    private ReservationFileRepository repository;

    @BeforeEach
    void setUp(PathResolver pathResolver) throws Exception {
        repository = newReservationFileRepository(pathResolver);
    }

    @Test
    void testAdd() throws Exception {
        var addReservation = Reservation.ofTransient(LocalDateTime.of(2021, 4, 18, 10, 0, 0), LocalDateTime.of(2021, 5, 16, 20, 0, 0), "メモ4", 3, 1);
        repository.add(addReservation);

        List<String[]> records = PlatformTestUtils.getAllRecords(repository.getStoragePath());
        String[] lastRecord = (String[]) records.get(records.size() - 1);
        String[] expectRow = { String.valueOf(records.size()), "2021/04/18 10:00", "2021/05/16 20:00", "メモ4", "3", "1" };

        assertThat(lastRecord).containsExactly(expectRow);
    }

    @Override
    protected ReservationRepository repository() {
        return repository;
    }
}
