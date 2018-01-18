package demo.model;

import java.math.BigDecimal;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table (name="coordinates")
public class Coordinate {
	@EmbeddedId
	private CoordinateId id;           // primary key
	
	@Transient
	private String status;

	public Coordinate() {
	}

	public Coordinate(BigDecimal latitude, BigDecimal longitude) {
		this.id = new CoordinateId(latitude, longitude);
		this.status = "";
	}

	public CoordinateId getId() {
		return id;
	}

	public void setId(CoordinateId id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return id.toString(); //  "[Latitude=" + id.getLatitude().toString() + ", Longitude=" + id.getLongitude().toString() + "]";
	}

}
