package com.skyroster;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Smoke test to verify the main application class exists.
 * Full context-load test requires PostgreSQL and is in integration tests.
 */
class SkyRosterApplicationTests {

	@Test
	void shouldHaveMainMethod() {
		assertDoesNotThrow(() -> SkyRosterApplication.class.getDeclaredMethod("main", String[].class));
	}
}
