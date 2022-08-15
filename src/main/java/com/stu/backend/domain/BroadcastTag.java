package com.stu.backend.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class BroadcastTag {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(targetEntity = Broadcast.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "broadcast_id")
	private Broadcast broadcast;

	@ManyToOne(targetEntity = Tag.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "tag_id")
	private Tag tag;
}
