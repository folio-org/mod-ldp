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
@Table(name = "log", schema="ldpsystem")
public class LogObj implements Serializable {
    @Id
    private Timestamp logTime;
    private String tableName;
    private String elapsedTime;
    private String message;
}
