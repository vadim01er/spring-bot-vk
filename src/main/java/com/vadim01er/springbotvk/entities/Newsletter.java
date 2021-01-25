package com.vadim01er.springbotvk.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "newsletters")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Newsletter {
    @Id
    @Column(name = "time")
    private String time;

    @Column(name = "text_newsletter")
    private String textNewsletter;

}
