package io.extact.msa.rms.reservation;

import java.io.IOException;
import java.nio.file.Path;

import io.extact.msa.rms.platform.fw.persistence.file.io.FileAccessor;
import io.extact.msa.rms.platform.fw.persistence.file.io.PathResolver;
import io.extact.msa.rms.reservation.it.external.stub.RentalItemCheckApiLocalStub;
import io.extact.msa.rms.reservation.it.external.stub.UserAccountCheckApiLocalStub;
import io.extact.msa.rms.reservation.persistence.file.ReservationArrayConverter;
import io.extact.msa.rms.reservation.persistence.file.ReservationFileRepository;
import io.extact.msa.rms.reservation.service.ReservationService;

/**
 * テストケースで利用するコンポーネントファクトリユーティルクラス。
 */
public class ReservationComponentFactoryTestUtils {

    private static final String RESERVATION_TEST_FILE_NAME = "reservationTest.csv";

    public static ReservationFileRepository newReservationFileRepository(PathResolver pathResolver) throws IOException {
        Path tempFile =  FileAccessor.copyResourceToRealPath(RESERVATION_TEST_FILE_NAME, pathResolver);
        FileAccessor fa = new FileAccessor(tempFile );
        return new ReservationFileRepository(fa, ReservationArrayConverter.INSTANCE);
    }

    public static ReservationService newReservationService(PathResolver pathResolver) throws IOException {
        var itemStub = new RentalItemCheckApiLocalStub();
        var userStub = new UserAccountCheckApiLocalStub();
        itemStub.init();
        userStub.init();
        return new ReservationService(newReservationFileRepository(pathResolver), itemStub, userStub);
    }
}
