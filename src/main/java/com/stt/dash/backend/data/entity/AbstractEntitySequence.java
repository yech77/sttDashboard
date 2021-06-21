package com.stt.dash.backend.data.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.*;

@MappedSuperclass
public abstract class AbstractEntitySequence implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	@Version
	private int version=0;

	public Long getId() {
		return id;
	}

	public int getVersion() {
		return version;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, version);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		AbstractEntitySequence that = (AbstractEntitySequence) o;
		return version == that.version &&
				Objects.equals(id, that.id);
	}
}
