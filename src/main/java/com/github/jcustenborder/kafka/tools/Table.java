package com.github.jcustenborder.kafka.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Table {
  ConsoleFormat consoleFormat = ConsoleFormat.Table;

  List<Map<String, String>> rows = new ArrayList<>();

  public void addRow(String field0, Object value0) {
    Map<String, Object> row = new LinkedHashMap<>();
    row.put(field0, value0);
    addRow(row);
  }

  public void addRow(String field0, Object value0, String field1, Object value1) {
    Map<String, Object> row = new LinkedHashMap<>();
    row.put(field0, value0);
    row.put(field1, value1);
    addRow(row);
  }

  public void addRow(
      String field0, Object value0,
      String field1, Object value1,
      String field2, Object value2
  ) {
    Map<String, Object> row = new LinkedHashMap<>();
    row.put(field0, value0);
    row.put(field1, value1);
    row.put(field2, value2);
    addRow(row);
  }

  public void addRow(
      String field0, Object value0,
      String field1, Object value1,
      String field2, Object value2,
      String field3, Object value3
  ) {
    Map<String, Object> row = new LinkedHashMap<>();
    row.put(field0, value0);
    row.put(field1, value1);
    row.put(field2, value2);
    row.put(field3, value3);
    addRow(row);
  }

  public void addRow(
      String field0, Object value0,
      String field1, Object value1,
      String field2, Object value2,
      String field3, Object value3,
      String field4, Object value4
  ) {
    Map<String, Object> row = new LinkedHashMap<>();
    row.put(field0, value0);
    row.put(field1, value1);
    row.put(field2, value2);
    row.put(field3, value3);
    row.put(field4, value4);
    addRow(row);
  }

  public void addRow(Map<String, Object> row) {
    Map<String, String> result = new LinkedHashMap<>();
    for (Map.Entry<String, Object> e : row.entrySet()) {
      String value = e.getValue() == null ? "" : e.getValue().toString();
      result.put(e.getKey(), value);
    }
    rows.add(result);
  }

  String toStringJson() {
    try {
      return ObjectMapperFactory.INSTANCE.writeValueAsString(rows);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }

  String toStringAsciiTable() {
    Optional<Map<String, String>> optionalFirstRow = rows.stream().findFirst();
    if (!optionalFirstRow.isPresent()) {
      return "";
    }
    Map<String, String> firstRow = optionalFirstRow.get();
    Set<String> fields = firstRow.keySet();

    AsciiTable at = new AsciiTable();
    at.addRule();
    at.addRow(fields);
    at.addRule();

    for (Map<String, String> row : rows) {
      List<String> atRow = new ArrayList<>(row.size());
      for (String field : fields) {
        atRow.add(row.get(field));
      }
      at.addRow(atRow);
    }
    at.addRule();
    at.setTextAlignment(TextAlignment.LEFT);
    return at.render(150);
  }


  @Override
  public String toString() {
    String result;

    switch (this.consoleFormat) {
      case Json:
        result = toStringJson();
        break;
      case Table:
        result = toStringAsciiTable();
        break;
      default:
        throw new IllegalStateException(
            String.format("%s it not supported.", this.consoleFormat)
        );
    }

    return result;
  }

  public void outputFormat(ConsoleFormat consoleFormat) {
    this.consoleFormat = consoleFormat;
  }


}
