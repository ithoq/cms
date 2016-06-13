package be.ttime.core.controller;

import be.ttime.StartCMS;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration
@SpringApplicationConfiguration(classes = StartCMS.class)
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)
@ActiveProfiles("test")
public class AdminCmsControllerTest {

    private MockMvc mockMvc;

    // TEST SPRING SECURITY

    // TEST POST

    // TEST DATABASE CONSTRAINT
    @Test
    @WithMockUser(roles = "ADMIN_PRIVILEGE")
    public void testGetPage() throws Exception {
        mockMvc.perform(get("/admin/cms/get/{id}", 3L))
                .andExpect(status().isNotFound());
    }



}
