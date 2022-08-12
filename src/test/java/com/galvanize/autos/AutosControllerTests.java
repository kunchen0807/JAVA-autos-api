package com.galvanize.autos;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AutosController.class)
public class AutosControllerTests {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    AutosService autosService;

    ObjectMapper mapper = new ObjectMapper();

    // GET /api/autos
    // GET: /api/autos returns list of all autos in database (200)
    @Test
    void getAuto_noParams_exists_returnsAutosLists() throws Exception {
        // Arrange
        List<Automobile> automobiles = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            automobiles.add(new Automobile(1967 + i, "Ford", "Mustang", "AABB" + i));
        }
        when(autosService.getAutos()).thenReturn(new AutosList(automobiles));
        // Act
        mockMvc.perform(get("/api/autos"))
                .andDo(print())
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.automobiles", hasSize(5)));
    }

    // GET: /api/autos no autos in database returns (204) no content
    @Test
    void getAutos_noParams_none_returnsNoContent() throws Exception {
        // Arrange
        when(autosService.getAutos()).thenReturn(new AutosList());
        // ACT
        mockMvc.perform(get("/api/autos"))
                .andDo(print())
                // Assert
                .andExpect(status().isNoContent());
    }

    // GET: /api/autos?color=yellow returns all autos whose color is yellow (200)
    @Test
    void getAutos_onlySearchParamsColor_exists_returnsAutosList() throws Exception {
        // Arrange
        List<Automobile> automobiles = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            automobiles.add(new Automobile(1967 + i, "Toyota", "Corolla", "AABB" + i));
        }
        when(autosService.getAutosWithMatchingColor(anyString())).thenReturn(new AutosList(automobiles));
        // Act
        mockMvc.perform(get("/api/autos?color=White"))
                .andDo(print())
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.automobiles", hasSize(5)));
    }

    // GET: /api/autos?make=Toyota returns all autos whose maker is Toyota (200)
    @Test
    void getAutos_onlySearchParamsMake_exists_returnsAutosList() throws Exception {
        // Arrange
        List<Automobile> automobiles = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            automobiles.add(new Automobile(1967 + i, "Toyota", "Corolla", "AABB" + i));
        }
        when(autosService.getAutosWithMatchingMake(anyString())).thenReturn(new AutosList(automobiles));
        // Act
        mockMvc.perform(get("/api/autos?make=Toyota"))
                .andDo(print())
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.automobiles", hasSize(5)));
    }

    // GET: /api/autos?color=white&make=Toyota returns all autos whose maker is Toyota and color is white (200)
    @Test
    void getAutos_searchParams_exists_returnsAutosList() throws Exception {
        // Arrange
        List<Automobile> automobiles = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            automobiles.add(new Automobile(1967 + i, "Toyota", "Corolla", "AABB" + i));
        }
        when(autosService.getAutos(anyString(), anyString())).thenReturn(new AutosList(automobiles));
        // ACT
        mockMvc.perform(get("/api/autos?color=White&make=Toyota"))
                .andDo(print())
                //Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.automobiles", hasSize(5)));
    }

    // POST /api/autos
    // POST: /api/autos returns created auto (200)
    @Test
    void addAuto_valid_returnsAuto() throws Exception {
        Automobile automobile = new Automobile(1967, "Ford", "Mustang", "AABBCC");
        when(autosService.addAuto(any(Automobile.class))).thenReturn(automobile);
        mockMvc.perform(post("/api/autos").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(automobile)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("make").value("Ford"));
    }

    // POST: /api/autos returns error message with (400) status code
    @Test
    void addAuto_badRequest_returns400() throws Exception {
        Automobile automobile = new Automobile(1967, "Ford", "Mustang", "AABBCC");
        when(autosService.addAuto(any(Automobile.class))).thenThrow(InvalidAutoException.class);
        mockMvc.perform(post("/api/autos").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(automobile)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // GET /api/autos/{vin}
    // GET /api/autos/{vin} returns auto whose vin is requested (200)
    @Test
    void getAuto_withVin_returnsAuto() throws Exception {
        Automobile automobile = new Automobile(1967, "Ford", "Mustang", "AABBCC");
        when(autosService.getAuto(anyString())).thenReturn(automobile);
        mockMvc.perform(get("/api/autos/AABBCC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("vin").value(automobile.getVin()));
    }

    // GET /api/autos/{vin} returns (204) no content if auto with requested vin is not in the database
    @Test
    void getAuto_withVin_returnsNoContent() throws Exception {
        when(autosService.getAuto(anyString())).thenReturn(null);
        mockMvc.perform(get("/api/autos/AABBCC"))
                .andExpect(status().isNoContent());
    }

    // PATCH /api/autos/{vin}
    // PATCH /api/autos/{vin} returns updated auto information (200)
    @Test
    void updateAuto_withObject_returnsAuto() throws Exception {
        Automobile automobile = new Automobile(1967, "Ford", "Mustang", "AABBCC");
        when(autosService.updateAuto(anyString(), anyString(), anyString())).thenReturn(automobile);
        mockMvc.perform(patch("/api/autos/" + automobile.getVin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"color\":\"Red\",\"owner\":\"Bob\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("color").value("Red"))
                .andExpect(jsonPath("owner").value("Bob"));
    }

    // PATCH /api/autos/{vin} returns (204) no content if this vin is not in the database
    @Test
    void updateAuto_withObject_returnsNoContent() throws Exception {
        when(autosService.updateAuto(anyString(), anyString(), anyString())).thenReturn(null);
        mockMvc.perform(patch("/api/autos/AABBCC")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"color\":\"Red\",\"owner\":\"Bob\"}"))
                .andExpect(status().isNoContent());
    }

    // PATCH /api/autos/{vin} returns error message with (400) status code
    @Test
    void updateAuto_badRequest_returns400() throws Exception {
        UpdateOwnerRequest updateOwnerRequest = new UpdateOwnerRequest("Red", "Bob");
        when(autosService.updateAuto(anyString(), anyString(), anyString())).thenThrow(InvalidAutoUpdateException.class);
        mockMvc.perform(patch("/api/autos/AABBCC")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateOwnerRequest)))
                .andExpect(status().isBadRequest());
    }

    // DELETE /api/autos/{vin}
    // DELETE /api/autos/{vin} returns (202) status code indicate Automobile delete request accepted
    @Test
    void deleteAuto_withVin_exists_returns202() throws Exception {
        mockMvc.perform(delete("/api/autos/AABBCC"))
                .andExpect(status().isAccepted());
        verify(autosService).deleteAuto(anyString());
    }

    // DELETE /api/autos/{vin} returns (204) if auto with requested vin does not found in database
    @Test
    void deleteAuto_withVin_notExists_returns204() throws Exception {
        doThrow(new AutoNotFoundException()).when(autosService).deleteAuto(anyString());
        mockMvc.perform(delete("/api/autos/AABBCC"))
                .andExpect(status().isNoContent());
        verify(autosService).deleteAuto(anyString());
    }
}
