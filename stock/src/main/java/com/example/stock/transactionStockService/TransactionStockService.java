package com.example.stock.transactionStockService;

import com.example.stock.service.StockService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransactionStockService {

    private final StockService stockService;

    public void decrease(Long id, Long quantity){
        startTransaction();  // 트랜잭션 시작

        stockService.decrease(id, quantity);  // 실제 서비스 로직

        endTransaction();  // 트랜잭션 종료
    }

    public void startTransaction(){}

    public void endTransaction(){}
}
