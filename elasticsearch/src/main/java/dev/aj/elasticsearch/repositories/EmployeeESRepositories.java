package dev.aj.elasticsearch.repositories;

import dev.aj.elasticsearch.domain.Employee;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeESRepositories extends ElasticsearchRepository<Employee, Long> {

}
