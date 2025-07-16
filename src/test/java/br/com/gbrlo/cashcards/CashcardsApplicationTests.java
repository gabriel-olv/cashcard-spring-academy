package br.com.gbrlo.cashcards;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CashcardsApplicationTests {

	@Autowired
	TestRestTemplate rest;

	@Test
	void shouldReturnACashCardWhenDataIsSaved() {
		var response = rest.getForEntity("/cashcards/99", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		var documentContext = JsonPath.parse(response.getBody());
		var id = documentContext.read("$.id");
		var amount = documentContext.read("$.amount");
		assertThat(id).isEqualTo(99);
		assertThat(amount).isEqualTo(123.45);
	}

	@Test
	void shouldNotReturnACashCardWithUnknownId() {
		var response = rest.getForEntity("/cashcards/1000", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isBlank();
	}

	@Test
	@DirtiesContext
	void shouldCreateANewCashCard() {
		var newCashCard = new CashCard(null, 250.0);
		var createResponse = rest.postForEntity("/cashcards", newCashCard, Void.class);

		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		var locationOfNewCashCard = createResponse.getHeaders().getLocation();
		var getResponse = rest.getForEntity(locationOfNewCashCard, String.class);

		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		var documentContext = JsonPath.parse(getResponse.getBody());
		var id = documentContext.read("$.id");
		var amount = documentContext.read("$.amount");

		assertThat(id).isNotNull();
		assertThat(amount).isEqualTo(250.0);
	}

	@Test
	void shouldReturnAllCashCardsWhenListIsRequested() {
		var response = rest.getForEntity("/cashcards", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		var documentContext = JsonPath.parse(response.getBody());
		var cashCardCount = documentContext.read("$.length()");
		assertThat(cashCardCount).isEqualTo(3);

		JSONArray ids = documentContext.read("$..id");
		JSONArray amount = documentContext.read("$..amount");
		assertThat(ids).containsExactlyInAnyOrder(99, 100, 101);
		assertThat(amount).containsExactlyInAnyOrder(123.45, 1.0, 150.0);
	}
}
