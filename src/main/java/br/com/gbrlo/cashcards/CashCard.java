package br.com.gbrlo.cashcards;

import org.springframework.data.annotation.Id;

public record CashCard(@Id Long id, Double amount, String owner) {
}
