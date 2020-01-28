package com.stroganova.reportingapp.jms;

import com.stroganova.reportingapp.enums.ReportFormat;
import com.stroganova.reportingapp.enums.ReportRequestType;
import com.stroganova.reportingapp.enums.ReportStatus;
import com.stroganova.reportingapp.enums.ReportType;
import com.stroganova.reportingapp.reports.*;
import com.stroganova.reportingapp.repository.MovieRepository;
import com.stroganova.reportingapp.repository.ReportRequestRepository;
import com.stroganova.reportingapp.service.reportrequest.ReportRequestServiceRegistry;
import com.stroganova.reportingapp.service.reportrequest.impl.AllMoviesXlsxReportRequestService;
import com.stroganova.reportingapp.service.reportrequest.impl.DefaultReportRequestService;
import com.stroganova.reportingapp.service.reportrequest.impl.MoviesByPeriodXlsxReportRequestService;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.jms.Message;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@Slf4j
@TestPropertySource(locations = "classpath:jms-test.properties")
@SpringBootTest

class ReportRequestReceiverITest {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${jms.queue.destination.request}")
    private String destination;

    @Value("${jms.queue.destination.response}")
    private String responseDestination;

    @MockBean
    private ReportRequestServiceRegistry reportRequestServiceRegistry;

    @MockBean
    private MoviesByPeriodXlsxReportRequestService moviesByPeriodXlsxReportRequestService;

    @MockBean
    private AllMoviesXlsxReportRequestService allMoviesXlsxReportRequestService;

    @MockBean
    private DefaultReportRequestService defaultReportRequestService;


