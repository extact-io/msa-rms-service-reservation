package io.extact.msa.rms.reservation.it.service;

import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.HelidonTest;

@HelidonTest
@AddConfig(key = "persistence.apiType", value = "file")
@AddConfig(key = "csv.type", value = "temporary")
class ReservationServiceByTemporaryFileCrudTest extends AbstractReservationServiceCrudTest {
}
