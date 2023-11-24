package io.github.raniagus.example.bootstrap;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public class CsvReader<T> {
  private final CsvMapper mapper;
  private final Class<T> clazz;
  private final String filename;

  public CsvReader(Class<T> clazz, String filename) {
    this.mapper = new CsvMapper();
    this.clazz = clazz;
    this.filename = filename;
  }

  @SuppressWarnings("unchecked")
  public List<T> readAll() {
    var schema = mapper.schemaFor(clazz).withHeader();
    try (var inputStream = new FileInputStream(filename)) {
      try (var reader = mapper.readerFor(clazz).with(schema).readValues(inputStream)) {
        return (List<T>) reader.readAll();
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
