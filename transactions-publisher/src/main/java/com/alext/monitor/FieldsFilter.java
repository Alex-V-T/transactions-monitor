package com.alext.monitor;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Component
@PropertySource("fields-to-display.properties")
public class FieldsFilter {
    private final Map<TransactionType, Collection<String>> fieldsToDisplay;

    public FieldsFilter(
            @Value("${fields.transfer}") final String transferFields,
            @Value("${fields.deposit}") final String depositFields,
            @Value("${fields.withdrawal}") final String withdrawalFields
    ) {
        fieldsToDisplay = new HashMap<>(3);

        fieldsToDisplay.put(TransactionType.TRANSFER, parseString(transferFields));
        fieldsToDisplay.put(TransactionType.DEPOSIT, parseString(depositFields));
        fieldsToDisplay.put(TransactionType.WITHDRAWAL, parseString(withdrawalFields));
    }

    static Collection<String> parseString(final String fileds) {
        return Arrays.stream(fileds.trim().split("\\s*,\\s*"))
                .filter(s -> !s.isEmpty()).collect(toList());
    }

    public Transaction filter(final Transaction transaction) {
        Transaction.Builder filteredBuilder = new Transaction.Builder().
                id(transaction.getId()).
                type(transaction.getType()).
                timestamp(transaction.getTimestamp());

        if (transaction.getFields() != null) {
            for (String field : fieldsToDisplay.get(transaction.getType())) {
                if (transaction.getFields().containsKey(field)) {
                    filteredBuilder.field(field, transaction.getFields().get(field));
                }
            }
        }

        return filteredBuilder.build();
    }
}
