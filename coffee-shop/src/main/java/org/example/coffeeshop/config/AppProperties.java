package org.example.coffeeshop.config;

import java.util.List;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app")
public class AppProperties {

	private String rootPath;
	private String shopId;
	private List<String> queues;

}
