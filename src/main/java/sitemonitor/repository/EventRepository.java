package sitemonitor.repository;

import java.util.Date;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

@RepositoryRestResource(collectionResourceRel = "events", path = "events")
public interface EventRepository extends PagingAndSortingRepository<Event, Long> {

	@Cacheable("event-findAll")
	Iterable<Event> findAll(Sort sort);
	
	@Cacheable("event-findBySite")
	List<Event> findBySite(@Param("site") Site site, Sort sort);
	
	@Cacheable("event-findBySiteAndEventTimeBetween")
	List<Event> findBySiteAndEventTimeBetween(@Param("site") Site site, @Param("start") Date start, @Param("end") Date end, Sort sort);
	
	@Cacheable("event-findByStatusChange")
	List<Event> findByStatusChange(String statusChange, Sort sort);
	
	@Modifying
	@Transactional
	@CacheEvict(value = { "event-findAll", "event-findBySite", "event-findBySiteAndEventTimeBetween", "event-findByStatusChange" }, allEntries = true)
	@Query("delete from Event e where e.eventTime < ?1")
	void deleteEventsOlderThan(Date date);
	
	@Override
	@CacheEvict(value = { "event-findAll", "event-findBySite", "event-findBySiteAndEventTimeBetween", "event-findByStatusChange" }, allEntries = true)
	public <S extends Event> S save(S entity);
	
	@Override
	@CacheEvict(value = { "event-findAll", "event-findBySite", "event-findBySiteAndEventTimeBetween", "event-findByStatusChange" }, allEntries = true)
	public void delete(Event entity);	
	
	@CacheEvict(value = { "event-findAll", "event-findBySite", "event-findBySiteAndEventTimeBetween", "event-findByStatusChange" }, allEntries = true)
	public void deleteAll();
	
}
