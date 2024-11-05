package com.chat.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "CHANNELS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Channel {

	@Id
	@GeneratedValue
	private String id;

	private String name;

	@OneToMany
	private List<Message> messages;

	@ManyToMany
	private List<User> members;
}
