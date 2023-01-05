package io.extact.msa.rms.reservation.it.persistence;

import io.extact.msa.rms.test.utils.ClearOpenTelemetryContextCdiExtension;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.AddExtension;
import io.helidon.microprofile.tests.junit5.HelidonTest;

@HelidonTest(resetPerTest = true)
@AddExtension(ClearOpenTelemetryContextCdiExtension.class)
@AddConfig(key = "persistence.apiType", value = "jpa")
class ReservationJpaRepositoryValidationTest extends AbstractReservationRepositoryValidationTest {
}