    @Test
    void testReceiveMoviesByPeriodReportRequest() {
        log.info("STARTED MoviesByPeriod");

        String uuid = UUID.randomUUID().toString();
        ReportTypeAndFormat typeAndFormat = new ReportTypeAndFormat(ReportType.MOVIES_BY_PERIOD, ReportFormat.XLSX);
        DatePeriod datePeriod = new DatePeriod(LocalDate.of(2020, 1, 1), LocalDate.of(2022, 1, 1));

        ReportRequestByPeriod expectedReportRequestByPeriod = new ReportRequestByPeriod();

        expectedReportRequestByPeriod.setUuid(uuid);
        expectedReportRequestByPeriod.setReportRequestInfo(new ReportRequestInfo(ReportRequestType.REPORT_ADD, typeAndFormat));

        expectedReportRequestByPeriod.setDatePeriod(datePeriod);

        jmsTemplate.send(destination, session -> {

            log.info("creating message");
            Message message = session.createMessage();

            message.setStringProperty(ReportRequestProperties.REPORT_REQUEST_IDENTIFIER, uuid);
            message.setStringProperty(ReportRequestProperties.REPORT_REQUEST_TYPE, ReportRequestType.REPORT_ADD.getName());
            message.setStringProperty(ReportRequestProperties.REPORT_TYPE, "MoviesByPeriod");
            message.setStringProperty(ReportRequestProperties.REPORT_FORMAT, "xlsx");
            Map<String, String> datePeriodMap = new HashMap<>();
            datePeriodMap.put("dateFrom", "2020-01-01");
            datePeriodMap.put("dateTo", "2022-01-01");
            message.setObjectProperty(ReportRequestProperties.REPORT_DATE_PERIOD_MAP, datePeriodMap);

            return message;
        });


        try {
            log.info("update latch MoviesByPeriod");
            TestConfig.latch = new CountDownLatch(1);
            log.info("wait message receiving MoviesByPeriod");

            when(reportRequestServiceRegistry.getService(expectedReportRequestByPeriod.getReportRequestInfo())).thenReturn(moviesByPeriodXlsxReportRequestService);
            when(moviesByPeriodXlsxReportRequestService.handle(expectedReportRequestByPeriod)).thenReturn(expectedReportRequestByPeriod);

            assertTrue(TestConfig.latch.await(15, TimeUnit.SECONDS));

            log.info("continue test MoviesByPeriod");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ReportRequestByPeriod reportRequestReceived = (ReportRequestByPeriod) TestConfig.received;

        assertNotNull(reportRequestReceived);

        log.info("exp {}", expectedReportRequestByPeriod);
        log.info("act {}", reportRequestReceived);
        assertEquals(expectedReportRequestByPeriod, reportRequestReceived);

        log.info("FINISHED MoviesByPeriod");
    }


    @Test
    void testReceiveAllMoviesReportRequest() {
        log.info("STARTED AllMovies");

        String uuid = UUID.randomUUID().toString();
        ReportTypeAndFormat typeAndFormat = new ReportTypeAndFormat(ReportType.ALL_MOVIES, ReportFormat.XLSX);

        ReportRequest expectedReportRequest = new ReportRequest();
        expectedReportRequest.setUuid(uuid);
        expectedReportRequest.setReportRequestInfo(new ReportRequestInfo(ReportRequestType.REPORT_ADD, typeAndFormat));

        jmsTemplate.send(destination, session -> {

            log.info("creating message");
            Message message = session.createMessage();
            message.setStringProperty(ReportRequestProperties.REPORT_REQUEST_IDENTIFIER, uuid);
            message.setStringProperty(ReportRequestProperties.REPORT_REQUEST_TYPE, ReportRequestType.REPORT_ADD.getName());

            message.setStringProperty(ReportRequestProperties.REPORT_TYPE, "AllMovies");
            message.setStringProperty(ReportRequestProperties.REPORT_FORMAT, "xlsx");
            return message;
        });

        try {
            log.info("update latch AllMovies");
            TestConfig.latch = new CountDownLatch(1);
            log.info("wait message receiving AllMovies");

            when(reportRequestServiceRegistry.getService(expectedReportRequest.getReportRequestInfo())).thenReturn(allMoviesXlsxReportRequestService);


            when(allMoviesXlsxReportRequestService.handle(expectedReportRequest)).thenReturn(expectedReportRequest);

            assertTrue(TestConfig.latch.await(5, TimeUnit.SECONDS));
            log.info("continue test AllMovies");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ReportRequest reportRequestReceived = TestConfig.received;

        assertNotNull(reportRequestReceived);
        log.info("exp {}", expectedReportRequest);
        log.info("act {}", reportRequestReceived);
        assertEquals(expectedReportRequest, reportRequestReceived);

        log.info("FINISHED AllMovies");
    }

    @Test
    void testReceiveGetLinkReportRequest() {
        ReportRequestType reportRequestType = ReportRequestType.GET_LINK;

        log.info("STARTED {} ", reportRequestType);
        String uuid = UUID.randomUUID().toString();
        ReportRequest expectedReportRequest = new ReportRequest();
        expectedReportRequest.setUuid(uuid);
        expectedReportRequest.setReportRequestInfo(new ReportRequestInfo(reportRequestType, null));

        jmsTemplate.send(destination, session -> {
            log.info("creating message GetLink");
            Message message = session.createMessage();
            message.setStringProperty(ReportRequestProperties.REPORT_REQUEST_IDENTIFIER, uuid);
            message.setStringProperty(ReportRequestProperties.REPORT_REQUEST_TYPE, reportRequestType.getName());
            return message;
        });

        try {
            log.info("update latch GetLink");
            TestConfig.latch = new CountDownLatch(1);
            log.info("wait message receiving GetLink");

            when(reportRequestServiceRegistry.getService(expectedReportRequest.getReportRequestInfo())).thenReturn(defaultReportRequestService);

            ReportRequest reportRequestWithLink = new ReportRequest();
            reportRequestWithLink.setUuid(uuid);
            reportRequestWithLink.setReportRequestInfo(new ReportRequestInfo(ReportRequestType.REPORT_ADD, null));
            reportRequestWithLink.setLink("/link");

            when(defaultReportRequestService.handle(expectedReportRequest)).thenReturn(reportRequestWithLink);

            assertTrue(TestConfig.latch.await(5, TimeUnit.SECONDS));
            log.info("continue test GetLink");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ReportRequest reportRequestReceived = TestConfig.received;

        assertNotNull(reportRequestReceived);
        log.info("exp {}", expectedReportRequest);
        log.info("act {}", reportRequestReceived);
        assertEquals(expectedReportRequest, reportRequestReceived);

        log.info("FINISHED GetLink");
    }

    @Test
    void testReceiveStatusReportRequest() {
        ReportRequestType reportRequestType = ReportRequestType.CHECK_STATUS;

        log.info("STARTED {} ", reportRequestType);
        String uuid = UUID.randomUUID().toString();
        ReportRequest expectedReportRequest = new ReportRequest();
        expectedReportRequest.setUuid(uuid);
        expectedReportRequest.setReportRequestInfo(new ReportRequestInfo(reportRequestType, null));

        jmsTemplate.send(destination, session -> {
            log.info("creating message Status");
            Message message = session.createMessage();
            message.setStringProperty(ReportRequestProperties.REPORT_REQUEST_IDENTIFIER, uuid);
            message.setStringProperty(ReportRequestProperties.REPORT_REQUEST_TYPE, reportRequestType.getName());
            return message;
        });

        try {
            log.info("update latch Status");
            TestConfig.latch = new CountDownLatch(1);
            log.info("wait message receiving Status");

            when(reportRequestServiceRegistry.getService(expectedReportRequest.getReportRequestInfo())).thenReturn(defaultReportRequestService);

            ReportRequest reportRequestWithStatus = new ReportRequest();
            reportRequestWithStatus.setUuid(uuid);
            reportRequestWithStatus.setReportRequestInfo(new ReportRequestInfo(ReportRequestType.REPORT_ADD, null));
            reportRequestWithStatus.setLink(ReportStatus.DONE.name());

            when(defaultReportRequestService.handle(expectedReportRequest)).thenReturn(reportRequestWithStatus);

            assertTrue(TestConfig.latch.await(5, TimeUnit.SECONDS));
            log.info("continue test Status");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ReportRequest reportRequestReceived = TestConfig.received;

        assertNotNull(reportRequestReceived);
        log.info("exp {}", expectedReportRequest);
        log.info("act {}", reportRequestReceived);
        assertEquals(expectedReportRequest, reportRequestReceived);

        log.info("FINISHED Status");
    }

    @TestConfiguration
    public static class TestConfig {

        private static CountDownLatch latch;

        private static ReportRequest received;

        @MockBean
        private MovieRepository movieRepository;
        @MockBean
        private ReportRequestRepository reportRequestRepository;

        @Bean
        public static BeanPostProcessor reportRequestReceiverWrapper() {
            return new BeanPostProcessor() {

                @Override
                public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                    if (bean instanceof ReportRequestReceiver) {
                        MethodInterceptor interceptor = (invocation) -> {
                            Object result = invocation.proceed();
                            if (invocation.getMethod().getName().equals("receive")) {
                                received = (ReportRequest) invocation.getArguments()[0];
                                log.info("latch.countDown()");
                                latch.countDown();
                            }
                            return result;
                        };

                        if (AopUtils.isAopProxy(bean)) {
                            ((Advised) bean).addAdvice(interceptor);
                            return bean;
                        } else {
                            ProxyFactory proxyFactory = new ProxyFactory(bean);
                            proxyFactory.addAdvice(interceptor);
                            return proxyFactory.getProxy();
                        }
                    } else {
                        return bean;
                    }
                }

            };
        }




    }


}

