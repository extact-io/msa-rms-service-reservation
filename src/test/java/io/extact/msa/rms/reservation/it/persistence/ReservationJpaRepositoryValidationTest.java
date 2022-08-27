package io.extact.msa.rms.reservation.it.persistence;

import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.HelidonTest;

@HelidonTest(resetPerTest = true)
@AddConfig(key = "persistence.apiType", value = "jpa")
class ReservationJpaRepositoryValidationTest extends AbstractReservationRepositoryValidationTest {
}
