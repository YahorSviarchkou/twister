package com.twister.workflow;

import com.twister.domain.reference.Country;
import com.twister.domain.reference.ServiceType;
import com.twister.domain.reference.SpareBrand;
import com.twister.domain.reference.SpareType;
import com.twister.domain.reference.TransportBrand;
import com.twister.domain.reference.TransportType;
import com.twister.domain.repair.RepairOrderStatusHistory;
import com.twister.domain.repair.RepairTaskItemStatusHistory;
import com.twister.domain.repair.RepairTaskStatusHistory;
import com.twister.workflow.dao.CustomerDao;
import com.twister.workflow.dao.UserDao;
import com.twister.workflow.dao.reference.ReferenceDao;
import com.twister.workflow.dao.reference.SpareDao;
import com.twister.workflow.dao.reference.TransportDao;
import com.twister.workflow.dao.repair.RepairInvoiceDao;
import com.twister.workflow.dao.repair.RepairOrderDao;
import com.twister.workflow.dao.repair.RepairTaskDao;
import com.twister.workflow.dao.repair.RepairTaskItemDao;
import com.twister.workflow.dao.repair.StatusHistoryDao;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class WorkflowTestConfiguration {

    @Bean
    @ConditionalOnMissingBean(value = Country.class, parameterizedContainer = ReferenceDao.class)
    public ReferenceDao<Country> countryDaoMock() {
        //noinspection unchecked
        return (ReferenceDao<Country>) Mockito.mock(ReferenceDao.class);
    }

    @Bean
    @ConditionalOnMissingBean(value = ServiceType.class, parameterizedContainer = ReferenceDao.class)
    public ReferenceDao<ServiceType> serviceTypeDaoMock() {
        //noinspection unchecked
        return (ReferenceDao<ServiceType>) Mockito.mock(ReferenceDao.class);
    }

    @Bean
    @ConditionalOnMissingBean(value = SpareBrand.class, parameterizedContainer = ReferenceDao.class)
    public ReferenceDao<SpareBrand> spareBrandDaoMock() {
        //noinspection unchecked
        return (ReferenceDao<SpareBrand>) Mockito.mock(ReferenceDao.class);
    }

    @Bean
    @ConditionalOnMissingBean(value = SpareType.class, parameterizedContainer = ReferenceDao.class)
    public ReferenceDao<SpareType> spareTypeDaoMock() {
        //noinspection unchecked
        return (ReferenceDao<SpareType>) Mockito.mock(ReferenceDao.class);
    }

    @Bean
    @ConditionalOnMissingBean(value = TransportBrand.class, parameterizedContainer = ReferenceDao.class)
    public ReferenceDao<TransportBrand> transportBrandDaoMock() {
        //noinspection unchecked
        return (ReferenceDao<TransportBrand>) Mockito.mock(ReferenceDao.class);
    }

    @Bean
    @ConditionalOnMissingBean(value = TransportType.class, parameterizedContainer = ReferenceDao.class)
    public ReferenceDao<TransportType> transportTypeDaoMock() {
        //noinspection unchecked
        return (ReferenceDao<TransportType>) Mockito.mock(ReferenceDao.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public SpareDao spareDaoMock() {
        return Mockito.mock(SpareDao.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public TransportDao transportDaoMock() {
        return Mockito.mock(TransportDao.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public RepairInvoiceDao repairInvoiceDaoMock() {
        return Mockito.mock(RepairInvoiceDao.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public RepairOrderDao repairOrderDaoMock() {
        return Mockito.mock(RepairOrderDao.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public RepairTaskDao repairTaskDaoMock() {
        return Mockito.mock(RepairTaskDao.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public RepairTaskItemDao repairTaskItemDaoMock() {
        return Mockito.mock(RepairTaskItemDao.class);
    }

    @Bean
    @ConditionalOnMissingBean(value = RepairOrderStatusHistory.class, parameterizedContainer = StatusHistoryDao.class)
    public StatusHistoryDao<RepairOrderStatusHistory> repairOrderStatusHistoryDaoMock() {
        //noinspection unchecked
        return (StatusHistoryDao<RepairOrderStatusHistory>) Mockito.mock(StatusHistoryDao.class);
    }

    @Bean
    @ConditionalOnMissingBean(
            value = RepairTaskItemStatusHistory.class,
            parameterizedContainer = StatusHistoryDao.class)
    public StatusHistoryDao<RepairTaskItemStatusHistory> repairTaskItemStatusHistoryDaoMock() {
        //noinspection unchecked
        return (StatusHistoryDao<RepairTaskItemStatusHistory>) Mockito.mock(StatusHistoryDao.class);
    }

    @Bean
    @ConditionalOnMissingBean(value = RepairTaskStatusHistory.class, parameterizedContainer = StatusHistoryDao.class)
    public StatusHistoryDao<RepairTaskStatusHistory> repairTaskStatusHistoryDaoMock() {
        //noinspection unchecked
        return (StatusHistoryDao<RepairTaskStatusHistory>) Mockito.mock(StatusHistoryDao.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public CustomerDao customerDaoMock() {
        return Mockito.mock(CustomerDao.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public UserDao userDaoMock() {
        return Mockito.mock(UserDao.class);
    }
}
