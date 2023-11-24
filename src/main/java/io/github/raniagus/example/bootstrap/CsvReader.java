package io.github.raniagus.example.bootstrap;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;

public class CsvReader<T> implements AutoCloseable {
  private final CsvMapper mapper;
  private final Class<T> clazz;
  private final InputStream inputStream;

  public CsvReader(Class<T> clazz, InputStream inputStream) {
    this.mapper = new CsvMapper();
    this.clazz = clazz;
    this.inputStream = inputStream;
  }

  @SuppressWarnings("unchecked")
  public List<T> readAll() {
    var schema = mapper.schemaFor(clazz).withHeader();
    try (var reader = mapper.readerFor(clazz).with(schema).readValues(inputStream)) {
      return (List<T>) reader.readAll();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public void close() {
    try {
      inputStream.close();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
