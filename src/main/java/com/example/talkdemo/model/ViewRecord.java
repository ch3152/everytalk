package com.example.talkdemo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document(collection = "sns_view_track")
public class ViewRecord {
    @Id
    private String id;
    private String postId;
    private String nickname;
    private String ip;
    private LocalDate date;
}
