package com.github.dmmarchenko;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import com.github.dmmarchenko.lab.InputRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AppTests {
    @Value("${local.server.port}")
    private int port;
    private TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void validRequest() {
        InputRequest inputRequest = new InputRequest();
        inputRequest.setMonths(Arrays.asList("May", "April", "March"));
        inputRequest.setWeekDays(Arrays.asList("Friday", "Sunday", "Saturday"));

        ResponseEntity<Void> response = restTemplate.postForEntity(fullUrl("/home"), inputRequest, Void.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

    }

    @Test
    public void invalidMonth() {
        InputRequest inputRequest = new InputRequest();
        inputRequest.setMonths(Arrays.asList("May", "April", "SMarch"));
        inputRequest.setWeekDays(Arrays.asList("Friday", "Sunday", "Saturday"));

        ResponseEntity<Void> response = restTemplate.postForEntity(fullUrl("/home"), inputRequest, Void.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Test
    public void invalidWeekDay() {
        InputRequest inputRequest = new InputRequest();
        inputRequest.setMonths(Arrays.asList("May", "April", "March"));
        inputRequest.setWeekDays(Arrays.asList("Friday", "Srunday", "Saturday"));

        ResponseEntity<Void> response = restTemplate.postForEntity(fullUrl("/home"), inputRequest, Void.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private String fullUrl(String relativeUrl) {
        return "http://localhost:" + port + "/" + relativeUrl;
    }

}
