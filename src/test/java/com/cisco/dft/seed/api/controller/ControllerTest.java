package com.cisco.dft.seed.api.controller;

import com.cisco.dft.seed.api.config.ApplicationConfigLoader;
import com.cisco.dft.seed.api.service.TestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Controller.class, TestService.class, ApplicationConfigLoader.class})
@ComponentScan
@EnableAutoConfiguration
public class ControllerTest {

	@Autowired
	private Controller controller;
	
	private MockMvc mvc;

	@Before
	public void setUp() throws Exception {
		mvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	public void sayHello() throws Exception {
		mvc.perform(
				MockMvcRequestBuilders.get("/dft/seed/test/Testing").accept(
						MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().string("Testing"));
	}
}
