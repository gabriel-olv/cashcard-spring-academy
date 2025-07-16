package br.com.gbrlo.cashcards;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

@JsonTest
public class CashCardJsonTest {

    @Autowired
    private JacksonTester<CashCard> json;

    @Test
    void myFirstTest() {
        assertThat(42).isEqualTo(42);
    }

    @Test
    void cashCardSerializationTest() throws IOException {
        var cashCard = new CashCard(99L, 123.45);
        var cashCardJson = json.write(cashCard);
        assertThat(cashCardJson).isStrictlyEqualToJson("expected.json");
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
                    "amount": 123.45
                }
                """;
        var cashCard = new CashCard(99L, 123.45);
        assertThat(json.parse(expected)).isEqualTo(cashCard);
        assertThat(json.parseObject(expected).id()).isEqualTo(99);
        assertThat(json.parseObject(expected).amount()).isEqualTo(123.45);
    }
}
