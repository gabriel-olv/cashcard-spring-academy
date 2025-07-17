package br.com.gbrlo.cashcards;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
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
		var response = rest
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards/99", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		var documentContext = JsonPath.parse(response.getBody());
		var id = documentContext.read("$.id");
		var amount = documentContext.read("$.amount");
		assertThat(id).isEqualTo(99);
		assertThat(amount).isEqualTo(123.45);
	}

	@Test
	void shouldNotReturnACashCardWithUnknownId() {
		var response = rest
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards/1000", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isBlank();
	}

	@Test
	@DirtiesContext
	void shouldCreateANewCashCard() {
		var newCashCard = new CashCard(null, 250.0, null);
		var createResponse = rest
				.withBasicAuth("sarah1", "abc123")
				.postForEntity("/cashcards", newCashCard, Void.class);

		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		var locationOfNewCashCard = createResponse.getHeaders().getLocation();
		var getResponse = rest
				.withBasicAuth("sarah1", "abc123")
				.getForEntity(locationOfNewCashCard, String.class);

		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		var documentContext = JsonPath.parse(getResponse.getBody());
		var id = documentContext.read("$.id");
		var amount = documentContext.read("$.amount");

		assertThat(id).isNotNull();
		assertThat(amount).isEqualTo(250.0);
	}

	@Test
	void shouldReturnAllCashCardsWhenListIsRequested() {
		var response = rest
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		var documentContext = JsonPath.parse(response.getBody());
		var cashCardCount = documentContext.read("$.length()");
		assertThat(cashCardCount).isEqualTo(3);

		JSONArray ids = documentContext.read("$..id");
		JSONArray amount = documentContext.read("$..amount");
		assertThat(ids).containsExactlyInAnyOrder(99, 100, 101);
		assertThat(amount).containsExactlyInAnyOrder(123.45, 1.0, 150.0);
	}

	@Test
	void shouldReturnAPageOfCashCards() {
		var response = rest
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards?page=0&size=1", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		var documentContext = JsonPath.parse(response.getBody());
		JSONArray page = documentContext.read("$[*]");
		assertThat(page.size()).isEqualTo(1);
	}

	@Test
	void shouldReturnASortedPageOfCashCards() {
		var response = rest
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards?page=0&size=1&sort=amount,desc", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		var documentContext = JsonPath.parse(response.getBody());
		JSONArray list = documentContext.read("$[*]");
		assertThat(list.size()).isEqualTo(1);

		var amount = documentContext.read("$[0].amount");
		assertThat(amount).isEqualTo(150.0);
	}

	@Test
	void shouldReturnASortedPageOfCashCardsWithNoParametersAndUseDefaultValues() {
		var response = rest
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		var documentContext = JsonPath.parse(response.getBody());
		JSONArray list = documentContext.read("$[*]");
		assertThat(list.size()).isEqualTo(3);

		JSONArray amounts = documentContext.read("$..amount");
		assertThat(amounts).containsExactly(1.0, 123.45, 150.0);
	}

	@Test
	void shouldNotReturnACashCardWhenUsingBadCredentials() {
		var response = rest
				.withBasicAuth("bad-user", "abc123")
				.getForEntity("/cashcards/99", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

		response = rest
				.withBasicAuth("sarah1", "bad-password")
				.getForEntity("/cashcards/99", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	void shouldRejectUsersWhoAreNotCardOwner() {
		var response = rest
				.withBasicAuth("hank-owns-no-cards", "qrs456")
				.getForEntity("/cashcards/99", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	void shouldNotAllowAccessToCashCardsTheyDoNotOwn() {
		var response = rest
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards/102", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}
}
