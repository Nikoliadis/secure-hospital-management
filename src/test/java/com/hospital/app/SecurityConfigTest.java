package com.hospital.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void unauthenticatedUserIsRedirectedToLogin() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void loginPageIsPubliclyAccessible() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminCanAccessAdminPanel() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "secretary", roles = {"SECRETARIAT"})
    void secretariatCannotAccessAdminPanel() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "secretary", roles = {"SECRETARIAT"})
    void secretariatCanAccessPatients() throws Exception {
        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "dr.smith", roles = {"DOCTOR"})
    void doctorCannotAccessPatients() throws Exception {
        mockMvc.perform(get("/patients"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "dr.smith", roles = {"DOCTOR"})
    void doctorCanAccessVisits() throws Exception {
        mockMvc.perform(get("/visits"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "dr.smith", roles = {"DOCTOR"})
    void doctorCannotAccessAdminPanel() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "patient1", roles = {"PATIENT"})
    void patientCannotAccessPatientsList() throws Exception {
        mockMvc.perform(get("/patients"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "patient1", roles = {"PATIENT"})
    void patientCannotAccessAdminPanel() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void apiEndpointRequiresJwt() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminCanAccessDashboard() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk());
    }
}
