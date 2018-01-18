package demo.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

@Embeddable
public class CoordinateId implements Serializable {
	@Transient
	private static final long serialVersionUID = 1L;

	@Column(name="lat")
	private BigDecimal latitude;

	@Column(name="lng")
	private BigDecimal longitude;

	public CoordinateId() {
	}

	public CoordinateId(BigDecimal latitude, BigDecimal longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || !(obj instanceof CoordinateId))
			return false;
		
		CoordinateId that = (CoordinateId) obj;
		return  Objects.equals(getLatitude(), that.getLatitude()) && 
				Objects.equals(getLongitude(), that.getLongitude());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getLatitude(), getLongitude());
	}

	@Override
	public String toString() {
		return "[Latitude=" + latitude.toString() + ", Longitude=" + longitude.toString() + "]";
	}
}