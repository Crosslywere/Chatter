package com.chat.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "MESSAGES")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

	@Id
	@GeneratedValue
	@Column(name = "MESSAGE_ID")
	private String id;

	@ManyToOne
	private User sentBy;

	private String content;

	private Date timeSent;
}
