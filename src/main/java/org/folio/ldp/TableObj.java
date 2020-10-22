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
@Table(name = "tables", schema="dbsystem")
public class TableObj implements Serializable {
    @Id
    // private Timestamp updated;
    public String tableName;
    private String documentationUrl;
    private String rowCount;
}
