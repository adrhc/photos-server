package image.persistence.jpacustomizations;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.hibernate.jpa.QueryHints;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

@NoRepositoryBean
public class CustomRepositoryImpl<T, ID extends Serializable>
		extends SimpleJpaRepository<T, ID> implements ICustomCrudRepository<T, ID> {
	private final EntityManager em;

	public CustomRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.em = entityManager;
	}

	public CustomRepositoryImpl(Class<T> domainClass, EntityManager em) {
		super(domainClass, em);
		this.em = em;
	}

	@Override
	public long count() {
		return this.em.createQuery(getCountQueryStringSuper(), Long.class)
				.setHint(QueryHints.HINT_CACHEABLE, true)
				.getSingleResult();
	}

	@Override
	protected <S extends T> TypedQuery<S> getQuery(
			@Nullable Specification<S> spec, Class<S> domainClass, Sort sort) {
		TypedQuery<S> query = super.getQuery(spec, domainClass, sort);
		query.setHint(QueryHints.HINT_CACHEABLE, true);
		return query;
	}

	@Override
	protected <S extends T> TypedQuery<Long> getCountQuery(@Nullable Specification<S> spec, Class<S> domainClass) {
		TypedQuery<Long> query = super.getCountQuery(spec, domainClass);
		query.setHint(QueryHints.HINT_CACHEABLE, true);
		return query;
	}

	@Override
	@Transactional
	public void persist(T entity) {
		this.em.persist(entity);
	}

	@Override
	public T getById(ID id) {
		return this.em.find(getDomainClass(), id);
	}

	private String getCountQueryStringSuper() {
		try {
			return (String) MethodUtils.invokeMethod(this, true, "getCountQueryString");
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
