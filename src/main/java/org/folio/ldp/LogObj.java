package org.folio.ldp;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@SuppressWarnings("unused")
@NoArgsConstructor
@Data
@Entity
@Table(name = "log", schema="dbsystem")
public class LogObj implements Serializable {
    @Id
    private Timestamp logTime;
    private String tableName;
    private String elapsedTime;
    private String message;
}
