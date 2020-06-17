package com.folio.ldp;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
    private String logTime;
    private String tableName;
    private String elapsedTime;
    private String message;
}
