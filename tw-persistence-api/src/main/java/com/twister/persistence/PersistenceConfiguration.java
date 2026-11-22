package com.twister.persistence;

import com.twister.persistence.entity.AuditableEntity;
import com.twister.persistence.mapper.reference.CountryRepositoryMapperImpl;
import com.twister.persistence.mapper.reference.ServiceTypeRepositoryMapperImpl;
import com.twister.persistence.mapper.reference.SpareBrandRepositoryMapperImpl;
import com.twister.persistence.mapper.reference.SpareRepositoryMapperImpl;
import com.twister.persistence.mapper.reference.SpareTypeRepositoryMapperImpl;
import com.twister.persistence.mapper.reference.TransportBrandRepositoryMapperImpl;
import com.twister.persistence.mapper.reference.TransportRepositoryMapperImpl;
import com.twister.persistence.mapper.reference.TransportTypeRepositoryMapperImpl;
import com.twister.persistence.mapper.repair.RepairInvoiceRepositoryMapperImpl;
import com.twister.persistence.mapper.repair.RepairOrderRepositoryMapperImpl;
import com.twister.persistence.mapper.repair.RepairOrderStatusHistoryRepositoryMapperImpl;
import com.twister.persistence.repository.CustomerRepository;
import java.util.Optional;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// Must precede Boot's repository scan, which would register the same repositories again for an app above this package.
// TODO(discuss): apps now need their own @EntityScan and @EnableJpaRepositories; decide if plugins may add JPA types
@AutoConfiguration(before = DataJpaRepositoriesAutoConfiguration.class)
@EnableJpaRepositories(basePackageClasses = CustomerRepository.class)
@EntityScan(basePackageClasses = AuditableEntity.class)
@EnableJpaAuditing
@Import({
    CountryRepositoryMapperImpl.class,
    ServiceTypeRepositoryMapperImpl.class,
    SpareBrandRepositoryMapperImpl.class,
    SpareRepositoryMapperImpl.class,
    SpareTypeRepositoryMapperImpl.class,
    TransportBrandRepositoryMapperImpl.class,
    TransportRepositoryMapperImpl.class,
    TransportTypeRepositoryMapperImpl.class,
    RepairInvoiceRepositoryMapperImpl.class,
    RepairOrderRepositoryMapperImpl.class,
    RepairOrderStatusHistoryRepositoryMapperImpl.class
})
public class PersistenceConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of("SYSTEM");
    }
}
