package io.extact.msa.rms.reservation.it.service;

import io.extact.msa.rms.test.utils.ClearOpenTelemetryContextCdiExtension;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.AddExtension;

@AddExtension(ClearOpenTelemetryContextCdiExtension.class)
@AddConfig(key = "rms.persistence.apiType", value = "jpa")
class ReservationServiceByJpaCrudTest extends AbstractReservationServiceCrudTest {
}
