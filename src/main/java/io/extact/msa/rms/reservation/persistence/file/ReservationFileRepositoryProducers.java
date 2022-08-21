package io.extact.msa.rms.reservation.persistence.file;

import java.io.IOException;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import io.extact.msa.rms.platform.fw.persistence.file.io.FileAccessor;
import io.extact.msa.rms.platform.fw.persistence.file.producer.EntityArrayConverter;
import io.extact.msa.rms.platform.fw.persistence.file.producer.FileOpenPathDeriver;
import io.extact.msa.rms.platform.fw.persistence.file.producer.FileRepositoryProducers;
import io.extact.msa.rms.reservation.domain.Reservation;

@Dependent
public class ReservationFileRepositoryProducers implements FileRepositoryProducers<Reservation> {

    // ファイルパスが定義されている設定ファイルキー(csv.%s.fileName.%s)の2個目の%sの値
    private static final String FILE_NAME_TYPE_CONFIG_KEY = "reservation";
    private FileOpenPathDeriver pathDeriver;

    @Inject
    public ReservationFileRepositoryProducers(FileOpenPathDeriver pathDeriver) {
        this.pathDeriver = pathDeriver;
    }

    @Produces
    public FileAccessor creteFileAccessor() throws IOException {
        return new FileAccessor(pathDeriver.derive(FILE_NAME_TYPE_CONFIG_KEY));
    }

    @Produces
    public EntityArrayConverter<Reservation> createRentalItemConverter() {
        return ReservationArrayConverter.INSTANCE;
    }
}
