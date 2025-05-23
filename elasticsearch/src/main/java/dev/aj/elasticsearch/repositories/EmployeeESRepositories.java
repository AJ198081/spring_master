package dev.aj.elasticsearch.repositories;

import dev.aj.elasticsearch.domain.Employee;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EmployeeESRepositories extends ElasticsearchRepository<Employee, Long> {

}
