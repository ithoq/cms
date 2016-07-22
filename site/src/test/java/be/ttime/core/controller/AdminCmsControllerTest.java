package be.ttime.core.controller;


import be.ttime.StartCMS;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * NOTE : @WithMockUser not working
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = StartCMS.class)
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)
//@ActiveProfiles("test")
@WebAppConfiguration
public class AdminCmsControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    Environment env;

    @Autowired
    private Filter springSecurityFilterChain;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders
                        .webAppContextSetup(this.context)
                        .apply(springSecurity())
                        .build();

        // TO DO : INSTALL THE CMS
    }

    @Test
    public void testGetPageNotLoggedIn() throws Exception {
        mockMvc.perform(get("/admin/cms")).andExpect(status().is3xxRedirection());
    }
/*
    @Test
    public void testGetPageWithRoles() throws Exception {
        mockMvc.perform(get("/admin/cms").with(user("Admin").roles("ADMIN", "ADMIN_CMS")))
                .andExpect(status().isOk());
    }
*/
}