package org.folio.ldp;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@SuppressWarnings("unused")
@NoArgsConstructor
@Data
@Entity
@Table(name = "columns", schema="information_schema")
public class ColumnObj implements Serializable {
    @Id
    public String columnName;
    public String ordinalPosition;
    private String tableSchema;
    private String tableName;
}
