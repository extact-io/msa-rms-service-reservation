package io.extact.msa.rms.reservation.it.service;

import io.extact.msa.rms.test.utils.ClearOpenTelemetryContextCdiExtension;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.AddExtension;
import io.helidon.microprofile.tests.junit5.HelidonTest;

@HelidonTest
@AddExtension(ClearOpenTelemetryContextCdiExtension.class)
@AddConfig(key = "rms.persistence.apiType", value = "file")
@AddConfig(key = "rms.persistence.csv.type", value = "temporary")
class ReservationServiceByTemporaryFileCrudTest extends AbstractReservationServiceCrudTest {
}
