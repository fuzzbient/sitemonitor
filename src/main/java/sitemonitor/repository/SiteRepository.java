package sitemonitor.repository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "sites", path = "sites")
public interface SiteRepository extends PagingAndSortingRepository<Site, Long> {
	
	@Cacheable("site")
	public Site getById(Long id);

	@Override
	@Cacheable("sites")
	public Iterable<Site> findAll(Sort sort);
	
	@Cacheable("site")
	public Site getOneByName(@Param("name") String name);
	
	@Override
	@CacheEvict(cacheNames={"site","sites"}, allEntries=true)
	public <S extends Site> S save(S entity);
	
	@Override
	@CacheEvict(cacheNames={"site","sites"}, allEntries=true)
	public void delete(Site entity);	
	
}
