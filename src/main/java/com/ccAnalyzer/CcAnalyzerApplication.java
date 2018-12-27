package com.ccAnalyzer;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.NonNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.format.Formatter;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@SpringBootApplication
public class CcAnalyzerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CcAnalyzerApplication.class, args);
	}

	@NonNull
	@Bean
	Formatter<LocalDate> localDateFormatter() {
		return new Formatter<LocalDate>() {
			private final DateTimeFormatter date = DateTimeFormatter.ISO_DATE;

			@Override
			public LocalDate parse(final String text, Locale locale) {
				return Option.of(text)
						.map(it -> Try.of(() -> LocalDate.parse(it, date)).get())
						.getOrNull();
			}

			@Override
			public String print(final LocalDate localDate, Locale locale) {
				return Option.of(localDate).map(date::format).getOrNull();
			}
		};
	}

}

