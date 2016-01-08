package sitemonitor.repository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "sites", path = "sites")
public interface SiteRepository extends PagingAndSortingRepository<Site, Long> {
	
	@Cacheable("site-findOne")
	public Site findOne(Long id);

	@Cacheable("site-findAll")
	public Iterable<Site> findAll(Sort sort);
	
	@Cacheable("site-findOneByName")
	public Site findOneByName(@Param("name") String name);
	
	@Override
	@CacheEvict(value = { "site-findOne", "site-findAll", "site-findOneByName" }, allEntries = true)
	public <S extends Site> S save(S entity);
	
	@Override
	@CacheEvict(value = { "site-findOne", "site-findAll", "site-findOneByName" }, allEntries = true)
	public void delete(Site entity);	
	
}
