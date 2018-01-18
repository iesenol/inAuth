package demo.model.repository;

import org.springframework.data.repository.CrudRepository;

import demo.model.Coordinate;
import demo.model.CoordinateId;

public interface CoordinatesRepository extends CrudRepository<Coordinate, CoordinateId> {
}
