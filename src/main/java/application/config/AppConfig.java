package application.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;

import ninja.cero.sqltemplate.core.SqlTemplate;
import ninja.cero.sqltemplate.core.mapper.MapperBuilder;
import ninja.cero.sqltemplate.core.parameter.ParamBuilder;
import ninja.cero.sqltemplate.core.template.TemplateEngine;

/**
 * アプリケーション設定クラス
 * @author 作成者氏名
 *
 */
@Configuration
public class AppConfig {

	@Bean
	public SqlTemplate sqlTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		return new SqlTemplate(jdbcTemplate, namedParameterJdbcTemplate, TemplateEngine.FREEMARKER, new ParamBuilder(), new MapperBuilder());
	}

	@Bean
	public Java8TimeDialect java8TimeDialect() {
		return new Java8TimeDialect();
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

}
