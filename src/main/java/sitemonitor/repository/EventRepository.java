package sitemonitor.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

@RepositoryRestResource(collectionResourceRel = "events", path = "events")
public interface EventRepository extends PagingAndSortingRepository<Event, Long> {

	List<Event> findBySite(@Param("site") Site site, Sort sort);
	List<Event> findBySiteAndEventTimeBetween(@Param("site") Site site, @Param("start") Date start, @Param("end") Date end, Sort sort);
	List<Event> findByStatusChange(String statusChange, Sort sort);
	
	@Modifying
	@Transactional
	@Query("delete from Event e where e.eventTime < ?1")
	void deleteEventsOlderThan(Date date);
	
}
