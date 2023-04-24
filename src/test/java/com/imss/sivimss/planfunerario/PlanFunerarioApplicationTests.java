package com.imss.sivimss.planfunerario;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.imss.sivimss.planfunerario.PlanFunerarioApplication;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class PlanFunerarioApplicationTests {

	@Test
	void contextLoads() {
		String result="test";
		PlanFunerarioApplication.main(new String[]{});
		assertNotNull(result);
	}

}
