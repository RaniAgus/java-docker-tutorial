package io.github.raniagus.example.util;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Objects;

public class CsvReader<T> implements AutoCloseable {
  private final CsvMapper mapper;
  private final Class<T> clazz;
  private final InputStream inputStream;

  public CsvReader(Class<T> clazz, String path) {
    this.mapper = new CsvMapper();
    this.clazz = clazz;
    this.inputStream = Objects.requireNonNull(
        getClass().getResourceAsStream(path),
        "No se encontró el archivo %s".formatted(path)
    );
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
