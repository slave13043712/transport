package org.aakimov.transport.rest.controllers;

import org.aakimov.transport.api.MappedRouteDataReader;
import org.aakimov.transport.api.MemoryRouteManager;
import org.aakimov.transport.api.RouteManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class RouteManagerControllerTest {

    @Configuration
    @ComponentScan(basePackages = "org.aakimov.transport.rest")
    static class TestConfig {

        @Bean
        public RouteManager getRouteManager() {
            int[] routeData = new MappedRouteDataReader().read(
                Paths.get("src/test/resources/org/aakimov/transport/rest/controllers/route_data_file")
            );

            return new MemoryRouteManager(
                routeData
            );
        }
    }

    private static final String API_DIRECT_URL = "/api/direct";

    private static final String DEPARTURE_ID_PARAM_NAME = "dep_sid";

    private static final String ARRIVAL_ID_PARAM_NAME = "arr_sid";

    @Inject
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void testFindDirectRouteReturnsTrueIfRouteExists() throws Exception { //NOSONAR
        int departureStopId = 5;
        int arrivalStopId = 78;

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(API_DIRECT_URL)
            .param(DEPARTURE_ID_PARAM_NAME, String.valueOf(departureStopId))
            .param(ARRIVAL_ID_PARAM_NAME, String.valueOf(arrivalStopId));

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.dep_sid", is(departureStopId)))
            .andExpect(jsonPath("$.arr_sid", is(arrivalStopId)))
            .andExpect(jsonPath("$.direct_bus_route", is(true)));
    }

    @Test
    public void testFindDirectRouteReturnsFalseIfRouteDoesNotExist() throws Exception { //NOSONAR
        int departureStopId = -1;
        int arrivalStopId = -2;

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(API_DIRECT_URL)
            .param(DEPARTURE_ID_PARAM_NAME, String.valueOf(departureStopId))
            .param(ARRIVAL_ID_PARAM_NAME, String.valueOf(arrivalStopId));

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.dep_sid", is(departureStopId)))
            .andExpect(jsonPath("$.arr_sid", is(arrivalStopId)))
            .andExpect(jsonPath("$.direct_bus_route", is(false)));
    }

    @Test
    public void testFindDirectRouteReturnsBadRequestStatusCodeIfParametersAreInvalid() throws Exception { //NOSONAR
        String departureStopId = "some_wrong_value";
        int arrivalStopId = 10;

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(API_DIRECT_URL)
            .param(DEPARTURE_ID_PARAM_NAME, departureStopId)
            .param(ARRIVAL_ID_PARAM_NAME, String.valueOf(arrivalStopId));

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest());
    }
}
