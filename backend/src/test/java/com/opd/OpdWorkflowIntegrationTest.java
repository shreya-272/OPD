package com.opd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OpdWorkflowIntegrationTest {
    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void registersPatientBooksAppointmentAndCompletesConsultation() throws Exception {
        String patientJson = mvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Priya Sharma","gender":"FEMALE","age":32,"phoneNumber":"+91 98765 43210"}
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        JsonNode patient = objectMapper.readTree(patientJson);
        long patientId = patient.get("id").asLong();

        mvc.perform(get("/api/patients").param("search", "98765"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        LocalDateTime appointmentAt = LocalDateTime.now().plusHours(2).withNano(0);
        String appointmentJson = mvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"patientId":%d,"doctorName":"Dr. Ananya Rao","appointmentAt":"%s"}
                                """.formatted(patientId, appointmentAt)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long appointmentId = objectMapper.readTree(appointmentJson).get("id").asLong();

        mvc.perform(post("/api/appointments/{id}/consultation", appointmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"bloodPressure":"120/80","temperature":36.8,"notes":"Rest and follow up in one week."}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.appointment.status").value("COMPLETED"));

        mvc.perform(get("/api/appointments").param("date", appointmentAt.toLocalDate().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("COMPLETED"));

        mvc.perform(get("/api/patients/{id}/consultations", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].bloodPressure").value("120/80"));

        mvc.perform(post("/api/appointments/{id}/consultation", appointmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"bloodPressure":"120/80","temperature":36.8,"notes":"Duplicate"}
                                """))
                .andExpect(status().isConflict());
    }
}
