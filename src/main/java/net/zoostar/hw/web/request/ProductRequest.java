package net.zoostar.hw.web.request;

import net.zoostar.hw.entity.SourceEntityMapper;
import net.zoostar.hw.entity.Product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProductRequest implements SourceEntityMapper<Product, String> {

	private String source;
	
	private String sourceId;

	private String sku;
	
	private String desc;
	
	@Override
	public Product toEntity() {
		var entity = new Product();
		entity.setName(desc);
		entity.setSku(sku);
		entity.setSourceCode(source);
		entity.setSourceId(sourceId);
		return entity;
	}

}
