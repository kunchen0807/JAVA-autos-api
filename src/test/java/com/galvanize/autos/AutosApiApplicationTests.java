package com.galvanize.autos;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations= "classpath:application-test.properties")
class AutosApiApplicationTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    AutosRepository autosRepository;

    Random r = new Random();
    List<Automobile> testAutos;
    @BeforeEach
    void setUp() {
        this.testAutos = new ArrayList<>();
        Automobile auto;
        String[] colors = {"Red", "Blue", "Green", "Orange", "Yellow", "Black", "Brown", "Root Beer", "Magenta", "Amber"};

        for (int i = 0; i < 50; i++) {
            if(i % 3 == 0) {
                auto = new Automobile(1967, "Ford", "Mustang", "AABBCC" + (i*13));
                auto.setColor(colors[r.nextInt(10)]);
            } else if (i % 2 == 0) {
                auto = new Automobile(2000, "Dodge", "Viper", "VVBBXX" + (i*12));
                auto.setColor(colors[r.nextInt(10)]);
            } else {
                auto = new Automobile(2020, "Audi", "Quatro", "QQZZAA" + (i*11));
                auto.setColor(colors[r.nextInt(10)]);
            }
            this.testAutos.add(auto);
        }
        autosRepository.saveAll(this.testAutos);
    }
    @AfterEach
    void tearDown() {
        autosRepository.deleteAll();
    }

	@Test
	void contextLoads() {
	}

    @Test
    void getAutos_exists_returnsAutosList() {
        ResponseEntity<AutosList> response = restTemplate.getForEntity("/api/autos", AutosList.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isEmpty()).isFalse();
        for (Automobile auto : response.getBody().getAutomobiles()){
            System.out.println(auto);
        }
    }

    @Test
    void getAutos_search_returnsAutosList() {
        int seq = r.nextInt(50);
        String color = testAutos.get(seq).getColor();
        String make = testAutos.get(seq).getMake();
        ResponseEntity<AutosList> response = restTemplate.getForEntity(String.format("/api/autos?color=%s&make=%s", color, make), AutosList.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isEmpty()).isFalse();
        assertThat(response.getBody().getAutomobiles().size()).isGreaterThanOrEqualTo(1);
        for (Automobile auto : response.getBody().getAutomobiles()){
            System.out.println(auto);
        }
    }

    @Test
    void getAutos_search_returnsNoContent() {
        String color = "Pink";
        String make = "Toyota";
        ResponseEntity<AutosList> response = restTemplate.getForEntity(String.format("/api/autos?color=%s&make=%s", color, make), AutosList.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void getAutos_search_withOnlyColor_returnsAutosList() {
        int seq = r.nextInt(50);
        String color = testAutos.get(seq).getColor();
        ResponseEntity<AutosList> response = restTemplate.getForEntity(String.format("/api/autos?color=%s", color), AutosList.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isEmpty()).isFalse();
        assertThat(response.getBody().getAutomobiles().size()).isGreaterThanOrEqualTo(1);
        for (Automobile auto : response.getBody().getAutomobiles()){
            System.out.println(auto);
        }
    }

    @Test
    void getAutos_search_withOnlyColor_returnsNoContent() {
        String color = "Pink";
        ResponseEntity<AutosList> response = restTemplate.getForEntity(String.format("/api/autos?color=%s", color), AutosList.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void getAutos_search_withOnlyMake_returnsAutosList() {
        int seq = r.nextInt(50);
        String make = testAutos.get(seq).getMake();
        ResponseEntity<AutosList> response = restTemplate.getForEntity(String.format("/api/autos?make=%s", make), AutosList.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isEmpty()).isFalse();
        assertThat(response.getBody().getAutomobiles().size()).isGreaterThanOrEqualTo(1);
        for (Automobile auto : response.getBody().getAutomobiles()){
            System.out.println(auto);
        }
    }

    @Test
    void getAutos_search_withOnlyMake_returnsNoContent() {
        String make = "Toyota";
        ResponseEntity<AutosList> response = restTemplate.getForEntity(String.format("/api/autos?make=%s", make), AutosList.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void addAuto_returnsNewAutoDetails() {
        // Arrange
        Automobile automobile = new Automobile();
        automobile.setVin("ABC123XX");
        automobile.setYear(1993);
        automobile.setMake("Ford");
        automobile.setModel("Windstar");
        automobile.setColor("Blue");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<Automobile> request = new HttpEntity<>(automobile, headers);

        // Act
        ResponseEntity<Automobile> response = restTemplate.postForEntity("/api/autos", request, Automobile.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getVin()).isEqualTo(automobile.getVin());
    }

    @Test
    void updateAuto_returnsAutoDetails() {
        // Arrange
        UpdateOwnerRequest updateOwnerRequest = new UpdateOwnerRequest("White", "Kun");
        int seq = r.nextInt(50);
        String vin = testAutos.get(seq).getVin();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<UpdateOwnerRequest> request = new HttpEntity<>(updateOwnerRequest, headers);

        // Act
        ResponseEntity<Automobile> response = restTemplate.exchange(String.format("/api/autos/%s", vin), HttpMethod.PATCH, request, Automobile.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody().getColor()).isEqualTo("White");
        assertThat(response.getBody().getOwner()).isEqualTo("Kun");
        System.out.println(response.getBody());
    }
}
