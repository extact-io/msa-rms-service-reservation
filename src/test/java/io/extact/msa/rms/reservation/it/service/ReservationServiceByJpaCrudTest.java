package io.extact.msa.rms.reservation.it.service;

import io.helidon.microprofile.tests.junit5.AddConfig;

@AddConfig(key = "persistence.apiType", value = "jpa")
class ReservationServiceByJpaCrudTest extends AbstractReservationServiceCrudTest {
}
