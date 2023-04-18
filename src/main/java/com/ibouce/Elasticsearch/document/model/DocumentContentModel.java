/*package com.ibouce.Elasticsearch.document.model;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "documents")
@Data
public class DocumentContentModel {

    @Id
    private String id;

    @Field(type = FieldType.Text, name = "content", analyzer = "french")
    private String content;

}
*/