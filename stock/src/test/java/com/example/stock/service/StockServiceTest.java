package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockServiceTest {

    @Autowired private StockService stockService;
    @Autowired private StockRepository stockRepository;

    @Autowired private PessimisticLockStockService pessimisticLockStockService;

    @BeforeEach
    public void before(){
        Stock stock = Stock.builder()
                .productId(1L)
                .quantity(100L)
                .build();

        stockRepository.save(stock);
    }

    @AfterEach
    public void after(){
        stockRepository.deleteAll();
    }

    @Test
    public void stock_decrease() {
        // given
        stockService.decrease(1L, 1L);

        // when
        Stock stock = stockRepository.findById(1L).orElseThrow();

        // then
        assertEquals(99, stock.getQuantity());
    }

    @Test
    public void 동시에_100개의_요청() throws InterruptedException {
        int threadCount = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        // ExecutorService : 비동기로 실행하는 작업을 단순화 하여 사용할 수 있게 도와주는 API

        CountDownLatch latch = new CountDownLatch(threadCount);
        // 100개의 요청이 끝날 때까지 기다려야하므로 CountDownLatch 사용
        // CountDownLatch : 다른 스레드에서 실행중인 작업이 완료될 때까지 대기할 수 있도록 도와줌.

        for(int i = 0; i < threadCount; i++){
            executorService.submit(() -> {
                try{
                    stockService.decrease(1L, 1L);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();

        assertEquals(0L, stock.getQuantity());
    }

    @Test
    public void 동시에_100개의_요청_PessimisticLock() throws InterruptedException {
        int threadCount = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        // ExecutorService : 비동기로 실행하는 작업을 단순화 하여 사용할 수 있게 도와주는 API

        CountDownLatch latch = new CountDownLatch(threadCount);
        // 100개의 요청이 끝날 때까지 기다려야하므로 CountDownLatch 사용
        // CountDownLatch : 다른 스레드에서 실행중인 작업이 완료될 때까지 대기할 수 있도록 도와줌.

        for(int i = 0; i < threadCount; i++){
            executorService.submit(() -> {
                try{
                    pessimisticLockStockService.decrease(1L, 1L);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();

        assertEquals(0L, stock.getQuantity());
    }

}