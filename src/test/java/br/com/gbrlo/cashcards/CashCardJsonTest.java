package br.com.gbrlo.cashcards;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

@JsonTest
public class CashCardJsonTest {

    @Autowired
    private JacksonTester<CashCard> json;

    @Autowired
    private JacksonTester<CashCard[]> jsonList;

    private CashCard[] cashCards;

    @BeforeEach
    void setUp() {
        cashCards = Arrays.array(
                new CashCard(99L, 123.45, "sarah1"),
                new CashCard(100L, 1.0, "sarah1"),
                new CashCard(101L, 150.0, "sarah1")
        );
    }

    @Test
    void myFirstTest() {
        assertThat(42).isEqualTo(42);
    }

    @Test
    void cashCardSerializationTest() throws IOException {
        var cashCard = new CashCard(99L, 123.45, "sarah1");
        var cashCardJson = json.write(cashCard);
        assertThat(cashCardJson).isStrictlyEqualToJson("single.json");
        assertThat(cashCardJson).hasJsonPathNumberValue("@.id");
        assertThat(cashCardJson).extractingJsonPathNumberValue("@.id").isEqualTo(99);
        assertThat(cashCardJson).hasJsonPathNumberValue("@.amount");
        assertThat(cashCardJson).extractingJsonPathNumberValue("@.amount").isEqualTo(123.45);
    }

    @Test
    void cashCardDeserializationTest() throws IOException {
        var expected = """
                {
                    "id": 99,
                    "amount": 123.45,
                    "owner": "sarah1"
                }
                """;
        var cashCard = new CashCard(99L, 123.45, "sarah1");
        assertThat(json.parse(expected)).isEqualTo(cashCard);
        assertThat(json.parseObject(expected).id()).isEqualTo(99);
        assertThat(json.parseObject(expected).amount()).isEqualTo(123.45);
    }

    @Test
    void cashCardListSerializationTest() throws IOException {
        assertThat(jsonList.write(cashCards)).isStrictlyEqualToJson("list.json");
    }

    @Test
    void cashCardListDeserializationTest() throws IOException {
        var expected = """
                [
                    { "id": 99, "amount": 123.45, "owner": "sarah1" },
                    { "id": 100, "amount": 1.0, "owner": "sarah1" },
                    { "id": 101, "amount": 150.0, "owner": "sarah1" }
                ]
                """;
        assertThat(jsonList.parse(expected)).isEqualTo(cashCards);
    }
}
