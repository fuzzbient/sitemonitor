package sitemonitor.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "events", path = "events")
public interface EventRepository extends PagingAndSortingRepository<Event, Long> {

	List<Event> findBySite(@Param("site") Site site, Sort sort);
	List<Event> findByStatusChange(String statusChange, Sort sort);
	
}
